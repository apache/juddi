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

import org.apache.juddi.xlt.action.SOAP.GetAuthenticationToken;
import org.apache.juddi.xlt.action.SOAP.UnregisterBusiness;
import org.apache.juddi.xlt.flow.RegisterBusinessFlow;
import org.apache.juddi.xlt.util.AbstractUDDIClientTestCase;
import org.junit.Test;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessEntity;

import com.xceptance.xlt.api.data.DataPool;
import com.xceptance.xlt.api.data.GeneralDataProvider;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * 
 * @author jeremi
 *
 */
public class TDeleteBusinessPT extends AbstractUDDIClientTestCase
{
	//Create a pool that will be populated with some business entities that 
	//have been published to the jUDDI node.
	static DataPool<BusinessEntity> dataPool = new DataPool<BusinessEntity>(20, 50);
	
	//Uses lists in config/data/default/
	GeneralDataProvider data = GeneralDataProvider.getInstance();

	@Test
	public void deleteBusiness() throws Throwable 
	{
		GetAuthenticationToken getAuthenticationToken = new GetAuthenticationToken();
		getAuthenticationToken.run();
		
		//Get the AuthToken
		AuthToken authToken= getAuthenticationToken.getAuthenticationToken();
		
		BusinessEntity businessEntity;
		
		// If there are no businesses in the data pool submit one with a rate of 100 
		// to ensure that it will be entered
		if(dataPool.getSize() <= 10)
		{
			//Add a business to the pool since there are none
			addBusinessToPool(authToken, 100);				
		}	
		else
		{
			//Get a business entity from the pool.
			businessEntity= dataPool.getDataElement();
		
			//If the entity is null (pool is empty), add an element
			if(businessEntity == null)
			{
				//Put another one in
				addBusinessToPool(authToken);
			}
			else
			{
				//Delete the business gotten from the pool.
				UnregisterBusiness unregisterBusiness = new UnregisterBusiness(authToken, businessEntity);
				unregisterBusiness.run();
			}
		}

	}
	
	/**
	 * Adds a new business to the juddi node and adds 
	 * it's BussinessEntity to the data pool.
	 * 
	 * @param authToken
	 * @throws Throwable
	 */
	
	private void addBusinessToPool(AuthToken authToken, int rate) throws Throwable 
	{
		//Pick a random number of services to publish under the business.
		int numberOfBusinessService = XltRandom.nextInt(getProperty("maxNumberOfBusinessServices", 1)) + 1;
		
		//Pick a company name from a previously generated list of business names. 
		String businessName = data.getCompany(false);
		
		//Register the business with services
		RegisterBusinessFlow registerBusinessFlow = new RegisterBusinessFlow(authToken,
				businessName + "1TDel", numberOfBusinessService);
		
		//Add the returned BusinessEntity to the data pool.
		dataPool.add(registerBusinessFlow.getBusinessDetail().getBusinessEntity().get(0), rate);
	}
	
	/**
	 * Adds a new business to the juddi node and try to 
	 * add it's BussinessEntity to the data pool (may be randomly
	 * rejected). 
	 * 
	 * @param authToken
	 * @throws Throwable
	 */
	
	private void addBusinessToPool(AuthToken authToken) throws Throwable 
	{
		//Pick a random number of services to publish under the business.
		int numberOfBusinessService = XltRandom.nextInt(getProperty("maxNumberOfBusinessServices", 1)) + 1;
		
		//Pick a company name from a previously generated list of business names. 
		String businessName = data.getCompany(false);
		
		//Register the business with services
		RegisterBusinessFlow registerBusinessFlow = new RegisterBusinessFlow(authToken,
				businessName, numberOfBusinessService);
		
		//Add the returned BusinessEntity to the data pool.
		dataPool.add(registerBusinessFlow.getBusinessDetail().getBusinessEntity().get(0));
	}
}


