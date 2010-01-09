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

import org.apache.juddi.xlt.action.SOAP.FindBusinessByName;
import org.apache.juddi.xlt.action.SOAP.GetAuthenticationToken;
import org.apache.juddi.xlt.flow.RegisterBusinessFlow;
import org.apache.juddi.xlt.util.AbstractUDDIClientTestCase;
import org.junit.Test;
import org.uddi.api_v3.BusinessEntity;
import com.xceptance.xlt.api.data.DataPool;
import com.xceptance.xlt.api.data.GeneralDataProvider;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * This test case will register businesses in the jUDDI node so 
 * that they can be searched for later. 
 *
 */
public class TFindBusinessPT extends AbstractUDDIClientTestCase
{
	//Create a pool that will be populated with some business entities that 
	//have been published to the jUDDI node.
	static private DataPool<BusinessEntity> dataPool = new DataPool<BusinessEntity>(100, 50);
	
	/**
	 * This method will search for a business by getting an element 
	 * from the pool. If there aren't any it will generate some and
	 * then get search for this business. It will also reoffer the 
	 * element to the pool once the business was found.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void findBusiness() throws Throwable
	{
		//Get an AuthToken.
		GetAuthenticationToken getAuthenticationToken = new GetAuthenticationToken();
		getAuthenticationToken.run();
		
		//Uses lists in config/data/default/
		GeneralDataProvider data = GeneralDataProvider.getInstance();
		
		//Pick a random number of services to publish under the business.
		int numberOfBusinessService = XltRandom.nextInt(getProperty("maxNumberOfBusinessServices", 1)) + 1;
		
		if(dataPool.getSize() <= 50)
		{
			
			//Pick a company name from a previously generated list of business names. 
			String businessName = data.getCompany(false);
			
			//Register the business with services
			RegisterBusinessFlow registerBusinessFlow = new RegisterBusinessFlow(getAuthenticationToken.getAuthenticationToken(),
					businessName, numberOfBusinessService);
			
			Thread.sleep(4000);
			
			//Add the returnThread.sleep(8000);ed BusinessEntity to the data pool.
			dataPool.add(registerBusinessFlow.getBusinessDetail().getBusinessEntity().get(0), 100);
				
		}
		else
		{
			//Get a business entity from the pool.
			BusinessEntity businessEntity= dataPool.getDataElement();
			
			if (businessEntity == null)
			{
				//Pick a company name from a previously generated list of business names. 
				String businessName = data.getCompany(false);
				
				//Register the business with services
				RegisterBusinessFlow registerBusinessFlow = new RegisterBusinessFlow(getAuthenticationToken.getAuthenticationToken(),
						businessName, numberOfBusinessService);
				
				Thread.sleep(4000);
				
				//Add the returnThread.sleep(8000);ed BusinessEntity to the data pool.
				dataPool.add(registerBusinessFlow.getBusinessDetail().getBusinessEntity().get(0), 100);
			}
			else
			{
				//Find the random business by passing it's name and the AuthToken.
				FindBusinessByName findBusinessByName = new FindBusinessByName(getAuthenticationToken.getAuthenticationToken(), 
						businessEntity.getName().get(0).getValue());
				findBusinessByName.run();
				
				//Offer the business entity back to the pool with 50% chance that it will be rejected
				//if the pool is full.
				dataPool.add(businessEntity);
			}
			
			
		}
		
	}	
}
