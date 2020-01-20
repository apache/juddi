/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.apache.juddi.samples;

import javax.xml.datatype.DatatypeFactory;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.subscription.ISubscriptionCallback;
import org.apache.juddi.v3.client.subscription.SubscriptionCallbackListener;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.sub_v3.SubscriptionResultsList;

/**
 * Thie class shows you how to create a business and a subscription using UDDI
 * Subscription asynchronous callbacks for Assertion Status Reports
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiSubscribeAssertionStatus implements ISubscriptionCallback {

        boolean callbackRecieved = false;
        private UDDIClerk clerk = null;
        private UDDIClient client = null;

        public UddiSubscribeAssertionStatus() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        client = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        clerk = client.getClerk("default");

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }


        public static void main(String args[]) throws Exception {
                UddiSubscribeAssertionStatus sp = new UddiSubscribeAssertionStatus();
                sp.fire("default");
        }

        public void fire(String nodename) throws Exception {

                TModel createKeyGenator = UDDIClerk.createKeyGenator("somebusiness", "A test key domain SubscriptionCallbackTest1", "SubscriptionCallbackTest1");

                clerk.register(createKeyGenator);
                System.out.println("Registered tModel keygen: " + createKeyGenator.getTModelKey());

                //setup the business to attach to
                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey("uddi:somebusiness:somebusinesskey");
                be.getName().add(new Name("somebusiness SubscriptionCallbackTest1", null));
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.setBusinessKey("uddi:somebusiness:somebusinesskey");
                bs.setServiceKey("uddi:somebusiness:someservicekey");
                bs.getName().add(new Name("service SubscriptionCallbackTest1", null));
                be.getBusinessServices().getBusinessService().add(bs);
                BusinessEntity register = clerk.register(be);
                System.out.println("Registered business keygen: " + register.getBusinessKey());

                //start up our listener
                BindingTemplate start = SubscriptionCallbackListener.start(client, nodename);

                //register for callbacks
                SubscriptionCallbackListener.registerCallback(this);

                Subscription sub = new Subscription();
                sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(1000));
                sub.setBindingKey(start.getBindingKey());
                sub.setSubscriptionFilter(new SubscriptionFilter());
                sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                //it's optional

                //sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.STATUS_COMPLETE);
                Subscription subscriptionBiz = clerk.register(sub, clerk.getUDDINode().getApiNode());

                System.out.println("Registered GetAssertionStatus subscription key: " + (subscriptionBiz.getSubscriptionKey()) + " bindingkey: " + subscriptionBiz.getBindingKey());

                System.out.println("Waiting for callbacks. Now would be a good time to launch either another program or juddi-gui to make some changes. Press any key to stop!");

                System.in.read();

                SubscriptionCallbackListener.stop(client, nodename, start.getBindingKey());
                clerk.unRegisterSubscription(subscriptionBiz.getSubscriptionKey());

                clerk.unRegisterTModel(createKeyGenator.getTModelKey());

                clerk.unRegisterBusiness("uddi:somebusiness:somebusinesskey");

        }

        private PrintUDDI<SubscriptionResultsList> printer = new PrintUDDI<SubscriptionResultsList>();

        @Override
        public void handleCallback(SubscriptionResultsList body) {
                System.out.println("Callback received!");
                System.out.println(printer.print(body));
        }

        @Override
        public void notifyEndpointStopped() {
                System.out.println("The endpoint was stopped!");
        }

}
