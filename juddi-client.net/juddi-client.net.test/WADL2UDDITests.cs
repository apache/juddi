using net.java.dev.wadl;
using NUnit.Framework;
using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.mapping;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace juddi_client.net.test
{
    [TestFixture]
    public class WADL2UDDITests
    {
        bool serialize = false;
        string path = "";
        public WADL2UDDITests()
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
                path = Directory.GetCurrentDirectory() + Path.DirectorySeparatorChar + ".." + Path.DirectorySeparatorChar + ".." + Path.DirectorySeparatorChar + "resources";
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
        public void zootoolTest()
        {
            Assume.That(path != null);
            runTest(path + Path.DirectorySeparatorChar + "sample.wadl");
        }

        [Test]
        public void cxfTest()
        {
            Assume.That(path != null);
            runTest(path + Path.DirectorySeparatorChar + "cxf.wadl");
        }

        [Test]
        public void deliciouseTest()
        {
            Assume.That(path != null);
            runTest(path + Path.DirectorySeparatorChar + "deliciouse.wadl");
        }

        [Test]
        public void diggotest()
        {
            Assume.That(path != null);
            runTest(path + Path.DirectorySeparatorChar + "diggo.wadl");
        }


        [Test]
        public void hbookmarkstest()
        {
            Assume.That(path != null);
            runTest(path + Path.DirectorySeparatorChar + "hbookmarks.wadl");
        }

        void runTest(String pathAndFile)
        {
            Assume.That(File.Exists(pathAndFile));
            //Wadl Import example

            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            clerkManager = new UDDIClient("uddi.xml");
            UDDIClientContainer.addClient(clerkManager);

            transport = clerkManager.getTransport("default");
            clerk = clerkManager.getClerk("default");

            application app = WADL2UDDI.ParseWadl(pathAndFile);
            List<Uri> urls = WADL2UDDI.GetBaseAddresses(app);
            Assert.True(urls.Count > 0);
            Uri url = urls[0];
            String domain = url.Host;

            tModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");
            Assert.NotNull(keygen);
            Assert.NotNull(keygen.tModelKey);
            Properties properties = new Properties();
            properties.put("keyDomain", domain);
            properties.put("businessName", domain);
            properties.put("serverName", url.Host);
            properties.put("serverPort", url.Port.ToString());
            WADL2UDDI wadl2UDDI = new WADL2UDDI(clerk, properties); 
            businessService businessServices = wadl2UDDI.createBusinessService(new QName("MyWasdl.namespace", "Servicename"), app);
            if (serialize)
                Console.Out.WriteLine(new PrintUDDI<businessService>().print(businessServices));
            Assert.NotNull(businessServices);
            Assert.NotNull(businessServices.bindingTemplates);
            foreach (bindingTemplate bt in businessServices.bindingTemplates)
            {
                Assert.NotNull(bt);
                Assert.NotNull(bt.bindingKey);
                Assert.NotNull(bt.Item);
                Assert.NotNull(bt.serviceKey);
                Assert.True(bt.Item is accessPoint);
                Assert.NotNull(((accessPoint)bt.Item).useType);
                Assert.NotNull(((accessPoint)bt.Item).Value);
            }
            Assert.True(businessServices.bindingTemplates.Length > 0);
            Assert.NotNull(businessServices.description);
            Assert.True(businessServices.description.Length > 0);
            Assert.NotNull(businessServices.serviceKey);


            
        }
    }
}
