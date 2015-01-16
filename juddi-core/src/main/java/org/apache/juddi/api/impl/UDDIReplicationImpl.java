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

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import static org.apache.juddi.api.impl.AuthenticatedService.logger;
import static org.apache.juddi.api.impl.AuthenticatedService.node;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.ReplicationQuery;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.BusinessEntity;
import org.apache.juddi.model.BusinessService;
import org.apache.juddi.model.Operator;
import org.apache.juddi.model.PublisherAssertion;
import org.apache.juddi.model.PublisherAssertionId;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.replication.ReplicationNotifier;
import static org.apache.juddi.replication.ReplicationNotifier.FetchEdges;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.TransferNotAllowedException;
import org.apache.juddi.validation.ValidateReplication;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordAcknowledgement;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.ChangeRecords;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.GetChangeRecords;
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
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
@WebService(serviceName = "UDDI_Replication_PortType", targetNamespace = "urn:uddi-org:repl_v3_portType",
        endpointInterface = "org.uddi.v3_service.UDDIReplicationPortType")
@XmlSeeAlso({
        org.uddi.custody_v3.ObjectFactory.class,
        org.uddi.repl_v3.ObjectFactory.class,
        org.uddi.subr_v3.ObjectFactory.class,
        org.uddi.api_v3.ObjectFactory.class,
        org.uddi.vscache_v3.ObjectFactory.class,
        org.uddi.vs_v3.ObjectFactory.class,
        org.uddi.sub_v3.ObjectFactory.class,
        org.w3._2000._09.xmldsig_.ObjectFactory.class,
        org.uddi.policy_v3.ObjectFactory.class,
        org.uddi.policy_v3_instanceparms.ObjectFactory.class
})
public class UDDIReplicationImpl extends AuthenticatedService implements UDDIReplicationPortType {
        
        static void notifyConfigurationChange(ReplicationConfiguration oldConfig, ReplicationConfiguration newConfig) {

                //if the config is different
                Set<String> oldnodes = getNodes(oldConfig);
                Set<String> newNodes = getNodes(newConfig);
                
                Set<String> addedNodes = diffNodeList(oldnodes, newNodes);
                if (queue == null) {
                        queue = new ConcurrentLinkedQueue<String>();
                }
                for (String s : addedNodes) {
                        if (!s.equals(node)) {
                                logger.info("This node: " + node + ". New replication node queue for synchronization: " + s);
                                //HighWaterMarkVectorType highWaterMarkVectorType = new HighWaterMarkVectorType();
                                //highWaterMarkVectorType.getHighWaterMark().add(new ChangeRecordIDType(s, 0L));
                                queue.add(s);
                        }
                }
                
        }
        
        private static Set<String> getNodes(ReplicationConfiguration oldConfig) {
                Set<String> ret = new HashSet<String>();
                if (oldConfig == null) {
                        return ret;
                }
                for (org.uddi.repl_v3.Operator o : oldConfig.getOperator()) {
                        ret.add(o.getOperatorNodeID());
                }
                if (oldConfig.getCommunicationGraph() != null) {
                        ret.addAll(oldConfig.getCommunicationGraph().getNode());
                }
                return ret;
        }

        /**
         * returns items in "newNodes" that are not in "oldNodes"
         *
         * @param oldnodes
         * @param newNodes
         * @return
         */
        private static Set<String> diffNodeList(Set<String> oldnodes, Set<String> newNodes) {
                Set<String> diff = new HashSet<String>();
                Iterator<String> iterator = newNodes.iterator();
                while (iterator.hasNext()) {
                        String lhs = iterator.next();
                        Iterator<String> iterator1 = oldnodes.iterator();
                        boolean found = false;
                        while (iterator1.hasNext()) {
                                String rhs = iterator1.next();
                                if (rhs.equalsIgnoreCase(lhs)) {
                                        found = true;
                                        break;
                                }
                        }
                        if (!found) {
                                diff.add(lhs);
                        }
                        
                }
                return diff;
        }
        
        private UDDIServiceCounter serviceCounter;
        
        private static PullTimerTask timer = null;
        private long startBuffer = 5000l;//AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_START_BUFFER, 20000l); // 20s startup delay default 
        private long interval = 5000l;// AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default

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
                        interval = 5000;//AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
                } catch (ConfigurationException ex) {
                        logger.fatal(ex);
                }
                
        }
        
        private synchronized void Init() {
                if (queue == null) {
                        queue = new ConcurrentLinkedQueue<String>();
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

        /**
         * handles when a remote node tells me that there's an update(s)
         * available
         */
        private class PullTimerTask extends TimerTask {
                
                private Timer timer = null;
                
                public PullTimerTask() {
                        super();
                        timer = new Timer(true);
                        timer.scheduleAtFixedRate(this, startBuffer, interval);
                }
                
                @Override
                public void run() {
                        
                        if (!queue.isEmpty()) {
                                logger.info("Replication change puller thread started. Queue size: " + queue.size());
                        }
                        //ok someone told me there's a change available
                        while (!queue.isEmpty()) {
                                String poll = queue.poll();
                                if (poll != null && !poll.equalsIgnoreCase(node)) {
                                        UDDIReplicationPortType replicationClient = getReplicationClient(poll);
                                        if (replicationClient == null) {
                                                logger.fatal("unable to obtain a replication client to node " + poll);
                                        } else {
                                                try {
                                                        //get the high water marks for this node
                                                        //ok now get all the changes

                                                        //done  replace with last known record from the given node
                                                        //for (int xx = 0; xx < poll.getChangesAvailable().getHighWaterMark().size(); xx++) {
                                                        //        logger.info("Node " + poll.getChangesAvailable().getHighWaterMark().get(xx).getNodeID()
                                                        //                + " USN " + poll.getChangesAvailable().getHighWaterMark().get(xx).getOriginatingUSN());
                                                        //}
                                                        int recordsreturned = 1;
                                                        while (recordsreturned > 0) {
                                                                GetChangeRecords body = new GetChangeRecords();
                                                                body.setRequestingNode(node);
                                                                body.setResponseLimitCount(BigInteger.valueOf(20));
                                                                
                                                                body.setChangesAlreadySeen(getLastChangeRecordFrom(poll));
                                                                logger.info("fetching updates from " + poll + " since " + body.getChangesAlreadySeen().getHighWaterMark().get(0).getOriginatingUSN() + " items still in the queue: " + queue.size());
                                                                
                                                                List<ChangeRecord> records
                                                                        = replicationClient.getChangeRecords(body).getChangeRecord();
                                                                //ok now we need to persist the change records
                                                                logger.info("Change records retrieved from " + poll + ", " + records.size());
                                                                for (int i = 0; i < records.size(); i++) {
                                                                        //logger.info("Change records retrieved " + records.get(i).getChangeID().getNodeID() + " USN " + records.get(i).getChangeID().getOriginatingUSN());
                                                                        PersistChangeRecord(records.get(i));
                                                                }
                                                                recordsreturned = records.size();
                                                        }
                                                } catch (Exception ex) {
                                                        logger.error("Error caught fetching replication changes from " + poll + " @" + ((BindingProvider) replicationClient).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY), ex);
                                                }
                                        }
                                } else {
                                        logger.warn("weird, popped an object from the queue but it was null.");
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
                        logger.debug("_______________________Remote change request " + rec.getChangeID().getNodeID() + ":" + rec.getChangeID().getOriginatingUSN());
                        
                        if (rec.getChangeID().getNodeID().equalsIgnoreCase(node)) {
                                logger.info("Just received a change record that i created, ignoring....");
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
                                org.apache.juddi.model.ChangeRecord mapChangeRecord = MappingApiToModel.mapChangeRecord(rec);
                                mapChangeRecord.setId(null);
                                em.persist(mapChangeRecord);
                                tx.commit();
                                logger.info("Remote CR saved, it was from " + mapChangeRecord.getNodeID()
                                        + " USN:" + mapChangeRecord.getOriginatingUSN()
                                        + " Type:" + mapChangeRecord.getRecordType().name()
                                        + " Key:" + mapChangeRecord.getEntityKey()
                                        + " Local id:" + mapChangeRecord.getId());
                                tx = em.getTransaction();
                                tx.begin();
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
                                                Object tm=em.find(Tmodel.class, rec.getChangeRecordDelete().getTModelKey());
                                                if (tm!=null)
                                                        em.remove(tm);
                                                else
                                                        logger.error("failed to adminstratively delete tmodel because it doesn't exist. " + rec.getChangeRecordDelete().getTModelKey());
                                                //pub.deleteTModel(rec.getChangeRecordDelete().getTModelKey(), em);
                                        }
                                }
                                if (rec.getChangeRecordDeleteAssertion() != null && rec.getChangeRecordDeleteAssertion().getPublisherAssertion() != null) {
                                        //delete a pa template                            
                                        pub.deletePublisherAssertion(rec.getChangeRecordDeleteAssertion(), em);
                                }

//</editor-fold>
                                //<editor-fold defaultstate="collapsed" desc="New Data">
                                if (rec.getChangeRecordNewData() != null) {

                                        //The operationalInfo element MUST contain the operational information associated with the indicated new data.
                                        if (rec.getChangeRecordNewData().getOperationalInfo() == null) {
                                                logger.warn("Inbound replication data does not have the required OperationalInfo element and is NOT spec compliant. Data will be ignored");
                                        } else {
                                                if (rec.getChangeRecordNewData().getOperationalInfo().getNodeID() == null) {
                                                        throw new Exception("Inbound replication data is missiong node id! Change will not be applied");
                                                }
                                                if (rec.getChangeRecordNewData().getOperationalInfo().getNodeID().equalsIgnoreCase(node)) {
                                                        logger.warn("Inbound replication data is modifying locally owned data. This is not allowed");
                                                }
                                                if (rec.getChangeRecordNewData().getBindingTemplate() != null) {
                                                        //fetch the binding template if it exists already
                                                        //if it exists, 
                                                        //      confirm the owning node, it shouldn't be the local node id, if it is, throw
                                                        //      the owning node should be the same as it was before

                                                        BusinessService model = em.find(org.apache.juddi.model.BusinessService.class, rec.getChangeRecordNewData().getBindingTemplate().getServiceKey());
                                                        if (model == null) {
                                                                logger.error("Replication error, attempting to insert a binding where the service doesn't exist yet");
                                                        } else {
                                                                ValidateNodeIdMatches(rec.getChangeRecordNewData().getOperationalInfo().getNodeID(), model.getNodeId());
                                                                
                                                                org.apache.juddi.model.BindingTemplate bt = em.find(org.apache.juddi.model.BindingTemplate.class, rec.getChangeRecordNewData().getBindingTemplate().getBindingKey());
                                                                if (bt != null) {
                                                                        //ValidateNodeIdMatches(node, bt.getNodeId());
                                                                        em.remove(bt);
                                                                }
                                                                bt = new BindingTemplate();
                                                                MappingApiToModel.mapBindingTemplate(rec.getChangeRecordNewData().getBindingTemplate(), bt, model);
                                                                MappingApiToModel.mapOperationalInfo(bt, rec.getChangeRecordNewData().getOperationalInfo());
                                                                // MappingApiToModel.mapOperationalInfoIncludingChildren(model, rec.getChangeRecordNewData().getOperationalInfo());
                                                                em.persist(bt);
                                                        }
                                                        
                                                } else if (rec.getChangeRecordNewData().getBusinessEntity() != null) {
                                                        
                                                        BusinessEntity model = em.find(org.apache.juddi.model.BusinessEntity.class, rec.getChangeRecordNewData().getBusinessEntity().getBusinessKey());
                                                        if (model != null) {
                                                                ValidateNodeIdMatches(rec.getChangeRecordNewData().getOperationalInfo().getNodeID(), model.getNodeId());
                                                                //em.remove(model);
                                                                MappingApiToModel.mapBusinessEntity(rec.getChangeRecordNewData().getBusinessEntity(), model);
                                                                MappingApiToModel.mapOperationalInfoIncludingChildren(model, rec.getChangeRecordNewData().getOperationalInfo());
                                                                em.merge(model);
                                                        } else {
                                                                model = new BusinessEntity();
                                                                MappingApiToModel.mapBusinessEntity(rec.getChangeRecordNewData().getBusinessEntity(), model);
                                                                MappingApiToModel.mapOperationalInfoIncludingChildren(model, rec.getChangeRecordNewData().getOperationalInfo());
                                                                em.persist(model);
                                                        }
                                                }
                                                if (rec.getChangeRecordNewData().getBusinessService() != null) {
                                                        BusinessEntity find = em.find(org.apache.juddi.model.BusinessEntity.class, rec.getChangeRecordNewData().getBusinessService().getBusinessKey());
                                                        if (find == null) {
                                                                logger.error("Replication error, attempting to insert a service where the business doesn't exist yet");
                                                        } else {
                                                                
                                                                org.apache.juddi.model.BusinessService model = null;
                                                                model = em.find(org.apache.juddi.model.BusinessService.class, rec.getChangeRecordNewData().getBusinessService().getServiceKey());
                                                                if (model != null) {
                                                                        ValidateNodeIdMatches(rec.getChangeRecordNewData().getOperationalInfo().getNodeID(), model.getNodeId());
                                                                        em.remove(model);
                                                                }
                                                                
                                                                model = new org.apache.juddi.model.BusinessService();
                                                                MappingApiToModel.mapBusinessService(rec.getChangeRecordNewData().getBusinessService(), model, find);
                                                                MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewData().getOperationalInfo());
                                                                MappingApiToModel.mapOperationalInfoIncludingChildren(model, rec.getChangeRecordNewData().getOperationalInfo());
                                                                
                                                                em.persist(model);
                                                        }
                                                        
                                                } else if (rec.getChangeRecordNewData().getTModel() != null) {
                                                        
                                                        Tmodel model = em.find(org.apache.juddi.model.Tmodel.class, rec.getChangeRecordNewData().getTModel().getTModelKey());
                                                        if (model != null) {
                                                                ValidateNodeIdMatches(rec.getChangeRecordNewData().getOperationalInfo().getNodeID(), model.getNodeId());
                                                                em.remove(model);
                                                        }
                                                        model = new Tmodel();
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
                                        
                                        logger.info("Repl CR Publisher Assertion");
                                        //TODO are publisher assertions owned by a given node?
                                        PublisherAssertionId paid = new PublisherAssertionId(rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getFromKey(), rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getToKey());
                                        org.apache.juddi.model.PublisherAssertion model = em.find(org.apache.juddi.model.PublisherAssertion.class, paid);
                                        if (model != null) {
                                                logger.info("Repl CR Publisher Assertion - Existing");
                                                
                                                if (rec.getChangeRecordPublisherAssertion().isFromBusinessCheck()) {
                                                        model.setFromCheck("true");
                                                } else {
                                                        model.setFromCheck("false");
                                                }
                                                
                                                if (rec.getChangeRecordPublisherAssertion().isToBusinessCheck()) {
                                                        model.setToCheck("true");
                                                } else {
                                                        model.setToCheck("false");
                                                }
                                                
                                                model.setKeyName(rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getKeyedReference().getKeyName());
                                                model.setKeyValue(rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getKeyedReference().getKeyValue());
                                                model.setTmodelKey(rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getKeyedReference().getTModelKey());
                                                model.setModified(rec.getChangeRecordPublisherAssertion().getModified().toGregorianCalendar().getTime());
                                                //model.setSignatures(MappingApiToModel.mapApiSignaturesToModelSignatures(rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getSignature()));
                                                if ("false".equalsIgnoreCase(model.getFromCheck())
                                                        && "false".equalsIgnoreCase(model.getToCheck())) {
                                                        logger.warn("!!!New publisher assertion is both false and false, strange. no need to save it then!");
                                                        em.remove(model);
                                                }
                                                em.merge(model);
                                        } else {
                                                logger.info("Repl CR Publisher Assertion - new PA");
                                                
                                                model = new PublisherAssertion();
                                                MappingApiToModel.mapPublisherAssertion(rec.getChangeRecordPublisherAssertion().getPublisherAssertion(), model);
                                                model.setBusinessEntityByFromKey(null);
                                                model.setBusinessEntityByToKey(null);
                                                model.setBusinessEntityByFromKey(em.find(BusinessEntity.class, rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getFromKey()));
                                                model.setBusinessEntityByToKey(em.find(BusinessEntity.class, rec.getChangeRecordPublisherAssertion().getPublisherAssertion().getToKey()));
                                                
                                                if (rec.getChangeRecordPublisherAssertion().isFromBusinessCheck()) {
                                                        model.setFromCheck("true");
                                                } else {
                                                        model.setFromCheck("false");
                                                }
                                                
                                                if (rec.getChangeRecordPublisherAssertion().isToBusinessCheck()) {
                                                        model.setToCheck("true");
                                                } else {
                                                        model.setToCheck("false");
                                                }
                                                model.setModified(rec.getChangeRecordPublisherAssertion().getModified().toGregorianCalendar().getTime());
                                                em.persist(model);
                                        }
                                }
//</editor-fold>

                                if (rec.isAcknowledgementRequested()) {
                                        ChangeRecord posack = new ChangeRecord();
                                        posack.setChangeRecordAcknowledgement(new ChangeRecordAcknowledgement());
                                        posack.getChangeRecordAcknowledgement().setAcknowledgedChange(rec.getChangeID());
                                        posack.setAcknowledgementRequested(false);
                                        ReplicationNotifier.Enqueue(MappingApiToModel.mapChangeRecord(posack));
                                }
                                if (rec.getChangeRecordNewDataConditional() != null) {
                                        
                                        if (rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo().getNodeID() == null) {
                                                throw new Exception("Inbound replication data is missiong node id!");
                                        }

                                        //The operationalInfo element MUST contain the operational information associated with the indicated new data.
                                        if (rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo() == null) {
                                                logger.warn("Inbound replication data does not have the required OperationalInfo element and is NOT spec compliant. Data will be ignored");
                                        } else {
                                                if (rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBindingTemplate() != null) {
                                                        //fetch the binding template if it exists already
                                                        //if it exists, 
                                                        //      confirm the owning node, it shouldn't be the local node id, if it is, throw
                                                        //      the owning node should be the same as it was before

                                                        BusinessService model = em.find(org.apache.juddi.model.BusinessService.class, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBindingTemplate().getServiceKey());
                                                        if (model == null) {
                                                                logger.error("Replication error, attempting to insert a binding where the service doesn't exist yet");
                                                        } else {
                                                                
                                                                org.apache.juddi.model.BindingTemplate bt = em.find(org.apache.juddi.model.BindingTemplate.class, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBindingTemplate().getBindingKey());
                                                                if (bt != null) {
                                                                        ValidateNodeIdMatches(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo().getNodeID(), bt.getNodeId());
                                                                        
                                                                        em.remove(bt);
                                                                }
                                                                bt = new BindingTemplate();
                                                                MappingApiToModel.mapBindingTemplate(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBindingTemplate(), bt, model);
                                                                MappingApiToModel.mapOperationalInfo(bt, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());
                                                                // MappingApiToModel.mapOperationalInfoIncludingChildren(model, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());
                                                                em.persist(bt);
                                                        }
                                                        
                                                } else if (rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBusinessEntity() != null) {
                                                        
                                                        BusinessEntity model = em.find(org.apache.juddi.model.BusinessEntity.class, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBusinessEntity().getBusinessKey());
                                                        if (model != null) {
                                                                ValidateNodeIdMatches(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo().getNodeID(), model.getNodeId());
                                                                //TODO revisit access control rules
                                                                em.remove(model);
                                                        }
                                                        model = new BusinessEntity();
                                                        MappingApiToModel.mapBusinessEntity(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBusinessEntity(), model);
                                                        // MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());

                                                        MappingApiToModel.mapOperationalInfoIncludingChildren(model, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());
                                                        logger.warn("Name size on save is " + model.getBusinessNames().size());
                                                        em.persist(model);
                                                        
                                                }
                                                if (rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBusinessService() != null) {
                                                        BusinessEntity find = em.find(org.apache.juddi.model.BusinessEntity.class, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBusinessService().getBusinessKey());
                                                        if (find == null) {
                                                                logger.error("Replication error, attempting to insert a service where the business doesn't exist yet");
                                                        } else {
                                                                
                                                                org.apache.juddi.model.BusinessService model = null;
                                                                model = em.find(org.apache.juddi.model.BusinessService.class, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBusinessService().getServiceKey());
                                                                if (model != null) {
                                                                        ValidateNodeIdMatches(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo().getNodeID(), model.getNodeId());
                                                                        em.remove(model);
                                                                }
                                                                
                                                                model = new org.apache.juddi.model.BusinessService();
                                                                MappingApiToModel.mapBusinessService(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getBusinessService(), model, find);
                                                                MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());
                                                                MappingApiToModel.mapOperationalInfoIncludingChildren(model, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());
                                                                
                                                                em.persist(model);
                                                        }
                                                        
                                                } else if (rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getTModel() != null) {
                                                        
                                                        Tmodel model = em.find(org.apache.juddi.model.Tmodel.class, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getTModel().getTModelKey());
                                                        if (model != null) {
                                                                ValidateNodeIdMatches(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo().getNodeID(), model.getNodeId());
                                                                em.remove(model);
                                                        }
                                                        model = new Tmodel();
                                                        MappingApiToModel.mapTModel(rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getTModel(), model);
                                                        
                                                        MappingApiToModel.mapOperationalInfo(model, rec.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());
                                                        
                                                        em.persist(model);
                                                }
                                                
                                        }
                                        
                                }
                                if (rec.getChangeRecordNull() != null) {
                                        //No action required

                                }
                                if (rec.getChangeRecordCorrection() != null) {
                                        //TODO

                                }
                                if (rec.getChangeRecordConditionFailed() != null) {
                                        //TODO

                                }
                                tx.commit();
                                
                        } catch (Exception drfm) {
                                logger.warn("Error persisting change record!", drfm);
                                StringWriter sw = new StringWriter();
                                JAXB.marshal(rec, sw);
                                logger.warn("This is the record that failed to persist: " + sw.toString());
                        } finally {
                                if (tx.isActive()) {
                                        tx.rollback();
                                }
                                em.close();
                        }
                }
                
                private HighWaterMarkVectorType getLastChangeRecordFrom(String notifyingNode) {
                        HighWaterMarkVectorType ret = new HighWaterMarkVectorType();
                        ChangeRecordIDType cid = new ChangeRecordIDType();
                        cid.setNodeID(notifyingNode);
                        cid.setOriginatingUSN(0L);
                        EntityManager em = PersistenceManager.getEntityManager();
                        EntityTransaction tx = em.getTransaction();
                        try {
                                tx.begin();
                                Long id = 0L;
                                try {
                                        cid.setOriginatingUSN((Long) em.createQuery("select e.originatingUSN from ChangeRecord e where e.nodeID = :node order by e.originatingUSN desc").setParameter("node", notifyingNode).setMaxResults(1).getSingleResult());
                                } catch (Exception ex) {
                                        logger.info(ex);
                                }
                                
                                tx.rollback();
                                
                        } catch (Exception drfm) {
                                logger.warn("error caught fetching newest record from node " + notifyingNode, drfm);
                        } finally {
                                if (tx.isActive()) {
                                        tx.rollback();
                                }
                                em.close();
                        }
                        
                        ret.getHighWaterMark().add(cid);
                        
                        return ret;
                }
        }

        // private void ValidateDontChangeMyRecordsAtAnotherNode(String )
        private void ValidateNodeIdMatches(String newNodeId, String currentOwningNode) throws Exception {
                if (newNodeId == null || currentOwningNode == null) {
                        throw new Exception("either the local node ID is null or the inbound replication data's node id is null");
                }
                //only time this is allowed is custody transfer
                if (!newNodeId.equals(currentOwningNode)) {
                        //throw new Exception("node id mismatch!");
                        logger.info("AUDIT, custory transfer from node, " + currentOwningNode + " to " + newNodeId);
                }

                //if i already have a record and "own it" and the remote node has a record with the same key, reject the update
                //1.5.8 
                /**
                 * Each node has custody of a portion of the aggregate data
                 * managed by the registry of which it is a part. Each datum is
                 * by definition in the custody of exactly one such node. A
                 * datum in this context can be a businessEntity, a
                 * businessService, a bindingTemplate, a tModel, or a
                 * publisherAssertion. Changes to a datum in the registry MUST
                 * originate at the node which is the custodian of the datum.
                 * The registry defines the policy for data custody and, if
                 * allowed, the custodian node for a given datum can be changed;
                 * such custody transfer processes are discussed in Section 5.4
                 * Custody and Ownership Transfer API.
                 */
                //so someone else attempted to update one of my records, reject it
                if (newNodeId.equals(node)) {
                        //throw new Exception("node id mismatch! this node already has a record for key " + newDataOperationalInfo.getEntityKey() + " and I'm the authority for it.");
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
                        StringBuilder sql = new StringBuilder();
                        sql.append("select c from ReplicationConfiguration c order by c.serialNumber desc");
                        sql.toString();
                        Query qry = em.createQuery(sql.toString());
                        qry.setMaxResults(1);
                        
                        org.apache.juddi.model.ReplicationConfiguration resultList = (org.apache.juddi.model.ReplicationConfiguration) qry.getSingleResult();
                        for (Operator o : resultList.getOperator()) {
                                if (o.getOperatorNodeID().equalsIgnoreCase(node)) {
                                        ((BindingProvider) replicationClient).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, o.getSoapReplicationURL());
                                        cache.put(node, replicationClient);
                                        return replicationClient;
                                }
                        }
                        tx.rollback();
                        
                } catch (Exception ex) {
                        logger.fatal("Node not found!" + node, ex);
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
                //em.close();
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
        
        @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
        @WebResult(name = "changeRecords", targetNamespace = "urn:uddi-org:repl_v3", partName = "body")
        // @WebMethod(operationName = "get_changeRecords", action = "get_changeRecords")
        @Override
        public org.uddi.repl_v3.ChangeRecords getChangeRecords(
                @WebParam(partName = "body", name = "get_changeRecords", targetNamespace = "urn:uddi-org:repl_v3") org.uddi.repl_v3.GetChangeRecords body
        ) throws DispositionReportFaultMessage, RemoteException {
                long startTime = System.currentTimeMillis();
                String requestingNode = body.getRequestingNode();
                HighWaterMarkVectorType changesAlreadySeen = body.getChangesAlreadySeen();
                BigInteger responseLimitCount = body.getResponseLimitCount();
                HighWaterMarkVectorType responseLimitVector = body.getResponseLimitVector();
                
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
                        Long firstrecord = 0L;
                        Long lastrecord = null;
                        
                        if (changesAlreadySeen != null) {
                                //this is basically a lower limit (i.e. the newest record that was processed by the requestor
                                //therefore we want the oldest record stored locally to return to the requestor for processing
                                for (int i = 0; i < changesAlreadySeen.getHighWaterMark().size(); i++) {
                                        if (changesAlreadySeen.getHighWaterMark().get(i).getNodeID().equals(node)) {
                                                firstrecord = changesAlreadySeen.getHighWaterMark().get(i).getOriginatingUSN();
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
                        
                        logger.info("Query db for replication changes, lower index is " + (firstrecord) + " last index " + lastrecord + " record limit " + maxrecords);
                        Query createQuery = null;
                        /* 
                         //this don't work
                         if (lastrecord != null) {
                         createQuery = em.createQuery("select e from ChangeRecord e where "
                         + "((e.id > :inbound AND e.nodeID = :node AND e.id < :lastrecord) OR "
                         + "(e.originatingUSN > :inbound AND e.nodeID <> :node AND e.originatingUSN < :lastrecord)) "
                         + "order by e.id ASC");
                         createQuery.setParameter("lastrecord", lastrecord);
                         } else {
                         createQuery = em.createQuery("select e from ChangeRecord e where "
                         + "((e.id > :inbound AND e.nodeID = :node) OR "
                         + "(e.originatingUSN > :inbound AND e.nodeID <> :node)) "
                         + "order by e.id ASC");
                         }*/
                        if (lastrecord != null) {
                                createQuery = em.createQuery("select e from ChangeRecord e where "
                                        + "(e.id > :inbound AND e.nodeID = :node AND e.id < :lastrecord) "
                                        + "order by e.id ASC");
                                createQuery.setParameter("lastrecord", lastrecord);
                        } else {
                                createQuery = em.createQuery("select e from ChangeRecord e where "
                                        + "(e.id > :inbound AND e.nodeID = :node) "
                                        + "order by e.id ASC");
                        }
                        createQuery.setMaxResults(maxrecords);
                        createQuery.setParameter("inbound", firstrecord);
                        createQuery.setParameter("node", node);
                        
                        List<org.apache.juddi.model.ChangeRecord> records = (List<org.apache.juddi.model.ChangeRecord>) createQuery.getResultList();
                        logger.info(records.size() + " CR records returned from query");
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
                logger.info("Change records returned for " + requestingNode + ": " + ret.size());
                //JAXB.marshal(ret, System.out);
                ChangeRecords x = new ChangeRecords();
                x.getChangeRecord().addAll(ret);
                //JAXB.marshal(x, System.out);
                return x;
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
                HashMap<String, Long> map = new HashMap<String, Long>();
                try {
                        tx.begin();
                        if (FetchEdges != null) {
                                Iterator<String> it = FetchEdges.getCommunicationGraph().getNode().iterator();
                                while (it.hasNext()) {
                                        String nextNode = it.next();
                                        if (!nextNode.equals(node)) {
                                                if (!map.containsKey(nextNode)) {
                                                        Long id = 0L;
                                                        try {
                                                                id = (Long) em.createQuery("select e.originatingUSN from ChangeRecord e where e.nodeID = :node order by e.originatingUSN desc").setParameter("node", nextNode).setMaxResults(1).getSingleResult();
                                                        } catch (Exception ex) {
                                                                logger.debug(ex);
                                                        }
                                                        if (id == null) {
                                                                id = 0L;
                                                                //per the spec
                                                        }
                                                        map.put(nextNode, id);
                                                        
                                                }
                                        }
                                }
                        }
                        //dont forget this node
                        Long id = (Long) em.createQuery("select (e.id) from ChangeRecord e where e.nodeID = :node  order by e.id desc")
                                .setParameter("node", node).setMaxResults(1).getSingleResult();
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
                
                Iterator<Map.Entry<String, Long>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                        Map.Entry<String, Long> next = iterator.next();
                        ret.add(new ChangeRecordIDType(next.getKey(), next.getValue()));
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

                //some other node just told us there's new records available, call
                //getChangeRecords from the remote node asynch
                new ValidateReplication(null).validateNotifyChangeRecordsAvailable(body, ctx);
                
                logger.info(body.getNotifyingNode() + " just told me that there are change records available, enqueuing...size is " + queue.size());
                if (!queue.contains(body.getNotifyingNode())) {
                        queue.add(body.getNotifyingNode());
                }
                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ReplicationQuery.NOTIFY_CHANGERECORDSAVAILABLE,
                        QueryStatus.SUCCESS, procTime);
        }
        private static Queue<String> queue = null;

        /**
         * transfers custody of an entity from node1/user1 to node2/user2
         *
         * assume this node is node 2.
         *
         * user1 on node1 requests a transfer token. node 1 issues the token.
         *
         * user1 now has a transfer token for their stuff user now takes the
         * token to node 2 and calls transferEntities
         * <img src="http://www.uddi.org/pubs/uddi-v3.0.2-20041019_files/image086.gif">
         *
         * @param body
         * @throws DispositionReportFaultMessage
         */
        @Override
        public void transferCustody(TransferCustody body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                logger.info("Inbound transfer request (via replication api, node to node");
                try {
                        tx.begin();
                //*this node is transfering data to another node
                        //ValidateReplication.unsupportedAPICall();
                        //a remote node just told me to give up control of some of my entities

                        //EntityTransaction tx = em.getTransaction();
                        //confirm i have a replication config
                        boolean ok = false;
                        ReplicationConfiguration FetchEdges = ReplicationNotifier.FetchEdges();
                        if (FetchEdges != null) {
                                for (int i = 0; i < FetchEdges.getOperator().size(); i++) {
                                        //confirm that the destination node is in the replication config
                                        if (FetchEdges.getOperator().get(i).getOperatorNodeID().equals(body.getTransferOperationalInfo().getNodeID())) {
                                                ok = true;
                                                break;
                                        }
                                }
                        }
                        if (!ok) {
                                throw new TransferNotAllowedException(new ErrorMessage("E_transferNotAllowedUnknownNode"));
                        }
                        
                        new ValidateReplication(null).validateTransfer(em, body);
                        
                        TransferEntities te = new TransferEntities();
                        te.setKeyBag(body.getKeyBag());
                        te.setTransferToken(body.getTransferToken());
                        te.setAuthInfo(null);
                        //make the change
                        //enqueue in replication notifier
                        //discard the token
                        logger.debug("request validated, processing transfer");
                        List<ChangeRecord> executeTransfer = new UDDICustodyTransferImpl().executeTransfer(te, em, body.getTransferOperationalInfo().getAuthorizedName(), body.getTransferOperationalInfo().getNodeID());
                        
                        for (ChangeRecord c : executeTransfer) {
                                try {
                                        c.setChangeID(new ChangeRecordIDType());
                                        c.getChangeID().setNodeID(node);
                                        c.getChangeID().setOriginatingUSN(null);
                                        ReplicationNotifier.Enqueue(MappingApiToModel.mapChangeRecord(c));
                                } catch (UnsupportedEncodingException ex) {
                                        logger.error("", ex);
                                }
                        }
                        /**
                         * The custodial node must verify that it has granted
                         * permission to transfer the entities identified and
                         * that this permission is still valid. This operation
                         * is comprised of two steps:
                         *
                         * 1. Verification that the transferToken was issued by
                         * it, that it has not expired, that it represents the
                         * authority to transfer no more and no less than those
                         * entities identified by the businessKey and tModelKey
                         * elements and that all these entities are still valid
                         * and not yet transferred. The transferToken is
                         * invalidated if any of these conditions are not met.
                         *
                         * 2. If the conditions above are met, the custodial
                         * node will prevent any further changes to the entities
                         * identified by the businessKey and tModelKey elements
                         * identified. The entity will remain in this state
                         * until the replication stream indicates it has been
                         * successfully processed via the replication stream.
                         * Upon successful verification of the custody transfer
                         * request by the custodial node, an empty message is
                         * returned by it indicating the success of the request
                         * and acknowledging the custody transfer. Following the
                         * issue of the empty message, the custodial node will
                         * submit into the replication stream a
                         * changeRecordNewData providing in the operationalInfo,
                         * the nodeID accepting custody of the datum and the
                         * authorizedName of the publisher accepting ownership.
                         * The acknowledgmentRequested attribute of this change
                         * record MUST be set to "true".
                         *
                         *
                         *
                         * Finally, the custodial node invalidates the
                         * transferToken in order to prevent additional calls of
                         * the transfer_entities API.
                         */
                        tx.commit();
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(ReplicationQuery.TRANSFER_CUSTODY,
                                QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage d) {
                        logger.error("Unable to process node to node custody transfer ", d);
                        throw d;
                } finally {
                        if (em != null && em.isOpen()) {
                                em.close();
                        }
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                }
        }
        
}
