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

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.tck.TckBindingTemplate;
import org.uddi.api_v3.tck.TckBusiness;
import org.uddi.api_v3.tck.TckBusinessService;
import org.uddi.api_v3.tck.TckPublisher;
import org.uddi.api_v3.tck.TckSecurity;
import org.uddi.api_v3.tck.TckSubscription;
import org.uddi.api_v3.tck.TckTModel;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_080_SubscriptionTest 
{
	private static Logger logger = Logger.getLogger(API_080_SubscriptionTest.class);

	private static API_010_PublisherTest api010 = new API_010_PublisherTest();
	private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBindingTemplate tckBindingTemplate = new TckBindingTemplate(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckSubscription tckSubscription = new TckSubscription(new UDDISubscriptionImpl(), new UDDISecurityImpl());

	private static String authInfoJoe = null;
	private static String authInfoSam = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.JOE_PUBLISHER_ID,  TckPublisher.JOE_PUBLISHER_CRED);

			api010.saveSamSyndicator();
			authInfoSam = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.SAM_SYNDICATOR_ID,  TckPublisher.SAM_SYNDICATOR_CRED);

		} catch (DispositionReportFaultMessage e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
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

	
}
