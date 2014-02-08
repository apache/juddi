using org.apache.juddi.apiv3;
using org.apache.juddi.v3.client.transport.wrapper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.transport
{
    public class AspNetv2TranslationTransport : AspNetTransport
    {

        String nodeName = null;
        String clientName = null;
        Inquiry3to2 inquiryService = new Inquiry3to2();
        Publish3to2 publishService = new Publish3to2();
        Security3to2 securityService = new Security3to2();
        
        JUDDIApiService publisherService = new JUDDIApiService();
        private string managerName;
        private config.ClientConfig clientConfig;


        public AspNetv2TranslationTransport(string managerName, string nodeName, config.ClientConfig clientConfig)
        {
            this.managerName = managerName;
            this.nodeName = nodeName;
            this.clientConfig = clientConfig;
            this.inquiryService.Url = clientConfig.getUDDINode(nodeName).getInquiryUrl();
            this.publisherService.Url = clientConfig.getUDDINode(nodeName).getJuddiApiUrl();
            this.publishService.Url = clientConfig.getUDDINode(nodeName).getPublishUrl();
            this.securityService.Url = clientConfig.getUDDINode(nodeName).getSecurityUrl();
           
        }
        public override uddi.apiv3.UDDI_Inquiry_SoapBinding getUDDIInquiryService()
        {
            return inquiryService;
        }
        public override uddi.apiv3.UDDI_Inquiry_SoapBinding getUDDIInquiryService(string endpointURL)
        {
            return new Inquiry3to2(endpointURL);
        }
        public override uddi.apiv3.UDDI_Publication_SoapBinding getUDDIPublishService(string endpointURL)
        {
            return new Publish3to2(endpointURL);
        }
        public override uddi.apiv3.UDDI_Publication_SoapBinding getUDDIPublishService()
        {
            return this.publishService;
        }
        public override uddi.apiv3.UDDI_Security_SoapBinding getUDDISecurityService()
        {
            return this.securityService;
        }

        public override uddi.apiv3.UDDI_Security_SoapBinding getUDDISecurityService(string endpointURL)
        {
            return new Security3to2(endpointURL);
        }

    }
}
