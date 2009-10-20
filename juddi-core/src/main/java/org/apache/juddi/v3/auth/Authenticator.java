/*
 * Copyright 2001-2009 The Apache Software Foundation.
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

package org.apache.juddi.v3.auth;

import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.FatalErrorException;

/**
 * Authenticator interface. Any class implementing this interface can be invoked by
 * the AuthenticatorFactory to handle authentication of the user executing a UDDI request.
 * 
 * This occurs in two steps:
 * 
 * 1) Authenticating the user based the passed credentials
 * 2) Identifying the user by associating either the authorizationKey or user Id with publisher information
 * 
 * @author Steve Viens (sviens@apache.org)
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public interface Authenticator {

	/**
	 * 
	 * @param authorizedName - userId of the user making the registry request
	 * @param cred   - some authentical creditial (i.e. a password) which can be used to 
	 * 		           authenticate the user.
	 * 
	 * @return The publisherID for this user
	 * @throws {@link AuthenticationException}, {@link FatalErrorException}
	 */
	String authenticate(String authorizedName, String cred) throws AuthenticationException, FatalErrorException;
	
	/*
	 * Either input parameter can be used to populate the publisher object
	 * 
	 * @param authInfo - the authorization token
	 * @param authorizedName - the authorized Name
	 * 
	 * @return - The entity publisher
	 */
	UddiEntityPublisher identify(String authInfo, String authorizedName) throws AuthenticationException, FatalErrorException;
}
