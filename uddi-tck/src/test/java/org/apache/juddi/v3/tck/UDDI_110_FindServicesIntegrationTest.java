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
import org.apache.juddi.ClassUtil;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.InVMTransport;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.Name;
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
public class UDDI_110_FindServicesIntegrationTest 
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
    
    private static Logger logger                     = Logger.getLogger(UDDI_040_BusinessServiceIntegrationTest.class);
	
	protected static TckTModel tckTModel               = null;
	protected static TckTModel tckTModel01             = null;
	protected static TckTModel tckTModel02             = null;	
	
	protected static TckBusiness tckBusiness           = null;
	
	protected static String authInfoTom               = null;
	
	private static UDDIInquiryPortType inquiry = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		String clazz = UDDIClientContainer.getDefaultTransportClass();
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.start();
		}
		logger.debug("Getting auth tokens..");
		try {
	         Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance("default");
	        	 
	        	 UDDISecurityPortType security = transport.getUDDISecurityService();
	        	 authInfoTom = TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID,  TckPublisher.JOE_PUBLISHER_CRED);
	        	 Assert.assertNotNull(authInfoTom);
	        	 
	        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
	        	 inquiry = transport.getUDDIInquiryService();
	        	 
	        	 tckTModel  = new TckTModel(publication, inquiry);
	        	 tckTModel01 = new TckTModel(publication, inquiry);
	        	 tckTModel02 = new TckTModel(publication, inquiry);
	        	 tckBusiness = new TckBusiness(publication, inquiry);
	         } else {
	        	 Assert.fail();
	         }
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
			 Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		String clazz = UDDIClientContainer.getDefaultTransportClass();
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.stop();
		}
	}
	
	@Test
	public void tompublisher() {
		try {
			tckTModel.saveTModel(authInfoTom, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel01.saveTModel(authInfoTom, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel02.saveTModel(authInfoTom, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);
			
			tckBusiness.saveBusinesses(authInfoTom, TOM_BUSINESS_XML, TOM_BUSINESS_KEY, 1);
			
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
					if (biList.get(0).getServiceInfos().getServiceInfo().size() != 1) {
						Assert.fail("Should have found only one ServiceInfo");
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
			tckBusiness.deleteBusinesses(authInfoTom, TOM_BUSINESS_XML, TOM_BUSINESS_KEY, 1);
			
			tckTModel.deleteTModel(authInfoTom, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
			tckTModel01.deleteTModel(authInfoTom, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
			tckTModel02.deleteTModel(authInfoTom, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

		}
	}
	
}
