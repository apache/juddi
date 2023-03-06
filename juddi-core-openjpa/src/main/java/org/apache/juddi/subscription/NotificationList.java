/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
