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
package org.apache.juddi.v3.client.mapping;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.jaxb.PrintUDDI;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.TModel;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class BPEL2UDDITest {

	
	static BPEL2UDDI bpel2UDDI;
	PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
	PrintUDDI<FindTModel> pFindTModel = new PrintUDDI<FindTModel>();
	PrintUDDI<BindingTemplate> pBinding = new PrintUDDI<BindingTemplate>();
	ReadWSDL rw = new ReadWSDL();
	
	@BeforeClass
	public static void before() throws JAXBException, ConfigurationException {
		Properties properties = new Properties();
		properties.put("keyDomain", "juddi.apache.org");
		properties.put("nodeName", "localhost");
		URLLocalizer urlLocalizer = new URLLocalizerDefaultImpl();
		
		bpel2UDDI = new BPEL2UDDI(null, urlLocalizer, properties);
	}
	
	@Test
	public void testTN_WSDLPortTypeModels() throws WSDLException, JAXBException, Exception {

		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("bpel/bpel-technote.wsdl");
	    @SuppressWarnings("unchecked")
		Map<QName,PortType> portTypes= (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
	    Set<TModel> portTypeTModels = bpel2UDDI.createWSDLPortTypeTModels(wsdlDefinition.getDocumentBaseURI(), portTypes); 
	    for (TModel tModel : portTypeTModels) {
			System.out.println("***** UDDI PortType TModel: " + tModel.getName().getValue());
			System.out.println(pTModel.print(tModel));
		}
		Assert.assertEquals(2,portTypeTModels.size());
	}
	
	@Test
	public void testTN_BPEL4WSProcessTModel() throws WSDLException, JAXBException, Exception {

		// Obtained from the .bpel file:
		String targetNamespace = "http://example.com/travelagent";
		QName serviceName = new QName (targetNamespace, "ReservationAndBookingTickets");
		String bpelOverViewUrl = "http://localhost/registry/" + serviceName.getLocalPart() + ".bpel";
		
		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("bpel/bpel-technote.wsdl");
		
	    @SuppressWarnings("unchecked")
		Map<QName,PortType> portTypes= (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
	    TModel bpel4WSTModel = bpel2UDDI.createBPEL4WSProcessTModel(serviceName, targetNamespace, portTypes, bpelOverViewUrl);
	    
		System.out.println("***** BPEL4WS Process TModel: " + bpel4WSTModel.getName().getValue());
		System.out.println(pTModel.print(bpel4WSTModel));
		
		Assert.assertNotNull(bpel4WSTModel);
	}
	
	@Test
	public void testTN_FindTModelForProcessName() throws JAXBException {
		
		QName processName = new QName("http://example.com/travelagent","ReservationAndBookingTickets");
	
		FindTModel findTModel = bpel2UDDI.createFindTModelForProcessName(processName);
		
		System.out.println("***** Find TModel For ProcessName: " + processName);
		System.out.println(pFindTModel.print(findTModel));
		
		Assert.assertNotNull(findTModel.getCategoryBag());
	}
	
	@Test
	public void testHelloWorld_WSDLPortTypeModels() throws WSDLException, JAXBException , Exception{

		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("bpel/HelloWorld.wsdl");
	    @SuppressWarnings("unchecked")
		Map<QName,PortType> portTypes= (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
	    Set<TModel> portTypeTModels = bpel2UDDI.createWSDLPortTypeTModels(wsdlDefinition.getDocumentBaseURI(), portTypes);
	    
		for (TModel tModel : portTypeTModels) {
			System.out.println("***** UDDI PortType TModel: " + tModel.getName().getValue());
			System.out.println(pTModel.print(tModel));
		}
		Assert.assertEquals(1,portTypeTModels.size());
	}
	
	@Test
	public void testHelloWorld_UDDIBindingModel() throws WSDLException, JAXBException, Exception {

		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("bpel/HelloWorld.wsdl");
	    @SuppressWarnings("unchecked")
		Map<QName,Binding> bindings = (Map<QName,Binding>) wsdlDefinition.getAllBindings();
	    Set<TModel> bindingTModels = bpel2UDDI.createWSDLBindingTModels(wsdlDefinition.getDocumentBaseURI(), bindings);
	    
		for (TModel tModel : bindingTModels) {
			System.out.println("***** UDDI Binding TModel: " + tModel.getName().getValue());
			System.out.println(pTModel.print(tModel));
		}
		Assert.assertEquals(1,bindingTModels.size());
	}
	
	@Test
	public void testHelloWorld_BPEL4WSProcessTModel() throws WSDLException, JAXBException , Exception{

		//Obtained from the .bpel file:
		String targetNamespace = "http://www.jboss.org/bpel/examples";
		QName serviceName = new QName (targetNamespace, "HelloWorld");
		String bpelOverViewUrl = "http://localhost/registry/" + serviceName.getLocalPart() + ".bpel";
		
		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("bpel/HelloWorld.wsdl");
		
	    @SuppressWarnings("unchecked")
		Map<QName,PortType> portTypes= (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
	    TModel bpel4WSTModel = bpel2UDDI.createBPEL4WSProcessTModel(serviceName, targetNamespace, portTypes, bpelOverViewUrl);
	    
		System.out.println("***** BPEL4WS Process TModel: " + bpel4WSTModel.getName().getValue());
		System.out.println(pTModel.print(bpel4WSTModel));
		
		Assert.assertNotNull(bpel4WSTModel);
	}
	
	@Test
	public void testHelloWorld_BPELBinding() throws WSDLException, JAXBException, MalformedURLException, Exception {

		//Obtained from the .bpel file:
		String portName = "HelloPort";
		QName serviceName = new QName ("http://www.jboss.org/bpel/examples/wsdl", "HelloService");
		URL serviceUrl =  new URL("http://localhost/sample");
		
		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("bpel/HelloWorld.wsdl");
	    BindingTemplate binding = bpel2UDDI.createBPELBinding(serviceName, portName, serviceUrl, wsdlDefinition);
	    
		System.out.println("***** WSDL Port BindingTemplate: " + binding.getBindingKey());
		System.out.println(pBinding.print(binding));
		
		Assert.assertNotNull(binding.getTModelInstanceDetails());
	}
	
	@Test
	public void testHelloWorld_FindTModelForProcessName() throws JAXBException {
		
		QName processName = new QName("http://www.jboss.org/bpel/examples/wsdl","HelloWorld");
		 
		FindTModel findTModel = bpel2UDDI.createFindTModelForProcessName(processName);
		
		System.out.println("***** Find TModel For ProcessName: " + processName);
		System.out.println(pFindTModel.print(findTModel));
		
		Assert.assertNotNull(findTModel.getCategoryBag());
	}
}
