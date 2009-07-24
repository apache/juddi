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

package org.apache.juddi.config;

import java.util.ResourceBundle;
import java.util.Locale;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ResourceConfig {
	public static final String GLOBAL_MESSAGES_FILE = "messages";

	private static final ResourceBundle globalMessages;

	static {
		try {
			globalMessages = ResourceBundle.getBundle(GLOBAL_MESSAGES_FILE, Locale.getDefault());
		}
		catch (Throwable t) {
			System.err.println("Initial configuration failed:" + t);
			throw new ExceptionInInitializerError(t);
		}
	}

	public static String getGlobalMessage(String key) {
		String msg = null;
		if (globalMessages != null) {
			if (key != null && key.length() > 0)
				msg = globalMessages.getString(key);
		}
		if (msg != null && msg.length() > 0)
			return msg;
		
		return key;
	}
}
