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

import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.SaveBinding;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class API_050_BindingTemplateTest 
{
	final static String JOE_BINDING_XML               = "api_xml_data/joepublisher/bindingTemplate.xml";
    final static String JOE_BINDING_KEY               = "uddi:juddi.apache.org:joepublisher:bindingtwo";
    
    private static Logger logger                      = Logger.getLogger(API_050_BindingTemplateTest.class);
	private UDDIPublicationImpl publish               = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry                   = new UDDIInquiryImpl();
	
	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
	private static API_020_TmodelTest api020          = new API_020_TmodelTest();
	private static API_030_BusinessEntityTest api030  = new API_030_BusinessEntityTest();
	private static API_040_BusinessServiceTest api040 = new API_040_BusinessServiceTest();
	private static String authInfoJoe                 = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			authInfoJoe = API_010_PublisherTest.authInfoJoe();
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
			api040.saveJoePublisherService(authInfoJoe);
			saveJoePublisherBinding(authInfoJoe);
			deleteJoePublisherBinding(authInfoJoe);
		} finally {
			api040.deleteJoePublisherService(authInfoJoe);
			api030.deleteJoePublisherBusiness(authInfoJoe);
			api020.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
		
	protected void saveJoePublisherBinding(String authInfoJoe) {
		saveBinding(authInfoJoe, JOE_BINDING_XML, JOE_BINDING_KEY);
	}
	
	protected void deleteJoePublisherBinding(String authInfoJoe) {
		deleteBinding(authInfoJoe, JOE_BINDING_KEY);
	}
	
	private void saveBinding(String authInfo, String bindingXML, String bindingKey) {
		try {
			// First save the entity
			SaveBinding sb = new SaveBinding();
			sb.setAuthInfo(authInfo);
			
			BindingTemplate btIn = (BindingTemplate)UDDIApiTestHelper.buildEntityFromDoc(bindingXML, "org.uddi.api_v3");
			sb.getBindingTemplate().add(btIn);
			publish.saveBinding(sb);
			
			// Now get the entity and check the values
			GetBindingDetail gb = new GetBindingDetail();
			gb.getBindingKey().add(bindingKey);
			BindingDetail bd = inquiry.getBindingDetail(gb);
			List<BindingTemplate> btOutList = bd.getBindingTemplate();
			BindingTemplate btOut = btOutList.get(0);

			assertEquals(btIn.getServiceKey(), btOut.getServiceKey());
			assertEquals(btIn.getBindingKey(), btOut.getBindingKey());
			
			UDDIApiTestHelper.checkDescriptions(btIn.getDescription(), btOut.getDescription());
			UDDIApiTestHelper.checkCategories(btIn.getCategoryBag(), btOut.getCategoryBag());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown: " + e.getMessage());
		}
		
	}
	
	private void deleteBinding(String authInfo, String bindingKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteBinding db = new DeleteBinding();
			db.setAuthInfo(authInfo);
			
			db.getBindingKey().add(bindingKey);
			publish.deleteBinding(db);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}

}
