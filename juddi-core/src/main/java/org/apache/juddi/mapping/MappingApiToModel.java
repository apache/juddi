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

package org.apache.juddi.mapping;

import java.util.List;
import java.util.Set;
import java.util.Date;

import javax.xml.bind.JAXBElement;

import org.uddi.v3_service.DispositionReportFaultMessage;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class MappingApiToModel {

	public static void mapPublisher(org.apache.juddi.api.datatype.Publisher apiPublisher, 
									org.apache.juddi.model.Publisher modelPublisher) 
				   throws DispositionReportFaultMessage {

		modelPublisher.setPublisherId(apiPublisher.getPublisherId());
		modelPublisher.setPublisherName(apiPublisher.getPublisherName());
		modelPublisher.setEmailAddress(apiPublisher.getEmailAddress());
		modelPublisher.setIsAdmin(apiPublisher.getIsAdmin());
		modelPublisher.setIsEnabled(apiPublisher.getIsEnabled());
		modelPublisher.setMaxBindingsPerService(apiPublisher.getMaxBindingsPerService());
		modelPublisher.setMaxBusinesses(apiPublisher.getMaxBusinesses());
		modelPublisher.setMaxServicesPerBusiness(apiPublisher.getMaxServicePerBusiness());
		modelPublisher.setMaxTmodels(apiPublisher.getMaxTModels());
	}
	
	public static void mapBusinessEntity(org.uddi.api_v3.BusinessEntity apiBusinessEntity, 
										 org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {

		modelBusinessEntity.setBusinessKey(apiBusinessEntity.getBusinessKey());
		modelBusinessEntity.setLastUpdate(new Date());
		
		mapBusinessNames(apiBusinessEntity.getName(), modelBusinessEntity.getBusinessNames(), modelBusinessEntity);
		mapBusinessDescriptions(apiBusinessEntity.getDescription(), modelBusinessEntity.getBusinessDescrs(), modelBusinessEntity);
		mapDiscoveryUrls(apiBusinessEntity.getDiscoveryURLs(), modelBusinessEntity.getDiscoveryUrls(), modelBusinessEntity);
		mapContacts(apiBusinessEntity.getContacts(), modelBusinessEntity.getContacts(), modelBusinessEntity);
		mapBusinessIdentifiers(apiBusinessEntity.getIdentifierBag(), modelBusinessEntity.getBusinessIdentifiers(), modelBusinessEntity);
		mapBusinessCategories(apiBusinessEntity.getCategoryBag(), modelBusinessEntity.getBusinessCategories(), modelBusinessEntity);
		
		mapBusinessServices(apiBusinessEntity.getBusinessServices(), modelBusinessEntity.getBusinessServices(), modelBusinessEntity);
	}
	

	public static void mapBusinessNames(List<org.uddi.api_v3.Name> apiNameList, 
										Set<org.apache.juddi.model.BusinessName> modelNameList,
										org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelNameList.clear();

		int id = 0;
		for (org.uddi.api_v3.Name apiName : apiNameList) {
			org.apache.juddi.model.BusinessNameId businessNameId = new org.apache.juddi.model.BusinessNameId(modelBusinessEntity.getBusinessKey(), id++);
			modelNameList.add(new org.apache.juddi.model.BusinessName(businessNameId, modelBusinessEntity, apiName.getLang(), apiName.getValue()));
		}
	}

	public static void mapBusinessDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
			  								   Set<org.apache.juddi.model.BusinessDescr> modelDescList,
			  								   org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		int id = 0;
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			org.apache.juddi.model.BusinessDescrId businessDescId = new org.apache.juddi.model.BusinessDescrId(modelBusinessEntity.getBusinessKey(), id++);
			modelDescList.add(new org.apache.juddi.model.BusinessDescr(businessDescId, modelBusinessEntity, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapDiscoveryUrls(org.uddi.api_v3.DiscoveryURLs apiDiscUrls, 
										Set<org.apache.juddi.model.DiscoveryUrl> modelDiscUrlList,
										org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelDiscUrlList.clear();
		
		if (apiDiscUrls != null) {
			List<org.uddi.api_v3.DiscoveryURL> apiDiscUrlList = apiDiscUrls.getDiscoveryURL();
			int id = 0;
			for (org.uddi.api_v3.DiscoveryURL apiDiscUrl : apiDiscUrlList) {
				org.apache.juddi.model.DiscoveryUrlId discUrlId = new org.apache.juddi.model.DiscoveryUrlId(modelBusinessEntity.getBusinessKey(), id++);
				modelDiscUrlList.add(new org.apache.juddi.model.DiscoveryUrl(discUrlId, modelBusinessEntity, apiDiscUrl.getUseType(), apiDiscUrl.getValue()));
			}
		}
	}

	public static void mapContacts(org.uddi.api_v3.Contacts apiContacts, 
								   Set<org.apache.juddi.model.Contact> modelContactList,
								   org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelContactList.clear();
		
		if (apiContacts != null) {
			List<org.uddi.api_v3.Contact> apiContactList = apiContacts.getContact();
			int id = 0;
			for (org.uddi.api_v3.Contact apiContact : apiContactList) {
				// TODO: The model only supports one personName per contact and it is just a string value (no language code).
				List<org.uddi.api_v3.PersonName> apiNameList = apiContact.getPersonName();
				String personName = null;
				if (apiNameList != null && apiNameList.size() > 0)
					personName = ((org.uddi.api_v3.PersonName)apiNameList.get(0)).getValue();

				org.apache.juddi.model.ContactId contactId = new org.apache.juddi.model.ContactId(modelBusinessEntity.getBusinessKey(), id++);
				org.apache.juddi.model.Contact modelContact = new org.apache.juddi.model.Contact(contactId, modelBusinessEntity, personName);
				modelContact.setUseType(apiContact.getUseType());
				
				mapContactDescriptions(apiContact.getDescription(), modelContact.getContactDescrs(), modelContact, modelBusinessEntity.getBusinessKey());
				mapContactEmails(apiContact.getEmail(), modelContact.getEmails(), modelContact, modelBusinessEntity.getBusinessKey());
				mapContactPhones(apiContact.getPhone(), modelContact.getPhones(), modelContact, modelBusinessEntity.getBusinessKey());
				mapContactAddresses(apiContact.getAddress(), modelContact.getAddresses(), modelContact, modelBusinessEntity.getBusinessKey());
				
				modelContactList.add(modelContact);
			}
		}
	}

	public static void mapContactDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											  Set<org.apache.juddi.model.ContactDescr> modelDescList,
											  org.apache.juddi.model.Contact modelContact,
											  String businessKey) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		int id = 0;
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			org.apache.juddi.model.ContactDescrId contactDescId = new org.apache.juddi.model.ContactDescrId(businessKey, modelContact.getId().getContactId(), id++);
			modelDescList.add(new org.apache.juddi.model.ContactDescr(contactDescId, modelContact, apiDesc.getLang(), apiDesc.getValue()));
		}
	}
	
	public static void mapContactEmails(List<org.uddi.api_v3.Email> apiEmailList, 
										Set<org.apache.juddi.model.Email> modelEmailList,
										org.apache.juddi.model.Contact modelContact,
										String businessKey) 
				   throws DispositionReportFaultMessage {
		modelEmailList.clear();

		int id = 0;
		for (org.uddi.api_v3.Email apiEmail : apiEmailList) {
			org.apache.juddi.model.EmailId emailId = new org.apache.juddi.model.EmailId(businessKey, modelContact.getId().getContactId(), id++);
			modelEmailList.add(new org.apache.juddi.model.Email(emailId, modelContact, apiEmail.getUseType(), apiEmail.getValue()));
		}
	}
	
	public static void mapContactPhones(List<org.uddi.api_v3.Phone> apiPhoneList, 
										Set<org.apache.juddi.model.Phone> modelPhoneList,
										org.apache.juddi.model.Contact modelContact,
										String businessKey) 
				   throws DispositionReportFaultMessage {
		modelPhoneList.clear();

		int id = 0;
		for (org.uddi.api_v3.Phone apiPhone : apiPhoneList) {
			org.apache.juddi.model.PhoneId phoneId = new org.apache.juddi.model.PhoneId(businessKey, modelContact.getId().getContactId(), id++);
			modelPhoneList.add(new org.apache.juddi.model.Phone(phoneId, modelContact, apiPhone.getUseType(), apiPhone.getValue()));
		}
	}
	
	public static void mapContactAddresses(List<org.uddi.api_v3.Address> apiAddressList, 
										   Set<org.apache.juddi.model.Address> modelAddressList,
										   org.apache.juddi.model.Contact modelContact,
										   String businessKey) 
				   throws DispositionReportFaultMessage {
		modelAddressList.clear();

		int id = 0;
		for (org.uddi.api_v3.Address apiAddress : apiAddressList) {
			org.apache.juddi.model.AddressId addressId = new org.apache.juddi.model.AddressId(businessKey, modelContact.getId().getContactId(), id++);
			org.apache.juddi.model.Address modelAddress = new org.apache.juddi.model.Address(addressId, modelContact);
			modelAddress.setSortCode(apiAddress.getSortCode());
			modelAddress.setTmodelKey(apiAddress.getTModelKey());
			modelAddress.setUseType(apiAddress.getUseType());
			
			mapAddressLines(apiAddress.getAddressLine(), modelAddress.getAddressLines(), modelAddress, businessKey, modelContact.getId().getContactId());
			
			modelAddressList.add(modelAddress);
		}
	}

	public static void mapAddressLines(List<org.uddi.api_v3.AddressLine> apiAddressLineList, 
									   Set<org.apache.juddi.model.AddressLine> modelAddressLineList,
									   org.apache.juddi.model.Address modelAddress,
									   String businessKey,
									   int contactId) 
				   throws DispositionReportFaultMessage {
		modelAddressLineList.clear();

		int id = 0;
		for (org.uddi.api_v3.AddressLine apiAddressLine : apiAddressLineList) {
			org.apache.juddi.model.AddressLineId addressLineId = new org.apache.juddi.model.AddressLineId(businessKey, contactId, modelAddress.getId().getAddressId(), id++);
			modelAddressLineList.add(new org.apache.juddi.model.AddressLine(addressLineId, modelAddress, apiAddressLine.getValue(), apiAddressLine.getKeyName(), apiAddressLine.getKeyValue()));
		}
	}

	
	public static void mapBusinessIdentifiers(org.uddi.api_v3.IdentifierBag apiIdentifierBag, 
											  Set<org.apache.juddi.model.BusinessIdentifier> modelIdentifierList,
											  org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelIdentifierList.clear();

		if (apiIdentifierBag != null) {
			List<org.uddi.api_v3.KeyedReference> apiKeyedRefList = apiIdentifierBag.getKeyedReference();
			int id = 0;
			for (org.uddi.api_v3.KeyedReference apiKeyedRef : apiKeyedRefList) {
				org.apache.juddi.model.BusinessIdentifierId identifierId = new org.apache.juddi.model.BusinessIdentifierId(modelBusinessEntity.getBusinessKey(), id++);
				modelIdentifierList.add(new org.apache.juddi.model.BusinessIdentifier(identifierId, modelBusinessEntity, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
			}
		}
	}

	public static void mapBusinessCategories(org.uddi.api_v3.CategoryBag apiCategoryBag, 
											 Set<org.apache.juddi.model.BusinessCategory> modelCategoryList,
											 org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelCategoryList.clear();

		if (apiCategoryBag != null) {
			List<JAXBElement<?>> apiCategoryList = apiCategoryBag.getContent();
			int id = 0;
			for (JAXBElement<?> elem : apiCategoryList) {
				// TODO:  Currently, the model doesn't allow for the persistence of keyedReference groups.  This must be incorporated into the model.  For now
				// the KeyedReferenceGroups are ignored.
				if (elem.getValue() instanceof org.uddi.api_v3.KeyedReference) {
					org.uddi.api_v3.KeyedReference apiKeyedRef = (org.uddi.api_v3.KeyedReference)elem.getValue();
					
					org.apache.juddi.model.BusinessCategoryId categoryId = new org.apache.juddi.model.BusinessCategoryId(modelBusinessEntity.getBusinessKey(), id++);
					modelCategoryList.add(new org.apache.juddi.model.BusinessCategory(categoryId, modelBusinessEntity, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
				}
			}
		}
	}
	
	public static void mapBusinessServices(org.uddi.api_v3.BusinessServices apiBusinessServices,
										   Set<org.apache.juddi.model.BusinessService> modelBusinessServiceList,
										   org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelBusinessServiceList.clear();

		if (apiBusinessServices != null) {
			List<org.uddi.api_v3.BusinessService> apiBusinessServiceList = apiBusinessServices.getBusinessService();
			for (org.uddi.api_v3.BusinessService apiBusinessService : apiBusinessServiceList) {
				org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();

				mapBusinessService(apiBusinessService, modelBusinessService, modelBusinessEntity);
				
				modelBusinessServiceList.add(modelBusinessService);
			}
		}
	}
	
	public static void mapBusinessService(org.uddi.api_v3.BusinessService apiBusinessService, 
										  org.apache.juddi.model.BusinessService modelBusinessService,
										  org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {

		modelBusinessService.setBusinessEntity(modelBusinessEntity);
		modelBusinessService.setServiceKey(apiBusinessService.getServiceKey());
		modelBusinessService.setLastUpdate(new Date());
		
		mapServiceNames(apiBusinessService.getName(), modelBusinessService.getServiceNames(), modelBusinessService);
		mapServiceDescriptions(apiBusinessService.getDescription(), modelBusinessService.getServiceDescrs(), modelBusinessService);
		mapServiceCategories(apiBusinessService.getCategoryBag(), modelBusinessService.getServiceCategories(), modelBusinessService);
		
		mapBindingTemplates(apiBusinessService.getBindingTemplates(), modelBusinessService.getBindingTemplates(), modelBusinessService);

	}

	public static void mapServiceNames(List<org.uddi.api_v3.Name> apiNameList, 
									   Set<org.apache.juddi.model.ServiceName> modelNameList,
									   org.apache.juddi.model.BusinessService modelBusinessService) 
				   throws DispositionReportFaultMessage {
		modelNameList.clear();

		int id = 0;
		for (org.uddi.api_v3.Name apiName : apiNameList) {
			org.apache.juddi.model.ServiceNameId serviceNameId = new org.apache.juddi.model.ServiceNameId(modelBusinessService.getServiceKey(), id++);
			modelNameList.add(new org.apache.juddi.model.ServiceName(serviceNameId, modelBusinessService, apiName.getLang(), apiName.getValue()));
		}
	}
	
	public static void mapServiceDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											  Set<org.apache.juddi.model.ServiceDescr> modelDescList,
											  org.apache.juddi.model.BusinessService modelBusinessService) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		int id = 0;
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			org.apache.juddi.model.ServiceDescrId serviceDescId = new org.apache.juddi.model.ServiceDescrId(modelBusinessService.getServiceKey(), id++);
			modelDescList.add(new org.apache.juddi.model.ServiceDescr(serviceDescId, modelBusinessService, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapServiceCategories(org.uddi.api_v3.CategoryBag apiCategoryBag, 
											Set<org.apache.juddi.model.ServiceCategory> modelCategoryList,
											org.apache.juddi.model.BusinessService modelBusinessService) 
				   throws DispositionReportFaultMessage {
		modelCategoryList.clear();

		if (apiCategoryBag != null) {
			List<JAXBElement<?>> apiCategoryList = apiCategoryBag.getContent();
			int id = 0;
			for (JAXBElement<?> elem : apiCategoryList) {
				// TODO:  Currently, the model doesn't allow for the persistence of keyedReference groups.  This must be incorporated into the model.  For now
				// the KeyedReferenceGroups are ignored.
				if (elem.getValue() instanceof org.uddi.api_v3.KeyedReference) {
					org.uddi.api_v3.KeyedReference apiKeyedRef = (org.uddi.api_v3.KeyedReference)elem.getValue();

					org.apache.juddi.model.ServiceCategoryId categoryId = new org.apache.juddi.model.ServiceCategoryId(modelBusinessService.getServiceKey(), id++);
					modelCategoryList.add(new org.apache.juddi.model.ServiceCategory(categoryId, modelBusinessService, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
				}
			}
		}
	}

	public static void mapBindingTemplates(org.uddi.api_v3.BindingTemplates apiBindingTemplates, 
										   Set<org.apache.juddi.model.BindingTemplate> modelBindingTemplateList,
										   org.apache.juddi.model.BusinessService modelBusinessService) 
				   throws DispositionReportFaultMessage {
		modelBindingTemplateList.clear();

		if (apiBindingTemplates != null) {
			List<org.uddi.api_v3.BindingTemplate> apiBindingTemplateList = apiBindingTemplates.getBindingTemplate();
			for (org.uddi.api_v3.BindingTemplate apiBindingTemplate : apiBindingTemplateList) {
				org.apache.juddi.model.BindingTemplate modelBindingTemplate = new org.apache.juddi.model.BindingTemplate();

				mapBindingTemplate(apiBindingTemplate, modelBindingTemplate, modelBusinessService);

				modelBindingTemplateList.add(modelBindingTemplate);
			}
		}
	}
	
	public static void mapBindingTemplate(org.uddi.api_v3.BindingTemplate apiBindingTemplate, 
										  org.apache.juddi.model.BindingTemplate modelBindingTemplate,
										  org.apache.juddi.model.BusinessService modelBusinessService) 
				   throws DispositionReportFaultMessage {

		modelBindingTemplate.setBusinessService(modelBusinessService);
		modelBindingTemplate.setBindingKey(apiBindingTemplate.getBindingKey());
		modelBindingTemplate.setLastUpdate(new Date());
		modelBindingTemplate.setAccessPointType(apiBindingTemplate.getAccessPoint().getUseType());
		modelBindingTemplate.setAccessPointUrl(apiBindingTemplate.getAccessPoint().getValue());
		//modelBindingTemplate.setHostingRedirector(apiBindingTemplate.getHostingRedirector().getBindingKey());
		
		mapBindingDescriptions(apiBindingTemplate.getDescription(), modelBindingTemplate.getBindingDescrs(), modelBindingTemplate);
		mapBindingCategories(apiBindingTemplate.getCategoryBag(), modelBindingTemplate.getBindingCategories(), modelBindingTemplate);
		mapTModelInstanceDetails(apiBindingTemplate.getTModelInstanceDetails(), modelBindingTemplate.getTmodelInstanceInfos(), modelBindingTemplate);
	}
	
	public static void mapBindingDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											  Set<org.apache.juddi.model.BindingDescr> modelDescList,
											  org.apache.juddi.model.BindingTemplate modelBindingTemplate) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		int id = 0;
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			org.apache.juddi.model.BindingDescrId bindingDescId = new org.apache.juddi.model.BindingDescrId(modelBindingTemplate.getBindingKey(), id++);
			modelDescList.add(new org.apache.juddi.model.BindingDescr(bindingDescId, modelBindingTemplate, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapBindingCategories(org.uddi.api_v3.CategoryBag apiCategoryBag,
											Set<org.apache.juddi.model.BindingCategory> modelCategoryList,
											org.apache.juddi.model.BindingTemplate modelBindingTemplate)
				   throws DispositionReportFaultMessage {
		modelCategoryList.clear();

		if (apiCategoryBag != null) {
			List<JAXBElement<?>> apiCategoryList = apiCategoryBag.getContent();
			int id = 0;
			for (JAXBElement<?> elem : apiCategoryList) {
				// TODO:  Currently, the model doesn't allow for the persistence of keyedReference groups.  This must be incorporated into the model.  For now
				// the KeyedReferenceGroups are ignored.
				if (elem.getValue() instanceof org.uddi.api_v3.KeyedReference) {
					org.uddi.api_v3.KeyedReference apiKeyedRef = (org.uddi.api_v3.KeyedReference)elem.getValue();

					org.apache.juddi.model.BindingCategoryId categoryId = new org.apache.juddi.model.BindingCategoryId(modelBindingTemplate.getBindingKey(), id++);
					modelCategoryList.add(new org.apache.juddi.model.BindingCategory(categoryId, modelBindingTemplate, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
				}
			}
		}
	}

	public static void mapTModelInstanceDetails(org.uddi.api_v3.TModelInstanceDetails apiTModelInstDetails, 
												Set<org.apache.juddi.model.TmodelInstanceInfo> modelTModelInstInfoList,
												org.apache.juddi.model.BindingTemplate modelBindingTemplate) 
				   throws DispositionReportFaultMessage {
		modelTModelInstInfoList.clear();

		if (apiTModelInstDetails != null) {
			List<org.uddi.api_v3.TModelInstanceInfo> apiTModelInstInfoList = apiTModelInstDetails.getTModelInstanceInfo();
			int id = 0;
			for (org.uddi.api_v3.TModelInstanceInfo apiTModelInstInfo : apiTModelInstInfoList) {
				org.apache.juddi.model.TmodelInstanceInfoId tmodelInstInfoId = new org.apache.juddi.model.TmodelInstanceInfoId(modelBindingTemplate.getBindingKey(), id++);
				org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo = new org.apache.juddi.model.TmodelInstanceInfo(tmodelInstInfoId, modelBindingTemplate, apiTModelInstInfo.getTModelKey());
				
				mapTModelInstanceInfoDescriptions(apiTModelInstInfo.getDescription(), modelTModelInstInfo.getTmodelInstanceInfoDescrs(), modelTModelInstInfo, modelBindingTemplate.getBindingKey());
				mapInstanceDetails(apiTModelInstInfo.getInstanceDetails(), modelTModelInstInfo, modelBindingTemplate.getBindingKey());
				
				modelTModelInstInfoList.add(modelTModelInstInfo);
			}
		}
	}

	public static void mapTModelInstanceInfoDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
														 Set<org.apache.juddi.model.TmodelInstanceInfoDescr> modelDescList,
														 org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo,
														 String bindingKey) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		int id = 0;
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			org.apache.juddi.model.TmodelInstanceInfoDescrId tmodelInstInfoDescId = new org.apache.juddi.model.TmodelInstanceInfoDescrId(bindingKey, modelTModelInstInfo.getId().getTmodelInstanceInfoId(), id++);
			modelDescList.add(new org.apache.juddi.model.TmodelInstanceInfoDescr(tmodelInstInfoDescId, modelTModelInstInfo, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapInstanceDetails(org.uddi.api_v3.InstanceDetails apiInstanceDetails, 
										  org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo,
										  String bindingKey) 
				   throws DispositionReportFaultMessage {
		modelTModelInstInfo.getInstanceDetailsDescrs().clear();

		if (apiInstanceDetails != null) {
			List<JAXBElement<?>> apiInstanceDetailsContent = apiInstanceDetails.getContent();
			int docId = 0;
			int descId = 0;
			for (JAXBElement<?> elem : apiInstanceDetailsContent) {
				if (elem.getValue() instanceof org.uddi.api_v3.OverviewDoc) {
					org.uddi.api_v3.OverviewDoc apiOverviewDoc = (org.uddi.api_v3.OverviewDoc)elem.getValue();
					// TODO: OverviewDoc is not mapped properly in the model.
				}
				else if (elem.getValue() instanceof org.uddi.api_v3.Description) {
					org.uddi.api_v3.Description apiDesc = (org.uddi.api_v3.Description)elem.getValue();
					
					org.apache.juddi.model.InstanceDetailsDescrId instanceDetailsDescId = new org.apache.juddi.model.InstanceDetailsDescrId(bindingKey, modelTModelInstInfo.getId().getTmodelInstanceInfoId(), descId++);
					modelTModelInstInfo.getInstanceDetailsDescrs().add(new org.apache.juddi.model.InstanceDetailsDescr(instanceDetailsDescId, modelTModelInstInfo, apiDesc.getLang(), apiDesc.getValue()));
				}
				else if (elem.getValue() instanceof String) {
					modelTModelInstInfo.setInstanceParms((String)elem.getValue());
				}
			}
		}
	}
	
	public static void mapTModel(org.uddi.api_v3.TModel apiTModel, 
								 org.apache.juddi.model.Tmodel modelTModel) 
				   throws DispositionReportFaultMessage {

		modelTModel.setTmodelKey(apiTModel.getTModelKey());
		modelTModel.setLastUpdate(new Date());
		modelTModel.setName(apiTModel.getName().getValue());
		modelTModel.setDeleted(apiTModel.isDeleted());

		mapTModelDescriptions(apiTModel.getDescription(), modelTModel.getTmodelDescrs(), modelTModel);
		mapTModelIdentifiers(apiTModel.getIdentifierBag(), modelTModel.getTmodelIdentifiers(), modelTModel);
		mapTModelCategories(apiTModel.getCategoryBag(), modelTModel.getTmodelCategories(), modelTModel);
		//TODO: OverviewDoc - model doesn't have logical mapping

	}

	public static void mapTModelDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											 Set<org.apache.juddi.model.TmodelDescr> modelDescList,
											 org.apache.juddi.model.Tmodel modelTModel) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		int id = 0;
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			org.apache.juddi.model.TmodelDescrId tmodelDescId = new org.apache.juddi.model.TmodelDescrId(modelTModel.getTmodelKey(), id++);
			modelDescList.add(new org.apache.juddi.model.TmodelDescr(tmodelDescId, modelTModel, apiDesc.getLang(), apiDesc.getValue()));
		}
	}
	
	public static void mapTModelIdentifiers(org.uddi.api_v3.IdentifierBag apiIdentifierBag, 
											Set<org.apache.juddi.model.TmodelIdentifier> modelIdentifierList,
											org.apache.juddi.model.Tmodel modelTModel) 
				   throws DispositionReportFaultMessage {
		modelIdentifierList.clear();

		if (apiIdentifierBag != null) {
			List<org.uddi.api_v3.KeyedReference> apiKeyedRefList = apiIdentifierBag.getKeyedReference();
			int id = 0;
			for (org.uddi.api_v3.KeyedReference apiKeyedRef : apiKeyedRefList) {
				org.apache.juddi.model.TmodelIdentifierId identifierId = new org.apache.juddi.model.TmodelIdentifierId(modelTModel.getTmodelKey(), id++);
				modelIdentifierList.add(new org.apache.juddi.model.TmodelIdentifier(identifierId, modelTModel, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
			}
		}
	}
	
	public static void mapTModelCategories(org.uddi.api_v3.CategoryBag apiCategoryBag,
										   Set<org.apache.juddi.model.TmodelCategory> modelCategoryList,
										   org.apache.juddi.model.Tmodel modelTModel)
				   throws DispositionReportFaultMessage {
		modelCategoryList.clear();

		if (apiCategoryBag != null) {
			List<JAXBElement<?>> apiCategoryList = apiCategoryBag.getContent();
			int id = 0;
			for (JAXBElement<?> elem : apiCategoryList) {
				// TODO:  Currently, the model doesn't allow for the persistence of keyedReference groups.  This must be incorporated into the model.  For now
				// the KeyedReferenceGroups are ignored.
				if (elem.getValue() instanceof org.uddi.api_v3.KeyedReference) {
					org.uddi.api_v3.KeyedReference apiKeyedRef = (org.uddi.api_v3.KeyedReference)elem.getValue();

					org.apache.juddi.model.TmodelCategoryId categoryId = new org.apache.juddi.model.TmodelCategoryId(modelTModel.getTmodelKey(), id++);
					modelCategoryList.add(new org.apache.juddi.model.TmodelCategory(categoryId, modelTModel, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
				}
			}
		}
	}
	
	public static void mapPublisherAssertion(org.uddi.api_v3.PublisherAssertion apiPubAssertion, 
											 org.apache.juddi.model.PublisherAssertion modelPubAssertion) 
				   throws DispositionReportFaultMessage {

		modelPubAssertion.setId(new org.apache.juddi.model.PublisherAssertionId(apiPubAssertion.getFromKey(), apiPubAssertion.getToKey()));

		org.apache.juddi.model.BusinessEntity beFrom = new org.apache.juddi.model.BusinessEntity();
		beFrom.setBusinessKey(apiPubAssertion.getFromKey());
		modelPubAssertion.setBusinessEntityByFromKey(beFrom);
		
		org.apache.juddi.model.BusinessEntity beTo = new org.apache.juddi.model.BusinessEntity();
		beFrom.setBusinessKey(apiPubAssertion.getToKey());
		modelPubAssertion.setBusinessEntityByToKey(beTo);
		
		org.uddi.api_v3.KeyedReference apiKeyedRef = apiPubAssertion.getKeyedReference();
		if (apiKeyedRef != null) {
			modelPubAssertion.setTmodelKey(apiKeyedRef.getTModelKey());
			modelPubAssertion.setKeyName(apiKeyedRef.getKeyName());
			modelPubAssertion.setKeyValue(apiKeyedRef.getKeyValue());
		}
	}

	public static void mapSubscription(org.uddi.sub_v3.Subscription apiSubscription,
			org.apache.juddi.model.Subscription modelSubscription) throws DispositionReportFaultMessage {
		modelSubscription.setBindingKey(apiSubscription.getBindingKey());
		modelSubscription.setSubscriptionKey(apiSubscription.getSubscriptionKey());
		modelSubscription.setNotificationInterval(apiSubscription.getNotificationInterval().toString());
	}
	
}
	
