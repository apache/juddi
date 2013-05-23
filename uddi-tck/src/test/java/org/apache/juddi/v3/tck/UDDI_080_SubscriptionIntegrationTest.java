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
import javax.xml.ws.Holder;
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
import org.uddi.api_v3.SaveBusiness;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_080_SubscriptionIntegrationTest {

    private static Log logger = LogFactory.getLog(UDDI_080_SubscriptionIntegrationTest.class);
    private static TckTModel tckTModel = null;
    private static TckBusiness tckBusiness = null;
    private static TckBusinessService tckBusinessService = null;
    private static TckBindingTemplate tckBindingTemplate = null;
    private static TckSubscription tckSubscription = null;
    private static String authInfoJoe = null;
    private static String authInfoSam = null;
    private static UDDIClient manager;
    static UDDIPublicationPortType publication = null;

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
            Assert.assertNotNull(authInfoJoe);
            UDDISubscriptionPortType subscription = transport.getUDDISubscriptionService();

            publication = transport.getUDDIPublishService();
            UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
            tckTModel = new TckTModel(publication, inquiry);
            tckBusiness = new TckBusiness(publication, inquiry);
            tckBusinessService = new TckBusinessService(publication, inquiry);
            tckBindingTemplate = new TckBindingTemplate(publication, inquiry);

            tckSubscription = new TckSubscription(subscription, security);
            String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
            tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
            tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
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
        try {
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
            tckBusinessService.saveJoePublisherService(authInfoJoe);
            tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
            tckSubscription.saveJoePublisherSubscription(authInfoJoe);
            tckSubscription.getJoePublisherSubscriptionResults(authInfoJoe);
        } finally {
            tckSubscription.deleteJoePublisherSubscription(authInfoJoe);
            tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
            tckBusinessService.deleteJoePublisherService(authInfoJoe);
            tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
            tckTModel.deleteJoePublisherTmodel(authInfoJoe);
        }
    }

    @Test
    public void samSyndicator() {
        try {
            tckTModel.saveSamSyndicatorTmodel(authInfoSam);
            tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
            tckBusinessService.saveSamSyndicatorService(authInfoSam);
            tckSubscription.saveSamSyndicatorSubscription(authInfoSam);
            tckSubscription.getSamSyndicatorSubscriptionResults(authInfoSam);
        } finally {
            tckSubscription.deleteSamSyndicatorSubscription(authInfoSam);
            tckBusinessService.deleteSamSyndicatorService(authInfoSam);
            tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
            tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
        }

    }

    @Test
    public void samSyndicatorWithChunkingOnFind() {
        try {
            tckTModel.saveSamSyndicatorTmodel(authInfoSam);
            tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
            tckBusinessService.saveSamSyndicatorService(authInfoSam);
            tckSubscription.saveSamSyndicatorSubscriptionWithChunkingOnFind(authInfoSam);
            tckSubscription.getSamSyndicatorSubscriptionResultsWithChunkingOnFind(authInfoSam);
        } finally {
            tckSubscription.deleteSamSyndicatorSubscriptionWithChunkingOnFind(authInfoSam);
            tckBusinessService.deleteSamSyndicatorService(authInfoSam);
            tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
            tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
        }

    }

    @Test
    public void samSyndicatorWithChunkingOnGet() {
        try {
            tckTModel.saveSamSyndicatorTmodel(authInfoSam);
            tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
            tckBusinessService.saveSamSyndicatorService(authInfoSam);
            tckSubscription.saveSamSyndicatorSubscriptionWithChunkingOnGet(authInfoSam);
            tckSubscription.getSamSyndicatorSubscriptionResultsWithChunkingOnGet(authInfoSam);
        } finally {
            tckSubscription.deleteSamSyndicatorSubscriptionWithChunkingOnGet(authInfoSam);
            tckBusinessService.deleteSamSyndicatorService(authInfoSam);
            tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
            tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
        }

    }

    /**
     * Null expiration time
     */
    @Test
    public void JUDDI_606_1() {
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
            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.assertNotNull(data.value.get(0).getExpiresAfter());
        } catch (Exception ex) {
            HandleException(ex);
            Assert.fail();
        }
    }

    @Test
    public void JUDDI_606_2() throws DatatypeConfigurationException {
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
            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.fail();
        } catch (Exception ex) {
            //HandleException(ex);
        }
    }

    @Test
    public void JUDDI_606_3() {
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
            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.assertNotNull(data.value.get(0).getSubscriptionKey());
            Assert.assertTrue(data.value.get(0).getSubscriptionKey().length() > 0);
        } catch (Exception ex) {
            //HandleException(ex);
        }
    }

    @Test
    public void JUDDI_606_4() {
        //null subscription filter

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
            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.fail();
        } catch (Exception ex) {
            //HandleException(ex);
        }
    }

    @Test
    public void JUDDI_606_5() {
        //empty subscription filter

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
            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.fail();
        } catch (Exception ex) {
            //HandleException(ex);
        }
    }

    @Test
    public void JUDDI_606_6() {
        //negative max entities

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
            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.assertTrue(data.value.get(0).getMaxEntities() > 0);
        } catch (Exception ex) {
            //HandleException(ex);
            //this is ok as well
        }
    }

    @Test
    public void JUDDI_606_7() {
        //minimal settings 
    }

    @Test
    public void JUDDI_606_8() {
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

            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.assertNotNull(data.value.get(0).getExpiresAfter());
            XMLGregorianCalendar xcal = data.value.get(0).getExpiresAfter();

            Thread.sleep(5000);
            data.value.get(0).setExpiresAfter(null);
            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
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

            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
            Assert.fail();
        } catch (Exception ex) {
            HandleException(ex);
            //Assert.fail();
        }
    }

    @Test
    public void JUDDI_606_10() throws DatatypeConfigurationException {
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

            tckSubscription.subscription.saveSubscription(authInfoJoe, data);
        } catch (Exception ex) {
            HandleException(ex);
            Assert.fail();
        }
    }

}
