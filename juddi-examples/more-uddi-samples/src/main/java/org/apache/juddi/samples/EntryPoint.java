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

import org.apache.juddi.v3.client.config.UDDIClient;
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
                        System.out.println(" 3) jUDDI Admin service - Register a Node");
                        System.out.println(" 4) Find Binding by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println(" 5) Find Service by QOS Parameters (Binding/tModelInstanceInfo)");
                        System.out.println(" 6) UDDI Create Bulk (makes N business/services");
                        System.out.println(" 7) UDDI Custody Transfer");
                        System.out.println(" 8) UDDI Digital Signatures - Sign a Business");
                        System.out.println(" 9) UDDI Digital Signatures - Sign a Service");
                        System.out.println("10) UDDI Digital Signatures - Sign a tModel");
                        System.out.println("11) UDDI Digital Signatures - Search for Signed Items");
                        System.out.println("12) Find a Binding, lists all bindings for all services");
                        System.out.println("13) Find Endpoints of a service");
                        System.out.println("14) Get the details of a service");
                        System.out.println("15) Make a Key Generator tModel");
                        System.out.println("16) Create a Business Relationship Between two users and two Businesses");
                        System.out.println("17) Subscriptions - Asynchronous, listens for all business changes");
                        System.out.println("18) Subscriptions - Synchronous");
                        System.out.println("19) WSDL2UDDI - Register a service from a WSDL document");
                        System.out.println("20) WADL2UDDI - Register a service from a WADL document");
                        System.out.println("21) Logout (discard auth token)");
                        System.out.println("22) Print auth token");
                        System.out.println("23) Print Subscriptions");
                        System.out.println("24) Delete a subscription");
                        System.out.println("25) Delete all subscriptions");
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
                        Transport transport = clerkManager.getTransport();
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
                        new JuddiAdminService().go(authtoken);
                }
                if (input.equals("4")) {
                        SearchByQos.doFindBinding(authtoken);
                }
                if (input.equals("5")) {
                        SearchByQos.doFindService(authtoken);
                }
                if (input.equals("6")) {
                        System.out.print("businesses: ");
                        int biz = Integer.parseInt(System.console().readLine());
                        System.out.print("servicesPerBusiness: ");
                        int svc = Integer.parseInt(System.console().readLine());
                        new UddiCreatebulk().publishBusiness(authtoken, biz, svc);
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

        }
}
