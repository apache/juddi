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

import java.net.URI;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


public class RMITransport extends Transport {

	InitialContext context = null;
	private Log logger = LogFactory.getLog(this.getClass());
	private String nodeName = null;
	private String clientName = null;
	
	public RMITransport() {
		super();
		this.nodeName = Transport.DEFAULT_NODE_NAME;
	}
	
	public RMITransport(String nodeName) throws NamingException, ConfigurationException {
		super();
		this.nodeName = nodeName;
		initContext();
	}
	
	public RMITransport(String clientName, String nodeName) throws NamingException, ConfigurationException {
		super();
		this.nodeName = nodeName;
		this.clientName = clientName;
		initContext();
	}
	
	private void initContext() throws NamingException, ConfigurationException {
		Properties env = new Properties();
		UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
		String factoryInitial = client.getClientConfig().getHomeNode().getFactoryInitial();
		String factoryURLPkgs = client.getClientConfig().getHomeNode().getFactoryURLPkgs();
		String factoryNamingProvider = client.getClientConfig().getHomeNode().getFactoryNamingProvider();
        if (factoryInitial!=null && factoryInitial!="") env.setProperty(Property.UDDI_PROXY_FACTORY_INITIAL, factoryInitial);
        if (factoryURLPkgs!=null && factoryURLPkgs!="") env.setProperty(Property.UDDI_PROXY_FACTORY_URL_PKS, factoryURLPkgs);
        if (factoryNamingProvider!=null && factoryNamingProvider!="") env.setProperty(Property.UDDI_PROXY_PROVIDER_URL, factoryNamingProvider);
    	logger.debug("Initial Context using env=" + env.toString());
    	context = new InitialContext(env);
	}

	private UDDIInquiryPortType inquiryService = null;
	private UDDISecurityPortType securityService = null;
	private UDDIPublicationPortType publishService = null;
	private UDDISubscriptionPortType subscriptionService = null;
	private UDDISubscriptionListenerPortType subscriptionListenerService = null;
	private UDDICustodyTransferPortType custodyTransferService = null;
	private JUDDIApiPortType publisherService = null;
	

	public UDDIInquiryPortType getUDDIInquiryService(String endpointURL) throws TransportException {
		if (inquiryService==null) {
			try {
				if (endpointURL==null) {
					UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
					endpointURL = client.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
				}
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
		    	inquiryService = (UDDIInquiryPortType) requestHandler;
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
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
				securityService = (UDDISecurityPortType) requestHandler;
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
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
				publishService = (UDDIPublicationPortType) requestHandler;
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
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
		    	subscriptionService = (UDDISubscriptionPortType) requestHandler;
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
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
		    	subscriptionListenerService = (UDDISubscriptionListenerPortType)  requestHandler;
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
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
		    	custodyTransferService = (UDDICustodyTransferPortType)  requestHandler;
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
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
		    	publisherService = (JUDDIApiPortType)  requestHandler;
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publisherService;
	}

}
