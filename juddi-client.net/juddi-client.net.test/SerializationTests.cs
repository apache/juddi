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
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
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

        #region basic serialization tests
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
            description d = new description("v", "en");
            overviewURL ol = new overviewURL("http://url", "website");

            r.Items = new object[] { d, ol };
            XmlSerializer xr = new XmlSerializer(typeof(overviewDoc));
            StringWriter sw = new StringWriter();
            xr.Serialize(sw, r);
            System.Console.Out.WriteLine(sw.ToString());
        }
        #endregion





        #region calls using web services

        [Test]
        public void xr_subscriptionResultsListws()
        {
            subscriptionResultsList r = new subscriptionResultsList();
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient("uddi.xml");
                UDDIClientContainer.addClient(clerkManager);
                transport = clerkManager.getTransport("default");
                org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                org.uddi.apiv3.UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();
                org.uddi.apiv3.UDDI_CustodyTransfer_SoapBinding custody = transport.getUDDICustodyTransferService();
                org.uddi.apiv3.UDDI_Subscription_SoapBinding sub = transport.getUDDISubscriptionService();
                clerk = clerkManager.getClerk("default");

            }
            catch (Exception ex)
            {
                while (ex != null)
                {
                    System.Console.WriteLine("Error! " + ex.Message);
                    ex = ex.InnerException;
                }
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

        [Test]
        public void xr_subscriptionFilterws()
        {
            Console.Out.WriteLine("xr_subscriptionFilterws");
            subscriptionFilter r = new subscriptionFilter();
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient("uddi.xml");
                UDDIClientContainer.addClient(clerkManager);
                transport = clerkManager.getTransport("default");
                org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                org.uddi.apiv3.UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();
                org.uddi.apiv3.UDDI_CustodyTransfer_SoapBinding custody = transport.getUDDICustodyTransferService();
                org.uddi.apiv3.UDDI_Subscription_SoapBinding sub = transport.getUDDISubscriptionService();
                clerk = clerkManager.getClerk("default");

                save_subscription ss = new save_subscription();
                ss.subscription = new subscription[] { new subscription() };
                ss.subscription[0].subscriptionFilter = r;
                sub.save_subscription(ss);
            }
            catch (Exception ex)
            {
                String s = "";
                while (ex != null)
                {
                    s = s + ex.Message;
                    ex = ex.InnerException;
                }
                if (ContainsSerializationError(s))
                    Assert.Fail(s);

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

        private bool ContainsSerializationError(string s)
        {

            if (s.Contains("There was an error reflecting type"))
                return true;
            return false;
        }
        /*
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
        }*/

       

    }


        #endregion






}

