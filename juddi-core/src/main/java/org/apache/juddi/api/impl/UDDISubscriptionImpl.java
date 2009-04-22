/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

package org.apache.juddi.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.Holder;

import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.KeyBag;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionPortType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.SubscriptionMatch;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.FindSubscriptionByPublisherQuery;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.util.JAXBMarshaller;
import org.apache.juddi.validation.ValidateSubscription;
import org.apache.log4j.Logger;


@WebService(serviceName="UDDISubscriptionService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionPortType",
			targetNamespace = "urn:uddi-org:sub_v3_portType")
public class UDDISubscriptionImpl extends AuthenticatedService implements UDDISubscriptionPortType {

	private static Logger logger = Logger.getLogger(UDDISubscriptionImpl.class);

	public static final int DEFAULT_SUBSCRIPTIONEXPIRATION_DAYS = 30;


	public void deleteSubscription(DeleteSubscription body)
			throws DispositionReportFaultMessage {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		new ValidateSubscription(publisher).validateDeleteSubscription(em, body);
		
        List<String> subscriptionKeyList = body.getSubscriptionKey();
        for (String subscriptionKey : subscriptionKeyList) {
                Object obj = em.find(org.apache.juddi.model.Subscription.class, subscriptionKey);
                em.remove(obj);
        }

        tx.commit();
        em.close();
	}


	public SubscriptionResultsList getSubscriptionResults(
			GetSubscriptionResults body) throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
		
		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		new ValidateSubscription(publisher).validateGetSubscriptionResults(em, body);
		
		org.apache.juddi.model.Subscription modelSubscription = em.find(org.apache.juddi.model.Subscription.class, body.getSubscriptionKey());
		SubscriptionFilter subscriptionFilter = null;
		try {
			subscriptionFilter = (SubscriptionFilter)JAXBMarshaller.unmarshallFromString(modelSubscription.getSubscriptionFilter(), JAXBMarshaller.PACKAGE_SUBSCRIPTION);
		} 
		catch (JAXBException e) {
			logger.error("JAXB Exception while unmarshalling subscription filter", e);
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		}

		SubscriptionResultsList result = new SubscriptionResultsList();
		
		if (subscriptionFilter.getFindBinding() != null) {
			//Get the current matching keys
			List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);

			// See if there's any missing keys by comparing against the previous matches.  If so, they missing keys are added to the KeyBag and
			// then added to the result
			List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
			if (missingKeys != null && missingKeys.size() > 0) {
				KeyBag missingKeyBag = new KeyBag();
				for (String key : missingKeys)
					missingKeyBag.getBindingKey().add(key);
				
				result.getKeyBag().add(missingKeyBag);
			}
			
			// Re-setting the subscription matches to the new matching key collection
			modelSubscription.getSubscriptionMatches().clear();
			for (Object key : currentMatchingKeys) {
				SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				modelSubscription.getSubscriptionMatches().add(subMatch);
			}
			
			// Now, finding the necessary entities, within the coverage period limits
			FindBinding fb = subscriptionFilter.getFindBinding();
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(fb.getFindQualifiers());
			
			Date startPointDate = new Date(body.getCoveragePeriod().getStartPoint().toGregorianCalendar().getTimeInMillis());
			Date endPointDate = new Date(body.getCoveragePeriod().getEndPoint().toGregorianCalendar().getTimeInMillis());
			
			BindingDetail bindingDetail = InquiryHelper.getBindingDetailFromKeys(fb, findQualifiers, em, currentMatchingKeys, startPointDate, endPointDate);
			
			result.setBindingDetail(bindingDetail);
			
		}
		if (subscriptionFilter.getFindBusiness() != null) {
			//Get the current matching keys
			List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);

			List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
			if (missingKeys != null && missingKeys.size() > 0) {
				KeyBag missingKeyBag = new KeyBag();
				for (String key : missingKeys)
					missingKeyBag.getBusinessKey().add(key);

				result.getKeyBag().add(missingKeyBag);
			}
			
			// Re-setting the subscription matches to the new matching key collection
			modelSubscription.getSubscriptionMatches().clear();
			for (Object key : currentMatchingKeys) {
				SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				modelSubscription.getSubscriptionMatches().add(subMatch);
			}
			
			// Now, finding the necessary entities, within the coverage period limits
			FindBusiness fb = subscriptionFilter.getFindBusiness();
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(fb.getFindQualifiers());
			
			Date startPointDate = new Date(body.getCoveragePeriod().getStartPoint().toGregorianCalendar().getTimeInMillis());
			Date endPointDate = new Date(body.getCoveragePeriod().getEndPoint().toGregorianCalendar().getTimeInMillis());
			
			BusinessList businessList = InquiryHelper.getBusinessListFromKeys(fb, findQualifiers, em, currentMatchingKeys, startPointDate, endPointDate);
			
			result.setBusinessList(businessList);
			
		}
		if (subscriptionFilter.getFindService() != null) {
			//Get the current matching keys
			List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);

			List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
			if (missingKeys != null && missingKeys.size() > 0) {
				KeyBag missingKeyBag = new KeyBag();
				for (String key : missingKeys)
					missingKeyBag.getServiceKey().add(key);

				result.getKeyBag().add(missingKeyBag);
			}
			
			// Re-setting the subscription matches to the new matching key collection
			modelSubscription.getSubscriptionMatches().clear();
			for (Object key : currentMatchingKeys) {
				SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				modelSubscription.getSubscriptionMatches().add(subMatch);
			}
			
			// Now, finding the necessary entities, within the coverage period limits
			FindService fs = subscriptionFilter.getFindService();
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(fs.getFindQualifiers());
			
			Date startPointDate = new Date(body.getCoveragePeriod().getStartPoint().toGregorianCalendar().getTimeInMillis());
			Date endPointDate = new Date(body.getCoveragePeriod().getEndPoint().toGregorianCalendar().getTimeInMillis());
			
			ServiceList serviceList = InquiryHelper.getServiceListFromKeys(fs, findQualifiers, em, currentMatchingKeys, startPointDate, endPointDate);
			
			result.setServiceList(serviceList);
		}
		if (subscriptionFilter.getFindTModel() != null) {
			//Get the current matching keys
			List<?> currentMatchingKeys = getSubscriptionMatches(subscriptionFilter, em);

			List<String> missingKeys = getMissingKeys(currentMatchingKeys, modelSubscription.getSubscriptionMatches());
			if (missingKeys != null && missingKeys.size() > 0) {
				KeyBag missingKeyBag = new KeyBag();
				for (String key : missingKeys)
					missingKeyBag.getTModelKey().add(key);

				result.getKeyBag().add(missingKeyBag);
			}
			
			// Re-setting the subscription matches to the new matching key collection
			modelSubscription.getSubscriptionMatches().clear();
			for (Object key : currentMatchingKeys) {
				SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
				modelSubscription.getSubscriptionMatches().add(subMatch);
			}
			
			// Now, finding the necessary entities, within the coverage period limits
			FindTModel ft = subscriptionFilter.getFindTModel();
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(ft.getFindQualifiers());
			
			Date startPointDate = new Date(body.getCoveragePeriod().getStartPoint().toGregorianCalendar().getTimeInMillis());
			Date endPointDate = new Date(body.getCoveragePeriod().getEndPoint().toGregorianCalendar().getTimeInMillis());
			
			TModelList tmodelList = InquiryHelper.getTModelListFromKeys(ft, findQualifiers, em, currentMatchingKeys, startPointDate, endPointDate);
			
			result.setTModelList(tmodelList);
			
		}
		if (subscriptionFilter.getFindRelatedBusinesses() != null) {
		}
		if (subscriptionFilter.getGetBindingDetail() != null) {
		}
		if (subscriptionFilter.getGetBusinessDetail() != null) {
		}
		if (subscriptionFilter.getGetServiceDetail() != null) {
		}
		if (subscriptionFilter.getGetTModelDetail() != null) {
		}
		if (subscriptionFilter.getGetAssertionStatusReport() != null) {
		}
		
		
		
        tx.commit();
        em.close();

        return result;
	}

	@SuppressWarnings("unchecked")
	public List<Subscription> getSubscriptions(String authInfo)
			throws DispositionReportFaultMessage {
		EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);
		
		List<Subscription> result = new ArrayList<Subscription>(0);
		
		List<org.apache.juddi.model.Subscription> modelSubscriptionList = (List<org.apache.juddi.model.Subscription>)FindSubscriptionByPublisherQuery.select(em, publisher.getAuthorizedName());
		if (modelSubscriptionList != null && modelSubscriptionList.size() > 0) {
			for (org.apache.juddi.model.Subscription modelSubscription : modelSubscriptionList) {
				
				Subscription apiSubscription = new Subscription();
				
				MappingModelToApi.mapSubscription(modelSubscription, apiSubscription);
				
				result.add(apiSubscription);
			}
		}
        
        tx.commit();
        em.close();
		
		return result;
	}


	public void saveSubscription(String authInfo,
			Holder<List<Subscription>> subscription)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);
		new ValidateSubscription(publisher).validateSubscriptions(em, subscription.value);
		
		List<org.uddi.sub_v3.Subscription> apiSubscriptionList = subscription.value;
		for (org.uddi.sub_v3.Subscription apiSubscription : apiSubscriptionList) {
			
			org.apache.juddi.model.Subscription modelSubscription = new org.apache.juddi.model.Subscription();
			
			Object existingEntity = em.find(org.apache.juddi.model.Subscription.class, apiSubscription.getSubscriptionKey());
			if (existingEntity != null) {
				doRenewal((org.apache.juddi.model.Subscription)existingEntity, apiSubscription);
				em.remove(existingEntity);
			}

			doSubscriptionExpirationDate(apiSubscription);
			
			MappingApiToModel.mapSubscription(apiSubscription, modelSubscription);
			
			modelSubscription.setAuthorizedName(publisher.getAuthorizedName());

			// Add the matching keys to the match collection
			List<?> keys = getSubscriptionMatches(apiSubscription.getSubscriptionFilter(), em);
			if (keys != null && keys.size() > 0) {
				for (Object key : keys) {
					SubscriptionMatch subMatch = new SubscriptionMatch(modelSubscription, (String)key);
					modelSubscription.getSubscriptionMatches().add(subMatch);
				}
			}
			
			em.persist(modelSubscription);
		}

		tx.commit();
		em.close();
	}

	/**
	 * Will perform the necessary logic for when a subscription is renewed (evidenced by a subscription with the same key in existence).  
	 * In general, the appropriate data is copied from the stored subscription to the renewal subscription request.
	 * 
	 * @param existingSubscription - existing stored subscription
	 * @param apiSubscription - renewal subscription request
	 * @throws DispositionReportFaultMessage 
	 */
	private void doRenewal(org.apache.juddi.model.Subscription existingSubscription, org.uddi.sub_v3.Subscription apiSubscription) throws DispositionReportFaultMessage {
		if (apiSubscription.getSubscriptionFilter() == null) {
			String rawFilter = existingSubscription.getSubscriptionFilter();
			try {
				SubscriptionFilter existingFilter = (SubscriptionFilter)JAXBMarshaller.unmarshallFromString(rawFilter, "org.uddi.sub_v3");
				apiSubscription.setSubscriptionFilter(existingFilter);
			} 
			catch (JAXBException e) {
				logger.error("JAXB Exception while marshalling subscription filter", e);
				throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
			} 
		}
		
	}
	
	/**
	 * Will add the expiration date to the provided subscription request.  Date is earlier of user provided date and the system default
	 * 
	 * @param apiSubscription
	 * @throws DispositionReportFaultMessage
	 */
	private void doSubscriptionExpirationDate(org.uddi.sub_v3.Subscription apiSubscription) throws DispositionReportFaultMessage {

		int subscriptionExpirationDays = DEFAULT_SUBSCRIPTIONEXPIRATION_DAYS;
		try { 
			subscriptionExpirationDays = AppConfig.getConfiguration().getInt(Property.JUDDI_SUBSCRIPTION_EXPIRATION_DAYS); 
		}
		catch(ConfigurationException ce) { 
			throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval"));
		}

		GregorianCalendar expirationDate = new GregorianCalendar();
		expirationDate.add(GregorianCalendar.DAY_OF_MONTH, subscriptionExpirationDays);
		
		// The expiration date is the earlier of the provided date and that specified by the parameter.
		if (apiSubscription.getExpiresAfter() != null) {
			GregorianCalendar userExpiration = apiSubscription.getExpiresAfter().toGregorianCalendar();
			if (userExpiration.getTimeInMillis() < expirationDate.getTimeInMillis())
				expirationDate.setTimeInMillis(userExpiration.getTimeInMillis());
		}

		try { 
			DatatypeFactory df = DatatypeFactory.newInstance();
			apiSubscription.setExpiresAfter(df.newXMLGregorianCalendar(expirationDate));
		}
		catch(DatatypeConfigurationException ce) { 
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		}
		
	}

	/**
	 * Will take a snapshot of the keys that match the subscription filter return them.
	 * 
	 * @param subscriptionFilter
	 * @param em
	 * @return
	 * @throws DispositionReportFaultMessage
	 */
	private List<?> getSubscriptionMatches(SubscriptionFilter subscriptionFilter, EntityManager em) 
			 throws DispositionReportFaultMessage {
		
		
		List<?> keys = null;
		if (subscriptionFilter.getFindBinding() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindBinding().getFindQualifiers());
			keys = InquiryHelper.findBinding(subscriptionFilter.getFindBinding(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindBusiness() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindBusiness().getFindQualifiers());
			keys = InquiryHelper.findBusiness(subscriptionFilter.getFindBusiness(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindService() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindService().getFindQualifiers());
			keys = InquiryHelper.findService(subscriptionFilter.getFindService(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindTModel() != null) {
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(subscriptionFilter.getFindTModel().getFindQualifiers());
			keys = InquiryHelper.findTModel(subscriptionFilter.getFindTModel(), findQualifiers, em);
		}
		if (subscriptionFilter.getFindRelatedBusinesses() != null) {
			// TODO: should we bother taking a snapshot of these?
		}
		if (subscriptionFilter.getGetBindingDetail() != null) {
			keys = subscriptionFilter.getGetBindingDetail().getBindingKey();
		}
		if (subscriptionFilter.getGetBusinessDetail() != null) {
			keys = subscriptionFilter.getGetBusinessDetail().getBusinessKey();
		}
		if (subscriptionFilter.getGetServiceDetail() != null) {
			keys = subscriptionFilter.getGetServiceDetail().getServiceKey();
		}
		if (subscriptionFilter.getGetTModelDetail() != null) {
			keys = subscriptionFilter.getGetTModelDetail().getTModelKey();
		}
		if (subscriptionFilter.getGetAssertionStatusReport() != null) {
			// Nothing needs to be done
		}
		return keys;
		
	}
	
	private List<String> getMissingKeys(List<?> currentMatchingKeys, List<SubscriptionMatch> subscriptionMatches) {

		List<String> result = new ArrayList<String>(subscriptionMatches.size());
		for (SubscriptionMatch subMatch : subscriptionMatches)
			result.add(subMatch.getEntityKey());
		
		result.removeAll(currentMatchingKeys);
		
		return result;
	}
	
}
