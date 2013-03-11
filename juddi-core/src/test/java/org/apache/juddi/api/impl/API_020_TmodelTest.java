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
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_020_TmodelTest {
	
	private static TckTModel tckTModel                = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static Log logger                         = LogFactory.getLog(API_020_TmodelTest.class);
	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
	private static String authInfoJoe                 = null;
	private static String authInfoSam                 = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException, RemoteException {
		Registry.start();
		logger.debug("Getting auth tokens..");
		try {
			api010.saveJoePublisher();
			api010.saveSamSyndicator();
			UDDISecurityPortType security      = new UDDISecurityImpl();
			authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
			authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(),  TckPublisher.getSamPassword());
		} catch (DispositionReportFaultMessage e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	
	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		Registry.stop();
	}
	
	@Test
	public void testJoePublisherTmodel() {
		tckTModel.saveJoePublisherTmodel(authInfoJoe);
		
		//Now if we use a finder it should be found.
		TModelList tModelList = tckTModel.findJoeTModelDetail();
		Assert.assertNotNull(tModelList.getTModelInfos());
		
		tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		
		//Even if it deleted you should still be able to access it through a getTModelDetail
		TModelDetail detail = tckTModel.getJoePublisherTmodel(authInfoJoe);
		Assert.assertNotNull(detail.getTModel());
		
		//However if we use a finder it should not be found.
		TModelList tModelList2 = tckTModel.findJoeTModelDetail();
		Assert.assertNull(tModelList2.getTModelInfos());
		
		//Make sure none of the found key generators is Joe's key generator
		TModelList tModelList3 = tckTModel.findJoeTModelDetailByCategoryBag();
		for (TModelInfo tModelInfo : tModelList3.getTModelInfos().getTModelInfo()) {
			Assert.assertFalse("uddi:uddi.joepublisher.com:keygenerator".equals(tModelInfo.getTModelKey()));
		}
	}
	
	@Test
	public void testSamSyndicatorTmodelTest() {
		tckTModel.saveSamSyndicatorTmodel(authInfoSam);
		tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
	}	
	
}
