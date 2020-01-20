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
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;

/**
 * This class show you how get all available Access Points/Endpoints for a
 * service. This is harder than it sounds due to the complexity of UDDI's data
 * structure. The output is the list of URLs given a service's key
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiFindEndpoints {

        private UDDIClerk clerk = null;

        public UddiFindEndpoints() {
                try {
            // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        clerk = clerkManager.getClerk("default");
            // a ClerkManager can be a client to multiple UDDI nodes, so 
                        // supply the nodeName (defined in your uddi.xml.
                        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void fire(String authtoken, String key) {
                try {
                        if (key == null) {
                                key = "uddi:juddi.apache.org:services-inquiry";
                        }

                        List<String> endpoints = clerk.getEndpoints(key);
                        System.out.println("Endpoints returned: " + endpoints.size());
                        for (int i = 0; i < endpoints.size(); i++) {
                                System.out.println(endpoints.get(i));
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                UddiFindEndpoints sp = new UddiFindEndpoints();
                sp.fire(null, null);
        }
}
