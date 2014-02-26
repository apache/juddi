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

import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.juddi.v3.client.config.TokenResolver;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class JUDDI_200_GUI_IntegrationTest {

        private static final Log logger = LogFactory.getLog(JUDDI_200_GUI_IntegrationTest.class);
        private static UDDIClient manager;
        private static UDDIClerk clerk;
        private static String baseurl = null;

        @AfterClass
        public static void stopManager() throws ConfigurationException {

        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {

                 if (!TckPublisher.isEnabled()) return;
                manager = new UDDIClient();
                baseurl = manager.getClientConfig().getConfiguration().getString("client.nodes.node(0).juddigui");
                if (baseurl != null) {
                        if (!baseurl.endsWith("/")) {
                                baseurl = baseurl + "/";
                                
                        }
                        baseurl = TokenResolver.replaceTokens(baseurl, manager.getClientConfig().getUDDINode("default").getProperties());
                }
        }

        @Test
        public void Test001() throws Exception {
                runTest(baseurl + "home.jsp");
        }

        @Test
        public void Test002() throws Exception {
                runTest(baseurl + "about.jsp");
        }

        @Test
        public void Test003() throws Exception {
                runTest(baseurl + "advanced.jsp");
        }

        @Test
        public void Test004() throws Exception {
                runTest(baseurl + "assertions.jsp");
        }

        @Test
        public void Test005() throws Exception {
                runTest(baseurl + "bindingChooser.jsp");
        }

        @Test
        public void Test006() throws Exception {
                runTest(baseurl + "businessBrowse.jsp");
        }

        @Test
        public void Test007() throws Exception {
                runTest(baseurl + "businessChooser.jsp");
        }

        @Test
        public void Test008() throws Exception {
                runTest(baseurl + "businessEditor2.jsp");
        }

        @Test
        public void Test009() throws Exception {
                runTest(baseurl + "editSubscription.jsp");
        }

        @Test
        public void Test0010() throws Exception {
                runTest(baseurl + "error.jsp");
        }

        @Test
        public void Test0011() throws Exception {
                runTest(baseurl + "header-top.jsp");
        }

        @Test
        public void Test0012() throws Exception {
                runTest(baseurl + "home.jsp");
        }

        @Test
        public void Test0013() throws Exception {
                runTest(baseurl + "importFromWadl.jsp");
        }

        @Test
        public void Test0014() throws Exception {
                runTest(baseurl + "importFromWsdl.jsp");
        }

        @Test
        public void Test0015() throws Exception {
                runTest(baseurl + "index.jsp");
        }

        //@Test
        public void Test0016() throws Exception {
                runTest(baseurl + "reginfo.jsp");
        }

        @Test
        public void Test0017() throws Exception {
                runTest(baseurl + "search.jsp");
        }

        @Test
        public void Test0018() throws Exception {
                runTest(baseurl + "serviceBrowse.jsp");
        }

       // @Test expect redirect
        public void Test0019() throws Exception {
                runTest(baseurl + "serviceEditor.jsp?id=uddi:juddi.apache.org:services-inquiry-rest");
        }

        //@Test auth req
        public void Test0020() throws Exception {
                runTest(baseurl + "settings.jsp");
        }

        @Test
        public void Test0021() throws Exception {
                runTest(baseurl + "signer.jsp");
        }

        @Test
        public void Test0022() throws Exception {
                runTest(baseurl + "subscriptionFeed.jsp");
        }

        @Test
        public void Test0023() throws Exception {
                runTest(baseurl + "tmodelBrowse.jsp");
        }

        @Test 
        public void Test0024() throws Exception {
                runTest(baseurl + "tmodelEditor.jsp?id=uddi:uddi.org:sortorder:binarysort");
        }

        @Test
        public void Test0025() throws Exception {
                runTest(baseurl + "tmodelPartitions.jsp");
        }

        @Test
        public void Test0026() throws Exception {
                runTest(baseurl + "transfer.jsp");
        }

        @Test
        public void Test0027() throws Exception {
                runTest(baseurl + "viewSubscriptions.jsp");
        }

        private void runTest(String url) throws Exception {
                Assume.assumeTrue(TckPublisher.isJUDDI());
                Assume.assumeTrue(baseurl != null && baseurl.length() >= 5);
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                logger.info("Fetching " + httpGet.getURI());
                HttpResponse response = client.execute(httpGet);
                client.getConnectionManager().shutdown();
                Assert.assertTrue(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode() == 200);
        }
}
