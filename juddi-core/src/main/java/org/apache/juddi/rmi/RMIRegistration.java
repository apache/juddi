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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kurt Stam (kstam@apache.org)
 */
public class RMIRegistration
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
	Registry registry = null;
	private static RMIRegistration registration = null;
	
	public static RMIRegistration getInstance(int port) throws NamingException, RemoteException {
		if (registration==null) {
			registration = new RMIRegistration(port);
		}
		return registration;
	}
	
	private RMIRegistration(int port) throws NamingException, RemoteException{
		super();
		registry = LocateRegistry.createRegistry(port);
	}
	/**
	 * Registers the Publish and Inquiry Services to JNDI and instantiates a
	 * instance of each so we can remotely attach to it later.
	 */
	public void register(int port) {
		try {
			securityService = new UDDISecurityService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_SECURITY_SERVICE + ", " + securityService.getClass());
			registry.bind(UDDI_SECURITY_SERVICE, securityService);
			
			publicationService = new UDDIPublicationService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_PUBLICATION_SERVICE + ", " + publicationService.getClass());
			registry.bind(UDDI_PUBLICATION_SERVICE, publicationService);
			
			inquiryService = new UDDIInquiryService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_INQUIRY_SERVICE + ", " + inquiryService.getClass());
			registry.bind(UDDI_INQUIRY_SERVICE, inquiryService);
			
			subscriptionService = new UDDISubscriptionService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_SUBSCRIPTION_SERVICE + ", " + subscriptionService.getClass());
			registry.bind(UDDI_SUBSCRIPTION_SERVICE, subscriptionService);
			
			subscriptionListenerService = new UDDISubscriptionListenerService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_SUBSCRIPTION_LISTENER_SERVICE + ", " + subscriptionListenerService.getClass());
			registry.bind(UDDI_SUBSCRIPTION_LISTENER_SERVICE, subscriptionListenerService);
			
			custodyTransferService = new UDDICustodyTransferService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + UDDI_CUSTODY_TRANSFER_SERVICE + ", " + custodyTransferService.getClass());
			registry.bind(UDDI_CUSTODY_TRANSFER_SERVICE, custodyTransferService);
			
			publisherService = new JUDDIApiService(port);
			if (log.isDebugEnabled()) log.debug("Setting " + JUDDI_PUBLISHER_SERVICE + ", " + publisherService.getClass());
			registry.bind(JUDDI_PUBLISHER_SERVICE, publisherService);
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public void unregister() {
		
		try {
			registry.unbind(UDDI_SECURITY_SERVICE);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		securityService = null;
		try {
			registry.unbind(UDDI_PUBLICATION_SERVICE);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		publicationService = null;
		try {
			registry.unbind(UDDI_INQUIRY_SERVICE);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		inquiryService = null;
		try {
			registry.unbind(UDDI_SUBSCRIPTION_SERVICE);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		subscriptionService = null;
		try {
			registry.unbind(UDDI_SUBSCRIPTION_LISTENER_SERVICE);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		subscriptionListenerService = null;
		try {
			registry.unbind(UDDI_CUSTODY_TRANSFER_SERVICE);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		custodyTransferService = null;
		try {
			registry.unbind(JUDDI_PUBLISHER_SERVICE);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		publisherService = null;
		
	}
}
