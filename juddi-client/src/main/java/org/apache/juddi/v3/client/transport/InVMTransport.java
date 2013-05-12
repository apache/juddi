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

import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


public class InVMTransport extends Transport {

	private String nodeName = null;
	private String clientName = null;
	UDDIInquiryPortType inquiryService = null;
	UDDISecurityPortType securityService = null;
	UDDIPublicationPortType publishService = null;
	UDDISubscriptionPortType subscriptionService = null;
	UDDISubscriptionListenerPortType subscriptionListenerService = null;
	UDDICustodyTransferPortType custodyTransferService = null;
	JUDDIApiPortType publisherService = null;

	public InVMTransport() {
		super();
		this.nodeName = Transport.DEFAULT_NODE_NAME;
	}
	
	public InVMTransport(String nodeName) {
		super();
		this.nodeName = nodeName;
	}
	
	public InVMTransport(String clientName, String nodeName) {
		super();
		this.nodeName = nodeName;
		this.clientName = clientName;
	}
	
	public UDDIInquiryPortType getUDDIInquiryService(String endpointURL) throws TransportException {
		if (inquiryService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
				}
				Class<?> c = Class.forName(endpointURL);
				inquiryService = (UDDIInquiryPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return inquiryService;
	}
	
	public UDDISecurityPortType getUDDISecurityService(String endpointURL) throws TransportException {
		if (securityService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getSecurityUrl();
				}
				Class<?> c = Class.forName(endpointURL);
				securityService = (UDDISecurityPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return securityService;
	}
	
	public UDDIPublicationPortType getUDDIPublishService(String endpointURL) throws TransportException {
		if (publishService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getPublishUrl();
				}
				Class<?> c = Class.forName(endpointURL);
				publishService = (UDDIPublicationPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publishService;
	}
	
	public UDDISubscriptionPortType getUDDISubscriptionService(String endpointURL) throws TransportException {
		if (subscriptionService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getSubscriptionUrl();
				}
				Class<?> c = Class.forName(endpointURL);
				subscriptionService = (UDDISubscriptionPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionService;
	}
	
	public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService(String endpointURL) throws TransportException {
		if (subscriptionListenerService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getSubscriptionListenerUrl();
				}
				Class<?> c = Class.forName(endpointURL);
				subscriptionListenerService = (UDDISubscriptionListenerPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionListenerService;
	}
	
	public UDDICustodyTransferPortType getUDDICustodyTransferService(String endpointURL) throws TransportException {
		if (custodyTransferService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getCustodyTransferUrl();
				}
				Class<?> c = Class.forName(endpointURL);
				custodyTransferService = (UDDICustodyTransferPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return custodyTransferService;
	}
	
	public JUDDIApiPortType getJUDDIApiService(String endpointURL) throws TransportException {
		if (publisherService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getJuddiApiUrl();
				}
				Class<?> c = Class.forName(endpointURL);
				publisherService = (JUDDIApiPortType) c.newInstance();
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publisherService;
	}


}
