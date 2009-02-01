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
package org.apache.juddi.client;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.api_v3.tck.TckSubscriber;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:tcunningh@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_080_SubscriberSaveTest 
{
	private static Logger logger = Logger.getLogger(UDDI_080_SubscriberSaveTest.class);
	private static TckSubscriber tckSubscriber = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting subscriber proxy..");
		try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.newInstance();
	        	 
	        	 UDDISecurityPortType security = transport.getSecurityService();
	        	 UDDISubscriptionPortType subscriber = transport.getSubscriptionService();
	        	 tckSubscriber = new TckSubscriber(subscriber, security);
	         } else {
	        	 Assert.fail();
	         }
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
			 Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test @Ignore //TODO as part of JUDDI-153
	public void saveSubscriber() {
		tckSubscriber.saveSubscriber();
	}

	@Test @Ignore
	public void deleteSubscriber() {
		tckSubscriber.deleteSubscriber();
	}
}
