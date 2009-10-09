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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.portlets.client.service.SecurityResponse;
import org.apache.juddi.portlets.client.service.SecurityService;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.AuthToken;
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
	private Logger log = Logger.getLogger(this.getClass());
	

	public SecurityResponse get(String username, String password) {
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		log.debug("User " + username + " sending token request..");
		SecurityResponse response = new SecurityResponse();
		String token = (String) session.getAttribute("AuthToken");
		if (username==null) {
			username = (String) session.getAttribute("UserName");
		}
		Principal user = request.getUserPrincipal();
		log.debug("UserPrincipal " + user);
		if (username==null && user!=null) {
			username = user.getName();
			password = "";
		} 
		if (token==null) {
			if (username==null) {
				response.setSuccess(true);
				return response;
			} else {
				try {
					AuthToken authToken = login(username, password,"default");
					response.setSuccess(true);
					response.setResponse(authToken.getAuthInfo());
					
					session.setAttribute("AuthToken", authToken.getAuthInfo());
					session.setAttribute("UserName", username);
				
					//upon success obtain tokens from other registries
					Map<String, UDDIClerk> clerks = UDDIClerkManager.getClientConfig().getClerks();
					for (UDDIClerk clerk : clerks.values()) {
						if (username.equals(clerk.getPublisher())) {
							try {
								AuthToken clerkToken = login(clerk.getPublisher(), clerk.getPassword(), clerk.getNode().getName());
								//set the clerkToken into the session
								session.setAttribute("token-" + clerk.getName(), clerkToken.getAuthInfo());
							} catch (Exception e) {
								log.warn("Could not obtain authToken for clerk=" + clerk.getName());
							} 
						}
					}
					
				} catch (Exception e) {
					log.error("Could not obtain token. " + e.getMessage(), e);
					response.setSuccess(false);
					response.setMessage(e.getMessage());
					response.setErrorCode("101");
				} catch (Throwable t) {
					log.error("Could not obtain token. " + t.getMessage(), t);
					response.setSuccess(false);
					response.setMessage(t.getMessage());
					response.setErrorCode("101");
				}
			} 
		} else {
			response.setSuccess(true);
			response.setResponse(token);
		}
		response.setUsername(username);
		return response;
	}
	
	private AuthToken login(String username, String password, String node) throws ConfigurationException, ClassNotFoundException,
		InstantiationException, IllegalAccessException, TransportException, DispositionReportFaultMessage, RemoteException, 
		IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		
		String clazz = UDDIClerkManager.getClientConfig().getNodes().get(node).getProxyTransport();
        Class<?> transportClass = Loader.loadClass(clazz);
        Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance(node);  
		UDDISecurityPortType securityService = transport.getUDDISecurityService();
		GetAuthToken getAuthToken = new GetAuthToken();
		getAuthToken.setUserID(username);
		getAuthToken.setCred(password);
		AuthToken authToken = securityService.getAuthToken(getAuthToken);
		log.info("User " + username + " obtained token from node=" + node);
		return authToken;
	}
}
