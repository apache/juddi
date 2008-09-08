/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

package org.apache.juddi.ws.impl;

import javax.jws.WebService;

import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

@WebService(serviceName="UDDISecurityService", 
			endpointInterface="org.uddi.v3_service.UDDISecurityPortType")
public class UDDISecurityImpl implements UDDISecurityPortType {


	public void discardAuthToken(DiscardAuthToken body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}


	public AuthToken getAuthToken(GetAuthToken body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

}
