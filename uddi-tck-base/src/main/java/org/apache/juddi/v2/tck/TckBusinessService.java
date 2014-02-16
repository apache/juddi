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

import java.util.List;

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
public class TckBusinessService 
{
	
    public final static String JOE_SERVICE_XML = "uddi_data_v2/joepublisher/businessService.xml";
    public final static String JOE_SERVICE_KEY = "b82739a9-f35c-4331-999a-6bfb2968ca5e";
    public final static String JOE_SERVICE_XML_2 = "uddi_data_v2/joepublisher/businessService2.xml";
    public final static String JOE_SERVICE_KEY_2 = "6687b480-c526-4175-b6bf-32a2a8734cec";
    public final static String SAM_SERVICE_XML = "uddi_data_v2/samsyndicator/businessService.xml";
    public final static String SAM_SERVICE_KEY = "bc9f7399-bb91-4693-8dc4-67ca38a4734f";
    public final static String JOE_BINDING_KEY_1="c0af9342-f4f1-4625-b1f4-810fa35ae346";
    private Log logger = LogFactory.getLog(this.getClass());
    private Publish publication = null;
    private Inquire inquiry = null;
	
	public TckBusinessService(Publish publication, 
				  Inquire inquiry) {
		super();
		this.publication = publication;
		this.inquiry = inquiry;
	}

	public String saveJoePublisherService(String authInfoJoe) {
		return saveService(authInfoJoe, JOE_SERVICE_XML, JOE_SERVICE_KEY);
	}
	
	public void updateJoePublisherService(String authInfoJoe, String description) {
		saveService(authInfoJoe, JOE_SERVICE_XML, JOE_SERVICE_KEY, description);
	}
	
	public void saveJoePublisherService2(String authInfoJoe) {
		saveService2(authInfoJoe, JOE_SERVICE_KEY, JOE_SERVICE_XML_2, JOE_SERVICE_KEY_2);
	}

	public void saveJoePublisherServices(String authInfoJoe, int businessInt, int numberOfCopies) {
		saveServices(authInfoJoe, businessInt, JOE_SERVICE_XML, JOE_SERVICE_KEY, numberOfCopies, TckBusiness.JOE_BUSINESS_KEY);
	}
	
	public void deleteJoePublisherService(String authInfoJoe) {
		deleteService(authInfoJoe, JOE_SERVICE_KEY);
	}
	
	public void deleteJoePublisherService2(String authInfoJoe) {
		deleteService(authInfoJoe, JOE_SERVICE_KEY_2);
	}

	
	
	public void deleteJoePublisherServices(String authInfoJoe, int businessInt, int numberOfCopies) {
		deleteServices(authInfoJoe, businessInt,JOE_SERVICE_KEY, numberOfCopies);
	}
	
	public void saveSamSyndicatorService(String authInfoSam) {
		saveService(authInfoSam, SAM_SERVICE_XML, SAM_SERVICE_KEY);
	}
	
	public void saveSamSyndicatorServices(String authInfoSam, int businessInt, int numberOfCopies) {
		saveServices(authInfoSam, businessInt, SAM_SERVICE_XML, SAM_SERVICE_KEY, numberOfCopies, TckBusiness.SAM_BUSINESS_KEY);
	}
	
	public void deleteSamSyndicatorService(String authInfoSam) {
		deleteService(authInfoSam, SAM_SERVICE_KEY);
	}
	
	public void deleteSamSyndicatorServices(String authInfoSam, int businessInt, int numberOfCopies) {
		deleteServices(authInfoSam, businessInt, SAM_SERVICE_KEY, numberOfCopies);
	}
	
	public void saveServices(String authInfo, int businessInt, String serviceXML, String serviceKey, int numberOfCopies, String parentBusinessKey) {
		SaveService ss = null;
		try {
			org.uddi.api_v2.BusinessService bsIn = (org.uddi.api_v2.BusinessService)EntityCreator.buildFromDoc(serviceXML, "org.uddi.api_2");
			String serviceName = bsIn.getName().get(0).getValue();
			String bindingKey = bsIn.getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
			for (int i=0; i<numberOfCopies; i++) {
			    // save the entity
				ss = new SaveService();
                    ss.setGeneric("2.0");
				ss.setAuthInfo(authInfo);
				bsIn.getName().get(0).setValue(serviceName + "-" + i);
				bsIn.setBusinessKey(parentBusinessKey);
				bsIn.setServiceKey(serviceKey + "-" + businessInt + "-" + i);
				bsIn.getBindingTemplates().getBindingTemplate().get(0).setBindingKey(bindingKey + "-" + businessInt + "-" + i);
				bsIn.getBindingTemplates().getBindingTemplate().get(0).setServiceKey(serviceKey + "-" + businessInt + "-" + i);
				ss.getBusinessService().add(bsIn);
				
				BindingTemplate bt = bsIn.getBindingTemplates().getBindingTemplate().get(0);
				if (! bt.getServiceKey().equals(serviceKey + "-" + businessInt + "-" + i)) {
					System.out.println("not the same");
				}
				
				publication.saveService(ss);
				logger.info("Add service with key " + bsIn.getServiceKey());
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public String saveService(String authInfo, String serviceXML, String serviceKey) {
		return saveService(authInfo, serviceXML, serviceKey, null);
	}
	
	public String saveService(String authInfo, String serviceXML, String serviceKey, String description) {
		try {
                        logger.info("saving service key " + serviceKey + " from " + serviceXML);
			// First save the entity
			SaveService ss = new SaveService();
			ss.setAuthInfo(authInfo);
			ss.setGeneric("2.0");
			org.uddi.api_v2.BusinessService bsIn = (org.uddi.api_v2.BusinessService)EntityCreator.buildFromDoc(serviceXML, "org.uddi.api_v2");
			
			if (description!=null) bsIn.getDescription().get(0).setValue("updated description");
		    ss.getBusinessService().add(bsIn);
			publication.saveService(ss);
			
			// Now get the entity and check the values
			GetServiceDetail gs = new GetServiceDetail();
			gs.getServiceKey().add(serviceKey);
			ServiceDetail sd = inquiry.getServiceDetail(gs);
			List<BusinessService> bsOutList = sd.getBusinessService();
			BusinessService bsOut = bsOutList.get(0);

			assertEquals(bsIn.getServiceKey().toLowerCase(), bsOut.getServiceKey());
			
			TckValidator.checkNames(bsIn.getName(), bsOut.getName());
			TckValidator.checkDescriptions(bsIn.getDescription(), bsOut.getDescription());
			TckValidator.checkBindingTemplates(bsIn.getBindingTemplates(), bsOut.getBindingTemplates());
			TckValidator.checkCategories(bsIn.getCategoryBag(), bsOut.getCategoryBag());
                        if (bsOut.getBindingTemplates()!=null && !bsOut.getBindingTemplates().getBindingTemplate().isEmpty())
                            return bsOut.getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		return null;
	}
	
	public void saveService2(String authInfo, String serviceKey, String serviceXML2,  String serviceKey2) {
		try {
			// First save the entity
			SaveService ss = new SaveService();
			ss.setAuthInfo(authInfo);
			
			org.uddi.api_v2.BusinessService bsIn = (org.uddi.api_v2.BusinessService)EntityCreator.buildFromDoc(serviceXML2, "org.uddi.api_v2");
			ss.getBusinessService().add(bsIn);
               ss.setGeneric("2.0");
			publication.saveService(ss);
			
			// Now get the entity and check the values
			GetServiceDetail gs = new GetServiceDetail();
			gs.getServiceKey().add(serviceKey);
			gs.getServiceKey().add(serviceKey2);
			ServiceDetail sd = inquiry.getServiceDetail(gs);
			List<BusinessService> bsOutList = sd.getBusinessService();
			int size = bsOutList.size();

			assertEquals(2, size);
			
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}
	
	public void deleteService(String authInfo, String serviceKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteService ds = new DeleteService();
			ds.setAuthInfo(authInfo);
			ds.setGeneric("2.0");
			ds.getServiceKey().add(serviceKey);
			publication.deleteService(ds);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public void deleteServices(String authInfo, int businessInt, String serviceKey, int numberOfCopies) {
		try {
			for (int i=0; i<numberOfCopies; i++) {
				// Delete the entity and make sure it is removed
				DeleteService ds = new DeleteService();
                    ds.setGeneric("2.0");
				ds.setAuthInfo(authInfo);
				ds.getServiceKey().add(serviceKey + "-" + businessInt + "-" + i);
				publication.deleteService(ds);
				logger.debug("Deleted Service with key " + businessInt + "-" + serviceKey + "-" + i);
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}

}