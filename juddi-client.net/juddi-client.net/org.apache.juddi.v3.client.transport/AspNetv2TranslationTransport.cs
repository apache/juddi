/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

using org.apache.juddi.apiv3;
using org.apache.juddi.v3.client.transport.wrapper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.transport
{
    public class AspNetv2TranslationTransport : AspNetTransport, IDisposable
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
            this.securityService.Url = clientConfig.getUDDINode(nodeName).getPublishUrl();
           
        }
        public override uddi.apiv3.UDDI_Inquiry_SoapBinding getUDDIInquiryService()
        {
            return inquiryService;
        }
        /// <summary>
        /// callers must call .Dispose of returned objects
        /// </summary>
        /// <param name="endpointURL"></param>
        /// <returns></returns>
        public override uddi.apiv3.UDDI_Inquiry_SoapBinding getUDDIInquiryService(string endpointURL)
        {
            return new Inquiry3to2(endpointURL);
        }
        /// <summary>
        /// callers must call .Dispose of returned objects
        /// </summary>
        /// <param name="endpointURL"></param>
        /// <returns></returns>
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
        /// <summary>
        /// callers must call .Dispose of returned objects
        /// </summary>
        /// <param name="endpointURL"></param>
        /// <returns></returns>
        public override uddi.apiv3.UDDI_Security_SoapBinding getUDDISecurityService(string endpointURL)
        {
            return new Security3to2(endpointURL);
        }

        public void Dispose()
        {
            inquiryService.Dispose();
            publishService.Dispose();
            publisherService.Dispose();
            securityService.Dispose();
            base.Dispose();
        }
        
    }
}
