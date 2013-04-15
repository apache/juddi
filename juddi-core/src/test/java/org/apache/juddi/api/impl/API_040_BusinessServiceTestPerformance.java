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
package org.apache.juddi.api.impl;

import java.rmi.RemoteException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_040_BusinessServiceTestPerformance
{	
	private static Log logger                        = LogFactory.getLog(API_040_BusinessServiceTest.class);
	
	private static API_010_PublisherTest api010      = new API_010_PublisherTest();
	protected static TckTModel tckTModel               = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	protected static TckBusiness tckBusiness           = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	protected static TckBusinessService tckBusinessService  = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static UDDIInquiryImpl inquiry             = new UDDIInquiryImpl();
	
	int numberOfBusinesses=100;
	int numberOfServices=100;
	
	protected static String authInfoJoe                = null;
	protected static String authInfoSam                = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		Registry.start();
		logger.debug("Getting auth tokens..");
		try {
			api010.saveJoePublisher();
			UDDISecurityPortType security      = new UDDISecurityImpl();
			authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
			String authInfoUDDI  = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(),  TckPublisher.getUDDIPassword());
			tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
			tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	@Test
	public void find20Businesses() throws DispositionReportFaultMessage {
		tckTModel.saveJoePublisherTmodel(authInfoJoe);
		long startSave = System.currentTimeMillis();
		//loading up 100 businesses, with a 100 services each
		tckBusiness.saveJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
		for (int i=0; i<numberOfBusinesses; i++) {
			tckBusinessService.saveJoePublisherServices(authInfoJoe, i, numberOfServices);
		}
		long saveDuration = System.currentTimeMillis() - startSave;
		System.out.println("Saved " + numberOfBusinesses + " businesses with each " + numberOfServices + " services in " + saveDuration + "ms");
		
		//find 20 businesses by name
		FindBusiness fb = new FindBusiness();
		org.uddi.api_v3.FindQualifiers apiFq = new org.uddi.api_v3.FindQualifiers();
		apiFq.getFindQualifier().add(FindQualifiers.APPROXIMATE_MATCH);
		apiFq.getFindQualifier().add(FindQualifiers.CASE_INSENSITIVE_MATCH);
		fb.setFindQualifiers(apiFq);
		Name name = new Name();
		name.setValue("John%");
		fb.getName().add(name);
		fb.setMaxRows(20);
		long startFind = System.currentTimeMillis();
		BusinessList result = inquiry.findBusiness(fb);
		long findDuration = System.currentTimeMillis() - startFind;
		System.out.println("Find 20 businesses took "  +  findDuration + "ms. Size=" + result.getBusinessInfos().getBusinessInfo().size());
		// it takes less then 1 second, make sure it stays faster then 5 seconds
		//Assert.assertTrue(findDuration < 5000);
				
		FindService fs = new FindService();
		fs.setFindQualifiers(apiFq);
		name.setValue("Service One%");
		fs.getName().add(name);
		startFind = System.currentTimeMillis();
		//this will match ALL services (100 * 100 =) 10,000 services
		int all = numberOfBusinesses * numberOfServices;
		System.out.println("Matching " + all+  " services");
		ServiceList serviceList = inquiry.findService(fs);
		findDuration = System.currentTimeMillis() - startFind;
		System.out.println("Find " + all + " services took "  +  findDuration + "ms. Size=" + serviceList.getServiceInfos().getServiceInfo().size());

		long startDelete = System.currentTimeMillis();
		for (int i=0; i<numberOfBusinesses; i++) {
			tckBusinessService.deleteJoePublisherServices(authInfoJoe, i, numberOfServices);
		}
		long deleteDuration = System.currentTimeMillis() - startDelete;
		System.out.println("Delete all business and services in " + deleteDuration + "ms");
		tckBusiness.deleteJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
		tckTModel.deleteJoePublisherTmodel(authInfoJoe);
	}
	
}
