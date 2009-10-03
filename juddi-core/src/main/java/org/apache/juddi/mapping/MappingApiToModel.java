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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.log4j.Logger;
import org.uddi.api_v3.Description;
import org.uddi.sub_v3.ObjectFactory;
import org.uddi.v3_service.DispositionReportFaultMessage;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class MappingApiToModel {
	private static Logger logger = Logger.getLogger(MappingApiToModel.class);

	
	public static void mapPublisher(org.apache.juddi.api_v3.Publisher apiPublisher, 
									org.apache.juddi.model.Publisher modelPublisher) 
				   throws DispositionReportFaultMessage {

		modelPublisher.setAuthorizedName(apiPublisher.getAuthorizedName());
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

		modelBusinessEntity.setEntityKey(apiBusinessEntity.getBusinessKey());
		
		mapBusinessNames(apiBusinessEntity.getName(), modelBusinessEntity.getBusinessNames(), modelBusinessEntity);
		mapBusinessDescriptions(apiBusinessEntity.getDescription(), modelBusinessEntity.getBusinessDescrs(), modelBusinessEntity);
		mapDiscoveryUrls(apiBusinessEntity.getDiscoveryURLs(), modelBusinessEntity.getDiscoveryUrls(), modelBusinessEntity);
		mapContacts(apiBusinessEntity.getContacts(), modelBusinessEntity.getContacts(), modelBusinessEntity);
		mapBusinessIdentifiers(apiBusinessEntity.getIdentifierBag(), modelBusinessEntity.getBusinessIdentifiers(), modelBusinessEntity);
		if (apiBusinessEntity.getCategoryBag()!=null) {
			modelBusinessEntity.setCategoryBag(new org.apache.juddi.model.BusinessCategoryBag(modelBusinessEntity));
			mapCategoryBag(apiBusinessEntity.getCategoryBag(), modelBusinessEntity.getCategoryBag());
		}
		
		mapBusinessServices(apiBusinessEntity.getBusinessServices(), 
							modelBusinessEntity.getBusinessServices(), 
							modelBusinessEntity.getServiceProjections(), 
							modelBusinessEntity);
	}
	

	public static void mapBusinessNames(List<org.uddi.api_v3.Name> apiNameList, 
										List<org.apache.juddi.model.BusinessName> modelNameList,
										org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelNameList.clear();

		for (org.uddi.api_v3.Name apiName : apiNameList) {
			modelNameList.add(new org.apache.juddi.model.BusinessName(modelBusinessEntity, apiName.getLang(), apiName.getValue()));
		}
	}

	public static void mapBusinessDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
			  								   List<org.apache.juddi.model.BusinessDescr> modelDescList,
			  								   org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();
		
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			modelDescList.add(new org.apache.juddi.model.BusinessDescr(modelBusinessEntity, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapDiscoveryUrls(org.uddi.api_v3.DiscoveryURLs apiDiscUrls, 
										List<org.apache.juddi.model.DiscoveryUrl> modelDiscUrlList,
										org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelDiscUrlList.clear();
		
		if (apiDiscUrls != null) {
			List<org.uddi.api_v3.DiscoveryURL> apiDiscUrlList = apiDiscUrls.getDiscoveryURL();
			for (org.uddi.api_v3.DiscoveryURL apiDiscUrl : apiDiscUrlList) {
				modelDiscUrlList.add(new org.apache.juddi.model.DiscoveryUrl(modelBusinessEntity, apiDiscUrl.getUseType(), apiDiscUrl.getValue()));
			}
		}
	}

	public static void mapContacts(org.uddi.api_v3.Contacts apiContacts, 
								   List<org.apache.juddi.model.Contact> modelContactList,
								   org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelContactList.clear();
		
		if (apiContacts != null) {
			List<org.uddi.api_v3.Contact> apiContactList = apiContacts.getContact();
			for (org.uddi.api_v3.Contact apiContact : apiContactList) {
				org.apache.juddi.model.Contact modelContact = new org.apache.juddi.model.Contact(modelBusinessEntity);
				modelContact.setUseType(apiContact.getUseType());
				
				mapPersonNames(apiContact.getPersonName(), modelContact.getPersonNames(), modelContact, modelBusinessEntity.getEntityKey());
				mapContactDescriptions(apiContact.getDescription(), modelContact.getContactDescrs(), modelContact, modelBusinessEntity.getEntityKey());
				mapContactEmails(apiContact.getEmail(), modelContact.getEmails(), modelContact, modelBusinessEntity.getEntityKey());
				mapContactPhones(apiContact.getPhone(), modelContact.getPhones(), modelContact, modelBusinessEntity.getEntityKey());
				mapContactAddresses(apiContact.getAddress(), modelContact.getAddresses(), modelContact, modelBusinessEntity.getEntityKey());
				
				modelContactList.add(modelContact);
			}
		}
	}

	public static void mapContactDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											  List<org.apache.juddi.model.ContactDescr> modelDescList,
											  org.apache.juddi.model.Contact modelContact,
											  String businessKey) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			modelDescList.add(new org.apache.juddi.model.ContactDescr(modelContact, apiDesc.getLang(), apiDesc.getValue()));
		}
	}
	
	public static void mapPersonNames(List<org.uddi.api_v3.PersonName> apiPersonNameList, 
			  						  List<org.apache.juddi.model.PersonName> modelPersonNameList,
			  						  org.apache.juddi.model.Contact modelContact,
			  						  String businessKey) 
				throws DispositionReportFaultMessage {
		modelPersonNameList.clear();
		
		for (org.uddi.api_v3.PersonName apiPersonName : apiPersonNameList) {
		modelPersonNameList.add(new org.apache.juddi.model.PersonName(modelContact, apiPersonName.getLang(), apiPersonName.getValue()));
		}
	}
	
	public static void mapContactEmails(List<org.uddi.api_v3.Email> apiEmailList, 
										List<org.apache.juddi.model.Email> modelEmailList,
										org.apache.juddi.model.Contact modelContact,
										String businessKey) 
				   throws DispositionReportFaultMessage {
		modelEmailList.clear();

		for (org.uddi.api_v3.Email apiEmail : apiEmailList) {
			modelEmailList.add(new org.apache.juddi.model.Email(modelContact, apiEmail.getUseType(), apiEmail.getValue()));
		}
	}
	
	public static void mapContactPhones(List<org.uddi.api_v3.Phone> apiPhoneList, 
										List<org.apache.juddi.model.Phone> modelPhoneList,
										org.apache.juddi.model.Contact modelContact,
										String businessKey) 
				   throws DispositionReportFaultMessage {
		modelPhoneList.clear();

		for (org.uddi.api_v3.Phone apiPhone : apiPhoneList) {
			modelPhoneList.add(new org.apache.juddi.model.Phone(modelContact, apiPhone.getUseType(), apiPhone.getValue()));
		}
	}
	
	public static void mapContactAddresses(List<org.uddi.api_v3.Address> apiAddressList, 
										   List<org.apache.juddi.model.Address> modelAddressList,
										   org.apache.juddi.model.Contact modelContact,
										   String businessKey) 
				   throws DispositionReportFaultMessage {
		modelAddressList.clear();

		for (org.uddi.api_v3.Address apiAddress : apiAddressList) {
			org.apache.juddi.model.Address modelAddress = new org.apache.juddi.model.Address(modelContact);
			modelAddress.setSortCode(apiAddress.getSortCode());
			modelAddress.setTmodelKey(apiAddress.getTModelKey());
			modelAddress.setUseType(apiAddress.getUseType());
			
			mapAddressLines(apiAddress.getAddressLine(), modelAddress.getAddressLines(), modelAddress, businessKey, modelContact.getId());
			
			modelAddressList.add(modelAddress);
		}
	}

	public static void mapAddressLines(List<org.uddi.api_v3.AddressLine> apiAddressLineList, 
									   List<org.apache.juddi.model.AddressLine> modelAddressLineList,
									   org.apache.juddi.model.Address modelAddress,
									   String businessKey,
									   Long contactId) 
				   throws DispositionReportFaultMessage {
		modelAddressLineList.clear();

		for (org.uddi.api_v3.AddressLine apiAddressLine : apiAddressLineList) {
			modelAddressLineList.add(new org.apache.juddi.model.AddressLine(modelAddress, apiAddressLine.getValue(), apiAddressLine.getKeyName(), apiAddressLine.getKeyValue()));
		}
	}

	
	public static void mapBusinessIdentifiers(org.uddi.api_v3.IdentifierBag apiIdentifierBag, 
											  List<org.apache.juddi.model.BusinessIdentifier> modelIdentifierList,
											  org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelIdentifierList.clear();

		if (apiIdentifierBag != null) {
			List<org.uddi.api_v3.KeyedReference> apiKeyedRefList = apiIdentifierBag.getKeyedReference();
			for (org.uddi.api_v3.KeyedReference apiKeyedRef : apiKeyedRefList) {
				modelIdentifierList.add(new org.apache.juddi.model.BusinessIdentifier(modelBusinessEntity, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
			}
		}
	}
	
	public static void mapBusinessServices(org.uddi.api_v3.BusinessServices apiBusinessServices,
										   List<org.apache.juddi.model.BusinessService> modelBusinessServiceList,
										   List<org.apache.juddi.model.ServiceProjection> modelServiceProjectionList,
										   org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {
		modelBusinessServiceList.clear();

		if (apiBusinessServices != null) {
			List<org.uddi.api_v3.BusinessService> apiBusinessServiceList = apiBusinessServices.getBusinessService();
			for (org.uddi.api_v3.BusinessService apiBusinessService : apiBusinessServiceList) {
				org.apache.juddi.model.BusinessService modelBusinessService = new org.apache.juddi.model.BusinessService();

				// If the parent businessEntity key and the service businessEntity key (if provided) do not match, it's a projection.
				if (apiBusinessService.getBusinessKey() != null && apiBusinessService.getBusinessKey().length() > 0 && 
				    !modelBusinessEntity.getEntityKey().equalsIgnoreCase(apiBusinessService.getBusinessKey())) {

					modelBusinessService.setEntityKey(apiBusinessService.getServiceKey());
					org.apache.juddi.model.ServiceProjection modelServiceProjection = new org.apache.juddi.model.ServiceProjection(modelBusinessEntity, modelBusinessService);
					modelServiceProjectionList.add(modelServiceProjection);
				}
				else {
					mapBusinessService(apiBusinessService, modelBusinessService, modelBusinessEntity);
					modelBusinessServiceList.add(modelBusinessService);
				}
			}
		}
	}
	
	public static void mapBusinessService(org.uddi.api_v3.BusinessService apiBusinessService, 
										  org.apache.juddi.model.BusinessService modelBusinessService,
										  org.apache.juddi.model.BusinessEntity modelBusinessEntity) 
				   throws DispositionReportFaultMessage {

		modelBusinessService.setBusinessEntity(modelBusinessEntity);
		modelBusinessService.setEntityKey(apiBusinessService.getServiceKey());
		
		mapServiceNames(apiBusinessService.getName(), modelBusinessService.getServiceNames(), modelBusinessService);
		mapServiceDescriptions(apiBusinessService.getDescription(), modelBusinessService.getServiceDescrs(), modelBusinessService);
		if (apiBusinessService.getCategoryBag()!=null) {
			modelBusinessService.setCategoryBag(new org.apache.juddi.model.ServiceCategoryBag(modelBusinessService));
			mapCategoryBag(apiBusinessService.getCategoryBag(), modelBusinessService.getCategoryBag());
		}
		
		mapBindingTemplates(apiBusinessService.getBindingTemplates(), modelBusinessService.getBindingTemplates(), modelBusinessService);

	}

	
	
	public static void mapServiceNames(List<org.uddi.api_v3.Name> apiNameList, 
									   List<org.apache.juddi.model.ServiceName> modelNameList,
									   org.apache.juddi.model.BusinessService modelBusinessService) 
				   throws DispositionReportFaultMessage {
		modelNameList.clear();

		for (org.uddi.api_v3.Name apiName : apiNameList) {
			modelNameList.add(new org.apache.juddi.model.ServiceName(modelBusinessService, apiName.getLang(), apiName.getValue()));
		}
	}
	
	public static void mapServiceDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											  List<org.apache.juddi.model.ServiceDescr> modelDescList,
											  org.apache.juddi.model.BusinessService modelBusinessService) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			modelDescList.add(new org.apache.juddi.model.ServiceDescr(modelBusinessService, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapBindingTemplates(org.uddi.api_v3.BindingTemplates apiBindingTemplates, 
										   List<org.apache.juddi.model.BindingTemplate> modelBindingTemplateList,
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
		modelBindingTemplate.setEntityKey(apiBindingTemplate.getBindingKey());
		modelBindingTemplate.setAccessPointType(apiBindingTemplate.getAccessPoint().getUseType());
		modelBindingTemplate.setAccessPointUrl(apiBindingTemplate.getAccessPoint().getValue());
		if (apiBindingTemplate.getHostingRedirector() != null) {
			modelBindingTemplate.setHostingRedirector(apiBindingTemplate.getHostingRedirector().getBindingKey());
		}
		
		mapBindingDescriptions(apiBindingTemplate.getDescription(), modelBindingTemplate.getBindingDescrs(), modelBindingTemplate);
		if (apiBindingTemplate.getCategoryBag()!=null) {
			modelBindingTemplate.setCategoryBag(new org.apache.juddi.model.BindingCategoryBag(modelBindingTemplate));
			mapCategoryBag(apiBindingTemplate.getCategoryBag(), modelBindingTemplate.getCategoryBag());
		}
		mapTModelInstanceDetails(apiBindingTemplate.getTModelInstanceDetails(), modelBindingTemplate.getTmodelInstanceInfos(), modelBindingTemplate);
	}
	
	public static void mapBindingDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											  List<org.apache.juddi.model.BindingDescr> modelDescList,
											  org.apache.juddi.model.BindingTemplate modelBindingTemplate) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();
		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			modelDescList.add(new org.apache.juddi.model.BindingDescr(modelBindingTemplate, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapCategoryBag(org.uddi.api_v3.CategoryBag apiCategoryBag,
									  org.apache.juddi.model.CategoryBag modelCategoryBag)
				   throws DispositionReportFaultMessage {

		if (apiCategoryBag != null) {
			List<org.uddi.api_v3.KeyedReference> krList = apiCategoryBag.getKeyedReference();
			for (Object elem : krList) {
				if (elem instanceof org.uddi.api_v3.KeyedReference) {
					List<org.apache.juddi.model.KeyedReference> modelKeyedReferences=modelCategoryBag.getKeyedReferences();
					//modelKeyedReferences.clear();
					org.uddi.api_v3.KeyedReference apiKeyedReference = (org.uddi.api_v3.KeyedReference)elem;
					modelKeyedReferences.add(new org.apache.juddi.model.KeyedReference(modelCategoryBag, 
						apiKeyedReference.getTModelKey(), apiKeyedReference.getKeyName(), apiKeyedReference.getKeyValue()));
				}
			}
			List<org.uddi.api_v3.KeyedReferenceGroup> krgList = apiCategoryBag.getKeyedReferenceGroup(); 
			for (org.uddi.api_v3.KeyedReferenceGroup elem : krgList) {
				if (elem instanceof org.uddi.api_v3.KeyedReferenceGroup) {
					org.uddi.api_v3.KeyedReferenceGroup apiKeyedReferenceGroup = (org.uddi.api_v3.KeyedReferenceGroup) elem;

					org.apache.juddi.model.KeyedReferenceGroup modelKeyedReferenceGroup = new org.apache.juddi.model.KeyedReferenceGroup();
					List<org.apache.juddi.model.KeyedReferenceGroup> modelKeyedReferenceGroups=modelCategoryBag.getKeyedReferenceGroups();
					//modelKeyedReferenceGroups.clear();
					
					mapKeyedReferenceGroup(apiKeyedReferenceGroup, modelKeyedReferenceGroup, modelCategoryBag);
					
					modelKeyedReferenceGroups.add(modelKeyedReferenceGroup);
				}
			}
		}
	}

	public static void mapKeyedReferenceGroup(org.uddi.api_v3.KeyedReferenceGroup apiKeyedReferenceGroup,
											  org.apache.juddi.model.KeyedReferenceGroup modelKeyedReferenceGroup,
											  org.apache.juddi.model.CategoryBag modelCategoryBag)
				   throws DispositionReportFaultMessage {
		if (apiKeyedReferenceGroup != null) {
			modelKeyedReferenceGroup.setCategoryBag(modelCategoryBag);
			modelKeyedReferenceGroup.setTmodelKey(apiKeyedReferenceGroup.getTModelKey());
			
			if (apiKeyedReferenceGroup.getKeyedReference() != null) {
				List<org.apache.juddi.model.KeyedReference> modelKeyedReferences = modelKeyedReferenceGroup.getKeyedReferences();
				for (org.uddi.api_v3.KeyedReference apiKeyedReference : apiKeyedReferenceGroup.getKeyedReference()) {
					modelKeyedReferences.add(new org.apache.juddi.model.KeyedReference(modelKeyedReferenceGroup, 
						apiKeyedReference.getTModelKey(), apiKeyedReference.getKeyName(), apiKeyedReference.getKeyValue()));
				}
			}
			
		}
		
	}
	
	public static void mapTModelInstanceDetails(org.uddi.api_v3.TModelInstanceDetails apiTModelInstDetails, 
												List<org.apache.juddi.model.TmodelInstanceInfo> modelTModelInstInfoList,
												org.apache.juddi.model.BindingTemplate modelBindingTemplate) 
				   throws DispositionReportFaultMessage {
		modelTModelInstInfoList.clear();

		if (apiTModelInstDetails != null) {
			List<org.uddi.api_v3.TModelInstanceInfo> apiTModelInstInfoList = apiTModelInstDetails.getTModelInstanceInfo();
			for (org.uddi.api_v3.TModelInstanceInfo apiTModelInstInfo : apiTModelInstInfoList) {
				org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo = new org.apache.juddi.model.TmodelInstanceInfo(modelBindingTemplate, apiTModelInstInfo.getTModelKey());
				
				mapTModelInstanceInfoDescriptions(apiTModelInstInfo.getDescription(), modelTModelInstInfo.getTmodelInstanceInfoDescrs(), modelTModelInstInfo);
				mapInstanceDetails(apiTModelInstInfo.getInstanceDetails(), modelTModelInstInfo);
				
				modelTModelInstInfoList.add(modelTModelInstInfo);
			}
		}
	}

	public static void mapTModelInstanceInfoDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
														 List<org.apache.juddi.model.TmodelInstanceInfoDescr> modelDescList,
														 org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			modelDescList.add(new org.apache.juddi.model.TmodelInstanceInfoDescr(modelTModelInstInfo, apiDesc.getLang(), apiDesc.getValue()));
		}
	}

	public static void mapInstanceDetails(org.uddi.api_v3.InstanceDetails apiInstanceDetails, 
										  org.apache.juddi.model.TmodelInstanceInfo modelTmodelInstInfo) 
				   throws DispositionReportFaultMessage {
		modelTmodelInstInfo.getInstanceDetailsDescrs().clear();

		if (apiInstanceDetails != null) {
			List<org.uddi.api_v3.Description> descriptions = apiInstanceDetails.getDescription();
			List<org.uddi.api_v3.OverviewDoc> overviewdocs = apiInstanceDetails.getOverviewDoc();
			for (org.uddi.api_v3.Description apiDesc : descriptions) {
				org.apache.juddi.model.InstanceDetailsDescr modelInstanceDetailsDescr = 
						new org.apache.juddi.model.InstanceDetailsDescr(
							modelTmodelInstInfo, apiDesc.getLang(), apiDesc.getValue());
				modelTmodelInstInfo.getInstanceDetailsDescrs().add(modelInstanceDetailsDescr);
			}
			for (org.uddi.api_v3.OverviewDoc apiOverviewDoc : overviewdocs) {
				org.apache.juddi.model.OverviewDoc modelOverviewDoc = new org.apache.juddi.model.OverviewDoc(modelTmodelInstInfo);
				mapOverviewDoc(apiOverviewDoc, modelOverviewDoc);
				modelTmodelInstInfo.getOverviewDocs().add(modelOverviewDoc);
			}
			modelTmodelInstInfo.setInstanceParms((String)apiInstanceDetails.getInstanceParms());
		}
	}
	
	public static void mapOverviewDoc(org.uddi.api_v3.OverviewDoc apiOverviewDoc, 
									  org.apache.juddi.model.OverviewDoc modelOverviewDoc)
				throws DispositionReportFaultMessage {
		if (apiOverviewDoc != null) {
			
			List<Description> descContent = apiOverviewDoc.getDescription();
			for (Object elem : descContent) {
				org.uddi.api_v3.Description description = (org.uddi.api_v3.Description) elem;
				if (description != null) {
					org.apache.juddi.model.OverviewDocDescr modelOverviewDocDescr 
						= new org.apache.juddi.model.OverviewDocDescr(
							modelOverviewDoc, description.getLang(), description.getValue());
					modelOverviewDoc.getOverviewDocDescrs().add(modelOverviewDocDescr);
				}				
			}

			org.uddi.api_v3.OverviewURL elem = apiOverviewDoc.getOverviewURL();
			if (elem instanceof org.uddi.api_v3.OverviewURL) {
				org.uddi.api_v3.OverviewURL overviewURL = (org.uddi.api_v3.OverviewURL) elem;
				modelOverviewDoc.setOverviewUrl(overviewURL.getValue());
				modelOverviewDoc.setOverviewUrlUseType(overviewURL.getUseType());
			}

		}
	}
	
	public static void mapTModel(org.uddi.api_v3.TModel apiTModel, 
								 org.apache.juddi.model.Tmodel modelTModel) 
				   throws DispositionReportFaultMessage {

		modelTModel.setEntityKey(apiTModel.getTModelKey());
		modelTModel.setName(apiTModel.getName().getValue());
		modelTModel.setDeleted(apiTModel.isDeleted());

		mapTModelDescriptions(apiTModel.getDescription(), modelTModel.getTmodelDescrs(), modelTModel);
		mapTModelIdentifiers(apiTModel.getIdentifierBag(), modelTModel.getTmodelIdentifiers(), modelTModel);
		if (apiTModel.getCategoryBag()!=null) {
			modelTModel.setCategoryBag(new org.apache.juddi.model.TmodelCategoryBag(modelTModel));
			mapCategoryBag(apiTModel.getCategoryBag(), modelTModel.getCategoryBag());
		}
		mapTModelOverviewDocs(apiTModel.getOverviewDoc(), modelTModel.getOverviewDocs(), modelTModel);
	}

	public static void mapTModelDescriptions(List<org.uddi.api_v3.Description> apiDescList, 
											 List<org.apache.juddi.model.TmodelDescr> modelDescList,
											 org.apache.juddi.model.Tmodel modelTModel) 
				   throws DispositionReportFaultMessage {
		modelDescList.clear();

		for (org.uddi.api_v3.Description apiDesc : apiDescList) {
			modelDescList.add(new org.apache.juddi.model.TmodelDescr(modelTModel, apiDesc.getLang(), apiDesc.getValue()));
		}
	}
	
	public static void mapTModelIdentifiers(org.uddi.api_v3.IdentifierBag apiIdentifierBag, 
											List<org.apache.juddi.model.TmodelIdentifier> modelIdentifierList,
											org.apache.juddi.model.Tmodel modelTModel) 
				   throws DispositionReportFaultMessage {
		modelIdentifierList.clear();

		if (apiIdentifierBag != null) {
			List<org.uddi.api_v3.KeyedReference> apiKeyedRefList = apiIdentifierBag.getKeyedReference();
			for (org.uddi.api_v3.KeyedReference apiKeyedRef : apiKeyedRefList) {
				modelIdentifierList.add(new org.apache.juddi.model.TmodelIdentifier(modelTModel, apiKeyedRef.getTModelKey(), apiKeyedRef.getKeyName(), apiKeyedRef.getKeyValue()));
			}
		}
	}
	
	public static void mapTModelOverviewDocs(List<org.uddi.api_v3.OverviewDoc> apiOverviewDocList, 
			 List<org.apache.juddi.model.OverviewDoc> modelOverviewDocList,
			 org.apache.juddi.model.Tmodel modelTmodel)
		throws DispositionReportFaultMessage {
		modelOverviewDocList.clear();
		
		for (org.uddi.api_v3.OverviewDoc apiOverviewDoc : apiOverviewDocList) {
			org.apache.juddi.model.OverviewDoc modelOverviewDoc = new org.apache.juddi.model.OverviewDoc(modelTmodel);
			mapOverviewDoc(apiOverviewDoc, modelOverviewDoc);
			modelTmodel.getOverviewDocs().add(modelOverviewDoc);
		}
	}
	
	public static void mapPublisherAssertion(org.uddi.api_v3.PublisherAssertion apiPubAssertion, 
											 org.apache.juddi.model.PublisherAssertion modelPubAssertion) 
				   throws DispositionReportFaultMessage {

		modelPubAssertion.setId(new org.apache.juddi.model.PublisherAssertionId(apiPubAssertion.getFromKey(), apiPubAssertion.getToKey()));

		org.apache.juddi.model.BusinessEntity beFrom = new org.apache.juddi.model.BusinessEntity();
		beFrom.setEntityKey(apiPubAssertion.getFromKey());
		modelPubAssertion.setBusinessEntityByFromKey(beFrom);
		
		org.apache.juddi.model.BusinessEntity beTo = new org.apache.juddi.model.BusinessEntity();
		beFrom.setEntityKey(apiPubAssertion.getToKey());
		modelPubAssertion.setBusinessEntityByToKey(beTo);
		
		org.uddi.api_v3.KeyedReference apiKeyedRef = apiPubAssertion.getKeyedReference();
		if (apiKeyedRef != null) {
			modelPubAssertion.setTmodelKey(apiKeyedRef.getTModelKey());
			modelPubAssertion.setKeyName(apiKeyedRef.getKeyName());
			modelPubAssertion.setKeyValue(apiKeyedRef.getKeyValue());
		}
	}

	public static void mapSubscription(org.uddi.sub_v3.Subscription apiSubscription,
									   org.apache.juddi.model.Subscription modelSubscription) 
				   throws DispositionReportFaultMessage {

		modelSubscription.setSubscriptionKey(apiSubscription.getSubscriptionKey());
		modelSubscription.setBindingKey(apiSubscription.getBindingKey());
		if (apiSubscription.getNotificationInterval()!=null) {
			modelSubscription.setNotificationInterval(apiSubscription.getNotificationInterval().toString());
		}
		modelSubscription.setMaxEntities(apiSubscription.getMaxEntities());
		if (apiSubscription.getExpiresAfter() != null) {
			GregorianCalendar gc = apiSubscription.getExpiresAfter().toGregorianCalendar();
			modelSubscription.setExpiresAfter(new Date(gc.getTimeInMillis()));
		}
		
		if (modelSubscription.isBrief() != null) {
			modelSubscription.setBrief(apiSubscription.isBrief());
		} else {
			modelSubscription.setBrief(new Boolean(false));
		}
			
		try {
			String rawFilter = JAXBMarshaller.marshallToString(new ObjectFactory().createSubscriptionFilter(apiSubscription.getSubscriptionFilter()), "org.uddi.sub_v3");
			logger.debug("marshalled subscription filter:  " + rawFilter);
			modelSubscription.setSubscriptionFilter(rawFilter);

		} catch (JAXBException e) {
			logger.error("JAXBException while marshalling subscription filter", e);
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		}
		
	}
	
}
	
