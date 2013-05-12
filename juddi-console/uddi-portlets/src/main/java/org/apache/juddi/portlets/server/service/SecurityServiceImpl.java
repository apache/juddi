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
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.portlets.client.service.SecurityResponse;
import org.apache.juddi.portlets.client.service.SecurityService;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.WebHelper;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * 
 */
public class SecurityServiceImpl extends RemoteServiceServlet implements
		SecurityService {

	private static final long serialVersionUID = -154327520485409858L;
	private Log logger = LogFactory.getLog(this.getClass());
	

	public SecurityResponse get(String username, String password) {
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		
		logger.debug("User " + username + " sending token request..");
		SecurityResponse response = new SecurityResponse();
		String token = (String) session.getAttribute("AuthToken");
		if (username==null) {
			username = (String) session.getAttribute("UserName");
		}
		Principal user = request.getUserPrincipal();
		logger.debug("UserPrincipal " + user);
		if (username==null && user!=null) {
			username = user.getName();
			password = "";
			try {
				//if we can find this class we get obtain the password from the Tomcat User.
				ClassUtil.forName("org.apache.catalina.User", this.getClass());
				password = new CatalinaUser().getPassword(user);
			} catch ( ClassNotFoundException cnfe) {
				logger.warn("The class org.apache.cataline.User was not found. You may" +
						" need a SSO solution take care of authentication, or fall back" +
						" to JUDDIAuthentication.");
			}
		} 
		if (token==null) {
			if (username==null) {
				log("Could not obtain username, this session is invalid.");
				response.setSuccess(false);
				return response;
			} else {
				try {
					AuthToken authToken = login(username, password, session.getServletContext());
					response.setSuccess(true);
					response.setResponse(authToken.getAuthInfo());
					session.setAttribute("AuthToken", authToken.getAuthInfo());
					session.setAttribute("UserName", username);
					
				    setClerkAuthenticationTokensInSession(username);
				} catch (Exception e) {
					logger.error("Could not obtain token. " + e.getMessage(), e);
					response.setSuccess(false);
					response.setMessage(e.getMessage());
					response.setErrorCode("101");
				} catch (Throwable t) {
					logger.error("Could not obtain token. " + t.getMessage(), t);
					response.setSuccess(false);
					response.setMessage(t.getMessage());
					response.setErrorCode("101");
				}
			} 
		} else {
			try {
				setClerkAuthenticationTokensInSession(username);
				response.setSuccess(true);
				response.setResponse(token);
			} catch (Exception e) {
				logger.error("Could not obtain token. " + e.getMessage(), e);
				response.setSuccess(false);
				response.setMessage(e.getMessage());
				response.setErrorCode("101");
			} catch (Throwable t) {
				logger.error("Could not obtain token. " + t.getMessage(), t);
				response.setSuccess(false);
				response.setMessage(t.getMessage());
				response.setErrorCode("101");
			}
		}
		response.setUsername(username);
		return response;
	}
	
	private AuthToken login(String username, String password, ServletContext servletContext) throws ConfigurationException, ClassNotFoundException,
		InstantiationException, IllegalAccessException, TransportException, DispositionReportFaultMessage, RemoteException, 
		IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		
        Transport transport = WebHelper.getTransport(servletContext);
		UDDISecurityPortType securityService = transport.getUDDISecurityService();
		GetAuthToken getAuthToken = new GetAuthToken();
		getAuthToken.setUserID(username);
		getAuthToken.setCred(password);
		AuthToken authToken = securityService.getAuthToken(getAuthToken);
		logger.info("User " + username + " obtained token from node=" + WebHelper.getUDDIHomeNode(servletContext).getName());
		return authToken;
	}
	
	public void setClerkAuthenticationTokensInSession(String username) throws ConfigurationException {
		
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		//upon success obtain tokens from other registries
		UDDIClient client = WebHelper.getUDDIClient(session.getServletContext());
		Map<String, UDDIClerk> clerks = client.getClientConfig().getUDDIClerks();
		for (UDDIClerk clerk : clerks.values()) {
			//only setting token for the clerks of the current user/publisher
			if (username.equals(clerk.getPublisher())) {
				try {
					if (session.getAttribute("token-" + clerk.getName())==null) {
						AuthToken clerkToken = login(clerk.getPublisher(), clerk.getPassword(), session.getServletContext());
						//set the clerkToken into the session
						session.setAttribute("token-" + clerk.getName(), clerkToken.getAuthInfo());
					}
				} catch (Exception e) {
					logger.warn("Could not obtain authToken for clerk=" + clerk.getName());
				} 
			}
		}
	}
	
	public SecurityResponse logout() {
		SecurityResponse response = new SecurityResponse();
		try {
			HttpServletRequest request = getThreadLocalRequest();
			HttpSession session = request.getSession();
			String token = (String) session.getAttribute("AuthToken");
			Transport transport = WebHelper.getTransport(session.getServletContext());
			UDDISecurityPortType securityService = transport.getUDDISecurityService();
			DiscardAuthToken discardAuthToken = new DiscardAuthToken();
			discardAuthToken.setAuthInfo(token);
			securityService.discardAuthToken(discardAuthToken);
			logger.info("Invalided token " + token);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return response;
	}
}
