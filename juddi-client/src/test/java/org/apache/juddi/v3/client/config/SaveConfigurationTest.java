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
package org.apache.juddi.v3.client.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.juddi.api_v3.Node;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for JUDDI-890 when saving a config with node/properties defined,
 * the output is concat'd
 *
 * @author Alex O'Ree
 */
public class SaveConfigurationTest {

        @BeforeClass
        public static void setUpClass() {
        }

        @AfterClass
        public static void tearDownClass() {
        }

        @Before
        public void setUp() {
        }

        @After
        public void tearDown() {
        }

        static Node getCloudInstance() {
                Node n = new Node();
                n.setName(UUID.randomUUID().toString() + "juddicloud");
                n.setCustodyTransferUrl("http://uddi-jbossoverlord.rhcloud.com/services/custody-transfer");
                n.setDescription("juddicloud");
                n.setProxyTransport("org.apache.juddi.v3.client.transport.JAXWSTransport");
                n.setInquiryUrl("http://uddi-jbossoverlord.rhcloud.com/services/inquiry");
                n.setJuddiApiUrl("http://uddi-jbossoverlord.rhcloud.com/services/juddi-api");
                n.setPublishUrl("http://uddi-jbossoverlord.rhcloud.com/services/publish");
                n.setSecurityUrl("http://uddi-jbossoverlord.rhcloud.com/services/security");
                n.setSubscriptionListenerUrl("http://uddi-jbossoverlord.rhcloud.com/services/subscription-listener");
                n.setSubscriptionUrl("http://uddi-jbossoverlord.rhcloud.com/services/subscription");
                n.setReplicationUrl("uddi-jbossoverlord.rhcloud.com/services/replication");
                return n;
        }

        @Test
        public void openAndSave() throws Exception {
                System.out.println("openAndSave");
                try {
                        UDDIClient client = new UDDIClient("META-INF/uddi.xml");
                        List<Node> before = client.getClientConfig().getUDDINodeList();
                        client.getClientConfig().saveConfig();
                        client = null;

                        client = new UDDIClient("META-INF/uddi.xml");
                        List<Node> after = client.getClientConfig().getUDDINodeList();

                        compareNodes(before, after);
                } catch (Exception ex) {
                        ex.printStackTrace();
                        Assert.fail(ex.getMessage());
                }
        }

        @Test
        public void openAddNodeAndSave() throws Exception {
                System.out.println("openAddNodeAndSave");
                UDDIClient client = new UDDIClient("META-INF/uddi.xml");
                List<Node> before = client.getClientConfig().getUDDINodeList();
                client.getClientConfig().addUDDINode(new UDDINode(getCloudInstance()));
                client.getClientConfig().saveConfig();
                List<Node> afterAdding = client.getClientConfig().getUDDINodeList();
                client = null;

                Assert.assertTrue(before.size() != afterAdding.size());

                client = new UDDIClient("META-INF/uddi.xml");
                List<Node> after = client.getClientConfig().getUDDINodeList();

                compareNodes(afterAdding, after);
        }

        private void compareNodes(List<Node> before, List<Node> after) {

                if (before == null && after == null) {
                        return;
                }
                if (before != null && after == null) {
                        Assert.fail("after is null, read error?");
                }
                if (before == null && after != null) {
                        Assert.fail("unexpected before is null and after isn't, read error?");
                }

                Assert.assertTrue(before.size() == after.size());

                //this is because saving/opening the config file does not preserve order
                Map<String, Node> aftermap = new HashMap<String, Node>();

                for (int i = 0; i < after.size(); i++) {
                        aftermap.put(after.get(i).getName(), after.get(i));
                }
 
                for (int i = 0; i < before.size(); i++) {
                        compare(before.get(i), aftermap.get(before.get(i).getName()));
                }
        }

        private void compare(Node get, Node get0) {
                Assert.assertNotNull(get);
                Assert.assertNotNull(get0);

                Assert.assertNotNull(get0.getClientName());
                Assert.assertNotNull(get.getClientName());

                Assert.assertNotNull(get0.getName());
                Assert.assertNotNull(get.getName());

                if (!get.getName().equals(get0.getName())) {
                        Assert.fail("getName: " + get.getName() + " <> " + get0.getName());
                }

                if (!get.getClientName().equals(get0.getClientName())) {
                        Assert.fail("getClientName");
                }

                if (get.getCustodyTransferUrl() == null && get0.getCustodyTransferUrl() != null) {
                        Assert.fail("getCustodyTransferUrl");
                }

                if (get.getCustodyTransferUrl() != null && !get.getCustodyTransferUrl().equals(get0.getCustodyTransferUrl())) {
                        Assert.fail("getCustodyTransferUrl");
                }

                if (get.getDescription() == null && get0.getDescription() != null) {
                        Assert.fail("getDescription");
                }

                if (get.getDescription() != null && !get.getDescription().equals(get0.getDescription())) {
                        Assert.fail("getDescription");
                }

                if (get.getFactoryInitial() == null && get0.getFactoryInitial() != null) {
                        Assert.fail("getFactoryInitial");
                }
                if (get.getFactoryInitial() != null && !get.getFactoryInitial().equals(get0.getFactoryInitial())) {
                        Assert.fail("getFactoryInitial");
                }
                if (get.getFactoryNamingProvider() == null && get0.getFactoryNamingProvider() != null) {
                        Assert.fail("getFactoryNamingProvider");
                }
                if (get.getFactoryNamingProvider() != null && !get.getFactoryNamingProvider().equals(get0.getFactoryNamingProvider())) {
                        Assert.fail("getFactoryNamingProvider");
                }
                if (get.getFactoryURLPkgs() == null && get0.getFactoryURLPkgs() != null) {
                        Assert.fail("getFactoryURLPkgs");
                }
                if (get.getFactoryURLPkgs() != null && !get.getFactoryURLPkgs().equals(get0.getFactoryURLPkgs())) {
                        Assert.fail("getFactoryURLPkgs");
                }

                if (get.getInquiryUrl() != null && !get.getInquiryUrl().equals(get0.getInquiryUrl())) {
                        Assert.fail("getInquiryUrl");
                }

                if (get.getJuddiApiUrl() == null && get0.getJuddiApiUrl() != null) {
                        Assert.fail("getJuddiApiUrl");
                }

                if (get.getJuddiApiUrl() != null && !get.getJuddiApiUrl().equals(get0.getJuddiApiUrl())) {
                        Assert.fail("getJuddiApiUrl");
                }

                if (get.getProxyTransport() != null && !get.getProxyTransport().equals(get0.getProxyTransport())) {
                        Assert.fail("getProxyTransport");
                }

                if (get.getPublishUrl() == null && get0.getPublishUrl() != null) {
                        Assert.fail("getPublishUrl");
                }

                if (get.getPublishUrl() != null && !get.getPublishUrl().equals(get0.getPublishUrl())) {
                        Assert.fail("getPublishUrl");
                }

                if (get.getReplicationUrl() == null && get0.getReplicationUrl() != null) {
                        Assert.fail("getReplicationUrl");
                }

                if (get.getReplicationUrl() != null && !get.getReplicationUrl().equals(get0.getReplicationUrl())) {
                        Assert.fail("getReplicationUrl");
                }

                if (get.getSecurityUrl() == null && get0.getSecurityUrl() != null) {
                        Assert.fail("getSecurityUrl");
                }

                if (get.getSecurityUrl() != null && !get.getSecurityUrl().equals(get0.getSecurityUrl())) {
                        Assert.fail("getSecurityUrl");
                }

                if (get.getSubscriptionListenerUrl() == null && get0.getSubscriptionListenerUrl() != null) {
                        Assert.fail("getSubscriptionListenerUrl");
                }

                if (get.getSubscriptionListenerUrl() != null && !get.getSubscriptionListenerUrl().equals(get0.getSubscriptionListenerUrl())) {
                        Assert.fail("getSubscriptionListenerUrl");
                }

                if (get.getSubscriptionUrl() == null && get0.getSubscriptionUrl() != null) {
                        Assert.fail("getSubscriptionUrl");
                }

                if (get.getSubscriptionUrl() != null && !get.getSubscriptionUrl().equals(get0.getSubscriptionUrl())) {
                        Assert.fail("getSubscriptionUrl");
                }

        }
}
