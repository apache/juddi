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

import java.util.HashMap;
import java.util.Map;

import org.apache.juddi.portlets.client.InquiryResponse;
import org.apache.juddi.portlets.client.InquiryService;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.v3_service.UDDIInquiryPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class InquiryServiceImpl extends RemoteServiceServlet implements InquiryService {

	private Logger logger = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	public InquiryResponse getTModelDetail(String authToken, String tModelKey) 
	{
		//HttpServletRequest request = this.getThreadLocalRequest();
		//HttpSession session = request.getSession();
		GetTModelDetail getTModelDetail = new GetTModelDetail();
		getTModelDetail.setAuthInfo(authToken);
		getTModelDetail.getTModelKey().add(tModelKey);
		InquiryResponse response = new InquiryResponse();
		logger.debug("TModelDetail " + getTModelDetail + " sending tmodelDetail request..");
		Map<String,String> tmodelDetailMap = new HashMap<String,String>();
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
        	 Transport transport = (Transport) transportClass.newInstance(); 
        	 UDDIInquiryPortType inquiryService = transport.getInquiryService();
        	 TModelDetail tmodelDetail = inquiryService.getTModelDetail(getTModelDetail);
        	 for (TModel tmodel : tmodelDetail.getTModel()) {
        		 //TODO figure out how to deal with i18n
        		 tmodelDetailMap.put("name",tmodel.getName().getValue());
			 }
        	 response.setSuccess(true);
        	 response.setResponse(tmodelDetailMap);
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
