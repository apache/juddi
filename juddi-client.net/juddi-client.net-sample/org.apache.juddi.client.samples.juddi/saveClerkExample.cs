using org.apache.juddi.apiv3;
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
    class saveClerkExample
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

                UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();
                JUDDIApiService juddi= transport.getJUDDIApiService();

                clerk = clerkManager.getClerk("default");

               clerk   newclerk = new clerk();
               newclerk.node = new node();
                newclerk.node.name = "juddicloud";
                newclerk.publisher = "root";
                newclerk.password = "root";
                
                newclerk.name = "juddicloud";
                UDDIClerk cc =new UDDIClerk(newclerk);


                  save_clerk saveClerk = new save_clerk();
                saveClerk.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                saveClerk.clerk = new clerk[] { newclerk };
              clerk[] ret=  juddi.save_Clerk(saveClerk);

              Console.Out.WriteLine(ret.Length);
                
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
