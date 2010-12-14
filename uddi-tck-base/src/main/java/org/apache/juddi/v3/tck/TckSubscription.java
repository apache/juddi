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
package org.apache.juddi.v3.tck;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.KeyBag;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;
import static junit.framework.Assert.assertEquals;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class TckSubscription 
{	
	final static String JOE_SUBSCRIPTION_XML = "uddi_data/subscription/subscription1.xml";
    final static String JOE_SUBSCRIPTION_KEY = "uddi:uddi.joepublisher.com:subscriptionone";
	final static String JOE_SUBSCRIPTIONRESULTS_XML = "uddi_data/subscription/subscriptionresults1.xml";

	final static String SAM_SUBSCRIPTION_XML = "uddi_data/subscription/subscription2.xml";
    final static String SAM_SUBSCRIPTION_KEY = "uddi:www.samco.com:subscriptionone";
	final static String SAM_SUBSCRIPTIONRESULTS_XML = "uddi_data/subscription/subscriptionresults2.xml";
	final static String SAM_DUMMYBUSINESSKEY = "uddi:www.this.key.doesnot.exist";

	final static String SAM_SUBSCRIPTION2_XML = "uddi_data/subscription/subscription3.xml";
    final static String SAM_SUBSCRIPTION2_KEY = "uddi:www.samco.com:subscriptiontwo";
	final static String SAM_SUBSCRIPTIONRESULTS2_XML = "uddi_data/subscription/subscriptionresults3.xml";
	final static int FINDQUALIFIER_TMODEL_TOTAL = 22;

	final static String SAM_SUBSCRIPTION3_XML = "uddi_data/subscription/subscription4.xml";
    final static String SAM_SUBSCRIPTION3_KEY = "uddi:www.samco.com:subscriptionthree";
	final static String SAM_SUBSCRIPTIONRESULTS3_XML = "uddi_data/subscription/subscriptionresults4.xml";
	
	private Log logger = LogFactory.getLog(this.getClass());
    UDDISubscriptionPortType subscription = null;
	UDDISecurityPortType security = null;
	
	public TckSubscription(UDDISubscriptionPortType subscription, UDDISecurityPortType security) {
		super();
		this.subscription = subscription;
		this.security = security;
	}

	public void saveJoePublisherSubscription(String authInfoJoe, String subscriptionXML,
			String subscriptionKey) {
		saveSubscription(authInfoJoe, subscriptionXML, subscriptionKey);
	}
		
	public void saveJoePublisherSubscription(String authInfoJoe) {
		saveSubscription(authInfoJoe, JOE_SUBSCRIPTION_XML, JOE_SUBSCRIPTION_KEY);
	}

	public void deleteJoePublisherSubscription(String authInfoJoe) {
		deleteSubscription(authInfoJoe, JOE_SUBSCRIPTION_KEY);
	}
	
	public void getJoePublisherSubscriptionResults(String authInfoJoe) {		
		try {
			GetSubscriptionResults getSubResultsIn = (GetSubscriptionResults)EntityCreator.buildFromDoc(JOE_SUBSCRIPTIONRESULTS_XML, "org.uddi.sub_v3");
			getSubResultsIn.setAuthInfo(authInfoJoe);
			
			SubscriptionResultsList result = subscription.getSubscriptionResults(getSubResultsIn);
			if (result == null)
				Assert.fail("Null result from getSubscriptionResults operation");

			ServiceInfos sInfos = result.getServiceList().getServiceInfos();
			if (sInfos == null)
				Assert.fail("No result from getSubscriptionResults operation");
			List<ServiceInfo> siList = sInfos.getServiceInfo();
			if (siList == null || siList.size() == 0)
				Assert.fail("No result from getSubscriptionResults operation");
			ServiceInfo siOut = siList.get(0);
			
			BusinessService bsIn = (BusinessService)EntityCreator.buildFromDoc(TckBusinessService.JOE_SERVICE_XML, "org.uddi.api_v3");

			assertEquals(bsIn.getServiceKey(), siOut.getServiceKey());
			
			TckValidator.checkNames(bsIn.getName(), siOut.getName());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");		
		}
		
	}
	
	public void saveSamSyndicatorSubscription(String authInfoSam) {
		saveSubscription(authInfoSam, SAM_SUBSCRIPTION_XML, SAM_SUBSCRIPTION_KEY);
	}

	public void saveSamSyndicatorSubscriptionWithChunkingOnFind(String authInfoSam) {
		saveSubscription(authInfoSam, SAM_SUBSCRIPTION2_XML, SAM_SUBSCRIPTION2_KEY);
	}

	public void saveSamSyndicatorSubscriptionWithChunkingOnGet(String authInfoSam) {
		saveSubscription(authInfoSam, SAM_SUBSCRIPTION3_XML, SAM_SUBSCRIPTION3_KEY);
	}
	
	public void deleteSamSyndicatorSubscription(String authInfoSam) {
		deleteSubscription(authInfoSam, SAM_SUBSCRIPTION_KEY);
	}

	public void deleteSamSyndicatorSubscriptionWithChunkingOnFind(String authInfoSam) {
		deleteSubscription(authInfoSam, SAM_SUBSCRIPTION2_KEY);
	}

	public void deleteSamSyndicatorSubscriptionWithChunkingOnGet(String authInfoSam) {
		deleteSubscription(authInfoSam, SAM_SUBSCRIPTION3_KEY);
	}
	
	public void getSamSyndicatorSubscriptionResults(String authInfoSam) {		
		try {
			GetSubscriptionResults getSubResultsIn = (GetSubscriptionResults)EntityCreator.buildFromDoc(SAM_SUBSCRIPTIONRESULTS_XML, "org.uddi.sub_v3");
			getSubResultsIn.setAuthInfo(authInfoSam);
			
			SubscriptionResultsList result = subscription.getSubscriptionResults(getSubResultsIn);
			if (result == null)
				Assert.fail("Null result from getSubscriptionResults operation");

			BusinessDetail busDetail = result.getBusinessDetail();
			if (busDetail == null)
				Assert.fail("No result from getSubscriptionResults operation");
			List<BusinessEntity> beList = busDetail.getBusinessEntity();
			if (beList == null || beList.size() == 0)
				Assert.fail("No result from getSubscriptionResults operation");
			BusinessEntity beOut = beList.get(0);
			
			BusinessEntity beIn = (BusinessEntity)EntityCreator.buildFromDoc(TckBusiness.SAM_BUSINESS_XML, "org.uddi.api_v3");

			assertEquals(beIn.getBusinessKey(), beOut.getBusinessKey());
			
			TckValidator.checkNames(beIn.getName(), beOut.getName());
			TckValidator.checkDescriptions(beIn.getDescription(), beOut.getDescription());
			TckValidator.checkDiscoveryUrls(beIn.getDiscoveryURLs(), beOut.getDiscoveryURLs());
			TckValidator.checkContacts(beIn.getContacts(), beOut.getContacts());
			TckValidator.checkCategories(beIn.getCategoryBag(), beOut.getCategoryBag());
			
			List<KeyBag> keyBagList = result.getKeyBag();
			if (keyBagList == null || keyBagList.size() == 0)
				Assert.fail("No keyBag from SamSyndicator getSubscriptionResults operation");
			KeyBag keyBag = keyBagList.get(0);
			assertEquals(SAM_DUMMYBUSINESSKEY, keyBag.getBusinessKey().get(0));
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");		
		}
		
	}
	
	public void getSamSyndicatorSubscriptionResultsWithChunkingOnFind(String authInfoSam) {		
		try {
			GetSubscriptionResults getSubResultsIn = (GetSubscriptionResults)EntityCreator.buildFromDoc(SAM_SUBSCRIPTIONRESULTS2_XML, "org.uddi.sub_v3");
			getSubResultsIn.setAuthInfo(authInfoSam);
			
			Subscription subIn = (Subscription)EntityCreator.buildFromDoc(SAM_SUBSCRIPTION2_XML, "org.uddi.sub_v3");
			
			int expectedIterations = FINDQUALIFIER_TMODEL_TOTAL / subIn.getMaxEntities();
			if (FINDQUALIFIER_TMODEL_TOTAL % subIn.getMaxEntities() >0)
				expectedIterations++;
			
			String chunkToken = "";
			int iterations = 0;
			while (chunkToken != null) {
				iterations++;
				
				getSubResultsIn.setChunkToken(chunkToken);
				SubscriptionResultsList result = subscription.getSubscriptionResults(getSubResultsIn);
				if (result == null)
					Assert.fail("Null result from getSubscriptionResults operation");
				
				TModelList tmodelList = result.getTModelList();
				if (tmodelList == null)
					Assert.fail("No result from getSubscriptionResults operation on chunk attempt " + iterations);

				int resultSize = tmodelList.getTModelInfos().getTModelInfo().size();
				
				if (iterations < expectedIterations)
					assertEquals(resultSize, subIn.getMaxEntities().intValue());
				else {
					if (FINDQUALIFIER_TMODEL_TOTAL % subIn.getMaxEntities() > 0)
						assertEquals(resultSize, FINDQUALIFIER_TMODEL_TOTAL % subIn.getMaxEntities());
					else
						assertEquals(resultSize, subIn.getMaxEntities().intValue());
				}
				
				chunkToken = result.getChunkToken();
				
			}
		
			assertEquals(iterations, expectedIterations);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");		
		}
		
	}
	
	public void getSamSyndicatorSubscriptionResultsWithChunkingOnGet(String authInfoSam) {
		try {
			GetSubscriptionResults getSubResultsIn = (GetSubscriptionResults)EntityCreator.buildFromDoc(SAM_SUBSCRIPTIONRESULTS3_XML, "org.uddi.sub_v3");
			getSubResultsIn.setAuthInfo(authInfoSam);
					
			Subscription subIn = (Subscription)EntityCreator.buildFromDoc(SAM_SUBSCRIPTION3_XML, "org.uddi.sub_v3");
			
			int expectedIterations = FINDQUALIFIER_TMODEL_TOTAL / subIn.getMaxEntities();
			if (FINDQUALIFIER_TMODEL_TOTAL % subIn.getMaxEntities() >0)
				expectedIterations++;
			
			String chunkToken = "";
			int iterations = 0;
			while (chunkToken != null) {
				iterations++;
				
				getSubResultsIn.setChunkToken(chunkToken);
				SubscriptionResultsList result = subscription.getSubscriptionResults(getSubResultsIn);
				if (result == null)
					Assert.fail("Null result from getSubscriptionResults operation");
				
				TModelDetail tmodelDetail = result.getTModelDetail();
				if (tmodelDetail == null)
					Assert.fail("No result from getSubscriptionResults operation on chunk attempt " + iterations);

				int resultSize = tmodelDetail.getTModel().size();
				
				if (iterations < expectedIterations)
					assertEquals(resultSize, subIn.getMaxEntities().intValue());
				else {
					if (FINDQUALIFIER_TMODEL_TOTAL % subIn.getMaxEntities() > 0)
						assertEquals(resultSize, FINDQUALIFIER_TMODEL_TOTAL % subIn.getMaxEntities());
					else
						assertEquals(resultSize, subIn.getMaxEntities().intValue());
				}
				
				chunkToken = result.getChunkToken();
				
			}
		
			assertEquals(iterations, expectedIterations);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");		
		}
		
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
			
			assertEquals(subIn.getSubscriptionKey(), subOut.getSubscriptionKey());
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