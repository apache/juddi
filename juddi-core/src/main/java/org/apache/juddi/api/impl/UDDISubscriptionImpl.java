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

import java.util.List;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.ws.Holder;

import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionPortType;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.PersistenceManager;


@WebService(serviceName="UDDISubscriptionService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionPortType")
public class UDDISubscriptionImpl implements UDDISubscriptionPortType {


	public void deleteSubscription(DeleteSubscription body)
			throws DispositionReportFaultMessage {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

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
        String authInfo = body.getAuthInfo();
		
		EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        tx.commit();
        em.close();
        return null;
	}


	public List<Subscription> getSubscriptions(String authInfo)
			throws DispositionReportFaultMessage {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        List<?> keysFound = null;

        // TODO : find the subscriptions
        
        tx.commit();
        em.close();
		
		return null;
	}


	public void saveSubscription(String authInfo,
			Holder<List<Subscription>> subscription)
			throws DispositionReportFaultMessage {
		
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
                
        tx.commit();
        em.close();

	}

}
