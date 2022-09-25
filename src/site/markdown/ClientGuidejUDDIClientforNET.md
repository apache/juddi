## jUDDI Client for NET

Since 3.2, the majority of the functions in the jUDDI Client for Java have been ported to .NET. This guide will show you how to use it and integrate it with your own .NET based applications.

### Procedure

1. Add a reference to jUDDI-Client.NET.dll
3. Add a reference to System.Web.Services
4. Add a reference to System.ServiceModel
5. Add a reference to System.Xml
6. Add a reference to System.Runtime.Serialization
7. Add a reference to System.Configuration
8. Add a reference to System.Security
9. Add a copy of the sample uddi.xml file. Modify it to meet your environment and operational needs.

Note, many of the settings are identical to the Java jUDDI-client. The APIs are also nearly identical, so example code should be easily portable from one language to another.

Sample Code

````

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

            org.uddi.apiv3.UDDI_Security_SoapBinding security = 
				transport.getUDDISecurityService();
            org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = 
				transport.getUDDIInquiryService();

            UDDIClerk clerk = clerkManager.getClerk("default");
            
           
            find_business fb = new find_business();
            fb.authInfo = clerk.getAuthToken(security.Url);
            fb.findQualifiers = new string[] { U
				DDIConstants.APPROXIMATE_MATCH };
            fb.name = new name[1];
            fb.name[0] = new name(UDDIConstants.WILDCARD, "en");
            businessList bl = inquiry.find_business(fb);
            for (int i = 0; i < bl.businessInfos.Length; i++)
            {
                Console.WriteLine(bl.businessInfos[i].name[0].
					Value);
            }
            Console.Read();

        }
    }
}

````

The sample code above should print out a list of all businesses currently registered in the registry. If credentials are stored in the uddi.xml file and are encrypted, they will be decrypted automatically for you.

Within the jUDDI Source Tree, there are many different examples of how to use the jUDDI Client for .NET. They are available here: http://svn.apache.org/repos/asf/juddi/trunk/juddi-client.net/juddi-client.net-sample/
