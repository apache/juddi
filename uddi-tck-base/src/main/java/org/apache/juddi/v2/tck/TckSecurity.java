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
package org.apache.juddi.v2.tck;

import java.rmi.RemoteException;
import javax.xml.ws.BindingProvider;

import org.uddi.v2_service.DispositionReport;
import org.uddi.v2_service.Publish;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class TckSecurity {

	public static String getAuthToken(Publish securityService, String pubId, String cred) throws DispositionReport, RemoteException {
	
                if (!TckPublisher.isUDDIAuthMode())
                        return null;
		org.uddi.api_v2.GetAuthToken ga = new org.uddi.api_v2.GetAuthToken();
          ga.setGeneric("2.0");
		ga.setUserID(pubId);
		ga.setCred(cred);

		org.uddi.api_v2.AuthToken token = securityService.getAuthToken(ga);
		
		return token.getAuthInfo();
	}

        /**
         * used for non UDDI AuthToken authentication. I.e. HTTP NTLM, HTTP BASIC, HTTP DIGEST
         * @param bindingProvider
         * @param publisherId
         * @param password 
         */
        public static void setCredentials(BindingProvider bindingProvider, String publisherId, String password) {
                bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, publisherId);
                bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        }

}
