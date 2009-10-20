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

import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.RegistryException;
import org.apache.juddi.v3.error.UDDIErrorHelper;

/**
 * E_invalidTime: (40030) Signifies that the time period, the date/time, or the pair of date/time is invalid. The error structure 
 * signifies the condition that occurred and the error text clearly calls out the cause of the problem.
 *  
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class InvalidTimeException extends RegistryException {

	private static final long serialVersionUID = 8225896425924629956L;

	public InvalidTimeException(ErrorMessage message) {
		super(message, UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_KEY_PASSED));
	}
}
