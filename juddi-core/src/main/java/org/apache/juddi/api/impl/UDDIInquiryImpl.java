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

import java.util.Iterator;
import java.util.List;

import javax.jws.WebService;

import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;

import org.apache.juddi.config.ResourceConfig;
import org.apache.juddi.error.UDDIErrorHelper;
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
import org.apache.juddi.util.JPAUtil;
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

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@WebService(serviceName="UDDIInquiryService", 
			endpointInterface="org.uddi.v3_service.UDDIInquiryPortType")
public class UDDIInquiryImpl implements UDDIInquiryPortType {

	public BindingDetail findBinding(FindBinding body)
			throws DispositionReportFaultMessage {

		org.uddi.api_v3.BindingDetail result = new org.uddi.api_v3.BindingDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		// TODO: Validate input here
		org.uddi.api_v3.TModelBag tmodelKeys = body.getTModelBag();
		org.uddi.api_v3.CategoryBag categories = body.getCategoryBag();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());
		
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<?> keysFound = null;
		if (body.getServiceKey() == null || body.getServiceKey().length() == 0) {
			keysFound = FindBindingByTModelKeyQuery.select(em, findQualifiers, tmodelKeys, keysFound);
			//keysFound = FindBindingByCategoryQuery.select(em, findQualifiers, tmodelKeys, keysFound);
		}
		else {
			DynamicQuery.Parameter keyRestriction = new DynamicQuery.Parameter(BindingTemplateQuery.ENTITY_ALIAS + "." + BusinessServiceQuery.KEY_NAME, body.getServiceKey(), DynamicQuery.PREDICATE_EQUALS);
			keysFound = FindBindingByTModelKeyQuery.select(em, findQualifiers, tmodelKeys, keysFound, keyRestriction);
			//keysFound = FindBindingByCategoryQuery.select(em, findQualifiers, tmodelKeys, keysFound, keyRestriction);
		}
		
		// Sort and retrieve the final results with paging taken into account
		List<?> queryResults = FetchBindingTemplatesQuery.select(em, findQualifiers, keysFound, body.getMaxRows(), body.getListHead());

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

		org.uddi.api_v3.BusinessList result = new org.uddi.api_v3.BusinessList();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		// TODO: Validate input here
		org.uddi.api_v3.IdentifierBag identifiers = body.getIdentifierBag();
		org.uddi.api_v3.DiscoveryURLs discURLs = body.getDiscoveryURLs();
		org.uddi.api_v3.CategoryBag categories = body.getCategoryBag();
		List<org.uddi.api_v3.Name> names = body.getName();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());
		
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<?> keysFound = null;
		keysFound = FindBusinessByIdentifierQuery.select(em, findQualifiers, identifiers, keysFound);
		keysFound = FindBusinessByDiscoveryURLQuery.select(em, findQualifiers, discURLs, keysFound);
		//keysFound = FindBusinessByCategoryQuery.select(em, findQualifiers, categories, keysFound);
		keysFound = FindBusinessByNameQuery.select(em, findQualifiers, names, keysFound);

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

		org.uddi.api_v3.ServiceList result = new org.uddi.api_v3.ServiceList();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		// TODO: Validate input here
		org.uddi.api_v3.CategoryBag categories = body.getCategoryBag();
		List<org.uddi.api_v3.Name> names = body.getName();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());
		
		
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<?> keysFound = null;
		//keysFound = FindServiceByCategoryQuery.select(em, findQualifiers, categories, keysFound);
		keysFound = FindServiceByNameQuery.select(em, findQualifiers, names, keysFound);

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

		org.uddi.api_v3.TModelList result = new org.uddi.api_v3.TModelList();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		// TODO: Validate input here
		org.uddi.api_v3.IdentifierBag identifiers = body.getIdentifierBag();
		org.uddi.api_v3.CategoryBag categories = body.getCategoryBag();
		org.uddi.api_v3.Name name = body.getName();
		
		org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
		findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());
		
		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<?> keysFound = null;
		keysFound = FindTModelByIdentifierQuery.select(em, findQualifiers, identifiers, keysFound);
		//keysFound = FindTModelByCategoryQuery.select(em, findQualifiers, identifiers, keysFound);
		keysFound = FindTModelByNameQuery.select(em, findQualifiers, name, keysFound);

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

		org.uddi.api_v3.BindingDetail result = new org.uddi.api_v3.BindingDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<String> bindingKeyList = body.getBindingKey();
		Iterator<String> bindingKeyListItr = bindingKeyList.iterator();
		while (bindingKeyListItr.hasNext()) {
			String bindingKey = bindingKeyListItr.next();
			
			
			org.apache.juddi.model.BindingTemplate modelBindingTemplate = em.find(org.apache.juddi.model.BindingTemplate.class, bindingKey);
			if (modelBindingTemplate == null) {
				throw new DispositionReportFaultMessage(ResourceConfig.getGlobalMessage("errors.invalidkey.BindingTemplateNotFound") + ":  " + bindingKey, 
														UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_KEY_PASSED));
			}
			
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
		
		org.uddi.api_v3.BusinessDetail result = new org.uddi.api_v3.BusinessDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<String> businessKeyList = body.getBusinessKey();
		Iterator<String> businessKeyListItr = businessKeyList.iterator();
		while (businessKeyListItr.hasNext()) {
			String businessKey = businessKeyListItr.next();
			
			
			org.apache.juddi.model.BusinessEntity modelBusinessEntity = em.find(org.apache.juddi.model.BusinessEntity.class, businessKey);
			if (modelBusinessEntity == null) {
				throw new DispositionReportFaultMessage(ResourceConfig.getGlobalMessage("errors.invalidkey.BusinessNotFound") + ":  " + businessKey, 
														UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_KEY_PASSED));
			}
			
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

		org.uddi.api_v3.ServiceDetail result = new org.uddi.api_v3.ServiceDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<String> serviceKeyList = body.getServiceKey();
		Iterator<String> serviceKeyListItr = serviceKeyList.iterator();
		while (serviceKeyListItr.hasNext()) {
			String serviceKey = serviceKeyListItr.next();
			
			
			org.apache.juddi.model.BusinessService modelBusinessService = em.find(org.apache.juddi.model.BusinessService.class, serviceKey);
			if (modelBusinessService == null) {
				throw new DispositionReportFaultMessage(ResourceConfig.getGlobalMessage("errors.invalidkey.ServiceNotFound") + ":  " + serviceKey, 
														UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_KEY_PASSED));
			}
			
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

		org.uddi.api_v3.TModelDetail result = new org.uddi.api_v3.TModelDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		EntityManager em = JPAUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<String> tmodelKeyList = body.getTModelKey();
		Iterator<String> tmodelKeyListItr = tmodelKeyList.iterator();
		while (tmodelKeyListItr.hasNext()) {
			String tmodelKey = tmodelKeyListItr.next();
			
			
			org.apache.juddi.model.Tmodel modelTModel = em.find(org.apache.juddi.model.Tmodel.class, tmodelKey);
			if (modelTModel == null) {
				throw new DispositionReportFaultMessage(ResourceConfig.getGlobalMessage("errors.invalidkey.TModelNotFound") + ":  " + tmodelKey, 
														UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_KEY_PASSED));
			}
			
			org.uddi.api_v3.TModel apiTModel = new org.uddi.api_v3.TModel();
			
			MappingModelToApi.mapTModel(modelTModel, apiTModel);
			
			result.getTModel().add(apiTModel);
		}

		tx.commit();
		em.close();
		
		return result;
	}

}
