/*
 * Copyright 2013 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client.subscription;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;

/**
 * WebService which implements the UDDI v3 SubscriptionListener API. This
 * service will be called by the UDDI registry when any change to a Service or
 * BindingTemplate call in to it.
 * <h1>Usage scenario</h1>
 * Use this call for when you need to be notified from a UDDI server that either
 * a UDDI entity was created, changed, or deleted via the UDDI Subscription web
 * service. This class will start up an embedded Jetty server (built into the
 * JRE). You can then register your code to be notified of any inbound messages
 * received from the UDDI server asynchronously. Here's some sample code.
 * <pre>
 * UDDIClient c = new UDDIClient("META-INF/uddiclient.xml");
 * UDDIClerk clerk = c.getClerk("default");
 * TModel createKeyGenator = UDDIClerk.createKeyGenator("uddi:org.apache.juddi:test:keygenerator", "Test domain", "en");
 * clerk.register(createKeyGenator);
 * BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
 * //keep alive
 * while(running)
 * Thread.sleep(1000);
 * SubscriptionCallbackListener.stop(c, "default", start.getBindingKey());
 * </pre>
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @since 3.2
 */

@WebService(serviceName="UDDISubscriptionListenerService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionListenerPortType",
			targetNamespace = "urn:uddi-org:v3_service")
public class SubscriptionCallbackListener implements org.uddi.v3_service.UDDISubscriptionListenerPortType, Runnable {

        /**
         * adds a shutdown hook to trap and warn about leaving the server
         * running on exit
         */
        public SubscriptionCallbackListener() {
                Runtime runtime = Runtime.getRuntime();
                runtime.addShutdownHook(new Thread(this));
        }

        /**
         * used for unit tests, may return null if the endpoint isn't started
         * yet
         *
         * @return gets an instance
         */
        protected static SubscriptionCallbackListener getInstance() {
                return instance;
        }
        private static final Log log = LogFactory.getLog(SubscriptionCallbackListener.class);
        private static List<ISubscriptionCallback> callbacks = new ArrayList<ISubscriptionCallback>();
        private static SubscriptionCallbackListener instance = null;
        private static Endpoint ep = null;

        /**
         * Starts a embedded Jetty web server (comes with the JDK) using the
         * Endpoint API.
         *
         * @param client
         * @param cfg_node_name
         * @param endpoint this is the url that a UDDI server would use to
         * connect to the client's subscription listener service Recommend
         * specifying a port that is firewall friendly
         * @param keydomain
         
         * @param autoregister
         * @param behavior
         * @param serviceKey
         * @return null, if and only if callbackBusinessService was null,
         * otherwise the modified callbackBusinessService is returned. Clients
         * can then use it to continue the registration process.
         * @throws ServiceAlreadyStartedException
         * @throws SecurityException
         * @throws ConfigurationException
         * @throws TransportException
         * @throws DispositionReportFaultMessage
         * @throws java.rmi.UnexpectedException
         * @throws org.apache.juddi.v3.client.subscription.RegistrationAbortedException
         * @throws java.net.MalformedURLException
         * @throws org.apache.juddi.v3.client.subscription.UnableToSignException
         * @see Endpoint
         */
        public static synchronized BindingTemplate start(UDDIClient client, String cfg_node_name, String endpoint,
                String keydomain, boolean autoregister, String serviceKey,
                SignatureBehavior behavior) throws ServiceAlreadyStartedException, SecurityException, ConfigurationException, TransportException, DispositionReportFaultMessage, RemoteException, UnexpectedException, RegistrationAbortedException, UnableToSignException, MalformedURLException {

                if (instance == null) {
                        instance = new SubscriptionCallbackListener();
                }

                if (ep != null && ep.isPublished()) {
                        throw new ServiceAlreadyStartedException();
                }

                URL url = null;
                try {
                        url = new URL(endpoint);
                } catch (Exception ex) {
                        log.warn("Callback endpoint couldn't be parsed, generating a random one: " + ex.getMessage());
                        url = new URL("http://" + GetHostname() + ":" + GetRandomPort(4000) + "/" + UUID.randomUUID().toString());
                }
                endpoint = url.toString();
        //if (endpoint == null || endpoint.equals("")) {
                //    endpoint = "http://" + GetHostname() + ":" + GetRandomPort(url.getPort()) + "/" + UUID.randomUUID().toString();

                int attempts = 5;
                if (ep == null) {
                        while ((ep == null || !ep.isPublished()) && attempts > 0) {
                                try {
                                        ep = Endpoint.publish(endpoint, instance);
                                        callback = endpoint;
                                } catch (Exception be) {
                                        log.info("trouble starting callback at " + endpoint + ", trying again with a random port: " + be.getMessage());
                                        log.debug(be);
                                        attempts--;
                                        //if (be instanceof java.net.BindException) {
                                        url = new URL("http://" + url.getHost() + ":" + GetRandomPort(url.getPort()) + "/" + url.getPath());
                                        endpoint = url.toString();

                                }
                        }
                }
                if (ep == null || !ep.isPublished()) {
                        log.warn("Unable to start callback endpoint, aborting");
                        throw new SecurityException("unable to start endpoint, view previous errors for reason");
                }

                log.info("Endpoint started at " + callback);

                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue(callback);
                bt.getAccessPoint().setUseType("endPoint");
                TModelInstanceInfo instanceInfo = new TModelInstanceInfo();
                instanceInfo.setTModelKey("uddi:uddi.org:transport:http");
                bt.setTModelInstanceDetails(new TModelInstanceDetails());
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(instanceInfo);
                bt.setServiceKey(serviceKey);
                if (keydomain.endsWith(":")) {
                        bt.setBindingKey(keydomain + GetHostname() + "_subscription_callback");
                } else {
                        bt.setBindingKey(keydomain + ":" + GetHostname() + "_subscription_callback");
                }

                if (autoregister) {
                        bt = registerBinding(client, cfg_node_name, bt, behavior);
                }

                return bt;

        }

        /**
         * Starts a subscription callback service using the juddi client config
         * file's settings
         *
         * @param client
         * @param cfg_node_name - this is the uddi/client@name
         * @return a bindingtemplate populated with the relevant information for
         * most UDDI servers for asynchronous callbacks.
         * @throws ServiceAlreadyStartedException
         * @throws SecurityException
         * @throws ConfigurationException
         * @throws TransportException
         * @throws DispositionReportFaultMessage
         * @throws UnexpectedException
         * @throws RemoteException
         * @throws org.apache.juddi.v3.client.subscription.RegistrationAbortedException
         * @throws org.apache.juddi.v3.client.subscription.UnableToSignException
         * @throws java.net.MalformedURLException
         */
        public static synchronized BindingTemplate start(UDDIClient client, String cfg_node_name) throws ServiceAlreadyStartedException, SecurityException, ConfigurationException, TransportException, DispositionReportFaultMessage, UnexpectedException, RemoteException, RegistrationAbortedException, UnableToSignException, MalformedURLException {

                boolean reg = (client.getClientConfig().getConfiguration().getBoolean(PROPERTY_AUTOREG_BT, false));
                String endpoint = client.getClientConfig().getConfiguration().getString(PROPERTY_LISTENURL);
                String kd = client.getClientConfig().getConfiguration().getString(PROPERTY_KEYDOMAIN);
                String key = client.getClientConfig().getConfiguration().getString(PROPERTY_AUTOREG_SERVICE_KEY);
                String sbs = client.getClientConfig().getConfiguration().getString(PROPERTY_SIGNATURE_BEHAVIOR);
                SignatureBehavior sb = SignatureBehavior.DoNothing;
                try {
                        sb = SignatureBehavior.valueOf(sbs);
                } catch (Exception ex) {
                        log.warn("Unable to parse config setting for SignatureBehavior, defaulting to DoNothing", ex);
                }

                return start(client, cfg_node_name, endpoint, kd, reg, key, sb);
        }
        private static String callback = null;

        /**
         * gets the current callback url, may be null if the endpoint isn't
         * started yet
         *
         * @return the current callback url or null
         */
        public static String getCallbackURL() {
                return callback;

        }

        /**
         * Registers an implementation of ISubscriptionCallback for subscription
         * callbacks from a UDDI server.
         *
         *
         * @param callback if null, no action is taken
         */
        public static synchronized void registerCallback(ISubscriptionCallback callback) {
                if (callback != null) {
                        if (!callbacks.contains(callback)) {
                                callbacks.add(callback);
                        }
                }
        }

        /**
         * unregisters a ISubscriptionCallback for callbacks
         *
         * @param callback if null, no action is taken
         */
        public static synchronized void unRegisterCallback(ISubscriptionCallback callback) {
                if (callback != null) {
                        if (callbacks.contains(callback)) {
                                callbacks.remove(callback);
                        }
                }
        }
        /**
         * config parameter
         */
        public static final String PROPERTY_LISTENURL = "client.subscriptionCallbacks.listenUrl";
        /**
         * config parameter
         */
        public static final String PROPERTY_KEYDOMAIN = "client.subscriptionCallbacks.keyDomain";
        /**
         * config parameter true/false
         */
        public static final String PROPERTY_AUTOREG_BT = "client.subscriptionCallbacks.autoRegisterBindingTemplate";
        /**
         * config parameter business key
         */
        public static final String PROPERTY_AUTOREG_SERVICE_KEY = "client.subscriptionCallbacks.autoRegisterBusinessServiceKey";
        /**
         * config parameter
         *
         * @see SignatureBehavior
         */
        public static final String PROPERTY_SIGNATURE_BEHAVIOR = "client.subscriptionCallbacks.signatureBehavior";

        /**
         * return true if and only if the binding exists and is signed
         *
         * @param bindingKey
         * @param uddiInquiryService
         * @param token
         * @param behavior
         * @return true/false
         */
        private static boolean CheckExistingBindingForSignature(String bindingKey, UDDIInquiryPortType uddiInquiryService, String token, SignatureBehavior behavior) {
                GetBindingDetail gbd = new GetBindingDetail();
                gbd.setAuthInfo(token);
                gbd.getBindingKey().add(bindingKey);
                try {
                        BindingDetail bindingDetail = uddiInquiryService.getBindingDetail(gbd);
                        if (bindingDetail != null
                                && !bindingDetail.getBindingTemplate().isEmpty()
                                && !bindingDetail.getBindingTemplate().get(0).getSignature().isEmpty()) {
                                log.info("the binding template with key=" + bindingKey + " exists and is digitally signed");
                        }
                        return true;
                } catch (Exception ex) {
                        log.debug("Error caught checking for the existence of and if a signature is present for binding key " + bindingKey + " this may be ignorable", ex);
                }
                return false;
        }

        private static boolean CheckServiceAndParentForSignature(String serviceKey, UDDIInquiryPortType uddiInquiryService, String token) throws UnexpectedResponseException {
                GetServiceDetail gsd = new GetServiceDetail();
                gsd.setAuthInfo(token);
                gsd.getServiceKey().add(serviceKey);
                String bizkey = null;
                try {
                        ServiceDetail serviceDetail = uddiInquiryService.getServiceDetail(gsd);
                        if (serviceDetail != null) {
                                if (!serviceDetail.getBusinessService().isEmpty()) {
                                        bizkey = serviceDetail.getBusinessService().get(0).getBusinessKey();
                                        if (!serviceDetail.getBusinessService().get(0).getSignature().isEmpty()) {
                                                log.info("the service with key=" + serviceKey + " exists and is digitally signed");
                                                return true;
                                        }
                                }
                        }
                } catch (Exception ex) {
                        log.info("Error caught checking for the existence of and if a signature is present for service key " + serviceKey, ex);
                        throw new UnexpectedResponseException("Error caught checking for the existence of and if a signature is present for service key " + serviceKey, ex);
                }
                if (bizkey == null) {
                        throw new UnexpectedResponseException("The service with key " + serviceKey + " parent's business key could not be determined. This is unexpected");
                }
                GetBusinessDetail gbd = new GetBusinessDetail();
                gbd.setAuthInfo(token);
                gbd.getBusinessKey().add(bizkey);
                try {
                        BusinessDetail businessDetail = uddiInquiryService.getBusinessDetail(gbd);
                        if (businessDetail != null && !businessDetail.getBusinessEntity().isEmpty()) {
                                if (!businessDetail.getBusinessEntity().get(0).getSignature().isEmpty()) {
                                        log.info("the business with key=" + bizkey + " exists and is digitally signed");
                                        return true;
                                }
                        }
                } catch (Exception ex) {
                        log.info("Error caught checking for the existence of and if a signature is present for business key " + bizkey, ex);
                        throw new UnexpectedResponseException("Error caught checking for the existence of and if a signature is present for business key " + bizkey, ex);
                }
                return false;
        }

        private static int GetRandomPort(int oldport) {
                if (oldport <= 0) {
                        oldport = 4000;
                }
                return oldport + new Random().nextInt(99);

        }

        @Override
        protected void finalize() throws Throwable {
                run();
                super.finalize();

        }

        /**
         * shutdown hook
         */
        @Override
        public void run() {
                shutdown();
        }

        private synchronized void shutdown() {
                if (ep != null && !ep.isPublished()) {
                        log.fatal("Hey, someone should tell the developer to call SubscriptionCallbackListern.stop(...) before ending the program. Stopping endpoint at " + callback);
                        unregisterAllCallbacks();
                        ep.stop();
                        ep = null;
                        callback = null;
                }

        }

        /**
         * This defines how the automatic subscription binding template is
         * suppose to behave
         */
        public enum SignatureBehavior {

                /**
                 * Aborts the save request if either the entity exists and is
                 * already signed, or if any parent uddi element is signed
                 */
                AbortIfSigned,
                /**
                 * Signs this element. Warning: It may cause signatures of
                 * parent elements to become invalid. If unable to sign, an
                 * exception will be thrown
                 */
                SignAlways,
                /**
                 * Signs this element, but only if parents are not signed. If
                 * unable to sign, an exception will be thrown
                 */
                SignOnlyIfParentIsntSigned,
                /**
                 * Do nothing, don't sign it and don't check if a parent item is
                 * signed or not.
                 */
                DoNothing
        }

        /**
         * Registers a UDDI binding template that represents the subscription
         * callback endpoint
         *
         * @param client
         * @param cfg_node_name
         * @param bt - Binding Template
         * @param behavior
         * @return a binding template
         * @throws ServiceAlreadyStartedException
         * @throws SecurityException
         * @throws ConfigurationException
         * @throws TransportException
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         * @throws UnexpectedException
         * @throws RegistrationAbortedException
         * @throws UnableToSignException
         */
        public static BindingTemplate registerBinding(UDDIClient client, String cfg_node_name, BindingTemplate bt, SignatureBehavior behavior) throws ServiceAlreadyStartedException, SecurityException, ConfigurationException, TransportException, DispositionReportFaultMessage, RemoteException, UnexpectedException, RegistrationAbortedException, UnableToSignException {

                UDDIClerk clerk = client.getClerk(cfg_node_name);
                Transport tp = client.getTransport(cfg_node_name);
                UDDIInquiryPortType uddiInquiryService = tp.getUDDIInquiryService();
                UDDIPublicationPortType uddiPublishService = tp.getUDDIPublishService();

                String token = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());

                switch (behavior) {
                        case AbortIfSigned:
                                if (CheckExistingBindingForSignature(bt.getBindingKey(), uddiInquiryService, token, behavior)) {
                                        throw new RegistrationAbortedException("Aborting, Either the item exists and is signed");
                                }
                                if (CheckServiceAndParentForSignature(bt.getServiceKey(), uddiInquiryService, token)) {
                                        throw new RegistrationAbortedException("Aborting, Either the service or busness is signed");
                                }
                                break;
                        case DoNothing:
                                break;
                        case SignAlways:
                                try {
                                        DigSigUtil ds = new DigSigUtil(client.getClientConfig().getDigitalSignatureConfiguration());
                                        bt = ds.signUddiEntity(bt);
                                } catch (Exception ex) {
                                        log.error("Unable to sign", ex);
                                        throw new UnableToSignException(ex);
                                }

                                break;
                        case SignOnlyIfParentIsntSigned:
                                if (!CheckServiceAndParentForSignature(bt.getServiceKey(), uddiInquiryService, token)) {
                                        try {
                                                DigSigUtil ds = new DigSigUtil(client.getClientConfig().getDigitalSignatureConfiguration());
                                                bt = ds.signUddiEntity(bt);
                                        } catch (Exception ex) {
                                                log.error("Unable to sign", ex);
                                                throw new UnableToSignException(ex);
                                        }
                                }
                                break;
                }
                SaveBinding sb = new SaveBinding();
                sb.setAuthInfo(token);
                sb.getBindingTemplate().add(bt);

                BindingDetail saveBinding = uddiPublishService.saveBinding(sb);
                if (saveBinding.getBindingTemplate().isEmpty() || saveBinding.getBindingTemplate().size() > 1) {
                        throw new UnexpectedResponseException("The number of binding templates returned was unexpected, count=" + saveBinding.getBindingTemplate().size());
                }
                return saveBinding.getBindingTemplate().get(0);
        }

        protected static synchronized void unregisterAllCallbacks() {
                if (callbacks != null) {
                        log.info("Notifying all subscribing classes, count=" + callbacks.size());
                        for (int i = 0; i < callbacks.size(); i++) {
                                if (callbacks.get(i) != null) {
                                        try {
                                                callbacks.get(i).NotifyEndpointStopped();
                                        } catch (Exception ex) {
                                                log.warn("Your implementation on ISubscriptionCallback is faulty and threw an error, contact the developer", ex);
                                        }
                                }
                        }
                        callbacks.clear();
                }

        }

        /**
         * This effectively stops the endpoint address and notifies all
         * ISubscriptionCallback clients that the endpoint as been stopped.
         * After it has been stopped, all ISubscriptionCallback are removed
         * from the callback list. If the configuration file is set to automatically
         * register binding templates, the binding template will be unregistered from
         * the UDDI server
         * @param client
         * @param cfg_node_name
         * @param bindingKey
         * @throws org.apache.commons.configuration.ConfigurationException
         */
        public static synchronized void stop(UDDIClient client, String cfg_node_name, String bindingKey) throws ConfigurationException {
                //stop the service
                if (ep != null && ep.isPublished()) {
                        log.warn("Stopping jUDDI Subscription callback endpoint at " + callback);
                        ep.stop();
                        if (ep.isPublished()) {
                                log.fatal("Unable to stop the endpoint. the port may be locked until this java process terminates");
                        }
                        ep = null;
                        callback = null;
                }
                unregisterAllCallbacks();
                if (client.getClientConfig().getConfiguration().getBoolean(PROPERTY_AUTOREG_BT, false) && bindingKey != null) {
                        
                        try {
                                UDDIClerk clerk = client.getClerk(cfg_node_name);
                                Transport tp = client.getTransport(cfg_node_name);
                                String token = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                                UDDIPublicationPortType uddiPublishService = tp.getUDDIPublishService();
                                DeleteBinding db = new DeleteBinding();
                                db.setAuthInfo(token);
                                db.getBindingKey().add(bindingKey);
                                uddiPublishService.deleteBinding(db);
                                log.info("Subscription callback binding unregistered.");
                        } catch (Exception ex) {
                                log.error("Unable to unregister binding " + bindingKey, ex);
                        }
                }

        //TODO optionally kill the subscription?
                //get all subscriptions from the uddi node, 
                //loop through and deduce which ones are pointed at this endpoint
                //then remove them
        }

        private static String GetHostname() {
                try {
                        return java.net.InetAddress.getLocalHost().getHostName();
                } catch (UnknownHostException ex) {
                        return "HOST_UNKNOWN";
                }
        }

        @Override
        public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
                for (int i = 0; i < callbacks.size(); i++) {
                        try {
                                callbacks.get(i).HandleCallback(body.getSubscriptionResultsList());
                        } catch (Exception ex) {
                                log.warn("Your implementation on ISubscriptionCallback is faulty and threw an error, contact the developer", ex);
                        }
                }
                DispositionReport r = new DispositionReport();
                r.getResult().add(new Result());
                return r;
        }
}
