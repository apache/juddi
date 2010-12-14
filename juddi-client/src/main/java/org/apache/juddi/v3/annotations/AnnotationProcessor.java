/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.v3.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.TokenResolver;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;

public class AnnotationProcessor {
	
	private static final String KEYED_REFERENCE="keyedReference=";
	private static final String KEY_NAME="keyName=";
	private static final String KEY_VALUE="keyValue=";
	private static final String TMODEL_KEY="tModelKey=";
	
	private Log log = LogFactory.getLog(AnnotationProcessor.class);
	
	public Collection<BusinessService> readServiceAnnotations(String[] classesWithAnnotations, Properties properties) {
		Collection<BusinessService> services = new ArrayList<BusinessService>();
		for (String className : classesWithAnnotations) {
			try {		
				BusinessService service = readServiceAnnotations(className, properties);
				services.add(service);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return services;
	}
	
	public BusinessService readServiceAnnotations(String classWithAnnotations, Properties properties) throws ClassNotFoundException {
		
		BusinessService service = new BusinessService();
		Class<?> clazz = ClassUtil.forName(classWithAnnotations, this.getClass());
		UDDIService uddiService= (UDDIService) clazz.getAnnotation(UDDIService.class);
		WebService webServiceAnnotation = (WebService) clazz.getAnnotation(WebService.class);
		
		if (uddiService!=null) {
			//service
			String lang = "en"; //default to english
			if (uddiService.lang()!=null) {
				lang = uddiService.lang();
			}
			Name name = new Name();
			name.setLang(lang); 
			service.setBusinessKey(TokenResolver.replaceTokens(uddiService.businessKey(),properties));
			service.setServiceKey(TokenResolver.replaceTokens(uddiService.serviceKey(),properties));
			if (!"".equals(uddiService.serviceName())) {
				name.setValue(TokenResolver.replaceTokens(uddiService.serviceName(),properties));
			} else if (webServiceAnnotation!=null && !"".equals(webServiceAnnotation.serviceName())) {
				name.setValue(webServiceAnnotation.serviceName());
			} else {
				name.setValue(clazz.getSimpleName());
			}
			service.getName().add(name);
			Description description = new Description();
			description.setLang(lang);
			description.setValue(TokenResolver.replaceTokens(uddiService.description(),properties));
			service.getDescription().add(description);
			
			//categoryBag on the service
			if (!"".equals(uddiService.categoryBag())) {
				CategoryBag categoryBag = parseCategoryBag(uddiService.categoryBag());
		        service.setCategoryBag(categoryBag);
			}
			
			//bindingTemplate on service
			BindingTemplate bindingTemplate = parseServiceBinding(clazz, lang, webServiceAnnotation, properties);
			if (bindingTemplate!=null) {
				bindingTemplate.setServiceKey(service.getServiceKey());
				if (service.getBindingTemplates()==null) {
					service.setBindingTemplates(new BindingTemplates());
				}
				service.getBindingTemplates().getBindingTemplate().add(bindingTemplate);
			}
			
			
		} else {
			log.error("Missing UDDIService annotation in class " + classWithAnnotations);
		}
			
		return service;
	}
	
	protected BindingTemplate parseServiceBinding(Class<?> classWithAnnotations, String lang, 
			WebService webServiceAnnotation, Properties properties) {
		
		BindingTemplate bindingTemplate = null;
		UDDIServiceBinding uddiServiceBinding= (UDDIServiceBinding) classWithAnnotations.getAnnotation(UDDIServiceBinding.class);
		//binding
		if (uddiServiceBinding!=null) {
			bindingTemplate = new BindingTemplate();
			
			bindingTemplate.setBindingKey(TokenResolver.replaceTokens(uddiServiceBinding.bindingKey(), properties));
			
			String bindingLang = String.valueOf(lang);
			if (uddiServiceBinding.lang()!=null) {
				bindingLang = TokenResolver.replaceTokens(uddiServiceBinding.lang(),properties);
			}
			Description bindingDescription = new Description();
			bindingDescription.setLang(bindingLang);
			bindingDescription.setValue(TokenResolver.replaceTokens(uddiServiceBinding.description(),properties));
			bindingTemplate.getDescription().add(bindingDescription);
			
			AccessPoint accessPoint = new AccessPoint();
			accessPoint.setUseType(AccessPointType.WSDL_DEPLOYMENT.toString());
			if (!"".equals(uddiServiceBinding.accessPointType())) {
				accessPoint.setUseType(uddiServiceBinding.accessPointType());
			}
			if (!"".equals(uddiServiceBinding.accessPoint())) {
				String endPoint = uddiServiceBinding.accessPoint();
				endPoint = TokenResolver.replaceTokens(endPoint, properties);
                log.debug("AccessPoint EndPoint=" + endPoint);
				accessPoint.setValue(endPoint);
			} else if (webServiceAnnotation!=null && webServiceAnnotation.wsdlLocation()!=null) {
				accessPoint.setValue(webServiceAnnotation.wsdlLocation());
			}
			bindingTemplate.setAccessPoint(accessPoint);
			
			//tModelKeys on the binding
			if (!"".equals(uddiServiceBinding.tModelKeys())) {
				String[] tModelKeys= uddiServiceBinding.tModelKeys().split(",");
				for (String tModelKey : tModelKeys) {
					TModelInstanceInfo instanceInfo = new TModelInstanceInfo();
					instanceInfo.setTModelKey(tModelKey);
					if (bindingTemplate.getTModelInstanceDetails()==null) {
						bindingTemplate.setTModelInstanceDetails(new TModelInstanceDetails());
					}
					bindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo().add(instanceInfo);
				}
			}
			//categoryBag on the binding
			if (!"".equals(uddiServiceBinding.categoryBag())) {
				CategoryBag categoryBag = parseCategoryBag(uddiServiceBinding.categoryBag());
		        bindingTemplate.setCategoryBag(categoryBag);
			}
		} else {
			log.error("Missing UDDIServiceBinding annotation in class " + classWithAnnotations);
		}
		return bindingTemplate;
	}
	/**
	 * parse something like: [keyName=uddi-org:types:wsdl,keyValue=wsdlDeployment,tModelKey=uddi:uddi.org:categorization:types]
							
	 * @param categoryBagStr
	 * @return
	 */
	protected CategoryBag parseCategoryBag(String categoryBagStr) {
		
		CategoryBag categoryBag = new CategoryBag();
		log.debug("CategoryBag Annotation=" + categoryBagStr);
		if (!"".equals(categoryBagStr)) {
			String[] sections = categoryBagStr.split(",");
	        for (String section : sections) {
	        	if (section.startsWith(KEYED_REFERENCE)) {
		            String keyedReferenceStr = section.substring(KEYED_REFERENCE.length(),section.length());
		            log.debug("Found KeyedReference=" + keyedReferenceStr);
		            String[] keyedReferences = keyedReferenceStr.split(";");
		            KeyedReference keyedReference = new KeyedReference();
		            for (String key : keyedReferences) {
						if (key.startsWith(KEY_NAME)) keyedReference.setKeyName(key.substring(KEY_NAME.length(),key.length()));
						if (key.startsWith(KEY_VALUE)) keyedReference.setKeyValue(key.substring(KEY_VALUE.length(),key.length()));
						if (key.startsWith(TMODEL_KEY)) keyedReference.setTModelKey(key.substring(TMODEL_KEY.length(),key.length()));
					}
		            log.debug("KeyedReference = " + KEY_NAME + keyedReference.getKeyName() + " " 
		            		                      + KEY_VALUE + keyedReference.getKeyValue() + " "
		            		                      + TMODEL_KEY + keyedReference.getTModelKey());
		            categoryBag.getKeyedReference().add(keyedReference);
	        	} else {
	        		log.warn("Ignoring " + section);
	                 //TODO add support for KeyedReferenceGroups?
	        	}
	        }
        }
        return categoryBag;
	}
}
