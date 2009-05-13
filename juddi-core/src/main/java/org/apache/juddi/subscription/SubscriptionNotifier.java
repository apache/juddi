package org.apache.juddi.subscription;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Subscription;
import org.apache.juddi.model.SubscriptionMatch;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.PersistenceManager;
import org.apache.log4j.Logger;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;

public class SubscriptionNotifier extends TimerTask {

	private Logger log = Logger.getLogger(this.getClass());
	Timer timer = new Timer();
	long interval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
	UDDISubscriptionImpl subscriptionImpl = new UDDISubscriptionImpl();
	private static long ACCEPTABLE_LAG_TIME = 20l; //20 milliseconds
	
	public SubscriptionNotifier() throws ConfigurationException {
		super();
		timer.scheduleAtFixedRate(this, new Date(), interval);
	}

	public void run() 
	{
		if (firedOnTime(scheduledExecutionTime())) {
			long startTime = System.currentTimeMillis();
			log.info("Start Notification background task; checking if subscription notifications need to be send out..");
			
			Collection<Subscription> subscriptions = getAllSubscriptions();
			for (Subscription subscription : subscriptions) {
				if (subscription.getExpiresAfter()==null || subscription.getExpiresAfter().getTime() > startTime) {
					SubscriptionResultsList resultList = getSubscriptionResultList(subscription);
					if (resultListContainsChanges(resultList)) {
						log.info("We need to notify..");
						System.out.println(resultList);
						//Obtain the service we need to call.
						
						
						//now log to the db that we notified.
						//Date notificationDate = new Date();
						//subscription.setLastNotified(notificationDate);
						//em.persist(subscription);
					}
				}
			}
            long endTime   = System.currentTimeMillis();
            
            if ((endTime-startTime) > interval) {
            	log.warn("Notification background task duration exceeds the JUDDI_NOTIFICATION_INTERVAL of " + interval);
            	log.warn("Notification background task took " + (endTime - startTime) + " milliseconds.");
            } else {
            	log.info("Notification background task took " + (endTime - startTime) + " milliseconds.");
            }
		} else {
			log.warn("Skipping current notification cycle because the registry is busy.");
		}
	}
	
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
	/**
	 * Obtains the SubscriptionResultsList for a subscription.
	 * 
	 * @param subscription
	 * @return
	 */
	protected SubscriptionResultsList getSubscriptionResultList(Subscription subscription) {
		SubscriptionResultsList resultList = null;
		try {
			Date startPoint = subscription.getLastNotified();
			if (startPoint==null) startPoint = subscription.getCreateDate();
			Date endPoint   = new Date(scheduledExecutionTime());

			Duration duration = TypeConvertor.convertStringToDuration(subscription.getNotificationInterval());
			Date nextDesiredNotificationDate = startPoint;
			duration.addTo(nextDesiredNotificationDate);

			if (subscription.getLastNotified()==null || nextDesiredNotificationDate.after(startPoint) && nextDesiredNotificationDate.before(endPoint)) {
				GetSubscriptionResults subscriptionResults = new GetSubscriptionResults();
				
				CoveragePeriod period = new CoveragePeriod();
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(startPoint.getTime());
				period.setStartPoint(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
				calendar.setTimeInMillis(endPoint.getTime());
				period.setEndPoint(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
				subscriptionResults.setCoveragePeriod(period);
				
				subscriptionResults.setSubscriptionKey(subscription.getSubscriptionKey());
				UddiEntityPublisher publisher = new UddiEntityPublisher();
				publisher.setAuthorizedName(subscription.getAuthorizedName());
				resultList = subscriptionImpl.getSubscriptionResults(subscriptionResults, publisher);
			}

		} catch (Exception e) {
			log.error("Could not obtain subscriptionResult for subscriptionKey " 
					+ subscription.getSubscriptionKey() + ". " + e.getMessage(),e);
		}
		return resultList;
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
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Collection<Subscription> getAllSubscriptions() {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery("SELECT s FROM Subscription s");
	    Collection<Subscription> subscriptions = (Collection<Subscription>) query.getResultList();
	    tx.commit();
		em.close();
	    return subscriptions;
	}
	/**
	 * Sends out the notifications.
	 * @param resultList
	 * @throws MalformedURLException 
	 */
	protected void notify(SubscriptionResultsList resultList) throws MalformedURLException {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		org.apache.juddi.model.Subscription modelSubscription = em.find(org.apache.juddi.model.Subscription.class, resultList.getSubscription().getSubscriptionKey());
		org.apache.juddi.model.BindingTemplate bindingTemplate= em.find(org.apache.juddi.model.BindingTemplate.class, modelSubscription.getBindingKey());
		if (bindingTemplate!=null) {
			if (bindingTemplate.getAccessPointType().equalsIgnoreCase("wsdl")) {
				URL url = new URL(bindingTemplate.getAccessPointUrl());
				//TODO call this url.
			} else if (bindingTemplate.getAccessPointType().equalsIgnoreCase("email")) {
				log.warn("Notifications via email are not yet supported");
			} else {
				log.error("Not supported binding type.");
			}
		}
		System.out.println("hello");
	}

}
