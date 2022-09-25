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
package org.apache.juddi.subscription;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.API_010_PublisherTest;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.model.Subscription;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.tck.TckBindingTemplate;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckSubscription;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class SubscriptionNotifierTest {

    private static Log logger = LogFactory.getLog(SubscriptionNotifierTest.class);
    private static API_010_PublisherTest api010 = new API_010_PublisherTest();
    private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckBindingTemplate tckBindingTemplate = new TckBindingTemplate(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckSubscription tckSubscription = new TckSubscription(new UDDISubscriptionImpl(), new UDDISecurityImpl(), new UDDIInquiryImpl());
    private static String authInfoJoe = null;

    @BeforeClass
    public static void setup() {
        logger.info("SubscriptionNotifierTest setup");
        logger.debug("Getting auth token..");
        try {
            DumpAllBusinesses();
            //calls uddi-tck-base//rc/main/resources/uddi_data/joepublisher
            //setups up a JUDDI user account

            api010.saveJoePublisher();

            //login as joe
            authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
            //makes tmodel uddi:uddi.joepublisher.com:keygenerator
            tckTModel.saveJoePublisherTmodel(authInfoJoe);

            //saves a digitally signed business, no services
            //key = uddi:uddi.joepublisher.com:businessone
            //sournce = uddi_data/joepublisher/businessEntity.xml
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            
            //saves a service and binding template
            //service key = uddi:uddi.joepublisher.com:serviceone
            //source "uddi_data/joepublisher/businessService.xml"
            //bt = uddi:uddi.joepublisher.com:bindingone
            String bindingKey = tckBusinessService.saveJoePublisherService(authInfoJoe);

            if (bindingKey==null || bindingKey.length()==0)
                logger.fatal("Service binding key is null!");

            //makes subscription key=uddi:uddi.joepublisher.com:subscriptionone
            //find exact match on "Service One"
            //callback binding key=should be set to bindingKey
            tckSubscription.saveJoePublisherSubscription(authInfoJoe, bindingKey);

            DumpAllBusinesses();



            //tckSubscription.getJoePublisherSubscriptionResults(authInfoJoe);
        } catch (RemoteException e) {
            logger.error(e.getMessage(), e);
            Assert.fail("Could not obtain authInfo token.");
        }
    }

    private static void DumpAllBusinesses() {
        UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
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
                    logger.warn(findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getValue() + " "
                            + findService.getServiceInfos().getServiceInfo().get(i).getServiceKey() + " "
                            + findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetSubscriptionResults()
            throws ConfigurationException, MalformedURLException, DispositionReportFaultMessage, DatatypeConfigurationException {
        SubscriptionNotifier notifier = new SubscriptionNotifier();
        notifier.cancel();
        Collection<Subscription> subscriptions = notifier.getAllAsyncSubscriptions();

        Assert.assertEquals(1, subscriptions.size());
        Subscription subscription = subscriptions.iterator().next();
        Assert.assertNotNull(subscription);
        GetSubscriptionResults getSubscriptionResults = notifier.buildGetSubscriptionResults(subscription, new Date(new Date().getTime() + 60000l));
        if (getSubscriptionResults!=null)
        {
            getSubscriptionResults.setSubscriptionKey(subscription.getSubscriptionKey());
            UddiEntityPublisher publisher = new UddiEntityPublisher();
            publisher.setAuthorizedName(subscription.getAuthorizedName());
            SubscriptionResultsList resultList = notifier.getSubscriptionImpl().getSubscriptionResults(getSubscriptionResults, publisher);
            logger.info("Expecting the resultList to be null: " + resultList.getServiceList());
            Assert.assertNull(resultList.getServiceList());
            tckBusinessService.updateJoePublisherService(authInfoJoe, "updated description");
            resultList = notifier.getSubscriptionImpl().getSubscriptionResults(getSubscriptionResults, publisher);
            //We're expecting a changed service
            logger.info("Expecting the resultList to have 1 service: " + resultList.getServiceList());
            Assert.assertNotNull(resultList.getServiceList());
            //We should detect these changes.
            boolean hasChanges = notifier.resultListContainsChanges(resultList);
            Assert.assertTrue(hasChanges);
            System.out.print(resultList);
            notifier.notify(getSubscriptionResults, resultList, new Date());
        }
        else{
            logger.error("testGetSubscriptionResults, getSubscriptionResults unexpectedly null");
            Assume.assumeTrue(getSubscriptionResults==null);
        }
    }

    @AfterClass
    public static void teardown() {
        logger.info("Calling teardown");
        //if (logger.isDebugEnabled()) 
        {
            DumpAllBusinesses();
        }
        tckSubscription.deleteJoePublisherSubscription(authInfoJoe);
        //tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
        tckBusinessService.deleteJoePublisherService(authInfoJoe);
        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
    }
}
