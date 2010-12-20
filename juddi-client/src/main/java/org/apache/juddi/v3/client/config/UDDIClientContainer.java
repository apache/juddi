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
 */
package org.apache.juddi.v3.client.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UDDIClientContainer {

	private static Log log = LogFactory.getLog(UDDIClientContainer.class);
	private static Map<String,UDDIClerkManager> managers = new HashMap<String, UDDIClerkManager>();
	
	public static UDDIClerkManager getUDDIClerkManager(String managerName) 
		throws ConfigurationException {

		if (managerName!=null) {
			if (managers.containsKey(managerName)) {
				return (managers.get(managerName));
			} else {
				throw new ConfigurationException("No manager by name " + managerName + " was found. " +
						" Please check your client uddi.xml files, and make sure this manager was started");
			}
		} else if (managers.size()==1 && managerName==null) {
			log.debug("Deprecated, please specify a manager name");
			return managers.values().iterator().next();
		} else {
			log.info("Deprecated, please specify a manager name");
			UDDIClerkManager manager = new UDDIClerkManager(null);
			addClerkManager(manager);
			manager.start();
			return manager;
		}
	}
	
	public static void addClerkManager(UDDIClerkManager manager) {
		managers.put(manager.getClientConfig().getManagerName(), manager);
	}
	
	public static void removeClerkManager(String managerName)
		throws ConfigurationException {
		if (managers.containsKey(managerName)) {
			managers.remove(managerName);
		} else if (managers.size()==1 && managerName==null) {
			String name = managers.keySet().iterator().next();
			log.info("Removing " + name + " from UDDIClient.");
			managers.remove(name);
		} else {
			throw new ConfigurationException("Could not remove UDDIClerkManager for name " + managerName);
		}
	}
	
	public static String getDefaultTransportClass() throws ConfigurationException {
		UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(null);
		return manager.getClientConfig().getUDDINode("default").getProxyTransport();
	}
	
	
	
	
	
}
