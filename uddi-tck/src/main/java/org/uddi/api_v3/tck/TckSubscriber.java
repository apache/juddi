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

import org.junit.Assert;
import org.junit.Test;
import org.uddi.sub_v3.SaveSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckSubscriber 
{	
	final static String SUBSCRIPTION_XML    = "uddi_data/subscription/subscription1.xml";
	UDDISubscriptionPortType subscription = null;
	UDDISecurityPortType security = null;
	
	public TckSubscriber(UDDISubscriptionPortType subscription, UDDISecurityPortType security) {
		super();
		this.subscription = subscription;
		this.security = security;
	}

	public void saveSubscription() {
		try {
			String authInfo = TckSecurity.getAuthToken(security, "root", "");

			Subscription subIn = (Subscription)EntityCreator.buildFromDoc(SUBSCRIPTION_XML, "org.uddi.sub_v3");
			List<Subscription> subscriptionList = new ArrayList<Subscription>();
			subscriptionList.add(subIn);
			Holder<List<Subscription>> subscriptionHolder = new Holder<List<Subscription>>();
			subscriptionHolder.value = subscriptionList;
			
			subscription.saveSubscription(authInfo, subscriptionHolder);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("No exception should be thrown");		
		}
	}

	public void deleteSubscription() {
		try {
			String authInfo = TckSecurity.getAuthToken(security, "root", "");
			System.out.println("AUTHINFO=" + authInfo);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.fail("No exception should be thrown");
		}
	}
}