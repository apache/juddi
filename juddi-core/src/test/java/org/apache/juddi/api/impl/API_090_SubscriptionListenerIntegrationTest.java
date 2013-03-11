package org.apache.juddi.api.impl;

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
import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckSubscriptionListener;
import org.apache.juddi.v3.tck.TckTModel;
import org.apache.juddi.v3.tck.UDDISubscriptionListenerImpl;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class API_090_SubscriptionListenerIntegrationTest
{
	private static Log logger = LogFactory.getLog(API_090_SubscriptionListenerIntegrationTest.class);
	private static API_010_PublisherTest api010      = new API_010_PublisherTest();
	private static TckTModel tckTModel               = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness           = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusinessService tckBusinessService  = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckSubscriptionListener tckSubscriptionListener = new TckSubscriptionListener(new UDDISubscriptionImpl(), new UDDIPublicationImpl());
	private static Endpoint endPoint;
	private static String authInfoJoe = null;
	

	@AfterClass
	public static void stopManager() throws ConfigurationException {
		//manager.stop();
		//shutting down the TCK SubscriptionListener
		endPoint.stop();
		
		Registry.stop();
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		Registry.start();
		try {
			//bring up the TCK SubscriptionListener
			endPoint = Endpoint.publish("http://localhost:12345/tcksubscriptionlistener", new UDDISubscriptionListenerImpl());
			
			//manager  = new UDDIClerkManager();
			//manager.start();
			
			logger.debug("Getting auth tokens..");
		
			 
			 api010.saveJoePublisher();
			 UDDISecurityPortType security      = new UDDISecurityImpl();
        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
        	 Assert.assertNotNull(authInfoJoe);
        	  
	     } catch (Exception e) {
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
			//Saving the Listener Service
			tckSubscriptionListener.saveService(authInfoJoe);
			//Saving the Subscription
			tckSubscriptionListener.saveNotifierSubscription(authInfoJoe);
			//Changing the service we subscribed to "JoePublisherService"
			tckBusinessService.updateJoePublisherService(authInfoJoe, "foo");
			//tckSubscriptionListener.changeSubscribedObject(authInfoJoe);
			
            //waiting up to 100 seconds for the listener to notice the change.
			String test="";
			for (int i=0; i<200; i++) {
				Thread.sleep(500);
				System.out.print(".");
				if (UDDISubscriptionListenerImpl.notificationCount > 0) {
					break;
				} else {
					System.out.print(test);
				}
			}
			if (UDDISubscriptionListenerImpl.notificationCount == 0) {
				Assert.fail("No Notification was sent");
			}
			if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Service One</name>")) {
				Assert.fail("Notification does not contain the correct service");
			}
			
		} catch (Exception e) {
			e.printStackTrace();

			Assert.fail();
		} finally {
			
			tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
    
}
