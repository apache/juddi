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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_040_BusinessServiceIntegrationTest 
{
	 
	private static Log logger = LogFactory.getLog(UDDI_040_BusinessServiceIntegrationTest.class);
	
	protected static TckTModel tckTModel               = null;
	protected static TckBusiness tckBusiness           = null;
	protected static TckBusinessService tckBusinessService  = null;
	
	protected static String authInfoJoe                = null;
	protected static String authInfoSam                = null;
    private static UDDIClient manager;
	
	@AfterClass
	public static void stopManager() throws ConfigurationException {
		manager.stop();
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		manager  = new UDDIClient();
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
        	 tckBusiness = new TckBusiness(publication, inquiry);
        	 tckBusinessService = new TckBusinessService(publication, inquiry);
 			 String authInfoUDDI  = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(),  TckPublisher.getUDDIPassword());
 			 tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
 			 tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test
	public void joepublisher() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	@Test
	public void samsyndicator() {
		try {
			
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusinessService.saveSamSyndicatorService(authInfoSam);
			tckBusinessService.deleteSamSyndicatorService(authInfoSam);
		} finally {
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
	
	/**
	 * 5.2.16.3 paragraph 4
	 * Data contained within businessEntity structures can be rearranged with 
	 * this API call. This can be done by redefining parent container relationships 
	 * for other registered information. For instance, if a new businessEntity 
	 * is saved with information about a businessService that is registered 
	 * already as part of a different businessEntity, this results in the 
	 * businessService being moved from its current container to the new businessEntity.	
	 * This condition occurs when the businessKey of the businessService being 
	 * saved matches the businessKey of the businessEntity being saved. 
	 * An attempt to delete or move a businessService in this manner by 
	 * a party who is not the publisher of the businessService MUST be 
	 * rejected with an error E_userMismatch.
	 */
	@Test
	public void joepublisherMoveBusinessService() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBusiness.checkServicesBusinessOne(1);
			tckBusiness.saveJoePublisherBusiness3(authInfoJoe);
			//check that this business has no services
			tckBusiness.checkServicesBusinessThree(0);
			//Now move the service from one to three
			tckBusiness.saveJoePublisherBusiness1to3(authInfoJoe);
			tckBusiness.checkServicesBusinessOne(0);
			tckBusiness.checkServicesBusinessThree(1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness3(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	

}
