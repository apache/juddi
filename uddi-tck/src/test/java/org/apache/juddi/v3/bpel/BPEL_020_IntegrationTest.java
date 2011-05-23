/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.bpel;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.mapping.BPEL2UDDI;
import org.apache.juddi.v3.client.mapping.ReadWSDL;
import org.apache.juddi.v3.client.mapping.URLLocalizerImpl;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class BPEL_020_IntegrationTest {
private static Log logger = LogFactory.getLog(BPEL_010_IntegrationTest.class);
	
	private static TckTModel tckTModel           = null;
	private static TckBusinessService tckService = null;
	private static TckBusiness tckBusiness       = null;
	private static String authInfoRiftSaw        = null;
	private static UDDIClerkManager manager;
	static ReadWSDL rw;
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		
		manager  = new UDDIClerkManager();
		manager.start();
		
		logger.debug("Getting auth token for user riftsaw/riftsaw..");
		try {
			 Transport transport = manager.getTransport();
	        	 
        	 UDDISecurityPortType security = transport.getUDDISecurityService();
        	 authInfoRiftSaw = TckSecurity.getAuthToken(security, 
        			 TckPublisher.getRiftSawPublisherId(),  TckPublisher.getRiftSawPassword());
        	 Assert.assertNotNull(authInfoRiftSaw);
        	
        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
        	 tckTModel  = new TckTModel(publication, inquiry);
        	 tckService = new TckBusinessService(publication, inquiry);
        	 tckBusiness= new TckBusiness(publication, inquiry);
        	
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	     rw = new ReadWSDL();
	}
	
	@Before //jUDDI only to add the keygenerator and business
	public void saveRiftSawKeyGenerator() {
		tckTModel.saveTModel(authInfoRiftSaw, TckTModel.RIFTSAW_PUBLISHER_TMODEL_XML, TckTModel.RIFTSAW_PUBLISHER_TMODEL_KEY);
		tckBusiness.saveBusiness(authInfoRiftSaw, TckBusiness.RIFTSAW_BUSINESS_XML, TckBusiness.RIFTSAW_BUSINESS_KEY);
	}
	
	@Test
	public void parseWSDL_PortTypeTModels() throws WSDLException  {
		
	    Definition wsdlDefinition = rw.readWSDL("uddi_data/bpel/riftsaw/bpel-technote.wsdl");
		@SuppressWarnings("unchecked")
		Map<QName,PortType> portTypes = (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
		String ns = wsdlDefinition.getTargetNamespace();
		System.out.println("Namespace: " + ns);
		
		int i=0;
	    for (QName qName : portTypes.keySet()) {
	    	String nsp = qName.getNamespaceURI();
	    	String localpart = qName.getLocalPart();
	    	System.out.println("Namespace: " + nsp);
	    	System.out.println("LocalPart: " + localpart);
	    	if (i++==0) Assert.assertEquals("InterfaceOfTravelAgent", localpart);
	    	else Assert.assertEquals("InterfaceOfCustomer", localpart);
		}
	}
	
	@Test
	public void registerBPELProcess() throws WSDLException, ConfigurationException,
		MalformedURLException, RemoteException, TransportException  {
		
		UDDIClerk clerk = new UDDIClerk();
		clerk.setManagerName(manager.getName());
		clerk.setName("testClerk");
		clerk.setPublisher(TckPublisher.getRiftSawPublisherId());
		clerk.setPassword(TckPublisher.getRiftSawPassword());
		
		clerk.setUDDINode(manager.getClientConfig().getHomeNode());
		
		Properties properties = manager.getClientConfig().getHomeNode().getProperties();
		properties.put("keyDomain", "riftsaw.jboss.org");
		properties.put("nodeName", "localhost");
		properties.put("businessName", "redhat-jboss");
		BPEL2UDDI bpel2UDDI = new BPEL2UDDI(clerk, new URLLocalizerImpl(), properties);
		
	    Definition wsdlDefinition = rw.readWSDL("uddi_data/bpel/riftsaw/HelloWorld.wsdl");
		QName serviceName = new QName("http://www.jboss.org/bpel/examples/wsdl","HelloService");
		String portName = "HelloPort";
		URL serviceUrl = new URL("http://localhost:8080/helloworld");
		bpel2UDDI.register(serviceName, portName, serviceUrl, wsdlDefinition);
		
		System.out.println("DONE");
		
		bpel2UDDI.unRegister(serviceName, portName, serviceUrl);
	}
	
}
