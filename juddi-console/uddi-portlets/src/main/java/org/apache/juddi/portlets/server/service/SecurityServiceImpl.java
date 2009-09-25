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

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.juddi.portlets.client.service.SecurityResponse;
import org.apache.juddi.portlets.client.service.SecurityService;
import org.apache.juddi.v3.client.config.ClientConfig;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDISecurityPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * 
 */
public class SecurityServiceImpl extends RemoteServiceServlet implements
		SecurityService {

	private Logger logger = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

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
		} 
		if (token==null) {
			if (username==null) {
				response.setSuccess(true);
				return response;
			} else {
				try {
					String clazz = ClientConfig.getInstance().getNodes().get("default").getProxyTransport();
					Class<?> transportClass = Loader.loadClass(clazz);
					Transport transport = (Transport) transportClass.newInstance();
					UDDISecurityPortType securityService = transport.getUDDISecurityService();
					GetAuthToken getAuthToken = new GetAuthToken();
					getAuthToken.setUserID(username);
					getAuthToken.setCred(password);
					AuthToken authToken = securityService
							.getAuthToken(getAuthToken);
					logger.debug("User " + username + " obtained token="
							+ authToken.getAuthInfo());
					response.setSuccess(true);
					response.setResponse(authToken.getAuthInfo());
					session.setAttribute("AuthToken", authToken.getAuthInfo());
					session.setAttribute("UserName", username);
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
			response.setSuccess(true);
			response.setResponse(token);
		}
		response.setUsername(username);
		return response;
	}
}
