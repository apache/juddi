package org.apache.juddi.v3.client.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.transport.Transport;
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
		log.info("Starting UDDI Clerks for manager " + clientConfig.getManagerName() + "...");
		UDDIClientContainer.addClerkManager(this);
		if (clientConfig.isRegisterOnStartup()) {
			saveClerkAndNodeInfo();
			registerAnnotatedServices();
			xRegister();
		}
		log.info("Clerks started succesfully for manager " + clientConfig.getManagerName());
 	}
	
	public void restart() throws ConfigurationException {
		stop();
		start();
 	}
	/**
	 * Saves the clerk and node info from the uddi.xml to the jUDDI registry.
	 */
	public void saveClerkAndNodeInfo() {
		
		Map<String,UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
		if (uddiClerks.size() > 0) {
			for (UDDIClerk defaultClerk : uddiClerks.values()) {
				if (Transport.DEFAULT_NODE_NAME.equals(defaultClerk.uddiNode.getName())) {
					for (UDDINode uddiNode : clientConfig.getUDDINodes().values()) {
						if (uddiNode.isAllowJUDDIAPI()) defaultClerk.saveNode(uddiNode.getApiNode());
					}
					for (UDDIClerk uddiClerk : clientConfig.getUDDIClerks().values()) {
						if (uddiClerk.getUDDINode().isAllowJUDDIAPI()) defaultClerk.saveClerk(uddiClerk);
					}
					break;
				}
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

