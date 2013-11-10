using org.apache.juddi.apiv3;
using org.apache.juddi.jaxb;
/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;


namespace org.apache.juddi.client.sample.juddi
{
    class saveNodeExample
    {
        public static void main(string[] args)
        {
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient("uddi.xml");
                UDDIClientContainer.addClient(clerkManager);

                transport = clerkManager.getTransport("default");

                org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                org.uddi.apiv3.UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();
                JUDDIApiService juddi= transport.getJUDDIApiService();

                clerk = clerkManager.getClerk("default");
                
                node n1 = new node();
                n1.clientName = "juddicloud";
                //the following are optional
                /*
                 * max length is 255
                 * proxy transport
                 * factory*
                 * juddiapi url
                 */
                n1.proxyTransport = "org.apache.juddi.v3.client.transport.JAXWSTransport";  
                n1.custodyTransferUrl = "http://uddi-jbossoverlord.rhcloud.com/services/custody-transfer";
                n1.inquiryUrl = "http://uddi-jbossoverlord.rhcloud.com/services/inquiry";
                n1.publishUrl = "http://uddi-jbossoverlord.rhcloud.com/services/publish";
                n1.securityUrl = "http://uddi-jbossoverlord.rhcloud.com/services/security";
                n1.subscriptionUrl = "http://uddi-jbossoverlord.rhcloud.com/services/subscription";
                n1.subscriptionListenerUrl = "http://uddi-jbossoverlord.rhcloud.com/services/subscription-listener";
                n1.name = "juddicloud";
                n1.description = "juddicloud";
                save_nodeInfo saveNode = new save_nodeInfo();
                saveNode.authInfo = (clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl()));
                saveNode.node = new node[] { (n1) };
                PrintJUDDI<save_nodeInfo> p = new PrintJUDDI<save_nodeInfo>();
                Console.Out.WriteLine("Before sending");
                Console.Out.WriteLine(p.print(saveNode));


                node[] nodes = clerk.getUDDINode().getTransport().getJUDDIApiService(clerk.getUDDINode().getJuddiApiUrl()).save_Node(saveNode);


              
             //   clerk.saveNode(n1);
                
            }
            catch (Exception ex)
            {
                while (ex != null)
                {
                    System.Console.WriteLine("Error! " + ex.Message);
                    ex = ex.InnerException;
                }
            }
            finally
            {
                if (transport != null && transport is IDisposable)
                {
                    ((IDisposable)transport).Dispose();
                }
                if (clerk != null)
                    clerk.Dispose();
            }




        }
    }
}
