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
package org.apache.juddi.v3.client.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;

public class AsyncRegistration implements Runnable {

	private static Log log = LogFactory.getLog(AsyncRegistration.class);
	private UDDIClerk clerk;
	private URLLocalizer urlLocalizer;
	private Properties properties;
	private static Map<String,ServiceLocator> serviceLocators = new HashMap<String,ServiceLocator>();
	private RegistrationInfo registrationInfo;
	
	public AsyncRegistration(UDDIClerk clerk, URLLocalizer urlLocalizer, 
		Properties properties, RegistrationInfo registrationInfo) {
		super();
		this.clerk = clerk;
		this.urlLocalizer = urlLocalizer;
		this.properties = properties;
		this.registrationInfo = registrationInfo;
		
	}

	public void run() {
		
		try {
			ServiceLocator serviceLocator = null;
			synchronized (serviceLocators) {
				if (!serviceLocators.containsKey(clerk.getName())) {
					serviceLocator = new ServiceLocator(clerk, urlLocalizer, properties);
					serviceLocators.put(clerk.getName(), serviceLocator);
				} else {
					serviceLocator = serviceLocators.get(clerk.getName());
				}
			}
			if (RegistrationType.WSDL.equals(registrationInfo.getRegistrationType())) {
				WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(clerk, urlLocalizer, properties);
				BusinessService service = wsdl2UDDI.registerBusinessService(registrationInfo.getServiceQName(), 
								   registrationInfo.getPortName(),
								   registrationInfo.getServiceUrl(),
								   registrationInfo.getWsdlDefinition()).getBusinessService();
				serviceLocator.addService(service.getServiceKey());
				
			} else if (RegistrationType.BPEL.equals(registrationInfo.getRegistrationType())) {
				BPEL2UDDI bpel2UDDI = new BPEL2UDDI(clerk, urlLocalizer, properties);
				BindingTemplate binding = bpel2UDDI.register(registrationInfo.getServiceQName(), 
								   registrationInfo.getPortName(),
								   registrationInfo.getServiceUrl(),
								   registrationInfo.getWsdlDefinition());
				serviceLocator.addService(binding.getServiceKey());
			} else {
				log.error("Registration error, due to unsupported registration type of " + registrationInfo.getRegistrationType());
			}
		} catch (Exception e) {
			log.error("Not able to register " + registrationInfo.getRegistrationType() 
					+ " endpoint " + registrationInfo.getServiceQName()
					+ " to UDDI Registry. " + e.getMessage(),e);
		}
		
	}

	public ServiceLocator getServiceLocator(String clerkName) {
		return serviceLocators.get(clerkName);
	}
}
