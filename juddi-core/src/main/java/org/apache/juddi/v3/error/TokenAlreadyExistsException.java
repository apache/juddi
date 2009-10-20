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
 * E_tokenAlreadyExists: (40070) Signifies that one or more of the businessKey or tModelKey elements that identify entities to be transferred 
 * are not owned by the publisher identified by the authInfo element.  The error text SHOULD clearly indicate which entity keys caused the error.  
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TokenAlreadyExistsException extends RegistryException {

	private static final long serialVersionUID = 2721076133362327262L;

	public TokenAlreadyExistsException(ErrorMessage message) {
		super(message, UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_TOKEN_ALREADY_EXISTS));
	}
}
