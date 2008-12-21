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

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_020_TmodelTest {
	
	final static String JOE_PUBLISHER_TMODEL_XML      = "api_xml_data/joepublisher/tModelKeyGen.xml";
    final static String JOE_PUBLISHER_TMODEL_KEY      = "uddi:juddi.apache.org:joepublisher:KEYGENERATOR";
    
    final static String SAM_SYNDICATOR_TMODEL_XML     = "api_xml_data/samsyndicator/tModelKeyGen.xml";
    final static String SAM_SYNDICATOR_TMODEL_KEY     = "uddi:juddi.apache.org:samco:repository:keygenerator";
    
	private static UDDIPublicationImpl publish        = new UDDIPublicationImpl();
	private static UDDIInquiryImpl inquiry            = new UDDIInquiryImpl();
	
	private static Logger logger                      = Logger.getLogger(API_020_TmodelTest.class);
	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
	private static String authInfoJoe                 = null;
	private static String authInfoSam                 = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			api010.saveSamSyndicator();
			authInfoJoe = API_010_PublisherTest.authInfoJoe();
			authInfoSam = API_010_PublisherTest.authInfoSam();
		} catch (DispositionReportFaultMessage e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	
	@Test
	public void testJoePublisherTmodel() {
		saveJoePublisherTmodel(authInfoJoe);
		deleteJoePublisherTmodel(authInfoJoe);
	}
	
	@Test
	public void testSamSyndicatorTmodelTest() {
		api010.saveSamSyndicator();
		saveSamSyndicatorTmodel(authInfoSam);
		deleteSamSyndicatorTmodel(authInfoSam);
	}
	
	protected void saveJoePublisherTmodel(String authInfoJoe) {
		saveTModel(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY);
	}
	
	protected void deleteJoePublisherTmodel(String authInfoJoe) {
		deleteTModel(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY);
	}
	
	protected void saveSamSyndicatorTmodel(String authInfoSam) {
		saveTModel(authInfoSam, SAM_SYNDICATOR_TMODEL_XML, SAM_SYNDICATOR_TMODEL_KEY);
	}
	
	protected void deleteSamSyndicatorTmodel(String authInfoSam) {
		deleteTModel(authInfoSam, SAM_SYNDICATOR_TMODEL_XML, SAM_SYNDICATOR_TMODEL_KEY);
	}
	
	private void saveTModel(String authInfo, String tModelXml, String tModelKey) {
		
		// Add the tModel
		try {
			SaveTModel st = new SaveTModel();
			st.setAuthInfo(authInfo);

			TModel tmIn = (TModel)UDDIApiTestHelper.buildEntityFromDoc(tModelXml, "org.uddi.api_v3");
			st.getTModel().add(tmIn);
			publish.saveTModel(st);
	
			// Now get the entity and check the values
			GetTModelDetail gt = new GetTModelDetail();
			gt.getTModelKey().add(tModelKey);
			TModelDetail td = inquiry.getTModelDetail(gt);
			List<TModel> tmOutList = td.getTModel();
			TModel tmOut = tmOutList.get(0);

			assertEquals(tmIn.getTModelKey(), tmOut.getTModelKey());
			assertEquals(tmIn.getName().getLang(), tmOut.getName().getLang());
			assertEquals(tmIn.getName().getValue(), tmOut.getName().getValue());
			UDDIApiTestHelper.checkDescriptions(tmIn.getDescription(), tmOut.getDescription());
			UDDIApiTestHelper.checkCategories(tmIn.getCategoryBag(), tmOut.getCategoryBag());
			for (OverviewDoc overviewDoc : tmIn.getOverviewDoc()) {
				UDDIApiTestHelper.checkOverviewDocs(overviewDoc, tmOut.getOverviewDoc());
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	private void deleteTModel(String authInfo, String tModelXml, String tModelKey) {
		
		try {
			//Now deleting the TModel
			// Delete the entity and make sure it is removed
			DeleteTModel dt = new DeleteTModel();
			dt.setAuthInfo(authInfo);
			
			dt.getTModelKey().add(tModelKey);
			publish.deleteTModel(dt);
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
}
