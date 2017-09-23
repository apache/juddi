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

import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This is an example how to make a UDDI key generator, which will enable you to
 * make UDDI v3 keys with (almost) whatever pattern you want
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiKeyGenerator {

        private static UDDISecurityPortType security = null;
        private static UDDIPublicationPortType publish = null;

        public UddiKeyGenerator() {
                try {
            // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void fire(String token, String domain) {
                try {
            // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
                        // and can save other publishers).
                        if (token == null) {
                                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                                getAuthTokenRoot.setUserID("uddi");
                                getAuthTokenRoot.setCred("uddi");

                                // Making API call that retrieves the authentication token for the 'root' user.
                                AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                                System.out.println("uddi AUTHTOKEN = " + "don't log auth tokens!");
                                token = rootAuthToken.getAuthInfo();
                        }
                        SaveTModel st = new SaveTModel();
                        st.setAuthInfo(token);
                        st.getTModel().add(UDDIClerk.createKeyGenator(domain, domain, "en"));
                        TModelDetail saveTModel = publish.saveTModel(st);

                        System.out.println("Saved!  key = " + saveTModel.getTModel().get(0).getTModelKey());
                        /*
            //Hey! tModel Key Generators can be nested too!
                        st = new SaveTModel();
                        st.setAuthInfo(rootAuthToken.getAuthInfo());
                        st.getTModel().add(UDDIClerk.createKeyGenator("uddi:bea.com:servicebus.default:keygenerator", "bea.com:servicebus.default", "en"));
                        publish.saveTModel(st);

                        // This code block proves that you can create tModels based on a nested keygen
                        st = new SaveTModel();
                        TModel m = new TModel();
                        m.setTModelKey("uddi:bea.com:servicebus.default.proxytest2");
                        m.setName(new Name("name", "lang"));
                        st.setAuthInfo(rootAuthToken.getAuthInfo());
                        st.getTModel().add(m);
                        publish.saveTModel(st);

                                */
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                UddiKeyGenerator sp = new UddiKeyGenerator();
                sp.fire(null, "www.juddi.is.cool.org");
        }
}
