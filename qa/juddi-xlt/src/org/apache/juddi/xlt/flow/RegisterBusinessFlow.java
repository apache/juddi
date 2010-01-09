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
package org.apache.juddi.xlt.flow;

import org.apache.juddi.xlt.action.SOAP.RegisterBusiness;
import org.apache.juddi.xlt.action.SOAP.RegisterService;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessDetail;

/**
 * This flow registers a business with services under it. This should be 
 * used whenever a business is published since businesses without services
 * are presently not searchable (2009-08-10).
 *  
 * @author jeremi
 *
 */
public class RegisterBusinessFlow 
{
	BusinessDetail businessDetail;
	
	/**
	 * Constructor. Publishes a business under the given name and publishes the 
	 * specified number of services under it.
	 * 
	 * @param authenticationToken
	 * @param businessName
	 * @param numberOfBusinessService
	 * @throws Throwable
	 */
	public RegisterBusinessFlow(AuthToken authenticationToken, String businessName, int numberOfBusinessService) throws Throwable
	{ 
		//register the business under "businessName".
		RegisterBusiness registerBusiness = new RegisterBusiness(authenticationToken, businessName);
		registerBusiness.run();
		
		businessDetail = registerBusiness.getBusinessDetail();
		
		//get the returned business key and name.
		String businesskey = businessDetail.getBusinessEntity().get(0).getBusinessKey();
		String name = businessDetail.getBusinessEntity().get(0).getName().get(0).getValue();		
		
		//register numberOfBusinessService services under the new business.
		for (int serviceCount = 0; serviceCount <= numberOfBusinessService; serviceCount++ )
		{
			RegisterService registerService = new RegisterService(authenticationToken, businesskey, name);
			registerService.run();
		}
	}
	
	/**
	 * Constructor. Publishes a business under the unique name and publishes the 
	 * specified number of services under it.
	 * 
	 * @param authenticationToken
	 * @param numberOfBusinessService
	 * @throws Throwable
	 */
	public RegisterBusinessFlow(AuthToken authenticationToken, int numberOfBusinessService)throws Throwable
	{ 
		//register the business under a unique name.
		RegisterBusiness registerBusiness = new RegisterBusiness(authenticationToken);
		registerBusiness.run();
		
		businessDetail = registerBusiness.getBusinessDetail();
		
		//get the returned business key and name.
		String businesskey = registerBusiness.getBusinessDetail().getBusinessEntity().get(0).getBusinessKey();
		String name = registerBusiness.getBusinessDetail().getBusinessEntity().get(0).getName().get(0).getValue();	
		
		//register numberOfBusinessService services under the new business.
		for (int serviceCount = 0; serviceCount < numberOfBusinessService; serviceCount++ )
		{
			RegisterService registerService = new RegisterService(authenticationToken, businesskey, name);
			registerService.run();
		}
	}
	
	/**
	 * Returns the BusinessDetail object.
	 * @return
	 */
	public BusinessDetail getBusinessDetail()
	{
		return businessDetail;
	}
}
