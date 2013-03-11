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

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
import java.rmi.RemoteException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckPublisherAssertion;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDISecurityPortType;

public class API_060_PublisherAssertionTest {
	
	private static Log logger = LogFactory.getLog(API_060_PublisherAssertionTest.class);
    	
	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
	private static TckTModel tckTModel                = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness            = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckPublisherAssertion tckAssertion = new TckPublisherAssertion(new UDDIPublicationImpl());
	private static TckFindEntity tckFindEntity        = new TckFindEntity(new UDDIInquiryImpl());
	private static String authInfoJoe                 = null;
	private static String authInfoSam                 = null;
	private static String authInfoMary                = null;
	
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
			authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(),  TckPublisher.getMaryPassword());
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
	public void testJoepublisherToSamSyndicator() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);	
			tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
	/**
	 * This test should find no publisher assertions because we only save them
	 * from the joe publisher side.
	 */
	@Test
	public void testFindNoAssertions() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckTModel.saveMaryPublisherTmodel(authInfoMary);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusiness.saveMaryPublisherBusiness(authInfoMary);
			tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.saveJoePublisherPublisherAssertion2(authInfoJoe);
			
			tckFindEntity.findRelatedBusiness_sortByName(true);
			tckFindEntity.findRelatedBusinessToKey(true);
			tckFindEntity.findRelatedBusinessFromKey(true);
			
			tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.deleteJoePublisherPublisherAssertion2(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
			tckTModel.deleteMaryPublisherTmodel(authInfoMary);
		}
	}
	
	/**
	 * This test should find 2 publisher assertions.
	 */
	@Test
	public void testFindAssertions() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckTModel.saveMaryPublisherTmodel(authInfoMary);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusiness.saveMaryPublisherBusiness(authInfoMary);
			tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.saveJoePublisherPublisherAssertion2(authInfoJoe);
			tckAssertion.saveSamPublisherPublisherAssertion(authInfoSam);
			tckAssertion.saveMaryPublisherPublisherAssertion(authInfoMary);
			
			tckFindEntity.findRelatedBusiness_sortByName(false);
			tckFindEntity.findRelatedBusinessToKey(false);
			tckFindEntity.findRelatedBusinessFromKey(false);
			
			tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
			tckAssertion.deleteJoePublisherPublisherAssertion2(authInfoJoe);
			
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
			tckTModel.deleteMaryPublisherTmodel(authInfoMary);
		}
	}
}
