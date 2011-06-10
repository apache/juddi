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

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.Holder;

import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.AssertionStatusReport;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.KeyBag;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionPortType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.SecurityQuery;
import org.apache.juddi.api.util.SubscriptionQuery;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.SubscriptionChunkToken;
import org.apache.juddi.model.SubscriptionMatch;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.FindSubscriptionByPublisherQuery;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidValueException;
import org.apache.juddi.validation.ValidateSubscription;

@WebService(serviceName="UDDISubscriptionService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionPortType",
			targetNamespace = "urn:uddi-org:v3_service")
public class UDDISubscriptionImpl extends AuthenticatedService implements UDDISubscriptionPortType {

	private static Log logger = LogFactory.getLog(UDDISubscriptionImpl.class);

	public static final int DEFAULT_SUBSCRIPTIONEXPIRATION_DAYS = 30;
	public static final int DEFAULT_CHUNKEXPIRATION_MINUTES = 5;

	public static final String CHUNK_TOKEN_PREFIX = "chunktoken:";

        private UDDIServiceCounter serviceCounter;

        public UDDISubscriptionImpl() {
            super();
            serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(this.getClass());
        }
	
	public void deleteSubscription(DeleteSubscription body)
			throws DispositionReportFaultMessage {
	        long startTime = System.nanoTime();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
			new ValidateSubscription(publisher).validateDeleteSubscription(em, body);
			
	        List<String> subscriptionKeyList = body.getSubscriptionKey();
	        for (String subscriptionKey : subscriptionKeyList) {
	                Object obj = em.find(org.apache.juddi.model.Subscription.class, subscriptionKey);
	                em.remove(obj);
	        }
	
	        tx.commit();
                long procTime = System.nanoTime() - startTime;
                serviceCounter.update(SubscriptionQuery.DELETE_SUBSCRIPTION, 
                        QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(SubscriptionQuery.DELETE_SUBSCRIPTION, 
                            QueryStatus.FAILED, procTime);                      
                    throw drfm;                                                                                                 
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
   
    public SubscriptionResultsList getSubscriptionResults(GetSubscriptionResults body) throws DispositionReportFaultMessage {
    	return getSubscriptionResults(body, null);
    }
	/* (non-Javadoc)
	 * @see org.uddi.v3_service.UDDISubscriptionPortType#getSubscriptionResults(org.uddi.sub_v3.GetSubscriptionResults)
	 * 
	 * Notes: Does it make sense to refresh the subscription matches on a call to this method?  I don't think so, the user theoretically had
	 * a set of entities in mind when the subscription was saved and the snapshot should remain just that - a snapshot of the entities at the
	 * time of the subscription save.  The result of this policy is that if an entity is deleted, that deleted result will appear in the keyBag
	 * on every call to this method.  To resolve this, the user can renew the subscription at which time the "match" snapshot will be refreshed.
	 * 
	 * The WS needs to be authenticated (null publisher), however the notificationSubscriber is calling this method also. The
	 * notificationSubscriber will pass in the publisher and this method will work in unauthenticated mode.
	 */
	@SuppressWarnings("unchecked")
	public SubscriptionResultsList getSubscriptionResults(GetSubscriptionResults body, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
            
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
                try {
	        tx.begin();
	        
			if (publisher==null) {
				publisher = this.getEntityPublisher(em, body.getAuthInfo());
				new ValidateSubscription(publisher).validateGetSubscriptionResults(em, body);
			}
			
			org.apache.juddi.model.Subscription modelSubscription = em.find(org.apache.juddi.model.Subscription.class, body.getSubscriptionKey());
			SubscriptionFilter subscriptionFilter = null;
			try {
				subscriptionFilter = (SubscriptionFilter)JAXBMarshaller.unmarshallFromString(modelSubscription.getSubscriptionFilter(), JAXBMarshaller.PACKAGE_SUBSCRIPTION);
			} 
			catch (JAXBException e) {
				logger.error("JAXB Exception while unmarshalling subscription filter", e);
				throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
			}
			if (logger.isDebugEnabled()) logger.debug("filter=" + modelSubscription.getSubscriptionFilter());
			
			SubscriptionResultsList result = new SubscriptionResultsList();
			result.setCoveragePeriod(body.getCoveragePeriod());
	
			// The subscription structure is required output for the results
			org.uddi.sub_v3.Subscription apiSubscription = new org.uddi.sub_v3.Subscription();
			MappingModelToApi.mapSubscription(modelSubscription, apiSubscription);
			result.setSubscription(apiSubscription);
			
			Date startPointDate = new Date(body.getCoveragePeriod().getStartPoint().toGregorianCalendar().getTimeInMillis());
			Date endPointDate = new Date(body.getCoveragePeriod().getEndPoint().toGregorianCalendar().getTimeInMillis());
	
			Integer chunkData = null;
			if (body.getChunkToken() != null && body.getChunkToken().length() > 0) {
				SubscriptionChunkToken chunkToken = em.find(SubscriptionChunkToken.class, body.getChunkToken());
				
				if (chunkToken == null)
					throw new InvalidValueException(new ErrorMessage("errors.getsubscriptionresult.InvalidChunkToken", body.getChunkToken()));
				if (!chunkToken.getSubscriptionKey().equals(chunkToken.getSubscriptionKey()))
					throw new InvalidValueException(new ErrorMessage("errors.getsubscriptionresult.NonMatchingChunkToken", body.getChunkToken()));
				if (chunkToken.getStartPoint() != null && chunkToken.getStartPoint().getTime() != startPointDate.getTime())
					throw new InvalidValueException(new ErrorMessage("errors.getsubscriptionresult.NonMatchingChunkToken", body.getChunkToken()));
				if (chunkToken.getEndPoint() != null && chunkToken.getEndPoint().getTime() != endPointDate.getTime())
					throw new InvalidValueException(new ErrorMessage("errors.getsubscriptionresult.NonMatchingChunkToken", body.getChunkToken()));
				if (chunkToken.getExpiresAfter().before(new Date()))
					throw new InvalidValueException(new ErrorMessage("errors.getsubscriptionresult.ExpiredChunkToken", body.getChunkToken()));
				
				chunkData = chunkToken.getData();
				// We've got the data from the chunk token, now it is no longer needed (once it's called, it's used up)
				em.remove(chunkToken);
			}
	
			
			if (subscriptionFilter.getFindBinding() != null) {
				//Get the current matching keys
				List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);
				// See if there's any missing keys by comparing against the previous matches.  If so, they missing keys are added to the KeyBag and
				// then added to the result
				List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
				if (missingKeys != null && missingKeys.size() > 0) {
					KeyBag missingKeyBag = new KeyBag();
					missingKeyBag.setDeleted(true);
					for (String key : missingKeys)
						missingKeyBag.getBindingKey().add(key);
					
					result.getKeyBag().add(missingKeyBag);
				}
				
				// Re-setting the subscription matches to the new matching key collection
				//modelSubscription.getSubscriptionMatches().clear();
				//for (Object key : currentMatchingKeys) {
				//	SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				//	modelSubscription.getSubscriptionMatches().add(subMatch);
				//}
				
				// Now, finding the necessary entities, within the coverage period limits
				if (modelSubscription.isBrief()) {
					KeyBag resultsKeyBag = new KeyBag();
					for (String key : (List<String>)currentMatchingKeys)
						resultsKeyBag.getBindingKey().add(key);
					
					result.getKeyBag().add(resultsKeyBag);
				}
				else {
					FindBinding fb = subscriptionFilter.getFindBinding();
					org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
					findQualifiers.mapApiFindQualifiers(fb.getFindQualifiers());
					
					// To do subscription "chunking", the listHead and maxRows are nulled which will set them to system default.  User settings for
					// these values don't make sense with the "chunking" feature.
					fb.setListHead(null);
					fb.setMaxRows(null);
					// Setting the start index to the chunkData
					Holder<Integer> subscriptionStartIndex = new Holder<Integer>(chunkData);
					
					BindingDetail bindingDetail = InquiryHelper.getBindingDetailFromKeys(fb, findQualifiers, em, currentMatchingKeys,
																						 startPointDate, endPointDate, subscriptionStartIndex, modelSubscription.getMaxEntities());
							
					// Upon exiting above function, if more results are to be had, the subscriptionStartIndex will contain the latest value (or null
					// if no more results)
					chunkData = subscriptionStartIndex.value;
					
					result.setBindingDetail(bindingDetail);
				}
			}
			if (subscriptionFilter.getFindBusiness() != null) {
				//Get the current matching keys
				List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);
	
				List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
				if (missingKeys != null && missingKeys.size() > 0) {
					KeyBag missingKeyBag = new KeyBag();
					missingKeyBag.setDeleted(true);
					for (String key : missingKeys)
						missingKeyBag.getBusinessKey().add(key);
	
					result.getKeyBag().add(missingKeyBag);
				}
				
				// Re-setting the subscription matches to the new matching key collection
				//modelSubscription.getSubscriptionMatches().clear();
				//for (Object key : currentMatchingKeys) {
				//	SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				//	modelSubscription.getSubscriptionMatches().add(subMatch);
				//}
				
				// Now, finding the necessary entities, within the coverage period limits
				if (modelSubscription.isBrief()) {
					KeyBag resultsKeyBag = new KeyBag();
					for (String key : (List<String>)currentMatchingKeys)
						resultsKeyBag.getBusinessKey().add(key);
					
					result.getKeyBag().add(resultsKeyBag);
				}
				else {
					FindBusiness fb = subscriptionFilter.getFindBusiness();
					org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
					findQualifiers.mapApiFindQualifiers(fb.getFindQualifiers());
					
					// To do subscription "chunking", the listHead and maxRows are nulled which will set them to system default.  User settings for
					// these values don't make sense with the "chunking" feature.
					fb.setListHead(null);
					fb.setMaxRows(null);
					// Setting the start index to the chunkData
					Holder<Integer> subscriptionStartIndex = new Holder<Integer>(chunkData);
					
					BusinessList businessList = InquiryHelper.getBusinessListFromKeys(fb, findQualifiers, em, currentMatchingKeys,
																					  startPointDate, endPointDate, subscriptionStartIndex, modelSubscription.getMaxEntities());
					
					// Upon exiting above function, if more results are to be had, the subscriptionStartIndex will contain the latest value (or null
					// if no more results)
					chunkData = subscriptionStartIndex.value;
					
					result.setBusinessList(businessList);
				}
			}
			if (subscriptionFilter.getFindService() != null) {
				//Get the current matching keys
				List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);
				if (logger.isDebugEnabled()) logger.debug("current matching keys=" + currentMatchingKeys);
				List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
				if (missingKeys != null && missingKeys.size() > 0) {
					KeyBag missingKeyBag = new KeyBag();
					missingKeyBag.setDeleted(true);
					for (String key : missingKeys)
						missingKeyBag.getServiceKey().add(key);
	
					result.getKeyBag().add(missingKeyBag);
				}
				
				// Re-setting the subscription matches to the new matching key collection
				//modelSubscription.getSubscriptionMatches().clear();
				//for (Object key : currentMatchingKeys) {
				//	SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				//	modelSubscription.getSubscriptionMatches().add(subMatch);
				//}
				
				// Now, finding the necessary entities, within the coverage period limits
				if (modelSubscription.isBrief()) {
					KeyBag resultsKeyBag = new KeyBag();
					for (String key : (List<String>)currentMatchingKeys)
						resultsKeyBag.getServiceKey().add(key);
					
					result.getKeyBag().add(resultsKeyBag);
				}
				else {
					FindService fs = subscriptionFilter.getFindService();
					org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
					findQualifiers.mapApiFindQualifiers(fs.getFindQualifiers());
					
					// To do subscription "chunking", the listHead and maxRows are nulled which will set them to system default.  User settings for
					// these values don't make sense with the "chunking" feature.
					fs.setListHead(null);
					fs.setMaxRows(null);
					// Setting the start index to the chunkData
					Holder<Integer> subscriptionStartIndex = new Holder<Integer>(chunkData);
	
					ServiceList serviceList = InquiryHelper.getServiceListFromKeys(fs, findQualifiers, em, currentMatchingKeys,
																				   startPointDate, endPointDate, subscriptionStartIndex, modelSubscription.getMaxEntities());
					if (serviceList.getServiceInfos()==null || serviceList.getServiceInfos().getServiceInfo().size()==0) {
						serviceList=null;
					}
					// Upon exiting above function, if more results are to be had, the subscriptionStartIndex will contain the latest value (or null
					// if no more results)
					chunkData = subscriptionStartIndex.value;
					
					result.setServiceList(serviceList);
				}
			}
			if (subscriptionFilter.getFindTModel() != null) {
				//Get the current matching keys
				List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);
	
				List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
				if (missingKeys != null && missingKeys.size() > 0) {
					KeyBag missingKeyBag = new KeyBag();
					missingKeyBag.setDeleted(true);
					for (String key : missingKeys)
						missingKeyBag.getTModelKey().add(key);
	
					result.getKeyBag().add(missingKeyBag);
				}
				
				// Re-setting the subscription matches to the new matching key collection
				//modelSubscription.getSubscriptionMatches().clear();
				//for (Object key : currentMatchingKeys) {
				//	SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				//	modelSubscription.getSubscriptionMatches().add(subMatch);
				//}
				
				// Now, finding the necessary entities, within the coverage period limits
				if (modelSubscription.isBrief()) {
					KeyBag resultsKeyBag = new KeyBag();
					for (String key : (List<String>)currentMatchingKeys)
						resultsKeyBag.getTModelKey().add(key);
					
					result.getKeyBag().add(resultsKeyBag);
				}
				else {
					FindTModel ft = subscriptionFilter.getFindTModel();
					org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
					findQualifiers.mapApiFindQualifiers(ft.getFindQualifiers());
	
					// To do subscription "chunking", the listHead and maxRows are nulled which will set them to system default.  User settings for
					// these values don't make sense with the "chunking" feature.
					ft.setListHead(null);
					ft.setMaxRows(null);
					// Setting the start index to the chunkData
					Holder<Integer> subscriptionStartIndex = new Holder<Integer>(chunkData);
					
					// If more results are to be had, chunkData will come out with a value and a new token will be generated below.  Otherwise, it will
					// be null and no token will be generated.
					TModelList tmodelList = InquiryHelper.getTModelListFromKeys(ft, findQualifiers, em, currentMatchingKeys, 
																				startPointDate, endPointDate, subscriptionStartIndex, modelSubscription.getMaxEntities());
	
					// Upon exiting above function, if more results are to be had, the subscriptionStartIndex will contain the latest value (or null
					// if no more results)
					chunkData = subscriptionStartIndex.value;
					
					result.setTModelList(tmodelList);
				}
				
			}
			if (subscriptionFilter.getFindRelatedBusinesses() != null) {
				FindRelatedBusinesses findRelatedBusiness = subscriptionFilter.getFindRelatedBusinesses();
				RelatedBusinessesList  relatedBusinessList = InquiryHelper.getRelatedBusinessesList(findRelatedBusiness, em, startPointDate, endPointDate);
				result.setRelatedBusinessesList(relatedBusinessList);
			}
			if (subscriptionFilter.getGetBindingDetail() != null) {
				GetBindingDetail getDetail = subscriptionFilter.getGetBindingDetail();
				
				// Running through the key list here to determine the deleted keys and store the existing entities.
				KeyBag missingKeyBag = new KeyBag();
				missingKeyBag.setDeleted(true);
				List<org.apache.juddi.model.BindingTemplate> existingList = new ArrayList<org.apache.juddi.model.BindingTemplate>(0);
				for (String key : getDetail.getBindingKey()) {
					org.apache.juddi.model.BindingTemplate modelBindingTemplate = em.find(org.apache.juddi.model.BindingTemplate.class, key);
					if (modelBindingTemplate != null)
						existingList.add(modelBindingTemplate);
					else
						missingKeyBag.getBindingKey().add(key);
				}
				// Store deleted keys in the results
				if (missingKeyBag.getBindingKey() != null && missingKeyBag.getBindingKey().size() > 0)
					result.getKeyBag().add(missingKeyBag);
				
				KeyBag resultsKeyBag = new KeyBag();
				BindingDetail bindingDetail = new BindingDetail();
	
				// Set the currentIndex to 0 or the value of the chunkData
				int currentIndex = 0;
				if (chunkData != null)
					currentIndex = chunkData;
	
				int returnedRowCount = 0;
				while(currentIndex < existingList.size()) {
	
					org.apache.juddi.model.BindingTemplate modelBindingTemplate = existingList.get(currentIndex);
						
					if (startPointDate.after(modelBindingTemplate.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (endPointDate.before(modelBindingTemplate.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (modelSubscription.isBrief()) {
						resultsKeyBag.getBindingKey().add(modelBindingTemplate.getEntityKey());
					}
					else {
						org.uddi.api_v3.BindingTemplate apiBindingTemplate = new org.uddi.api_v3.BindingTemplate();
						MappingModelToApi.mapBindingTemplate(modelBindingTemplate, apiBindingTemplate);
						bindingDetail.getBindingTemplate().add(apiBindingTemplate);
						
						returnedRowCount++;
					}
	
					// If the returned rows equals the max allowed, we can end the loop.
					if (modelSubscription.getMaxEntities() != null) {
						if (returnedRowCount == modelSubscription.getMaxEntities())
							break;
					}
	
					currentIndex++;
				}
				
				// If the loop was broken prematurely (max row count hit) we set the chunk data to the next index to start with.
				// A non-null value of chunk data will cause a chunk token to be generated. 
				if (currentIndex < (existingList.size() - 1))
					chunkData = currentIndex + 1;
				else
					chunkData = null;
				
				if (modelSubscription.isBrief())
					result.getKeyBag().add(resultsKeyBag);
				else
					result.setBindingDetail(bindingDetail);
				
				
			}
			if (subscriptionFilter.getGetBusinessDetail() != null) {
				GetBusinessDetail getDetail = subscriptionFilter.getGetBusinessDetail();
	
				// Running through the key list here to determine the deleted keys and store the existing entities.
				KeyBag missingKeyBag = new KeyBag();
				missingKeyBag.setDeleted(true);
				List<org.apache.juddi.model.BusinessEntity> existingList = new ArrayList<org.apache.juddi.model.BusinessEntity>(0);
				for (String key : getDetail.getBusinessKey()) {
					org.apache.juddi.model.BusinessEntity modelBusinessEntity = em.find(org.apache.juddi.model.BusinessEntity.class, key);
					if (modelBusinessEntity != null)
						existingList.add(modelBusinessEntity);
					else
						missingKeyBag.getBusinessKey().add(key);
				}
				// Store deleted keys in the results
				if (missingKeyBag.getBusinessKey() != null && missingKeyBag.getBusinessKey().size() > 0)
					result.getKeyBag().add(missingKeyBag);
				
				KeyBag resultsKeyBag = new KeyBag();
				BusinessDetail businessDetail = new BusinessDetail();
	
				// Set the currentIndex to 0 or the value of the chunkData
				int currentIndex = 0;
				if (chunkData != null)
					currentIndex = chunkData;
				
				int returnedRowCount = 0;
				while(currentIndex < existingList.size()) {
	
					org.apache.juddi.model.BusinessEntity modelBusinessEntity = existingList.get(currentIndex);
	
					if (startPointDate.after(modelBusinessEntity.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (endPointDate.before(modelBusinessEntity.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (modelSubscription.isBrief()) {
						resultsKeyBag.getBusinessKey().add(modelBusinessEntity.getEntityKey());
					}
					else {
						org.uddi.api_v3.BusinessEntity apiBusinessEntity = new org.uddi.api_v3.BusinessEntity();
						MappingModelToApi.mapBusinessEntity(modelBusinessEntity, apiBusinessEntity);
						businessDetail.getBusinessEntity().add(apiBusinessEntity);
						
						returnedRowCount++;
					}
	
					// If the returned rows equals the max allowed, we can end the loop.
					if (modelSubscription.getMaxEntities() != null) {
						if (returnedRowCount == modelSubscription.getMaxEntities())
							break;
					}
	
					currentIndex++;
				}
				
				// If the loop was broken prematurely (max row count hit) we set the chunk data to the next index to start with.
				// A non-null value of chunk data will cause a chunk token to be generated. 
				if (currentIndex < (existingList.size() - 1))
					chunkData = currentIndex + 1;
				else
					chunkData = null;
				
				if (modelSubscription.isBrief())
					result.getKeyBag().add(resultsKeyBag);
				else
					result.setBusinessDetail(businessDetail);
				
			}
			if (subscriptionFilter.getGetServiceDetail() != null) {
				GetServiceDetail getDetail = subscriptionFilter.getGetServiceDetail();
	
				// Running through the key list here to determine the deleted keys and store the existing entities.
				KeyBag missingKeyBag = new KeyBag();
				missingKeyBag.setDeleted(true);
				List<org.apache.juddi.model.BusinessService> existingList = new ArrayList<org.apache.juddi.model.BusinessService>(0);
				for (String key : getDetail.getServiceKey()) {
					org.apache.juddi.model.BusinessService modelBusinessService = em.find(org.apache.juddi.model.BusinessService.class, key);
					if (modelBusinessService != null)
						existingList.add(modelBusinessService);
					else
						missingKeyBag.getBusinessKey().add(key);
				}
				// Store deleted keys in the results
				if (missingKeyBag.getServiceKey() != null && missingKeyBag.getServiceKey().size() > 0)
					result.getKeyBag().add(missingKeyBag);
	
				KeyBag resultsKeyBag = new KeyBag();
				ServiceDetail serviceDetail = new ServiceDetail();
				
				// Set the currentIndex to 0 or the value of the chunkData
				int currentIndex = 0;
				if (chunkData != null)
					currentIndex = chunkData;
				
				int returnedRowCount = 0;
				while(currentIndex < existingList.size()) {
					
					org.apache.juddi.model.BusinessService modelBusinessService = existingList.get(currentIndex);
						
					if (startPointDate.after(modelBusinessService.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (endPointDate.before(modelBusinessService.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (modelSubscription.isBrief()) {
						resultsKeyBag.getServiceKey().add(modelBusinessService.getEntityKey());
					}
					else {
						org.uddi.api_v3.BusinessService apiBusinessService = new org.uddi.api_v3.BusinessService();
						MappingModelToApi.mapBusinessService(modelBusinessService, apiBusinessService);
						serviceDetail.getBusinessService().add(apiBusinessService);
						
						returnedRowCount++;
					}
	
					// If the returned rows equals the max allowed, we can end the loop.
					if (modelSubscription.getMaxEntities() != null) {
						if (returnedRowCount == modelSubscription.getMaxEntities())
							break;
					}
	
					currentIndex++;
				}
				
				// If the loop was broken prematurely (max row count hit) we set the chunk data to the next index to start with.
				// A non-null value of chunk data will cause a chunk token to be generated. 
				if (currentIndex < (existingList.size() - 1))
					chunkData = currentIndex + 1;
				else
					chunkData = null;
				
				if (modelSubscription.isBrief())
					result.getKeyBag().add(resultsKeyBag);
				else
					result.setServiceDetail(serviceDetail);
				
			}
			if (subscriptionFilter.getGetTModelDetail() != null) {
				GetTModelDetail getDetail = subscriptionFilter.getGetTModelDetail();
	
				// Running through the key list here to determine the deleted keys and store the existing entities.
				KeyBag missingKeyBag = new KeyBag();
				missingKeyBag.setDeleted(true);
				List<org.apache.juddi.model.Tmodel> existingList = new ArrayList<org.apache.juddi.model.Tmodel>(0);
				for (String key : getDetail.getTModelKey()) {
					org.apache.juddi.model.Tmodel modelTModel = em.find(org.apache.juddi.model.Tmodel.class, key);
					if (modelTModel != null)
						existingList.add(modelTModel);
					else
						missingKeyBag.getTModelKey().add(key);
				}
				// Store deleted keys in the results
				if (missingKeyBag.getTModelKey() != null && missingKeyBag.getTModelKey().size() > 0)
					result.getKeyBag().add(missingKeyBag);
				
				KeyBag resultsKeyBag = new KeyBag();
				TModelDetail tmodelDetail = new TModelDetail();
	
				// Set the currentIndex to 0 or the value of the chunkData
				int currentIndex = 0;
				if (chunkData != null)
					currentIndex = chunkData;
				
				int returnedRowCount = 0;
				while(currentIndex < existingList.size()) {
	
					org.apache.juddi.model.Tmodel modelTModel = existingList.get(currentIndex);
						
					if (startPointDate.after(modelTModel.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (endPointDate.before(modelTModel.getModifiedIncludingChildren())) {
						currentIndex++;
						continue;
					}
					
					if (modelSubscription.isBrief()) {
						resultsKeyBag.getTModelKey().add(modelTModel.getEntityKey());
					}
					else {
						org.uddi.api_v3.TModel apiTModel = new org.uddi.api_v3.TModel();
						MappingModelToApi.mapTModel(modelTModel, apiTModel);
						tmodelDetail.getTModel().add(apiTModel);
						
						returnedRowCount++;
					}
	
					// If the returned rows equals the max allowed, we can end the loop.
					if (modelSubscription.getMaxEntities() != null) {
						if (returnedRowCount == modelSubscription.getMaxEntities())
							break;
					}
	
					currentIndex++;
				}
				
				// If the loop was broken prematurely (max row count hit) we set the chunk data to the next index to start with.
				// A non-null value of chunk data will cause a chunk token to be generated. 
				if (currentIndex < (existingList.size() - 1))
					chunkData = currentIndex + 1;
				else
					chunkData = null;
				
				if (modelSubscription.isBrief())
					result.getKeyBag().add(resultsKeyBag);
				else
					result.setTModelDetail(tmodelDetail);
				
			}
			if (subscriptionFilter.getGetAssertionStatusReport() != null) {
				// The coverage period doesn't apply here (basically because publisher assertions don't keep operational info).
				
				GetAssertionStatusReport getAssertionStatusReport = subscriptionFilter.getGetAssertionStatusReport();
				
				List<AssertionStatusItem> assertionList = PublicationHelper.getAssertionStatusItemList(publisher, getAssertionStatusReport.getCompletionStatus(), em);
	
				AssertionStatusReport assertionStatusReport  = new AssertionStatusReport();
				for(AssertionStatusItem asi : assertionList)
					assertionStatusReport.getAssertionStatusItem().add(asi);
				
				result.setAssertionStatusReport(assertionStatusReport);
			}
			
			// If chunkData contains non-null data, a new token must be created and the token returned in the results
			if (chunkData != null) {
				String chunkToken = CHUNK_TOKEN_PREFIX + UUID.randomUUID();
				SubscriptionChunkToken newChunkToken = new SubscriptionChunkToken(chunkToken);
				newChunkToken.setSubscriptionKey(body.getSubscriptionKey());
				newChunkToken.setStartPoint(startPointDate);
				newChunkToken.setEndPoint(endPointDate);
				newChunkToken.setData(chunkData);
	
				int chunkExpirationMinutes = DEFAULT_CHUNKEXPIRATION_MINUTES;
				try { 
					chunkExpirationMinutes = AppConfig.getConfiguration().getInt(Property.JUDDI_SUBSCRIPTION_CHUNKEXPIRATION_MINUTES); 
				}
				catch(ConfigurationException ce) { 
					throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval"));
				}
				newChunkToken.setExpiresAfter(new Date(System.currentTimeMillis() + chunkExpirationMinutes * 60 * 1000));
				
				em.persist(newChunkToken);
				
				result.setChunkToken(chunkToken);
			}
			
	        tx.commit();
                long procTime = System.nanoTime() - startTime;
                serviceCounter.update(SubscriptionQuery.GET_SUBSCRIPTIONRESULTS, 
                        QueryStatus.SUCCESS, procTime);

	        return result;
        } catch (DispositionReportFaultMessage drfm) {
            long procTime = System.nanoTime() - startTime;
            serviceCounter.update(SubscriptionQuery.GET_SUBSCRIPTIONRESULTS, 
                    QueryStatus.FAILED, procTime);                      
            throw drfm;                                                                                                 
        } finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Subscription> getSubscriptions(String authInfo)
			throws DispositionReportFaultMessage {
	        long startTime = System.nanoTime();
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();
	
			UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);
			
			List<Subscription> result = new ArrayList<Subscription>(0);
			
			List<org.apache.juddi.model.Subscription> modelSubscriptionList = (List<org.apache.juddi.model.Subscription>)FindSubscriptionByPublisherQuery.select(em, publisher.getAuthorizedName());
			if (modelSubscriptionList != null && modelSubscriptionList.size() > 0) {
				for (org.apache.juddi.model.Subscription modelSubscription : modelSubscriptionList) {
					
					Subscription apiSubscription = new Subscription();
					
					MappingModelToApi.mapSubscription(modelSubscription, apiSubscription);
					
					result.add(apiSubscription);
				}
			}
	        
	        tx.commit();
                long procTime = System.nanoTime() - startTime;
                serviceCounter.update(SubscriptionQuery.GET_SUBSCRIPTIONS, 
                        QueryStatus.SUCCESS, procTime);

		return result;
        } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(SubscriptionQuery.GET_SUBSCRIPTIONS, 
                            QueryStatus.FAILED, procTime);                      
                    throw drfm;                                                                                                 		
        } finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}


	/* (non-Javadoc)
	 * @see org.uddi.v3_service.UDDISubscriptionPortType#saveSubscription(java.lang.String, javax.xml.ws.Holder)
	 * 
	 * Notes: The matching keys are saved on a new subscription (or renewed subscription) for the find_* filters only.  With the other filter 
	 * types, taking a snapshot of the matches doesn't make sense.
	 * 
	 */
	public void saveSubscription(String authInfo,
			Holder<List<Subscription>> subscription)
			throws DispositionReportFaultMessage {
	        long startTime = System.nanoTime();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);
			new ValidateSubscription(publisher).validateSubscriptions(em, subscription.value);
			
			List<org.uddi.sub_v3.Subscription> apiSubscriptionList = subscription.value;
			for (org.uddi.sub_v3.Subscription apiSubscription : apiSubscriptionList) {
				
				org.apache.juddi.model.Subscription modelSubscription = new org.apache.juddi.model.Subscription();
				
				Object existing = em.find(org.apache.juddi.model.Subscription.class, apiSubscription.getSubscriptionKey());
				if (existing != null) {
					org.apache.juddi.model.Subscription existingEntity = (org.apache.juddi.model.Subscription) existing;
					doRenewal(existingEntity, apiSubscription);
					//carrying over the created and last notified dates if this is a renewal.
					modelSubscription.setCreateDate(existingEntity.getCreateDate());
					modelSubscription.setLastNotified(existingEntity.getLastNotified());
					em.remove(existing);
				} else {
					modelSubscription.setCreateDate(new Date());
				}
	
				doSubscriptionExpirationDate(apiSubscription);
				
				MappingApiToModel.mapSubscription(apiSubscription, modelSubscription);
				
				modelSubscription.setAuthorizedName(publisher.getAuthorizedName());
				
				// Add the matching keys to the match collection
				List<?> keys = getSubscriptionMatches(apiSubscription.getSubscriptionFilter(), em);
				if (keys != null && keys.size() > 0) {
					for (Object key : keys) {
						SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
						modelSubscription.getSubscriptionMatches().add(subMatch);
					}
				}
				
				em.persist(modelSubscription);
			}
	
			tx.commit();
	                long procTime = System.nanoTime() - startTime;
	                serviceCounter.update(SubscriptionQuery.SAVE_SUBSCRIPTION, 
	                        QueryStatus.SUCCESS, procTime);
	        } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(SubscriptionQuery.SAVE_SUBSCRIPTION, 
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
	 * Will perform the necessary logic for when a subscription is renewed (evidenced by a subscription with the same key in existence).  
	 * In general, the appropriate data is copied from the stored subscription to the renewal subscription request.
	 * 
	 * @param existingSubscription - existing stored subscription
	 * @param apiSubscription - renewal subscription request
	 * @throws DispositionReportFaultMessage 
	 */
	private void doRenewal(org.apache.juddi.model.Subscription existingSubscription, org.uddi.sub_v3.Subscription apiSubscription) throws DispositionReportFaultMessage {
		if (apiSubscription.getSubscriptionFilter() == null) {
			String rawFilter = existingSubscription.getSubscriptionFilter();
			try {
				SubscriptionFilter existingFilter = (SubscriptionFilter)JAXBMarshaller.unmarshallFromString(rawFilter, "org.uddi.sub_v3");
				apiSubscription.setSubscriptionFilter(existingFilter);
			} 
			catch (JAXBException e) {
				logger.error("JAXB Exception while marshalling subscription filter", e);
				throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
			} 
		}
		
	}
	
	/**
	 * Will add the expiration date to the provided subscription request.  Date is earlier of user provided date and the system default
	 * 
	 * @param apiSubscription
	 * @throws DispositionReportFaultMessage
	 */
	private void doSubscriptionExpirationDate(org.uddi.sub_v3.Subscription apiSubscription) throws DispositionReportFaultMessage {

		int subscriptionExpirationDays = DEFAULT_SUBSCRIPTIONEXPIRATION_DAYS;
		try { 
			subscriptionExpirationDays = AppConfig.getConfiguration().getInt(Property.JUDDI_SUBSCRIPTION_EXPIRATION_DAYS); 
		}
		catch(ConfigurationException ce) { 
			throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval"));
		}

		GregorianCalendar expirationDate = new GregorianCalendar();
		expirationDate.add(GregorianCalendar.DAY_OF_MONTH, subscriptionExpirationDays);
		
		// The expiration date is the earlier of the provided date and that specified by the parameter.
		if (apiSubscription.getExpiresAfter() != null) {
			GregorianCalendar userExpiration = apiSubscription.getExpiresAfter().toGregorianCalendar();
			if (userExpiration.getTimeInMillis() < expirationDate.getTimeInMillis())
				expirationDate.setTimeInMillis(userExpiration.getTimeInMillis());
		}

		try { 
			DatatypeFactory df = DatatypeFactory.newInstance();
			apiSubscription.setExpiresAfter(df.newXMLGregorianCalendar(expirationDate));
		}
		catch(DatatypeConfigurationException ce) { 
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		}
		
	}

	/**
	 * Will take a snapshot of the keys that match the subscription filter return them.  Currently, keys are only returned for the find_*
	 * filters.  It seems redundant to return the keys in the get_*Detail filters.
	 * 
	 * @param subscriptionFilter
	 * @param em
	 * @return
	 * @throws DispositionReportFaultMessage
	 */
	private List<?> getSubscriptionMatches(SubscriptionFilter subscriptionFilter, EntityManager em) 
			 throws DispositionReportFaultMessage {
		
		
		List<?> keys = null;
		if (subscriptionFilter.getFindBinding() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindBinding().getFindQualifiers());
			keys = InquiryHelper.findBinding(subscriptionFilter.getFindBinding(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindBusiness() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindBusiness().getFindQualifiers());
			keys = InquiryHelper.findBusiness(subscriptionFilter.getFindBusiness(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindService() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindService().getFindQualifiers());
			keys = InquiryHelper.findService(subscriptionFilter.getFindService(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindTModel() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindTModel().getFindQualifiers());
			keys = InquiryHelper.findTModel(subscriptionFilter.getFindTModel(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindRelatedBusinesses() != null) {
			// TODO: should we bother taking a snapshot of these?
		}
		if (subscriptionFilter.getGetBindingDetail() != null) {
			//keys = subscriptionFilter.getGetBindingDetail().getBindingKey();
			// Nothing needs to be done
		}
		if (subscriptionFilter.getGetBusinessDetail() != null) {
			//keys = subscriptionFilter.getGetBusinessDetail().getBusinessKey();
			// Nothing needs to be done
		}
		if (subscriptionFilter.getGetServiceDetail() != null) {
			//keys = subscriptionFilter.getGetServiceDetail().getServiceKey();
			// Nothing needs to be done
		}
		if (subscriptionFilter.getGetTModelDetail() != null) {
			//keys = subscriptionFilter.getGetTModelDetail().getTModelKey();
			// Nothing needs to be done
		}
		if (subscriptionFilter.getGetAssertionStatusReport() != null) {
			// Nothing needs to be done
		}
		return keys;
		
	}
	
	private List<String> getMissingKeys(List<?> currentMatchingKeys, List<SubscriptionMatch> subscriptionMatches) {

		List<String> result = new ArrayList<String>(subscriptionMatches.size());
		for (SubscriptionMatch subMatch : subscriptionMatches)
			result.add(subMatch.getEntityKey());
		
		result.removeAll(currentMatchingKeys);
		
		return result;
	}
	
}
