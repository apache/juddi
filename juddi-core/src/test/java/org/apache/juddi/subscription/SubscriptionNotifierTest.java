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
package org.apache.juddi.subscription;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api.impl.API_010_PublisherTest;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.model.Subscription;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.subscription.SubscriptionNotifier;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
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
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class SubscriptionNotifierTest 
{
	private static Logger logger = Logger.getLogger(SubscriptionNotifierTest.class);
	private static API_010_PublisherTest api010 = new API_010_PublisherTest();
	private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBindingTemplate tckBindingTemplate = new TckBindingTemplate(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckSubscription tckSubscription = new TckSubscription(new UDDISubscriptionImpl(), new UDDISecurityImpl());

	private static String authInfoJoe = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.JOE_PUBLISHER_ID,  TckPublisher.JOE_PUBLISHER_CRED);
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
			tckSubscription.saveJoePublisherSubscription(authInfoJoe);
			tckSubscription.getJoePublisherSubscriptionResults(authInfoJoe);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	@Test
	public void testGetSubscriptionResults() 
		throws ConfigurationException, MalformedURLException, DispositionReportFaultMessage, DatatypeConfigurationException
	{
		SubscriptionNotifier notifier = new SubscriptionNotifier();
		notifier.cancel();
		Collection<Subscription> subscriptions = notifier.getAllSubscriptions();
		Assert.assertEquals(1, subscriptions.size());
		Subscription subscription = subscriptions.iterator().next();
		GetSubscriptionResults getSubscriptionResults = notifier.buildGetSubscriptionResults(subscription);
		getSubscriptionResults.setSubscriptionKey(subscription.getSubscriptionKey());
		UddiEntityPublisher publisher = new UddiEntityPublisher();
		publisher.setAuthorizedName(subscription.getAuthorizedName());
		SubscriptionResultsList resultList = notifier.getSubscriptionImpl().getSubscriptionResults(getSubscriptionResults, publisher);
		//We're expecting a changed service (since it was added in the 
		Assert.assertNotNull(resultList.getServiceList());
		//We should detect these changes.
		boolean hasChanges = notifier.resultListContainsChanges(resultList);
		Assert.assertTrue(hasChanges);
		System.out.print(resultList);
		notifier.notify(getSubscriptionResults,resultList);
	}
	
	
	@AfterClass
	public static void teardown() {
		tckSubscription.deleteJoePublisherSubscription(authInfoJoe);
		tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
		tckBusinessService.deleteJoePublisherService(authInfoJoe);
		tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
		tckTModel.deleteJoePublisherTmodel(authInfoJoe);
	}
	
}
