/*
 * Copyright 2015 The Apache Software Foundation.
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
package org.apache.juddi.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.junit.*;
import org.uddi.repl_v3.ReplicationConfiguration;

import javax.xml.bind.JAXB;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

/**
 *
 * @author alex
 */
public class InstallTest {
        
        public InstallTest() {
        }
        
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

        /**
         * Test of applyReplicationTokenChanges method, of class Install.
         */
        @Test
        public void testApplyReplicationTokenChanges() throws Exception {
                System.out.println("applyReplicationTokenChanges");
                InputStream fis = getClass().getClassLoader().getResourceAsStream("juddi_install_data/root_replicationConfiguration.xml");
                
                ReplicationConfiguration replicationCfg = JAXB.unmarshal(fis, ReplicationConfiguration.class);
                Properties props = new Properties();
                props.put(Property.JUDDI_NODE_ID, "uddi:a_custom_node");
                props.put(Property.JUDDI_BASE_URL, "http://juddi.apache.org");
                props.put(Property.JUDDI_BASE_URL_SECURE, "https://juddi.apache.org");
                
                Configuration config = new MapConfiguration(props);
                String thisnode = "uddi:a_custom_node";
                
                ReplicationConfiguration result = Install.applyReplicationTokenChanges(replicationCfg, config, thisnode);
                StringWriter sw = new StringWriter();
                JAXB.marshal(result, sw);
                Assert.assertFalse(sw.toString().contains("${juddi.nodeId}"));
                Assert.assertFalse(sw.toString().contains("${juddi.server.baseurlsecure}"));
                Assert.assertFalse(sw.toString().contains("${juddi.server.baseurl}"));
                
        }
        
}
