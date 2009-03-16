package org.apache.juddi.portlets.server;

import org.apache.juddi.portlets.client.GetTokenService;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.v3_service.UDDISecurityPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GetTokenServiceImpl extends RemoteServiceServlet implements GetTokenService {

	private static final long serialVersionUID = 1L;
	
	public String get(String username, String password) {
		System.out.println("user=" + username);
		String token = null;
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
        	 Transport transport = (Transport) transportClass.newInstance(); 
        	 UDDISecurityPortType securityService = transport.getSecurityService();
        	 GetAuthToken getAuthToken = new GetAuthToken();
        	 getAuthToken.setUserID(username);
        	 getAuthToken.setCred("");
        	 AuthToken authToken = securityService.getAuthToken(getAuthToken);
        	 System.out.println(authToken.getAuthInfo());
        	 token = authToken.getAuthInfo();
	     } catch (Exception e) {
	         e.printStackTrace();
	     }  catch (Throwable e) {
	         e.printStackTrace();
	     } 
		return token;
	}
}
