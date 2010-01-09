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
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.ServiceList;
import org.uddi.v3_service.UDDIInquiryPortType;

import com.xceptance.xlt.api.data.GeneralDataProvider;
/**
 * Search for services on the Juddi node.
 * @author Jeremi
 *
 */
public class FindServices extends AbstractJUDDIAction
{
	private UDDIInquiryPortType inquiryService;

	private AuthToken authenticationToken;

	private ServiceInfo serviceInfo;
	
	private ServiceInfos serviceInfos;
	
	private String serviceToFind;
	
	/**
	 * Constructor. If only the authentication token is passed, the action 
	 * will look for all services on the node by submitting the string "%" as
	 * the name of the service.
	 *  
	 * @param authenticationToken
	 */
	public FindServices(AuthToken authenticationToken) 
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.serviceToFind = "%";
	}
	
	/**
	 * Constructor. If a string is passed with the AuthToken, that string will 
	 * be submitted plus wild cards on each end to search for services with 
	 * names containing the passed string.
	 * 
	 * @param authenticationToken
	 * @param serviceToFind
	 */
	public FindServices(AuthToken authenticationToken, String serviceToFind) 
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.serviceToFind = "%" + serviceToFind + "%";
	}
	
	@Override
	public void preValidate() throws Exception 
	{
		inquiryService = JUDDIServiceProvider.getInquiryService();
		Assert.assertNotNull("Inquiry service is missing");
	}

	@Override
	protected void execute() throws Exception 
	{		
		FindQualifiers findQualifiers = new FindQualifiers();
		findQualifiers.getFindQualifier().add("sortByNameDesc");
		findQualifiers.getFindQualifier().add("approximateMatch");

		int maxServ = 0;
		Name r = new Name();
		r.setLang("en");
		r.setValue(serviceToFind);
		
		FindService findService = new FindService();
		findService.setAuthInfo(authenticationToken.getAuthInfo());
		findService.getName().add(r);
		findService.setFindQualifiers(findQualifiers);
		findService.setMaxRows(maxServ);
		
		ServiceList serviceList = inquiryService.findService(findService);
		serviceInfos = serviceList.getServiceInfos();
		
		//ServiceInfo serviceInfo = null;
		
		//serviceInfo becomes the first service found?
		for (ServiceInfo si : serviceInfos.getServiceInfo()) 
		{
			if (serviceInfo == null)
			{
				serviceInfo = si;
			}
			
			Name _name = si.getName().get(0);
			System.out.println("ServiceInfo #: " + _name.getValue());
		}

		
	}

	@Override
	protected void postValidate() throws Exception 
	{
		Assert.assertNotNull("No service found", serviceInfo);
	}

	public ServiceInfo getServiceInfo(int index) 
	{
		return serviceInfos.getServiceInfo().get(index);
	}
	
	public ServiceInfo getServiceInfo(String nameOrServiceKey) throws Exception
	{
		serviceInfo = null;
		
		for (ServiceInfo si : serviceInfos.getServiceInfo())
		{
			for (Name n : si.getName())
			{
				if (n.getValue().equals(nameOrServiceKey))
				{
					serviceInfo = si;
				}
			}
			if (si.getServiceKey().equals(nameOrServiceKey))
			{
				serviceInfo = si;
			}
		}
		
		return serviceInfo;	
	}
	
	public ServiceInfos getServiceInfos() 
	{
		return serviceInfos;
	}
}
