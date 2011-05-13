/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.config;

import java.util.Properties;

import javax.xml.namespace.QName;

public class Property 
{
	public final static String UDDI_RELOAD_DELAY  ="reloadDelay";
	
	public final static String UDDI_PROXY_FACTORY_INITIAL       = "java.naming.factory.initial";
	public final static String UDDI_PROXY_PROVIDER_URL          = "java.naming.provider.url";
	public final static String UDDI_PROXY_FACTORY_URL_PKS       = "java.naming.factory.url.pkgs";

	public final static String DEFAULT_UDDI_PROXY_TRANSPORT     = "org.uddi.api_v3.client.transport.JAXWSTransport";
	
	//Properties that can be set in the uddi.xml
	public static final String LANG                             = "lang";
	public static final String BUSINESS_KEY                     = "businessKey";
	public static final String KEY_DOMAIN                       = "keyDomain";
	public static final String SERVICE_KEY_FORMAT               = "bpelServiceKeyFormat";
	public static final String BUSINESS_KEY_FORMAT              = "businessKeyFormat";
	public static final String BINDING_KEY_FORMAT               = "bindingKeyFormat";
	public static final String SERVICE_DESCRIPTION              = "serviceDescription";
	public static final String BINDING_DESCRIPTION              = "bindingDescription";
	public static final String SERVICE_CATEGORY_BAG             = "serviceCategoryBag";
	public static final String BINDING_CATEGORY_BAG             = "bindingCategoryBag";
	
	//Default Values
	public static final String DEFAULT_LANG                     = "en:";
	public static final String DEFAULT_BUSINESS_KEY_FORMAT      = "uddi:${keyDomain}:${businessName}";
	public static final String DEFAULT_SERVICE_KEY_FORMAT       = "uddi:${keyDomain}:${serviceName}";
	public static final String DEFAULT_BINDING_KEY_FORMAT       = "uddi:${keyDomain}:${nodeName}_${serviceName}_${portName}";
	public static final String DEFAULT_SERVICE_DESCRIPTION      = "Default service description when no <wsdl:document> element is defined inside the <wsdl:service> element.";
	public static final String DEFAULT_BINDING_DESCRIPTION      = "Default binding description when no <wsdl:document> element is defined inside the <wsdl:binding> element.";
	/**
	 * Constructs the serviceKey based on the bindingKeyFormat specified in the properties. When no
	 * businessKeyFormat is specific the default format of uddi:${keyDomain}:${businessName} is used. The businessName
	 * property needs to be set properties.
	 * 
	 * @param properties
	 * @return the businessKey
	 */
	public static String getBusinessKey(Properties properties) {
		String keyFormat = properties.getProperty(BUSINESS_KEY_FORMAT, DEFAULT_BUSINESS_KEY_FORMAT);
		String businessKey = TokenResolver.replaceTokens(keyFormat, properties).toLowerCase();
		return businessKey;
	}
	/**
	 * Constructs the serviceKey based on the serviceKeyFormat specified in the properties. When no
	 * serviceKeyFormat is specific the default format of uddi:${keyDomain}:${serviceName} is used.
	 * 
	 * @param properties
	 * @param serviceName
	 * @return the serviceKey
	 */
	public static String getServiceKey(Properties properties, QName serviceName) {
		Properties tempProperties = new Properties();
		tempProperties.putAll(properties);
		tempProperties.put("serviceName", serviceName.getLocalPart());
		//Constructing the serviceKey
		String keyFormat = tempProperties.getProperty(SERVICE_KEY_FORMAT, DEFAULT_SERVICE_KEY_FORMAT);
		String serviceKey = TokenResolver.replaceTokens(keyFormat, tempProperties).toLowerCase();
		return serviceKey;
	}
	/**
	 * Constructs the bindingKey based on the bindingKeyFormat specified in the properties. When no
	 * bindingKeyFormat is specific the default format of uddi:${keyDomain}:${nodeName}-${serviceName}-{portName} is used.
	 * 
	 * @param properties
	 * @param serviceName
	 * @param portName
	 * @return the bindingKey
	 */
	public static String getBindingKey(Properties properties, QName serviceName, String portName) {
		Properties tempProperties = new Properties();
		tempProperties.putAll(properties);
		tempProperties.put("serviceName", serviceName.getLocalPart());
		tempProperties.put("portName", portName);
		//Constructing the binding Key
		String keyFormat = properties.getProperty(BINDING_KEY_FORMAT, DEFAULT_BINDING_KEY_FORMAT);
		String bindingKey = TokenResolver.replaceTokens(keyFormat, tempProperties).toLowerCase();
		return bindingKey;
	}
}
