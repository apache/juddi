package org.apache.juddi.v3.tck;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.UDDIServiceWSDL;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDINode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDIReplicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * Checking the UDDI Server for all the required ports specified in the UDDI v3 spec.
 * @author kstam
 *
 */
public class UDDI_001_UDDIServiceTest {
	
	static UDDIClerkManager manager;
	static UDDINode uddiServer;
	
	@BeforeClass
	public static void readClientConfig() throws ConfigurationException {
		manager = new UDDIClerkManager();
		manager.start();
		uddiServer = manager.getClientConfig().getHomeNode();
	}
	
	@AfterClass
	public static void stop() throws ConfigurationException {
		if (manager!=null) manager.stop();
	}
	
	@Test
	public void findSecurityPort() throws IOException {
		String url = uddiServer.getSecurityUrl();
		UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
		URL tmpWSDLFile = uddiServiceWSDL.getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.SECURITY, url);
		System.out.println("WSDL File: " + tmpWSDLFile);
		UDDIService uddiService = new UDDIService(tmpWSDLFile);
		UDDISecurityPortType port = uddiService.getUDDISecurityPort();
	    assertNotNull(port);
	    GetAuthToken body = new GetAuthToken();
	    body.setUserID("root");
	    body.setCred("root");
	    AuthToken token = port.getAuthToken(body);
	    System.out.println("token=" + token);
	}
	
	@Test
	public void findInquiryPort() throws IOException {
		UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
		String url = uddiServer.getSecurityUrl();
		URL tmpWSDLFile = uddiServiceWSDL.getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.INQUIRY, url);
		UDDIService uddiService = new UDDIService(tmpWSDLFile);
		UDDIInquiryPortType port = uddiService.getUDDIInquiryPort();
	    assertNotNull(port);
	}
	
	@Test
	public void findPublicationPort() throws IOException {
		UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
		String url = uddiServer.getSecurityUrl();
		URL tmpWSDLFile = uddiServiceWSDL.getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.PUBLISH, url);
		UDDIService uddiService = new UDDIService(tmpWSDLFile);
		UDDIPublicationPortType port = uddiService.getUDDIPublicationPort();
	    assertNotNull(port);
	}
	
	@Test
	public void findSubscriptionPort() throws IOException {
		UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
		String url = uddiServer.getSecurityUrl();
		URL tmpWSDLFile = uddiServiceWSDL.getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.SUBSCRIPTION, url);
		UDDIService uddiService = new UDDIService(tmpWSDLFile);
		UDDISubscriptionPortType port = uddiService.getUDDISubscriptionPort();
	    assertNotNull(port);
	}
	
	@Test
	public void findReplicationPort() throws IOException {
		String url = uddiServer.getSecurityUrl();
		if (url!=null) { //Replication is option, but if it is configured in the uddi.xml then we assume it is implemented
			UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
			URL tmpWSDLFile = uddiServiceWSDL.getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.REPLICATION,url);
			UDDIService uddiService = new UDDIService(tmpWSDLFile);
			UDDIReplicationPortType port = uddiService.getUDDIReplicationPort();
		    assertNotNull(port);
		}
	}
	
	@Test
	public void findSubscriptionListenerPort() throws IOException {
		String url = uddiServer.getSecurityUrl();
		if (url!=null) { //Subscription Listener is client side, but if it is configured in the uddi.xml then we assume it is implemented
			UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
			URL tmpWSDLFile = uddiServiceWSDL.getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.SUBSCRIPTION_LISTENER, url);
			UDDIService uddiService = new UDDIService(tmpWSDLFile);
			UDDISubscriptionListenerPortType port = uddiService.getUDDISubscriptionListenerPort();
		    assertNotNull(port);
		}
	}
	
	
	
}
