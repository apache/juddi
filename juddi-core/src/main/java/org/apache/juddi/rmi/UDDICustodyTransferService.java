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

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.apache.juddi.api.impl.UDDICustodyTransferImpl;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDICustodyTransferPortType;

/**
 * UDDISecurityPortType wrapper so it can be exposed as a service over RMI.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class UDDICustodyTransferService extends UnicastRemoteObject implements UDDICustodyTransferPortType {

	private static final long serialVersionUID = 8900970766388210839L;
	private UDDICustodyTransferPortType custodyTransfer = new UDDICustodyTransferImpl();
	
	protected UDDICustodyTransferService(int port) throws RemoteException {
		super(port);
	}

	public void discardTransferToken(DiscardTransferToken body)
			throws DispositionReportFaultMessage, RemoteException {
		custodyTransfer.discardTransferToken(body);
	}

	public void getTransferToken(String authInfo, KeyBag keyBag,
			Holder<String> nodeID, Holder<XMLGregorianCalendar> expirationTime,
			Holder<byte[]> opaqueToken) throws DispositionReportFaultMessage,
			RemoteException {
		custodyTransfer.getTransferToken(authInfo, keyBag, nodeID, expirationTime, opaqueToken);
	}

	public void transferEntities(TransferEntities body)
			throws DispositionReportFaultMessage, RemoteException {
		custodyTransfer.transferEntities(body);
	}
	
	

}
