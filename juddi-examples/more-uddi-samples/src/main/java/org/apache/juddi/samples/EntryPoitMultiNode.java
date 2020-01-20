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
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.UDDIInquiryPortType;

/**
 *
 * @author alex
 */
public class EntryPoitMultiNode {

        static void goMultiNode() throws Exception {
               
                UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");

               
                String input = null;
                do {
                        System.out.println("1) Sets undirected replication two instances of jUDDI");
                        System.out.println("2) Sets undirected replication 3 instances of jUDDI");
                        System.out.println("3) Sets directed replication between 3 instances of jUDDI");
                        System.out.println("4) Prints the replication status for all nodes");
                        System.out.println("5) Prints the business, service, and tmodels counts");
                        System.out.println("6) Ping all nodes");

                        System.out.println("q) quit");
                        System.out.print("Selection: ");
                        input = System.console().readLine();

                        processInput(input, clerkManager);

                } while (!input.equalsIgnoreCase("q"));
        }

        private static void processInput(String input, UDDIClient clerkManager) throws Exception {
                if (input.equals("1")) {

                        new JuddiAdminService(clerkManager, null).autoMagic();
                        List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                        for (int i = 0; i < uddiNodeList.size(); i++) {
                                new UddiCreatebulk(uddiNodeList.get(i).getName()).publishBusiness(null, 1, 1, "root@" + uddiNodeList.get(i).getName());
                        }
                        //new UddiCreatebulk("uddi:another.juddi.apache.org:node2").publishBusiness(null, 1, 1);
                } else if (input.equals("2")) {

                        new JuddiAdminService(clerkManager, null).autoMagic3();
                        List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                        for (int i = 0; i < uddiNodeList.size(); i++) {
                                new UddiCreatebulk(uddiNodeList.get(i).getName()).publishBusiness(null, 1, 1, "root@" + uddiNodeList.get(i).getName());
                        }
                        //new UddiCreatebulk("uddi:another.juddi.apache.org:node2").publishBusiness(null, 1, 1);
                } else if (input.equals("3")) {
                        new JuddiAdminService(clerkManager, null).autoMagicDirected();

                        List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                        for (int i = 0; i < uddiNodeList.size(); i++) {
                                new UddiCreatebulk(uddiNodeList.get(i).getName()).publishBusiness(null, 1, 1, "root@" + uddiNodeList.get(i).getName());
                        }
                } else if (input.equals("4")) {
                        new JuddiAdminService(clerkManager, null).printStatus();
                } else if (input.equals("5")) {
                        List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                        for (Node n : uddiNodeList) {
                                UDDIInquiryPortType uddiInquiryService = clerkManager.getTransport(n.getName()).getUDDIInquiryService();

                                FindBusiness fb = new FindBusiness();

                                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                                fb.setFindQualifiers(new FindQualifiers());
                                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                                fb.setMaxRows(1);
                                fb.setListHead(0);
                                BusinessList findBusiness = uddiInquiryService.findBusiness(fb);
                                System.out.println(n.getName() + " business count "
                                        + findBusiness.getListDescription().getActualCount());
                                FindService fs = new FindService();

                                fs.getName().add(new Name(UDDIConstants.WILDCARD, null));
                                fs.setFindQualifiers(new FindQualifiers());
                                fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                                fs.setMaxRows(1);
                                fs.setListHead(0);
                                ServiceList findService = uddiInquiryService.findService(fs);
                                System.out.println(n.getName() + " service count "
                                        + findService.getListDescription().getActualCount());

                                FindTModel ft = new FindTModel();
                                ft.setName(new Name(UDDIConstants.WILDCARD, null));
                                ft.setFindQualifiers(new FindQualifiers());
                                ft.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                                ft.setMaxRows(1);
                                ft.setListHead(0);
                                TModelList findTModel = uddiInquiryService.findTModel(ft);
                                System.out.println(n.getName() + " tModel count "
                                        + findTModel.getListDescription().getActualCount());

                        }
                        System.out.println();
                } else if (input.equals("6")) {
                        new JuddiAdminService(clerkManager, null).pingAll();
                }

        }

}
