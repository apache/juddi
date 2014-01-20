/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.config;

import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.wsdl.Definition;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.cryptor.CryptorFactory;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.wsdl.ReadWSDL;
import org.apache.juddi.v3.client.mapping.wsdl.WSDL2UDDI;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.PublisherAssertions;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.api_v3.TModelList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * The UDDIClerk provides an easy way to access a UDDI service. The clerk can be
 * configured programmatically, but it is recommended to specify the server
 * endpoint and access credentials in a uddi.xml file.
 *
 * Recommended use:
 * <pre>
 * {@code
 * UDDIClient uddiClient = new UDDIClient();
 * UDDIClerk clerk = uddiClient.getClerk(clerkName);
 * }
 * </pre> where the clerkName "MyClerk" is defined as attribute on the clerk
 * element
 * <pre>
 * {@code
 *  <clerks registerOnStartup="true">
 *    <clerk name="MyClerk" node="default" publisher="root" password="root" isPasswordEncrypted="false" cryptoProvider="">
 *      <class>org.apache.juddi.example.HelloWorldImpl</class>
 *    </clerk>
 *  </clerks>
 * }
 * </pre> Credentials: In the clerk section you need to specify the publisher to
 * be used, as well the password. The password can be encrypted and a
 * cryptoProvider class can be set.
 *
 * UDDI Annotations: If you want to register classes containing UDDIAnnotations,
 * then you need to set registerOnStartup="true" and you can list the classes
 * with the annotations as subelements. See the HelloWorldImpl class above, as
 * well as the uddi-annotations example.
 *
 * @author kstam
 *
 */
public class UDDIClerk implements Serializable {

        private static final long serialVersionUID = -8597375975981358134L;

        private Log log = LogFactory.getLog(this.getClass());
        protected String name;
        protected UDDINode uddiNode;
        protected String publisher;
        protected String password;
        private Date tokenBirthDate;
        private String authToken;
        private String cryptoProvider;
        private boolean isencrypted = false;
        private String[] classWithAnnotations;
        private WSDL[] wsdls;
        private String managerName;
        private Map<String, Properties> services = new HashMap<String, Properties>();

        /**
         * use caution calling the default constructor, many of the functions of
         * the UDDI Clerk will not function unless manually set. The Node must
         * be set for using most of the functions provided by this class.
         */
        public UDDIClerk() {
                super();
                // TODO Auto-generated constructor stub
        }

        public UDDIClerk(Clerk clerk) {
                super();
                this.name = clerk.getName();
                this.password = clerk.getPassword();
                this.publisher = clerk.getPublisher();
                this.uddiNode = new UDDINode(clerk.getNode());
        }

        /**
         * A list of classes defined in the config file that have UDDI
         * Annotations on them for automated registration
         * <br>client.clerks.clerk(" + i + ").class
         *
         * @return classes with UDDI Annotations
         * @see org.apache.juddi.v3.annotations.UDDIService
         * @see org.apache.juddi.v3.annotations.UDDIServiceBinding
         */
        public String[] getClassWithAnnotations() {
                return classWithAnnotations;
        }

        /**
         * Returns the UDDI node that this clerk is associated with
         * client.clerks.clerk(" + i + ")@node
         *
         * @return UDDINode
         */
        public UDDINode getUDDINode() {
                return this.uddiNode;
        }

        /**
         * A list of classes defined in the config file that have UDDI
         * Annotations on them for automated registration
         * <br>client.clerks.clerk(" + i + ").class
         *
         * @see org.apache.juddi.v3.annotations.UDDIService
         * @see org.apache.juddi.v3.annotations.UDDIServiceBinding
         */
        public void setClassWithAnnotations(String[] classWithAnnotations) {
                this.classWithAnnotations = classWithAnnotations;
        }

        /**
         * This is not used within the jUDDI code base and will be removed in the next release
         *
         * @return a map
         * @deprecated 
         */
        @Deprecated
        public Map<String, Properties> getServices() {
                return services;
        }

        /**
         * This is not used within the jUDDI code base and will be removed in the next release
         * @param services
         * @deprecated 
         */
        @Deprecated
        public void setServices(Map<String, Properties> services) {
                this.services = services;
        }

        /**
         * The client manager name as defined in the config file client[@name]
         *
         * @return the manager name
         */
        public String getManagerName() {
                return managerName;
        }

        /**
         * The client manager name as defined in the config file client[@name]
         *
         * @param managerName
         */
        public void setManagerName(String managerName) {
                this.managerName = managerName;
        }

        /**
         * Performans the process of parsing the configuration defined wsdls to
         * UDDI. This is a convenience wrapper Note, if registration fails, no
         * exception is thrown
         */
        public void registerWsdls() {
                registerWsdls(null);
        }

        /**
         * Registers the WSDL files referenced in the clerk onto the UDDI node
         * referenced by the clerk. Note, if registration fails, no exception is
         * thrown
         * <pre>
         * {@code
         * <clerks registerOnStartup="false">
         *     <clerk name="joe" node="default" publisher="joepublisher" password="joepublisher" isPasswordEncrypted="false" cryptoProvider="">
         *        <wsdl businessName="WSDL-Business">wsdl/helloworld.wsdl</wsdl>
         *     </clerk>
         *  </clerks>
         * }
         * </pre>
         *
         * @param localizerBaseUrl - The baseUrl part which will be used when
         * building the bindingTemplate.
         */
        public void registerWsdls(URL localizerBaseUrl) {
                if (this.getWsdls() != null) {
                        Properties properties = new Properties();
                        properties.putAll(this.getUDDINode().getProperties());

                        for (WSDL wsdl : this.getWsdls()) {
                                try {
                                        URL wsdlUrl = this.getClass().getClassLoader().getResource(wsdl.getFileName());
                                        ReadWSDL rw = new ReadWSDL();
                                        Definition wsdlDefinition = rw.readWSDL(wsdlUrl);
                                        if (wsdl.keyDomain != null) {
                                                properties.setProperty("keyDomain", wsdl.keyDomain);
                                        }
                                        if (wsdl.businessKey != null) {
                                                properties.setProperty("businessKey", wsdl.getBusinessKey());
                                        }

                                        WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizerDefaultImpl(localizerBaseUrl), properties);
                                        wsdl2UDDI.registerBusinessServices(wsdlDefinition);
                                } catch (Exception e) {
                                        log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.getMessage(), e);
                                } catch (Throwable t) {
                                        log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + t.getMessage(), t);
                                }
                        }
                }
        }

        /**
         * Registers a WSDL Definition onto the UDDI node referenced by the
         * clerk. Note, if registration fails, no exception is thrown
         *
         * @param wsdlDefinition - the WSDL Definition
         * @param keyDomain - the keyDomain which will be used to construct the
         * UDDI key IDs. If left null the keyDomain defined in the node's
         * properties will be used.
         * @param businessKey - the key of the business to which this service
         * belongs. If left null the businessKey defined in the node's
         * properties will be used.
         *
         */
        public void registerWsdls(Definition wsdlDefinition, String keyDomain, String businessKey) {

                try {
                        Properties properties = new Properties();
                        properties.putAll(this.getUDDINode().getProperties());
                        if (keyDomain != null) {
                                properties.setProperty("keyDomain", keyDomain);
                        }
                        if (businessKey != null) {
                                properties.setProperty("businessKey", businessKey);
                        }

                        WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizerDefaultImpl(), properties);
                        wsdl2UDDI.registerBusinessServices(wsdlDefinition);
                } catch (Exception e) {
                        log.error("Unable to register wsdl " + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to register wsdl " + " ." + t.getMessage(), t);
                }
        }

        /**
         * Removes the UDDI data structures belonging to the WSDLs for this
         * clerk from the UDDI node. Note, if registration fails, no exception
         * is thrown
         */
        public void unRegisterWsdls() {
                if (this.getWsdls() != null) {
                        Properties properties = new Properties();
                        properties.putAll(this.getUDDINode().getProperties());

                        for (WSDL wsdl : this.getWsdls()) {
                                try {
                                        URL wsdlUrl = this.getClass().getClassLoader().getResource(wsdl.getFileName());
                                        ReadWSDL rw = new ReadWSDL();
                                        Definition wsdlDefinition = rw.readWSDL(wsdlUrl);
                                        if (wsdl.keyDomain != null) {
                                                properties.setProperty("keyDomain", wsdl.keyDomain);
                                        }
                                        if (wsdl.businessKey != null) {
                                                properties.setProperty("businessKey", wsdl.getBusinessKey());
                                        }

                                        WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizerDefaultImpl(), properties);
                                        wsdl2UDDI.unRegisterBusinessServices(wsdlDefinition);
                                } catch (Exception e) {
                                        log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.getMessage(), e);
                                } catch (Throwable t) {
                                        log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + t.getMessage(), t);
                                }
                        }
                }
        }

        /**
         * Registers the Subscription that is passed in to the UDDI node for
         * this clerk.
         *
         * Note, if registration fails, no exception is thrown
         *
         * @param subscription
         * @return a subscription object, or null if failed
         */
        public Subscription register(Subscription subscription) {
                return register(subscription, this.getUDDINode().getApiNode());
        }

        /**
         * Register a Subscription to UDDI node passed in. Make sure you use a
         * clerk that has credentials for this node. Note, if registration
         * fails, no exception is thrown
         *
         * @param subscription a UDDI subscription, remember only one filter
         * type is allowed by UDDI
         * @param node the UDDI node referenced from the config file
         * @return the potentially UDDI server modified subscription
         */
        public Subscription register(Subscription subscription, Node node) {

                log.info("Registering subscription with key " + subscription.getSubscriptionKey());
                Holder<List<Subscription>> holder = new Holder<List<Subscription>>();
                try {
                        List<Subscription> subscriptions = new ArrayList<Subscription>();
                        subscriptions.add(subscription);
                        holder.value = subscriptions;
                        getUDDINode().getTransport().getUDDISubscriptionService(node.getSubscriptionUrl()).
                                saveSubscription(getAuthToken(node.getSecurityUrl()), holder);
                        if (log.isDebugEnabled()) {
                                log.debug("Registering subscription " + subscription.getSubscriptionKey() + " completed.");
                        }
                } catch (Exception e) {
                        log.error("Unable to register subscription " + subscription.getSubscriptionKey()
                                + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to register subscriptionl " + subscription.getSubscriptionKey()
                                + " ." + t.getMessage(), t);
                }
                subscription = holder.value.get(0);
                return subscription;
        }

        /**
         * Register a tModel, using the node of current clerk ('this'). Note, if
         * registration fails, no exception is thrown
         *
         * @param tModel
         * @return the TModelDetail of the newly registered TModel
         */
        public TModelDetail register(TModel tModel) {
                return register(tModel, this.getUDDINode().getApiNode());
        }

        /**
         * Register a tModel. Note, if registration fails, no exception is
         * thrown
         *
         * @param tModel
         * @param node
         * @return a tModel object or null if registration failed
         */
        public TModelDetail register(TModel tModel, Node node) {
                TModelDetail tModelDetail = null;
                log.info("Registering tModel with key " + tModel.getTModelKey());
                try {
                        SaveTModel saveTModel = new SaveTModel();
                        saveTModel.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        saveTModel.getTModel().add(tModel);
                        tModelDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveTModel(saveTModel);
                        if (log.isDebugEnabled()) {
                                log.debug("Registering tModel " + tModel.getTModelKey() + " completed.");
                        }
                } catch (Exception e) {
                        log.error("Unable to register tModel " + tModel.getTModelKey()
                                + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to register tModel " + tModel.getTModelKey()
                                + " ." + t.getMessage(), t);
                }
                return tModelDetail;
        }

        /**
         * Register a BindingTemplate, using the node of current clerk ('this').
         * Note, if registration fails, no exception is thrown
         *
         * @param binding
         * @return a binding object or null
         */
        public BindingTemplate register(BindingTemplate binding) {
                return register(binding, this.getUDDINode().getApiNode());
        }

        /**
         * Register a BindingTemplate. Note, if registration fails, no exception
         * is thrown
         *
         * @param binding
         * @param node
         * @return a binding object or null
         */
        public BindingTemplate register(BindingTemplate binding, Node node) {

                BindingTemplate bindingTemplate = null;
                log.info("Registering bindingTemplate with key " + binding.getBindingKey());
                try {
                        SaveBinding saveBinding = new SaveBinding();
                        saveBinding.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        saveBinding.getBindingTemplate().add(binding);
                        BindingDetail bindingDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveBinding(saveBinding);
                        bindingTemplate = bindingDetail.getBindingTemplate().get(0);
                        if (log.isDebugEnabled()) {
                                log.debug("Registering template binding " + bindingTemplate.getBindingKey() + " completed.");
                        }
                } catch (Exception e) {
                        log.error("Unable to register template binding " + binding.getBindingKey()
                                + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to register template binding " + binding.getBindingKey()
                                + " ." + t.getMessage(), t);
                }
                return bindingTemplate;
        }

        /**
         * Register a service, using the node of current clerk ('this'). Note,
         * if registration fails, no exception is thrown and null is returned
         *
         * @param service
         * @return a service object or null
         */
        public BusinessService register(BusinessService service) {
                return register(service, this.getUDDINode().getApiNode());
        }

        /**
         * Register a service.
         *
         * Note, if registration fails, no exception is thrown
         *
         * @param service the element returned by the server, it may be modified
         * from the original
         * @param node
         * @return the potentially modified service by the UDDI server or NULL if save failed
         */
        public BusinessService register(BusinessService service, Node node) {

                BusinessService businessService = null;
                if (service.getName().isEmpty()){
                        log.error("Unable to register service, a 'name' element is required");
                        return null;
                }
                log.info("Registering service " + service.getName().get(0).getValue()
                        + " with key " + service.getServiceKey());
                try {
                        SaveService saveService = new SaveService();
                        saveService.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        saveService.getBusinessService().add(service);
                        ServiceDetail serviceDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveService(saveService);
                        businessService = serviceDetail.getBusinessService().get(0);
                        if (log.isDebugEnabled()) {
                                log.debug("Registering service " + service.getName().get(0).getValue() + " completed.");
                        }
                } catch (Exception e) {
                        log.error("Unable to register service " + service.getName().get(0).getValue()
                                + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to register service " + service.getName().get(0).getValue()
                                + " ." + t.getMessage(), t);
                }
                return businessService;
        }

        /**
         * registers a UDDI business. This is a convenience wrapper
         *
         * @param business
         * @return a possibility modified business entity as registered or NULL if the save failed
         */
        public BusinessEntity register(BusinessEntity business) {
                return register(business, this.getUDDINode().getApiNode());
        }

        /**
         * Registers a UDDI Business referencing the specified Node from the
         * config file
         *
         * @param business
         * @param node
         * @return a possibility modified business entity as registered or NULL if the save failed
         */
        public BusinessEntity register(BusinessEntity business, Node node) {

                if (business.getName().get(0) == null) {
                        log.error("Unable to register business because no Name elements have been added.");
                        return null;
                }
                BusinessEntity businessEntity = null;
                log.info("Registering business " + business.getName().get(0).getValue()
                        + " with key " + business.getBusinessKey());
                try {
                        SaveBusiness saveBusiness = new SaveBusiness();
                        saveBusiness.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        saveBusiness.getBusinessEntity().add(business);
                        BusinessDetail businessDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveBusiness(saveBusiness);
                        businessEntity = businessDetail.getBusinessEntity().get(0);
                        if (log.isDebugEnabled()) {
                                log.debug("Registering businessEntity " + businessEntity.getName().get(0).getValue() + " completed.");
                        }
                } catch (Exception e) {
                        log.error("Unable to register business " + business.getName().get(0).getValue()
                                + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to register business " + business.getName().get(0).getValue()
                                + " ." + t.getMessage(), t);
                }
                return businessEntity;
        }

        /**
         * removes a business from UDDI. This is a convenience wrapper Note, if
         * registration fails, no exception is thrown
         *
         * @param businessKey
         */
        public void unRegisterBusiness(String businessKey) {
                unRegisterBusiness(businessKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the service with specified serviceKey using the specified
         * Node, as defined in the config file Note, if registration fails, no
         * exception is thrown
         *
         * @param businessKey
         * @param node
         */
        public void unRegisterBusiness(String businessKey, Node node) {
                log.info("UnRegistering the business " + businessKey);
                try {
                        DeleteBusiness deleteBusiness = new DeleteBusiness();
                        deleteBusiness.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        deleteBusiness.getBusinessKey().add(businessKey);
                        getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteBusiness(deleteBusiness);
                } catch (Exception e) {
                        log.error("Unable to register service " + businessKey
                                + " ." + e.getMessage(), e);
                }
        }

        /**
         * removes a service by key. This is a convenience wrapper Note, if
         * registration fails, no exception is thrown
         *
         * @param serviceKey
         */
        public void unRegisterService(String serviceKey) {
                unRegisterService(serviceKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the service with specified serviceKey. Note, if
         * registration fails, no exception is thrown
         *
         * @param serviceKey
         * @param node
         */
        public void unRegisterService(String serviceKey, Node node) {
                log.info("UnRegistering the service " + serviceKey);
                try {
                        DeleteService deleteService = new DeleteService();
                        deleteService.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        deleteService.getServiceKey().add(serviceKey);
                        getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteService(deleteService);
                } catch (Exception e) {
                        log.error("Unable to register service " + serviceKey
                                + " ." + e.getMessage(), e);
                }
        }

        /**
         * removes a binding by key. This is a convenience wrapper Note, if
         * registration fails, no exception is thrown
         *
         * @param bindingKey
         */
        public void unRegisterBinding(String bindingKey) {
                unRegisterBinding(bindingKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the BindingTemplate with specified bindingKey and
         * referenced node defined in the config file Note, if registration
         * fails, no exception is thrown
         *
         * @param bindingKey
         * @param node
         */
        public void unRegisterBinding(String bindingKey, Node node) {
                log.info("UnRegistering binding key " + bindingKey);
                try {
                        DeleteBinding deleteBinding = new DeleteBinding();
                        deleteBinding.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        deleteBinding.getBindingKey().add(bindingKey);
                        getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteBinding(deleteBinding);
                } catch (Exception e) {
                        log.error("Unable to unregister bindingkey " + bindingKey
                                + " ." + e.getMessage(), e);
                }
        }

        /**
         * removes a tModel. Note, UDDI just flags tModels as deleted, it will
         * still be accessible but not returned in a search. This is a
         * convenience wrapper Note, if registration fails, no exception is
         * thrown
         *
         * @param tModelKey
         */
        public void unRegisterTModel(String tModelKey) {
                unRegisterTModel(tModelKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the BindingTemplate with specified bindingKey. Note, if
         * registration fails, no exception is thrown
         *
         * @param tModelKey
         * @param node
         */
        public void unRegisterTModel(String tModelKey, Node node) {
                log.info("UnRegistering tModel key " + tModelKey);
                try {
                        String authToken = getAuthToken(node.getSecurityUrl());
                        DeleteTModel deleteTModel = new DeleteTModel();
                        deleteTModel.setAuthInfo(authToken);
                        deleteTModel.getTModelKey().add(tModelKey);
                        getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteTModel(deleteTModel);
                } catch (Exception e) {
                        log.error("Unable to unregister tModelkey " + tModelKey
                                + " ." + e.getMessage(), e);
                }
        }

        /**
         * removes a subscription by key. This is a convenience wrapper Note, if
         * registration fails, no exception is thrown
         *
         * @param subscriptionKey
         */
        public void unRegisterSubscription(String subscriptionKey) {
                unRegisterSubscription(subscriptionKey, this.getUDDINode().getApiNode());
        }

        /**
         * removes a subscription by key, referencing the specified node in the
         * config file Note, if registration fails, no exception is thrown
         *
         * @param subscriptionKey
         * @param node
         */
        public void unRegisterSubscription(String subscriptionKey, Node node) {
                log.info("UnRegistering subscription with key " + subscriptionKey);
                try {
                        String authToken = getAuthToken(node.getSecurityUrl());
                        DeleteSubscription deleteSubscription = new DeleteSubscription();
                        deleteSubscription.setAuthInfo(authToken);
                        deleteSubscription.getSubscriptionKey().add(subscriptionKey);
                        getUDDINode().getTransport().getUDDISubscriptionService(node.getSubscriptionUrl()).deleteSubscription(deleteSubscription);
                } catch (Exception e) {
                        log.error("Unable to unregister subscription key " + subscriptionKey
                                + " ." + e.getMessage(), e);
                }
        }

        /**
         * finds a tmodel. This is a convenience wrapper
         *
         * @param findTModel
         * @return null if not found or error
         * @throws RemoteException
         * @throws ConfigurationException
         * @throws TransportException
         */
        public TModelList findTModel(FindTModel findTModel) throws RemoteException, ConfigurationException, TransportException {
                return findTModel(findTModel, this.getUDDINode().getApiNode());
        }

        /**
         * finds a tModel
         *
         * @param findTModel
         * @param node
         * @return null if not found or error
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public TModelList findTModel(FindTModel findTModel, Node node) throws RemoteException,
                TransportException, ConfigurationException {

                findTModel.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                try {
                        TModelList tModelList = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).findTModel(findTModel);
                        return tModelList;
                } catch (DispositionReportFaultMessage dr) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                        checkForErrorInDispositionReport(report, null, null);
                } catch (SOAPFaultException sfe) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
                        checkForErrorInDispositionReport(report, null, null);
                } catch (UndeclaredThrowableException ute) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
                        checkForErrorInDispositionReport(report, null, null);
                }
                return null;
        }

        /**
         * Gets the details of a tModel by the key. This is a convenience
         * wrapper
         *
         * @param tModelKey
         * @return null if not found or error, otherwise the details of the
         * tModel(s)
         * @throws RemoteException
         * @throws ConfigurationException
         * @throws TransportException
         */
        public TModelDetail getTModelDetail(String tModelKey) throws RemoteException, ConfigurationException, TransportException {
                GetTModelDetail getTModelDetail = new GetTModelDetail();
                getTModelDetail.getTModelKey().add(tModelKey);
                return getTModelDetail(getTModelDetail);
        }

        /**
         * Gets the details of a tModel by the key. This is a convenience
         * wrapper
         *
         * @param getTModelDetail
         * @return null if not found or error
         * @throws RemoteException
         * @throws ConfigurationException
         * @throws TransportException
         */
        public TModelDetail getTModelDetail(GetTModelDetail getTModelDetail) throws RemoteException, ConfigurationException, TransportException {
                return getTModelDetail(getTModelDetail, this.getUDDINode().getApiNode());
        }

        /**
         * Gets the details of a tModel by the key using the referenced Node
         * from the config file
         *
         * @param getTModelDetail
         * @param node
         * @return null if not found or error
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public TModelDetail getTModelDetail(GetTModelDetail getTModelDetail, Node node) throws RemoteException,
                TransportException, ConfigurationException {

                getTModelDetail.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                try {
                        TModelDetail tModelDetail = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getTModelDetail(getTModelDetail);
                        return tModelDetail;
                } catch (DispositionReportFaultMessage dr) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                        checkForErrorInDispositionReport(report, null, null);
                } catch (SOAPFaultException sfe) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
                        checkForErrorInDispositionReport(report, null, null);
                } catch (UndeclaredThrowableException ute) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
                        checkForErrorInDispositionReport(report, null, null);
                }
                return null;
        }

        /**
         * Finds a service by the key, . This is a convenience wrapper
         *
         * @param serviceKey
         * @return null if not found or error
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public BusinessService getServiceDetail(String serviceKey) throws RemoteException,
                TransportException, ConfigurationException {
                return getServiceDetail(serviceKey, this.getUDDINode().getApiNode());
        }

        /**
         * Finds a service by the key, . This is a convenience wrapper
         *
         * @param serviceKey
         * @return null if not found or error
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         * @deprecated see getServiceDetail
         */
        @Deprecated
        public BusinessService findService(String serviceKey) throws RemoteException,
                TransportException, ConfigurationException {
                return getServiceDetail(serviceKey, this.getUDDINode().getApiNode());
        }

        /**
         * Finds a service by the key, referencing the specific Node from the
         * configuration file
         *
         * @param serviceKey
         * @param node
         * @return null if not found or error
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         * @deprecated see getServiceDetail
         */
        @Deprecated
        public BusinessService findService(String serviceKey, Node node) throws RemoteException,
                TransportException, ConfigurationException {
                return getServiceDetail(serviceKey, node);
        }

        /**
         * Finds a service by the key, referencing the specific Node from the
         * configuration file
         *
         * @param serviceKey
         * @param node
         * @return null if not found or error
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public BusinessService getServiceDetail(String serviceKey, Node node) throws RemoteException,
                TransportException, ConfigurationException {
                GetServiceDetail getServiceDetail = new GetServiceDetail();
                getServiceDetail.getServiceKey().add(serviceKey);
                getServiceDetail.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                try {
                        ServiceDetail sd = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getServiceDetail(getServiceDetail);
                        List<BusinessService> businessServiceList = sd.getBusinessService();
                        if (businessServiceList.size() == 0) {
                                throw new ConfigurationException("Could not find Service with key=" + serviceKey);
                        }
                        return businessServiceList.get(0);
                } catch (DispositionReportFaultMessage dr) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, serviceKey);
                } catch (SOAPFaultException sfe) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, serviceKey);
                } catch (UndeclaredThrowableException ute) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, serviceKey);
                }
                return null;
        }

        /**
         *
         * @param bindingKey
         * @return returns a binding template or null if not found
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         * @deprecated see getServiceBindingDetail
         */
        @Deprecated
        public BindingTemplate findServiceBinding(String bindingKey) throws DispositionReportFaultMessage, RemoteException,
                TransportException, ConfigurationException {
                return getServiceBindingDetail(bindingKey, this.getUDDINode().getApiNode());
        }

        /**
         * gets a binding detail by key
         *
         * @param bindingKey
         * @return null if not found or error, or the binding template
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public BindingTemplate getServiceBindingDetail(String bindingKey) throws DispositionReportFaultMessage, RemoteException,
                TransportException, ConfigurationException {
                return getServiceBindingDetail(bindingKey, this.getUDDINode().getApiNode());
        }

        /**
         * @deprecated use getServiceBindingDetail instead
         * @param bindingKey
         * @param node
         * @return null if not found
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         * @deprecated
         */
        @Deprecated
        public BindingTemplate findServiceBinding(String bindingKey, Node node) throws DispositionReportFaultMessage, RemoteException,
                TransportException, ConfigurationException {
                return getServiceBindingDetail(bindingKey, node);

        }

        /**
         * Gets the details of a specific service binding key
         *
         * @param bindingKey
         * @param node
         * @return null if not found, or error, or the details of the binding
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public BindingTemplate getServiceBindingDetail(String bindingKey, Node node) throws DispositionReportFaultMessage, RemoteException,
                TransportException, ConfigurationException {
                GetBindingDetail getBindingDetail = new GetBindingDetail();
                getBindingDetail.getBindingKey().add(bindingKey);
                getBindingDetail.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                try {
                        BindingDetail bd = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getBindingDetail(getBindingDetail);
                        List<BindingTemplate> bindingTemplateList = bd.getBindingTemplate();
                        if (bindingTemplateList.size() == 0) {
                                throw new ConfigurationException("Could not find ServiceBinding with key=" + bindingKey);
                        }
                        return bindingTemplateList.get(0);
                } catch (DispositionReportFaultMessage dr) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, bindingKey);
                } catch (SOAPFaultException sfe) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, bindingKey);
                } catch (UndeclaredThrowableException ute) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, bindingKey);
                }
                return null;
        }

        /**
         * finds a business
         *
         * @deprecated Use getBusinessDetail instead
         * @param businessKey
         * @return BusinessEntity if found, or null if not found.
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        @Deprecated
        public BusinessEntity findBusiness(String businessKey) throws RemoteException,
                TransportException, ConfigurationException {
                return getBusinessDetail(businessKey, this.getUDDINode().getApiNode());
        }

        /**
         * Looks up the BusinessEntiry in the registry, will return null if is
         * not found.
         *
         * @deprecated Use getBusinessDetail instead
         * @param businessKey - the key we are looking for
         * @param node - the node which is going to be queried
         * @return BusinessEntity if found, or null if not found.
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        @Deprecated
        public BusinessEntity findBusiness(String businessKey, Node node) throws RemoteException,
                TransportException, ConfigurationException {
                return getBusinessDetail(businessKey, node);
        }

        /**
         * Looks up the BusinessEntiry in the registry, will return null if is
         * not found.
         *
         * @param businessKey - the key we are looking for
         * @return BusinessEntity is found, or null if not found.
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public BusinessEntity getBusinessDetail(String businessKey) throws RemoteException,
                TransportException, ConfigurationException {
                return getBusinessDetail(businessKey, this.getUDDINode().getApiNode());
        }

        /**
         * Looks up the BusinessEntiry in the registry, will return null if is
         * not found.
         *
         * @param businessKey - the key we are looking for
         * @param node - the node which is going to be queried
         * @return BusinessEntity is found, or null if not found.
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public BusinessEntity getBusinessDetail(String businessKey, Node node) throws RemoteException,
                TransportException, ConfigurationException {
                GetBusinessDetail getBusinessDetail = new GetBusinessDetail();
                getBusinessDetail.getBusinessKey().add(businessKey);
                getBusinessDetail.setAuthInfo(node.getSecurityUrl());
                try {
                        BusinessDetail bd = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getBusinessDetail(getBusinessDetail);
                        return bd.getBusinessEntity().get(0);
                } catch (DispositionReportFaultMessage dr) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
                } catch (SOAPFaultException sfe) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
                } catch (UndeclaredThrowableException ute) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
                }
                return null;
        }

        /**
         * Looks up the BusinessEntity in the registry for "related" businesses.
         * This means that there is a business relationship defined. This is
         * also referred to as a "Publisher Assertion",
         *
         * @see PublisherAssertion
         * @see PublisherAssertions
         * @param businessKey - the key we are looking for
         * @param node - the node which is going to be queried
         * @return BusinessEntity is found, or null if not found.
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public RelatedBusinessesList findRelatedBusinesses(String businessKey, Node node) throws RemoteException,
                TransportException, ConfigurationException {
                FindRelatedBusinesses findRelatedBusinesses = new FindRelatedBusinesses();
                findRelatedBusinesses.setBusinessKey(businessKey);
                findRelatedBusinesses.setAuthInfo(node.getSecurityUrl());
                try {
                        RelatedBusinessesList rbl = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).findRelatedBusinesses(findRelatedBusinesses);
                        return rbl;
                } catch (DispositionReportFaultMessage dr) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
                } catch (SOAPFaultException sfe) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
                } catch (UndeclaredThrowableException ute) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
                        checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
                }
                return null;
        }

        private void checkForErrorInDispositionReport(DispositionReport report, String Error, String entityKey) {

                if (entityKey != null && report != null && report.countainsErrorCode(DispositionReport.E_INVALID_KEY_PASSED)) {
                        log.info("entityKey " + entityKey + " was not found in the registry");
                } else {
                        if (report == null) {
                                log.info("Missing DispositionReport");
                        } else {
                                for (Result result : report.getResult()) {
                                        log.error(result.getErrInfo().getErrCode() + " " + result.getErrInfo().getValue());
                                }
                        }
                }
        }

        /**
         * kills the current auth token, aka Logout current Node
         */
        public void discardAuthToken() {
                discardAuthToken(authToken);
        }

        /**
         * kills the provided auth token, aka Logout
         *
         * @param token
         */
        public void discardAuthToken(String token) {
                if (token != null) {
                        try {
                                DiscardAuthToken discardAuthToken = new DiscardAuthToken();
                                discardAuthToken.setAuthInfo(token);
                                getUDDINode().getTransport().getUDDISecurityService(getUDDINode().getSecurityUrl()).discardAuthToken(discardAuthToken);
                                token = null;
                        } catch (Exception ex) {
                                log.warn("Error discarding auth token: " + ex.getMessage());
                                log.debug("Error discarding auth token: " + ex.getMessage(), ex);
                        }
                }
        }

        /**
         * kills the provided auth token, aka Logout
         *
         * @param token
         * @param endpoint
         */
        public void discardAuthToken(String token, String endpoint) {
                if (token != null) {
                        try {
                                DiscardAuthToken discardAuthToken = new DiscardAuthToken();
                                discardAuthToken.setAuthInfo(token);
                                getUDDINode().getTransport().getUDDISecurityService(endpoint).discardAuthToken(discardAuthToken);
                                token = null;
                        } catch (Exception ex) {
                                log.warn("Error discarding auth token: " + ex.getMessage());
                                log.debug("Error discarding auth token: " + ex.getMessage(), ex);
                        }
                }
        }

        /**
         * Gets an auth token from the uddi server using the uddi auth token
         * <br>
         * Notice: never log auth tokens! Treat it like a password
         *
         * Calls 
         * @return  getAuthToken(getUDDINode().getSecurityUrl());
         * @throws TransportException
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         * @since 3.2
         */
        public String getAuthToken() throws TransportException, DispositionReportFaultMessage, RemoteException {
                return getAuthToken(getUDDINode().getSecurityUrl());
        }
        /**
         * Gets an auth token from the uddi server using the uddi auth token
         * <br>
         * Notice: never log auth tokens! Treat it like a password
         *
         * notes: changed to public to have access from the subscription
         * callback API 8/20/2013 AO
         *
         * @param endpointURL
         * @return an auth token
         * @throws TransportException
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         */
        public String getAuthToken(String endpointURL) throws TransportException, DispositionReportFaultMessage, RemoteException {
                //if the token is older then 10 minutes discard it, and create a new one.
                if ((authToken != null && !"".equals(authToken)) && (tokenBirthDate != null && System.currentTimeMillis() > tokenBirthDate.getTime() + 600000)) {
                        DiscardAuthToken discardAuthToken = new DiscardAuthToken();
                        discardAuthToken.setAuthInfo(authToken);
                        getUDDINode().getTransport().getUDDISecurityService(endpointURL).discardAuthToken(discardAuthToken);
                        authToken = null;
                }
                if (authToken == null || "".equals(authToken)) {
                        if (getPublisher()==null || getPassword()==null){
                                log.warn("No credentials provided for login!");
                                return null;
                        }
                        tokenBirthDate = new Date();
                        GetAuthToken getAuthToken = new GetAuthToken();
                        getAuthToken.setUserID(getPublisher());
                        if (isencrypted) {
                                if (cryptoProvider == null) {
                                        log.fatal("Credentials are encrypted but no cryptoProvider was defined in the config file!");
                                } else {
                                        try {
                                                getAuthToken.setCred(CryptorFactory.getCryptor(this.cryptoProvider).decrypt(getPassword()));
                                        } catch (Exception ex) {
                                                log.fatal("Unable to decrypt credentials! sending it as is", ex);
                                                getAuthToken.setCred(getPassword());
                                        }
                                }
                        } else {
                                log.warn("Hey, I couldn't help but notice that your credentials aren't encrypted. Please consider doing so");
                                getAuthToken.setCred(getPassword());
                        }
                        authToken = getUDDINode().getTransport().getUDDISecurityService(endpointURL).getAuthToken(getAuthToken).getAuthInfo();
                }
                return authToken;
        }

        /**
         * This calls a jUDDI implementation specific API call and is used to
         * help configure internode communication between jUDDI servers. This is
         * NOT part of the UDDI specification.<br>
         * Note: this API call should only be used with secure ports (SSL/TLS)
         *
         * @param node
         * @return a node or null if registration fails
         */
        public NodeDetail saveNode(Node node) {
                NodeDetail nodeDetail = null;
                try {
                        log.info("Sending Node " + node.getName() + " info to jUDDI " + getUDDINode().getName());
                        SaveNode saveNode = new SaveNode();
                        saveNode.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                        saveNode.getNode().add(node);
                        nodeDetail = getUDDINode().getTransport().getJUDDIApiService(node.getJuddiApiUrl()).saveNode(saveNode);
                } catch (Exception e) {
                        log.error("Unable to save node " + node.getName()
                                + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to save node " + node.getName()
                                + " ." + t.getMessage(), t);
                }
                return nodeDetail;
        }

        /**
         * This calls a jUDDI implementation specific API call and is used to
         * help configure internode communication between jUDDI servers. This is
         * NOT part of the UDDI specification.<br>
         * Note: this API call should only be used with secure ports (SSL/TLS)
         *
         * @param senderClerk
         * @return null if registration fails
         */
        public ClerkDetail saveClerk(UDDIClerk senderClerk) {
                ClerkDetail clerkDetail = null;
                try {
                        log.debug("Sending Clerk " + senderClerk.getName() + " info to jUDDI " + getUDDINode().getName());
                        SaveClerk saveClerk = new SaveClerk();
                        saveClerk.setAuthInfo(getAuthToken(senderClerk.getUDDINode().getSecurityUrl()));
                        saveClerk.getClerk().add(getApiClerk());
                        clerkDetail = getUDDINode().getTransport().getJUDDIApiService(senderClerk.getUDDINode().getJuddiApiUrl()).saveClerk(saveClerk);
                } catch (Exception e) {
                        log.error("Unable to save clerk " + getName()
                                + " ." + e.getMessage(), e);
                } catch (Throwable t) {
                        log.error("Unable to save clerk " + getName()
                                + " ." + t.getMessage(), t);
                }
                return clerkDetail;
        }

        /**
         * The API Clerk is the JAXWS generated "Clerk" element that is used
         * both on the jUDDI Client and on the jUDDI Web Service (not part of
         * the UDDI spec).
         *
         * @return the config file defined clerk
         */
        public Clerk getApiClerk() {
                Clerk apiClerk = new Clerk();
                apiClerk.setName(name);
                apiClerk.setNode(uddiNode.getApiNode());
                apiClerk.setPassword(password);
                apiClerk.setPublisher(publisher);
                return apiClerk;
        }

        /**
         * client.clerks.clerk(" + i + ")[@name]
         *
         * @return name
         */
        public String getName() {
                return name;
        }

        /**
         * client.clerks.clerk(" + i + ")[@name]
         *
         * @param name
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * client.clerks.clerk(" + i + ")[@node] reference to UDDI Node
         *
         * @param uddiNode
         */
        public void setUDDINode(UDDINode uddiNode) {
                this.uddiNode = uddiNode;
        }

        /**
         * This is the username client.clerks.clerk(" + i + ")[@publisher]
         *
         * @return the username
         */
        public String getPublisher() {
                return publisher;
        }

        /**
         * This is the username client.clerks.clerk(" + i + ")[@publisher]
         *
         * @param publisher
         */
        public void setPublisher(String publisher) {
                this.publisher = publisher;
        }

        /**
         * If the password is encrypted, it will be decrypted if possible,
         * otherwise the cipher text will be returned. client.clerks.clerk(" + i
         * + ")[@password]
         *
         * @return unencrypted password
         */
        public String getPassword() {
                if (isencrypted) {
                        try {
                                return CryptorFactory.getCryptor(cryptoProvider).decrypt(password);
                        } catch (Exception ex) {
                                log.fatal("Unable to decrypt the password", ex);
                        }
                }
                return password;
        }

        /**
         * If the password is encrypted, it cipher text is returned, otherwise
         * the clear text will be returned. client.clerks.clerk(" + i +
         * ")[@password]
         *
         * @return password as is in the config file
         */
        public String getRawPassword() {
                return password;
        }

        /**
         * Use with caution, don't forget to set the IsEncrypted and Crypto
         * provider client.clerks.clerk(" + i + ")[@password]
         *
         * @param password
         */
        public void setPassword(String password) {
                this.password = password;
        }

        /**
         * Used for the automated registration of services via WSDL2UDDI<br>
         * config file: client.clerks.clerk(" + i + ").wsdl
         *
         * @return a WSDL array
         * @see WSDL2UDDI
         */
        public WSDL[] getWsdls() {
                return wsdls;
        }

        /**
         * Used for the automated registration of services via WSDL2UDDI<br>
         * config file: client.clerks.clerk(" + i + ").wsdl
         *
         * @param wsdls
         * @see WSDL2UDDI
         */
        public void setWsdls(WSDL[] wsdls) {
                this.wsdls = wsdls;
        }

        /**
         * client.clerks.clerk(" + i + ")[@cryptoProvider]
         *
         * @param clazz
         */
        public void setCryptoProvider(String clazz) {
                this.cryptoProvider = clazz;
        }

        /**
         * client.clerks.clerk(" + i + ")[@isPasswordEncrypted] default is false
         *
         * @param option
         */
        public void setIsPasswordEncrypted(boolean option) {
                this.isencrypted = option;
        }

        /**
         * client.clerks.clerk(" + i + ")[@cryptoProvider]
         *
         * @return may be null if not defined
         */
        public String getCryptoProvider() {
                return this.cryptoProvider;
        }

        /**
         * client.clerks.clerk(" + i + ")[@isPasswordEncrypted]
         *
         * @return true/false
         */
        public boolean getIsPasswordEncrypted() {
                return this.isencrypted;

        }

        /**
         * Internal classed used for wsdl registration
         */
        public class WSDL {

                private String businessKey;
                private String keyDomain;
                private String fileName;

                public String getBusinessKey() {
                        return businessKey;
                }

                public void setBusinessKey(String businessKey) {
                        this.businessKey = businessKey;
                }

                public String getFileName() {
                        return fileName;
                }

                public void setFileName(String fileName) {
                        this.fileName = fileName;
                }

                public String getKeyDomain() {
                        return keyDomain;
                }

                public void setKeyDomain(String keyDomain) {
                        this.keyDomain = keyDomain;
                }
        }

        /**
         * A helper function to create a tModel key generator.<br>
         * Why would I want a key generator? In UDDIv3, you're suppose to
         * specify what you want the keys (unique identifiers) to be, however
         * there's a number of naming rules associated with the keys. Generally,
         * use the FQDN of your business or organization. Optionally, when
         * saving an UDDI entity, you can just leave the key name blank and the
         * server should generate one for you. It's normally a UUID that's not
         * easy to remember. In this case, there's no need to call this method.
         * <br><br>
         * In addition, no changes are made to the UDDI server. You'll have to
         * do that one using code similar to this:
         * <pre>
         * UDDIClerk clerk = ...
         * TModel keygen = UDDIClerk.createKeyGenator("uddi:mydomain.com:keygenerator", "my domain", "en");
         * clerk.register(keygen);
         *
         * @param partitionName think of this as the domain, i.e. juddi.apache.org, but it can really be anything you want. This will become part of the
         * key associated with the tModel generator (uddi:juddi.apache.org:keygenerator). The colon ":" is the delimitor, so avoid using them unless you want to
         * create nexted key domains. If you're missing the "uddi:" prefix or the ":keygenerator" suffix, it will be added automatically.
         * @param DescriptiveName required. max length is 255 char
         * @param DescriptiveNameLanguage optional, max length is 26 char
         * @return a populated tModel entity representing a tModel key
         * generator. No changes are made to any connect UDDI service
         * @since 3.2
         */
        public static TModel createKeyGenator(String partitionName, String DescriptiveName, String DescriptiveNameLanguage) {
                if (partitionName == null || partitionName.length() == 0 || partitionName.length() > 255) {
                        throw new IllegalArgumentException();
                }

                if (DescriptiveName == null || DescriptiveName.length() == 0 || DescriptiveName.length() > 255) {
                        throw new IllegalArgumentException();
                }
                if (!partitionName.startsWith("uddi:")) {
                        //throw new IllegalArgumentException("partitionName must have a 'uddi:' prefix");
                        partitionName = "uddi:" + partitionName;

                }
                if (!partitionName.endsWith(":keygenerator")) {
                        //throw new IllegalArgumentException("partitionName must have a ':keygenerator' postfix");
                        partitionName = partitionName + ":keygenerator";
                }
                TModel tm = new TModel();
                tm.setName(new Name());
                tm.getName().setValue(DescriptiveName);
                tm.getName().setLang(DescriptiveNameLanguage);
                tm.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey(UDDIConstants.KEY_GENERATOR_TMODEL);
                kr.setKeyName(UDDIConstants.KEY_GENERATOR);
                kr.setKeyValue(UDDIConstants.KEY_GENERATOR_VALUE);
                tm.getCategoryBag().getKeyedReference().add(kr);
                OverviewDoc overviewDoc = new OverviewDoc();
                OverviewURL overviewUrl = new OverviewURL();
                overviewUrl.setUseType("text");
                overviewUrl.setValue("http://uddi.org/pubs/uddi_v3.htm#keyGen");
                overviewDoc.setOverviewURL(overviewUrl);
                tm.getOverviewDoc().add(overviewDoc);
                tm.setTModelKey(partitionName.toLowerCase());
                return tm;
        }

        /**
         * This is a convenience function that will build and return a
         * TModelInstanceInfo as described in the following link that will
         * enable you to tag web services registered in UDDI with some kind of
         * version information.<Br><Br>
         * Article source: <a
         * href="http://www.ibm.com/developerworks/webservices/library/ws-version/">http://www.ibm.com/developerworks/webservices/library/ws-version/</a>
         * <Br><Br>
         *
         * When using this tModel as a tModelInstance, it can be used to
         * describe a version associated with either a service interface, a
         * bindingTemplate service instance. Note: This is a jUDDI specific
         * addon and may not be present in other registries
         *
         * @param version From the article, no specificity is provided on what
         * to use as a value, but we recommend that you use the string
         * representation of major.minor[.build[.revision]].<br>
         * Example
         * <ul>
         * <li>6.1.2.3</li>
         * <li>1.0</li>
         * <li>0.1</li>
         * </ul>
         * @return TModelInstanceInfo populated as described in the article,
         * plus some descriptive information
         */
        public static TModelInstanceInfo createServiceInterfaceVersion(String version, String lang) throws IllegalArgumentException {
                if (version == null) {
                        throw new IllegalArgumentException();
                }
                TModelInstanceInfo tt = new TModelInstanceInfo();
                tt.setTModelKey(UDDIConstants.VERSION_TMODEL);
                tt.setInstanceDetails(new InstanceDetails());
                tt.getInstanceDetails().setInstanceParms(version);

                OverviewDoc doc = new OverviewDoc();
                doc.setOverviewURL(new OverviewURL("http://www.ibm.com/developerworks/webservices/library/ws-version/", "text"));
                doc.getDescription().add(new Description(
                        "Describes a version associated with either a service interface, a bindingTemplate service instance.", lang));
                tt.getDescription().add(new Description("Describes a version associated with either a service interface, a bindingTemplate service instance.", lang));
                tt.getInstanceDetails().getOverviewDoc().add(doc);
                return tt;
        }

        /**
         * This is a convenience function that will filter a list of binding
         * templates and return a list of bindings matching the specified
         * version number.
         *
         * This implements and expands upon service versioning described in the
         * following link and will enable you to tag web services registered in
         * UDDI with some kind of version information.<Br><Br>
         * Article source: <a
         * href="http://www.ibm.com/developerworks/webservices/library/ws-version/">http://www.ibm.com/developerworks/webservices/library/ws-version/</a>
         * <Br><Br>
         *
         * @see createServiceInterfaceVersion for more information<Br><br>
         *
         * This function operates using tModelInstances that are used to
         * describe a version associated with either a service interface, a
         * bindingTemplate service instance. Note: This is a jUDDI specific
         * addon and may not be present in other registries
         *
         * @param version From the article, no specificity is provided on what
         * to use as a value, but we recommend that you use the string
         * representation of major.minor[.build[.revision]].<br>
         * Example
         * <ul>
         * <li>6.1.2.3</li>
         * <li>1.0</li>
         * <li>0.1</li>
         * </ul>
         * @param bindingTemplate the input binding template, must not be null
         * @return a list if binding templates where the version equals ignoring
         * case trimmed equals the version value
         * @throws IllegalArgumentException if the version or bindingTemplate is null
         */
        public static Set<BindingTemplate> getBindingByVersion(String version, List<BindingTemplate> bindingTemplate) throws IllegalArgumentException {
                if (version == null) {
                        throw new IllegalArgumentException();
                }
                if (bindingTemplate == null) {
                        throw new IllegalArgumentException();
                }
                Set<BindingTemplate> ret = new HashSet<BindingTemplate>();
                for (int i = 0; i < bindingTemplate.size(); i++) {
                        if (bindingTemplate.get(i).getTModelInstanceDetails() != null) {
                                for (int k = 0; k < bindingTemplate.get(i).getTModelInstanceDetails().getTModelInstanceInfo().size(); k++) {
                                        if (bindingTemplate.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getTModelKey().equalsIgnoreCase(UDDIConstants.VERSION_TMODEL)) {
                                                if (bindingTemplate.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails() != null) {
                                                        if (bindingTemplate.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getInstanceParms() != null) {
                                                                if (bindingTemplate.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getInstanceParms().trim().equalsIgnoreCase(version.trim())) {
                                                                        ret.add(bindingTemplate.get(i));
                                                                        break;
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
                return ret;
        }

        /**
         * JUDDI-700 This implements the "find_endpoints" pattern as described
         * in Alex O'Ree's Master's Thesis on UDDI. Basically, UDDI never
         * provided a 'simple' way to get a list of execution URLs for a web
         * service. This function will resolve all AccessPoint and
         * HostingRedictor indirections and provide to you a list of URLs that
         * <i>should</i> be accessible for the given service.
         *
         * @param serviceKey
         * @return a list of URI endpoints
         */
        public List<String> getEndpoints(String serviceKey) {
                List<String> items = new ArrayList<String>();
                BusinessService serviceDetail = null;
                try {
                        serviceDetail = this.getServiceDetail(serviceKey);
                } catch (Exception ex) {
                        log.error("Unable to fetch the specified service's details", ex);
                }
                if (serviceDetail == null) {
                        return items;
                }

                if (serviceDetail.getBindingTemplates() != null) {
                        for (int k = 0; k < serviceDetail.getBindingTemplates().getBindingTemplate().size(); k++) {
                                try {
                                        items.addAll(ParseBinding(serviceDetail.getBindingTemplates().getBindingTemplate().get(k)));
                                } catch (Exception ex) {
                                        log.warn(ex);
                                }
                        }
                }

                return items;
        }

        private List<String> GetBindingInfo(String value) throws Exception {
                List<String> items = new ArrayList<String>();
                if (value == null) {
                        return items;
                }

                GetBindingDetail b = new GetBindingDetail();
                b.setAuthInfo(getAuthToken(this.getApiClerk().getNode().getSecurityUrl()));
                b.getBindingKey().add(value);
                BindingDetail bindingDetail = getUDDINode().getTransport().getUDDIInquiryService(this.getApiClerk().getNode().getInquiryUrl()).getBindingDetail(b);
                for (int i = 0; i < bindingDetail.getBindingTemplate().size(); i++) {
                        items.addAll(ParseBinding(bindingDetail.getBindingTemplate().get(i)));
                }
                return items;
        }

        private List<String> ParseBinding(BindingTemplate get) throws Exception {
                List<String> items = new ArrayList<String>();
                if (get == null || get.getAccessPoint() == null) {
                        return items;
                }
                if (get.getHostingRedirector() != null) {
                        //hosting Redirector is the same as "reference this other binding template". It's actually deprecated so 
                        //don't expect to see this too often
                        items.addAll(GetBindingInfo(get.getHostingRedirector().getBindingKey()));
                }
                if (get.getAccessPoint() != null) {
                        String usetype = get.getAccessPoint().getUseType();
                        if (usetype == null) {
                                //this is unexpected, usetype is a required field
                                items.add(get.getAccessPoint().getValue());
                        } else if (usetype.equalsIgnoreCase(AccessPointType.BINDING_TEMPLATE.toString())) {
                                //referencing another binding template
                                items.addAll(GetBindingInfo(get.getAccessPoint().getValue()));
                        } else if (usetype.equalsIgnoreCase(AccessPointType.HOSTING_REDIRECTOR.toString())) {
                                //this one is a bit strange. the value should be a binding template

                                items.addAll(GetBindingInfo(get.getAccessPoint().getValue()));

                        } else if (usetype.equalsIgnoreCase(AccessPointType.WSDL_DEPLOYMENT.toString())) {
                                //fetch wsdl and parse
                                items.addAll(FetchWSDL(get.getAccessPoint().getValue()));
                        } else if (usetype.equalsIgnoreCase(AccessPointType.END_POINT.toString())) {
                                items.add(get.getAccessPoint().getValue());
                        } else {
                                //treat it has an extension or whatever
                                log.info("Unable to figure out the useType for " + get.getAccessPoint().getValue());
                                items.add(get.getAccessPoint().getValue());
                        }

                }
                return items;
        }

        /**
         * fetches a wsdl endpoint and parses for execution urls
         *
         * @param value
         * @return a list of endpoints from a WSDL provided it is reachable and parsable 
         */
        private List<String> FetchWSDL(String value) {
                List<String> items = new ArrayList<String>();

                if (value.startsWith("http://") || value.startsWith("https://")) {
                        //here, we need an HTTP Get for WSDLs
                        org.apache.juddi.v3.client.mapping.wsdl.ReadWSDL r = new ReadWSDL();
                        r.setIgnoreSSLErrors(true);
                        try {
                                Definition wsdlDefinition = r.readWSDL(new URL(value));
                                Properties properties = new Properties();

                                properties.put("keyDomain", "domain");
                                properties.put("businessName", "biz");
                                properties.put("serverName", "localhost");
                                properties.put("serverPort", "80");

                                WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
                                BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
                                for (int i = 0; i < businessServices.getBusinessService().size(); i++) {
                                        if (businessServices.getBusinessService().get(i).getBindingTemplates() != null) {
                                                for (int k = 0; k < businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                                                        items.addAll(ParseBinding(businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k)));
                                                }
                                        }
                                }
                        } catch (Exception ex) {
                                log.error(ex);
                        }

                }
                return items;
        }

        /**
         * Gets service details or NULL if it doesn't exist or an error occurred
         *
         * @param getDetail
         * @return null if not found
         * @throws RemoteException
         * @throws ConfigurationException
         * @throws TransportException
         */
        public ServiceDetail getServiceDetail(GetServiceDetail getDetail) throws RemoteException, ConfigurationException, TransportException {
                return getServiceDetail(getDetail, this.getUDDINode().getApiNode());
        }

        /**
         * Gets service details or NULL if it doesn't exist or an error occurred
         *
         * @param getDetail
         * @param node
         * @return null if not found
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public ServiceDetail getServiceDetail(GetServiceDetail getDetail, Node node) throws RemoteException,
                TransportException, ConfigurationException {

                getDetail.setAuthInfo(getAuthToken(node.getSecurityUrl()));
                try {
                        ServiceDetail tModelDetail = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getServiceDetail(getDetail);
                        return tModelDetail;
                } catch (DispositionReportFaultMessage dr) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                        checkForErrorInDispositionReport(report, null, null);
                } catch (SOAPFaultException sfe) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
                        checkForErrorInDispositionReport(report, null, null);
                } catch (UndeclaredThrowableException ute) {
                        DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
                        checkForErrorInDispositionReport(report, null, null);
                }
                return null;
        }

}
