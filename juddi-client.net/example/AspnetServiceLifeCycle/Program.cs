using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.annotations;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;


namespace AspnetServiceLifeCycle
{
    class Program
    {
        static void Main(string[] args)
        {
            Type t = typeof(HelloImpl);
            Console.Out.WriteLine(t.AssemblyQualifiedName);
            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;

            clerkManager = new UDDIClient("uddi.xml");
            UDDIClientContainer.addClient(clerkManager);

            transport = clerkManager.getTransport("default");

            org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
            org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService ();

            clerk = clerkManager.getClerk("default");
            clerkManager.registerAnnotatedServices();
            HelloImpl x = new HelloImpl();
            clerkManager.unRegisterAnnotatedServices();
            Thread.Sleep(15000);

        }
    }

    [UDDIService(businessKey = "uddi:juddi.apache.org:businesses-asf", description = "My custom service via annotations",
        serviceName = "HelloImpl, such a cool name", lang = "en", serviceKey = "uddi:juddi.apache.org:services-customHelloImpl")]
    [UDDIServiceBinding(accessPoint="http://localhost/here", accessPointType="endPoint")]
    class HelloImpl : IHello_Binding
    {
        public string sayHello(string firstName)
        {
            return "hi";
        }
    }
}
