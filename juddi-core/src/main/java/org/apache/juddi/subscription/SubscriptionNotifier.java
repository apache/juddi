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
import javax.persistence.Query;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api.AccessPointType;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Subscription;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.PersistenceManager;
import org.apache.log4j.Logger;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

/*
 * TODO lock the database per subscription, so multiple nodes can share the same
 * database and work on notifications.
 */
public class SubscriptionNotifier extends TimerTask {

	private Logger log = Logger.getLogger(this.getClass());
	Timer timer = new Timer();
	long interval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
	UDDISubscriptionImpl subscriptionImpl = new UDDISubscriptionImpl();
	private static long ACCEPTABLE_LAG_TIME = 20l; //20 milliseconds
	private static String SUBR_V3_NAMESPACE = "urn:uddi-org:subr_v3_portType";
	private static String SUBSCRIPTION_LISTENER = "SubscriptionListener";
	
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
						log.info("We have a change and need to notify..");
						notify(resultList);
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
	 * @throws DispositionReportFaultMessage 
	 */
	protected void notify(SubscriptionResultsList resultList) 
	{
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			org.apache.juddi.model.Subscription modelSubscription = em.find(org.apache.juddi.model.Subscription.class, resultList.getSubscription().getSubscriptionKey());
			org.apache.juddi.model.BindingTemplate bindingTemplate= em.find(org.apache.juddi.model.BindingTemplate.class, modelSubscription.getBindingKey());
			NotifySubscriptionListener body = new NotifySubscriptionListener();
			body.setSubscriptionResultsList(resultList);
			//body.setAuthInfo();  //where would I get the authInfo from?
			if (bindingTemplate!=null) {
				if (AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
					QName qName = new QName(SUBR_V3_NAMESPACE, SUBSCRIPTION_LISTENER);
					try {
						Service service = Service.create(new URL(bindingTemplate.getAccessPointUrl()), qName);
						UDDISubscriptionListenerPortType subscriptionListener = (UDDISubscriptionListenerPortType) service.getPort(UDDISubscriptionListenerPortType.class);
						subscriptionListener.notifySubscriptionListener(body);
					
						//now log to the db that we notified.
						Date notificationDate = new Date();
						modelSubscription.setLastNotified(notificationDate);
						em.persist(modelSubscription);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				} else {
					log.error("Unsupported binding type.");
				}
			}
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

}
