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
package org.uddi.api_v3.tck;

import static junit.framework.Assert.assertEquals;

import java.util.List;

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
	final static String JOE_SERVICE_XML              = "uddi_data/joepublisher/businessService.xml";
    final static String JOE_SERVICE_KEY              = "uddi:juddi.apache.org:joepublisher:serviceone";
    final static String SAM_SERVICE_XML              = "uddi_data/samsyndicator/businessService.xml";
    final static String SAM_SERVICE_KEY              = "uddi:juddi.apache.org:samco:repository:listingservice";
   
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
	
	public void deleteJoePublisherService(String authInfoJoe) {
		deleteService(authInfoJoe, JOE_SERVICE_KEY);
	}
	
	public void saveSamSyndicatorService(String authInfoSam) {
		saveService(authInfoSam, SAM_SERVICE_XML, SAM_SERVICE_KEY);
	}
	
	public void deleteSamSyndicatorService(String authInfoSam) {
		deleteService(authInfoSam, SAM_SERVICE_KEY);
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

}