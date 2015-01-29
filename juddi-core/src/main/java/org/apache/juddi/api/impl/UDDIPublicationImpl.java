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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.Holder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.PublicationQuery;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.BusinessEntity;
import org.apache.juddi.model.BusinessService;
import org.apache.juddi.model.ChangeRecord;
import org.apache.juddi.model.Signature;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.FetchBusinessEntitiesQuery;
import org.apache.juddi.query.FetchTModelsQuery;
import org.apache.juddi.query.FindBusinessByPublisherQuery;
import org.apache.juddi.query.FindPublisherAssertionByBusinessQuery;
import org.apache.juddi.query.FindTModelByPublisherQuery;
import org.apache.juddi.query.TModelQuery;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.replication.ReplicationNotifier;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidValueException;
import org.apache.juddi.validation.ValidatePublish;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.InfoSelection;
import org.uddi.api_v3.ListDescription;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.repl_v3.ChangeRecordDelete;
import org.uddi.repl_v3.ChangeRecordDeleteAssertion;
import org.uddi.repl_v3.ChangeRecordHide;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.ChangeRecordNewData;
import org.uddi.repl_v3.ChangeRecordNewDataConditional;
import org.uddi.repl_v3.ChangeRecordPublisherAssertion;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIPublicationPortType;

/**
 * This class implements the UDDI Publication Service
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a> (and many others)
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a> added support for
 * replication and several bug fixes
 */
@WebService(serviceName = "UDDIPublicationService",
        endpointInterface = "org.uddi.v3_service.UDDIPublicationPortType",
        targetNamespace = "urn:uddi-org:v3_service")
public class UDDIPublicationImpl extends AuthenticatedService implements UDDIPublicationPortType {

        private static Log log = LogFactory.getLog(UDDIInquiryImpl.class);
        private UDDIServiceCounter serviceCounter;

        private static DatatypeFactory df = null;

        public UDDIPublicationImpl() {
                super();
                serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(UDDIPublicationImpl.class);
                if (df == null) {
                        try {
                                df = DatatypeFactory.newInstance();
                        } catch (DatatypeConfigurationException ex) {
                                logger.fatal(ex);
                        }
                }
        }

        @Override
        public void addPublisherAssertions(AddPublisherAssertions body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateAddPublisherAssertions(em, body);

                        List<org.uddi.api_v3.PublisherAssertion> apiPubAssertionList = body.getPublisherAssertion();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (org.uddi.api_v3.PublisherAssertion apiPubAssertion : apiPubAssertionList) {

                                org.apache.juddi.model.PublisherAssertion modelPubAssertion = new org.apache.juddi.model.PublisherAssertion();

                                MappingApiToModel.mapPublisherAssertion(apiPubAssertion, modelPubAssertion);

                                org.apache.juddi.model.PublisherAssertion existingPubAssertion = em.find(modelPubAssertion.getClass(), modelPubAssertion.getId());
                                boolean persistNewAssertion = true;
                                if (existingPubAssertion != null) {
                                        if (modelPubAssertion.getTmodelKey().equalsIgnoreCase(existingPubAssertion.getTmodelKey())
                                                && modelPubAssertion.getKeyName().equalsIgnoreCase(existingPubAssertion.getKeyName())
                                                && modelPubAssertion.getKeyValue().equalsIgnoreCase(existingPubAssertion.getKeyValue())) {
                                                // This pub assertion is already been "asserted".  Simply need to set the "check" value on the existing (and persistent) assertion
                                                if (publisher.isOwner(existingPubAssertion.getBusinessEntityByFromKey())) {
                                                        existingPubAssertion.setFromCheck("true");
                                                }
                                                if (publisher.isOwner(existingPubAssertion.getBusinessEntityByToKey())) {
                                                        existingPubAssertion.setToCheck("true");
                                                }
                                                //it's also possible that the signatures have changed
                                                removeExistingPublisherAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(), existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), em);
                                                savePushliserAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(), existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), modelPubAssertion.getSignatures(), em);

                                                em.merge(existingPubAssertion);
                                                persistNewAssertion = false;
                                                changes.add(getChangeRecord_deletePublisherAssertion(apiPubAssertion, node, existingPubAssertion.getToCheck().equalsIgnoreCase("false"), existingPubAssertion.getFromCheck().equalsIgnoreCase("false"), System.currentTimeMillis()));
                                        } else {
                                                // Otherwise, it is a new relationship between these entities.  Remove the old one so the new one can be added.
                                                // TODO: the model only seems to allow one assertion per two business (primary key is fromKey and toKey). Spec seems to imply as 
                                                // many relationships as desired (the differentiator would be the keyedRef values).
                                                removeExistingPublisherAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(), existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), em);
                                                em.remove(existingPubAssertion);
                                                changes.add(getChangeRecord_deletePublisherAssertion(apiPubAssertion, node, true, true, System.currentTimeMillis()));
                                        }
                                }

                                if (persistNewAssertion) {
                                        org.apache.juddi.model.BusinessEntity beFrom = em.find(org.apache.juddi.model.BusinessEntity.class, modelPubAssertion.getId().getFromKey());
                                        org.apache.juddi.model.BusinessEntity beTo = em.find(org.apache.juddi.model.BusinessEntity.class, modelPubAssertion.getId().getToKey());
                                        modelPubAssertion.setBusinessEntityByFromKey(beFrom);
                                        modelPubAssertion.setBusinessEntityByToKey(beTo);

                                        modelPubAssertion.setFromCheck("false");
                                        modelPubAssertion.setToCheck("false");

                                        if (publisher.isOwner(modelPubAssertion.getBusinessEntityByFromKey())) {
                                                modelPubAssertion.setFromCheck("true");
                                        }
                                        if (publisher.isOwner(modelPubAssertion.getBusinessEntityByToKey())) {
                                                modelPubAssertion.setToCheck("true");
                                        }
                                        modelPubAssertion.setModified(new Date());
                                         savePushliserAssertionSignatures(modelPubAssertion.getBusinessEntityByFromKey().getEntityKey(), modelPubAssertion.getBusinessEntityByToKey().getEntityKey(), modelPubAssertion.getSignatures(), em);

                                        em.persist(modelPubAssertion);

                                        changes.add(getChangeRecord_NewAssertion(apiPubAssertion, modelPubAssertion, node));

                                }

                        }

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }

                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.ADD_PUBLISHERASSERTIONS,
                                QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.ADD_PUBLISHERASSERTIONS, QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        public void deleteBinding(DeleteBinding body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateDeleteBinding(em, body);

                        List<String> entityKeyList = body.getBindingKey();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (String entityKey : entityKeyList) {
                                deleteBinding(entityKey, em);
                                changes.add(getChangeRecord_deleteBinding(entityKey, node));
                        }
                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }

                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_BINDING,
                                QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_BINDING, QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * deletes the referenced object, assuming authorization rules are
         * already processed and there is already an open transaction
         *
         * @param entityKey
         * @param em
         * @throws DispositionReportFaultMessage
         */
        protected void deleteBinding(String entityKey, EntityManager em) throws DispositionReportFaultMessage {

                Object obj = em.find(org.apache.juddi.model.BindingTemplate.class, entityKey);

                ((org.apache.juddi.model.BindingTemplate) obj).getBusinessService().setModifiedIncludingChildren(new Date());
                // JUDDI-421:  now the businessEntity parent will have it's modifiedIncludingChildren set
                ((org.apache.juddi.model.BindingTemplate) obj).getBusinessService().getBusinessEntity().setModifiedIncludingChildren(new Date());

                em.remove(obj);

        }

        public void deleteBusiness(DeleteBusiness body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateDeleteBusiness(em, body);

                        List<String> entityKeyList = body.getBusinessKey();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (String entityKey : entityKeyList) {
                                deleteBusiness(entityKey, em);
                                changes.add(getChangeRecord_deleteBusiness(entityKey, node));
                        }

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_BUSINESS, QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_BUSINESS, QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * deletes the referenced object, assuming authorization rules are
         * already processed and there is already an open transaction
         *
         * @param entityKey
         * @param em
         * @throws DispositionReportFaultMessage
         */
        protected void deleteBusiness(String key, EntityManager em) throws DispositionReportFaultMessage {
                Object obj = em.find(org.apache.juddi.model.BusinessEntity.class, key);
                em.remove(obj);
        }

        public void deletePublisherAssertions(DeletePublisherAssertions body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateDeletePublisherAssertions(em, body);

                        List<org.uddi.api_v3.PublisherAssertion> entityList = body.getPublisherAssertion();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (org.uddi.api_v3.PublisherAssertion entity : entityList) {
                                org.apache.juddi.model.PublisherAssertion modelPubAssertion = new org.apache.juddi.model.PublisherAssertion();

                                MappingApiToModel.mapPublisherAssertion(entity, modelPubAssertion);

                                org.apache.juddi.model.PublisherAssertion existingPubAssertion = em.find(org.apache.juddi.model.PublisherAssertion.class,
                                        modelPubAssertion.getId());
                                if (existingPubAssertion == null) {
                                        throw new InvalidValueException(new ErrorMessage("E_assertionNotFound"));
                                }

                                boolean fromkey = publisher.isOwner(em.find(BusinessEntity.class, entity.getFromKey()));
                                boolean tokey = publisher.isOwner(em.find(BusinessEntity.class, entity.getToKey()));
                                if (fromkey) {
                                        existingPubAssertion.setFromCheck("false");
                                }
                                if (tokey) {
                                        existingPubAssertion.setToCheck("false");
                                }
                                if ("false".equalsIgnoreCase(existingPubAssertion.getToCheck())
                                        && "false".equalsIgnoreCase(existingPubAssertion.getFromCheck())) {
                                        logger.info("Publisher assertion updated database via replication");
                                        removeExistingPublisherAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(), existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), em);
                                        em.remove(existingPubAssertion);
                                } else {
                                        existingPubAssertion.setModified(new Date());
                                        logger.info("Publisher assertion updated database via replication");
                                        removeExistingPublisherAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(), existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), em);
                                        savePushliserAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(),
                                                existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), modelPubAssertion.getSignatures(), em);
                                        em.persist(existingPubAssertion);
                                }

                                changes.add(getChangeRecord_deletePublisherAssertion(entity, node, tokey, fromkey, existingPubAssertion.getModified().getTime()));
                        }

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_PUBLISHERASSERTIONS,
                                QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_PUBLISHERASSERTIONS, QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * deletes the referenced object, assuming authorization rules are
         * already processed and there is already an open transaction. this is
         * primarily used to support replication calls, i.e. another node just
         * changed a PA record and let us know
         *
         * @param entityKey
         * @param em
         * @throws DispositionReportFaultMessage
         */
        protected void deletePublisherAssertion(org.uddi.repl_v3.ChangeRecordDeleteAssertion entity, EntityManager em) throws DispositionReportFaultMessage {

                org.apache.juddi.model.PublisherAssertion modelPubAssertion = new org.apache.juddi.model.PublisherAssertion();

                MappingApiToModel.mapPublisherAssertion(entity.getPublisherAssertion(), modelPubAssertion);

                org.apache.juddi.model.PublisherAssertion existingPubAssertion = em.find(org.apache.juddi.model.PublisherAssertion.class,
                        modelPubAssertion.getId());

                if (existingPubAssertion == null) {
                        throw new FatalErrorException(new ErrorMessage("E_assertionNotFound"));
                }
                boolean fromkey = entity.isFromBusinessCheck();// publisher.isOwner(em.find(BusinessEntity.class, entity.getFromKey()));
                boolean tokey = entity.isToBusinessCheck();//  publisher.isOwner(em.find(BusinessEntity.class, entity.getToKey()));
                if (fromkey) {
                        existingPubAssertion.setFromCheck("false");
                }
                if (tokey) {
                        existingPubAssertion.setToCheck("false");
                }
                if ("false".equalsIgnoreCase(existingPubAssertion.getToCheck())
                        && "false".equalsIgnoreCase(existingPubAssertion.getFromCheck())) {
                        logger.info("Deletion of publisher assertion from database via replication");
                        removeExistingPublisherAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(), existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), em);
                        em.remove(existingPubAssertion);
                } else {
                        existingPubAssertion.setModified(new Date());
                        logger.info("Publisher assertion updated database via replication");
                        removeExistingPublisherAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(), existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), em);
                        savePushliserAssertionSignatures(existingPubAssertion.getBusinessEntityByFromKey().getEntityKey(),
                                existingPubAssertion.getBusinessEntityByToKey().getEntityKey(), modelPubAssertion.getSignatures(), em);
                        em.persist(existingPubAssertion);
                }

        }

        public void deleteService(DeleteService body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateDeleteService(em, body);

                        List<String> entityKeyList = body.getServiceKey();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (String entityKey : entityKeyList) {
                                deleteService(entityKey, em);
                                changes.add(getChangeRecord_deleteService(entityKey, node));
                        }

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_SERVICE,
                                QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_SERVICE, QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * deletes the referenced object, assuming authorization rules are
         * already processed and there is already an open transaction
         *
         * @param entityKey
         * @param em
         * @throws DispositionReportFaultMessage
         */
        protected void deleteService(String key, EntityManager em) throws DispositionReportFaultMessage {
                Object obj = em.find(org.apache.juddi.model.BusinessService.class, key);
                //((org.apache.juddi.model.BusinessService) obj).getBusinessEntity().setModifiedIncludingChildren(new Date());
                if (obj != null) {
                        em.remove(obj);
                } else {
                        logger.warn("Unable to remove service with the key '" + key + "', it doesn't exist in the database");
                }
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void deleteTModel(DeleteTModel body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateDeleteTModel(em, body);

                        // tModels are only lazily deleted!
                        List<String> entityKeyList = body.getTModelKey();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (String entityKey : entityKeyList) {
                                deleteTModel(entityKey, em);
                                changes.add(getChangeRecord_deleteTModelHide(entityKey, node));
                        }

                        tx.commit();

                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_TMODEL, QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.DELETE_TMODEL, QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * deletes the referenced object, assuming authorization rules are
         * already processed and there is already an open transaction
         *
         * @param entityKey
         * @param em
         * @throws DispositionReportFaultMessage
         */
        protected void deleteTModel(String key, EntityManager em) {
                Object obj = em.find(org.apache.juddi.model.Tmodel.class, key);
                ((org.apache.juddi.model.Tmodel) obj).setDeleted(true);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public List<AssertionStatusItem> getAssertionStatusReport(String authInfo,
                CompletionStatus completionStatus)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);

                        List<org.uddi.api_v3.AssertionStatusItem> result = PublicationHelper.getAssertionStatusItemList(publisher, completionStatus, em);

                        tx.commit();
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.GET_ASSERTIONSTATUSREPORT,
                                QueryStatus.SUCCESS, procTime);

                        return result;
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.GET_ASSERTIONSTATUSREPORT, QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        public List<PublisherAssertion> getPublisherAssertions(String authInfo)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);

                        List<org.uddi.api_v3.PublisherAssertion> result = new ArrayList<org.uddi.api_v3.PublisherAssertion>(0);

                        List<?> businessKeysFound = null;
                        businessKeysFound = FindBusinessByPublisherQuery.select(em, null, publisher, businessKeysFound);

                        List<org.apache.juddi.model.PublisherAssertion> pubAssertionList = FindPublisherAssertionByBusinessQuery.select(em, businessKeysFound, null);
                        if (pubAssertionList != null) {
                                for (org.apache.juddi.model.PublisherAssertion modelPubAssertion : pubAssertionList) {
                                        org.uddi.api_v3.PublisherAssertion apiPubAssertion = new org.uddi.api_v3.PublisherAssertion();

                                        MappingModelToApi.mapPublisherAssertion(modelPubAssertion, apiPubAssertion);

                                        result.add(apiPubAssertion);
                                }
                        }

                        tx.commit();
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.GET_PUBLISHERASSERTIONS,
                                QueryStatus.SUCCESS, procTime);
                        return result;
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.GET_PUBLISHERASSERTIONS,
                                QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * {@inheritdoc}
         *
         */
        public RegisteredInfo getRegisteredInfo(GetRegisteredInfo body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateRegisteredInfo(body);

                        List<?> businessKeysFound = null;
                        businessKeysFound = FindBusinessByPublisherQuery.select(em, null, publisher, businessKeysFound);

                        List<?> tmodelKeysFound = null;

                        if (body.getInfoSelection().equals(InfoSelection.HIDDEN)) {
                                tmodelKeysFound = FindTModelByPublisherQuery.select(em, null, publisher, tmodelKeysFound, new DynamicQuery.Parameter(TModelQuery.ENTITY_ALIAS + ".deleted", Boolean.TRUE, DynamicQuery.PREDICATE_EQUALS));
                        } else if (body.getInfoSelection().equals(InfoSelection.VISIBLE)) {
                                tmodelKeysFound = FindTModelByPublisherQuery.select(em, null, publisher, tmodelKeysFound, new DynamicQuery.Parameter(TModelQuery.ENTITY_ALIAS + ".deleted", Boolean.FALSE, DynamicQuery.PREDICATE_EQUALS));
                        } else {
                                tmodelKeysFound = FindTModelByPublisherQuery.select(em, null, publisher, tmodelKeysFound);
                        }

                        RegisteredInfo result = new RegisteredInfo();

                        // Sort and retrieve the final results
                        List<?> queryResults = FetchBusinessEntitiesQuery.select(em, new FindQualifiers(), businessKeysFound, null, null, null);
                        if (queryResults.size() > 0) {
                                result.setBusinessInfos(new org.uddi.api_v3.BusinessInfos());

                                for (Object item : queryResults) {
                                        org.apache.juddi.model.BusinessEntity modelBusinessEntity = (org.apache.juddi.model.BusinessEntity) item;
                                        org.uddi.api_v3.BusinessInfo apiBusinessInfo = new org.uddi.api_v3.BusinessInfo();

                                        MappingModelToApi.mapBusinessInfo(modelBusinessEntity, apiBusinessInfo);

                                        result.getBusinessInfos().getBusinessInfo().add(apiBusinessInfo);
                                }
                        }

                        // Sort and retrieve the final results
                        queryResults = FetchTModelsQuery.select(em, new FindQualifiers(), tmodelKeysFound, null, null, null);
                        if (queryResults.size() > 0) {
                                result.setTModelInfos(new org.uddi.api_v3.TModelInfos());

                                for (Object item : queryResults) {
                                        org.apache.juddi.model.Tmodel modelTModel = (org.apache.juddi.model.Tmodel) item;
                                        org.uddi.api_v3.TModelInfo apiTModelInfo = new org.uddi.api_v3.TModelInfo();

                                        MappingModelToApi.mapTModelInfo(modelTModel, apiTModelInfo);

                                        result.getTModelInfos().getTModelInfo().add(apiTModelInfo);
                                }
                        }

                        tx.commit();
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.GET_REGISTEREDINFO,
                                QueryStatus.SUCCESS, procTime);

                        return result;
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.GET_REGISTEREDINFO,
                                QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * {@inheritdoc}
         *
         */
        public BindingDetail saveBinding(SaveBinding body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
                        publisher.populateKeyGeneratorKeys(em);
                        ValidatePublish validator = new ValidatePublish(publisher);
                        validator.validateSaveBinding(em, body, null, publisher);

                        BindingDetail result = new BindingDetail();
                        result.setListDescription(new ListDescription());
                        List<org.uddi.api_v3.BindingTemplate> apiBindingTemplateList = body.getBindingTemplate();
                        List<org.apache.juddi.model.ChangeRecord> changes = new ArrayList<ChangeRecord>();

                        for (org.uddi.api_v3.BindingTemplate apiBindingTemplate : apiBindingTemplateList) {

                                org.apache.juddi.model.BindingTemplate modelBindingTemplate = new org.apache.juddi.model.BindingTemplate();

                                org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();
                                modelBusinessService.setEntityKey(apiBindingTemplate.getServiceKey());

                                MappingApiToModel.mapBindingTemplate(apiBindingTemplate, modelBindingTemplate, modelBusinessService);

                                setOperationalInfo(em, modelBindingTemplate, publisher, true);

                                em.persist(modelBindingTemplate);

                                result.getBindingTemplate().add(apiBindingTemplate);
                                result.getListDescription().setActualCount(result.getListDescription().getActualCount() + 1);
                                result.getListDescription().setIncludeCount(result.getListDescription().getIncludeCount() + 1);
                                validator.validateSaveBindingMax(em, modelBindingTemplate.getBusinessService().getEntityKey());
                                changes.add(getChangeRecord(modelBindingTemplate, apiBindingTemplate, node));
                        }

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_BINDING,
                                QueryStatus.SUCCESS, procTime);

                        return result;
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_BINDING,
                                QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * {@inheritdoc}
         *
         */
        public BusinessDetail saveBusiness(SaveBusiness body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();
                if (!body.getBusinessEntity().isEmpty()) {
                        log.debug("Inbound save business request for key " + body.getBusinessEntity().get(0).getBusinessKey());
                }
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
                        publisher.populateKeyGeneratorKeys(em);
                        ValidatePublish validator = new ValidatePublish(publisher);
                        validator.validateSaveBusiness(em, body, null, publisher);

                        BusinessDetail result = new BusinessDetail();

                        List<org.uddi.api_v3.BusinessEntity> apiBusinessEntityList = body.getBusinessEntity();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();

                        for (org.uddi.api_v3.BusinessEntity apiBusinessEntity : apiBusinessEntityList) {

                                org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();

                                MappingApiToModel.mapBusinessEntity(apiBusinessEntity, modelBusinessEntity);

                                setOperationalInfo(em, modelBusinessEntity, publisher);
                                log.debug("Saving business " + modelBusinessEntity.getEntityKey());

                                em.persist(modelBusinessEntity);
                                changes.add(getChangeRecord(modelBusinessEntity, apiBusinessEntity, node));
                                result.getBusinessEntity().add(apiBusinessEntity);
                        }

                        //check how many business this publisher owns.
                        validator.validateSaveBusinessMax(em);

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_BUSINESS,
                                QueryStatus.SUCCESS, procTime);

                        return result;
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_BUSINESS,
                                QueryStatus.FAILED, procTime);
                        throw drfm;
                } catch (Exception ex) {
                        StringWriter sw = new StringWriter();
                        if (body != null) {
                                JAXB.marshal(body, sw);
                        }
                        log.fatal("unexpected error!" + sw.toString(), ex);
                        throw new FatalErrorException(new ErrorMessage("E_fatalError", ex.getMessage()));
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * {@inheritdoc}
         *
         */
        public ServiceDetail saveService(SaveService body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
                        publisher.populateKeyGeneratorKeys(em);
                        ValidatePublish validator = new ValidatePublish(publisher);
                        validator.validateSaveService(em, body, null, publisher);

                        ServiceDetail result = new ServiceDetail();

                        List<org.uddi.api_v3.BusinessService> apiBusinessServiceList = body.getBusinessService();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (org.uddi.api_v3.BusinessService apiBusinessService : apiBusinessServiceList) {

                                org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();
                                org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();
                                modelBusinessEntity.setEntityKey(apiBusinessService.getBusinessKey());

                                MappingApiToModel.mapBusinessService(apiBusinessService, modelBusinessService, modelBusinessEntity);

                                setOperationalInfo(em, modelBusinessService, publisher, false);

                                em.persist(modelBusinessService);

                                result.getBusinessService().add(apiBusinessService);

                                validator.validateSaveServiceMax(em, modelBusinessService.getBusinessEntity().getEntityKey());
                                changes.add(getChangeRecord(modelBusinessService, apiBusinessService, node));
                        }

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_SERVICE,
                                QueryStatus.SUCCESS, procTime);

                        return result;
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_SERVICE,
                                QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * {@inheritdoc}
         *
         */
        @Override
        public TModelDetail saveTModel(SaveTModel body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
                        publisher.populateKeyGeneratorKeys(em);
                        new ValidatePublish(publisher).validateSaveTModel(em, body, null, publisher);

                        TModelDetail result = new TModelDetail();

                        List<org.uddi.api_v3.TModel> apiTModelList = body.getTModel();
                        List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                        for (org.uddi.api_v3.TModel apiTModel : apiTModelList) {

                                // Object obj=em.find( org.apache.juddi.model.Tmodel.class, apiTModel.getTModelKey());
                                //just making changes to an existing tModel, no worries
                                org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();

                                MappingApiToModel.mapTModel(apiTModel, modelTModel);

                                setOperationalInfo(em, modelTModel, publisher);

                                em.persist(modelTModel);

                                result.getTModel().add(apiTModel);
                                changes.add(getChangeRecord(modelTModel, apiTModel, node));
                                /*
                                 //TODO JUDDI-915
                                 if (obj != null) {

                                 changes.add(getChangeRecord(modelTModel, apiTModel, node));
                                 } else {
                                 //special case for replication, must setup a new data conditional change record
                                 changes.add(getChangeRecordConditional(modelTModel, apiTModel, node));
                                 }*/

                        }

                        tx.commit();
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_TMODEL,
                                QueryStatus.SUCCESS, procTime);

                        return result;
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SAVE_TMODEL,
                                QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * {@inheritdoc}
         *
         */
        @Override
        public void setPublisherAssertions(String authInfo,
                Holder<List<PublisherAssertion>> publisherAssertion)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                List<ChangeRecord> changes = new ArrayList<ChangeRecord>();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);

                        new ValidatePublish(publisher).validateSetPublisherAssertions(em, publisherAssertion);

                        List<?> businessKeysFound = null;
                        businessKeysFound = FindBusinessByPublisherQuery.select(em, null, publisher, businessKeysFound);

                        //TODO this has to be reworked to record what was deleted.
                        // First, identify all previous assertions that need to be removed
                        List<org.apache.juddi.model.PublisherAssertion> existingAssertions = FindPublisherAssertionByBusinessQuery.select(em, businessKeysFound, null);

                        logger.info(">>>> Existing assertions " + existingAssertions.size() + ", inbound set " + publisherAssertion.value.size());
                        List<org.apache.juddi.model.PublisherAssertion> deleteMe = diff(publisherAssertion.value, existingAssertions);
                        logger.info(">>>> DIFF size is " + deleteMe.size());
                        for (org.apache.juddi.model.PublisherAssertion del : deleteMe) {
                                logger.info(">>>> PROCESSING " + del.getBusinessEntityByFromKey().getEntityKey() + " " + del.getBusinessEntityByToKey().getEntityKey());
                                boolean from = false;
                                if (del.getFromCheck() != null) {
                                        del.getFromCheck().equalsIgnoreCase("true");
                                }
                                boolean to = false;
                                if (del.getToCheck() != null) {
                                        del.getToCheck().equalsIgnoreCase("true");
                                }
                                if (publisher.isOwner(del.getBusinessEntityByFromKey())) {
                                        from = false;
                                }
                                if (publisher.isOwner(del.getBusinessEntityByToKey())) {
                                        to = false;
                                }
                                PublisherAssertion api = new PublisherAssertion();
                                MappingModelToApi.mapPublisherAssertion(del, api);

                                if (!to && !from) {
                                        logger.info(">>>> DELETE ME " + del.getBusinessEntityByFromKey().getEntityKey() + " " + del.getBusinessEntityByToKey().getEntityKey());
                                        em.remove(del);
                                } else {
                                        logger.info(">>>> MERGING ME " + del.getBusinessEntityByFromKey().getEntityKey() + " " + del.getBusinessEntityByToKey().getEntityKey());
                                        del.setFromCheck(from ? "true" : "false");
                                        del.setToCheck(to ? "true" : "false");
                                        del.setModified(new Date());
                                        em.merge(del);
                                }
                                changes.add(getChangeRecord_deletePublisherAssertion(api, node, to, from, System.currentTimeMillis()));
                        }
                        //DeletePublisherAssertionByBusinessQuery.delete(em, businessKeysFound);

                        // Slate is clean for all assertions involving this publisher, now we simply need to add the new ones (and they will all be "new").
                        /*List<org.uddi.api_v3.PublisherAssertion> apiPubAssertionList = publisherAssertion.value;

                        
                         for (org.uddi.api_v3.PublisherAssertion apiPubAssertion : apiPubAssertionList) {

                         org.apache.juddi.model.PublisherAssertion modelPubAssertion = new org.apache.juddi.model.PublisherAssertion();

                         MappingApiToModel.mapPublisherAssertion(apiPubAssertion, modelPubAssertion);
                                
                         org.apache.juddi.model.BusinessEntity beFrom = em.find(org.apache.juddi.model.BusinessEntity.class, modelPubAssertion.getId().getFromKey());
                         org.apache.juddi.model.BusinessEntity beTo = em.find(org.apache.juddi.model.BusinessEntity.class, modelPubAssertion.getId().getToKey());
                         modelPubAssertion.setBusinessEntityByFromKey(beFrom);
                         modelPubAssertion.setBusinessEntityByToKey(beTo);

                         modelPubAssertion.setFromCheck("false");
                         modelPubAssertion.setToCheck("false");

                         if (publisher.isOwner(modelPubAssertion.getBusinessEntityByFromKey())) {
                         modelPubAssertion.setFromCheck("true");
                         }
                         if (publisher.isOwner(modelPubAssertion.getBusinessEntityByToKey())) {
                         modelPubAssertion.setToCheck("true");
                         }
                         em.persist(modelPubAssertion);

                         changes.add(getChangeRecord_NewAssertion(apiPubAssertion, modelPubAssertion, node));

                         }*/
                        tx.commit();
                        if (!publisherAssertion.value.isEmpty()) {
                                AddPublisherAssertions addPublisherAssertions = new AddPublisherAssertions();
                                addPublisherAssertions.setAuthInfo(authInfo);
                                addPublisherAssertions.getPublisherAssertion().addAll(publisherAssertion.value);
                                addPublisherAssertions(addPublisherAssertions);
                        }
                        for (int i = 0; i < changes.size(); i++) {
                                ReplicationNotifier.Enqueue(changes.get(i));
                        }
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SET_PUBLISHERASSERTIONS,
                                QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(PublicationQuery.SET_PUBLISHERASSERTIONS,
                                QueryStatus.FAILED, procTime);
                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        private void setOperationalInfo(EntityManager em, org.apache.juddi.model.BusinessEntity uddiEntity, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {

                uddiEntity.setAuthorizedName(publisher.getAuthorizedName());

                Date now = new Date();
                uddiEntity.setModified(now);
                uddiEntity.setModifiedIncludingChildren(now);

                String nodeId = "";
                try {
                        nodeId = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                } catch (ConfigurationException ce) {
                        throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval", Property.JUDDI_NODE_ID));
                }
                uddiEntity.setNodeId(nodeId);

                org.apache.juddi.model.BusinessEntity existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
                if (existingUddiEntity != null) {
                        uddiEntity.setCreated(existingUddiEntity.getCreated());
                } else {
                        uddiEntity.setCreated(now);
                }

                List<org.apache.juddi.model.BusinessService> serviceList = uddiEntity.getBusinessServices();
                for (org.apache.juddi.model.BusinessService service : serviceList) {
                        setOperationalInfo(em, service, publisher, true);
                }

                if (existingUddiEntity != null) {
                        em.remove(existingUddiEntity);
                }

        }

        private void setOperationalInfo(EntityManager em, org.apache.juddi.model.BusinessService uddiEntity, UddiEntityPublisher publisher, boolean isChild) throws DispositionReportFaultMessage {

                uddiEntity.setAuthorizedName(publisher.getAuthorizedName());

                Date now = new Date();
                uddiEntity.setModified(now);
                uddiEntity.setModifiedIncludingChildren(now);

                if (!isChild) {
                        org.apache.juddi.model.BusinessEntity parent = em.find(org.apache.juddi.model.BusinessEntity.class, uddiEntity.getBusinessEntity().getEntityKey());
                        parent.setModifiedIncludingChildren(now);
                        em.persist(parent);
                }

                String nodeId = "";
                try {
                        nodeId = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                } catch (ConfigurationException ce) {
                        throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval", Property.JUDDI_NODE_ID));
                }
                uddiEntity.setNodeId(nodeId);

                org.apache.juddi.model.BusinessService existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
                if (existingUddiEntity != null) {
                        uddiEntity.setCreated(existingUddiEntity.getCreated());
                } else {
                        uddiEntity.setCreated(now);
                }

                List<org.apache.juddi.model.BindingTemplate> bindingList = uddiEntity.getBindingTemplates();
                for (org.apache.juddi.model.BindingTemplate binding : bindingList) {
                        setOperationalInfo(em, binding, publisher, true);
                }

                if (existingUddiEntity != null) {
                        em.remove(existingUddiEntity);
                }

        }

        private void setOperationalInfo(EntityManager em, org.apache.juddi.model.BindingTemplate uddiEntity, UddiEntityPublisher publisher, boolean isChild) throws DispositionReportFaultMessage {

                uddiEntity.setAuthorizedName(publisher.getAuthorizedName());

                Date now = new Date();
                uddiEntity.setModified(now);
                uddiEntity.setModifiedIncludingChildren(now);

                //if (!isChild) {
                org.apache.juddi.model.BusinessService parent = em.find(org.apache.juddi.model.BusinessService.class, uddiEntity.getBusinessService().getEntityKey());
                if (parent != null) {
                        parent.setModifiedIncludingChildren(now);
                        em.persist(parent);

                        // JUDDI-421:  now the businessEntity parent will have it's modifiedIncludingChildren set
                        org.apache.juddi.model.BusinessEntity businessParent = em.find(org.apache.juddi.model.BusinessEntity.class, parent.getBusinessEntity().getEntityKey());
                        if (businessParent != null) {
                                businessParent.setModifiedIncludingChildren(now);
                                em.persist(businessParent);
                        } else {
                                logger.debug("Parent business is null for saved binding template!");
                        }
                } else {
                        logger.debug("Parent service is null for saved binding template!");
                }
                // }

                String nodeId = "";
                try {
                        nodeId = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                } catch (ConfigurationException ce) {
                        throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval", Property.JUDDI_NODE_ID));
                }
                uddiEntity.setNodeId(nodeId);

                org.apache.juddi.model.BindingTemplate existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
                if (existingUddiEntity != null) {
                        uddiEntity.setCreated(existingUddiEntity.getCreated());
                } else {
                        uddiEntity.setCreated(now);
                }

                if (existingUddiEntity != null) {
                        em.remove(existingUddiEntity);
                }

        }

        private void setOperationalInfo(EntityManager em, org.apache.juddi.model.Tmodel uddiEntity, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {

                uddiEntity.setAuthorizedName(publisher.getAuthorizedName());

                Date now = new Date();
                uddiEntity.setModified(now);
                uddiEntity.setModifiedIncludingChildren(now);

                String nodeId = "";
                try {
                        nodeId = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                } catch (ConfigurationException ce) {
                        throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval", Property.JUDDI_NODE_ID));
                }
                uddiEntity.setNodeId(nodeId);

                org.apache.juddi.model.Tmodel existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
                if (existingUddiEntity != null) {
                        uddiEntity.setCreated(existingUddiEntity.getCreated());
                } else {
                        uddiEntity.setCreated(now);
                }

                if (existingUddiEntity != null) {
                        em.remove(existingUddiEntity);
                }

        }

        public static ChangeRecord getChangeRecord(BindingTemplate modelBindingTemplate, org.uddi.api_v3.BindingTemplate api, String node) throws DispositionReportFaultMessage {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(modelBindingTemplate.getEntityKey());
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordNewData);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordNewData(new ChangeRecordNewData());
                crapi.getChangeRecordNewData().setBindingTemplate(api);
                crapi.getChangeRecordNewData().setOperationalInfo(new OperationalInfo());
                MappingModelToApi.mapOperationalInfo(modelBindingTemplate, crapi.getChangeRecordNewData().getOperationalInfo());
                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord(BusinessService model, org.uddi.api_v3.BusinessService api, String node) throws DispositionReportFaultMessage {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(model.getEntityKey());
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordNewData);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordNewData(new ChangeRecordNewData());
                crapi.getChangeRecordNewData().setBusinessService(api);
                crapi.getChangeRecordNewData().setOperationalInfo(new OperationalInfo());
                MappingModelToApi.mapOperationalInfo(model, crapi.getChangeRecordNewData().getOperationalInfo());
                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord_deleteBusiness(String entityKey, String node) {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(entityKey);
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordDelete);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordDelete(new ChangeRecordDelete());
                crapi.getChangeRecordDelete().setBusinessKey(entityKey);
                crapi.getChangeRecordDelete().setModified(df.newXMLGregorianCalendar(new GregorianCalendar()));

                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord_deleteService(String entityKey, String node) {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(entityKey);
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordDelete);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordDelete(new ChangeRecordDelete());
                crapi.getChangeRecordDelete().setServiceKey(entityKey);
                crapi.getChangeRecordDelete().setModified(df.newXMLGregorianCalendar(new GregorianCalendar()));

                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        /**
         * this is for "hiding" a tmodel, not removing it entirely
         *
         * @param entityKey
         * @param node
         * @return
         */
        public static ChangeRecord getChangeRecord_deleteTModelHide(String entityKey, String node) {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(entityKey);
                cr.setNodeID(node);
                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordHide);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));

                crapi.setChangeRecordHide(new ChangeRecordHide());
                crapi.getChangeRecordHide().setTModelKey(entityKey);
                crapi.getChangeRecordHide().setModified(df.newXMLGregorianCalendar(new GregorianCalendar()));

                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                //JAXB.marshal(crapi, System.out);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        /**
         * this is for deleting a tmodel, not hiding it
         *
         * @param entityKey
         * @param node
         * @return
         */
        public static ChangeRecord getChangeRecord_deleteTModelDelete(String entityKey, String node) {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(entityKey);
                cr.setNodeID(node);
                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordDelete);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));

                crapi.setChangeRecordDelete(new ChangeRecordDelete());
                crapi.getChangeRecordDelete().setTModelKey(entityKey);
                crapi.getChangeRecordDelete().setModified(df.newXMLGregorianCalendar(new GregorianCalendar()));

                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                //JAXB.marshal(crapi, System.out);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord(BusinessEntity modelBusinessEntity, org.uddi.api_v3.BusinessEntity apiBusinessEntity, String node) throws DispositionReportFaultMessage {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(modelBusinessEntity.getEntityKey());
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordNewData);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordNewData(new ChangeRecordNewData());
                crapi.getChangeRecordNewData().setBusinessEntity(apiBusinessEntity);
                crapi.getChangeRecordNewData().setOperationalInfo(new OperationalInfo());
                MappingModelToApi.mapOperationalInfo(modelBusinessEntity, crapi.getChangeRecordNewData().getOperationalInfo());
                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord(Tmodel modelBusinessEntity, org.uddi.api_v3.TModel apiBusinessEntity, String node) throws DispositionReportFaultMessage {
                ChangeRecord cr = new ChangeRecord();
                if (!apiBusinessEntity.getTModelKey().equals(modelBusinessEntity.getEntityKey())) {
                        throw new FatalErrorException(new ErrorMessage("E_fatalError", "the model and api keys do not match when saving a tmodel!"));
                }
                cr.setEntityKey(modelBusinessEntity.getEntityKey());
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordNewData);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordNewData(new ChangeRecordNewData());
                crapi.getChangeRecordNewData().setTModel(apiBusinessEntity);
                crapi.getChangeRecordNewData().getTModel().setTModelKey(modelBusinessEntity.getEntityKey());
                crapi.getChangeRecordNewData().setOperationalInfo(new OperationalInfo());
                MappingModelToApi.mapOperationalInfo(modelBusinessEntity, crapi.getChangeRecordNewData().getOperationalInfo());
                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord_deleteBinding(String entityKey, String node) {
                ChangeRecord cr = new ChangeRecord();
                cr.setEntityKey(entityKey);
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordDelete);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordDelete(new ChangeRecordDelete());
                crapi.getChangeRecordDelete().setBindingKey(entityKey);
                crapi.getChangeRecordDelete().setModified(df.newXMLGregorianCalendar(new GregorianCalendar()));

                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord_deletePublisherAssertion(PublisherAssertion entity, String node, boolean ToBusinessDelete, boolean FromBusinessDelete, long timestamp) {
                ChangeRecord cr = new ChangeRecord();

                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordDeleteAssertion);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordDeleteAssertion(new ChangeRecordDeleteAssertion());
                crapi.getChangeRecordDeleteAssertion().setPublisherAssertion(entity);
                crapi.getChangeRecordDeleteAssertion().setToBusinessCheck(ToBusinessDelete);
                crapi.getChangeRecordDeleteAssertion().setFromBusinessCheck(FromBusinessDelete);
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(timestamp);
                crapi.getChangeRecordDeleteAssertion().setModified(df.newXMLGregorianCalendar(gcal));

                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        public static ChangeRecord getChangeRecord_NewAssertion(PublisherAssertion apiPubAssertion, org.apache.juddi.model.PublisherAssertion modelPubAssertion, String node) {
                ChangeRecord cr = new ChangeRecord();

                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordPublisherAssertion);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordPublisherAssertion(new ChangeRecordPublisherAssertion());
                crapi.getChangeRecordPublisherAssertion().setFromBusinessCheck(modelPubAssertion.getFromCheck().equalsIgnoreCase("true"));
                crapi.getChangeRecordPublisherAssertion().setToBusinessCheck(modelPubAssertion.getToCheck().equalsIgnoreCase("true"));
                crapi.getChangeRecordPublisherAssertion().setPublisherAssertion(apiPubAssertion);

                crapi.getChangeRecordPublisherAssertion().setModified(df.newXMLGregorianCalendar(new GregorianCalendar()));

                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        /**
         *
         * @param value keep these
         * @param existingAssertions return a list of these that are NOT in
         * 'value'
         * @return
         * @throws DispositionReportFaultMessage
         */
        private List<org.apache.juddi.model.PublisherAssertion> diff(List<PublisherAssertion> value, List<org.apache.juddi.model.PublisherAssertion> existingAssertions) throws DispositionReportFaultMessage {
                List<org.apache.juddi.model.PublisherAssertion> ret = new ArrayList<org.apache.juddi.model.PublisherAssertion>();
                if (value == null || value.isEmpty()) {
                        return existingAssertions;
                }
                if (existingAssertions == null) {
                        return new ArrayList<org.apache.juddi.model.PublisherAssertion>();
                }
                for (org.apache.juddi.model.PublisherAssertion model : existingAssertions) {

                        boolean found = false;
                        for (PublisherAssertion paapi : value) {
                                if (model.getBusinessEntityByFromKey().getEntityKey().equalsIgnoreCase(paapi.getFromKey())
                                        && model.getBusinessEntityByToKey().getEntityKey().equalsIgnoreCase(paapi.getToKey())
                                        && model.getKeyName().equals(paapi.getKeyedReference().getKeyName())
                                        && model.getKeyValue().equals(paapi.getKeyedReference().getKeyValue())
                                        && model.getTmodelKey().equalsIgnoreCase(paapi.getKeyedReference().getTModelKey())) {
                                        found = true;
                                        break;
                                }
                        }
                        if (!found) {
                                ret.add(model);
                        }
                }
                return ret;
        }

        private static ChangeRecord getChangeRecordConditional(Tmodel modelTModel, TModel apiTModel, String node) throws DispositionReportFaultMessage {
                ChangeRecord cr = new ChangeRecord();
                if (!apiTModel.getTModelKey().equals(modelTModel.getEntityKey())) {
                        throw new FatalErrorException(new ErrorMessage("E_fatalError", "the model and api keys do not match when saving a tmodel!"));
                }
                cr.setEntityKey(modelTModel.getEntityKey());
                cr.setNodeID(node);

                cr.setRecordType(ChangeRecord.RecordType.ChangeRecordNewDataConditional);
                org.uddi.repl_v3.ChangeRecord crapi = new org.uddi.repl_v3.ChangeRecord();
                crapi.setChangeID(new ChangeRecordIDType(node, -1L));
                crapi.setChangeRecordNewDataConditional(new ChangeRecordNewDataConditional());
                crapi.getChangeRecordNewDataConditional().setChangeRecordNewData(new ChangeRecordNewData());
                crapi.getChangeRecordNewDataConditional().getChangeRecordNewData().setTModel(apiTModel);
                crapi.getChangeRecordNewDataConditional().getChangeRecordNewData().getTModel().setTModelKey(modelTModel.getEntityKey());
                crapi.getChangeRecordNewDataConditional().getChangeRecordNewData().setOperationalInfo(new OperationalInfo());
                MappingModelToApi.mapOperationalInfo(modelTModel, crapi.getChangeRecordNewDataConditional().getChangeRecordNewData().getOperationalInfo());
                StringWriter sw = new StringWriter();
                JAXB.marshal(crapi, sw);
                try {
                        cr.setContents(sw.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException ex) {
                        logger.error(ex);
                }
                return cr;
        }

        private void removeExistingPublisherAssertionSignatures(String from, String to, EntityManager em) {
                Query createQuery = em.createQuery("delete from Signature pa where pa.publisherAssertionFromKey=:from and pa.publisherAssertionToKey=:to");
                createQuery.setParameter("from", from);
                createQuery.setParameter("to", to);
                createQuery.executeUpdate();
        }

        private void savePushliserAssertionSignatures(String from, String to, List<Signature> signatures, EntityManager em) {
                if (signatures == null) {
                        return;
                }
                for (Signature s : signatures) {
                        s.setPublisherAssertionFromKey(from);
                        s.setPublisherAssertionToKey(to);
                        em.persist(s);
                }
        }

}
