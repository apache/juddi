package org.apache.juddi.v3.client.config;

import org.apache.log4j.Logger;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;

public class XRegistration {

	

	private Logger log = Logger.getLogger(this.getClass());
	private UDDIClerk toClerk;
	private UDDIClerk fromClerk;
	private String bindingKey;
	
	public UDDIClerk getToClerk() {
		return toClerk;
	}
	public void setToClerk(UDDIClerk toClerk) {
		this.toClerk = toClerk;
	}
	public UDDIClerk getFromClerk() {
		return fromClerk;
	}
	public void setFromClerk(UDDIClerk fromClerk) {
		this.fromClerk = fromClerk;
	}
	public String getBindingKey() {
		return bindingKey;
	}
	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}
	
	public void xRegister() {
		BusinessService businessService;
		try {
			BindingTemplate bindingTemplate = fromClerk.findServiceBinding(bindingKey);
			businessService = fromClerk.findService(bindingTemplate.getServiceKey());
			businessService.getBindingTemplates().getBindingTemplate().clear();
			businessService.getBindingTemplates().getBindingTemplate().add(bindingTemplate);
			log.info("xregister service " + businessService.getName().get(0).getValue() + " + from "
					+ fromClerk.getName() + " to " + toClerk.getName());
			toClerk.register(businessService);
		} catch (Exception e) {
			log.error("Could not " + toString() + ". " + e.getMessage() + " " + e.getCause(),e);
		}
	}
	
	public String toString() {
		return " xregister binding: " + bindingKey + " + from " + fromClerk.getName() + " to " + toClerk.getName(); 
	}
	
	
}
