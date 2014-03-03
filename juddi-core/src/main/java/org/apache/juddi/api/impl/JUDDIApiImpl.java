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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.api_v3.AdminSaveBusinessWrapper;
import org.apache.juddi.api_v3.AdminSaveTModelWrapper;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.ClerkList;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.DeleteClerk;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.DeleteNode;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.NodeList;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.api_v3.SubscriptionWrapper;
import org.apache.juddi.api_v3.SyncSubscription;
import org.apache.juddi.api_v3.SyncSubscriptionDetail;
import org.apache.juddi.api_v3.ValidValues;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.ClientSubscriptionInfo;
import org.apache.juddi.model.Node;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.ReplicationConfiguration;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.model.ValueSetValue;
import org.apache.juddi.model.ValueSetValues;
import org.apache.juddi.subscription.NotificationList;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.juddi.validation.ValidateClerk;
import org.apache.juddi.validation.ValidateClientSubscriptionInfo;
import org.apache.juddi.validation.ValidateNode;
import org.apache.juddi.validation.ValidatePublish;
import org.apache.juddi.validation.ValidatePublisher;
import org.apache.juddi.validation.ValidateValueSetValidation;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.InfoSelection;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelInfos;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * Implements the jUDDI API service. These methods are outside of the UDDI spec
 * and are specific to jUDDI. They are primarily used for administrative functions.
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
@WebService(serviceName = "JUDDIApiService",
        endpointInterface = "org.apache.juddi.v3_service.JUDDIApiPortType",
        targetNamespace = "urn:juddi-apache-org:v3_service")
public class JUDDIApiImpl extends AuthenticatedService implements JUDDIApiPortType {

        private Log log = LogFactory.getLog(this.getClass());

        /**
         * Saves publisher(s) to the persistence layer. This method is specific
         * to jUDDI. Administrative privilege required.
         *
         * @param body
         * @return PublisherDetail
         * @throws DispositionReportFaultMessage
         */
        public PublisherDetail savePublisher(SavePublisher body)
                throws DispositionReportFaultMessage {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateSavePublisher(em, body);

                        PublisherDetail result = new PublisherDetail();

                        List<org.apache.juddi.api_v3.Publisher> apiPublisherList = body.getPublisher();
                        for (org.apache.juddi.api_v3.Publisher apiPublisher : apiPublisherList) {

                                org.apache.juddi.model.Publisher modelPublisher = new org.apache.juddi.model.Publisher();

                                MappingApiToModel.mapPublisher(apiPublisher, modelPublisher);

                                Object existingUddiEntity = em.find(modelPublisher.getClass(), modelPublisher.getAuthorizedName());
                                if (existingUddiEntity != null) {
                                        em.remove(existingUddiEntity);
                                }

                                em.persist(modelPublisher);

                                result.getPublisher().add(apiPublisher);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * Deletes publisher(s) from the persistence layer. This method is
         * specific to jUDDI. Administrative privilege required.
         *
         * @param body
         * @throws DispositionReportFaultMessage
         */
        public void deletePublisher(DeletePublisher body)
                throws DispositionReportFaultMessage {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateDeletePublisher(em, body);

                        List<String> entityKeyList = body.getPublisherId();
                        for (String entityKey : entityKeyList) {
                        	Publisher obj = em.find(org.apache.juddi.model.Publisher.class, entityKey);
                        
                        	//get an authtoken for this publisher so that we can get its registeredInfo
                        	UDDISecurityImpl security = new UDDISecurityImpl();
                        	AuthToken authToken = security.getAuthToken(entityKey);
                        	
                        	GetRegisteredInfo r = new GetRegisteredInfo();
                        	r.setAuthInfo(authToken.getAuthInfo());
                        	r.setInfoSelection(InfoSelection.ALL);
	                       
                        	log.info("removing all businesses owned by publisher " + entityKey + ".");
                        	UDDIPublicationImpl publish = new UDDIPublicationImpl();
                        	RegisteredInfo registeredInfo = publish.getRegisteredInfo(r);
                        	BusinessInfos businessInfos = registeredInfo.getBusinessInfos();
                        	if (businessInfos!=null && businessInfos.getBusinessInfo()!=null) {
                        		Iterator<BusinessInfo> iter = businessInfos.getBusinessInfo().iterator();
                        	    while (iter.hasNext()) {
                        	    	BusinessInfo businessInfo = iter.next();
                        	    	Object business = em.find(org.apache.juddi.model.BusinessEntity.class, businessInfo.getBusinessKey());
                        	    	em.remove(business);
                        	    }
							}
                        	
                        	log.info("mark all tmodels for publisher " + entityKey + " as deleted.");
                        	TModelInfos tmodelInfos = registeredInfo.getTModelInfos();
                        	if (tmodelInfos!=null && tmodelInfos.getTModelInfo()!=null) {
                        		Iterator<TModelInfo> iter = tmodelInfos.getTModelInfo().iterator();
                        		while (iter.hasNext()) {
                        			TModelInfo tModelInfo = iter.next();
                        			Tmodel tmodel = (Tmodel) em.find(org.apache.juddi.model.Tmodel.class, tModelInfo.getTModelKey());
                        			tmodel.setDeleted(true);
                        			em.persist(tmodel);
                        		}
                        	}
                        	log.info("remove all persisted AuthTokens for publisher " + entityKey + ".");
                        	Query q1 = em.createQuery("DELETE FROM AuthToken auth WHERE auth.authorizedName = '" + entityKey + "'");
                            q1.executeUpdate();
                      
                            log.info("removing publisher " + entityKey + ".");
                            //delete the publisher
                        	em.remove(obj);
                        }

                        tx.commit();
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        
        /**
         * Retrieves publisher(s) from the persistence layer. This method is
         * specific to jUDDI. Administrative privilege required. 
         * @param body
         * @return PublisherDetail
         * @throws DispositionReportFaultMessage 
         */
        public PublisherDetail getPublisherDetail(GetPublisherDetail body)
                throws DispositionReportFaultMessage {

                new ValidatePublisher(null).validateGetPublisherDetail(body);

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        this.getEntityPublisher(em, body.getAuthInfo());

                        PublisherDetail result = new PublisherDetail();

                        List<String> publisherIdList = body.getPublisherId();
                        for (String publisherId : publisherIdList) {
                                org.apache.juddi.model.Publisher modelPublisher = null;
                                try {
                                        modelPublisher = em.find(org.apache.juddi.model.Publisher.class, publisherId);
                                } catch (ClassCastException e) {
                                }
                                if (modelPublisher == null) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.PublisherNotFound", publisherId));
                                }

                                org.apache.juddi.api_v3.Publisher apiPublisher = new org.apache.juddi.api_v3.Publisher();

                                MappingModelToApi.mapPublisher(modelPublisher, apiPublisher);

                                result.getPublisher().add(apiPublisher);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

        }

        /**
         * Retrieves all publisher from the persistence layer. This method is
         * specific to jUDDI. Administrative privilege required. Use caution when calling, result
         * set is not bound. If there are many publishers, it is possible to have a 
         * result set that is too large
         * @param body
         * @return PublisherDetail
         * @throws DispositionReportFaultMessage
         * @throws RemoteException 
         */
        @SuppressWarnings("unchecked")
        public PublisherDetail getAllPublisherDetail(GetAllPublisherDetail body)
                throws DispositionReportFaultMessage, RemoteException {

                new ValidatePublisher(null).validateGetAllPublisherDetail(body);

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        this.getEntityPublisher(em, body.getAuthInfo());

                        PublisherDetail result = new PublisherDetail();

                        Query query = em.createQuery("SELECT p from Publisher as p");
                        List<Publisher> modelPublisherList = query.getResultList();

                        for (Publisher modelPublisher : modelPublisherList) {

                                org.apache.juddi.api_v3.Publisher apiPublisher = new org.apache.juddi.api_v3.Publisher();

                                MappingModelToApi.mapPublisher(modelPublisher, apiPublisher);

                                result.getPublisher().add(apiPublisher);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * Completely deletes a tModel from the persistence layer.
         * Administrative privilege required. All entities that reference this tModel
         * will no longer be able to use the tModel if jUDDI Option Enforce referential Integrity is enabled.<br>
         * Required permission, you must be am administrator
         * {@link Property#JUDDI_ENFORCE_REFERENTIAL_INTEGRITY}
         * @param body
         * @throws DispositionReportFaultMessage 
         */
        public void adminDeleteTModel(DeleteTModel body)
                throws DispositionReportFaultMessage {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidatePublish(publisher).validateAdminDeleteTModel(em, body);

                        List<String> entityKeyList = body.getTModelKey();
                        for (String entityKey : entityKeyList) {
                                Object obj = em.find(org.apache.juddi.model.Tmodel.class, entityKey);
                                em.remove(obj);
                        }

                        tx.commit();
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * Delete's a client's subscription information. This is typically used for
         * server to server subscriptions
         * Administrative privilege required.
         * @param body
         * @throws DispositionReportFaultMessage
         * @throws RemoteException 
         */
        public void deleteClientSubscriptionInfo(DeleteClientSubscriptionInfo body)
                throws DispositionReportFaultMessage, RemoteException {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidateClientSubscriptionInfo(publisher).validateDeleteClientSubscriptionInfo(em, body);

                        List<String> entityKeyList = body.getSubscriptionKey();
                        for (String entityKey : entityKeyList) {
                                Object obj = em.find(org.apache.juddi.model.ClientSubscriptionInfo.class, entityKey);
                                em.remove(obj);
                        }

                        tx.commit();
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

        }

        /**
         * Adds client subscription information. This effectively links a server to
         * serverr subscription to clerk
         * Administrative privilege required.
         * @param body
         * @return ClientSubscriptionInfoDetail
         * @throws DispositionReportFaultMessage
         * @throws RemoteException 
         */
        public ClientSubscriptionInfoDetail saveClientSubscriptionInfo(SaveClientSubscriptionInfo body)
                throws DispositionReportFaultMessage, RemoteException {
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidateClientSubscriptionInfo(publisher).validateSaveClientSubscriptionInfo(em, body);

                        ClientSubscriptionInfoDetail result = new ClientSubscriptionInfoDetail();

                        List<org.apache.juddi.api_v3.ClientSubscriptionInfo> apiClientSubscriptionInfoList = body.getClientSubscriptionInfo();
                        for (org.apache.juddi.api_v3.ClientSubscriptionInfo apiClientSubscriptionInfo : apiClientSubscriptionInfoList) {

                                org.apache.juddi.model.ClientSubscriptionInfo modelClientSubscriptionInfo = new org.apache.juddi.model.ClientSubscriptionInfo();

                                MappingApiToModel.mapClientSubscriptionInfo(apiClientSubscriptionInfo, modelClientSubscriptionInfo);

                                Object existingUddiEntity = em.find(modelClientSubscriptionInfo.getClass(), modelClientSubscriptionInfo.getSubscriptionKey());
                                if (existingUddiEntity != null) {
                                        em.remove(existingUddiEntity);
                                }

                                em.persist(modelClientSubscriptionInfo);

                                result.getClientSubscriptionInfo().add(apiClientSubscriptionInfo);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * Gets all client subscription information. This is used for server to server subscriptions
         * Administrative privilege required.
         * @param body
         * @return ClientSubscriptionInfoDetail
         * @throws DispositionReportFaultMessage 
         */
        @SuppressWarnings("unchecked")
        public ClientSubscriptionInfoDetail getAllClientSubscriptionInfoDetail(GetAllClientSubscriptionInfoDetail body)
                throws DispositionReportFaultMessage {

                new ValidateClientSubscriptionInfo(null).validateGetAllClientSubscriptionDetail(body);

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        this.getEntityPublisher(em, body.getAuthInfo());

                        ClientSubscriptionInfoDetail result = new ClientSubscriptionInfoDetail();

                        Query query = em.createQuery("SELECT cs from ClientSubscriptionInfo as cs");
                        List<org.apache.juddi.model.ClientSubscriptionInfo> modelClientSubscriptionInfoList = query.getResultList();

                        for (ClientSubscriptionInfo modelClientSubscriptionInfo : modelClientSubscriptionInfoList) {

                                org.apache.juddi.api_v3.ClientSubscriptionInfo apiClientSubscriptionInfo = new org.apache.juddi.api_v3.ClientSubscriptionInfo();

                                MappingModelToApi.mapClientSubscriptionInfo(modelClientSubscriptionInfo, apiClientSubscriptionInfo);

                                result.getClientSubscriptionInfo().add(apiClientSubscriptionInfo);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

        }

        /**
         * Retrieves clientSubscriptionKey(s) from the persistence layer. This
         * method is specific to jUDDI. Used for server to server subscriptions
         * Administrative privilege required.
         * @param body
         * @return ClientSubscriptionInfoDetail
         * @throws DispositionReportFaultMessage 
         */
        public ClientSubscriptionInfoDetail getClientSubscriptionInfoDetail(GetClientSubscriptionInfoDetail body)
                throws DispositionReportFaultMessage {

                new ValidateClientSubscriptionInfo(null).validateGetClientSubscriptionInfoDetail(body);

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        this.getEntityPublisher(em, body.getAuthInfo());

                        ClientSubscriptionInfoDetail result = new ClientSubscriptionInfoDetail();

                        List<String> subscriptionKeyList = body.getClientSubscriptionKey();
                        for (String subscriptionKey : subscriptionKeyList) {

                                org.apache.juddi.model.ClientSubscriptionInfo modelClientSubscriptionInfo = null;
                                try {
                                        modelClientSubscriptionInfo = em.find(org.apache.juddi.model.ClientSubscriptionInfo.class, subscriptionKey);
                                } catch (ClassCastException e) {
                                }
                                if (modelClientSubscriptionInfo == null) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.SubscripKeyNotFound", subscriptionKey));
                                }

                                org.apache.juddi.api_v3.ClientSubscriptionInfo apiClientSubscriptionInfo = new org.apache.juddi.api_v3.ClientSubscriptionInfo();

                                MappingModelToApi.mapClientSubscriptionInfo(modelClientSubscriptionInfo, apiClientSubscriptionInfo);

                                result.getClientSubscriptionInfo().add(apiClientSubscriptionInfo);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

        }

        
        /**
         * Saves clerk(s) to the persistence layer. This method is specific to
         * jUDDI. This is used for server to server subscriptions and for future use
         * with replication. Administrative privilege required.
         * @param body
         * @return ClerkDetail
         * @throws DispositionReportFaultMessage 
         */
        public ClerkDetail saveClerk(SaveClerk body)
                throws DispositionReportFaultMessage {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidateClerk(publisher).validateSaveClerk(em, body);

                        ClerkDetail result = new ClerkDetail();

                        List<org.apache.juddi.api_v3.Clerk> apiClerkList = body.getClerk();;
                        for (org.apache.juddi.api_v3.Clerk apiClerk : apiClerkList) {

                                org.apache.juddi.model.Clerk modelClerk = new org.apache.juddi.model.Clerk();

                                MappingApiToModel.mapClerk(apiClerk, modelClerk);

                                Object existingUddiEntity = em.find(modelClerk.getClass(), modelClerk.getClerkName());
                                if (existingUddiEntity != null) {
                                        em.merge(modelClerk);
                                } else {
                                        em.persist(modelClerk);
                                }

                                result.getClerk().add(apiClerk);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * Saves nodes(s) to the persistence layer. This method is specific to
         * jUDDI. Administrative privilege required. This is used for server to server subscriptions and for future use
         * with replication. Administrative privilege required.
         * @param body
         * @return NodeDetail
         * @throws DispositionReportFaultMessage 
         */
        public NodeDetail saveNode(SaveNode body)
                throws DispositionReportFaultMessage {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        new ValidateNode(publisher).validateSaveNode(em, body);

                        NodeDetail result = new NodeDetail();

                        List<org.apache.juddi.api_v3.Node> apiNodeList = body.getNode();;
                        for (org.apache.juddi.api_v3.Node apiNode : apiNodeList) {

                                org.apache.juddi.model.Node modelNode = new org.apache.juddi.model.Node();

                                MappingApiToModel.mapNode(apiNode, modelNode);

                                Object existingUddiEntity = em.find(modelNode.getClass(), modelNode.getName());
                                if (existingUddiEntity != null) {
                                        em.merge(modelNode);
                                } else {
                                        em.persist(modelNode);
                                }

                                result.getNode().add(apiNode);
                        }

                        tx.commit();
                        return result;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        /**
         * Instructs the registry to perform a synchronous subscription
         * response.
         * @param body
         * @return SyncSubscriptionDetail
         * @throws DispositionReportFaultMessage
         * @throws RemoteException 
         */
        @SuppressWarnings("unchecked")
        public SyncSubscriptionDetail invokeSyncSubscription(
                SyncSubscription body) throws DispositionReportFaultMessage,
                RemoteException {

                //validate
                SyncSubscriptionDetail syncSubscriptionDetail = new SyncSubscriptionDetail();

                Map<String, org.apache.juddi.api_v3.ClientSubscriptionInfo> clientSubscriptionInfoMap
                        = new HashMap<String, org.apache.juddi.api_v3.ClientSubscriptionInfo>();
                //find the clerks to go with these subscriptions
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        this.getEntityPublisher(em, body.getAuthInfo());
                        for (GetSubscriptionResults getSubscriptionResult : body.getGetSubscriptionResultsList()) {
                                String subscriptionKey = getSubscriptionResult.getSubscriptionKey();
                                org.apache.juddi.model.ClientSubscriptionInfo modelClientSubscriptionInfo = null;
                                try {
                                        modelClientSubscriptionInfo = em.find(org.apache.juddi.model.ClientSubscriptionInfo.class, subscriptionKey);
                                } catch (ClassCastException e) {
                                }
                                if (modelClientSubscriptionInfo == null) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.SubscripKeyNotFound", subscriptionKey));
                                }
                                org.apache.juddi.api_v3.ClientSubscriptionInfo apiClientSubscriptionInfo = new org.apache.juddi.api_v3.ClientSubscriptionInfo();
                                MappingModelToApi.mapClientSubscriptionInfo(modelClientSubscriptionInfo, apiClientSubscriptionInfo);
                                clientSubscriptionInfoMap.put(apiClientSubscriptionInfo.getSubscriptionKey(), apiClientSubscriptionInfo);
                        }

                        tx.commit();
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

                for (GetSubscriptionResults getSubscriptionResult : body.getGetSubscriptionResultsList()) {
                        try {
                                String subscriptionKey = getSubscriptionResult.getSubscriptionKey();
                                Clerk fromClerk = clientSubscriptionInfoMap.get(subscriptionKey).getFromClerk();
                                Clerk toClerk = clientSubscriptionInfoMap.get(subscriptionKey).getToClerk();
                                String clazz = fromClerk.getNode().getProxyTransport();
                                Class<?> transportClass = ClassUtil.forName(clazz, this.getClass());
                                Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance(fromClerk.getNode().getName());
                                UDDISubscriptionPortType subscriptionService = transport.getUDDISubscriptionService(fromClerk.getNode().getSubscriptionUrl());
                                SubscriptionResultsList list = subscriptionService.getSubscriptionResults(getSubscriptionResult);

                                JAXBContext context = JAXBContext.newInstance(list.getClass());
                                Marshaller marshaller = context.createMarshaller();
                                StringWriter sw = new StringWriter();
                                marshaller.marshal(list, sw);

                                log.info("Notification received by UDDISubscriptionListenerService : " + sw.toString());

                                NotificationList<String> nl = NotificationList.getInstance();
                                nl.getNotifications().add(sw.toString());

                                //update the registry with the notification list.
                                XRegisterHelper.handle(fromClerk, toClerk, list);

                                syncSubscriptionDetail.getSubscriptionResultsList().add(list);
                        } catch (Exception ce) {
                                log.error(ce.getMessage(), ce);
                                if (ce instanceof DispositionReportFaultMessage) {
                                        throw (DispositionReportFaultMessage) ce;
                                }
                                if (ce instanceof RemoteException) {
									 DispositionReportFaultMessage x = new FatalErrorException(new ErrorMessage("errors.subscriptionnotifier.client", ce.getMessage()));
                                        throw x;
                                }
                        }
                }
                //for now sending a clean object back

                return syncSubscriptionDetail;
        }

        @Override
        public NodeList getAllNodes(String authInfo) throws DispositionReportFaultMessage, RemoteException {

                NodeList r = new NodeList();


                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);

                        new ValidatePublish(publisher).validateGetAllNodes();

                        StringBuilder sql = new StringBuilder();
                        sql.append("select distinct c from Node c ");
                        sql.toString();
                        Query qry = em.createQuery(sql.toString());
                        List<org.apache.juddi.model.Node> resultList = qry.getResultList();
                        for (int i = 0; i < resultList.size(); i++) {
                               org.apache.juddi.api_v3.Node api = new org.apache.juddi.api_v3.Node();
                                MappingModelToApi.mapNode(resultList.get(i), api);
                                r.getNode().add(api);

                        }

                        tx.commit();

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

                return r;
        }

        @Override
        public ClerkList getAllClerks(String authInfo) throws DispositionReportFaultMessage, RemoteException {

                ClerkList ret = new ClerkList();
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);

                        new ValidatePublish(publisher).validateGetAllNodes();

                        StringBuilder sql = new StringBuilder();
                        sql.append("select distinct c from Clerk c ");
                        sql.toString();
                        Query qry = em.createQuery(sql.toString());
                        List<org.apache.juddi.model.Clerk> resultList = qry.getResultList();
                        for (int i = 0; i < resultList.size(); i++) {
                                Clerk api = new Clerk();
                                MappingModelToApi.mapClerk(resultList.get(i), api);
                                ret.getClerk().add(api);

                        }
                        tx.commit();

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

                return ret;

        }

        @Override
        public void deleteNode(DeleteNode req) throws DispositionReportFaultMessage , RemoteException{
                boolean found = false;
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        
                        UddiEntityPublisher publisher = this.getEntityPublisher(em, req.getAuthInfo());
                        new ValidatePublish(publisher).validateDeleteNode(em, req);
                        


                        org.apache.juddi.model.Node existingUddiEntity = em.find(org.apache.juddi.model.Node.class, req.getNodeID());
                        if (existingUddiEntity != null) {

                                //TODO cascade delete all clerks tied to this node

                                em.remove(existingUddiEntity);
                                found = true;
                        }
                        tx.commit();

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

                if (!found) {
                        
                   
                throw new InvalidKeyPassedException(new ErrorMessage("errors.deleteNode.NotFound"));
                }
        }

        @Override
        public void deleteClerk(DeleteClerk req) throws DispositionReportFaultMessage, RemoteException {

                boolean found = false;
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, req.getAuthInfo());

                        new ValidatePublish(publisher).validateDeleteClerk(em, req);

                        org.apache.juddi.model.Clerk existingUddiEntity = em.find(org.apache.juddi.model.Clerk.class, req.getClerkID());
                        if (existingUddiEntity != null) { 
                                em.remove(existingUddiEntity);
                                found = true;
                        }
                        tx.commit();

                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

                if (!found) {
                throw new InvalidKeyPassedException(new ErrorMessage("errors.deleteClerk.NotFound"));      
                }
                

        }

        /**
         * enables tmodel owners to setup valid values for tmodel instance infos
         * to use, TODO
         *
         * @param authInfo
         * @param values
         * @return
         * @throws DispositionReportFaultMessage
         */
        @Override
        public DispositionReport setAllValidValues(String authInfo, List<ValidValues> values) throws DispositionReportFaultMessage, RemoteException {

                EntityManager em = PersistenceManager.getEntityManager();
                UddiEntityPublisher entityPublisher = getEntityPublisher(em, authInfo);

                new ValidateValueSetValidation(entityPublisher).validateSetAllValidValues(values);


                EntityTransaction tx = em.getTransaction();
                try {

                        //TODO is this tModel used anywhere?, if so, validate all instances against the new rule?

                        tx.begin();

                        //each tmodel/value set
                        for (int i = 0; i < values.size(); i++) {
                                //remove any existing references to the key
                                ValueSetValues find = em.find(ValueSetValues.class, values.get(i).getTModekKey());

                                if (find != null) {

                                        //first pass, add any missing values
                                        for (int x = 0; x < values.get(i).getValue().size(); x++) {
                                                boolean found = false;
                                                for (int k = 0; k < find.getValues().size(); k++) {
                                                        if (find.getValues().get(k).getValue().equals(values.get(i).getValue().get(x))) {
                                                                found = true;
                                                                break;
                                                        }
                                                }
                                                if (!found) {
                                                        ValueSetValue valueSetValue = new org.apache.juddi.model.ValueSetValue(values.get(i).getTModekKey(), values.get(i).getValue().get(x));
                                                        find.getValues().add(valueSetValue);
                                                }
                                        }

                                        //second pass, remove any values that were present, but now are not
                                        for (int k = 0; k < find.getValues().size(); k++) {

                                                boolean found = false;
                                                for (int x = 0; x < values.get(i).getValue().size(); x++) {
                                                        if (find.getValues().get(k).getValue().equals(values.get(i).getValue().get(x))) {
                                                                found = true;
                                                                break;
                                                        }
                                                }
                                                if (!found) {
                                                        em.remove(find.getValues().get(k));
                                                }
                                        }

                                        em.persist(find);

                                } else {
                                        org.apache.juddi.model.ValueSetValues vv = new ValueSetValues();
                                        vv.setTModelKey(values.get(i).getTModekKey());
                                        List<ValueSetValue> items = new ArrayList<ValueSetValue>();
                                        for (int k = 0; k < values.get(i).getValue().size(); k++) {
                                                ValueSetValue valueSetValue = new org.apache.juddi.model.ValueSetValue(values.get(i).getTModekKey(), values.get(i).getValue().get(k));
                                                valueSetValue.setParent(vv);
                                                em.persist(valueSetValue);
                                                items.add(valueSetValue);
                                        }
                                        vv.setValues(items);
                                        em.persist(vv);
                                }
                        }

                        tx.commit();
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
                DispositionReport r = new DispositionReport();
                r.getResult().add(new Result());
                return r;
        }

        @Override
        public void adminDeleteSubscription(String authInfo, List<String> subscriptionKey) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }


        @Override
        public DispositionReport adminSaveBusiness(String authInfo, List<AdminSaveBusinessWrapper> values) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public DispositionReport adminSaveTModel(String authInfo, List<AdminSaveTModelWrapper> values) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        
         @SuppressWarnings("unchecked")
        @Override
        public List<SubscriptionWrapper> getAllClientSubscriptionInfo(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        
        @Override
        public DispositionReport setReplicationNodes(String authInfo, org.uddi.repl_v3.ReplicationConfiguration replicationConfiguration) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public org.uddi.repl_v3.ReplicationConfiguration getReplicationNodes(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override

    public void adminSaveSubscription(

        String authInfo,

        String publisherOrUsername,

        List<Subscription> subscriptions)
        throws DispositionReportFaultMessage, RemoteException
    {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
}
