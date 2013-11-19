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
using NUnit.Framework;
using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.cryptor;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.IO;
using System.Net;
using System.Threading;

namespace juddi_client.net_integration.test
{
    [TestFixture]
    public class DigitalSignatureIntegrationTests
    {
        [TestFixtureSetUp]
        public static void init()
        {


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
        static Boolean online = true;

        string path = "";
        bool serialize = false;
        public DigitalSignatureIntegrationTests()
        {

            if (Environment.GetEnvironmentVariable("debug") != null
                && Environment.GetEnvironmentVariable("debug").Equals("true", StringComparison.CurrentCultureIgnoreCase))
            {
                serialize = true;
            }
            Console.Out.WriteLine(Directory.GetCurrentDirectory());
            path = Directory.GetCurrentDirectory() + Path.DirectorySeparatorChar + "resources";
            if (!Directory.Exists(path))
            {
                path = Directory.GetCurrentDirectory() + Path.DirectorySeparatorChar + ".." + Path.DirectorySeparatorChar + ".." + Path.DirectorySeparatorChar + ".." + Path.DirectorySeparatorChar + "juddi-client.net.test" + Path.DirectorySeparatorChar + "resources" + Path.DirectorySeparatorChar;
                Console.Out.WriteLine(path);
            }
            if (!Directory.Exists(path))
            {
                path = Environment.GetEnvironmentVariable("JUDDI_TEST_RES");
                Console.Out.WriteLine(path);
            }
            if (path == null || !Directory.Exists(path))
            {
                path = null;
                Console.Out.WriteLine("uh oh, I can't find the resources directory, override with the environment variable JUDDI_TEST_RES=<path>");
            }
            
        }

        [Test]
        public void fromWindowsStore()
        {
            Assume.That(online);
            Console.Out.WriteLine("DigitalSignatureIntegration fromWindowsstore");
            SignSaveAndVerifyToJuddi("resource/uddisigwinstore.xml");
        }

        [Test]
        public void fromFile()
        {
            Assume.That(online);
            Console.Out.WriteLine("DigitalSignatureIntegration fromFile");
            SignSaveAndVerifyToJuddi("resource/uddisigfile.xml");
        }

        /// <summary>
        /// From file
        /// create business, save in juddi, download from juddi, sign and save it,
        /// download it again, verify sig is valid
        /// </summary>
        /// <param name="config"></param>
        void SignSaveAndVerifyToJuddi(String config)
        {
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient(config);
                UDDIClientContainer.addClient(clerkManager);
                transport = clerkManager.getTransport("default");
                UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();
                clerk = clerkManager.getClerk("default");

                businessEntity be = new businessEntity();
                be.name = new name[] { new name("Test biz", "en") };

                Console.Out.WriteLine("saving test biz");
                businessEntity complete = clerk.register(be);
                Console.Out.WriteLine("attempting to sign");

                DigSigUtil ds = new DigSigUtil(clerkManager.getClientConfig().getDigitalSignatureConfiguration());
                businessEntity signed = (businessEntity)ds.signUddiEntity(complete);

                PrintUDDI<businessEntity> p = new PrintUDDI<businessEntity>();
                Console.Out.WriteLine("signed successfully!");
                if (serialize)
                    Console.Out.WriteLine(p.print(signed));

                Console.Out.WriteLine("attempting verify signature locally");
                String err = "";
                bool valid = ds.verifySignedUddiEntity(signed, out err);
                Console.Out.WriteLine("Signature is " + (valid ? "Valid, Yippy!" : "Invalid!") + " msg: " + err);
                Assert.True(valid);
                Assert.True(String.IsNullOrEmpty(err));

                Console.Out.WriteLine("saving to signed entity to the registry");
                clerk.register(signed);
                Console.Out.WriteLine("reloading content from the server...");

                get_businessDetail gsd = new get_businessDetail();
                gsd.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                gsd.businessKey = new string[] { signed.businessKey };
                businessDetail saved = inquiry.get_businessDetail(gsd);

                Console.Out.WriteLine("done. attempting verify signed entity");
                if (serialize)
                    Console.Out.WriteLine(p.print(saved.businessEntity[0]));

                err = "";
                valid = ds.verifySignedUddiEntity(saved.businessEntity[0], out err);
                Console.Out.WriteLine("Signature is " + (valid ? "Valid, Yippy!" : "Invalid!") + " msg: " + err);

            }
            catch (Exception ex)
            {
                while (ex != null)
                {
                    System.Console.WriteLine("Error! " + ex.Message);
                    ex = ex.InnerException;
                }
                throw ex;
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

        //TODO create in JAVA, save in juddi, download in .NET, verify sig is valid
    }
}
