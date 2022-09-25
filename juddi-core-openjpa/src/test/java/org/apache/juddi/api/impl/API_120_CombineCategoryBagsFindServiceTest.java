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
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceList;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Test to verify JUDDI-456
 * 
 * Test does a find_service with the combinedCategoryBags findQualifier set, expects
 * back one service with a serviceKey of uddi:uddi.tompublisher.com:servicetest02.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class API_120_CombineCategoryBagsFindServiceTest 
{
    final static String TOM_PUBLISHER_TMODEL_XML      = "uddi_data/tompublisher/tModelKeyGen.xml";
    final static String TOM_PUBLISHER_TMODEL01_XML 	  = "uddi_data/tompublisher/tModel01.xml";
    final static String TOM_PUBLISHER_TMODEL02_XML 	  = "uddi_data/tompublisher/tModel02.xml";

    final static String TOM_PUBLISHER_TMODEL_KEY      = "uddi:uddi.tompublisher.com:keygenerator";
    final static String TOM_PUBLISHER_TMODEL01_KEY    = "uddi:uddi.tompublisher.com:tmodeltest01";
    final static String TOM_PUBLISHER_TMODEL01_NAME   = "tmodeltest01";
    final static String TOM_PUBLISHER_TMODEL02_KEY    = "uddi:uddi.tompublisher.com:tmodeltest02";

    final static String TOM_BUSINESS1_XML       = "uddi_data/tompublisher/juddi456-business1.xml";
    final static String TOM_BUSINESS2_XML       = "uddi_data/tompublisher/juddi456-business2.xml";
    final static String TOM_BUSINESS5_XML       = "uddi_data/tompublisher/juddi456-business5.xml";
    final static String TOM_BUSINESS1_KEY       = "uddi:uddi.tompublisher.com:businesstest01";
    final static String TOM_BUSINESS2_KEY       = "uddi:uddi.tompublisher.com:businesstest02";
    final static String TOM_BUSINESS5_KEY       = "uddi:uddi.tompublisher.com:businesstest05";

    final static String SERVICE_KEY1 = "uddi:uddi.tompublisher.com:servicetest01";
    final static String SERVICE_KEY5 = "uddi:uddi.tompublisher.com:servicetest05";
    
    final static String TOM_PUBLISHER_SERVICEINFO_NAME = "servicetest01";
    
    private static Log logger = LogFactory.getLog(API_120_CombineCategoryBagsFindServiceTest.class);
	
    private static API_010_PublisherTest api010       = new API_010_PublisherTest();
    private static TckTModel tckTModel                = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckBusiness tckBusiness            = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static UDDIInquiryImpl inquiry            = new UDDIInquiryImpl();
	protected static String authInfoJoe               = null;
	
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
	public void findServiceByCategoryBag() {
		try {
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
			
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS5_XML, TOM_BUSINESS5_KEY, 1);
			try {
				int size = 0;
				ServiceList sl = null;

				FindService fs = new FindService();
				
				//by default keys are ADD, we should only find service 5
                KeyedReference keyRef1 = new KeyedReference();
                keyRef1.setTModelKey(TOM_PUBLISHER_TMODEL01_KEY);
                keyRef1.setKeyValue("value-z");
                
                KeyedReference keyRef2 = new KeyedReference();
                keyRef2.setTModelKey(TOM_PUBLISHER_TMODEL02_KEY);
				keyRef2.setKeyValue("value-x");
				
				CategoryBag cb = new CategoryBag();
				cb.getKeyedReference().add(keyRef1);
				cb.getKeyedReference().add(keyRef2);
				fs.setCategoryBag(cb);
				
				sl = inquiry.findService(fs);
				if (sl.getServiceInfos() == null) {
                    Assert.fail("Should have found one entry on FindService with TModelBag, "
                       + "found " + size);
				}
				size = sl.getServiceInfos().getServiceInfo().size();
				if (size != 1) {
					Assert.fail("Should have found one entry on FindService with TModelBag, "
							+ "found " + size);
				} else {
				    List<ServiceInfo> siList = sl.getServiceInfos().getServiceInfo();
					String serviceKey = siList.get(0).getServiceKey();
					if (!SERVICE_KEY5.equals(serviceKey)) {
					    Assert.fail("Should have found service key " + SERVICE_KEY5
					            + " but found [" + serviceKey + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} finally {
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS5_XML, TOM_BUSINESS5_KEY, 1);
			
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
		}
	}
	
	@Test
	public void findNoServiceByCategoryBag() {
		try {
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
			
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS5_XML, TOM_BUSINESS5_KEY, 1);
			try {
				int size = 0;
				ServiceList sl = null;

				FindService fs = new FindService();
				
				//by default keys are ADD, we should only find service 5
                KeyedReference keyRef1 = new KeyedReference();
                keyRef1.setTModelKey(TOM_PUBLISHER_TMODEL01_KEY);
                keyRef1.setKeyValue("value-y");
               
				CategoryBag cb = new CategoryBag();
				cb.getKeyedReference().add(keyRef1);
				fs.setCategoryBag(cb);
				
				sl = inquiry.findService(fs);
				if (sl.getServiceInfos() != null) {
                    Assert.fail("Should have found no entries on FindService, "
                       + " found " + size);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} finally {
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS5_XML, TOM_BUSINESS5_KEY, 1);
			
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
		}
	}
	/*
	 * For a combinedCategoryBag, we find a service if either the categoryBag on the service
	 * matches, or one of the categoryBags on the bindings.
	 */
	@Test
	public void findServiceByCombinedCategoryBag() {
		try {
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
			
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
			try {
				int size = 0;
				ServiceList sl = null;

				FindService fs = new FindService();
				FindQualifiers fqs = new FindQualifiers();
				fqs.getFindQualifier().add(UDDIConstants.COMBINE_CATEGORY_BAGS);
				fs.setFindQualifiers(fqs);

                KeyedReference keyRef1 = new KeyedReference();
                keyRef1.setTModelKey(TOM_PUBLISHER_TMODEL01_KEY);
                keyRef1.setKeyValue("value-y");
				
				CategoryBag cb = new CategoryBag();
				cb.getKeyedReference().add(keyRef1);
				fs.setCategoryBag(cb);
				
				sl = inquiry.findService(fs);
				if (sl.getServiceInfos() == null) {
                    Assert.fail("Should have found one entry on FindService with TModelBag, "
                       + "found " + size);
				}
				size = sl.getServiceInfos().getServiceInfo().size();
				if (size != 1) {
					Assert.fail("Should have found one entry on FindService with TModelBag, "
							+ "found " + size);
				} else {
				    List<ServiceInfo> siList = sl.getServiceInfos().getServiceInfo();
					String serviceKey = siList.get(0).getServiceKey();
					if (!SERVICE_KEY1.equals(serviceKey)) {
					    Assert.fail("Should have found service key " + SERVICE_KEY1
					            + " but found [" + serviceKey + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} finally {
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
            tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
			
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
		}
	}
	
}
