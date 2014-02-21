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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;

import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.keygen.KeyGeneratorFactory;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.InvalidTimeException;
import org.apache.juddi.v3.error.KeyUnavailableException;
import org.apache.juddi.v3.error.UserMismatchException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ValidateSubscription extends ValidateUDDIApi {

    public ValidateSubscription(UddiEntityPublisher publisher) {
        super(publisher);
    }
    private DatatypeFactory df = null;

    public void validateSubscriptions(EntityManager em, List<org.uddi.sub_v3.Subscription> entityList, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {

        // No null or empty list
        if (entityList == null || entityList.size() == 0) {
            throw new ValueNotAllowedException(new ErrorMessage("errors.savesubscription.NoInput"));
        }

        for (org.uddi.sub_v3.Subscription entity : entityList) {
            validateSubscription(em, entity, publisher);
        }
    }

    private void validateSubscription(EntityManager em, org.uddi.sub_v3.Subscription subscription, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {

        // A supplied subscription can't be null
        if (subscription == null) {
            throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.NullInput"));
        }
        if (df == null) {
            try {
                df = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException ex) {
                throw new FatalErrorException(new ErrorMessage("errors.DatatypeFactor"));
            }
        }
        boolean entityExists = false;
        String entityKey = subscription.getSubscriptionKey();
        //no key specified, make a new one
        if (entityKey == null || entityKey.length() == 0) {
            KeyGenerator keyGen = KeyGeneratorFactory.getKeyGenerator();
            entityKey = keyGen.generate(publisher);
            subscription.setSubscriptionKey(entityKey);
        } else {
            //key specified, validate it
            // Per section 4.4: keys must be case-folded
            entityKey = entityKey.toLowerCase();
            subscription.setSubscriptionKey(entityKey);

            Object obj = em.find(org.apache.juddi.model.Subscription.class, entityKey);
            if (obj != null) {
                entityExists = true;
                //revising a new item
                // Make sure publisher owns this entity.
                if (!publisher.getAuthorizedName().equals(((org.apache.juddi.model.Subscription) obj).getAuthorizedName())) {
                    throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
                }
            } else {
                //new item
                // Inside this block, we have a key proposed by the publisher on a new entity

                // Validate key and then check to see that the proposed key is valid for this publisher
                ValidateUDDIKey.validateUDDIv3Key(entityKey);
                if (!publisher.isValidPublisherKey(em, entityKey)) {
                    throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                }

            }

        }

        if (!entityExists) {
            // Check to make sure key isn't used by another entity.
            if (!isUniqueKey(em, entityKey)) {
                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.KeyExists", entityKey));
            }
        }
        //AO, if it's already expired, why even allow it?
        if (subscription.getExpiresAfter() != null) {
            long expiresat = subscription.getExpiresAfter().toGregorianCalendar().getTimeInMillis();
            if (System.currentTimeMillis() > expiresat) {
                throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.expired"));
            }
        }
        if (subscription.getMaxEntities() != null) {
            if (subscription.getMaxEntities().intValue() <= 0) {
                throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.maxrecordstoosmall"));
            }
        }
        //maxEntities:  This optional integer specifies the maximum number of entities in a notification returned to a subscription listener. 
        //If not specified, the number of entities sent is not limited, unless by node policy.
        try {
            if (subscription.getMaxEntities() == null
                    || subscription.getMaxEntities().intValue() > AppConfig.getConfiguration().getInt(Property.JUDDI_SUBSCRIPTION_MAXENTITIES, 1000)) {
                subscription.setMaxEntities(AppConfig.getConfiguration().getInt(Property.JUDDI_SUBSCRIPTION_MAXENTITIES, 1000));
            }
        } catch (ConfigurationException x) {
            subscription.setMaxEntities(1000);
        }
        /*
         *  notificationInterval:  This optional argument is only required when asynchronous notifications are used. 
         * It is of type xsd:duration and specifies how often change notifications are to be provided to a subscriber. 
         * If the notificationInterval specified is not acceptable due to node policy, then the node adjusts the value to
         * match the next longer time period that is supported.  The adjusted value is provided with the returns from this
         * API.  Also see Section 5.5.1.1 Specifying Durations.
         */
        if (subscription.getNotificationInterval() == null && subscription.getBindingKey() != null) {
            throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.notificationintervalnotdefined"));
        }
        
        //validate that the binding key exists
        validateSubscriptionBindingkeyExists(em,subscription.getBindingKey());
        
        validateSubscriptionFilter(subscription.getSubscriptionFilter(), entityExists);
    }

    /**
     * this handles just the filter items only
     *
     * @param subscriptionFilter
     * @param entityExists or more accurately, is this a new item or not?
     * @throws DispositionReportFaultMessage
     */
    private void validateSubscriptionFilter(SubscriptionFilter subscriptionFilter, boolean entityExists) throws DispositionReportFaultMessage {
        if (!entityExists && subscriptionFilter == null) {
            throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.NoFilterOnNewSubscription"));
        }

        if (subscriptionFilter != null) {
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

            if (filterCount == 0) {
                throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.BlankFilter"));
            }
            //the spec defines subscription filters as a switch, exactly one is required
            if (filterCount > 1) {
                throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.TooManyFilters", String.valueOf(filterCount)));
            }

        }

    }

    public void validateDeleteSubscription(EntityManager em, DeleteSubscription body) throws DispositionReportFaultMessage {
        // No null input
        if (body == null) {
            throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
        }

        // No null or empty list
        List<String> entityKeyList = body.getSubscriptionKey();
        if (entityKeyList == null || entityKeyList.size() == 0) {
            throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
        }

        HashSet<String> dupCheck = new HashSet<String>();
        int i = 0;
        for (String entityKey : entityKeyList) {

            // Per section 4.4: keys must be case-folded
            entityKey = entityKey.toLowerCase();
            entityKeyList.set(i, entityKey);

            boolean inserted = dupCheck.add(entityKey);
            if (!inserted) {
                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
            }

            Object obj = em.find(org.apache.juddi.model.Subscription.class, entityKey);
            if (obj == null) {
                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.SubscriptionNotFound", entityKey));
            }

            // Make sure publisher owns this entity.
            if (!publisher.getAuthorizedName().equals(((org.apache.juddi.model.Subscription) obj).getAuthorizedName())) {
                throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
            }

            i++;
        }
    }

    public void validateGetSubscriptionResults(EntityManager em, GetSubscriptionResults body) throws DispositionReportFaultMessage {
        // No null input
        if (body == null) {
            throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
        }

        String subscriptionKey = body.getSubscriptionKey();
        if (subscriptionKey == null || subscriptionKey.length() == 0) {
            throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NullKey", subscriptionKey));
        }

        // Per section 4.4: keys must be case-folded
        subscriptionKey = subscriptionKey.toLowerCase();
        body.setSubscriptionKey(subscriptionKey);

        Object obj = em.find(org.apache.juddi.model.Subscription.class, subscriptionKey);
        if (obj == null) {
            throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.SubscriptionNotFound", subscriptionKey));
        }

        Date expiresAfter = ((org.apache.juddi.model.Subscription) obj).getExpiresAfter();
        Date now = new Date();
        if (expiresAfter.getTime() < now.getTime()) {
            throw new InvalidKeyPassedException(new ErrorMessage("errors.getsubscriptionresult.SubscriptionExpired", subscriptionKey));
        }

        CoveragePeriod coveragePeriod = body.getCoveragePeriod();
        if (coveragePeriod == null) {
            throw new InvalidTimeException(new ErrorMessage("errors.getsubscriptionresult.NullCoveragePeriod"));
        }

        if (coveragePeriod.getStartPoint() == null || coveragePeriod.getEndPoint() == null) {
            throw new InvalidTimeException(new ErrorMessage("errors.getsubscriptionresult.InvalidDateInCoveragePeriod"));
        }

        GregorianCalendar startPoint = coveragePeriod.getStartPoint().toGregorianCalendar();
        GregorianCalendar endPoint = coveragePeriod.getEndPoint().toGregorianCalendar();
        if (startPoint.getTimeInMillis() > endPoint.getTimeInMillis()) {
            throw new InvalidTimeException(new ErrorMessage("errors.getsubscriptionresult.StartPointAfterEndPoint", startPoint.toString()));
        }
    }

    private void validateSubscriptionBindingkeyExists(EntityManager em, String bindingKey) throws ValueNotAllowedException {
        if (bindingKey==null || bindingKey.length()==0) {
            return;
        }
        Object obj = em.find(org.apache.juddi.model.BindingTemplate.class, bindingKey);
        if (obj==null) {
            throw new ValueNotAllowedException(new ErrorMessage("errors.subscription.BindingDoesntExist",bindingKey));
        }
    }
}
