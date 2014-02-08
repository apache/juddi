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
using System;
using System.Collections.Generic;
using System.Text;
using org.uddi.apiv3;
using org.apache.juddi.apiv3;
namespace org.apache.juddi.v3.client.transport
{
   public abstract class Transport
    {
       	
	public readonly static String DEFAULT_NODE_NAME             = "default";
	
	public abstract UDDI_Inquiry_SoapBinding getUDDIInquiryService(String endpointURL);//           throws TransportException;
	public abstract UDDI_Security_SoapBinding getUDDISecurityService(String endpointURL);//         throws TransportException;
	public abstract UDDI_Publication_SoapBinding getUDDIPublishService(String endpointURL);//       throws TransportException;
	public abstract UDDI_Subscription_SoapBinding getUDDISubscriptionService(String endpointURL);// throws TransportException;
	public abstract UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService(String endpointURL);// throws TransportException;
	public abstract UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService(String endpointURL);// throws TransportException;
	public abstract JUDDIApiService getJUDDIApiService(String endpointURL);// throws TransportException;
	
	public abstract UDDI_Inquiry_SoapBinding getUDDIInquiryService();// throws TransportException {
		
	public abstract UDDI_Security_SoapBinding getUDDISecurityService();
	public abstract UDDI_Publication_SoapBinding getUDDIPublishService();

    public abstract UDDI_Subscription_SoapBinding getUDDISubscriptionService();
    public abstract UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService();
    public abstract UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService();
    public abstract JUDDIApiService getJUDDIApiService();

    }
}
