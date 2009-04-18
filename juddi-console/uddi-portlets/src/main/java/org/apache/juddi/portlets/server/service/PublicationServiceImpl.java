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

import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.service.PublicationResponse;
import org.apache.juddi.portlets.client.service.PublicationService;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.InfoSelection;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.i18n.EntityForLang;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.v3_service.UDDIPublicationPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class PublicationServiceImpl extends RemoteServiceServlet implements PublicationService {

	private Logger logger = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	public PublicationResponse getBusinesses(String authToken, String infoSelection) 
	{
		HttpServletRequest request = this.getThreadLocalRequest();
		String lang = request.getLocale().getLanguage();
		
		GetRegisteredInfo getRegistrationInfo = new GetRegisteredInfo();
		getRegistrationInfo.setAuthInfo(authToken);
		getRegistrationInfo.setInfoSelection(InfoSelection.ALL);
		
		PublicationResponse response = new PublicationResponse();
		logger.debug("GetRegistrationInfo " + getRegistrationInfo + " sending get Busineses request..");
		List<Business> businesses = new ArrayList<Business>();
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
        	 Transport transport = (Transport) transportClass.newInstance(); 
        	 UDDIPublicationPortType publicationService = transport.getPublishService();
        	 RegisteredInfo info = publicationService.getRegisteredInfo(getRegistrationInfo);
        	 for (BusinessInfo businessInfo : info.getBusinessInfos().getBusinessInfo()) {
				Business business = new Business(
						businessInfo.getBusinessKey(),
						EntityForLang.get(businessInfo.getName(),lang).getValue(),
						EntityForLang.get(businessInfo.getDescription(),lang).getValue());
				List<Service> services = new ArrayList<Service>();
				for (ServiceInfo serviceInfo : businessInfo.getServiceInfos().getServiceInfo()) {
					Service service = new Service(
							serviceInfo.getServiceKey(),
							EntityForLang.get(serviceInfo.getName(), lang).getValue());
					services.add(service);
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
