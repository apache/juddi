package org.uddi.api_v3.client.transport;

import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;;

public interface Transport {
	public final static String API_V3_NAMESPACE     = "urn:uddi-org:api_v3_portType";
	public final static String SUB_V3_NAMESPACE     = "urn:uddi-org:sub_v3_portType";
	public final static String INQUIRY_SERVICE      = "InquiryService";
	public final static String SECURITY_SERVICE     = "SecurityService";
	public final static String PUBLISH_SERVICE      = "PublishService";
	public final static String SUBSCRIPTION_SERVICE = "SubscriptionService";
	
	UDDIInquiryPortType getInquiryService()           throws TransportException;
	UDDISecurityPortType getSecurityService()         throws TransportException;
	UDDIPublicationPortType getPublishService()       throws TransportException;
	UDDISubscriptionPortType getSubscriptionService() throws TransportException;
}
