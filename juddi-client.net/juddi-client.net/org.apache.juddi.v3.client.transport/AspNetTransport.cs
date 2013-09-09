/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
using org.apache.juddi.apiv3;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Text;

namespace org.apache.juddi.v3.client.transport
{

    public class AspNetTransport : Transport, IDisposable
    {//AspNetTransport 
        String nodeName = null;
        String clientName = null;
        UDDI_Inquiry_SoapBinding inquiryService = new UDDI_Inquiry_SoapBinding();
        UDDI_Security_SoapBinding securityService = new UDDI_Security_SoapBinding();
        UDDI_Publication_SoapBinding publishService = new UDDI_Publication_SoapBinding();
        UDDI_Subscription_SoapBinding subscriptionService = new UDDI_Subscription_SoapBinding();
        UDDI_SubscriptionListener_SoapBinding subscriptionListenerService = new UDDI_SubscriptionListener_SoapBinding();
        UDDI_CustodyTransfer_SoapBinding custodyTransferService = new UDDI_CustodyTransfer_SoapBinding();
        JUDDIApiService publisherService = new JUDDIApiService();
        private string managerName;
        private config.ClientConfig clientConfig;

        public AspNetTransport()
        {
            this.nodeName = Transport.DEFAULT_NODE_NAME;
        }


        public AspNetTransport(String nodeName)
        {
            this.nodeName = nodeName;
        }

        public AspNetTransport(String clientName, String nodeName)
        {

            this.clientName = clientName;
            this.nodeName = nodeName;

        }

        public AspNetTransport(string managerName, string nodeName, config.ClientConfig clientConfig)
        {
            // TODO: Complete member initialization
            this.managerName = managerName;
            this.nodeName = nodeName;
            this.clientConfig = clientConfig;
            this.inquiryService.Url = clientConfig.getUDDINode(nodeName).getInquiryUrl();

            this.inquiryService.Url = clientConfig.getUDDINode(nodeName).getInquiryUrl();
            this.publisherService.Url = clientConfig.getUDDINode(nodeName).getJuddiApiUrl();
            this.publishService.Url = clientConfig.getUDDINode(nodeName).getPublishUrl();
            this.securityService.Url = clientConfig.getUDDINode(nodeName).getSecurityUrl();
            this.custodyTransferService.Url = clientConfig.getUDDINode(nodeName).getCustodyTransferUrl();
            this.subscriptionService.Url = clientConfig.getUDDINode(nodeName).getSubscriptionUrl();
            this.subscriptionListenerService.Url = clientConfig.getUDDINode(nodeName).getSubscriptionListenerUrl();
        }

        public override JUDDIApiService getJUDDIApiService(string enpointURL)
        {
            this.publisherService.Url = enpointURL;
            return this.publisherService;
        }

        public override JUDDIApiService getJUDDIApiService()
        {
            return this.publisherService;
        }

        public override UDDI_Inquiry_SoapBinding getUDDIInquiryService(string enpointURL)
        {
            this.inquiryService.Url = enpointURL;
            return this.inquiryService;
        }

        public override UDDI_Security_SoapBinding getUDDISecurityService(string enpointURL)
        {
            this.securityService.Url = enpointURL;
            return this.securityService;
        }

        public override UDDI_Publication_SoapBinding getUDDIPublishService(string enpointURL)
        {
            this.publishService.Url = enpointURL;
            return this.publishService;
        }

        public override UDDI_Subscription_SoapBinding getUDDISubscriptionService(string enpointURL)
        {
            this.subscriptionService.Url = enpointURL;
            return this.subscriptionService;
        }

        public override UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService(string enpointURL)
        {
            this.custodyTransferService.Url = enpointURL;
            return this.custodyTransferService;
        }

        public override UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService(string enpointURL)
        {
            this.subscriptionListenerService.Url = enpointURL;
            return this.subscriptionListenerService;
        }

        public override UDDI_Inquiry_SoapBinding getUDDIInquiryService()
        {
            return this.inquiryService;
        }

        public override UDDI_Security_SoapBinding getUDDISecurityService()
        {
            return this.securityService;
        }

        public override UDDI_Publication_SoapBinding getUDDIPublishService()
        {
            return this.publishService;
        }

        public override UDDI_Subscription_SoapBinding getUDDISubscriptionService()
        {
            return this.subscriptionService;
        }

        public override UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService()
        {
            return this.subscriptionListenerService;
        }

        public override UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService()
        {
            return this.custodyTransferService;
        }

        public void Dispose()
        {
            inquiryService.Dispose();
            securityService.Dispose();
            publishService.Dispose();
            subscriptionService.Dispose();
            subscriptionListenerService.Dispose();
            custodyTransferService.Dispose();
            publisherService.Dispose();
        }
    }
}
