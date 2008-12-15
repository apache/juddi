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

import org.apache.juddi.test.UDDIApiTestHelper;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.SaveBusiness;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_030_BusinessEntityTest {
	
	final static String BUSINESS_XML    = "api_xml_data/joepublisher/businessEntity.xml";
    final static String BUSINESS_KEY    = "uddi:juddi.apache.org:joepublisher:businessone";
    
	private static UDDIPublicationImpl publish = new UDDIPublicationImpl();
	private static UDDIInquiryImpl inquiry     = new UDDIInquiryImpl();
	private static Logger logger               = Logger.getLogger(API_030_BusinessEntityTest.class);
	
	@Test
	public void joePublisher() {
		
		try {
			API_010_PublisherTest api010 = new API_010_PublisherTest();
			if (!api010.isExistPublisher(API_010_PublisherTest.JOE_PUBLISHER_ID)) {
				//Add the Publisher
				api010.savePublisher(API_010_PublisherTest.JOE_PUBLISHER_ID, API_010_PublisherTest.JOE_PUBLISHER_XML);
			}
			new API_020_TmodelTest().saveTModel(API_010_PublisherTest.JOE_PUBLISHER_ID, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_XML, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_KEY);
			saveBusiness(API_010_PublisherTest.JOE_PUBLISHER_ID, BUSINESS_XML, BUSINESS_KEY);
			deleteBusiness(API_010_PublisherTest.JOE_PUBLISHER_ID, BUSINESS_XML, BUSINESS_KEY);
		} finally {
			new API_020_TmodelTest().deleteTModel(API_010_PublisherTest.JOE_PUBLISHER_ID, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_XML, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_KEY);
		}
	}
	
	
	public void saveBusiness(String publisherId, String businessXML, String businessKey) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
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
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}

	}
	
	
	public void deleteBusiness(String publisherId, String businessXML, String businessKey) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
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
