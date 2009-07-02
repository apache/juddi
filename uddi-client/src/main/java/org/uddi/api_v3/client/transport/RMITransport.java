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

import java.net.URI;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3_service.JUDDIPublisherPortType;
import org.apache.log4j.Logger;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


public class RMITransport implements Transport {

	InitialContext context = null;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public RMITransport() throws NamingException, ConfigurationException {
		super();
		Properties env = new Properties();
		String factoryInitial = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_FACTORY_INITIAL);
		String factoryURLPkgs = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_FACTORY_URL_PKS);
		String factoryNamingProvider = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_PROVIDER_URL);
        if (factoryInitial!=null && factoryInitial!="") env.setProperty(Property.UDDI_PROXY_FACTORY_INITIAL, factoryInitial);
        if (factoryURLPkgs!=null && factoryURLPkgs!="") env.setProperty(Property.UDDI_PROXY_FACTORY_URL_PKS, factoryURLPkgs);
        if (factoryNamingProvider!=null && factoryNamingProvider!="") env.setProperty(Property.UDDI_PROXY_PROVIDER_URL, factoryNamingProvider);
    	logger.debug("Initial Context using env=" + env.toString());
    	context = new InitialContext(env);
	}

	UDDIInquiryPortType inquiryService = null;
	UDDISecurityPortType securityService = null;
	UDDIPublicationPortType publishService = null;
	UDDISubscriptionPortType subscriptionService = null;
	UDDISubscriptionListenerPortType subscriptionListenerService = null;
	UDDICustodyTransferPortType custodyTransferService = null;
	JUDDIPublisherPortType publisherService = null;

	public UDDIInquiryPortType getUDDIInquiryService() throws TransportException {
		if (inquiryService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_INQUIRY_URL);
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
	
	public UDDISecurityPortType getUDDISecurityService() throws TransportException {
		if (securityService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SECURITY_URL);
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
	
	public UDDIPublicationPortType getUDDIPublishService() throws TransportException {
		if (publishService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_PUBLISH_URL);
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
	
	public UDDISubscriptionPortType getUDDISubscriptionService() throws TransportException {
		if (subscriptionService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SUBSCRIPTION_URL);
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
	
	public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService() throws TransportException {
		if (subscriptionListenerService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SUBSCRIPTION_LISTENER_URL);
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
	
	public UDDICustodyTransferPortType getUDDICustodyTransferService() throws TransportException {
		if (custodyTransferService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_CUSTODY_TRANSFER_URL);
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
	
	public JUDDIPublisherPortType getJUDDIPublisherService() throws TransportException {
		if (publisherService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.JUDDI_PUBLISHER_TRANSFER_URL);
				URI endpointURI = new URI(endpointURL);
		    	String service    = endpointURI.getPath();
		    	logger.debug("Looking up service=" + service);
		    	Object requestHandler = context.lookup(service);
		    	publisherService = (JUDDIPublisherPortType)  requestHandler;
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publisherService;
	}

}
