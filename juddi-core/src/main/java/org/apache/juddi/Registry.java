package org.apache.juddi;

import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.rmi.JNDIRegistration;
import org.apache.juddi.rmi.RMIRegistration;
import org.apache.juddi.subscription.SubscriptionNotifier;

public class Registry {
	
	private static Registry registry = null;
	private static Log log = LogFactory.getLog(Registry.class);
	private static SubscriptionNotifier subscriptionNotifier = null;
	/**
	 * Singleton.
	 */
	private Registry() {
		super();
	}
	/**
	 * Stops the registry.
	 * @throws ConfigurationException 
	 */
	public synchronized static void stop() throws ConfigurationException {
		if (registry!=null) {
			log.info("Stopping jUDDI registry...");
			if (subscriptionNotifier!=null) {
				log.info("Shutting down SubscriptionNotifier");
				subscriptionNotifier.cancel();
				subscriptionNotifier=null;
			}
			if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_JNDI_REGISTRATION, false)) {
				try {
					JNDIRegistration.getInstance().unregister();
				} catch (NamingException e) {
					log.error("Unable to Register jUDDI services with JNDI. " + e.getMessage(), e);
				}
			}
			registry=null;
			log.info("jUDDI shutdown completed.");
		}
	}
	/**
	 * Starts the registry.
	 * @throws ConfigurationException 
	 */
	public synchronized static void start() throws ConfigurationException {
		if (registry==null) {
			log.info("Starting jUDDI registry...");
			registry = new Registry();
			
			if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_SUBSCRIPTION_NOTIFICATION, true)) {
				subscriptionNotifier = new SubscriptionNotifier();
			}
			if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_JNDI_REGISTRATION, false)) {
				try {
					int port = AppConfig.getConfiguration().getInteger(Property.JUDDI_RMI_PORT,0);
					JNDIRegistration.getInstance().register(port);
				} catch (NamingException e) {
					log.error("Unable to Register jUDDI services with JNDI. " + e.getMessage(), e);
				}
			}
			if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_RMI_REGISTRATION, false)) {
				try {
					int rmiport = AppConfig.getConfiguration().getInteger(Property.JUDDI_RMI_REGISTRY_PORT,1099);
					int port = AppConfig.getConfiguration().getInteger(Property.JUDDI_RMI_PORT,0);
					RMIRegistration.getInstance(rmiport).register(port);
				} catch (Exception e) {
					log.error("Unable to Register jUDDI services with RMI Registry. " + e.getMessage(), e);
				}
			}
			
			log.info("jUDDI registry started succesfully.");
		}
 	}
	
}

