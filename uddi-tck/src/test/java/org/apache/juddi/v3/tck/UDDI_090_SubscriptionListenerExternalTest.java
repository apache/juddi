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
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.mail.util.MimeMessageParser;
//import org.apache.commons.io.IOUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;
import org.apache.commons.net.pop3.POP3SClient;
import static org.apache.juddi.v3.tck.TckBusiness.MARY_BUSINESS_XML;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;

/**
 * Used only when running the TCK against a running UDDI server (could be
 * something other than jUDDI) from the juddi-tck-runner application. The
 * 'external' part is really managing hostnames for callbacks, and for using an
 * external smtp server for smtp based notifications.<br><br>
 * WARNING, when adding changes to this class, you MUST always add the
 * corresponding functions.<br><br>
 * to UDDI_090_SubscriptionListenerIntegrationTest
 *
 * @see UDDI_090_SubscriptionListenerIntegrationTest
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_090_SubscriptionListenerExternalTest {

        private static Log logger = LogFactory.getLog(UDDI_090_SubscriptionListenerExternalTest.class);
        private static UDDISubscriptionPortType subscriptionMary = null;
        private static UDDIInquiryPortType inquiryMary = null;
        private static TckTModel tckTModelMary = null;
        private static TckBusiness tckBusinessMary = null;
        private static TckBusinessService tckBusinessServiceMary = null;
        private static TckSubscriptionListener tckSubscriptionListenerMary = null;
        private static String hostname = null;
        private static UDDISubscriptionPortType subscriptionJoe = null;
        private static UDDIInquiryPortType inquiryJoe = null;
        private static TckTModel tckTModelJoe = null;
        private static TckBusiness tckBusinessJoe = null;
        private static TckBusinessService tckBusinessServiceJoe = null;
        private static TckSubscriptionListener tckSubscriptionListenerJoe = null;
        private static Endpoint endPoint;
        private static String authInfoJoe = null;
        private static String authInfoMary = null;
        private static UDDIClient manager;
        private static String email = null;
        private static Integer httpPort = 80;
        private static UDDIPublicationPortType publicationMary = null;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
             if (!TckPublisher.isEnabled()) return;
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                tckTModelMary.deleteCreatedTModels(authInfoMary);
                manager.stop();
                //shutting down the TCK SubscriptionListener
                endPoint.stop();
                endPoint = null;
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
             if (!TckPublisher.isEnabled()) return;
                try {
                        httpPort = 9600 + new Random().nextInt(99);

                        hostname = TckPublisher.getProperties().getProperty("bindaddress");
                        if (hostname == null) {
                                hostname = InetAddress.getLocalHost().getHostName();
                        }

                        //bring up the TCK SubscriptionListener
                        String httpEndpoint = "http://" + hostname + ":" + httpPort + "/tcksubscriptionlistener";
                        System.out.println("Bringing up SubscriptionListener endpoint at " + httpEndpoint);
                        endPoint = Endpoint.publish(httpEndpoint, new UDDISubscriptionListenerImpl());

                        manager = new UDDIClient();
                        manager.start();

                        logger.debug("Getting auth tokens..");

                        Transport transport = manager.getTransport("uddiv3");
                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());

                        UDDIPublicationPortType publication = transport.getUDDIPublishService();
                        inquiryJoe = transport.getUDDIInquiryService();
                        subscriptionJoe = transport.getUDDISubscriptionService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                        tckTModelJoe = new TckTModel(publication, inquiryJoe);
                        tckBusinessJoe = new TckBusiness(publication, inquiryJoe);
                        tckBusinessServiceJoe = new TckBusinessService(publication, inquiryJoe);
                        tckSubscriptionListenerJoe = new TckSubscriptionListener(subscriptionJoe, publication);

                        transport = manager.getTransport("uddiv3");
                        publication = transport.getUDDIPublishService();
                        publicationMary = publication;
                        inquiryMary = transport.getUDDIInquiryService();
                        subscriptionMary = transport.getUDDISubscriptionService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        }

                        tckTModelMary = new TckTModel(publication, inquiryMary);
                        tckBusinessMary = new TckBusiness(publication, inquiryMary);
                        tckBusinessServiceMary = new TckBusinessService(publication, inquiryMary);
                        tckSubscriptionListenerMary = new TckSubscriptionListener(subscriptionMary, publication);

                        email = TckPublisher.getProperties().getProperty("mail.pop3.to");

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @Test
        public void joePublisherUpdate_HTTP_FIND_SERVICE() {
             Assume.assumeTrue(TckPublisher.isEnabled());
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
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort, hostname);
                        //Saving the HTTP Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription1.xml");
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessServiceJoe.updateJoePublisherService(authInfoJoe, "foo");
                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        //waiting up to 100 seconds for the listener to notice the change.
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                        }
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                Assert.fail("No HttpNotification was sent");
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

        /**
         * gets all current messages from the mail server and returns the number
         * of messages containing the string, messages containing the string are deleted from the mail server
         * String is the body of each message
         * @return number of matching and deleted messages
         * @param contains a string to search for
         */
        private static int fetchMail(String contains) {

                //final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

                /* Set the mail properties */
                Properties properties = TckPublisher.getProperties();
                // Set manual Properties

                int found = 0;
                Session session = Session.getDefaultInstance(properties, null);
                Store store = null;
                try {
                        store = session.getStore("pop3");

                        store.connect(properties.getProperty("mail.pop3.host"), Integer.parseInt(properties.getProperty("mail.pop3.port", "110")), properties.getProperty("mail.pop3.username"), properties.getProperty("mail.pop3.password"));
                        /* Mention the folder name which you want to read. */
                        // inbox = store.getDefaultFolder();
                        // inbox = inbox.getFolder("INBOX");
                        Folder inbox = store.getFolder("INBOX");

                        /* Open the inbox using store. */
                        inbox.open(Folder.READ_WRITE);

                        Message messages[] = inbox.getMessages();

                        for (int i = 0; i < messages.length; i++) {

                                MimeMessageParser p = new MimeMessageParser(new MimeMessage(session, messages[i].getInputStream()));
                                Enumeration allHeaders = p.getMimeMessage().getAllHeaders();
                                while (allHeaders.hasMoreElements()) {
                                        Object j = allHeaders.nextElement();
                                        if (j instanceof javax.mail.Header) {
                                                javax.mail.Header msg = (javax.mail.Header) j;
                                                logger.info("XML as message header is " + msg.getValue());
                                                if (msg.getValue().contains(contains)) {
                                                        //found it
                                                        messages[i].setFlag(Flags.Flag.DELETED, true);
                                                        found++;
                                                }
                                        }
                                }
                                for (int k = 0; k < p.getAttachmentList().size(); k++) {
                                        InputStream is = p.getAttachmentList().get((k)).getInputStream();
                                        QuotedPrintableCodec qp = new QuotedPrintableCodec();
                                        // If "is" is not already buffered, wrap a BufferedInputStream
                                        // around it.
                                        if (!(is instanceof BufferedInputStream)) {
                                                is = new BufferedInputStream(is);
                                        }
                                        int c;
                                        StringBuilder sb = new StringBuilder();
                                        System.out.println("Message : ");
                                        while ((c = is.read()) != -1) {
                                                sb.append(c);
                                                System.out.write(c);
                                        }
                                        is.close();
                                        String decoded = qp.decode(sb.toString());
                                        logger.info("decode message is " + decoded);
                                        if (decoded.contains(contains)) {
                                                //found it
                                                messages[i].setFlag(Flags.Flag.DELETED, true);
                                                found++;
                                        }
                                }

                        }
                        inbox.close(true);

                } catch (Exception ex) {
                        ex.printStackTrace();
                } finally {
                        if (store != null) {
                                try {
                                        store.close();
                                } catch (Exception ex) {
                                }
                        }
                }
                return found;
        }

        
  
        @Test
        public void joePublisher_SMTP_FIND_SERVICE() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(email);
                logger.info("joePublisher_SMTP_FIND_SERVICE");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);

                        String before = TckCommon.DumpAllServices(authInfoJoe, inquiryJoe);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);

                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the SMTP Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);
                        //Saving the SMTP Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");

                        tckBusinessServiceJoe.updateJoePublisherService(authInfoJoe, "foo");

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        //waiting up to 100 seconds for the listener to notice the change.
                        boolean received = false;
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (fetchMail("Service One") > 0) {
                                        logger.info("Received Email Notification");
                                        received = true;
                                        break;
                                }
                        }
                        if (!received) {
                                logger.warn("Test failed, dumping service list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllServices(authInfoJoe, inquiryJoe));
                        }
                        Assert.assertTrue("No email was received", received);

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void joePublisherUpdate_HTTP_FIND_BUSINESS() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("joePublisherUpdate_HTTP_FIND_BUSINESS");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        logger.info("Saving Joe's callback endpoint ********** ");
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort, hostname);
                        //Saving the Subscription
                        logger.info("Saving Joe's subscription********** ");
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription2.xml");

                        logger.info("Clearing the WS inbox********** ");
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        Thread.sleep(1000);
                        logger.info("Saving Mary's tModel ********** ");
                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        logger.info("Saving Mary's Business ********** ");
                        tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);
                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        //waiting up to 10 seconds for the listener to notice the change.
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                }
                        }
                        logger.info("RX " + UDDISubscriptionListenerImpl.notificationCount + " notifications");
                        Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        boolean found = false;

                        while (it.hasNext()) {
                                String test = it.next();
                                if (TckCommon.isDebug()) {
                                        logger.info("Notification: " + test);
                                }
                                if (test.toLowerCase().contains("marybusinessone")) {
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
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                }
        }

        @Test
        public void joePublisherUpdate_SMTP_FIND_BUSINESS() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(email);
                logger.info("joePublisherUpdate_SMTP_FIND_BUSINESS");
                try {
                        TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);

                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION2_SMTP_XML);

                        Thread.sleep(3000);
                        logger.info("Saving Mary's tModel ********** ");
                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        logger.info("Saving Mary's Business ********** ");
                        tckBusinessMary.saveBusiness(authInfoMary, MARY_BUSINESS_XML, "uddi:uddi.marypublisher.com:marybusinessone");

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        boolean received = false;
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                if (fetchMail("marybusinessone") > 0) {
                                        logger.info("Received Email Notification");
                                        received = true;
                                        break;
                                }
                        }
                        if (!received) {
                                logger.warn("Test failed, dumping before and after business list");
                                logger.warn("Before " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("No email was received");
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
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);
                }
        }

        //tmodel tests
        @Test
        public void joePublisherUpdate_HTTP_FIND_TMODEL() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("joePublisherUpdate_HTTP_FIND_TMODEL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                try {
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        String before = TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, hostname);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting tModel ********** ");

                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, true);

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
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
                        Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        boolean found = false;
                        while (it.hasNext()) {
                                String test = it.next();
                                if (test.contains("tModel One")) {
                                        found = true;
                                        break;
                                }
                        }
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe));
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
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeNotNull(email);
                logger.info("joePublisherUpdate_SMTP_FIND_TMODEL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                try {

                        String before = TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_SMTP_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting tModel ********** ");
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, true);

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        boolean received = false;
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (fetchMail("tModel One") > 0) {
                                        logger.info("Received Email Notification");
                                        received = true;
                                        break;
                                }
                        }
                        if (!received) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe));
                                Assert.fail("No email was received");
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
                }
        }
        //TODO If a subscriber specifies a maximum number of entries to be returned with a subscription and the amount of data to be returned exceeds this limit, or if the node determines based on its policy that there are too many entries to be returned in a single group, then the node SHOULD provide a chunkToken with results.  
        //TODO  If no more results are pending, the value of the chunkToken MUST be "0".

        /**
         * getBusiness tests joe want's updates on mary's business
         *
         * @throws Exception
         */
        @Test
        //@Ignore
        public void joePublisherUpdate_HTTP_GET_BUSINESS_DETAIL() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("joePublisherUpdate_HTTP_GET_BUSINESS_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, hostname);
                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey("uddi:uddi.joepublisher.com:bindinglistener");
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetBusinessDetail(new GetBusinessDetail());
                        sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().add(TckBusiness.MARY_BUSINESS_KEY);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's business ********** ");
                        updatePublisherBusiness(authInfoMary, saveMaryPublisherBusiness, publicationMary);

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        //waiting up to 100 seconds for the listener to notice the change.
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                        }

                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("No Notification was sent");
                        }
                        Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        StringBuilder sb = new StringBuilder();
                        boolean found = false;
                        while (it.hasNext()) {
                                String test = it.next();
                                sb.append("Message: " + test + System.getProperty("line.separator"));
                                if (test.contains("Updated Name")) {
                                        found = true;
                                        break;
                                }
                        }
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("Notification does not contain the correct service. Messages received: " + sb.toString());
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
        //@Ignore
        public void joePublisherUpdate_SMTP_GET_BUSINESS_DETAIL() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("joePublisherUpdate_SMTP_GET_BUSINESS_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        String before = TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);

                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        BusinessEntity saveMaryPublisherBusiness = tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);
                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey("uddi:uddi.joepublisher.com:bindinglistenersmtp");
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetBusinessDetail(new GetBusinessDetail());
                        sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().add(TckBusiness.MARY_BUSINESS_KEY);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's business ********** ");
                        updatePublisherBusiness(authInfoMary, saveMaryPublisherBusiness, publicationMary);

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        //waiting up to 100 seconds for the listener to notice the change.
                        boolean received = false;
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (fetchMail("Updated Name") > 0) {
                                        logger.info("Received Email Notification");
                                        received = true;
                                        break;
                                }
                        }

                        if (!received) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                Assert.fail("No Notification was sent");
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
         * getBusiness tests joe want's updates on mary's business
         *
         * @throws Exception
         */
        @Test
        public void joePublisherUpdate_HTTP_GET_TMODEL_DETAIL() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("joePublisherUpdate_HTTP_GET_TMODEL_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        String before = TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        TModel saveMaryPublisherTmodel = tckTModelMary.saveMaryPublisherTmodel(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, hostname);

                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey("uddi:uddi.joepublisher.com:bindinglistener");
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetTModelDetail(new GetTModelDetail());
                        sub.getSubscriptionFilter().getGetTModelDetail().getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's tModel ********** ");
                        updateTModel(authInfoMary, saveMaryPublisherTmodel, publicationMary);

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        //waiting up to 100 seconds for the listener to notice the change.
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        //    logger.info("Received Notification");
//                                        break;
                                }
                        }

                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe));
                                Assert.fail("No Notification was sent");
                        }
                        Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        StringBuilder sb = new StringBuilder();
                        boolean found = false;
                        while (it.hasNext()) {
                                String test = it.next();
                                sb.append("Message: " + test + System.getProperty("line.separator"));
                                if (test.contains("a new description")) {
                                        found = true;
                                        break;
                                }
                        }
                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllBusinesses(authInfoJoe, inquiryJoe));
                                logger.warn("Messages RX " + sb.toString());
                                Assert.fail("Notification does not contain the correct service. Messages received: " + sb.toString());
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

        /**
         * getBusiness tests joe want's updates on mary's business
         *
         * @throws Exception
         */
        @Test
        public void joePublisherUpdate_SMTP_GET_TMODEL_DETAIL() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("joePublisherUpdate_SMTP_GET_TMODEL_DETAIL");
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                Holder<List<Subscription>> holder = null;
                try {
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        String before = TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        TModel saveMaryPublisherTmodel = tckTModelMary.saveMaryPublisherTmodel(authInfoMary);

                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);
                        //Saving the Subscription
                        holder = new Holder<List<Subscription>>();
                        holder.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBindingKey("uddi:uddi.joepublisher.com:bindinglistenersmtp");
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(5000));
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetTModelDetail(new GetTModelDetail());
                        sub.getSubscriptionFilter().getGetTModelDetail().getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);

                        holder.value.add(sub);
                        subscriptionJoe.saveSubscription(authInfoJoe, holder);
                        //tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("updating Mary's tModel ********** ");
                        updateTModel(authInfoMary, saveMaryPublisherTmodel, publicationMary);

                        logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery");
                        //waiting up to 100 seconds for the listener to notice the change.
                        boolean found = false;
                        for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                                Thread.sleep(1000);
                                System.out.print(".");
                                if (fetchMail("a new description") > 0) {
                                        found = true;
                                        break;
                                }
                        }

                        if (!found) {
                                logger.warn("Test failed, dumping business list");
                                logger.warn("BEFORE " + before);
                                logger.warn("After " + TckCommon.DumpAllTModels(authInfoJoe, inquiryJoe));
                                Assert.fail("No Notification was sent");
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
}
