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

using org.apache.juddi.apiv3;
using org.apache.juddi.v3.client.cryptor;
using org.apache.juddi.v3.client.log;
using org.apache.juddi.v3.client.mapping;
using org.uddi.apiv3;
using org.xmlsoap.schemas.easyWsdl;
using System;
using System.Collections.Generic;
using System.Configuration;

namespace org.apache.juddi.v3.client.config
{
    /// <summary>
    /// The UDDIClerk provides an easy way to access a UDDI service. The clerk can be
 /// configured programmatically, but it is recommended to specify the server
 /// endpoint and access credentials in a uddi.xml file.
    /// </summary>
/// Recommended use:
 ///<pre>
 /// {@code
 /// UDDIClient uddiClient = new UDDIClient();
 /// UDDIClerk clerk = uddiClient.getClerk(clerkName);
 /// }
 /// </pre> where the clerkName "MyClerk" is defined as attribute on the clerk
 /// element
 /// <pre>
 /// {@code
 ///  <clerks registerOnStartup="true">
 ///    <clerk name="MyClerk" node="default" publisher="root" password="root" isPasswordEncrypted="false" cryptoProvider="">
 ///      <class>org.apache.juddi.example.HelloWorldImpl</class>
 ///    </clerk>
 ///  </clerks>
 /// }
 /// </pre> Credentials: In the clerk section you need to specify the publisher to
 /// be used, as well the password. The password can be encrypted and a
 /// cryptoProvider class can be set.
 ///
 /// UDDI Annotations: If you want to register classes containing UDDIAnnotations,
 /// then you need to set registerOnStartup="true" and you can list the classes
 /// with the annotations as subelements. See the HelloWorldImpl class above, as
 /// well as the uddi-annotations example.
 ///
 /// @author kstam
 ///
 
    public class UDDIClerk : IDisposable
    {

        private Log log = LogFactory.getLog(typeof(UDDIClerk));
        protected String name;
        protected UDDINode uddinode;
        protected String publisher;
        protected String password;
        private DateTime tokenBirthDate;
        private String authToken;
        private String[] classWithAnnotations;
        private WSDL[] wsdls;
        private String managerName;
        private bool isEncrypted = false;
        private string cryptoProvider;
        private Dictionary<String, Properties> services = new Dictionary<String, Properties>();

        /// <summary>
        ///  use caution calling the default constructor, many of the functions of
         /// the UDDI Clerk will not function unless manually set. The Node must
         /// be set for using most of the functions provided by this class.
        /// </summary>
        public UDDIClerk()
        {
        }

        public UDDIClerk(org.apache.juddi.apiv3.clerk clerk)
        {

            this.name = clerk.name;
            this.password = clerk.password;
            this.publisher = clerk.publisher;
            this.uddinode = new UDDINode(clerk.node);
        }
        /**
         * A list of classes defined in the config file that have UDDI
         * Annotations on them for automated registration
         * <br>client.clerks.clerk(" + i + ").class
         *
         * @return
         * @see org.apache.juddi.v3.annotations.UDDIService
         * @see org.apache.juddi.v3.annotations.UDDIServiceBinding
         */
        public String[] getClassWithAnnotations()
        {
            return classWithAnnotations;
        }

        /**
       * A list of classes defined in the config file that have UDDI
       * Annotations on them for automated registration
       * <br>client.clerks.clerk(" + i + ").class
       *
       * @see org.apache.juddi.v3.annotations.UDDIService
       * @see org.apache.juddi.v3.annotations.UDDIServiceBinding
       */
        public void setClassWithAnnotations(String[] classWithAnnotations)
        {
            this.classWithAnnotations = classWithAnnotations;
        }

        [Obsolete]
        public Dictionary<String, Properties> getServices()
        {
            return services;
        }

        [Obsolete]
        public void setServices(Dictionary<String, Properties> services)
        {
            this.services = services;
        }


        /**
         * The client manager name as defined in the config file client[@name]
         *
         * @return
         */
        public String getManagerName()
        {
            return managerName;
        }
        /**
        * The client manager name as defined in the config file client[@name]
        *
        * @param managerName
        */
        public void setManagerName(String managerName)
        {
            this.managerName = managerName;
        }

        /**
        * Performans the process of parsing the configuration defined wsdls to
        * UDDI. This is a convenience wrapper Note, if registration fails, no
        * exception is thrown
        */
        public void registerWsdls()
        {
            if (this.getWsdls() != null)
            {
                Properties properties = new Properties();
                properties.putAll(this.getUDDINode().getProperties());

                foreach (WSDL wsdl in this.getWsdls())
                {
                    try
                    {
                        ReadWSDL rw = new ReadWSDL();
                        tDefinitions wsdlDefinition = rw.readWSDL(wsdl.getFileName());
                        if (wsdl.getKeyDomain() != null)
                        {
                            properties.setProperty("keyDomain", wsdl.getKeyDomain());
                        }
                        if (wsdl.getBusinessKey() != null)
                        {
                            properties.setProperty("businessKey", wsdl.getBusinessKey());
                        }

                        WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizer(), properties);
                        wsdl2UDDI.registerBusinessServices(wsdlDefinition);
                    }
                    catch (Exception e)
                    {
                        log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.Message, e);
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
        public void registerWsdls(tDefinitions wsdlDefinition, String keyDomain, String businessKey)
        {

            try
            {
                Properties properties = new Properties();
                properties.putAll(this.getUDDINode().getProperties());

                if (keyDomain != null)
                {
                    properties.setProperty("keyDomain", keyDomain);
                }
                if (businessKey != null)
                {
                    properties.setProperty("businessKey", businessKey);
                }
                WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizer(), properties);
                wsdl2UDDI.registerBusinessServices(wsdlDefinition);
            }
            catch (Exception e)
            {
                log.error("Unable to register wsdl " + " ." + e.Message, e);
            }
        }

        /**
         * Removes the UDDI data structures belonging to the WSDLs for this
         * clerk from the UDDI node. Note, if registration fails, no exception
         * is thrown
         */
        public void unRegisterWsdls()
        {
            if (this.getWsdls() != null)
            {
                Properties properties = new Properties();
                properties.putAll(this.getUDDINode().getProperties());

                foreach (WSDL wsdl in this.getWsdls())
                {
                    try
                    {
                        ReadWSDL rw = new ReadWSDL();
                        tDefinitions wsdlDefinition = rw.readWSDL(wsdl.getFileName());
                        if (wsdl.getKeyDomain() != null)
                        {
                            properties.setProperty("keyDomain", wsdl.getKeyDomain());
                        }
                        if (wsdl.getBusinessKey() != null)
                        {
                            properties.setProperty("businessKey", wsdl.getBusinessKey());
                        }

                        WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizer(), properties);
                        wsdl2UDDI.unRegisterBusinessServices(wsdlDefinition);
                    }
                    catch (Exception e)
                    {
                        log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.Message, e);
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
        * @return
        */
        public org.uddi.apiv3.subscription register(org.uddi.apiv3.subscription subscription)
        {
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
        public org.uddi.apiv3.subscription register(org.uddi.apiv3.subscription subscription, org.apache.juddi.apiv3.node node)
        {

            log.info("Registering subscription with key " + subscription.subscriptionKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                save_subscription ss = new save_subscription();
                ss.authInfo = authToken;

                //List<org.uddi.apiv3.subscription> subscriptions = new List<org.uddi.apiv3.subscription>();
                //subscriptions.Add(subscription);
                ss.subscription = new org.uddi.apiv3.subscription[1];
                ss.subscription[0] = subscription;
                //holder.value = subscriptions;
                using (UDDI_Subscription_SoapBinding sub = getUDDINode().getTransport().getUDDISubscriptionService(node.subscriptionUrl))
                {
                    sub.save_subscription(ss);
                }

                if (log.isDebugEnabled())
                {
                    log.debug("Registering subscription " + subscription.subscriptionKey + " completed.");
                }
                subscription = ss.subscription[0];
            }
            catch (Exception e)
            {
                log.error("Unable to register subscription " + subscription.subscriptionKey
                        + " ." + e.Message, e);
            }
            //subscription = holder.value.get(0);
            return subscription;
        }

        /**
        * Register a tModel, using the node of current clerk ('this'). Note, if
        * registration fails, no exception is thrown
        *
        * @param tModel
        * @return the TModelDetail of the newly registered TModel
        */
        public org.uddi.apiv3.tModelDetail register(org.uddi.apiv3.tModel tModel)
        {
            return register(tModel, this.getUDDINode().getApiNode());
        }

        /**
         * Register a tModel. Note, if registration fails, no exception is
         * thrown
         * @param tModel
         * @param node
         * @return 
         */
        public org.uddi.apiv3.tModelDetail register(org.uddi.apiv3.tModel tModel, org.apache.juddi.apiv3.node node)
        {
            org.uddi.apiv3.tModelDetail tModelDetail = null;
            log.info("Registering tModel with key " + tModel.tModelKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                save_tModel saveTModel = new save_tModel();
                saveTModel.authInfo = (authToken);
                saveTModel.tModel = new tModel[1];
                saveTModel.tModel[0] = (tModel);
                using (UDDI_Publication_SoapBinding pub = getUDDINode().getTransport().getUDDIPublishService(node.publishUrl))
                {
                    tModelDetail = pub.save_tModel(saveTModel);
                }
                if (log.isDebugEnabled())
                {
                    log.debug("Registering tModel " + tModel.tModelKey + " completed.");
                }
            }
            catch (Exception e)
            {
                log.error("Unable to register tModel " + tModel.tModelKey
                        + " ." + e.Message, e);
            }
            return tModelDetail;
        }

        /**
         * Register a BindingTemplate, using the node of current clerk ('this').
         * Note, if registration fails, no exception is thrown
         * @param binding
         * @return 
         */
        public org.uddi.apiv3.bindingTemplate register(org.uddi.apiv3.bindingTemplate binding)
        {
            return register(binding, this.getUDDINode().getApiNode());
        }

        /**
       * Register a BindingTemplate. Note, if registration fails, no exception
       * is thrown
       * @param binding
       * @param node
       * @return 
       */
        public org.uddi.apiv3.bindingTemplate register(org.uddi.apiv3.bindingTemplate binding, org.apache.juddi.apiv3.node node)
        {

            org.uddi.apiv3.bindingTemplate bindingTemplate = null;
            log.info("Registering bindingTemplate with key " + binding.bindingKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                save_binding saveBinding = new save_binding();
                saveBinding.authInfo = (authToken);
                saveBinding.bindingTemplate = new bindingTemplate[1];
                saveBinding.bindingTemplate[0] = (binding);

                org.uddi.apiv3.bindingDetail bindingDetail = null;
                using (UDDI_Publication_SoapBinding pub = getUDDINode().getTransport().getUDDIPublishService(node.publishUrl))
                {
                    bindingDetail = pub.save_binding(saveBinding);
                }

                bindingTemplate = bindingDetail.bindingTemplate[0];
                if (log.isDebugEnabled())
                {
                    log.debug("Registering template binding " + binding.bindingKey + " completed.");
                }
            }
            catch (Exception e)
            {
                log.error("Unable to register template binding " + bindingTemplate.bindingKey
                        + " ." + e.Message, e);
            }
            return bindingTemplate;
        }

        /**
         * Register a service, using the node of current clerk ('this'). Note,
         * if registration fails, no exception is thrown
         *
         * @param service
         * @return
         */
        public org.uddi.apiv3.businessService register(org.uddi.apiv3.businessService service)
        {
            return register(service, this.getUDDINode().getApiNode());
        }


        /**
         * Register a service.
         *
         * Note, if registration fails, no exception is thrown
         *
         * @param service the element returned by the server, it may be modified from the original
         * @param node
         * @return the potentially modified service by the UDDI server
         */
        public org.uddi.apiv3.businessService register(org.uddi.apiv3.businessService service, org.apache.juddi.apiv3.node node)
        {

            org.uddi.apiv3.businessService businessService = null;
            log.info("Registering service " + service.name[0].Value
                    + " with key " + service.serviceKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                save_service saveService = new save_service();
                saveService.authInfo = (authToken);
                saveService.businessService = new businessService[1];
                saveService.businessService[0] = (service);
                org.uddi.apiv3.serviceDetail serviceDetail = null;
                using (UDDI_Publication_SoapBinding pub = getUDDINode().getTransport().getUDDIPublishService(node.publishUrl))
                {
                    serviceDetail = pub.save_service(saveService);
                }
                businessService = serviceDetail.businessService[0];
                log.debug("Registering service " + service.name[0].Value + " completed.");
            }
            catch (Exception e)
            {
                log.error("Unable to register service " + service.name[0].Value
                        + " ." + e.Message, e);
            }
            return businessService;
        }

        /**
         * registers a UDDI business. This is a convenience wrapper
         *
         * @param business
         * @return a possibility modified business entity as registered
         */
        public org.uddi.apiv3.businessEntity register(org.uddi.apiv3.businessEntity business)
        {
            return register(business, this.getUDDINode().getApiNode());
        }

        /**
          * Registers a UDDI Business referencing the specified Node from the
          * config file
          *
          * @param business
          * @param node
          * @return a possibility modified business entity as registered
          */
        public org.uddi.apiv3.businessEntity register(org.uddi.apiv3.businessEntity business, org.apache.juddi.apiv3.node node)
        {

            org.uddi.apiv3.businessEntity businessEntity = null;
            log.info("Registering business " + business.name[0].Value
                    + " with key " + business.businessKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                save_business saveBusiness = new save_business();
                saveBusiness.authInfo = (authToken);
                saveBusiness.businessEntity = new businessEntity[1];
                saveBusiness.businessEntity[0] = business;
                using (UDDI_Publication_SoapBinding pub = getUDDINode().getTransport().getUDDIPublishService(node.publishUrl))
                {
                    org.uddi.apiv3.businessDetail businessDetail = pub.save_business(saveBusiness);
                    businessEntity = businessDetail.businessEntity[0];
                    if (log.isDebugEnabled())
                    {
                        log.debug("Registering businessEntity " + business.name[0].Value + " completed.");
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Unable to register business " + business.name[0].Value
                        + " ." + e.Message, e);
            }
            return businessEntity;
        }

        /**
        * removes a business from UDDI. This is a convenience wrapper Note, if
        * registration fails, no exception is thrown
        *
        * @param businessKey
        */
        public void unRegisterBusiness(String businessKey)
        {
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
        public void unRegisterBusiness(String businessKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering the business " + businessKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_business deleteBusiness = new delete_business();
                deleteBusiness.authInfo = (authToken);
                deleteBusiness.businessKey = new string[1];
                deleteBusiness.businessKey[0] = (businessKey);
                using (UDDI_Publication_SoapBinding pub = getUDDINode().getTransport().getUDDIPublishService(node.publishUrl))
                {
                    pub.delete_business(deleteBusiness);
                }
            }
            catch (Exception e)
            {
                log.error("Unable to register service " + businessKey
                        + " ." + e.Message, e);
            }
        }

        /**
         * removes a service by key. This is a convenience wrapper Note, if
         * registration fails, no exception is thrown
         *
         * @param serviceKey
         */
        public void unRegisterService(String serviceKey)
        {
            unRegisterService(serviceKey, this.getUDDINode().getApiNode());
        }

        /**
        * Unregisters the service with specified serviceKey. Note, if
        * registration fails, no exception is thrown
        *
        * @param serviceKey
        * @param node
        */
        public void unRegisterService(String serviceKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering the service " + serviceKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_service deleteService = new delete_service();
                deleteService.authInfo = (authToken);
                deleteService.serviceKey = new string[] { serviceKey };
                getUDDINode().getTransport().getUDDIPublishService(node.publishUrl).delete_service(deleteService);
            }
            catch (Exception e)
            {
                log.error("Unable to register service " + serviceKey
                        + " ." + e.Message, e);
            }
        }

        /**
         * removes a binding by key. This is a convenience wrapper Note, if
         * registration fails, no exception is thrown
         *
         * @param bindingKey
         */
        public void unRegisterBinding(String bindingKey)
        {
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
        public void unRegisterBinding(String bindingKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering binding key " + bindingKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_binding deleteBinding = new delete_binding();
                deleteBinding.authInfo = (authToken);
                deleteBinding.bindingKey = new string[] { bindingKey };
                getUDDINode().getTransport().getUDDIPublishService(node.publishUrl).delete_binding(deleteBinding);
            }
            catch (Exception e)
            {
                log.error("Unable to unregister bindingkey " + bindingKey
                        + " ." + e.Message, e);
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
        public void unRegisterTModel(String tModelKey)
        {
            unRegisterTModel(tModelKey, this.getUDDINode().getApiNode());
        }

        /**
        * Unregisters the BindingTemplate with specified bindingKey. Note, if
        * registration fails, no exception is thrown
        *
        * @param tModelKey
        * @param node
        */
        public void unRegisterTModel(String tModelKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering tModel key " + tModelKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                org.uddi.apiv3.delete_tModel deleteTModel = new org.uddi.apiv3.delete_tModel();
                deleteTModel.authInfo = (authToken);
                deleteTModel.tModelKey = new string[] { tModelKey };
                getUDDINode().getTransport().getUDDIPublishService(node.publishUrl).delete_tModel(deleteTModel);
            }
            catch (Exception e)
            {
                log.error("Unable to unregister tModelkey " + tModelKey
                        + " ." + e.Message, e);
            }
        }

        /**
         * removes a subscription by key. This is a convenience wrapper Note, if
         * registration fails, no exception is thrown
         *
         * @param subscriptionKey
         */
        public void unRegisterSubscription(String subscriptionKey)
        {
            unRegisterSubscription(subscriptionKey, this.getUDDINode().getApiNode());
        }

        /**
         * removes a subscription by key, referencing the specified node in the
         * config file Note, if registration fails, no exception is thrown
         *
         * @param subscriptionKey
         * @param node
         */
        public void unRegisterSubscription(String subscriptionKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering subscription with key " + subscriptionKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_subscription deleteSubscription = new delete_subscription();
                deleteSubscription.authInfo = (authToken);
                deleteSubscription.subscriptionKey = new string[] { subscriptionKey };
                getUDDINode().getTransport().getUDDISubscriptionService(node.securityUrl).delete_subscription(deleteSubscription);
            }
            catch (Exception e)
            {
                log.error("Unable to unregister subscription key " + subscriptionKey
                        + " ." + e.Message, e);
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
        public tModelList findTModel(find_tModel findTModel)
        {
            return this.findTModel(findTModel, this.uddinode.getApiNode());
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
        public tModelList findTModel(find_tModel findTModel, org.apache.juddi.apiv3.node node)
        {

            findTModel.authInfo = (getAuthToken(node.securityUrl));
            try
            {
                tModelList tModelList = getUDDINode().getTransport().getUDDIInquiryService(node.inquiryUrl).find_tModel(findTModel);
                return tModelList;
            }
            catch (Exception dr)
            {
                log.error("", dr);
                //                DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                //checkForErrorInDispositionReport(report, null, null);
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
        public tModelDetail getTModelDetail(String tModelKey)
        {
            get_tModelDetail getTModelDetail = new get_tModelDetail();
            getTModelDetail.tModelKey = new string[] { tModelKey };
            return this.getTModelDetail(getTModelDetail);
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
        public tModelDetail getTModelDetail(get_tModelDetail getTModelDetail)
        {
            return this.getTModelDetail(getTModelDetail, this.getUDDINode().getApiNode());
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
        public tModelDetail getTModelDetail(get_tModelDetail getTModelDetail, org.apache.juddi.apiv3.node node)
        {

            getTModelDetail.authInfo = (getAuthToken(node.securityUrl));
            try
            {
                tModelDetail tModelDetail = getUDDINode().getTransport().getUDDIInquiryService(node.inquiryUrl).get_tModelDetail(getTModelDetail);
                return tModelDetail;
            }
            catch (Exception dr)
            {
                log.error("", dr);
                //DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                //checkForErrorInDispositionReport(report, null, null);
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
        public businessService getServiceDetail(String serviceKey)
        {
            return getServiceDetail(serviceKey, this.uddinode.getApiNode());
        }

        /**
         * Finds a service by the key, . This is a convenience wrapper
         *
         * @param serviceKey
         * @return null if not found or error
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         * @deprecated see
         */
        public businessService getServiceDetail(String serviceKey, org.apache.juddi.apiv3.node node)
        {
            get_serviceDetail getServiceDetail = new get_serviceDetail();
            getServiceDetail.serviceKey = new string[] { serviceKey };
            getServiceDetail.authInfo = (getAuthToken(node.securityUrl));
            try
            {
                serviceDetail sd = getUDDINode().getTransport().getUDDIInquiryService(node.inquiryUrl).get_serviceDetail(getServiceDetail);
                businessService[] businessServiceList = sd.businessService;
                if (businessServiceList.Length == 0)
                {
                    throw new ConfigurationErrorsException("Could not find Service with key=" + serviceKey);
                }
                return businessServiceList[0];
            }
            catch (Exception dr)
            {
                log.error("", dr);
                //DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                //checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, serviceKey);
            }

            return null;
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
        public bindingTemplate getServiceBindingDetail(String bindingKey)
        {
            return getServiceBindingDetail(bindingKey, this.uddinode.getApiNode());
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
        public bindingTemplate getServiceBindingDetail(String bindingKey, org.apache.juddi.apiv3.node node)
        {
            get_bindingDetail getBindingDetail = new get_bindingDetail();
            getBindingDetail.bindingKey = new string[] { bindingKey };
            getBindingDetail.authInfo = (getAuthToken(node.securityUrl));
            try
            {
                bindingDetail bd = getUDDINode().getTransport().getUDDIInquiryService(node.inquiryUrl).get_bindingDetail(getBindingDetail);
                bindingTemplate[] bindingTemplateList = bd.bindingTemplate;
                if (bindingTemplateList.Length == 0)
                {
                    throw new ConfigurationErrorsException("Could not find ServiceBinding with key=" + bindingKey);
                }
                return bindingTemplateList[0];
            }
            catch (Exception dr)
            {
                log.error("", dr);
                //DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                //checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, bindingKey);
            }

            return null;
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
        public org.uddi.apiv3.businessEntity getBusinessDetail(String businessKey)
        {
            return this.getBusinessDetail(businessKey, this.getUDDINode().getApiNode());
        }

        /**
         * Looks up the BusinessEntiry in the registry, will return null if is not
         * found.
         *
         * @param businessKey - the key we are looking for
         * @param node - the node which is going to be queried
         * @return businessEntity is found, or null if not found.
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException
         */
        public businessEntity getBusinessDetail(String businessKey, org.apache.juddi.apiv3.node node)
        {
            get_businessDetail getBusinessDetail = new get_businessDetail();
            getBusinessDetail.businessKey = new string[] { businessKey };
            getBusinessDetail.authInfo = (node.securityUrl);
            try
            {
                businessDetail bd = getUDDINode().getTransport().getUDDIInquiryService(node.inquiryUrl).get_businessDetail(getBusinessDetail);
                return bd.businessEntity[0];
            }
            catch (Exception dr)
            {
                log.error("", dr);
                //DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                //checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
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
        public relatedBusinessesList findRelatedBusinesses(String businessKey, org.apache.juddi.apiv3.node node)
        {
            find_relatedBusinesses findRelatedBusinesses = new find_relatedBusinesses();
            findRelatedBusinesses.Item = (businessKey);
            findRelatedBusinesses.authInfo = (node.securityUrl);
            try
            {
                relatedBusinessesList rbl = getUDDINode().getTransport().getUDDIInquiryService(node.inquiryUrl).find_relatedBusinesses(findRelatedBusinesses);
                return rbl;
            }
            catch (Exception dr)
            {
                log.error("", dr);
                //DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                //checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
            }

            return null;
        }
        /*
        private void checkForErrorInDispositionReport(dispositionReport report, String Error, String entityKey)
        {

            if (entityKey != null && report != null && report.countainsErrorCode(DispositionReport.E_INVALID_KEY_PASSED))
            {
                log.info("entityKey " + entityKey + " was not found in the registry");
            }
            else
            {
                if (report == null)
                {
                    log.info("Missing DispositionReport");
                }
                else
                {
                    foreach (result result in report.result)
                    {
                  //      log.error(result.getErrInfo().getErrCode() + " " + result.getErrInfo().getValue());
                    }
                }
            }
        }
        */


        /**
         * Gets an auth token from the uddi server using the uddi auth token
         * <br>
         * Notice: never log auth tokens! Treat it like a password
         *
         * notes: changed to public to have access from the subscription
         * callback API 8/20/2013 AO
         *
         * @param endpointURL
         * @return
         * @throws TransportException
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         */
        public String getAuthToken(String endpointURL)
        {
            //if the token is older then 10 minutes discard it, and create a new one.
            if ((authToken != null && !"".Equals(authToken)) && (tokenBirthDate != null && DateTime.Now > tokenBirthDate.AddMilliseconds(600000)))
            {
                discard_authToken discardAuthToken = new discard_authToken();
                discardAuthToken.authInfo = (authToken);
                using (UDDI_Security_SoapBinding sec = getUDDINode().getTransport().getUDDISecurityService(endpointURL))
                {
                    getUDDINode().getTransport().getUDDISecurityService(endpointURL).discard_authToken(discardAuthToken);
                    authToken = null;
                }
            }
            if (authToken == null || "".Equals(authToken))
            {
                tokenBirthDate = new DateTime();
                get_authToken getAuthToken = new get_authToken();
                getAuthToken.userID = (getPublisher());
                if (isEncrypted)
                {
                    if (String.IsNullOrEmpty(cryptoProvider))
                    {
                        log.error("Credentials are encrypted but no cryptoProvider was defined in the config file!");
                    }
                    else
                        try
                        {
                            getAuthToken.cred = (CryptorFactory.getCryptor(this.cryptoProvider).decrypt(getPassword()));
                        }
                        catch (Exception ex)
                        {
                            log.error("Unable to decrypt credentials! sending it as is", ex);
                            getAuthToken.cred = (getPassword());
                        }
                }
                else
                {
                    getAuthToken.cred = (getPassword());
                }
                using (UDDI_Security_SoapBinding sec = getUDDINode().getTransport().getUDDISecurityService(endpointURL))
                {
                    authToken = getUDDINode().getTransport().getUDDISecurityService(endpointURL).get_authToken(getAuthToken).authInfo;
                }

            }
            return authToken;
        }



        /// <summary>
        ///  This calls a jUDDI implementation specific API call and is used to help 
        /// configure internode communication between jUDDI servers. This is NOT 
        /// part of the UDDI specification. Note: this API call should only
        /// be used with secure ports (SSL/TLS)
        /// </summary>
        /// <param name="senderClerk"></param>
        /// <returns></returns>
        public clerk[] saveClerk(UDDIClerk senderClerk)
        {

            clerk[] clerkDetail = null;
            try
            {
                log.debug("Sending Clerk " + senderClerk.getName() + " info to jUDDI " + getUDDINode().getName());
                save_clerk saveClerk = new save_clerk();
                saveClerk.authInfo = (getAuthToken(senderClerk.getUDDINode().getSecurityUrl()));
                saveClerk.clerk = new clerk[] { (getApiClerk()) };
                clerkDetail = getUDDINode().getTransport().getJUDDIApiService(senderClerk.getUDDINode().getJuddiApiUrl()).save_Clerk(saveClerk);
            }
            catch (Exception e)
            {
                log.error("Unable to save clerk " + getName()
                        + " ." + e.Message, e);
            }

            return clerkDetail;
        }

        /**
         * Returns the UDDI node that this clerk is associated with
         * client.clerks.clerk(" + i + ")@node
         *
         * @return
         */
        public UDDINode getUDDINode()
        {
            return uddinode;
        }

        /**
       * client.clerks.clerk(" + i + ")[@name]
       *
       * @return
       */
        public String getName()
        {
            return name;
        }

        /**
        * client.clerks.clerk(" + i + ")[@name]
        *
        * @param name
        */
        public void setName(String name)
        {
            this.name = name;
        }

        /**
        * client.clerks.clerk(" + i + ")[@node] reference to UDDI Node
        *
        * @param uddiNode
        */
        public void setUDDInode(UDDINode uddinode)
        {
            this.uddinode = uddinode;
        }

        /**
        * This is the username client.clerks.clerk(" + i + ")[@publisher]
        *
        * @return
        */
        public String getPublisher()
        {
            return publisher;
        }

        /**
         * This is the username client.clerks.clerk(" + i + ")[@publisher]
         *
         * @param publisher
         */
        public void setPublisher(String publisher)
        {
            this.publisher = publisher;
        }

        /**
        * If the password is encrypted, it will be decrypted if possible,
        * otherwise the cipher text will be returned. client.clerks.clerk(" + i
        * + ")[@password]
        *
        * @return
        */
        public String getPassword()
        {
            if (isEncrypted)
            {
                try
                {
                    return CryptorFactory.getCryptor(cryptoProvider).decrypt(password);
                }
                catch (Exception ex)
                {
                    log.error("Unable to decrypt the password", ex);
                }
            }
            return password;
        }

        /**
         * If the password is encrypted, it cipher text is returned, otherwise
         * the clear text will be returned. client.clerks.clerk(" + i +
         * ")[@password]
         *
         * @return
         */
        public String getRawPassword()
        {
            return password;
        }

        /**
         * Use with caution, don't forget to set the IsEncrypted and Crypto
         * provider client.clerks.clerk(" + i + ")[@password]
         *
         * @param password
         */
        public void setPassword(String password)
        {
            this.password = password;
        }

        /**
         * Used for the automated registration of services via WSDL2UDDI<br>
         * config file: client.clerks.clerk(" + i + ").wsdl
         *
         * @return 
         * @see WSDL2UDDI
         */
        public WSDL[] getWsdls()
        {
            return wsdls;
        }

        /**
         * Used for the automated registration of services via WSDL2UDDI<br>
         * config file: client.clerks.clerk(" + i + ").wsdl
         *
         * @param wsdls
         * @see WSDL2UDDI
         */
        public void setWsdls(WSDL[] wsdls)
        {
            this.wsdls = wsdls;
        }

        public class WSDL
        {

            private String businessKey;
            private String keyDomain;
            private String fileName;

            public String getBusinessKey()
            {
                return businessKey;
            }

            public void setBusinessKey(String businessKey)
            {
                this.businessKey = businessKey;
            }

            public String getFileName()
            {
                return fileName;
            }

            public void setFileName(String fileName)
            {
                this.fileName = fileName;
            }

            public String getKeyDomain()
            {
                return keyDomain;
            }

            public void setKeyDomain(String keyDomain)
            {
                this.keyDomain = keyDomain;
            }
        }

        /// <summary>
        /// 
        /// A helper function to create a tModel key generator.&lt;br&gt;
        /// Why would I want a key generator? In UDDIv3, you&#39;re suppose to specify what you want the keys (unique identifiers) to be, however there&#39;s
        /// a number of naming rules associated with the keys. Generally, use the FQDN of your business or organization.
        /// Optionally, when saving an UDDI entity, you can just leave the key name blank and the server
        /// should generate one for you. It&#39;s normally a UUID that&#39;s not easy to remember. In this case, there&#39;s no need to call this method. &lt;br&gt;&lt;br&gt;
        /// In addition, no changes are made to the UDDI server. You&#39;ll have to do that one using code similar to this:
        /// &lt;pre&gt;
        /// UDDIClerk clerk = ...
        /// TModel keygen = UDDIClerk.createKeyGenator(&quot;uddi:mydomain.com:keygenerator&quot;, &quot;my domain&quot;, &quot;en&quot;);
        /// clerk.register(keygen);
        /// 
        /// </summary>
        /// <param name="partitionName">think of this as the domain, i.e. juddi.apache.org, but it can really be anything you want. This will become part of the</param>
        /// key associated with the tModel generator (uddi:juddi.apache.org:keygenerator)
        /// <param name="DescriptiveName">required. max length is 255 char</param>
        /// <param name="DescriptiveNameLanguage">optional, max length is 26 char</param>
        /// <returns>a populated tModel entity representing a tModel key generator. No changes are made to any connect UDDI service</returns>
        /// @since 3.2
        public static tModel createKeyGenator(String partitionName, String DescriptiveName, String DescriptiveNameLanguage)
        {
            if (partitionName == null || partitionName.Length == 0)
            {
                throw new ArgumentOutOfRangeException();
            }

            if (DescriptiveName == null || DescriptiveName.Length == 0)
            {
                throw new ArgumentOutOfRangeException();
            }
            if (!partitionName.StartsWith("uddi:"))
            {
                partitionName = "uddi:" + partitionName;
               // throw new ArgumentOutOfRangeException("partitionName must have a 'uddi:' prefix");

            }
            if (!partitionName.EndsWith(":keygenerator"))
            {
                partitionName = partitionName + "keygenerator";
                //throw new ArgumentOutOfRangeException("partitionName must have a ':keyGenerator' postfix");
            }
            tModel tm = new tModel();
            tm.name = new name();
            tm.name.Value = (DescriptiveName);
            tm.name.lang = (DescriptiveNameLanguage);
            tm.categoryBag = new categoryBag();
            keyedReference kr = new keyedReference();
            kr.tModelKey = (UDDIConstants.KEY_GENERATOR_TMODEL);
            kr.keyName = (UDDIConstants.KEY_GENERATOR);
            kr.keyValue = (UDDIConstants.KEY_GENERATOR_VALUE);
            tm.categoryBag.Items = new object[] { kr };
            overviewDoc
                overviewDoc = new overviewDoc();
            overviewURL overviewUrl = new overviewURL();
            overviewUrl.useType = ("text");
            overviewUrl.Value = ("http://uddi.org/pubs/uddi_v3.htm#keyGen");
            overviewDoc.overviewURLs = new overviewURL[] { overviewUrl };
            tm.overviewDoc = new overviewDoc[] { overviewDoc };
            tm.tModelKey = (partitionName.ToLower());
            return tm;
        }

        /// <summary>
        /// 
        /// This calls a jUDDI implementation specific API call and is used to help
        /// configure internode communication between jUDDI servers. This is NOT
        /// part of the UDDI specification.&lt;br&gt;
        /// Note: this API call should only be used with secure ports (SSL/TLS)
        /// </summary>
        /// <param name="node"></param>
        /// <returns></returns>
        public node[] saveNode(apiv3.node node)
        {
            node[] nodeDetail = null;
            try
            {
                log.info("Sending Node " + node.name + " info to jUDDI " + getUDDINode().getName());
                save_node saveNode = new save_node();
                saveNode.authInfo = (getAuthToken(this.getUDDINode().getSecurityUrl()));
                saveNode.node = new node[] { (node) };
                nodeDetail = getUDDINode().getTransport().getJUDDIApiService(this.getUDDINode().getJuddiApiUrl()).save_Node(saveNode);
            }
            catch (Exception e)
            {
                log.error("Unable to save node " + node.name
                        + " ." + e.Message, e);
            }

            return nodeDetail;
        }

        /**
        * client.clerks.clerk(" + i + ")[@cryptoProvider]
        *
        * @param clazz
        */
        public void setCryptoProvider(string p)
        {

            this.cryptoProvider = p;
        }

        /**
         * client.clerks.clerk(" + i + ")[@isPasswordEncrypted] default is false
         *
         * @param option
         */
        public void setPasswordEncrypted(bool p)
        {
            this.isEncrypted = p;
        }
        /**
         * client.clerks.clerk(" + i + ")[@cryptoProvider]
         *
         * @return
         */
        public string getCryptoProvider()
        {

            return this.cryptoProvider;
        }
        /**
        * client.clerks.clerk(" + i + ")[@isPasswordEncrypted]
        *
        * @return
        */
        public bool getPasswordEncrypted()
        {
            return this.isEncrypted;
        }

        public void Dispose()
        {
            uddinode.Dispose();
        }


        /// <summary>
        /// 
        /// This is a convenience function that will build and return a TModelInstanceInfo
        /// as described in the following link that will enable you to tag web services
        /// registered in UDDI with some kind of version information.&lt;Br&gt;&lt;Br&gt;
        /// Article source: &lt;a href=&quot;http://www.ibm.com/developerworks/webservices/library/ws-version/&quot;&gt;http://www.ibm.com/developerworks/webservices/library/ws-version/&lt;/a&gt;
        /// &lt;Br&gt;&lt;Br&gt;
        /// 
        /// When using this tModel as a tModelInstance, it can be used to describe a
        /// version associated with either a service interface, a bindingTemplate
        /// service instance. Note: This is a jUDDI specific addon and may not be
        /// present in other registries
        /// 
        /// </summary>
        /// <param name="version">From the article, no specificity is provided on what to use as a value, but</param>
        /// we recommend that you use the string representation of major.minor[.build[.revision]].&lt;br&gt;
        /// Example
        /// &lt;ul&gt;
        /// &lt;li&gt;6.1.2.3&lt;/li&gt;
        /// &lt;li&gt;1.0&lt;/li&gt;
        /// &lt;li&gt;0.1&lt;/li&gt;
        /// &lt;/ul&gt;
        /// <returns>TModelInstanceInfo populated as described in the article, plus some descriptive information</returns>
        public static tModelInstanceInfo createServiceInterfaceVersion(String version, String lang)
        {
            if (version == null)
                throw new ArgumentNullException();
            tModelInstanceInfo tt = new tModelInstanceInfo();
            tt.tModelKey = (UDDIConstants.VERSION_TMODEL);
            tt.instanceDetails = new instanceDetails();


            overviewDoc doc = new overviewDoc();
            doc.overviewURLs = new overviewURL[] { (new overviewURL("http://www.ibm.com/developerworks/webservices/library/ws-version/", "text")) };
            //,new description(
            //"Describes a version associated with either a service interface, a bindingTemplate service instance.", lang)};
            tt.description = new description[] { new description("Describes a version associated with either a service interface, a bindingTemplate service instance.", lang) };

            tt.instanceDetails.Items = new overviewDoc[] { doc };
            tt.instanceDetails.instanceParms = version;

            //tt.instanceDetails.Items = new object[] { doc };
            return tt;
        }

        /// <summary>
        /// 
        /// This is a convenience function that will filter a list of binding templates
        /// and return a list of bindings matching the specified version number.
        /// 
        /// This implements and expands upon service versioning described in the
        /// following link and will enable you to tag web services
        /// registered in UDDI with some kind of version information.&lt;Br&gt;&lt;Br&gt;
        /// Article source: &lt;a href=&quot;http://www.ibm.com/developerworks/webservices/library/ws-version/&quot;&gt;http://www.ibm.com/developerworks/webservices/library/ws-version/&lt;/a&gt;
        /// &lt;Br&gt;&lt;Br&gt;
        /// </summary>
        /// <see cref="createServiceInterfaceVersion"></see>
        /// <see cref="createServiceInterfaceVersion"></see>
        /// <see cref="createServiceInterfaceVersion"></see>
        /// <see cref="createServiceInterfaceVersion"></see>
        /// <see cref="createServiceInterfaceVersion"></see>
        /// 
        /// This function operates using tModelInstances that are used to describe a
        /// version associated with either a service interface, a bindingTemplate
        /// service instance. Note: This is a jUDDI specific addon and may not be
        /// present in other registries
        /// 
        /// <param name="version">From the article, no specificity is provided on what to use as a value, but</param>
        /// we recommend that you use the string representation of major.minor[.build[.revision]].&lt;br&gt;
        /// Example
        /// &lt;ul&gt;
        /// &lt;li&gt;6.1.2.3&lt;/li&gt;
        /// &lt;li&gt;1.0&lt;/li&gt;
        /// &lt;li&gt;0.1&lt;/li&gt;
        /// &lt;/ul&gt;
        /// <param name="version"></param>
        /// <param name="bindingTemplate"></param>
        /// <returns>a list if binding templates where the version equals ignoring case trimmed equals the version value</returns>

        public static List<bindingTemplate> getBindingByVersion(String version, bindingTemplate[] bindingTemplate)
        {
            if (version == null)
            {
                throw new ArgumentNullException();
            }
            if (bindingTemplate == null)
            {
                throw new ArgumentNullException();
            }
            List<bindingTemplate> ret = new List<bindingTemplate>();
            for (int i = 0; i < bindingTemplate.Length; i++)
            {
                if (bindingTemplate[i].tModelInstanceDetails != null)
                {
                    for (int k = 0; k < bindingTemplate[i].tModelInstanceDetails.Length; k++)
                    {
                        if (bindingTemplate[i].tModelInstanceDetails[k].tModelKey.Equals(UDDIConstants.VERSION_TMODEL))
                        {
                            if (bindingTemplate[i].tModelInstanceDetails[k].instanceDetails != null)
                            {
                                if (bindingTemplate[i].tModelInstanceDetails[k].instanceDetails.instanceParms != null)
                                {
                                    if ((bindingTemplate[i].tModelInstanceDetails[k].instanceDetails.instanceParms.Trim().Equals(version.Trim(), StringComparison.CurrentCultureIgnoreCase)))
                                    {
                                        ret.Add(bindingTemplate[i]);
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
         * JUDDI-700
         * This implements the "find_endpoints" pattern as described in Alex O'Ree's
         * Master's Thesis on UDDI. Basically, UDDI never provided a 'simple' way to 
         * get a list of execution URLs for a web service. This function will resolve
         * all AccessPoint and HostingRedictor indirections and provide to you a list
         * of URLs that <i>should</i> be accessible for the given service.
         * @param serviceKey
         * @return 
         */
        public List<String> getEndpoints(String serviceKey)
        {
            List<String> items = new List<String>();
            businessService serviceDetail = null;
            try
            {
                serviceDetail = this.getServiceDetail(serviceKey);
            }
            catch (Exception ex)
            {
                log.error("Unable to fetch the specified service's details", ex);
            }
            if (serviceDetail == null)
            {
                return items;
            }
           
                if (serviceDetail.bindingTemplates != null)
                {
                    for (int k = 0; k < serviceDetail.bindingTemplates.Length; k++)
                    {
                        try
                        {
                            items.AddRange(ParseBinding(serviceDetail.bindingTemplates[k]));
                        }
                        catch (Exception ex)
                        {
                            log.warn("error parsing binding", ex);
                        }
                    }
                }
           
            return items;
        }

        private List<String> GetBindingInfo(String value)
        {
            List<String> items = new List<String>();
            if (value == null)
            {
                return items;
            }

            get_bindingDetail b = new get_bindingDetail();
            b.authInfo = (getAuthToken(this.getApiClerk().getNode().getSecurityUrl()));
            b.bindingKey = new string[] { (value) };
            bindingDetail bindingDetail = getUDDINode().getTransport().getUDDIInquiryService(this.getApiClerk().getNode().getInquiryUrl()).get_bindingDetail(b);
            if (bindingDetail.bindingTemplate != null)
                for (int i = 0; i < bindingDetail.bindingTemplate.Length; i++)
                {
                    items.AddRange(ParseBinding(bindingDetail.bindingTemplate[i]));
                }
            return items;
        }

        /**
         * The API Clerk is the JAXWS generated "Clerk" element that is used
         * both on the jUDDI Client and on the jUDDI Web Service (not part of
         * the UDDI spec).
         *
         * @return
         */
        public clerk getApiClerk()
        {
            clerk apiClerk = new clerk();
            apiClerk.name = (name);
            apiClerk.node = (uddinode.getApiNode());
            apiClerk.password = (password);
            apiClerk.publisher = (publisher);
            return apiClerk;
        }

        private List<String> ParseBinding(bindingTemplate get)
        {
            List<String> items = new List<String>();
            if (get == null || get.Item == null)
            {
                return items;
            }
            if (get.Item is hostingRedirector)
            {
                //hosting Redirector is the same as "reference this other binding template". It's actually deprecated so 
                //don't expect to see this too often
                items.AddRange(GetBindingInfo(((hostingRedirector)get.Item).bindingKey));
            }
            if (get.Item is accessPoint)
            {
                String usetype = ((accessPoint)get.Item).useType;
                if (usetype == null)
                {
                    //this is unexpected, usetype is a required field
                    items.Add(((accessPoint)get.Item).Value);
                }
                else if (usetype.Equals(AccessPointType.bindingTemplate.ToString(), StringComparison.CurrentCultureIgnoreCase))
                {
                    //referencing another binding template
                    items.AddRange(GetBindingInfo(((accessPoint)get.Item).Value));
                }
                else if (usetype.Equals(AccessPointType.hostingDirector.ToString(), StringComparison.CurrentCultureIgnoreCase))
                {
                    //this one is a bit strange. the value should be a binding template

                    items.AddRange(GetBindingInfo(((accessPoint)get.Item).Value));

                }
                else if (usetype.Equals(AccessPointType.wsdlDeployment.ToString(), StringComparison.CurrentCultureIgnoreCase))
                {
                    //fetch wsdl and parse
                    items.AddRange(FetchWSDL(((accessPoint)get.Item).Value));
                }
                else if (usetype.Equals(AccessPointType.endPoint.ToString(), StringComparison.CurrentCultureIgnoreCase))
                {
                    items.Add(((accessPoint)get.Item).Value);
                }
                else
                {
                    //treat it has an extension or whatever
                    items.Add(((accessPoint)get.Item).Value);
                }

            }

            return items;
        }


        /**
         * fetches a wsdl endpoint and parses for execution urls
         * @param value
         * @return 
         */
        private List<String> FetchWSDL(String value)
        {
            List<String> items = new List<String>();

            if (value.StartsWith("http://") || value.StartsWith("https://"))
            {
                //here, we need an HTTP Get for WSDLs
                org.apache.juddi.v3.client.mapping.ReadWSDL r = new ReadWSDL();
                r.setIgnoreSSLErrors(true);
                try
                {
                    tDefinitions wsdlDefinition = r.readWSDL(value);
                    Properties properties = new Properties();


                    properties.put("keyDomain", "domain");
                    properties.put("businessName", "biz");
                    properties.put("serverName", "localhost");
                    properties.put("serverPort", "80");

                    WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizer(), properties);
                    businessService[] businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
                    for (int i = 0; i < businessServices.Length; i++)
                    {
                        if (businessServices[i].bindingTemplates != null)
                        {
                            for (int k = 0; k < businessServices[i].bindingTemplates.Length; k++)
                            {
                                items.AddRange(ParseBinding(businessServices[i].bindingTemplates[k]));
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    log.error("error fetching wsdl for parsing", ex);
                }

            }
            return items;
        }

      

        /**
         * Gets service details or NULL if it doesn't exist or an error occurred
         * @param getDetail
         * @return
         * @throws RemoteException
         * @throws ConfigurationException
         * @throws TransportException 
         */
        public serviceDetail getServiceDetail(get_serviceDetail getDetail)
        {
            return getServiceDetail(getDetail, this.getUDDINode().getApiNode());
        }

        /**
         * Gets service details or NULL if it doesn't exist or an error occurred
         * @param getDetail
         * @param node
         * @return
         * @throws RemoteException
         * @throws TransportException
         * @throws ConfigurationException 
         */
        public serviceDetail getServiceDetail(get_serviceDetail getDetail, node node)
        {

            getDetail.authInfo = (getAuthToken(node.securityUrl));
            try
            {
                serviceDetail tModelDetail = getUDDINode().getTransport().getUDDIInquiryService(node.inquiryUrl).get_serviceDetail(getDetail);
                return tModelDetail;
            }
            catch (Exception dr)
            {
                //dispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
                log.error("error fetching service detail", dr);
            }
            return null;
        }

    }

}
