/*
 * Copyright 2014 The Apache Software Foundation.
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
import static org.apache.juddi.samples.JuddiAdminService.clerkManager;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class EntryPoint {

        public static void main(String[] args) throws Exception {
                String input = null;
                do {
                        System.out.println("____________________________");
                        System.out.println("Interactive Samples for UDDI");
                        System.out.println("____________________________");
                        System.out.println(" 1) Login (get an auth token)");
                        System.out.println(" 2) Compare Two Binding/tModelInstanceInfo - QOS Example");
                        System.out.println(" 3) jUDDI Admin service - Register a Node (quick add of the jUDDI cloud server)");
                        System.out.println(" 4) Find Binding by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println(" 4.4) Find Business by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println(" 5) Find Service by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println(" 6) UDDI Create Bulk (makes N business/services");
                        System.out.println(" 7) UDDI Custody Transfer (within a single node)");
                        System.out.println(" 8) UDDI Digital Signatures - Sign a Business");
                        System.out.println(" 9) UDDI Digital Signatures - Sign a Service");
                        System.out.println("10) UDDI Digital Signatures - Sign a tModel");
                        System.out.println("11) UDDI Digital Signatures - Search for Signed Items");
                        System.out.println("12) Find a Binding, lists all bindings for all services");
                        System.out.println("13) Find Endpoints of a service (given the key)");
                        System.out.println("14) Get the details of a service");
                        System.out.println("15) Make a Key Generator tModel");
                        System.out.println("16) Create a Business Relationship Between two users and two Businesses");
                        System.out.println("17) Subscriptions - Asynchronous, listens for all changes");
                        System.out.println("18) Subscriptions - Synchronous");
                        System.out.println("19) WSDL2UDDI - Register a service from a WSDL document");
                        System.out.println("20) WADL2UDDI - Register a service from a WADL document");
                        System.out.println("21) Logout (discard auth token)");
                        System.out.println("22) Print auth token");
                        System.out.println("23) Print Subscriptions");
                        System.out.println("24) Delete a subscription");
                        System.out.println("25) Delete all subscriptions");
                        System.out.println("26) Subscriptions - Asynchronous, publisher assertion subscriptions");
                        System.out.println("27) Replication - do_ping");
                        System.out.println("28) Replication - get high watermarks");
                        System.out.println("29) Replication - get change records");
                        System.out.println("30) Replication - Setup replication between two nodes");
                        System.out.println("31) Quick add the jUDDI cloud node to *this's configuration file");
                        System.out.println("32) Add a node to *this's configuration file");
                        System.out.println("33) Register a *this node to a jUDDI server");
                        System.out.println("34) View all registered remote nodes on a jUDDI server");
                        System.out.println("35) View all registered nodes for this client");
                        System.out.println("36) UnRegister a node on a jUDDI server");
                        System.out.println("37) Fetch the replication config from a jUDDI server");
                        System.out.println("38) Set the replication config on a remote jUDDI server");
                        System.out.println("magic) Sets the replication between two instances of jUDDI on 8080 and 8090");
                        System.out.println("rep) Prints the current replication status of a given node");
                        System.out.println("39) Digitally sign a UDDI entity from a file.");

                        System.out.println("q) quit");
                        System.out.print("Selection: ");
                        input = System.console().readLine();
                        try {
                                processInput(input);
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                } while (!input.equalsIgnoreCase("q"));
        }
        private static String authtoken = null;

        private static void processInput(String input) throws Exception {
                if (input == null) {
                        return;
                }
                if (input.equals("1")) {
                        UDDISecurityPortType security = null;
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
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
                        security = transport.getUDDISecurityService();
                        System.out.print("username: ");
                        String uname = System.console().readLine();
                        char passwordArray[] = System.console().readPassword("password: ");
                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(uname);
                        getAuthTokenRoot.setCred(new String(passwordArray));
                        authtoken = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                        System.out.println("Success!");
                }
                if (input.equals("2")) {
                        CompareByTModelInstanceInfoQOS.main(null);
                }
                if (input.equals("3")) {
                        new JuddiAdminService().quickRegisterRemoteCloud(authtoken);
                }
                if (input.equals("4")) {
                        SearchByQos.doFindBinding(authtoken);
                }
                 if (input.equals("4.5")) {
                        SearchByQos.doFindBusiness(authtoken);
                }
                if (input.equals("5")) {
                        SearchByQos.doFindService(authtoken);
                }
                if (input.equals("6")) {
                        System.out.print("businesses: ");
                        int biz = Integer.parseInt(System.console().readLine());
                        System.out.print("servicesPerBusiness: ");
                        int svc = Integer.parseInt(System.console().readLine());
                        new UddiCreatebulk("default").publishBusiness(authtoken, biz, svc);
                }
                if (input.equals("7")) {
                        UDDISecurityPortType security = null;
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        security = transport.getUDDISecurityService();

                        System.out.print("Transfer from username: ");
                        String uname = (System.console().readLine());
                        char passwordArray[] = System.console().readPassword("password: ");
                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(uname);
                        getAuthTokenRoot.setCred(new String(passwordArray));
                        String authtokenFrom = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                        System.out.println("Success!");

                        System.out.print("Transfer to username: ");
                        String uname2 = (System.console().readLine());
                        char passwordArray2[] = System.console().readPassword("password: ");
                        getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(uname2);
                        getAuthTokenRoot.setCred(new String(passwordArray2));
                        String authtokenFrom2 = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                        System.out.println("Success!");
                        System.out.print("business/tModel key to transfer: ");
                        String key = (System.console().readLine());
                        new UddiCustodyTransfer().TransferBusiness(uname, authtokenFrom, uname2, authtokenFrom2, key);
                }
                if (input.equals("8")) {
                        System.out.print("Business key to sign: ");
                        String key = (System.console().readLine());
                        new UddiDigitalSignatureBusiness().Fire(authtoken, key);
                }
                if (input.equals("9")) {
                        System.out.print("Service key to sign: ");
                        String key = (System.console().readLine());
                        new UddiDigitalSignatureService().Fire(authtoken, key);
                }
                if (input.equals("10")) {
                        System.out.print("tModel key to sign: ");
                        String key = (System.console().readLine());
                        new UddiDigitalSignatureTmodel().Fire(authtoken, key);
                }
                if (input.equals("11")) {
                        new UddiDigitalSignatureSearch().Fire(authtoken);
                }
                if (input.equals("12")) {
                        new UddiFindBinding().Fire(authtoken);
                }
                if (input.equals("13")) {
                        System.out.print("Service key to parse the endpoints: ");
                        String key = (System.console().readLine());
                        new UddiFindEndpoints().Fire(authtoken, key);
                }
                if (input.equals("14")) {
                        System.out.print("Service key: ");
                        String key = (System.console().readLine());
                        new UddiGetServiceDetails().Fire(authtoken, key);
                }
                if (input.equals("15")) {
                        System.out.print("Get FQDN: ");
                        String key = (System.console().readLine());
                        new UddiKeyGenerator().Fire(authtoken, key);
                }
                if (input.equals("16")) {
                        UDDISecurityPortType security = null;
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        security = transport.getUDDISecurityService();

                        System.out.print("1st Business username: ");
                        String uname = (System.console().readLine());
                        char passwordArray[] = System.console().readPassword("password: ");
                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(uname);
                        getAuthTokenRoot.setCred(new String(passwordArray));
                        String authtokenFrom = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                        System.out.println("Success!");

                        System.out.print(uname + "'s business : ");
                        String key = (System.console().readLine());

                        System.out.print("2st Business username: ");
                        String uname2 = (System.console().readLine());
                        char passwordArray2[] = System.console().readPassword("password: ");
                        getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(uname2);
                        getAuthTokenRoot.setCred(new String(passwordArray2));
                        String authtokenFrom2 = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                        System.out.println("Success!");

                        System.out.print(uname2 + "'s business : ");
                        String key2 = (System.console().readLine());

                        System.out.print("relationship (parent-child, peer-peer,or identity) : ");
                        String relationship = (System.console().readLine());
                        new UddiRelatedBusinesses().Fire(key, authtokenFrom, key2, authtokenFrom2, relationship);
                }
                if (input.equals("17")) {
                        new UddiSubscribe().Fire();

                }
                if (input.equals("18")) {
                        System.out.print("Subscription key: ");
                        String key = (System.console().readLine());
                        System.out.println("Fetching results for the past 30 days...");
                        new UddiSubscribeValidate().go(authtoken, input);
                }

                if (input.equals("19")) {
                        System.out.print("Path or URL to WSDL file: ");
                        String url = (System.console().readLine());
                        System.out.print("Business key to attach to: ");
                        String key = (System.console().readLine());
                        new WsdlImport().Fire(url, key, authtoken);
                }
                if (input.equals("20")) {
                        System.out.print("Path or URL to WADL file: ");
                        String url = (System.console().readLine());
                        System.out.print("Business key to attach to: ");
                        String key = (System.console().readLine());
                        new WadlImport().Fire(url, key, authtoken);
                }
                if (input.equals("21")) {
                        UDDISecurityPortType security = null;
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        security = transport.getUDDISecurityService();

                        DiscardAuthToken getAuthTokenRoot = new DiscardAuthToken();
                        getAuthTokenRoot.setAuthInfo(authtoken);
                        security.discardAuthToken(getAuthTokenRoot);
                        System.out.println("Success!");
                }

                if (input.equals("22")) {
                        System.out.println("Token info: " + authtoken);
                }
                if (input.equals("23")) {
                        new UddiSubscriptionManagement().PrintSubscriptions(authtoken);
                }
                if (input.equals("24")) {
                        System.out.print("Subscription key: ");
                        String key2 = (System.console().readLine());
                        new UddiSubscriptionManagement().DeleteSubscription(authtoken, key2);
                }
                if (input.equals("25")) {
                        new UddiSubscriptionManagement().DeleteAllSubscriptions(authtoken);
                }
                if (input.equals("26")) {
                        new UddiSubscribeAssertionStatus().Fire();

                }
                if (input.equals("27")) {
                        //System.out.println("27) Replication - do_ping");

                        System.out.print("URL: ");
                        String key2 = (System.console().readLine());
                        new UddiReplication().DoPing(key2);

                }
                if (input.equals("28")) {
                        //System.out.println("28) Replication - get high watermarks");
                        System.out.print("URL: ");
                        String key2 = (System.console().readLine());
                        new UddiReplication().GetHighWatermarks(key2);

                }
                if (input.equals("29")) {
                        //System.out.println("29) Replication - get change records");
                        System.out.print("URL: ");
                        String key2 = (System.console().readLine());

                        System.out.print("Change ID to fetch: ");
                        String id = (System.console().readLine());
                        
                        System.out.print("Node id of something in the replication graph: ");
                        String src = (System.console().readLine());

                        new UddiReplication().GetChangeRecords(key2, Long.parseLong(id),src);

                }
                if (input.equals("30")) {
                        // System.out.println("30) Replication - Setup replication between two nodes");
                        new JuddiAdminService().setupReplication();

                }
                if (input.equals("31")) {
                        //System.out.println("31) Quick add the jUDDI cloud node to *this's configuration file");
                        new JuddiAdminService().quickRegisterLocalCloud();
                }
                if (input.equals("32")) {
                        // System.out.println("32) Add a node to *this's configuration file");
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        UDDINode node = new UDDINode();
                        System.out.print("Name (must be unique: ");
                        node.setClientName(System.console().readLine());
                        node.setName(node.getClientName());
                        System.out.print("Description: ");
                        node.setDescription(System.console().readLine());

                        node.setHomeJUDDI(false);
                        System.out.print("Inquiry URL: ");
                        node.setInquiryUrl(System.console().readLine());

                        System.out.print("Inquiry REST URL (optional): ");
                        node.setInquiryRESTUrl(System.console().readLine());

                        System.out.print("jUDDI API URL (optional): ");
                        node.setJuddiApiUrl(System.console().readLine());
                        System.out.print("Custody Transfer URL: ");
                        node.setCustodyTransferUrl(System.console().readLine());
                        System.out.print("Publish URL: ");
                        node.setPublishUrl(System.console().readLine());
                        System.out.print("Replication URL: ");
                        node.setReplicationUrl(System.console().readLine());
                        System.out.print("Security URL: ");
                        node.setSecurityUrl(System.console().readLine());
                        System.out.print("Subscription URL: ");
                        node.setSubscriptionUrl(System.console().readLine());

                        System.out.print("Subscription Listener URL: ");
                        node.setSubscriptionListenerUrl(System.console().readLine());

                        System.out.print("Transport (defaults to JAXWS): ");
                        node.setProxyTransport(System.console().readLine());
                        if (node.getProxyTransport() == null || node.getProxyTransport().trim().equalsIgnoreCase("")) {
                                node.setProxyTransport(org.apache.juddi.v3.client.transport.JAXWSTransport.class.getCanonicalName());
                        }
                        System.out.print("Factory Initial (optional): ");
                        node.setFactoryInitial(System.console().readLine());
                        System.out.print("Factory Naming Provider (optional): ");
                        node.setFactoryNamingProvider(System.console().readLine());
                        System.out.print("Factory URL Packages (optional): ");
                        node.setFactoryURLPkgs(System.console().readLine());

                        clerkManager.getClientConfig().addUDDINode(node);
                        clerkManager.getClientConfig().saveConfig();
                        System.out.println("Saved.");
                }
                if (input.equals("33")) {

                        //System.out.println("32) Register a *this node to a jUDDI server");
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                        System.out.println();

                        System.out.println("Locally defined nodes:");
                        for (int i = 0; i < uddiNodeList.size(); i++) {
                                System.out.println("________________________________________________________________________________");
                                System.out.println((i + 1) + ") Node name: " + uddiNodeList.get(i).getName());
                                System.out.println((i + 1) + ") Node description: " + uddiNodeList.get(i).getDescription());
                                System.out.println((i + 1) + ") Transport: " + uddiNodeList.get(i).getProxyTransport());
                                System.out.println((i + 1) + ") jUDDI URL: " + uddiNodeList.get(i).getJuddiApiUrl());
                        }
                        System.out.println("Local Source Node: ");
                        int index = Integer.parseInt(System.console().readLine()) - 1;

                        System.out.println("Remote Destination(s):");
                        for (int i = 0; i < uddiNodeList.size(); i++) {
                                System.out.println("________________________________________________________________________________");
                                System.out.println((i + 1) + ") Node name: " + uddiNodeList.get(i).getName());
                                System.out.println((i + 1) + ") Node description: " + uddiNodeList.get(i).getDescription());
                                System.out.println((i + 1) + ") Transport: " + uddiNodeList.get(i).getProxyTransport());
                                System.out.println((i + 1) + ") jUDDI URL: " + uddiNodeList.get(i).getJuddiApiUrl());
                        }
                        System.out.println("Remote Destination Node to publish to: ");
                        int index2 = Integer.parseInt(System.console().readLine()) - 1;

                        new JuddiAdminService().registerLocalNodeToRemoteNode(authtoken, uddiNodeList.get(index), uddiNodeList.get(index2));

                }
                if (input.equals("34")) {

                        // System.out.println("33) View all register remote nodes on a jUDDI server");
                        new JuddiAdminService().viewRemoteNodes(authtoken);
                }

                if (input.equals("35")) {
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                        for (int i = 0; i < uddiNodeList.size(); i++) {
                                System.out.println("________________________________________________________________________________");
                                System.out.println("Client name: " + uddiNodeList.get(i).getClientName());
                                System.out.println("Node name: " + uddiNodeList.get(i).getName());
                                System.out.println("Node description: " + uddiNodeList.get(i).getDescription());
                                System.out.println("Transport: " + uddiNodeList.get(i).getProxyTransport());
                                System.out.println(i + ") jUDDI URL: " + uddiNodeList.get(i).getJuddiApiUrl());

                        }

                }
                if (input.equals("36")) {

                        new JuddiAdminService().viewRemoveRemoteNode(authtoken);
                        //System.out.println("35) UnRegister a node on a jUDDI server");
                }
                if (input.equals("37")) {
                        new JuddiAdminService().viewReplicationConfig(authtoken);
                }
                if (input.equals("38")) {
                        new JuddiAdminService().setReplicationConfig(authtoken);
                }
                if (input.equals("magic")) {
                        //secret menu, setups up replication between juddi8080 and 9080 and adds a record or two on 8080
                        //UDDISecurityPortType security = null;
                        //UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        
                       /* Transport transport = clerkManager.getTransport("default");
                        security = transport.getUDDISecurityService();
                       
                       String uname = "root";
                       
                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(uname);
                        getAuthTokenRoot.setCred("root");
                        authtoken = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                        System.out.println("Success!");*/
                        
                        //this setups up a replication config between the two nodes
                        new JuddiAdminService().autoMagic();

                        new UddiCreatebulk("default").publishBusiness(null, 1, 1);
                        new UddiCreatebulk("uddi:another.juddi.apache.org:node2").publishBusiness(null, 1, 1);
                }
                if (input.equals("rep")) {
                        new JuddiAdminService().printStatus();
                }
                if (input.equals("39")) {
                        new UddiDigitalSignatureFile().Fire(null, null, null);
                }

        }
}
