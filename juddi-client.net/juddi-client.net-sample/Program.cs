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
    class Program
    {
        static void Main(string[] args)
        {
            Console.Out.WriteLine("jUDDI.Net sampe programs!");
            Console.Out.WriteLine("1) SimpleInquiry");
            Console.Out.WriteLine("2) ServiceVersioning");
            Console.Out.WriteLine("3) Encryption");
            Console.Out.WriteLine("4) WADL2UDDI");
            Console.Out.WriteLine("5) WSDL2UDDI");
            Console.Out.WriteLine("6) Find_endpoints");
            Console.Out.Write("Enter selection> ");
            String selection=Console.In.ReadLine();
            selection = selection.Trim();
            if (selection.Equals("1"))
                SimpleInquiry.Run();
            else if (selection.Equals("2"))
                new ServiceVersioning().go();
            else if (selection.Equals("3"))
                Encryption.main(args);
            else if (selection.Equals("4"))
                WadlImport.main(args);
            else if (selection.Equals("5"))
                WsdlImport.main(args);
            else if (selection.Equals("6"))
                FindendpointsDemo.main(args);

            Console.WriteLine("Press any key to exit");
            Console.Read();

        }
    }
}
