/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.xlt.action.SOAP;

import org.apache.juddi.xlt.util.JUDDIServiceProvider;
import org.junit.Assert;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This action will get an authentication token from the jUDDI node. 
 * 
 */
public class GetAuthenticationToken extends AbstractJUDDIAction {

	private UDDISecurityPortType securityService;

	private AuthToken authenticationToken;

	/**
	 * Constructor. 
	 */
	public GetAuthenticationToken() {
		super(null);
	}

	/**
	 * The preValidate method ensures that all necessary elements
	 * are present to execute the action. In this case, the Security 
	 * Service from the JUDDIServiceProvider must be available.
	 */
	@Override
	public void preValidate() throws Exception {
		securityService = JUDDIServiceProvider.getSecurityService();
		Assert.assertNotNull("Security service is missing");
	}
	
	/**
	 * The execute method will send the SOAP message to jUDDI. Here, we
	 * are requesting an AuthToken for user "root".
	 */
	@Override
	protected void execute() throws Exception {

		GetAuthToken getAuthToken = new GetAuthToken();
		getAuthToken.setUserID("root");
		getAuthToken.setCred("root");

		authenticationToken = securityService.getAuthToken(getAuthToken);
		
	}
	
	/**
	 * The postValidate ensure that the correct conditions exists after the 
	 * action has been executed. In this case, we verify that there was indeed
	 * a AuthToken returned.
	 */
	@Override
	protected void postValidate() throws Exception 
	{
		Assert.assertNotNull("AuthToken must not be null", authenticationToken);
	}

	/**
	 * Returns the AuthToken for use in subsequent actions.
	 * @return
	 */
	public AuthToken getAuthenticationToken() {
		return authenticationToken;
	}
}
