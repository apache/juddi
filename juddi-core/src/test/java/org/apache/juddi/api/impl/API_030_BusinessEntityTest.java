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
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_030_BusinessEntityTest {
	
	private static Log logger                   = LogFactory.getLog(API_030_BusinessEntityTest.class);
	
	private static API_010_PublisherTest api010 = new API_010_PublisherTest();
	private static TckTModel tckTModel          = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness      = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckFindEntity tckFindEntity  = new TckFindEntity(new UDDIInquiryImpl());
	private static String authInfoJoe           = null;
	private static String authInfoSam           = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		Registry.start();
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			api010.saveSamSyndicator();
			UDDISecurityPortType security      = new UDDISecurityImpl();
			authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
			authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(),  TckPublisher.getSamPassword());
			String authInfoUDDI  = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(),  TckPublisher.getUDDIPassword());
			tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
			tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	
	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		Registry.stop();
	}
	
	@Test
	public void testJoePublisherBusinessEntity() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
	
	@Test
	public void testSamSyndicatorBusiness() {
		try {
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
		} finally {
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
	
	@Test
	public void testJoePublisherBusinessEntitySignature() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusinessX509Signature(authInfoJoe);
			tckFindEntity.findAllBusiness();
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			
			//find the signed business
			tckFindEntity.findAllSignedBusiness();
			
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			
		} finally {
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
	
	
}
