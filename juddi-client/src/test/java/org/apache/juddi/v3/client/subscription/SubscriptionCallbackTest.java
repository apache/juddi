/*
 * Copyright 2013 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.subscription;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.subr_v3.NotifySubscriptionListener;

/**
 * a basic test client for the subscription callback
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class SubscriptionCallbackTest implements ISubscriptionCallback {

    static final Logger log = Logger.getLogger(SubscriptionCallbackTest.class.getCanonicalName());

    @Test
    public void Test1_NoAutoRegister() throws Exception {
        log.info("Test1_NoAutoRegister");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        c.start();
        UDDIClerk clerk = c.getClerk("default");
        // TModel createKeyGenator = UDDIClerk.createKeyGenator("uddi:org.apache.juddi:test:keygenerator", "Test domain", "en");
        //clerk.register(createKeyGenator);
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        Assert.assertNotNull(start);
        Assert.assertNotNull(start);
        //Assert.assertNotNull(start.getBindingKey());
        SubscriptionCallbackListener.stop(c, "default", start.getBindingKey());
        c.stop();

    }
    boolean Test2_NoAutoRegisterAndShortCircuitReceive_ = false;

    @Test
    public void Test2_NoAutoRegisterAndShortCircuitReceive() throws Exception {
        log.info("Test2_NoAutoRegisterAndShortCircuitReceive");
        Test2_NoAutoRegisterAndShortCircuitReceive_ = false;
        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        c.start();
        UDDIClerk clerk = c.getClerk("default");
        // TModel createKeyGenator = UDDIClerk.createKeyGenator("uddi:org.apache.juddi:test:keygenerator", "Test domain", "en");
        //clerk.register(createKeyGenator);
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        Assert.assertNotNull(start);
        //Assert.assertNotNull(start.getBindingKey());
        SubscriptionCallbackListener.registerCallback(this);
        Assert.assertNotNull(SubscriptionCallbackListener.getInstance());
        SubscriptionCallbackListener.getInstance().notifySubscriptionListener(new NotifySubscriptionListener());

        SubscriptionCallbackListener.stop(c, "default", start.getBindingKey());
        c.stop();
        Assert.assertTrue(Test2_NoAutoRegisterAndShortCircuitReceive_);
    }

    @Test(expected = ServiceAlreadyStartedException.class)
    public void Test3_StartWhenAlreadyStarted() throws Exception {
        log.info("Test3_StartWhenAlreadyStarted");
        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        c.start();
        UDDIClerk clerk = c.getClerk("default");
        // TModel createKeyGenator = UDDIClerk.createKeyGenator("uddi:org.apache.juddi:test:keygenerator", "Test domain", "en");
        //clerk.register(createKeyGenator);
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        try {
            BindingTemplate start1 = SubscriptionCallbackListener.start(c, "default");
        } catch (ServiceAlreadyStartedException x) {
            SubscriptionCallbackListener.stop(c, "default", null);
            c.stop();
            throw x;
        }


    }
    private boolean Test4_NotifyEndpointStopped_ = false;

    @Test
    public void Test4_NotifyEndpointStopped() throws Exception {
        log.info("Test4_NotifyEndpointStopped");
        //reset
        Test4_NotifyEndpointStopped_ = false;
        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        c.start();
        UDDIClerk clerk = c.getClerk("default");

        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        SubscriptionCallbackListener.registerCallback(this);

        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
        Assert.assertTrue(Test4_NotifyEndpointStopped_);


    }

    @Test
    public void Test5_RegisterNullCallback() throws ConfigurationException {
        log.info("Test5_RegisterNullCallback");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        SubscriptionCallbackListener.registerCallback(null);

        SubscriptionCallbackListener.unRegisterCallback(null);

        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
    }

    @Test
    public void Test6_UnRegisterUnRegisteredCallback() throws ConfigurationException, ServiceAlreadyStartedException {
        log.info("Test6_UnRegisterUnRegisteredCallback");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");

        SubscriptionCallbackListener.unregisterAllCallbacks();

        SubscriptionCallbackListener.unRegisterCallback(new ISubscriptionCallback() {
            @Override
            public void handleCallback(SubscriptionResultsList body) {
            }

            @Override
            public void notifyEndpointStopped() {
            }
        });

        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
    }

    @Test
    public void Test7_NullCallbackAddress() throws Exception {
        log.info("Test7_NullCallbackAddress");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback2.xml");
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        Assert.assertNotNull(start);
        Assert.assertNotNull(SubscriptionCallbackListener.getCallbackURL());
        Assert.assertNotNull(start.getAccessPoint());
        Assert.assertNotNull(start.getAccessPoint().getValue());
        log.log(Level.INFO, "AP url: {0} EP url: {1}", new Object[]{start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL()});
        Assert.assertEquals(start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL());
        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
    }

    @Test
    public void Test8_InvalidCallbackAddress() throws Exception {
        log.info("Test8_InvalidCallbackAddress");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback3.xml");
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        Assert.assertNotNull(start);
        Assert.assertNotNull(SubscriptionCallbackListener.getCallbackURL());
        Assert.assertNotNull(start.getAccessPoint());
        Assert.assertNotNull(start.getAccessPoint().getValue());
        log.log(Level.INFO, "AP url: {0} EP url: {1}", new Object[]{start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL()});
        Assert.assertEquals(start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL());
        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
    }

    @Test
    public void Test9_FaultyImplementator1() throws Exception {
        log.info("Test9_FaultyImplementator1");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        Assert.assertNotNull(start);
        Assert.assertNotNull(SubscriptionCallbackListener.getCallbackURL());
        Assert.assertNotNull(start.getAccessPoint());
        Assert.assertNotNull(start.getAccessPoint().getValue());
        log.log(Level.INFO, "AP url: {0} EP url: {1}", new Object[]{start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL()});
        Assert.assertEquals(start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL());

        SubscriptionCallbackListener.registerCallback(new ISubscriptionCallback() {
            @Override
            public void handleCallback(SubscriptionResultsList body) {
                log.info("bogus callback received");
            }

            @Override
            public void notifyEndpointStopped() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        SubscriptionCallbackListener.getInstance().notifySubscriptionListener(new NotifySubscriptionListener());
        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
    }

    @Test
    public void Test10_FaultyImplementator1() throws Exception {
        log.info("Test10_FaultyImplementator1");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        Assert.assertNotNull(start);
        Assert.assertNotNull(SubscriptionCallbackListener.getCallbackURL());
        Assert.assertNotNull(start.getAccessPoint());
        Assert.assertNotNull(start.getAccessPoint().getValue());
        log.log(Level.INFO, "AP url: {0} EP url: {1}", new Object[]{start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL()});
        Assert.assertEquals(start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL());

        SubscriptionCallbackListener.registerCallback(new ISubscriptionCallback() {
            @Override
            public void handleCallback(SubscriptionResultsList body) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void notifyEndpointStopped() {
                log.info("bogus callback received");

            }
        });
        SubscriptionCallbackListener.getInstance().notifySubscriptionListener(new NotifySubscriptionListener());
        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
    }

    @Test
    public void Test11_DoubleStop() throws Exception {
        log.info("Test11_DoubleStop");

        UDDIClient c = new UDDIClient("META-INF/uddi-subcallback1.xml");
        BindingTemplate start = SubscriptionCallbackListener.start(c, "default");
        Assert.assertNotNull(start);
        Assert.assertNotNull(SubscriptionCallbackListener.getCallbackURL());
        Assert.assertNotNull(start.getAccessPoint());
        Assert.assertNotNull(start.getAccessPoint().getValue());
        log.log(Level.INFO, "AP url: {0} EP url: {1}", new Object[]{start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL()});
        Assert.assertEquals(start.getAccessPoint().getValue(), SubscriptionCallbackListener.getCallbackURL());


        SubscriptionCallbackListener.stop(c, "default", null);
        SubscriptionCallbackListener.stop(c, "default", null);
        c.stop();
        c.stop();
    }

    @Override
    public void handleCallback(SubscriptionResultsList body) {
        log.info("HandleCallback received");
        Test2_NoAutoRegisterAndShortCircuitReceive_ = true;
    }

    @Override
    public void notifyEndpointStopped() {
        log.info("NotifyEndpointStopped received");
        Test4_NotifyEndpointStopped_ = true;
    }
}
