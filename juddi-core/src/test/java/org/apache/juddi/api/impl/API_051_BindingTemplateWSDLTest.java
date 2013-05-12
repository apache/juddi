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
package org.apache.juddi.api.impl;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.mapping.ReadWSDL;
import org.apache.juddi.v3.client.mapping.URLLocalizer;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WSDL2UDDI;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindTModel;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_051_BindingTemplateWSDLTest 
{
	private static Log logger                             = LogFactory.getLog(API_051_BindingTemplateWSDLTest.class);
	
	private static API_010_PublisherTest api010           = new API_010_PublisherTest();
	private static TckTModel tckTModel                    = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness                = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	
	private static String authInfoJoe                 = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		Registry.start();
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
			UDDISecurityPortType security      = new UDDISecurityImpl();
			String authInfoUDDI  = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(),  TckPublisher.getUDDIPassword());
			tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
			tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}

	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		Registry.stop();
	}
	
	@Test 
	public void testDirectCall() throws ConfigurationException, WSDLException, RemoteException, TransportException, MalformedURLException {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			
			UDDIClerk clerk = new UDDIClient("META-INF/uddi.xml").getClerk("joe");
			Properties properties = clerk.getUDDINode().getProperties();
			URLLocalizer urlLocalizer = new URLLocalizerDefaultImpl();
			
			//get the wsdl
			Definition wsdlDefinition = new ReadWSDL().readWSDL("wsdl/sample.wsdl");
			//set required properties
			properties.put("keyDomain", "uddi.joepublisher.com");
			properties.put(org.apache.juddi.v3.client.config.Property.BUSINESS_KEY, "uddi:uddi.joepublisher.com:businessone");
			properties.put("nodeName", "api.example.org_80");
			WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(clerk, urlLocalizer, properties);
			//register all services in the wsdl
			wsdl2UDDI.registerBusinessServices(wsdlDefinition);
			//done
			
			String portTypeName = "StockQuotePortType";
			String namespace    = "http://example.com/stockquote/";
			FindTModel findTModelForPortType = WSDL2UDDI.createFindPortTypeTModelForPortType(portTypeName, namespace);
			System.out.println(new PrintUDDI<FindTModel>().print(findTModelForPortType));
			
			wsdl2UDDI.unRegisterBusinessServices(wsdlDefinition);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	@Test 
	public void testClerkCall() throws ConfigurationException, WSDLException, RemoteException, TransportException, MalformedURLException {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			
			UDDIClerk clerk = new UDDIClient("META-INF/uddi.xml").getClerk("joe");
			clerk.registerWsdls();
			
			
			String portTypeName = "StockQuotePortType";
			String namespace    = "http://example.com/stockquote/";
			FindTModel findTModelForPortType = WSDL2UDDI.createFindPortTypeTModelForPortType(portTypeName, namespace);
			System.out.println(new PrintUDDI<FindTModel>().print(findTModelForPortType));
			
			clerk.unRegisterWsdls();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	
}
