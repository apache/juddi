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
import java.util.Iterator;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.ws.Holder;

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
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIPublicationPortType;

import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.util.JPAUtil;
import org.apache.juddi.validation.ValidatePublish;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.model.UddiEntityPublisher;

import org.apache.juddi.model.Publisher;
import org.apache.juddi.api.datatype.PublisherDetail;
import org.apache.juddi.api.datatype.SavePublisher;
import org.apache.juddi.api.datatype.DeletePublisher;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@WebService(serviceName="UDDIPublicationService", 
			endpointInterface="org.uddi.v3_service.UDDIPublicationPortType")
public class UDDIPublicationImpl extends AuthenticatedService implements UDDIPublicationPortType {

	
	public void addPublisherAssertions(AddPublisherAssertions body)
			throws DispositionReportFaultMessage {
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		List<org.uddi.api_v3.PublisherAssertion> apiPubAssertionList = body.getPublisherAssertion();
		Iterator<org.uddi.api_v3.PublisherAssertion> apiPubAssertionListItr = apiPubAssertionList.iterator();
		while (apiPubAssertionListItr.hasNext()) {
			org.uddi.api_v3.PublisherAssertion apiPubAssertion = apiPubAssertionListItr.next();
			
			//TODO:  Validate the input here
			
			org.apache.juddi.model.PublisherAssertion modelPubAssertion = new org.apache.juddi.model.PublisherAssertion();
			
			MappingApiToModel.mapPublisherAssertion(apiPubAssertion, modelPubAssertion);
			
			JPAUtil.persistEntity(modelPubAssertion, modelPubAssertion.getId());
		}
	}

	public void deleteBinding(DeleteBinding body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		ValidatePublish.validateDeleteBinding(em, publisher, body);
		
		List<String> entityKeyList = body.getBindingKey();
		for (String entityKey : entityKeyList) {
			Object obj = em.find(org.apache.juddi.model.BindingTemplate.class, entityKey);
			em.remove(obj);
		}

		tx.commit();
		em.close();
	}

	public void deleteBusiness(DeleteBusiness body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

		ValidatePublish.validateDeleteBusiness(em, publisher, body);
		
		List<String> entityKeyList = body.getBusinessKey();
		for (String entityKey : entityKeyList) {
			Object obj = em.find(org.apache.juddi.model.BusinessEntity.class, entityKey);
			em.remove(obj);
		}

		tx.commit();
		em.close();
	}

	public void deletePublisherAssertions(DeletePublisherAssertions body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

		ValidatePublish.validateDeletePublisherAssertions(em, body);
		
		List<org.uddi.api_v3.PublisherAssertion> entityList = body.getPublisherAssertion();
		for (org.uddi.api_v3.PublisherAssertion entity : entityList) {
			org.apache.juddi.model.PublisherAssertionId pubAssertionId = new org.apache.juddi.model.PublisherAssertionId(entity.getFromKey(), entity.getToKey());
			Object obj = em.find(org.apache.juddi.model.BusinessEntity.class, pubAssertionId);
			em.remove(obj);
		}

		tx.commit();
		em.close();
	}

	public void deleteService(DeleteService body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		ValidatePublish.validateDeleteService(em, publisher, body);
		
		List<String> entityKeyList = body.getServiceKey();
		for (String entityKey : entityKeyList) {
			Object obj = em.find(org.apache.juddi.model.BusinessService.class, entityKey);
			em.remove(obj);
		}

		tx.commit();
		em.close();
	}


	public void deleteTModel(DeleteTModel body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

		ValidatePublish.validateDeleteTModel(em, publisher, body);

		// tModels are only lazily deleted!
		List<String> entityKeyList = body.getTModelKey();
		for (String entityKey : entityKeyList) {
			Object obj = em.find(org.apache.juddi.model.Tmodel.class, entityKey);
			((org.apache.juddi.model.Tmodel)obj).setDeleted(true);
		}

		tx.commit();
		em.close();
	}


	public List<AssertionStatusItem> getAssertionStatusReport(String authInfo,
			CompletionStatus completionStatus)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}


	public List<PublisherAssertion> getPublisherAssertions(String authInfo)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}


	public RegisteredInfo getRegisteredInfo(GetRegisteredInfo body)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub
		return null;
	}


	public BindingDetail saveBinding(SaveBinding body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		ValidatePublish.validateSaveBinding(em, publisher, body);

		BindingDetail result = new BindingDetail();
		
		List<org.uddi.api_v3.BindingTemplate> apiBindingTemplateList = body.getBindingTemplate();
		for (org.uddi.api_v3.BindingTemplate apiBindingTemplate : apiBindingTemplateList) {
			
			org.apache.juddi.model.BindingTemplate modelBindingTemplate = new org.apache.juddi.model.BindingTemplate();
			org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();
			modelBusinessService.setServiceKey(apiBindingTemplate.getServiceKey());
			
			MappingApiToModel.mapBindingTemplate(apiBindingTemplate, modelBindingTemplate, modelBusinessService);
			
			Object existingUddiEntity = em.find(modelBindingTemplate.getClass(), modelBindingTemplate.getBindingKey());
			if (existingUddiEntity != null)
				em.remove(existingUddiEntity);
			
			modelBindingTemplate.assignPublisherId(publisher.getPublisherId());
			em.persist(modelBindingTemplate);
			
			result.getBindingTemplate().add(apiBindingTemplate);
		}

		tx.commit();
		em.close();
		
		return result;
	}


	public BusinessDetail saveBusiness(SaveBusiness body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		ValidatePublish.validateSaveBusiness(em, publisher, body);

		BusinessDetail result = new BusinessDetail();
		
		List<org.uddi.api_v3.BusinessEntity> apiBusinessEntityList = body.getBusinessEntity();
		for (org.uddi.api_v3.BusinessEntity apiBusinessEntity : apiBusinessEntityList) {
			
			org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();
			
			MappingApiToModel.mapBusinessEntity(apiBusinessEntity, modelBusinessEntity);
			
			Object existingUddiEntity = em.find(modelBusinessEntity.getClass(), modelBusinessEntity.getBusinessKey());
			if (existingUddiEntity != null)
				em.remove(existingUddiEntity);
			
			modelBusinessEntity.assignPublisherId(publisher.getPublisherId());
			em.persist(modelBusinessEntity);

			result.getBusinessEntity().add(apiBusinessEntity);
		}

		tx.commit();
		em.close();
		
		return result;
	}


	public ServiceDetail saveService(SaveService body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		ValidatePublish.validateSaveService(em, publisher, body);
		
		ServiceDetail result = new ServiceDetail();

		List<org.uddi.api_v3.BusinessService> apiBusinessServiceList = body.getBusinessService();
		for (org.uddi.api_v3.BusinessService apiBusinessService : apiBusinessServiceList) {
			
			org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();
			org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();
			modelBusinessEntity.setBusinessKey(apiBusinessService.getBusinessKey());
			
			MappingApiToModel.mapBusinessService(apiBusinessService, modelBusinessService, modelBusinessEntity);
			
			Object existingUddiEntity = em.find(modelBusinessService.getClass(), modelBusinessService.getServiceKey());
			if (existingUddiEntity != null)
				em.remove(existingUddiEntity);
			
			modelBusinessService.assignPublisherId(publisher.getPublisherId());
			em.persist(modelBusinessService);
			
			result.getBusinessService().add(apiBusinessService);
		}

		tx.commit();
		em.close();

		return result;
	}


	public TModelDetail saveTModel(SaveTModel body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

		ValidatePublish.validateSaveTModel(em, publisher, body);

		TModelDetail result = new TModelDetail();

		List<org.uddi.api_v3.TModel> apiTModelList = body.getTModel();
		for (org.uddi.api_v3.TModel apiTModel : apiTModelList) {
			
			org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
			
			MappingApiToModel.mapTModel(apiTModel, modelTModel);
			
			Object existingUddiEntity = em.find(modelTModel.getClass(), modelTModel.getTmodelKey());
			if (existingUddiEntity != null)
				em.remove(existingUddiEntity);
			
			modelTModel.assignPublisherId(publisher.getPublisherId());
			em.persist(modelTModel);
			
			result.getTModel().add(apiTModel);
		}

		tx.commit();
		em.close();
		
		return result;
	}


	public void setPublisherAssertions(String authInfo,
			Holder<List<PublisherAssertion>> publisherAssertion)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

	
	/*-------------------------------------------------------------------
	 Publisher functions are specific to jUDDI.
	 --------------------------------------------------------------------*/
	
	/*
	 * Saves publisher(s) to the persistence layer.  This method is specific to jUDDI.
	 */
	public PublisherDetail savePublisher(SavePublisher body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		ValidatePublish.validateSavePublisher(em, (Publisher)publisher, body);
		
		PublisherDetail result = new PublisherDetail();

		List<org.apache.juddi.api.datatype.Publisher> apiPublisherList = body.getPublisher();
		for (org.apache.juddi.api.datatype.Publisher apiPublisher : apiPublisherList) {
			
			org.apache.juddi.model.Publisher modelPublisher = new org.apache.juddi.model.Publisher();
			
			MappingApiToModel.mapPublisher(apiPublisher, modelPublisher);
			
			Object existingUddiEntity = em.find(modelPublisher.getClass(), modelPublisher.getPublisherId());
			if (existingUddiEntity != null)
				em.remove(existingUddiEntity);
			
			em.persist(modelPublisher);
			
			result.getPublisher().add(apiPublisher);
		}

		tx.commit();
		em.close();
		
		return result;
	}

	/*
	 * Deletes publisher(s) from the persistence layer.  This method is specific to jUDDI.
	 */
	public void deletePublisher(DeletePublisher body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		ValidatePublish.validateDeletePublisher(em, (Publisher)publisher, body);

		List<String> entityKeyList = body.getPublisherId();
		for (String entityKey : entityKeyList) {
			Object obj = em.find(org.apache.juddi.model.Publisher.class, entityKey);
			em.remove(obj);
		}

		tx.commit();
		em.close();
	}

	
}
