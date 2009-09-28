package org.apache.juddi.v3.client;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.config.ClientConfig;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.XRegistration;
import org.apache.log4j.Logger;
import org.uddi.api_v3.BusinessService;

public class UDDIClerkManager {
	
	private static UDDIClerkManager manager = null;
	private static Logger log = Logger.getLogger(UDDIClerkManager.class);
    private static ClientConfig clientConfig = null;
	
	/**
	 * Singleton.
	 * @throws ConfigurationException 
	 */
	private UDDIClerkManager() throws ConfigurationException {
		super();
		clientConfig = ClientConfig.getInstance();
	}
	/**
	 * Stops the clerks.
	 * @throws ConfigurationException 
	 */
	public synchronized static void stop() throws ConfigurationException {
		log.info("Stopping UDDI Clerks...");
		if (manager!=null) {
			manager.releaseResources();
			manager=null;
			log.info("UDDI Clerks shutdown completed.");
		} else {
			log.warn("UDDI Clerks where not running.");
		}
	}
	
	private synchronized void releaseResources() {
		//TODO unregister bindings from the annotation
	}
 	/**
	 * Initializes the UDDI Clerk.
	 * @return
	 * @throws ConfigurationException
	 * @throws  
	 */
	public synchronized static UDDIClerkManager start() throws ConfigurationException {
		log.info("Starting UDDI Clerks...");
		if (manager==null) {
			manager = new UDDIClerkManager();
		} else {
			log.warn("ClerkManager was already started. Going to restart..");
			manager.releaseResources();
			manager = new UDDIClerkManager();
		}
		log.info("Initializing clerks...");
		Map<String,UDDIClerk> clerks = clientConfig.getClerks();
		if (clerks.size() > 0) {
			AnnotationProcessor ap = new AnnotationProcessor();
			for (UDDIClerk clerk : clerks.values()) {
				Collection<BusinessService> services = ap.readServiceAnnotations(clerk.getClassWithAnnotations());
				for (BusinessService businessService : services) {
					clerk.register(businessService);
				}
			}
		}
		//XRegistration of listed services
		Set<XRegistration> xRegistrations = clientConfig.getXRegistrations();
		log.info("Starting cross registration...");
		for (XRegistration xRegistration : xRegistrations) {
			xRegistration.xRegister();
		}
		log.info("Cross registration completed");
		log.info("Clerks started succesfully.");
		
		return manager;
 	}
	
	public ClientConfig getClientConfig() {
		return clientConfig;
	}
	
}

