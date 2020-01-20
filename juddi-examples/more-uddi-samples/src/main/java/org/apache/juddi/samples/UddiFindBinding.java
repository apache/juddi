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

import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class shows you how to find an endpoint by searching through all
 * services
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiFindBinding {

        private UDDISecurityPortType security = null;
        private UDDIInquiryPortType inquiry = null;

        public UddiFindBinding() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        inquiry = transport.getUDDIInquiryService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void fire(String token) {
                try {
                        // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
                        // and can save other publishers).
                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID("root");
                        getAuthTokenRoot.setCred("root");

                        if (token == null) {
                                // Making API call that retrieves the authentication token for the 'root' user.
                                AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                                System.out.println("root AUTHTOKEN = " + "don't log auth tokens!");
                                token = rootAuthToken.getAuthInfo();
                        }
                        FindService fs = new FindService();
                        fs.setAuthInfo(token);
                        fs.getName().add(new Name());
                        fs.getName().get(0).setValue("%");
                        fs.setFindQualifiers(new FindQualifiers());
                        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);

                        ServiceList findService = inquiry.findService(fs);
                        System.out.println(findService.getServiceInfos().getServiceInfo().size());
                        GetServiceDetail gs = new GetServiceDetail();
                        for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                                gs.getServiceKey().add(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey());
                        }

                        ServiceDetail serviceDetail = inquiry.getServiceDetail(gs);
                        for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
                                //System.out.println(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size());
                                for (int k = 0; k < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                                        System.out.println(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint().getValue());
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                UddiFindBinding sp = new UddiFindBinding();
                sp.fire(null);
        }
}
