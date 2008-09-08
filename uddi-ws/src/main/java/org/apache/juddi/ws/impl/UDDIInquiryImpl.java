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
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;

@WebService(serviceName="UDDIInquiryService", 
			endpointInterface="org.uddi.v3_service.UDDIInquiryPortType")
public class UDDIInquiryImpl implements UDDIInquiryPortType {

	public BindingDetail findBinding(FindBinding body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public BusinessList findBusiness(FindBusiness body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public RelatedBusinessesList findRelatedBusinesses(
			FindRelatedBusinesses body) throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceList findService(FindService body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public TModelList findTModel(FindTModel body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public BindingDetail getBindingDetail(GetBindingDetail body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public BusinessDetail getBusinessDetail(GetBusinessDetail body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public OperationalInfos getOperationalInfo(GetOperationalInfo body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceDetail getServiceDetail(GetServiceDetail body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public TModelDetail getTModelDetail(GetTModelDetail body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

}
