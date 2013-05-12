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
import java.net.URI;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.SubscriptionListenerQuery;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckSubscriptionListener;
import org.apache.juddi.v3.tck.TckSubscriptionListenerRMI;
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
public class API_091_RMISubscriptionListenerIntegrationTest
{
	
	private static Log logger = LogFactory.getLog(API_090_SubscriptionListenerIntegrationTest.class);
	private static API_010_PublisherTest api010      = new API_010_PublisherTest();
	private static TckTModel tckTModel               = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness           = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusinessService tckBusinessService  = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckSubscriptionListenerRMI tckSubscriptionListenerRMI = new TckSubscriptionListenerRMI(new UDDISubscriptionImpl(), new UDDIPublicationImpl());
	
	private static String authInfoJoe = null;
	//private static UDDIClient manager;
	private static UDDISubscriptionListenerImpl rmiSubscriptionListenerService = null;
	private static Registry registry;
	private static String path = null;
	private static Integer randomPort = null;

	@AfterClass
	public static void stopManager() throws ConfigurationException, AccessException, RemoteException, NotBoundException {
		//manager.stop();
		//shutting down the TCK SubscriptionListener
		registry.unbind(path);
		org.apache.juddi.Registry.stop();
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
		org.apache.juddi.Registry.start();
		try {
			//random port
			randomPort = 19800 + new Random().nextInt(99);
			System.out.println("RMI Random port=" + randomPort);
			//bring up the RMISubscriptionListener
			URI rmiEndPoint = new URI("rmi://localhost:" + randomPort + "/tck/rmisubscriptionlistener");
			registry = LocateRegistry.createRegistry(rmiEndPoint.getPort());
			path = rmiEndPoint.getPath();
			
			//starting the service
			rmiSubscriptionListenerService = new UDDISubscriptionListenerImpl(0);
			//binding to the RMI Registry
			registry.bind(path,rmiSubscriptionListenerService);
			
			//double check that the service is bound in the local Registry
			Registry registry2 = LocateRegistry.getRegistry(rmiEndPoint.getHost(), rmiEndPoint.getPort());
			registry2.lookup(rmiEndPoint.getPath());
           
            
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			Assert.fail();
		}

		//manager  = new UDDIClient();
		//manager.start();
		
		logger.debug("Getting auth tokens..");
		try {
			 
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
			tckSubscriptionListenerRMI.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_RMI_SERVICE_XML, randomPort);
			//Saving the Subscription
			tckSubscriptionListenerRMI.saveNotifierSubscription(authInfoJoe, TckSubscriptionListenerRMI.SUBSCRIPTION_XML_RMI);
			//Changing the service we subscribed to "JoePublisherService"
			tckBusinessService.updateJoePublisherService(authInfoJoe, "foo");
			
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
			
			tckSubscriptionListenerRMI.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListenerRMI.SUBSCRIPTION_KEY_RMI);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
    
}
