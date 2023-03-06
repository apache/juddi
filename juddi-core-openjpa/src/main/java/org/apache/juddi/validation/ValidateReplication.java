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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.xml.ws.WebServiceContext;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.juddi.api.impl.AuthenticatedService.UTF8;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Node;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.client.cryptor.CryptorFactory;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidValueException;
import org.apache.juddi.v3.error.TransferNotAllowedException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.repl_v3.CommunicationGraph.Edge;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.Operator;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.repl_v3.TransferCustody;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * Processing an inbound replication message may fail due to a server internal
 * error. The common behavior for all error cases is to return an E_fatalError
 * error code. Error reporting SHALL be that specified by Section 4.8 – Success
 * and Error Reporting of this specification.
 */
public class ValidateReplication extends ValidateUDDIApi {

        private final static Log log = LogFactory.getLog(ValidateReplication.class);
        public ValidateReplication(UddiEntityPublisher publisher) {
                super(publisher);
        }
        
       public ValidateReplication(UddiEntityPublisher publisher, String nodeid) {
		 super(publisher, nodeid);
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
                //if (!ContainsNode(requestingNode, FetchEdges)) {
                //        throw new FatalErrorException(new ErrorMessage("errors.replication.unknownNode"));
                //}

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
                                if (responseLimitVector.getHighWaterMark().get(i).getNodeID() == null
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

        public void validateSetReplicationNodes(ReplicationConfiguration replicationConfiguration, EntityManager em, String thisnode, Configuration config) throws DispositionReportFaultMessage, ConfigurationException {
                if (replicationConfiguration == null) {
                        throw new InvalidValueException(new ErrorMessage("errors.replication.configNull"));

                }
                if (replicationConfiguration.getCommunicationGraph() == null) {
                        throw new InvalidValueException(new ErrorMessage("errors.replication.configNull"));
                }
                if (replicationConfiguration.getRegistryContact() == null) {
                        throw new InvalidValueException(new ErrorMessage("errors.replication.contactNull"));
                }
                if (replicationConfiguration.getRegistryContact().getContact() == null) {
                        throw new InvalidValueException(new ErrorMessage("errors.replication.contactNull"));
                }
                if (replicationConfiguration.getRegistryContact().getContact().getPersonName().get(0) == null) {
                        throw new InvalidValueException(new ErrorMessage("errors.replication.contactNull"));
                }

                if (replicationConfiguration.getOperator() == null || replicationConfiguration.getOperator().isEmpty()) {
                        throw new InvalidValueException(new ErrorMessage("errors.replication.contactNull", "Operator is null or empty"));
                }
                for (int i = 0; i < replicationConfiguration.getOperator().size(); i++) {
                        if (replicationConfiguration.getOperator().get(i).getSoapReplicationURL() == null
                                || "".equals(replicationConfiguration.getOperator().get(i).getSoapReplicationURL())) {
                                throw new InvalidValueException(new ErrorMessage("errors.replication.contactNull", "Replication URL is null or empty"));
                        }
                        if (!replicationConfiguration.getOperator().get(i).getSoapReplicationURL().toLowerCase().startsWith("http")) {
                                throw new InvalidValueException(new ErrorMessage("errors.replication.contactNull", "Replication URL is invalid, only HTTP is supported"));
                        }
                        if (replicationConfiguration.getOperator().get(i).getOperatorNodeID() == null
                                || replicationConfiguration.getOperator().get(i).getOperatorNodeID().equalsIgnoreCase("")) {
                                throw new InvalidValueException(new ErrorMessage("errors.replication.contactNull", "Node ID is not defined"));
                        }
                }
                if (replicationConfiguration.getCommunicationGraph() != null) {
                        for (String s : replicationConfiguration.getCommunicationGraph().getNode()) {
                                if (!Contains(replicationConfiguration.getOperator(), s)) {
                                        throw new InvalidValueException(new ErrorMessage("errors.replication.configNodeNotFound"));
                                }
                        }
                        for (Edge s : replicationConfiguration.getCommunicationGraph().getEdge()) {
                                //TODO revisit this for correctness
                                //Node find = null;
                                //if (!thisnode.equalsIgnoreCase(s.getMessageReceiver())) {
                                if (!Contains(replicationConfiguration.getOperator(), s.getMessageReceiver())) {
                                        throw new InvalidValueException(new ErrorMessage("errors.replication.configNodeNotFound"));
                                        //}
                                }
                                //find = null;
                                //if (!thisnode.equalsIgnoreCase(s.getMessageSender())) {
                                if (!Contains(replicationConfiguration.getOperator(), s.getMessageSender())) {
                                        throw new InvalidValueException(new ErrorMessage("errors.replication.configNodeNotFound"));
                                        //}
                                }
                                if (s.getMessageReceiver().equalsIgnoreCase(s.getMessageSender())){
                                        throw new InvalidValueException(new ErrorMessage("errors.replication.configNodeLoop"));
                                }
                                for (String id : s.getMessageReceiverAlternate()) {
                                        if (!Contains(replicationConfiguration.getOperator(), id)) {
                                                throw new InvalidValueException(new ErrorMessage("errors.replication.configNodeNotFound"));
                                        }
                                }

                        }
                }
                boolean shouldcheck = config.getBoolean(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_ENABLE, false);
                initDigSig(config);
                if (shouldcheck && !replicationConfiguration.getSignature().isEmpty() && ds != null) {
                        AtomicReference<String> outmsg = new AtomicReference<String>();
                        boolean ok = ds.verifySignedUddiEntity(replicationConfiguration, outmsg);
                        if (!ok) {
                                throw new FatalErrorException(new ErrorMessage("errors.digitalsignature.validationfailure" + " " + outmsg.get()));
                        }

                }
        }
        
         private org.apache.juddi.v3.client.cryptor.DigSigUtil ds = null;

        private synchronized void initDigSig(Configuration config) {
                if (ds == null) {
                        
                        Properties p = new Properties();
                        /**
                         * <trustStorePath>truststore.jks</trustStorePath>
                         * <trustStoreType>JKS</trustStoreType>
                         * <trustStorePassword
                         * isPasswordEncrypted="false"
                         * cryptoProvider="org.apache.juddi.v3.client.crypto.AES128Cryptor">password</trustStorePassword>
                         *
                         * <checkTimestamps>true</checkTimestamps>
                         * <checkTrust>true</checkTrust>
                         * <checkRevocationCRL>true</checkRevocationCRL>
                         */
                        p.put(DigSigUtil.TRUSTSTORE_FILE, config.getString(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "trustStorePath", ""));
                        p.put(DigSigUtil.TRUSTSTORE_FILETYPE, config.getString(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "trustStoreType", ""));

                        String enc = config.getString(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "trustStorePassword", "");
                        if (config.getBoolean(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "trustStorePassword[@isPasswordEncrypted]", false)) {
                                log.debug("trust password is encrypted, decrypting...");
                                
                                String prov = config.getString(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "trustStorePassword[@cryptoProvider]", "");
                                try {
                                        p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, CryptorFactory.getCryptor(prov).decrypt(enc));
                                } catch (Exception ex) {
                                        log.warn("unable to decrypt trust store password " + ex.getMessage());
                                        log.debug("unable to decrypt trust store password " + ex.getMessage(), ex);
                                }

                        } else if (!"".equals(enc)){
                                log.warn("Hey, you should consider encrypting your trust store password!");
                                p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, enc);
                        }

                        p.put(DigSigUtil.CHECK_REVOCATION_STATUS_CRL, config.getString(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "checkRevocationCRL", "true"));
                        p.put(DigSigUtil.CHECK_TRUST_CHAIN, config.getString(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "checkTrust", "true"));
                        p.put(DigSigUtil.CHECK_TIMESTAMPS, config.getString(Property.JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX + "checkTimestamps", "true"));

                        try {
                                ds = new DigSigUtil(p);
                        } catch (CertificateException ex) {
                                log.error("", ex);
                        }
                        //System.out.println("loaded from " + AppConfig.getConfigFileURL());
                        //p.list(System.out);
                }
        }

      

        private boolean Contains(List<Operator> operator, String s) {
                if (operator == null) {
                        return false;
                }
                for (Operator o : operator) {
                        if (o.getOperatorNodeID().equalsIgnoreCase(s)) {
                                return true;
                        }
                }
                return false;
        }

        public void validateTransfer(EntityManager em, TransferCustody body) throws DispositionReportFaultMessage {

                if (body == null) {
                        throw new TransferNotAllowedException(new ErrorMessage("errors.NullInput"));
                }
                if (body.getTransferToken() == null) {
                        throw new TransferNotAllowedException(new ErrorMessage("errors.NullInput"));
                }
                if (body.getKeyBag() == null) {
                        throw new TransferNotAllowedException(new ErrorMessage("errors.NullInput"));
                }
                if (body.getTransferOperationalInfo() == null) {
                        throw new TransferNotAllowedException(new ErrorMessage("errors.NullInput"));
                }

                if (body.getTransferOperationalInfo().getNodeID() == null) {
                        throw new TransferNotAllowedException(new ErrorMessage("errors.NullInput"));
                }
                if (body.getTransferOperationalInfo().getAuthorizedName() == null) {
                        throw new TransferNotAllowedException(new ErrorMessage("errors.NullInput"));
                }

                //confirm i own the records in question
                //confirm i issued the transfer token
                TransferEntities x = new TransferEntities();
                x.setKeyBag(body.getKeyBag());
                x.setTransferToken(body.getTransferToken());
                String transferTokenId;
                try {
                    transferTokenId = new String(body.getTransferToken().getOpaqueToken(), UTF8);
                } catch (UnsupportedEncodingException ex) {
                    throw new InvalidValueException(new ErrorMessage("errors.stringEncoding"));
                }
                new ValidateCustodyTransfer(null).validateTransferLocalEntities(em, transferTokenId, body.getKeyBag().getKey());

        }

}
