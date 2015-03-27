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
package org.apache.juddi.v3.client.cli;

import java.util.List;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author alex
 */
public class EntryPointSingleNode {

        static void goSingleNode() throws Exception {
                String currentNode = "default";
                UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");

                List<Node> uddiNodeList = clerkManager.getClientConfig().getUDDINodeList();
                System.out.println();

                System.out.println("Nodes:");
                for (int i = 0; i < uddiNodeList.size(); i++) {
                        System.out.println((i + 1) + ") Node name: " + uddiNodeList.get(i).getName());
                }
                System.out.print("Destination Node: ");
                int index = Integer.parseInt(System.console().readLine()) - 1;

                currentNode = uddiNodeList.get(index).getName();
                Transport transport = clerkManager.getTransport(currentNode);
                authtoken = login(currentNode, transport);
                String input = null;
                do {
                        System.out.println(" 1) Login");
                        System.out.println(" 2) Print auth token");
                        System.out.println(" 3) Logout (discard auth token)");

                        System.out.println("- [ Searching and Browsing ] -");
                        System.out.println(" 4) List all Businesses (XML)");
                        System.out.println(" 5) List all Businesses (Human readable)");
                        System.out.println(" 6) List all Services (XML)");
                        System.out.println(" 7) List all Services (Human readable)");
                        System.out.println(" 8) List all tModels (XML)");
                        System.out.println(" 9) List all tModels (Human readable)");
                        System.out.println("10) Find a Business");
                        System.out.println("11) Find a Service");
                        System.out.println("12) Find a tModel");

                        System.out.println("13) Find Binding by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println("14) Find Business by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println("15) Find Service by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println("16) Find a Binding, lists all bindings for all services");
                        System.out.println("17) Find Endpoints of a service (given the key)");
                        System.out.println("18) Get the details of a service");
                        System.out.println("19) UDDI Digital Signatures - Search for Signed Items");

                        System.out.println("- [ Publishing ] -");
                        System.out.println("20) Make a Key Generator tModel");
                        System.out.println("21) UDDI Create Bulk (makes N business with N services) (create for load testing)");
                        System.out.println("22) WSDL2UDDI - Register a service from a WSDL document (business key required)");
                        System.out.println("23) WADL2UDDI - Register a service from a WADL document (business key required)");
                        System.out.println("24) UDDI Custody Transfer (within a single node)");

                        System.out.println("25) UDDI Digital Signatures - Sign a Business");
                        System.out.println("26) UDDI Digital Signatures - Sign a Service");
                        System.out.println("27) UDDI Digital Signatures - Sign a tModel");

                        System.out.println("28) Create a Business Relationship Between two users and two Businesses(Publisher Assertion)");

                        System.out.println("- [ Subscriptions ] -");
                        System.out.println("29) Subscriptions - Asynchronous, listens for all changes (req stored credentials)");
                        System.out.println("30) Subscriptions - Synchronous");
                        System.out.println("31) Print Subscriptions");
                        System.out.println("32) Delete a subscription");
                        System.out.println("33) Delete all subscriptions");
                        System.out.println("34) Subscriptions - Asynchronous, publisher assertion subscriptions");

                        System.out.println("- [ Replication ] -");
                        System.out.println("35) Replication - do_ping");
                        System.out.println("36) Replication - get high watermarks");
                        System.out.println("37) Replication - get change records");
                        System.out.println("38) Replication - get failed change records (jUDDI only)");

                        //remote config management - juddi only
                        System.out.println("- [ jUDDI Configuration Management ] -");
                        System.out.println("39) Quick register the jUDDI cloud node to the current node");
                        System.out.println("40) Register the a locally defined node to another jUDDI server");
                        System.out.println("41) View all registered remote nodes on a jUDDI server");
                        System.out.println("42) UnRegister a node on a jUDDI server");

                        //juddi only
                        System.out.println("43) View the replication config from the current jUDDI server");
                        System.out.println("44) Set the replication config on a remote jUDDI server");
                        System.out.println("45) Prints the current replication status of a given node");
                        System.out.println("46) Periodic publisher, 1biz+1svc every 5 seconds");

                        //deleters
                        System.out.println("47) Bulk delete business");
                        System.out.println("48) Bulk delete services");
                        System.out.println("49) Bulk delete tModels");

                        System.out.println("q) quit");
                        System.out.print(username + "@" + currentNode + "# ");
                        input = System.console().readLine();
                        try {
                                processInput(input, currentNode, transport, clerkManager);
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                } while (!input.equalsIgnoreCase("q"));
        }
        private static String authtoken = null;
        static String password;
        static String username;

        private static String login(String currentNode, Transport transport) throws Exception {
                System.out.println("Options:");
                System.out.println("1) Enter a username/password for auth token");
                System.out.println("2) Enter a username/password for HTTP based logins");
                System.out.println("3) Enter a username/password for use stored credentials");
                System.out.print("Login Method: ");
                String input = System.console().readLine();
                if ("1".equalsIgnoreCase(input)) {
                        UDDISecurityPortType security = null;
                        security = transport.getUDDISecurityService();
                        System.out.print(currentNode + "# username: ");
                        username = System.console().readLine();
                        char passwordArray[] = System.console().readPassword(currentNode + "# password: ");
                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(username);
                        password = new String(passwordArray);
                        getAuthTokenRoot.setCred((password));
                        String lauthtoken = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                        System.out.println("Success!");
                        return lauthtoken;
                } else if ("2".equalsIgnoreCase(input)) {
                        System.out.println("not implemented yet!");

                } else if ("3".equalsIgnoreCase(input)) {
                        System.out.println("not implemented yet!");
                } else {
                        System.out.println("Aborted!");
                }
                return null;

        }

        private static void processInput(final String input, final String currentNode, final Transport transport, UDDIClient client) throws Exception {
                if (input == null) {
                        return;
                }
                if (input.equals("1")) {
                        authtoken = login(currentNode, transport);
                } else if (input.equals("2")) {
                        System.out.println("Token info: " + authtoken);
                } else if (input.equals("3")) {
                        if (authtoken != null) {
                                UDDISecurityPortType security = null;
                                security = transport.getUDDISecurityService();
                                DiscardAuthToken getAuthTokenRoot = new DiscardAuthToken();
                                getAuthTokenRoot.setAuthInfo(authtoken);
                                security.discardAuthToken(getAuthTokenRoot);
                                System.out.println("Success!");
                        }
                } else if (input.equals("4")) {
                        new SimpleBrowse(transport).printBusinessList(authtoken, null, true);
                } else if (input.equals("5")) {
                        new SimpleBrowse(transport).printBusinessList(authtoken, null, false);
                } else if (input.equals("6")) {
                        new SimpleBrowse(transport).printServiceList(authtoken, null, true);
                } else if (input.equals("7")) {
                        new SimpleBrowse(transport).printServiceList(authtoken, null, false);
                } else if (input.equals("8")) {
                        new SimpleBrowse(transport).printTModelList(authtoken, null, true);
                } else if (input.equals("9")) {
                        new SimpleBrowse(transport).printTModelList(authtoken, null, false);
                } else if (input.equals("10")) {
                        System.out.print("Tip: (use % for wildcard searches)");
                        System.out.print("Name to search for: ");
                        String url = (System.console().readLine());
                        new SimpleBrowse(transport).printBusinessList(authtoken, url, false);
                } else if (input.equals("11")) {
                        System.out.print("Tip: (use % for wildcard searches)");
                        System.out.print("Name to search for: ");
                        String url = (System.console().readLine());
                        new SimpleBrowse(transport).printServiceList(authtoken, url, false);
                } else if (input.equals("12")) {
                        System.out.print("Tip: (use % for wildcard searches)");
                        System.out.print("Name to search for: ");
                        String url = (System.console().readLine());
                        new SimpleBrowse(transport).printTModelList(authtoken, url, false);
                } else if (input.equals("13")) {
                        SearchByQos.doFindBinding(authtoken, transport);
                } else if (input.equals("14")) {
                        SearchByQos.doFindBusiness(authtoken, transport);
                } else if (input.equals("15")) {
                        SearchByQos.doFindService(authtoken, transport);
                } else if (input.equals("16")) {
                        new UddiFindBinding().Fire(authtoken);
                } else if (input.equals("17")) {
                        System.out.print("Service key to parse the endpoints: ");
                        String key = (System.console().readLine());
                        new UddiFindEndpoints().Fire(authtoken, key);
                } else if (input.equals("18")) {
                        System.out.print("Service key: ");
                        String key = (System.console().readLine());
                        new UddiGetServiceDetails().Fire(authtoken, key);
                } else if (input.equals("19")) {
                        new UddiDigitalSignatureSearch().Fire(authtoken);
                } else if (input.equals("20")) {
                        System.out.print("Get FQDN: ");
                        String key = (System.console().readLine());
                        new UddiKeyGenerator().Fire(authtoken, key);
                } else if (input.equals("21")) {

                        System.out.print("businesses: ");
                        int biz = Integer.parseInt(System.console().readLine());
                        System.out.print("servicesPerBusiness: ");
                        int svc = Integer.parseInt(System.console().readLine());
                        new UddiCreatebulk(transport, false, currentNode).publishBusiness(authtoken, biz, svc, username);
                } else if (input.equals("22")) {
                        System.out.print("Path or URL to WSDL file: ");
                        String url = (System.console().readLine());
                        System.out.print("Business key to attach to: ");
                        String key = (System.console().readLine());
                        new WsdlImport().Fire(url, key, authtoken, transport);
                } else if (input.equals("23")) {
                        System.out.print("Path or URL to WADL file: ");
                        String url = (System.console().readLine());
                        System.out.print("Business key to attach to: ");
                        String key = (System.console().readLine());
                        new WadlImport().Fire(url, key, authtoken, transport);
                } else if (input.equals("24")) {
                        UDDISecurityPortType security = null;
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
                } else if (input.equals("25")) {
                        System.out.print("Business key to sign: ");
                        String key = (System.console().readLine());
                        new UddiDigitalSignatureBusiness().Fire(authtoken, key);
                } else if (input.equals("26")) {
                        System.out.print("Service key to sign: ");
                        String key = (System.console().readLine());
                        new UddiDigitalSignatureService().Fire(authtoken, key);
                } else if (input.equals("27")) {
                        System.out.print("tModel key to sign: ");
                        String key = (System.console().readLine());
                        new UddiDigitalSignatureTmodel().Fire(authtoken, key);
                } else if (input.equals("28")) {
                        UDDISecurityPortType security = null;

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
                } else if (input.equals("29")) {
                        new UddiSubscribe(client, currentNode, transport).Fire();

                } else if (input.equals("30")) {
                        System.out.print("Subscription key: ");
                        String key = (System.console().readLine());
                        System.out.println("Fetching results for the past 30 days...");
                        new UddiSubscribeValidate(transport).go(authtoken, input);
                } else if (input.equals("31")) {
                        new UddiSubscriptionManagement(transport).PrintSubscriptions(authtoken);
                } else if (input.equals("32")) {
                        System.out.print("Subscription key: ");
                        String key2 = (System.console().readLine());
                        new UddiSubscriptionManagement(transport).DeleteSubscription(authtoken, key2);
                } else if (input.equals("33")) {
                        new UddiSubscriptionManagement(transport).DeleteAllSubscriptions(authtoken);
                } else if (input.equals("34")) {
                        new UddiSubscribeAssertionStatus(transport).Fire(currentNode);

                } else if (input.equals("35")) {

                        new UddiReplication(client, currentNode).DoPing();

                } else if (input.equals("36")) {
                        //System.out.println("28) Replication - get high watermarks");
                        new UddiReplication(client, currentNode).GetHighWatermarks();

                } else if (input.equals("37")) {
                        //System.out.println("29) Replication - get change records");

                        System.out.print("Change ID to fetch: ");
                        String id = (System.console().readLine());

                        System.out.print("Node id of something in the replication graph: ");
                        String src = (System.console().readLine());

                        new UddiReplication(client, currentNode).GetChangeRecords(Long.parseLong(id), src);

                } else if ("38".equals(input)) {
                        new JuddiAdminService(client, transport).dumpFailedReplicationRecords(authtoken);
                } else if (input.equals("39")) {

                        new JuddiAdminService(client, transport).quickRegisterRemoteCloud(authtoken);
                } else if (input.equals("40")) {

                        //System.out.println("32) Register a *this node to a jUDDI server");
                        UDDIClient clerkManager = new UDDIClient();
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

                        new JuddiAdminService(client, transport).registerLocalNodeToRemoteNode(authtoken, uddiNodeList.get(index), uddiNodeList.get(index2));

                } else if (input.equals("41")) {

                        // System.out.println("33) View all register remote nodes on a jUDDI server");
                        new JuddiAdminService(client, transport).viewRemoteNodes(authtoken);
                } /*  if (input.equals("35")) {
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

                 }*/ else if (input.equals("42")) {

                        new JuddiAdminService(client, transport).viewRemoveRemoteNode(authtoken);
                        //System.out.println("35) UnRegister a node on a jUDDI server");
                } else if (input.equals("43")) {
                        new JuddiAdminService(client, transport).viewReplicationConfig(authtoken, currentNode);
                } else if (input.equals("44")) {
                        new JuddiAdminService(client, transport).setReplicationConfig(authtoken);
                } else if (input.equals("45")) {
                        new JuddiAdminService(client, transport).printStatus(transport, authtoken);
                } else if (input.equals("46")) {
                        //TODO current counts
                        UDDIInquiryPortType uddiInquiryService = transport.getUDDIInquiryService();
                        FindBusiness fb = new FindBusiness();
                        fb.setAuthInfo(authtoken);
                        fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                        fb.setFindQualifiers(new FindQualifiers());
                        fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fb.setMaxRows(1);
                        fb.setListHead(0);
                        BusinessList findBusiness = uddiInquiryService.findBusiness(fb);
                        System.out.println("current business counts "
                                + findBusiness.getListDescription().getActualCount() + " "
                                + findBusiness.getListDescription().getIncludeCount() + " "
                                + findBusiness.getListDescription().getListHead());
                        FindService fs = new FindService();
                        fs.setAuthInfo(authtoken);
                        fs.getName().add(new Name(UDDIConstants.WILDCARD, null));
                        fs.setFindQualifiers(new FindQualifiers());
                        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fs.setMaxRows(1);
                        fs.setListHead(0);
                        ServiceList findService = uddiInquiryService.findService(fs);
                        System.out.println("current service counts "
                                + findService.getListDescription().getActualCount() + " "
                                + findService.getListDescription().getIncludeCount() + " "
                                + findService.getListDescription().getListHead());

                        running = true;
                        createdServices = 0;
                        createdBusinesses = 0;

                        new Thread(new Runnable() {
                                @Override
                                public void run() {
                                        UddiCreatebulk uddiCreatebulk = new UddiCreatebulk(transport, false, currentNode);
                                        while (running) {
                                                try {
                                                        uddiCreatebulk.publishBusiness(authtoken, 1, 1, username);
                                                        createdBusinesses++;
                                                        createdServices++;
                                                        Thread.sleep(5000);
                                                } catch (Exception ex) {
                                                        System.out.println("eception caught, assuming it's an expired token, attempting to reauthenticate " + ex.getMessage());
                                                        //potentially an expired token, reauthenticate
                                                        try {
                                                                UDDISecurityPortType security = null;
                                                                security = transport.getUDDISecurityService();

                                                                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                                                                getAuthTokenRoot.setUserID(username);
                                                                getAuthTokenRoot.setCred((password));
                                                                authtoken = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
                                                        } catch (Exception x) {
                                                                System.out.println("unable to reauthenticate, aborting!");
                                                                ex.printStackTrace();
                                                                running = false;
                                                        }

                                                }
                                        }
                                }
                        }).start();
                        System.out.println("Started, press <Enter> to stop!");
                        System.console().readLine();
                        running = false;

                        System.out.println("before business counts "
                                + findBusiness.getListDescription().getActualCount());

                        fb.setAuthInfo(authtoken);
                        BusinessList afterfindBusiness = uddiInquiryService.findBusiness(fb);
                        System.out.println("after business counts "
                                + afterfindBusiness.getListDescription().getActualCount());
                        System.out.println("actual created " + createdBusinesses);
                        System.out.println("Delta = " + (afterfindBusiness.getListDescription().getActualCount() - findBusiness.getListDescription().getActualCount()));

                        System.out.println("before service counts "
                                + findService.getListDescription().getActualCount());

                        fs.setAuthInfo(authtoken);
                        ServiceList afterfindService = uddiInquiryService.findService(fs);
                        System.out.println("after service counts "
                                + afterfindService.getListDescription().getActualCount());
                        System.out.println("actual created " + createdServices);
                        System.out.println("delta = " + (afterfindService.getListDescription().getActualCount() - findService.getListDescription().getActualCount()));
                        if ((afterfindService.getListDescription().getActualCount() - findService.getListDescription().getActualCount()) == createdServices) {
                                System.out.println("success");
                        } else {
                                System.out.println("failure!");
                        }

                } else if (input.equals("47")) {
                        System.out.println("We'll run a search first, then the results will be deleted (after confirmation). Use % as a wild card");
                        System.out.print("Search query: ");
                        String key = (System.console().readLine());
                        FindBusiness fb = new FindBusiness();
                        fb.setFindQualifiers(new FindQualifiers());
                        fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fb.getName().add(new Name(key, null));
                        BusinessList findBusiness = transport.getUDDIInquiryService().findBusiness(fb);

                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authtoken);
                        for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                                db.getBusinessKey().add(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey());
                                System.out.println(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey() + " " + findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName().get(0).getValue());
                        }
                        System.out.print("The above businesses will be deleted, are you sure? (y/n) : ");
                        key = (System.console().readLine());
                        if ("y".equalsIgnoreCase(key.trim().toLowerCase())) {
                                transport.getUDDIPublishService().deleteBusiness(db);
                                System.out.println("done.");
                        }else
                                System.out.println("aborted.");
                }
                else if (input.equals("48")) {
                        System.out.println("We'll run a search first, then the results will be deleted (after confirmation). Use % as a wild card");
                        System.out.print("Search query: ");
                        String key = (System.console().readLine());
                        
                        FindService fb = new FindService();
                        fb.setFindQualifiers(new FindQualifiers());
                        fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fb.getName().add(new Name(key, null));
                        ServiceList findBusiness = transport.getUDDIInquiryService().findService(fb);

                        DeleteService db = new DeleteService();
                        db.setAuthInfo(authtoken);
                        for (int i = 0; i < findBusiness.getServiceInfos().getServiceInfo().size(); i++) {
                                db.getServiceKey().add(findBusiness.getServiceInfos().getServiceInfo().get(i).getServiceKey());
                                System.out.println(findBusiness.getServiceInfos().getServiceInfo().get(i).getServiceKey() + " " + findBusiness.getServiceInfos().getServiceInfo().get(i).getName().get(0).getValue());
                        }
                        System.out.print("The above services will be deleted, are you sure? (y/n) : ");
                        key = (System.console().readLine());
                        if ("y".equalsIgnoreCase(key.trim().toLowerCase())) {
                                transport.getUDDIPublishService().deleteService(db);
                                System.out.println("done.");
                        }else
                                System.out.println("aborted.");
                }
                else if (input.equals("49")) {
                        System.out.println("We'll run a search first, then the results will be deleted (after confirmation). Use % as a wild card");
                        System.out.print("Search query: ");
                        String key = (System.console().readLine());
                        FindTModel fb = new FindTModel();
                        fb.setFindQualifiers(new FindQualifiers());
                        fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fb.setName(new Name(key, null));
                        TModelList findBusiness = transport.getUDDIInquiryService().findTModel(fb);

                        DeleteTModel db = new DeleteTModel();
                        db.setAuthInfo(authtoken);
                        for (int i = 0; i < findBusiness.getTModelInfos().getTModelInfo().size(); i++) {
                                db.getTModelKey().add(findBusiness.getTModelInfos().getTModelInfo().get(i).getTModelKey());
                                System.out.println(findBusiness.getTModelInfos().getTModelInfo().get(i).getTModelKey() + " " + findBusiness.getTModelInfos().getTModelInfo().get(i).getName().getValue());
                        }
                        System.out.print("The above tModels will be deleted, are you sure? (y/n) : ");
                        key = (System.console().readLine());
                        if ("y".equalsIgnoreCase(key.trim().toLowerCase())) {
                                transport.getUDDIPublishService().deleteTModel(db);
                                System.out.println("done.");
                        }else
                                System.out.println("aborted.");
                }

        }
        static boolean running = true;
        static int createdServices = 0;
        static int createdBusinesses = 0;

}
