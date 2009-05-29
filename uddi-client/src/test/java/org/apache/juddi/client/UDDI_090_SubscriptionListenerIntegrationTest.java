package org.apache.juddi.client;

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
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.api_v3.tck.TckBindingTemplate;
import org.uddi.api_v3.tck.TckBusiness;
import org.uddi.api_v3.tck.TckBusinessService;
import org.uddi.api_v3.tck.TckPublisher;
import org.uddi.api_v3.tck.TckSecurity;
import org.uddi.api_v3.tck.TckSubscription;
import org.uddi.api_v3.tck.TckSubscriptionListener;
import org.uddi.api_v3.tck.TckTModel;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

import org.uddi.api_v3.BusinessService;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDI_090_SubscriptionListenerIntegrationTest
{
	private static Logger logger = Logger.getLogger(UDDI_090_SubscriptionListenerIntegrationTest.class);

	private static TckTModel tckTModel                    = null;
	private static TckBusiness tckBusiness                = null;
	private static TckBusinessService tckBusinessService  = null;
	private static TckBindingTemplate tckBindingTemplate  = null;
	private static TckSubscription tckSubscription = null;
	private static TckSubscriptionListener tckSubscriptionListener = null;

	private static UDDIPublicationPortType publication = null;

	
	private static String authInfoJoe = null;
	private static String authInfoSam = null;

	@BeforeClass
	public static void setup() {
		logger.debug("Getting subscriber proxy..");
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT, Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.newInstance();
	        	 
	        	 UDDISecurityPortType security = transport.getSecurityService();
	        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID, TckPublisher.JOE_PUBLISHER_CRED);
	        	 Assert.assertNotNull(authInfoJoe);
	        	 authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.SAM_SYNDICATOR_ID, TckPublisher.SAM_SYNDICATOR_CRED);
	        	 Assert.assertNotNull(authInfoSam);
	        	 
	        	 publication = transport.getPublishService();
	        	 UDDIInquiryPortType inquiry = transport.getInquiryService();
	        	 UDDISubscriptionPortType subscription = transport.getSubscriptionService();
	        	 UDDISubscriptionListenerPortType listener = transport.getSubscriptionListenerService();
	        	 
	        	 tckTModel  = new TckTModel(publication, inquiry);
	        	 tckBusiness = new TckBusiness(publication, inquiry);
	        	 tckBusinessService = new TckBusinessService(publication, inquiry);
	        	 tckBindingTemplate = new TckBindingTemplate(publication, inquiry);
	        	 tckSubscription = new TckSubscription(subscription, security);
	        	 tckSubscriptionListener = new TckSubscriptionListener(listener, subscription, inquiry, publication);	
	         } else {
	        	 Assert.fail();
	         }
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
		} 
		finally {
			tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
}
