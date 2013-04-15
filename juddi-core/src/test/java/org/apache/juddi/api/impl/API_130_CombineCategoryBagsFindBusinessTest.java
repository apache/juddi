package org.apache.juddi.api.impl;

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

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.KeyedReference;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Test to verify JUDDI-456, does a findBusiness query with combineCategoryBags findQualifier.
 * 
 * Expected result is one returned businessEntity with a businessKey of uddi:uddi.tompublisher.com:businesstest04
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class API_130_CombineCategoryBagsFindBusinessTest 
{
	final static String TOM_PUBLISHER_TMODEL_XML      = "uddi_data/tompublisher/tModelKeyGen.xml";
	final static String TOM_PUBLISHER_TMODEL01_XML 	  = "uddi_data/tompublisher/tModel01.xml";
	final static String TOM_PUBLISHER_TMODEL02_XML 	  = "uddi_data/tompublisher/tModel02.xml";

	final static String TOM_PUBLISHER_TMODEL_KEY      = "uddi:uddi.tompublisher.com:keygenerator";
	final static String TOM_PUBLISHER_TMODEL01_KEY      = "uddi:uddi.tompublisher.com:tmodeltest01";
	final static String TOM_PUBLISHER_TMODEL01_NAME 	= "tmodeltest01";
	final static String TOM_PUBLISHER_TMODEL02_KEY      = "uddi:uddi.tompublisher.com:tmodeltest02";

	final static String TOM_BUSINESS1_XML       = "uddi_data/tompublisher/juddi456-business3.xml";
	final static String TOM_BUSINESS2_XML       = "uddi_data/tompublisher/juddi456-business4.xml";
	final static String TOM_BUSINESS545_XML       = "uddi_data/tompublisher/juddi545-business.xml";

	final static String TOM_BUSINESS1_KEY        = "uddi:uddi.tompublisher.com:businesstest03";
	final static String TOM_BUSINESS2_KEY        = "uddi:uddi.tompublisher.com:businesstest04";
	final static String TOM_BUSINESS545_KEY        = "uddi:uddi.tompublisher.com:businesstest545";

	final static String BUSINESS_KEY    = "uddi:uddi.tompublisher.com:businesstest04";
	final static String BUSINESS_KEY545 = "uddi:uddi.tompublisher.com:businesstest545";

	private static Log logger = LogFactory.getLog(API_130_CombineCategoryBagsFindBusinessTest.class);

	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
    private static TckTModel tckTModel                = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckBusiness tckBusiness      = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static UDDIInquiryImpl inquiry             = new UDDIInquiryImpl();
    
	protected static String authInfoJoe                = null;

	@AfterClass
	public static void stopManager() throws ConfigurationException {
		Registry.stop();
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		Registry.start();
		
		logger.debug("Getting auth tokens..");
		try {
			api010.saveJoePublisher();
			api010.saveSamSyndicator();
			UDDISecurityPortType security      = new UDDISecurityImpl();
        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
        	 Assert.assertNotNull(authInfoJoe);
    
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test
	public void findBusiness() {
		try {
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

			tckBusiness.saveBusiness(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY);
			tckBusiness.saveBusiness(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY);
			try {
				int size = 0;
				BusinessList bl = null;

				FindBusiness fb = new FindBusiness();
				FindQualifiers fqs = new FindQualifiers();
				fqs.getFindQualifier().add("combineCategoryBags");
				fb.setFindQualifiers(fqs);

				KeyedReference keyRef1 = new KeyedReference();
				keyRef1.setTModelKey(TOM_PUBLISHER_TMODEL01_KEY);
				keyRef1.setKeyValue("value-z");

				KeyedReference keyRef2 = new KeyedReference();
				keyRef2.setTModelKey(TOM_PUBLISHER_TMODEL02_KEY);
				keyRef2.setKeyValue("value-x");

				CategoryBag cb = new CategoryBag();
				cb.getKeyedReference().add(keyRef1);
				cb.getKeyedReference().add(keyRef2);
				fb.setCategoryBag(cb);

				bl = inquiry.findBusiness(fb);
				if (bl.getBusinessInfos() == null) {
					Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
							+ "found " + size);
				}
				size = bl.getBusinessInfos().getBusinessInfo().size();
				if (size != 1) {
					Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
							+ "found " + size);
				} else {
					List<BusinessInfo> biList = bl.getBusinessInfos().getBusinessInfo();
					String businessKey = biList.get(0).getBusinessKey();
					if (!BUSINESS_KEY.equals(businessKey)) {
						Assert.fail("Should have found business key " + BUSINESS_KEY
								+ " but found [" + businessKey + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} finally {
			tckBusiness.deleteBusiness(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY);
			tckBusiness.deleteBusiness(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY);

			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

		}
	}
	
	@Test
	public void findBusinessJUDDI545() {
		try {
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

			tckBusiness.saveBusiness(authInfoJoe, TOM_BUSINESS545_XML, TOM_BUSINESS545_KEY);
			
			try {
				int size = 0;
				BusinessList bl = null;

				FindBusiness fb = new FindBusiness();
				FindQualifiers fqs = new FindQualifiers();
				fqs.getFindQualifier().add("combineCategoryBags");
				fb.setFindQualifiers(fqs);

				KeyedReference keyRef1 = new KeyedReference();
				keyRef1.setTModelKey(TOM_PUBLISHER_TMODEL01_KEY);
				keyRef1.setKeyValue("value-z");

				KeyedReference keyRef2 = new KeyedReference();
				keyRef2.setTModelKey(TOM_PUBLISHER_TMODEL02_KEY);
				keyRef2.setKeyValue("value-x");
				
				KeyedReference keyRef3 = new KeyedReference();
				keyRef3.setTModelKey(TOM_PUBLISHER_TMODEL01_KEY);
				keyRef3.setKeyValue("value-y");

				CategoryBag cb = new CategoryBag();
				cb.getKeyedReference().add(keyRef1);
				cb.getKeyedReference().add(keyRef2);
				cb.getKeyedReference().add(keyRef3);
				fb.setCategoryBag(cb);

				bl = inquiry.findBusiness(fb);
				if (bl.getBusinessInfos() == null) {
					Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
							+ "found " + size);
				}
				size = bl.getBusinessInfos().getBusinessInfo().size();
				if (size != 1) {
					Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
							+ "found " + size);
				} else {
					List<BusinessInfo> biList = bl.getBusinessInfos().getBusinessInfo();
					String businessKey = biList.get(0).getBusinessKey();
					if (!BUSINESS_KEY545.equals(businessKey)) {
						Assert.fail("Should have found business key " + BUSINESS_KEY545
								+ " but found [" + businessKey + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} finally {
			tckBusiness.deleteBusiness(authInfoJoe, TOM_BUSINESS545_XML, TOM_BUSINESS545_KEY);

			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

		}
	}	
}
