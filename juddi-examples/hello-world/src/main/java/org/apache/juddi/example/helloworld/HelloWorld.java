/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.example.helloworld;

import org.uddi.api_v3.*;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This basic hello world example shows you how to sign into a UDDI server that
 * supports the Security API to issue "auth tokens". 
 * 
 * The config loads from META-INF and uses JAXWS transport, meaning that you
 * need a functioning UDDI server to use this
 *
 */
public class HelloWorld {

        private static UDDISecurityPortType security = null;

        public HelloWorld() {
                try {
        	// create a client and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient uddiClient = new UDDIClient("META-INF/hello-world-uddi.xml");
        	// a UddiClient can be a client to multiple UDDI nodes, so 
                        // supply the nodeName (defined in your uddi.xml.
                        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                        Transport transport = uddiClient.getTransport("default");
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void getAuthToken() {
                GetAuthToken getAuthToken = new GetAuthToken();
                getAuthToken.setUserID("root");
                getAuthToken.setCred("");
                try {
                        AuthToken authToken = security.getAuthToken(getAuthToken);
                        System.out.println("Login successful!");
                        System.out.println("AUTHTOKEN = "
                                + "(don't log auth tokens!)");
                        
                        security.discardAuthToken(new DiscardAuthToken(authToken.getAuthInfo()));
                        System.out.println("Logged out");
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                HelloWorld hw = new HelloWorld();
                hw.getAuthToken();
        }
}
