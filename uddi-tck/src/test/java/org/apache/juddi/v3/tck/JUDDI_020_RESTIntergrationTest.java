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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class JUDDI_020_RESTIntergrationTest {

        private static UDDISecurityPortType security = null;
        private static JUDDIApiPortType publisher = null;
        private static Log logger = LogFactory.getLog(JUDDI_100_ClientSubscriptionInfoTest.class);
        private static String authInfo = null;
        private static UDDIClient manager;

        @BeforeClass
        public static void startRegistry() throws ConfigurationException {

                manager = new UDDIClient();
                manager.start();


                logger.debug("Getting auth tokens..");
                try {
                        Transport transport = manager.getTransport();

                        security = transport.getUDDISecurityService();
                        GetAuthToken getAuthToken = new GetAuthToken();
                        getAuthToken.setUserID(TckPublisher.getRootPublisherId());
                        getAuthToken.setCred(TckPublisher.getRootPassword());
                        authInfo = security.getAuthToken(getAuthToken).getAuthInfo();

                        publisher = transport.getJUDDIApiService();
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                manager.stop();
        }

        @Test
        public void BusinessREST_GET() throws Exception {
                String url = manager.getClientConfig().getHomeNode().getInquiry_REST_Url();
                Assume.assumeNotNull(url);
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + "?wadl");
                HttpResponse response = client.execute(httpGet);
                
        }
}
