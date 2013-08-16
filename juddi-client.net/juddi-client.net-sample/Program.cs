using org.apache.juddi.v3.client;
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
            org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = new org.uddi.apiv3.UDDI_Inquiry_SoapBinding(

                "http://uddi-jbossoverlord.rhcloud.com/services/inquiry");
            find_business fb = new find_business();
            fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
            fb.name = new name[1];
            fb.name[0] = new name(UDDIConstants.WILDCARD, "en");
            businessList bl = inquiry.find_business(fb);
            for (int i = 0; i < bl.businessInfos.Length; i++)
            {
                Console.WriteLine(bl.businessInfos[i].name[0].Value);
            }

        }
    }
}
