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
package org.apache.juddi.v3.client.cli;

import java.util.List;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class shows you how to search for services that are digitally signed
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiDigitalSignatureSearch {

        private static UDDISecurityPortType security = null;
        private static UDDIInquiryPortType inquiry = null;
        private static UDDIPublicationPortType publish = null;

        /**
         * This sets up the ws proxies using uddi.xml in META-INF
         */
        public UddiDigitalSignatureSearch() {
                try {
            // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        inquiry = transport.getUDDIInquiryService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /**
         * Main entry point
         *
         * @param args
         */
        public static void main(String args[]) {

                UddiDigitalSignatureSearch sp = new UddiDigitalSignatureSearch();
                sp.Fire(null);
        }

        public void Fire(String token) {
                try {

                        FindService fs = new FindService();
                        //optional, usually
                        if (token == null) {
                                token = GetAuthKey("root", "root");
                        }
                        fs.setAuthInfo(token);
                        fs.setFindQualifiers(new FindQualifiers());
                        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.SORT_BY_DATE_ASC);
                        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.SIGNATURE_PRESENT);
                        Name n = new Name();
                        n.setValue("%");
                        fs.getName().add(n);
                        ServiceList findService = inquiry.findService(fs);
                        if (findService != null && findService.getServiceInfos() != null) {
                                for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                                        System.out.println(ListToString(findService.getServiceInfos().getServiceInfo().get(i).getName()));
                                }
                        } else
                                System.out.println("no results found.");
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /**
         * Gets a UDDI style auth token, otherwise, appends credentials to the
         * ws proxies (not yet implemented)
         *
         * @param username
         * @param password
         * @param style
         * @return
         */
        private String GetAuthKey(String username, String password) {
                try {

                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(username);
                        getAuthTokenRoot.setCred(password);

                        // Making API call that retrieves the authentication token for the 'root' user.
                        AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                        System.out.println("root AUTHTOKEN = " + "dont log auth tokens!");
                        return rootAuthToken.getAuthInfo();
                } catch (Exception ex) {
                        System.out.println("Could not authenticate with the provided credentials " + ex.getMessage());
                }
                return null;
        }

        private String ListToString(List<Name> name) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < name.size(); i++) {
                        sb.append(name.get(i).getValue()).append(" ");
                }
                return sb.toString();
        }
}
