package org.apache.juddi.v3.tck;

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
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelBag;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Test to verify JUDDI-456
 * 
 * Test does a find_service with the combinedCategoryBags findQualifier set, expects
 * back one service with a serviceKey of uddi:uddi.tompublisher.com:servicetest02.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDI_120_CombineCategoryBagsFindServiceIntegrationTest 
{
    final static String TOM_PUBLISHER_TMODEL_XML      = "uddi_data/tompublisher/tModelKeyGen.xml";
    final static String TOM_PUBLISHER_TMODEL01_XML 	  = "uddi_data/tompublisher/tModel01.xml";
    final static String TOM_PUBLISHER_TMODEL02_XML 	  = "uddi_data/tompublisher/tModel02.xml";

    final static String TOM_PUBLISHER_TMODEL_KEY      = "uddi:uddi.tompublisher.com:keygenerator";
    final static String TOM_PUBLISHER_TMODEL01_KEY      = "uddi:uddi.tompublisher.com:tmodeltest01";
    final static String TOM_PUBLISHER_TMODEL01_NAME 	= "tmodeltest01";
    final static String TOM_PUBLISHER_TMODEL02_KEY      = "uddi:uddi.tompublisher.com:tmodeltest02";

    final static String TOM_BUSINESS1_XML       = "uddi_data/tompublisher/juddi456-business1.xml";
    final static String TOM_BUSINESS2_XML       = "uddi_data/tompublisher/juddi456-business2.xml";
    final static String TOM_BUSINESS1_KEY        = "uddi:uddi.tompublisher.com:businesstest01";
    final static String TOM_BUSINESS2_KEY        = "uddi:uddi.tompublisher.com:businesstest02";

    final static String SERVICE_KEY = "uddi:uddi.tompublisher.com:servicetest02";
    
    final static String TOM_PUBLISHER_SERVICEINFO_NAME = "servicetest01";
    
    private static Log logger = LogFactory.getLog(UDDI_040_BusinessServiceIntegrationTest.class);
	
	protected static TckTModel tckTModel               = null;
	protected static TckTModel tckTModel01             = null;
	protected static TckTModel tckTModel02             = null;	
	
	protected static TckBusiness tckBusiness1          = null;
	protected static TckBusiness tckBusiness2          = null;
	protected static String authInfoJoe                = null;
	
	private static UDDIInquiryPortType inquiry = null;
	private static UDDIClerkManager manager;

	@AfterClass
	public static void stopManager() throws ConfigurationException {
		manager.stop();
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		manager  = new UDDIClerkManager();
		manager.start();
		
		logger.debug("Getting auth tokens..");
		try {
			 Transport transport = manager.getTransport();
        	 UDDISecurityPortType security = transport.getUDDISecurityService();
        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
        	 Assert.assertNotNull(authInfoJoe);
        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
        	 inquiry = transport.getUDDIInquiryService();
        	 
        	 tckTModel  = new TckTModel(publication, inquiry);
        	 tckTModel01 = new TckTModel(publication, inquiry);
        	 tckTModel02 = new TckTModel(publication, inquiry);
        	 tckBusiness1 = new TckBusiness(publication, inquiry);
        	 tckBusiness2 = new TckBusiness(publication, inquiry); 
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test
	public void tompublisher() {
		try {
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel01.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel02.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
			
			tckBusiness1.saveBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
			tckBusiness2.saveBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
			try {
				int size = 0;
				ServiceList sl = null;

				FindService fs = new FindService();
				FindQualifiers fqs = new FindQualifiers();
				fqs.getFindQualifier().add("combineCategoryBags");
				fs.setFindQualifiers(fqs);

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
                                    Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
                                            + "found " + size);
				}
				size = sl.getServiceInfos().getServiceInfo().size();
				if (size != 1) {
					Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
							+ "found " + size);
				} else {
				        List<ServiceInfo> siList = sl.getServiceInfos().getServiceInfo();
					String serviceKey = siList.get(0).getServiceKey();
					if (!SERVICE_KEY.equals(serviceKey)) {
					    Assert.fail("Should have found service key " + SERVICE_KEY
					            + " but found [" + serviceKey + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} finally {
                        tckBusiness1.deleteBusinesses(authInfoJoe, TOM_BUSINESS1_XML, TOM_BUSINESS1_KEY, 1);
                        tckBusiness2.deleteBusinesses(authInfoJoe, TOM_BUSINESS2_XML, TOM_BUSINESS2_KEY, 1);
			
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel01.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel02.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

		}
	}
	
}
