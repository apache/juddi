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
using org.apache.juddi.v3.client.cryptor;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;

namespace org.apache.juddi.client.sample
{
    class DigitalSignaturesExample
    {
        public static void Run(string[] args)
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

                clerk = clerkManager.getClerk("default");


                find_business fb = new find_business();
                fb.authInfo = clerk.getAuthToken(security.Url);
                fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
                fb.name = new name[1];
                fb.name[0] = new name(UDDIConstants.WILDCARD, "en");
                businessList bl = inquiry.find_business(fb);
                if (bl.businessInfos.Length > 0)
                {
                    Console.Out.WriteLine(bl.businessInfos[0].name[0].Value);
                    Console.Out.WriteLine("attempting to sign");
                    serviceDetail sd = clerk.getServiceDetail(bl.businessInfos[0].serviceInfos[0].serviceKey);

                    //pist, the signing config comes from the stuff in in uddi.xml
                    DigSigUtil ds = new DigSigUtil(clerkManager.getClientConfig().getDigitalSignatureConfiguration());
                    businessService signedsvc = (businessService)ds.signUddiEntity(sd.businessService[0]);
                    PrintUDDI<businessService> p = new PrintUDDI<businessService>();
                    Console.Out.WriteLine("signed successfully!");

                    Console.Out.WriteLine(p.print(signedsvc));
                    Console.Out.WriteLine("attempting verify and validate");
                    String err = "";
                    bool valid = ds.verifySignedUddiEntity(signedsvc, out err);
                    Console.Out.WriteLine("Signature is " + (valid ? "Valid, Yippy!" : "Invalid!") + " msg: " + err);

                    Console.Out.WriteLine("saving");
                    clerk.register(signedsvc);
                    Console.Out.WriteLine("reloading content from the server...");

                    get_serviceDetail gsd = new get_serviceDetail();
                    gsd.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                    gsd.serviceKey = new string[] { signedsvc.serviceKey };
                    sd = inquiry.get_serviceDetail(gsd);

                    Console.Out.WriteLine(p.print(sd.businessService[0]));
                    Console.Out.WriteLine("attempting verify and validate");
                    err = "";
                    valid = ds.verifySignedUddiEntity(sd.businessService[0], out err);
                    Console.Out.WriteLine("Signature is " + (valid ? "Valid, Yippy!" : "Invalid!") + " msg: " + err);


                }
                else
                    Console.Out.WriteLine("no businesses were returned!");

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
