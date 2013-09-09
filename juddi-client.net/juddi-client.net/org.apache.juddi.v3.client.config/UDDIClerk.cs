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

using org.apache.juddi.v3.client.crypto;
using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
    /// <summary>
    /// 
    /// </summary>
    /// 
    public class UDDIClerk: IDisposable
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

        public UDDIClerk()
        {

            // TODO Auto-generated constructor stub
        }

        public UDDIClerk(org.apache.juddi.apiv3.clerk clerk)
        {

            this.name = clerk.name;
            this.password = clerk.password;
            this.publisher = clerk.publisher;
            this.uddinode = new UDDINode(clerk.node);
        }

        public String[] getClassWithAnnotations()
        {
            return classWithAnnotations;
        }

        public void setClassWithAnnotations(String[] classWithAnnotations)
        {
            this.classWithAnnotations = classWithAnnotations;
        }

        public Dictionary<String, Properties> getServices()
        {
            return services;
        }

        public void setServices(Dictionary<String, Properties> services)
        {
            this.services = services;
        }

        public String getManagerName()
        {
            return managerName;
        }

        public void setManagerName(String managerName)
        {
            this.managerName = managerName;
        }
        /*
    public void registerWsdls() {
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
                    wsdl2UDDI.registerBusinessServices(wsdlDefinition);
                } catch (Exception e) {
                    log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.Message, e);
                } catch (Throwable t) {
                    log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + t.getMessage(), t);
                }
            }
        }
    }

    public void registerWsdls(Definition wsdlDefinition, String keyDomain, String businessKey) {

        try {
            Properties properties = new Properties();
            properties.putAll(this.getUDDINode().getProperties());
            //Definition wsdlDefinition = rw.readWSDL(wsdlUrl);
            if (keyDomain != null) {
                properties.setProperty("keyDomain", keyDomain);
            }
            if (businessKey != null) {
                properties.setProperty("businessKey", businessKey);
            }

            WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizerDefaultImpl(), properties);
            wsdl2UDDI.registerBusinessServices(wsdlDefinition);
        } catch (Exception e) {
            log.error("Unable to register wsdl " + " ." + e.Message, e);
        } catch (Throwable t) {
            log.error("Unable to register wsdl " + " ." + t.getMessage(), t);
        }
    }

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
                    log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.Message, e);
                } catch (Throwable t) {
                    log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + t.getMessage(), t);
                }
            }
        }
    }
        */
        public org.uddi.apiv3.subscription register(org.uddi.apiv3.subscription subscription)
        {
            return register(subscription, this.getUDDINode().getApiNode());
        }

        /**
         * Register a Subscription.
         */
        public org.uddi.apiv3.subscription register(org.uddi.apiv3.subscription subscription, org.apache.juddi.apiv3.node node)
        {

            log.info("Registering subscription with key " + subscription.subscriptionKey);
            //Holder<List<Subscription>> holder = new Holder<List<Subscription>>();
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                save_subscription ss = new save_subscription();
                ss.authInfo = authToken;

                //List<org.uddi.apiv3.subscription> subscriptions = new List<org.uddi.apiv3.subscription>();
                //subscriptions.Add(subscription);
                ss.subscription = new subscription[1];
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
         * Register a tModel, using the node of current clerk ('this').
         *
         * @param tModel
         * @return the tModelDetail of the newly registered tModel
         */
        public org.uddi.apiv3.tModelDetail register(org.uddi.apiv3.tModel tModel)
        {
            return register(tModel, this.getUDDINode().getApiNode());
        }

        /**
         * Register a tModel.
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
         *
         */
        public org.uddi.apiv3.bindingTemplate register(org.uddi.apiv3.bindingTemplate binding)
        {
            return register(binding, this.getUDDINode().getApiNode());
        }

        /**
         * Register a BindingTemplate.
         *
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
         * Register a service, using the node of current clerk ('this').
         *
         */
        public org.uddi.apiv3.businessService register(org.uddi.apiv3.businessService service)
        {
            return register(service, this.getUDDINode().getApiNode());
        }

        /**
         * Register a service.
         *
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
                if (log.isDebugEnabled())
                {
                    log.debug("Registering service " + service.name[0].Value + " completed.");
                }
            }
            catch (Exception e)
            {
                log.error("Unable to register service " + service.name[0].Value
                        + " ." + e.Message, e);
            }
            return businessService;
        }

        public org.uddi.apiv3.businessEntity register(org.uddi.apiv3.businessEntity business)
        {
            return register(business, this.getUDDINode().getApiNode());
        }

        /**
         * Register a service. returns null if not successful
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

        public void unRegisterBusiness(String businessKey)
        {
            unRegisterBusiness(businessKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the service with specified serviceKey.
         *
         * @param service
         */
        public void unRegisterBusiness(String businessKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering the business " + businessKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_business deleteBusiness = new delete_business();
                deleteBusiness.authInfo=(authToken);
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

        public void unRegisterService(String serviceKey)
        {
            unRegisterService(serviceKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the service with specified serviceKey.
         *
         * @param service
         */
        public void unRegisterService(String serviceKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering the service " + serviceKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_service deleteService = new delete_service();
                deleteService.authInfo=(authToken);
                deleteService.serviceKey = new string[]{serviceKey};
                getUDDINode().getTransport().getUDDIPublishService(node.publishUrl).delete_service(deleteService);
            }
            catch (Exception e)
            {
                log.error("Unable to register service " + serviceKey
                        + " ." + e.Message, e);
            }   
        }

        public void unRegisterBinding(String bindingKey)
        {
            unRegisterBinding(bindingKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the BindingTemplate with specified bindingKey.
         *
         * @param bindingTemplate
         * @param node
         */
        public void unRegisterBinding(String bindingKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering binding key " + bindingKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_binding deleteBinding = new delete_binding();
                deleteBinding.authInfo=(authToken);
                deleteBinding.bindingKey = new string[]{bindingKey};
                getUDDINode().getTransport().getUDDIPublishService(node.publishUrl).delete_binding(deleteBinding);
            }
            catch (Exception e)
            {
                log.error("Unable to unregister bindingkey " + bindingKey
                        + " ." + e.Message, e);
            }
        }

        public void unRegisterTModel(String tModelKey)
        {
            unRegisterTModel(tModelKey, this.getUDDINode().getApiNode());
        }

        /**
         * Unregisters the BindingTemplate with specified bindingKey.
         *
         * @param bindingTemplate
         * @param node
         */
        public void unRegisterTModel(String tModelKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering tModel key " + tModelKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                org.uddi.apiv3.delete_tModel deleteTModel = new org.uddi.apiv3.delete_tModel();
                deleteTModel.authInfo=(authToken);
                deleteTModel.tModelKey=new string[]{tModelKey};
                getUDDINode().getTransport().getUDDIPublishService(node.publishUrl).delete_tModel(deleteTModel);
            }
            catch (Exception e)
            {
                log.error("Unable to unregister tModelkey " + tModelKey
                        + " ." + e.Message, e);
            }
        }

        public void unRegisterSubscription(String subscriptionKey)
        {
            unRegisterSubscription(subscriptionKey, this.getUDDINode().getApiNode());
        }

        public void unRegisterSubscription(String subscriptionKey, org.apache.juddi.apiv3.node node)
        {
            log.info("UnRegistering subscription with key " + subscriptionKey);
            try
            {
                String authToken = getAuthToken(node.securityUrl);
                delete_subscription deleteSubscription = new delete_subscription();
                deleteSubscription.authInfo=(authToken);
                deleteSubscription.subscriptionKey = new string[]{subscriptionKey};
                getUDDINode().getTransport().getUDDISubscriptionService(node.securityUrl).delete_subscription(deleteSubscription);
            }
            catch (Exception e)
            {
                log.error("Unable to unregister subscription key " + subscriptionKey
                        + " ." + e.Message, e);
            }
        }

        public tModelList findTModel(find_tModel findTModel)
        {
            return this.findTModel(findTModel, this.uddinode.getApiNode());
        }

        public tModelList findTModel(find_tModel findTModel, org.apache.juddi.apiv3.node node)
        {

            findTModel.authInfo=(getAuthToken(node.securityUrl));
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

        public tModelDetail getTModelDetail(String tModelKey)
        {
            get_tModelDetail getTModelDetail = new get_tModelDetail();
            getTModelDetail.tModelKey = new string[]{tModelKey};
            return this.getTModelDetail(getTModelDetail);
        }

        public tModelDetail getTModelDetail(get_tModelDetail getTModelDetail)
        {
            return this.getTModelDetail(getTModelDetail, this.getUDDINode().getApiNode());
        }

        public tModelDetail getTModelDetail(get_tModelDetail getTModelDetail, org.apache.juddi.apiv3.node node)
        {

            getTModelDetail.authInfo=(getAuthToken(node.securityUrl));
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

        public businessService findService(String serviceKey)
        {
            return findService(serviceKey, this.uddinode.getApiNode());
        }

        public businessService findService(String serviceKey, org.apache.juddi.apiv3.node node)
        {
            get_serviceDetail getServiceDetail = new get_serviceDetail();
            getServiceDetail.serviceKey=new string[]{serviceKey};
            getServiceDetail.authInfo=(getAuthToken(node.securityUrl));
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

        public bindingTemplate findServiceBinding(String bindingKey)
        {
            return findServiceBinding(bindingKey, this.uddinode.getApiNode());
        }

        public bindingTemplate findServiceBinding(String bindingKey, org.apache.juddi.apiv3.node node)
        {
            get_bindingDetail getBindingDetail = new get_bindingDetail();
            getBindingDetail.bindingKey = new string[]{bindingKey};
            getBindingDetail.authInfo=(getAuthToken(node.securityUrl));
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

        public org.uddi.apiv3.businessEntity findBusiness(String businessKey)
        {
            return this.findBusiness(businessKey, this.getUDDINode().getApiNode());
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
        public businessEntity findBusiness(String businessKey, org.apache.juddi.apiv3.node node)
        {
            get_businessDetail getBusinessDetail = new get_businessDetail();
            getBusinessDetail.businessKey = new string[]{businessKey};
            getBusinessDetail.authInfo=(node.securityUrl);
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
        public relatedBusinessesList findRelatedBusinesses(String businessKey, org.apache.juddi.apiv3.node node)
        {
            find_relatedBusinesses findRelatedBusinesses = new find_relatedBusinesses();
            findRelatedBusinesses.Item=(businessKey);
            findRelatedBusinesses.authInfo=(node.securityUrl);
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
        public String getAuthToken(String endpointURL)
        {
            //if the token is older then 10 minutes discard it, and create a new one.
            if ((authToken != null && !"".Equals(authToken)) && (tokenBirthDate != null && DateTime.Now > tokenBirthDate.AddMilliseconds(600000)))
            {
                discard_authToken discardAuthToken = new discard_authToken();
                discardAuthToken.authInfo=(authToken);
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
                getAuthToken.userID=(getPublisher());
                if (isEncrypted)
                {
                    if (String.IsNullOrEmpty(cryptoProvider))
                    {
                        log.error("Credentials are encrypted but no cryptoProvider was defined in the config file!");
                    }else
                        try
                        {
                            getAuthToken.cred=(CryptorFactory.getCryptor(this.cryptoProvider).decrypt(getPassword()));
                        }
                        catch (Exception ex)
                        {
                            log.error("Unable to decrypt credentials! sending it as is", ex);
                            getAuthToken.cred=(getPassword());
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

        public apiv3.node[] savenode(org.apache.juddi.apiv3.node node)
        {
            apiv3.node[] nodeDetail = null;
            try
            {
                log.info("Sending node " + node.name + " info to jUDDI " + getUDDINode().getName());
                org.apache.juddi.apiv3.save_noodeInfo savenode = new org.apache.juddi.apiv3.save_noodeInfo();
                savenode.authInfo=(getAuthToken(node.securityUrl));
                savenode.node=new apiv3.node[]{node};
                using (org.apache.juddi.apiv3.JUDDIApiService juddi = getUDDINode().getTransport().getJUDDIApiService(node.juddiApiUrl))
                {
                    nodeDetail = juddi.save_Node(savenode);
                }
            }
            catch (Exception e)
            {
                log.error("Unable to save node " + node.name
                        + " ." + e.Message, e);
            }
            
            return nodeDetail;
        }

        /* public ClerkDetail saveClerk(UDDIClerk senderClerk)
         {
             ClerkDetail clerkDetail = null;
             try
             {
                 log.debug("Sending Clerk " + senderClerk.getName() + " info to jUDDI " + getUDDINode().getName());
                 SaveClerk saveClerk = new SaveClerk();
                 saveClerk.setAuthInfo(getAuthToken(senderClerk.getUDDINode().getSecurityUrl()));
                 saveClerk.getClerk().add(getApiClerk());
                 clerkDetail = getUDDINode().getTransport().getJUDDIApiService(senderClerk.getUDDINode().getJuddiApiUrl()).save_Clerk(saveClerk);
             }
             catch (Exception e)
             {
                 log.error("Unable to save clerk " + getName()
                         + " ." + e.Message, e);
             }
            
             return clerkDetail;
         }
        
         public Clerk getApiClerk()
         {
             Clerk apiClerk = new Clerk();
             apiClerk.setName(name);
             apiClerk.setnode(uddinode.getApiNode());
             apiClerk.setPassword(password);
             apiClerk.setPublisher(publisher);
             return apiClerk;
         }
         */
        public UDDINode getUDDINode()
        {
            return uddinode;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void setUDDInode(UDDINode uddinode)
        {
            this.uddinode = uddinode;
        }

        public String getPublisher()
        {
            return publisher;
        }

        public void setPublisher(String publisher)
        {
            this.publisher = publisher;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public WSDL[] getWsdls()
        {
            return wsdls;
        }

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

        /**
         *A helper class to create a tModel key generator.<br>
         * Why would I want a key generator? In UDDIv3, you're support to specify what you want the keys (unique identifiers) to be, however there's
         * a number of naming rules associated with the keys. Generally, use the FQDN of your business or organization.
         * Optionally, when saving an UDDI entity, you can just leave the key name blank and the server
         * should generate one for you. It's normally a UUID that's not easy to remember. In this case, there's no need to call this method.<br><br>
         * In addition, no changes are made to the UDDI server. You'll have to do that one using code similar to this:
         * <pre>
         * UDDIClerk clerk = ...
         * tModel keygen = UDDIClerk.createKeyGenator("mydomain.com", "my domain", "en");
         * clerk.register(keygen);
         * 
         * @param partitionName think of this as the domain, i.e. juddi.apache.org, but it can really be anything you want. This will become part of the
         * key associated with the tModel generator (uddi:juddi.apache.org:keygenerator)
         * @param DescriptiveName required. max length is 255 char
         * @param DescriptiveNameLanguage optional, max length is 26 char
         * @return a populated tModel entity representing a tModel key generator. No changes are made to any connect UDDI service
         * @since 3.2
         */
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
                throw new ArgumentOutOfRangeException("partitionName must have a 'uddi:' prefix");

            }
            if (!partitionName.EndsWith(":keygenerator"))
            {
                throw new ArgumentOutOfRangeException("partitionName must have a ':keyGenerator' postfix");
            }
            tModel tm = new tModel();
            tm.name = new name();
            tm.name.Value=(DescriptiveName);
            tm.name.lang=(DescriptiveNameLanguage);
            tm.categoryBag = new categoryBag();
            keyedReference kr = new keyedReference();
            kr.tModelKey=(UDDIConstants.KEY_GENERATOR_TMODEL);
            kr.keyName=(UDDIConstants.KEY_GENERATOR);
            kr.keyValue=(UDDIConstants.KEY_GENERATOR_VALUE);
            tm.categoryBag.Items = new object[] { kr };
            overviewDoc
                overviewDoc = new overviewDoc();
            overviewURL overviewUrl = new overviewURL();
            overviewUrl.useType=("text");
            overviewUrl.Value=("http://uddi.org/pubs/uddi_v3.htm#keyGen");
            overviewDoc.Items= new object[]{overviewUrl};
            tm.overviewDoc = new overviewDoc[]{overviewDoc};
            tm.tModelKey=(partitionName.ToLower());
            return tm;
        }

        internal void registerWsdls()
        {
            throw new NotImplementedException();
        }

        internal void unRegisterWsdls()
        {
            throw new NotImplementedException();
        }

        internal void saveNode(apiv3.node node)
        {
            throw new NotImplementedException();
        }

        public void setCryptoProvider(string p)
        {

            this.cryptoProvider = p;
        }

        public void setPasswordEncrypted(bool p)
        {
            this.isEncrypted = p;
        }

        public string getCryptoProvider()
        {

            return this.cryptoProvider;
        }

        public bool getPasswordEncrypted()
        {
            return this.isEncrypted;
        }

        public void Dispose()
        {
            uddinode.Dispose();
        }
    }
}
