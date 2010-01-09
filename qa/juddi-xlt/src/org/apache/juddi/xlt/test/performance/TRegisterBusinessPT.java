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
package org.apache.juddi.xlt.test.performance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.juddi.xlt.action.SOAP.GetAuthenticationToken;
import org.apache.juddi.xlt.flow.RegisterBusinessFlow;
import org.apache.juddi.xlt.util.AbstractUDDIClientTestCase;
import org.junit.Test;

import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * This testcase registers a new business and some services under it, and then 
 * prints out the name of the business to file. 
 *
 */

public class TRegisterBusinessPT extends AbstractUDDIClientTestCase
{	
	/**
	 * This Test method will first get an AuthToken, save a business under a 
	 * unique name, save a random number of service under that new business, 
	 * and then write the name of the newly published business to a file.   
	 * 
	 * @throws Throwable
	 */
	@Test
	public void registerBusiness() throws Throwable
	{
		//Get an Authentification Token.
		GetAuthenticationToken getAuthenticationToken = new GetAuthenticationToken();
		getAuthenticationToken.run();
		
		//Generate a unique business name including a pseudo random number.
		String uniqueBusinessName = "business no " + UUID.randomUUID().toString();
		
		//Publish a random number of services (0 < number of services generate < maxNumberOfBusinessServices).
		//If the property maxNumberOfBusinessServices is not found, the max will be the second argument 
		//of the getProperties method call (in this case 2
		int numberOfBusinessService = XltRandom.nextInt(getProperty("maxNumberOfBusinessServices", 1)) + 1;
		
		//Save business under generated name using Auth token and save the above random number of services.
		RegisterBusinessFlow registerBusinessFlow = new RegisterBusinessFlow(getAuthenticationToken.getAuthenticationToken(), 
				uniqueBusinessName, numberOfBusinessService); 
	}

}
