package org.apache.juddi.api.impl;

/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Iterator;
import java.util.Random;

import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.config.Property;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckSubscriptionListener;
import org.apache.juddi.v3.tck.TckTModel;
import org.apache.juddi.v3.tck.UDDISubscriptionListenerImpl;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDISecurityPortType;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.tck.TckCommon;
import org.junit.Assume;
import org.junit.Ignore;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class API_090_SubscriptionListenerIntegrationTest {

        private static final Log logger = LogFactory.getLog(API_090_SubscriptionListenerIntegrationTest.class);
        private static API_010_PublisherTest api010 = new API_010_PublisherTest();
        private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        static UDDIPublicationPortType publication = new UDDIPublicationImpl();
        static UDDISubscriptionPortType subscription = new UDDISubscriptionImpl();
        private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckSubscriptionListener tckSubscriptionListener = new TckSubscriptionListener(new UDDISubscriptionImpl(), new UDDIPublicationImpl());
        private static Endpoint endPoint;
        private static String authInfoJoe = null;
        private static String authInfoMary = null;
        private static SimpleSmtpServer mailServer;

        private static Integer smtpPort = 25;
        private static Integer httpPort = 80;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                //manager.stop();
                //shutting down the TCK SubscriptionListener
                endPoint.stop();
                Registry.stop();
                mailServer.stop();
        }
        static String httpEndpoint = null;

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                Registry.start();
                try {
                        smtpPort = 9700 + new Random().nextInt(99);
                        httpPort = 9600 + new Random().nextInt(99);
                        System.setProperty(Property.DEFAULT_JUDDI_EMAIL_PREFIX + "mail.smtp.host", "localhost");
                        System.setProperty(Property.DEFAULT_JUDDI_EMAIL_PREFIX + "mail.smtp.port", String.valueOf(smtpPort));
                        System.setProperty(Property.DEFAULT_JUDDI_EMAIL_PREFIX + "mail.smtp.from", "jUDDI@example.org");
                        mailServer = SimpleSmtpServer.start(smtpPort);
                        //bring up the TCK HTTP SubscriptionListener
                        httpEndpoint = "http://localhost:" + httpPort + "/tcksubscriptionlistener";
                        System.out.println("Bringing up SubscriptionListener endpoint at " + httpEndpoint);
                        endPoint = Endpoint.publish(httpEndpoint, new UDDISubscriptionListenerImpl());
                        logger.debug("Getting auth tokens..");

                        api010.saveJoePublisher();
                        api010.saveMaryPublisher();
                        UDDISecurityPortType security = new UDDISecurityImpl();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        Assert.assertNotNull(authInfoJoe);

                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        Assert.assertNotNull(authInfoMary);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail(e.getMessage());
                }
        }

        @Test
        public void joePublisherUpdateService_HTTP() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        //Saving the binding template that will be called by the server for a subscription event
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        //Saving the HTTP Listener Service
                        tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, "localhost");
                        //Saving the HTTP Subscription
                        tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessService.updateJoePublisherService(authInfoJoe, "foo");

                        //waiting up to 100 seconds for the listener to notice the change.
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        logger.info("Received HTTP Notification");
                                        break;
                                }
                        }
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                Assert.fail("No HttpNotification was sent");
                        }
                        if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Service One</name>")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void joePublisherUpdateService_SMTP() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        //Saving the binding template that will be called by the server for a subscription event
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        //Saving the SMTP Listener Service
                        tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_XML, 0, "localhost");
                        //Saving the SMTP Subscription
                        tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessService.updateJoePublisherService(authInfoJoe, "foo");

                        //waiting up to 100 seconds for the listener to notice the change.
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (mailServer.getReceivedEmailSize() > 0) {
                                        logger.info("Received Email Notification");
                                        break;
                                }
                        }
                        if (mailServer.getReceivedEmailSize() == 0) {
                                Assert.fail("No SmtpNotification was sent");
                        }
                        @SuppressWarnings("rawtypes")
                        Iterator emailIter = mailServer.getReceivedEmail();
                        SmtpMessage email = (SmtpMessage) emailIter.next();
                        System.out.println("Subject:" + email.getHeaderValue("Subject"));
                        System.out.println("Body:" + email.getBody());

                        if (!email.getBody().contains("Service One")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void joePublisherUpdateBusiness_HTTP() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, "localhost");
                        //Saving the Subscription
                        tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting Business ********** ");
                        tckBusiness.updateJoePublisherBusiness(authInfoJoe);

                        //waiting up to 100 seconds for the listener to notice the change.
                        String test = "";
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        logger.info("Received Notification");
                                        break;
                                } else {
                                        System.out.print(test);
                                }
                        }
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                Assert.fail("No Notification was sent");
                        }
                        if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Service One</name>")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        /**
         * PUBLISHERASSERTION tests joe want's updates on when someone asserts
         * that they own his business
         *
         * @throws Exception
         */
        @Test
        //@Ignore
        public void joePublisherUpdate_PUBLISHERASSERTION_DETAIL_TO() throws Exception {

                logger.info("joePublisherUpdate_HTTP_PUBLISHERASSERTION_DETAIL_TO");

                UDDISubscriptionListenerImpl.notifcationMap.clear();
                UDDISubscriptionListenerImpl.notificationCount=0;
                Holder<List<Subscription>> holder = null;
                try {

                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        //tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModel.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusiness.saveMaryPublisherBusiness(authInfoMary);

                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        String bindingkey = tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, "localhost");

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
                        subscription.saveSubscription(authInfoJoe, holder);
                        logger.info("subscription saved for " + holder.value.get(0).getSubscriptionKey());
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("saving Mary's publisher assertion********** ");
                        AddPublisherAssertions pa = new AddPublisherAssertions();
                        pa.setAuthInfo(authInfoMary);
                        PublisherAssertion pas = new PublisherAssertion();
                        pas.setToKey(TckBusiness.MARY_BUSINESS_KEY);
                        pas.setFromKey(TckBusiness.JOE_BUSINESS_KEY);
                        pas.setKeyedReference(new KeyedReference(UDDIConstants.RELATIONSHIPS, "parent", "parent-child"));
                        pa.getPublisherAssertion().add(pas);

                        publication.addPublisherAssertions(pa);
                        boolean found = verifyDelivery(TckBusiness.MARY_BUSINESS_KEY);

                        DeletePublisherAssertions deletePublisherAssertions = new DeletePublisherAssertions();
                        deletePublisherAssertions.setAuthInfo(authInfoMary);
                        deletePublisherAssertions.getPublisherAssertion().add(pas);
                        publication.deletePublisherAssertions(deletePublisherAssertions);
                        if (!found) {

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
                        subscription.deleteSubscription(ds);
                        tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModel.deleteMaryPublisherTmodel(authInfoMary);

                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                }
        }

        public static boolean  verifyDelivery(String findMe) {
                for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                        try {
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                                     
                                        while (it.hasNext()) {
                                                String test = it.next();
                                                if (test.toLowerCase().contains(findMe.toLowerCase())) {
                                                         return true;
                                                }
                                        }
                                }
                                Thread.sleep(1000);
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                        System.out.print(".");
                        //if (UDDISubscriptionListenerImpl.notificationCount > 0) {                        }
                }
                logger.info("RX " + UDDISubscriptionListenerImpl.notificationCount + " notifications");
                Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                boolean found = false;

                while (it.hasNext()) {
                        String test = it.next();
                        if (TckCommon.isDebug()) {
                                logger.info("Notification: " + test);
                        }
                        if (test.toLowerCase().contains(findMe.toLowerCase())) {
                                found = true;
                        }
                }
                return found;
        }

}
