package org.apache.juddi.v3.client.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.uddi.api_v3.BusinessService;

public class UDDIClerk {

	private Logger logger = Logger.getLogger(this.getClass());
	private String name;
	private UDDINode node;
	private String publisher;
	private String password;
	private String[] classWithAnnotations;
	private Map<String,Properties> services = new HashMap<String,Properties>(); 

	public UDDIClerk() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Register all services.
	 * @throws ConfigurationException 
	 */
	public void register(Collection<BusinessService> services) throws ConfigurationException {
		if (services.size()>0) {
			for (BusinessService service : services) {
				logger.info("Registering service " + service);
			}
		}
	}
	
	public void unRegister(Collection<BusinessService> services) throws ConfigurationException {
		if (services.size()>0) {
			for (BusinessService service : services) {
				logger.info("UnRegistering service/binding " + service);
			}
		}
	}

	public String[] getClassWithAnnotations() {
		return classWithAnnotations;
	}

	public void setClassWithAnnotations(String[] classWithAnnotations) {
		this.classWithAnnotations = classWithAnnotations;
	}

	public UDDINode getNode() {
		return node;
	}

	public void setNode(UDDINode node) {
		this.node = node;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, Properties> getServices() {
		return services;
	}

	public void setServices(Map<String, Properties> services) {
		this.services = services;
	}
	
}
