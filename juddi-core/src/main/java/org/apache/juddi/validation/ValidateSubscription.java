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

 package org.apache.juddi.validation;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.KeyUnavailableException;
import org.apache.juddi.error.ValueNotAllowedException;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.keygen.KeyGeneratorFactory;
import org.apache.juddi.model.UddiEntityPublisher;
import org.uddi.sub_v3.SaveSubscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ValidateSubscription extends ValidateUDDIApi {

	public ValidateSubscription(UddiEntityPublisher publisher) {
		super(publisher);
	}

	public void validateSubscriptions(EntityManager em, List<org.uddi.sub_v3.Subscription> entityList) throws DispositionReportFaultMessage {

		// No null or empty list
		if (entityList == null || entityList.size() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.savesubscription.NoInput"));
		
		for (org.uddi.sub_v3.Subscription entity : entityList) {
			validateSubscription(em, entity);
		}
	}
	
	public void validateSubscription(EntityManager em, org.uddi.sub_v3.Subscription subscription) throws DispositionReportFaultMessage {

		// A supplied subscription can't be null
		if (subscription == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.NullInput"));
		
		boolean entityExists = false;
		String entityKey = subscription.getSubscriptionKey();
		if (entityKey == null || entityKey.length() == 0) {
			KeyGenerator keyGen = KeyGeneratorFactory.getKeyGenerator();
			entityKey = keyGen.generate();
			subscription.setSubscriptionKey(entityKey);
		}
		else {
			// Per section 4.4: keys must be case-folded
			entityKey = entityKey.toLowerCase();
			subscription.setSubscriptionKey(entityKey);

			Object obj = em.find(org.apache.juddi.model.Subscription.class, entityKey);
			if (obj != null) {
				entityExists = true;

				// Subscriptions don't specify ownership.  Therefore, anyone can change a subscription (if they have the key).  
				// This could be implemented by adding the authorizedName field to the model class.
				// Make sure publisher owns this entity.
				//if (!publisher.isOwner((UddiEntity)obj))
				//	throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
			}
			else {
				// Inside this block, we have a key proposed by the publisher on a new entity

				// Validate key and then check to see that the proposed key is valid for this publisher
				ValidateUDDIKey.validateUDDIv3Key(entityKey);
				if (!publisher.isValidPublisherKey(em, entityKey))
					throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));

			}
			
		}

		if (!entityExists) {
			// Check to make sure key isn't used by another entity.
			if (!isUniqueKey(em, entityKey))
				throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.KeyExists", entityKey));
		}
		
		
		validateSubscriptionFilter(subscription.getSubscriptionFilter(), entityExists);
	}

	public void validateSubscriptionFilter(SubscriptionFilter subscriptionFilter, boolean entityExists) throws DispositionReportFaultMessage {
		if (!entityExists && subscriptionFilter == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.NoFilterOnNewSubscription"));
			
		int filterCount = 0;
		ValidateInquiry validateInquiry = new ValidateInquiry(publisher);
		if (subscriptionFilter.getFindBinding() != null) {
			filterCount++;
			validateInquiry.validateFindBinding(subscriptionFilter.getFindBinding());
		}
		if (subscriptionFilter.getFindBusiness() != null) {
			filterCount++;
			validateInquiry.validateFindBusiness(subscriptionFilter.getFindBusiness());
		}
		if (subscriptionFilter.getFindService() != null) {
			filterCount++;
			validateInquiry.validateFindService(subscriptionFilter.getFindService());
		}
		if (subscriptionFilter.getFindTModel() != null) {
			filterCount++;
			validateInquiry.validateFindTModel(subscriptionFilter.getFindTModel(), false);
		}
		if (subscriptionFilter.getFindRelatedBusinesses() != null) {
			filterCount++;
			validateInquiry.validateFindRelatedBusinesses(subscriptionFilter.getFindRelatedBusinesses(), false);
		}
		if (subscriptionFilter.getGetBindingDetail() != null) {
			filterCount++;
			validateInquiry.validateGetBindingDetail(subscriptionFilter.getGetBindingDetail());
		}
		if (subscriptionFilter.getGetBusinessDetail() != null) {
			filterCount++;
			validateInquiry.validateGetBusinessDetail(subscriptionFilter.getGetBusinessDetail());
		}
		if (subscriptionFilter.getGetServiceDetail() != null) {
			filterCount++;
			validateInquiry.validateGetServiceDetail(subscriptionFilter.getGetServiceDetail());
		}
		if (subscriptionFilter.getGetTModelDetail() != null) {
			filterCount++;
			validateInquiry.validateGetTModelDetail(subscriptionFilter.getGetTModelDetail());
		}
		if (subscriptionFilter.getGetAssertionStatusReport() != null) {
			filterCount++;
		}
		
		if (!entityExists && filterCount == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.NoFilterOnNewSubscription"));
		
		if (filterCount > 1)
			throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.TooManyFilters", String.valueOf(filterCount)));
		
	}
	
}
