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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
	public static final String SUBSCRIPTION_KEY_FORMAT          = "subscriptionKeyFormat";
	public static final String SERVICE_DESCRIPTION              = "serviceDescription";
	public static final String BINDING_DESCRIPTION              = "bindingDescription";
	public static final String SERVICE_CATEGORY_BAG             = "serviceCategoryBag";
	public static final String BINDING_CATEGORY_BAG             = "bindingCategoryBag";
	public static final String BASIC_AUTH_USERNAME              = "basicAuthUsername";
	public static final String BASIC_AUTH_PASSWORD              = "basicAuthPassword";
	
	//Default Values
	public static final String DEFAULT_LANG                     = "en";
	public static final String DEFAULT_SERVICE_DESCRIPTION      = "Default service description when no <wsdl:document> element is defined inside the <wsdl:service> element.";
	public static final String DEFAULT_BINDING_DESCRIPTION      = "Default binding description when no <wsdl:document> element is defined inside the <wsdl:binding> element.";
	
	public static String getTempDir() {
		String tmpDir = System.getProperty("jboss.server.temp.dir");
		if (tmpDir == null) {
			tmpDir = System.getProperty("java.io.tmpdir");
		}
		return tmpDir;
	}

}
