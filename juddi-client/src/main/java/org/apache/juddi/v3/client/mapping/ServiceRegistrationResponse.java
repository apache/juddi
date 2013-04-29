package org.apache.juddi.v3.client.mapping;

import org.uddi.api_v3.BusinessService;

public class ServiceRegistrationResponse {
	
	private String bindingKey;
	private BusinessService businessService;
	
	public String getBindingKey() {
		return bindingKey;
	}
	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}
	public BusinessService getBusinessService() {
		return businessService;
	}
	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}
	
}
