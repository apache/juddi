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
package org.uddi.api_v3.tck;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;
import static junit.framework.Assert.assertEquals;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class TckSubscription 
{	
	final static String JOE_SUBSCRIPTION_XML = "uddi_data/subscription/subscription1.xml";
    final static String JOE_SUBSCRIPTION_KEY = "uddi:uddi.joepublisher.com:subscriptionone";

	private Logger logger = Logger.getLogger(this.getClass());
    UDDISubscriptionPortType subscription = null;
	UDDISecurityPortType security = null;
	
	public TckSubscription(UDDISubscriptionPortType subscription, UDDISecurityPortType security) {
		super();
		this.subscription = subscription;
		this.security = security;
	}

	public void saveJoePublisherSubscription(String authInfoJoe) {
		saveSubscription(authInfoJoe, JOE_SUBSCRIPTION_XML, JOE_SUBSCRIPTION_KEY);
	}

	public void deleteJoePublisherSubscription(String authInfoJoe) {
		deleteSubscription(authInfoJoe, JOE_SUBSCRIPTION_KEY);
	}
	
	private void saveSubscription(String authInfo, String subscriptionXML, String subscriptionKey) {
		try {
			Subscription subIn = (Subscription)EntityCreator.buildFromDoc(subscriptionXML, "org.uddi.sub_v3");
			List<Subscription> subscriptionList = new ArrayList<Subscription>();
			subscriptionList.add(subIn);
			Holder<List<Subscription>> subscriptionHolder = new Holder<List<Subscription>>();
			subscriptionHolder.value = subscriptionList;
			
			subscription.saveSubscription(authInfo, subscriptionHolder);
			
			Subscription subOut = subscriptionHolder.value.get(0);
			
			assertEquals(subIn.getSubscriptionKey(), subOut.getSubscriptionKey());
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");		
		}
		
	}
	
	private void deleteSubscription(String authInfo, String subscriptionKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteSubscription ds = new DeleteSubscription();
			ds.setAuthInfo(authInfo);
			
			ds.getSubscriptionKey().add(subscriptionKey);
			subscription.deleteSubscription(ds);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
}