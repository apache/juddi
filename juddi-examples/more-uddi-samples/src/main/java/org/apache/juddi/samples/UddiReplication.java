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
package org.apache.juddi.samples;

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXB;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.ChangeRecords;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.GetChangeRecords;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.v3_service.UDDIReplicationPortType;

/**
 *
 * @author Alex O'Ree
 */
class UddiReplication {

        public UddiReplication(UDDIClient client, String nodename) throws ConfigurationException {

                uddiReplicationPort = new UDDIService().getUDDIReplicationPort();
                ((BindingProvider) uddiReplicationPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, client.getClientConfig().getUDDINode(nodename).getReplicationUrl());
        }

        private UDDIReplicationPortType uddiReplicationPort = null;

        public String doPing() {
                try {
                        String doPing = uddiReplicationPort.doPing(new DoPing());
                        System.out.println("Ping Success, remote node's id is " + doPing);
                        return doPing;
                } catch (Exception ex) {
                        Logger.getLogger(UddiReplication.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
        }

        public void getHighWatermarks() {
                try {
                        List<ChangeRecordIDType> highWaterMarks = uddiReplicationPort.getHighWaterMarks();
                        System.out.println("Success....");
                        System.out.println("Node, USN");
                        for (int i = 0; i < highWaterMarks.size(); i++) {
                                System.out.println(
                                        highWaterMarks.get(i).getNodeID() + ", "
                                        + highWaterMarks.get(i).getOriginatingUSN());
                        }
                } catch (Exception ex) {
                        Logger.getLogger(UddiReplication.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        public void getChangeRecords(Long record, String sourcenode) {
                try {
                        HighWaterMarkVectorType highWaterMarkVectorType = new HighWaterMarkVectorType();

                        highWaterMarkVectorType.getHighWaterMark().add(new ChangeRecordIDType(doPing(), record));
                        GetChangeRecords req = new GetChangeRecords();
                        req.setRequestingNode(sourcenode);
                        req.setChangesAlreadySeen(highWaterMarkVectorType);
                        req.setResponseLimitCount(BigInteger.valueOf(100));
                        ChangeRecords res = uddiReplicationPort.getChangeRecords(req);
                        List<ChangeRecord> changeRecords = res.getChangeRecord();
                        System.out.println("Success...." + changeRecords.size() + " records returned");
                        System.out.println("Node, USN, type");
                        for (int i = 0; i < changeRecords.size(); i++) {
                                System.out.println(
                                        changeRecords.get(i).getChangeID().getNodeID() + ", "
                                        + changeRecords.get(i).getChangeID().getOriginatingUSN() + ": "
                                        + getChangeType(changeRecords.get(i)));
                                JAXB.marshal(changeRecords.get(i), System.out);
                        }
                } catch (Exception ex) {
                        Logger.getLogger(UddiReplication.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        private String getChangeType(ChangeRecord get) {
                if (get.getChangeRecordAcknowledgement() != null) {
                        return "ACK";
                }
                if (get.getChangeRecordConditionFailed() != null) {
                        return "ConditionFailed";
                }
                if (get.getChangeRecordCorrection() != null) {
                        return "Correction";
                }
                if (get.getChangeRecordDelete() != null) {
                        return "Deletion";
                }
                if (get.getChangeRecordDeleteAssertion() != null) {
                        return "Delete Assertion";
                }
                if (get.getChangeRecordHide() != null) {
                        return "Hide tmodel";
                }
                if (get.getChangeRecordNewData() != null) {
                        return "New Data";
                }
                if (get.getChangeRecordNewDataConditional() != null) {
                        return "New data conditional";
                }
                if (get.getChangeRecordNull() != null) {
                        return "Null";
                }
                if (get.getChangeRecordPublisherAssertion() != null) {
                        return "New publisher assertion";
                }
                return null;
        }

}
