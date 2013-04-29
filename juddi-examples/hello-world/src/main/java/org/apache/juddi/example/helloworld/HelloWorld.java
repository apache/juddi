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
package org.apache.juddi.example.helloworld;

import org.uddi.api_v3.*;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDISecurityPortType;

public class HelloWorld {
	private static UDDISecurityPortType security = null;

	public HelloWorld() {
        try {
        	// create a manager and read the config in the archive; 
        	// you can use your config file name
        	UDDIClerkManager clerkManager = new UDDIClerkManager("META-INF/hello-world-uddi.xml");
        	// a ClerkManager can be a client to multiple UDDI nodes, so 
        	// supply the nodeName (defined in your uddi.xml.
        	// The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        	Transport transport = clerkManager.getTransport("default");
        	// Now you create a reference to the UDDI API
			security = transport.getUDDISecurityService();
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
