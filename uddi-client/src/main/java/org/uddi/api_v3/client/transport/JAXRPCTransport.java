package org.uddi.api_v3.client.transport;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


public class JAXRPCTransport implements Transport {

	UDDIInquiryPortType inquiryService = null;
	UDDISecurityPortType securityService = null;
	UDDIPublicationPortType publishService = null;
	UDDISubscriptionPortType subscriptionService = null;

	public UDDIInquiryPortType getInquiryService() throws TransportException {

		if (inquiryService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_INQUIRY_URL);
				QName qName = new QName(Transport.API_V3_NAMESPACE, Transport.INQUIRY_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				inquiryService = (UDDIInquiryPortType) service.getPort(UDDIInquiryPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return inquiryService;
	}
	
	public UDDISecurityPortType getSecurityService() throws TransportException {

		if (securityService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SECURITY_URL);
				QName qName = new QName(Transport.API_V3_NAMESPACE, Transport.SECURITY_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				securityService = (UDDISecurityPortType) service.getPort(UDDISecurityPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return securityService;
	}
	
	public UDDIPublicationPortType getPublishService() throws TransportException {

		if (publishService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_PUBLISH_URL);
				QName qName = new QName(Transport.API_V3_NAMESPACE, Transport.PUBLISH_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				publishService = (UDDIPublicationPortType) service.getPort(UDDIPublicationPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return publishService;
	}
	
	public UDDISubscriptionPortType getSubscriptionService() throws TransportException {

		if (subscriptionService==null) {
			try {
				String endpointURL = ClientConfig.getConfiguration().getString(Property.UDDI_SUBSCRIPTION_URL);
				QName qName = new QName(Transport.SUB_V3_NAMESPACE, Transport.SUBSCRIPTION_SERVICE);
				Service service = Service.create(new URL(endpointURL), qName);
				subscriptionService = (UDDISubscriptionPortType) service.getPort(UDDISubscriptionPortType.class);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(), e);
			}
		}
		return subscriptionService;
	}

}
