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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.juddi.v3.client.UDDIConstants;
import static org.apache.juddi.v3.tck.TckBusiness.MARY_BUSINESS_XML;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDI_090_SubscriptionListenerIntegrationTest {

    private static Log logger = LogFactory.getLog(UDDI_090_SubscriptionListenerIntegrationTest.class);
    private static TckTModel tckTModel = null;
    private static TckBusiness tckBusiness = null;
    private static TckBusinessService tckBusinessService = null;
    private static TckSubscriptionListener tckSubscriptionListener = null;
    private static Endpoint endPoint;
    private static String authInfoJoe = null;
    private static String authInfoMary = null;
    private static UDDIClient manager;
    private static SimpleSmtpServer mailServer;
    private static Integer smtpPort = 25;
    private static Integer httpPort = 80;
    private static UDDISubscriptionPortType subscription = null;
    private static UDDIInquiryPortType inquiry=null;

    @AfterClass
    public static void stopManager() throws ConfigurationException {
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

            //bring up the TCK SubscriptionListener
            String httpEndpoint = "http://localhost:" + httpPort + "/tcksubscriptionlistener";
            System.out.println("Bringing up SubscriptionListener endpoint at " + httpEndpoint);
            endPoint = Endpoint.publish(httpEndpoint, new UDDISubscriptionListenerImpl());

            manager = new UDDIClient();
            manager.start();

            logger.debug("Getting auth tokens..");


            Transport transport = manager.getTransport();
            UDDISecurityPortType security = transport.getUDDISecurityService();
            authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
            authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
            Assert.assertNotNull(authInfoJoe);
            subscription = transport.getUDDISubscriptionService();

            UDDIPublicationPortType publication = transport.getUDDIPublishService();
             inquiry = transport.getUDDIInquiryService();
            tckTModel = new TckTModel(publication, inquiry);
            tckBusiness = new TckBusiness(publication, inquiry);
            tckBusinessService = new TckBusinessService(publication, inquiry);
            tckSubscriptionListener = new TckSubscriptionListener(subscription, publication);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Assert.fail("Could not obtain authInfo token.");
        }
    }

    private void removeAllExistingSubscriptions(String authinfo) {
        List<Subscription> subscriptions;
        try {
            subscriptions = subscription.getSubscriptions(authinfo);

            DeleteSubscription ds = new DeleteSubscription();
            ds.setAuthInfo(authinfo);
            for (int i = 0; i < subscriptions.size(); i++) {
                ds.getSubscriptionKey().add(subscriptions.get(i).getSubscriptionKey());
            }
            if (!subscriptions.isEmpty()) {
                logger.info("Purging " + subscriptions.size() + " old subscriptions");
                subscription.deleteSubscription(ds);
            }
        } catch (Exception ex) {
            logger.warn("error clearing subscriptions", ex);
        }
    }

    @Test
    public void joePublisherUpdateService_HTTP_FIND_SERVICE() {
        logger.info("joePublisherUpdateService_HTTP_FIND_SERVICE");
        try {
            removeAllExistingSubscriptions(authInfoJoe);
            UDDISubscriptionListenerImpl.notifcationMap.clear();
            UDDISubscriptionListenerImpl.notificationCount = 0;
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            //Saving the binding template that will be called by the server for a subscription event
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the HTTP Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort);
            //Saving the HTTP Subscription
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription1.xml");
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
            tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
            tckBusinessService.deleteJoePublisherService(authInfoJoe);
            tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
            tckTModel.deleteJoePublisherTmodel(authInfoJoe);
        }
    }

    @Test
    public void joePublisherUpdateService_SMTP_FIND_SERVICE() {
        logger.info("joePublisherUpdateService_SMTP_FIND_SERVICE");
        try {
            removeAllExistingSubscriptions(authInfoJoe);
            //    if (mailServer != null && !mailServer.isStopped()) {
            //        mailServer.stop();
            //    }
            mailServer = SimpleSmtpServer.start(smtpPort);

            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            //Saving the binding template that will be called by the server for a subscription event
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the SMTP Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_XML, 0);
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
            if (!email.getBody().replace("=", "").contains("Service One")) {
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
            mailServer.stop();
        }
    }

    @Test
    public void joePublisherUpdateBusiness_HTTP_FIND_BUSINESS() {
        logger.info("joePublisherUpdateBusiness_HTTP_FIND_BUSINESS");
        try {
            removeAllExistingSubscriptions(authInfoJoe);
            UDDISubscriptionListenerImpl.notifcationMap.clear();
            UDDISubscriptionListenerImpl.notificationCount = 0;
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort);
            //Saving the Subscription
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription2.xml");
            //Changing the service we subscribed to "JoePublisherService"
            Thread.sleep(1000);
            logger.info("Saving Mary's Business ********** ");
            tckBusiness.saveMaryPublisherBusiness(authInfoMary);

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
            if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("uddi:uddi.marypublisher.com:marybusinessone")) {
                DumpAllBusinesses();
                Assert.fail("Notification does not contain the correct service");
            }

        } catch (Exception e) {
            logger.error("No exceptions please.");
            e.printStackTrace();

            Assert.fail();
        } finally {
            tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
            tckBusinessService.deleteJoePublisherService(authInfoJoe);
            tckTModel.deleteJoePublisherTmodel(authInfoJoe);
            tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
        }
    }

    private static void DumpAllBusinesses() {

        FindService fs = new FindService();
        fs.setFindQualifiers(new FindQualifiers());
        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        fs.getName().add(new Name("%", null));
        try {
            ServiceList findService = inquiry.findService(fs);
            if (findService.getServiceInfos() == null) {
                logger.warn("NO SERVICES RETURNED!");
            } else {
                for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                    logger.warn(findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getValue() + " "
                            + findService.getServiceInfos().getServiceInfo().get(i).getServiceKey() + " "
                            + findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void joePublisherUpdateBusiness_SMTP_FIND_BUSINESS() {
        logger.info("joePublisherUpdateBusiness_SMTP_FIND_BUSINESS");
        try {
            removeAllExistingSubscriptions(authInfoJoe);
            //  if (mailServer != null && !mailServer.isStopped()) {
            //      mailServer.stop();
            //  }
            mailServer = SimpleSmtpServer.start(smtpPort);
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_XML, 0);
            //tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort);
            //Saving the Subscription
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION2_SMTP_XML);
            //tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_XML);

            Thread.sleep(3000);
            logger.info("Saving Mary's Business ********** ");
            tckBusiness.saveBusiness(authInfoMary, MARY_BUSINESS_XML, "uddi:uddi.marypublisher.com:marybusinessone");

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
            if (!email.getBody().replaceAll("=", "").contains("uddi:uddi.marypublisher.com:marybusinessone")) {
                DumpAllBusinesses();
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
            //      tckTModel.deleteJoePublisherTmodel(authInfoJoe);
            tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
            mailServer.stop();
        }
    }

    //tmodel tests
    //@Test
    public void joePublisherUpdateBusiness_HTTP_FIND_TMODEL() {
        logger.info("joePublisherUpdateBusiness_HTTP_FIND_TMODEL");
        removeAllExistingSubscriptions(authInfoJoe);
        try {
            UDDISubscriptionListenerImpl.notifcationMap.clear();
            UDDISubscriptionListenerImpl.notificationCount = 0;
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckTModel.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort);
            //Saving the Subscription
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
            //Changing the service we subscribed to "JoePublisherService"
            Thread.sleep(1000);
            logger.info("Deleting tModel ********** ");
            tckTModel.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);

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
            if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">tModel One</name>")) {
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
            tckTModel.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
        }
    }

    //@Test
    public void joePublisherUpdateBusiness_SMTP_FIND_TMODEL() {
        logger.info("joePublisherUpdateBusiness_SMTP_FIND_TMODEL");
        removeAllExistingSubscriptions(authInfoJoe);
        try {
            if (mailServer != null && !mailServer.isStopped()) {
                mailServer.stop();
            }
            mailServer = SimpleSmtpServer.start(smtpPort);

            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckTModel.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_XML, 0);
            //Saving the Subscription
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_SMTP_XML);
            //Changing the service we subscribed to "JoePublisherService"
            Thread.sleep(1000);
            logger.info("Deleting tModel ********** ");
            tckTModel.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);


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
            if (!email.getBody().contains("tModel One")) {
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
            tckTModel.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
            tckTModel.deleteJoePublisherTmodel(authInfoJoe);
            mailServer.stop();
        }
    }
}
