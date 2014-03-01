/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.v3.tck;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelBag;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 *
 * @author Alex O'Ree
 */
public abstract class UDDI_090_SubscriptionListenerIntegrationBase {

        protected static Log logger = LogFactory.getLog(UDDI_090_SubscriptionListenerIntegrationBase.class);
        private static UDDISubscriptionPortType subscriptionMary = null;
        private static UDDIInquiryPortType inquiryMary = null;
        private static TckTModel tckTModelMary = null;
        private static TckBusiness tckBusinessMary = null;
        private static TckBusinessService tckBusinessServiceMary = null;
        private static TckSubscriptionListener tckSubscriptionListenerMary = null;
        private static String hostname = null;
        private static UDDISubscriptionPortType subscriptionJoe = null;
        private static UDDIInquiryPortType inquiryJoe = null;
        private static UDDIPublicationPortType publicationMary = null;
        private static UDDIPublicationPortType publicationJoe = null;
        private static TckTModel tckTModelJoe = null;
        private static TckBusiness tckBusinessJoe = null;
        private static TckBusinessService tckBusinessServiceJoe = null;
        private static TckSubscriptionListener tckSubscriptionListenerJoe = null;

        private static String authInfoJoe = null;
        private static String authInfoMary = null;
        private static UDDIClient manager;

        public static void stopManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                tckTModelMary.deleteCreatedTModels(authInfoMary);
                manager.stop();
                //shutting down the TCK SubscriptionListener

        }

        public static void startManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                try {
                        /*                 smtpPort = 9700 + new Random().nextInt(99);
                         httpPort = 9600 + new Random().nextInt(99);
                         Properties properties = new Properties();
                         properties.setProperty("juddi.mail.smtp.host", "localhost");
                         properties.setProperty("juddi.mail.smtp.port", String.valueOf(smtpPort));
                         properties.setProperty("juddi.mail.smtp.from", "jUDDI@example.org");
                         String version = Release.getRegistryVersion().replaceAll(".SNAPSHOT", "-SNAPSHOT");
                         String curDir = System.getProperty("user.dir");
                         if (!curDir.endsWith("uddi-tck")) {
                         curDir += "/uddi-tck";
                         }
                         String path = curDir + "/target/juddi-tomcat-" + version + "/temp/";
                         System.out.println("Saving jUDDI email properties to " + path);
                         File tmpDir = new File(path);
                         File tmpFile = new File(tmpDir + "/juddi-mail.properties");
                         if (!tmpFile.createNewFile()) {
                         tmpFile.delete();
                         tmpFile.createNewFile();
                         }
                         properties.store(new FileOutputStream(tmpFile), "tmp email settings");
                         */

                        manager = new UDDIClient();
                        manager.start();

                        logger.debug("Getting auth tokens..");

                        Transport transport = manager.getTransport("uddiv3");
                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        //Assert.assertNotNull(authInfoJoe);

                        publicationJoe = transport.getUDDIPublishService();
                        inquiryJoe = transport.getUDDIInquiryService();
                        subscriptionJoe = transport.getUDDISubscriptionService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                        tckTModelJoe = new TckTModel(publicationJoe, inquiryJoe);
                        tckBusinessJoe = new TckBusiness(publicationJoe, inquiryJoe);
                        tckBusinessServiceJoe = new TckBusinessService(publicationJoe, inquiryJoe);
                        tckSubscriptionListenerJoe = new TckSubscriptionListener(subscriptionJoe, publicationJoe);

                        transport = manager.getTransport("uddiv3");
                        publicationMary = transport.getUDDIPublishService();
                        inquiryMary = transport.getUDDIInquiryService();
                        subscriptionMary = transport.getUDDISubscriptionService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        }

                        tckTModelMary = new TckTModel(publicationMary, inquiryMary);
                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);

                        tckBusinessMary = new TckBusiness(publicationMary, inquiryMary);
                        tckBusinessServiceMary = new TckBusinessService(publicationMary, inquiryMary);
                        tckSubscriptionListenerMary = new TckSubscriptionListener(subscriptionMary, publicationMary);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        /**
         * verifies the delivery of the notification, will block until timeout
         * or success
         *
         * @param findMe
         * @return true is success
         */
        public abstract boolean verifyDelivery(String findMe);

        /**
         * reform transport specific resets of the listener
         */
        public abstract void reset();

        public abstract String getXMLLocationOfServiceForDelivery();

        /**
         * used for logging purposes
         *
         * @return
         */
        public abstract String getTransport();

        /**
         * listener port
         *
         * @return
         */
        public abstract int getPort();

        /**
         * either returns localhost hostname or an email or delivery address
         *
         * @return
         */
        public abstract String getHostame();

        @Test
        public void joePublisherUpdate_FIND_SERVICE() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_FIND_SERVICE");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        String before = TckCommon.DumpAllServices(authInfoJoe, inquiryJoe);
                        reset();

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        //Saving the binding template that will be called by the server for a subscription event
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the HTTP Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());
                        //Saving the HTTP Subscription
                        String saveNotifierSubscription = tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, getSubscription1XML());
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessServiceJoe.updateJoePublisherService(authInfoJoe, "Service One");

                        boolean found = verifyDelivery("Service One");

                        if (!found) {
                                logger.warn("Test failed, dumping service list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllServices(authInfoJoe, inquiryJoe));
                                //if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Service One</name>")) {
                                Assert.fail("Notification does not contain the correct service");
                        }
                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, getSubscriptionKey1());
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void joePublisherUpdate_FIND_BUSINESS() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_FIND_BUSINESS");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        TckCommon.DeleteBusiness(TckBusiness.MARY_BUSINESS_KEY, authInfoMary, publicationMary);
                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        reset();

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        logger.info("Saving Joe's callback endpoint ********** ");
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());
                        //Saving the Subscription
                        logger.info("Saving Joe's subscription********** ");
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, getSubscription2XML());
                        //Changing the service we subscribed to "JoePublisherService"

                        logger.info("Saving Mary's Business ********** ");
                        tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        boolean found = verifyDelivery("mary");

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service");
                        }
                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, getSubscriptionKey2());
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        TckCommon.DeleteBusiness(TckBusiness.MARY_BUSINESS_KEY, authInfoMary, publicationMary);
                        //tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                }
        }

        public abstract String getSubscription1XML();

        public abstract String getSubscription2XML();

        public abstract String getSubscription3XML();

        public abstract String getSubscriptionKey1();

        public abstract String getSubscriptionKey2();

        public abstract String getSubscriptionKey3();

        //tmodel tests
        @Test
        public void joePublisherUpdate_FIND_TMODEL() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_FIND_TMODEL " + getXMLLocationOfServiceForDelivery() + " " + getPort() + " " + getHostame());
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                try {
                        reset();

                        String before = TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service

                        tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());
                        //Saving the Subscription
                        String saveNotifierSubscription = tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, getSubscription3XML());
                        logger.info("subscription saved for " + saveNotifierSubscription);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting tModel ********** ");
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);

                        boolean found = verifyDelivery("tModel One");

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, getSubscriptionKey3());
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                }
        }

        /**
         * getBusiness tests joe want's updates on mary's business
         *
         * @throws Exception
         */
        @Test
        public void joePublisherUpdate_GET_BUSINESS_DETAIL() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_GET_BUSINESS_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetBusinessDetail(new GetBusinessDetail());
                        sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().add(TckBusiness.MARY_BUSINESS_KEY);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's business ********** ");
                        updatePublisherBusiness(authInfoMary, saveMaryPublisherBusiness, publicationMary);

                        boolean found = verifyDelivery("Updated Name");

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        /**
         * getBusiness tests joe want's updates on mary's business
         *
         * @throws Exception
         */
        @Test
        public void joePublisherUpdate_GET_TMODEL_DETAIL() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_GET_TMODEL_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        TModel saveMaryPublisherTmodel = tckTModelMary.saveMaryPublisherTmodel(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetTModelDetail(new GetTModelDetail());
                        sub.getSubscriptionFilter().getGetTModelDetail().getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's tModel ********** ");
                        updateTModel(authInfoMary, saveMaryPublisherTmodel, publicationMary);

                        boolean found = verifyDelivery("a new description");

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        //tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        //TODO If a subscriber specifies a maximum number of entries to be returned with a subscription and the amount of data to be returned exceeds 
//this limit, or if the node determines based on its policy that there are too many entries to be returned in a single group, 
        //then the node SHOULD provide a chunkToken with results.  
        //TODO  If no more results are pending, the value of the chunkToken MUST be "0".
        public static void updateTModel(String authInfoMary, TModel saveMaryPublisherTmodel, UDDIPublicationPortType publicationMary) throws Exception {
                saveMaryPublisherTmodel.getDescription().add(new Description("a new description", null));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoMary);
                stm.getTModel().add(saveMaryPublisherTmodel);
                publicationMary.saveTModel(stm);
        }

        /**
         * adds a new name to the business, then resaves it
         *
         * @param auth
         * @param biz
         * @param pub
         */
        public static void updatePublisherBusiness(String auth, BusinessEntity biz, UDDIPublicationPortType pub) throws Exception {
                biz.getName().add(new Name("Updated Name", "en"));
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(auth);
                sb.getBusinessEntity().add(biz);
                pub.saveBusiness(sb);
        }

        /**
         * getService tests joe want's updates on mary's service
         *
         * @throws Exception
         */
        @Test
        public void joePublisherUpdate_GET_SERVICE_DETAIL() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_GET_SERVICE_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        BusinessService bs = new BusinessService();
                        bs.setBusinessKey(saveMaryPublisherBusiness.getBusinessKey());
                        bs.setServiceKey(TckTModel.MARY_KEY_PREFIX + UUID.randomUUID().toString());
                        bs.getName().add(new Name("Mary's service for " + getTransport(), null));
                        SaveService ss = new SaveService();
                        ss.getBusinessService().add(bs);
                        ss.setAuthInfo(authInfoMary);
                        bs = publicationMary.saveService(ss).getBusinessService().get(0);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetServiceDetail(new GetServiceDetail());
                        sub.getSubscriptionFilter().getGetServiceDetail().getServiceKey().add(bs.getServiceKey());

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's service ********** ");
                        updatePublisherService(authInfoMary, bs, publicationMary);

                        boolean found = verifyDelivery("Updated Name");

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        /**
         * getBinding tests joe want's updates on mary's binding
         *
         * @throws Exception
         */
        @Test
        public void joePublisherUpdate_GET_BINDING_DETAIL() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_GET_BINDING_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        BusinessService bs = new BusinessService();
                        bs.setBusinessKey(saveMaryPublisherBusiness.getBusinessKey());
                        bs.setServiceKey(TckTModel.MARY_KEY_PREFIX + UUID.randomUUID().toString());
                        bs.getName().add(new Name("Mary's service for " + getTransport(), null));
                        bs.setBindingTemplates(new BindingTemplates());
                        BindingTemplate bt = new BindingTemplate();
                        bt.setAccessPoint(new AccessPoint("http://localhost", "endPoint"));
                        bt.setBindingKey(TckTModel.MARY_KEY_PREFIX + UUID.randomUUID().toString());
                        bt.setServiceKey(bs.getServiceKey());
                        bt = UDDIClient.addSOAPtModels(bt);

                        bs.getBindingTemplates().getBindingTemplate().add(bt);
                        SaveService ss = new SaveService();
                        ss.getBusinessService().add(bs);
                        ss.setAuthInfo(authInfoMary);
                        bs = publicationMary.saveService(ss).getBusinessService().get(0);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetBindingDetail(new GetBindingDetail());
                        sub.getSubscriptionFilter().getGetBindingDetail().getBindingKey().add(bt.getBindingKey());

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's binding ********** ");
                        updatePublisherBinding(authInfoMary, bt, publicationMary);

                        boolean found = verifyDelivery("wsdlDeployment");

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        /**
         * PUBLISHERASSERTION tests joe want's updates on mary's binding
         *
         * @throws Exception
         */
        @Test
        @Ignore
        public void joePublisherUpdate_PUBLISHERASSERTION_DETAIL_TO() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_PUBLISHERASSERTION_DETAIL_TO");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        //tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                        sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.STATUS_TO_KEY_INCOMPLETE);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("saving Mary's publisher assertion********** ");
                        AddPublisherAssertions pa = new AddPublisherAssertions();
                        pa.setAuthInfo(authInfoMary);
                        PublisherAssertion pas = new PublisherAssertion();
                        pas.setToKey(TckBusiness.JOE_BUSINESS_KEY);
                        pas.setFromKey(TckBusiness.MARY_BUSINESS_KEY);
                        pas.setKeyedReference(new KeyedReference(UDDIConstants.RELATIONSHIPS, "parent", "parent-child"));
                        pa.getPublisherAssertion().add(pas);

                        publicationMary.addPublisherAssertions(pa);

                        boolean found = verifyDelivery(TckBusiness.MARY_BUSINESS_KEY);

                        DeletePublisherAssertions deletePublisherAssertions = new DeletePublisherAssertions();
                        deletePublisherAssertions.setAuthInfo(authInfoMary);
                        deletePublisherAssertions.getPublisherAssertion().add(pas);
                        publicationMary.deletePublisherAssertions(deletePublisherAssertions);
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        /**
         * PUBLISHERASSERTION tests joe want's updates on mary's binding
         *
         * @throws Exception
         */
        @Test
        @Ignore
        public void joePublisherUpdate_PUBLISHERASSERTION_DETAIL_FROM() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_PUBLISHERASSERTION_DETAIL_FROM");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        //tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                        sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("saving Mary's publisher assertion********** ");
                        AddPublisherAssertions pa = new AddPublisherAssertions();
                        pa.setAuthInfo(authInfoMary);
                        PublisherAssertion pas = new PublisherAssertion();
                        pas.setToKey(TckBusiness.JOE_BUSINESS_KEY);
                        pas.setFromKey(TckBusiness.MARY_BUSINESS_KEY);
                        pas.setKeyedReference(new KeyedReference(UDDIConstants.RELATIONSHIPS, "parent", "parent-child"));
                        pa.getPublisherAssertion().add(pas);

                        publicationMary.addPublisherAssertions(pa);

                        boolean found = verifyDelivery(TckBusiness.MARY_BUSINESS_KEY);

                        DeletePublisherAssertions deletePublisherAssertions = new DeletePublisherAssertions();
                        deletePublisherAssertions.setAuthInfo(authInfoMary);
                        deletePublisherAssertions.getPublisherAssertion().add(pas);
                        publicationMary.deletePublisherAssertions(deletePublisherAssertions);
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        /**
         * PUBLISHERASSERTION2 tests joe want's updates on mary's binding
         *
         * @throws Exception
         */
        @Test
        @Ignore
        public void joePublisherUpdate_PUBLISHERASSERTION_DETAIL_COMPLETE() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_PUBLISHERASSERTION_DETAIL_COMPLETE");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        //tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                        sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.STATUS_COMPLETE);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("saving Mary's publisher assertion********** ");
                        AddPublisherAssertions pa = new AddPublisherAssertions();
                        pa.setAuthInfo(authInfoMary);
                        PublisherAssertion pas = new PublisherAssertion();
                        pas.setToKey(TckBusiness.JOE_BUSINESS_KEY);
                        pas.setFromKey(TckBusiness.MARY_BUSINESS_KEY);
                        pas.setKeyedReference(new KeyedReference(UDDIConstants.RELATIONSHIPS, "parent", "parent-child"));
                        pa.getPublisherAssertion().add(pas);

                        publicationMary.addPublisherAssertions(pa);
                        pa.setAuthInfo(authInfoJoe);
                        publicationJoe.addPublisherAssertions(pa);

                        boolean found = verifyDelivery(TckBusiness.MARY_BUSINESS_KEY);
                        DeletePublisherAssertions deletePublisherAssertions = new DeletePublisherAssertions();
                        deletePublisherAssertions.setAuthInfo(authInfoMary);
                        deletePublisherAssertions.getPublisherAssertion().add(pas);
                        publicationMary.deletePublisherAssertions(deletePublisherAssertions);
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        //
        /**
         * find related businesses tests joe want's updates on mary's binding
         *
         * @throws Exception
         */
        @Test
        @Ignore
        public void joePublisherUpdate_FIND_RELATED_BIZ() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_FIND_RELATED_BIZ");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        //tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindRelatedBusinesses(new FindRelatedBusinesses());
                        sub.getSubscriptionFilter().getFindRelatedBusinesses().setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("saving Mary's publisher assertion********** ");
                        AddPublisherAssertions pa = new AddPublisherAssertions();
                        pa.setAuthInfo(authInfoMary);
                        PublisherAssertion pas = new PublisherAssertion();
                        pas.setToKey(TckBusiness.JOE_BUSINESS_KEY);
                        pas.setFromKey(TckBusiness.MARY_BUSINESS_KEY);
                        pas.setKeyedReference(new KeyedReference(UDDIConstants.RELATIONSHIPS, "parent", "parent-child"));
                        pa.getPublisherAssertion().add(pas);

                        publicationMary.addPublisherAssertions(pa);
                        logger.info("saving Joe's publisher assertion********** ");
                        pa.setAuthInfo(authInfoJoe);
                        publicationJoe.addPublisherAssertions(pa);

                        boolean found = verifyDelivery(TckBusiness.MARY_BUSINESS_KEY);
                        DeletePublisherAssertions deletePublisherAssertions = new DeletePublisherAssertions();
                        deletePublisherAssertions.setAuthInfo(authInfoMary);
                        deletePublisherAssertions.getPublisherAssertion().add(pas);
                        publicationMary.deletePublisherAssertions(deletePublisherAssertions);
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        /**
         * getBinding tests joe want's updates on mary's binding
         *
         * @throws Exception
         */
        @Test
        @Ignore
        public void joePublisherUpdate_FIND_BINDING() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(getHostame());
                logger.info("joePublisherUpdate_" + getTransport() + "_FIND_BINDING");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        reset();

                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        BusinessService bs = new BusinessService();
                        bs.setBusinessKey(saveMaryPublisherBusiness.getBusinessKey());
                        bs.setServiceKey(TckTModel.MARY_KEY_PREFIX + UUID.randomUUID().toString());
                        bs.getName().add(new Name("Mary's service for " + getTransport(), null));
                        
                        bs.setBindingTemplates(new BindingTemplates());
                        BindingTemplate bt = new BindingTemplate();
                        bt.setAccessPoint(new AccessPoint("http://localhost", "endPoint"));
                        bt.setBindingKey(TckTModel.MARY_KEY_PREFIX + UUID.randomUUID().toString());
                        bt.setServiceKey(bs.getServiceKey());
                        bt = UDDIClient.addSOAPtModels(bt);

                        bs.getBindingTemplates().getBindingTemplate().add(bt);
                        SaveService ss = new SaveService();
                        ss.getBusinessService().add(bs);
                        ss.setAuthInfo(authInfoMary);
                        bs = publicationMary.saveService(ss).getBusinessService().get(0);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListenerJoe.saveService(authInfoJoe, getXMLLocationOfServiceForDelivery(), getPort(), getHostame());

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey(bindingkey);
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindBinding(new FindBinding());
                        sub.getSubscriptionFilter().getFindBinding().setServiceKey(bs.getServiceKey());
                        sub.getSubscriptionFilter().getFindBinding().setTModelBag(new TModelBag());
                        sub.getSubscriptionFilter().getFindBinding().getTModelBag().getTModelKey().add("uddi:uddi.org:categorization:types");
                        

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's binding ********** ");
                        updatePublisherBinding(authInfoMary, bt, publicationMary);

                        boolean found = verifyDelivery("wsdlDeployment");

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service.");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        //tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authInfoJoe);
                        ds.getSubscriptionKey().add(holder.value.get(0).getSubscriptionKey());
                        subscriptionJoe.deleteSubscription(ds);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        //find binding
        private void updatePublisherService(String authInfo, BusinessService bs, UDDIPublicationPortType pub) throws Exception {
                bs.getName().add(new Name("Updated name", null));
                SaveService ss = new SaveService();
                ss.getBusinessService().add(bs);
                ss.setAuthInfo(authInfo);
                pub.saveService(ss);
        }

        private void updatePublisherBinding(String authInfo, BindingTemplate bt, UDDIPublicationPortType pub) throws Exception {
                SaveBinding sb = new SaveBinding();
                sb.setAuthInfo(authInfo);
                bt.getAccessPoint().setUseType("wsdlDeployment");
                sb.getBindingTemplate().add(bt);
                pub.saveBinding(sb);
        }

}
