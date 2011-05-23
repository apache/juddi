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

package org.apache.juddi.v3.client.mapping;

import java.io.StringWriter;

import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

/**
 * WebService which implements the UDDI v3 SubscriptionListener API. This service will be called by
 * the UDDI registry when any change to a Service or BindingTemplate
 * call in to it.
 * 
 * @author kstam
 *
 */
@WebService(serviceName="UDDIClientSubscriptionListenerService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionListenerPortType",
			targetNamespace = "urn:uddi-org:v3_service")
public class UDDIClientSubscriptionListenerImpl implements UDDISubscriptionListenerPortType {
	
	private static final long serialVersionUID = 100157393329807903L;
	private static Log logger = LogFactory.getLog(UDDIClientSubscriptionListenerImpl.class);
	private UDDIServiceCache serviceCache;
	
	public UDDIClientSubscriptionListenerImpl(UDDIServiceCache serviceCache) {
		super();
		this.serviceCache = serviceCache;
	}
	
	public DispositionReport notifySubscriptionListener(
			NotifySubscriptionListener body)
			throws DispositionReportFaultMessage 
	{
		try {
			JAXBContext context = JAXBContext.newInstance(body.getClass());
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();
			marshaller.marshal(body, sw);
			logger.info("Notification received by UDDISubscriptionListenerService : " + sw.toString());
			
			//Update the current subscription
			serviceCache.registerSubscription();
			//reset the cache
			serviceCache.removeAll();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		DispositionReport dr = new DispositionReport();
		Result res = new Result();
		dr.getResult().add(res);
		return dr;
	}
	
}
