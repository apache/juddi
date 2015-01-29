/*
 * Copyright 2015 The Apache Software Foundation.
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
package org.apache.juddi.samples;

import java.util.List;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;

/**
 *
 * @author alex
 */
public class EntryPoitMultiNode {

        static void goMultiNode() throws Exception{
              String currentNode = "default";
                UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                
                List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                String input=null;
                do {
                        System.out.println("1) Replication - Setup replication between two nodes");
                      
                        System.out.println("2) Sets the replication between two instances of jUDDI on 8080 and 9080");
                        System.out.println("3) Sets directed replication between 3 instances of jUDDI on 8080 and 8080, 10080");
                        System.out.println("4) Prints the replication status for all nodes");


                        System.out.println("q) quit");
                        System.out.print("Selection: ");
                        input = System.console().readLine();
                        
                                processInput(input,clerkManager);
                        
                } while (!input.equalsIgnoreCase("q"));
        }

        private static void processInput(String input, UDDIClient clerkManager) throws Exception{
                 if (input.equals("1")) {
                        // System.out.println("30) Replication - Setup replication between two nodes");
                        new JuddiAdminService(null, null).setupReplication();

                }
                else  if (input.equals("2")) {
                       
                        new JuddiAdminService(null, null).autoMagic();
                         List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                         for (int i=0; i < uddiNodeList.size(); i++){
                                 new UddiCreatebulk(uddiNodeList.get(i).getName()).publishBusiness(null, 1, 1);
                         }
                        //new UddiCreatebulk("uddi:another.juddi.apache.org:node2").publishBusiness(null, 1, 1);
                }
               else if (input.equals("3")) {
                        new JuddiAdminService(clerkManager, null).autoMagicDirected();

                        List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                         for (int i=0; i < uddiNodeList.size(); i++){
                                 new UddiCreatebulk(uddiNodeList.get(i).getName()).publishBusiness(null, 1, 1);
                         }
                }
               else if (input.equals("4")) {
                        new JuddiAdminService(clerkManager, null).printStatus();
                }
                
        }
        
}
