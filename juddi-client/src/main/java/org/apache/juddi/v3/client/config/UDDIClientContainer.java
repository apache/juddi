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
	private static Map<String,UDDIClient> clients = new HashMap<String, UDDIClient>();
	
	public static UDDIClient getUDDIClient(String clientName) 
		throws ConfigurationException {

		if (clientName!=null) {
			if (clients.containsKey(clientName)) {
				return (clients.get(clientName));
			} else {
				throw new ConfigurationException("No client by name " + clientName + " was found. " +
						" Please check your client uddi.xml files, and make sure this client was started");
			}
		} else if (clients.size()==1 && clientName==null) {
			log.warn("Deprecated, please specify a client name");
			return clients.values().iterator().next();
		} else {
			log.warn("Deprecated, please specify a client name");
			UDDIClient client = new UDDIClient(null);
			addClient(client);
			client.start();
			return client;
		}
	}
	
	public static boolean addClient(UDDIClient manager) {
		if (!clients.containsKey(manager.getClientConfig().getClientName())) {
			clients.put(manager.getClientConfig().getClientName(), manager);
			return true;
		} else {
			return false;
		}
	}
	
	public static void removeClerkManager(String clientName)
		throws ConfigurationException {
		if (clients.containsKey(clientName)) {
			clients.remove(clientName);
		} else if (clients.size()==1 && clientName==null) {
			String name = clients.keySet().iterator().next();
			log.info("Removing " + name + " from UDDIClient.");
			clients.remove(name);
		} else {
			throw new ConfigurationException("Could not remove UDDIClient for name " + clientName);
		}
	}
	
}
