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
package org.apache.juddi.xlt.util;

import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.api_v3.client.transport.TransportException;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

public class JUDDIServiceProvider {

	private static final JUDDIServiceProvider instance = new JUDDIServiceProvider();

	private Transport transporter;

	private UDDISecurityPortType securityService;

	private UDDIInquiryPortType inquiryService;

	private UDDIPublicationPortType publishService;
	
	private UDDISubscriptionPortType subscriptionService;

	private JUDDIServiceProvider() {
		try {
			String clazz = ClientConfig.getConfiguration().getString(
					Property.UDDI_PROXY_TRANSPORT,
					Property.DEFAULT_UDDI_PROXY_TRANSPORT);
			Class<?> transportClass = Loader.loadClass(clazz);

			if (transportClass != null) {
				transporter = (Transport) transportClass.newInstance();
			} else {
				throw new IllegalStateException("Could not set up transporter");
			}

			securityService = transporter.getUDDISecurityService();
			inquiryService = transporter.getUDDIInquiryService();
			publishService = transporter.getUDDIPublishService();
			subscriptionService = transporter.getUDDISubscriptionService();

			registerService((BindingProvider) securityService);
			registerService((BindingProvider) inquiryService);
			registerService((BindingProvider) publishService);
			registerService((BindingProvider) subscriptionService);

		} catch (ConfigurationException e) {
			throw new IllegalStateException("Failed to read configuration", e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Unable to load transporter class",
					e);
		} catch (InstantiationException e) {
			throw new IllegalStateException(
					"Unable to instantiate transporter class", e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(
					"Illegal access instantiating transporter class", e);
		} catch (TransportException e) {
			throw new IllegalStateException("Unable to retrieve service", e);
		}

	}

	public static UDDISecurityPortType getSecurityService()
			throws TransportException {
		return instance.securityService;
	}

	public static UDDIInquiryPortType getInquiryService()
			throws TransportException {
		return instance.inquiryService;
	}

	public static UDDIPublicationPortType getPublishService()
			throws TransportException {
		return instance.publishService;
	}
	
	public static UDDISubscriptionPortType getSubscriptionService()
			throws TransportException {
		return instance.subscriptionService;
}

	private void registerService(BindingProvider bindingProvider) {
		Binding binding = bindingProvider.getBinding();
		List<Handler> handlerChain = binding.getHandlerChain();

		handlerChain.add(new LoggingHandler());

		// set the handler chain again for the changes to take effect
		binding.setHandlerChain(handlerChain);
	}
}
