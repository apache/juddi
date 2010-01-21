package org.apache.juddi.subscription;

import java.util.Vector;

public class NotificationList {
	private static NotificationList nl = null;
	private static Vector<String> list = null;
	
	private NotificationList() {	
		list = new Vector<String>();
	}
	
	public static NotificationList getInstance() {
		if (nl == null) {
			nl = new NotificationList();
		}
		return nl;
	}
	
	public Vector<String> getNotifications() {
		return list;
	}
	
}
