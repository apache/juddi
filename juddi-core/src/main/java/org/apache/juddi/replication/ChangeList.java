package org.apache.juddi.replication;

import org.apache.juddi.subscription.*;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;

public class ChangeList<T>  {
	private static ChangeList<String> nl = null;
	private static Buffer list = null;
	
	private ChangeList() {	
		list = BufferUtils.synchronizedBuffer(new CircularFifoBuffer(10));
	}
	
	public static ChangeList<String> getInstance() {
		if (nl == null) {
			nl = new ChangeList<String>();
		}
		return nl;	
	}
	
	public Buffer getNotifications() {
		return list;
	}
}
