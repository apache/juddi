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

public class UDDIClient {
	
	private static UDDIClient client = null;
	private static Logger log = Logger.getLogger(UDDIClient.class);
    private static ClientConfig clientConfig = null;
	
	/**
	 * Singleton.
	 * @throws ConfigurationException 
	 */
	private UDDIClient() throws ConfigurationException {
		super();
		clientConfig = ClientConfig.getInstance();
	}
	/**
	 * Stops the client.
	 * @throws ConfigurationException 
	 */
	public synchronized static void stop() throws ConfigurationException {
		log.info("Stopping UDDI-client...");
		if (client!=null) {
			//TODO unregister bindings from the annotation
			client=null;
			log.info("UDDI-client shutdown completed.");
		} else {
			log.warn("UDDI-Client was not running.");
		}
	}
	/**
	 * Initializes the UDDI client.
	 * @return
	 * @throws ConfigurationException
	 * @throws  
	 */
	public synchronized static UDDIClient start() throws ConfigurationException {
		log.info("Starting UDDI-client...");
		if (client==null) {
			client = new UDDIClient();
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
			if (clientConfig.isRegisterOnStartup()) {
				log.info("Starting cross registration...");
				for (XRegistration xRegistration : xRegistrations) {
					xRegistration.xRegister();
				}
				log.info("Cross registration completed");
			}
			log.info("UDDI client started succesfully.");
		} else {
			log.warn("UDDI Client was already start.");
		}
		return client;
 	}
	
	public ClientConfig getClientConfig() {
		return clientConfig;
	}
	
}

