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


namespace org.apache.juddi.client.sample
{
    public class FindendpointsDemo
    {
       public static void main(string[] args)
       {
           UDDIClient clerkManager = null;
           Transport transport = null;
           UDDIClerk clerk = null;
           try
           {
               clerkManager = new UDDIClient("uddi.xml");

               transport = clerkManager.getTransport("default_non_root");

               UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
               UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
               UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();

               clerk = clerkManager.getClerk("default_non_root");


               find_business fb = new find_business();
               fb.authInfo = clerk.getAuthToken(security.Url);
               fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
               fb.name = new name[1];
               fb.name[0] = new name(UDDIConstants.WILDCARD, "en");
               businessList bl = inquiry.find_business(fb);
               for (int i = 0; i < bl.businessInfos.Length; i++)
               {
                   Console.WriteLine("Business: " + bl.businessInfos[i].name[0].Value);
                   if (bl.businessInfos[i].serviceInfos != null && bl.businessInfos[i].serviceInfos.Length > 0)
                   {
                       Console.WriteLine("Service: " + bl.businessInfos[i].serviceInfos[0].serviceKey);
                       Console.WriteLine("Running find_endpoints");
                       List<String> eps = clerk.getEndpoints(bl.businessInfos[i].serviceInfos[0].serviceKey);
                       Console.WriteLine(eps.Count + " endpoints found");
                       for (int k = 0; k < eps.Count; k++)
                       {
                           Console.WriteLine("[" + k + "] " + eps[i] );
                       }
                       break;
                   }
               }
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
