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

import org.apache.log4j.Logger;

public class BackGroundRegistration implements Runnable {

	private UDDIClerkManager manager = null;
	private static Logger log = Logger.getLogger(UDDIClerkManager.class);
	

	public BackGroundRegistration(UDDIClerkManager manager) {
		super();
		this.manager = manager;
	}

	public void run() {
		log.info("Starting UDDI Clerks for manager " + manager.getClientConfig().getManagerName() + "...");
		if (manager.getClientConfig().isRegisterOnStartup()) {
			manager.saveClerkAndNodeInfo();
			manager.registerAnnotatedServices();
			manager.xRegister();
		}
		UDDIClientContainer.addClerkManager(manager);
		log.info("Clerks started succesfully for manager " + manager.getClientConfig().getManagerName());
	}
}
