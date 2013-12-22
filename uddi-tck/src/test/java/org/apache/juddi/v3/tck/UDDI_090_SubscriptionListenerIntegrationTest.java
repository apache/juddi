package org.apache.juddi.v3.tck;

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
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.Release;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.net.InetAddress;
import javax.xml.ws.BindingProvider;
import org.apache.juddi.v3.client.UDDIConstants;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;

/**
 * Used during the Maven build process for testing against jUDDI
 *
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @see UDDI_090_SubscriptionListenerExternalTest
 */
public class UDDI_090_SubscriptionListenerIntegrationTest {

        private static Log logger = LogFactory.getLog(UDDI_090_SubscriptionListenerIntegrationTest.class);
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
        private static Endpoint endPoint;
        private static String authInfoJoe = null;
        private static String authInfoMary = null;
        private static UDDIClient manager;
        private static SimpleSmtpServer mailServer;
        private static Integer smtpPort = 25;
        private static Integer httpPort = 80;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                tckTModelMary.deleteCreatedTModels(authInfoMary);
                manager.stop();
                //shutting down the TCK SubscriptionListener
                endPoint.stop();
                endPoint = null;
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                try {
                        smtpPort = 9700 + new Random().nextInt(99);
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

                        hostname = InetAddress.getLocalHost().getHostName();
                        //bring up the TCK SubscriptionListener
                        String httpEndpoint = "http://" + hostname + ":" + httpPort + "/tcksubscriptionlistener";
                        System.out.println("Bringing up SubscriptionListener endpoint at " + httpEndpoint);
                        endPoint = Endpoint.publish(httpEndpoint, new UDDISubscriptionListenerImpl());
                        int count = 0;
                        while (!endPoint.isPublished()) {
                                httpPort = 9600 + new Random().nextInt(99);
                                httpEndpoint = "http://" + hostname + ":" + httpPort + "/tcksubscriptionlistener";
                                System.out.println("Bringing up SubscriptionListener endpoint at " + httpEndpoint);
                                endPoint = Endpoint.publish(httpEndpoint, new UDDISubscriptionListenerImpl());
                                count++;
                                if (count > 10) {
                                        Assert.fail("unable to bring up endpoint");
                                }
                        }

                        manager = new UDDIClient();
                        manager.start();

                        logger.debug("Getting auth tokens..");


                        Transport transport = manager.getTransport();
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




                        transport = manager.getTransport();
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

        @Test
        public void joePublisherUpdate_HTTP_FIND_SERVICE() {
                logger.info("joePublisherUpdate_HTTP_FIND_SERVICE");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        String before = TckCommon.DumpAllServices(authInfoJoe, inquiryJoe);
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        //Saving the binding template that will be called by the server for a subscription event
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the HTTP Listener Service
                        logger.info("sanity check, this shouldn't be the rmi file " + TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML);
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, hostname);
                        //Saving the HTTP Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription1.xml");
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessServiceJoe.updateJoePublisherService(authInfoJoe, "foo");

                        //waiting up to 100 seconds for the listener to notice the change.
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        logger.info("Received HTTP Notification");
                                        break;
                                }
                        }
                        Iterator<String> iterator = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                                String test = iterator.next();
                                if (test.toLowerCase().contains("service one")) {
                                        found = true;
                                }
                        }
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
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void joePublisherUpdate_SMTP_FIND_SERVICE() {
                logger.info("joePublisherUpdate_SMTP_FIND_SERVICE");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        String before = TckCommon.DumpAllServices(authInfoJoe, inquiryJoe);
                        if (mailServer != null && !mailServer.isStopped()) {
                                mailServer.stop();
                        }
                        mailServer = SimpleSmtpServer.start(smtpPort);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        //Saving the binding template that will be called by the server for a subscription event
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the SMTP Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_XML, 0, hostname);
                        //Saving the SMTP Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessServiceJoe.updateJoePublisherService(authInfoJoe, "foo");

                        //waiting up to 30 seconds for the listener to notice the change.
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(mailServer.getReceivedEmailSize() + ",");
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
                        boolean found = false;
                        while (emailIter.hasNext()) {
                                SmtpMessage email = (SmtpMessage) emailIter.next();
                                if (TckCommon.isDebug()) {
                                        System.out.println("Subject:" + email.getHeaderValue("Subject"));
                                        System.out.println("Body:" + email.getBody());
                                }
                                if (email.getBody().replace("=", "").contains("Service One")) {
                                        found = true;
                                }
                        }
                        if (!found) {
                                logger.warn("Test failed, dumping service list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllServices(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification NOT received. Emails received " + mailServer.getReceivedEmailSize());
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        mailServer.stop();
                }
        }

        @Test
        public void joePublisherUpdate_HTTP_FIND_BUSINESS() {
                logger.info("joePublisherUpdate_HTTP_FIND_BUSINESS");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        TckCommon.DeleteBusiness(TckBusiness.MARY_BUSINESS_KEY, authInfoMary, publicationMary);
                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);
                        Thread.sleep(5000);
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        logger.info("sanity check, this shouldn't be the rmi file " + TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML);
                        logger.info("Saving Joe's callback endpoint ********** ");
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, hostname);
                        //Saving the Subscription
                        logger.info("Saving Joe's subscription********** ");
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription2.xml");
                        //Changing the service we subscribed to "JoePublisherService"

                        logger.info("Clearing the inbox********** ");
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        Thread.sleep(2000);
                        logger.info("Saving Mary's Business ********** ");
                        tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        //waiting up to 10 seconds for the listener to notice the change.

                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                //if (UDDISubscriptionListenerImpl.notificationCount > 0) {

                                //}
                        }
                        logger.info("RX " + UDDISubscriptionListenerImpl.notificationCount + " notifications");
                        Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        boolean found = false;
                        while (it.hasNext()) {
                                String msg = it.next();
                                if (TckCommon.isDebug()) {
                                        logger.info("Notification: " + msg);
                                }
                                if (msg.toLowerCase().contains("mary")) {
                                        found = true;
                                }

                        }
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("No Notification was sent");
                        }
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
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        TckCommon.DeleteBusiness(TckBusiness.MARY_BUSINESS_KEY, authInfoMary, publicationMary);
                        //tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                }
        }

        @Test
        public void joePublisherUpdate_SMTP_FIND_BUSINESS() {
                logger.info("joePublisherUpdate_SMTP_FIND_BUSINESS");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        TckCommon.DeleteBusiness(TckBusiness.MARY_BUSINESS_KEY, authInfoMary, publicationMary);
                        if (mailServer != null && !mailServer.isStopped()) {
                                mailServer.stop();
                        }
                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);
                        mailServer = SimpleSmtpServer.start(smtpPort);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_XML, 0, hostname);
                        //tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION2_SMTP_XML);
                        //tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_XML);

                        Thread.sleep(3000);
                        logger.info("Saving Mary's Business ********** ");
                        //"uddi:uddi.marypublisher.com:marybusinessone"
                        tckBusinessMary.saveBusiness(authInfoMary, TckBusiness.MARY_BUSINESS_XML, TckBusiness.MARY_BUSINESS_KEY);

                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.println(mailServer.getReceivedEmailSize() + " message received.");
                                if (mailServer.getReceivedEmailSize() > 0) {
                                        //logger.info("Received Email Notification");
                                        //break;
                                }
                        }
                        if (mailServer.getReceivedEmailSize() == 0) {
                                Assert.fail("No SmtpNotification was sent");
                        }
                        @SuppressWarnings("rawtypes")
                        Iterator emailIter = mailServer.getReceivedEmail();
                        boolean found = false;
                        while (emailIter.hasNext()) {
                                SmtpMessage email = (SmtpMessage) emailIter.next();
                                if (TckCommon.isDebug()) {
                                        System.out.println("Subject:" + email.getHeaderValue("Subject"));
                                        System.out.println("Body:" + email.getBody());

                                }
                                if (email.getBody().toLowerCase().contains("mary")) {
                                        found = true;
                                }
                        }
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                logger.fatal("Notification does not contain the correct service");
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        //      tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        //tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        TckCommon.DeleteBusiness(TckBusiness.MARY_BUSINESS_KEY, authInfoMary, publicationMary);
                        mailServer.stop();
                }
        }

        //tmodel tests
        @Test
        public void joePublisherUpdate_HTTP_FIND_TMODEL() {
                logger.info("joePublisherUpdate_HTTP_FIND_TMODEL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                try {
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        String before=TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        logger.info("sanity check, this shouldn't be the rmi file " + TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML);
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, hostname);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting tModel ********** ");
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);

                        //waiting up to 100 seconds for the listener to notice the change.

                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        logger.info("Received Notification");
                                        break;
                                }
                        }
                        
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe));
                                Assert.fail("No Notification was sent");
                        }
                        if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">tModel One</name>")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                }
        }

        @Test
        public void joePublisherUpdate_SMTP_FIND_TMODEL() {
                logger.info("joePublisherUpdate_SMTP_FIND_TMODEL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                try {
                        if (mailServer != null && !mailServer.isStopped()) {
                                mailServer.stop();
                        }
                        mailServer = SimpleSmtpServer.start(smtpPort);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_XML, 0, hostname);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_SMTP_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting tModel ********** ");
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);


                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
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
                        if (TckCommon.isDebug()) {
                                System.out.println("Subject:" + email.getHeaderValue("Subject"));
                                System.out.println("Body:" + email.getBody());
                        }
                        if (!email.getBody().contains("tModel One")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        mailServer.stop();
                }
        }
        //TODO If a subscriber specifies a maximum number of entries to be returned with a subscription and the amount of data to be returned exceeds 
//this limit, or if the node determines based on its policy that there are too many entries to be returned in a single group, 
        //then the node SHOULD provide a chunkToken with results.  
        //TODO  If no more results are pending, the value of the chunkToken MUST be "0".
}
