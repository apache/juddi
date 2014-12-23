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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.CustodyTransferQuery;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.BusinessEntity;
import org.apache.juddi.model.BusinessService;
import org.apache.juddi.model.Operator;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.model.TransferTokenKey;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.replication.ReplicationNotifier;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.validation.ValidateCustodyTransfer;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.custody_v3.TransferOperationalInfo;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.ChangeRecordNewData;
import org.uddi.repl_v3.TransferCustody;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIReplicationPortType;

/**
 * This implements the UDDI v3 Custody Transfer API web service
 *
 */
@WebService(serviceName = "UDDICustodyTransferService",
        endpointInterface = "org.uddi.v3_service.UDDICustodyTransferPortType",
        targetNamespace = "urn:uddi-org:v3_service")
public class UDDICustodyTransferImpl extends AuthenticatedService implements UDDICustodyTransferPortType {

        public static final String TRANSFER_TOKEN_PREFIX = "transfertoken:";
        public static final int DEFAULT_TRANSFEREXPIRATION_DAYS = 3;

        private static Log logger = LogFactory.getLog(UDDICustodyTransferImpl.class);

        private static DatatypeFactory df = null;
        private UDDIServiceCounter serviceCounter;

        public UDDICustodyTransferImpl() {
                super();
                serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(this.getClass());
                if (df == null) {
                        try {
                                df = DatatypeFactory.newInstance();
                        } catch (DatatypeConfigurationException ex) {
                                Logger.getLogger(UDDICustodyTransferImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void discardTransferToken(DiscardTransferToken body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidateCustodyTransfer(publisher).validateDiscardTransferToken(em, body);

                        org.uddi.custody_v3.TransferToken apiTransferToken = body.getTransferToken();
                        if (apiTransferToken != null) {
                                String transferTokenId = new String(apiTransferToken.getOpaqueToken());
                                org.apache.juddi.model.TransferToken modelTransferToken = em.find(org.apache.juddi.model.TransferToken.class, transferTokenId);
                                if (modelTransferToken != null) {
                                        em.remove(modelTransferToken);
                                }
                        }

                        KeyBag keyBag = body.getKeyBag();
                        if (keyBag != null) {
                                List<String> keyList = keyBag.getKey();
                                Vector<DynamicQuery.Parameter> params = new Vector<DynamicQuery.Parameter>(0);
                                for (String key : keyList) {
                                        // Creating parameters for key-checking query
                                        DynamicQuery.Parameter param = new DynamicQuery.Parameter("UPPER(ttk.entityKey)",
                                                key.toUpperCase(),
                                                DynamicQuery.PREDICATE_EQUALS);

                                        params.add(param);
                                }

                                // Find the associated transfer tokens and remove them.
                                DynamicQuery getTokensQry = new DynamicQuery();
                                getTokensQry.append("select distinct ttk.transferToken from TransferTokenKey ttk").pad();
                                getTokensQry.WHERE().pad().appendGroupedOr(params.toArray(new DynamicQuery.Parameter[0]));

                                Query qry = getTokensQry.buildJPAQuery(em);
                                List<org.apache.juddi.model.TransferToken> tokensToDelete = qry.getResultList();
                                if (tokensToDelete != null && tokensToDelete.size() > 0) {
                                        for (org.apache.juddi.model.TransferToken tt : tokensToDelete) {
                                                em.remove(tt);
                                        }
                                }
                        }

                        tx.commit();
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(CustodyTransferQuery.DISCARD_TRANSFERTOKEN,
                                QueryStatus.SUCCESS, procTime);

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        @Override
        public void getTransferToken(String authInfo, KeyBag keyBag,
                Holder<String> nodeID, Holder<XMLGregorianCalendar> expirationTime,
                Holder<byte[]> opaqueToken) throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);

                        new ValidateCustodyTransfer(publisher).validateGetTransferToken(em, keyBag);

                        int transferExpirationDays = DEFAULT_TRANSFEREXPIRATION_DAYS;
                        try {
                                transferExpirationDays = AppConfig.getConfiguration().getInt(Property.JUDDI_TRANSFER_EXPIRATION_DAYS);
                                // For output
                                nodeID.value = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                        } catch (ConfigurationException ce) {
                                throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval"));
                        }

                        String transferKey = TRANSFER_TOKEN_PREFIX + UUID.randomUUID();
                        org.apache.juddi.model.TransferToken transferToken = new org.apache.juddi.model.TransferToken();
                        transferToken.setTransferToken(transferKey);
                        // For output
                        opaqueToken.value = transferKey.getBytes();

                        GregorianCalendar gc = new GregorianCalendar();
                        gc.add(GregorianCalendar.DAY_OF_MONTH, transferExpirationDays);

                        transferToken.setExpirationDate(gc.getTime());

                        try {
                                DatatypeFactory df = DatatypeFactory.newInstance();
                                // For output
                                expirationTime.value = df.newXMLGregorianCalendar(gc);
                        } catch (DatatypeConfigurationException ce) {
                                throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
                        }

                        List<String> keyList = keyBag.getKey();
                        for (String key : keyList) {
                                TransferTokenKey tokenKey = new TransferTokenKey(transferToken, key);
                                transferToken.getTransferKeys().add(tokenKey);
                        }

                        em.persist(transferToken);

                        tx.commit();

                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(CustodyTransferQuery.GET_TRANSFERTOKEN,
                                QueryStatus.SUCCESS, procTime);

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        @Override
        public void transferEntities(TransferEntities body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                 List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        ValidateCustodyTransfer verifier = new ValidateCustodyTransfer(publisher);

                        //if the destination transfer is to a different node, 
                        if (!verifier.validateTransferEntities(em, body)) {
                                //i don't own these entities, so tell the ower to transfer to me.

                                //look up the replication config endpoint for that node and trigger the transfer, then return
                                //ok this is a node to node transfer, first up a replication client to the destination node
                                String sourceNode = null;
                                try {
                                        KeyBag keyBag = body.getKeyBag();
                                        List<String> keyList = keyBag.getKey();
                                        for (String key : keyList) {
                                                UddiEntity uddiEntity = em.find(UddiEntity.class, key);
                                                if (sourceNode != null
                                                        && !uddiEntity.getNodeId().equals(sourceNode)) {
                                                        throw new Exception("All entities to be transfer must be transfered to the same source and destination node");
                                                }
                                                sourceNode = uddiEntity.getNodeId();
                                        }

                                        UDDIReplicationPortType replicationClient = getReplicationClient(sourceNode);
                                        if (replicationClient == null) {
                                                throw new Exception("Unknown node. is it in the replication graph?" + sourceNode);
                                        }
                                        TransferCustody transferCustody = new TransferCustody();
                                        transferCustody.setTransferToken(body.getTransferToken());
                                        transferCustody.setKeyBag(body.getKeyBag());
                                        transferCustody.setTransferOperationalInfo(new TransferOperationalInfo());
                                        transferCustody.getTransferOperationalInfo().setAuthorizedName(publisher.getAuthorizedName());
                                        transferCustody.getTransferOperationalInfo().setNodeID(node);

                                        replicationClient.transferCustody(transferCustody);
                                } catch (Exception ex) {
                                        logger.error("Unable to transfer entities to node " + sourceNode + ".", ex);
                                }
                                //and trigger the transfer
                        } else {

                                // Once validated, the ownership transfer is as simple as switching the publisher
                                KeyBag keyBag = body.getKeyBag();
                                List<String> keyList = keyBag.getKey();
                                //used for the change journal
                               
                                for (String key : keyList) {
                                        UddiEntity uddiEntity = em.find(UddiEntity.class, key);
                                        uddiEntity.setAuthorizedName(publisher.getAuthorizedName());
                                        Date now = new Date();
                                        uddiEntity.setModified(now);
                                        uddiEntity.setModifiedIncludingChildren(now);
                                        
                                        if (uddiEntity instanceof BusinessEntity) {
                                                BusinessEntity be = (BusinessEntity) uddiEntity;

                                                List<BusinessService> bsList = be.getBusinessServices();
                                                for (BusinessService bs : bsList) {
                                                        bs.setAuthorizedName(publisher.getAuthorizedName());
                                                        bs.setModified(now);
                                                        bs.setModifiedIncludingChildren(now);

                                                        List<BindingTemplate> btList = bs.getBindingTemplates();
                                                        for (BindingTemplate bt : btList) {
                                                                bt.setAuthorizedName(publisher.getAuthorizedName());
                                                                bt.setModified(now);
                                                                bt.setModifiedIncludingChildren(now);
                                                       
                                                        }
                                                }
                                        }
                                        ChangeRecord cr = new ChangeRecord();
                                        cr.setChangeRecordNewData(new ChangeRecordNewData());
                                        if (uddiEntity instanceof BusinessEntity) {
                                                cr.getChangeRecordNewData().setBusinessEntity(new org.uddi.api_v3.BusinessEntity());
                                                MappingModelToApi.mapBusinessEntity((BusinessEntity) uddiEntity, cr.getChangeRecordNewData().getBusinessEntity());
                                        }
                                        if (uddiEntity instanceof Tmodel) {
                                                cr.getChangeRecordNewData().setTModel(new org.uddi.api_v3.TModel());
                                                MappingModelToApi.mapTModel((Tmodel) uddiEntity, cr.getChangeRecordNewData().getTModel());
                                        }
                                        changes.add(cr);
                                        em.persist(uddiEntity);

                                }

                                // After transfer is finished, the token can be removed
                                org.uddi.custody_v3.TransferToken apiTransferToken = body.getTransferToken();
                                String transferTokenId = new String(apiTransferToken.getOpaqueToken());
                                org.apache.juddi.model.TransferToken modelTransferToken = em.find(org.apache.juddi.model.TransferToken.class, transferTokenId);
                                em.remove(modelTransferToken);
                        }
                        tx.commit();
                        //we need to do something for replication purposes here
                        //enqueue notifications and storage of the changed records
                        for (ChangeRecord c: changes)
                                 try {
                                         c.setChangeID(new ChangeRecordIDType());
                                         c.getChangeID().setNodeID(node);
                                         c.getChangeID().setOriginatingUSN(null);
                                         ReplicationNotifier.Enqueue(MappingApiToModel.mapChangeRecord(c));
                                } catch (UnsupportedEncodingException ex) {
                                        Logger.getLogger(UDDICustodyTransferImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(CustodyTransferQuery.TRANSFER_ENTITIES,
                                QueryStatus.SUCCESS, procTime);

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

        }

        private synchronized UDDIReplicationPortType getReplicationClient(String node) {

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

                                        return replicationClient;
                                }
                        }
                        tx.rollback();

                } catch (Exception ex) {
                        logger.fatal("Node not found (or there isn't a replication config)!" + node, ex);
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
                //em.close();
                return null;

        }
}
