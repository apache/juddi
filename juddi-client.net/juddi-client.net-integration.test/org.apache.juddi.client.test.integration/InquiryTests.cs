/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

using NUnit.Framework;
using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;

namespace juddi_client.net_integration.test
{
    [TestFixture]
    public class InquiryTests
    {
        static UDDIClient clerkManager = null;
        static Transport transport = null;
        static UDDIClerk clerk = null;
        static UDDINode node = null;
        static org.uddi.apiv3.UDDI_Security_SoapBinding security = null;
        static org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = null;
        static org.uddi.apiv3.UDDI_Publication_SoapBinding publish = null;
        static org.uddi.apiv3.UDDI_Subscription_SoapBinding sub = null;
        static bool online = true;
        static bool serialize = false;
        [TestFixtureSetUp]
        public static void init()
        {

            if (Environment.GetEnvironmentVariable("debug") != null
                && Environment.GetEnvironmentVariable("debug").Equals("true", StringComparison.CurrentCultureIgnoreCase))
            {
                serialize = true;
            }

            clerkManager = new UDDIClient("resource/uddi.xml");

            transport = clerkManager.getTransport("default");

            security = transport.getUDDISecurityService();
            inquiry = transport.getUDDIInquiryService();
            publish = transport.getUDDIPublishService();
            sub = transport.getUDDISubscriptionService();
            clerk = clerkManager.getClerk("default");
            node = clerk.getUDDINode();
            WebClient c = new WebClient();

            Console.Out.WriteLine("Checking to see if tomcat is running");
            String s = null;
            int count = 100;
            while (s == null && count > 0)
            {
                try
                {
                    s = c.DownloadString("http://localhost:8080/");
                    Console.Out.WriteLine("Tomcat is running");
                    break;
                }
                catch
                { }
                Console.Out.WriteLine("tomcat isn't running yet, waiting...");
                Thread.Sleep(1000);
            }

            if (s == null)
            {
                Console.Out.WriteLine("Unable to confirm if tomcat is running, aborting");
                online = false;
            }

        }


        [Test]
        public void findBusinesses()
        {
            Console.Out.Write("findBusinesses");
            Assert.True(online);
            find_business fb = new find_business();
            fb.name = new name[] { new name(UDDIConstants.WILDCARD, null) };
            fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
            businessList bl = inquiry.find_business(fb);
            Assert.NotNull(bl);
            Assert.NotNull(bl.businessInfos);
            Assert.True(bl.businessInfos.Length > 0);
            for (int i = 0; i < bl.businessInfos.Length; i++)
            {
                Assert.IsNotNullOrEmpty(bl.businessInfos[i].businessKey);
            }
            PrintUDDI<businessList> p = new PrintUDDI<businessList>();
            if (serialize)
                Console.Out.WriteLine(p.print(bl));

        }

        [Test]
        public void findServices()
        {
            Console.Out.Write("findServices");
            Assert.True(online);
            find_service fb = new find_service();
            fb.name = new name[] { new name(UDDIConstants.WILDCARD, null) };
            fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
            serviceList bl = inquiry.find_service(fb);
            Assert.NotNull(bl);
            Assert.NotNull(bl.serviceInfos);
            Assert.True(bl.serviceInfos.Length > 0);
            for (int i = 0; i < bl.serviceInfos.Length; i++)
            {
                Assert.IsNotNullOrEmpty(bl.serviceInfos[i].businessKey);
                Assert.IsNotNullOrEmpty(bl.serviceInfos[i].serviceKey);
            }
            PrintUDDI<serviceList> p = new PrintUDDI<serviceList>();
            if (serialize)
                Console.Out.WriteLine(p.print(bl));
        }

        [Test]
        public void findTmodels()
        {
            Console.Out.Write("find_tModel");
            Assert.True(online);
            find_tModel fb = new find_tModel();
            fb.name = new name(UDDIConstants.WILDCARD, null);
            fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
            tModelList bl = inquiry.find_tModel(fb);
            Assert.NotNull(bl);
            Assert.NotNull(bl.tModelInfos);
            Assert.True(bl.tModelInfos.Length > 0);
            for (int i = 0; i < bl.tModelInfos.Length; i++)
            {
                Assert.IsNotNullOrEmpty(bl.tModelInfos[i].tModelKey);
                Assert.NotNull(bl.tModelInfos[i].name);
            }
            PrintUDDI<tModelList> p = new PrintUDDI<tModelList>();
            if (serialize)
                Console.Out.WriteLine(p.print(bl));
        }



    }
}
