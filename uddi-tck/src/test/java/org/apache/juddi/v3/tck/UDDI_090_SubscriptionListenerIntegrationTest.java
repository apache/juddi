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
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.log4j.Logger;
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
	
	
	private static Logger logger = Logger.getLogger(UDDI_090_SubscriptionListenerIntegrationTest.class);

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
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		
		//bring up the TCK SubscriptionListener
		endPoint = Endpoint.publish("http://localhost:12345/tcksubscriptionlistener", new TCK_UDDISubscriptionListenerImpl());
		
		manager  = new UDDIClerkManager();
		manager.start();
		
		logger.debug("Getting auth tokens..");
		try {
			 
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
	public void joePublisher() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckSubscriptionListener.saveService(authInfoJoe);
			
			tckSubscriptionListener.saveNotifierSubscription(authInfoJoe);

			tckSubscriptionListener.changeSubscribedObject(authInfoJoe);
			
            //waiting up to 100 seconds for the listener to notice the change.
			String test="";
			for (int i=0; i<200; i++) {
				Thread.sleep(500);
				System.out.print(".");
				if (TCK_UDDISubscriptionListenerImpl.notificationCount > 0) {
					break;
				} else {
					System.out.print(test);
				}
			}
			if (TCK_UDDISubscriptionListenerImpl.notificationCount == 0 || !TCK_UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Notifier One</name>")) {
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
