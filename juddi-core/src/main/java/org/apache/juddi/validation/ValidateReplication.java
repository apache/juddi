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
package org.apache.juddi.validation;

import java.math.BigInteger;
import javax.xml.ws.WebServiceContext;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * Processing an inbound replication message may fail due to a server internal
 * error. The common behavior for all error cases is to return an E_fatalError
 * error code. Error reporting SHALL be that specified by Section 4.8 – Success
 * and Error Reporting of this specification.
 */
public class ValidateReplication extends ValidateUDDIApi {

        public ValidateReplication(UddiEntityPublisher publisher) {
                super(publisher);
        }

        public void validateNotifyChangeRecordsAvailable(NotifyChangeRecordsAvailable body, WebServiceContext ctx) throws DispositionReportFaultMessage {
                //TODO
        }

        public void validateGetChangeRecords(String requestingNode, HighWaterMarkVectorType changesAlreadySeen, BigInteger responseLimitCount, HighWaterMarkVectorType responseLimitVector, ReplicationConfiguration FetchEdges, WebServiceContext ctx) throws DispositionReportFaultMessage {
                //TODO

                if (requestingNode == null || requestingNode.trim().equalsIgnoreCase("")) {
                        //requestingNode: The requestingNode element provides the identity of the calling node.  
                        //This is the unique key for the calling node and SHOULD be specified within the Replication Configuration Structure.
                        throw new FatalErrorException(new ErrorMessage("errors.replication.nodeNotSpecified"));
                }
                if (!ContainsNode(requestingNode, FetchEdges)) {
                        throw new FatalErrorException(new ErrorMessage("errors.replication.unknownNode"));
                }

                if (changesAlreadySeen != null) {
                        // changesAlreadySeen: The changesAlreadySeen element, if present, indicates changes from each
                        //node that the requestor has successfully processed, and thus which should not be resent, if possible.

                        //no validation needed?
                }

                if (responseLimitCount != null && responseLimitVector != null) {
                        throw new FatalErrorException(new ErrorMessage("errors.replication.bothLimitsSpecified"));
                }
                if (responseLimitCount != null) {
                        //can't be 0 since 0 is banned as being a change record id
                        if (responseLimitCount.longValue() <= 0) {
                                throw new FatalErrorException(new ErrorMessage("errors.replication.negativeLimit", responseLimitCount.toString()));
                        }
                }
                if (responseLimitVector != null) {
                        for (int i = 0; i < responseLimitVector.getHighWaterMark().size(); i++) {
                                if (responseLimitVector.getHighWaterMark().get(i).getOriginatingUSN() == null
                                     || responseLimitVector.getHighWaterMark().get(i).getOriginatingUSN() <= 0) {
                                        throw new FatalErrorException(new ErrorMessage("errors.replication.limitVectorNull"));
                                }
                                if (responseLimitVector.getHighWaterMark().get(i).getNodeID()== null
                                     || responseLimitVector.getHighWaterMark().get(i).getNodeID().trim().equalsIgnoreCase("")) {
                                        throw new FatalErrorException(new ErrorMessage("errors.replication.limitVectorNoNode"));
                                }
                        }
                }

                /**
                 * responseLimitCount or responseLimitVector: A caller MAY place
                 * an upper bound on the number of change records he wishes to
                 * receive in response to this message by either providing a
                 * integer responseLimitCount, or, using responseLimitVector,
                 * indicating for each node in the graph the first change
                 * originating there that he does not wish to be returned.
                 *
                 */
        }

        private static boolean ContainsNode(String requestingNode, ReplicationConfiguration FetchEdges) {
                if (FetchEdges == null) {
                        return false;
                }
                if (FetchEdges.getCommunicationGraph() == null) {
                        return false;
                }
                for (int i = 0; i < FetchEdges.getCommunicationGraph().getNode().size(); i++) {
                        if (FetchEdges.getCommunicationGraph().getNode().get(i).equalsIgnoreCase(requestingNode)) {
                                return true;
                        }
                }
                return false;
        }

        public void validateSetReplicationNodes(ReplicationConfiguration replicationConfiguration) throws DispositionReportFaultMessage {
                
        }

}
