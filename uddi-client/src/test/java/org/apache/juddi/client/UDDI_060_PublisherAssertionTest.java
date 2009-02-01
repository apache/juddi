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

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.api_v3.tck.TckBusiness;
import org.uddi.api_v3.tck.TckPublisher;
import org.uddi.api_v3.tck.TckPublisherAssertion;
import org.uddi.api_v3.tck.TckSecurity;
import org.uddi.api_v3.tck.TckTModel;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public class UDDI_060_PublisherAssertionTest {
	
	private static Logger logger = Logger.getLogger(UDDI_060_PublisherAssertionTest.class);
    
	private static TckTModel tckTModel                = null;
	private static TckBusiness tckBusiness            = null;
	private static TckPublisherAssertion tckAssertion = null;
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
	
	@Test @Ignore
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
