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
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;

/**
 * This jUDDI action deletes a service from a business in a jUDDI node.
 * 
 * @author jeremi
 *
 */
public class UnregisterService extends AbstractJUDDIAction
{
	private UDDIPublicationPortType publishService;
	private AuthToken authenticationToken;
	private ServiceInfo serviceInfo;
	private UDDIInquiryPortType inqueryService;
	
	/**
	 * Constructor. The AuthToken and a ServiceInfo of the service being
	 * deleted are given as arguments. 
	 * 
	 * @param authenticationToken
	 * @param serviceInfo
	 */
	public UnregisterService(AuthToken authenticationToken,
			ServiceInfo serviceInfo)
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.serviceInfo = serviceInfo;	
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
	 * The execute method will send the SOAP message to jUDDI. In this case, 
	 * the service key is then given to a DeleteService object
	 * and sent to the jUDDI node.
	 */
	@Override
	protected void execute() throws Exception 
	{
		
		DeleteService deleteService = new DeleteService();
		deleteService.setAuthInfo(authenticationToken.getAuthInfo());
		deleteService.getServiceKey().add(serviceInfo.getServiceKey());
		
		// execute the DeleteService operation.
		publishService.deleteService(deleteService);
	}

	/**
	 * Verifies that the deleted business can no longer be found. If a business list
	 * with business infos is returned, verifies that each returned business key is 
	 * different from the deleted business key.
	 */
	@Override
	protected void postValidate() throws Exception 
	{	
		Name name = serviceInfo.getName().get(0);
		String businessKey = serviceInfo.getBusinessKey();
		String serviceKey = serviceInfo.getServiceKey();
		
		FindService findService = new FindService();
		findService.getName().add(name);
		findService.setBusinessKey(businessKey);
		
		inqueryService = JUDDIServiceProvider.getInquiryService();
		ServiceList serviceList = inqueryService.findService(findService);
		
		try
		{
			Assert.assertNull("A non-null ServiceInfos object was retured in a search for the name of a " +
					"service item that should previously have been deleted", serviceList.getServiceInfos());
		}
		catch(AssertionError e)
		{
			for (ServiceInfo si: serviceList.getServiceInfos().getServiceInfo())
			{
				Assert.assertFalse("A service with the same name and service key of the service that should " +
					"have been deleted has been found", si.getServiceKey().equals(serviceKey));
			}
			
		}
	}
}
