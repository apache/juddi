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

import org.apache.juddi.api.impl.UDDISubscriptionImpl;

import org.apache.juddi.config.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.sub_v3.SaveSubscription;
import org.uddi.sub_v3.Subscription;

/**
 * @author <a href="mailto:tcunningh@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_080_SubscriberSaveTest {
	
	final static String SUBSCRIPTION_XML    = "api_xml_data/subscription/subscription1.xml";
	
	private UDDISubscriptionImpl subscribe = new UDDISubscriptionImpl();
	
	@Test
	public void saveSubscriber() {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(Constants.ROOT_PUBLISHER);
			
			SaveSubscription ss = new SaveSubscription();
			ss.setAuthInfo(authInfo);

			Subscription subIn = (Subscription)UDDIApiTestHelper.buildEntityFromDoc(SUBSCRIPTION_XML, "org.uddi.sub_v3");
			ss.getSubscription().add(subIn);
			subscribe.saveSubscription(authInfo, ss);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("No exception should be thrown");		
		}
	}

	@Test
	public void deleteSubscriber() {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(Constants.ROOT_PUBLISHER);
			System.out.println("AUTHINFO=" + authInfo);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.fail("No exception should be thrown");
		}
	}
}
