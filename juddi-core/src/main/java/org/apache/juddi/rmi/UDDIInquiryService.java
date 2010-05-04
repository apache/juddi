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

import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.UDDIInquiryPortType;
/**
 * UDDIInquiryPortType wrapper so it can be exposed as a service over RMI.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class UDDIInquiryService extends UnicastRemoteObject implements UDDIInquiryPortType {

	private static final long serialVersionUID = -8384112899703963130L;
	private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

	protected UDDIInquiryService(int port) throws RemoteException {
		super(port);
	}

	public BindingDetail findBinding(FindBinding body)
			throws RemoteException {
		return inquiry.findBinding(body);
	}

	public BusinessList findBusiness(FindBusiness body)
			throws RemoteException {
		return inquiry.findBusiness(body);
	}

	public RelatedBusinessesList findRelatedBusinesses(
			FindRelatedBusinesses body) throws RemoteException {
		return inquiry.findRelatedBusinesses(body);
	}

	public ServiceList findService(FindService body)
			throws RemoteException {
		return inquiry.findService(body);
	}

	public TModelList findTModel(FindTModel body)
			throws RemoteException {
		return inquiry.findTModel(body);
	}

	public BindingDetail getBindingDetail(GetBindingDetail body)
			throws RemoteException {
		return inquiry.getBindingDetail(body);
	}

	public BusinessDetail getBusinessDetail(GetBusinessDetail body)
			throws RemoteException {
		return inquiry.getBusinessDetail(body);
	}

	public OperationalInfos getOperationalInfo(GetOperationalInfo body)
			throws RemoteException {
		return inquiry.getOperationalInfo(body);
	}

	public ServiceDetail getServiceDetail(GetServiceDetail body)
			throws RemoteException {
		return inquiry.getServiceDetail(body);
	}

	public TModelDetail getTModelDetail(GetTModelDetail body)
			throws RemoteException {
		return inquiry.getTModelDetail(body);
	}

}
