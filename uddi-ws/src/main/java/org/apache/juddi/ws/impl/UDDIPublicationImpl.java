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

package org.apache.juddi.ws.impl;

import java.util.List;
import java.util.Iterator;

import javax.jws.WebService;
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
import org.apache.juddi.error.UDDIErrorHelper;
import org.apache.juddi.config.Configuration;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@WebService(serviceName="UDDIPublicationService", 
			endpointInterface="org.uddi.v3_service.UDDIPublicationPortType")
public class UDDIPublicationImpl implements UDDIPublicationPortType {

	
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

		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		List<String> entityKeyList = body.getBindingKey();
		Iterator<String> entityKeyListListItr = entityKeyList.iterator();
		while (entityKeyListListItr.hasNext()) {
			String entityKey = entityKeyListListItr.next();
			
			JPAUtil.deleteEntity(org.apache.juddi.model.BindingTemplate.class, entityKey);
		}
	}

	public void deleteBusiness(DeleteBusiness body)
			throws DispositionReportFaultMessage {

		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		List<String> entityKeyList = body.getBusinessKey();
		Iterator<String> entityKeyListListItr = entityKeyList.iterator();
		while (entityKeyListListItr.hasNext()) {
			String entityKey = entityKeyListListItr.next();
			
			JPAUtil.deleteEntity(org.apache.juddi.model.BusinessEntity.class, entityKey);
		}
	}

	public void deletePublisherAssertions(DeletePublisherAssertions body)
			throws DispositionReportFaultMessage {

		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		List<org.uddi.api_v3.PublisherAssertion> apiPubAssertionList = body.getPublisherAssertion();
		Iterator<org.uddi.api_v3.PublisherAssertion> apiPubAssertionListItr = apiPubAssertionList.iterator();
		while (apiPubAssertionListItr.hasNext()) {
			org.uddi.api_v3.PublisherAssertion apiPubAssertion = apiPubAssertionListItr.next();
			org.apache.juddi.model.PublisherAssertionId pubAssertionId = new org.apache.juddi.model.PublisherAssertionId(apiPubAssertion.getFromKey(), apiPubAssertion.getToKey());

			JPAUtil.deleteEntity(org.apache.juddi.model.PublisherAssertion.class, pubAssertionId);
		}
	}

	public void deleteService(DeleteService body)
			throws DispositionReportFaultMessage {

		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		List<String> entityKeyList = body.getServiceKey();
		Iterator<String> entityKeyListListItr = entityKeyList.iterator();
		while (entityKeyListListItr.hasNext()) {
			String entityKey = entityKeyListListItr.next();
			
			JPAUtil.deleteEntity(org.apache.juddi.model.BusinessService.class, entityKey);
		}
	}


	public void deleteTModel(DeleteTModel body)
			throws DispositionReportFaultMessage {

		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		List<String> entityKeyList = body.getTModelKey();
		Iterator<String> entityKeyListListItr = entityKeyList.iterator();
		while (entityKeyListListItr.hasNext()) {
			String entityKey = entityKeyListListItr.next();
			
			JPAUtil.deleteEntity(org.apache.juddi.model.Tmodel.class, entityKey);
		}
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

		org.uddi.api_v3.BindingDetail result = new org.uddi.api_v3.BindingDetail();

		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		List<org.uddi.api_v3.BindingTemplate> apiBindingTemplateList = body.getBindingTemplate();
		Iterator<org.uddi.api_v3.BindingTemplate> apiBindingTemplateListItr = apiBindingTemplateList.iterator();
		while (apiBindingTemplateListItr.hasNext()) {
			org.uddi.api_v3.BindingTemplate apiBindingTemplate = apiBindingTemplateListItr.next();
			
			//TODO:  Validate the input here
			if (JPAUtil.getEntity(org.apache.juddi.model.BusinessService.class, apiBindingTemplate.getServiceKey()) == null) {
				throw new DispositionReportFaultMessage(Configuration.getGlobalMessage("errors.invalidkey.ServiceNotFound") + ":  " + apiBindingTemplate.getServiceKey(), 
														UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_KEY_PASSED));
			}
			//TODO:  Test if key is null, and if so, apply key-generation strategy
			String bindingKey = apiBindingTemplate.getBindingKey();
			
			org.apache.juddi.model.BindingTemplate modelBindingTemplate = new org.apache.juddi.model.BindingTemplate();
			org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();
			modelBusinessService.setServiceKey(apiBindingTemplate.getServiceKey());
			
			MappingApiToModel.mapBindingTemplate(apiBindingTemplate, modelBindingTemplate, modelBusinessService);
			
			JPAUtil.persistEntity(modelBindingTemplate, modelBindingTemplate.getBindingKey());
			
			result.getBindingTemplate().add(apiBindingTemplate);
		}
		return result;
	}


	public BusinessDetail saveBusiness(SaveBusiness body)
			throws DispositionReportFaultMessage {

		org.uddi.api_v3.BusinessDetail result = new org.uddi.api_v3.BusinessDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();
		
		List<org.uddi.api_v3.BusinessEntity> apiBusinessEntityList = body.getBusinessEntity();
		Iterator<org.uddi.api_v3.BusinessEntity> apiBusinessEntityListItr = apiBusinessEntityList.iterator();
		while (apiBusinessEntityListItr.hasNext()) {
			org.uddi.api_v3.BusinessEntity apiBusinessEntity = apiBusinessEntityListItr.next();
			
			//TODO:  Validate the input here
			//TODO:  Test if key is null, and if so, apply key-generation strategy
			String businessKey = apiBusinessEntity.getBusinessKey();
			
			org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();
			
			MappingApiToModel.mapBusinessEntity(apiBusinessEntity, modelBusinessEntity);
			
			JPAUtil.persistEntity(modelBusinessEntity, modelBusinessEntity.getBusinessKey());

			result.getBusinessEntity().add(apiBusinessEntity);
		}
		return result;
	}


	public ServiceDetail saveService(SaveService body)
			throws DispositionReportFaultMessage {

		org.uddi.api_v3.ServiceDetail result = new org.uddi.api_v3.ServiceDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		List<org.uddi.api_v3.BusinessService> apiBusinessServiceList = body.getBusinessService();
		Iterator<org.uddi.api_v3.BusinessService> apiBusinessServiceListItr = apiBusinessServiceList.iterator();
		while (apiBusinessServiceListItr.hasNext()) {
			org.uddi.api_v3.BusinessService apiBusinessService = apiBusinessServiceListItr.next();
			
			//TODO:  Validate the input here
			if (JPAUtil.getEntity(org.apache.juddi.model.BusinessEntity.class, apiBusinessService.getBusinessKey()) == null) {
				throw new DispositionReportFaultMessage(Configuration.getGlobalMessage("errors.invalidkey.BusinessNotFound") + ":  " + apiBusinessService.getBusinessKey(), 
														UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_KEY_PASSED));
			}
			//TODO:  Test if key is null, and if so, apply key-generation strategy
			String serviceKey = apiBusinessService.getServiceKey();
			
			org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();
			org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();
			modelBusinessEntity.setBusinessKey(apiBusinessService.getBusinessKey());
			
			MappingApiToModel.mapBusinessService(apiBusinessService, modelBusinessService, modelBusinessEntity);
			
			JPAUtil.persistEntity(modelBusinessService, modelBusinessService.getServiceKey());
			
			result.getBusinessService().add(apiBusinessService);
		}
		return result;
	}


	public TModelDetail saveTModel(SaveTModel body)
			throws DispositionReportFaultMessage {

		org.uddi.api_v3.TModelDetail result = new org.uddi.api_v3.TModelDetail();
		
		// TODO: Perform necessary authentication logic
		String authInfo = body.getAuthInfo();

		List<org.uddi.api_v3.TModel> apiTModelList = body.getTModel();
		Iterator<org.uddi.api_v3.TModel> apiTModelListItr = apiTModelList.iterator();
		while (apiTModelListItr.hasNext()) {
			org.uddi.api_v3.TModel apiTModel = apiTModelListItr.next();
			
			//TODO:  Validate the input here
			//TODO:  Test if key is null, and if so, apply key-generation strategy
			String tmodelKey = apiTModel.getTModelKey();
			
			org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
			
			MappingApiToModel.mapTModel(apiTModel, modelTModel);
			
			JPAUtil.persistEntity(modelTModel, modelTModel.getTmodelKey());
			
			result.getTModel().add(apiTModel);
		}
		return result;
	}


	public void setPublisherAssertions(String authInfo,
			Holder<List<PublisherAssertion>> publisherAssertion)
			throws DispositionReportFaultMessage {
		// TODO Auto-generated method stub

	}

}
