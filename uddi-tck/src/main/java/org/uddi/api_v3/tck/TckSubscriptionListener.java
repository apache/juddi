package org.uddi.api_v3.tck;

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

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Assert;

import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.SaveBinding;


import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelList;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.FindTModel;


/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class TckSubscriptionListener
{
	public final static String SUBSCRIBED_SERVICE_XML              = "uddi_data/joepublisher/businessService.xml";
    public final static String SUBSCRIBED_SERVICE_KEY              = "uddi:uddi.joepublisher.com:serviceone";

	final static String JOE_SERVICE_XML              = "uddi_data/joepublisher/businessService.xml";
    final static String JOE_SERVICE_KEY              = "uddi:uddi.joepublisher.com:serviceone";
    final static String SAM_SERVICE_XML              = "uddi_data/samsyndicator/businessService.xml";
    final static String SAM_SERVICE_KEY              = "uddi:www.samco.com:listingservice";
   
	final static String JOE_SUBSCRIPTION_XML = "uddi_data/subscription/subscription1.xml";
    final static String JOE_SUBSCRIPTION_KEY = "uddi:uddi.joepublisher.com:subscriptionone";
	final static String JOE_SUBSCRIPTIONRESULTS_XML = "uddi_data/subscription/subscriptionresults1.xml";

	public final static String NOTIFIER_BINDING_XML = "uddi_data/subscriptionnotifier/bindingTemplate.xml";
    public final static String NOTIFIER_BINDING_KEY = "uddi:uddi.joepublisher.com:bindingnotifier";    
    
	public final static String SUBSCRIPTION_XML = "uddi_data/subscriptionnotifier/subscription1.xml";
    public final static String SUBSCRIPTION_KEY = "uddi:uddi.joepublisher.com:subscriptionone";
    
	private Logger logger = Logger.getLogger(this.getClass());
	private UDDIPublicationPortType publication = null;
    private UDDIInquiryPortType inquiry = null;
    private UDDISubscriptionListenerPortType listener = null;
    private UDDISubscriptionPortType subscription = null;
    private SaveService ss = null;
    
	public TckSubscriptionListener(UDDISubscriptionListenerPortType listener,
			UDDISubscriptionPortType subscription,
			UDDIInquiryPortType inquiry,
			UDDIPublicationPortType publication) {
		super();
		this.listener = listener;
		this.subscription = subscription;
		this.publication = publication;
		this.inquiry = inquiry;
	}
	
	public void saveNotifierBinding(String authInfo, String bindingXML, String bindingKey) {
		try {
			SaveBinding sb = new SaveBinding();
			sb.setAuthInfo(authInfo);
			
			BindingTemplate btIn = (BindingTemplate)EntityCreator.buildFromDoc(bindingXML, "org.uddi.api_v3");
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
	
	public void saveService(String authInfo) {
		try {
			// First save the entity
			ss = new SaveService();
			ss.setAuthInfo(authInfo);
			
			org.uddi.api_v3.BusinessService bsIn = (org.uddi.api_v3.BusinessService)EntityCreator.buildFromDoc(SUBSCRIBED_SERVICE_XML, "org.uddi.api_v3");
			ss.getBusinessService().add(bsIn);
			publication.saveService(ss);
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
	}
	
	public void changeSubscribedObject(String authInfo) {
		try	{
			ss.getBusinessService().get(0).getName().get(0).setValue("foo");
			publication.saveService(ss);
			Thread.sleep(100000);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown. " + e.getMessage());
		}

	}
	
	public void notifyService(String authInfo) {
		try {
			
			NotifySubscriptionListener nsl = new NotifySubscriptionListener();
			nsl.setAuthInfo(authInfo);
			SubscriptionResultsList srl = new SubscriptionResultsList();
			
//			GetSubscriptionResults getSubResultsIn = (GetSubscriptionResults)EntityCreator.buildFromDoc(JOE_SUBSCRIPTIONRESULTS_XML, "org.uddi.sub_v3");
	//		getSubResultsIn.setAuthInfo(authInfo);
			
//			SubscriptionResultsList result = subscription.getSubscriptionResults(getSubResultsIn);

			nsl.setSubscriptionResultsList(srl);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}
}