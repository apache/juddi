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
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckBusiness 
{
	final static String JOE_BUSINESS_XML        = "uddi_data/joepublisher/businessEntity.xml";
    final static String JOE_BUSINESS_KEY        = "uddi:juddi.apache.org:joepublisher:businessone";
    final static String SAM_BUSINESS_XML        = "uddi_data/samsyndicator/businessEntity.xml";
    final static String SAM_BUSINESS_KEY        = "uddi:juddi.apache.org:samco:repository:samco";
    
	private Logger logger = Logger.getLogger(this.getClass());
	private UDDIPublicationPortType publication = null;
    private UDDIInquiryPortType inquiry = null;
	
	public TckBusiness(UDDIPublicationPortType publication, 
				  UDDIInquiryPortType inquiry) {
		super();
		this.publication = publication;
		this.inquiry = inquiry;
	}

	public void saveSamSyndicatorBusiness(String authInfoSam) {
		saveBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
	}
	
	public void deleteSamSyndicatorBusiness(String authInfoSam) {
		deleteBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
	}
	
	public void saveJoePublisherBusiness(String authInfoJoe) {
    	saveBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
    }
    
	public void deleteJoePublisherBusiness(String authInfoJoe) {
    	deleteBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
    }
	 
	private void saveBusiness(String authInfo, String businessXML, String businessKey) {
		try {
			SaveBusiness sb = new SaveBusiness();
			sb.setAuthInfo(authInfo);

			BusinessEntity beIn = (BusinessEntity)EntityCreator.buildFromDoc(businessXML, "org.uddi.api_v3");
			sb.getBusinessEntity().add(beIn);
			publication.saveBusiness(sb);
	
			// Now get the entity and check the values
			GetBusinessDetail gb = new GetBusinessDetail();
			gb.getBusinessKey().add(businessKey);
			BusinessDetail bd = inquiry.getBusinessDetail(gb);
			List<BusinessEntity> beOutList = bd.getBusinessEntity();
			BusinessEntity beOut = beOutList.get(0);

			assertEquals(beIn.getBusinessKey(), beOut.getBusinessKey());
			
			TckValidator.checkNames(beIn.getName(), beOut.getName());
			TckValidator.checkDescriptions(beIn.getDescription(), beOut.getDescription());
			TckValidator.checkDiscoveryUrls(beIn.getDiscoveryURLs(), beOut.getDiscoveryURLs());
			TckValidator.checkContacts(beIn.getContacts(), beOut.getContacts());
			TckValidator.checkCategories(beIn.getCategoryBag(), beOut.getCategoryBag());
			
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}

	}
	
	
	private void deleteBusiness(String authInfo, String businessXML, String businessKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteBusiness db = new DeleteBusiness();
			db.setAuthInfo(authInfo);
			db.getBusinessKey().add(businessKey);
			publication.deleteBusiness(db);
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}

	}
}