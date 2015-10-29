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
package org.apache.juddi.v3.client;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.JAXWSv2TranslationTransport;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BusinessService;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIClientTest {

        @Test
        public void testReadingTheConfig() {
                try {
                        UDDIClient client = new UDDIClient();
                        client.start();
                        client.getClientConfig().getUDDINode("default");
                        assertEquals(3, client.getClientConfig().getUDDIClerks().size());
                        Thread.sleep(500);
                        client.stop();
                } catch (Exception e) {
                        //we should not have any issues reading the config
                        e.printStackTrace();
                        Assert.fail();
                }
        }

        @Test
        public void testMultipleClientConfigFiles() {
                try {
                        UDDIClient client = new UDDIClient("META-INF/uddi.xml");
                        client.start();
                        assertEquals("test-client", client.getName());

                        UDDIClient client2 = new UDDIClient("META-INF/uddi2.xml");
                        client2.start();
                        assertEquals("second-client", client2.getName());
                        Thread.sleep(500);
                        client.stop();
                        client2.stop();

                } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("No exceptions are expected");
                }

        }

        @Test
        public void testDefaultConfigFile() {
                try {
                        UDDIClient client = new UDDIClient(null);
                        //We're expecting the client defined in the META-INF/uddi.xml file.
                        client.start();
                        assertEquals("test-client", client.getName());
                        Thread.sleep(500);
                        client.stop();

                } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("No exceptions are expected");
                }

        }

        @Test
        public void testDefaultManager() {
                try {
                        //This is a special case where the client in the META-INF/uddi.xml file is 
                        //instantiated and started simply by getting it.
                        //This functionality was add for backwards compatibility. 
                        UDDIClient client = new UDDIClient();
                        client.start();
                        assertEquals("test-client", client.getName());
                        assertEquals("default", client.getClientConfig().getHomeNode().getName());
                        Thread.sleep(500);
                        client.stop();

                } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("No exceptions are expected");
                }

        }

        @Test
        public void testReadingAnnotations() {
                try {
                        UDDIClient client = new UDDIClient();
                        Map<String, UDDIClerk> clerks = client.getClientConfig().getUDDIClerks();
                        AnnotationProcessor ap = new AnnotationProcessor();
                        if (clerks.containsKey("default")) {
                                UDDIClerk clerk = clerks.get("default");
                                BusinessService service = ap.readServiceAnnotations(
                                        HelloWorldMockup.class.getName(), clerk.getUDDINode().getProperties());
                                assertEquals("uddi:juddi.apache.org:services-helloworld", service.getServiceKey());
                        } else {
                                Assert.fail("Could not find expected clerk='default'");
                        }
                } catch (Exception e) {
                        //we should not have any issues reading the config
                        e.printStackTrace();
                        Assert.fail("No exceptions are expected");
                }

        }

        @Test
        public void testReleaseVersion() {
                String v = Release.getjUDDIClientVersion();
                //Assert.assertNotSame(v, Release.UNKNOWN);
                //Apparently maven attaches the version info to the manifest after the tests are ran
        }

        @Test
        public void testDefaultManagerGetTransport() {
                try {
                        //This is a special case where the client in the META-INF/uddi.xml file is 
                        //instantiated and started simply by getting it.
                        //This functionality was add for backwards compatibility. 
                        UDDIClient client = new UDDIClient();
                        client.start();
                        assertEquals("test-client", client.getName());
                        assertEquals("default", client.getClientConfig().getHomeNode().getName());
                        Transport transport = client.getTransport("default");
                        Assert.assertNotNull(transport);
                        Assert.assertNotNull(transport.getJUDDIApiService());
                        Assert.assertNotNull(transport.getUDDICustodyTransferService());
                        Assert.assertNotNull(transport.getUDDIInquiryService());
                        Assert.assertNotNull(transport.getUDDIPublishService());
                        Assert.assertNotNull(transport.getUDDISecurityService());
                        Assert.assertNotNull(transport.getUDDISubscriptionListenerService());
                        Assert.assertNotNull(transport.getUDDISubscriptionService());

                        Thread.sleep(500);
                        client.stop();

                } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("No exceptions are expected");
                }

        }

        /**
         * gets UDDI v2 clients
         *
         * @throws Exception
         */
        @Test
        public void getVersion2Clients() throws Exception {
                UDDIClient client = new UDDIClient("META-INF/uddiv2.xml");
                client.start();
                Transport transport = client.getTransport("default");
                Assert.assertNotNull(transport);
                Assert.assertEquals(transport.getClass().getCanonicalName(), JAXWSv2TranslationTransport.class.getCanonicalName());
                Assert.assertNotNull(transport.getUDDIInquiryService());
                Assert.assertNotNull(transport.getUDDIPublishService());
                Thread.sleep(500);
                client.stop();

        }

}
