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
package org.apache.juddi.v2.tck;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v2.*;
import org.uddi.v2_service.Inquire;
import org.uddi.v2_service.Publish;


/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckFindEntity 
{
	final static String FIND_BUSINESS_XML             = "uddi_data_v2/find/findBusiness1.xml";
	final static String FIND_ALL_BUSINESSES_XML       = "uddi_data_v2/find/findAllBusinesses.xml";
	
	final static String FIND_RELATED_BUSINESS_SORT_BY_NAME_XML = "uddi_data_v2/find/findRelatedBusinesses_sortByName.xml";
	final static String FIND_RELATED_BUSINESS_FROM_KEY= "uddi_data_v2/find/findRelatedBusinesses_fromKey.xml";
	final static String FIND_RELATED_BUSINESS_TO_KEY  = "uddi_data_v2/find/findRelatedBusinesses_toKey.xml";
	final static String FIND_SERVICE_XML              = "uddi_data_v2/find/findService1.xml";
	final static String FIND_BINDING_XML              = "uddi_data_v2/find/findBinding1.xml";
	final static String FIND_TMODEL_XML               = "uddi_data_v2/find/findTModel1.xml";
	final static String COMBINE_CAT_FIND_SERVICES     = "uddi_data_v2/joepublisher/combineCatBagsFindServices.xml";
	   
   
	private Log logger = LogFactory.getLog(this.getClass());
	Inquire inquiry =null;
	
	public TckFindEntity(Inquire inquiry) {
		super();
		this.inquiry = inquiry;
	}

	public void findBusiness() {
		try {
			FindBusiness body = (FindBusiness)EntityCreator.buildFromDoc(FIND_BUSINESS_XML, "org.uddi.api_v2");
               body.setGeneric("2.0");
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
			
			BusinessEntity beIn = (BusinessEntity)EntityCreator.buildFromDoc(TckBusiness.JOE_BUSINESS_XML, "org.uddi.api_v2");
			
			assertEquals(beIn.getBusinessKey(), biOut.getBusinessKey());
			
			TckValidator.checkNames(beIn.getName(), biOut.getName());
			TckValidator.checkDescriptions(beIn.getDescription(), biOut.getDescription());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public void getNonExitingBusiness() {
		String nonExistingKey = "nonexistingKey";
		try {
			GetBusinessDetail body = new GetBusinessDetail();
               body.setGeneric("2.0");
			body.getBusinessKey().add(nonExistingKey);
			BusinessDetail  result = inquiry.getBusinessDetail(body);
			Assert.fail("No business should be found");
			System.out.println(result.getBusinessEntity().size());
		} catch (Exception e) {
			try {
				System.out.println("(Expected) Error message = " + e.getMessage());
				DispositionReport report = org.uddi.v2_service.DispositionReport.getDispositionReport(e);
				assertNotNull(report);
				assertTrue(report.countainsErrorCode(DispositionReport.E_INVALID_KEY_PASSED));
			} catch (Exception e1) {
				Assert.fail("We only expect DispositionReportFaultMessage, not " + e1.getClass());
				logger.error(e.getMessage(), e1);
			}
		}
	}
	
	public void findAllBusiness() {
		try {
			FindBusiness body = (FindBusiness)EntityCreator.buildFromDoc(FIND_ALL_BUSINESSES_XML, "org.uddi.api_v2");
               body.setGeneric("2.0");
			BusinessList result = inquiry.findBusiness(body);
			if (result == null)
				Assert.fail("Null result from find business operation");
			BusinessInfos bInfos = result.getBusinessInfos();
			if (bInfos == null)
				Assert.fail("No result from find business operation");
			List<BusinessInfo> biList = bInfos.getBusinessInfo();
			if (biList == null || biList.size() == 0)
				Assert.fail("No result from find business operation");
			//expecting more than 2 businesses
			Assert.assertTrue(biList.size()>1);
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	
	public void findRelatedBusiness_sortByName(boolean isOneSided) {
		try {
			FindRelatedBusinesses body = (FindRelatedBusinesses)EntityCreator.buildFromDoc(FIND_RELATED_BUSINESS_SORT_BY_NAME_XML, "org.uddi.api_v2");
               body.setGeneric("2.0");
			RelatedBusinessesList result = inquiry.findRelatedBusinesses(body);
			if (result == null)
				Assert.fail("Null result from find related business operation");
			RelatedBusinessInfos bInfos = result.getRelatedBusinessInfos();
		
			//both parties need to register the assertion for it to be live.
			if (isOneSided) {
				Assert.assertTrue(bInfos==null || bInfos.getRelatedBusinessInfo().isEmpty());
			} else {
				if (bInfos == null)
					Assert.fail("No result from find related business operation");
				List<RelatedBusinessInfo> biList = bInfos.getRelatedBusinessInfo();
				if (biList == null || biList.size() == 0)
					Assert.fail("No result from find related business operation");
				Set<String> keys = new HashSet<String>();
				for (RelatedBusinessInfo relatedBusinessInfo : biList) {
					keys.add(relatedBusinessInfo.getBusinessKey());
				}
				Assert.assertTrue(keys.contains(TckBusiness.SAM_BUSINESS_KEY));
				Assert.assertTrue(keys.contains(TckBusiness.MARY_BUSINESS_KEY));
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public void findRelatedBusinessToKey(boolean isOneSided) {
		try {
			FindRelatedBusinesses body = (FindRelatedBusinesses)EntityCreator.buildFromDoc(FIND_RELATED_BUSINESS_TO_KEY, "org.uddi.api_v2");
               body.setGeneric("2.0");
			RelatedBusinessesList result = inquiry.findRelatedBusinesses(body);
			if (result == null)
				Assert.fail("Null result from find related business operation");
			RelatedBusinessInfos bInfos = result.getRelatedBusinessInfos();
		
			//both parties need to register the assertion for it to be live.
			if (isOneSided) {
				Assert.assertTrue(bInfos==null || bInfos.getRelatedBusinessInfo().isEmpty());
			} else {
				if (bInfos == null)
					Assert.fail("No result from find related business operation");
				List<RelatedBusinessInfo> biList = bInfos.getRelatedBusinessInfo();
				if (biList == null || biList.size() == 0)
					Assert.fail("No result from find related business operation");
				Set<String> keys = new HashSet<String>();
				for (RelatedBusinessInfo relatedBusinessInfo : biList) {
					keys.add(relatedBusinessInfo.getBusinessKey());
				}
				Assert.assertTrue(keys.contains(TckBusiness.JOE_BUSINESS_KEY));
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public void findRelatedBusinessFromKey(boolean isOneSided) {
		try {
			FindRelatedBusinesses body = (FindRelatedBusinesses)EntityCreator.buildFromDoc(FIND_RELATED_BUSINESS_FROM_KEY, "org.uddi.api_v2");
               body.setGeneric("2.0");
			RelatedBusinessesList result = inquiry.findRelatedBusinesses(body);
			if (result == null)
				Assert.fail("Null result from find related business operation");
			RelatedBusinessInfos bInfos = result.getRelatedBusinessInfos();
		
			//both parties need to register the assertion for it to be live.
			if (isOneSided) {
				Assert.assertNull(bInfos);
			} else {
				if (bInfos == null)
					Assert.fail("No result from find related business operation");
				List<RelatedBusinessInfo> biList = bInfos.getRelatedBusinessInfo();
				if (biList == null || biList.size() == 0)
					Assert.fail("No result from find related business operation");
				Set<String> keys = new HashSet<String>();
				for (RelatedBusinessInfo relatedBusinessInfo : biList) {
					keys.add(relatedBusinessInfo.getBusinessKey());
				}
				Assert.assertTrue(keys.contains(TckBusiness.SAM_BUSINESS_KEY));
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public String findService(String findQualifier) {
		String serviceKey = null;
		try {
			FindService body = (FindService)EntityCreator.buildFromDoc(FIND_SERVICE_XML, "org.uddi.api_v2");
               body.setGeneric("2.0");
			if (findQualifier!=null) body.getFindQualifiers().getFindQualifier().add(findQualifier);
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
			
			BusinessService bsIn = (BusinessService)EntityCreator.buildFromDoc(TckBusinessService.JOE_SERVICE_XML, "org.uddi.api_v2");
			
			assertEquals(bsIn.getServiceKey(), siOut.getServiceKey());
			
			TckValidator.checkNames(bsIn.getName(), siOut.getName());
			serviceKey = siOut.getServiceKey();
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		return serviceKey;
	}
	
	public String findService_CombinedCatBag() {
		String serviceKey = null;
		try {
			FindService body = (FindService)EntityCreator.buildFromDoc(COMBINE_CAT_FIND_SERVICES, "org.uddi.api_v2");
               body.setGeneric("2.0");
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
			
			serviceKey = siOut.getServiceKey();
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		return serviceKey;
	}

	public void findServiceDetail(String serviceKey) {
		try {
			GetServiceDetail getServiceDetail = new GetServiceDetail();
               getServiceDetail.setGeneric("2.0");
			getServiceDetail.getServiceKey().add(serviceKey);
			
			
			ServiceDetail result = inquiry.getServiceDetail(getServiceDetail);
			
			if (result == null)
				Assert.fail("Null result from find service operation");
			
			BindingTemplates templates = result.getBusinessService().get(0).getBindingTemplates();
			if (templates!=null && templates.getBindingTemplate()!=null) {
				System.out.println(templates.getBindingTemplate().size());
				System.out.println("key=" + templates.getBindingTemplate().get(0).getBindingKey());
			}
//			ServiceInfos sInfos = result.getServiceInfos();
//			if (sInfos == null)
//				Assert.fail("No result from find service operation");
//			List<ServiceInfo> siList = sInfos.getServiceInfo();
//			if (siList == null || siList.size() == 0)
//				Assert.fail("No result from find service operation");
//			ServiceInfo siOut = siList.get(0);
//			
//			BusinessService bsIn = (BusinessService)EntityCreator.buildFromDoc(TckBusinessService.JOE_SERVICE_XML, "org.uddi.api_v2");
//			
//			assertEquals(bsIn.getServiceKey(), siOut.getServiceKey());
//			
//			TckValidator.checkNames(bsIn.getName(), siOut.getName());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	public void findBinding(String findQualifier) {
		try {
			FindBinding body = (FindBinding)EntityCreator.buildFromDoc(FIND_BINDING_XML, "org.uddi.api_v2");
			if (findQualifier!=null) body.getFindQualifiers().getFindQualifier().add(findQualifier);
               body.setGeneric("2.0");
			BindingDetail result = inquiry.findBinding(body);
			if (result == null)
				Assert.fail("Null result from find binding operation");
			List<BindingTemplate> btList = result.getBindingTemplate();
			if (btList == null || btList.size() == 0)
				Assert.fail("No result from find binding operation");
			BindingTemplate btOut = btList.get(0);
			
			BindingTemplate btIn = (BindingTemplate)EntityCreator.buildFromDoc(TckBindingTemplate.JOE_BINDING_XML, "org.uddi.api_v2");
			
			assertEquals(btIn.getServiceKey(), btOut.getServiceKey());
			assertEquals(btIn.getBindingKey(), btOut.getBindingKey());
			
			TckValidator.checkDescriptions(btIn.getDescription(), btOut.getDescription());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown:  " + e.getMessage());
		}
	}
	
	public void findTModel(String findQualifier) {
		try {
			FindTModel body = (FindTModel)EntityCreator.buildFromDoc(FIND_TMODEL_XML, "org.uddi.api_v2");
			if (findQualifier!=null) body.getFindQualifiers().getFindQualifier().add(findQualifier);
               body.setGeneric("2.0");
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
			
			TModel tmIn = (TModel)EntityCreator.buildFromDoc(TckTModel.JOE_PUBLISHER_TMODEL_XML, "org.uddi.api_v2");
			
			assertEquals(tmIn.getTModelKey(), tiOut.getTModelKey());
			assertEquals(tmIn.getName().getLang(), tiOut.getName().getLang());
			assertEquals(tmIn.getName().getValue(), tiOut.getName().getValue());

		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
}