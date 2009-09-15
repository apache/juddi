/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.tck;

import java.rmi.RemoteException;

import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckSecurity {

	public static String getAuthToken(UDDISecurityPortType securityService, String pubId, String cred) throws DispositionReportFaultMessage, RemoteException {
	
		org.uddi.api_v3.GetAuthToken ga = new org.uddi.api_v3.GetAuthToken();
		ga.setUserID(pubId);
		ga.setCred(cred);

		org.uddi.api_v3.AuthToken token = securityService.getAuthToken(ga);
		
		return token.getAuthInfo();
	}

}
