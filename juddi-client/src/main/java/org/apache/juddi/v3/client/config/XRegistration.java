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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;

public class XRegistration {

	private Log log = LogFactory.getLog(this.getClass());
	private UDDIClerk toClerk;
	private UDDIClerk fromClerk;
	private String entityKey;
    
	public XRegistration() {}
	
	public XRegistration(String entityKey, UDDIClerk fromClerk,
			UDDIClerk toClerk) {
		super();
		this.fromClerk = fromClerk;
		this.toClerk = toClerk;
		this.entityKey = entityKey;
	}
	
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
	public String getEntityKey() {
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}
	
	/**
	 * Copies the BusinessInformation from one UDDI to another UDDI. Note that no services are being
	 * copied over by this service. Use xRegisterService to copy over services.
	 */
	public void xRegisterBusiness() {
		BusinessEntity businessEntity;
		try {
			businessEntity = fromClerk.findBusiness(entityKey,fromClerk.getUDDINode().getApiNode());
			log.info("xregister business " + businessEntity.getName().get(0).getValue() + " + from "
					+ fromClerk.getName() + " to " + toClerk.getName() + ".");
			//not bringing over the services. They need to be explicitly copied using xRegisterService.
			businessEntity.setBusinessServices(null);
			toClerk.register(businessEntity,toClerk.getUDDINode().getApiNode());
		} catch (Exception e) {
			log.error("Could not " + toString() + ". " + e.getMessage() + " " + e.getCause(),e);
		}
	}
	
	/**
	 * Copies the BusinessInformation from one UDDI to another UDDI.
	 */
	public void xRegisterBusinessAndServices() {
		BusinessEntity businessEntity;
		try {
			businessEntity = fromClerk.findBusiness(entityKey,fromClerk.getUDDINode().getApiNode());
			log.info("xregister business " + businessEntity.getName().get(0).getValue() + " + from "
					+ fromClerk.getName() + " to " + toClerk.getName() + " including all services owned by this business.");
			toClerk.register(businessEntity,toClerk.getUDDINode().getApiNode());
		} catch (Exception e) {
			log.error("Could not " + toString() + ". " + e.getMessage() + " " + e.getCause(),e);
		}
	}
	/**
	 * Copies the Service from one UDDI to another UDDI.
	 */
	public void xRegisterService() {
		BusinessService businessService;
		try {
			businessService = fromClerk.findService(entityKey,fromClerk.getUDDINode().getApiNode());
			log.info("xregister service " + businessService.getName().get(0).getValue() + " + from "
					+ fromClerk.getName() + " to " + toClerk.getName());
			businessService.setBindingTemplates(null);
			toClerk.register(businessService,toClerk.getUDDINode().getApiNode());
		} catch (Exception e) {
			log.error("Could not " + toString() + ". " + e.getMessage() + " " + e.getCause(),e);
		}
	}
	/**
	 * Copies the Service from one UDDI to another UDDI along with all the bindingTemplates.
	 */
	public void xRegisterServiceAndBindings() {
		BusinessService businessService;
		try {
			businessService = fromClerk.findService(entityKey,fromClerk.getUDDINode().getApiNode());
			log.info("xregister service " + businessService.getName().get(0).getValue() + " + from "
					+ fromClerk.getName() + " to " + toClerk.getName());
			toClerk.register(businessService,toClerk.getUDDINode().getApiNode());
		} catch (Exception e) {
			log.error("Could not " + toString() + ". " + e.getMessage() + " " + e.getCause(),e);
		}
	}
	/**
	 * Copies the TemplateBinding from one UDDI to another UDDI.
	 */
	public void xRegisterServiceBinding() {
		try {
			BindingTemplate bindingTemplate = fromClerk.findServiceBinding(entityKey,fromClerk.getUDDINode().getApiNode());
			log.info("xregister binding " + bindingTemplate.getBindingKey()+ " + from "
					+ fromClerk.getName() + " to " + toClerk.getName());
			toClerk.register(bindingTemplate,toClerk.getUDDINode().getApiNode());
		} catch (Exception e) {
			log.error("Could not " + toString() + ". " + e.getMessage() + " " + e.getCause(),e);
		}
	}
	
	public String toString() {
		return " xregister entityKey: " + entityKey + " + from " + fromClerk.getName() + " to " + toClerk.getName(); 
	}
	
	
}
