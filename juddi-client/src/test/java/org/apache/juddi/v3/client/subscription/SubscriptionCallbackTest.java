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

import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.TransportException;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.TModel;
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
        Assert.assertTrue(Test4_NotifyEndpointStopped_);


    }

    @Override
    public void HandleCallback(SubscriptionResultsList body) {
        log.info("HandleCallback received");
        Test2_NoAutoRegisterAndShortCircuitReceive_ = true;
    }

    @Override
    public void NotifyEndpointStopped() {
        log.info("NotifyEndpointStopped received");
        Test4_NotifyEndpointStopped_ = true;
    }
}
