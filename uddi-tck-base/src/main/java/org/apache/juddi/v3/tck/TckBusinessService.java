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

import org.apache.juddi.jaxb.EntityCreator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.ServiceDetail;
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
    final static String SAM_SERVICE_XML              = "uddi_data/samsyndicator/businessService.xml";
    final static String SAM_SERVICE_KEY              = "uddi:www.samco.com:listingservice";
   
	private Logger logger = Logger.getLogger(this.getClass());
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

	public void saveJoePublisherServices(String authInfoJoe, int numberOfCopies) {
		saveServices(authInfoJoe, JOE_SERVICE_XML, JOE_SERVICE_KEY, numberOfCopies);
	}
	
	public void deleteJoePublisherService(String authInfoJoe) {
		deleteService(authInfoJoe, JOE_SERVICE_KEY);
	}

	public void saveNotifierService(String authInfoJoe) {
		saveService(authInfoJoe, NOTIFIER_SERVICE_XML, NOTIFIER_SERVICE_KEY);
	}
	
	public void deleteNotifierService(String authInfoJoe) {
		deleteService(authInfoJoe, NOTIFIER_SERVICE_KEY);
	}

	
	public void deleteJoePublisherServices(String authInfoJoe, int numberOfCopies) {
		deleteServices(authInfoJoe, JOE_SERVICE_KEY, numberOfCopies);
	}
	
	public void saveSamSyndicatorService(String authInfoSam) {
		saveService(authInfoSam, SAM_SERVICE_XML, SAM_SERVICE_KEY);
	}
	
	public void saveSamSyndicatorServices(String authInfoSam, int numberOfCopies) {
		saveServices(authInfoSam, SAM_SERVICE_XML, SAM_SERVICE_KEY, numberOfCopies);
	}
	
	public void deleteSamSyndicatorService(String authInfoSam) {
		deleteService(authInfoSam, SAM_SERVICE_KEY);
	}
	
	public void deleteSamSyndicatorServices(String authInfoSam, int numberOfCopies) {
		deleteServices(authInfoSam, SAM_SERVICE_KEY, numberOfCopies);
	}
	
	private void saveServices(String authInfo, String serviceXML, String serviceKey, int numberOfCopies) {
		try {
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)EntityCreator.buildFromDoc(serviceXML, "org.uddi.api_v3");
			String serviceName = bsIn.getName().get(0).getValue();
			String bindingKey = bsIn.getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
			for (int i=0; i<numberOfCopies; i++) {
			    // save the entity
				SaveService ss = new SaveService();
				ss.setAuthInfo(authInfo);
				bsIn.getName().get(0).setValue(serviceName + "-" + i);
				
				bsIn.setServiceKey(serviceKey + "-" + i);
				bsIn.getBindingTemplates().getBindingTemplate().get(0).setBindingKey(bindingKey + "-" + i);
				ss.getBusinessService().add(bsIn);
				publication.saveService(ss);
				logger.debug("Add service with key " + bsIn.getServiceKey());
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	
	private void saveService(String authInfo, String serviceXML, String serviceKey) {
		try {
			// First save the entity
			SaveService ss = new SaveService();
			ss.setAuthInfo(authInfo);
			
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)EntityCreator.buildFromDoc(serviceXML, "org.uddi.api_v3");
			ss.getBusinessService().add(bsIn);
			publication.saveService(ss);
			
			// Now get the entity and check the values
			GetServiceDetail gs = new GetServiceDetail();
			gs.getServiceKey().add(serviceKey);
			ServiceDetail sd = inquiry.getServiceDetail(gs);
			List<BusinessService> bsOutList = sd.getBusinessService();
			BusinessService bsOut = bsOutList.get(0);

			assertEquals(bsIn.getServiceKey(), bsOut.getServiceKey());
			
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
	
	private void deleteService(String authInfo, String serviceKey) {
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
	
	private void deleteServices(String authInfo, String serviceKey, int numberOfCopies) {
		try {
			for (int i=0; i<numberOfCopies; i++) {
				// Delete the entity and make sure it is removed
				DeleteService ds = new DeleteService();
				ds.setAuthInfo(authInfo);
				
				ds.getServiceKey().add(serviceKey + "-" + i);
				publication.deleteService(ds);
				logger.debug("Deleted Service with key " + serviceKey + "-" + i);
			}
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}

}