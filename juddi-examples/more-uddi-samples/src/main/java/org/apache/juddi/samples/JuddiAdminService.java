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

import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.jaxb.PrintJUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
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

    public JuddiAdminService() {
        try {
            // create a manager and read the config in the archive; 
            // you can use your config file name
            UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
            // register the clerkManager with the client side container
            UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            clerk = clerkManager.getClerk("default");
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = clerkManager.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            publish = transport.getUDDIPublishService();
            juddi = transport.getJUDDIApiService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() {
        try {
            // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
            // and can save other publishers).
            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID("root");
            getAuthTokenRoot.setCred("root");

            // Making API call that retrieves the authentication token for the 'root' user.
            AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
            System.out.println("root AUTHTOKEN = " + "***** don't log auth tokens");
            
            SaveNode node = new SaveNode();
            node.setAuthInfo(rootAuthToken.getAuthInfo());
            Node n = new Node();
            n.setClientName("juddicloud");
            n.setName("juddicloud");
            n.setCustodyTransferUrl("http://uddi-jbossoverlord.rhcloud.com/services/custody-transfer");
            n.setDescription("juddicloud");
            n.setProxyTransport("org.apache.juddi.v3.client.transport.JAXWSTransport");
            n.setInquiryUrl("http://uddi-jbossoverlord.rhcloud.com/services/inquiry");
            n.setJuddiApiUrl("http://uddi-jbossoverlord.rhcloud.com/services/juddi-api");
            n.setPublishUrl("http://uddi-jbossoverlord.rhcloud.com/services/publish");
            n.setSecurityUrl( "http://uddi-jbossoverlord.rhcloud.com/services/security");
            n.setSubscriptionListenerUrl("http://uddi-jbossoverlord.rhcloud.com/services/subscription-listener");
            n.setSubscriptionUrl("http://uddi-jbossoverlord.rhcloud.com/services/subscription");
            node.getNode().add(n);
            
            PrintJUDDI<SaveNode> p = new PrintJUDDI<SaveNode>();
            System.out.println("Before sending");
            System.out.println(p.print(node));
            juddi.saveNode(node);
         //   clerk.saveNode(node);
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        JuddiAdminService sp = new JuddiAdminService();
        sp.go();
    }
}
