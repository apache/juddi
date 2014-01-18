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
import javax.xml.datatype.DatatypeFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.subscription.ISubscriptionCallback;
import org.apache.juddi.v3.client.subscription.SubscriptionCallbackListener;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.TModel;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class JUDDI_101_ClientSubscriptionCallbackAPIIntegrationTest implements ISubscriptionCallback {

        private static final Log logger = LogFactory.getLog(JUDDI_101_ClientSubscriptionCallbackAPIIntegrationTest.class);
        private static UDDIClient manager;
        private static UDDIClerk clerk;
        private static UDDISecurityPortType security = null;
        private static JUDDIApiPortType publisher = null;

        private static String authInfo = null;

        @AfterClass
        public static void stopManager() throws ConfigurationException {

        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {

                manager = new UDDIClient();
                manager.start();
                clerk=manager.getClerk("default");
                clerk.setPassword(TckPublisher.getJoePassword());
                clerk.setPublisher(TckPublisher.getJoePublisherId());
                clerk.setIsPasswordEncrypted(false);
                try {
                        //logger.debug("Getting auth tokens..");
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail(e.getMessage());
                }
        }

        @Test
        public void SubscriptionCallbackTest1() throws Exception {
                //first some setup
                reset();
                
                
                
                TModel createKeyGenator = UDDIClerk.createKeyGenator("somebusiness", "A test key domain SubscriptionCallbackTest1", "SubscriptionCallbackTest1");
                Assert.assertNotNull(createKeyGenator);
                clerk.register(createKeyGenator);
                logger.info("Registered tModel keygen: " + createKeyGenator.getTModelKey());

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
                logger.info("Registered business keygen: " + register.getBusinessKey());

                //start up our listener
                BindingTemplate start = SubscriptionCallbackListener.start(manager, "default");
                Assert.assertNotNull(start);

                if (TckCommon.isDebug()) {
                        PrintUDDI<BindingTemplate> p = new PrintUDDI<BindingTemplate>();
                        logger.info(p.print(start));
                }

                //register for callbacks
                SubscriptionCallbackListener.registerCallback(this);

                Subscription sub = new Subscription();

                sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(1000));
                sub.setBindingKey(start.getBindingKey());
                sub.setSubscriptionFilter(new SubscriptionFilter());
                sub.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                sub.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                sub.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                sub.getSubscriptionFilter().getFindBusiness().getName().add(new Name(UDDIConstants.WILDCARD, null));

                Subscription subscription = clerk.register(sub, clerk.getUDDINode().getApiNode());
                if (TckCommon.isDebug()) {
                        PrintUDDI<Subscription> p2 = new PrintUDDI<Subscription>();
                        logger.info(p2.print(subscription));
                }
                Assert.assertNotNull(subscription);
                Assert.assertNotNull(subscription.getBindingKey());
                Assert.assertNotNull(subscription.getSubscriptionKey());
                logger.info("Registered subscription key: " + (subscription.getSubscriptionKey()) + " bindingkey: " + subscription.getBindingKey());

                //fetch the business just to verify binding registration
                BusinessEntity businessDetail = clerk.getBusinessDetail("uddi:somebusiness:somebusinesskey");
                Assert.assertNotNull(businessDetail);
                Assert.assertNotNull(businessDetail.getBusinessKey());
                Assert.assertNotNull(businessDetail.getBusinessServices());
                Assert.assertNotNull(businessDetail.getBusinessServices().getBusinessService().get(0));
                Assert.assertNotNull(businessDetail.getBusinessServices().getBusinessService().get(0).getBindingTemplates());
                Assert.assertNotNull(businessDetail.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0));
                if (TckCommon.isDebug()) {
                        PrintUDDI<BusinessEntity> p2 = new PrintUDDI<BusinessEntity>();
                        logger.info(p2.print(businessDetail));
                }

                //trigger the callback
                BusinessEntity trigger = new BusinessEntity();
                trigger.getName().add(new Name("somebusiness trigger SubscriptionCallbackTest1", null));
                BusinessEntity triggered = clerk.register(trigger);
                //wait up to 30 seconds or until we get something
                long wait = TckPublisher.getSubscriptionTimeout();
                while (wait > 0 && notifications == 0) {
                        Thread.sleep(1000);
                        System.out.print(".");
                        wait--;
                }

                logger.info("Callback check." + notifications);
                //Thread.sleep(2000);
                SubscriptionCallbackListener.stop(manager, "default", start.getBindingKey());
                clerk.unRegisterSubscription(subscription.getSubscriptionKey());
                clerk.unRegisterTModel(createKeyGenator.getTModelKey());

                clerk.unRegisterBusiness(triggered.getBusinessKey());
                clerk.unRegisterBusiness(businessDetail.getBusinessKey());
                //verify
                logger.info("Callback check." + notifications);
                Assert.assertTrue("No notifications received! " + notifications, notifications > 0);

        }

        private static void reset() {
                notifications = 0;
                notificationsMessages.clear();
                logger.info("Callback check." + notifications);
        }
        private static int notifications = 0;
        private static List<SubscriptionResultsList> notificationsMessages = new ArrayList<SubscriptionResultsList>();

        @Override
        public void HandleCallback(SubscriptionResultsList body) {
                if (TckCommon.isDebug()) 
                {
                        PrintUDDI<SubscriptionResultsList> p2 = new PrintUDDI<SubscriptionResultsList>();
                        logger.info(p2.print(body));
                }

                notifications++;
                notificationsMessages.add(body);

                logger.info("Callback received." + notifications);
        }

        @Override
        public void NotifyEndpointStopped() {
                logger.info("Notify endpoints stopped");
        }

}
