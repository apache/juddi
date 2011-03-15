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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public class UDDI_060_PublisherAssertionIntegrationTest {
	
	private static Log logger = LogFactory.getLog(UDDI_060_PublisherAssertionIntegrationTest.class);
    
	private static TckTModel tckTModel                = null;
	private static TckBusiness tckBusiness            = null;
	private static TckPublisherAssertion tckAssertion = null;
	private static TckFindEntity tckFindEntity        = null;
	private static String authInfoJoe                 = null;
	private static String authInfoSam                 = null;
	private static String authInfoMary                = null;
	
    private static UDDIClerkManager manager;
	
	@AfterClass
	public static void stopManager() throws ConfigurationException {
		manager.stop();
	}
	
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
        	 authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(),  TckPublisher.getMaryPassword());
        	 Assert.assertNotNull(authInfoJoe);
        	 
        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
        	 tckTModel  = new TckTModel(publication, inquiry);
        	 tckBusiness = new TckBusiness(publication, inquiry);
        	 tckAssertion = new TckPublisherAssertion(publication);
        	 tckFindEntity = new TckFindEntity(inquiry);
        	  
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
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
	
	/**
	 * This test should find no publisher assertions because we only save them
	 * from the joe publisher side.
	 */
	@Test
	public void testFindNoAssertions() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckTModel.saveMaryPublisherTmodel(authInfoMary);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusiness.saveMaryPublisherBusiness(authInfoMary);
			tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.saveJoePublisherPublisherAssertion2(authInfoJoe);
			
			tckFindEntity.findRelatedBusiness_sortByName(true);
			tckFindEntity.findRelatedBusinessToKey(true);
			tckFindEntity.findRelatedBusinessFromKey(true);
			
			tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.deleteJoePublisherPublisherAssertion2(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
			tckTModel.deleteMaryPublisherTmodel(authInfoMary);
		}
	}
	
	/**
	 * This test should find 2 publisher assertions.
	 */
	@Test
	public void testFindAssertions() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckTModel.saveMaryPublisherTmodel(authInfoMary);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusiness.saveMaryPublisherBusiness(authInfoMary);
			tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.saveJoePublisherPublisherAssertion2(authInfoJoe);
			tckAssertion.saveSamPublisherPublisherAssertion(authInfoSam);
			tckAssertion.saveMaryPublisherPublisherAssertion(authInfoMary);
			
			tckFindEntity.findRelatedBusiness_sortByName(false);
			tckFindEntity.findRelatedBusinessToKey(false);
			tckFindEntity.findRelatedBusinessFromKey(false);
			
			tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.deleteJoePublisherPublisherAssertion2(authInfoJoe);
			
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
			tckTModel.deleteMaryPublisherTmodel(authInfoMary);
		}
	}
}
