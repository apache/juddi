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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.xml.ws.Endpoint;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;

/**
 *
 * @author Alex O'Ree
 */
public class UDDI_090_Smtp_ExternalTest extends UDDI_090_SubscriptionListenerIntegrationBase {

        public UDDI_090_Smtp_ExternalTest(){
                super();
        }
        @AfterClass
        public static void stop() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) return;
                stopManager();

        }

        @BeforeClass
        public static void start() throws Exception {
                if (!TckPublisher.isEnabled()) return;
                startManager();
                email = TckPublisher.getProperties().getProperty("mail.pop3.to");
        }

        static String  email = TckPublisher.getProperties().getProperty("mail.pop3.to");

        @Override
        public boolean verifyDelivery(String findMe) {
                logger.info("Waiting " + TckPublisher.getSubscriptionTimeout() + " seconds for delivery, searching for " + findMe);
                boolean received = false;
                for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                        try {
                                Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                        System.out.print(".");
                        if (fetchMail(findMe) > 0) {
                                logger.info("Received Email Notification");
                                received = true;
                                break;
                        }
                }
                return received;
        }

        @Override
        public void reset() {

        }

        @Override
        public String getXMLLocationOfServiceForDelivery() {
                return TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML;
        }

        @Override
        public String getTransport() {
                return "SMTP";
        }

        @Override
        public int getPort() {
                return 0;
        }

        @Override
        public String getHostame() {
                return TckPublisher.getProperties().getProperty("mail.pop3.to");
        }

        /**
         * gets all current messages from the mail server and returns the number
         * of messages containing the string, messages containing the string are
         * deleted from the mail server String is the body of each message
         *
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

        @Override
        public String getSubscription1XML() {
                 return TckSubscriptionListener.SUBSCRIPTION_SMTP_XML;
        }

        @Override
        public String getSubscription2XML() {
                 return TckSubscriptionListener.SUBSCRIPTION2_SMTP_XML;
        }

        @Override
        public String getSubscription3XML() {
                return  TckSubscriptionListener.SUBSCRIPTION3_SMTP_XML;
        }

        @Override
        public String getSubscriptionKey1() {
                return TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY;
        }

        @Override
        public String getSubscriptionKey2() {
                return TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY;
        }

        @Override
        public String getSubscriptionKey3() {
                return TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY;
        }

}
