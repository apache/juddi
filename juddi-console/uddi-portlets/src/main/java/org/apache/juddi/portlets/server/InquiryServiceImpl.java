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

public class InquiryServiceImpl extends RemoteServiceServlet implements InquiryService {

	private Logger logger = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	public InquiryResponse getTModelDetail(String authToken, String tModelKey) 
	{
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
