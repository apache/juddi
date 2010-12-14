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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.portlets.client.service.NotifyResponse;
import org.apache.juddi.portlets.client.service.NotifyService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 *
 */
public class NotifyServiceImpl extends RemoteServiceServlet implements NotifyService {
	
	private static final long serialVersionUID = 6312615857204115941L;
	private Log logger = LogFactory.getLog(this.getClass());
	
	
	public NotifyServiceImpl() {
		super();
	}
	
	public NotifyResponse getSubscriptionNotifications(String authToken) 
	{
		NotifyResponse response = new NotifyResponse();
		try {
			URL url = new URL(getThreadLocalRequest().getScheme(),
							getThreadLocalRequest().getRemoteHost(), 
							getThreadLocalRequest().getLocalPort(), 
							"/juddiv3/notify/");	
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			while (br.ready()) {
				sb.append(br.readLine());
			}
			response.setSubscriptionNotifications(sb.toString());
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
