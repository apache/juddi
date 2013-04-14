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
import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDI_090_SubscriptionListenerIntegrationTest
{
	
	
	private static Log logger = LogFactory.getLog(UDDI_090_SubscriptionListenerIntegrationTest.class);

	private static TckTModel tckTModel                    = null;
	private static TckBusiness tckBusiness                = null;
	private static TckBusinessService tckBusinessService  = null;
	private static TckSubscriptionListener tckSubscriptionListener = null;
	private static Endpoint endPoint;
	private static String authInfoJoe = null;
	private static UDDIClerkManager manager;

	@AfterClass
	public static void stopManager() throws ConfigurationException {
		manager.stop();
		//shutting down the TCK SubscriptionListener
		endPoint.stop();
		endPoint = null;
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		try {
			//bring up the TCK SubscriptionListener
			endPoint = Endpoint.publish("http://localhost:12345/tcksubscriptionlistener", new UDDISubscriptionListenerImpl());
			
			manager  = new UDDIClerkManager();
			manager.start();
			
			logger.debug("Getting auth tokens..");
		
			 
			 Transport transport = manager.getTransport();
        	 UDDISecurityPortType security = transport.getUDDISecurityService();
        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
        	 Assert.assertNotNull(authInfoJoe);
        	 UDDISubscriptionPortType subscription = transport.getUDDISubscriptionService();
        	 
        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
        	 tckTModel  = new TckTModel(publication, inquiry);
        	 tckBusiness = new TckBusiness(publication, inquiry);
        	 tckBusinessService = new TckBusinessService(publication, inquiry);
        	 tckSubscriptionListener = new TckSubscriptionListener(subscription, publication);	
        	  
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test
	public void joePublisherUpdateService() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			//Saving the Listener Service
			tckSubscriptionListener.saveService(authInfoJoe);
			//Saving the Subscription
			tckSubscriptionListener.saveNotifierSubscription(authInfoJoe);
            //Changing the service we subscribed to "JoePublisherService"
			Thread.sleep(1000);
			logger.info("Updating Service ********** ");
			tckBusinessService.updateJoePublisherService(authInfoJoe, "foo");
			
            //waiting up to 100 seconds for the listener to notice the change.
			String test="";
			for (int i=0; i<200; i++) {
				Thread.sleep(500);
				System.out.print(".");
				if (UDDISubscriptionListenerImpl.notificationCount > 0) {
					logger.info("Received Notification");
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
			logger.error("No exceptions please.");
			e.printStackTrace();

			Assert.fail();
		} finally {
				tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe);
				tckBusinessService.deleteJoePublisherService(authInfoJoe);
				tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
				tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	@Test
	public void joePublisherUpdateBusiness() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			//Saving the Listener Service
			tckSubscriptionListener.saveService(authInfoJoe);
			//Saving the Subscription
			tckSubscriptionListener.saveNotifierSubscription(authInfoJoe);
            //Changing the service we subscribed to "JoePublisherService"
			Thread.sleep(1000);
			logger.info("Deleting Business ********** ");
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			
            //waiting up to 100 seconds for the listener to notice the change.
			String test="";
			for (int i=0; i<200; i++) {
				Thread.sleep(500);
				System.out.print(".");
				if (UDDISubscriptionListenerImpl.notificationCount > 0) {
					logger.info("Received Notification");
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
			logger.error("No exceptions please.");
			e.printStackTrace();

			Assert.fail();
		} finally {
				tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe);
				tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
    
}
