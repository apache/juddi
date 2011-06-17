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
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.UDDIPublicationPortType;
/**
 * UDDIPublicationPortType wrapper so it can be exposed as a service over RMI.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class UDDIPublicationService extends UnicastRemoteObject implements UDDIPublicationPortType {

	private static final long serialVersionUID = 8342463046574333026L;
	private UDDIPublicationPortType publication = new UDDIPublicationImpl();
	
	protected UDDIPublicationService(int port) throws RemoteException {
		super(port);
	}

	public void addPublisherAssertions(AddPublisherAssertions body)
			throws RemoteException {
		publication.addPublisherAssertions(body);
	}

	public void deleteBinding(DeleteBinding body)
			throws RemoteException {
		publication.deleteBinding(body);
	}

	public void deleteBusiness(DeleteBusiness body)
			throws RemoteException {
		publication.deleteBusiness(body);
	}

	public void deletePublisherAssertions(DeletePublisherAssertions body)
			throws RemoteException {
		publication.deletePublisherAssertions(body);
	}

	public void deleteService(DeleteService body)
			throws RemoteException {
		publication.deleteService(body);
	}

	public void deleteTModel(DeleteTModel body)
			throws RemoteException {
		publication.deleteTModel(body);
	}

	public List<AssertionStatusItem> getAssertionStatusReport(String authInfo,
			CompletionStatus completionStatus)
			throws RemoteException {
		return publication.getAssertionStatusReport(authInfo, completionStatus);
	}

	public List<PublisherAssertion> getPublisherAssertions(String authInfo)
			throws RemoteException {
		return publication.getPublisherAssertions(authInfo);
	}

	public RegisteredInfo getRegisteredInfo(GetRegisteredInfo body)
			throws RemoteException {
		return publication.getRegisteredInfo(body);
	}

	public BindingDetail saveBinding(SaveBinding body)
			throws RemoteException {
		return publication.saveBinding(body);
	}

	public BusinessDetail saveBusiness(SaveBusiness body)
			throws RemoteException {
		return publication.saveBusiness(body);
	}

	public ServiceDetail saveService(SaveService body)
			throws RemoteException {
		return publication.saveService(body);
	}

	public TModelDetail saveTModel(SaveTModel body)
			throws RemoteException {
		return publication.saveTModel(body);
	}

	public void setPublisherAssertions(String authInfo,
			Holder<List<PublisherAssertion>> publisherAssertion)
			throws RemoteException {
		publication.setPublisherAssertions(authInfo, publisherAssertion);
	}

}
