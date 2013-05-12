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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.model.CanonicalizationMethod;
import org.apache.juddi.model.KeyInfo;
import org.apache.juddi.model.OverviewDoc;
import org.apache.juddi.model.Reference;
import org.apache.juddi.model.SignatureTransform;
import org.apache.juddi.model.SignatureValue;
import org.apache.juddi.model.SignedInfo;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.model.KeyDataValue;
import org.apache.juddi.model.SignatureTransformDataValue;
import org.apache.juddi.subscription.TypeConvertor;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.w3._2000._09.xmldsig_.CanonicalizationMethodType;
import org.w3._2000._09.xmldsig_.DSAKeyValueType;
import org.w3._2000._09.xmldsig_.DigestMethodType;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.KeyValueType;
import org.w3._2000._09.xmldsig_.PGPDataType;
import org.w3._2000._09.xmldsig_.RSAKeyValueType;
import org.w3._2000._09.xmldsig_.ReferenceType;
import org.w3._2000._09.xmldsig_.RetrievalMethodType;
import org.w3._2000._09.xmldsig_.SPKIDataType;
import org.w3._2000._09.xmldsig_.SignatureMethodType;
import org.w3._2000._09.xmldsig_.SignatureType;
import org.w3._2000._09.xmldsig_.SignatureValueType;
import org.w3._2000._09.xmldsig_.SignedInfoType;
import org.w3._2000._09.xmldsig_.TransformType;
import org.w3._2000._09.xmldsig_.TransformsType;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class MappingModelToApi {
	private static Log logger = LogFactory.getLog(MappingModelToApi.class);

	
	public static void mapPublisher(org.apache.juddi.model.Publisher modelPublisher, 
									org.apache.juddi.api_v3.Publisher apiPublisher) 
				   throws DispositionReportFaultMessage {

		apiPublisher.setAuthorizedName(modelPublisher.getAuthorizedName());
		apiPublisher.setPublisherName(modelPublisher.getPublisherName());
		apiPublisher.setEmailAddress(modelPublisher.getEmailAddress());
		apiPublisher.setIsAdmin(modelPublisher.getIsAdmin());
		apiPublisher.setIsEnabled(modelPublisher.getIsEnabled());
		apiPublisher.setMaxBindingsPerService(modelPublisher.getMaxBindingsPerService());
		apiPublisher.setMaxBusinesses(modelPublisher.getMaxBusinesses());
		apiPublisher.setMaxServicePerBusiness(modelPublisher.getMaxServicesPerBusiness());
		apiPublisher.setMaxTModels(modelPublisher.getMaxTmodels());
                mapSignature(modelPublisher.getSignatures(), apiPublisher.getSignature());
	}
	
	public static void mapBusinessEntity(org.apache.juddi.model.BusinessEntity modelBusinessEntity, 
										 org.uddi.api_v3.BusinessEntity apiBusinessEntity) 
				   throws DispositionReportFaultMessage {

		apiBusinessEntity.setBusinessKey(modelBusinessEntity.getEntityKey());
		
		mapBusinessNames(modelBusinessEntity.getBusinessNames(), apiBusinessEntity.getName());
		mapBusinessDescriptions(modelBusinessEntity.getBusinessDescrs(), apiBusinessEntity.getDescription());

		mapDiscoveryUrls(modelBusinessEntity.getDiscoveryUrls(), apiBusinessEntity.getDiscoveryURLs(), apiBusinessEntity);
		mapContacts(modelBusinessEntity.getContacts(), apiBusinessEntity.getContacts(), apiBusinessEntity);
		mapBusinessIdentifiers(modelBusinessEntity.getBusinessIdentifiers(), apiBusinessEntity.getIdentifierBag(), apiBusinessEntity);
		apiBusinessEntity.setCategoryBag(mapCategoryBag(modelBusinessEntity.getCategoryBag(), apiBusinessEntity.getCategoryBag()));
		
		mapBusinessServices(modelBusinessEntity.getBusinessServices(), modelBusinessEntity.getServiceProjections(), apiBusinessEntity.getBusinessServices(), apiBusinessEntity);
	
                mapSignature(modelBusinessEntity.getSignatures(), apiBusinessEntity.getSignature());
	}

        public static void mapSignature(List<org.apache.juddi.model.Signature> signatureList,
										List<org.w3._2000._09.xmldsig_.SignatureType> apiSignatureList) 
				   throws DispositionReportFaultMessage {
            apiSignatureList.clear();
            for (org.apache.juddi.model.Signature modelSig : signatureList) {
                SignatureType apiSignature = new SignatureType();
                apiSignature.setId(modelSig.getXmlID());
                
                SignedInfo modelSignedInfo = modelSig.getSignedInfo();
                SignedInfoType apiSignedInfoType = new SignedInfoType();
                apiSignature.setSignedInfo(apiSignedInfoType);
                // Canonicalization method
                CanonicalizationMethod modelCanonMethod = modelSignedInfo.getCanonicalizationMethod();
                CanonicalizationMethodType apiCanonMethod = new CanonicalizationMethodType();
                apiCanonMethod.setAlgorithm(modelCanonMethod.getAlgorithm());
                apiSignedInfoType.setCanonicalizationMethod(apiCanonMethod);
                
                // Signature Method
                String sigAlg = modelSignedInfo.getSignatureMethod().getAlgorithm();
                SignatureMethodType apiSigMethod = new SignatureMethodType();
                apiSigMethod.setAlgorithm(sigAlg);
                apiSignedInfoType.setSignatureMethod(apiSigMethod);
                
                // References
                List<Reference> modelReferenceList = modelSignedInfo.getReference();
                List<ReferenceType> apiReferenceList = apiSignedInfoType.getReference();
                for (Reference modelRef : modelReferenceList) {
                    ReferenceType apiRef = new ReferenceType();
                    String refUri = modelRef.getUri();
                    if (refUri == null) {
                        refUri = "";
                    }
                    apiRef.setURI(refUri);
                    
                    List<SignatureTransform> modelSigTransformList = modelRef.getTransforms();
                    TransformsType apiTransformsType = apiRef.getTransforms();
                    if (apiTransformsType == null) {
                        apiTransformsType = new TransformsType();
                        apiRef.setTransforms(apiTransformsType);
                    }
                    List<TransformType> apiTransformList = apiTransformsType.getTransform();
                    for (SignatureTransform modelSigTransform : modelSigTransformList) {
                        String modelTransformAlgStr = modelSigTransform.getTransform();
                        TransformType apiTransform = new TransformType();
                        apiTransform.setAlgorithm(modelTransformAlgStr);
                        
                        List<SignatureTransformDataValue> sigTransformSDVList = modelSigTransform.getSignatureTransformDataValue();
                        for (SignatureTransformDataValue sigTransformSDV : sigTransformSDVList) {
                            String type = sigTransformSDV.getContentType();
                            byte[] xformBytes = sigTransformSDV.getContentBytes();
                            Object transformObject = convertDataToTransformContent(type, xformBytes);
                            apiTransform.getContent().add(transformObject);
                        }
                        
                        apiTransformList.add(apiTransform);
                    }
                    
                    String digestMethodStr = modelRef.getDigestMethod();
                    byte[] digestValBytes = modelRef.getDigestValue();
                    
                    DigestMethodType apiDigestMethod = new DigestMethodType();
                    apiDigestMethod.setAlgorithm(digestMethodStr);
                    apiRef.setDigestMethod(apiDigestMethod);
                    apiRef.setDigestValue(digestValBytes);
                    
                    apiReferenceList.add(apiRef);
                }
                
                // Signature Value
                SignatureValueType apiSignatureValue = new SignatureValueType();
                SignatureValue modelSigValue = modelSig.getSignatureValue();
                apiSignatureValue.setId(modelSigValue.getXmlID());
                apiSignatureValue.setValue(modelSigValue.getValue());
                
                apiSignature.setSignatureValue(apiSignatureValue);
                
                KeyInfoType apiKeyInfo = new KeyInfoType();
                KeyInfo modelKeyInfo = modelSig.getKeyInfo();
                apiKeyInfo.setId(modelKeyInfo.getXmlID());
                
                List<KeyDataValue> modelKeyDataValueList = modelKeyInfo.getKeyDataValue();
                List<Object> apiX509KeyInfoList = apiKeyInfo.getContent();
                mapModelKeyDataValue(modelKeyDataValueList, apiX509KeyInfoList);
                apiSignature.setKeyInfo(apiKeyInfo);
                apiSignatureList.add(apiSignature);
            }
        }
        
        private static void mapModelKeyDataValue(List<KeyDataValue> modelKeyDataValueList, List<Object> parentKeyDataList) {
            for (KeyDataValue modelKeyDataValue : modelKeyDataValueList) {
                String tagName = modelKeyDataValue.getKeyDataName();
                String dataType = modelKeyDataValue.getKeyDataType();
                byte[] contentBytes = modelKeyDataValue.getKeyDataValueBytes();
                String contentStr = modelKeyDataValue.getKeyDataValueString();
                Object contents = contentBytes != null ? contentBytes : contentStr;
                
                if (contents == null) {
                    List<Object> childKeyDataList = new ArrayList<Object>();
                    if (dataType.equals(X509DataType.class.getSimpleName())) {
                        X509DataType x509DataType = new X509DataType();
                        mapModelKeyDataValue(modelKeyDataValue.getKeyDataValueList(), childKeyDataList);
                        x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName().addAll(childKeyDataList);
                        JAXBElement dataJAXB = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", tagName), X509DataType.class, x509DataType);
                        parentKeyDataList.add(dataJAXB);   
                    } else if (dataType.equals(RetrievalMethodType.class.getSimpleName())) {
                        RetrievalMethodType retrievalMethodType = new RetrievalMethodType();
                        TransformsType transformsType = new TransformsType();
                        for (KeyDataValue retrievalMethodKDV : modelKeyDataValue.getKeyDataValueList()) {
                            if (retrievalMethodKDV.getKeyDataName().equals("Transform")) {
                                TransformType tType = new TransformType();
                                tType.setAlgorithm(retrievalMethodKDV.getKeyDataValueString());
                                
                                for (KeyDataValue transformContentKDV: retrievalMethodKDV.getKeyDataValueList()) {
                                    String type = transformContentKDV.getKeyDataType();
                                    byte[] xformBytes = transformContentKDV.getKeyDataValueBytes();
                                    Object transformObject = convertDataToTransformContent(type, xformBytes);
                                    tType.getContent().add(transformObject);
                                }
                                
                                transformsType.getTransform().add(tType);
                            } else if (retrievalMethodKDV.getKeyDataName().equals("Type")) {
                                retrievalMethodType.setType(retrievalMethodKDV.getKeyDataValueString());
                            } else if (retrievalMethodKDV.getKeyDataName().equals("URI")) {
                                retrievalMethodType.setURI(retrievalMethodKDV.getKeyDataValueString());
                            } else {
                                throw new RuntimeException("Unrecognized key data type: " + retrievalMethodKDV.getKeyDataType());
                            }
                        }
                        if (transformsType.getTransform() != null && !transformsType.getTransform().isEmpty()) {
                            retrievalMethodType.setTransforms(transformsType);
                        }
                        JAXBElement dataJAXB = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", tagName), RetrievalMethodType.class, retrievalMethodType);
                        parentKeyDataList.add(dataJAXB);   
                    } else if (dataType.equals(PGPDataType.class.getSimpleName())) {
                        PGPDataType pgpDataType = new PGPDataType();
                        
                        for (KeyDataValue pgpDataKDV : modelKeyDataValue.getKeyDataValueList()) {
                            String pgpDataTagName = pgpDataKDV.getKeyDataName();
                            Object pgpDataContents = pgpDataKDV.getKeyDataValueBytes() != null ? pgpDataKDV.getKeyDataValueBytes() : pgpDataKDV.getKeyDataValueString();
                            Class pgpDataClazz = pgpDataContents.getClass();
                            JAXBElement jaxb = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", pgpDataTagName), pgpDataClazz, pgpDataContents);
                            pgpDataType.getContent().add(jaxb);
                        }
                        
                        JAXBElement dataJAXB = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", tagName), PGPDataType.class, pgpDataType);
                        parentKeyDataList.add(dataJAXB);   
                    } else if (dataType.equals(SPKIDataType.class.getSimpleName())) {
                        SPKIDataType spkiDataType = new SPKIDataType();
                        
                        for (KeyDataValue spkiDataKDV : modelKeyDataValue.getKeyDataValueList()) {
                            String spkiDataTagName = spkiDataKDV.getKeyDataName();
                            Object spkiDataContents = spkiDataKDV.getKeyDataValueBytes() != null ? spkiDataKDV.getKeyDataValueBytes() : spkiDataKDV.getKeyDataValueString();
                            Class spkiDataClazz = spkiDataContents.getClass();
                            JAXBElement jaxb = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", spkiDataTagName), spkiDataClazz, spkiDataContents);
                            spkiDataType.getSPKISexpAndAny().add(jaxb);
                        }
                        
                        JAXBElement dataJAXB = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", tagName), SPKIDataType.class, spkiDataType);
                        parentKeyDataList.add(dataJAXB);
                    } else if (dataType.equals(KeyValueType.class.getSimpleName())) {
                        KeyValueType keyValueType = new KeyValueType();
                        for (KeyDataValue keyValueDataKDV : modelKeyDataValue.getKeyDataValueList()) {
                            for (KeyDataValue keyValueDataChildKDV : keyValueDataKDV.getKeyDataValueList()) {
                                String keyDataDataType = keyValueDataChildKDV.getKeyDataType();
                                if (keyDataDataType.equals("DSAKeyValueType")) {
                                    DSAKeyValueType dsaKeyValueType = new DSAKeyValueType();
                                    for (KeyDataValue dsaKDV : keyValueDataChildKDV.getKeyDataValueList()) {
                                        if ("G".equals(dsaKDV.getKeyDataName())) {
                                            dsaKeyValueType.setG(dsaKDV.getKeyDataValueBytes());
                                        } else if ("P".equals(dsaKDV.getKeyDataName())) {
                                            dsaKeyValueType.setP(dsaKDV.getKeyDataValueBytes());
                                        } else if ("Q".equals(dsaKDV.getKeyDataName())) {
                                            dsaKeyValueType.setQ(dsaKDV.getKeyDataValueBytes());
                                        } else if ("Y".equals(dsaKDV.getKeyDataName())) {
                                            dsaKeyValueType.setY(dsaKDV.getKeyDataValueBytes());
                                        } else if ("J".equals(dsaKDV.getKeyDataName())) {
                                            dsaKeyValueType.setJ(dsaKDV.getKeyDataValueBytes());
                                        } else if ("Seed".equals(dsaKDV.getKeyDataName())) {
                                            dsaKeyValueType.setSeed(dsaKDV.getKeyDataValueBytes());
                                        } else if ("PgenCounter".equals(dsaKDV.getKeyDataName())) {
                                            dsaKeyValueType.setPgenCounter(dsaKDV.getKeyDataValueBytes());
                                        } else {
                                            throw new RuntimeException("Unrecognized dsa type: " + dsaKDV.getKeyDataName());
                                        } 
                                    }
                                    JAXBElement jaxb = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", keyValueDataChildKDV.getKeyDataName()), DSAKeyValueType.class, dsaKeyValueType);
                                    keyValueType.getContent().add(jaxb);
                                } else if (keyDataDataType.equals("RSAKeyValueType")) {
                                    RSAKeyValueType rsaKeyValueType = new RSAKeyValueType();
                                    for (KeyDataValue rsaKDV : keyValueDataChildKDV.getKeyDataValueList()) {
                                        if ("Exponent".equals(rsaKDV.getKeyDataName())) {
                                            rsaKeyValueType.setExponent(rsaKDV.getKeyDataValueBytes());
                                        } else if ("Modulus".equals(rsaKDV.getKeyDataName())) {
                                            rsaKeyValueType.setModulus(rsaKDV.getKeyDataValueBytes());
                                        } else {
                                            throw new RuntimeException("Unrecognized dsa type: " + rsaKDV.getKeyDataName());
                                        } 
                                    }
                                    JAXBElement jaxb = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", keyValueDataChildKDV.getKeyDataName()), RSAKeyValueType.class, rsaKeyValueType);
                                    keyValueType.getContent().add(jaxb);
                                } else {
                                    throw new RuntimeException("Unrecognized element: " + keyDataDataType);
                                }
                            }
                        }
                        JAXBElement dataJAXB = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", tagName), KeyValueType.class, keyValueType);
                        parentKeyDataList.add(dataJAXB);
                    } else {
                        throw new RuntimeException("Unrecognized type: " + dataType);
                    }
                } else {
                    JAXBElement dataJAXB = new JAXBElement(new QName("http://www.w3.org/2000/09/xmldsig#", tagName), contents.getClass(), contents);
                    parentKeyDataList.add(dataJAXB);
                }
            }
        }
                
	public static void mapBusinessNames(List<org.apache.juddi.model.BusinessName> modelNameList, 
										List<org.uddi.api_v3.Name> apiNameList) 
				   throws DispositionReportFaultMessage {
		apiNameList.clear();

		for (org.apache.juddi.model.BusinessName modelName : modelNameList) {
			org.uddi.api_v3.Name apiName = new org.uddi.api_v3.Name();
			apiName.setLang(modelName.getLangCode());
			apiName.setValue(modelName.getName());
			apiNameList.add(apiName);
		}
	}

	public static void mapBusinessDescriptions(List<org.apache.juddi.model.BusinessDescr> modelDescList, 
											   List<org.uddi.api_v3.Description> apiDescList) 
				   throws DispositionReportFaultMessage {
		apiDescList.clear();

		for (org.apache.juddi.model.BusinessDescr modelDesc : modelDescList) {
			org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
			apiDesc.setLang(modelDesc.getLangCode());
			apiDesc.setValue(modelDesc.getDescr());
			apiDescList.add(apiDesc);
		}
	}

	public static void mapDiscoveryUrls(List<org.apache.juddi.model.DiscoveryUrl> modelDiscUrlList, 
										org.uddi.api_v3.DiscoveryURLs apiDiscUrls,
										org.uddi.api_v3.BusinessEntity apiBusinessEntity) 
				   throws DispositionReportFaultMessage {
		if (modelDiscUrlList == null || modelDiscUrlList.size() == 0)
			return;
		
		if (apiDiscUrls == null)
			apiDiscUrls = new org.uddi.api_v3.DiscoveryURLs();

		List<org.uddi.api_v3.DiscoveryURL> apiDiscUrlList = apiDiscUrls.getDiscoveryURL();
		apiDiscUrlList.clear();
		
		for (org.apache.juddi.model.DiscoveryUrl modelDiscUrl : modelDiscUrlList) {
			org.uddi.api_v3.DiscoveryURL apiDiscUrl = new org.uddi.api_v3.DiscoveryURL();
			apiDiscUrl.setUseType(modelDiscUrl.getUseType());
			String discoveryURL = modelDiscUrl.getUrl();
			try {
				String baseUrl = AppConfig.getConfiguration().getString("juddi.server.baseurl");
				if (baseUrl==null) {
					logger.warn("Token 'juddi.server.baseurl' not found in the juddiv3.properties, defaulting to '" 
							+ Property.DEFAULT_BASE_URL + "'");
					baseUrl = Property.DEFAULT_BASE_URL;
				}
				discoveryURL = discoveryURL.replaceAll("\\$\\{juddi.server.baseurl\\}", baseUrl);
			} catch (ConfigurationException e) {
				logger.error(e.getMessage(),e);
			}
			apiDiscUrl.setValue(discoveryURL);
			apiDiscUrlList.add(apiDiscUrl);
		}
		apiBusinessEntity.setDiscoveryURLs(apiDiscUrls);
	}
	
	public static void mapContacts(List<org.apache.juddi.model.Contact> modelContactList, 
								   org.uddi.api_v3.Contacts apiContacts,
								   org.uddi.api_v3.BusinessEntity apiBusinessEntity) 
				   throws DispositionReportFaultMessage {
		if (modelContactList == null || modelContactList.size() == 0)
			return;
		
		if (apiContacts == null)
			apiContacts = new org.uddi.api_v3.Contacts();

		List<org.uddi.api_v3.Contact> apiContactList = apiContacts.getContact();
		apiContactList.clear();

		for (org.apache.juddi.model.Contact modelContact : modelContactList) {
			org.uddi.api_v3.Contact apiContact = new org.uddi.api_v3.Contact();
			apiContact.setUseType(modelContact.getUseType());
			
			mapPersonNames(modelContact.getPersonNames(), apiContact.getPersonName());
			mapContactDescriptions(modelContact.getContactDescrs(), apiContact.getDescription());
			mapContactEmails(modelContact.getEmails(), apiContact.getEmail());
			mapContactPhones(modelContact.getPhones(), apiContact.getPhone());
			mapContactAddresses(modelContact.getAddresses(), apiContact.getAddress());
			
			apiContactList.add(apiContact);
		}
		apiBusinessEntity.setContacts(apiContacts);
	}

	public static void mapContactDescriptions(List<org.apache.juddi.model.ContactDescr> modelDescList, 
											  List<org.uddi.api_v3.Description> apiDescList) 
	throws DispositionReportFaultMessage {
		apiDescList.clear();

		for (org.apache.juddi.model.ContactDescr modelDesc : modelDescList) {
			org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
			apiDesc.setLang(modelDesc.getLangCode());
			apiDesc.setValue(modelDesc.getDescr());
			apiDescList.add(apiDesc);
		}
	}
	
	public static void mapPersonNames(List<org.apache.juddi.model.PersonName> modelPersonNameList, 
			  List<org.uddi.api_v3.PersonName> apiPersonNameList) 
	throws DispositionReportFaultMessage {
		apiPersonNameList.clear();
		
		for (org.apache.juddi.model.PersonName personName : modelPersonNameList) {
			org.uddi.api_v3.PersonName apiPersonName = new org.uddi.api_v3.PersonName();
			apiPersonName.setLang(personName.getLangCode());
			apiPersonName.setValue(personName.getName());
			apiPersonNameList.add(apiPersonName);
		}
	}

	public static void mapContactEmails(List<org.apache.juddi.model.Email> modelEmailList, 
										List<org.uddi.api_v3.Email> apiEmailList) 
				   throws DispositionReportFaultMessage {
		apiEmailList.clear();

		for (org.apache.juddi.model.Email modelEmail : modelEmailList) {
			org.uddi.api_v3.Email apiEmail = new org.uddi.api_v3.Email();
			apiEmail.setUseType(modelEmail.getUseType());
			apiEmail.setValue(modelEmail.getEmailAddress());
			apiEmailList.add(apiEmail);
		}
	}
	
	public static void mapContactPhones(List<org.apache.juddi.model.Phone> modelPhoneList, 
										List<org.uddi.api_v3.Phone> apiPhoneList) 
				   throws DispositionReportFaultMessage {
		apiPhoneList.clear();

		for (org.apache.juddi.model.Phone modelPhone : modelPhoneList) {
			org.uddi.api_v3.Phone apiPhone = new org.uddi.api_v3.Phone();
			apiPhone.setUseType(modelPhone.getUseType());
			apiPhone.setValue(modelPhone.getPhoneNumber());
			apiPhoneList.add(apiPhone);
		}
	}

	public static void mapContactAddresses(List<org.apache.juddi.model.Address> modelAddressList, 
										   List<org.uddi.api_v3.Address> apiAddressList) 
				   throws DispositionReportFaultMessage {
		apiAddressList.clear();

		for (org.apache.juddi.model.Address modelAddress : modelAddressList) {
			org.uddi.api_v3.Address apiAddress = new org.uddi.api_v3.Address();
			apiAddress.setUseType(modelAddress.getUseType());
			apiAddress.setLang("");
			apiAddress.setSortCode(modelAddress.getSortCode());
			apiAddress.setTModelKey(modelAddress.getTmodelKey());
			
			mapAddressLines(modelAddress.getAddressLines(), apiAddress.getAddressLine());
			
			apiAddressList.add(apiAddress);
		}
	}

	public static void mapAddressLines(List<org.apache.juddi.model.AddressLine> modelAddressLineList, 
									   List<org.uddi.api_v3.AddressLine> apiAddressLineList) 
				   throws DispositionReportFaultMessage {
		apiAddressLineList.clear();

		for (org.apache.juddi.model.AddressLine modelAddressLine : modelAddressLineList) {
			org.uddi.api_v3.AddressLine apiAddressLine = new org.uddi.api_v3.AddressLine();
			apiAddressLine.setKeyName(modelAddressLine.getKeyName());
			apiAddressLine.setKeyValue(modelAddressLine.getKeyValue());
			apiAddressLine.setValue(modelAddressLine.getLine());
			apiAddressLineList.add(apiAddressLine);
		}
	}

	public static void mapBusinessIdentifiers(List<org.apache.juddi.model.BusinessIdentifier> modelIdentifierList, 
											  org.uddi.api_v3.IdentifierBag apiIdentifierBag,
											  org.uddi.api_v3.BusinessEntity apiBusinessEntity) 
				   throws DispositionReportFaultMessage {
		if (modelIdentifierList == null || modelIdentifierList.size() == 0)
			return;
		
		if (apiIdentifierBag == null)
			apiIdentifierBag = new org.uddi.api_v3.IdentifierBag();

		List<org.uddi.api_v3.KeyedReference> apiKeyedRefList = apiIdentifierBag.getKeyedReference();
		apiKeyedRefList.clear();

		for (org.apache.juddi.model.BusinessIdentifier modelIdentifier : modelIdentifierList) {
			org.uddi.api_v3.KeyedReference apiKeyedRef = new org.uddi.api_v3.KeyedReference();
			apiKeyedRef.setTModelKey(modelIdentifier.getTmodelKeyRef());
			apiKeyedRef.setKeyName(modelIdentifier.getKeyName());
			apiKeyedRef.setKeyValue(modelIdentifier.getKeyValue());
			apiKeyedRefList.add(apiKeyedRef);
		}
		apiBusinessEntity.setIdentifierBag(apiIdentifierBag);
	}

	public static void mapBusinessServices(List<org.apache.juddi.model.BusinessService> modelBusinessServiceList, 
										   List<org.apache.juddi.model.ServiceProjection> modelServiceProjectionList,
										   org.uddi.api_v3.BusinessServices apiBusinessServices,
										   org.uddi.api_v3.BusinessEntity apiBusinessEntity) 
				   throws DispositionReportFaultMessage {
		
		if (apiBusinessServices == null)
			apiBusinessServices = new org.uddi.api_v3.BusinessServices();

		List<org.uddi.api_v3.BusinessService> apiBusinessServiceList = apiBusinessServices.getBusinessService();
		apiBusinessServiceList.clear();
		
		if (modelBusinessServiceList != null && modelBusinessServiceList.size() > 0) {
			for (org.apache.juddi.model.BusinessService modelBusinessService : modelBusinessServiceList) {
				org.uddi.api_v3.BusinessService apiBusinessService = new org.uddi.api_v3.BusinessService();
				mapBusinessService(modelBusinessService, apiBusinessService);
				apiBusinessServiceList.add(apiBusinessService);
			}
		}
		
		
		if (modelServiceProjectionList != null && modelServiceProjectionList.size() > 0) {
			for (org.apache.juddi.model.ServiceProjection modelServiceProjection : modelServiceProjectionList) {
				org.uddi.api_v3.BusinessService apiBusinessService = new org.uddi.api_v3.BusinessService();
				mapBusinessService(modelServiceProjection.getBusinessService(), apiBusinessService);
				apiBusinessServiceList.add(apiBusinessService);
			}
		}
		
		if (apiBusinessServiceList.size() > 0)
			apiBusinessEntity.setBusinessServices(apiBusinessServices);
	}

	public static void mapBusinessService(org.apache.juddi.model.BusinessService modelBusinessService, 
										  org.uddi.api_v3.BusinessService apiBusinessService) 
				   throws DispositionReportFaultMessage {

		apiBusinessService.setBusinessKey(modelBusinessService.getBusinessEntity().getEntityKey());
		apiBusinessService.setServiceKey(modelBusinessService.getEntityKey());

		mapServiceNames(modelBusinessService.getServiceNames(), apiBusinessService.getName());
		mapServiceDescriptions(modelBusinessService.getServiceDescrs(), apiBusinessService.getDescription());
        mapBindingTemplates(modelBusinessService.getBindingTemplates(), apiBusinessService.getBindingTemplates(), apiBusinessService);
		apiBusinessService.setCategoryBag(mapCategoryBag(modelBusinessService.getCategoryBag(), apiBusinessService.getCategoryBag()));

                mapSignature(modelBusinessService.getSignatures(), apiBusinessService.getSignature());
	}

	public static void mapServiceNames(List<org.apache.juddi.model.ServiceName> modelNameList, 
									   List<org.uddi.api_v3.Name> apiNameList) 
				   throws DispositionReportFaultMessage {
		apiNameList.clear();

		for (org.apache.juddi.model.ServiceName modelName : modelNameList) {
			org.uddi.api_v3.Name apiName = new org.uddi.api_v3.Name();
			apiName.setLang(modelName.getLangCode());
			apiName.setValue(modelName.getName());
			apiNameList.add(apiName);
		}
	}

	public static void mapServiceDescriptions(List<org.apache.juddi.model.ServiceDescr> modelDescList, 
											  List<org.uddi.api_v3.Description> apiDescList) 
				   throws DispositionReportFaultMessage {
		apiDescList.clear();

		for (org.apache.juddi.model.ServiceDescr modelDesc : modelDescList) {
			org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
			apiDesc.setLang(modelDesc.getLangCode());
			apiDesc.setValue(modelDesc.getDescr());
			apiDescList.add(apiDesc);
		}
	}

	public static void mapBindingTemplates(List<org.apache.juddi.model.BindingTemplate> modelBindingTemplateList, 
										   org.uddi.api_v3.BindingTemplates apiBindingTemplates,
										   org.uddi.api_v3.BusinessService apiBusinessService) 
				   throws DispositionReportFaultMessage {
		if (modelBindingTemplateList == null || modelBindingTemplateList.size() == 0)
			return;
		
		if (apiBindingTemplates == null)
			apiBindingTemplates = new org.uddi.api_v3.BindingTemplates();

		List<org.uddi.api_v3.BindingTemplate> apiBindingTemplateList = apiBindingTemplates.getBindingTemplate();
		apiBindingTemplateList.clear();

		for (org.apache.juddi.model.BindingTemplate modelBindingTemplate : modelBindingTemplateList) {
			org.uddi.api_v3.BindingTemplate apiBindingTemplate = new org.uddi.api_v3.BindingTemplate();
			mapBindingTemplate(modelBindingTemplate, apiBindingTemplate);
			apiBindingTemplateList.add(apiBindingTemplate);
		}
		apiBusinessService.setBindingTemplates(apiBindingTemplates);
	}

	public static void mapBindingTemplate(org.apache.juddi.model.BindingTemplate modelBindingTemplate, 
										  org.uddi.api_v3.BindingTemplate apiBindingTemplate) 
				   throws DispositionReportFaultMessage {

		apiBindingTemplate.setServiceKey(modelBindingTemplate.getBusinessService().getEntityKey());
		apiBindingTemplate.setBindingKey(modelBindingTemplate.getEntityKey());
		org.uddi.api_v3.AccessPoint apiAccessPoint = new org.uddi.api_v3.AccessPoint();
		apiAccessPoint.setUseType(modelBindingTemplate.getAccessPointType());
		String accessPointValue = modelBindingTemplate.getAccessPointUrl();
		if (accessPointValue!=null) {
			try {
				String baseUrl = AppConfig.getConfiguration().getString("juddi.server.baseurl");
				if (baseUrl==null) {
					logger.warn("Token 'juddi.server.baseurl' not found in the juddiv3.properties, defaulting to '" 
							+ Property.DEFAULT_BASE_URL + "'");
					baseUrl = Property.DEFAULT_BASE_URL;
				}
				accessPointValue = accessPointValue.replaceAll("\\$\\{juddi.server.baseurl\\}", baseUrl);
			} catch (ConfigurationException e) {
				logger.error(e.getMessage(),e);
			}
		}
		apiAccessPoint.setValue(accessPointValue);
		apiBindingTemplate.setAccessPoint(apiAccessPoint);
		if (modelBindingTemplate.getHostingRedirector()!=null) {
			org.uddi.api_v3.HostingRedirector apiHost = new org.uddi.api_v3.HostingRedirector();
			apiHost.setBindingKey(modelBindingTemplate.getHostingRedirector());
			apiBindingTemplate.setHostingRedirector(apiHost);
		}
        mapTModelInstanceDetails(modelBindingTemplate.getTmodelInstanceInfos(), apiBindingTemplate.getTModelInstanceDetails(),apiBindingTemplate);
		mapBindingDescriptions(modelBindingTemplate.getBindingDescrs(), apiBindingTemplate.getDescription());

		apiBindingTemplate.setCategoryBag(mapCategoryBag(modelBindingTemplate.getCategoryBag(), apiBindingTemplate.getCategoryBag()));
                mapSignature(modelBindingTemplate.getSignatures(), apiBindingTemplate.getSignature());
	}

	public static void mapBindingDescriptions(List<org.apache.juddi.model.BindingDescr> modelDescList, 
											  List<org.uddi.api_v3.Description> apiDescList) 
			throws DispositionReportFaultMessage {
		apiDescList.clear();

		for (org.apache.juddi.model.BindingDescr modelDesc : modelDescList) {
			org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
			apiDesc.setLang(modelDesc.getLangCode());
			apiDesc.setValue(modelDesc.getDescr());
			apiDescList.add(apiDesc);
		}
	}

	public static org.uddi.api_v3.CategoryBag mapCategoryBag(org.apache.juddi.model.CategoryBag modelCategoryBag, 
															 org.uddi.api_v3.CategoryBag apiCategoryBag) 
				   throws DispositionReportFaultMessage {

		if (modelCategoryBag != null) {
			if (apiCategoryBag == null)
				apiCategoryBag = new org.uddi.api_v3.CategoryBag();
			
			for (org.apache.juddi.model.KeyedReference modelKeyedReference : modelCategoryBag.getKeyedReferences()) {
				org.uddi.api_v3.KeyedReference apiKeyedReference = new org.uddi.api_v3.KeyedReference();
				apiKeyedReference.setTModelKey(modelKeyedReference.getTmodelKeyRef());
				apiKeyedReference.setKeyName(modelKeyedReference.getKeyName());
				apiKeyedReference.setKeyValue(modelKeyedReference.getKeyValue());
				apiCategoryBag.getKeyedReference().add(apiKeyedReference);
			}
			for (org.apache.juddi.model.KeyedReferenceGroup modelKeyedReferenceGroup : modelCategoryBag.getKeyedReferenceGroups()) {
				org.uddi.api_v3.KeyedReferenceGroup apiKeyedReferenceGroup = new org.uddi.api_v3.KeyedReferenceGroup();
	
				mapKeyedReferenceGroup(modelKeyedReferenceGroup, apiKeyedReferenceGroup);
				
				apiCategoryBag.getKeyedReferenceGroup().add(apiKeyedReferenceGroup);
			}
		}
		return apiCategoryBag;
	}

	public static void mapKeyedReferenceGroup(org.apache.juddi.model.KeyedReferenceGroup modelKeyedReferenceGroup, 
											  org.uddi.api_v3.KeyedReferenceGroup apiKeyedReferenceGroup) 
				   throws DispositionReportFaultMessage {

		apiKeyedReferenceGroup.setTModelKey(modelKeyedReferenceGroup.getTmodelKey());

		for (org.apache.juddi.model.KeyedReference modelKeyedReference : modelKeyedReferenceGroup.getKeyedReferences()) {
			org.uddi.api_v3.KeyedReference apiKeyedReference = new org.uddi.api_v3.KeyedReference();
			apiKeyedReference.setTModelKey(modelKeyedReference.getTmodelKeyRef());
			apiKeyedReference.setKeyName(modelKeyedReference.getKeyName());
			apiKeyedReference.setKeyValue(modelKeyedReference.getKeyValue());
			apiKeyedReferenceGroup.getKeyedReference().add(apiKeyedReference);
		}
		
	}
	
	public static void mapTModelInstanceDetails(List<org.apache.juddi.model.TmodelInstanceInfo> modelTModelInstInfoList, 
												org.uddi.api_v3.TModelInstanceDetails apiTModelInstDetails,
												org.uddi.api_v3.BindingTemplate apiBindingTemplate) 
				   throws DispositionReportFaultMessage {
		if (modelTModelInstInfoList == null || modelTModelInstInfoList.size() == 0)
			return;
		
		if (apiTModelInstDetails == null)
			apiTModelInstDetails = new org.uddi.api_v3.TModelInstanceDetails();

		List<org.uddi.api_v3.TModelInstanceInfo> apiTModelInstInfoList = apiTModelInstDetails.getTModelInstanceInfo();
		apiTModelInstInfoList.clear();

		for (org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo : modelTModelInstInfoList) {
			org.uddi.api_v3.TModelInstanceInfo apiTModelInstInfo = new org.uddi.api_v3.TModelInstanceInfo();
			apiTModelInstInfo.setTModelKey(modelTModelInstInfo.getTmodelKey());
			mapTModelInstanceInfoDescriptions(modelTModelInstInfo.getTmodelInstanceInfoDescrs(), apiTModelInstInfo.getDescription());
			mapInstanceDetails(modelTModelInstInfo, apiTModelInstInfo.getInstanceDetails(), apiTModelInstInfo);

			apiTModelInstInfoList.add(apiTModelInstInfo);
		}
		apiBindingTemplate.setTModelInstanceDetails(apiTModelInstDetails);
	}
	
	public static void mapTModelInstanceInfoDescriptions(List<org.apache.juddi.model.TmodelInstanceInfoDescr> modelDescList, 
														 List<org.uddi.api_v3.Description> apiDescList) 
				   throws DispositionReportFaultMessage {
		apiDescList.clear();

		for (org.apache.juddi.model.TmodelInstanceInfoDescr modelDesc : modelDescList) {
			org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
			apiDesc.setLang(modelDesc.getLangCode());
			apiDesc.setValue(modelDesc.getDescr());
			apiDescList.add(apiDesc);
		}
	}

	public static void mapInstanceDetails(org.apache.juddi.model.TmodelInstanceInfo modelTModelInstInfo, 
										  org.uddi.api_v3.InstanceDetails apiInstanceDetails,
										  org.uddi.api_v3.TModelInstanceInfo apiTModelInstInfo) 
				   throws DispositionReportFaultMessage {
		if (modelTModelInstInfo == null || 
				(modelTModelInstInfo.getInstanceParms() == null && modelTModelInstInfo.getOverviewDocs().size() == 0))
			return;
		
		if (apiInstanceDetails == null)
			apiInstanceDetails = new org.uddi.api_v3.InstanceDetails();

		//InstanceParms
		apiInstanceDetails.setInstanceParms(modelTModelInstInfo.getInstanceParms());
		//Descriptions
		List<org.apache.juddi.model.InstanceDetailsDescr> modelInstDetailsDescrList = modelTModelInstInfo.getInstanceDetailsDescrs();
		for (org.apache.juddi.model.InstanceDetailsDescr modelInstDetailDescr : modelInstDetailsDescrList) {
			org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
			apiDesc.setLang(modelInstDetailDescr.getLangCode());
			apiDesc.setValue(modelInstDetailDescr.getDescr());
			apiInstanceDetails.getDescription().add(apiDesc);
		}
		//OverviewDoc
		mapOverviewDocs(modelTModelInstInfo.getOverviewDocs(),apiInstanceDetails,null);
	    
		apiTModelInstInfo.setInstanceDetails(apiInstanceDetails);
	}
	
	public static void mapOverviewDocs(List<org.apache.juddi.model.OverviewDoc> modelOverviewDocs,
			                           org.uddi.api_v3.InstanceDetails apiInstanceDetails,
			                           org.uddi.api_v3.TModel apiTModel)
	{
		for (OverviewDoc modelOverviewDoc : modelOverviewDocs) {
			org.uddi.api_v3.OverviewDoc apiOverviewDoc = new org.uddi.api_v3.OverviewDoc();
			
			//Descriptions
			List<org.apache.juddi.model.OverviewDocDescr> overviewDocDescrList = modelOverviewDoc.getOverviewDocDescrs();
			for (org.apache.juddi.model.OverviewDocDescr overviewDocDescr : overviewDocDescrList) {
				org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
				apiDesc.setLang(overviewDocDescr.getLangCode());
				apiDesc.setValue(overviewDocDescr.getDescr());
				apiOverviewDoc.getDescription().add(apiDesc);
			}
			//OverviewURL
			org.uddi.api_v3.OverviewURL apiOverviewURL = new org.uddi.api_v3.OverviewURL();
			apiOverviewURL.setUseType(modelOverviewDoc.getOverviewUrlUseType());
			apiOverviewURL.setValue(modelOverviewDoc.getOverviewUrl());
			apiOverviewDoc.setOverviewURL(apiOverviewURL);
			//Set the entity on the apiOverviewDoc
			if (apiInstanceDetails!=null) {
				apiInstanceDetails.getOverviewDoc().add(apiOverviewDoc);
			} else {
				apiTModel.getOverviewDoc().add(apiOverviewDoc);
			}
		}
	}
	
	
	public static void mapTModel(org.apache.juddi.model.Tmodel modelTModel, 
								 org.uddi.api_v3.TModel apiTModel) 
				   throws DispositionReportFaultMessage {

		apiTModel.setTModelKey(modelTModel.getEntityKey());
		org.uddi.api_v3.Name apiName = new org.uddi.api_v3.Name();
		apiName.setValue(modelTModel.getName());
		apiName.setLang(modelTModel.getLangCode());
		apiTModel.setName(apiName);
		apiTModel.setDeleted(modelTModel.getDeleted());
		
		mapTModelDescriptions(modelTModel.getTmodelDescrs(), apiTModel.getDescription());

		mapTModelIdentifiers(modelTModel.getTmodelIdentifiers(), apiTModel.getIdentifierBag(), apiTModel);
		apiTModel.setCategoryBag(mapCategoryBag(modelTModel.getCategoryBag(), apiTModel.getCategoryBag()));
		
		mapOverviewDocs(modelTModel.getOverviewDocs(), null, apiTModel);
                mapSignature(modelTModel.getSignatures(), apiTModel.getSignature());
	}

	public static void mapTModelDescriptions(List<org.apache.juddi.model.TmodelDescr> modelDescList, 
											 List<org.uddi.api_v3.Description> apiDescList) 
			    throws DispositionReportFaultMessage {
		apiDescList.clear();

		for (org.apache.juddi.model.TmodelDescr modelDesc : modelDescList) {
			org.uddi.api_v3.Description apiDesc = new org.uddi.api_v3.Description();
			apiDesc.setLang(modelDesc.getLangCode());
			apiDesc.setValue(modelDesc.getDescr());
			apiDescList.add(apiDesc);
		}
	}

	public static void mapTModelIdentifiers(List<org.apache.juddi.model.TmodelIdentifier> modelIdentifierList, 
											org.uddi.api_v3.IdentifierBag apiIdentifierBag,
											org.uddi.api_v3.TModel apiTModel) 
				   throws DispositionReportFaultMessage {
		if (modelIdentifierList == null || modelIdentifierList.size() ==0)
			return;
		
		if (apiIdentifierBag == null)
			apiIdentifierBag = new org.uddi.api_v3.IdentifierBag();

		List<org.uddi.api_v3.KeyedReference> apiKeyedRefList = apiIdentifierBag.getKeyedReference();
		apiKeyedRefList.clear();

		for (org.apache.juddi.model.TmodelIdentifier modelIdentifier : modelIdentifierList) {
			org.uddi.api_v3.KeyedReference apiKeyedRef = new org.uddi.api_v3.KeyedReference();
			apiKeyedRef.setTModelKey(modelIdentifier.getTmodelKeyRef());
			apiKeyedRef.setKeyName(modelIdentifier.getKeyName());
			apiKeyedRef.setKeyValue(modelIdentifier.getKeyValue());
			apiKeyedRefList.add(apiKeyedRef);
		}
		apiTModel.setIdentifierBag(apiIdentifierBag);
	}

	public static void mapBusinessInfo(org.apache.juddi.model.BusinessEntity modelBusinessEntity, 
									   org.uddi.api_v3.BusinessInfo apiBusinessInfo) 
				   throws DispositionReportFaultMessage {
		
		apiBusinessInfo.setBusinessKey(modelBusinessEntity.getEntityKey());
		
		mapBusinessNames(modelBusinessEntity.getBusinessNames(), apiBusinessInfo.getName());
		mapBusinessDescriptions(modelBusinessEntity.getBusinessDescrs(), apiBusinessInfo.getDescription());
		
		mapServiceInfos(modelBusinessEntity.getBusinessServices(), apiBusinessInfo.getServiceInfos(), apiBusinessInfo);
		
	}

	public static void mapServiceInfos(List<org.apache.juddi.model.BusinessService> modelBusinessServiceList, 
									   org.uddi.api_v3.ServiceInfos apiServiceInfos,
									   org.uddi.api_v3.BusinessInfo apiBusinessInfo) 
				   throws DispositionReportFaultMessage {
		if (modelBusinessServiceList.size()==0) return;
		if (apiServiceInfos == null)
			apiServiceInfos = new org.uddi.api_v3.ServiceInfos();
		
		List<org.uddi.api_v3.ServiceInfo> apiServiceInfoList = apiServiceInfos.getServiceInfo();
		apiServiceInfoList.clear();
		
		for (org.apache.juddi.model.BusinessService modelBusinessService : modelBusinessServiceList) {
			org.uddi.api_v3.ServiceInfo apiServiceInfo = new org.uddi.api_v3.ServiceInfo();

			mapServiceInfo(modelBusinessService, apiServiceInfo);

			apiServiceInfos.getServiceInfo().add(apiServiceInfo);
		}
		apiBusinessInfo.setServiceInfos(apiServiceInfos);
	}
	
	public static void mapServiceInfo(org.apache.juddi.model.BusinessService modelBusinessService, 
									  org.uddi.api_v3.ServiceInfo apiServiceInfo) 
				   throws DispositionReportFaultMessage {

		apiServiceInfo.setBusinessKey(modelBusinessService.getBusinessEntity().getEntityKey());
		apiServiceInfo.setServiceKey(modelBusinessService.getEntityKey());

		mapServiceNames(modelBusinessService.getServiceNames(), apiServiceInfo.getName());
	}
	
	public static void mapTModelInfo(org.apache.juddi.model.Tmodel modelTModel, 
									 org.uddi.api_v3.TModelInfo apiTModelInfo) 
				   throws DispositionReportFaultMessage {

		apiTModelInfo.setTModelKey(modelTModel.getEntityKey());
		org.uddi.api_v3.Name apiName = new org.uddi.api_v3.Name();
		apiName.setValue(modelTModel.getName());
		apiName.setLang(modelTModel.getLangCode());
		apiTModelInfo.setName(apiName);

		mapTModelDescriptions(modelTModel.getTmodelDescrs(), apiTModelInfo.getDescription());

	}
	
	public static void mapAuthToken(org.apache.juddi.model.AuthToken modelAuthToken, 
									org.uddi.api_v3.AuthToken apiAuthToken) 
				   throws DispositionReportFaultMessage {
		apiAuthToken.setAuthInfo(modelAuthToken.getAuthToken());
	
	}

	public static void mapPublisherAssertion(org.apache.juddi.model.PublisherAssertion modelPublisherAssertion, 
											 org.uddi.api_v3.PublisherAssertion apiPublisherAssertion) 
				   throws DispositionReportFaultMessage {

		apiPublisherAssertion.setFromKey(modelPublisherAssertion.getId().getFromKey());
		apiPublisherAssertion.setToKey(modelPublisherAssertion.getId().getToKey());
		
		org.uddi.api_v3.KeyedReference keyedRef = new org.uddi.api_v3.KeyedReference();
		keyedRef.setTModelKey(modelPublisherAssertion.getTmodelKey());
		keyedRef.setKeyName(modelPublisherAssertion.getKeyName());
		keyedRef.setKeyValue(modelPublisherAssertion.getKeyValue());
		
		apiPublisherAssertion.setKeyedReference(keyedRef);
	}
	
	public static void mapAssertionStatusItem(org.apache.juddi.model.PublisherAssertion modelPublisherAssertion, 
											  org.uddi.api_v3.AssertionStatusItem apiAssertionStatusItem,
											  List<?> businessKeys) 
				   throws DispositionReportFaultMessage {

		apiAssertionStatusItem.setFromKey(modelPublisherAssertion.getId().getFromKey());
		apiAssertionStatusItem.setToKey(modelPublisherAssertion.getId().getToKey());
		
		org.uddi.api_v3.KeyedReference keyedRef = new org.uddi.api_v3.KeyedReference();
		keyedRef.setTModelKey(modelPublisherAssertion.getTmodelKey());
		keyedRef.setKeyName(modelPublisherAssertion.getKeyName());
		keyedRef.setKeyValue(modelPublisherAssertion.getKeyValue());
		
		apiAssertionStatusItem.setKeyedReference(keyedRef);
		
		if ("true".equalsIgnoreCase(modelPublisherAssertion.getFromCheck()) && 
			"true".equalsIgnoreCase(modelPublisherAssertion.getToCheck()))
			apiAssertionStatusItem.setCompletionStatus(CompletionStatus.STATUS_COMPLETE);
		else if(!"true".equalsIgnoreCase(modelPublisherAssertion.getFromCheck()) && 
				"true".equalsIgnoreCase(modelPublisherAssertion.getToCheck()))
			apiAssertionStatusItem.setCompletionStatus(CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);
		else if("true".equalsIgnoreCase(modelPublisherAssertion.getFromCheck()) && 
				!"true".equalsIgnoreCase(modelPublisherAssertion.getToCheck()))
			apiAssertionStatusItem.setCompletionStatus(CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
		else if(!"true".equalsIgnoreCase(modelPublisherAssertion.getFromCheck()) && 
				!"true".equalsIgnoreCase(modelPublisherAssertion.getToCheck()))
			apiAssertionStatusItem.setCompletionStatus(CompletionStatus.STATUS_BOTH_INCOMPLETE);
		
		org.uddi.api_v3.KeysOwned keysOwned = new org.uddi.api_v3.KeysOwned();
		
		//converting resultList to simple List
		List<String> businessKeyList = new ArrayList<String>();
		for (Object businessKey : businessKeys) {
			businessKeyList.add(String.valueOf(businessKey));
		}
		Collections.sort(businessKeyList);
		if (Collections.binarySearch(businessKeyList, modelPublisherAssertion.getBusinessEntityByFromKey().getEntityKey()) >= 0)
			keysOwned.setFromKey(modelPublisherAssertion.getBusinessEntityByFromKey().getEntityKey());
		
		if (Collections.binarySearch(businessKeyList, modelPublisherAssertion.getBusinessEntityByToKey().getEntityKey()) >= 0)
			keysOwned.setToKey(modelPublisherAssertion.getBusinessEntityByToKey().getEntityKey());
		if (keysOwned.getFromKey() == null && keysOwned.getToKey() == null) {
			throw new FatalErrorException(new ErrorMessage("errors.invalidKey.KeysOwned"));
		}
		apiAssertionStatusItem.setKeysOwned(keysOwned);
	}

	public static void mapRelatedBusinessInfo(org.apache.juddi.model.PublisherAssertion modelPublisherAssertion,
											  org.apache.juddi.model.BusinessEntity modelRelatedBusiness,
											  org.uddi.api_v3.Direction direction,
											  org.uddi.api_v3.RelatedBusinessInfo apiRelatedBusinessInfo) 
				   throws DispositionReportFaultMessage {

		apiRelatedBusinessInfo.setBusinessKey(modelRelatedBusiness.getEntityKey());
		
		mapBusinessNames(modelRelatedBusiness.getBusinessNames(), apiRelatedBusinessInfo.getName());
		mapBusinessDescriptions(modelRelatedBusiness.getBusinessDescrs(), apiRelatedBusinessInfo.getDescription());
		
		org.uddi.api_v3.SharedRelationships sharedRelationships = new org.uddi.api_v3.SharedRelationships();
		sharedRelationships.setDirection(direction);
		
		org.uddi.api_v3.KeyedReference keyedRef = new org.uddi.api_v3.KeyedReference();
		keyedRef.setTModelKey(modelPublisherAssertion.getTmodelKey());
		keyedRef.setKeyName(modelPublisherAssertion.getKeyName());
		keyedRef.setKeyValue(modelPublisherAssertion.getKeyValue());
		sharedRelationships.getKeyedReference().add(keyedRef);
		
		apiRelatedBusinessInfo.getSharedRelationships().add(sharedRelationships);
	}

	public static void mapOperationalInfo(UddiEntity modelUddiEntity,
										  OperationalInfo apiOperationalInfo)
				   throws DispositionReportFaultMessage {
		
		apiOperationalInfo.setCreated(TypeConvertor.convertDateToXMLGregorianCalendar(modelUddiEntity.getCreated()));
		apiOperationalInfo.setModified(TypeConvertor.convertDateToXMLGregorianCalendar(modelUddiEntity.getModified()));
		apiOperationalInfo.setModifiedIncludingChildren(TypeConvertor.convertDateToXMLGregorianCalendar(modelUddiEntity.getModifiedIncludingChildren()));
		apiOperationalInfo.setNodeID(modelUddiEntity.getNodeId());
		apiOperationalInfo.setAuthorizedName(modelUddiEntity.getAuthorizedName());
		apiOperationalInfo.setEntityKey(modelUddiEntity.getEntityKey());
	}


	public static void mapSubscription(org.apache.juddi.model.Subscription modelSubscription, 
									   org.uddi.sub_v3.Subscription apiSubscription) 
				   throws DispositionReportFaultMessage {
		
		apiSubscription.setSubscriptionKey(modelSubscription.getSubscriptionKey());
		apiSubscription.setBrief(modelSubscription.isBrief());
		apiSubscription.setExpiresAfter(TypeConvertor.convertDateToXMLGregorianCalendar(modelSubscription.getExpiresAfter()));
		apiSubscription.setBindingKey(modelSubscription.getBindingKey());
		apiSubscription.setMaxEntities(modelSubscription.getMaxEntities());
		apiSubscription.setNotificationInterval(TypeConvertor.convertStringToDuration(modelSubscription.getNotificationInterval()));

		try {
			SubscriptionFilter existingFilter = (SubscriptionFilter)JAXBMarshaller.unmarshallFromString(modelSubscription.getSubscriptionFilter(), JAXBMarshaller.PACKAGE_SUBSCRIPTION);
			apiSubscription.setSubscriptionFilter(existingFilter);
		} 
		catch (JAXBException e) {
			logger.error("JAXB Exception while marshalling subscription filter", e);
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		} 
	}
	
	public static void mapClientSubscriptionInfo(org.apache.juddi.model.ClientSubscriptionInfo modelClientSubscriptionInfo, 
			                                     org.apache.juddi.api_v3.ClientSubscriptionInfo apiClientSubscriptionInfo) 
				throws DispositionReportFaultMessage {
		
		apiClientSubscriptionInfo.setSubscriptionKey(modelClientSubscriptionInfo.getSubscriptionKey());
		apiClientSubscriptionInfo.setLastModified(modelClientSubscriptionInfo.getLastNotified());
		
		if (modelClientSubscriptionInfo.getFromClerk()!=null) {
			org.apache.juddi.api_v3.Clerk apiFromClerk = new org.apache.juddi.api_v3.Clerk();
			mapClerk(modelClientSubscriptionInfo.getFromClerk(), apiFromClerk);
			apiClientSubscriptionInfo.setFromClerk(apiFromClerk);
		}
		if (modelClientSubscriptionInfo.getToClerk()!=null) {
			org.apache.juddi.api_v3.Clerk apiToClerk = new org.apache.juddi.api_v3.Clerk();
			mapClerk(modelClientSubscriptionInfo.getToClerk(), apiToClerk);
			apiClientSubscriptionInfo.setToClerk(apiToClerk);
		}
	}
	
	public static void mapClerk(org.apache.juddi.model.Clerk modelClerk, 
            org.apache.juddi.api_v3.Clerk apiClerk) 
		throws DispositionReportFaultMessage {
		
		apiClerk.setName(modelClerk.getClerkName());
		apiClerk.setPassword(modelClerk.getCred());
		apiClerk.setPublisher(modelClerk.getPublisherId());
		if (modelClerk.getNode()!=null) {
			org.apache.juddi.api_v3.Node apiNode = new org.apache.juddi.api_v3.Node();
			mapNode(modelClerk.getNode(), apiNode);
			apiClerk.setNode(apiNode);
		}
	}

	public static void mapNode(org.apache.juddi.model.Node modelNode, 
            org.apache.juddi.api_v3.Node apiNode) 
		throws DispositionReportFaultMessage {
		
		apiNode.setCustodyTransferUrl(modelNode.getCustodyTransferUrl());
		apiNode.setFactoryInitial(modelNode.getFactoryInitial());
		apiNode.setFactoryNamingProvider(modelNode.getFactoryNamingProvider());
		apiNode.setFactoryURLPkgs(modelNode.getFactoryURLPkgs());
		apiNode.setInquiryUrl(modelNode.getInquiryUrl());
		apiNode.setJuddiApiUrl(modelNode.getJuddiApiUrl());
		apiNode.setName(modelNode.getName());
		apiNode.setClientName(modelNode.getClientName());
		apiNode.setProxyTransport(modelNode.getProxyTransport());
		apiNode.setPublishUrl(modelNode.getPublishUrl());
		apiNode.setSecurityUrl(modelNode.getSecurityUrl());
		apiNode.setSubscriptionUrl(modelNode.getSubscriptionUrl());
	}

    private static Object convertDataToTransformContent(String type, byte[] xformBytes) throws RuntimeException {
        Object transformObject;
        if (type.equals(String.class.getSimpleName())) {
            try {
                transformObject = new String(xformBytes, "UTF-8");
            } catch (Exception e) {
                throw new RuntimeException("Error decoding string due to: " + e.getMessage(), e);
            }
        } else if (type.equals(byte[].class.getSimpleName())) {
            transformObject = xformBytes;
        } else if (type.equals(Element.class.getCanonicalName())) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setXIncludeAware(true);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new ByteArrayInputStream(xformBytes));
                transformObject = doc.getDocumentElement();
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse element due to: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Unrecognized type: " + type);
        }
        return transformObject;
    }
}
