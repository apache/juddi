package org.apache.juddi.subscription;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;

public class NotificationList<T>  {
	private static NotificationList<String> nl = null;
	private static Buffer list = null;
	
	private NotificationList() {	
		list = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(10));
	}
	
	public static NotificationList<String> getInstance() {
		if (nl == null) {
			nl = new NotificationList<String>();
		}
		return nl;	
	}
	
	public Buffer getNotifications() {
		return list;
	}
}
