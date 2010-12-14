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
 */
package org.apache.juddi.rmi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kurt Stam (kstam@apache.org)
 */
public class JNDIRegistration
{
	public static String JUDDI = "/juddiv3";
	public static String UDDI_SECURITY_SERVICE    = JUDDI + "/UDDISecurityService";
	public static String UDDI_PUBLICATION_SERVICE = JUDDI + "/UDDIPublicationService";
	public static String UDDI_INQUIRY_SERVICE     = JUDDI + "/UDDIInquiryService";
	public static String UDDI_SUBSCRIPTION_SERVICE = JUDDI + "/UDDISubscriptionService";
	public static String UDDI_SUBSCRIPTION_LISTENER_SERVICE = JUDDI + "/UDDISubscriptionListenerService";
	public static String UDDI_CUSTODY_TRANSFER_SERVICE = JUDDI + "/UDDICustodyTransferService";
	public static String JUDDI_PUBLISHER_SERVICE  = JUDDI + "/JUDDIApiService";
	
	private UDDISecurityService securityService = null;
	private UDDIPublicationService publicationService = null;
	private UDDIInquiryService inquiryService = null;
	private UDDISubscriptionService subscriptionService = null;
	private UDDISubscriptionListenerService subscriptionListenerService = null;
	private UDDICustodyTransferService custodyTransferService = null;
	private JUDDIApiService publisherService = null;
	
	private Log log = LogFactory.getLog(this.getClass());
	InitialContext context = null;
	private static JNDIRegistration registration = null;
	
	public static JNDIRegistration getInstance() throws NamingException {
		if (registration==null) {
			registration = new JNDIRegistration();
		}
		return registration;
	}
	
	private JNDIRegistration() throws NamingException{
		super();
		context = new InitialContext(); 
	}
	/**
	 * Registers the Publish and Inquiry Services to JNDI and instantiates a
	 * instance of each so we can remotely attach to it later.
	 */
	public void register(int port) {
		try {
			Context juddiContext = context.createSubcontext(JUDDI);
			
			securityService = new UDDISecurityService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_SECURITY_SERVICE + ", " + securityService.getClass());
			juddiContext.bind(UDDI_SECURITY_SERVICE, securityService);
			
			publicationService = new UDDIPublicationService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_PUBLICATION_SERVICE + ", " + publicationService.getClass());
			juddiContext.bind(UDDI_PUBLICATION_SERVICE, publicationService);
			
			inquiryService = new UDDIInquiryService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_INQUIRY_SERVICE + ", " + inquiryService.getClass());
			juddiContext.bind(UDDI_INQUIRY_SERVICE, inquiryService);
			
			subscriptionService = new UDDISubscriptionService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_SUBSCRIPTION_SERVICE + ", " + subscriptionService.getClass());
			juddiContext.bind(UDDI_SUBSCRIPTION_SERVICE, subscriptionService);
			
			subscriptionListenerService = new UDDISubscriptionListenerService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_SUBSCRIPTION_LISTENER_SERVICE + ", " + subscriptionListenerService.getClass());
			juddiContext.bind(UDDI_SUBSCRIPTION_LISTENER_SERVICE, subscriptionListenerService);
			
			custodyTransferService = new UDDICustodyTransferService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_CUSTODY_TRANSFER_SERVICE + ", " + custodyTransferService.getClass());
			juddiContext.bind(UDDI_CUSTODY_TRANSFER_SERVICE, custodyTransferService);
			
			publisherService = new JUDDIApiService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + JUDDI_PUBLISHER_SERVICE + ", " + publisherService.getClass());
			juddiContext.bind(JUDDI_PUBLISHER_SERVICE, publisherService);
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public void unregister() {
		
		try {
			context.unbind(UDDI_SECURITY_SERVICE);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		securityService = null;
		try {
			context.unbind(UDDI_PUBLICATION_SERVICE);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		publicationService = null;
		try {
			context.unbind(UDDI_INQUIRY_SERVICE);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		inquiryService = null;
		try {
			context.unbind(UDDI_SUBSCRIPTION_SERVICE);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		subscriptionService = null;
		try {
			context.unbind(UDDI_SUBSCRIPTION_LISTENER_SERVICE);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		subscriptionListenerService = null;
		try {
			context.unbind(UDDI_CUSTODY_TRANSFER_SERVICE);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		custodyTransferService = null;
		try {
			context.unbind(JUDDI_PUBLISHER_SERVICE);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		publisherService = null;
		try {
			context.unbind(JUDDI);
		} catch (NamingException e) {
			log.error(e.getMessage(),e);
		}
		
	}
}
