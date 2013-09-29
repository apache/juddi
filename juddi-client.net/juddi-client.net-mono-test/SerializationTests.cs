using NUnit.Framework;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Xml.Serialization;


namespace juddi_client.net_mono_test
{

    [TestFixture]
    public class SerializationTests
    {

        [Test]
        public void xr_subscriptionResultsList()
        {

            subscriptionResultsList r = new subscriptionResultsList();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_subscriptionFilter()
        {

            subscriptionFilter r = new subscriptionFilter();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_categoryBag()
        {

            categoryBag r = new categoryBag();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_find_relatedBusinesses()
        {

            find_relatedBusinesses r = new find_relatedBusinesses();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_keysOwned()
        {

            keysOwned r = new keysOwned();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_bindingTemplate()
        {

            bindingTemplate r = new bindingTemplate();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_instanceDetails()
        {

            instanceDetails r = new instanceDetails();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_TransformType()
        {

            TransformType r = new TransformType();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_KeyInfoType()
        {

            KeyInfoType r = new KeyInfoType();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_KeyValueType()
        {

            KeyValueType r = new KeyValueType();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }



        [Test]
        public void xr_PGPDataType()
        {

            PGPDataType r = new PGPDataType();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_X509DataType()
        {

            X509DataType r = new X509DataType();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_keyBag1()
        {

            keyBag1 r = new keyBag1();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_SignaturePropertyType()
        {

            SignaturePropertyType r = new SignaturePropertyType();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_validate_values()
        {

            validate_values r = new validate_values();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }
        [Test]
        public void xr_discard_transferToken()
        {

            discard_transferToken r = new discard_transferToken();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_get_changeRecords()
        {

            get_changeRecords r = new get_changeRecords();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_changeRecordNewData()
        {

            changeRecordNewData r = new changeRecordNewData();
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_overviewDoc()
        {

            overviewDoc r = new overviewDoc();
            description d = new description("v","en");
            overviewURL ol = new overviewURL("http://url","website");
            
            r.Items = new object[] {d ,ol};
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionResultsList));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

    }
}
