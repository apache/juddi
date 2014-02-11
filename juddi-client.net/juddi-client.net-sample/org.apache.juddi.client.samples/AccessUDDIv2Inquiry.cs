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

namespace org.apache.juddi.client.samples
{
    class AccessUDDIv2Inquiry
    {
        internal static void Run(string[] args)
        {
            
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient("uddi.xml");

                transport = clerkManager.getTransport("uddiv2");

                UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();

                clerk = clerkManager.getClerk("uddiv2");


                find_business fb = new find_business();
                //fb.authInfo = clerk.getAuthToken(security.Url);
                fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
                fb.name = new name[1];
                fb.name[0] = new name(UDDIConstants.WILDCARD, "en");
                businessList bl = inquiry.find_business(fb);
                for (int i = 0; i < bl.businessInfos.Length; i++)
                {
                    Console.WriteLine(bl.businessInfos[i].name[0].Value);

                }

                find_service fs = new find_service();
                fs.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
                fs.name = new name[1];
                fs.name[0] = new name("%", null);
                serviceList sl=inquiry.find_service(fs);
                for (int i = 0; i < sl.serviceInfos.Length; i++)
                {
                    Console.WriteLine(sl.serviceInfos[i].name[0].Value);

                }

                find_tModel ft = new find_tModel();
                ft.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
                ft.name = new name("%", null);
                tModelList tl = inquiry.find_tModel(ft);
                for (int i = 0; i < tl.tModelInfos.Length; i++)
                {
                    Console.WriteLine(tl.tModelInfos[i].name.Value);

                }
                //  serviceDetail s= clerk.getServiceDetail("uddi:mydomain.com:zerocoolsvc");
                //   Console.Out.WriteLine(new PrintUDDI<serviceDetail>().print(s));
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
