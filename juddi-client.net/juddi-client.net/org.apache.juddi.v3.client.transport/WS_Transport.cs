using org.apache.juddi.apiv3;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Text;

namespace org.apache.juddi.v3.client.transport
{

    public class WS_Transport : Transport
    {
        String nodeName = null;
        String clientName = null;
        UDDI_Inquiry_SoapBinding inquiryService = null;
        UDDI_Security_SoapBinding securityService = null;
        UDDI_Publication_SoapBinding publishService = null;
        UDDI_Subscription_SoapBinding subscriptionService = null;
        UDDI_SubscriptionListener_SoapBinding subscriptionListenerService = null;
        UDDI_CustodyTransfer_SoapBinding custodyTransferService = null;
        JUDDIApiService publisherService = null;

        public WS_Transport()
        {
            this.nodeName = Transport.DEFAULT_NODE_NAME;
        }


        public WS_Transport(String nodeName)
        {
            this.nodeName = nodeName;
        }

        public WS_Transport(String clientName, String nodeName)
        {

            this.clientName = clientName;
            this.nodeName = nodeName;
        }

        public override JUDDIApiService getJUDDIApiService(string enpointURL)
        {
            throw new NotImplementedException();
        }

        public override JUDDIApiService getJUDDIApiService()
        {
            throw new NotImplementedException();
        }

        public override UDDI_Inquiry_SoapBinding getUDDIInquiryService(string enpointURL)
        {
            throw new NotImplementedException();
        }

        public override UDDI_Security_SoapBinding getUDDISecurityService(string enpointURL)
        {
            throw new NotImplementedException();
        }

        public override UDDI_Publication_SoapBinding getUDDIPublishService(string enpointURL)
        {
            throw new NotImplementedException();
        }

        public override UDDI_Subscription_SoapBinding getUDDISubscriptionService(string enpointURL)
        {
            throw new NotImplementedException();
        }

        public override UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService(string enpointURL)
        {
            throw new NotImplementedException();
        }

        public override UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService(string enpointURL)
        {
            throw new NotImplementedException();
        }

        public override UDDI_Inquiry_SoapBinding getUDDIInquiryService()
        {
            throw new NotImplementedException();
        }

        public override UDDI_Security_SoapBinding getUDDISecurityService()
        {
            throw new NotImplementedException();
        }

        public override UDDI_Publication_SoapBinding getUDDIPublishService()
        {
            throw new NotImplementedException();
        }

        public override UDDI_Subscription_SoapBinding getUDDISubscriptionService()
        {
            throw new NotImplementedException();
        }

        public override UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService()
        {
            throw new NotImplementedException();
        }

        public override UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService()
        {
            throw new NotImplementedException();
        }
    }
}
