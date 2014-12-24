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
package org.apache.juddi.api.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.Holder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.tck.TckBindingTemplate;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.Name;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;

/**
 * Load tests for subscriptions
 * @author Alex O'Ree
 */
public class API_099_LoadTest {

        private static Log logger = LogFactory.getLog(API_080_SubscriptionTest.class);

        private static API_010_PublisherTest api010 = new API_010_PublisherTest();
        private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBindingTemplate tckBindingTemplate = new TckBindingTemplate(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        UDDISubscriptionImpl sub = new UDDISubscriptionImpl();

        private static String authInfoJoe = null;
        private static String authInfoSam = null;

        @BeforeClass
        public static void setup() throws ConfigurationException {
                Registry.start();
                logger.info("API_080_SubscriptionTest");
                logger.debug("Getting auth token..");
                try {
                        api010.saveJoePublisher();
                        authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                } catch (RemoteException e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }

        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                tckTModel.deleteCreatedTModels(authInfoJoe);
                Registry.stop();
        }

        @Test
        public void joePublisher() throws Exception {
                //Assume.assumeTrue(TckPublisher.isLoadTest());
                List<String> keys = new ArrayList<String>();
                for (int i = 0; i < 1000; i++) {
                        Holder<List<Subscription>> items = new Holder<List<Subscription>>();
                        items.value = new ArrayList<Subscription>();
                        Subscription s = new Subscription();
                        s.setSubscriptionFilter(new SubscriptionFilter());
                        s.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                        s.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                        s.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        s.getSubscriptionFilter().getFindBusiness().getName().add(new Name(UDDIConstants.WILDCARD, null));
                        items.value.add(s);
                        sub.saveSubscription(authInfoJoe, items);
                        keys.add(items.value.get(0).getSubscriptionKey());
                }
                List<Subscription> subscriptions = sub.getSubscriptions(authInfoJoe);
                DeleteSubscription ds = new DeleteSubscription();
                ds.setAuthInfo(authInfoJoe);
                ds.getSubscriptionKey().addAll(keys);
                sub.deleteSubscription(ds);
                Assert.assertEquals(subscriptions.size(), keys.size());
        }

}
