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

import java.io.FileInputStream;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXB;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api_v3.DeleteNode;
import org.apache.juddi.api_v3.GetFailedReplicationChangeRecordsMessageRequest;
import org.apache.juddi.api_v3.GetFailedReplicationChangeRecordsMessageResponse;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.NodeList;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.jaxb.PrintJUDDI;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.client.cryptor.TransportSecurityHelper;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.Email;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.Phone;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.CommunicationGraph.Edge;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.Operator;
import org.uddi.repl_v3.OperatorStatusType;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.v3_service.UDDIReplicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class JuddiAdminService {

        private JUDDIApiPortType juddi = null;
        private UDDIClient clerkManager = null;

        public JuddiAdminService(UDDIClient client, Transport transport) {
                try {
                        clerkManager = client;
                        if (transport != null) // create a manager and read the config in the archive; 
                        // you can use your config file name
                        // clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        //clerk = clerkManager.getClerk("default");
                        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                        {
                                transport = clerkManager.getTransport();
                                juddi = transport.getJUDDIApiService();
                        }
                        // Now you create a reference to the UDDI API
                        //security = transport.getUDDISecurityService();
                        //publish = transport.getUDDIPublishService();

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

        JuddiAdminService(UDDIClient client, Transport transport, String currentNode) {
                curentnode = currentNode;
                try {
                        clerkManager = client;
                        if (transport == null) // create a manager and read the config in the archive; 
                        // you can use your config file name
                        // clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        //clerk = clerkManager.getClerk("default");
                        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                        {
                                transport = clerkManager.getTransport();
                        }
                        // Now you create a reference to the UDDI API
                        //security = transport.getUDDISecurityService();
                        //publish = transport.getUDDIPublishService();
                        juddi = transport.getJUDDIApiService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        String curentnode = "default";

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

                UDDISecurityPortType security2 = transport.getUDDISecurityService();
                System.out.print("username: ");
                String uname = System.console().readLine();
                char passwordArray[] = System.console().readPassword("password: ");
                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                getAuthTokenRoot.setUserID(uname);
                getAuthTokenRoot.setCred(new String(passwordArray));
                authtoken = security2.getAuthToken(getAuthTokenRoot).getAuthInfo();
                System.out.println("Success!");

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                SaveNode sn = new SaveNode();
                sn.setAuthInfo(authtoken);
                sn.getNode().add(cfg);
                NodeDetail saveNode = juddiApiService.saveNode(sn);
                JAXB.marshal(saveNode, System.out);
                System.out.println("Success.");

        }

        void viewReplicationConfig(String authtoken, String node) throws Exception {

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
                System.out.println("fetching...");
                //NodeList allNodes = juddiApiService.getAllNodes(authtoken);
                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
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
                        System.out.println("d) Done, save changes");
                        System.out.println("q) Quit and abandon changes");

                        input = System.console().readLine();
                        if (input.equalsIgnoreCase("1")) {
                                menu_RemoveReplicationNode(replicationNodes);
                        } else if (input.equalsIgnoreCase("2")) {
                                menu_AddReplicationNode(replicationNodes, juddiApiService, authtoken);
                        } else if (input.equalsIgnoreCase("d")) {
                                if (replicationNodes.getCommunicationGraph() == null) {
                                        replicationNodes.setCommunicationGraph(new CommunicationGraph());
                                }
                                if (replicationNodes.getRegistryContact() == null) {
                                        replicationNodes.setRegistryContact(new ReplicationConfiguration.RegistryContact());
                                }
                                if (replicationNodes.getRegistryContact().getContact() == null) {
                                        replicationNodes.getRegistryContact().setContact(new Contact());
                                        replicationNodes.getRegistryContact().getContact().getPersonName().add(new PersonName("unknown", null));
                                }

                                replicationNodes.setSerialNumber(0L);
                                replicationNodes.setTimeOfConfigurationUpdate("");
                                replicationNodes.setMaximumTimeToGetChanges(BigInteger.ONE);
                                replicationNodes.setMaximumTimeToSyncRegistry(BigInteger.ONE);

                                JAXB.marshal(replicationNodes, System.out);
                                juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                        }

                }
                if (input.equalsIgnoreCase("d")) {
                        //save the changes
                        juddiApiService.setReplicationNodes(authtoken, replicationNodes);
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

                if (replicationNodes.getCommunicationGraph() == null) {
                        replicationNodes.setCommunicationGraph(new CommunicationGraph());
                }

                Operator op = new Operator();
                System.out.println("The Operator Node id should be the UDDI Node ID of the new node to replicate with. It should also match the root business key in"
                        + " the new registry.");
                System.out.print("Operator Node id: ");
                op.setOperatorNodeID(System.console().readLine().trim());

                System.out.print("Replication URL: ");
                op.setSoapReplicationURL(System.console().readLine().trim());
                op.setOperatorStatus(OperatorStatusType.NEW);
                System.out.println("The replication node requires at least one point of contact");
                System.out.print("Number of contacts: ");
                int index = Integer.parseInt(System.console().readLine());
                for (int i = 0; i < index; i++) {
                        System.out.println("_______________________");
                        System.out.println("Info for contact # " + (i + 1));
                        op.getContact().add(getContactInfo());
                }
                System.out.println("_______________________");
                System.out.println("Current Operator:");
                System.out.println("_______________________");
                JAXB.marshal(op, System.out);

                System.out.print("Confirm adding? (y/n) :");
                if (System.console().readLine().trim().equalsIgnoreCase("y")) {
                        replicationNodes.getOperator().add(op);
                        replicationNodes.getCommunicationGraph().getNode().add(op.getOperatorNodeID());

                }
        }

        private Contact getContactInfo() {
                Contact c = new Contact();
                System.out.print("Use Type (i.e. primary): ");
                c.setUseType(System.console().readLine().trim());
                if (c.getUseType().trim().equalsIgnoreCase("")) {
                        c.setUseType("primary");
                }

                c.getPersonName().add(getPersonName());

                while (true) {
                        System.out.println("Thus far:");
                        JAXB.marshal(c, System.out);
                        System.out.println("Options:");
                        System.out.println("\t1) Add PersonName");
                        System.out.println("\t2) Add Email address");
                        System.out.println("\t3) Add Phone number");
                        System.out.println("\t4) Add Description");
                        System.out.println("\t<enter> Finish and return");

                        System.out.print("> ");
                        String input = System.console().readLine();
                        if (input.trim().equalsIgnoreCase("")) {
                                break;
                        } else if (input.trim().equalsIgnoreCase("1")) {
                                c.getPersonName().add(getPersonName());
                        } else if (input.trim().equalsIgnoreCase("2")) {
                                c.getEmail().add(getEmail());
                        } else if (input.trim().equalsIgnoreCase("3")) {
                                c.getPhone().add(getPhoneNumber());
                        } else if (input.trim().equalsIgnoreCase("4")) {
                                c.getDescription().add(getDescription());
                        }
                }
                return c;
        }

        private PersonName getPersonName() {
                PersonName pn = new PersonName();
                System.out.print("Name: ");
                pn.setValue(System.console().readLine().trim());
                System.out.print("Language (en): ");
                pn.setLang(System.console().readLine().trim());
                if (pn.getLang().equalsIgnoreCase("")) {
                        pn.setLang("en");
                }

                return pn;
        }

        private Email getEmail() {
                Email pn = new Email();
                System.out.print("Email address Value: ");

                pn.setValue(System.console().readLine().trim());
                System.out.print("Use type (i.e. primary): ");
                pn.setUseType(System.console().readLine().trim());
                return pn;
        }

        private Phone getPhoneNumber() {

                Phone pn = new Phone();
                System.out.print("Phone Value: ");

                pn.setValue(System.console().readLine().trim());
                System.out.print("Use type (i.e. primary): ");
                pn.setUseType(System.console().readLine().trim());
                return pn;
        }

        private Description getDescription() {
                Description pn = new Description();
                System.out.print("Value: ");

                pn.setValue(System.console().readLine().trim());
                System.out.print("Language (en): ");
                pn.setLang(System.console().readLine().trim());
                if (pn.getLang().equalsIgnoreCase("")) {
                        pn.setLang("en");
                }
                return pn;
        }

        void autoMagic123() throws Exception {

                //1 > 2 >3 >1
                Transport transport = clerkManager.getTransport("default");
                String authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                System.out.println("fetching...");

                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
                if (replicationNodes.getCommunicationGraph() == null) {
                        replicationNodes.setCommunicationGraph(new CommunicationGraph());
                }
                Operator op = new Operator();
                op.setOperatorNodeID("uddi:juddi.apache.org:node1");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("default").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("bob", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().clear();
                replicationNodes.getOperator().add(op);

                op = new Operator();
                op.setOperatorNodeID("uddi:another.juddi.apache.org:node2");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:another.juddi.apache.org:node2").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);
                op = new Operator();

                op.setOperatorNodeID("uddi:yet.another.juddi.apache.org:node3");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:yet.another.juddi.apache.org:node3").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);

                replicationNodes.getCommunicationGraph().getNode().add("uddi:another.juddi.apache.org:node2");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:juddi.apache.org:node1");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:yet.another.juddi.apache.org:node3");
                replicationNodes.setSerialNumber(0L);
                replicationNodes.setTimeOfConfigurationUpdate("");
                replicationNodes.setMaximumTimeToGetChanges(BigInteger.ONE);
                replicationNodes.setMaximumTimeToSyncRegistry(BigInteger.ONE);

                if (replicationNodes.getRegistryContact().getContact() == null) {
                        replicationNodes.getRegistryContact().setContact(new Contact());
                        replicationNodes.getRegistryContact().getContact().getPersonName().add(new PersonName("unknown", null));
                }

                JAXB.marshal(replicationNodes, System.out);
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

                transport = clerkManager.getTransport("uddi:another.juddi.apache.org:node2");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

                transport = clerkManager.getTransport("uddi:yet.another.juddi.apache.org:node3");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

        }

        void autoMagicDirected() throws Exception {

                //1 > 2 >3 >1
                Transport transport = clerkManager.getTransport("default");
                String authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                System.out.println("fetching...");

                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
                if (replicationNodes.getCommunicationGraph() == null) {
                        replicationNodes.setCommunicationGraph(new CommunicationGraph());
                }
                Operator op = new Operator();
                op.setOperatorNodeID("uddi:juddi.apache.org:node1");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("default").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("bob", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().clear();
                replicationNodes.getOperator().add(op);

                op = new Operator();
                op.setOperatorNodeID("uddi:another.juddi.apache.org:node2");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:another.juddi.apache.org:node2").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);
                op = new Operator();

                op.setOperatorNodeID("uddi:yet.another.juddi.apache.org:node3");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:yet.another.juddi.apache.org:node3").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);
                replicationNodes.getCommunicationGraph().getNode().clear();
                replicationNodes.getCommunicationGraph().getNode().add("uddi:another.juddi.apache.org:node2");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:juddi.apache.org:node1");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:yet.another.juddi.apache.org:node3");
                replicationNodes.getCommunicationGraph().getEdge().clear();
                Edge e = new CommunicationGraph.Edge();
                e.setMessageSender("uddi:juddi.apache.org:node1");
                e.setMessageReceiver("uddi:another.juddi.apache.org:node2");
                replicationNodes.getCommunicationGraph().getEdge().add(e);

                e = new CommunicationGraph.Edge();
                e.setMessageSender("uddi:another.juddi.apache.org:node2");
                e.setMessageReceiver("uddi:yet.another.juddi.apache.org:node3");
                replicationNodes.getCommunicationGraph().getEdge().add(e);

                e = new CommunicationGraph.Edge();
                e.setMessageSender("uddi:yet.another.juddi.apache.org:node3");
                e.setMessageReceiver("uddi:juddi.apache.org:node1");
                replicationNodes.getCommunicationGraph().getEdge().add(e);

                replicationNodes.setSerialNumber(0L);
                replicationNodes.setTimeOfConfigurationUpdate("");
                replicationNodes.setMaximumTimeToGetChanges(BigInteger.ONE);
                replicationNodes.setMaximumTimeToSyncRegistry(BigInteger.ONE);

                if (replicationNodes.getRegistryContact().getContact() == null) {
                        replicationNodes.getRegistryContact().setContact(new Contact());
                        replicationNodes.getRegistryContact().getContact().getPersonName().add(new PersonName("unknown", null));
                }

                JAXB.marshal(replicationNodes, System.out);

                juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                System.out.println("Saved to node1");
                TestEquals(replicationNodes, juddiApiService.getReplicationNodes(authtoken));

                transport = clerkManager.getTransport("uddi:another.juddi.apache.org:node2");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();
                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                System.out.println("Saved to node2");
                TestEquals(replicationNodes, juddiApiService.getReplicationNodes(authtoken));

                transport = clerkManager.getTransport("uddi:yet.another.juddi.apache.org:node3");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();
                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                System.out.println("Saved to node3");
                TestEquals(replicationNodes, juddiApiService.getReplicationNodes(authtoken));

        }

        void autoMagic() throws Exception {

                Transport transport = clerkManager.getTransport("default");
                String authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                System.out.println("fetching...");

                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
                //if (replicationNodes.getCommunicationGraph() == null) {
                replicationNodes.setCommunicationGraph(new CommunicationGraph());
                //}
                Operator op = new Operator();
                op.setOperatorNodeID("uddi:juddi.apache.org:node1");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("default").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("bob", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().clear();
                replicationNodes.getOperator().add(op);

                op = new Operator();
                op.setOperatorNodeID("uddi:another.juddi.apache.org:node2");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:another.juddi.apache.org:node2").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);
                replicationNodes.getCommunicationGraph().getNode().add("uddi:another.juddi.apache.org:node2");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:juddi.apache.org:node1");
                replicationNodes.setSerialNumber(0L);
                replicationNodes.setTimeOfConfigurationUpdate("");
                replicationNodes.setMaximumTimeToGetChanges(BigInteger.ONE);
                replicationNodes.setMaximumTimeToSyncRegistry(BigInteger.ONE);

                if (replicationNodes.getRegistryContact().getContact() == null) {
                        replicationNodes.getRegistryContact().setContact(new Contact());
                        replicationNodes.getRegistryContact().getContact().getPersonName().add(new PersonName("unknown", null));
                }

                JAXB.marshal(replicationNodes, System.out);
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

                transport = clerkManager.getTransport("uddi:another.juddi.apache.org:node2");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

        }

        void autoMagic3() throws Exception {

                Transport transport = clerkManager.getTransport("default");
                String authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                System.out.println("fetching...");

                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
                if (replicationNodes.getCommunicationGraph() == null) {
                        replicationNodes.setCommunicationGraph(new CommunicationGraph());
                }
                Operator op = new Operator();
                op.setOperatorNodeID("uddi:juddi.apache.org:node1");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("default").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("bob", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().clear();
                replicationNodes.getOperator().add(op);

                op = new Operator();
                op.setOperatorNodeID("uddi:another.juddi.apache.org:node2");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:another.juddi.apache.org:node2").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);

                op = new Operator();
                op.setOperatorNodeID("uddi:yet.another.juddi.apache.org:node3");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:yet.another.juddi.apache.org:node3").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);

                replicationNodes.getCommunicationGraph().getNode().add("uddi:another.juddi.apache.org:node2");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:yet.another.juddi.apache.org:node3");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:juddi.apache.org:node1");
                replicationNodes.setSerialNumber(0L);
                replicationNodes.setTimeOfConfigurationUpdate("");
                replicationNodes.setMaximumTimeToGetChanges(BigInteger.ONE);
                replicationNodes.setMaximumTimeToSyncRegistry(BigInteger.ONE);

                if (replicationNodes.getRegistryContact().getContact() == null) {
                        replicationNodes.getRegistryContact().setContact(new Contact());
                        replicationNodes.getRegistryContact().getContact().getPersonName().add(new PersonName("unknown", null));
                }

                JAXB.marshal(replicationNodes, System.out);
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

                transport = clerkManager.getTransport("uddi:another.juddi.apache.org:node2");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

                transport = clerkManager.getTransport("uddi:yet.another.juddi.apache.org:node3");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);

        }

        void printStatus(Transport transport, String authtoken) throws Exception {

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                System.out.println("fetching...");

                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
                UDDIReplicationPortType uddiReplicationPort = new UDDIService().getUDDIReplicationPort();

                for (Operator o : replicationNodes.getOperator()) {
                        System.out.println("*******************\n\rstats for node " + o.getOperatorNodeID());
                        ((BindingProvider) uddiReplicationPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, o.getSoapReplicationURL());

                        List<ChangeRecordIDType> highWaterMarks = uddiReplicationPort.getHighWaterMarks();
                        for (ChangeRecordIDType cr : highWaterMarks) {
                                JAXB.marshal(cr, System.out);
                        }
                }
        }

        void printStatus() throws Exception {

                //List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                Transport transport = clerkManager.getTransport("default");
                String authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                System.out.println("fetching...");

                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
                UDDIReplicationPortType uddiReplicationPort = new UDDIService().getUDDIReplicationPort();

                for (Operator o : replicationNodes.getOperator()) {
                        System.out.println("*******************\n\rstats for node " + o.getOperatorNodeID());
                        ((BindingProvider) uddiReplicationPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, o.getSoapReplicationURL());

                        List<ChangeRecordIDType> highWaterMarks = uddiReplicationPort.getHighWaterMarks();
                        for (ChangeRecordIDType cr : highWaterMarks) {
                                JAXB.marshal(cr, System.out);
                        }
                }

        }

        private void TestEquals(ReplicationConfiguration setter, ReplicationConfiguration getter) {
                if (getter == null) {
                        l("null getter");
                        return;
                }
                if (setter == null) {
                        l("null setter");
                        return;
                }
                if (getter.getOperator().size() != setter.getOperator().size()) {
                        l("operator size mismatch");
                        return;
                }
                if (getter.getCommunicationGraph().getNode().size() != setter.getCommunicationGraph().getNode().size()) {
                        l("comm/node size mismatch");
                        return;
                }
                if (getter.getCommunicationGraph().getEdge().size() != setter.getCommunicationGraph().getEdge().size()) {
                        l("comm/node size mismatch");
                        return;
                }

        }

        private void l(String msg) {
                System.out.println(msg);
        }

        void dumpFailedReplicationRecords(String authtoken) throws Exception {
                GetFailedReplicationChangeRecordsMessageRequest req = new GetFailedReplicationChangeRecordsMessageRequest();
                req.setAuthInfo(authtoken);
                req.setMaxRecords(20);
                req.setOffset(0);
                GetFailedReplicationChangeRecordsMessageResponse failedReplicationChangeRecords = juddi.getFailedReplicationChangeRecords(req);
                while (failedReplicationChangeRecords != null
                        && failedReplicationChangeRecords.getChangeRecords() != null
                        && !failedReplicationChangeRecords.getChangeRecords().getChangeRecord().isEmpty()) {
                        for (int i = 0; i < failedReplicationChangeRecords.getChangeRecords().getChangeRecord().size(); i++) {
                                JAXB.marshal(failedReplicationChangeRecords.getChangeRecords().getChangeRecord().get(i), System.out);
                        }
                        req.setOffset(req.getOffset() + failedReplicationChangeRecords.getChangeRecords().getChangeRecord().size());
                        failedReplicationChangeRecords = juddi.getFailedReplicationChangeRecords(req);
                }
        }

        void printStatusSingleNode(Transport transport, String authtoken) throws Exception {
                String replicationUrl = clerkManager.getClientConfig().getUDDINode(curentnode).getReplicationUrl();

                SSLContext sc = SSLContext.getInstance("SSLv3");

                KeyManagerFactory kmf
                        = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                FileInputStream fis = null;
                try {
                        fis = new FileInputStream(System.getProperty("javax.net.ssl.keyStore"));
                        ks.load(fis, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
                } catch (Exception ex) {

                } finally {
                        if (fis != null) {
                                fis.close();
                        }
                }

                kmf.init(ks, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());

                sc.init(kmf.getKeyManagers(), null, null);

                UDDIReplicationPortType uddiReplicationPort = new UDDIService().getUDDIReplicationPort();
                ((BindingProvider) uddiReplicationPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, replicationUrl);
                ((BindingProvider) uddiReplicationPort).getRequestContext()
                        .put(
                                "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory",
                                sc.getSocketFactory());
                /*((BindingProvider) uddiReplicationPort).getRequestContext()
                 .put(
                 JAXWSProperties.SSL_SOCKET_FACTORY,
                 sc.getSocketFactory());*/

                String doPing = uddiReplicationPort.doPing(new DoPing());
                System.out.println(doPing + ".., success");

        }

        void autoMagicDirectedSSL() throws Exception {
                //1 > 2 >3 >1

                Transport transport = clerkManager.getTransport("default");
                String authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();

                JUDDIApiPortType juddiApiService = transport.getJUDDIApiService();
                System.out.println("fetching...");

                ReplicationConfiguration replicationNodes = null;
                try {
                        replicationNodes = juddiApiService.getReplicationNodes(authtoken);
                } catch (Exception ex) {
                        System.out.println("Error getting replication config");
                        ex.printStackTrace();
                        replicationNodes = new ReplicationConfiguration();

                }
                //if (replicationNodes.getCommunicationGraph() == null) {
                replicationNodes.setCommunicationGraph(new CommunicationGraph());
                //}
                Operator op = new Operator();
                op.setOperatorNodeID("uddi:juddi.apache.org:node1");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("default").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("bob", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().clear();
                replicationNodes.getOperator().add(op);

                op = new Operator();
                op.setOperatorNodeID("uddi:another.juddi.apache.org:node2");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:another.juddi.apache.org:node2").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);
                op = new Operator();

                op.setOperatorNodeID("uddi:yet.another.juddi.apache.org:node3");
                op.setSoapReplicationURL(clerkManager.getClientConfig().getUDDINode("uddi:yet.another.juddi.apache.org:node3").getReplicationUrl());
                op.setOperatorStatus(OperatorStatusType.NORMAL);
                op.getContact().add(new Contact());
                op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                op.getContact().get(0).setUseType("admin");
                replicationNodes.getOperator().add(op);
                replicationNodes.getCommunicationGraph().getNode().clear();
                replicationNodes.getCommunicationGraph().getNode().add("uddi:another.juddi.apache.org:node2");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:juddi.apache.org:node1");
                replicationNodes.getCommunicationGraph().getNode().add("uddi:yet.another.juddi.apache.org:node3");
                replicationNodes.getCommunicationGraph().getEdge().clear();
                Edge e = new CommunicationGraph.Edge();
                e.setMessageSender("uddi:juddi.apache.org:node1");
                e.setMessageReceiver("uddi:another.juddi.apache.org:node2");
                replicationNodes.getCommunicationGraph().getEdge().add(e);

                e = new CommunicationGraph.Edge();
                e.setMessageSender("uddi:another.juddi.apache.org:node2");
                e.setMessageReceiver("uddi:yet.another.juddi.apache.org:node3");
                replicationNodes.getCommunicationGraph().getEdge().add(e);

                e = new CommunicationGraph.Edge();
                e.setMessageSender("uddi:yet.another.juddi.apache.org:node3");
                e.setMessageReceiver("uddi:juddi.apache.org:node1");
                replicationNodes.getCommunicationGraph().getEdge().add(e);

                replicationNodes.setSerialNumber(0L);
                replicationNodes.setTimeOfConfigurationUpdate("");
                replicationNodes.setMaximumTimeToGetChanges(BigInteger.ONE);
                replicationNodes.setMaximumTimeToSyncRegistry(BigInteger.ONE);

                if (replicationNodes.getRegistryContact().getContact() == null) {
                        replicationNodes.getRegistryContact().setContact(new Contact());
                        replicationNodes.getRegistryContact().getContact().getPersonName().add(new PersonName("unknown", null));
                }

                JAXB.marshal(replicationNodes, System.out);

                juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                System.out.println("Saved to node1");
                TestEquals(replicationNodes, juddiApiService.getReplicationNodes(authtoken));

                transport = clerkManager.getTransport("uddi:another.juddi.apache.org:node2");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();
                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                System.out.println("Saved to node2");
                TestEquals(replicationNodes, juddiApiService.getReplicationNodes(authtoken));

                transport = clerkManager.getTransport("uddi:yet.another.juddi.apache.org:node3");
                authtoken = transport.getUDDISecurityService().getAuthToken(new GetAuthToken("root", "root")).getAuthInfo();
                juddiApiService = transport.getJUDDIApiService();
                juddiApiService.setReplicationNodes(authtoken, replicationNodes);
                System.out.println("Saved to node3");
                TestEquals(replicationNodes, juddiApiService.getReplicationNodes(authtoken));
        }

        void pingAll() throws Exception {
                List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();

                UDDIReplicationPortType uddiReplicationPort = new UDDIService().getUDDIReplicationPort();
                TransportSecurityHelper.applyTransportSecurity((BindingProvider) uddiReplicationPort);

                for (Node currenteNode : uddiNodeList) {
                        try {
                                String replicationUrl = clerkManager.getClientConfig().getUDDINode(currenteNode.getName()).getReplicationUrl();
                                ((BindingProvider) uddiReplicationPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, replicationUrl);
                                long now = System.currentTimeMillis();
                                String doPing = uddiReplicationPort.doPing(new DoPing());
                                System.out.println(replicationUrl + " " + currenteNode.getName() + " says it's node id is " + doPing + " (took " + (System.currentTimeMillis() - now) + "ms)");
                        } catch (Exception ex) {
                                System.out.println("Error! " + currenteNode.getName() + " " + currenteNode.getReplicationUrl());
                                ex.printStackTrace();
                        }

                }

        }

}
