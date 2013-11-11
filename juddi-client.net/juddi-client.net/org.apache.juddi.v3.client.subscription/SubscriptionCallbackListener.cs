/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */

using org.apache.juddi.v3.client.log;
using org.apache.juddi.v3.client.subscription.wcf;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using org.apache.juddi.client.org.apache.juddi.v3.client.subscription;
using System.Security;
using System.ServiceModel;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using org.apache.juddi.v3.client.crypto;
using System.Net;

namespace org.apache.juddi.v3.client.subscription
{
    /// <summary>
    /// WebService which implements the UDDI v3 SubscriptionListener API. 
    /// This service will be called by the UDDI registry when any change to a Service or BindingTemplate call in to it.
    /// </summary>
    /// Usage scenario
    /// Usethis call for when you need to be notified from a UDDI server that either a UDDI entity was created, changed, or deleted via the UDDI Subscription web service. This class will start up an embedded Jetty server (built into the JRE). You can then register your code to be notified of any inbound messages received from the UDDI server asynchronously. Here's some sample code.
    /// <pre>
    /// 
    /*
    /// 
    /// UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient("uddi.xml");
                UDDIClientContainer.addClient(clerkManager);

                transport = clerkManager.getTransport("default");

                UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();

                clerk = clerkManager.getClerk("default");

                tModel createKeyGenator = UDDIClerk.createKeyGenator("uddi:org.apache.juddi:test:keygenerator", "Test domain", "en");
                clerk.register(createKeyGenator);
                bindingTemplate start = SubscriptionCallbackListener.start(clerkManager, "default");
                //bkeep alive
                DateTime stop = DateTime.Now.Add(new TimeSpan(0, 1, 0));
                while (DateTime.Now < stop)
                {
                    Thread.Sleep(1000);

                }
                SubscriptionCallbackListener.stop(c, "default", start.bindingKey);
            }
            catch (Exception ex)
            {
                while (ex != null)
                {
                    System.Console.WriteLine("Error! " + ex.Message);
                    ex = ex.InnerException;
                }
            }
            finally
            {
                if (transport != null && transport is IDisposable)
                {
                    ((IDisposable)transport).Dispose();
                }
                if (clerk != null)
                    clerk.Dispose();
            }
*/
    /// </pre>
    /// 
    [ServiceBehaviorAttribute( AutomaticSessionShutdown=false, ConcurrencyMode=ConcurrencyMode.Single, Name="SubscriptionCallbackListener",
        Namespace="org.apache.juddi.v3.client.subscription", IncludeExceptionDetailInFaults=false, InstanceContextMode=InstanceContextMode.Single, ValidateMustUnderstand=false,
        AddressFilterMode=AddressFilterMode.Any)]
    public class SubscriptionCallbackListener : UDDI_SubscriptionListener_PortType
    {
        public SubscriptionCallbackListener()
        {
            AppDomain.CurrentDomain.ProcessExit += new EventHandler(CurrentDomain_ProcessExit);

        }



        protected static SubscriptionCallbackListener getInstance()
        {
            return instance;
        }
        private static Log log = LogFactory.getLog(typeof(SubscriptionCallbackListener));
        private static List<ISubscriptionCallback> callbacks = new List<ISubscriptionCallback>();
        private static SubscriptionCallbackListener instance = null;
        private static ServiceHost ep = null;

        private static String callback = null;

        /**
         * gets the current callback url, may be null if the endpoint isn't started
         * yet
         *
         * @return
         */
        public static String getCallbackURL()
        {
            return callback;

        }


        /**
    * Starts a embedded WCF web server (comes with .NET) using the
    * Endpoint API.
    *
    * @param endpoint this is the url that a UDDI server would use to connect
    * to the client's subscription listener service Recommend specifying a port
    * that is firewall friendly
    * @param callbackBusinessService - optional. if specified, a binding
    * template is appended to the business service and returned. The new
    * binding template is annotated for subscription callbacks.
    * @return null, if and only if callbackBusinessService was null, otherwise
    * the modified callbackBusinessService is returned. Clients can then use it
    * to continue the registration process.
    * @throws ServiceAlreadyStartedException
    * @throws SecurityException
    * @see Endpoint
    */
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static bindingTemplate start(UDDIClient client, String cfg_node_name, String endpoint,
                String keydomain, bool autoregister, String serviceKey,
                SignatureBehavior behavior)
        {


            if (instance == null)
            {
                instance = new SubscriptionCallbackListener();
            }

            if (ep != null && ep.State == CommunicationState.Opened)
            {
                throw new ServiceAlreadyStartedException();
            }


            Uri url = null;
            try
            {
                url = new Uri(endpoint);
            }
            catch (Exception ex)
            {
                log.warn("Callback endpoint couldn't be parsed, generating a random one: " + ex.Message);
                url = new Uri("http://" + GetHostname() + ":" + GetRandomPort(4000) + "/" + Guid.NewGuid().ToString());
            }
            endpoint = url.ToString();
            //if (endpoint == null || endpoint.equals("")) {
            //    endpoint = "http://" + GetHostname() + ":" + GetRandomPort(url.getPort()) + "/" + UUID.randomUUID().toString();

            int attempts = 5;
            if (ep == null)
            {
                while ((ep == null || ep.State != CommunicationState.Opened) && attempts > 0)
                {
                    try
                    {
                        if (endpoint.Contains("localhost"))
                            endpoint = endpoint.Replace("localhost", GetHostname());
                        ep = new ServiceHost(instance, new Uri[] { new Uri(endpoint) });
                        //ep = Endpoint.publish(endpoint, instance);
                        ep.Open();
                        callback = endpoint;
                    }
                    catch (Exception be)
                    {
                        log.info("trouble starting callback at " + endpoint + ", trying again with a random port: " + be.Message);
                        log.debug(be);
                        attempts--;
                        //if (be instanceof java.net.BindException) {
                        url = new Uri("http://" + url.Host + ":" + GetRandomPort(url.Port) + "/" + url.PathAndQuery);
                        endpoint = url.ToString();

                    }
                }
            }
            if (ep == null || ep.State != CommunicationState.Opened)
            {
                log.warn("Unable to start callback endpoint, aborting");
                throw new SecurityException("unable to start endpoint, view previous errors for reason");
            }

            log.info("Endpoint started at " + callback);

            bindingTemplate bt = new bindingTemplate();
            bt.Item = (new accessPoint(callback, "endPoint"));

            tModelInstanceInfo instanceInfo = new tModelInstanceInfo();
            instanceInfo.tModelKey = ("uddi:uddi.org:transport:http");
            bt.tModelInstanceDetails = new tModelInstanceInfo[] { instanceInfo };

            bt.serviceKey = (serviceKey);
            if (keydomain.EndsWith(":"))
            {
                bt.bindingKey = (keydomain + GetHostname() + "_Subscription_Callback");
            }
            else
            {
                bt.bindingKey = (keydomain + ":" + GetHostname() + "_Subscription_Callback");
            }

            if (autoregister)
            {
                bt = registerBinding(client, cfg_node_name, bt, behavior);
            }

            return bt;

        }

        private static int GetRandomPort(int oldport)
        {
            if (oldport <= 0)
            {
                oldport = 4000;
            }
            return oldport + new Random().Next(99);

        }

        /// <summary>
        /// Starts a subscription callback service using the juddi client config
        /// file's settings
        /// </summary>
        /// <param name="client"></param>
        /// <param name="cfg_node_name"></param>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static bindingTemplate start(UDDIClient client, String cfg_node_name)
        {

            bool reg = client.getClientConfig().getConfiguration().client.subscriptionCallbacks.autoRegisterBindingTemplate;
            String endpoint = client.getClientConfig().getConfiguration().client.subscriptionCallbacks.listenUrl;
            String kd = client.getClientConfig().getConfiguration().client.subscriptionCallbacks.keyDomain;
            String key = client.getClientConfig().getConfiguration().client.subscriptionCallbacks.autoRegisterBusinessServiceKey;
            String sbs = client.getClientConfig().getConfiguration().client.subscriptionCallbacks.signatureBehavior;
            SignatureBehavior sb = SignatureBehavior.DoNothing;
            try
            {
                sb = (SignatureBehavior)Enum.Parse(typeof(SignatureBehavior), sbs);
            }
            catch (Exception ex)
            {
                log.warn("Unable to parse config setting for SignatureBehavior, defaulting to DoNothing", ex);
            }

            return start(client, cfg_node_name, endpoint, kd, reg, key, sb);
        }



        /**
    * Registers a UDDI binding template that represents the subscription
    * callback endpoint
    *
    * @param client
    * @param cfg_node_name
    * @param bt - Binding Template
    * @param behavior
    * @return
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
        public static bindingTemplate registerBinding(UDDIClient client, String cfg_node_name, bindingTemplate bt, SignatureBehavior behavior)
        {
            log.info("Attempting to register binding " + bt.bindingKey);
            UDDIClerk clerk = client.getClerk(cfg_node_name);
            Transport tp = client.getTransport(cfg_node_name);
            UDDI_Inquiry_SoapBinding uddiInquiryService = tp.getUDDIInquiryService();
            UDDI_Publication_SoapBinding uddiPublishService = tp.getUDDIPublishService();


            String token = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());

            switch (behavior)
            {
                case SignatureBehavior.AbortIfSigned:
                    if (CheckExistingBindingForSignature(bt.bindingKey, uddiInquiryService, token, behavior))
                    {
                        throw new RegistrationAbortedException("Aborting, Either the item exists and is signed");
                    }
                    if (CheckServiceAndParentForSignature(bt.serviceKey, uddiInquiryService, token))
                    {
                        throw new RegistrationAbortedException("Aborting, Either the service or busness is signed");
                    }
                    break;
                case SignatureBehavior.DoNothing:
                    break;
                case SignatureBehavior.SignAlways:
                    try
                    {
                        DigSigUtil ds = new DigSigUtil(client.getClientConfig().getDigitalSignatureConfiguration());
                        bt = ds.signUddiEntity(bt);
                    }
                    catch (Exception ex)
                    {
                        log.error("Unable to sign", ex);
                        throw new UnableToSignException(ex);
                    }

                    break;
                case SignatureBehavior.SignOnlyIfParentIsntSigned:
                    if (!CheckServiceAndParentForSignature(bt.serviceKey, uddiInquiryService, token))
                    {
                        try
                        {
                            DigSigUtil ds = new DigSigUtil(client.getClientConfig().getDigitalSignatureConfiguration());
                            bt = ds.signUddiEntity(bt);
                        }
                        catch (Exception ex)
                        {
                            log.error("Unable to sign", ex);
                            throw new UnableToSignException(ex);
                        }
                    }
                    break;
            }
            save_binding sb = new save_binding();
            sb.authInfo = (token);
            sb.bindingTemplate = new bindingTemplate[] { bt };

            bindingDetail saveBinding = uddiPublishService.save_binding(sb);
            log.info("Binding registered successfully");
            if (saveBinding.bindingTemplate == null || saveBinding.bindingTemplate.Length > 1)
            {
                throw new UnexpectedResponseException("The number of binding templates returned was unexpected, count=" + (saveBinding.bindingTemplate == null ? "none" : saveBinding.bindingTemplate.Length.ToString()));
            }
            return saveBinding.bindingTemplate[0];
        }



        private static bool CheckServiceAndParentForSignature(String serviceKey, UDDI_Inquiry_SoapBinding uddiInquiryService, String token)
        {
            get_serviceDetail gsd = new get_serviceDetail();
            gsd.authInfo = (token);
            gsd.serviceKey = new string[] { serviceKey };
            String bizkey = null;
            try
            {
                serviceDetail serviceDetail = uddiInquiryService.get_serviceDetail(gsd);
                if (serviceDetail != null && serviceDetail.businessService != null)
                {
                    {
                        bizkey = serviceDetail.businessService[0].businessKey;
                        if (serviceDetail.businessService[0].Signature != null && serviceDetail.businessService[0].Signature.Length > 0)
                        {
                            log.info("the service with key=" + serviceKey + " exists and is digitally signed");
                            return true;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                log.info("Error caught checking for the existence of and if a signature is present for service key " + serviceKey, ex);
                throw new UnexpectedResponseException("Error caught checking for the existence of and if a signature is present for service key " + serviceKey, ex);
            }
            if (bizkey == null)
            {
                throw new UnexpectedResponseException("The service with key " + serviceKey + " parent's business key could not be determined. This is unexpected");
            }
            get_businessDetail gbd = new get_businessDetail();
            gbd.authInfo = (token);
            gbd.businessKey = new string[] { bizkey };
            try
            {
                businessDetail businessDetail = uddiInquiryService.get_businessDetail(gbd);
                if (businessDetail != null && businessDetail.businessEntity != null)
                {
                    if (businessDetail.businessEntity[0].Signature != null && businessDetail.businessEntity[0].Signature.Length > 0)
                    {
                        log.info("the business with key=" + bizkey + " exists and is digitally signed");
                        return true;
                    }
                }
            }
            catch (Exception ex)
            {
                log.info("Error caught checking for the existence of and if a signature is present for business key " + bizkey, ex);
                throw new UnexpectedResponseException("Error caught checking for the existence of and if a signature is present for business key " + bizkey, ex);
            }
            return false;
        }


        /**
         * return true if and only if the binding exists and is signed
         *
         * @param bindingKey
         * @param uddiInquiryService
         * @param token
         * @param behavior
         * @return
         */
        private static bool CheckExistingBindingForSignature(String bindingKey, UDDI_Inquiry_SoapBinding uddiInquiryService, String token, SignatureBehavior behavior)
        {
            get_bindingDetail gbd = new get_bindingDetail();
            gbd.authInfo = (token);
            gbd.bindingKey = new string[] { bindingKey };
            try
            {
                bindingDetail bindingDetail = uddiInquiryService.get_bindingDetail(gbd);
                if (bindingDetail != null
                        && bindingDetail.bindingTemplate != null
                        && bindingDetail.bindingTemplate.Length > 0
                        && bindingDetail.bindingTemplate[0].Signature != null
                        && bindingDetail.bindingTemplate[0].Signature.Length > 0)
                {
                    log.info("the binding template with key=" + bindingKey + " exists and is digitally signed");
                }
                return true;
            }
            catch (Exception ex)
            {
                log.debug("Error caught checking for the existence of and if a signature is present for binding key " + bindingKey + " this may be ignorable", ex);
            }
            return false;
        }

        private void CurrentDomain_ProcessExit(object sender, EventArgs e)
        {
            if (ep != null && ep.State == CommunicationState.Opened)
            {
                log.error("Hey, someone should tell the developer to call SubscriptionCallbackListern.stop(...) before ending the program. Stopping endpoint at " + callback);
                unregisterAllCallbacks();
                ep.Close();
                ep = null;
                callback = null;
            }
        }
        public uddi.apiv3.dispositionReport notify_subscriptionListener(uddi.apiv3.notify_subscriptionListener body)
        {
            for (int i = 0; i < callbacks.Count; i++)
            {
                try
                {
                    callbacks[i].HandleCallback(body.subscriptionResultsList);
                }
                catch (Exception ex)
                {
                    log.warn("Your implementation on ISubscriptionCallback is faulty and threw an error, contact the developer", ex);
                }
            }
            dispositionReport r = new dispositionReport();
            r.result = new result[] { new result() };
            return r;

        }



        [MethodImpl(MethodImplOptions.Synchronized)]
        protected static void unregisterAllCallbacks()
        {
            if (callbacks != null)
            {
                log.info("Notifying all subscribing classes, count=" + callbacks.Count);
                for (int i = 0; i < callbacks.Count; i++)
                {
                    if (callbacks[(i)] != null)
                    {
                        try
                        {
                            callbacks[(i)].NotifyEndpointStopped();
                        }
                        catch (Exception ex)
                        {
                            log.warn("Your implementation on ISubscriptionCallback is faulty and threw an error, contact the developer", ex);
                        }
                    }
                }
                callbacks.Clear();
            }

        }

        /**
         * This effectively stops the endpoint address and notifies all
         * ISubscriptionCallback clients that the endpoint as been stopped. After it
         * has been stopped, call ISubscriptionCallback are removed from the
         * callback list. If unable to remove an auto registered binding template,
         * no exception will be thrown
         */
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static void stop(UDDIClient client, String cfg_node_name, String bindingKey)
        {
            //stop the service
            if (ep != null && ep.State == CommunicationState.Opened)
            {
                log.warn("Stopping jUDDI Subscription callback endpoint at " + callback);
                ep.Close();
                if (ep.State != CommunicationState.Closed && ep.State != CommunicationState.Closing)
                {
                    log.error("Unable to stop the endpoint. the port may be locked until this process terminates");
                }
                ep = null;
                callback = null;
            }
            unregisterAllCallbacks();

            if (client.getClientConfig().getConfiguration().client != null &&
                client.getClientConfig().getConfiguration().client.subscriptionCallbacks != null &&
                client.getClientConfig().getConfiguration().client.subscriptionCallbacks.signatureBehavior != null &&
                client.getClientConfig().getConfiguration().client.subscriptionCallbacks.signatureBehavior.Equals("true", StringComparison.CurrentCultureIgnoreCase) &&
                bindingKey != null)
            {
                log.info("Attempting to unregister the binding");
                try
                {
                    UDDIClerk clerk = client.getClerk(cfg_node_name);
                    Transport tp = client.getTransport(cfg_node_name);
                    String token = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                    UDDI_Publication_SoapBinding uddiPublishService = tp.getUDDIPublishService();
                    delete_binding db = new delete_binding();
                    db.authInfo = (token);
                    db.bindingKey = new string[] { (bindingKey) };
                    uddiPublishService.delete_binding(db);
                }
                catch (Exception ex)
                {
                    log.error("Unable to unregister binding " + bindingKey, ex);
                }
            }


            //TODO optionally kill the subscription?
            //get all subscriptions from the uddi node, 
            //loop through and deduce which ones are pointed at this endpoint
            //then remove them
        }
        private static String GetHostname()
        {
            try
            {
                return Dns.GetHostName();
            }
            catch (Exception ex)
            {
                return "HOST_UNKNOWN";
            }
        }

        /**
     * config parameter
     */
        public static readonly String PROPERTY_LISTENURL = "client.subscriptionCallbacks.listenUrl";
        /**
         * config parameter
         */
        public static readonly String PROPERTY_KEYDOMAIN = "client.subscriptionCallbacks.keyDomain";
        /**
         * config parameter true/false
         */
        public static readonly String PROPERTY_AUTOREG_BT = "client.subscriptionCallbacks.autoRegisterBindingTemplate";
        /**
         * config parameter business key
         */
        public static readonly String PROPERTY_AUTOREG_SERVICE_KEY = "client.subscriptionCallbacks.autoRegisterBusinessServiceKey";
        /**
         * config parameter
         *
         * @see SignatureBehavior
         */
        public static readonly String PROPERTY_SIGNATURE_BEHAVIOR = "client.subscriptionCallbacks.signatureBehavior";

    }
    /**
    * This defines how the automatic subscription binding template is suppose
    * to behave
    */
    public enum SignatureBehavior
    {

        /**
         * Aborts the save request if either the entity exists and is already
         * signed, or if any parent uddi element is signed
         */
        AbortIfSigned,
        /**
         * Signs this element. Warning: It may cause signatures of parent
         * elements to become invalid. If unable to sign, an exception will be
         * thrown
         */
        SignAlways,
        /**
         * Signs this element, but only if parents are not signed. If unable to
         * sign, an exception will be thrown
         */
        SignOnlyIfParentIsntSigned,
        /**
         * Do nothing, don't sign it and don't check if a parent item is signed
         * or not.
         */
        DoNothing
    }

}
