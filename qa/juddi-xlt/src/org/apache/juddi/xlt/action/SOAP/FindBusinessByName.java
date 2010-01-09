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
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.Name;
import org.uddi.v3_service.UDDIInquiryPortType;

/**
 * This action will try to find a business on the jUDDI node given a certain name.
 * 
 */
public class FindBusinessByName extends AbstractJUDDIAction {

	private UDDIInquiryPortType inquiryService;

	private AuthToken authenticationToken;

	private BusinessInfo businessInfo;

	private BusinessInfos businessInfos;
	
	private String businessName = "%";

	/**
	 * Constructor. Using only the Auth Token as argument will run a search for 
	 * all businesses.
	 * 
	 * @param authenticationToken
	 */
	public FindBusinessByName(AuthToken authenticationToken) {
		super(null);
		this.authenticationToken = authenticationToken;
	}
	/**
	 * Constructor. using the Auth Token and a business name as arguments 
	 * will run a search for the specific business name.
	 * 
	 * @param authenticationToken
	 * @param businessName
	 */
	public FindBusinessByName(AuthToken authenticationToken, String businessName) {
		super(null);
		this.authenticationToken = authenticationToken;
		this.businessName = businessName;
	}

	/**
	 * The preValidate method ensures that all necessary elements
	 * are present to execute the action. Here we insure that the 
	 * inquiry service is available.
	 */
	@Override
	public void preValidate() throws Exception {
		inquiryService = JUDDIServiceProvider.getInquiryService();
		Assert.assertNotNull("Inquiry service is missing");
	}

	/**
	 * The execute method will send the SOAP message to jUDDI. Here, we create a 
	 * FindBusiness object with the parameters we wish to search for, the business
	 * name. The object is then sent to the jUDDI node, unmarshalled, and a 
	 * BusinessList is returned. The list hold info on all matching businesses.
	 */
	@Override
	protected void execute() throws Exception {

		//Name of business were looking for in English.
		int max = 5;
		Name q = new Name();
		q.setLang("en");
		q.setValue(businessName);

		//Optional qualifiers that will modify the search.
		FindQualifiers findQualifiers = new FindQualifiers();
		findQualifiers.getFindQualifier().add("sortByNameDesc");
		findQualifiers.getFindQualifier().add("approximateMatch");

		//The FindBusiness object that is submitted via the inquiry service. 
		FindBusiness findBusiness = new FindBusiness();
		findBusiness.setAuthInfo(authenticationToken.getAuthInfo());
		findBusiness.getName().add(q);
		findBusiness.setFindQualifiers(findQualifiers);
		findBusiness.setMaxRows(max);

		BusinessList businessList = inquiryService.findBusiness(findBusiness);
		businessInfos = businessList.getBusinessInfos();

		//Print out the names of the returned businesses
		for (BusinessInfo bi : businessInfos.getBusinessInfo()) {
			if (businessInfo == null) {
				businessInfo = bi;
			}
			Name _name = bi.getName().get(0);
			System.out.println("BusinessInfo: " + _name.getValue());
		}

		
	}

	/**
	 * The postValidate ensure that the correct conditions exists after the 
	 * action has been executed. Here, we ensure that there is indeed a 
	 * returned BusinessInfo object (in the BusinessList) and that it contains 
	 * the business name we were looking for.
	 */
	@Override
	protected void postValidate() throws Exception 
	{
		
		Assert.assertNotNull("No business found", businessInfos);
		
		// Check all returned businesses to ensure they contain the search business name
		if(!businessName.contains("%"))
		{
			for (BusinessInfo bi : businessInfos.getBusinessInfo()) 
			{
				Assert.assertTrue("Wrong business name was returned:" + bi.getName().get(0).getValue()  + " - got instead: " + businessName, bi.getName().get(0).getValue().toLowerCase().equals(businessName.toLowerCase()));
			}
		}
	}

	/**
	 * Return the first BusinessInfo in the list.
	 * 
	 * @return
	 */
	public BusinessInfo getBusinessInfo() {
		return businessInfo;
	}
	
	/**
	 * Return the BusinessInfos object.
	 * 
	 * @return
	 */
	public BusinessInfos getBusinessInfos() {
		return businessInfos;
	}
}
