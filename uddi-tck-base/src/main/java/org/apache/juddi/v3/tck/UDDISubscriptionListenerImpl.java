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

package org.apache.juddi.v3.tck;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

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
 * WebService which implements the UDDI v3 SubscriptionListener API. This service can be
 * brought during integration testing on the test side. The UDDI server can then
 * call in to it.
 * 
 * @author kstam
 *
 */
@WebService(serviceName="UDDISubscriptionListenerService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionListenerPortType",
			targetNamespace = "urn:uddi-org:v3_service")
public class UDDISubscriptionListenerImpl extends UnicastRemoteObject  implements
		UDDISubscriptionListenerPortType {
	
	private static final long serialVersionUID = -4621713293140278731L;
	private static Log logger = LogFactory.getLog(UDDISubscriptionListenerImpl.class);
	public static Integer notificationCount = 0;
	public static Map<Integer,String> notifcationMap = new HashMap<Integer,String>();
	
	public UDDISubscriptionListenerImpl() throws RemoteException  {
		super();
	}
	
	public UDDISubscriptionListenerImpl(int port) throws RemoteException {
		super(port);
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
			
			//Adding the received subscription XML to a Map.
			notifcationMap.put(notificationCount++, sw.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		DispositionReport dr = new DispositionReport();
		Result res = new Result();
		dr.getResult().add(res);
		return dr;
	}
	
}
