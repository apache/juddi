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
using org.uddi.apiv3;
using System;
using System.Collections.Generic;

using System.Text;
using System.Threading;


namespace AspnetServiceLifeCycle
{
    class Program
    {
        static void Main(string[] args)
        {
            //Normally, this coder snippet would be executed by some kind of web service life cycle listener
            //such as global.asax
            //a web service contructor
            Type t = typeof(HelloImpl);
            Console.Out.WriteLine(t.AssemblyQualifiedName);
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;

            clerkManager = new UDDIClient("uddi.xml");
            UDDIClientContainer.addClient(clerkManager);

            transport = clerkManager.getTransport("default");

            org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
            org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService ();

            clerk = clerkManager.getClerk("default");
            clerkManager.registerAnnotatedServices();
            HelloImpl x = new HelloImpl();

            //this is more of an "onShutdown" event
            clerkManager.unRegisterAnnotatedServices();
            Thread.Sleep(15000);

        }
    }

    [UDDIService(businessKey = "uddi:juddi.apache.org:businesses-asf", description = "My custom service via annotations",
        serviceName = "HelloImpl, such a cool name", lang = "en", serviceKey = "uddi:juddi.apache.org:services-customHelloImpl")]
    [UDDIServiceBinding(accessPoint="http://localhost/here", accessPointType="endPoint")]
    class HelloImpl : IHello_Binding
    {
        public string sayHello(string firstName)
        {
            return "hi";
        }
    }
}
