/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.examples.helloworld;

import org.uddi.api_v3.*;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDISecurityPortType;

public class HelloWorld {
	private Transport transport = null;
	private static UDDISecurityPortType security = null;

	public HelloWorld() {
        try {
            String clazz = UDDIClientContainer.getUDDIClerkManager("example-manager").
            	getClientConfig().getUDDINode("default").getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
			if (transportClass!=null) {
				Transport transport = (Transport) transportClass.
					getConstructor(String.class).newInstance("default");
				security = transport.getUDDISecurityService();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void getAuthToken() {
		GetAuthToken getAuthToken = new GetAuthToken();
		getAuthToken.setUserID("root");
		getAuthToken.setCred("");
		try {
			AuthToken authToken = security.getAuthToken(getAuthToken);
			System.out.println ("AUTHTOKEN = " 
				+ authToken.getAuthInfo());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		

	public static void main (String args[]) {
		HelloWorld hw = new HelloWorld();
		hw.getAuthToken();	
	}
}
