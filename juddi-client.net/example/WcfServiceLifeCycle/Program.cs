/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
using org.apache.juddi.v3.client.annotations;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using System;
using System.Collections.Generic;

using System.Text;
using System.Threading;

namespace WcfServiceLifeCycle
{
    class Program
    {
        static void Main(string[] args)
        {
            Type t = typeof(HelloWorldWCF);
            Console.Out.WriteLine(t.AssemblyQualifiedName);
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;

            clerkManager = new UDDIClient("uddi.xml");

            transport = clerkManager.getTransport("default");

            org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
            org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();

            clerk = clerkManager.getClerk("default");
            clerkManager.registerAnnotatedServices();
            HelloWorldWCF x = new HelloWorldWCF();
         //   clerkManager.unRegisterAnnotatedServices();
            Thread.Sleep(15000);

        }
    }


    [UDDIService(businessKey = "uddi:juddi.apache.org:businesses-asf", description = "My custom WCF service registered via annotations",
      serviceName = "HelloImplWcf, such a cool name", lang = "en", serviceKey = "uddi:juddi.apache.org:services-customHelloImplWCF")]
    [UDDIServiceBinding(accessPoint = "http://localhost/here", accessPointType = "endPoint")]
    public class HelloWorldWCF : Hello_PortType
    {
        public sayHelloResponse sayHello(sayHelloRequest request)
        {
            return new sayHelloResponse("hi");
        }
    }
}
