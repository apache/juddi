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
import org.junit.BeforeClass;
import org.junit.Test;

import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.SaveService;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_040_BusinessServiceTest 
{
	final static String JOE_SERVICE_XML              = "api_xml_data/joepublisher/businessService.xml";
    final static String JOE_SERVICE_KEY              = "uddi:juddi.apache.org:joepublisher:serviceone";
    
    final static String SAM_SERVICE_XML              = "api_xml_data/samsyndicator/businessService.xml";
    final static String SAM_SERVICE_KEY              = "uddi:juddi.apache.org:samco:repository:listingservice";
    
    private static Logger logger                     = Logger.getLogger(API_040_BusinessServiceTest.class);
	private UDDIPublicationImpl publish              = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry                  = new UDDIInquiryImpl();
	
	private static API_010_PublisherTest api010      = new API_010_PublisherTest();
	private static API_020_TmodelTest api020         = new API_020_TmodelTest();
	private static API_030_BusinessEntityTest api030 = new API_030_BusinessEntityTest();
	private static String authInfoJoe                = null;
	private static String authInfoSam                = null;
	
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
	public void joepublisher() {
		try {
			api020.saveJoePublisherTmodel(authInfoJoe);
			api030.saveJoePublisherBusiness(authInfoJoe);
			saveJoePublisherService(authInfoJoe);
			deleteJoePublisherService(authInfoJoe);
		} finally {
			api030.deleteJoePublisherBusiness(authInfoJoe);
			api020.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	@Test
	public void samsyndicator() {
		try {
			
			api020.saveSamSyndicatorTmodel(authInfoSam);
			api030.saveSamSyndicatorBusiness(authInfoSam);
			saveSamSyndicatorService(authInfoSam);
			deleteSamSyndicatorService(authInfoSam);
		} finally {
			api030.deleteSamSyndicatorBusiness(authInfoSam);
			api020.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
	
	protected void saveJoePublisherService(String authInfoJoe) {
		saveService(authInfoJoe, JOE_SERVICE_XML, JOE_SERVICE_KEY);
	}
	
	protected void deleteJoePublisherService(String authInfoJoe) {
		deleteService(authInfoJoe, JOE_SERVICE_KEY);
	}
	
	protected void saveSamSyndicatorService(String authInfoSam) {
		saveService(authInfoSam, SAM_SERVICE_XML, SAM_SERVICE_KEY);
	}
	
	protected void deleteSamSyndicatorService(String authInfoSam) {
		deleteService(authInfoSam, SAM_SERVICE_KEY);
	}
	
	
	private void saveService(String authInfo, String serviceXML, String serviceKey) {
		try {
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
			UDDIApiTestHelper.checkBindingTemplates(bsIn.getBindingTemplates(), bsOut.getBindingTemplates());
			UDDIApiTestHelper.checkCategories(bsIn.getCategoryBag(), bsOut.getCategoryBag());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}
	
	private void deleteService(String authInfo, String serviceKey) {
		try {
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
