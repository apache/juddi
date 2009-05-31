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

package org.apache.juddi.api.impl;

import java.math.BigInteger;
import java.util.List;

import javax.jws.WebService;

import org.apache.juddi.validation.ValidateReplication;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.TransferCustody;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIReplicationPortType;

@WebService(serviceName="UDDIReplicationService", 
			endpointInterface="org.uddi.v3_service.UDDIReplicationPortType",
			targetNamespace = "urn:uddi-org:repl_v3_portType")
public class UDDIReplicationImpl extends AuthenticatedService implements UDDIReplicationPortType {


	public String doPing(DoPing body) throws DispositionReportFaultMessage {
		ValidateReplication.unsupportedAPICall();
		return null;
	}


	public List<ChangeRecord> getChangeRecords(String requestingNode,
			HighWaterMarkVectorType changesAlreadySeen,
			BigInteger responseLimitCount,
			HighWaterMarkVectorType responseLimitVector)
			throws DispositionReportFaultMessage {
		ValidateReplication.unsupportedAPICall();
		return null;
	}


	public List<ChangeRecordIDType> getHighWaterMarks()
			throws DispositionReportFaultMessage {
		ValidateReplication.unsupportedAPICall();
		return null;
	}


	public void notifyChangeRecordsAvailable(NotifyChangeRecordsAvailable body)
			throws DispositionReportFaultMessage {
		ValidateReplication.unsupportedAPICall();
	}


	public void transferCustody(TransferCustody body)
			throws DispositionReportFaultMessage {
		ValidateReplication.unsupportedAPICall();
	}
}
