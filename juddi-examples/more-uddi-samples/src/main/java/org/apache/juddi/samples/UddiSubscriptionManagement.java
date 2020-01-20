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
package org.apache.juddi.samples;

import java.util.List;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 *
 * @author Alex O'Ree
 */
public class UddiSubscriptionManagement {
        public static void main(String args[]) throws Exception {
                UddiSubscriptionManagement sp = new UddiSubscriptionManagement();
                sp.printSubscriptions(null);
        }

        private UDDISubscriptionPortType uddiSubscriptionService = null;

        private UDDIClerk clerk = null;
        private UDDIClient client = null;
        private PrintUDDI<Subscription> printer = new PrintUDDI<Subscription>();

        public UddiSubscriptionManagement() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        client = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        clerk = client.getClerk("default");
                        Transport transport = client.getTransport();
                        // Now you create a reference to the UDDI API
                        uddiSubscriptionService = transport.getUDDISubscriptionService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public UddiSubscriptionManagement(Transport transport) {
                try {

                        // Now you create a reference to the UDDI API
                        uddiSubscriptionService = transport.getUDDISubscriptionService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }


        public void printSubscriptions(String authtoken) throws Exception {

                if (authtoken == null) {
                        authtoken = clerk.getAuthToken();
                }
                List<Subscription> subscriptions = uddiSubscriptionService.getSubscriptions(authtoken);
                System.out.println(subscriptions.size() + " subscriptions found");
                for (int i = 0; i < subscriptions.size(); i++) {
                        System.out.println("_____________________________________");

                        System.out.println(printer.print(subscriptions.get(i)));
                }
        }

        public void deleteSubscription(String authtoken, String key) throws Exception {

                if (authtoken == null) {
                        authtoken = clerk.getAuthToken();
                }
                if (key == null) {
                        System.out.println("No key specified!");
                        return;
                }
                DeleteSubscription ds = new DeleteSubscription();
                ds.setAuthInfo(authtoken);
                ds.getSubscriptionKey().add(key);
                uddiSubscriptionService.deleteSubscription(ds);
                System.out.println("Deleted!");

        }

        public void deleteAllSubscriptions(String authtoken) throws Exception {

                if (authtoken == null) {
                        authtoken = clerk.getAuthToken();
                }
                List<Subscription> subscriptions = uddiSubscriptionService.getSubscriptions(authtoken);
                System.out.println(subscriptions.size() + " subscriptions found");
                for (int i = 0; i < subscriptions.size(); i++) {
                        System.out.println("_____________________________________");
                        System.out.println(subscriptions.get(i).getSubscriptionKey());
                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authtoken);
                        ds.getSubscriptionKey().add(subscriptions.get(i).getSubscriptionKey());
                        uddiSubscriptionService.deleteSubscription(ds);
                        System.out.println("Deleted!");
                }

        }


}
