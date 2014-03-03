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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.PublicationQuery;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.ReplicationQuery;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.model.Node;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.validation.ValidatePublish;
import org.apache.juddi.validation.ValidateReplication;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.TransferCustody;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIReplicationPortType;

//@WebService(serviceName="UDDIReplicationService", 
//			endpointInterface="org.uddi.v3_service.UDDIReplicationPortType",
//			targetNamespace = "urn:uddi-org:v3_service")
/**
 * UDDI Replication defines four APIs. The first two presented here are used to
 * perform replication and issue notifications. The latter ancillary APIs
 * provide support for other aspects of UDDI Replication.
 * <ul><li>get_changeRecords</li>
 * <li>notify_changeRecordsAvailable</li>
 * <li>do_ping</li>
 * <li>get_highWaterMarks</li>
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree<a/>
 */
public class UDDIReplicationImpl extends AuthenticatedService implements UDDIReplicationPortType {

        private static Log log = LogFactory.getLog(UDDIReplicationImpl.class);
        private UDDIServiceCounter serviceCounter;

        public UDDIReplicationImpl() {
                super();
                serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(UDDIReplicationImpl.class);

        }

        private synchronized UDDIReplicationPortType getReplicationClient(String node) {
                if (cache.containsKey(node)) {
                        return cache.get(node);
                }
                UDDIService svc = new UDDIService();
                UDDIReplicationPortType replicationClient = svc.getUDDIReplicationPort();
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        Node find = em.find(org.apache.juddi.model.Node.class, node);
                        ((BindingProvider) replicationClient).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, find.getReplicationUrl());
                        cache.put(node, replicationClient);
                        return replicationClient;
                } catch (Exception ex) {
                        log.fatal("Node not found!" + node, ex);
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
                em.close();
                return null;

        }
        private Map<String, UDDIReplicationPortType> cache = new HashMap<String, UDDIReplicationPortType>();

        /**
         * @since 3.3
         * @param body
         * @return
         * @throws DispositionReportFaultMessage
         */
        public String doPing(DoPing body) throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();
                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ReplicationQuery.DO_PING, QueryStatus.SUCCESS, procTime);
                try {
                        return AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ROOT_BUSINESS);
                } catch (ConfigurationException ex) {
                        log.fatal("Unable to load configuration!", ex);
                }
                throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval"));
        }

        public List<ChangeRecord> getChangeRecords(String requestingNode,
                HighWaterMarkVectorType changesAlreadySeen,
                BigInteger responseLimitCount,
                HighWaterMarkVectorType responseLimitVector)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();
                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ReplicationQuery.GET_CHANGERECORDS,
                        QueryStatus.SUCCESS, procTime);

                //TODO fetch all records that have changed since changesAlreadySeen
                ChangeRecord r = new ChangeRecord();
                
                ValidateReplication.unsupportedAPICall();
                return null;
        }

        public List<ChangeRecordIDType> getHighWaterMarks()
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();
                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ReplicationQuery.GET_HIGHWATERMARKS, QueryStatus.SUCCESS, procTime);

                //fetch from database the highest known watermark
                ValidateReplication.unsupportedAPICall();
                return null;
        }

        public void notifyChangeRecordsAvailable(NotifyChangeRecordsAvailable body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();
                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ReplicationQuery.NOTIFY_CHANGERECORDSAVAILABLE,
                        QueryStatus.SUCCESS, procTime);
                //some other node just told us there's new records available, call
                //getChangeRecords from the remote node asynch

                ValidateReplication.unsupportedAPICall();
        }

        public void transferCustody(TransferCustody body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();


                //*this node is transfering data to another node
                //body.getTransferOperationalInfo().


                ValidateReplication.unsupportedAPICall();

                EntityManager em = PersistenceManager.getEntityManager();
                //EntityTransaction tx = em.getTransaction();


                //The custodial node must verify that it has granted permission to transfer the entities identified and that this permission is still valid.  This operation is comprised of two steps:
                //1.       Verification that the transferToken was issued by it, that it has not expired, that it represents the authority to transfer no more and no less than those entities identified by the businessKey and tModelKey elements and that all these entities are still valid and not yet transferred. The transferToken is invalidated if any of these conditions are not met.
                //2.       If the conditions above are met, the custodial node will prevent any further changes to the entities identified by the businessKey and tModelKey elements identified. The entity will remain in this state until the replication stream indicates it has been successfully processed via the replication stream. 

                //Upon successful verification of the custody transfer request by the custodial node, an empty message is returned by it indicating the success of the request and acknowledging the custody transfer. 

                //Following the issue of the empty message, the custodial node will submit into the replication stream a changeRecordNewData providing in the operationalInfo, the nodeID accepting custody of the datum and the authorizedName of the publisher accepting ownership. The acknowledgmentRequested attribute of this change record MUST be set to "true".
                //TODO enqueue Replication message

                //Finally, the custodial node invalidates the transferToken in order to prevent additional calls of the transfer_entities API.
                DiscardTransferToken dtt = new DiscardTransferToken();
                dtt.setKeyBag(body.getKeyBag());
                dtt.setTransferToken(body.getTransferToken());
                new UDDICustodyTransferImpl().discardTransferToken(dtt);
        }
}
