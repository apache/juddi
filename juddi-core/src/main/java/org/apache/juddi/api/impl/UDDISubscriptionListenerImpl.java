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

package org.apache.juddi.api.impl;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.validation.ValidateSubscriptionListener;
import org.apache.juddi.util.NotificationList;
import org.apache.log4j.Logger;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

@WebService(serviceName="UDDISubscriptionListenerService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionListenerPortType",
			targetNamespace = "urn:uddi-org:subr_v3_portType")
public class UDDISubscriptionListenerImpl implements
		UDDISubscriptionListenerPortType {
	
	private static Logger logger = Logger.getLogger(UDDISubscriptionListenerImpl.class);
		
	public DispositionReport notifySubscriptionListener(
			NotifySubscriptionListener body)
			throws DispositionReportFaultMessage {
		try {
			JAXBContext context = JAXBContext.newInstance(body.getClass());
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();
			marshaller.marshal(body, sw);

			logger.info("Notification received by UDDISubscriptionListenerService : " + sw.toString());
			
			NotificationList nl = NotificationList.getInstance();
			nl.getNotifications().add(sw.toString());
			
			logger.info("Notification received by UDDISubscriptionListenerService : " 
					+ sw.toString());
		} catch (JAXBException jaxbe) {
			logger.error("", jaxbe);
			throw new FatalErrorException(new ErrorMessage("errors.subscriptionnotifier.client"));
		}	
		
		new ValidateSubscriptionListener().validateNotification(body);
			
		DispositionReport dr = new DispositionReport();
		Result res = new Result();
		dr.getResult().add(res);
		return dr;
	}
}
