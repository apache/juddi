/*
 * Copyright 2014 The Apache Software Foundation.
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
 */
package org.apache.juddi.api.runtime;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.List;
import javax.jws.WebService;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.ChangeRecords;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.GetChangeRecords;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.TransferCustody;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIReplicationPortType;

/**
 *
 * @author alex
 */
@WebService(serviceName = "UDDI_Replication_PortType", targetNamespace = "urn:uddi-org:repl_v3_portType",
        endpointInterface = "org.uddi.v3_service.UDDIReplicationPortType")
              public class ReplicantImpl implements UDDIReplicationPortType {
      
        public ReplicantImpl(){
        }

    

        @Override
        public void notifyChangeRecordsAvailable(NotifyChangeRecordsAvailable body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
        }

        @Override
        public String doPing(DoPing body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public List<ChangeRecordIDType> getHighWaterMarks() throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public void transferCustody(TransferCustody body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
        }

        @Override
        public ChangeRecords getChangeRecords(GetChangeRecords body) throws DispositionReportFaultMessage, RemoteException {
                    CLIServerTest.sink = true;
                return null;
        }
        
}
