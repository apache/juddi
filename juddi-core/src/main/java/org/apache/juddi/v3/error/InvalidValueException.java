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
 * E_invalidValue: (20200) This error code has multiple uses.  This error code applies to the subscription APIs and the value set APIs. It 
 * can be used to indicate that a value that was passed in a keyValue attribute did not pass validation.  This applies to checked value 
 * sets that are referenced using keyedReferences. The error text SHOULD clearly indicate the key and value combination that failed validation. 
 * It can also be used to indicate that a chunkToken supplied is invalid. This applies in both subscription and value set APIs.  The error text
 * SHOULD clearly indicate the reason for failure.
 *  
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class InvalidValueException extends RegistryException {

	private static final long serialVersionUID = -5108592555540144175L;

	public InvalidValueException(ErrorMessage message) {
		super(message, UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_VALUE));
	}
}
