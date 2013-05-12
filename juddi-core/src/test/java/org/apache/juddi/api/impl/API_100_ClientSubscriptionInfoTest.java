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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.ClientSubscriptionInfo;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.GetClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
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
	private static Log logger                         = LogFactory.getLog(API_100_ClientSubscriptionInfoTest.class);
	private static API_010_PublisherTest api010       = new API_010_PublisherTest();
	private static String authInfoJoe                 = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException, RemoteException {
		Registry.start();
		logger.debug("Getting auth tokens..");
		try {
			api010.saveJoePublisher();
			UDDISecurityPortType security      = new UDDISecurityImpl();
			authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
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
		node.setCustodyTransferUrl("http://localhost:8080/services/securityUrl");
		node.setDescription("description");
		node.setInquiryUrl("http://localhost:8080/services/securityUrl");
		node.setPublishUrl("http://localhost:8080/services/securityUrl");
		node.setProxyTransport("class");
		node.setSubscriptionUrl("http://localhost:8080/services/securityUrl");
		node.setName("default");
		node.setClientName("defaultClient");
		SaveNode saveNode = new SaveNode();
		saveNode.setAuthInfo(authInfoJoe);
		saveNode.getNode().add(node);
		
		Clerk clerk = new Clerk();
		clerk.setName("clerkName");
		clerk.setPublisher("root");
		clerk.setNode(node);
		SaveClerk saveClerk = new SaveClerk();
		saveClerk.setAuthInfo(authInfoJoe);
		saveClerk.getClerk().add(clerk);
		
		clientSubscriptionInfo.setFromClerk(clerk);
		
		Node node2 = new Node();
		node2.setSecurityUrl("http://localhost:8080/services/securityUrl2");
		node2.setCustodyTransferUrl("https://localhost:8080/services/securityUrl2");
		node2.setDescription("description2");
		node2.setInquiryUrl("http://localhost:8080/services/securityUrl2");
		node2.setPublishUrl("http://localhost:8080/services/securityUrl2");
		node2.setProxyTransport("class2");
		node2.setSubscriptionUrl("http://localhost:8080/services/securityUrl2");
		node2.setName("default2");
		node2.setClientName("default2Client");
		saveNode.getNode().add(node2);
		
		Clerk clerk2 = new Clerk();
		clerk2.setName("clerkName2");
		clerk2.setPublisher("root");
		clerk2.setNode(node2);
		saveClerk.getClerk().add(clerk2);
		
		clientSubscriptionInfo.setFromClerk(clerk);
		
		clientSubscriptionInfo.setSubscriptionKey("mykey");
		
		SaveClientSubscriptionInfo saveClientSubscriptionInfo = new SaveClientSubscriptionInfo();
		saveClientSubscriptionInfo.setAuthInfo(authInfoJoe);
		saveClientSubscriptionInfo.getClientSubscriptionInfo().add(clientSubscriptionInfo);
		
		ClientSubscriptionInfo clientSubscriptionInfo2 = new ClientSubscriptionInfo();
		clientSubscriptionInfo2.setSubscriptionKey("mykey2");
		clientSubscriptionInfo2.setFromClerk(clerk2);
		saveClientSubscriptionInfo.getClientSubscriptionInfo().add(clientSubscriptionInfo2);
		
		try {

			NodeDetail nodeDetail = publisher.saveNode(saveNode);
			ClerkDetail clerkDetail = publisher.saveClerk(saveClerk);
			Assert.assertEquals(2,nodeDetail.getNode().size());
			Assert.assertEquals(2,clerkDetail.getClerk().size());
			
			ClientSubscriptionInfoDetail detail = publisher.saveClientSubscriptionInfo(saveClientSubscriptionInfo);
			Assert.assertEquals("mykey", detail.getClientSubscriptionInfo().get(0).getSubscriptionKey());
			
			GetClientSubscriptionInfoDetail getDetail = new GetClientSubscriptionInfoDetail();
			getDetail.setAuthInfo(authInfoJoe);
			getDetail.getClientSubscriptionKey().add("mykey");
			
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
