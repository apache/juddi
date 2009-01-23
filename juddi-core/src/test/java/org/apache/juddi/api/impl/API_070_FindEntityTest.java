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
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelInfos;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class API_070_FindEntityTest 
{
	final static String FIND_BUSINESS_XML             = "api_xml_data/find/findBusiness1.xml";
	final static String FIND_SERVICE_XML              = "api_xml_data/find/findService1.xml";
	final static String FIND_BINDING_XML              = "api_xml_data/find/findBinding1.xml";
	final static String FIND_TMODEL_XML               = "api_xml_data/find/findTModel1.xml";
    
    private static Logger logger                      = Logger.getLogger(API_070_FindEntityTest.class);
	private UDDIInquiryImpl inquiry                   = new UDDIInquiryImpl();
	
	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
	private static API_020_TmodelTest api020          = new API_020_TmodelTest();
	private static API_030_BusinessEntityTest api030  = new API_030_BusinessEntityTest();
	private static API_040_BusinessServiceTest api040 = new API_040_BusinessServiceTest();
	private static API_050_BindingTemplateTest api050 = new API_050_BindingTemplateTest();
	private static String authInfoJoe                 = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			api010.saveSamSyndicator();
			authInfoJoe = API_010_PublisherTest.authInfoJoe();
		} catch (DispositionReportFaultMessage e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	
	@Test
	public void findEntities() {
		try {
			api020.saveJoePublisherTmodel(authInfoJoe);
			api030.saveJoePublisherBusiness(authInfoJoe);
			api040.saveJoePublisherService(authInfoJoe);
			api050.saveJoePublisherBinding(authInfoJoe);
			findBusiness();
			findService();
			findBinding();
			findTModel();
		}
		finally {
			api050.deleteJoePublisherBinding(authInfoJoe);
			api040.deleteJoePublisherService(authInfoJoe);
			api030.deleteJoePublisherBusiness(authInfoJoe);
			api020.deleteJoePublisherTmodel(authInfoJoe);
		}
		
	}

	private void findBusiness() {
		try {
			FindBusiness body = (FindBusiness)UDDIApiTestHelper.buildEntityFromDoc(FIND_BUSINESS_XML, "org.uddi.api_v3");
			BusinessList result = inquiry.findBusiness(body);
			if (result == null)
				Assert.fail("Null result from find business operation");
			BusinessInfos bInfos = result.getBusinessInfos();
			if (bInfos == null)
				Assert.fail("No result from find business operation");
			List<BusinessInfo> biList = bInfos.getBusinessInfo();
			if (biList == null || biList.size() == 0)
				Assert.fail("No result from find business operation");
			BusinessInfo biOut = biList.get(0);
			
			BusinessEntity beIn = (BusinessEntity)UDDIApiTestHelper.buildEntityFromDoc(API_030_BusinessEntityTest.JOE_BUSINESS_XML, "org.uddi.api_v3");
			
			assertEquals(beIn.getBusinessKey(), biOut.getBusinessKey());
			
			UDDIApiTestHelper.checkNames(beIn.getName(), biOut.getName());
			UDDIApiTestHelper.checkDescriptions(beIn.getDescription(), biOut.getDescription());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	private void findService() {
		try {
			FindService body = (FindService)UDDIApiTestHelper.buildEntityFromDoc(FIND_SERVICE_XML, "org.uddi.api_v3");
			ServiceList result = inquiry.findService(body);
			if (result == null)
				Assert.fail("Null result from find service operation");
			ServiceInfos sInfos = result.getServiceInfos();
			if (sInfos == null)
				Assert.fail("No result from find service operation");
			List<ServiceInfo> siList = sInfos.getServiceInfo();
			if (siList == null || siList.size() == 0)
				Assert.fail("No result from find service operation");
			ServiceInfo siOut = siList.get(0);
			
			BusinessService bsIn = (BusinessService)UDDIApiTestHelper.buildEntityFromDoc(API_040_BusinessServiceTest.JOE_SERVICE_XML, "org.uddi.api_v3");
			
			assertEquals(bsIn.getServiceKey(), siOut.getServiceKey());
			
			UDDIApiTestHelper.checkNames(bsIn.getName(), siOut.getName());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}

	private void findBinding() {
		try {
			FindBinding body = (FindBinding)UDDIApiTestHelper.buildEntityFromDoc(FIND_BINDING_XML, "org.uddi.api_v3");
			BindingDetail result = inquiry.findBinding(body);
			if (result == null)
				Assert.fail("Null result from find binding operation");
			List<BindingTemplate> btList = result.getBindingTemplate();
			if (btList == null || btList.size() == 0)
				Assert.fail("No result from find binding operation");
			BindingTemplate btOut = btList.get(0);
			
			BindingTemplate btIn = (BindingTemplate)UDDIApiTestHelper.buildEntityFromDoc(API_050_BindingTemplateTest.JOE_BINDING_XML, "org.uddi.api_v3");
			
			assertEquals(btIn.getServiceKey(), btOut.getServiceKey());
			assertEquals(btIn.getBindingKey(), btOut.getBindingKey());
			
			UDDIApiTestHelper.checkDescriptions(btIn.getDescription(), btOut.getDescription());
			UDDIApiTestHelper.checkCategories(btIn.getCategoryBag(), btOut.getCategoryBag());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown:  " + e.getMessage());
		}
	}
	
	private void findTModel() {
		try {
			FindTModel body = (FindTModel)UDDIApiTestHelper.buildEntityFromDoc(FIND_TMODEL_XML, "org.uddi.api_v3");
			TModelList result = inquiry.findTModel(body);
			if (result == null)
				Assert.fail("Null result from find tModel operation");
			TModelInfos tInfos = result.getTModelInfos();
			if (tInfos == null)
				Assert.fail("No result from find tModel operation");
			List<TModelInfo> tiList = tInfos.getTModelInfo();
			if (tiList == null || tiList.size() == 0)
				Assert.fail("No result from find tModel operation");
			TModelInfo tiOut = tiList.get(0);
			
			TModel tmIn = (TModel)UDDIApiTestHelper.buildEntityFromDoc(API_020_TmodelTest.JOE_PUBLISHER_TMODEL_XML, "org.uddi.api_v3");
			
			assertEquals(tmIn.getTModelKey(), tiOut.getTModelKey());
			assertEquals(tmIn.getName().getLang(), tiOut.getName().getLang());
			assertEquals(tmIn.getName().getValue(), tiOut.getName().getValue());

			UDDIApiTestHelper.checkDescriptions(tmIn.getDescription(), tiOut.getDescription());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}

}
