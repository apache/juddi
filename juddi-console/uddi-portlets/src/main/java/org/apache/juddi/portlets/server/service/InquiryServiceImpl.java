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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.model.ServiceBinding;
import org.apache.juddi.portlets.client.service.InquiryResponse;
import org.apache.juddi.portlets.client.service.InquiryService;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.portlets.client.service.SearchResponse;
import org.apache.juddi.v3.client.i18n.EntityForLang;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.UDDIInquiryPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class InquiryServiceImpl extends RemoteServiceServlet implements InquiryService {

	private static final long serialVersionUID = 8509627428299232161L;
	private Logger logger = Logger.getLogger(this.getClass());
	private Transport transport = null;
	
	public InquiryServiceImpl() {
		super();
		
	}
	
	private Transport getTransport() 
		throws ConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		if (transport==null) {
			 UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(Constants.MANAGER_NAME);
			 String clazz = manager.getClientConfig().getUDDINode(Constants.NODE_NAME).getProxyTransport();
	         Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
	         transport = (Transport) transportClass.getConstructor(String.class).newInstance(Constants.NODE_NAME);  
		}
		return transport;
	}

	public SearchResponse queryJUDDI(String query) {
		SearchResponse response = new SearchResponse();

		 try {
			 UDDIInquiryPortType inquiryService = getTransport().getUDDIInquiryService();
			 org.apache.juddi.v3.client.transport.wrapper.UDDIInquiryService uis = 
				 new org.apache.juddi.v3.client.transport.wrapper.UDDIInquiryService();
       		 String reply = uis.inquire(inquiryService, query);
       		 response.setMessage(reply);
       		 response.setSuccess(true);       	 
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
        	 UDDIInquiryPortType inquiryService = getTransport().getUDDIInquiryService();
        	 TModelDetail tmodelDetail = inquiryService.getTModelDetail(getTModelDetail);
        	 //demo code fix up what to return for real.
        	 for (TModel tmodel : tmodelDetail.getTModel()) {
        		 tmodelDetailMap.put("name",tmodel.getName().getValue());
			 }
        	 response.setSuccess(true);
        	 
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
	
	public InquiryResponse getBusinessDetail(String authToken, String businessKey) 
	{
		HttpServletRequest request = this.getThreadLocalRequest();
		String lang = request.getLocale().getLanguage();
		
		GetBusinessDetail getBusinessDetail = new GetBusinessDetail();
		getBusinessDetail.setAuthInfo(authToken);
		getBusinessDetail.getBusinessKey().add(businessKey);
		InquiryResponse response = new InquiryResponse();
		logger.debug("BusinessDetail " + getBusinessDetail + " sending businessDetail request..");
		try {
        	 UDDIInquiryPortType inquiryService = getTransport().getUDDIInquiryService();
        	 BusinessDetail businessDetail = inquiryService.getBusinessDetail(getBusinessDetail);
        	 for (BusinessEntity businessEntity : businessDetail.getBusinessEntity()) {
        		 Business business = new Business(
        				 businessEntity.getBusinessKey(),
        				 EntityForLang.getName(businessEntity.getName(),lang).getValue(),
        				 EntityForLang.getDescription(businessEntity.getDescription(),lang).getValue());
        		 if (businessEntity.getBusinessServices()!=null) {
	        		 for (BusinessService businessService : businessEntity.getBusinessServices().getBusinessService()) {
	        			 Service service = new Service(
	        					 businessService.getServiceKey(),
	        					 EntityForLang.getName(businessService.getName(),lang).getValue(),
	        					 EntityForLang.getDescription(businessService.getDescription(),lang).getValue());
	        			 business.getServices().add(service);
	        		 }
        		 }
        		 //for (Contact contact : businessEntity.getContacts().getContact()) {
        			 //contact.get
        		 //}
        		 response.setBusiness(business);
			 }
        	 
        	 response.setSuccess(true);
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
	
	public InquiryResponse getServiceDetail(String authToken, String serviceKey) 
	{
		
		HttpServletRequest request = this.getThreadLocalRequest();
		String lang = request.getLocale().getLanguage();
	
		GetServiceDetail getServiceDetail = new GetServiceDetail();
		getServiceDetail.setAuthInfo(authToken);
		getServiceDetail.getServiceKey().add(serviceKey);
		InquiryResponse response = new InquiryResponse();
		logger.debug("ServiceDetail " + getServiceDetail + " sending serviceDetail request..");
		try {
        	 UDDIInquiryPortType inquiryService = getTransport().getUDDIInquiryService();
        	 ServiceDetail serviceDetail = inquiryService.getServiceDetail(getServiceDetail);
        	 for (BusinessService businessService : serviceDetail.getBusinessService()) {
        		 Service service = new Service(
        				 businessService.getServiceKey(),
        				 EntityForLang.getName(businessService.getName(),lang).getValue(),
        				 EntityForLang.getDescription(businessService.getDescription(),lang).getValue());
        		 for (BindingTemplate bindingTemplate : businessService.getBindingTemplates().getBindingTemplate()) {
        			 ServiceBinding serviceBinding = new ServiceBinding(
        					 bindingTemplate.getBindingKey(),
        					 bindingTemplate.getAccessPoint().getValue(),
        					 EntityForLang.getDescription(bindingTemplate.getDescription(),lang).getValue(),
        					 bindingTemplate.getAccessPoint().getUseType());
        			 service.getServiceBindings().add(serviceBinding);
        		 }
        		 response.setService(service);
			 }
        	 response.setSuccess(true);
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
