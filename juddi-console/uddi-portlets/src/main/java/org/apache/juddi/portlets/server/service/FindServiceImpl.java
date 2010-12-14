/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.portlets.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.service.FindResponse;
import org.apache.juddi.portlets.client.service.FindService;
import org.apache.juddi.v3.client.config.WebHelper;
import org.apache.juddi.v3.client.i18n.EntityForLang;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.v3_service.UDDIInquiryPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class FindServiceImpl extends RemoteServiceServlet implements FindService {

	private static final long serialVersionUID = 1939609260067702168L;
	private Log logger = LogFactory.getLog(this.getClass());


	public FindResponse getBusinesses(String nameStr, String[] findQualifyers) 
	{
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession();
		String lang = request.getLocale().getLanguage();
		FindResponse response = new FindResponse();
		try {
			FindBusiness findBusiness = new FindBusiness();
			FindQualifiers findQualifiers = new FindQualifiers();
			for (String string : findQualifyers) {
				findQualifiers.getFindQualifier().add(string);
			}
			findBusiness.setFindQualifiers(findQualifiers);

			Name name = new Name();
			name.setValue(nameStr);
			findBusiness.getName().add(name);


			logger.debug("FindBusiness " + findBusiness + " sending findBusinesses request..");
			List<Business> businesses = new ArrayList<Business>();
			
			Transport transport = WebHelper.getTransport(session.getServletContext());
			UDDIInquiryPortType inquiryService = transport.getUDDIInquiryService();
			BusinessList businessList = inquiryService.findBusiness(findBusiness);
			for (BusinessInfo businessInfo : businessList.getBusinessInfos().getBusinessInfo()) {
				Business business = new Business(
						businessInfo.getBusinessKey(),
						EntityForLang.getName(businessInfo.getName(),lang).getValue(),
						EntityForLang.getDescription(businessInfo.getDescription(),lang).getValue());
				List<Service> services = new ArrayList<Service>();
				if (businessInfo.getServiceInfos()!=null) {
					for (ServiceInfo serviceInfo : businessInfo.getServiceInfos().getServiceInfo()) {
						Service service = new Service(
								serviceInfo.getServiceKey(),
								EntityForLang.getName(serviceInfo.getName(), lang).getValue());
						services.add(service);
					}
				}
				business.setServices(services);
				businesses.add(business);
			}
			response.setSuccess(true);
			response.setBusinesses(businesses);
		} catch (Exception e) {
			logger.error("Could not obtain token. " + e.getMessage(), e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setErrorCode("102");
		}  catch (Throwable t) {
			logger.error("Could not obtain token. " + t.getMessage(), t);
			response.setSuccess(false);
			response.setMessage(t.getMessage());
			response.setErrorCode("102");
		} 
		return response;
	}
}
