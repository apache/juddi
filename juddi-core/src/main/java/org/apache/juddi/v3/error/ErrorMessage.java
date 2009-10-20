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

package org.apache.juddi.v3.error;

import org.apache.juddi.config.ResourceConfig;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ErrorMessage {
	private String message;
	private String value;
	
	public ErrorMessage(String messageCode) {
		this(messageCode, null);
	}
	
	public ErrorMessage(String messageCode, String value) {
		this.message = ResourceConfig.getGlobalMessage(messageCode);
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		if (value == null || value.length() == 0)
			return message;
		else
			return message + ":  " + value;
			
	}
}
