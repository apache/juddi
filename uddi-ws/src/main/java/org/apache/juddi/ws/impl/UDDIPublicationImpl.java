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

import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Holder;

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
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIPublicationPortType;

@WebService(serviceName="UDDIPublicationService", 
			endpointInterface="org.uddi.v3_service.UDDIPublicationPortType")
public class UDDIPublicationImpl implements UDDIPublicationPortType {

	@Override
	public void addPublisherAssertions(AddPublisherAssertions body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteBinding(DeleteBinding body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteBusiness(DeleteBusiness body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	@Override
	public void deletePublisherAssertions(DeletePublisherAssertions body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteService(DeleteService body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTModel(DeleteTModel body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	@Override
	public List<AssertionStatusItem> getAssertionStatusReport(String authInfo,
			CompletionStatus completionStatus)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PublisherAssertion> getPublisherAssertions(String authInfo)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegisteredInfo getRegisteredInfo(GetRegisteredInfo body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BindingDetail saveBinding(SaveBinding body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BusinessDetail saveBusiness(SaveBusiness body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceDetail saveService(SaveService body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TModelDetail saveTModel(SaveTModel body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPublisherAssertions(String authInfo,
			Holder<List<PublisherAssertion>> publisherAssertion)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

}
