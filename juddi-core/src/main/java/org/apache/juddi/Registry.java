package org.apache.juddi;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.subscription.SubscriptionNotifier;
import org.apache.log4j.Logger;

public class Registry {
	
	private static Registry registry = null;
	private static Logger log = Logger.getLogger(Registry.class);
	private static SubscriptionNotifier subscriptionNotifier = null;
	/**
	 * Singleton.
	 */
	private Registry() {
		super();
	}
	/**
	 * Stops the registry.
	 */
	public static void stop() {
		if (registry!=null) {
			log.info("Stopping jUDDI registry...");
			subscriptionNotifier.cancel();
			registry=null;
		}
	}
	/**
	 * Starts the registry.
	 * @return
	 * @throws ConfigurationException
	 */
	public static Registry start() throws ConfigurationException {
		if (registry==null) {
			log.info("Creating new jUDDI registry...");
			registry = new Registry();
			subscriptionNotifier = new SubscriptionNotifier();
		}
		return registry;
 	}
}
