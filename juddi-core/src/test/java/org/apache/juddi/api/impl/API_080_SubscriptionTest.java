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
import org.apache.juddi.v3.tck.TckBindingTemplate;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckSubscription;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_080_SubscriptionTest 
{
	private static Log logger = LogFactory.getLog(API_080_SubscriptionTest.class);

	private static API_010_PublisherTest api010 = new API_010_PublisherTest();
	private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBindingTemplate tckBindingTemplate = new TckBindingTemplate(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckSubscription tckSubscription = new TckSubscription(new UDDISubscriptionImpl(), new UDDISecurityImpl());

	private static String authInfoJoe = null;
	private static String authInfoSam = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		Registry.start();
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());

			api010.saveSamSyndicator();
			authInfoSam = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getSamPublisherId(),  TckPublisher.getSamPassword());

			String authInfoUDDI  = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getUDDIPublisherId(),  TckPublisher.getUDDIPassword());
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
	public void joePublisher() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
			tckSubscription.saveJoePublisherSubscription(authInfoJoe);
			tckSubscription.getJoePublisherSubscriptionResults(authInfoJoe);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}
 		finally {
			tckSubscription.deleteJoePublisherSubscription(authInfoJoe);
			tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
		
	}

	@Test
	public void samSyndicator() {
		try {
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusinessService.saveSamSyndicatorService(authInfoSam);
			tckSubscription.saveSamSyndicatorSubscription(authInfoSam);
			tckSubscription.getSamSyndicatorSubscriptionResults(authInfoSam);
		} 
		finally {
			tckSubscription.deleteSamSyndicatorSubscription(authInfoSam);
			tckBusinessService.deleteSamSyndicatorService(authInfoSam);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
		
	}

	@Test
	public void samSyndicatorWithChunkingOnFind() {
		try {
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusinessService.saveSamSyndicatorService(authInfoSam);
			tckSubscription.saveSamSyndicatorSubscriptionWithChunkingOnFind(authInfoSam);
			tckSubscription.getSamSyndicatorSubscriptionResultsWithChunkingOnFind(authInfoSam);
		} 
		finally {
			tckSubscription.deleteSamSyndicatorSubscriptionWithChunkingOnFind(authInfoSam);
			tckBusinessService.deleteSamSyndicatorService(authInfoSam);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
		
	}
	
	@Test
	public void samSyndicatorWithChunkingOnGet() {
		try {
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
			tckBusinessService.saveSamSyndicatorService(authInfoSam);
			tckSubscription.saveSamSyndicatorSubscriptionWithChunkingOnGet(authInfoSam);
			tckSubscription.getSamSyndicatorSubscriptionResultsWithChunkingOnGet(authInfoSam);
		} 
		finally {
			tckSubscription.deleteSamSyndicatorSubscriptionWithChunkingOnGet(authInfoSam);
			tckBusinessService.deleteSamSyndicatorService(authInfoSam);
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
		}
		
	}
	
}
