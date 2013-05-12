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
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.TModelBag;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Test to verify JUDDI-398
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDI_110_FindBusinessIntegrationTest 
{
	final static String TOM_PUBLISHER_TMODEL_XML      = "uddi_data/tompublisher/tModelKeyGen.xml";
	final static String TOM_PUBLISHER_TMODEL01_XML 	  = "uddi_data/tompublisher/tModel01.xml";
	final static String TOM_PUBLISHER_TMODEL02_XML 	  = "uddi_data/tompublisher/tModel02.xml";

	final static String TOM_PUBLISHER_TMODEL_KEY      = "uddi:uddi.tompublisher.com:keygenerator";
	final static String TOM_PUBLISHER_TMODEL01_KEY      = "uddi:uddi.tompublisher.com:tmodeltest01";
	final static String TOM_PUBLISHER_TMODEL01_NAME 	= "tmodeltest01";
	final static String TOM_PUBLISHER_TMODEL02_KEY      = "uddi:uddi.tompublisher.com:tmodeltest02";

	final static String TOM_BUSINESS_XML        = "uddi_data/tompublisher/businessEntity.xml";
    final static String TOM_BUSINESS_KEY        = "uddi:uddi.tompublisher.com:businesstest01";
	
    final static String TOM_PUBLISHER_SERVICEINFO_NAME = "servicetest01";
    
    private static Log logger = LogFactory.getLog(UDDI_040_BusinessServiceIntegrationTest.class);
	
	protected static TckTModel tckTModel               = null;
	protected static TckTModel tckTModel01             = null;
	protected static TckTModel tckTModel02             = null;	
	
	protected static TckBusiness tckBusiness           = null;
	
	protected static String authInfoJoe               = null;
	
	private static UDDIInquiryPortType inquiry = null;
	private static UDDIClient manager;

	@AfterClass
	public static void stopManager() throws ConfigurationException {
		manager.stop();
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		manager  = new UDDIClient();
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
        	 tckBusiness = new TckBusiness(publication, inquiry);
        	  
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test
	public void findBusinessByTModelBag() {
		try {
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
			
			tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS_XML, TOM_BUSINESS_KEY, 1);
			
			try {
				int size = 0;
				BusinessList bl = null;
	
				FindBusiness fbb = new FindBusiness();
				TModelBag tmb = new TModelBag();
				tmb.getTModelKey().add(TOM_PUBLISHER_TMODEL01_KEY);
				fbb.setTModelBag(tmb);
				bl = inquiry.findBusiness(fbb);
				size = bl.getBusinessInfos().getBusinessInfo().size();
				if (size != 1) {
					Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
							+ "found " + size);
				} else {
					List<BusinessInfo> biList = bl.getBusinessInfos().getBusinessInfo();
					if (biList.get(0).getServiceInfos().getServiceInfo().size() != 2) {
						Assert.fail("Should have found two ServiceInfos");
					} else {
							List<ServiceInfo> siList = biList.get(0).getServiceInfos().getServiceInfo();
							ServiceInfo si = siList.get(0);
							if (!TOM_PUBLISHER_SERVICEINFO_NAME.equals(si.getName().get(0).getValue())) {
								Assert.fail("Should have found " + TOM_PUBLISHER_TMODEL01_NAME + " as the "
										+ "ServiceInfo name, found " + si.getName().get(0).getValue());
							}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} finally {
			tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS_XML, TOM_BUSINESS_KEY, 1);
			
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

		}
	}
	
}
