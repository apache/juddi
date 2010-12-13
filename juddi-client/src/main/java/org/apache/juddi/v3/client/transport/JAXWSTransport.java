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

import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.UDDIServiceWSDL;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


public class JAXWSTransport extends Transport {

	public final static String JUDDI_V3_SERVICE_NAMESPACE    = "urn:juddi-apache-org:v3_service";
	public final static String PUBLISHER_SERVICE             = "JUDDIApiService";
	
	String nodeName = null;
	String managerName = null;
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
	
	public JAXWSTransport(String managerName, String nodeName) {
		super();
		this.managerName = managerName;
		this.nodeName = nodeName;
	}
	
	public UDDIInquiryPortType getUDDIInquiryService(String endpointURL) throws TransportException {

		if (inquiryService==null) {
			try {
				if (endpointURL==null)  {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					endpointURL = manager.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
				}
				URL tmpWSDLFile = new UDDIServiceWSDL().getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.INQUIRY, endpointURL);
				UDDIService service = new UDDIService(tmpWSDLFile);
				inquiryService = service.getUDDIInquiryPort();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return inquiryService;
	}
	
	public UDDISecurityPortType getUDDISecurityService(String endpointURL) throws TransportException {

		if (securityService==null) {
			try {
				if (endpointURL==null)  {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					endpointURL = manager.getClientConfig().getUDDINode(nodeName).getSecurityUrl();
				}
				URL tmpWSDLFile = new UDDIServiceWSDL().getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.SECURITY, endpointURL);
				UDDIService service = new UDDIService(tmpWSDLFile);
				securityService = service.getUDDISecurityPort();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return securityService;
	}
	
	public UDDIPublicationPortType getUDDIPublishService(String endpointURL) throws TransportException {

		if (publishService==null) {
			try {
				if (endpointURL==null)  {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					endpointURL = manager.getClientConfig().getUDDINode(nodeName).getPublishUrl();
				}
				URL tmpWSDLFile = new UDDIServiceWSDL().getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.PUBLISH, endpointURL);
				UDDIService service = new UDDIService(tmpWSDLFile);
				publishService = service.getUDDIPublicationPort();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publishService;
	}
	
	public UDDISubscriptionPortType getUDDISubscriptionService(String endpointURL) throws TransportException {

		if (subscriptionService==null) {
			try {
				if (endpointURL==null)  {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					endpointURL = manager.getClientConfig().getUDDINode(nodeName).getSubscriptionUrl();
				}
				URL tmpWSDLFile = new UDDIServiceWSDL().getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.SUBSCRIPTION, endpointURL);
				UDDIService service = new UDDIService(tmpWSDLFile);
				subscriptionService = service.getUDDISubscriptionPort();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionService;
	}
	
	public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService(String endpointURL) throws TransportException {
		if (subscriptionListenerService == null) {
			try {
				if (endpointURL==null)  {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					endpointURL = manager.getClientConfig().getUDDINode(nodeName).getSubscriptionListenerUrl();
				}
				URL tmpWSDLFile = new UDDIServiceWSDL().getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.SUBSCRIPTION_LISTENER, endpointURL);
				UDDIService service = new UDDIService(tmpWSDLFile);
				subscriptionListenerService = service.getUDDISubscriptionListenerPort();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionListenerService;
	}
	
	public UDDICustodyTransferPortType getUDDICustodyTransferService(String endpointURL) throws TransportException {
		if (custodyTransferService == null) {
			try {
				if (endpointURL==null)  {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					endpointURL = manager.getClientConfig().getUDDINode(nodeName).getCustodyTransferUrl();
				}
				URL tmpWSDLFile = new UDDIServiceWSDL().getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.CUSTODY_TRANSFER, endpointURL);
				UDDIService service = new UDDIService(tmpWSDLFile);
				custodyTransferService = service.getUDDICustodyPort();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return custodyTransferService;
	}
	
	/**
	 * This is a jUDDI specific API
	 */
	public JUDDIApiPortType getJUDDIApiService(String endpointURL) throws TransportException {
		if (publisherService == null) {
			try {
				if (endpointURL==null)  {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					endpointURL = manager.getClientConfig().getUDDINode(nodeName).getJuddiApiUrl();
				}
				QName qName = new QName(JUDDI_V3_SERVICE_NAMESPACE, PUBLISHER_SERVICE);
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
