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
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.Name;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;

/**
 * This jUDDI action deletes a business and it's services from a jUDDI node.
 *
 */
public class UnregisterBusiness extends AbstractJUDDIAction
{
	private UDDIPublicationPortType publishService;
	private UDDIInquiryPortType inqueryService;
	private AuthToken authenticationToken;
	private BusinessEntity businessEntity;
	
	/**
	 * Constructor. The AuthToken and a business key of the business being
	 * deleted are given as arguments. 
	 * 
	 * @param authenticationToken
	 * @param businessKey
	 */
	public UnregisterBusiness(AuthToken authenticationToken,
			BusinessEntity businessEntity)
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.businessEntity = businessEntity;
	}

	/**
	 * The preValidate method ensures that all necessary elements
	 * are present to execute the action. Here we insure that the 
	 * publishing service is available. 
	 */
	@Override
	public void preValidate() throws Exception 
	{
		publishService = JUDDIServiceProvider.getPublishService();
		Assert.assertNotNull("Publish service is missing", publishService);
	}

	/**
	 * The execute method will send the SOAP message to jUDDI. In this case, 
	 * the business key is then given to a DeleteBusiness object
	 * and sent to the jUDDI node.
	 */
	@Override
	protected void execute() throws Exception 
	{
		
		DeleteBusiness deleteBusiness = new DeleteBusiness();
		deleteBusiness.setAuthInfo(authenticationToken.getAuthInfo());
		deleteBusiness.getBusinessKey().add(businessEntity.getBusinessKey());
		
		// execute the DeleteService operation.
		publishService.deleteBusiness(deleteBusiness);
	}

	/**
	 * Verifies that the deleted business can no longer be found. If a business list
	 * with business infos is returned, verifies that each returned business key is 
	 * different from the deleted business key.
	 */
	@Override
	protected void postValidate() throws Exception 
	{
		BusinessList businessList;
		
		Name name = new Name();
		name.setValue(businessEntity.getName().get(0).getValue());
		name.setLang(businessEntity.getName().get(0).getLang());
		
		FindBusiness findBusiness = new FindBusiness();
		findBusiness.getName().add(name);
		
		inqueryService = JUDDIServiceProvider.getInquiryService();
		businessList = inqueryService.findBusiness(findBusiness);
		try
		{
			Assert.assertNull("A non-null BusinessInfos object was retured in a search for the name of a " +
					"bussiness item that should previously have been deleted", businessList.getBusinessInfos());
		}
		catch(AssertionError e)
		{
			for (BusinessInfo bi: businessList.getBusinessInfos().getBusinessInfo())
			{
				Assert.assertFalse("A business with the same name and business key of the business that should " +
					"have been deleted has been found", bi.getBusinessKey()	.equals(businessEntity.getBusinessKey()));
			}
			
		}
		
	}
}
