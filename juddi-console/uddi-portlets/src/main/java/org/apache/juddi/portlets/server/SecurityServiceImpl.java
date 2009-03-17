package org.apache.juddi.portlets.server;

import org.apache.juddi.portlets.client.SecurityService;
import org.apache.juddi.portlets.client.SecurityResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.v3_service.UDDISecurityPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SecurityServiceImpl extends RemoteServiceServlet implements SecurityService {

	private Logger logger = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	public SecurityResponse get(String username, String password) {
		logger.debug("User " + username + " sending token request..");
		SecurityResponse response = new SecurityResponse();
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
        	 Transport transport = (Transport) transportClass.newInstance(); 
        	 UDDISecurityPortType securityService = transport.getSecurityService();
        	 GetAuthToken getAuthToken = new GetAuthToken();
        	 getAuthToken.setUserID(username);
        	 getAuthToken.setCred(password);
        	 AuthToken authToken = securityService.getAuthToken(getAuthToken);
        	 logger.debug("User " + username + " obtained token=" + authToken.getAuthInfo());
        	 response.setSuccess(true);
        	 response.setResponse(authToken.getAuthInfo());
	     } catch (Exception e) {
	    	 logger.error("Could not obtain token. " + e.getMessage(), e);
	    	 response.setSuccess(false);
	    	 response.setMessage(e.getMessage());
	    	 response.setErrorCode("101");
	     }  catch (Throwable t) {
	    	 logger.error("Could not obtain token. " + t.getMessage(), t);
	    	 response.setSuccess(false);
	    	 response.setMessage(t.getMessage());
	    	 response.setErrorCode("101");
	     } 
		 return response;
	}
}
