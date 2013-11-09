using net.java.dev.wadl;
using NUnit.Framework;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.mapping;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using org.xmlsoap.schemas.easyWsdl;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace juddi_client.net.test
{
    [TestFixture]
    public class WSDL2UDDITest
    {
        string path = "";
        public WSDL2UDDITest()
        {
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
        public void juddiapiflattenedTest()
        {
            Assume.That(path != null);
            runTest(path + Path.DirectorySeparatorChar + "juddi-api-flattened.wsdl");
        }


        [Test]
        public void ReadWSDLTest1()
        {
            ReadWSDL wsi = new ReadWSDL();
            try
            {
                org.xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition = wsi.readWSDL(null);
            }
            catch (ArgumentNullException)
            {

            }
        }

        [Test]
        public void WSDL2UDDITest2()
        {
            try
            {
                WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, null, null);
            }
            catch (ArgumentNullException)
            {

            }
        }


        void runTest(String pathAndFile)
        {
            Assume.That(File.Exists(pathAndFile));

            String wsdlURL = "http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL";
            ReadWSDL wsi = new ReadWSDL();
            org.xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition = wsi.readWSDL(
               pathAndFile
                );
            Properties properties1 = new Properties();
            properties1.put("keyDomain", "my.key.domain");
            WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizer(), properties1);
            Assert.NotNull(wsdl2UDDI);

            Dictionary<QName, tPortType> portTypes1 = (Dictionary<QName, tPortType>)wsdlDefinition.getAllPortTypes();
            Assert.NotNull(portTypes1);
            Assert.True(portTypes1.Count > 0);
            List<tModel> portTypeTModels1 = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes1);

            Assert.NotNull(portTypeTModels1);
            Assert.True(portTypeTModels1.Count > 0);


            Dictionary<QName, tBinding> allBindings1 = wsdlDefinition.getAllBindings();
            Assert.NotNull(allBindings1);
            Assert.True(allBindings1.Count > 0);
            List<tModel> createWSDLBindingTModels1 = wsdl2UDDI.createWSDLBindingTModels(wsdlURL, allBindings1);
            Assert.NotNull(createWSDLBindingTModels1);
            Assert.True(createWSDLBindingTModels1.Count > 0);

            businessService[] businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);

            Assert.NotNull(businessServices);
            Assert.True(businessServices.Length > 0);
            for (int i = 0; i < businessServices.Length; i++)
            {
                foreach (bindingTemplate bt in businessServices[i].bindingTemplates)
                {
                    Assert.NotNull(bt);
                    Assert.NotNull(bt.bindingKey);
                    Assert.NotNull(bt.Item);
                    Assert.NotNull(bt.serviceKey);
                    Assert.True(bt.Item is accessPoint);
                    Assert.NotNull(((accessPoint)bt.Item).useType);
                    Assert.NotNull(((accessPoint)bt.Item).Value);
                }
                Assert.True(businessServices[i].bindingTemplates.Length > 0);
                Assert.NotNull(businessServices[i].description);
                Assert.True(businessServices[i].description.Length > 0);
                Assert.NotNull(businessServices[i].serviceKey);
            }


        }

    }
}
