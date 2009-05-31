/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.uddi.api_v3.client.transport;

import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


public class InVMTransport implements Transport {

	UDDIInquiryPortType inquiryService = null;
	UDDISecurityPortType securityService = null;
	UDDIPublicationPortType publishService = null;
	UDDISubscriptionPortType subscriptionService = null;
	UDDISubscriptionListenerPortType subscriptionListenerService = null;
	UDDICustodyTransferPortType custodyTransferService = null;

	public UDDIInquiryPortType getInquiryService() throws TransportException {
		if (inquiryService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_INQUIRY_URL);
				Class<?> c = Class.forName(endpointURL);
				inquiryService = (UDDIInquiryPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return inquiryService;
	}
	
	public UDDISecurityPortType getSecurityService() throws TransportException {
		if (securityService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SECURITY_URL);
				Class<?> c = Class.forName(endpointURL);
				securityService = (UDDISecurityPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return securityService;
	}
	
	public UDDIPublicationPortType getPublishService() throws TransportException {
		if (publishService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_PUBLISH_URL);
				Class<?> c = Class.forName(endpointURL);
				publishService = (UDDIPublicationPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publishService;
	}
	
	public UDDISubscriptionPortType getSubscriptionService() throws TransportException {
		if (subscriptionService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SUBSCRIPTION_URL);
				Class<?> c = Class.forName(endpointURL);
				subscriptionService = (UDDISubscriptionPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionService;
	}
	
	public UDDISubscriptionListenerPortType getSubscriptionListenerService() throws TransportException {
		if (subscriptionListenerService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SUBSCRIPTION_LISTENER_URL);
				Class<?> c = Class.forName(endpointURL);
				subscriptionListenerService = (UDDISubscriptionListenerPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionListenerService;
	}
	
	public UDDICustodyTransferPortType getCustodyTransferService() throws TransportException {
		if (custodyTransferService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_CUSTODY_TRANSFER_URL);
				Class<?> c = Class.forName(endpointURL);
				custodyTransferService = (UDDICustodyTransferPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return custodyTransferService;
	}

}
