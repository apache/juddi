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

import org.apache.juddi.api.impl.JUDDIApiImpl;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.api_v3.SyncSubscription;
import org.apache.juddi.api_v3.SyncSubscriptionDetail;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.v3_service.DispositionReportFaultMessage;
/**
 * UDDIPublicationPortType wrapper so it can be exposed as a service over RMI.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class JUDDIApiService extends UnicastRemoteObject implements JUDDIApiPortType {

	private static final long serialVersionUID = 1404805184314952141L;
	private JUDDIApiPortType juddiAPI = new JUDDIApiImpl();
	
	protected JUDDIApiService(int port) throws RemoteException {
		super(port);
	}

	public void deletePublisher(DeletePublisher body)
			throws DispositionReportFaultMessage, RemoteException {
		juddiAPI.deletePublisher(body);
	}

	public PublisherDetail getAllPublisherDetail(GetAllPublisherDetail body)
			throws DispositionReportFaultMessage, RemoteException {
		return juddiAPI.getAllPublisherDetail(body);
	}

	public PublisherDetail getPublisherDetail(GetPublisherDetail body)
			throws DispositionReportFaultMessage, RemoteException {
		return juddiAPI.getPublisherDetail(body);
	}

	public PublisherDetail savePublisher(SavePublisher body)
			throws DispositionReportFaultMessage, RemoteException {
		return juddiAPI.savePublisher(body);
	}

	public void adminDeleteTModel(DeleteTModel body)
			throws DispositionReportFaultMessage, RemoteException {
		juddiAPI.adminDeleteTModel(body);
	}

	public void deleteClientSubscriptionInfo(DeleteClientSubscriptionInfo body)
			throws DispositionReportFaultMessage, RemoteException {
		juddiAPI.deleteClientSubscriptionInfo(body);
	}

	public ClientSubscriptionInfoDetail saveClientSubscriptionInfo(SaveClientSubscriptionInfo body)
			throws DispositionReportFaultMessage, RemoteException {
		return juddiAPI.saveClientSubscriptionInfo(body);
		
	}
	
	public ClerkDetail saveClerk(SaveClerk body)
			throws DispositionReportFaultMessage, RemoteException {
		return juddiAPI.saveClerk(body);

	}
	
	public NodeDetail saveNode(SaveNode body)
			throws DispositionReportFaultMessage, RemoteException {
		return juddiAPI.saveNode(body);
	
	}

	public SyncSubscriptionDetail invokeSyncSubscription(SyncSubscription body)
			throws DispositionReportFaultMessage, RemoteException {
		return juddiAPI.invokeSyncSubscription(body);
	}
	
	


}
