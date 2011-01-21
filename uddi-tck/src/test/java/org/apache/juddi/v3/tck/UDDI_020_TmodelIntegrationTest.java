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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_020_TmodelIntegrationTest {
	
	private static TckTModel tckTModel                = null;
	private static Logger logger                      = Logger.getLogger(UDDI_020_TmodelIntegrationTest.class);
	
	private static String authInfoJoe                 = null;
	private static String authInfoSam                 = null;
	
private static UDDIClerkManager manager;
	
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		
		manager  = new UDDIClerkManager();
		manager.start();
		logger.debug("Getting auth tokens..");
		try {
			 Transport transport = manager.getTransport();
	        	 
        	 UDDISecurityPortType security = transport.getUDDISecurityService();
        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
 			 authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(),  TckPublisher.getSamPassword());
        	 Assert.assertNotNull(authInfoJoe);
        	 Assert.assertNotNull(authInfoSam);
        	 
        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
        	 tckTModel  = new TckTModel(publication, inquiry);
	        
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@AfterClass
	public static void stopManager() throws ConfigurationException {
		manager.stop();
	}
	
	@Test
	public void testJoePublisherTmodel() {
		tckTModel.saveJoePublisherTmodel(authInfoJoe);
		
		//Now if we use a finder it should be found.
		TModelList tModelList = tckTModel.findJoeTModelDetail();
		Assert.assertNotNull(tModelList.getTModelInfos());
		
		tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		
		//Even if it deleted you should still be able to access it through a getTModelDetail
		TModelDetail detail = tckTModel.getJoePublisherTmodel(authInfoJoe);
		Assert.assertNotNull(detail.getTModel());
		
		//However if we use a finder it should not be found.
		TModelList tModelList2 = tckTModel.findJoeTModelDetail();
		Assert.assertNull(tModelList2.getTModelInfos());
		
		//Make sure none of the found key generators is Joe's key generator
		TModelList tModelList3 = tckTModel.findJoeTModelDetailByCategoryBag();
		for (TModelInfo tModelInfo : tModelList3.getTModelInfos().getTModelInfo()) {
			Assert.assertFalse("uddi:uddi.joepublisher.com:keygenerator".equals(tModelInfo.getTModelKey()));
		}
	}
	
	@Test
	public void testSamSyndicatorTmodelTest() {
		tckTModel.saveSamSyndicatorTmodel(authInfoSam);
		tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
	}	
	
}
