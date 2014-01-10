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

using net.java.dev.wadl;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.mapping;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;

namespace org.apache.juddi.client.sample
{
    public class WadlImport
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

                //Wadl Import example


                application app = WADL2UDDI.ParseWadl("..\\..\\..\\juddi-client.net.test\\resources\\sample.wadl");
                List<Uri> urls = WADL2UDDI.GetBaseAddresses(app);
                Uri url = urls[0];
                String domain = url.Host;

                tModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");
                //save the keygen
                save_tModel stm = new save_tModel();
                stm.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                stm.tModel = new tModel[] { keygen };

                publish.save_tModel(stm);
                Properties properties = new Properties();

                properties.put("keyDomain", domain);
                properties.put("businessName", domain);
                properties.put("serverName", url.Host);
                properties.put("serverPort", url.Port.ToString());
                //wsdlURL = wsdlDefinition.getDocumentBaseURI();
                WADL2UDDI wadl2UDDI = new WADL2UDDI(clerk, properties);

                businessService businessServices = wadl2UDDI.createBusinessService(new QName("MyWasdl.namespace", "Servicename"), app);


                HashSet<tModel> portTypeTModels = wadl2UDDI.createWADLPortTypeTModels(url.ToString(), app);

                //When parsing a WSDL, there's really two things going on
                //1) convert a bunch of stuff (the portTypes) to tModels
                //2) convert the service definition to a BusinessService

                //Since the service depends on the tModel, we have to save the tModels first
                save_tModel tms = new save_tModel();
                tms.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                HashSet<tModel>.Enumerator it = portTypeTModels.GetEnumerator();
                List<tModel> ts = new List<tModel>();
                while (it.MoveNext())
                {
                    ts.Add(it.Current);
                }
                tModel[] tmodels = ts.ToArray();

                tms.tModel = tmodels;
                if (tms.tModel.Length > 0)
                    //important, you'll need to save your new tModels, or else saving the business/service may fail
                    publish.save_tModel(tms);



                //finaly, we're ready to save all of the services defined in the WSDL
                //again, we're creating a new business, if you have one already, look it up using the Inquiry getBusinessDetails


                // inquiry.find_tModel(new find_tModel

                save_business sb = new save_business();
                //  sb.setAuthInfo(rootAuthToken.getAuthInfo());
                businessEntity be = new businessEntity();
                be.businessKey = (businessServices.businessKey);
                //TODO, use some relevant here
                be.name = new name[] { new name(domain, "en") };


                be.businessServices = new businessService[] { businessServices };
                sb.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                sb.businessEntity = new businessEntity[] { be };
                publish.save_business(sb);
                Console.Out.WriteLine("Saved!");
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
