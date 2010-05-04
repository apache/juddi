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
package org.apache.juddi.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * UDDISecurityPortType wrapper so it can be exposed as a service over RMI.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class UDDISecurityService extends UnicastRemoteObject implements UDDISecurityPortType {

	private static final long serialVersionUID = -7931578658303681458L;
	private UDDISecurityPortType security = new UDDISecurityImpl();
	
	protected UDDISecurityService(int port) throws RemoteException {
		super(port);
	}
	
	public void discardAuthToken(DiscardAuthToken body)
			throws RemoteException {
			security.discardAuthToken(body);
	}

	public AuthToken getAuthToken(GetAuthToken body)
			throws RemoteException {
			return security.getAuthToken(body);
	}

}
