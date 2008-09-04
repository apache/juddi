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

import java.math.BigInteger;
import java.util.List;

import javax.jws.WebService;

import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.TransferCustody;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIReplicationPortType;

@WebService(serviceName="UDDIReplicationService", 
			endpointInterface="org.uddi.v3_service.UDDIReplicationPortType")
public class UDDIReplicationImpl implements UDDIReplicationPortType {

	@Override
	public String doPing(DoPing body) throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChangeRecord> getChangeRecords(String requestingNode,
			HighWaterMarkVectorType changesAlreadySeen,
			BigInteger responseLimitCount,
			HighWaterMarkVectorType responseLimitVector)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChangeRecordIDType> getHighWaterMarks()
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyChangeRecordsAvailable(NotifyChangeRecordsAvailable body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferCustody(TransferCustody body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

}
