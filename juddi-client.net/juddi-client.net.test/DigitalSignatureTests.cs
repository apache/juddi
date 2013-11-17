using NUnit.Framework;
using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.cryptor;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace juddi_client.net.test
{
    [TestFixture]
    public class DigitalSignatureTests
    {
        org.apache.juddi.v3.client.cryptor.DigSigUtil ds = null;
        string path = "";
        bool serialize = false;
        public DigitalSignatureTests()
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
            ds = new DigSigUtil();
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, path +  Path.DirectorySeparatorChar+"cert.pfx");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "PFX");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "password");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "selfsigned");      //the friendly name = alias
            //ds.put(DigSigUtil.TRUSTSTORE_FILE, "./src/test/resources/truststore.jks");
            //ds.put(DigSigUtil.TRUSTSTORE_FILETYPE, "JKS");
            //ds.put(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, "Test");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "true");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "true");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "true");
            ds.put(DigSigUtil.CHECK_TIMESTAMPS, "true");
            
        }

        void resetDS()
        {
            ds = new DigSigUtil();
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, path + Path.DirectorySeparatorChar + "cert.pfx");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "PFX");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "password");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "selfsigned");      //the friendly name = alias
            //ds.put(DigSigUtil.TRUSTSTORE_FILE, "./src/test/resources/truststore.jks");
            //ds.put(DigSigUtil.TRUSTSTORE_FILETYPE, "JKS");
            //ds.put(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, "Test");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "true");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "true");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "true");
        }

        [Test]
        public void Business()
        {

            businessEntity be = new businessEntity();
            be.businessKey = "uddi:business";
            be.businessServices = new businessService[] { NewBusinessService() };
            be.name = new name[] { new name("stuff", null) };
            be.identifierBag = new keyedReference[] { new keyedReference("key", "name", "val") };
            be.categoryBag = new categoryBag();
            be.categoryBag.Items = new object[] { new keyedReference("key", "name", "val") };
            be.contacts = new contact[] { new contact() };

            be.contacts[0].useType = "admin";
            be.contacts[0].personName = new personName[] { new personName("bob", "en") };
            be.businessServices = new businessService[] { getService() };

            SigningAndVerify(be);
        }

        private businessService NewBusinessService()
        {
            businessService bs = new businessService();
            bs.bindingTemplates = new bindingTemplate[] { NewBindingTempalte() };
            bs.businessKey = "asd";
            bs.categoryBag = new categoryBag();
            bs.categoryBag.Items = new object[] { new keyedReference("key", "name", "val") };
            bs.name = new name[] { new name("stuff", null) };
            bs.serviceKey = "asd";
            return bs;
        }

        private bindingTemplate NewBindingTempalte()
        {
            bindingTemplate bt = new bindingTemplate();
            bt.bindingKey = "asd";
            bt.categoryBag = new categoryBag();
            bt.categoryBag.Items = new object[] { new keyedReference("key", "name", "val") };
            bt.description = new description[] { new description("hi", "en") };
            bt.Item = new accessPoint("http://localhost", "wsdl");
            bt.serviceKey = "svc";
            bt.tModelInstanceDetails = new tModelInstanceInfo[] { new tModelInstanceInfo() };
            return bt;
        }
        [Test]
        public void Service()
        {
            businessService bs = NewBusinessService();
            SigningAndVerify(bs);
        }

        [Test]
        public void Tmodel()
        {
            tModel bs = UDDIClerk.createKeyGenator("part", "desc", "en");
            
            SigningAndVerify(bs);
        }

        private businessService getService()
        {
            businessService bs = new businessService();
            bs.serviceKey = "hello";
            bs.name = new name[] { new name("hi", "en") };
            bs.businessKey = "asd";
            return bs;

        }

        void SigningAndVerify(object uddielement)
        {

            uddielement = ds.signUddiEntity(uddielement);
            Assert.NotNull(uddielement);
            if (serialize)
            {
                if (uddielement.GetType().Equals(typeof(bindingTemplate)))
                {
                    PrintUDDI<bindingTemplate> p = new PrintUDDI<bindingTemplate>();
                    Console.Out.WriteLine(p.print(uddielement));

                }
                if (uddielement.GetType().Equals(typeof(businessService)))
                {
                    PrintUDDI<businessService> p = new PrintUDDI<businessService>();
                    Console.Out.WriteLine(p.print(uddielement));
                }
                if (uddielement.GetType().Equals(typeof(businessEntity)))
                {
                    PrintUDDI<businessEntity> p = new PrintUDDI<businessEntity>();
                    Console.Out.WriteLine(p.print(uddielement));
                }
                if (uddielement.GetType().Equals(typeof(tModel)))
                {
                    PrintUDDI<tModel> p = new PrintUDDI<tModel>();
                    Console.Out.WriteLine(p.print(uddielement));
                }
            }
            String err="";
            bool check=ds.verifySignedUddiEntity(uddielement, out err);
            Console.Out.WriteLine("Signature status is " + check + ": " + err);
            Assert.True(check);
            Assert.True(String.IsNullOrEmpty(err));
            validAllSignatureElementsArePresent(uddielement);
        }

        private void validAllSignatureElementsArePresent(object uddielement)
        {
            if (uddielement.GetType().Equals(typeof(bindingTemplate)))
            {
                validAllSignatureElementsArePresent(((bindingTemplate)uddielement).Signature);
            }
            if (uddielement.GetType().Equals(typeof(businessService)))
            {
                validAllSignatureElementsArePresent(((businessService)uddielement).Signature);
            }
            if (uddielement.GetType().Equals(typeof(businessEntity)))
            {
                validAllSignatureElementsArePresent(((businessEntity)uddielement).Signature);
            }
            if (uddielement.GetType().Equals(typeof(tModel)))
            {
                validAllSignatureElementsArePresent(((tModel)uddielement).Signature);
            }
        }

        private void validAllSignatureElementsArePresent(SignatureType[] signatureType)
        {
            if (signatureType == null || signatureType.Length == 0)
                Assert.Fail("no signed");
            for (int i = 0; i < signatureType.Length; i++)
            {
                Assert.NotNull(signatureType[i].KeyInfo);
                Assert.NotNull(signatureType[i].KeyInfo.Items);

            }
        }

    }
}
