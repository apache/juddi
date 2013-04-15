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
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.query.FetchBindingTemplatesQuery;
import org.apache.juddi.query.FetchBusinessEntitiesQuery;
import org.apache.juddi.query.FetchBusinessServicesQuery;
import org.apache.juddi.query.FetchTModelsQuery;
import org.apache.juddi.query.FindBindingByCategoryGroupQuery;
import org.apache.juddi.query.FindBindingByCategoryQuery;
import org.apache.juddi.query.FindBindingByTModelKeyQuery;
import org.apache.juddi.query.FindBusinessByCategoryGroupQuery;
import org.apache.juddi.query.FindBusinessByCategoryQuery;
import org.apache.juddi.query.FindBusinessByCombinedCategoryQuery;
import org.apache.juddi.query.FindBusinessByDiscoveryURLQuery;
import org.apache.juddi.query.FindBusinessByIdentifierQuery;
import org.apache.juddi.query.FindBusinessByNameQuery;
import org.apache.juddi.query.FindBusinessByTModelKeyQuery;
import org.apache.juddi.query.FindServiceByCategoryGroupQuery;
import org.apache.juddi.query.FindServiceByCategoryQuery;
import org.apache.juddi.query.FindServiceByCombinedCategoryQuery;
import org.apache.juddi.query.FindServiceByNameQuery;
import org.apache.juddi.query.FindServiceByTModelKeyQuery;
import org.apache.juddi.query.FindTModelByCategoryGroupQuery;
import org.apache.juddi.query.FindTModelByCategoryQuery;
import org.apache.juddi.query.FindTModelByIdentifierQuery;
import org.apache.juddi.query.FindTModelByNameQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.Direction;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.ListDescription;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelBag;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**Co
 * Used to factor out inquiry functionality as it is used in more than one spot.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class InquiryHelper {

	private static Log logger = LogFactory.getLog(InquiryHelper.class);
	
	public static List<?> findBinding(FindBinding body, FindQualifiers findQualifiers, EntityManager em) throws DispositionReportFaultMessage {

		List<?> keysFound = null;

		// First perform the embedded FindTModel search which will augment the tModel bag with any resulting tModel keys.
		if (body.getTModelBag() == null)
			body.setTModelBag(new TModelBag());
		doFindTModelEmbeddedSearch(em, body.getFindQualifiers(), body.getFindTModel(), body.getTModelBag());
		
		keysFound = FindBindingByTModelKeyQuery.select(em, findQualifiers, body.getTModelBag(), body.getServiceKey(), keysFound);
		keysFound = FindBindingByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), body.getServiceKey(), keysFound);
		keysFound = FindBindingByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), body.getServiceKey(), keysFound);
		
		return keysFound;
	}
	
	public static BindingDetail getBindingDetailFromKeys(FindBinding body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound) throws DispositionReportFaultMessage {
		return getBindingDetailFromKeys(body, findQualifiers, em, keysFound, null, null, null, null);
	}
	
	public static BindingDetail getBindingDetailFromKeys(FindBinding body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound,
														 Date modifiedAfter, Date modifiedBefore, Holder<Integer> subscriptionStartIndex, Integer subscriptionMaxRows)
				   throws DispositionReportFaultMessage {

		BindingDetail result = new BindingDetail();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);
		
		// Sort and retrieve the final results with paging taken into account
		List<?> queryResults = FetchBindingTemplatesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);

		// Set the currentIndex to 0 or the value of the subscriptionStartIndex
		int currentIndex = 0;
		if (subscriptionStartIndex != null && subscriptionStartIndex.value != null)
			currentIndex = subscriptionStartIndex.value;

		int returnedRowCount = 0;
		
		while (currentIndex < queryResults.size()) {
			Object item = queryResults.get(currentIndex);

			org.apache.juddi.model.BindingTemplate modelBindingTemplate = (org.apache.juddi.model.BindingTemplate)item;
			org.uddi.api_v3.BindingTemplate apiBindingTemplate = new org.uddi.api_v3.BindingTemplate();
			
			if (modifiedAfter != null && modifiedAfter.after(modelBindingTemplate.getModifiedIncludingChildren())) {
				currentIndex++;
				continue;
			}
			
			if (modifiedBefore != null && modifiedBefore.before(modelBindingTemplate.getModifiedIncludingChildren())) {
				currentIndex++;
				continue;
			}
			
			MappingModelToApi.mapBindingTemplate(modelBindingTemplate, apiBindingTemplate);
			
			result.getBindingTemplate().add(apiBindingTemplate);

			returnedRowCount++;
			// If the returned rows equals the max allowed, we can end the loop (applies to subscription calls only)
			if (subscriptionMaxRows != null) {
				if (returnedRowCount == subscriptionMaxRows)
					break;
			}

			currentIndex++;
		}

		// If the loop was broken prematurely (max row count hit) we set the subscriptionStartIndex to the next index to start with.
		// Otherwise, set it to null so the subscription call won't trigger chunk token generation. 
		if (currentIndex < (queryResults.size() - 1)) {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = currentIndex + 1;
		}
		else {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = null;
		}
		
		return result;
	}	
	
	public static List<?> findBusiness(FindBusiness body, FindQualifiers findQualifiers, EntityManager em) throws DispositionReportFaultMessage {

		List<?> keysFound = null;

		// First perform the embedded FindTModel search which will augment the tModel bag with any resulting tModel keys.
		if (body.getTModelBag() == null)
			body.setTModelBag(new TModelBag());
		doFindTModelEmbeddedSearch(em, body.getFindQualifiers(), body.getFindTModel(), body.getTModelBag());
		
		// The embedded find_relatedBusinesses search is performed first.  This is done the same as the actual API call, except the resulting business keys are 
		// extracted and placed in the keysFound array to restrict future searches to only those keys.
		if (body.getFindRelatedBusinesses() != null) {
			FindRelatedBusinesses frb = body.getFindRelatedBusinesses();
			
			org.uddi.api_v3.RelatedBusinessInfos relatedBusinessInfos = new org.uddi.api_v3.RelatedBusinessInfos();
			if (body.getFindRelatedBusinesses().getBusinessKey() != null ) {
				getRelatedBusinesses(em, Direction.FROM_KEY, frb.getBusinessKey(), frb.getKeyedReference(), relatedBusinessInfos);
				getRelatedBusinesses(em, Direction.TO_KEY, frb.getBusinessKey(), frb.getKeyedReference(), relatedBusinessInfos);
			}
			else if (body.getFindRelatedBusinesses().getFromKey() != null)
				getRelatedBusinesses(em, Direction.FROM_KEY, frb.getFromKey(), frb.getKeyedReference(), relatedBusinessInfos);
			else if (body.getFindRelatedBusinesses().getToKey() != null)
				getRelatedBusinesses(em, Direction.TO_KEY, frb.getToKey(), frb.getKeyedReference(), relatedBusinessInfos);
			
			List<String> relatedBusinessKeys = new ArrayList<String>(0);
			for (org.uddi.api_v3.RelatedBusinessInfo rbi : relatedBusinessInfos.getRelatedBusinessInfo())
				relatedBusinessKeys.add(rbi.getBusinessKey());
			
			keysFound = relatedBusinessKeys;
		}
		
		keysFound = FindBusinessByTModelKeyQuery.select(em, findQualifiers, body.getTModelBag(), keysFound);
		keysFound = FindBusinessByIdentifierQuery.select(em, findQualifiers, body.getIdentifierBag(), keysFound);
		keysFound = FindBusinessByDiscoveryURLQuery.select(em, findQualifiers, body.getDiscoveryURLs(), keysFound);
        if (findQualifiers.isCombineCategoryBags()) {
            keysFound = FindBusinessByCombinedCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
        } else {
            keysFound = FindBusinessByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
        }

		keysFound = FindBusinessByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindBusinessByNameQuery.select(em, findQualifiers, body.getName(), keysFound);
		
		// If there no keys in the bag then remove the empty TModelBag
		if (body.getTModelBag().getTModelKey().size()==0) body.setTModelBag(null);
				
		return keysFound;
	}

	public static BusinessList getBusinessListFromKeys(FindBusiness body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound) throws DispositionReportFaultMessage {
		return getBusinessListFromKeys(body, findQualifiers, em, keysFound, null, null, null, null);
	}
	
	public static BusinessList getBusinessListFromKeys(FindBusiness body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound,
													   Date modifiedAfter, Date modifiedBefore, Holder<Integer> subscriptionStartIndex, Integer subscriptionMaxRows)
				   throws DispositionReportFaultMessage {

		BusinessList result = new BusinessList();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);

		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchBusinessEntitiesQuery.select(
				em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);
			
		if (queryResults != null && queryResults.size() > 0)
			result.setBusinessInfos(new org.uddi.api_v3.BusinessInfos());
		
		// Set the currentIndex to 0 or the value of the subscriptionStartIndex
		int currentIndex = 0;
		if (subscriptionStartIndex != null && subscriptionStartIndex.value != null)
			currentIndex = subscriptionStartIndex.value;

		int returnedRowCount = 0;
		
		while (currentIndex < queryResults.size()) {
			Object item = queryResults.get(currentIndex);

			org.apache.juddi.model.BusinessEntity modelBusinessEntity = (org.apache.juddi.model.BusinessEntity)item;
			org.uddi.api_v3.BusinessInfo apiBusinessInfo = new org.uddi.api_v3.BusinessInfo();
			
			if (modifiedAfter != null && modifiedAfter.after(modelBusinessEntity.getModifiedIncludingChildren())){
				currentIndex++;
				continue;
			}
			
			if (modifiedBefore != null && modifiedBefore.before(modelBusinessEntity.getModifiedIncludingChildren())) {
				currentIndex++;
				continue;
			}
			
			MappingModelToApi.mapBusinessInfo(modelBusinessEntity, apiBusinessInfo);
			
			result.getBusinessInfos().getBusinessInfo().add(apiBusinessInfo);

			returnedRowCount++;
			// If the returned rows equals the max allowed, we can end the loop (applies to subscription calls only)
			if (subscriptionMaxRows != null) {
				if (returnedRowCount == subscriptionMaxRows)
					break;
			}

			currentIndex++;
		}

		// If the loop was broken prematurely (max row count hit) we set the subscriptionStartIndex to the next index to start with.
		// Otherwise, set it to null so the subscription call won't trigger chunk token generation. 
		if (currentIndex < (queryResults.size() - 1)) {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = currentIndex + 1;
		}
		else {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = null;
		}
		
		return result;
	}
	
	public static List<?> findService(FindService body, FindQualifiers findQualifiers, EntityManager em) throws DispositionReportFaultMessage {

		List<?> keysFound = null;

		// First perform the embedded FindTModel search which will augment the tModel bag with any resulting tModel keys.
		if (body.getTModelBag() == null)
			body.setTModelBag(new TModelBag());
		doFindTModelEmbeddedSearch(em, body.getFindQualifiers(), body.getFindTModel(), body.getTModelBag());
		
		keysFound = FindServiceByTModelKeyQuery.select(em, findQualifiers, body.getTModelBag(), body.getBusinessKey(), keysFound);
        if (findQualifiers.isCombineCategoryBags()) {
		    keysFound = FindServiceByCombinedCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), body.getBusinessKey(), keysFound);
		} else {
			keysFound = FindServiceByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), body.getBusinessKey(), keysFound);
		}
		keysFound = FindServiceByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), body.getBusinessKey(), keysFound);
		keysFound = FindServiceByNameQuery.select(em, findQualifiers, body.getName(), body.getBusinessKey(), keysFound);
		
		if (body.getTModelBag().getTModelKey().size()==0) body.setTModelBag(null);
		return keysFound;
	}
	
	public static ServiceList getServiceListFromKeys(FindService body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound) throws DispositionReportFaultMessage {
		return getServiceListFromKeys(body, findQualifiers, em, keysFound, null, null, null, null);
	}

	public static ServiceList getServiceListFromKeys(FindService body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound,
													 Date modifiedAfter, Date modifiedBefore, Holder<Integer> subscriptionStartIndex, Integer subscriptionMaxRows)
				   throws DispositionReportFaultMessage {
		ServiceList result = new ServiceList();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);
		
		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchBusinessServicesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);
		if (queryResults != null && queryResults.size() > 0)
			result.setServiceInfos(new org.uddi.api_v3.ServiceInfos());

		// Set the currentIndex to 0 or the value of the subscriptionStartIndex
		int currentIndex = 0;
		if (subscriptionStartIndex != null && subscriptionStartIndex.value != null)
			currentIndex = subscriptionStartIndex.value;

		int returnedRowCount = 0;
		if (logger.isDebugEnabled()) logger.debug("Period = " + modifiedAfter + " ---- " + modifiedBefore);
		while (currentIndex < queryResults.size()) {
			Object item = queryResults.get(currentIndex);

			org.apache.juddi.model.BusinessService modelBusinessService = (org.apache.juddi.model.BusinessService)item;
			org.uddi.api_v3.ServiceInfo apiServiceInfo = new org.uddi.api_v3.ServiceInfo();
			
			logger.debug(modelBusinessService.getEntityKey() + " is modified " + modelBusinessService.getModifiedIncludingChildren() + " " + modelBusinessService.getModifiedIncludingChildren().getTime() );
			if (modifiedAfter != null && modifiedAfter.after(modelBusinessService.getModifiedIncludingChildren())) {
				currentIndex++;
				continue;
			}
			
			if (modifiedBefore != null && modifiedBefore.before(modelBusinessService.getModifiedIncludingChildren())) {
				currentIndex++;
				continue;
			}
			MappingModelToApi.mapServiceInfo(modelBusinessService, apiServiceInfo);
			
			result.getServiceInfos().getServiceInfo().add(apiServiceInfo);

			returnedRowCount++;
			// If the returned rows equals the max allowed, we can end the loop (applies to subscription calls only)
			if (subscriptionMaxRows != null) {
				if (returnedRowCount == subscriptionMaxRows)
					break;
			}

			currentIndex++;
		}

		// If the loop was broken prematurely (max row count hit) we set the subscriptionStartIndex to the next index to start with.
		// Otherwise, set it to null so the subscription call won't trigger chunk token generation. 
		if (currentIndex < (queryResults.size() - 1)) {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = currentIndex + 1;
		}
		else {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = null;
		}
		
		return result;
	}

	public static List<?> findTModel(FindTModel body, FindQualifiers findQualifiers, EntityManager em) throws DispositionReportFaultMessage {
		List<?> keysFound = null;

		keysFound = FindTModelByIdentifierQuery.select(em, findQualifiers, body.getIdentifierBag(), keysFound);
		keysFound = FindTModelByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindTModelByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindTModelByNameQuery.select(em, findQualifiers, body.getName(), keysFound);
		
		return keysFound;
	}

	public static TModelList getTModelListFromKeys(FindTModel body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound) throws DispositionReportFaultMessage {
		return getTModelListFromKeys(body, findQualifiers, em, keysFound, null, null, null, null);
	}
	
	public static TModelList getTModelListFromKeys(FindTModel body, FindQualifiers findQualifiers, EntityManager em, List<?> keysFound,
												   Date modifiedAfter, Date modifiedBefore, Holder<Integer> subscriptionStartIndex, Integer subscriptionMaxRows)
				   throws DispositionReportFaultMessage {
		TModelList result = new TModelList();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);

		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchTModelsQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);
		if (queryResults != null && queryResults.size() > 0)
			result.setTModelInfos(new org.uddi.api_v3.TModelInfos());
		
		// Set the currentIndex to 0 or the value of the subscriptionStartIndex
		int currentIndex = 0;
		if (subscriptionStartIndex != null && subscriptionStartIndex.value != null)
			currentIndex = subscriptionStartIndex.value;

		int returnedRowCount = 0;
		
		while (currentIndex < queryResults.size()) {
			Object item = queryResults.get(currentIndex);
			
			org.apache.juddi.model.Tmodel modelTModel = (org.apache.juddi.model.Tmodel)item;
			org.uddi.api_v3.TModelInfo apiTModelInfo = new org.uddi.api_v3.TModelInfo();
			
			if (modifiedAfter != null && modifiedAfter.after(modelTModel.getModifiedIncludingChildren())) {
				currentIndex++;
				continue;
			}
			
			if (modifiedBefore != null && modifiedBefore.before(modelTModel.getModifiedIncludingChildren())) {
				currentIndex++;
				continue;
			}
			
			MappingModelToApi.mapTModelInfo(modelTModel, apiTModelInfo);
			
			result.getTModelInfos().getTModelInfo().add(apiTModelInfo);
			
			returnedRowCount++;
			// If the returned rows equals the max allowed, we can end the loop (applies to subscription calls only)
			if (subscriptionMaxRows != null) {
				if (returnedRowCount == subscriptionMaxRows)
					break;
			}
			
			currentIndex++;
		}
		
		// If the loop was broken prematurely (max row count hit) we set the subscriptionStartIndex to the next index to start with.
		// Otherwise, set it to null so the subscription call won't trigger chunk token generation. 
		if (currentIndex < (queryResults.size() - 1)) {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = currentIndex + 1;
		}
		else {
			if (subscriptionStartIndex != null)
				subscriptionStartIndex.value = null;
		}
		
		return result;
	}
	
	
	/*
	 * Retrieves related businesses based on the focal business and the direction (fromKey or toKey).  The focal business is retrieved and then the
	 * appropriate publisher assertion collection is examined for matches.  The assertion must be "completed" and if a keyedReference is passed, it must
	 * match exactly.  Successful assertion matches are mapped to a RelationBusinessInfo structure and added to the passed in RelationalBusinessInfos 
	 * structure.
	 */
	public static void getRelatedBusinesses(EntityManager em, 
											Direction direction, 
											String focalKey, 
											org.uddi.api_v3.KeyedReference keyedRef,
											org.uddi.api_v3.RelatedBusinessInfos relatedBusinessInfos)
			throws DispositionReportFaultMessage {
		getRelatedBusinesses(em, direction, focalKey, keyedRef, relatedBusinessInfos, null, null);
	}
	
	public static void getRelatedBusinesses(EntityManager em, 
											Direction direction, 
											String focalKey, 
											org.uddi.api_v3.KeyedReference keyedRef,
											org.uddi.api_v3.RelatedBusinessInfos relatedBusinessInfos,
											Date modifiedAfter,
											Date modifiedBefore)
			throws DispositionReportFaultMessage {
		if (relatedBusinessInfos == null)
			relatedBusinessInfos = new org.uddi.api_v3.RelatedBusinessInfos();
		org.apache.juddi.model.BusinessEntity focalBusiness = null;
		try {
			focalBusiness = em.find(org.apache.juddi.model.BusinessEntity.class, focalKey);
		} catch (ClassCastException e) {}
		if (focalBusiness == null)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BusinessNotFound", focalKey));

		List<org.apache.juddi.model.PublisherAssertion> pubAssertList = null;
		if (direction == Direction.FROM_KEY)
			pubAssertList = focalBusiness.getPublisherAssertionsForFromKey();
		else
			pubAssertList = focalBusiness.getPublisherAssertionsForToKey();
		
		if (pubAssertList != null) {
			for (org.apache.juddi.model.PublisherAssertion modelPublisherAssertion : pubAssertList) {
				if ("true".equalsIgnoreCase(modelPublisherAssertion.getFromCheck()) && "true".equalsIgnoreCase(modelPublisherAssertion.getToCheck())) {
					if (keyedRef != null) {
						if(!keyedRef.getTModelKey().equals(modelPublisherAssertion.getTmodelKey()) || 
						   !keyedRef.getKeyName().equals(modelPublisherAssertion.getKeyName()) || 
						   !keyedRef.getKeyValue().equals(modelPublisherAssertion.getKeyValue())) {
							continue;
						}
					}
					
					org.apache.juddi.model.BusinessEntity modelRelatedBusiness  = null;
					if (direction == Direction.FROM_KEY)
						modelRelatedBusiness = em.find(org.apache.juddi.model.BusinessEntity.class, modelPublisherAssertion.getId().getToKey());
					else
						modelRelatedBusiness = em.find(org.apache.juddi.model.BusinessEntity.class, modelPublisherAssertion.getId().getFromKey());
					
					if (modifiedAfter != null && modifiedAfter.after(modelRelatedBusiness.getModifiedIncludingChildren()))
						continue;
					
					if (modifiedBefore != null && modifiedBefore.before(modelRelatedBusiness.getModifiedIncludingChildren()))
						continue;
					
					org.uddi.api_v3.RelatedBusinessInfo apiRelatedBusinessInfo = new org.uddi.api_v3.RelatedBusinessInfo();

					MappingModelToApi.mapRelatedBusinessInfo(modelPublisherAssertion, modelRelatedBusiness, direction, apiRelatedBusinessInfo);
					
					relatedBusinessInfos.getRelatedBusinessInfo().add(apiRelatedBusinessInfo);
				}
			}
		}
		
	}

	public static RelatedBusinessesList getRelatedBusinessesList(FindRelatedBusinesses body, EntityManager em) throws DispositionReportFaultMessage {
		return getRelatedBusinessesList(body, em, null, null);
	}
	
	public static RelatedBusinessesList getRelatedBusinessesList(FindRelatedBusinesses body, EntityManager em, Date modifiedAfter, Date modifiedBefore) throws DispositionReportFaultMessage {
		RelatedBusinessesList result = new RelatedBusinessesList();
		result.setBusinessKey(body.getBusinessKey());
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);
		
		// Either one of the businessKey, fromKey or toKey will be passed.  This is considered the "focal" business to which related businesses must be
		// found.  Rather than use a query, it seems simpler to take advantage of the model's publisher assertion collections.
		org.uddi.api_v3.RelatedBusinessInfos relatedBusinessInfos = new org.uddi.api_v3.RelatedBusinessInfos();
		if (body.getBusinessKey() != null ) {
			InquiryHelper.getRelatedBusinesses(em, Direction.FROM_KEY, body.getBusinessKey(), body.getKeyedReference(), relatedBusinessInfos, modifiedAfter, modifiedBefore);
			InquiryHelper.getRelatedBusinesses(em, Direction.TO_KEY, body.getBusinessKey(), body.getKeyedReference(), relatedBusinessInfos, modifiedAfter, modifiedBefore);
		}
		else if (body.getFromKey() != null) {
			InquiryHelper.getRelatedBusinesses(em, Direction.FROM_KEY, body.getFromKey(), body.getKeyedReference(), relatedBusinessInfos, modifiedAfter, modifiedBefore);
		    result.setBusinessKey(body.getFromKey());
		} else if (body.getToKey() != null) {
			InquiryHelper.getRelatedBusinesses(em, Direction.TO_KEY, body.getToKey(), body.getKeyedReference(), relatedBusinessInfos, modifiedAfter, modifiedBefore);
            result.setBusinessKey(body.getToKey());
		}
		if (relatedBusinessInfos.getRelatedBusinessInfo().size() > 0) {
			// TODO: Do proper pagination!
			listDesc.setActualCount(relatedBusinessInfos.getRelatedBusinessInfo().size());
			listDesc.setIncludeCount(relatedBusinessInfos.getRelatedBusinessInfo().size());
			listDesc.setListHead(1);
			
			result.setRelatedBusinessInfos(relatedBusinessInfos);
		}
		
		return result;
	}
		
	/*
	 * Performs the necessary queries for the find_tModel search and adds resulting tModel keys to the tModelBag provided.
	 */
	private static void doFindTModelEmbeddedSearch(EntityManager em, 
											org.uddi.api_v3.FindQualifiers fq, 
											FindTModel findTmodel, 
											TModelBag tmodelBag)
			throws DispositionReportFaultMessage {

		
		if (findTmodel != null && tmodelBag != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(findTmodel.getFindQualifiers());

			
			List<?> tmodelKeysFound = null;
			tmodelKeysFound = FindTModelByIdentifierQuery.select(em, findQualifiers, findTmodel.getIdentifierBag(), tmodelKeysFound);
			tmodelKeysFound = FindTModelByCategoryQuery.select(em, findQualifiers, findTmodel.getCategoryBag(), tmodelKeysFound);
			tmodelKeysFound = FindTModelByCategoryGroupQuery.select(em, findQualifiers, findTmodel.getCategoryBag(), tmodelKeysFound);
			tmodelKeysFound = FindTModelByNameQuery.select(em, findQualifiers, findTmodel.getName(), tmodelKeysFound);
			
			if (tmodelKeysFound != null && tmodelKeysFound.size() > 0) {
				for (Object item : tmodelKeysFound)
					tmodelBag.getTModelKey().add((String)item);
			}
		}
	}
	
	
}
