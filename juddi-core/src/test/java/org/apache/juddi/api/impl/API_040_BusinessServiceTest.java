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

import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.SaveService;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_040_BusinessServiceTest 
{
	final static String JOE_SERVICE_XML    = "api_xml_data/joepublisher/businessService.xml";
    final static String JOE_SERVICE_KEY    = "uddi:juddi.apache.org:joepublisher:serviceone";
    
    final static String SAM_SERVICE_XML    = "api_xml_data/samsyndicator/businessService.xml";
    final static String SAM_SERVICE_KEY    = "uddi:juddi.apache.org:samco:repository:listingservice";
    
    private Logger logger                  = Logger.getLogger(this.getClass());
	private UDDIPublicationImpl publish    = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry        = new UDDIInquiryImpl();

	@Test
	public void joepublisher() {
		String publisherId = API_010_PublisherTest.JOE_PUBLISHER_ID;
		try {
			API_010_PublisherTest api010 = new API_010_PublisherTest();
			if (!api010.isExistPublisher(publisherId)) {
				//Add the Publisher
				api010.savePublisher(publisherId, API_010_PublisherTest.JOE_PUBLISHER_XML);
			}
			new API_020_TmodelTest().saveTModel(publisherId, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_XML, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_KEY);
			new API_030_BusinessEntityTest().saveBusiness(publisherId, API_030_BusinessEntityTest.JOE_BUSINESS_XML, API_030_BusinessEntityTest.JOE_BUSINESS_KEY);
			saveService(publisherId, JOE_SERVICE_XML, JOE_SERVICE_KEY);
			deleteService(publisherId, JOE_SERVICE_KEY);
		} finally {
			new API_030_BusinessEntityTest().deleteBusiness(publisherId, API_030_BusinessEntityTest.JOE_BUSINESS_XML, API_030_BusinessEntityTest.JOE_BUSINESS_KEY);
			new API_020_TmodelTest().deleteTModel(publisherId, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_XML, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_KEY);
		}
	}
	
	@Test
	public void samsyndicator() {
		String publisherId = API_010_PublisherTest.SAM_SYNDICATOR_ID;
		try {
			API_010_PublisherTest api010 = new API_010_PublisherTest();
			if (!api010.isExistPublisher(publisherId)) {
				//Add the Publisher
				api010.savePublisher(publisherId, API_010_PublisherTest.SAM_SYNDICATOR_XML);
			}
			new API_020_TmodelTest().saveTModel(publisherId, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_XML, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_KEY);
			new API_030_BusinessEntityTest().saveBusiness(publisherId, API_030_BusinessEntityTest.SAM_BUSINESS_XML, API_030_BusinessEntityTest.SAM_BUSINESS_KEY);
			saveService(publisherId, SAM_SERVICE_XML, SAM_SERVICE_KEY);
			deleteService(publisherId, SAM_SERVICE_KEY);
		} finally {
			new API_030_BusinessEntityTest().deleteBusiness(publisherId, API_030_BusinessEntityTest.SAM_BUSINESS_XML, API_030_BusinessEntityTest.SAM_BUSINESS_KEY);
			new API_020_TmodelTest().deleteTModel(publisherId, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_XML, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_KEY);
		}
	}
	
	
	public void saveService(String publisherId, String serviceXML, String serviceKey) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);

			// First save the entity
			SaveService ss = new SaveService();
			ss.setAuthInfo(authInfo);
			
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)UDDIApiTestHelper.buildEntityFromDoc(serviceXML, "org.uddi.api_v3");
			ss.getBusinessService().add(bsIn);
			publish.saveService(ss);
			
			// Now get the entity and check the values
			GetServiceDetail gs = new GetServiceDetail();
			gs.getServiceKey().add(serviceKey);
			ServiceDetail sd = inquiry.getServiceDetail(gs);
			List<BusinessService> bsOutList = sd.getBusinessService();
			BusinessService bsOut = bsOutList.get(0);

			assertEquals(bsIn.getServiceKey(), bsOut.getServiceKey());
			
			UDDIApiTestHelper.checkNames(bsIn.getName(), bsOut.getName());
			UDDIApiTestHelper.checkDescriptions(bsIn.getDescription(), bsOut.getDescription());
			UDDIApiTestHelper.checkCategories(bsIn.getCategoryBag(), bsOut.getCategoryBag());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}
	
	public void deleteService(String publisherId, String serviceKey) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
	
			// Delete the entity and make sure it is removed
			DeleteService ds = new DeleteService();
			ds.setAuthInfo(authInfo);
			
			ds.getServiceKey().add(serviceKey);
			publish.deleteService(ds);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}

}
