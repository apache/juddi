/*
 * Copyright 2001-2010 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.xlt.action.SOAP;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.DatatypeConstants.Field;
import javax.xml.ws.Holder;

import junit.framework.Assert;

import org.apache.juddi.xlt.util.JUDDIServiceProvider;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.sub_v3.SaveSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * NOT YET FUNCTIONAL. 
 * 
 * @author Jeremi Thebeau
 *
 */
public class Subscribe extends AbstractJUDDIAction 
{
	//GeneralDataProvider
	private UDDISubscriptionPortType subscriptionService;
	private AuthToken authenticationToken;
	private String businessKey;
	private String subscriptionKey;
	
	public Subscribe (AuthToken authenticationToken, String businessKey)
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.businessKey = businessKey;
	}
	
	@Override
	public void preValidate() throws Exception 
	{
		subscriptionService = JUDDIServiceProvider.getSubscriptionService();
		Assert.assertNotNull("Publish service is missing");
		Assert.assertNotNull("No AuthToken was passed", authenticationToken);
	}
	
	@Override
	protected void execute() throws Exception 
	{
		//FindBusiness findBusiness = new FindBusiness();
		//findBusiness.setAuthInfo(authenticationToken.getAuthInfo());
		//findBusiness.get;
		
		GetBusinessDetail getBusinessDetail = new GetBusinessDetail();
		getBusinessDetail.setAuthInfo(authenticationToken.getAuthInfo());
		getBusinessDetail.getBusinessKey().add(businessKey);
		
		SubscriptionFilter subFil = new SubscriptionFilter();
		subFil.setGetBusinessDetail(getBusinessDetail);
		
		//Duration duration = new Duration(); 
		//duration.
		
		Subscription sub = new Subscription();
		sub.setSubscriptionFilter(subFil);
		sub.setBindingKey("bindingKeyOfTheClientsNotifySubscriptionListenerService");
		//sub.setNotificationInterval(duration);
		
		
		//SaveSubscription saveSub = new SaveSubscription();
		//saveSub.getSubscription().add(sub);
		//saveSub.setAuthInfo(authenticationToken.getAuthInfo());
		
		List<Subscription> subs = new ArrayList<Subscription>();
		subs.add(sub);
		Holder<List<Subscription>> subHolder = new Holder<List<Subscription>>();
		subHolder.value = subs;
		
		Assert.assertNotNull("Holder is Null", subHolder);
		Assert.assertNotNull("AuthToken is Null", authenticationToken.getAuthInfo());
		
		subscriptionService.saveSubscription(authenticationToken.getAuthInfo(), subHolder);
		
	}

	@Override
	protected void postValidate() throws Exception 
	{
		List<Subscription> subList = subscriptionService.getSubscriptions(authenticationToken.getAuthInfo());
		subscriptionKey = subList.get(0).getSubscriptionKey();
		Assert.assertNotNull("No Subscription key was returned", subscriptionKey);
	}
	
	public String getSubscriptionKey ()
	{
		return subscriptionKey;
	}

	

	
}
