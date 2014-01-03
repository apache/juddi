package org.apache.juddi.v3.tck;

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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDI_090_SubscriptionListenerIntegrationTest {

    private static Log logger = LogFactory.getLog(UDDI_090_SubscriptionListenerIntegrationTest.class);
    private static TckTModel tckTModel = null;
    private static TckBusiness tckBusiness = null;
    private static TckBusinessService tckBusinessService = null;
    private static TckSubscriptionListener tckSubscriptionListener = null;
    private static Endpoint endPoint;
    private static String authInfoJoe = null;
    private static String authInfoMary = null;
    private static UDDIClient manager;
    private static Integer httpPort = 80;
    private static UDDISubscriptionPortType subscription = null;
    private static UDDIInquiryPortType inquiry=null;

    @AfterClass
    public static void stopManager() throws ConfigurationException {
        manager.stop();
        //shutting down the TCK SubscriptionListener
        endPoint.stop();
        endPoint = null;
    }

    @BeforeClass
    public static void startManager() throws ConfigurationException {
        try {
            httpPort = 9600 + new Random().nextInt(99);

            //bring up the TCK SubscriptionListener
            String httpEndpoint = "http://localhost:" + httpPort + "/tcksubscriptionlistener";
            System.out.println("Bringing up SubscriptionListener endpoint at " + httpEndpoint);
            endPoint = Endpoint.publish(httpEndpoint, new UDDISubscriptionListenerImpl());

            manager = new UDDIClient();
            manager.start();

            logger.debug("Getting auth tokens..");


            Transport transport = manager.getTransport();
            UDDISecurityPortType security = transport.getUDDISecurityService();
            authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
            authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
            Assert.assertNotNull(authInfoJoe);
            subscription = transport.getUDDISubscriptionService();

            UDDIPublicationPortType publication = transport.getUDDIPublishService();
             inquiry = transport.getUDDIInquiryService();
            tckTModel = new TckTModel(publication, inquiry);
            tckBusiness = new TckBusiness(publication, inquiry);
            tckBusinessService = new TckBusinessService(publication, inquiry);
            tckSubscriptionListener = new TckSubscriptionListener(subscription, publication);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Assert.fail("Could not obtain authInfo token.");
        }
    }

    private void removeAllExistingSubscriptions(String authinfo) {
        List<Subscription> subscriptions;
        try {
            subscriptions = subscription.getSubscriptions(authinfo);

            DeleteSubscription ds = new DeleteSubscription();
            ds.setAuthInfo(authinfo);
            for (int i = 0; i < subscriptions.size(); i++) {
                ds.getSubscriptionKey().add(subscriptions.get(i).getSubscriptionKey());
            }
            if (!subscriptions.isEmpty()) {
                logger.info("Purging " + subscriptions.size() + " old subscriptions");
                subscription.deleteSubscription(ds);
            }
        } catch (Exception ex) {
            logger.warn("error clearing subscriptions", ex);
        }
    }

    @Test
    public void joePublisherUpdateService_HTTP_FIND_SERVICE() {
        logger.info("joePublisherUpdateService_HTTP_FIND_SERVICE");
        try {
            removeAllExistingSubscriptions(authInfoJoe);
            UDDISubscriptionListenerImpl.notifcationMap.clear();
            UDDISubscriptionListenerImpl.notificationCount = 0;
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            //Saving the binding template that will be called by the server for a subscription event
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the HTTP Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort);
            //Saving the HTTP Subscription
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription1.xml");
            //Changing the service we subscribed to "JoePublisherService"
            Thread.sleep(1000);
            logger.info("Updating Service ********** ");
            tckBusinessService.updateJoePublisherService(authInfoJoe, "foo");

            //waiting up to 100 seconds for the listener to notice the change.
            for (int i = 0; i < 200; i++) {
                Thread.sleep(500);
                System.out.print(".");
                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                    logger.info("Received HTTP Notification");
                    break;
                }
            }
            if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                Assert.fail("No HttpNotification was sent");
            }
            if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Service One</name>")) {
                Assert.fail("Notification does not contain the correct service");
            }

        } catch (Exception e) {
            logger.error("No exceptions please.");
            e.printStackTrace();

            Assert.fail();
        } finally {
            tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
            tckBusinessService.deleteJoePublisherService(authInfoJoe);
            tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
            tckTModel.deleteJoePublisherTmodel(authInfoJoe);
        }
    }

 //   @Test
    public void joePublisherUpdateBusiness_HTTP_FIND_BUSINESS() {
        logger.info("joePublisherUpdateBusiness_HTTP_FIND_BUSINESS");
        try {
            removeAllExistingSubscriptions(authInfoJoe);
            DumpAllBusinesses();
            Thread.sleep(5000);
            UDDISubscriptionListenerImpl.notifcationMap.clear();
            UDDISubscriptionListenerImpl.notificationCount = 0;
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the Listener Service
            logger.info("Saving Joe's callback endpoint ********** ");
            tckSubscriptionListener.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort);
            //Saving the Subscription
            logger.info("Saving Joe's subscription********** ");
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription2.xml");
            //Changing the service we subscribed to "JoePublisherService"
            DumpAllBusinesses();
            logger.info("Clearing the inbox********** ");
            UDDISubscriptionListenerImpl.notifcationMap.clear();
            UDDISubscriptionListenerImpl.notificationCount = 0;
            Thread.sleep(2000);
            logger.info("Saving Mary's Business ********** ");
            tckBusiness.saveMaryPublisherBusiness(authInfoMary);
            DumpAllBusinesses();
            //waiting up to 10 seconds for the listener to notice the change.
            String test = "";
            for (int i = 0; i < 20; i++) {
                Thread.sleep(500);
                System.out.print(".");
                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                    //logger.info("Received Notification");
                    //break;
                } else {
                    System.out.print(test);
                }
            }
            logger.info("RX " + UDDISubscriptionListenerImpl.notificationCount + " notifications");
            Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
            while (it.hasNext())
            {
                logger.info("Notification: " + it.next());
            }
            DumpAllBusinesses();
            if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                Assert.fail("No Notification was sent");
            }
            if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("uddi:uddi.marypublisher.com:marybusinessone")) {
                Assert.fail("Notification does not contain the correct service");
            }

        } catch (Exception e) {
            logger.error("No exceptions please.");
            e.printStackTrace();

            Assert.fail();
        } finally {
            tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
            tckBusinessService.deleteJoePublisherService(authInfoJoe);
            tckTModel.deleteJoePublisherTmodel(authInfoJoe);
            tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
        }
    }

    private static void DumpAllBusinesses() {
        logger.warn("Dumping the business/service list for debugging");
        FindService fs = new FindService();
        fs.setFindQualifiers(new FindQualifiers());
        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        fs.getName().add(new Name("%", null));
        try {
            ServiceList findService = inquiry.findService(fs);
            if (findService.getServiceInfos() == null) {
                logger.warn("NO SERVICES RETURNED!");
            } else {
                for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                    logger.warn(findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getValue() + " lang="
                            + findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getLang() + " "
                            + findService.getServiceInfos().getServiceInfo().get(i).getServiceKey() + " "
                            + findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //tmodel tests
    //@Test
    public void joePublisherUpdateBusiness_HTTP_FIND_TMODEL() {
        logger.info("joePublisherUpdateBusiness_HTTP_FIND_TMODEL");
        removeAllExistingSubscriptions(authInfoJoe);
        try {
            UDDISubscriptionListenerImpl.notifcationMap.clear();
            UDDISubscriptionListenerImpl.notificationCount = 0;
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckTModel.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            //Saving the Listener Service
            tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort);
            //Saving the Subscription
            tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
            //Changing the service we subscribed to "JoePublisherService"
            Thread.sleep(1000);
            logger.info("Deleting tModel ********** ");
            tckTModel.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);

            //waiting up to 100 seconds for the listener to notice the change.
            String test = "";
            for (int i = 0; i < 200; i++) {
                Thread.sleep(500);
                System.out.print(".");
                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                    logger.info("Received Notification");
                    break;
                } else {
                    System.out.print(test);
                }
            }
            if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                Assert.fail("No Notification was sent");
            }
            if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">tModel One</name>")) {
                Assert.fail("Notification does not contain the correct service");
            }

        } catch (Exception e) {
            logger.error("No exceptions please.");
            e.printStackTrace();

            Assert.fail();
        } finally {
            tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
            tckBusinessService.deleteJoePublisherService(authInfoJoe);
            tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
            tckTModel.deleteJoePublisherTmodel(authInfoJoe);
            tckTModel.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
        }
    }

    //TODO If a subscriber specifies a maximum number of entries to be returned with a subscription and the amount of data to be returned exceeds this limit, or if the node determines based on its policy that there are too many entries to be returned in a single group, then the node SHOULD provide a chunkToken with results.  
    
    
    //TODO  If no more results are pending, the value of the chunkToken MUST be "0".
}
