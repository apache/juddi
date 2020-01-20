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

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.dom.DOMResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.AuthenticatedService;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.model.Address;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.BusinessEntity;
import org.apache.juddi.model.BusinessService;
import org.apache.juddi.model.CanonicalizationMethod;
import org.apache.juddi.model.Contact;
import org.apache.juddi.model.ControlMessage;
import org.apache.juddi.model.Edge;
import org.apache.juddi.model.EdgeReceiverAlternate;
import org.apache.juddi.model.KeyDataValue;
import org.apache.juddi.model.KeyInfo;
import org.apache.juddi.model.Node;
import org.apache.juddi.model.Operator;
import org.apache.juddi.model.OperatorStatusType;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.Reference;
import org.apache.juddi.model.ReplicationConfigurationNode;
import org.apache.juddi.model.Signature;
import org.apache.juddi.model.SignatureMethod;
import org.apache.juddi.model.SignatureTransform;
import org.apache.juddi.model.SignatureTransformDataValue;
import org.apache.juddi.model.SignatureValue;
import org.apache.juddi.model.SignedInfo;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.SaveBinding;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.sub_v3.ObjectFactory;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.w3._2000._09.xmldsig_.DSAKeyValueType;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.KeyValueType;
import org.w3._2000._09.xmldsig_.PGPDataType;
import org.w3._2000._09.xmldsig_.RSAKeyValueType;
import org.w3._2000._09.xmldsig_.ReferenceType;
import org.w3._2000._09.xmldsig_.RetrievalMethodType;
import org.w3._2000._09.xmldsig_.SPKIDataType;
import org.w3._2000._09.xmldsig_.TransformType;
import org.w3._2000._09.xmldsig_.TransformsType;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3._2000._09.xmldsig_.X509IssuerSerialType;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSSerializer;

/**
 * Handles mapping from the internal jUDDL API Model to the JAXB/XML/WSDL UDDI
 * data structures
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class MappingApiToModel {

        private static Log logger = LogFactory.getLog(MappingApiToModel.class);

        /**
         *
         * @param apiPublisher
         * @param modelPublisher
         * @throws DispositionReportFaultMessage
         */
        public static void mapPublisher(org.apache.juddi.api_v3.Publisher apiPublisher,
                org.apache.juddi.model.Publisher modelPublisher)
                throws DispositionReportFaultMessage {
                modelPublisher.setAuthorizedName(apiPublisher.getAuthorizedName());
                modelPublisher.setPublisherName(apiPublisher.getPublisherName());
                modelPublisher.setEmailAddress(apiPublisher.getEmailAddress());
                modelPublisher.setIsAdmin(apiPublisher.isIsAdmin());
                modelPublisher.setIsEnabled(apiPublisher.isIsEnabled());
                modelPublisher.setMaxBindingsPerService(apiPublisher.getMaxBindingsPerService());
                modelPublisher.setMaxBusinesses(apiPublisher.getMaxBusinesses());
                modelPublisher.setMaxServicesPerBusiness(apiPublisher.getMaxServicePerBusiness());
                modelPublisher.setMaxTmodels(apiPublisher.getMaxTModels());
                mapPublisherSignatures(apiPublisher.getSignature(), modelPublisher);
        }

        /**
         *
         * @param apiBusinessEntity
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
        public static void mapBusinessEntity(org.uddi.api_v3.BusinessEntity apiBusinessEntity,
                org.apache.juddi.model.BusinessEntity modelBusinessEntity)
                throws DispositionReportFaultMessage {

                modelBusinessEntity.setEntityKey(apiBusinessEntity.getBusinessKey());

                mapBusinessNames(apiBusinessEntity.getName(), modelBusinessEntity.getBusinessNames(), modelBusinessEntity);
                mapBusinessDescriptions(apiBusinessEntity.getDescription(), modelBusinessEntity.getBusinessDescrs(), modelBusinessEntity);
                mapDiscoveryUrls(apiBusinessEntity.getDiscoveryURLs(), modelBusinessEntity.getDiscoveryUrls(), modelBusinessEntity);
                mapContacts(apiBusinessEntity.getContacts(), modelBusinessEntity.getContacts(), modelBusinessEntity);
                mapBusinessIdentifiers(apiBusinessEntity.getIdentifierBag(), modelBusinessEntity.getBusinessIdentifiers(), modelBusinessEntity);
                if (apiBusinessEntity.getCategoryBag() != null) {
                        modelBusinessEntity.setCategoryBag(new org.apache.juddi.model.BusinessCategoryBag(modelBusinessEntity));
                        mapCategoryBag(apiBusinessEntity.getCategoryBag(), modelBusinessEntity.getCategoryBag());
                }

                mapBusinessServices(apiBusinessEntity.getBusinessServices(),
                        modelBusinessEntity.getBusinessServices(),
                        modelBusinessEntity.getServiceProjections(),
                        modelBusinessEntity);

                mapBusinessSignature(apiBusinessEntity.getSignature(), modelBusinessEntity);
        }

        public static List<Signature> mapApiSignaturesToModelSignatures(List<org.w3._2000._09.xmldsig_.SignatureType> apiSignatures)
                throws DispositionReportFaultMessage {
                List<Signature> modelSignatures = new ArrayList<Signature>();
                modelSignatures.clear();
                for (org.w3._2000._09.xmldsig_.SignatureType signatureType : apiSignatures) {
                        Signature modelSignature = new Signature();

                        org.w3._2000._09.xmldsig_.SignedInfoType apiSignedInfo = signatureType.getSignedInfo();
                        SignedInfo modelSignedInfo = new SignedInfo();
                        modelSignature.setSignedInfo(modelSignedInfo);

                        String canonicalizationAlgMethod = apiSignedInfo.getCanonicalizationMethod().getAlgorithm();
                        CanonicalizationMethod modelCanonMethod = new CanonicalizationMethod();
                        modelSignedInfo.setCanonicalizationMethod(modelCanonMethod);
                        modelCanonMethod.setAlgorithm(canonicalizationAlgMethod);

                        SignatureMethod modelSigMethod = new SignatureMethod();
                        modelSignedInfo.setSignatureMethod(modelSigMethod);
                        String sigMethod = apiSignedInfo.getSignatureMethod().getAlgorithm();
                        modelSigMethod.setAlgorithm(sigMethod);

                        List<org.w3._2000._09.xmldsig_.ReferenceType> apiReferenceList = apiSignedInfo.getReference();
                        for (org.w3._2000._09.xmldsig_.ReferenceType apiReference : apiReferenceList) {
                                Reference ref = mapReference(modelSignedInfo, apiReference);
                                modelSignedInfo.getReference().add(ref);
                        }

                        modelSignedInfo.setCanonicalizationMethod(modelCanonMethod);

                        org.w3._2000._09.xmldsig_.SignatureValueType apiSignatureValue = signatureType.getSignatureValue();
                        SignatureValue modelSignatureValue = new SignatureValue();
                        byte[] signatureValueBytes = apiSignatureValue.getValue();
                        String signatureValueXmlID = apiSignatureValue.getId();
                        modelSignatureValue.setValue(signatureValueBytes);
                        modelSignatureValue.setXmlID(signatureValueXmlID);
                        modelSignature.setSignatureValue(modelSignatureValue);

                        org.w3._2000._09.xmldsig_.KeyInfoType apiKeyInfo = signatureType.getKeyInfo();
                        String apiKeyInfoXmlID = apiKeyInfo.getId();
                        KeyInfo modelKeyInfo = new KeyInfo();
                        modelSignature.setKeyInfo(modelKeyInfo);
                        modelKeyInfo.setXmlID(apiKeyInfoXmlID);

                        List<Object> apiKeyInfoContentList = apiKeyInfo.getContent();
                        List<KeyDataValue> keyInfoDataValues = modelKeyInfo.getKeyDataValue();
                        for (Object apiKeyInfoContentObj : apiKeyInfoContentList) {
                                if (apiKeyInfoContentObj instanceof JAXBElement) {
                                        JAXBElement apiKeyInfoContentJAXB = (JAXBElement) apiKeyInfoContentObj;
                                        String apiKeyInfoContentTagName = apiKeyInfoContentJAXB.getName().getLocalPart();
                                        if (apiKeyInfoContentJAXB.getValue() instanceof X509DataType) {
                                                KeyDataValue modelX509KeyData = mapX509DataType(apiKeyInfoContentJAXB, modelKeyInfo);
                                                keyInfoDataValues.add(modelX509KeyData);
                                        } else if (apiKeyInfoContentTagName.equals("KeyName")) {
                                                KeyDataValue modelKeyNameKDV = mapKeyName(apiKeyInfoContentJAXB);
                                                modelKeyNameKDV.setKeyInfo(modelKeyInfo);
                                                keyInfoDataValues.add(modelKeyNameKDV);
                                        } else if (apiKeyInfoContentTagName.equals("KeyValue")) {
                                                KeyDataValue modelKeyValueKDV = mapKeyValue(apiKeyInfoContentJAXB, keyInfoDataValues);
                                                modelKeyValueKDV.setKeyInfo(modelKeyInfo);
                                                keyInfoDataValues.add(modelKeyValueKDV);
                                        } else if (apiKeyInfoContentTagName.equals("MgmtData")) {
                                                KeyDataValue modelKeyValueKDV = new KeyDataValue();
                                                modelKeyValueKDV.setKeyDataName(apiKeyInfoContentTagName);
                                                modelKeyValueKDV.setKeyDataType("String");
                                                modelKeyValueKDV.setKeyDataValueString((String) apiKeyInfoContentJAXB.getValue());
                                                modelKeyValueKDV.setKeyInfo(modelKeyInfo);
                                                keyInfoDataValues.add(modelKeyValueKDV);
                                        } else if (apiKeyInfoContentTagName.equals("RetrievalMethod")) {
                                                RetrievalMethodType retrievalMethodType = (RetrievalMethodType) apiKeyInfoContentJAXB.getValue();
                                                KeyDataValue retrievalMethodTypeKDV = mapRetrievalMethod(apiKeyInfoContentTagName, modelKeyInfo, retrievalMethodType);
                                                keyInfoDataValues.add(retrievalMethodTypeKDV);
                                        } else if (apiKeyInfoContentTagName.equals("PGPData")) {
                                                PGPDataType pgpDataType = (PGPDataType) apiKeyInfoContentJAXB.getValue();
                                                KeyDataValue pgpDataTypeKDV = mapPGPDataType(apiKeyInfoContentTagName, modelKeyInfo, pgpDataType);
                                                keyInfoDataValues.add(pgpDataTypeKDV);
                                        } else if (apiKeyInfoContentTagName.equals("SPKIData")) {
                                                SPKIDataType spkiDataType = (SPKIDataType) apiKeyInfoContentJAXB.getValue();
                                                KeyDataValue spkiDataTypeKDV = mapSPKIDataType(apiKeyInfoContentTagName, modelKeyInfo, spkiDataType);
                                                keyInfoDataValues.add(spkiDataTypeKDV);
                                        } else {
                                                throw new RuntimeException("Unrecognized tag: " + apiKeyInfoContentTagName + " type: " + apiKeyInfoContentJAXB.getValue().getClass().getCanonicalName());
                                        }
                                }
                        }

                        modelSignatures.add(modelSignature);
                }
                return modelSignatures;
        }

        /**
         *
         * @param apiSignatures
         * @param modelBusinessService
         * @throws DispositionReportFaultMessage
         */
        public static void mapBusinessServiceSignature(List<org.w3._2000._09.xmldsig_.SignatureType> apiSignatures, BusinessService modelBusinessService)
                throws DispositionReportFaultMessage {
                List<Signature> modelSignatures = mapApiSignaturesToModelSignatures(apiSignatures);
                for (Signature modelSignature : modelSignatures) {
                        modelSignature.setBusinessService(modelBusinessService);
                }
                modelBusinessService.setSignatures(modelSignatures);
        }

        /**
         *
         * @param apiSignatures
         * @param modelTmodel
         * @throws DispositionReportFaultMessage
         */
        public static void mapTmodelSignatures(List<org.w3._2000._09.xmldsig_.SignatureType> apiSignatures, Tmodel modelTmodel)
                throws DispositionReportFaultMessage {
                List<Signature> modelSignatures = mapApiSignaturesToModelSignatures(apiSignatures);
                for (Signature modelSignature : modelSignatures) {
                        modelSignature.setTmodel(modelTmodel);
                }
                modelTmodel.setSignatures(modelSignatures);
        }

        /**
         *
         * @param apiSignatures
         * @param modelBindingTemplate
         * @throws DispositionReportFaultMessage
         */
        public static void mapBindingTemplateSignatures(List<org.w3._2000._09.xmldsig_.SignatureType> apiSignatures, BindingTemplate modelBindingTemplate)
                throws DispositionReportFaultMessage {
                List<Signature> modelSignatures = mapApiSignaturesToModelSignatures(apiSignatures);
                for (Signature modelSignature : modelSignatures) {
                        modelSignature.setBindingTemplate(modelBindingTemplate);
                }
                modelBindingTemplate.setSignatures(modelSignatures);
        }

        /**
         *
         * @param apiSignatures
         * @param modelPublisher
         * @throws DispositionReportFaultMessage
         */
        public static void mapPublisherSignatures(List<org.w3._2000._09.xmldsig_.SignatureType> apiSignatures, Publisher modelPublisher)
                throws DispositionReportFaultMessage {
                List<Signature> modelSignatures = mapApiSignaturesToModelSignatures(apiSignatures);
                for (Signature modelSignature : modelSignatures) {
                        modelSignature.setPublisher(modelPublisher);
                }
                modelPublisher.setSignatures(modelSignatures);
        }

        /**
         *
         * @param apiSignatures
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
        public static void mapBusinessSignature(List<org.w3._2000._09.xmldsig_.SignatureType> apiSignatures,
                org.apache.juddi.model.BusinessEntity modelBusinessEntity)
                throws DispositionReportFaultMessage {
                List<Signature> modelSignatures = mapApiSignaturesToModelSignatures(apiSignatures);
                for (Signature modelSignature : modelSignatures) {
                        modelSignature.setBusinessEntity(modelBusinessEntity);
                }
                modelBusinessEntity.setSignatures(modelSignatures);
        }

        /**
         *
         * @param apiNameList
         * @param modelNameList
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
        public static void mapBusinessNames(List<org.uddi.api_v3.Name> apiNameList,
                List<org.apache.juddi.model.BusinessName> modelNameList,
                org.apache.juddi.model.BusinessEntity modelBusinessEntity)
                throws DispositionReportFaultMessage {
                modelNameList.clear();

                for (org.uddi.api_v3.Name apiName : apiNameList) {
                        modelNameList.add(new org.apache.juddi.model.BusinessName(modelBusinessEntity, apiName.getLang(), apiName.getValue()));
                }
        }

        /**
         *
         * @param apiDescList
         * @param modelDescList
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
        public static void mapBusinessDescriptions(List<org.uddi.api_v3.Description> apiDescList,
                List<org.apache.juddi.model.BusinessDescr> modelDescList,
                org.apache.juddi.model.BusinessEntity modelBusinessEntity)
                throws DispositionReportFaultMessage {
                modelDescList.clear();

                for (org.uddi.api_v3.Description apiDesc : apiDescList) {
                        modelDescList.add(new org.apache.juddi.model.BusinessDescr(modelBusinessEntity, apiDesc.getLang(), apiDesc.getValue()));
                }
        }

        /**
         *
         * @param apiDiscUrls
         * @param modelDiscUrlList
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiContacts
         * @param modelContactList
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
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
                                mapContactAddresses(apiContact.getAddress(), modelContact.getAddresses(), modelContact);

                                modelContactList.add(modelContact);
                        }
                }
        }

        /**
         *
         * @param apiDescList
         * @param modelDescList
         * @param modelContact
         * @param businessKey
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiPersonNameList
         * @param modelPersonNameList
         * @param modelContact
         * @param businessKey
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiEmailList
         * @param modelEmailList
         * @param modelContact
         * @param businessKey
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiPhoneList
         * @param modelPhoneList
         * @param modelContact
         * @param businessKey
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiAddressList
         * @param modelAddressList
         * @param modelContact
         * @param businessKey
         * @throws DispositionReportFaultMessage
         */
        public static void mapContactAddresses(List<org.uddi.api_v3.Address> apiAddressList,
                List<org.apache.juddi.model.Address> modelAddressList,
                org.apache.juddi.model.Contact modelContact
        )
                throws DispositionReportFaultMessage {
                modelAddressList.clear();

                for (org.uddi.api_v3.Address apiAddress : apiAddressList) {
                        org.apache.juddi.model.Address modelAddress = new org.apache.juddi.model.Address(modelContact);
                        modelAddress.setSortCode(apiAddress.getSortCode());
                        modelAddress.setTmodelKey(apiAddress.getTModelKey());
                        modelAddress.setUseType(apiAddress.getUseType());

                        mapAddressLines(apiAddress.getAddressLine(), modelAddress.getAddressLines(), modelAddress, modelContact.getId());

                        modelAddressList.add(modelAddress);
                }
        }

        /**
         *
         * @param apiAddressLineList
         * @param modelAddressLineList
         * @param modelAddress
         *
         * @param contactId
         * @throws DispositionReportFaultMessage
         */
        public static void mapAddressLines(List<org.uddi.api_v3.AddressLine> apiAddressLineList,
                List<org.apache.juddi.model.AddressLine> modelAddressLineList,
                org.apache.juddi.model.Address modelAddress,
                Long contactId)
                throws DispositionReportFaultMessage {
                modelAddressLineList.clear();

                for (org.uddi.api_v3.AddressLine apiAddressLine : apiAddressLineList) {
                        modelAddressLineList.add(new org.apache.juddi.model.AddressLine(modelAddress, apiAddressLine.getValue(), apiAddressLine.getKeyName(), apiAddressLine.getKeyValue()));
                }
        }

        /**
         *
         * @param apiIdentifierBag
         * @param modelIdentifierList
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiBusinessServices
         * @param modelBusinessServiceList
         * @param modelServiceProjectionList
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
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
                                if (apiBusinessService.getBusinessKey() != null && apiBusinessService.getBusinessKey().length() > 0
                                        && !modelBusinessEntity.getEntityKey().equalsIgnoreCase(apiBusinessService.getBusinessKey())) {

                                        modelBusinessService.setEntityKey(apiBusinessService.getServiceKey());
                                        org.apache.juddi.model.ServiceProjection modelServiceProjection = new org.apache.juddi.model.ServiceProjection(modelBusinessEntity, modelBusinessService);

                                        modelServiceProjectionList.add(modelServiceProjection);
                                } else {
                                        mapBusinessService(apiBusinessService, modelBusinessService, modelBusinessEntity);
                                        modelBusinessServiceList.add(modelBusinessService);
                                }
                        }
                }
        }

        /**
         *
         * @param apiBusinessService
         * @param modelBusinessService
         * @param modelBusinessEntity
         * @throws DispositionReportFaultMessage
         */
        public static void mapBusinessService(org.uddi.api_v3.BusinessService apiBusinessService,
                org.apache.juddi.model.BusinessService modelBusinessService,
                org.apache.juddi.model.BusinessEntity modelBusinessEntity)
                throws DispositionReportFaultMessage {

                modelBusinessService.setBusinessEntity(modelBusinessEntity);
                modelBusinessService.setEntityKey(apiBusinessService.getServiceKey());

                mapServiceNames(apiBusinessService.getName(), modelBusinessService.getServiceNames(), modelBusinessService);
                mapServiceDescriptions(apiBusinessService.getDescription(), modelBusinessService.getServiceDescrs(), modelBusinessService);
                if (apiBusinessService.getCategoryBag() != null) {
                        modelBusinessService.setCategoryBag(new org.apache.juddi.model.ServiceCategoryBag(modelBusinessService));
                        mapCategoryBag(apiBusinessService.getCategoryBag(), modelBusinessService.getCategoryBag());
                }

                mapBindingTemplates(apiBusinessService.getBindingTemplates(), modelBusinessService.getBindingTemplates(), modelBusinessService);
                mapBusinessServiceSignature(apiBusinessService.getSignature(), modelBusinessService);
        }

        /**
         *
         * @param apiNameList
         * @param modelNameList
         * @param modelBusinessService
         * @throws DispositionReportFaultMessage
         */
        public static void mapServiceNames(List<org.uddi.api_v3.Name> apiNameList,
                List<org.apache.juddi.model.ServiceName> modelNameList,
                org.apache.juddi.model.BusinessService modelBusinessService)
                throws DispositionReportFaultMessage {
                modelNameList.clear();

                for (org.uddi.api_v3.Name apiName : apiNameList) {
                        modelNameList.add(new org.apache.juddi.model.ServiceName(modelBusinessService, apiName.getLang(), apiName.getValue()));
                }
        }

        /**
         *
         * @param apiDescList
         * @param modelDescList
         * @param modelBusinessService
         * @throws DispositionReportFaultMessage
         */
        public static void mapServiceDescriptions(List<org.uddi.api_v3.Description> apiDescList,
                List<org.apache.juddi.model.ServiceDescr> modelDescList,
                org.apache.juddi.model.BusinessService modelBusinessService)
                throws DispositionReportFaultMessage {
                modelDescList.clear();

                for (org.uddi.api_v3.Description apiDesc : apiDescList) {
                        modelDescList.add(new org.apache.juddi.model.ServiceDescr(modelBusinessService, apiDesc.getLang(), apiDesc.getValue()));
                }
        }

        /**
         *
         * @param apiBindingTemplates
         * @param modelBindingTemplateList
         * @param modelBusinessService
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiBindingTemplate
         * @param modelBindingTemplate
         * @param modelBusinessService
         * @throws DispositionReportFaultMessage
         */
        public static void mapBindingTemplate(org.uddi.api_v3.BindingTemplate apiBindingTemplate,
                org.apache.juddi.model.BindingTemplate modelBindingTemplate,
                org.apache.juddi.model.BusinessService modelBusinessService)
                throws DispositionReportFaultMessage {

                modelBindingTemplate.setBusinessService(modelBusinessService);
                modelBindingTemplate.setEntityKey(apiBindingTemplate.getBindingKey());
                if (apiBindingTemplate.getAccessPoint() != null) {
                        modelBindingTemplate.setAccessPointType(apiBindingTemplate.getAccessPoint().getUseType());
                        modelBindingTemplate.setAccessPointUrl(apiBindingTemplate.getAccessPoint().getValue());
                }
                if (apiBindingTemplate.getHostingRedirector() != null) {
                        modelBindingTemplate.setHostingRedirector(apiBindingTemplate.getHostingRedirector().getBindingKey());
                }

                mapBindingDescriptions(apiBindingTemplate.getDescription(), modelBindingTemplate.getBindingDescrs(), modelBindingTemplate);
                if (apiBindingTemplate.getCategoryBag() != null) {
                        modelBindingTemplate.setCategoryBag(new org.apache.juddi.model.BindingCategoryBag(modelBindingTemplate));
                        mapCategoryBag(apiBindingTemplate.getCategoryBag(), modelBindingTemplate.getCategoryBag());
                }
                mapTModelInstanceDetails(apiBindingTemplate.getTModelInstanceDetails(), modelBindingTemplate.getTmodelInstanceInfos(), modelBindingTemplate);
                mapBindingTemplateSignatures(apiBindingTemplate.getSignature(), modelBindingTemplate);
        }

        /**
         *
         * @param apiDescList
         * @param modelDescList
         * @param modelBindingTemplate
         * @throws DispositionReportFaultMessage
         */
        public static void mapBindingDescriptions(List<org.uddi.api_v3.Description> apiDescList,
                List<org.apache.juddi.model.BindingDescr> modelDescList,
                org.apache.juddi.model.BindingTemplate modelBindingTemplate)
                throws DispositionReportFaultMessage {
                modelDescList.clear();
                for (org.uddi.api_v3.Description apiDesc : apiDescList) {
                        modelDescList.add(new org.apache.juddi.model.BindingDescr(modelBindingTemplate, apiDesc.getLang(), apiDesc.getValue()));
                }
        }

        /**
         *
         * @param apiCategoryBag
         * @param modelCategoryBag
         * @throws DispositionReportFaultMessage
         */
        public static void mapCategoryBag(org.uddi.api_v3.CategoryBag apiCategoryBag,
                org.apache.juddi.model.CategoryBag modelCategoryBag)
                throws DispositionReportFaultMessage {

                if (apiCategoryBag != null) {
                        List<org.uddi.api_v3.KeyedReference> krList = apiCategoryBag.getKeyedReference();
                        for (Object elem : krList) {
                                if (elem instanceof org.uddi.api_v3.KeyedReference) {
                                        List<org.apache.juddi.model.KeyedReference> modelKeyedReferences = modelCategoryBag.getKeyedReferences();
                                        //modelKeyedReferences.clear();
                                        org.uddi.api_v3.KeyedReference apiKeyedReference = (org.uddi.api_v3.KeyedReference) elem;
                                        modelKeyedReferences.add(new org.apache.juddi.model.KeyedReference(modelCategoryBag,
                                                apiKeyedReference.getTModelKey(), apiKeyedReference.getKeyName(), apiKeyedReference.getKeyValue()));
                                }
                        }
                        List<org.uddi.api_v3.KeyedReferenceGroup> krgList = apiCategoryBag.getKeyedReferenceGroup();
                        for (org.uddi.api_v3.KeyedReferenceGroup elem : krgList) {
                                if (elem instanceof org.uddi.api_v3.KeyedReferenceGroup) {
                                        org.uddi.api_v3.KeyedReferenceGroup apiKeyedReferenceGroup = (org.uddi.api_v3.KeyedReferenceGroup) elem;

                                        org.apache.juddi.model.KeyedReferenceGroup modelKeyedReferenceGroup = new org.apache.juddi.model.KeyedReferenceGroup();
                                        List<org.apache.juddi.model.KeyedReferenceGroup> modelKeyedReferenceGroups = modelCategoryBag.getKeyedReferenceGroups();
                                        //modelKeyedReferenceGroups.clear();

                                        mapKeyedReferenceGroup(apiKeyedReferenceGroup, modelKeyedReferenceGroup, modelCategoryBag);

                                        modelKeyedReferenceGroups.add(modelKeyedReferenceGroup);
                                }
                        }
                }
        }

        /**
         *
         * @param apiKeyedReferenceGroup
         * @param modelKeyedReferenceGroup
         * @param modelCategoryBag
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiTModelInstDetails
         * @param modelTModelInstInfoList
         * @param modelBindingTemplate
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiDescList
         * @param modelDescList
         * @param modelTModelInstInfo
         * @throws DispositionReportFaultMessage
         */
        public static void mapTModelInstanceInfoDescriptions(List<org.uddi.api_v3.Description> apiDescList,
                List<org.apache.juddi.model.TmodelInstanceInfoDescr> modelDescList,
                org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo)
                throws DispositionReportFaultMessage {
                modelDescList.clear();

                for (org.uddi.api_v3.Description apiDesc : apiDescList) {
                        modelDescList.add(new org.apache.juddi.model.TmodelInstanceInfoDescr(modelTModelInstInfo, apiDesc.getLang(), apiDesc.getValue()));
                }
        }

        /**
         *
         * @param apiInstanceDetails
         * @param modelTmodelInstInfo
         * @throws DispositionReportFaultMessage
         */
        public static void mapInstanceDetails(org.uddi.api_v3.InstanceDetails apiInstanceDetails,
                org.apache.juddi.model.TmodelInstanceInfo modelTmodelInstInfo)
                throws DispositionReportFaultMessage {
                modelTmodelInstInfo.getInstanceDetailsDescrs().clear();

                if (apiInstanceDetails != null) {
                        List<org.uddi.api_v3.Description> descriptions = apiInstanceDetails.getDescription();
                        List<org.uddi.api_v3.OverviewDoc> overviewdocs = apiInstanceDetails.getOverviewDoc();
                        for (org.uddi.api_v3.Description apiDesc : descriptions) {
                                org.apache.juddi.model.InstanceDetailsDescr modelInstanceDetailsDescr
                                        = new org.apache.juddi.model.InstanceDetailsDescr(
                                                modelTmodelInstInfo, apiDesc.getLang(), apiDesc.getValue());
                                modelTmodelInstInfo.getInstanceDetailsDescrs().add(modelInstanceDetailsDescr);
                        }
                        for (org.uddi.api_v3.OverviewDoc apiOverviewDoc : overviewdocs) {
                                org.apache.juddi.model.OverviewDoc modelOverviewDoc = new org.apache.juddi.model.OverviewDoc(modelTmodelInstInfo);
                                mapOverviewDoc(apiOverviewDoc, modelOverviewDoc);
                                modelTmodelInstInfo.getOverviewDocs().add(modelOverviewDoc);
                        }
                        modelTmodelInstInfo.setInstanceParms((String) apiInstanceDetails.getInstanceParms());
                }
        }

        /**
         *
         * @param apiOverviewDoc
         * @param modelOverviewDoc
         * @throws DispositionReportFaultMessage
         */
        public static void mapOverviewDoc(org.uddi.api_v3.OverviewDoc apiOverviewDoc,
                org.apache.juddi.model.OverviewDoc modelOverviewDoc)
                throws DispositionReportFaultMessage {
                if (apiOverviewDoc != null) {

                        List<Description> descContent = apiOverviewDoc.getDescription();
                        for (Object elem : descContent) {
                                org.uddi.api_v3.Description description = (org.uddi.api_v3.Description) elem;
                                if (description != null) {
                                        org.apache.juddi.model.OverviewDocDescr modelOverviewDocDescr = new org.apache.juddi.model.OverviewDocDescr(
                                                modelOverviewDoc, description.getLang(), description.getValue());
                                        modelOverviewDoc.getOverviewDocDescrs().add(modelOverviewDocDescr);
                                }
                        }

                        org.uddi.api_v3.OverviewURL elem = apiOverviewDoc.getOverviewURL();
                        if (elem instanceof org.uddi.api_v3.OverviewURL) {
                                org.uddi.api_v3.OverviewURL overviewURL = elem;
                                modelOverviewDoc.setOverviewUrl(overviewURL.getValue());
                                modelOverviewDoc.setOverviewUrlUseType(overviewURL.getUseType());
                        }

                }
        }

        /**
         *
         * @param apiTModel
         * @param modelTModel
         * @throws DispositionReportFaultMessage
         */
        public static void mapTModel(org.uddi.api_v3.TModel apiTModel,
                org.apache.juddi.model.Tmodel modelTModel)
                throws DispositionReportFaultMessage {

                modelTModel.setEntityKey(apiTModel.getTModelKey());
                modelTModel.setName(apiTModel.getName().getValue());
                modelTModel.setLangCode(apiTModel.getName().getLang());
                modelTModel.setDeleted(apiTModel.isDeleted());

                mapTModelDescriptions(apiTModel.getDescription(), modelTModel.getTmodelDescrs(), modelTModel);
                mapTModelIdentifiers(apiTModel.getIdentifierBag(), modelTModel.getTmodelIdentifiers(), modelTModel);
                if (apiTModel.getCategoryBag() != null) {
                        modelTModel.setCategoryBag(new org.apache.juddi.model.TmodelCategoryBag(modelTModel));
                        mapCategoryBag(apiTModel.getCategoryBag(), modelTModel.getCategoryBag());
                }
                mapTModelOverviewDocs(apiTModel.getOverviewDoc(), modelTModel.getOverviewDocs(), modelTModel);
                mapTmodelSignatures(apiTModel.getSignature(), modelTModel);
        }

        /**
         *
         * @param apiDescList
         * @param modelDescList
         * @param modelTModel
         * @throws DispositionReportFaultMessage
         */
        public static void mapTModelDescriptions(List<org.uddi.api_v3.Description> apiDescList,
                List<org.apache.juddi.model.TmodelDescr> modelDescList,
                org.apache.juddi.model.Tmodel modelTModel)
                throws DispositionReportFaultMessage {
                modelDescList.clear();

                for (org.uddi.api_v3.Description apiDesc : apiDescList) {
                        modelDescList.add(new org.apache.juddi.model.TmodelDescr(modelTModel, apiDesc.getLang(), apiDesc.getValue()));
                }
        }

        /**
         *
         * @param apiIdentifierBag
         * @param modelIdentifierList
         * @param modelTModel
         * @throws DispositionReportFaultMessage
         */
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

        /**
         *
         * @param apiOverviewDocList
         * @param modelOverviewDocList
         * @param modelTmodel
         * @throws DispositionReportFaultMessage
         */
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

        /**
         * note that when using this, it does not reference the instance of the
         * specific businesses, it will create new ones. be sure to overwrite
         * them with the correct references
         *
         * @param apiPubAssertion
         * @param modelPubAssertion
         * @throws DispositionReportFaultMessage
         */
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
                if (!apiPubAssertion.getSignature().isEmpty()) {
                        modelPubAssertion.setSignatures(mapApiSignaturesToModelSignatures(apiPubAssertion.getSignature()));
                        for (Signature s : modelPubAssertion.getSignatures()) {
                                s.setPublisherAssertionFromKey(modelPubAssertion.getBusinessEntityByFromKey().getEntityKey());
                                s.setPublisherAssertionToKey(modelPubAssertion.getBusinessEntityByToKey().getEntityKey());

                        }

                }
        }

        /**
         *
         * @param apiSubscription
         * @param modelSubscription
         * @throws DispositionReportFaultMessage
         */
        public static void mapSubscription(org.uddi.sub_v3.Subscription apiSubscription,
                org.apache.juddi.model.Subscription modelSubscription)
                throws DispositionReportFaultMessage {

                modelSubscription.setSubscriptionKey(apiSubscription.getSubscriptionKey());
                modelSubscription.setBindingKey(apiSubscription.getBindingKey());
                if (apiSubscription.getNotificationInterval() != null) {
                        modelSubscription.setNotificationInterval(apiSubscription.getNotificationInterval().toString());
                }
                modelSubscription.setMaxEntities(apiSubscription.getMaxEntities());
                if (apiSubscription.getExpiresAfter() != null) {
                        GregorianCalendar gc = apiSubscription.getExpiresAfter().toGregorianCalendar();
                        modelSubscription.setExpiresAfter(new Date(gc.getTimeInMillis()));
                }

                if (apiSubscription.isBrief() != null) {
                        modelSubscription.setBrief(apiSubscription.isBrief());
                } else {
                        modelSubscription.setBrief(Boolean.FALSE);
                }

                String rawFilter = JAXBMarshaller.marshallToString(new ObjectFactory().createSubscriptionFilter(apiSubscription.getSubscriptionFilter()), "org.uddi.sub_v3");
                logger.debug("marshalled subscription filter:  " + rawFilter);
                modelSubscription.setSubscriptionFilter(rawFilter);

        }

        /**
         *
         * @param apiClientSubscriptionInfo
         * @param modelClientSubscriptionInfo
         * @throws DispositionReportFaultMessage
         */
        public static void mapClientSubscriptionInfo(org.apache.juddi.api_v3.ClientSubscriptionInfo apiClientSubscriptionInfo,
                org.apache.juddi.model.ClientSubscriptionInfo modelClientSubscriptionInfo)
                throws DispositionReportFaultMessage {

                modelClientSubscriptionInfo.setLastNotified(new Date());
                modelClientSubscriptionInfo.setSubscriptionKey(apiClientSubscriptionInfo.getSubscriptionKey());
                if (apiClientSubscriptionInfo.getFromClerk() != null) {
                        org.apache.juddi.model.Clerk modelClerk = new org.apache.juddi.model.Clerk();
                        mapClerk(apiClientSubscriptionInfo.getFromClerk(), modelClerk);
                        modelClientSubscriptionInfo.setFromClerk(modelClerk);
                }
                if (apiClientSubscriptionInfo.getToClerk() != null) {
                        org.apache.juddi.model.Clerk modelToClerk = new org.apache.juddi.model.Clerk();
                        mapClerk(apiClientSubscriptionInfo.getToClerk(), modelToClerk);
                        modelClientSubscriptionInfo.setToClerk(modelToClerk);
                }

        }

        /**
         *
         * @param apiClerk
         * @param modelClerk
         */
        public static void mapClerk(org.apache.juddi.api_v3.Clerk apiClerk, org.apache.juddi.model.Clerk modelClerk) {
                if (apiClerk != null) {

                        modelClerk.setClerkName(apiClerk.getName());
                        modelClerk.setCred(apiClerk.getPassword());
                        modelClerk.setPublisherId(apiClerk.getPublisher());
                        if (apiClerk.getNode() != null) {
                                org.apache.juddi.model.Node modelNode = new org.apache.juddi.model.Node();
                                mapNode(apiClerk.getNode(), modelNode);
                                modelClerk.setNode(modelNode.getName());
                        }
                }
        }

        /**
         *
         * @param apiNode
         * @param modelNode
         */
        public static void mapNode(org.apache.juddi.api_v3.Node apiNode, org.apache.juddi.model.Node modelNode) {
                if (apiNode != null) {

                        modelNode.setCustodyTransferUrl(apiNode.getCustodyTransferUrl());
                        modelNode.setInquiryUrl(apiNode.getInquiryUrl());
                        modelNode.setJuddiApiUrl(apiNode.getJuddiApiUrl());
                        modelNode.setName(apiNode.getName());
                        modelNode.setClientName(apiNode.getClientName());
                        modelNode.setProxyTransport(apiNode.getProxyTransport());
                        modelNode.setPublishUrl(apiNode.getPublishUrl());
                        modelNode.setSecurityUrl(apiNode.getSecurityUrl());
                        modelNode.setSubscriptionUrl(apiNode.getSubscriptionUrl());
                        modelNode.setSubscriptionListenerUrl(apiNode.getSubscriptionListenerUrl());
                        modelNode.setReplicationUrl(apiNode.getReplicationUrl());
                        modelNode.setFactoryInitial(apiNode.getFactoryInitial());
                        modelNode.setFactoryNamingProvider(apiNode.getFactoryNamingProvider());
                        modelNode.setFactoryURLPkgs(apiNode.getFactoryURLPkgs());
                }
        }

        private static Reference mapReference(SignedInfo modelSignedInfo, ReferenceType apiReference) {
                Reference ref = new Reference();
                ref.setSignedInfo(modelSignedInfo);
                String refUri = apiReference.getURI();
                if (refUri == null) {
                        refUri = "";
                }
                ref.setUri(refUri);
                List<org.w3._2000._09.xmldsig_.TransformType> apiTransformList = apiReference.getTransforms().getTransform();
                for (org.w3._2000._09.xmldsig_.TransformType apiTransform : apiTransformList) {
                        SignatureTransform modelTransform = new SignatureTransform();
                        modelTransform.setReference(ref);
                        modelTransform.setTransform(apiTransform.getAlgorithm());

                        for (Object xform : apiTransform.getContent()) {
                                SignatureTransformDataValue sdv = mapSignatureTransformDataValue(xform);
                                sdv.setSignatureTransform(modelTransform);
                                modelTransform.getSignatureTransformDataValue().add(sdv);
                        }

                        ref.getTransforms().add(modelTransform);
                }
                String digestMethodStr = apiReference.getDigestMethod().getAlgorithm();
                byte[] digestValueBytes = apiReference.getDigestValue();
                ref.setDigestMethod(digestMethodStr);
                ref.setDigestValue(digestValueBytes);
                return ref;
        }

        private static KeyDataValue mapX509DataType(JAXBElement apiKeyInfoContentJAXB, KeyInfo modelKeyInfo) throws RuntimeException {
                X509DataType apiKeyInfoContent = (X509DataType) apiKeyInfoContentJAXB.getValue();
                KeyDataValue modelX509KeyData = new KeyDataValue();
                modelX509KeyData.setKeyDataType(X509DataType.class.getSimpleName());
                modelX509KeyData.setKeyDataName(apiKeyInfoContentJAXB.getName().getLocalPart());
                modelX509KeyData.setKeyInfo(modelKeyInfo);
                List<Object> x509IssuerSerialOrX509SKIOrX509SubjectNameList = apiKeyInfoContent.getX509IssuerSerialOrX509SKIOrX509SubjectName();
                for (Object x509IssuerSerialOrX509SKIOrX509SubjectNameObj : x509IssuerSerialOrX509SKIOrX509SubjectNameList) {
                        JAXBElement x509IssuerSerialOrX509SKIOrX509SubjectNameJAXB = (JAXBElement) x509IssuerSerialOrX509SKIOrX509SubjectNameObj;
                        String tagName = x509IssuerSerialOrX509SKIOrX509SubjectNameJAXB.getName().getLocalPart();
                        Object x509IssuerSerialOrX509SKIOrX509SubjectName = x509IssuerSerialOrX509SKIOrX509SubjectNameJAXB.getValue();

                        KeyDataValue modelKeyInfoValue = new KeyDataValue();
                        modelKeyInfoValue.setKeyDataName(tagName);
                        if (x509IssuerSerialOrX509SKIOrX509SubjectName instanceof byte[]) {
                                modelKeyInfoValue.setKeyDataValueBytes((byte[]) x509IssuerSerialOrX509SKIOrX509SubjectName);
                        } else if (x509IssuerSerialOrX509SKIOrX509SubjectName instanceof String) {
                                modelKeyInfoValue.setKeyDataValueString((String) x509IssuerSerialOrX509SKIOrX509SubjectName);
                        } else if (x509IssuerSerialOrX509SKIOrX509SubjectName instanceof X509IssuerSerialType) {
                                modelKeyInfoValue.setKeyDataType(X509IssuerSerialType.class.getSimpleName());
                                X509IssuerSerialType x509IssuerSerialType = (X509IssuerSerialType) x509IssuerSerialOrX509SKIOrX509SubjectName;
                                modelKeyInfoValue.setKeyDataValueString(x509IssuerSerialType.getX509IssuerName());
                                modelKeyInfoValue.setKeyDataValueBytes(x509IssuerSerialType.getX509SerialNumber().toByteArray());
                        } else if (x509IssuerSerialOrX509SKIOrX509SubjectName != null) {
                                throw new RuntimeException("Unrecognized Value for Element: " + tagName + ": " + x509IssuerSerialOrX509SKIOrX509SubjectName.getClass().getCanonicalName());
                        }
                        modelKeyInfoValue.setKeyDataValue(modelX509KeyData);
                        modelX509KeyData.getKeyDataValueList().add(modelKeyInfoValue);
                }
                return modelX509KeyData;
        }

        private static KeyDataValue mapKeyName(JAXBElement apiKeyInfoContentJAXB) {
                KeyDataValue modelKeyNameKDV = new KeyDataValue();
                modelKeyNameKDV.setKeyDataType(String.class.getSimpleName());
                modelKeyNameKDV.setKeyDataName(apiKeyInfoContentJAXB.getName().getLocalPart());
                modelKeyNameKDV.setKeyDataValueString((String) apiKeyInfoContentJAXB.getValue());
                return modelKeyNameKDV;
        }

        private static KeyDataValue mapKeyValue(JAXBElement apiKeyInfoContentJAXB, List<KeyDataValue> keyInfoDataValues) {
                KeyValueType kvt = (KeyValueType) apiKeyInfoContentJAXB.getValue();
                KeyDataValue modelKeyValueKDV = new KeyDataValue();
                modelKeyValueKDV.setKeyDataType(KeyValueType.class.getSimpleName());
                modelKeyValueKDV.setKeyDataName(apiKeyInfoContentJAXB.getName().getLocalPart());
                keyInfoDataValues.add(modelKeyValueKDV);
                List<Object> kvObjList = kvt.getContent();
                for (Object kvObj : kvObjList) {
                        if (kvObj instanceof JAXBElement) {
                                JAXBElement kvJAXB = (JAXBElement) kvObj;
                                Object childVal = kvJAXB.getValue();

                                KeyDataValue childKDV = new KeyDataValue();
                                childKDV.setKeyDataValue(modelKeyValueKDV);
                                childKDV.setKeyDataName(kvJAXB.getName().getLocalPart());
                                childKDV.setKeyDataType(childVal.getClass().getSimpleName());
                                modelKeyValueKDV.getKeyDataValueList().add(childKDV);

                                if (childVal instanceof DSAKeyValueType) {
                                        DSAKeyValueType dsaKeyVal = (DSAKeyValueType) childVal;
                                        String dsaKeyValueTagName = kvJAXB.getName().getLocalPart();
                                        KeyDataValue dsaKeyValKDV = new KeyDataValue(null, DSAKeyValueType.class.getSimpleName(), dsaKeyValueTagName, null, null, childKDV);
                                        childKDV.getKeyDataValueList().add(dsaKeyValKDV);

                                        KeyDataValue gValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "G", dsaKeyVal.getG(), null, dsaKeyValKDV);
                                        dsaKeyValKDV.getKeyDataValueList().add(gValKDV);

                                        KeyDataValue jValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "J", dsaKeyVal.getJ(), null, dsaKeyValKDV);
                                        dsaKeyValKDV.getKeyDataValueList().add(jValKDV);

                                        KeyDataValue pValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "P", dsaKeyVal.getP(), null, dsaKeyValKDV);
                                        dsaKeyValKDV.getKeyDataValueList().add(pValKDV);

                                        KeyDataValue pGenCounterValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "PgenCounter", dsaKeyVal.getPgenCounter(), null, dsaKeyValKDV);
                                        dsaKeyValKDV.getKeyDataValueList().add(pGenCounterValKDV);

                                        KeyDataValue qValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "Q", dsaKeyVal.getQ(), null, dsaKeyValKDV);
                                        dsaKeyValKDV.getKeyDataValueList().add(qValKDV);

                                        KeyDataValue seedValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "Seed", dsaKeyVal.getSeed(), null, dsaKeyValKDV);
                                        dsaKeyValKDV.getKeyDataValueList().add(seedValKDV);

                                        KeyDataValue yValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "Y", dsaKeyVal.getY(), null, dsaKeyValKDV);
                                        dsaKeyValKDV.getKeyDataValueList().add(yValKDV);
                                } else if (childVal instanceof RSAKeyValueType) {
                                        RSAKeyValueType rsaKeyVal = (RSAKeyValueType) childVal;
                                        String rsaKeyValueTagName = kvJAXB.getName().getLocalPart();
                                        KeyDataValue rsaKeyValKDV = new KeyDataValue(null, RSAKeyValueType.class.getSimpleName(), rsaKeyValueTagName, null, null, childKDV);
                                        childKDV.getKeyDataValueList().add(rsaKeyValKDV);

                                        KeyDataValue exponentValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "Exponent", rsaKeyVal.getExponent(), null, rsaKeyValKDV);
                                        rsaKeyValKDV.getKeyDataValueList().add(exponentValKDV);

                                        KeyDataValue modulusValKDV = new KeyDataValue(null, byte[].class.getSimpleName(), "Modulus", rsaKeyVal.getModulus(), null, rsaKeyValKDV);
                                        rsaKeyValKDV.getKeyDataValueList().add(modulusValKDV);
                                }
                        }
                }
                return modelKeyValueKDV;
        }

        private static KeyDataValue mapRetrievalMethod(String apiKeyInfoContentTagName, KeyInfo modelKeyInfo, RetrievalMethodType retrievalMethodType) {
                KeyDataValue retrievalMethodTypeKDV = new KeyDataValue();
                retrievalMethodTypeKDV.setKeyDataName(apiKeyInfoContentTagName);
                retrievalMethodTypeKDV.setKeyDataType(RetrievalMethodType.class.getSimpleName());
                retrievalMethodTypeKDV.setKeyInfo(modelKeyInfo);
                KeyDataValue uriKDV = new KeyDataValue();
                uriKDV.setKeyDataName("URI");
                uriKDV.setKeyDataType(String.class.getSimpleName());
                uriKDV.setKeyDataValue(retrievalMethodTypeKDV);
                uriKDV.setKeyDataValueString(retrievalMethodType.getURI());
                retrievalMethodTypeKDV.getKeyDataValueList().add(uriKDV);
                KeyDataValue typeKDV = new KeyDataValue();
                typeKDV.setKeyDataName("Type");
                typeKDV.setKeyDataType(String.class.getSimpleName());
                typeKDV.setKeyDataValue(retrievalMethodTypeKDV);
                typeKDV.setKeyDataValueString(retrievalMethodType.getType());
                retrievalMethodTypeKDV.getKeyDataValueList().add(typeKDV);
                TransformsType transformsType = retrievalMethodType.getTransforms();
                if (transformsType != null) {
                        List<TransformType> tTypeList = transformsType.getTransform();
                        for (TransformType tType : tTypeList) {
                                KeyDataValue transformKDV = new KeyDataValue();
                                transformKDV.setKeyDataName("Transform");
                                transformKDV.setKeyDataType(String.class.getSimpleName());
                                transformKDV.setKeyDataValue(retrievalMethodTypeKDV);
                                transformKDV.setKeyDataValueString(tType.getAlgorithm());

                                for (Object xform : tType.getContent()) {
                                        SignatureTransformDataValue stdv = mapSignatureTransformDataValue(xform);
                                        KeyDataValue transformContentKDV = new KeyDataValue();
                                        transformContentKDV.setKeyDataType(stdv.getContentType());
                                        transformContentKDV.setKeyDataValueBytes(stdv.getContentBytes());
                                        transformContentKDV.setKeyDataValue(transformKDV);
                                        transformKDV.getKeyDataValueList().add(transformContentKDV);
                                }

                                retrievalMethodTypeKDV.getKeyDataValueList().add(transformKDV);
                        }
                }
                return retrievalMethodTypeKDV;
        }

        private static KeyDataValue mapPGPDataType(String apiKeyInfoContentTagName, KeyInfo modelKeyInfo, PGPDataType pgpDataType) {
                KeyDataValue pgpDataTypeKDV = new KeyDataValue();
                pgpDataTypeKDV.setKeyDataName(apiKeyInfoContentTagName);
                pgpDataTypeKDV.setKeyDataType(PGPDataType.class.getSimpleName());
                pgpDataTypeKDV.setKeyInfo(modelKeyInfo);

                List<Object> pgpDataValues = pgpDataType.getContent();
                for (Object pgpDataValue : pgpDataValues) {
                        if (pgpDataValue instanceof JAXBElement) {
                                JAXBElement pgpDataJAXB = (JAXBElement) pgpDataValue;
                                String tagName = pgpDataJAXB.getName().getLocalPart();

                                KeyDataValue keyIDKDV = new KeyDataValue();
                                keyIDKDV.setKeyDataName(tagName);
                                keyIDKDV.setKeyDataValue(pgpDataTypeKDV);
                                if (pgpDataJAXB.getValue() instanceof String) {
                                        keyIDKDV.setKeyDataValueString((String) pgpDataJAXB.getValue());
                                } else {
                                        keyIDKDV.setKeyDataValueBytes((byte[]) pgpDataJAXB.getValue());
                                }
                                pgpDataTypeKDV.getKeyDataValueList().add(keyIDKDV);
                        }
                }
                return pgpDataTypeKDV;
        }

        private static KeyDataValue mapSPKIDataType(String apiKeyInfoContentTagName, KeyInfo modelKeyInfo, SPKIDataType spkiDataType) {
                KeyDataValue spkiDataTypeKDV = new KeyDataValue();
                spkiDataTypeKDV.setKeyDataName(apiKeyInfoContentTagName);
                spkiDataTypeKDV.setKeyDataType(SPKIDataType.class.getSimpleName());
                spkiDataTypeKDV.setKeyInfo(modelKeyInfo);

                List<Object> spkiDataValues = spkiDataType.getSPKISexpAndAny();
                for (Object spkiDataValue : spkiDataValues) {
                        if (spkiDataValue instanceof JAXBElement) {
                                JAXBElement spkiDataJAXB = (JAXBElement) spkiDataValue;
                                String tagName = spkiDataJAXB.getName().getLocalPart();

                                KeyDataValue keyIDKDV = new KeyDataValue();
                                keyIDKDV.setKeyDataName(tagName);
                                keyIDKDV.setKeyDataValue(spkiDataTypeKDV);
                                if (spkiDataJAXB.getValue() instanceof String) {
                                        keyIDKDV.setKeyDataValueString((String) spkiDataJAXB.getValue());
                                } else {
                                        keyIDKDV.setKeyDataValueBytes((byte[]) spkiDataJAXB.getValue());
                                }
                                spkiDataTypeKDV.getKeyDataValueList().add(keyIDKDV);
                        } else {
                                throw new RuntimeException("Unrecognized type: " + spkiDataValue.getClass().getCanonicalName());
                        }
                }
                return spkiDataTypeKDV;
        }

        private static SignatureTransformDataValue mapSignatureTransformDataValue(Object xform) {
                SignatureTransformDataValue sdv = new SignatureTransformDataValue();
                if (xform instanceof String) {
                        sdv.setContentType(String.class.getSimpleName());
                        String xformStr = xform.toString();
                        try {
                                byte[] xformBytes = xformStr.getBytes(AuthenticatedService.UTF8);
                                sdv.setContentBytes(xformBytes);
                        } catch (Exception e) {
                                throw new RuntimeException("Failed to encode string due to: " + e.getMessage(), e);
                        }
                } else if (xform instanceof Element) {
                        sdv.setContentType(Element.class.getCanonicalName());
                        Element xformEl = (Element) xform;
                        String str = serializeTransformElement(xformEl);
                        try {
                                sdv.setContentBytes(str.getBytes(AuthenticatedService.UTF8));
                        } catch (Exception e) {
                                throw new RuntimeException("Failed to encode string due to: " + e.getMessage(), e);
                        }
                } else if (xform instanceof byte[]) {
                        sdv.setContentType(byte[].class.getSimpleName());
                        sdv.setContentBytes((byte[]) xform);
                } else if (xform instanceof JAXBElement) {
                        sdv.setContentType(Element.class.getCanonicalName());
                        JAXBElement xformJAXB = (JAXBElement) xform;
                        DOMResult domResult = new DOMResult();
                        JAXB.marshal(xformJAXB, domResult);
                        Element xformEl = ((Document) domResult.getNode()).getDocumentElement();
                        String str = serializeTransformElement(xformEl);
                        try {
                                sdv.setContentBytes(str.getBytes(AuthenticatedService.UTF8));
                        } catch (Exception e) {
                                throw new RuntimeException("Failed to encode string due to: " + e.getMessage(), e);
                        }
                } else {
                        throw new RuntimeException("Unrecognized type: " + xform.getClass().getCanonicalName());
                }
                return sdv;
        }

        private static String serializeTransformElement(Element xformEl) throws DOMException, LSException {
                Document document = xformEl.getOwnerDocument();
                DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
                LSSerializer serializer = domImplLS.createLSSerializer();
//        serializer.getDomConfig().setParameter("namespaces", true);
//        serializer.getDomConfig().setParameter("namespace-declarations", true);
                serializer.getDomConfig().setParameter("canonical-form", false);
                serializer.getDomConfig().setParameter("xml-declaration", false);
                String str = serializer.writeToString(xformEl);
                return str;
        }

        public static org.apache.juddi.model.ChangeRecord mapChangeRecord(ChangeRecord rec) throws UnsupportedEncodingException {
                org.apache.juddi.model.ChangeRecord r = new org.apache.juddi.model.ChangeRecord();
                //r.setId(rec.getChangeID().getOriginatingUSN());
                r.setOriginatingUSN(rec.getChangeID().getOriginatingUSN());
                if (r.getOriginatingUSN() == null) {
                        //       logger.warn("strange, the getOriginatingUSN is null!!");
                        //       JAXB.marshal(rec, System.out);
                        //       Thread.dumpStack();
                }
                r.setNodeID(rec.getChangeID().getNodeID());
                if (rec.getChangeRecordNewData() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordNewData);
                        r.setEntityKey(rec.getChangeRecordNewData().getOperationalInfo().getEntityKey());
                        if (rec.getChangeRecordNewData().getOperationalInfo().getEntityKey() == null) {
                                throw new UnsupportedEncodingException("entity key is null! make sure it's filled out before saving!");
                        }
                } else if (rec.getChangeRecordAcknowledgement() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordAcknowledgement);
                } else if (rec.getChangeRecordConditionFailed() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordConditionFailed);
                } else if (rec.getChangeRecordCorrection() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordCorrection);
                } else if (rec.getChangeRecordDelete() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordDelete);
                        if (rec.getChangeRecordDelete().getTModelKey() != null) {
                                r.setEntityKey(rec.getChangeRecordDelete().getTModelKey());
                        }
                        if (rec.getChangeRecordDelete().getBindingKey() != null) {
                                r.setEntityKey(rec.getChangeRecordDelete().getBindingKey());
                        }
                        if (rec.getChangeRecordDelete().getBusinessKey() != null) {
                                r.setEntityKey(rec.getChangeRecordDelete().getBusinessKey());
                        }
                        if (rec.getChangeRecordDelete().getServiceKey() != null) {
                                r.setEntityKey(rec.getChangeRecordDelete().getServiceKey());
                        }
                } else if (rec.getChangeRecordDeleteAssertion() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordDeleteAssertion);
                } else if (rec.getChangeRecordHide() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordHide);
                        r.setEntityKey(rec.getChangeRecordHide().getTModelKey());
                } else if (rec.getChangeRecordNewDataConditional() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordNewDataConditional);
                } else if (rec.getChangeRecordNull() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordNull);
                } else if (rec.getChangeRecordPublisherAssertion() != null) {
                        r.setRecordType(org.apache.juddi.model.ChangeRecord.RecordType.ChangeRecordPublisherAssertion);
                } else {
                        throw new UnsupportedEncodingException("unknown type!");
                }

                StringWriter sw = new StringWriter();
                JAXB.marshal(rec, sw);
                r.setContents(sw.toString().getBytes(AuthenticatedService.UTF8));
                return r;

        }

        public static void mapOperationalInfo(UddiEntity model, OperationalInfo operationalInfo) {
                if (operationalInfo == null || model == null) {
                        return;
                }
                if (operationalInfo.getCreated() != null) {
                        model.setCreated(operationalInfo.getCreated().toGregorianCalendar().getTime());
                }
                model.setAuthorizedName(operationalInfo.getAuthorizedName());
                if (operationalInfo.getModified() != null) {
                        model.setModified(operationalInfo.getModified().toGregorianCalendar().getTime());
                }
                if (operationalInfo.getModifiedIncludingChildren() != null) {
                        model.setModifiedIncludingChildren(operationalInfo.getModifiedIncludingChildren().toGregorianCalendar().getTime());
                }
                model.setNodeId(operationalInfo.getNodeID());

        }

        public static void mapOperationalInfoIncludingChildren(BusinessEntity model, OperationalInfo operationalInfo) {
                if (operationalInfo == null || model == null) {
                        return;
                }
                if (operationalInfo.getCreated() != null) {
                        model.setCreated(operationalInfo.getCreated().toGregorianCalendar().getTime());
                }
                model.setAuthorizedName(operationalInfo.getAuthorizedName());
                if (operationalInfo.getModified() != null) {
                        model.setModified(operationalInfo.getModified().toGregorianCalendar().getTime());
                }
                if (operationalInfo.getModifiedIncludingChildren() != null) {
                        model.setModifiedIncludingChildren(operationalInfo.getModifiedIncludingChildren().toGregorianCalendar().getTime());
                }
                model.setNodeId(operationalInfo.getNodeID());
                for (int i = 0; i < model.getBusinessServices().size(); i++) {
                        mapOperationalInfo(model.getBusinessServices().get(i), operationalInfo);
                        for (int k = 0; k < model.getBusinessServices().get(i).getBindingTemplates().size(); k++) {
                                mapOperationalInfo(model.getBusinessServices().get(i).getBindingTemplates().get(k), operationalInfo);
                        }
                }

        }

        public static void mapReplicationConfiguration(ReplicationConfiguration replicationConfiguration, org.apache.juddi.model.ReplicationConfiguration model, EntityManager em) throws DispositionReportFaultMessage {
                if (replicationConfiguration == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.replication.configNull"));
                }
                model.setMaximumTimeToGetChanges(replicationConfiguration.getMaximumTimeToGetChanges());
                model.setMaximumTimeToSyncRegistry(replicationConfiguration.getMaximumTimeToSyncRegistry());
                //this is set by the service when saving
                model.setSerialNumber(null);

                //the spec doesn't specify what the format should be, however there was an example
                //2002 03 04 1859Z
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmZ");
                model.setTimeOfConfigurationUpdate(sdf.format(new Date()));
                if (replicationConfiguration.getRegistryContact() != null) {
                        model.setContact(mapContact(replicationConfiguration.getRegistryContact().getContact(), model));
                }
                mapCommunicationGraph(model, replicationConfiguration.getCommunicationGraph(), em);
                model.setOperator(mapOperators(replicationConfiguration, model));
                if (replicationConfiguration.getSignature() != null) {
                        model.setSignatures(mapApiSignaturesToModelSignatures(replicationConfiguration.getSignature()));
                }

        }

        private static Contact mapContact(org.uddi.api_v3.Contact contact, org.apache.juddi.model.ReplicationConfiguration model) throws DispositionReportFaultMessage {
                Contact cmodel = new Contact();
                cmodel.setReplicationConfigId(model);
                cmodel.setAddresses(new ArrayList<Address>());
                mapContactAddresses(contact.getAddress(), cmodel.getAddresses(), cmodel);
                if (cmodel.getAddresses().isEmpty()) {
                        cmodel.setAddresses(null);
                }
                return cmodel;
        }

        private static List<Operator> mapOperators(ReplicationConfiguration cfg, org.apache.juddi.model.ReplicationConfiguration modelparent) throws DispositionReportFaultMessage {
                if (cfg == null) {
                        return null;
                }
                List<org.uddi.repl_v3.Operator> api = cfg.getOperator();
                if (api == null) {
                        return null;
                }
                List<Operator> model = new ArrayList<Operator>();
                for (int i = 0; i < api.size(); i++) {
                        Operator op = new Operator();
                        op.setParent(modelparent);

                        op.setSoapReplicationURL(api.get(i).getSoapReplicationURL());

                        if (!api.get(i).getContact().isEmpty()) {
                                op.setContact(new ArrayList<Contact>());
                                for (int k = 0; k < api.get(i).getContact().size(); k++) {
                                        op.getContact().add(mapContact(api.get(i).getContact().get(k), null));
                                }
                        }
                        op.setOperatorNodeID(api.get(i).getOperatorNodeID());
                        op.setOperatorStatus(mapOperatorStatus(api.get(i).getOperatorStatus()));
                        if (!api.get(i).getKeyInfo().isEmpty()) {
                                op.setKeyInfo(new ArrayList<KeyInfo>());
                                for (int k = 0; k < api.get(i).getKeyInfo().size(); k++) {
                                        org.apache.juddi.model.KeyInfo modelKeyInfo = new KeyInfo();
                                        modelKeyInfo.setXmlID(api.get(i).getKeyInfo().get(i).getId());
                                        modelKeyInfo.setKeyDataValue(new ArrayList<KeyDataValue>());
                                        if (api.get(i).getKeyInfo().get(i).getContent() != null) {
                                                modelKeyInfo.setKeyDataValue(new ArrayList<KeyDataValue>());
                                                for (int x = 0; x < api.get(i).getKeyInfo().get(k).getContent().size(); x++) {
                                                        mapKeyValue((JAXBElement) api.get(i).getKeyInfo().get(k).getContent().get(x), modelKeyInfo.getKeyDataValue());
                                                }
                                        }
                                        op.getKeyInfo().add(modelKeyInfo);
                                }

                        }
                        model.add(op);

                }
                return model;
        }

        private static OperatorStatusType mapOperatorStatus(org.uddi.repl_v3.OperatorStatusType operatorStatus) {
                switch (operatorStatus) {
                        case NEW:
                                return OperatorStatusType.NEW;
                        case NORMAL:
                                return OperatorStatusType.NORMAL;
                        case RESIGNED:
                                return OperatorStatusType.RESIGNED;
                }
                return null;
        }

        private static void mapCommunicationGraph(org.apache.juddi.model.ReplicationConfiguration model, CommunicationGraph communicationGraph, EntityManager em) throws ValueNotAllowedException {

                if (model == null) {
                        return;
                }

                /**
                 * Following the listing of nodes is the controlledMessage
                 * element that lists the set of messages over which this
                 * communication graph is intended to administer control of. If
                 * a message element local name is listed in the
                 * controlledMessage element, then such messages SHALL only be
                 * sent between nodes that are listed in the subsequent edges of
                 * the graph. In contrast, communication restrictions are not
                 * imposed on replication messages not identified in the
                 * controlledMessage element.
                 */
                if (communicationGraph.getControlledMessage() != null) {
                        model.setControlMessage(new ArrayList<ControlMessage>());

                        for (int k = 0; k < communicationGraph.getControlledMessage().size(); k++) {
                                ControlMessage BC = new ControlMessage(communicationGraph.getControlledMessage().get(k));
                                BC.setReplicationConfiguration(model);

                                model.getControlMessage().add(BC);
                        }
                }

                for (int i = 0; i < communicationGraph.getNode().size(); i++) {
                        ReplicationConfigurationNode replicationConfigurationNode = new ReplicationConfigurationNode();

                        replicationConfigurationNode.setNodeName(communicationGraph.getNode().get(i));
                        replicationConfigurationNode.setParent(model);
                        model.getNode().add(replicationConfigurationNode);
                }
                if (communicationGraph.getEdge() != null && !communicationGraph.getEdge().isEmpty()) {
                        List<Edge> ret = new ArrayList<Edge>();
                        for (int i = 0; i < communicationGraph.getEdge().size(); i++) {
                                Edge e = new Edge();
                                e.setReplicationConfiguration(model);

                                e.setMessageReceiver(communicationGraph.getEdge().get(i).getMessageReceiver());
                                e.setMessageSender(communicationGraph.getEdge().get(i).getMessageSender());

                                /**
                                 * The message elements contain the local name
                                 * of the Replication API message elements. They
                                 * indicate that only messages of the type
                                 * explicitly identified for a particular edge
                                 * MAY be sent from the specified messageSender
                                 * to the specified messageReceiver.
                                 */
                                if (communicationGraph.getEdge().get(i).getMessage() != null) {
                                        e.setMessage(new ArrayList<ControlMessage>());
                                        for (int k = 0; k < communicationGraph.getEdge().get(i).getMessage().size(); k++) {
                                                ControlMessage controlMessage = new ControlMessage(communicationGraph.getEdge().get(i).getMessage().get(k));
                                                controlMessage.setReplicationConfiguration(model);
                                                controlMessage.setParentEdge(e);
                                                e.getMessages().add(controlMessage);
                                        }
                                }
                                if (communicationGraph.getEdge().get(i).getMessageReceiverAlternate() != null) {
                                        List<EdgeReceiverAlternate> eras = new ArrayList<EdgeReceiverAlternate>();
                                        for (String s : communicationGraph.getEdge().get(i).getMessageReceiverAlternate()) {
                                                EdgeReceiverAlternate x = new EdgeReceiverAlternate();
                                                x.setParent(e);
                                                x.setReceiverAlternate(s);
                                                eras.add(x);
                                        }
                                        e.setMessageReceiverAlternate(eras);

                                }

                                ret.add(e);
                        }
                        model.setEdge(ret);
                }
        }

        public static void mapOperationalInfoIncludingChildren(BusinessService model, OperationalInfo operationalInfo) {
                if (operationalInfo == null || model == null) {
                        return;
                }
                if (operationalInfo.getCreated() != null) {
                        model.setCreated(operationalInfo.getCreated().toGregorianCalendar().getTime());
                }
                model.setAuthorizedName(operationalInfo.getAuthorizedName());
                if (operationalInfo.getModified() != null) {
                        model.setModified(operationalInfo.getModified().toGregorianCalendar().getTime());
                }
                if (operationalInfo.getModifiedIncludingChildren() != null) {
                        model.setModifiedIncludingChildren(operationalInfo.getModifiedIncludingChildren().toGregorianCalendar().getTime());
                }
                model.setNodeId(operationalInfo.getNodeID());

                for (int k = 0; k < model.getBindingTemplates().size(); k++) {
                        mapOperationalInfo(model.getBindingTemplates().get(k), operationalInfo);
                }

        }

}
