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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import static org.apache.juddi.api.impl.AuthenticatedService.logger;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.ReplicationQuery;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.BusinessEntity;
import org.apache.juddi.model.BusinessService;
import org.apache.juddi.model.Node;
import org.apache.juddi.model.Tmodel;
import static org.apache.juddi.replication.ReplicationNotifier.FetchEdges;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.validation.ValidateReplication;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.ReplicationConfiguration;
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

        private UDDIServiceCounter serviceCounter;

        private static PullTimerTask timer = null;
        private long startBuffer = 20000l;//AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_START_BUFFER, 20000l); // 20s startup delay default 
        private long interval = 300000l;// AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default

        private static UDDIPublicationImpl pub = null;

        public UDDIReplicationImpl() {
                super();
                if (pub == null) {
                        pub = new UDDIPublicationImpl();
                }
                serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(UDDIReplicationImpl.class);
                Init();
                try {
                        startBuffer = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_START_BUFFER, 20000l); // 20s startup delay default 
                        interval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
                } catch (ConfigurationException ex) {
                        logger.fatal(ex);
                }

        }

        private synchronized void Init() {
                if (queue == null) {
                        queue = new ConcurrentLinkedDeque<NotifyChangeRecordsAvailable>();
                }
                timer = new PullTimerTask();

        }

        private boolean Excluded(HighWaterMarkVectorType changesAlreadySeen, ChangeRecord r) {
                if (changesAlreadySeen != null) {
                        for (int i = 0; i < changesAlreadySeen.getHighWaterMark().size(); i++) {
                                if (changesAlreadySeen.getHighWaterMark().get(i).getNodeID().equals(r.getChangeID().getNodeID())
                                     && changesAlreadySeen.getHighWaterMark().get(i).getOriginatingUSN().equals(r.getChangeID().getOriginatingUSN())) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        private class PullTimerTask extends TimerTask {

                private Timer timer = null;

                public PullTimerTask() {
                        super();
                        timer = new Timer(true);
                        timer.scheduleAtFixedRate(this, startBuffer, interval);
                }

                @Override
                public void run() {

                        //ok someone told me there's a change available
                        while (!queue.isEmpty()) {
                                NotifyChangeRecordsAvailable poll = queue.poll();
                                if (poll != null) {
                                        UDDIReplicationPortType replicationClient = getReplicationClient(poll.getNotifyingNode());
                                        try {
                                                //ok now get all the changes
                                                List<ChangeRecord> records
                                                     = replicationClient.getChangeRecords(node,
                                                          null, null, poll.getChangesAvailable());
                                                //ok now we need to persist the change records
                                                for (int i = 0; i < records.size(); i++) {
                                                        PersistChangeRecord(records.get(i));
                                                }
                                        } catch (Exception ex) {
                                                logger.equals(ex);
                                        }
                                }
                        }
                }

                @Override
                public boolean cancel() {
                        timer.cancel();
                        return super.cancel();
                }

                /**
                 * someone told me there's a change available, we retrieved it
                 * and are processing the changes locally
                 *
                 * @param rec
                 */
                private void PersistChangeRecord(ChangeRecord rec) {
                        if (rec == null) {
                                return;
                        }
                        EntityManager em = PersistenceManager.getEntityManager();
                        EntityTransaction tx = em.getTransaction();
                        /**
                         * In nodes that support pre-bundled replication
                         * responses, the recipient of the get_changeRecords
                         * message MAY return more change records than requested
                         * by the caller. In this scenario, the caller MUST also
                         * be prepared to deal with such redundant changes where
                         * a USN is less than the USN specified in the
                         * changesAlreadySeen highWaterMarkVector.
                         */
                        try {
                                tx.begin();
                                //the change record rec must also be persisted!!
                                em.persist(MappingApiToModel.mapChangeRecord(rec));
                                //<editor-fold defaultstate="collapsed" desc="delete a record">

                                if (rec.getChangeRecordDelete() != null) {
                                        if (rec.getChangeRecordDelete() != null && rec.getChangeRecordDelete().getBindingKey() != null && !"".equalsIgnoreCase(rec.getChangeRecordDelete().getBindingKey())) {
                                                //delete a binding template
                                                pub.deleteBinding(rec.getChangeRecordDelete().getBindingKey(), em);
                                        }
                                        if (rec.getChangeRecordDelete() != null && rec.getChangeRecordDelete().getBusinessKey() != null && !"".equalsIgnoreCase(rec.getChangeRecordDelete().getBusinessKey())) {
                                                //delete a business 
                                                pub.deleteBusiness(rec.getChangeRecordDelete().getBusinessKey(), em);
                                        }
                                        if (rec.getChangeRecordDelete() != null && rec.getChangeRecordDelete().getServiceKey() != null && !"".equalsIgnoreCase(rec.getChangeRecordDelete().getServiceKey())) {
                                                //delete a service 
                                                pub.deleteService(rec.getChangeRecordDelete().getServiceKey(), em);
                                        }
                                        if (rec.getChangeRecordDelete() != null && rec.getChangeRecordDelete().getTModelKey() != null && !"".equalsIgnoreCase(rec.getChangeRecordDelete().getTModelKey())) {
                                                //delete a tmodel 
                                                /**
                                                 * The changeRecordDelete for a
                                                 * tModel does not correspond to
                                                 * any API described in this
                                                 * specification and should only
                                                 * appear in the replication
                                                 * stream as the result of an
                                                 * administrative function to
                                                 * permanently remove a tModel.
                                                 */
                                                pub.deleteTModel(rec.getChangeRecordDelete().getTModelKey(), em);
                                        }
                                }
                                if (rec.getChangeRecordDeleteAssertion() != null && rec.getChangeRecordDeleteAssertion().getPublisherAssertion() != null) {
                                        //delete a pa template                            
                                        pub.deletePublisherAssertion(rec.getChangeRecordDeleteAssertion().getPublisherAssertion(), em);
                                }

//</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="New Data">
                                if (rec.getChangeRecordNewData() != null) {

                                        if (rec.getChangeRecordNewData().getOperationalInfo().getNodeID() == null) {
                                                throw new Exception("Inbound replication data is missiong node id!");
                                        }

                                        //The operationalInfo element MUST contain the operational information associated with the indicated new data.
                                        if (rec.getChangeRecordNewData().getOperationalInfo() == null) {
                                                logger.warn("Inbound replication data does not have the required OperationalInfo element and is NOT spec compliant. Data will be ignored");
                                        } else {
                                                if (rec.getChangeRecordNewData().getBindingTemplate() != null) {
                                                        //fetch the binding template if it exists already
                                                        //if it exists, 
                                                        //      confirm the owning node, it shouldn't be the local node id, if it is, throw
                                                        //      the owning node should be the same as it was before

                                                        BusinessService model = em.find(org.apache.juddi.model.BusinessService.class, rec.getChangeRecordNewData().getBindingTemplate().getServiceKey());
                                                        if (model == null) {
                                                                logger.error("Replication error, attempting to insert a binding where the service doesn't exist yet");
                                                        } else {
                                                                ValidateNodeIdMatches(model.getNodeId(), rec.getChangeRecordNewData().getOperationalInfo());

                                                                org.apache.juddi.model.BindingTemplate modelT = new org.apache.juddi.model.BindingTemplate();
                                                                MappingApiToModel.mapBindingTemplate(rec.getChangeRecordNewData().getBindingTemplate(), modelT, model);
                                                                MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewData().getOperationalInfo());
                                                                em.persist(model);
                                                        }

                                                } else if (rec.getChangeRecordNewData().getBusinessEntity() != null) {

                                                        BusinessEntity model = em.find(org.apache.juddi.model.BusinessEntity.class, rec.getChangeRecordNewData().getBusinessEntity().getBusinessKey());
                                                        if (model == null) {
                                                                model = new BusinessEntity();
                                                        } else {
                                                                ValidateNodeIdMatches(model.getNodeId(), rec.getChangeRecordNewData().getOperationalInfo());
                                                        }
                                                        MappingApiToModel.mapBusinessEntity(rec.getChangeRecordNewData().getBusinessEntity(), model);
                                                        MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewData().getOperationalInfo());

                                                        em.persist(model);

                                                }
                                                if (rec.getChangeRecordNewData().getBusinessService() != null) {
                                                        BusinessEntity find = em.find(org.apache.juddi.model.BusinessEntity.class, rec.getChangeRecordNewData().getBusinessService().getBusinessKey());
                                                        if (find == null) {
                                                                logger.error("Replication error, attempting to insert a service where the business doesn't exist yet");
                                                        } else {
                                                                org.apache.juddi.model.BusinessService model = new org.apache.juddi.model.BusinessService();
                                                                MappingApiToModel.mapBusinessService(rec.getChangeRecordNewData().getBusinessService(), model, find);
                                                                MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewData().getOperationalInfo());

                                                                em.persist(model);
                                                        }

                                                } else if (rec.getChangeRecordNewData().getTModel() != null) {
                                                        Tmodel model = new Tmodel();
                                                        MappingApiToModel.mapTModel(rec.getChangeRecordNewData().getTModel(), model);

                                                        MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewData().getOperationalInfo());

                                                        em.persist(model);
                                                }

                                        }

                                }
//</editor-fold>

                                // changeRecordNull no action needed
                                // changeRecordHide tmodel only
                                //<editor-fold defaultstate="collapsed" desc="hide tmodel">
                                if (rec.getChangeRecordHide() != null) {
                                        /*
                                         A changeRecordHide element corresponds to the behavior of hiding a tModel described in the delete_tModel in the Publish API section of this Specification.  A tModel listed in a changeRecordHide should be marked as hidden, so that it is not returned in response to a find_tModel API call.
                                        
                                         The changeRecordHide MUST contain a modified timestamp to allow multi-node registries to calculate consistent modifiedIncludingChildren timestamps as described in Section 3.8 operationalInfo Structure.
                                         */
                                        String key = rec.getChangeRecordHide().getTModelKey();
                                        org.apache.juddi.model.Tmodel existing = em.find(org.apache.juddi.model.Tmodel.class, key);
                                        if (existing == null) {
                                                logger.error("Unexpected delete/hide tmodel message received for non existing key " + key);
                                        } else {
                                                existing.setDeleted(true);
                                                existing.setModified(rec.getChangeRecordHide().getModified().toGregorianCalendar().getTime());
                                                existing.setModifiedIncludingChildren(rec.getChangeRecordHide().getModified().toGregorianCalendar().getTime());
                                                em.persist(existing);
                                        }
                                }
//</editor-fold>

                                //<editor-fold defaultstate="collapsed" desc="changeRecordPublisherAssertion">
                                if (rec.getChangeRecordPublisherAssertion() != null) {
//TODO implement
                                }
//</editor-fold>

                                tx.commit();

                        } catch (Exception drfm) {
                                logger.warn(drfm);
                        } finally {
                                if (tx.isActive()) {
                                        tx.rollback();
                                }
                                em.close();
                        }
                }

        }

        private static void ValidateNodeIdMatches(String nodeId, OperationalInfo operationalInfo) throws Exception {
                if (nodeId == null || operationalInfo == null) {
                        throw new Exception("either the local node ID is null or the inbound replication data's node id is null");
                }
                if (!nodeId.equals(operationalInfo.getNodeID())) {
                        throw new Exception("node id mismatch!");
                }
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
                        logger.fatal("Node not found!" + node, ex);
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

                return node;

        }

        @Override
        public List<ChangeRecord> getChangeRecords(String requestingNode,
             HighWaterMarkVectorType changesAlreadySeen,
             BigInteger responseLimitCount,
             HighWaterMarkVectorType responseLimitVector)
             throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                new ValidateReplication(null).validateGetChangeRecords(requestingNode, changesAlreadySeen, responseLimitCount, responseLimitVector, FetchEdges(), ctx);

                //TODO should we validate that "requestingNode" is in the replication config?
                List<ChangeRecord> ret = new ArrayList<ChangeRecord>();
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();

                /**
                 * More specifically, the recipient determines the particular
                 * change records that are returned by comparing the originating
                 * USNs in the caller’s high water mark vector with the
                 * originating USNs of each of the changes the recipient has
                 * seen from others or generated by itself. The recipient SHOULD
                 * only return change records that have originating USNs that
                 * are greater than those listed in the changesAlreadySeen
                 * highWaterMarkVector and less than the limit required by
                 * either the responseLimitCount or the responseLimitVector.
                 *
                 *
                 * Part of the message is a high water mark vector that contains
                 * for each node of the registry the originating USN of the most
                 * recent change record that has been successfully processed by
                 * the invocating node
                 */
                int maxrecords = 100;
                if (responseLimitCount != null) {
                        maxrecords = responseLimitCount.intValue();
                }
                try {
                        tx.begin();
                        Long firstrecord = 1L;
                        Long lastrecord = null;

                        if (changesAlreadySeen != null) {
                                //this is basically a lower limit (i.e. the newest record that was processed by the requestor
                                //therefore we want the oldest record stored locally to return to the requestor for processing
                                for (int i = 0; i < changesAlreadySeen.getHighWaterMark().size(); i++) {
                                        if (responseLimitVector.getHighWaterMark().get(i).getNodeID().equals(node)) {
                                                firstrecord = changesAlreadySeen.getHighWaterMark().get(i).getOriginatingUSN() + 1;
                                        }
                                }
                        }
                        if (responseLimitVector != null) {
                                //using responseLimitVector, indicating for each node in the graph the first change originating there that he does not wish to be returned.
                                //upper limit basically
                                for (int i = 0; i < responseLimitVector.getHighWaterMark().size(); i++) {
                                        if (responseLimitVector.getHighWaterMark().get(i).getNodeID().equals(node)) {
                                                lastrecord = responseLimitVector.getHighWaterMark().get(i).getOriginatingUSN();
                                        }
                                }
                        }

                        Query createQuery = null;
                        if (lastrecord != null) {
                                createQuery = em.createQuery("select e from ChangeRecord e where (e.id > :inbound and e.nodeID = :node and e.id < :lastrecord) OR (e.originatingUSN > :inbound and e.nodeID != :node and e.originatingUSN < :lastrecord) order by e.id ASC");
                                createQuery.setParameter("lastrecord", lastrecord);
                        } else {
                                createQuery = em.createQuery("select e from ChangeRecord e where (e.id > :inbound and e.nodeID = :node) OR (e.originatingUSN > :inbound and e.nodeID != :node) order by e.id ASC");
                        }
                        createQuery.setMaxResults(maxrecords);
                        createQuery.setParameter("inbound", firstrecord);
                        createQuery.setParameter("node", node);

                        List<org.apache.juddi.model.ChangeRecord> records = (List<org.apache.juddi.model.ChangeRecord>) (org.apache.juddi.model.ChangeRecord) createQuery.getResultList();
                        for (int i = 0; i < records.size(); i++) {
                                ChangeRecord r = MappingModelToApi.mapChangeRecord(records.get(i));
                                if (!Excluded(changesAlreadySeen, r)) {
                                        ret.add(r);
                                }

                        }

                        tx.rollback();
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(ReplicationQuery.GET_CHANGERECORDS,
                             QueryStatus.SUCCESS, procTime);

                } catch (Exception ex) {
                        logger.fatal("Error, this node is: " + node, ex);
                        throw new FatalErrorException(new ErrorMessage("E_fatalError", ex.getMessage()));

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
                return ret;
        }

        /**
         * This UDDI API message provides a means to obtain a list of
         * highWaterMark element containing the highest known USN for all nodes
         * in the replication graph. If there is no graph, we just return the
         * local bits
         *
         * @return
         * @throws DispositionReportFaultMessage
         */
        @Override
        public List<ChangeRecordIDType> getHighWaterMarks()
             throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                List<ChangeRecordIDType> ret = new ArrayList<ChangeRecordIDType>();

                //fetch from database the highest known watermark
                ReplicationConfiguration FetchEdges = FetchEdges();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();
                        if (FetchEdges != null) {
                                Iterator<String> it = FetchEdges.getCommunicationGraph().getNode().iterator();
                                while (it.hasNext()) {
                                        String nextNode = it.next();
                                        if (!nextNode.equals(node)) {

                                                Long id = (Long) em.createQuery("select e.originatingUSN from ChangeRecord e where e.nodeID = :node order by e.originatingUSN desc").setParameter("node", nextNode).setMaxResults(1).getSingleResult();
                                                if (id == null) {
                                                        id = 0L;
                                                        //per the spec
                                                }
                                                ChangeRecordIDType x = new ChangeRecordIDType(nextNode, id);
                                                ret.add(x);
                                        }
                                }
                        }
                        //dont forget this node
                        Long id = (Long) em.createQuery("select (e.id) from ChangeRecord e where e.nodeID = :node  order by e.id desc").setParameter("node", node).setMaxResults(1).getSingleResult();
                        if (id == null) {
                                id = 0L;
                        }
                        ChangeRecordIDType x = new ChangeRecordIDType();
                        x.setNodeID(node);
                        x.setOriginatingUSN(id);
                        ret.add(x);

                        tx.rollback();
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(ReplicationQuery.GET_HIGHWATERMARKS, QueryStatus.SUCCESS, procTime);

                } catch (Exception drfm) {
                        throw new FatalErrorException(new ErrorMessage("E_fatalError", drfm.getMessage()));

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

                return ret;
        }

        /**
         * this means that another node has a change and we need to pick up the
         * change and apply it to our local database.
         *
         * @param body
         * @throws DispositionReportFaultMessage
         */
        @Override
        public void notifyChangeRecordsAvailable(NotifyChangeRecordsAvailable body)
             throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();
                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ReplicationQuery.NOTIFY_CHANGERECORDSAVAILABLE,
                     QueryStatus.SUCCESS, procTime);
                //some other node just told us there's new records available, call
                //getChangeRecords from the remote node asynch

                new ValidateReplication(null).validateNotifyChangeRecordsAvailable(body, ctx);

                queue.add(body);

                //ValidateReplication.unsupportedAPICall();
        }
        private static Queue<NotifyChangeRecordsAvailable> queue = null;

        /**
         * transfers custody of an entity from node1/user1 to node2/user2
         *
         * @param body
         * @throws DispositionReportFaultMessage
         */
        @Override
        public void transferCustody(TransferCustody body)
             throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                //*this node is transfering data to another node
                //body.getTransferOperationalInfo().
                ValidateReplication.unsupportedAPICall();

                EntityManager em = PersistenceManager.getEntityManager();
                //EntityTransaction tx = em.getTransaction();

                /**
                 * The custodial node must verify that it has granted permission
                 * to transfer the entities identified and that this permission
                 * is still valid. This operation is comprised of two steps: 
                 * 
                 * 1.
                 * Verification that the transferToken was issued by it, that it
                 * has not expired, that it represents the authority to transfer
                 * no more and no less than those entities identified by the
                 * businessKey and tModelKey elements and that all these
                 * entities are still valid and not yet transferred. The
                 * transferToken is invalidated if any of these conditions are
                 * not met. 
                 * 
                 * 2. If the conditions above are met, the custodial
                 * node will prevent any further changes to the entities
                 * identified by the businessKey and tModelKey elements
                 * identified. The entity will remain in this state until the
                 * replication stream indicates it has been successfully
                 * processed via the replication stream. Upon successful
                 * verification of the custody transfer request by the custodial
                 * node, an empty message is returned by it indicating the
                 * success of the request and acknowledging the custody
                 * transfer. Following the issue of the empty message, the
                 * custodial node will submit into the replication stream a
                 * changeRecordNewData providing in the operationalInfo, the
                 * nodeID accepting custody of the datum and the authorizedName
                 * of the publisher accepting ownership. The
                 * acknowledgmentRequested attribute of this change record MUST
                 * be set to "true".
                 *
                 * TODO enqueue Replication message 
                 * 
                 * Finally, the custodial node
                 * invalidates the transferToken in order to prevent additional
                 * calls of the transfer_entities API.
                 */
                DiscardTransferToken dtt = new DiscardTransferToken();
                dtt.setKeyBag(body.getKeyBag());
                dtt.setTransferToken(body.getTransferToken());
                new UDDICustodyTransferImpl().discardTransferToken(dtt);
        }

}
