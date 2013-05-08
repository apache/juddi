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

import java.util.HashSet;
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
import org.junit.Test;
import org.uddi.api_v3.TModel;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class WSDL2UDDITest {

	PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
	ReadWSDL rw = new ReadWSDL();
	
	@Test
	public void testUDDIBindingModel() throws WSDLException, JAXBException, ConfigurationException {

		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("wsdl/HelloWorld.wsdl");
		String wsdlURL = wsdlDefinition.getDocumentBaseURI();
		
		Properties properties = new Properties();
		properties.put("keyDomain", "juddi.apache.org");
		WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
		Set<TModel> tModels = new HashSet<TModel>();
	    @SuppressWarnings("unchecked")
		Map<QName,PortType> portTypes = (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
	    Set<TModel> portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
	    tModels.addAll(portTypeTModels);
	    
		for (TModel tModel : tModels) {
			System.out.println("UDDI PortType TModel " + tModel.getName().getValue());
			System.out.println(pTModel.print(tModel));
		}
		Assert.assertEquals(1,tModels.size());
	}
	
	@Test
	public void testWSDLBindingModel() throws WSDLException, JAXBException, ConfigurationException {

		// Reading the WSDL
		Definition wsdlDefinition = rw.readWSDL("wsdl/HelloWorld.wsdl");
		String wsdlURL = wsdlDefinition.getDocumentBaseURI();
		
		Properties properties = new Properties();
		properties.put("keyDomain", "juddi.apache.org");
		WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
		Set<TModel> tModels = new HashSet<TModel>();
	    @SuppressWarnings("unchecked")
		Map<QName,Binding> bindings= (Map<QName,Binding>) wsdlDefinition.getAllBindings();
	    Set<TModel> bindingTModels = wsdl2UDDI.createWSDLBindingTModels(wsdlURL, bindings);
	    tModels.addAll(bindingTModels);
	    
		for (TModel tModel : tModels) {
			System.out.println("UDDI Binding TModel " + tModel.getName().getValue());
			System.out.println(pTModel.print(tModel));
		}
		Assert.assertEquals(1,tModels.size());
	}
	
}
