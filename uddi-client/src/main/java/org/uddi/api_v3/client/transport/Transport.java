package org.uddi.api_v3.client.transport;

import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public interface Transport {
	public final static String API_V3_NAMESPACE = "urn:uddi-org:api_v3_portType";
	public final static String INQUIRY_SERVICE  = "InquiryService";
	public final static String SECURITY_SERVICE = "SecurityService";
	
	UDDIInquiryPortType getInquiryService() throws TransportException;
	UDDISecurityPortType getSecurityService() throws TransportException;
}
