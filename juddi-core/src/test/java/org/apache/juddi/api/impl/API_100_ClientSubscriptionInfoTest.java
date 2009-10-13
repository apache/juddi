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

import java.rmi.RemoteException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.Registry;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClientSubscriptionInfo;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.GetClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_100_ClientSubscriptionInfoTest {
	
	private JUDDIApiImpl publisher                    = new JUDDIApiImpl();
	private static Logger logger                      = Logger.getLogger(API_100_ClientSubscriptionInfoTest.class);
	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
	private static String authInfoJoe                 = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException, RemoteException {
		Registry.start();
		logger.debug("Getting auth tokens..");
		try {
			api010.saveJoePublisher();
			UDDISecurityPortType security      = new UDDISecurityImpl();
			authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID,  TckPublisher.JOE_PUBLISHER_CRED);
		} catch (DispositionReportFaultMessage e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	
	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		Registry.stop();
	}
	
	@Test
	public void addClientSubscriptionInfo() {
		ClientSubscriptionInfo clientSubscriptionInfo = new ClientSubscriptionInfo();
		
		Node node = new Node();
		node.setSecurityUrl("http://localhost:8080/services/securityUrl");
		node.setName("default");
		
		Clerk clerk = new Clerk();
		clerk.setName("clerkName");
		clerk.setPublisher("root");
		clerk.setNode(node);
		
		clientSubscriptionInfo.setClerk(clerk);
		
		clientSubscriptionInfo.setSubscriptionKey("mykey");
		
		SaveClientSubscriptionInfo saveClientSubscriptionInfo = new SaveClientSubscriptionInfo();
		saveClientSubscriptionInfo.setAuthInfo(authInfoJoe);
		saveClientSubscriptionInfo.getClientSubscriptionInfo().add(clientSubscriptionInfo);
		
		try {
			ClientSubscriptionInfoDetail detail = publisher.saveClientSubscriptionInfo(saveClientSubscriptionInfo);
		
			GetClientSubscriptionInfoDetail getDetail = new GetClientSubscriptionInfoDetail();
			getDetail.setAuthInfo(authInfoJoe);
			getDetail.getClientSubscriptionKey().add("mykey");
			
			Assert.assertEquals("mykey", detail.getClientSubscriptionInfo().get(0).getSubscriptionKey());
			
			ClientSubscriptionInfoDetail detail2 = publisher.getClientSubscriptionInfoDetail(getDetail);
			Assert.assertEquals("mykey", detail2.getClientSubscriptionInfo().get(0).getSubscriptionKey());
	
			DeleteClientSubscriptionInfo deleteInfo = new DeleteClientSubscriptionInfo();
			deleteInfo.setAuthInfo(authInfoJoe);
			deleteInfo.getSubscriptionKey().add("mykey");
			publisher.deleteClientSubscriptionInfo(deleteInfo);
			
			try {
				@SuppressWarnings("unused")
				ClientSubscriptionInfoDetail detail3 = publisher.getClientSubscriptionInfoDetail(getDetail);
				Assert.fail("We're expecting an InvalidKeyPassedException");
			} catch (Exception e) {
				Assert.assertEquals(InvalidKeyPassedException.class, e.getClass());
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
	}
	
}
