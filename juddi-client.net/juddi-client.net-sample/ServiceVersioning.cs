using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.client.sample
{
    /// <summary>
    /// 
    /// Hello world!
    /// This gives you an example of one way to use service version with UDDI and is
    /// a partial solution to https://issues.apache.org/jira/browse/JUDDI-509
    /// http://www.ibm.com/developerworks/webservices/library/ws-version/
    /// </summary>
    public class ServiceVersioning
    {
        public void go()
        {

            Init();

            Setup();

            ServiceLookUpByName();

            ServiceLookUpByVersion();

            Destroy();
        }


        /**
         * This will setup new a business, service, and binding template that's
         * versioned per the article linked above
         */
        private void Setup()
        {

            businessEntity be = new businessEntity();
            keygen = clerk.register(UDDIClerk.createKeyGenator(domain_prefix + "keygenerator", "my domain", lang)).tModel[0];

            be.businessKey = (domain_prefix + "zerocoolbiz");
            be.name = new name[] { new name("ZeroCool Business", lang) };
            businessService bs = new businessService();
            bs.name = new name[] { new name("ZeroCool Service", lang) };

            bs.businessKey = (domain_prefix + "zerocoolbiz");
            bs.serviceKey = (domain_prefix + "zerocoolsvc");

            //version 1
            bindingTemplate bt1 = new bindingTemplate();
            String version = "1.0.0.0";
            bt1.bindingKey = (domain_prefix + "binding10");
            bt1.Item = (new accessPoint("http://localhost", "wsdl")) ;
            bt1.tModelInstanceDetails = new tModelInstanceInfo[] { UDDIClerk.createServiceInterfaceVersion(version, lang) };

            //version 2
            bindingTemplate bt2 = new bindingTemplate();
            bt2.bindingKey = (domain_prefix + "binding12");
            String version2 = "1.2.0.0";
            bt2.Item =  (new accessPoint("http://localhost", "wsdl")) ;
            bt2.tModelInstanceDetails = new tModelInstanceInfo[] { UDDIClerk.createServiceInterfaceVersion(version2, lang) };

            bs.bindingTemplates = new bindingTemplate[] { bt1, bt2 };
            be.businessServices = new businessService[] { bs };
            clerk.register(be);

        }
        private String domain_prefix = "uddi:mydomain.com:";

        private String lang = "en";

        /**
         * this will look up our service by name and we'll discover multiple
         * bindings with different versions
         */
        private void ServiceLookUpByName()
        {

            //here we are assuming we don't know what key is used for the service, so we look it up
            find_service fs = new find_service();
            fs.findQualifiers = new string[] { UDDIConstants.EXACT_MATCH };
            fs.name = new name[] { new name("ZeroCool Service", lang) };
            serviceList findService = inquiry.find_service(fs);

            //parse the results and get a list of services to get the details on
            get_serviceDetail gsd = new get_serviceDetail();
            List<string> items = new List<string>();
            for (int i = 0; i < findService.serviceInfos.Length; i++)
            {
                items.Add(findService.serviceInfos[i].serviceKey);
            }
            gsd.serviceKey = items.ToArray();
            serviceDetail serviceDetail = inquiry.get_serviceDetail(gsd);
            List<bindingTemplate> ret = new List<bindingTemplate>();
            //parse the service details, looking for our versioned service
            Boolean success = false;
            if (serviceDetail != null)
            {
                for (int i = 0; i < serviceDetail.businessService.Length; i++)
                {
                    if (serviceDetail.businessService[i].bindingTemplates != null)
                    {
                        List<bindingTemplate> bindingByVersion = UDDIClerk.getBindingByVersion("1.2.0.0",
                            serviceDetail.businessService[i].bindingTemplates);
                        for (int x = 0; x < bindingByVersion.Count; x++)
                        {
                            success = true;
                            Console.Out.WriteLine("SUCCESS! Found the right version on key " + bindingByVersion[x].bindingKey);
                            //TODO so now that you've found the right version of the right service
                            //now you can go execute that the url
                        }

                    }
                }
            }
            if (!success)
            {
                Console.Out.WriteLine("FAIL! We didn't find a damn thing matching that version number :(" );
            }
        }

        /**
         * this will look up our service by name and version number
         */
        private void ServiceLookUpByVersion()
        {
            //here we are assuming we don't know what key is used for the service, so we look it up
            //TODO
        }
        private tModel keygen = null;
        private UDDI_Security_SoapBinding security = null;
        private UDDI_Inquiry_SoapBinding inquiry = null;
        private UDDI_Publication_SoapBinding publish = null;
        private UDDIClient client = null;
        private UDDIClerk clerk = null;

        private void Init() {
        try {
            // create a client and read the config in the archive; 
            // you can use your config file name
            client = new UDDIClient("uddi.xml");
            clerk = client.getClerk("default");
            // a UddiClient can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = client.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            inquiry = transport.getUDDIInquiryService();
            publish = transport.getUDDIPublishService();
        } catch (Exception e) {
            while (e!=null)
            {
                Console.Out.WriteLine(e.Message + Environment.NewLine + e.StackTrace + Environment.NewLine);
                e = e.InnerException;
            }
        }

    }

        private void Destroy()
        {
            clerk.unRegisterBusiness(domain_prefix + "zerocoolbiz");
            clerk.unRegisterTModel(keygen.tModelKey);
        }
    }
}
