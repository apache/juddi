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
package org.apache.juddi.subscription;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Subscription;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.log4j.Logger;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class SubscriptionNotifier extends TimerTask {

	private Logger log = Logger.getLogger(this.getClass());
	private Timer timer = new Timer();
	private long startBuffer = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_START_BUFFER, 20000l); // 20s startup delay default 
	private long interval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
	private UDDISubscriptionImpl subscriptionImpl = new UDDISubscriptionImpl();
	private static long ACCEPTABLE_LAG_TIME = 500l; //20 milliseconds
	private static String SUBR_V3_NAMESPACE = "urn:uddi-org:subr_v3_portType";
	private static String SUBSCRIPTION_LISTENER = "UDDISubscriptionListenerService";
	
	public SubscriptionNotifier() throws ConfigurationException {
		super();
		timer.scheduleAtFixedRate(this, startBuffer, interval);
	}

	public void run() 
	{
		if (firedOnTime(scheduledExecutionTime())) {
			long startTime = System.currentTimeMillis();
			log.debug("Start Notification background task; checking if subscription notifications need to be send out..");
			
			Collection<Subscription> subscriptions = getAllSubscriptions();
			for (Subscription subscription : subscriptions) {
				if (subscription.getExpiresAfter()==null || subscription.getExpiresAfter().getTime() > startTime) {
					try {
						GetSubscriptionResults getSubscriptionResults = buildGetSubscriptionResults(subscription);
						getSubscriptionResults.setSubscriptionKey(subscription.getSubscriptionKey());
						UddiEntityPublisher publisher = new UddiEntityPublisher();
						publisher.setAuthorizedName(subscription.getAuthorizedName());
						SubscriptionResultsList resultList = subscriptionImpl.getSubscriptionResults(getSubscriptionResults, publisher);
						if (resultListContainsChanges(resultList)) {
							log.info("We have a change and need to notify..");
							notify(getSubscriptionResults,resultList);
						}
					} catch (Exception e) {
						log.error("Could not obtain subscriptionResult for subscriptionKey " 
								+ subscription.getSubscriptionKey() + ". " + e.getMessage(),e);
					}
						
				}
			}
            long endTime   = System.currentTimeMillis();
            
            if ((endTime-startTime) > interval) {
            	log.warn("Notification background task duration exceeds the JUDDI_NOTIFICATION_INTERVAL of " + interval);
            	log.warn("Notification background task took " + (endTime - startTime) + " milliseconds.");
            } else {
            	log.debug("Notification background task took " + (endTime - startTime) + " milliseconds.");
            }
		} else {
			log.warn("Skipping current notification cycle because the registry is too busy.");
		}
	}
	/**
	 * Checks to see that the event are fired on time. If they are late this may indicate that the server
	 * is under load.
	 * 
	 * @param scheduleExecutionTime
	 * @return true if the server is within the acceptable latency lag.
	 */
	private boolean firedOnTime(long scheduleExecutionTime) {
		long lagTime = System.currentTimeMillis() - scheduleExecutionTime;
		if (lagTime <= ACCEPTABLE_LAG_TIME) {
			return true;
		} else {
			log.warn("NotificationTimer is lagging " + lagTime + " milli seconds behind. A lag time "
					+ "which exceeds an acceptable lagtime of " + ACCEPTABLE_LAG_TIME + "ms indicates "
					+ "that the registry server is under stress. We are therefore skipping this notification "
					+ "cycle.");
			return false;
		}
	}
	protected GetSubscriptionResults buildGetSubscriptionResults(Subscription subscription) 
		throws DispositionReportFaultMessage, DatatypeConfigurationException {
		
		GetSubscriptionResults getSubscriptionResults = null;
		Date startPoint = subscription.getLastNotified();
		if (startPoint==null) startPoint = subscription.getCreateDate();
		Date endPoint   = new Date(scheduledExecutionTime());

		Duration duration = TypeConvertor.convertStringToDuration(subscription.getNotificationInterval());
		Date nextDesiredNotificationDate = startPoint;
		duration.addTo(nextDesiredNotificationDate);

		if (subscription.getLastNotified()==null || nextDesiredNotificationDate.after(startPoint) && nextDesiredNotificationDate.before(endPoint)) {
			getSubscriptionResults = new GetSubscriptionResults();
			
			CoveragePeriod period = new CoveragePeriod();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(startPoint.getTime());
			period.setStartPoint(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
			calendar.setTimeInMillis(endPoint.getTime());
			period.setEndPoint(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
			getSubscriptionResults.setCoveragePeriod(period);
		}
		return getSubscriptionResults;
		
	}
	
	protected boolean resultListContainsChanges(SubscriptionResultsList resultList)
	{
		if (resultList==null) return false;
		if (resultList.getBindingDetail() !=null || resultList.getBusinessDetail()!=null
	     || resultList.getBusinessList()  !=null || resultList.getServiceDetail() !=null
	     || resultList.getServiceList()   !=null || resultList.getTModelDetail()  !=null
	     || resultList.getTModelList()    !=null || resultList.getRelatedBusinessesList() !=null) {
			return true;
		}
		return false;
	}
	/**
	 * Obtains all subscriptions in the system.
	 * @return Collection of All Subscriptions in the system.
	 */
	@SuppressWarnings("unchecked")
	protected Collection<Subscription> getAllSubscriptions() {
		Collection<Subscription> subscriptions = null;
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createQuery("SELECT s FROM Subscription s WHERE s.bindingKey IS NOT NULL");
		    subscriptions = (Collection<Subscription>) query.getResultList();
		    tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	    return subscriptions;
	}
	/**
	 * Sends out the notifications.
	 * @param resultList
	 * @throws MalformedURLException 
	 * @throws DispositionReportFaultMessage 
	 */
	protected void notify(GetSubscriptionResults getSubscriptionResults, SubscriptionResultsList resultList) 
	{
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			org.apache.juddi.model.Subscription modelSubscription = em.find(org.apache.juddi.model.Subscription.class, resultList.getSubscription().getSubscriptionKey());
			log.debug("Taking out a write lock on this subscription, and bail if we can't get it since that would mean" 
			 + " another jUDDI instance is in the process of sending out the notification.");
			em.lock(modelSubscription, LockModeType.WRITE);
			Date startPoint = resultList.getCoveragePeriod().getStartPoint().toGregorianCalendar().getTime();
			Date endPoint   = resultList.getCoveragePeriod().getEndPoint().toGregorianCalendar().getTime();
			if (modelSubscription.getLastNotified()!=null 
					&& startPoint.before(modelSubscription.getLastNotified()) 
					&& endPoint.after(modelSubscription.getLastNotified())) {
				 log.info("We already send out a notification within this coverage period, no need to send another one.");
				 return;
			}
			org.apache.juddi.model.BindingTemplate bindingTemplate= em.find(org.apache.juddi.model.BindingTemplate.class, modelSubscription.getBindingKey());
			NotifySubscriptionListener body = new NotifySubscriptionListener();
			if (resultList.getServiceList().getServiceInfos()!=null &&
					resultList.getServiceList().getServiceInfos().getServiceInfo().size() == 0) {
				resultList.getServiceList().setServiceInfos(null);
			}
			body.setSubscriptionResultsList(resultList);
			String authorizedName = modelSubscription.getAuthorizedName();
			UDDISecurityImpl security = new UDDISecurityImpl();
			try {
				//obtain a token for this publisher
				org.uddi.api_v3.AuthToken token = security.getAuthToken(authorizedName);
				body.setAuthInfo(token.getAuthInfo());
			} catch (DispositionReportFaultMessage e) {
				body.setAuthInfo("Failed to generate token, please contact UDDI admin");
				log.error(e.getMessage(),e);
			}
			
			if (bindingTemplate!=null) {
				if (AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
					QName qName = new QName(SUBR_V3_NAMESPACE, SUBSCRIPTION_LISTENER);
					try {
						Service service = Service.create(new URL(bindingTemplate.getAccessPointUrl()), qName);
						UDDISubscriptionListenerPortType subscriptionListenerPort = (UDDISubscriptionListenerPortType) service.getPort(UDDISubscriptionListenerPortType.class);
						log.info("Sending out notification to " + bindingTemplate.getAccessPointUrl());
						subscriptionListenerPort.notifySubscriptionListener(body);
						//there maybe more chunks we have to send
						String chunkToken=body.getSubscriptionResultsList().getChunkToken();
						while(chunkToken!=null) {
							UddiEntityPublisher publisher = new UddiEntityPublisher();
							publisher.setAuthorizedName(modelSubscription.getAuthorizedName());
							log.debug("Sending out next chunk: " + chunkToken + " to " + bindingTemplate.getAccessPointUrl());
							getSubscriptionResults.setChunkToken(chunkToken);
							resultList = subscriptionImpl.getSubscriptionResults(getSubscriptionResults, publisher);
							body.setSubscriptionResultsList(resultList);
							subscriptionListenerPort.notifySubscriptionListener(body);
							chunkToken=body.getSubscriptionResultsList().getChunkToken();
						}
						//now log to the db that we completed sending the notification.
						Date notificationDate = new Date();
						modelSubscription.setLastNotified(notificationDate);
						em.persist(modelSubscription);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				} else {
					log.error("Unsupported binding type.");
				}
			} else {
				log.error("There is no valid binding template defined for this subscription: " + modelSubscription.getBindingKey());
			}
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	protected UDDISubscriptionImpl getSubscriptionImpl() {
		return subscriptionImpl;
	}

}
