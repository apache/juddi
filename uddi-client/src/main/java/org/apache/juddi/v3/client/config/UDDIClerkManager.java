package org.apache.juddi.v3.client.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.log4j.Logger;
import org.uddi.api_v3.BusinessService;

public class UDDIClerkManager {
	
	private static Map<String,UDDIClerkManager> managers = new ConcurrentHashMap<String,UDDIClerkManager>();
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
	public void stop(String managerName) throws ConfigurationException {
		log.info("Stopping UDDI Clerks...");
		if (managers.containsKey(managerName)) {
			UDDIClerkManager manager = managers.get(managerName);
			manager.releaseResources();
			manager=null;
			managers.remove(managerName);
			log.info("UDDI Clerks shutdown completed.");
		} else {
			log.warn("UDDI Clerks for Manager" + managerName+ " where not running.");
		}
	}
	
	private void releaseResources() {
		//TODO unregister bindings from the annotation
	}
 	/**
	 * Initializes the UDDI Clerk.
	 * @return
	 * @throws ConfigurationException
	 * @throws  
	 */
	public void start(String managerName) throws ConfigurationException {
		log.info("Starting UDDI Clerks...");
		if (!managers.containsKey(managerName)) {
			UDDIClerkManager manager = new UDDIClerkManager();
			managers.put(managerName, manager);
		} else {
			log.warn("ClerkManager was already started. Going to restart..");
			UDDIClerkManager manager = managers.get(managerName);
			manager.releaseResources();
			manager = new UDDIClerkManager();
		}
		log.info("Initializing clerks...");
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
		//XRegistration of listed services
		Set<XRegistration> xRegistrations = clientConfig.getXRegistrations();
		log.info("Starting cross registration...");
		for (XRegistration xRegistration : xRegistrations) {
			xRegistration.xRegister();
		}
		log.info("Cross registration completed");
		log.info("Clerks started succesfully.");
 	}
	
	public static ClientConfig getClientConfig(String managerName) throws ConfigurationException {
		UDDIClerkManager manager = getManager(managerName);
		return manager.clientConfig;
	}
	
	public static UDDIClerkManager getManager(String managerName) throws ConfigurationException {
		if (managers.size()==0) new UDDIClerkManager().start(managerName);
		if (managers.containsKey(managerName)) {
			return managers.get(managerName);
		} else {
			//creating a new manager, making sure the name matches.
			throw new ConfigurationException("No UDDI ClerkManager by that name.");
		}
	}
	
	
	
}

