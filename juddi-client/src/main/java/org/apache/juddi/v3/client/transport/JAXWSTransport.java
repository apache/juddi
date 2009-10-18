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
package org.apache.juddi.v3.client.transport;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


public class JAXWSTransport implements Transport {

	String nodeName = null;
	UDDIInquiryPortType inquiryService = null;
	UDDISecurityPortType securityService = null;
	UDDIPublicationPortType publishService = null;
	UDDISubscriptionPortType subscriptionService = null;
	UDDISubscriptionListenerPortType subscriptionListenerService = null;
	UDDICustodyTransferPortType custodyTransferService = null;
	JUDDIApiPortType publisherService = null;

	public JAXWSTransport() {
		super();
		this.nodeName = Transport.DEFAULT_NODE_NAME;
	}
	
	public JAXWSTransport(String nodeName) {
		super();
		this.nodeName = nodeName;
	}

	public UDDIInquiryPortType getUDDIInquiryService() throws TransportException {

		if (inquiryService==null) {
			try {
				String endpointURL = UDDIClerkManager.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
				QName qName = new QName(Transport.API_V3_NAMESPACE, Transport.INQUIRY_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				inquiryService = (UDDIInquiryPortType) service.getPort(UDDIInquiryPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return inquiryService;
	}
	
	public UDDISecurityPortType getUDDISecurityService() throws TransportException {

		if (securityService==null) {
			try {
				String endpointURL = UDDIClerkManager.getClientConfig().getUDDINode(nodeName).getSecurityUrl();
				QName qName = new QName(Transport.API_V3_NAMESPACE, Transport.SECURITY_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				securityService = (UDDISecurityPortType) service.getPort(UDDISecurityPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return securityService;
	}
	
	public UDDIPublicationPortType getUDDIPublishService() throws TransportException {

		if (publishService==null) {
			try {
				String endpointURL = UDDIClerkManager.getClientConfig().getUDDINode(nodeName).getPublishUrl();
				QName qName = new QName(Transport.API_V3_NAMESPACE, Transport.PUBLISH_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				publishService = (UDDIPublicationPortType) service.getPort(UDDIPublicationPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publishService;
	}
	
	public UDDISubscriptionPortType getUDDISubscriptionService() throws TransportException {

		if (subscriptionService==null) {
			try {
				String endpointURL = UDDIClerkManager.getClientConfig().getUDDINode(nodeName).getSubscriptionUrl();
				QName qName = new QName(Transport.SUB_V3_NAMESPACE, Transport.SUBSCRIPTION_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				subscriptionService = (UDDISubscriptionPortType) service.getPort(UDDISubscriptionPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionService;
	}
	
	public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService() throws TransportException {
		if (subscriptionListenerService == null) {
			try {
				String endpointURL = UDDIClerkManager.getClientConfig().getUDDINode(nodeName).getSubscriptionListenerUrl();
				QName qName = new QName(Transport.SUBR_V3_NAMESPACE, Transport.SUBSCRIPTION_LISTENER_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				subscriptionListenerService = (UDDISubscriptionListenerPortType) service.getPort(UDDISubscriptionListenerPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionListenerService;
	}
	
	public UDDICustodyTransferPortType getUDDICustodyTransferService() throws TransportException {
		if (custodyTransferService == null) {
			try {
				String endpointURL = UDDIClerkManager.getClientConfig().getUDDINode(nodeName).getCustodyTransferUrl();
				QName qName = new QName(Transport.CUSTODY_V3_NAMESPACE, Transport.CUSTODY_TRANSFER_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				custodyTransferService = (UDDICustodyTransferPortType) service.getPort(UDDICustodyTransferPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return custodyTransferService;
	}
	
	public JUDDIApiPortType getJUDDIApiService() throws TransportException {
		if (publisherService == null) {
			try {
				String endpointURL = UDDIClerkManager.getClientConfig().getUDDINode(nodeName).getJuddiApiUrl();
				QName qName = new QName(Transport.JUDDI_API_V3_NAMESPACE, Transport.PUBLISHER_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				publisherService = (JUDDIApiPortType) service.getPort(JUDDIApiPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publisherService;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

}
