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
package org.uddi.api_v3.client.config;

public interface Property 
{
	public final static String UDDI_CONFIGURATION_RELOAD_DELAY  ="uddi.configuration.reload.delay";
	public final static String UDDI_INQUIRY_URL                 ="uddi.inquiry.url";
	public final static String UDDI_SECURITY_URL                ="uddi.security.url";
	public final static String UDDI_PUBLISH_URL                 ="uddi.publish.url";
	public final static String UDDI_SUBSCRIPTION_URL            ="uddi.subscription.url";
	public final static String UDDI_SUBSCRIPTION_LISTENER_URL	="uddi.subscription.listener.url";
	public final static String UDDI_PROXY_TRANSPORT             ="uddi.proxy.transport";
	public final static String UDDI_CUSTODY_TRANSFER_URL       ="uddi.custodytransfer.url";
	
	public final static String UDDI_PROXY_FACTORY_INITIAL       ="uddi.proxy.factory.initial";
	public final static String UDDI_PROXY_PROVIDER_URL          ="uddi.proxy.provider.url";
	public final static String UDDI_PROXY_FACTORY_URL_PKS       ="uddi.proxy.factory.url.pkg";

	public final static String DEFAULT_UDDI_PROXY_TRANSPORT     ="org.uddi.api_v3.client.transport.JAXWSTransport";
}
