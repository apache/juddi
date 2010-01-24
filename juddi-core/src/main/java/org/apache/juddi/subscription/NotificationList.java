package org.apache.juddi.subscription;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;

public class NotificationList  {
	private static NotificationList nl = null;
	private static Buffer list = null;
	
	private NotificationList() {	
		list = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(10));
	}
	
	public static NotificationList getInstance() {
		if (nl == null) {
			nl = new NotificationList();
		}
		return nl;	
	}
	
	public Buffer getNotifications() {
		return list;
	}
}
