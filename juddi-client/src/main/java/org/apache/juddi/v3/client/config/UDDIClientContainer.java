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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal jUDDI class to handle multiple clients on the same classloader.
 * 
 * @author kstam
 *
 */
public class UDDIClientContainer {

	private static Log log = LogFactory.getLog(UDDIClientContainer.class);
	private static Map<String,UDDIClient> clients = new HashMap<String, UDDIClient>();
	
	public static UDDIClient getUDDIClient(String clientName) 
		throws ConfigurationException {

		if (clientName!=null) {
			if (clients.containsKey(clientName)) {
				return (clients.get(clientName));
			} else {
                                StringBuilder sb = new StringBuilder();
                                Iterator<String> iterator = clients.keySet().iterator();
                                while (iterator.hasNext()){
                                        sb.append(iterator.next());
                                        if (iterator.hasNext())
                                                sb.append(",");
                                }
				throw new ConfigurationException("No client by name " + clientName + " was found. " +
						" Please check your client uddi.xml files, and make sure this client was started. Available clients: " + sb.toString());
			}
		} else throw new IllegalArgumentException("clientName is a required argument");
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

        /**
         * return true if the client exists in the current client collection
         * @param name
         * @return  true/false
         */
    public static boolean contains(String name) {
        return 	(clients.containsKey(name)) ;
    }
	
}
