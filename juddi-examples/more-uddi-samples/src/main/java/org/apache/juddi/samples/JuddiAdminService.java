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
package org.apache.juddi.samples;

import java.rmi.RemoteException;
import java.util.List;
import javax.xml.bind.JAXB;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api_v3.DeleteNode;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.NodeList;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.jaxb.PrintJUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class JuddiAdminService {

        private static UDDISecurityPortType security = null;
        private static UDDIPublicationPortType publish = null;
        static JUDDIApiPortType juddi = null;
        static UDDIClerk clerk = null;
        static UDDIClient clerkManager = null;

        public JuddiAdminService() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        clerk = clerkManager.getClerk("default");
                        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        publish = transport.getUDDIPublishService();
                        juddi = transport.getJUDDIApiService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        static Node getCloudInstance() {
                Node n = new Node();
                n.setClientName("juddicloud");
                n.setName("juddicloud");
                n.setCustodyTransferUrl("http://uddi-jbossoverlord.rhcloud.com/services/custody-transfer");
                n.setDescription("juddicloud");
                n.setProxyTransport("org.apache.juddi.v3.client.transport.JAXWSTransport");
                n.setInquiryUrl("http://uddi-jbossoverlord.rhcloud.com/services/inquiry");
                n.setJuddiApiUrl("http://uddi-jbossoverlord.rhcloud.com/services/juddi-api");
                n.setPublishUrl("http://uddi-jbossoverlord.rhcloud.com/services/publish");
                n.setSecurityUrl("http://uddi-jbossoverlord.rhcloud.com/services/security");
                n.setSubscriptionListenerUrl("http://uddi-jbossoverlord.rhcloud.com/services/subscription-listener");
                n.setSubscriptionUrl("http://uddi-jbossoverlord.rhcloud.com/services/subscription");
                n.setReplicationUrl("uddi-jbossoverlord.rhcloud.com/services/replication");
                return n;
        }

        public void quickRegisterRemoteCloud(String token) {
                try {
                        // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
                        // and can save other publishers).
                        SaveNode node = new SaveNode();
                        node.setAuthInfo(token);

                        node.getNode().add(getCloudInstance());

                        PrintJUDDI<SaveNode> p = new PrintJUDDI<SaveNode>();
                        System.out.println("Before sending");
                        System.out.println(p.print(node));
                        juddi.saveNode(node);
                        System.out.println("Saved");

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        void setupReplication() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        void viewRemoteNodes(String authtoken) throws ConfigurationException, TransportException, RemoteException {

                List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                System.out.println();
                System.out.println("Select a node (from *this config)");
                for (int i = 0; i < uddiNodeList.size(); i++) {
                        System.out.print(i + 1);
                        System.out.println(") " + uddiNodeList.get(i).getName() + uddiNodeList.get(i).getDescription());
                }
                System.out.println("Node #: ");
                int index = Integer.parseInt(System.console().readLine()) - 1;
                String node = uddiNodeList.get(index).getName();
                Transport transport = clerkManager.getTransport(node);

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();

                NodeList allNodes = juddiApiService.getAllNodes(authtoken);
                if (allNodes == null || allNodes.getNode().isEmpty()) {
                        System.out.println("No nodes registered!");
                } else {
                        for (int i = 0; i < allNodes.getNode().size(); i++) {
                                System.out.println("_______________________________________________________________________________");
                                System.out.println("Name :" + allNodes.getNode().get(i).getName());
                                System.out.println("Inquiry :" + allNodes.getNode().get(i).getInquiryUrl());
                                System.out.println("Publish :" + allNodes.getNode().get(i).getPublishUrl());
                                System.out.println("Securit :" + allNodes.getNode().get(i).getSecurityUrl());
                                System.out.println("Replication :" + allNodes.getNode().get(i).getReplicationUrl());
                                System.out.println("Subscription :" + allNodes.getNode().get(i).getSubscriptionUrl());
                                System.out.println("Custody Xfer :" + allNodes.getNode().get(i).getCustodyTransferUrl());

                        }
                }

        }

        void quickRegisterLocalCloud() throws ConfigurationException {
                UDDINode node = new UDDINode(getCloudInstance());
                clerkManager.getClientConfig().addUDDINode(node);
                clerkManager.getClientConfig().saveConfig();
                System.out.println();
                System.out.println("Added and saved.");
        }

        void registerLocalNodeToRemoteNode(String authtoken, Node cfg, Node publishTo) throws Exception {

                Transport transport = clerkManager.getTransport(publishTo.getName());

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                SaveNode sn = new SaveNode();
                sn.setAuthInfo(authtoken);
                sn.getNode().add(cfg);
                NodeDetail saveNode = juddiApiService.saveNode(sn);
                JAXB.marshal(saveNode, System.out);
                System.out.println("Success.");

        }

        void viewReplicationConfig(String authtoken) throws Exception {
                List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                System.out.println();
                System.out.println("Select a node (from *this config)");
                for (int i = 0; i < uddiNodeList.size(); i++) {
                        System.out.print(i + 1);
                        System.out.println(") " + uddiNodeList.get(i).getName() + uddiNodeList.get(i).getDescription());
                }
                System.out.println("Node #: ");
                int index = Integer.parseInt(System.console().readLine()) - 1;
                String node = uddiNodeList.get(index).getName();
                Transport transport = clerkManager.getTransport(node);

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                ReplicationConfiguration replicationNodes = juddiApiService.getReplicationNodes(authtoken);

                System.out.println("Current Config:");
                JAXB.marshal(replicationNodes, System.out);

        }

        void setReplicationConfig(String authtoken) throws Exception {
                List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                System.out.println();
                System.out.println("Select a node (from *this config)");
                for (int i = 0; i < uddiNodeList.size(); i++) {
                        System.out.print(i + 1);
                        System.out.println(") " + uddiNodeList.get(i).getName() + uddiNodeList.get(i).getDescription());
                }
                System.out.println("Node #: ");
                int index = Integer.parseInt(System.console().readLine()) - 1;
                String node = uddiNodeList.get(index).getName();
                Transport transport = clerkManager.getTransport(node);

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();

                ReplicationConfiguration replicationNodes = juddiApiService.getReplicationNodes(authtoken);

                String input = "";
                while (!"d".equalsIgnoreCase(input) && !"q".equalsIgnoreCase(input)) {
                        System.out.println("Current Config:");
                        JAXB.marshal(replicationNodes, System.out);
                        System.out.println("1) Remove a replication node");
                        System.out.println("2) Add a replication node");
                        System.out.println("3) Remove an Edge");
                        System.out.println("4) Add an Edge");
                        System.out.println("5) Set Registry Contact");
                        System.out.println("6) Add Operator info");
                        System.out.println("7) Remove Operator info");
                        input = System.console().readLine();
                        if (input.equalsIgnoreCase("1")) {
                                menu_RemoveReplicationNode(replicationNodes);
                        } else if (input.equalsIgnoreCase("2")) {
                                menu_AddReplicationNode(replicationNodes, juddiApiService, authtoken);
                        }

                }
                if (input.equalsIgnoreCase("d")) {
                        //save the changes
                        DispositionReport setReplicationNodes = juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                        System.out.println("Saved!, dumping config from the server");
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                        JAXB.marshal(replicationNodes, System.out);

                } else {
                        //quit this sub menu
                        System.out.println("aborting!");
                }

        }

        void viewRemoveRemoteNode(String authtoken) throws Exception {
                List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                System.out.println();
                System.out.println("Select a node (from *this config)");
                for (int i = 0; i < uddiNodeList.size(); i++) {
                        System.out.print(i + 1);
                        System.out.println(") " + uddiNodeList.get(i).getName() + uddiNodeList.get(i).getDescription());
                }
                System.out.println("Node #: ");
                int index = Integer.parseInt(System.console().readLine()) - 1;
                String node = uddiNodeList.get(index).getName();
                Transport transport = clerkManager.getTransport(node);

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();

                NodeList allNodes = juddiApiService.getAllNodes(authtoken);
                if (allNodes == null || allNodes.getNode().isEmpty()) {
                        System.out.println("No nodes registered!");
                } else {
                        for (int i = 0; i < allNodes.getNode().size(); i++) {
                                System.out.println("_______________________________________________________________________________");
                                System.out.println("(" + i + ") Name :" + allNodes.getNode().get(i).getName());
                                System.out.println("(" + i + ") Inquiry :" + allNodes.getNode().get(i).getInquiryUrl());

                        }

                        System.out.println("Node to remove from : ");
                        int nodenum = Integer.parseInt(System.console().readLine());
                        juddiApiService.deleteNode(new DeleteNode(authtoken, allNodes.getNode().get(nodenum).getName()));

                }
        }

        private void menu_RemoveReplicationNode(ReplicationConfiguration replicationNodes) {
                if (replicationNodes.getCommunicationGraph() == null) {
                        replicationNodes.setCommunicationGraph(new CommunicationGraph());
                }
                for (int i = 0; i < replicationNodes.getCommunicationGraph().getNode().size(); i++) {
                        System.out.println((i + 1) + ") " + replicationNodes.getCommunicationGraph().getNode().get(i));
                }
                System.out.println("Node #: ");
                int index = Integer.parseInt(System.console().readLine()) - 1;
                replicationNodes.getCommunicationGraph().getNode().remove(index);

        }

        private void menu_AddReplicationNode(ReplicationConfiguration replicationNodes, JUDDIApiPortType juddiApiService, String authtoken) throws Exception {

                NodeList allNodes = juddiApiService.getAllNodes(authtoken);
                if (allNodes == null || allNodes.getNode().isEmpty()) {
                        System.out.println("No nodes registered!");
                } else {
                        for (int i = 0; i < allNodes.getNode().size(); i++) {
                                System.out.println((i + 1) + ") Name :" + allNodes.getNode().get(i).getName());
                                System.out.println((i + 1) + ") Replication :" + allNodes.getNode().get(i).getReplicationUrl());

                        }
                        System.out.println("Node #: ");
                        int index = Integer.parseInt(System.console().readLine()) - 1;
                        replicationNodes.getCommunicationGraph().getNode().add(allNodes.getNode().get(index).getName());
                }

        }
}
