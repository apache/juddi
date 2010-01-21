package org.apache.juddi.subscription;

import java.util.Vector;

public class NotificationList {
	private static NotificationList nl = null;
	private static Vector<?> list = null;
	
	private NotificationList() {	
		list = new Vector();
	}
	
	@SuppressWarnings("unchecked")
	public static NotificationList getInstance() {
		if (nl == null) {
			nl = new NotificationList();
		}
		return nl;
	}
	
	@SuppressWarnings("unchecked")
	public Vector getNotifications() {
		return list;
	}
	
}
