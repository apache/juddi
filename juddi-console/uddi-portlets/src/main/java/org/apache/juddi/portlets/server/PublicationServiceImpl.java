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
package org.apache.juddi.portlets.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.juddi.portlets.client.PublicationResponse;
import org.apache.juddi.portlets.client.PublicationService;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.InfoSelection;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
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
		GetRegisteredInfo getRegistrationInfo = new GetRegisteredInfo();
		getRegistrationInfo.setAuthInfo(authToken);
		getRegistrationInfo.setInfoSelection(InfoSelection.ALL);
		
		PublicationResponse response = new PublicationResponse();
		logger.debug("GetRegistrationInfo " + getRegistrationInfo + " sending get Busineses request..");
		List<String> businesses = new ArrayList<String>();
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
        	 Transport transport = (Transport) transportClass.newInstance(); 
        	 UDDIPublicationPortType publicationService = transport.getPublishService();
        	 RegisteredInfo info = publicationService.getRegisteredInfo(getRegistrationInfo);
        	 for (BusinessInfo businessInfo : info.getBusinessInfos().getBusinessInfo()) {
				List<Name> names = businessInfo.getName();
				for (Name name : names) {
					if ("en".equals(name.getLang())) {
						String businessName = name.getValue();
						businesses.add(businessName);
					}
				}
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
