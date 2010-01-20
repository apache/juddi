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
package org.apache.juddi.v3.client.transport;

import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;;

public abstract class Transport {
	
	public final static String DEFAULT_NODE_NAME             = "default";
	
	public abstract UDDIInquiryPortType getUDDIInquiryService(String enpointURL)           throws TransportException;
	public abstract UDDISecurityPortType getUDDISecurityService(String enpointURL)         throws TransportException;
	public abstract UDDIPublicationPortType getUDDIPublishService(String enpointURL)       throws TransportException;
	public abstract UDDISubscriptionPortType getUDDISubscriptionService(String enpointURL) throws TransportException;
	public abstract UDDICustodyTransferPortType getUDDICustodyTransferService(String enpointURL) throws TransportException;
	public abstract UDDISubscriptionListenerPortType getUDDISubscriptionListenerService(String enpointURL) throws TransportException;
	public abstract JUDDIApiPortType getJUDDIApiService(String enpointURL) throws TransportException;
	
	public UDDIInquiryPortType getUDDIInquiryService() throws TransportException {
		return getUDDIInquiryService(null);
	}
	public UDDISecurityPortType getUDDISecurityService() throws TransportException {
		return getUDDISecurityService(null);
	}
	public UDDIPublicationPortType getUDDIPublishService() throws TransportException {
		return getUDDIPublishService(null);
	}
	public UDDISubscriptionPortType getUDDISubscriptionService() throws TransportException {
		return getUDDISubscriptionService(null);
	}
	public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService() throws TransportException {
		return getUDDISubscriptionListenerService(null);
	}
	public UDDICustodyTransferPortType getUDDICustodyTransferService() throws TransportException {
		return getUDDICustodyTransferService(null);
	}
	public JUDDIApiPortType getJUDDIApiService() throws TransportException {
		return getJUDDIApiService(null);
	}
}
