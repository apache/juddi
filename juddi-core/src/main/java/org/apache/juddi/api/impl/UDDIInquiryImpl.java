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
import java.util.List;
import javax.jws.WebService;
import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;

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
import org.apache.juddi.query.FindBusinessByDiscoveryURLQuery;
import org.apache.juddi.query.FindBusinessByIdentifierQuery;
import org.apache.juddi.query.FindBusinessByNameQuery;
import org.apache.juddi.query.FindBusinessByTModelKeyQuery;
import org.apache.juddi.query.FindServiceByCategoryGroupQuery;
import org.apache.juddi.query.FindServiceByCategoryQuery;
import org.apache.juddi.query.FindServiceByNameQuery;
import org.apache.juddi.query.FindServiceByTModelKeyQuery;
import org.apache.juddi.query.FindTModelByCategoryGroupQuery;
import org.apache.juddi.query.FindTModelByCategoryQuery;
import org.apache.juddi.query.FindTModelByIdentifierQuery;
import org.apache.juddi.query.FindTModelByNameQuery;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.validation.ValidateInquiry;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.ErrorMessage;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.api_v3.ListDescription;
import org.uddi.api_v3.TModelBag;
import org.uddi.api_v3.Direction;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.apache.juddi.api.datatype.GetPublisherDetail;
import org.apache.juddi.api.datatype.PublisherDetail;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@WebService(serviceName="UDDIInquiryService", 
			endpointInterface="org.uddi.v3_service.UDDIInquiryPortType",
			targetNamespace = "urn:uddi-org:api_v3_portType")
public class UDDIInquiryImpl implements UDDIInquiryPortType {

	public BindingDetail findBinding(FindBinding body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateFindBinding(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		List<?> keysFound = null;

		// First perform the embedded FindTModel search which will augment the tModel bag with any resulting tModel keys.
		if (body.getTModelBag() == null)
			body.setTModelBag(new TModelBag());
		doFindTModelEmbeddedSearch(em, body.getFindQualifiers(), body.getFindTModel(), body.getTModelBag());
		
		keysFound = FindBindingByTModelKeyQuery.select(em, findQualifiers, body.getTModelBag(), body.getServiceKey(), keysFound);
		keysFound = FindBindingByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), body.getServiceKey(), keysFound);
		keysFound = FindBindingByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), body.getServiceKey(), keysFound);

		BindingDetail result = new BindingDetail();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);
		
		// Sort and retrieve the final results with paging taken into account
		List<?> queryResults = FetchBindingTemplatesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);

		for (Object item : queryResults) {
			org.apache.juddi.model.BindingTemplate modelBindingTemplate = (org.apache.juddi.model.BindingTemplate)item;
			org.uddi.api_v3.BindingTemplate apiBindingTemplate = new org.uddi.api_v3.BindingTemplate();
			
			MappingModelToApi.mapBindingTemplate(modelBindingTemplate, apiBindingTemplate);
			
			result.getBindingTemplate().add(apiBindingTemplate);
		}
		
		tx.commit();
		em.close();
		
		return result;
	}

	public BusinessList findBusiness(FindBusiness body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateFindBusiness(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		// First perform the embedded FindTModel search which will augment the tModel bag with any resulting tModel keys.
		if (body.getTModelBag() == null)
			body.setTModelBag(new TModelBag());
		doFindTModelEmbeddedSearch(em, body.getFindQualifiers(), body.getFindTModel(), body.getTModelBag());
		
		List<?> keysFound = null;
		
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
		keysFound = FindBusinessByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindBusinessByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindBusinessByNameQuery.select(em, findQualifiers, body.getName(), keysFound);

		BusinessList result = new BusinessList();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);

		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchBusinessEntitiesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);
		if (queryResults != null && queryResults.size() > 0)
			result.setBusinessInfos(new org.uddi.api_v3.BusinessInfos());

		for (Object item : queryResults) {
			org.apache.juddi.model.BusinessEntity modelBusinessEntity = (org.apache.juddi.model.BusinessEntity)item;
			org.uddi.api_v3.BusinessInfo apiBusinessInfo = new org.uddi.api_v3.BusinessInfo();
			
			MappingModelToApi.mapBusinessInfo(modelBusinessEntity, apiBusinessInfo);
			
			result.getBusinessInfos().getBusinessInfo().add(apiBusinessInfo);
		}
		
		tx.commit();
		em.close();
		
		return result;
	}

	public RelatedBusinessesList findRelatedBusinesses(FindRelatedBusinesses body) 
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateFindRelatedBusinesses(body, false);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		// TODO: findQualifiers aren't really used for this call, except maybe for sorting.  Sorting must be done in Java due to the retrieval method used.  Right now
		// no sorting is performed.
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());
		
		RelatedBusinessesList result = new RelatedBusinessesList();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);
		
		// Either one of the businessKey, fromKey or toKey will be passed.  This is considered the "focal" business to which related businesses must be
		// found.  Rather than use a query, it seems simpler to take advantage of the model's publisher assertion collections.
		org.uddi.api_v3.RelatedBusinessInfos relatedBusinessInfos = new org.uddi.api_v3.RelatedBusinessInfos();
		if (body.getBusinessKey() != null ) {
			getRelatedBusinesses(em, Direction.FROM_KEY, body.getBusinessKey(), body.getKeyedReference(), relatedBusinessInfos);
			getRelatedBusinesses(em, Direction.TO_KEY, body.getBusinessKey(), body.getKeyedReference(), relatedBusinessInfos);
		}
		else if (body.getFromKey() != null)
			getRelatedBusinesses(em, Direction.FROM_KEY, body.getFromKey(), body.getKeyedReference(), relatedBusinessInfos);
		else if (body.getToKey() != null)
			getRelatedBusinesses(em, Direction.TO_KEY, body.getToKey(), body.getKeyedReference(), relatedBusinessInfos);

		if (relatedBusinessInfos.getRelatedBusinessInfo().size() > 0) {
			// TODO: Do proper pagination!
			listDesc.setActualCount(relatedBusinessInfos.getRelatedBusinessInfo().size());
			listDesc.setIncludeCount(relatedBusinessInfos.getRelatedBusinessInfo().size());
			listDesc.setListHead(1);
			
			result.setRelatedBusinessInfos(relatedBusinessInfos);
		}
		
		tx.commit();
		em.close();
		
		return result;
		
	}
	
	/*
	 * Retrieves related businesses based on the focal business and the direction (fromKey or toKey).  The focal business is retrieved and then the
	 * appropriate publisher assertion collection is examined for matches.  The assertion must be "completed" and if a keyedReference is passed, it must
	 * match exactly.  Successful assertion matches are mapped to a RelationBusinessInfo structure and added to the passed in RelationalBusinessInfos 
	 * structure.
	 */
	private void getRelatedBusinesses(EntityManager em, 
									  Direction direction, 
									  String focalKey, 
									  org.uddi.api_v3.KeyedReference keyedRef,
									  org.uddi.api_v3.RelatedBusinessInfos relatedBusinessInfos)
			 throws DispositionReportFaultMessage {
		if (relatedBusinessInfos == null)
			relatedBusinessInfos = new org.uddi.api_v3.RelatedBusinessInfos();
		
		org.apache.juddi.model.BusinessEntity focalBusiness = em.find(org.apache.juddi.model.BusinessEntity.class, focalKey);
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
					
					org.uddi.api_v3.RelatedBusinessInfo apiRelatedBusinessInfo = new org.uddi.api_v3.RelatedBusinessInfo();

					MappingModelToApi.mapRelatedBusinessInfo(modelPublisherAssertion, modelRelatedBusiness, direction, apiRelatedBusinessInfo);
					
					relatedBusinessInfos.getRelatedBusinessInfo().add(apiRelatedBusinessInfo);
				}
			}
		}
		
	}

	public ServiceList findService(FindService body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateFindService(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		// First perform the embedded FindTModel search which will augment the tModel bag with any resulting tModel keys.
		if (body.getTModelBag() == null)
			body.setTModelBag(new TModelBag());
		doFindTModelEmbeddedSearch(em, body.getFindQualifiers(), body.getFindTModel(), body.getTModelBag());
		
		List<?> keysFound = null;
		FindServiceByTModelKeyQuery.select(em, findQualifiers, body.getTModelBag(), body.getBusinessKey(), keysFound);
		keysFound = FindServiceByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), body.getBusinessKey(), keysFound);
		keysFound = FindServiceByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), body.getBusinessKey(), keysFound);
		keysFound = FindServiceByNameQuery.select(em, findQualifiers, body.getName(), body.getBusinessKey(), keysFound);

		ServiceList result = new ServiceList();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);
		
		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchBusinessServicesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);
		if (queryResults != null && queryResults.size() > 0)
			result.setServiceInfos(new org.uddi.api_v3.ServiceInfos());

		for (Object item : queryResults) {
			org.apache.juddi.model.BusinessService modelBusinessService = (org.apache.juddi.model.BusinessService)item;
			org.uddi.api_v3.ServiceInfo apiServiceInfo = new org.uddi.api_v3.ServiceInfo();
			
			MappingModelToApi.mapServiceInfo(modelBusinessService, apiServiceInfo);
			
			result.getServiceInfos().getServiceInfo().add(apiServiceInfo);
		}
		
		tx.commit();
		em.close();
		
		return result;
	}

	public TModelList findTModel(FindTModel body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateFindTModel(body, false);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		List<?> keysFound = null;
		keysFound = FindTModelByIdentifierQuery.select(em, findQualifiers, body.getIdentifierBag(), keysFound);
		keysFound = FindTModelByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindTModelByCategoryGroupQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindTModelByNameQuery.select(em, findQualifiers, body.getName(), keysFound);

		TModelList result = new TModelList();
		ListDescription listDesc = new ListDescription();
		result.setListDescription(listDesc);

		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchTModelsQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead(), listDesc);
		if (queryResults != null && queryResults.size() > 0)
			result.setTModelInfos(new org.uddi.api_v3.TModelInfos());
		
		for (Object item : queryResults) {
			org.apache.juddi.model.Tmodel modelTModel = (org.apache.juddi.model.Tmodel)item;
			org.uddi.api_v3.TModelInfo apiTModelInfo = new org.uddi.api_v3.TModelInfo();
			
			MappingModelToApi.mapTModelInfo(modelTModel, apiTModelInfo);
			
			result.getTModelInfos().getTModelInfo().add(apiTModelInfo);
		}
		
		tx.commit();
		em.close();
		
		return result;
	}

	public BindingDetail getBindingDetail(GetBindingDetail body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateGetBindingDetail(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		BindingDetail result = new BindingDetail();

		List<String> bindingKeyList = body.getBindingKey();
		for (String bindingKey : bindingKeyList) {
			
			org.apache.juddi.model.BindingTemplate modelBindingTemplate = em.find(org.apache.juddi.model.BindingTemplate.class, bindingKey);
			if (modelBindingTemplate == null)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BindingTemplateNotFound", bindingKey));
			
			org.uddi.api_v3.BindingTemplate apiBindingTemplate = new org.uddi.api_v3.BindingTemplate();
			
			MappingModelToApi.mapBindingTemplate(modelBindingTemplate, apiBindingTemplate);
			
			result.getBindingTemplate().add(apiBindingTemplate);
		}

		tx.commit();
		em.close();
		
		return result;
	}

	public BusinessDetail getBusinessDetail(GetBusinessDetail body)
			throws DispositionReportFaultMessage {
		
		new ValidateInquiry(null).validateGetBusinessDetail(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		BusinessDetail result = new BusinessDetail();
		
		List<String> businessKeyList = body.getBusinessKey();
		for (String businessKey : businessKeyList) {
			
			org.apache.juddi.model.BusinessEntity modelBusinessEntity = em.find(org.apache.juddi.model.BusinessEntity.class, businessKey);
			if (modelBusinessEntity == null)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BusinessNotFound", businessKey));
			
			org.uddi.api_v3.BusinessEntity apiBusinessEntity = new org.uddi.api_v3.BusinessEntity();
			
			MappingModelToApi.mapBusinessEntity(modelBusinessEntity, apiBusinessEntity);
			
			result.getBusinessEntity().add(apiBusinessEntity);
		}

		tx.commit();
		em.close();
		
		return result;
	}

	public OperationalInfos getOperationalInfo(GetOperationalInfo body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceDetail getServiceDetail(GetServiceDetail body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateGetServiceDetail(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		ServiceDetail result = new ServiceDetail();

		List<String> serviceKeyList = body.getServiceKey();
		for (String serviceKey : serviceKeyList) {
			
			org.apache.juddi.model.BusinessService modelBusinessService = em.find(org.apache.juddi.model.BusinessService.class, serviceKey);
			if (modelBusinessService == null)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ServiceNotFound", serviceKey));
			
			org.uddi.api_v3.BusinessService apiBusinessService = new org.uddi.api_v3.BusinessService();
			
			MappingModelToApi.mapBusinessService(modelBusinessService, apiBusinessService);
			
			result.getBusinessService().add(apiBusinessService);
		}

		tx.commit();
		em.close();
		
		return result;
	}

	public TModelDetail getTModelDetail(GetTModelDetail body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateGetTModelDetail(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		TModelDetail result = new TModelDetail();
		
		List<String> tmodelKeyList = body.getTModelKey();
		for (String tmodelKey : tmodelKeyList) {
			
			org.apache.juddi.model.Tmodel modelTModel = em.find(org.apache.juddi.model.Tmodel.class, tmodelKey);
			if (modelTModel == null) {
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.TModelNotFound", tmodelKey));
			}
			
			org.uddi.api_v3.TModel apiTModel = new org.uddi.api_v3.TModel();
			
			MappingModelToApi.mapTModel(modelTModel, apiTModel);
			
			result.getTModel().add(apiTModel);
		}

		tx.commit();
		em.close();
		
		return result;
	}

	/*-------------------------------------------------------------------
	 Publisher functions are specific to jUDDI.
	 --------------------------------------------------------------------*/
	
	/*
	 * Retrieves publisher(s) from the persistence layer.  This method is specific to jUDDI.
	 */
	public PublisherDetail getPublisherDetail(GetPublisherDetail body)
			throws DispositionReportFaultMessage {

		new ValidateInquiry(null).validateGetPublisherDetail(body);
		
		// TODO JUDDI-178: Perform necessary authentication logic
		@SuppressWarnings("unused")
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		PublisherDetail result = new PublisherDetail();
		
		List<String> publisherIdList = body.getPublisherId();
		for (String publisherId : publisherIdList) {
			
			org.apache.juddi.model.Publisher modelPublisher = em.find(org.apache.juddi.model.Publisher.class, publisherId);
			if (modelPublisher == null) {
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.PublisherNotFound", publisherId));
			}
			
			org.apache.juddi.api.datatype.Publisher apiPublisher = new org.apache.juddi.api.datatype.Publisher();
			
			MappingModelToApi.mapPublisher(modelPublisher, apiPublisher);
			
			result.getPublisher().add(apiPublisher);
		}

		tx.commit();
		em.close();
		
		return result;

	}
	
	/*
	 * Performs the necessary queries for the find_tModel search and adds resulting tModel keys to the tModelBag provided.
	 */
	private void doFindTModelEmbeddedSearch(EntityManager em, 
											org.uddi.api_v3.FindQualifiers fq, 
											FindTModel findTmodel, 
											TModelBag tmodelBag)
			throws DispositionReportFaultMessage {

		
		if (findTmodel != null && tmodelBag != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(fq);

			
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
