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
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_030_BusinessEntityTest {
	
	final static String JOE_BUSINESS_XML        = "api_xml_data/joepublisher/businessEntity.xml";
    final static String JOE_BUSINESS_KEY        = "uddi:juddi.apache.org:joepublisher:businessone";
    
    final static String SAM_BUSINESS_XML        = "api_xml_data/samsyndicator/businessEntity.xml";
    final static String SAM_BUSINESS_KEY        = "uddi:juddi.apache.org:samco:repository:samco";
    
	private static UDDIPublicationImpl publish  = new UDDIPublicationImpl();
	private static UDDIInquiryImpl inquiry      = new UDDIInquiryImpl();
	private static Logger logger                = Logger.getLogger(API_030_BusinessEntityTest.class);
	
	private static API_010_PublisherTest api010 = new API_010_PublisherTest();
	private static API_020_TmodelTest api020    = new API_020_TmodelTest();
	private static String authInfoJoe           = null;
	private static String authInfoSam           = null;
	
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
	public void testJoePublisherBusinessEntity() {
		try {
			api020.saveJoePublisherTmodel(authInfoJoe);
			saveJoePublisherBusiness(authInfoJoe);
			deleteJoePublisherBusiness(authInfoJoe);
		} finally {
			api020.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
	
	@Test
	public void testSamSyndicatorBusiness() {
		try {
			api020.saveSamSyndicatorTmodel(authInfoSam);
			saveSamSyndicatorBusiness(authInfoSam);
			deleteSamSyndicatorBusiness(authInfoSam);
		} finally {
			api020.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
	
	protected void saveSamSyndicatorBusiness(String authInfoSam) {
		saveBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
	}
	
	protected void deleteSamSyndicatorBusiness(String authInfoSam) {
		deleteBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
	}
	
	protected void saveJoePublisherBusiness(String authInfoJoe) {
    	saveBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
    }
    
    protected void deleteJoePublisherBusiness(String authInfoJoe) {
    	deleteBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
    }
	
	
	private void saveBusiness(String authInfo, String businessXML, String businessKey) {
		try {
			SaveBusiness sb = new SaveBusiness();
			sb.setAuthInfo(authInfo);

			BusinessEntity beIn = (BusinessEntity)UDDIApiTestHelper.buildEntityFromDoc(businessXML, "org.uddi.api_v3");
			sb.getBusinessEntity().add(beIn);
			publish.saveBusiness(sb);
	
			// Now get the entity and check the values
			GetBusinessDetail gb = new GetBusinessDetail();
			gb.getBusinessKey().add(businessKey);
			BusinessDetail bd = inquiry.getBusinessDetail(gb);
			List<BusinessEntity> beOutList = bd.getBusinessEntity();
			BusinessEntity beOut = beOutList.get(0);

			assertEquals(beIn.getBusinessKey(), beOut.getBusinessKey());
			
			UDDIApiTestHelper.checkNames(beIn.getName(), beOut.getName());
			UDDIApiTestHelper.checkDescriptions(beIn.getDescription(), beOut.getDescription());
			UDDIApiTestHelper.checkDiscoveryUrls(beIn.getDiscoveryURLs(), beOut.getDiscoveryURLs());
			UDDIApiTestHelper.checkContacts(beIn.getContacts(), beOut.getContacts());
			UDDIApiTestHelper.checkCategories(beIn.getCategoryBag(), beOut.getCategoryBag());
			
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}

	}
	
	
	private void deleteBusiness(String authInfo, String businessXML, String businessKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteBusiness db = new DeleteBusiness();
			db.setAuthInfo(authInfo);
			db.getBusinessKey().add(businessKey);
			publish.deleteBusiness(db);
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}

	}
}
