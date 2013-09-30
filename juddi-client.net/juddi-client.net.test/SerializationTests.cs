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
            XmlSerializer xr = new XmlSerializer(typeof(subscriptionFilter));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_categoryBag()
        {

            categoryBag r = new categoryBag();
            XmlSerializer xr = new XmlSerializer(typeof(categoryBag));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_find_relatedBusinesses()
        {

            find_relatedBusinesses r = new find_relatedBusinesses();
            XmlSerializer xr = new XmlSerializer(typeof(find_relatedBusinesses));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_keysOwned()
        {

            keysOwned r = new keysOwned();
            XmlSerializer xr = new XmlSerializer(typeof(keysOwned));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_bindingTemplate()
        {

            bindingTemplate r = new bindingTemplate();
            XmlSerializer xr = new XmlSerializer(typeof(bindingTemplate));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_instanceDetails()
        {

            instanceDetails r = new instanceDetails();
            XmlSerializer xr = new XmlSerializer(typeof(instanceDetails));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_TransformType()
        {

            TransformType r = new TransformType();
            XmlSerializer xr = new XmlSerializer(typeof(TransformType));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_KeyInfoType()
        {

            KeyInfoType r = new KeyInfoType();
            XmlSerializer xr = new XmlSerializer(typeof(KeyInfoType));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_KeyValueType()
        {

            KeyValueType r = new KeyValueType();
            XmlSerializer xr = new XmlSerializer(typeof(KeyValueType));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }



        [Test]
        public void xr_PGPDataType()
        {

            PGPDataType r = new PGPDataType();
            XmlSerializer xr = new XmlSerializer(typeof(PGPDataType));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_X509DataType()
        {

            X509DataType r = new X509DataType();
            XmlSerializer xr = new XmlSerializer(typeof(X509DataType));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_keyBag1()
        {

            keyBag1 r = new keyBag1();
            XmlSerializer xr = new XmlSerializer(typeof(keyBag1));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }


        [Test]
        public void xr_SignaturePropertyType()
        {

            SignaturePropertyType r = new SignaturePropertyType();
            XmlSerializer xr = new XmlSerializer(typeof(SignaturePropertyType));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_validate_values()
        {

            validate_values r = new validate_values();
            XmlSerializer xr = new XmlSerializer(typeof(validate_values));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }
        [Test]
        public void xr_discard_transferToken()
        {

            discard_transferToken r = new discard_transferToken();
            XmlSerializer xr = new XmlSerializer(typeof(discard_transferToken));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_get_changeRecords()
        {

            get_changeRecords r = new get_changeRecords();
            XmlSerializer xr = new XmlSerializer(typeof(get_changeRecords));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

        [Test]
        public void xr_changeRecordNewData()
        {

            changeRecordNewData r = new changeRecordNewData();
            XmlSerializer xr = new XmlSerializer(typeof(changeRecordNewData));
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
            XmlSerializer xr = new XmlSerializer(typeof(overviewDoc));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }

    }
}
