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
using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.crypto;
using org.apache.juddi.v3.client.mapping;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using org.xmlsoap.schemas.easyWsdl;
using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Text;

namespace org.apache.juddi.client.sample
{
    public class WsdlImport
    {
        public static void main(string[] args)
        {
            String wsdlURL = "http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL";
            ReadWSDL wsi = new ReadWSDL();
            org.xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition = wsi.readWSDL(
                // "http://localhost/UDDI_API_V31.wsdl"
                wsdlURL
                );
            Properties properties1 = new Properties();
            properties1.put("keyDomain", "my.key.domain");
            WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizer(), properties1);
            List<tModel> tModels1 = new List<tModel>();

            Dictionary<QName, tPortType> portTypes1 = (Dictionary<QName, tPortType>)wsdlDefinition.getAllPortTypes();
            List<tModel> portTypeTModels1 = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes1);

            tModels1.AddRange(portTypeTModels1);

            Dictionary<QName, tBinding> allBindings1 = wsdlDefinition.getAllBindings();
            List<tModel> createWSDLBindingTModels1 = wsdl2UDDI.createWSDLBindingTModels(wsdlURL, allBindings1);

            tModels1.AddRange(createWSDLBindingTModels1);
            businessService[] services = wsdl2UDDI.createBusinessServices(wsdlDefinition);

            save_service ss = new save_service();
            ss.businessService = services;
            Console.Out.WriteLine(new PrintUDDI<save_service>().print(ss));

            save_tModel st = new save_tModel();
            st.tModel = tModels1.ToArray();
            Console.Out.WriteLine(new PrintUDDI<save_tModel>().print(st));


        }
    }
}
