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

using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;

namespace org.apache.juddi.client.samples
{
    class AccessUDDIv2Publish
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

                save_business sb = new save_business();
                get_authToken rq=new get_authToken();
                rq.cred = "username";
                rq.userID="username";
                String token=security.get_authToken(rq).authInfo;
                System.Console.WriteLine("Got an auth token...");
                sb.authInfo = token;
                sb.businessEntity = new businessEntity[1];
                sb.businessEntity[0] = new businessEntity();
                sb.businessEntity[0].name = new name[] { new name("test business from .net via uddi2 translation", "en") };
                sb.businessEntity[0].description = new description[] { new description("a description", "en") };
                businessDetail detail=  publish.save_business(sb);
                System.Console.WriteLine("business saved");
                PrintUDDI<businessDetail> p = new PrintUDDI<businessDetail>();
                Console.Out.WriteLine(p.print(detail));

                save_service ss = new save_service();
                ss.authInfo = token;
                ss.businessService = new businessService[1];
                ss.businessService[0] = new businessService();
                ss.businessService[0].name = new name[] { new name("hello world", "en") };
                ss.businessService[0].businessKey = detail.businessEntity[0].businessKey;
                serviceDetail sd = publish.save_service(ss);
                System.Console.WriteLine("service saved");
                PrintUDDI<serviceDetail> p1 = new PrintUDDI<serviceDetail>();
                Console.Out.WriteLine(p1.print(sd));

                save_binding sbd = new save_binding();
                sbd.authInfo = token;
                sbd.bindingTemplate = new bindingTemplate[1];
                sbd.bindingTemplate[0] = new bindingTemplate();
                sbd.bindingTemplate[0].Item = new accessPoint("http://localhost", "endPoint");
                sbd.bindingTemplate[0].serviceKey = sd.businessService[0].serviceKey;
                bindingDetail bd = publish.save_binding(sbd);
                System.Console.WriteLine("binding saved");
                PrintUDDI<bindingDetail> p2 = new PrintUDDI<bindingDetail>();
                Console.Out.WriteLine(p2.print(bd));

                save_tModel stm = new save_tModel();
                stm.authInfo = token;
                stm.tModel = new tModel[1];
                stm.tModel[0] = new tModel();
                stm.tModel[0].name = new name("a uddiv2 tmodel", "en");
                tModelDetail td = publish.save_tModel(stm);
                System.Console.WriteLine("tModel saved");
                PrintUDDI<tModelDetail> p3 = new PrintUDDI<tModelDetail>();
                Console.Out.WriteLine(p3.print(td));

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
