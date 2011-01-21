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
package org.apache.juddi.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * UDDISecurityPortType wrapper so it can be exposed as a service over RMI.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class UDDISubscriptionService extends UnicastRemoteObject implements UDDISubscriptionPortType {

	private static final long serialVersionUID = 3534214581063707293L;
	private UDDISubscriptionPortType subscription = new UDDISubscriptionImpl();
	
	protected UDDISubscriptionService(int port) throws RemoteException {
		super(port);
	}

	public void deleteSubscription(DeleteSubscription body)
			throws DispositionReportFaultMessage, RemoteException {
		subscription.deleteSubscription(body);
	}

	public SubscriptionResultsList getSubscriptionResults(
			GetSubscriptionResults body) throws DispositionReportFaultMessage,
			RemoteException {
		return subscription.getSubscriptionResults(body);
	}

	public List<Subscription> getSubscriptions(String authInfo)
			throws DispositionReportFaultMessage, RemoteException {
		return subscription.getSubscriptions(authInfo);
	}

	public void saveSubscription(String authInfo,
			Holder<List<Subscription>> subscription)
			throws DispositionReportFaultMessage, RemoteException {
		this.subscription.saveSubscription(authInfo, subscription);
		
	}

}
