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

public interface Transport {
	
	public final static String API_V3_NAMESPACE              = "urn:uddi-org:api_v3_portType";
	public final static String SUB_V3_NAMESPACE              = "urn:uddi-org:sub_v3_portType";
	public final static String SUBR_V3_NAMESPACE	         = "urn:uddi-org:subr_v3_portType";
	public final static String CUSTODY_V3_NAMESPACE          = "urn:uddi-org:custody_v3_portType";
	public final static String JUDDI_API_V3_NAMESPACE        = "urn:juddi-apache-org:api_v3_portType";
	public final static String DEFAULT_NODE_NAME             = "default";
	public final static String DEFAULT_MANAGER_NAME          = "default-manager";
	
	public final static String INQUIRY_SERVICE               = "UDDIInquiryService";
	public final static String SECURITY_SERVICE              = "UDDISecurityService";
	public final static String PUBLISH_SERVICE               = "UDDIPublishService";
	public final static String SUBSCRIPTION_SERVICE          = "UDDISubscriptionService";
	public final static String SUBSCRIPTION_LISTENER_SERVICE = "UDDISubscriptionListenerService";
	public final static String CUSTODY_TRANSFER_SERVICE      = "UDDICustodyTransferService";
	public final static String PUBLISHER_SERVICE             = "JUDDIApiService";
	
	UDDIInquiryPortType getUDDIInquiryService()           throws TransportException;
	UDDISecurityPortType getUDDISecurityService()         throws TransportException;
	UDDIPublicationPortType getUDDIPublishService()       throws TransportException;
	UDDISubscriptionPortType getUDDISubscriptionService() throws TransportException;
	UDDICustodyTransferPortType getUDDICustodyTransferService() throws TransportException;
	UDDISubscriptionListenerPortType getUDDISubscriptionListenerService() throws TransportException;
	JUDDIApiPortType getJUDDIApiService() throws TransportException;
	
}
