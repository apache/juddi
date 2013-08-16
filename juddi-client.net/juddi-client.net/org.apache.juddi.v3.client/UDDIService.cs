using System;
using System.Collections.Generic;
using System.Text;
using org.uddi.apiv3;
namespace juddi_dotnet
{
    public class UDDIService
    {
        public static UDDI_Inquiry_SoapBinding getUDDIInquiryPort()
        {
            return new UDDI_Inquiry_SoapBinding();
        }
        public static UDDI_Inquiry_SoapBinding getUDDIInquiryPort(String endpoint)
        {
            UDDI_Inquiry_SoapBinding i = new UDDI_Inquiry_SoapBinding();
            i.Url = endpoint;
            return i;
        }
        public static UDDI_Publication_SoapBinding getUDDIPublicationPort()
        {
            return new UDDI_Publication_SoapBinding();
        }
        public static UDDI_Publication_SoapBinding getUDDIPublicationPort(String endpoint)
        {
            UDDI_Publication_SoapBinding i = new UDDI_Publication_SoapBinding();
            i.Url = endpoint;
            return i;
        }


    }
}
