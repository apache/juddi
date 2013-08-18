using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Text;


namespace juddi_client.net_sample
{
    class Program
    {
        static void Main(string[] args)
        {
            UDDIClient clerkManager = new UDDIClient("uddi.xml");
            UDDIClientContainer.addClient(clerkManager);
            Transport transport = clerkManager.getTransport("default");

            org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
            org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
            ClientConfig cfg = new ClientConfig("uddi.xml");
            
            find_business fb = new find_business();
            fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
            fb.name = new name[1];
            fb.name[0] = new name(UDDIConstants.WILDCARD, "en");
            businessList bl = inquiry.find_business(fb);
            for (int i = 0; i < bl.businessInfos.Length; i++)
            {
                Console.WriteLine(bl.businessInfos[i].name[0].Value);
            }
            Console.Read();

        }
    }
}
