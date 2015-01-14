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
package org.apache.juddi.subscription.notify;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.model.BindingTemplate;
import org.uddi.api_v3.DispositionReport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

public class RMINotifier implements Notifier {
	
	Log log = LogFactory.getLog(this.getClass());
	UDDISubscriptionListenerPortType  subscriptionListenerPort = null;
	
	public RMINotifier(BindingTemplate bindingTemplate) throws IOException, URISyntaxException, NotBoundException {
		super();
		if (!AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
			log.error("rmi enpoints only support AccessPointType " + AccessPointType.END_POINT);
		}
		String accessPointUrl = bindingTemplate.getAccessPointUrl().toLowerCase();
		if (!accessPointUrl.startsWith("rmi")) {
			log.warn("rmi accessPointUrl for bindingTemplate " + bindingTemplate.getEntityKey() + 
					" should start with 'rmi'");
		}
		URI accessPointURI = new URI(accessPointUrl);
		String host = accessPointURI.getHost();
		int port = accessPointURI.getPort();
		String path = accessPointURI.getPath();
		log.debug("Connecting to " + host + ":" + port);
		Registry registry = LocateRegistry.getRegistry(host, port);
		subscriptionListenerPort = (UDDISubscriptionListenerPortType) registry.lookup(path);
	}

	public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
		return subscriptionListenerPort.notifySubscriptionListener(body);
	}
}
