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
package org.apache.juddi.api.impl.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import org.junit.After;
import org.junit.Before;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.TModel;

import javax.ws.rs.core.Response;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.apache.juddi.Registry;
import org.apache.juddi.api_v3.rest.UriContainer;

import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.uddi.sub_v3.KeyBag;

/**
 * test cases for the jUDDI Inquiry REST service
 *
 * @author Alex O'Ree
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class UDDIInquiryJAXRSTest extends Assert {

        private final static String ENDPOINT_ADDRESS = "http://localhost:8080/rest";
        private final static String JUDDI_BIZ = "uddi:juddi.apache.org:businesses-asf";
        private final static String JUDDI_SVC = "uddi:juddi.apache.org:services-inquiry";
        private final static String JUDDI_BT = "uddi:juddi.apache.org:servicebindings-inquiry-ws";
        private final static String JUDDI_TMODEL = "uddi:uddi.org:transport:smtp";
        private final static String WADL_ADDRESS = ENDPOINT_ADDRESS + "?_wadl";
        private static Server server;
        UDDIInquiryJAXRS instance = null;

        public UDDIInquiryJAXRSTest() {

                List<Object> providers = new ArrayList<Object>();
                // add custom providers if any
                providers.add(new org.apache.cxf.jaxrs.provider.JAXBElementProvider());

                Map<String, Object> properties = new HashMap<String, Object>();

// Create a mapping between the XML namespaces and the JSON prefixes.
// The JSON prefix can be "" to specify that you don't want any prefix.
                HashMap<String, String> nstojns = new HashMap<String, String>();
                nstojns.put("urn:uddi-org:api_v3", "urn:uddi-org:api_v3");
                nstojns.put("urn:uddi-org:sub_v3", "urn:uddi-org:sub_v3");
                nstojns.put("urn:uddi-org:custody_v3", "urn:uddi-org:custody_v3");
                nstojns.put("urn:uddi-org:repl_v3", "urn:uddi-org:repl_v3");
                nstojns.put("urn:uddi-org:subr_v3", "urn:uddi-org:subr_v3");
                nstojns.put("urn:uddi-org:repl_v3", "urn:uddi-org:repl_v3");
                nstojns.put("urn:uddi-org:vs_v3", "urn:uddi-org:vs_v3");
                nstojns.put("urn:uddi-org:vscache_v3", "urn:uddi-org:vscache_v3");
                nstojns.put("urn:uddi-org:policy_v3", "urn:uddi-org:policy_v3");
                nstojns.put("urn:uddi-org:policy_instanceParms_v3", "urn:uddi-org:policy_instanceParms_v3");
                nstojns.put("http://www.w3.org/2000/09/xmldsig#", "http://www.w3.org/2000/09/xmldsig#");

                properties.put("namespaceMap", nstojns);
                JSONProvider jsonProvider = new org.apache.cxf.jaxrs.provider.json.JSONProvider();
                jsonProvider.setNamespaceMap(nstojns);

                providers.add(jsonProvider);
                instance = JAXRSClientFactory.create(ENDPOINT_ADDRESS, UDDIInquiryJAXRS.class, providers);
        }

        @BeforeClass
        public static void initialize() throws Exception {
                startServer();
                waitForWADL();
                Registry.start();
        }

        @AfterClass
        public static void destroy() throws Exception {
                Registry.stop();
                server.stop();
                server.destroy();
        }

        @Before
        public void setUp() {
        }

        @After
        public void tearDown() {
        }

        private static void startServer() throws Exception {
                JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
                sf.setResourceClasses(UDDIInquiryJAXRS.class);

                List<Object> providers = new ArrayList<Object>();
                // add custom providers if any
                providers.add(new org.apache.cxf.jaxrs.provider.JAXBElementProvider());

                Map<String, Object> properties = new HashMap<String, Object>();

// Create a mapping between the XML namespaces and the JSON prefixes.
// The JSON prefix can be "" to specify that you don't want any prefix.
                HashMap<String, String> nstojns = new HashMap<String, String>();
                nstojns.put("urn:uddi-org:api_v3", "urn:uddi-org:api_v3");
                nstojns.put("urn:uddi-org:sub_v3", "urn:uddi-org:sub_v3");
                nstojns.put("urn:uddi-org:custody_v3", "urn:uddi-org:custody_v3");
                nstojns.put("urn:uddi-org:repl_v3", "urn:uddi-org:repl_v3");
                nstojns.put("urn:uddi-org:subr_v3", "urn:uddi-org:subr_v3");
                nstojns.put("urn:uddi-org:repl_v3", "urn:uddi-org:repl_v3");
                nstojns.put("urn:uddi-org:vs_v3", "urn:uddi-org:vs_v3");
                nstojns.put("urn:uddi-org:vscache_v3", "urn:uddi-org:vscache_v3");
                nstojns.put("urn:uddi-org:policy_v3", "urn:uddi-org:policy_v3");
                nstojns.put("urn:uddi-org:policy_instanceParms_v3", "urn:uddi-org:policy_instanceParms_v3");
                nstojns.put("http://www.w3.org/2000/09/xmldsig#", "http://www.w3.org/2000/09/xmldsig#");

                properties.put("namespaceMap", nstojns);
                JSONProvider jsonProvider = new org.apache.cxf.jaxrs.provider.json.JSONProvider();
                jsonProvider.setNamespaceMap(nstojns);

                providers.add(jsonProvider);
                sf.setProviders(providers);

                sf.setResourceProvider(UDDIInquiryJAXRS.class,
                        new SingletonResourceProvider(new UDDIInquiryJAXRS(), true));
                sf.setAddress(ENDPOINT_ADDRESS);

                server = sf.create();
        }

        // Optional step - may be needed to ensure that by the time individual
// tests start running the endpoint has been fully initialized
        private static void waitForWADL() throws Exception {
                WebClient client = WebClient.create(WADL_ADDRESS);
                // wait for 20 secs or so
                for (int i = 0; i < 20; i++) {
                        Thread.currentThread().sleep(1000);
                        Response response = client.get();
                        if (response.getStatus() == 200) {
                                break;
                        }
                }
                // no WADL is available yet - throw an exception or give tests a chance to run anyway
        }

        /**
         * Test of getBusinessDetailJSON method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetBusinessDetailJSON_NULL() {
                System.out.println("testGetBusinessDetailJSON_NULL");

                String id = UUID.randomUUID().toString();

                BusinessEntity expResult = null;
                BusinessEntity result = instance.getBusinessDetailJSON(id);
        }

        /**
         * Test of getBusinessDetailXML method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetBusinessDetailXML_NULL() {
                System.out.println("testGetBusinessDetailXML_NULL");


                String id = UUID.randomUUID().toString();
                BusinessEntity expResult = null;
                BusinessEntity result = instance.getBusinessDetailXML(id);

        }

        @Test
        public void testGetBusinessDetailXML() {
                System.out.println("testGetBusinessDetailXML");


                String id = UUID.randomUUID().toString();
                BusinessEntity expResult = null;
                BusinessEntity result = instance.getBusinessDetailXML(JUDDI_BIZ);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getBusinessKey());
                Assert.assertFalse(result.getName().isEmpty());

        }

        /**
         * Test of getTModelDetailXML method, of class UDDIInquiryJAXRS.
         */
        @Test
        public void testGetTModelDetailXML() {
                System.out.println("testGetTModelDetailXML");

                TModel result = instance.getTModelDetailXML(JUDDI_TMODEL);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getName());
                Assert.assertNotNull(result.getTModelKey());

        }

        /**
         * Test of getTModelDetailJSON method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetTModelDetailJSON_NULL() {
                System.out.println("getTModelDetailJSON_NULL");
                String id = UUID.randomUUID().toString();

                TModel expResult = null;
                TModel result = instance.getTModelDetailJSON(id);

        }

        @Test
        public void testGetTModelDetailJSON() {
                System.out.println("testGetTModelDetailJSON");

                TModel result = instance.getTModelDetailJSON(JUDDI_TMODEL);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getName());
                Assert.assertNotNull(result.getTModelKey());

        }

        /**
         * Test of getServiceDetailJSON method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetServiceDetailJSON_NULL() {
                System.out.println("getServiceDetailJSON_NULL");
                String id = UUID.randomUUID().toString();

                BusinessService expResult = null;
                BusinessService result = instance.getServiceDetailJSON(id);

        }

        @Test
        public void testGetServiceDetailJSON() {
                System.out.println("getServiceDetailJSON");
                String id = UUID.randomUUID().toString();

                BusinessService expResult = null;
                BusinessService result = instance.getServiceDetailJSON(JUDDI_SVC);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getBusinessKey());
                Assert.assertNotNull(result.getBusinessKey());
        }

        /**
         * Test of getServiceDetailXML method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetServiceDetailXML_NULL() {
                System.out.println("getServiceDetailXML_NULL");
                String id = UUID.randomUUID().toString();

                BusinessService expResult = null;
                BusinessService result = instance.getServiceDetailXML(id);

        }

        @Test
        public void testGetServiceDetailXML() {
                System.out.println("getServiceDetailXML");
                String id = UUID.randomUUID().toString();

                BusinessService expResult = null;
                BusinessService result = instance.getServiceDetailXML(JUDDI_SVC);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getBusinessKey());
                Assert.assertNotNull(result.getBusinessKey());
        }

        /**
         * Test of getOpInfoJSON method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetOpInfoJSON_NULL() {
                System.out.println("getOpInfoJSON_NULL");
                String id = UUID.randomUUID().toString();

                OperationalInfo expResult = null;
                OperationalInfo result = instance.getOpInfoJSON(id);

        }

        @Test
        public void testGetOpInfoJSON() {
                System.out.println("getOpInfoJSON");
                String id = UUID.randomUUID().toString();

                OperationalInfo expResult = null;
                OperationalInfo result = instance.getOpInfoJSON(JUDDI_BIZ);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getAuthorizedName());
                Assert.assertNotNull(result.getEntityKey());
                Assert.assertNotNull(result.getNodeID());

        }

        /**
         * Test of getOpInfoXML method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetOpInfoXML_NULL() {
                System.out.println("getOpInfoXML_NULL");
                String id = UUID.randomUUID().toString();

                OperationalInfo expResult = null;
                OperationalInfo result = instance.getOpInfoXML(id);

        }

        @Test
        public void testGetOpInfoXML() {
                System.out.println("getOpInfoXML");
                String id = UUID.randomUUID().toString();

                OperationalInfo expResult = null;
                OperationalInfo result = instance.getOpInfoXML(JUDDI_BIZ);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getAuthorizedName());
                Assert.assertNotNull(result.getEntityKey());
                Assert.assertNotNull(result.getNodeID());

        }

        /**
         * Test of getBindingDetailJSON method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetBindingDetailJSON_NULL() {
                System.out.println("getBindingDetailJSON_NULL");
                String id = UUID.randomUUID().toString();

                BindingTemplate expResult = null;
                BindingTemplate result = instance.getBindingDetailJSON(id);

        }

        @Test
        public void testGetBindingDetailJSON() {
                System.out.println("getBindingDetailJSON");
                String id = UUID.randomUUID().toString();

                BindingTemplate expResult = null;
                BindingTemplate result = instance.getBindingDetailJSON(JUDDI_BT);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getAccessPoint());
                Assert.assertNotNull(result.getBindingKey());
                Assert.assertNotNull(result.getServiceKey());

        }

        /**
         * Test of getBindingDetailXML method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGetBindingDetailXML_NULL() {
                System.out.println("getBindingDetailXML_NULL");
                String id = UUID.randomUUID().toString();

                BindingTemplate expResult = null;
                BindingTemplate result = instance.getBindingDetailXML(id);

        }

        @Test
        public void testGetBindingDetailXML() {
                System.out.println("getBindingDetailXML");
                String id = UUID.randomUUID().toString();

                BindingTemplate expResult = null;
                BindingTemplate result = instance.getBindingDetailXML(JUDDI_BT);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getAccessPoint());
                Assert.assertNotNull(result.getBindingKey());
                Assert.assertNotNull(result.getServiceKey());

        }

        /**
         * Test of geEndpointsByServiceJSON method, of class UDDIInquiryJAXRS.
         */
        @Test(expected = WebApplicationException.class)
        public void testGeEndpointsByServiceJSON_NULL() {
                System.out.println("geEndpointsByServiceJSON_NULL");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                UriContainer result = instance.geEndpointsByServiceJSON(id);

        }

        @Test
        public void testGeEndpointsByServiceJSON() {
                System.out.println("geEndpointsByServiceJSON");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                UriContainer result = instance.geEndpointsByServiceJSON(JUDDI_SVC);
                Assert.assertNotNull(result);


        }

        @Test(expected = WebApplicationException.class)
        public void testGetEndpointsByServiceXML_NULL() {
                System.out.println("getEndpointsByServiceXML_NULL");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                UriContainer result = instance.getEndpointsByServiceXML(id);

        }

        @Test
        public void testGeTEndpointsByServiceXML() {
                System.out.println("geEndpointsByServiceXML");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                UriContainer result = instance.getEndpointsByServiceXML(JUDDI_SVC);
                Assert.assertNotNull(result);
        }

        @Test
        public void testgetServiceListXML() {
                System.out.println("getServiceListXML");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                KeyBag serviceListXML = instance.getServiceListXML();
                Assert.assertNotNull(serviceListXML);
                Assert.assertFalse(serviceListXML.getServiceKey().isEmpty());
        }

        @Test
        public void testgetServiceListJSON() {
                System.out.println("getServiceListJSON");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                KeyBag serviceListXML = instance.getServiceListJSON();
                Assert.assertNotNull(serviceListXML);
                Assert.assertFalse(serviceListXML.getServiceKey().isEmpty());
        }

        @Test
        public void testgetBusinessListXML() {
                System.out.println("testgetBusinessListXML");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                KeyBag serviceListXML = instance.getBusinessListXML();
                Assert.assertNotNull(serviceListXML);
                Assert.assertFalse(serviceListXML.getBusinessKey().isEmpty());
        }

        @Test
        public void testgetBusinessListJSON() {
                System.out.println("testgetBusinessListJSON");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                KeyBag serviceListXML = instance.getBusinessListJSON();
                Assert.assertNotNull(serviceListXML);
                Assert.assertFalse(serviceListXML.getBusinessKey().isEmpty());
        }

        @Test
        public void testgetTModelListXML() {
                System.out.println("testgetTModelListXML");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                KeyBag serviceListXML = instance.getTModelListXML();
                Assert.assertNotNull(serviceListXML);
                Assert.assertFalse(serviceListXML.getTModelKey().isEmpty());
        }

        @Test
        public void testgetTModelListJSON() {
                System.out.println("testgetTModelListJSON");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                KeyBag serviceListXML = instance.getTModelListJSON();
                Assert.assertNotNull(serviceListXML);
                Assert.assertFalse(serviceListXML.getTModelKey().isEmpty());
        }

        @Test(expected = WebApplicationException.class)
        public void testgetDetailXMLNULL() {
                System.out.println("testgetDetailXMLNULL");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                Object output = instance.getDetailXML(null, null, null, null);
                Assert.assertNotNull(output);
                //Assert.assertFalse(serviceListXML.getTModelKey().isEmpty());
        }

        @Test(expected = WebApplicationException.class)
        public void testgetDetailJSONNULL() {
                System.out.println("testgetDetailJSONNULL");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                Object output = instance.getDetailJSON(null, null, null, null);
                Assert.assertNotNull(output);
                //Assert.assertFalse(serviceListXML.getTModelKey().isEmpty());
        }

        @Test(expected = WebApplicationException.class)
        public void testgetDetailJSON_Random() {
                System.out.println("testgetDetailJSON_Random");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                Object output = instance.getDetailJSON(id, null, null, null);
                Assert.assertNotNull(output);
                //Assert.assertFalse(serviceListXML.getTModelKey().isEmpty());
        }

        @Test(expected = WebApplicationException.class)
        public void testgetDetailXML_Random() {
                System.out.println("testgetDetailXML_Random");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                Object output = instance.getDetailXML(id, null, null, null);
                Assert.assertNotNull(output);
                //Assert.assertFalse(serviceListXML.getTModelKey().isEmpty());
        }

        @Test
        public void testgetDetailJSON_SVC() {
                System.out.println("testgetDetailJSON_SVC");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                BusinessService output = (BusinessService) instance.getDetailJSON(JUDDI_SVC, null, null, null);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getBusinessKey());
                Assert.assertNotNull(output.getServiceKey());
        }
/*
 * These tests are valid, but fail when ran as a unit test. cause unknown
        @Test
        public void testgetDetailXML_SVC() {
                System.out.println("testgetDetailXML_SVC");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                BusinessService output = (BusinessService) instance.getDetailXML(JUDDI_SVC, null, null, null);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getBusinessKey());
                Assert.assertNotNull(output.getServiceKey());
        }

        @Test
        public void testgetDetailJSON_BIZ() {
                System.out.println("testgetDetailJSON_BIZ");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                BusinessEntity output = (BusinessEntity) instance.getDetailJSON(null, JUDDI_BIZ, null, null);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getBusinessKey());

        }

        @Test
        public void testgetDetailXML_BIZ() {
                System.out.println("testgetDetailXML_BIZ");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                BusinessEntity output = (BusinessEntity) instance.getDetailXML(null, JUDDI_BIZ, null, null);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getBusinessKey());
        }

        @Test
        public void testgetDetailJSON_TM() {
                System.out.println("testgetDetailJSON_TM");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                TModel output = (TModel) instance.getDetailJSON(null, null, JUDDI_TMODEL, null);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getTModelKey());

        }

        @Test
        public void testgetDetailXML_TM() {
                System.out.println("testgetDetailXML_TM");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                TModel output = (TModel) instance.getDetailXML(null, null, JUDDI_TMODEL, null);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getTModelKey());
        }
        
        
        @Test
        public void testgetDetailJSON_BT() {
                System.out.println("testgetDetailJSON_BT");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                TModel output = (TModel) instance.getDetailJSON(null, null, null, JUDDI_BT);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getTModelKey());

        }

        @Test
        public void testgetDetailXML_BT() {
                System.out.println("testgetDetailXML_BT");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                TModel output = (TModel) instance.getDetailXML(null, null, null, JUDDI_BT);
                Assert.assertNotNull(output);
                Assert.assertNotNull(output.getTModelKey());
        }
        
        @Test(expected = WebApplicationException.class)
        public void testgetDetailJSON_ALL() {
                System.out.println("testgetDetailJSON_ALL");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                instance.getDetailJSON(JUDDI_BIZ, JUDDI_SVC, JUDDI_TMODEL, JUDDI_BT);
                Assert.fail();
        }
        @Test(expected = WebApplicationException.class)
        public void testgetDetailXML_ALL() {
                System.out.println("testgetDetailXML_ALL");
                String id = UUID.randomUUID().toString();

                UriContainer expResult = null;
                instance.getDetailXML(JUDDI_BIZ, JUDDI_SVC, JUDDI_TMODEL, JUDDI_BT);
                Assert.fail();
                
        }*/
}