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

import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.UDDIErrorHelper;

/**
 *   E_authTokenRequired: (10120) Signifies that an authentication token is missing or is invalid for an API call that requires authentication.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class AuthTokenRequiredException extends AuthenticationException {

	private static final long serialVersionUID = 911181181554360596L;

	public AuthTokenRequiredException(ErrorMessage message) {
		super(message, UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_AUTH_TOKEN_REQUIRED));
	}
}
