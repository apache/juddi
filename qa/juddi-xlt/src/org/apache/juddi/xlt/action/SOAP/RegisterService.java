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

import org.apache.commons.lang.StringUtils;
import org.apache.juddi.xlt.util.JUDDIServiceProvider;
import org.junit.Assert;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.v3_service.UDDIPublicationPortType;

import com.xceptance.xlt.api.data.GeneralDataProvider;

/**
 * Publishes a service to the jUDDI node under a certain business.
 * @author jeremi
 *
 */
public class RegisterService extends AbstractJUDDIAction {

	private UDDIPublicationPortType publishService;
	private AuthToken authenticationToken;
	private String businessKey;
	private String businessName;
	private String serviceName;
	
	private ServiceDetail serviceDetail;

	/**
	 * Constructor. The AuthToken and the business key are given as arguments
	 * so that the service can be published under the certain business. The
	 * business name is also given but is only used to generate data for 
	 * certain values in the BusinessService object.
	 * 
	 * @param authenticationToken
	 * @param businessKey
	 * @param businessName
	 */
	public RegisterService(AuthToken authenticationToken, String businessKey, String businessName) 
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.businessKey = businessKey;
		this.businessName = businessName;
	}
	
	/**
	 * Constructor. Here a BusinessInfo object is given instead of the name and key.
	 * The object contains said values and are extracted instead
	 * 
	 * @param authentificationToken
	 * @param businessInfo
	 */
	public RegisterService(AuthToken authentificationToken, BusinessInfo businessInfo)
	{
		super(null);
		this.authenticationToken = authentificationToken;
		this.businessKey = businessInfo.getBusinessKey();
		this.businessName = businessInfo.getName().get(0).getValue();
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
		Assert.assertNotNull("Publish service is missing");
	}
	
	/**
	 * The execute method will send the SOAP message to jUDDI. Here, we create a 
	 * BusinessService object and fill the different values with random
	 * information. The BusinessService is then given to a SaveService object
	 * and published to the jUDDI node.
	 */
	@Override
	protected void execute() throws Exception 
	{
		GeneralDataProvider data = GeneralDataProvider.getInstance();
		serviceName = data.getCountry(false);// retrieves random service name from country file
		
		//Name of the service in English.
		Name name = new Name();
		name.setLang("en");
		name.setValue(serviceName);

		//Description of the service in English.
		Description description = new Description();
		description.setLang("en");
		description.setValue(data.getText(1, false));

		//Access Point details.
		AccessPoint accessPoint = new AccessPoint();
		accessPoint.setUseType("endPoint");
		accessPoint.setValue("http://www." + StringUtils.deleteWhitespace(businessName) + ".com:8080/uddi/services/" + StringUtils.deleteWhitespace(serviceName) + "?wsdl");

		//Description of the Access Point, in English.
		Description accessPointDescription = new Description();
		accessPointDescription.setLang("en");
		accessPointDescription
				.setValue(data.getText(1, 3, false));

		//Pass binging info to binding template.
		BindingTemplate bindingTemplate = new BindingTemplate();
		bindingTemplate.setAccessPoint(accessPoint);
		bindingTemplate.getDescription().add(accessPointDescription);
		
		//There can be several binding templates. Here only one is passed.
		BindingTemplates bindingTemplates = new BindingTemplates();
		bindingTemplates.getBindingTemplate().add(bindingTemplate);

		//Pass all info to BusinessService object.
		BusinessService businessService = new BusinessService();
		businessService.setBusinessKey(businessKey);
		businessService.setBindingTemplates(bindingTemplates);
		businessService.getName().add(name);
		businessService.getDescription().add(description);

		//Pass service to the SaveService object and publish
		SaveService saveService = new SaveService();
		saveService.setAuthInfo(authenticationToken.getAuthInfo());
		saveService.getBusinessService().add(businessService);

		// execute the SaveService operation and get back the ServiceDetail
		serviceDetail = publishService.saveService(saveService);

		
	}

	/**
	 * The postValidate ensure that the correct conditions exists after the 
	 * action has been executed. Here, we ensure that there is indeed a 
	 * returned ServiceDetail object and that it contains a service key
	 * and the proper service name.
	 */
	@Override
	protected void postValidate() throws Exception 
	{
		Assert.assertNotNull("No service detail retrieved", serviceDetail);
		String returnedKey = serviceDetail.getBusinessService().get(0).getServiceKey();
		String returnedName = serviceDetail.getBusinessService().get(0).getName().get(0).getValue();
		Assert.assertNotNull("Returned details have no service key.", returnedKey);
		Assert.assertTrue("Service was not saved under the right name. '" 
				+ serviceName + "' was expected but '" + returnedName + "' was returned.",
				returnedName.equals(serviceName));
	}
	
	/**
	 * Returns the service details.
	 * @return
	 */
	public ServiceDetail getServiceDetail()
	{
		return serviceDetail;
	}
}
