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
package org.apache.juddi.client;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.api_v3.tck.TckPublisher;
import org.uddi.api_v3.tck.TckSecurity;
import org.uddi.api_v3.tck.TckTModel;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_020_TmodelTest {
	
	private static TckTModel tckTModel                = null;
	private static Logger logger                      = Logger.getLogger(UDDI_020_TmodelTest.class);
	
	private static String authInfoJoe                 = null;
	private static String authInfoSam                 = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting auth tokens..");
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.newInstance();
	        	 
	        	 UDDISecurityPortType security = transport.getSecurityService();
	        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID,  TckPublisher.JOE_PUBLISHER_CRED);
	 			 authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.SAM_SYNDICATOR_ID,  TckPublisher.SAM_SYNDICATOR_CRED);
	        	 Assert.assertNotNull(authInfoJoe);
	        	 Assert.assertNotNull(authInfoSam);
	        	 
	        	 UDDIPublicationPortType publication = transport.getPublishService();
	        	 UDDIInquiryPortType inquiry = transport.getInquiryService();
	        	 tckTModel  = new TckTModel(publication, inquiry);
	         } else {
	        	 Assert.fail();
	         }
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test
	public void testJoePublisherTmodel() {
		tckTModel.saveJoePublisherTmodel(authInfoJoe);
		tckTModel.deleteJoePublisherTmodel(authInfoJoe);
	}
	
	@Test
	public void testSamSyndicatorTmodelTest() {
		tckTModel.saveSamSyndicatorTmodel(authInfoSam);
		tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
	}	
	
}
