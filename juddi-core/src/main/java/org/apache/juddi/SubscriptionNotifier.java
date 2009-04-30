package org.apache.juddi;

import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.log4j.Logger;

public class SubscriptionNotifier implements Runnable {

	private Logger log = Logger.getLogger(this.getClass());
	
	public void run() {
		
		while(true) {
			try {
				Thread.sleep(AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 60000l));
				log.info("SubscriptionNotifier wakes up.");
				//check subscriptions
				
				//send out notifications who are either WS calls, or emails
				
			} catch (Exception e) {
				e.printStackTrace();
				//TODO 
			}
			log.info("Hello");
		}
		
	}

}
