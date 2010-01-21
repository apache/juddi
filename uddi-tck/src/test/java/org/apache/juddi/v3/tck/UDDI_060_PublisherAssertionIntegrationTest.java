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
package org.apache.juddi.v3.tck;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.InVMTransport;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public class UDDI_060_PublisherAssertionIntegrationTest {
	
	private static Logger logger = Logger.getLogger(UDDI_060_PublisherAssertionIntegrationTest.class);
    
	private static TckTModel tckTModel                = null;
	private static TckBusiness tckBusiness            = null;
	private static TckPublisherAssertion tckAssertion = null;
	private static String authInfoJoe                 = null;
	private static String authInfoSam                 = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		String clazz = UDDIClientContainer.getDefaultTransportClass();
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.start();
		}
		logger.debug("Getting auth tokens..");
		try {
	         Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance("default");
	        	 
	        	 UDDISecurityPortType security = transport.getUDDISecurityService();
	        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID,  TckPublisher.JOE_PUBLISHER_CRED);
	        	 authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.SAM_SYNDICATOR_ID,  TckPublisher.SAM_SYNDICATOR_CRED);
	        	 Assert.assertNotNull(authInfoJoe);
	        	 Assert.assertNotNull(authInfoSam);
	        	 
	        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
	        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
	        	 
	        	 tckTModel  = new TckTModel(publication, inquiry);
	        	 tckBusiness = new TckBusiness(publication, inquiry);
	        	 tckAssertion = new TckPublisherAssertion(publication);
	         } else {
	        	 Assert.fail();
	         }
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
			 Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		String clazz = UDDIClientContainer.getDefaultTransportClass();
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.stop();
		}
	}
	
	@Test
	public void testJoepublisherToSamSyndicator() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
}
