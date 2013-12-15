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
package org.apache.juddi.v3.tck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_080_SubscriptionIntegrationTest {

        private static Log logger = LogFactory.getLog(UDDI_080_SubscriptionIntegrationTest.class);
        private static TckTModel tckTModelJoe = null;
        private static TckBusiness tckBusinessJoe = null;
        private static TckBusinessService tckBusinessServiceJoe = null;
        private static TckBindingTemplate tckBindingTemplateJoe = null;
        private static TckSubscription tckSubscriptionJoe = null;
        private static TckTModel tckTModelSam = null;
        private static TckBusiness tckBusinessSam = null;
        private static TckBusinessService tckBusinessServiceSam = null;
//        private static TckBindingTemplate tckBindingTemplateSam = null;
        private static TckSubscription tckSubscriptionSam = null;
        private static String authInfoJoe = null;
        private static String authInfoSam = null;
        private static UDDIClient manager;
        static UDDIPublicationPortType publicationJoe = null;
        static UDDIPublicationPortType publicationSam = null;
        static UDDISubscriptionPortType subscriptionJoe=null;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                manager.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth tokens..");
                try {
                        Transport transport = manager.getTransport();
                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());

                        //Assert.assertNotNull(authInfoJoe);
                        UDDISubscriptionPortType subscription = transport.getUDDISubscriptionService();

                        publicationJoe = transport.getUDDIPublishService();
                        UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();

                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) subscription, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                        tckTModelJoe = new TckTModel(publicationJoe, inquiry);
                        tckBusinessJoe = new TckBusiness(publicationJoe, inquiry);
                        tckBusinessServiceJoe = new TckBusinessService(publicationJoe, inquiry);
                        tckBindingTemplateJoe = new TckBindingTemplate(publicationJoe, inquiry);
                        subscriptionJoe = subscription;
                        tckSubscriptionJoe = new TckSubscription(subscription);


                        transport = manager.getTransport();
                        publicationSam = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();
                        subscription = transport.getUDDISubscriptionService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationSam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) subscription, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        }
                        tckTModelSam = new TckTModel(publicationSam, inquiry);
                        tckBusinessSam = new TckBusiness(publicationSam, inquiry);
                        tckBusinessServiceSam = new TckBusinessService(publicationSam, inquiry);
                        //tckBindingTemplateSam = new TckBindingTemplate(publicationSam, inquiry);
                        tckSubscriptionSam = new TckSubscription(subscription);


                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        transport = manager.getTransport();
                        UDDIPublicationPortType publication = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();

                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());

                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        }

                        TckTModel tckTModelUDDI = new TckTModel(publication, inquiry);
                        tckTModelUDDI.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModelUDDI.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }
        static final String TRANS = "The transaction has been rolled back";
        static final String MISSING_RESOURCE = "Can't find resource for bundle";

        static void HandleException(Exception ex) {
                System.err.println("Error caught of type " + ex.getClass().getCanonicalName());
                ex.printStackTrace();
                if (ex.getMessage() != null) {
                        Assert.assertFalse(ex.getMessage().contains(TRANS));
                        Assert.assertFalse(ex.getMessage().contains(MISSING_RESOURCE));
                }
                if (ex instanceof SOAPFault) {
                        SOAPFault sf = (SOAPFault) ex;
                        if (!sf.getTextContent().contains("org.apache.juddi.v3.error.ValueNotAllowedException")) {
                                Assert.fail();
                        }
                }
        }

        @Test
        public void joePublisher() {
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                try {
                        UDDI_090_SubscriptionListenerIntegrationTest.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplateJoe.saveJoePublisherBinding(authInfoJoe);
                        tckSubscriptionJoe.saveJoePublisherSubscription(authInfoJoe);
                        tckSubscriptionJoe.getJoePublisherSubscriptionResults(authInfoJoe);
                } finally {
                        tckSubscriptionJoe.deleteJoePublisherSubscription(authInfoJoe);
                        tckBindingTemplateJoe.deleteJoePublisherBinding(authInfoJoe);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void samSyndicator() {
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                try {
                        tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusinessServiceSam.saveSamSyndicatorService(authInfoSam);
                        tckSubscriptionSam.saveSamSyndicatorSubscription(authInfoSam);
                        tckSubscriptionSam.getSamSyndicatorSubscriptionResults(authInfoSam);
                } finally {
                        tckSubscriptionSam.deleteSamSyndicatorSubscription(authInfoSam);
                        tckBusinessServiceSam.deleteSamSyndicatorService(authInfoSam);
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                }

        }

        @Test
        public void samSyndicatorWithChunkingOnFind() {
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                try {
                        tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusinessServiceSam.saveSamSyndicatorService(authInfoSam);
                        tckSubscriptionSam.saveSamSyndicatorSubscriptionWithChunkingOnFind(authInfoSam);
                        tckSubscriptionSam.getSamSyndicatorSubscriptionResultsWithChunkingOnFind(authInfoSam);
                } finally {
                        tckSubscriptionSam.deleteSamSyndicatorSubscriptionWithChunkingOnFind(authInfoSam);
                        tckBusinessServiceSam.deleteSamSyndicatorService(authInfoSam);
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                }

        }

        @Test
        public void samSyndicatorWithChunkingOnGet() {
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                try {
                        tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusinessServiceSam.saveSamSyndicatorService(authInfoSam);
                        tckSubscriptionSam.saveSamSyndicatorSubscriptionWithChunkingOnGet(authInfoSam);
                        tckSubscriptionSam.getSamSyndicatorSubscriptionResultsWithChunkingOnGet(authInfoSam);
                } finally {
                        tckSubscriptionSam.deleteSamSyndicatorSubscriptionWithChunkingOnGet(authInfoSam);
                        tckBusinessServiceSam.deleteSamSyndicatorService(authInfoSam);
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                }

        }

        /**
         * Null expiration time
         */
        @Test
        public void JUDDI_606_1() {
                System.out.println("JUDDI_606_1");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                // Null expiration time

                Subscription sub = new Subscription();
                Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                data.value = new ArrayList<Subscription>();
                sub.setBrief(true);
                sub.setExpiresAfter(null);
                sub.setMaxEntities(1);
                sub.setNotificationInterval(null);
                sub.setBindingKey(null);
                sub.setSubscriptionFilter(new SubscriptionFilter());
                sub.getSubscriptionFilter().setFindService(new FindService());
                sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                data.value.add(sub);
                try {
                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.assertNotNull(data.value.get(0).getExpiresAfter());
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail();
                }
        }

        @Test
        public void JUDDI_606_2() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_2");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                // invalid expiration time
                DatatypeFactory df = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());
                gcal.add(Calendar.DATE, -1);
                XMLGregorianCalendar newXMLGregorianCalendar = df.newXMLGregorianCalendar(gcal);

                Subscription sub = new Subscription();
                Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                data.value = new ArrayList<Subscription>();
                sub.setBrief(true);
                sub.setExpiresAfter(newXMLGregorianCalendar);
                sub.setMaxEntities(1);
                sub.setNotificationInterval(null);
                sub.setBindingKey(null);
                sub.setSubscriptionFilter(new SubscriptionFilter());
                sub.getSubscriptionFilter().setFindService(new FindService());
                sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                data.value.add(sub);
                try {
                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.fail();
                } catch (Exception ex) {
                        //HandleException(ex);
                }
        }

        @Test
        public void JUDDI_606_3() {
                System.out.println("JUDDI_606_3");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //confirm a subscription key is returned when not specified

                Subscription sub = new Subscription();
                Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                data.value = new ArrayList<Subscription>();
                sub.setBrief(true);
                sub.setExpiresAfter(null);
                sub.setMaxEntities(1);
                sub.setNotificationInterval(null);
                sub.setBindingKey(null);
                sub.setSubscriptionFilter(new SubscriptionFilter());
                sub.getSubscriptionFilter().setFindService(new FindService());
                sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                data.value.add(sub);
                try {
                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.assertNotNull(data.value.get(0).getSubscriptionKey());
                        Assert.assertTrue(data.value.get(0).getSubscriptionKey().length() > 0);
                } catch (Exception ex) {
                        //HandleException(ex);
                }
        }

        @Test
        public void JUDDI_606_4() {
                System.out.println("JUDDI_606_4");
                //null subscription filter
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());

                Subscription sub = new Subscription();
                Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                data.value = new ArrayList<Subscription>();
                sub.setBrief(true);
                sub.setExpiresAfter(null);
                sub.setMaxEntities(1);
                sub.setNotificationInterval(null);
                sub.setBindingKey(null);
                sub.setSubscriptionFilter(null);

                try {
                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.fail();
                } catch (Exception ex) {
                        //HandleException(ex);
                }
        }

        @Test
        public void JUDDI_606_5() {
                System.out.println("JUDDI_606_5");
                //empty subscription filter
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());

                Subscription sub = new Subscription();
                Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                data.value = new ArrayList<Subscription>();
                sub.setBrief(true);
                sub.setExpiresAfter(null);
                sub.setMaxEntities(1);
                sub.setNotificationInterval(null);
                sub.setBindingKey(null);
                sub.setSubscriptionFilter(new SubscriptionFilter());

                data.value.add(sub);
                try {
                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.fail();
                } catch (Exception ex) {
                        //HandleException(ex);
                }
        }

        @Test
        public void JUDDI_606_6() {
                System.out.println("JUDDI_606_6");
                //negative max entities
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());

                Subscription sub = new Subscription();
                Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                data.value = new ArrayList<Subscription>();
                sub.setBrief(true);
                sub.setExpiresAfter(null);
                sub.setMaxEntities(-1);
                sub.setNotificationInterval(null);
                sub.setBindingKey(null);
                sub.setSubscriptionFilter(new SubscriptionFilter());
                sub.getSubscriptionFilter().setFindService(new FindService());
                sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                data.value.add(sub);
                try {
                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.assertTrue(data.value.get(0).getMaxEntities() > 0);
                } catch (Exception ex) {
                        //HandleException(ex);
                        //this is ok as well
                }
        }

        @Test
        public void JUDDI_606_7() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_7");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //more filters that expected
                DatatypeFactory df = DatatypeFactory.newInstance();
                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                        sub.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindBusiness().getName().add(new Name("%", null));
                        sub.getSubscriptionFilter().setFindService(new FindService());
                        sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));

                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.fail();
                } catch (Exception ex) {
                        logger.info(ex.getMessage());

                }
        }

        @Test
        public void JUDDI_606_8() {
                System.out.println("JUDDI_606_8");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //reset expiration

                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(true);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindService(new FindService());
                        sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.assertNotNull(data.value.get(0).getExpiresAfter());
                        XMLGregorianCalendar xcal = data.value.get(0).getExpiresAfter();

                        Thread.sleep(5000);
                        data.value.get(0).setExpiresAfter(null);
                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.assertNotNull(data.value.get(0).getExpiresAfter());
                        Assert.assertNotSame(xcal, data.value.get(0).getExpiresAfter());
                        long initial = xcal.toGregorianCalendar().getTimeInMillis();
                        long finaltime = data.value.get(0).getExpiresAfter().toGregorianCalendar().getTimeInMillis();
                        Assert.assertTrue(finaltime > initial);

                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail();
                }
        }

        @Test
        public void JUDDI_606_9() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_9");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //asynch subscriptions , key doesn't exist

                DatatypeFactory df = DatatypeFactory.newInstance();

                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(true);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(df.newDuration(5000));
                        sub.setBindingKey("uddi:uddi.joepublisher.com:mykey");
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindService(new FindService());
                        sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.fail();
                } catch (Exception ex) {
//            HandleException(ex);
                        //Assert.fail();
                }
        }

        @Test
        public void JUDDI_606_10() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_10");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //asynch subscriptions, key exists , null interval

                DatatypeFactory df = DatatypeFactory.newInstance();

                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(true);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(TckBindingTemplate.JOE_BINDING_KEY);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindService(new FindService());
                        sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        Assert.fail();
                } catch (Exception ex) {
                        //HandleException(ex);
                }
        }

        @Test
        public void JUDDI_606_11() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_11");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //set subscription, make a change as the same user, get subscription results
                //no key specified
                DatatypeFactory df = DatatypeFactory.newInstance();
                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                        sub.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindBusiness().getName().add(new Name("%", null));
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);
                        BusinessEntity be = new BusinessEntity();
                        be.getName().add(new Name("Test business", null));
                        sb.getBusinessEntity().add(be);
                        publicationJoe.saveBusiness(sb);

                        GetSubscriptionResults gsr = new GetSubscriptionResults();
                        gsr.setAuthInfo(authInfoJoe);
                        gsr.setSubscriptionKey(null);
                        gsr.setCoveragePeriod(new CoveragePeriod());
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gcal.add(Calendar.HOUR, -1);
                        gsr.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                        gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gsr.getCoveragePeriod().setEndPoint(df.newXMLGregorianCalendar(gcal));
                        tckSubscriptionJoe.subscription.getSubscriptionResults(gsr);
                        Assert.fail();
                } catch (Exception ex) {
                        //HandleException(ex);
                        //Assert.fail();
                }
        }

        @Test
        public void JUDDI_606_12() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_12");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //set subscription, make a change as the same user, get subscription results
                //no period specified

                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                        sub.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindBusiness().getName().add(new Name("%", null));
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);
                        BusinessEntity be = new BusinessEntity();
                        be.getName().add(new Name("Test business", null));
                        sb.getBusinessEntity().add(be);
                        publicationJoe.saveBusiness(sb);

                        GetSubscriptionResults gsr = new GetSubscriptionResults();
                        gsr.setAuthInfo(authInfoJoe);
                        gsr.setSubscriptionKey(data.value.get(0).getSubscriptionKey());
                        gsr.setCoveragePeriod(null);
                        tckSubscriptionJoe.subscription.getSubscriptionResults(gsr);
                        Assert.fail();
                } catch (Exception ex) {
                        //HandleException(ex);
                        //Assert.fail();
                }
        }

        @Test
        public void JUDDI_606_13() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_13");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //set subscription, make a change as the same user, get subscription results
                //valid find_Business
                DatatypeFactory df = DatatypeFactory.newInstance();
                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                        sub.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindBusiness().getName().add(new Name("%", null));
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);
                        BusinessEntity be = new BusinessEntity();
                        be.getName().add(new Name("Test business", null));
                        sb.getBusinessEntity().add(be);
                        publicationJoe.saveBusiness(sb);

                        GetSubscriptionResults gsr = new GetSubscriptionResults();
                        gsr.setAuthInfo(authInfoJoe);
                        gsr.setSubscriptionKey(data.value.get(0).getSubscriptionKey());
                        gsr.setCoveragePeriod(new CoveragePeriod());
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gcal.add(Calendar.HOUR, -1);
                        gsr.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                        gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gsr.getCoveragePeriod().setEndPoint(df.newXMLGregorianCalendar(gcal));
                        SubscriptionResultsList subscriptionResults = tckSubscriptionJoe.subscription.getSubscriptionResults(gsr);
                        Assert.assertNotNull(subscriptionResults);
                        Assert.assertNotNull(subscriptionResults.getBusinessList());
                        Assert.assertNotNull(subscriptionResults.getCoveragePeriod());
                        Assert.assertNotNull(subscriptionResults.getBusinessList().getBusinessInfos());
                        Assert.assertNotNull(subscriptionResults.getBusinessList().getBusinessInfos().getBusinessInfo().get(0));
                        Assert.assertNotNull(subscriptionResults.getBusinessList().getBusinessInfos().getBusinessInfo().get(0).getBusinessKey());
                        Assert.assertNotNull(subscriptionResults.getBusinessList().getBusinessInfos().getBusinessInfo().get(0).getName().get(0));

                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail();
                }
        }

        public void JUDDI_606_14() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_14");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //set subscription, make a change as the same user, get subscription results
                //valid find_services
                DatatypeFactory df = DatatypeFactory.newInstance();
                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindService(new FindService());
                        sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindService().getName().add(new Name("%", null));
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);
                        SaveService sb = new SaveService();
                        sb.setAuthInfo(authInfoJoe);
                        BusinessService bs = new BusinessService();
                        bs.getName().add(new Name("svc", null));
                        sb.getBusinessService().add(bs);
                        publicationJoe.saveService(sb);

                        GetSubscriptionResults gsr = new GetSubscriptionResults();
                        gsr.setAuthInfo(authInfoJoe);
                        gsr.setSubscriptionKey(data.value.get(0).getSubscriptionKey());
                        gsr.setCoveragePeriod(new CoveragePeriod());
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gcal.add(Calendar.HOUR, -1);
                        gsr.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                        gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gsr.getCoveragePeriod().setEndPoint(df.newXMLGregorianCalendar(gcal));
                        SubscriptionResultsList subscriptionResults = tckSubscriptionJoe.subscription.getSubscriptionResults(gsr);
                        Assert.assertNotNull(subscriptionResults);
                        Assert.assertNull(subscriptionResults.getBusinessList());
                        Assert.assertNotNull(subscriptionResults.getCoveragePeriod());
                        Assert.assertNotNull(subscriptionResults.getServiceList());
                        Assert.assertNotNull(subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().get(0));
                        Assert.assertNotNull(subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().get(0).getBusinessKey());
                        Assert.assertNotNull(subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().get(0).getName().get(0));

                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail();
                }
        }

        public void JUDDI_606_15() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_15");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //set subscription, make a change as the same user, get subscription results
                //valid publisher assertion, incomplete
                DatatypeFactory df = DatatypeFactory.newInstance();
                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                        sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);

                        AddPublisherAssertions r = new AddPublisherAssertions();
                        r.setAuthInfo(authInfoJoe);
                        PublisherAssertion pa = new PublisherAssertion();
                        pa.setFromKey(TckBusiness.JOE_BUSINESS_KEY);
                        pa.setToKey(TckBusiness.SAM_BUSINESS_KEY);
                        pa.setKeyedReference(new KeyedReference());
                        pa.getKeyedReference().setKeyName("Subsidiary");
                        pa.getKeyedReference().setKeyValue("parent-child");

                        pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");

                        r.getPublisherAssertion().add(pa);
                        publicationJoe.addPublisherAssertions(r);

                        GetSubscriptionResults gsr = new GetSubscriptionResults();
                        gsr.setAuthInfo(authInfoJoe);
                        gsr.setSubscriptionKey(data.value.get(0).getSubscriptionKey());
                        gsr.setCoveragePeriod(new CoveragePeriod());
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gcal.add(Calendar.HOUR, -1);
                        gsr.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                        gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gsr.getCoveragePeriod().setEndPoint(df.newXMLGregorianCalendar(gcal));
                        SubscriptionResultsList subscriptionResults = tckSubscriptionJoe.subscription.getSubscriptionResults(gsr);
                        Assert.assertNotNull(subscriptionResults);
                        Assert.assertNull(subscriptionResults.getBusinessList());
                        Assert.assertNotNull(subscriptionResults.getCoveragePeriod());
                        Assert.assertNull(subscriptionResults.getServiceList());
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport());
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem());
                        Assert.assertFalse(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().isEmpty());
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getFromKey(), TckBusiness.JOE_BUSINESS_KEY);
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getToKey(), TckBusiness.SAM_BUSINESS_KEY);
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getCompletionStatus());
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getCompletionStatus(), CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getKeyedReference());
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail();
                }
        }

        public void JUDDI_606_16() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_16");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //set subscription, make a change as the same user, get subscription results
                //valid publisher assertion, complete
                DatatypeFactory df = DatatypeFactory.newInstance();
                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                        sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.STATUS_COMPLETE);
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);

                        AddPublisherAssertions r = new AddPublisherAssertions();
                        r.setAuthInfo(authInfoJoe);
                        PublisherAssertion pa = new PublisherAssertion();
                        pa.setFromKey(TckBusiness.JOE_BUSINESS_KEY);
                        pa.setToKey(TckBusiness.SAM_BUSINESS_KEY);
                        pa.setKeyedReference(new KeyedReference());
                        pa.getKeyedReference().setKeyName("Subsidiary");
                        pa.getKeyedReference().setKeyValue("parent-child");
                        pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                        r.getPublisherAssertion().add(pa);
                        publicationJoe.addPublisherAssertions(r);


                        r = new AddPublisherAssertions();
                        r.setAuthInfo(authInfoSam);
                        pa = new PublisherAssertion();
                        pa.setFromKey(TckBusiness.JOE_BUSINESS_KEY);
                        pa.setToKey(TckBusiness.SAM_BUSINESS_KEY);
                        pa.setKeyedReference(new KeyedReference());
                        pa.getKeyedReference().setKeyName("Subsidiary");
                        pa.getKeyedReference().setKeyValue("parent-child");
                        pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                        r.getPublisherAssertion().add(pa);
                        publicationJoe.addPublisherAssertions(r);



                        GetSubscriptionResults gsr = new GetSubscriptionResults();
                        gsr.setAuthInfo(authInfoJoe);
                        gsr.setSubscriptionKey(data.value.get(0).getSubscriptionKey());
                        gsr.setCoveragePeriod(new CoveragePeriod());
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gcal.add(Calendar.HOUR, -1);
                        gsr.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                        gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gsr.getCoveragePeriod().setEndPoint(df.newXMLGregorianCalendar(gcal));
                        SubscriptionResultsList subscriptionResults = tckSubscriptionJoe.subscription.getSubscriptionResults(gsr);
                        Assert.assertNotNull(subscriptionResults);
                        Assert.assertNull(subscriptionResults.getBusinessList());
                        Assert.assertNotNull(subscriptionResults.getCoveragePeriod());
                        Assert.assertNull(subscriptionResults.getServiceList());
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport());
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem());
                        Assert.assertFalse(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().isEmpty());
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getFromKey(), TckBusiness.JOE_BUSINESS_KEY);
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getToKey(), TckBusiness.SAM_BUSINESS_KEY);
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getCompletionStatus());
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getCompletionStatus(), CompletionStatus.STATUS_COMPLETE);
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getKeyedReference());
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail();
                }
        }

        public void JUDDI_606_17() throws DatatypeConfigurationException {
                System.out.println("JUDDI_606_17");
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                //set subscription, make a change as the same user, get subscription results
                //valid publisher assertion, deleted
                DatatypeFactory df = DatatypeFactory.newInstance();
                try {

                        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                        data.value = new ArrayList<Subscription>();
                        Subscription sub = new Subscription();
                        sub.setBrief(false);
                        sub.setExpiresAfter(null);
                        sub.setMaxEntities(null);
                        sub.setNotificationInterval(null);
                        sub.setBindingKey(null);
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setGetAssertionStatusReport(new GetAssertionStatusReport());
                        sub.getSubscriptionFilter().getGetAssertionStatusReport().setCompletionStatus(CompletionStatus.STATUS_BOTH_INCOMPLETE);
                        data.value.add(sub);

                        tckSubscriptionJoe.subscription.saveSubscription(authInfoJoe, data);

                        AddPublisherAssertions r = new AddPublisherAssertions();
                        r.setAuthInfo(authInfoJoe);
                        PublisherAssertion pa = new PublisherAssertion();
                        pa.setFromKey(TckBusiness.JOE_BUSINESS_KEY);
                        pa.setToKey(TckBusiness.SAM_BUSINESS_KEY);
                        pa.setKeyedReference(new KeyedReference());
                        pa.getKeyedReference().setKeyName("Subsidiary");
                        pa.getKeyedReference().setKeyValue("parent-child");
                        pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                        r.getPublisherAssertion().add(pa);
                        publicationJoe.addPublisherAssertions(r);

                        //approve it
                        r = new AddPublisherAssertions();
                        r.setAuthInfo(authInfoSam);
                        r.getPublisherAssertion().add(pa);
                        publicationSam.addPublisherAssertions(r);

                        DeletePublisherAssertions dp = new DeletePublisherAssertions();
                        dp.setAuthInfo(authInfoJoe);
                        dp.getPublisherAssertion().add(pa);
                        publicationJoe.deletePublisherAssertions(dp);

                        dp = new DeletePublisherAssertions();
                        dp.setAuthInfo(authInfoSam);
                        dp.getPublisherAssertion().add(pa);
                        publicationSam.deletePublisherAssertions(dp);


                        GetSubscriptionResults gsr = new GetSubscriptionResults();
                        gsr.setAuthInfo(authInfoJoe);
                        gsr.setSubscriptionKey(data.value.get(0).getSubscriptionKey());
                        gsr.setCoveragePeriod(new CoveragePeriod());
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gcal.add(Calendar.HOUR, -1);
                        gsr.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                        gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        gsr.getCoveragePeriod().setEndPoint(df.newXMLGregorianCalendar(gcal));
                        SubscriptionResultsList subscriptionResults = tckSubscriptionJoe.subscription.getSubscriptionResults(gsr);
                        Assert.assertNotNull(subscriptionResults);
                        Assert.assertNull(subscriptionResults.getBusinessList());
                        Assert.assertNotNull(subscriptionResults.getCoveragePeriod());
                        Assert.assertNull(subscriptionResults.getServiceList());
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport());
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem());
                        Assert.assertFalse(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().isEmpty());
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getFromKey(), TckBusiness.JOE_BUSINESS_KEY);
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getToKey(), TckBusiness.SAM_BUSINESS_KEY);
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getCompletionStatus());
                        Assert.assertEquals(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getCompletionStatus(), CompletionStatus.STATUS_BOTH_INCOMPLETE);
                        Assert.assertNotNull(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(0).getKeyedReference());
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail();
                }
        }
}
