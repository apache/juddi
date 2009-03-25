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

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.tck.TckBusiness;
import org.uddi.api_v3.tck.TckPublisher;
import org.uddi.api_v3.tck.TckPublisherAssertion;
import org.uddi.api_v3.tck.TckSecurity;
import org.uddi.api_v3.tck.TckSubscriber;
import org.uddi.api_v3.tck.TckTModel;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:tcunningh@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_080_SubscriberSaveTest 
{
	private TckSubscriber tckSubscriber = new TckSubscriber(new UDDISubscriptionImpl(), new UDDISecurityImpl());
	private static Logger logger = Logger.getLogger(API_060_PublisherAssertionTest.class);
	
	private static API_080_SubscriberSaveTest api010  = new API_080_SubscriberSaveTest();

	@Test
	public void saveSubscriber() {
		tckSubscriber.saveSubscriber();
	}

	@Test
	public void deleteSubscriber() {
		tckSubscriber.deleteSubscriber();
	}
}
