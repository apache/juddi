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

import org.apache.juddi.xlt.action.SOAP.GetAuthenticationToken;
import org.apache.juddi.xlt.action.SOAP.RegisterBusiness;
import org.apache.juddi.xlt.action.SOAP.Subscribe;
import org.junit.Test;
import org.uddi.api_v3.AuthToken;
import com.xceptance.xlt.api.tests.AbstractTestCase;

public class TSubscription extends AbstractTestCase
{
	private String businessKey;
	private String serviceKey;
	
	/**
	 * 
	 */
	@Test
	public void subscribe() throws Throwable
	{
		GetAuthenticationToken getAuthenticationToken = new GetAuthenticationToken();
		getAuthenticationToken.run();
		AuthToken authToken = getAuthenticationToken.getAuthenticationToken();
		
		RegisterBusiness registerBusiness = new RegisterBusiness(authToken, "Abonement");
		registerBusiness.run();
		
		//FindBusinessByName findBusinessByName = new FindBusinessByName(authToken, 
		//		businessName);
		//if (findBusinessByName.preValidate())
		//{
				Subscribe subscribe = new Subscribe(authToken, registerBusiness.getBusinessDetail().getBusinessEntity().get(0).getBusinessKey());
				subscribe.run();
				
				System.out.println(subscribe.getSubscriptionKey());
		//}
		//else
		//{
		//	RegisterBusiness
		//}
	}
	

}
