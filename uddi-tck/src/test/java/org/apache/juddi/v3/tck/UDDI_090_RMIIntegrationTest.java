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

import java.net.InetAddress;
import java.net.URI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.Random;
import org.apache.commons.configuration.ConfigurationException;
import static org.apache.juddi.v3.tck.UDDI_090_SubscriptionListenerIntegrationBase.logger;
import static org.apache.juddi.v3.tck.UDDI_090_SubscriptionListenerIntegrationBase.startManager;
import static org.apache.juddi.v3.tck.UDDI_090_SubscriptionListenerIntegrationBase.stopManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 *
 * @author Alex O'Ree
 */
public class UDDI_090_RMIIntegrationTest extends UDDI_090_SubscriptionListenerIntegrationBase {

        private static UDDISubscriptionListenerImpl rmiSubscriptionListenerService = null;
        private static TckSubscriptionListenerRMI rmiSubscriptionListener = null;
        private static Registry registry;
        private static Integer randomPort = null;
        private static String hostname = null;

        @AfterClass
        public static void stop() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                stopManager();

        }

        @BeforeClass
        public static void startup() throws Exception {

                if (!TckPublisher.isEnabled()) {
                        return;
                }
                startManager();
                int count = 0;
                rmiSubscriptionListenerService = new UDDISubscriptionListenerImpl(0);
                UDDISubscriptionListenerImpl.notifcationMap.clear();
                UDDISubscriptionListenerImpl.notificationCount=0;
                while (true && count < 5) {
                        try {
                                count++;
                                randomPort = 19800 + new Random().nextInt(99);
                                System.out.println("RMI Random port=" + randomPort);
                                //bring up the RMISubscriptionListener
                                URI rmiEndPoint = new URI("rmi://localhost:" + randomPort + "/tck/rmisubscriptionlistener");
                                registry = LocateRegistry.createRegistry(rmiEndPoint.getPort());

                                String path = rmiEndPoint.getPath();
                                hostname = InetAddress.getLocalHost().getHostName();
                                //starting the service

                                //binding to the RMI Registry
                                registry.bind(path, rmiSubscriptionListenerService);

                                //double check that the service is bound in the local Registry
                                Registry registry2 = LocateRegistry.getRegistry(rmiEndPoint.getHost(), rmiEndPoint.getPort());
                                registry2.lookup(rmiEndPoint.getPath());
                                break;
                        } catch (Exception ex) {
                                logger.warn("trouble starting rmi endpoint " + ex.getMessage());
                        }
                }
                Assert.assertNotNull(registry);
                Assert.assertNotNull(hostname);
        }

        @Override
        public boolean verifyDelivery(String findMe) {
                for (int i = 0; i < TckPublisher.getSubscriptionTimeout(); i++) {
                        try {
                                Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                        System.out.println(".");
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

                if (!found) {
                        it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        Thread.dumpStack();
                        while (it.hasNext()) {
                                logger.info("Notification: " + it.next());
                        }
                }
                return found;
        }

        @Override
        public void reset() {
                UDDISubscriptionListenerImpl.notifcationMap.clear();
                UDDISubscriptionListenerImpl.notificationCount = 0;

        }

        @Override
        public String getXMLLocationOfServiceForDelivery() {
                return TckSubscriptionListener.LISTENER_RMI_SERVICE_XML;
        }

        @Override
        public String getTransport() {
                return "RMI";
        }

        @Override
        public int getPort() {
                return randomPort;
        }

        @Override
        public String getHostame() {
                return hostname;
        }

        @Override
        public String getSubscription1XML() {
                return TckSubscriptionListenerRMI.SUBSCRIPTION_XML_RMI;
        }

        @Override
        public String getSubscription2XML() {
                return TckSubscriptionListenerRMI.SUBSCRIPTION_XML2_RMI;
        }

        @Override
        public String getSubscription3XML() {
                return TckSubscriptionListenerRMI.SUBSCRIPTION_XML3_RMI;
        }

        @Override
        public String getSubscriptionKey1() {
                return TckSubscriptionListenerRMI.SUBSCRIPTION_KEY_RMI;
        }

        @Override
        public String getSubscriptionKey2() {
                return TckSubscriptionListenerRMI.SUBSCRIPTION_KEY_RMI;
        }

        @Override
        public String getSubscriptionKey3() {
                return TckSubscriptionListenerRMI.SUBSCRIPTION_KEY_RMI;
        }

        @Override
        public boolean IsEnabled() {
                return TckPublisher.isRMI();
        }

}
