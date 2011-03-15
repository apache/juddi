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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Subscription;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.subscription.notify.Notifier;
import org.apache.juddi.subscription.notify.NotifierFactory;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class SubscriptionNotifier extends TimerTask {

	private Log log = LogFactory.getLog(this.getClass());
	private Timer timer = null;
	private long startBuffer = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_START_BUFFER, 20000l); // 20s startup delay default 
	private long interval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
	private long acceptableLagTime = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_ACCEPTABLE_LAGTIME, 500l); //500 milliseconds
	private UDDISubscriptionImpl subscriptionImpl = new UDDISubscriptionImpl();
	private Boolean registryHasChanges = true;
	private Boolean alwaysNotify = false;
	
	public SubscriptionNotifier() throws ConfigurationException {
		super();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(this, startBuffer, interval);
	}

	public void run() 
	{
		if (alwaysNotify || (registryHasChanges && firedOnTime(scheduledExecutionTime()))) {
			long startTime = System.currentTimeMillis();
			log.debug("Start Notification background task; checking if subscription notifications need to be send out..");
			
			Collection<Subscription> subscriptions = getAllAsyncSubscriptions();
			for (Subscription subscription : subscriptions) {
				//expireCache after subscription.getExpiresAfter().getTime()
				if (subscription.getExpiresAfter()==null || subscription.getExpiresAfter().getTime() > startTime) {
					try {
						GetSubscriptionResults getSubscriptionResults = buildGetSubscriptionResults(subscription, new Date(scheduledExecutionTime()));
						if (getSubscriptionResults!=null) {
							getSubscriptionResults.setSubscriptionKey(subscription.getSubscriptionKey());
							UddiEntityPublisher publisher = new UddiEntityPublisher();
							publisher.setAuthorizedName(subscription.getAuthorizedName());
							SubscriptionResultsList resultList = subscriptionImpl.getSubscriptionResults(getSubscriptionResults, publisher);
							if (resultListContainsChanges(resultList)) {
								log.info("We have a change and need to notify..");
								notify(getSubscriptionResults,resultList);
							}
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
			log.debug("Skipping current notification cycle because lagtime is too great.");
		}
	}
	/**
	 * Checks to see that the event are fired on time. If they are late this may indicate that the server
	 * is under load. The acceptableLagTime is configurable using the "juddi.notification.acceptable.lagtime"
	 * property and is defaulted to 500ms. A negative value means that you do not care about the lag time
	 * and you simply always want to go do the notification work.
	 * 
	 * @param scheduleExecutionTime
	 * @return true if the server is within the acceptable latency lag.
	 */
	private boolean firedOnTime(long scheduleExecutionTime) {
		long lagTime = System.currentTimeMillis() - scheduleExecutionTime;
		if (lagTime <= acceptableLagTime || acceptableLagTime < 0) {
			return true;
		} else {
			log.warn("NotificationTimer is lagging " + lagTime + " milli seconds behind. A lag time "
					+ "which exceeds an acceptable lagtime of " + acceptableLagTime + "ms indicates "
					+ "that the registry server is under stress. We are therefore skipping this notification "
					+ "cycle.");
			return false;
		}
	}
	protected GetSubscriptionResults buildGetSubscriptionResults(Subscription subscription, Date endPoint) 
		throws DispositionReportFaultMessage, DatatypeConfigurationException {
		
		GetSubscriptionResults getSubscriptionResults = null;
		Duration duration = TypeConvertor.convertStringToDuration(subscription.getNotificationInterval());
		Date startPoint = subscription.getLastNotified();
		Date nextDesiredNotificationDate = null;
		if (startPoint==null) startPoint = subscription.getCreateDate();
		nextDesiredNotificationDate = new Date(startPoint.getTime());
		duration.addTo(nextDesiredNotificationDate);

		if (subscription.getLastNotified()==null || nextDesiredNotificationDate.after(startPoint) && nextDesiredNotificationDate.before(endPoint)) {
			getSubscriptionResults = new GetSubscriptionResults();
			CoveragePeriod period = new CoveragePeriod();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(startPoint.getTime());
			period.setStartPoint(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
			calendar.setTimeInMillis(endPoint.getTime());
			period.setEndPoint(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
			if (log.isDebugEnabled()) log.debug("Period " + period.getStartPoint() + " " + period.getEndPoint());
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
	protected Collection<Subscription> getAllAsyncSubscriptions() {
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
			if (resultList.getServiceList()!=null && resultList.getServiceList().getServiceInfos()!=null &&
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
				if (AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType()) ||
						AccessPointType.WSDL_DEPLOYMENT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
					try {
						Notifier notifier = new NotifierFactory().getNotifier(bindingTemplate);
						log.info("Sending out notification to " + bindingTemplate.getAccessPointUrl());
						notifier.notifySubscriptionListener(body);
						//there maybe more chunks we have to send
						String chunkToken=body.getSubscriptionResultsList().getChunkToken();
						while(chunkToken!=null) {
							UddiEntityPublisher publisher = new UddiEntityPublisher();
							publisher.setAuthorizedName(modelSubscription.getAuthorizedName());
							log.debug("Sending out next chunk: " + chunkToken + " to " + bindingTemplate.getAccessPointUrl());
							getSubscriptionResults.setChunkToken(chunkToken);
							resultList = subscriptionImpl.getSubscriptionResults(getSubscriptionResults, publisher);
							body.setSubscriptionResultsList(resultList);
							notifier.notifySubscriptionListener(body);
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
