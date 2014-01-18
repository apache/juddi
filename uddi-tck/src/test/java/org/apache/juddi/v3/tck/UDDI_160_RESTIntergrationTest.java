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
package org.apache.juddi.v3.tck;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXB;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;

/**
 * Optional HTTP GET, REST style web services for UDDI
 *
 * @author Alex O'Ree
 */
public class UDDI_160_RESTIntergrationTest {

        private static UDDIInquiryPortType inquiry = null;
        private static Log logger = LogFactory.getLog(UDDI_160_RESTIntergrationTest.class);
        private static String authInfo = null;
        private static UDDIClient manager;

        @BeforeClass
        public static void startRegistry() throws ConfigurationException {

                manager = new UDDIClient();
                manager.start();

                try {
                        Transport transport = manager.getTransport();
                        inquiry = transport.getUDDIInquiryService();
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                manager.stop();
        }

        /*@Test
         public void InquiryREST_WADL_GET() throws Exception {
         Assume.assumeTrue(TckPublisher.isInquiryRestEnabled());

         String url = manager.getClientConfig().getHomeNode().getInquiry_REST_Url();
         Assume.assumeNotNull(url);
         HttpClient client = new DefaultHttpClient();
         HttpGet httpGet = new HttpGet(url + "?_wadl");
         HttpResponse response = client.execute(httpGet);
         Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);

         }*/
        @Test
        public void InquiryREST_GET_Business() throws Exception {
                Assume.assumeTrue(TckPublisher.isInquiryRestEnabled());
                FindBusiness fb = new FindBusiness();
                fb.setMaxRows(1);
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                BusinessList findBusiness = inquiry.findBusiness(fb);
                Assume.assumeTrue(findBusiness != null);
                Assume.assumeTrue(findBusiness.getBusinessInfos() != null);
                Assume.assumeTrue(!findBusiness.getBusinessInfos().getBusinessInfo().isEmpty());

                String url = manager.getClientConfig().getHomeNode().getInquiry_REST_Url();

                Assume.assumeNotNull(url);
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + "?businessKey=" + findBusiness.getBusinessInfos().getBusinessInfo().get(0).getBusinessKey());
                logger.info("Fetching " + httpGet.getURI());
                HttpResponse response = client.execute(httpGet);
                client.getConnectionManager().shutdown();
                Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
                logger.info("Response content: " + response.getEntity().getContent());
                BusinessEntity unmarshal = JAXB.unmarshal(response.getEntity().getContent(), BusinessEntity.class);
                Assert.assertNotNull(unmarshal);
                Assert.assertEquals(unmarshal.getBusinessKey(), findBusiness.getBusinessInfos().getBusinessInfo().get(0).getBusinessKey());


        }

        @Test
        public void InquiryREST_GET_TModel() throws Exception {
                Assume.assumeTrue(TckPublisher.isInquiryRestEnabled());
                FindTModel fb = new FindTModel();
                fb.setMaxRows(1);
                fb.setName(new Name(UDDIConstants.WILDCARD, null));
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                TModelList findTModel = inquiry.findTModel(fb);
                Assume.assumeTrue(findTModel != null);
                Assume.assumeTrue(findTModel.getTModelInfos() != null);
                Assume.assumeTrue(!findTModel.getTModelInfos().getTModelInfo().isEmpty());

                String url = manager.getClientConfig().getHomeNode().getInquiry_REST_Url();

                Assume.assumeNotNull(url);
                
                HttpClient client = new DefaultHttpClient();
                
                HttpGet httpGet = new HttpGet(url + "?tModelKey=" + findTModel.getTModelInfos().getTModelInfo().get(0).getTModelKey());
                logger.info("Fetching " + httpGet.getURI());
                HttpResponse response = client.execute(httpGet);
                client.getConnectionManager().shutdown();
                Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
                logger.info("Response content: " + response.getEntity().getContent());
                TModel unmarshal = JAXB.unmarshal(response.getEntity().getContent(), TModel.class);
                Assert.assertNotNull(unmarshal);
                Assert.assertEquals(unmarshal.getTModelKey(), findTModel.getTModelInfos().getTModelInfo().get(0).getTModelKey());


        }

        @Test
        public void InquiryREST_GET_Service() throws Exception {
                Assume.assumeTrue(TckPublisher.isInquiryRestEnabled());
                //find the first service via inquriy soap
                FindService fb = new FindService();
                fb.setMaxRows(1);
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                ServiceList findService = inquiry.findService(fb);
                Assume.assumeTrue(findService != null);
                Assume.assumeTrue(findService.getServiceInfos() != null);
                Assume.assumeTrue(!findService.getServiceInfos().getServiceInfo().isEmpty());

                String url = manager.getClientConfig().getHomeNode().getInquiry_REST_Url();

                Assume.assumeNotNull(url);
                
                //get the results via inquiry rest
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + "?serviceKey=" + findService.getServiceInfos().getServiceInfo().get(0).getServiceKey());
                logger.info("Fetching " + httpGet.getURI());
                HttpResponse response = client.execute(httpGet);
                client.getConnectionManager().shutdown();
                Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
                logger.info("Response content: " + response.getEntity().getContent());
                BusinessService unmarshal = JAXB.unmarshal(response.getEntity().getContent(), BusinessService.class);
                Assert.assertNotNull(unmarshal);
                Assert.assertEquals(unmarshal.getServiceKey(), findService.getServiceInfos().getServiceInfo().get(0).getServiceKey());


        }

        private BindingTemplate getFirstBindingTemplate() {
                try {
                        int fetch = 10;
                        int offset = 0;
                        FindService fb = new FindService();
                        fb.setMaxRows(fetch);
                        fb.setListHead(offset);
                        fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                        fb.setFindQualifiers(new FindQualifiers());
                        fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        ServiceList findService = inquiry.findService(fb);
                        int returnedcount = findService.getServiceInfos().getServiceInfo().size();
                        do {
                                for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                                        GetServiceDetail gsd = new GetServiceDetail();
                                        gsd.getServiceKey().add(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey());
                                        ServiceDetail serviceDetail = inquiry.getServiceDetail(gsd);
                                        if (serviceDetail.getBusinessService().get(0).getBindingTemplates() != null
                                                && !serviceDetail.getBusinessService().get(0).getBindingTemplates().getBindingTemplate().isEmpty()) {
                                                return serviceDetail.getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0);
                                        }

                                }
                                offset = fetch;
                                fetch = fetch + 10;
                                fb = new FindService();
                                fb.setMaxRows(fetch);
                                fb.setListHead(offset);
                                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                                fb.setFindQualifiers(new FindQualifiers());
                                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                                findService = inquiry.findService(fb);
                                returnedcount = findService.getServiceInfos().getServiceInfo().size();

                        } while (returnedcount > 0);
                } catch (DispositionReportFaultMessage ex) {
                        Logger.getLogger(UDDI_160_RESTIntergrationTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RemoteException ex) {
                        Logger.getLogger(UDDI_160_RESTIntergrationTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
        }

        @Test
        public void InquiryREST_GET_Binding() throws Exception {
                Assume.assumeTrue(TckPublisher.isInquiryRestEnabled());
                
                BindingTemplate bt = getFirstBindingTemplate();
                Assume.assumeTrue(bt != null);
                
                String url = manager.getClientConfig().getHomeNode().getInquiry_REST_Url();

                Assume.assumeNotNull(url);
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + "?bindingKey=" + bt.getBindingKey());
                logger.info("Fetching " + httpGet.getURI());
                HttpResponse response = client.execute(httpGet);
                client.getConnectionManager().shutdown();
                Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
                logger.info("Response content: " + response.getEntity().getContent());
                BindingTemplate unmarshal = JAXB.unmarshal(response.getEntity().getContent(), BindingTemplate.class);
                Assert.assertNotNull(unmarshal);
                Assert.assertEquals(unmarshal.getServiceKey(), bt.getServiceKey());
                Assert.assertEquals(unmarshal.getBindingKey(), bt.getBindingKey());


        }
}
