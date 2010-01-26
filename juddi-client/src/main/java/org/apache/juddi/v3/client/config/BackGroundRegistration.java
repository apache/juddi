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
