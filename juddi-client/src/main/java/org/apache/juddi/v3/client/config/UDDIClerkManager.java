package org.apache.juddi.v3.client.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.log4j.Logger;
import org.uddi.api_v3.BusinessService;

public class UDDIClerkManager {
	
	private static UDDIClerkManager manager = null;
	private static Logger log = Logger.getLogger(UDDIClerkManager.class);
    private ClientConfig clientConfig = null;
	
	/**
	 * Singleton.
	 * @throws ConfigurationException 
	 */
	public UDDIClerkManager() throws ConfigurationException {
		super();
		clientConfig = new ClientConfig();
	}
	/**
	 * Stops the clerks.
	 * @throws ConfigurationException 
	 */
	public synchronized static void stop() throws ConfigurationException {
		
		if (manager!=null) {
			log.info("Stopping UDDI Clerks for manager " + manager.clientConfig.getManagerName());
			manager.releaseResources();
			manager=null;
			log.info("UDDI Clerks shutdown completed for manager " + manager.clientConfig.getManagerName());
		} else {
			log.warn("UDDI Clerks where not running for manager " + manager.clientConfig.getManagerName());
		}
	}
	
	private void releaseResources() {
		unRegisterAnnotatedServices();
	}
 	/**
	 * Initializes the UDDI Clerk.
	 * @throws ConfigurationException  
	 */
	public synchronized static void start() throws ConfigurationException {
		if (manager!=null) {
			log.warn("ClerkManager was already started. Going to reload..");
			manager.releaseResources();
			manager.clientConfig.loadManager();
		} else {
			log.info("Starting UDDI Clerks...");
			manager = new UDDIClerkManager();
		}
		if (manager.clientConfig.isRegisterOnStartup()) {
			manager.registerAnnotatedServices();
			manager.xRegister();
		}
		log.info("Clerks started succesfully for manager " + manager.clientConfig.getManagerName());
 	}
	
	public synchronized static void restart() throws ConfigurationException {
		if (manager==null) {
			log.warn("ClerkManager nor running..");
		} else {
			log.info("Restarting UDDI Clerks...");
			manager.releaseResources();
			manager.clientConfig.loadManager();
			manager = new UDDIClerkManager();
			manager.registerAnnotatedServices();
			manager.xRegister();
			log.info("Clerks restarted succesfully for manager " + manager.clientConfig.getManagerName());
		}
 	}
	
	/**
	 * X-Register services listed in the uddi.xml
	 */
	public void xRegister() {
		//XRegistration of listed services
		Set<XRegistration> xRegistrations = clientConfig.getXRegistrations();
		log.info("Starting cross registration...");
		for (XRegistration xRegistration : xRegistrations) {
			xRegistration.xRegister();
		}
		log.info("Cross registration completed");
	}
	/**
	 * Registers services to UDDI using a clerk, and the uddi.xml
	 * configuration.
	 */
	public void registerAnnotatedServices() {
		Map<String,UDDIClerk> clerks = clientConfig.getClerks();
		if (clerks.size() > 0) {
			AnnotationProcessor ap = new AnnotationProcessor();
			for (UDDIClerk clerk : clerks.values()) {
				Collection<BusinessService> services = ap.readServiceAnnotations(
						clerk.getClassWithAnnotations(),clerk.getNode().getProperties());
				for (BusinessService businessService : services) {
					clerk.register(businessService);
				}
			}
		}
	}
	/**
	 * Removes the bindings of the services of the annotated classes.
	 */
	public void unRegisterAnnotatedServices() {
		Map<String,UDDIClerk> clerks = clientConfig.getClerks();
		if (clerks.size() > 0) {
			AnnotationProcessor ap = new AnnotationProcessor();
			for (UDDIClerk clerk : clerks.values()) {
				Collection<BusinessService> services = ap.readServiceAnnotations(
						clerk.getClassWithAnnotations(),clerk.getNode().getProperties());
				for (BusinessService businessService : services) {
					clerk.unRegister(businessService);
				}
			}
		}
	}
	
	public static ClientConfig getClientConfig() throws ConfigurationException {
		return getManager().clientConfig;
	}
	
	public static UDDIClerkManager getManager() throws ConfigurationException {
		
		if (manager==null) {
			UDDIClerkManager.start();
		}
		return manager;
	}
	
	
	
	
}

