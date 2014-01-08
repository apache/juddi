/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.samples;

import javax.xml.bind.JAXB;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class show you how to get all available information for service
 * (excluding OperationalInfo)
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiGetServiceDetails {

        private static UDDISecurityPortType security = null;
        private static JUDDIApiPortType juddiApi = null;
        private static UDDIPublicationPortType publish = null;
        private static UDDIInquiryPortType inquiry = null;

        public UddiGetServiceDetails() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        juddiApi = transport.getJUDDIApiService();
                        publish = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void Fire(String token, String key) {
                if (key == null) {
                        System.out.println("No key provided!");
                        return;
                }
                try {
                        // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
                        // and can save other publishers).
                        if (token == null) {

                                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                                getAuthTokenRoot.setUserID("root");
                                getAuthTokenRoot.setCred("root");

                                // Making API call that retrieves the authentication token for the 'root' user.
                                AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                                System.out.println("root AUTHTOKEN = " + "don't log auth tokens!");
                                token = rootAuthToken.getAuthInfo();
                        }
                        GetServiceDetail fs = new GetServiceDetail();
                        fs.setAuthInfo(token);
                        fs.getServiceKey().add(key);
                        ServiceDetail serviceDetail = inquiry.getServiceDetail(fs);
                        if (serviceDetail == null || serviceDetail.getBusinessService().isEmpty()) {
                                System.out.println("mykey is not registered");
                        } else {
                                JAXB.marshal(serviceDetail, System.out);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                UddiGetServiceDetails sp = new UddiGetServiceDetails();
                sp.Fire(null, "uddi:juddi.apache.org:services-inquiry");
        }
}
