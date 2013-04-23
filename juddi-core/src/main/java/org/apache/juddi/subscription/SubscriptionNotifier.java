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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.ws.WebServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.ServiceCounterLifecycleResource;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.api.impl.UDDIServiceCounter;
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
	private long acceptableLagTime = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_ACCEPTABLE_LAGTIME, 1000l); //1000 milliseconds
	private int maxTries = AppConfig.getConfiguration().getInt(Property.JUDDI_NOTIFICATION_MAX_TRIES, 3);
	private long badListResetInterval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_LIST_RESET_INTERVAL, 1000l * 3600); //one hour
	private UDDISubscriptionImpl subscriptionImpl = new UDDISubscriptionImpl();
	private Boolean alwaysNotify = false;
	private Date desiredDate = null;
	private int lastUpdateCounter;
	private UDDIServiceCounter serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(UDDIPublicationImpl.class);
	private String[] attributes = {
			  "save_business",  "save_service",  "save_binding",  "save_tmodel",
			"delete_business","delete_service","delete_binding","delete_tmodel",
			"add_publisherassertions","set_publisherassertions","delete_publisherassertions"
	};
	private static Map<String,Integer> badNotifications= new ConcurrentHashMap<String,Integer>();
	private static Date lastBadNotificationReset = new Date();
	
	public SubscriptionNotifier() throws ConfigurationException {
		super();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(this, startBuffer, interval);
	}
	
	@Override
	public boolean cancel() {
		timer.cancel();
		return super.cancel();
	}
	
	/**
	 * If the CRUD methods on the publication API where not called, this registry node does not contain changes. If
	 * the registry database is shared with other registry nodes and one of those registries pushed in a change, then
	 * that registry node will take care of sending out notifications.
	 * @return
	 */
	protected boolean registryMayContainUpdates() {
		boolean isUpdated = false;
		int updateCounter = 0;
		//if the desiredDate is set it means that we've declined sending out a notification before
		//because the a client did not want a notification yet. However if this desired
		//notification time has come we should try sending out the notification now.
		if (desiredDate!=null && new Date().getTime() > desiredDate.getTime()) {
			return true;
		}
		try {
			for (String attribute : attributes) {
				String counter = serviceCounter.getAttribute(attribute + " successful queries").toString();
				updateCounter += Integer.valueOf(counter);
			}
			// if the counts are not the same something has changed, 
			// this accounts for the case where the counters where reset.
			if (updateCounter != lastUpdateCounter) {
				lastUpdateCounter = updateCounter;
				isUpdated = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return isUpdated;
	}

	public synchronized void run() 
	{
		if (badListResetInterval > 0 && new Date().getTime() > lastBadNotificationReset.getTime() + badListResetInterval) {
			badNotifications = new ConcurrentHashMap<String,Integer>();
			lastBadNotificationReset = new Date();
			log.debug("badNotificationList was reset");
		}
		if ((firedOnTime(scheduledExecutionTime()) || alwaysNotify) && registryMayContainUpdates()) {
			long startTime = System.currentTimeMillis();
			desiredDate = null;
			log.debug("Start Notification background task; checking if subscription notifications need to be send out..");
			
			Collection<Subscription> subscriptions = getAllAsyncSubscriptions();
			for (Subscription subscription : subscriptions) {
				
				
				if (subscription.getExpiresAfter()==null || subscription.getExpiresAfter().getTime() > startTime ||
						!isTemporarilyDisabled(subscription.getSubscriptionKey())) {
					try {
						//build a query with a coverage period from the lastNotified time to 
						//now (the scheduled Execution time)
						Date notificationDate = new Date(scheduledExecutionTime());
						GetSubscriptionResults getSubscriptionResults = 
							buildGetSubscriptionResults(subscription, notificationDate);
						if (getSubscriptionResults!=null) {
							getSubscriptionResults.setSubscriptionKey(subscription.getSubscriptionKey());
							UddiEntityPublisher publisher = new UddiEntityPublisher();
							publisher.setAuthorizedName(subscription.getAuthorizedName());
							SubscriptionResultsList resultList = subscriptionImpl.getSubscriptionResults(getSubscriptionResults, publisher);
							if (resultListContainsChanges(resultList)) {
								log.debug("We have a change and need to notify " + subscription.getSubscriptionKey());
								notify(getSubscriptionResults,resultList, notificationDate);
							} else {
								log.debug("No changes where recorded, no need to notify.");
							}
						}
					} catch (Exception e) {
						log.error("Could not obtain subscriptionResult for subscriptionKey " 
								+ subscription.getSubscriptionKey() + ". " + e.getMessage(),e);
					}	
				} else {
					// the subscription expired, we should delete it
					log.info("Subcription with key " + subscription.getSubscriptionKey() 
							+ " expired " + subscription.getExpiresAfter());
					deleteSubscription(subscription);
				}
			}
            long endTime   = System.currentTimeMillis();
            
            if ((endTime-startTime) > interval) {
            	log.debug("Notification background task duration exceeds the JUDDI_NOTIFICATION_INTERVAL" +
            			" of " + interval + ". Notification background task took " 
            			+ (endTime - startTime) + " milliseconds.");
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
			log.debug("NotificationTimer is lagging " + lagTime + " milli seconds behind. A lag time "
					+ "which exceeds an acceptable lagtime of " + acceptableLagTime + "ms indicates "
					+ "that the registry server is under load or was in sleep mode. We are therefore skipping this notification "
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
		//nextDesiredNotificationDate = lastTime + the Interval Duration, which should be:
		//AFTER the lastNotified time and BEFORE the endTime (current time). If it is
		//after the endTime, then the user does not want a notification yet, so we accumulate.
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
		} else {
			log.debug("Client does not yet want a notification. The next desidered notification Date " + nextDesiredNotificationDate + ". The current interval [" 
				+ startPoint + " , " + endPoint + "] therefore skipping this notification cycle.");
			if (desiredDate==null || nextDesiredNotificationDate.getTime() < desiredDate.getTime()) {
				desiredDate = nextDesiredNotificationDate;
			}
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
		//When the response is 'brief', or when there are deleted only keyBags are used.
		if (resultList.getKeyBag()!=null && resultList.getKeyBag().size() > 0) return true;
		//there are no changes to what was subscribed to
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
	 * Deletes the subscription. i.e. when it is expired.
	 * @param subscription
	 */
	protected void deleteSubscription(Subscription subscription) {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.remove(subscription);
		    tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
	/**
	 * Sends out the notifications.
	 * @param resultList
	 * @throws MalformedURLException 
	 * @throws DispositionReportFaultMessage 
	 */
	protected void notify(GetSubscriptionResults getSubscriptionResults, SubscriptionResultsList resultList, Date notificationDate) 
	{
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			String subscriptionKey = resultList.getSubscription().getSubscriptionKey();
			org.apache.juddi.model.Subscription modelSubscription = 
				em.find(org.apache.juddi.model.Subscription.class, subscriptionKey);
			Date lastNotifiedDate = modelSubscription.getLastNotified();
			//now log to the db that we are sending the notification.
			tx.begin();
			modelSubscription.setLastNotified(notificationDate);
			em.persist(modelSubscription);
			tx.commit();
			
			org.apache.juddi.model.BindingTemplate bindingTemplate= em.find(org.apache.juddi.model.BindingTemplate.class, modelSubscription.getBindingKey());
			NotifySubscriptionListener body = new NotifySubscriptionListener();
//			if (resultList.getServiceList()!=null && resultList.getServiceList().getServiceInfos()!=null &&
//					resultList.getServiceList().getServiceInfos().getServiceInfo().size() == 0) {
//				resultList.getServiceList().setServiceInfos(null);
//			}
			body.setSubscriptionResultsList(resultList);
			String authorizedName = modelSubscription.getAuthorizedName();
			UDDISecurityImpl security = new UDDISecurityImpl();
			
			if (authorizedName != null) { // add a security token if needed
				try {
					//obtain a token for this publisher
					org.uddi.api_v3.AuthToken token = security.getAuthToken(authorizedName);
					body.setAuthInfo(token.getAuthInfo());
				} catch (DispositionReportFaultMessage e) {
					body.setAuthInfo("Failed to generate token, please contact UDDI admin");
					log.error(e.getMessage(),e);
				}
			}
			
			if (bindingTemplate!=null) {
				if (AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType()) ||
						AccessPointType.WSDL_DEPLOYMENT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
					try {
						Notifier notifier = new NotifierFactory().getNotifier(bindingTemplate);
						if (notifier!=null) {
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
							//successful notification so remove from the badNotificationList
							if (badNotifications.containsKey(resultList.getSubscription().getSubscriptionKey()))
								badNotifications.remove(resultList.getSubscription().getSubscriptionKey());
						}
					} catch (WebServiceException e) {
						if (e.getCause() instanceof IOException) {
							addBadNotificationToList(subscriptionKey, bindingTemplate.getAccessPointUrl());
							//we could not notify so compensate the transaction above
							modelSubscription.setLastNotified(lastNotifiedDate);
							tx.begin();
							em.persist(modelSubscription);
							tx.commit();
						} else {
							log.warn("Unexpected WebServiceException " + e.getMessage() + e.getCause());
						}
						
					} catch (Exception e) {
						log.warn("Unexpected notification exception:" + e.getMessage() + e.getCause());
					}
				} else {
					log.info("Binding " + bindingTemplate.getEntityKey() + " has an unsupported binding type of " 
							+ bindingTemplate.getAccessPointType() + ". Only " 
							+ AccessPointType.END_POINT.toString() + " and "
							+ AccessPointType.WSDL_DEPLOYMENT.toString() + " are supported.");
					addBadNotificationToList(subscriptionKey, bindingTemplate.getAccessPointType() + " not supported");
				}
			} else {
				log.info("There is no valid binding template defined for this subscription: " + modelSubscription.getBindingKey());
				addBadNotificationToList(subscriptionKey, modelSubscription.getBindingKey() + " not found");
			}
			
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
	
	private boolean isTemporarilyDisabled(String subscriptionKey) {
		if (maxTries > 0 && badNotifications.containsKey(subscriptionKey) && badNotifications.get(subscriptionKey) > maxTries ) {
			log.debug("Subscription " + subscriptionKey + " is temperarily disabled. The notification endpoint" +
					" could not be reached more then " + maxTries + " times");
			return true;
		}
		return false;
	}
	
	private int addBadNotificationToList(String subscriptionKey, String endPoint) {
		Integer numberOfBadNotifications = 0;
		if (badNotifications.containsKey(subscriptionKey))
			numberOfBadNotifications = badNotifications.get(subscriptionKey);
		badNotifications.put(subscriptionKey, ++numberOfBadNotifications);
		log.debug("bad notification number " + numberOfBadNotifications + " for subscription " 
				+  subscriptionKey + " " + endPoint);
		return numberOfBadNotifications;
	}

}
