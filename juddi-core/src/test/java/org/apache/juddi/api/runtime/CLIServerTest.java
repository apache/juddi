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
package org.apache.juddi.api.runtime;

import java.math.BigInteger;
import java.util.Random;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import junit.framework.Assert;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.JUDDIApiService;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.v3_service.UDDIReplicationPortType;
import org.apache.juddi.v3.client.UDDIService;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.PersonName;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.DoPing;

/**
 *
 * @author alex
 */
public class CLIServerTest {

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                replication.stop();
                replication = null;
                juddiapi.stop();
                juddiapi = null;
        }

        static Endpoint replication = null;
        static Endpoint juddiapi = null;
        static String replUrl = null;
        static String juddiUrl = null;
        static boolean sink = false;
     static  replicantImpl repl= new replicantImpl();
      static  juddiTestimpl jude= new juddiTestimpl();

        @BeforeClass
        public static void startManager() throws Exception {
               
                Random r = new Random(System.currentTimeMillis());
                replUrl = "http://localhost:" + (7000 + r.nextInt(1000)) + "/repl";
                replication = Endpoint.publish(replUrl, repl);

                juddiUrl = "http://localhost:" + (7000 + r.nextInt(1000)) + "/juddi";
                juddiapi = Endpoint.publish(juddiUrl, jude);
                System.out.println("Endpoint up at " + replUrl);
                System.out.println("Endpoint up at " + juddiUrl);
        }

        @Test
        public void testGetReplicationConfig() throws Exception {

                JUDDIApiPortType juddiApiService = new JUDDIApiService().getJUDDIApiImplPort();
                ((BindingProvider) juddiApiService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, juddiUrl);
                juddiApiService.getReplicationNodes(null);
                Assert.assertTrue(sink);
                sink = false;
        }
        
        @Test
        public void testSetReplicationConfig() throws Exception {

                JUDDIApiPortType juddiApiService = new JUDDIApiService().getJUDDIApiImplPort();
                ((BindingProvider) juddiApiService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, juddiUrl);
                ReplicationConfiguration replicationConfiguration = new ReplicationConfiguration();
                replicationConfiguration.setCommunicationGraph(new CommunicationGraph());
                replicationConfiguration.setRegistryContact(new ReplicationConfiguration.RegistryContact());
                replicationConfiguration.getRegistryContact().setContact(new Contact());
                        replicationConfiguration.getRegistryContact().getContact().getPersonName().add(new PersonName("name", null));

                juddiApiService.setReplicationNodes(null, replicationConfiguration);
                Assert.assertTrue(sink);
                sink = false;
        }
        
         @Test
        public void testReplicationGetChanges() throws Exception {

                UDDIReplicationPortType juddiApiService = new UDDIService().getUDDIReplicationPort();
                ((BindingProvider) juddiApiService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, replUrl);
                juddiApiService.getChangeRecords(null, new HighWaterMarkVectorType(), BigInteger.ONE, new HighWaterMarkVectorType());
                Assert.assertTrue(sink);
                sink = false;
        }
 @Test
        public void testReplicationPing() throws Exception {

                 UDDIReplicationPortType juddiApiService = new UDDIService().getUDDIReplicationPort();
                ((BindingProvider) juddiApiService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, replUrl);
                juddiApiService.doPing(new DoPing());//null, new HighWaterMarkVectorType(), BigInteger.ONE, new HighWaterMarkVectorType());
                Assert.assertTrue(sink);
                sink = false;
        }


}
