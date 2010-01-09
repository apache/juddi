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
package org.apache.juddi.xlt.test;

import org.apache.juddi.xlt.action.SOAP.FindBusinessByName;
import org.apache.juddi.xlt.action.SOAP.GetAuthenticationToken;
import org.apache.juddi.xlt.action.SOAP.RegisterService;
import org.apache.juddi.xlt.util.AbstractUDDIClientTestCase;
import org.junit.Test;
import org.uddi.api_v3.BusinessInfo;

/**
 * This testcase publishes a single service to the "An Apache Node" Business
 * 
 *
 */
public class TPublishSingleService extends AbstractUDDIClientTestCase {

	@Test
	public void test() throws Throwable {
		GetAuthenticationToken getAuthenticationToken = new GetAuthenticationToken();
		getAuthenticationToken.run();

		FindBusinessByName findBusiness = new FindBusinessByName(
				getAuthenticationToken.getAuthenticationToken());
		findBusiness.run();
		
		for (BusinessInfo bi : findBusiness.getBusinessInfos().getBusinessInfo())
		{
			System.out.println(bi.getName().get(0).getValue());
		}
		

		RegisterService publishService = new RegisterService(
				getAuthenticationToken.getAuthenticationToken(), findBusiness
						.getBusinessInfo());
		publishService.run();
	}
}
