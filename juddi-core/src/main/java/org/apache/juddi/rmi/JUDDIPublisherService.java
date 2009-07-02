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

import org.apache.juddi.api.impl.JUDDIPublisherImpl;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.v3_service.JUDDIPublisherPortType;
import org.uddi.v3_service.DispositionReportFaultMessage;
/**
 * UDDIPublicationPortType wrapper so it can be exposed as a service over RMI.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class JUDDIPublisherService extends UnicastRemoteObject implements JUDDIPublisherPortType {

	private static final long serialVersionUID = 1L;
	private JUDDIPublisherPortType publisher = new JUDDIPublisherImpl();
	
	protected JUDDIPublisherService() throws RemoteException {
		super();
	}

	public void deletePublisher(DeletePublisher body)
			throws DispositionReportFaultMessage, RemoteException {
		publisher.deletePublisher(body);
	}

	public PublisherDetail getAllPublisherDetail(GetAllPublisherDetail body)
			throws DispositionReportFaultMessage, RemoteException {
		return publisher.getAllPublisherDetail(body);
	}

	public PublisherDetail getPublisherDetail(GetPublisherDetail body)
			throws DispositionReportFaultMessage, RemoteException {
		return publisher.getPublisherDetail(body);
	}

	public PublisherDetail savePublisher(SavePublisher body)
			throws DispositionReportFaultMessage, RemoteException {
		return publisher.savePublisher(body);
	}


}
