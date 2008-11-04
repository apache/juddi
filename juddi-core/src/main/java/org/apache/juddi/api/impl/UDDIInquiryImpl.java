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

import java.util.List;
import javax.jws.WebService;
import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;

import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.query.FetchBindingTemplatesQuery;
import org.apache.juddi.query.FetchBusinessEntitiesQuery;
import org.apache.juddi.query.FetchBusinessServicesQuery;
import org.apache.juddi.query.FetchTModelsQuery;
import org.apache.juddi.query.FindBindingByTModelKeyQuery;
import org.apache.juddi.query.BindingTemplateQuery;
import org.apache.juddi.query.FindBusinessByDiscoveryURLQuery;
import org.apache.juddi.query.FindBusinessByIdentifierQuery;
import org.apache.juddi.query.FindBusinessByNameQuery;
import org.apache.juddi.query.FindServiceByNameQuery;
import org.apache.juddi.query.BusinessServiceQuery;
import org.apache.juddi.query.FindTModelByIdentifierQuery;
import org.apache.juddi.query.FindTModelByNameQuery;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.util.JPAUtil;
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
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.apache.juddi.api.datatype.GetPublisherDetail;
import org.apache.juddi.api.datatype.PublisherDetail;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@WebService(serviceName="UDDIInquiryService", 
			endpointInterface="org.uddi.v3_service.UDDIInquiryPortType")
public class UDDIInquiryImpl implements UDDIInquiryPortType {

	public BindingDetail findBinding(FindBinding body)
			throws DispositionReportFaultMessage {

		ValidateInquiry.validateFindBinding(body);
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		List<?> keysFound = null;
		if (body.getServiceKey() == null || body.getServiceKey().length() == 0) {
			keysFound = FindBindingByTModelKeyQuery.select(em, findQualifiers, body.getTModelBag(), keysFound);
			//keysFound = FindBindingByCategoryQuery.select(em, findQualifiers, tmodelKeys, keysFound);
		}
		else {
			DynamicQuery.Parameter keyRestriction = new DynamicQuery.Parameter(BindingTemplateQuery.ENTITY_ALIAS + "." + BusinessServiceQuery.KEY_NAME, body.getServiceKey(), DynamicQuery.PREDICATE_EQUALS);
			if (body.getTModelBag() != null)
				keysFound = FindBindingByTModelKeyQuery.select(em, findQualifiers, body.getTModelBag(), keysFound, keyRestriction);
			//keysFound = FindBindingByCategoryQuery.select(em, findQualifiers, tmodelKeys, keysFound, keyRestriction);
		}
		
		// Sort and retrieve the final results with paging taken into account
		List<?> queryResults = FetchBindingTemplatesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead());

		BindingDetail result = new BindingDetail();
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

		ValidateInquiry.validateFindBusiness(body);
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		List<?> keysFound = null;
		keysFound = FindBusinessByIdentifierQuery.select(em, findQualifiers, body.getIdentifierBag(), keysFound);
		keysFound = FindBusinessByDiscoveryURLQuery.select(em, findQualifiers, body.getDiscoveryURLs(), keysFound);
		//keysFound = FindBusinessByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindBusinessByNameQuery.select(em, findQualifiers, body.getName(), keysFound);

		BusinessList result = new BusinessList();

		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchBusinessEntitiesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead());
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

	public RelatedBusinessesList findRelatedBusinesses(
			FindRelatedBusinesses body) throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceList findService(FindService body)
			throws DispositionReportFaultMessage {

		ValidateInquiry.validateFindService(body);
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		List<?> keysFound = null;
		//keysFound = FindServiceByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindServiceByNameQuery.select(em, findQualifiers, body.getName(), keysFound);

		ServiceList result = new ServiceList();
		
		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchBusinessServicesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead());
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

		ValidateInquiry.validateFindTModel(body);
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

		List<?> keysFound = null;
		keysFound = FindTModelByIdentifierQuery.select(em, findQualifiers, body.getIdentifierBag(), keysFound);
		//keysFound = FindTModelByCategoryQuery.select(em, findQualifiers, body.getCategoryBag(), keysFound);
		keysFound = FindTModelByNameQuery.select(em, findQualifiers, body.getName(), keysFound);

		TModelList result = new TModelList();

		// Sort and retrieve the final results taking paging into account
		List<?> queryResults = FetchTModelsQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead());
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

		ValidateInquiry.validateGetBindingDetail(body);
		
		// TODO: Perform necessary authentication logic
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
		
		ValidateInquiry.validateGetBusinessDetail(body);
		
		// TODO: Perform necessary authentication logic
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

		ValidateInquiry.validateGetServiceDetail(body);
		
		// TODO: Perform necessary authentication logic
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

		ValidateInquiry.validateGetTModelDetail(body);
		
		// TODO: Perform necessary authentication logic
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

	/*
	 * Retrieves publisher(s) from the persistence layer.  This method is specific to jUDDI.
	 */
	public PublisherDetail getPublisherDetail(GetPublisherDetail body)
			throws DispositionReportFaultMessage {

		ValidateInquiry.validateGetPublisherDetail(body);
		
		// TODO: Perform necessary authentication logic
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
	
}
