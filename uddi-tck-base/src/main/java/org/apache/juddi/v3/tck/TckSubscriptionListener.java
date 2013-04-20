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
import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveService;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class TckSubscriptionListener
{
	public static String LISTENER_HTTP_SERVICE_XML    = "uddi_data/subscriptionnotifier/listenerService.xml";
	public static String LISTENER_SMTP_SERVICE_XML    = "uddi_data/subscriptionnotifier/listenerServiceSMTP.xml";
	public static String LISTENER_RMI_SERVICE_XML     = "uddi_data/subscriptionnotifier/listenerServiceRMI.xml";
    public static String LISTENER_SERVICE_KEY         = "uddi:uddi.joepublisher.com:listenerone";

	/** note that the subscription1.xml contains the binding template for the UDDI server to call back into */
	public static String SUBSCRIPTION_XML = "uddi_data/subscriptionnotifier/subscription1.xml";
	public static String SUBSCRIPTION_SMTP_XML = "uddi_data/subscriptionnotifier/subscription1SMTP.xml";
    public static String SUBSCRIPTION_KEY = "uddi:uddi.joepublisher.com:subscriptionone";
    public static String SUBSCRIPTION_SMTP_KEY = "uddi:uddi.joepublisher.com:subscriptiononesmtp";
    
    private Log logger = LogFactory.getLog(this.getClass());
	private UDDIPublicationPortType publication = null;
    private UDDISubscriptionPortType subscription = null;
    private SaveService ss = null;
    
	public TckSubscriptionListener(
			UDDISubscriptionPortType subscription,
			UDDIPublicationPortType publication) {
		super();
		this.subscription = subscription;
		this.publication = publication;
	}
	
	public void saveNotifierBinding(String authInfo, String bindingXML, String bindingKey, Integer port) {
		try {
			SaveBinding sb = new SaveBinding();
			sb.setAuthInfo(authInfo);
			BindingTemplate btIn = (BindingTemplate)EntityCreator.buildFromDoc(bindingXML, "org.uddi.api_v3");
			String value = btIn.getAccessPoint().getValue();
			value = value.replace("{randomPort}", port.toString());
			sb.getBindingTemplate().add(btIn);
			publication.saveBinding(sb);		
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown: " + e.getMessage());
		}
	}
	
	public void deleteBinding(String authInfo, String bindingKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteBinding db = new DeleteBinding();
			db.setAuthInfo(authInfo);
			
			db.getBindingKey().add(bindingKey);
			publication.deleteBinding(db);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}
	
	public void saveService(String authInfo, String listenerService, Integer port) {
		try {
			// First save the entity
			ss = new SaveService();
			ss.setAuthInfo(authInfo);
			
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)EntityCreator.buildFromDoc(listenerService, "org.uddi.api_v3");
			if (port > 0) {
				for (BindingTemplate btIn: bsIn.getBindingTemplates().getBindingTemplate()) {
					String value = btIn.getAccessPoint().getValue();
					value = value.replace("{randomPort}", port.toString());
					btIn.getAccessPoint().setValue(value);
				}
			}
			ss.getBusinessService().add(bsIn);
			publication.saveService(ss);
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}

	public void saveNotifierSubscription(String authInfo, String subscriptionXML) {
		saveSubscription(authInfo, subscriptionXML, SUBSCRIPTION_KEY);
	}
	
	public void deleteNotifierSubscription(String authInfo, String subscriptionKey) {
		deleteSubscription(authInfo, subscriptionKey);
	}
	
	public void saveSubscription(String authInfo, String subscriptionXML, String subscriptionKey) {
		try {
			Subscription subIn = (Subscription)EntityCreator.buildFromDoc(subscriptionXML, "org.uddi.sub_v3");
			List<Subscription> subscriptionList = new ArrayList<Subscription>();
			subscriptionList.add(subIn);
			Holder<List<Subscription>> subscriptionHolder = new Holder<List<Subscription>>();
			subscriptionHolder.value = subscriptionList;
			
			subscription.saveSubscription(authInfo, subscriptionHolder);
			
			Subscription subDirectOut = subscriptionHolder.value.get(0);
			assertEquals(subIn.getSubscriptionKey(), subDirectOut.getSubscriptionKey());
			
			List<Subscription> outSubscriptionList = subscription.getSubscriptions(authInfo);
			Assert.assertNotNull(outSubscriptionList);
			Subscription subOut = outSubscriptionList.get(0);
			
			//assertEquals(subIn.getSubscriptionKey(), subOut.getSubscriptionKey());
			assertEquals(subDirectOut.getExpiresAfter().getMonth(), subOut.getExpiresAfter().getMonth());
			assertEquals(subDirectOut.getExpiresAfter().getDay(), subOut.getExpiresAfter().getDay());
			assertEquals(subDirectOut.getExpiresAfter().getYear(), subOut.getExpiresAfter().getYear());
			
			//assertEquals(subIn.getSubscriptionFilter().getFindService().getName().get(0).getValue(), 
			//			 subOut.getSubscriptionFilter().getFindService().getName().get(0).getValue());
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");		
		}	
	}
	
	public void deleteSubscription(String authInfo, String subscriptionKey) {
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