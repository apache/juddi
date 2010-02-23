/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.log4j.Logger;
import org.uddi.api_v3.BusinessService;

public class UDDIClerkManager {
	
	private static Logger log = Logger.getLogger(UDDIClerkManager.class);
    private ClientConfig clientConfig = null;
	
	/**
	 * Manages the clerks. Initiates reading the client configuration from the uddi.xml.
	 * @throws ConfigurationException 
	 */
	public UDDIClerkManager(String configurationFile) throws ConfigurationException {
		super();
		clientConfig = new ClientConfig(configurationFile);
	}
	/**
	 * Stops the clerks.
	 * @throws ConfigurationException 
	 */
	public void stop() throws ConfigurationException {
		log.info("Stopping UDDI Clerks for manager " + clientConfig.getManagerName());
		releaseResources();
		UDDIClientContainer.removeClerkManager(getName());
		log.info("UDDI Clerks shutdown completed for manager " + clientConfig.getManagerName());
	}
	
	private void releaseResources() {
		unRegisterAnnotatedServices();
	}
 	/**
	 * Initializes the UDDI Clerk.
	 * @throws ConfigurationException  
	 */
	public void start() throws ConfigurationException {
		Runnable runnable = new BackGroundRegistration(this);
		Thread thread = new Thread(runnable);
		thread.start();
 	}
	
	public void restart() throws ConfigurationException {
		stop();
		start();
	}
	
	/**
	 * Saves the clerk and node info from the uddi.xml to the home jUDDI registry.
	 * This info is needed if you want to JUDDI Server to do XRegistration/"replication".
	 */
	public void saveClerkAndNodeInfo() {
		
		Map<String,UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
		
		if (uddiClerks.size() > 0) {
			
			//obtaining a clerk that can write to the home registry
			UDDIClerk homeClerk=null;
			for (UDDIClerk clerk : uddiClerks.values()) {
				if (clerk.getUDDINode().isHomeJUDDI()) {
					homeClerk = clerk;
				}	
			}
			//registering nodes and clerks
			if (homeClerk!=null) {
				int numberOfHomeJUDDIs=0;
				for (UDDINode uddiNode : clientConfig.getUDDINodes().values()) {
					if (uddiNode.isHomeJUDDI()) numberOfHomeJUDDIs++;
					homeClerk.saveNode(uddiNode.getApiNode());
				}
				if (numberOfHomeJUDDIs==1) {
					for (UDDIClerk clerk : clientConfig.getUDDIClerks().values()) {
						homeClerk.saveClerk(clerk);
					}
				} else {
					log.error("The client config needs to have one homeJUDDI node and found " + numberOfHomeJUDDIs);
				}
			} else {
				log.info("No home clerk found.");
			}
		}	
	}
	
	/**
	 * X-Register services listed in the uddi.xml
	 */
	public void xRegister() {
		log.info("Starting cross registration...");
		//XRegistration of listed businesses
		Set<XRegistration> xBusinessRegistrations = clientConfig.getXBusinessRegistrations();
		for (XRegistration xRegistration : xBusinessRegistrations) {
			xRegistration.xRegisterBusiness();
		}
		//XRegistration of listed serviceBindings
		Set<XRegistration> xServiceBindingRegistrations = clientConfig.getXServiceBindingRegistrations();
		for (XRegistration xRegistration : xServiceBindingRegistrations) {
			xRegistration.xRegisterServiceBinding();
		}
		log.info("Cross registration completed");
	}
	/**
	 * Registers services to UDDI using a clerk, and the uddi.xml
	 * configuration.
	 */
	public void registerAnnotatedServices() {
		Map<String,UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
		if (uddiClerks.size() > 0) {
			AnnotationProcessor ap = new AnnotationProcessor();
			for (UDDIClerk uddiClerk : uddiClerks.values()) {
				Collection<BusinessService> services = ap.readServiceAnnotations(
						uddiClerk.getClassWithAnnotations(),uddiClerk.getUDDINode().getProperties());
				for (BusinessService businessService : services) {
					log.info("Node=" + uddiClerk.getUDDINode().getApiNode().getName());
					uddiClerk.register(businessService, uddiClerk.getUDDINode().getApiNode());
				}
			}
		}
	}
	/**
	 * Removes the bindings of the services of the annotated classes.
	 */
	public void unRegisterAnnotatedServices() {
		Map<String,UDDIClerk> clerks = clientConfig.getUDDIClerks();
		if (clerks.size() > 0) {
			AnnotationProcessor ap = new AnnotationProcessor();
			for (UDDIClerk clerk : clerks.values()) {
				Collection<BusinessService> services = ap.readServiceAnnotations(
						clerk.getClassWithAnnotations(),clerk.getUDDINode().getProperties());
				for (BusinessService businessService : services) {
					clerk.unRegister(businessService,clerk.getUDDINode().getApiNode());
				}
			}
		}
	}
	
	public ClientConfig getClientConfig() {
		return clientConfig;
	}
	
	public String getName() {
		return clientConfig.getManagerName();
	}
	
}

