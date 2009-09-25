package org.apache.juddi.v3.client;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.config.ClientConfig;
import org.apache.juddi.v3.client.config.UDDIClerk;
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
		if (client!=null) {
			log.info("Stopping UDDI-client...");
			//new AnnotationProcessor(clientConfig.getClerks()).unRegister();
			client=null;
			log.info("UDDI-client shutdown completed.");
		}
	}
	/**
	 * Initializes the UDDI client.
	 * @return
	 * @throws ConfigurationException
	 * @throws  
	 */
	public synchronized static UDDIClient start() throws ConfigurationException {
		if (client==null) {
			log.info("Starting UDDI-client...");
			client = new UDDIClient();
			Set<UDDIClerk> clerks = clientConfig.getClerks();
			AnnotationProcessor ap = new AnnotationProcessor();
			for (UDDIClerk clerk : clerks) {
				Collection<BusinessService> services = ap.readServiceAnnotations(clerk.getClassWithAnnotations());
				clerk.register(services);
			}
			
			log.info("UDDI client started succesfully.");
		}
		return client;
 	}
	
	public ClientConfig getClientConfig() {
		return clientConfig;
	}
	
}

