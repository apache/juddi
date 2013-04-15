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

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckBusinessService 
{
	
	public final static String NOTIFIER_SERVICE_XML  = "uddi_data/subscriptionnotifier/businessService.xml";
	public final static String NOTIFIER_SERVICE_KEY  = "uddi:uddi.joepublisher.com:notifierone";

	final static String JOE_SERVICE_XML              = "uddi_data/joepublisher/businessService.xml";
    final static String JOE_SERVICE_KEY              = "uddi:uddi.joepublisher.com:serviceone";
    final static String JOE_SERVICE_XML_2              = "uddi_data/joepublisher/businessService2.xml";
    final static String JOE_SERVICE_KEY_2              = "uddi:uddi.joepublisher.com:servicetwo";
    final static String SAM_SERVICE_XML              = "uddi_data/samsyndicator/businessService.xml";
    final static String SAM_SERVICE_KEY              = "uddi:www.samco.com:listingservice";
    
    public final static String RIFTSAW_PROCESS_XML          = "uddi_data/bpel/riftsaw/WSDLPort.xml";
    public final static String RIFTSAW_PROCESS_KEY          = "uddi:riftsaw.jboss.org:ticket-service";
   
    private Log logger = LogFactory.getLog(this.getClass());
	private UDDIPublicationPortType publication = null;
    private UDDIInquiryPortType inquiry = null;
	
	public TckBusinessService(UDDIPublicationPortType publication, 
				  UDDIInquiryPortType inquiry) {
		super();
		this.publication = publication;
		this.inquiry = inquiry;
	}

	public void saveJoePublisherService(String authInfoJoe) {
		saveService(authInfoJoe, JOE_SERVICE_XML, JOE_SERVICE_KEY);
	}
	
	public void updateJoePublisherService(String authInfoJoe, String description) {
		saveService(authInfoJoe, JOE_SERVICE_XML, JOE_SERVICE_KEY, description);
	}
	
	public void saveJoePublisherService2(String authInfoJoe) {
		saveService2(authInfoJoe, JOE_SERVICE_KEY, JOE_SERVICE_XML_2, JOE_SERVICE_KEY_2);
	}

	public void saveJoePublisherServices(String authInfoJoe, int businessInt, int numberOfCopies) {
		saveServices(authInfoJoe, businessInt, JOE_SERVICE_XML, JOE_SERVICE_KEY, numberOfCopies);
	}
	
	public void deleteJoePublisherService(String authInfoJoe) {
		deleteService(authInfoJoe, JOE_SERVICE_KEY);
	}
	
	public void deleteJoePublisherService2(String authInfoJoe) {
		deleteService(authInfoJoe, JOE_SERVICE_KEY_2);
	}

	public void saveNotifierService(String authInfoJoe) {
		saveService(authInfoJoe, NOTIFIER_SERVICE_XML, NOTIFIER_SERVICE_KEY);
	}
	
	public void deleteNotifierService(String authInfoJoe) {
		deleteService(authInfoJoe, NOTIFIER_SERVICE_KEY);
	}

	
	public void deleteJoePublisherServices(String authInfoJoe, int businessInt, int numberOfCopies) {
		deleteServices(authInfoJoe, businessInt,JOE_SERVICE_KEY, numberOfCopies);
	}
	
	public void saveSamSyndicatorService(String authInfoSam) {
		saveService(authInfoSam, SAM_SERVICE_XML, SAM_SERVICE_KEY);
	}
	
	public void saveSamSyndicatorServices(String authInfoSam, int businessInt, int numberOfCopies) {
		saveServices(authInfoSam, businessInt, SAM_SERVICE_XML, SAM_SERVICE_KEY, numberOfCopies);
	}
	
	public void deleteSamSyndicatorService(String authInfoSam) {
		deleteService(authInfoSam, SAM_SERVICE_KEY);
	}
	
	public void deleteSamSyndicatorServices(String authInfoSam, int businessInt, int numberOfCopies) {
		deleteServices(authInfoSam, businessInt, SAM_SERVICE_KEY, numberOfCopies);
	}
	
	public void saveServices(String authInfo, int businessInt, String serviceXML, String serviceKey, int numberOfCopies) {
		SaveService ss = null;
		try {
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)EntityCreator.buildFromDoc(serviceXML, "org.uddi.api_v3");
			String serviceName = bsIn.getName().get(0).getValue();
			String bindingKey = bsIn.getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
			for (int i=0; i<numberOfCopies; i++) {
			    // save the entity
				ss = new SaveService();
				ss.setAuthInfo(authInfo);
				bsIn.getName().get(0).setValue(serviceName + "-" + i);
				bsIn.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY + "-" + businessInt);
				bsIn.setServiceKey(serviceKey + "-" + businessInt + "-" + i);
				bsIn.getBindingTemplates().getBindingTemplate().get(0).setBindingKey(bindingKey + "-" + businessInt + "-" + i);
				bsIn.getBindingTemplates().getBindingTemplate().get(0).setServiceKey(serviceKey + "-" + businessInt + "-" + i);
				ss.getBusinessService().add(bsIn);
				
				BindingTemplate bt = bsIn.getBindingTemplates().getBindingTemplate().get(0);
				if (! bt.getServiceKey().equals(serviceKey + "-" + businessInt + "-" + i)) {
					System.out.println("not the same");
				}
				
				publication.saveService(ss);
				logger.debug("Add service with key " + bsIn.getServiceKey());
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public void saveService(String authInfo, String serviceXML, String serviceKey) {
		saveService(authInfo, serviceXML, serviceKey, null);
	}
	
	public void saveService(String authInfo, String serviceXML, String serviceKey, String description) {
		try {
			// First save the entity
			SaveService ss = new SaveService();
			ss.setAuthInfo(authInfo);
			
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)EntityCreator.buildFromDoc(serviceXML, "org.uddi.api_v3");
			
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
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}
	
	public void saveService2(String authInfo, String serviceKey, String serviceXML2,  String serviceKey2) {
		try {
			// First save the entity
			SaveService ss = new SaveService();
			ss.setAuthInfo(authInfo);
			
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)EntityCreator.buildFromDoc(serviceXML2, "org.uddi.api_v3");
			ss.getBusinessService().add(bsIn);
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