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
package org.apache.juddi.api.impl;

import static junit.framework.Assert.assertEquals;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.Holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.jaxb.EntityCreator;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.UserMismatchException;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckSubscription;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.TModel;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This test is jUDDI specific, as the publisher methods are an extension to the
 * UDDI api.
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class API_010_PublisherTest {

        private static Log logger = LogFactory.getLog(API_010_PublisherTest.class);
        private JUDDIApiImpl publisher = new JUDDIApiImpl();
        private UDDISecurityPortType security = new UDDISecurityImpl();
        private static TckSubscription tckSubscription = new TckSubscription(new UDDISubscriptionImpl(), new UDDISecurityImpl(), new UDDIInquiryImpl());

        @BeforeClass
        public static void startRegistry() throws ConfigurationException {
                Registry.start();
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                Registry.stop();
        }

        @Test
        public void testJoePublisher() {
                //We can only test this if the publisher is not there already.
                //If it already there is probably has foreign key relationships.
                //This test should really only run on an empty database. Seed
                //data will be added if the root publisher is missing.
                if (!isExistPublisher(TckPublisher.getJoePublisherId())) {
                        saveJoePublisher();
                        deleteJoePublisher();
                }
        }

        @Test
        public void testSamSyndicator() {
                //We can only test this if the publisher is not there already.
                if (!isExistPublisher(TckPublisher.getSamPublisherId())) {
                        saveSamSyndicator();
                        deleteSamSyndicator();
                }
        }

        /**
         * Persists Joe Publisher to the database.
         *
         * @return - true if the published did not exist already, - false in all
         * other cases.
         */
        public boolean saveJoePublisher() {
                if (!isExistPublisher(TckPublisher.getJoePublisherId())) {
                        savePublisher(TckPublisher.getJoePublisherId(), TckPublisher.JOE_PUBLISHER_XML);
                        return true;
                } else {
                        return false;
                }
        }

        /**
         * Persists Mary Publisher to the database.
         *
         * @return - true if the published did not exist already, - false in all
         * other cases.
         */
        public boolean saveMaryPublisher() {
                if (!isExistPublisher(TckPublisher.getMaryPublisherId())) {
                        savePublisher(TckPublisher.getMaryPublisherId(), TckPublisher.MARY_PUBLISHER_XML);
                        return true;
                } else {
                        return false;
                }
        }

        /**
         * Removes Joe Publisher from the database, this will fail if there are
         * child objects attached; think Services etc.
         */
        public void deleteJoePublisher() {
                deletePublisher(TckPublisher.getJoePublisherId());
        }

        /**
         * Persists Sam Syndicator to the database.
         *
         * @return publisherId
         */
        public String saveSamSyndicator() {
                if (!isExistPublisher(TckPublisher.getSamPublisherId())) {
                        savePublisher(TckPublisher.getSamPublisherId(), TckPublisher.SAM_SYNDICATOR_XML);
                }
                return TckPublisher.getSamPublisherId();
        }

        /**
         * Removes Sam Syndicator from the database, this will fail if there are
         * child objects attached; think Services etc.
         */
        public void deleteSamSyndicator() {
                deletePublisher(TckPublisher.getSamPublisherId());
        }

        private void savePublisher(String publisherId, String publisherXML) {
                try {
                        String rootPublisherStr = AppConfig.getConfiguration().getString(Property.JUDDI_ROOT_PUBLISHER);
                        logger.info("savePublisher as " + rootPublisherStr);
                        String authInfo = TckSecurity.getAuthToken(security, rootPublisherStr, "");
                        logger.debug("Saving new publisher: " + publisherXML);
                        SavePublisher sp = new SavePublisher();
                        sp.setAuthInfo(authInfo);
                        Publisher pubIn = (Publisher) EntityCreator.buildFromDoc(publisherXML, EntityCreator.JUDDIv3_Package);
                        sp.getPublisher().add(pubIn);
                        publisher.savePublisher(sp);

                        // Now get the entity and check the values
                        GetPublisherDetail gp = new GetPublisherDetail();
                        gp.getPublisherId().add(publisherId);
                        gp.setAuthInfo(authInfo);
                        PublisherDetail pd = publisher.getPublisherDetail(gp);
                        List<Publisher> pubOutList = pd.getPublisher();
                        Publisher pubOut = pubOutList.get(0);

                        assertEquals(pubIn.getAuthorizedName(), pubOut.getAuthorizedName());
                        assertEquals(pubIn.getPublisherName(), pubOut.getPublisherName());
                        assertEquals(pubIn.getEmailAddress(), pubOut.getEmailAddress());
                        assertEquals(pubIn.isIsAdmin(), pubOut.isIsAdmin());
                        assertEquals(pubIn.isIsEnabled(), pubOut.isIsEnabled());
                        assertEquals(pubIn.getMaxBindingsPerService(), pubOut.getMaxBindingsPerService());
                        assertEquals(pubIn.getMaxBusinesses(), pubOut.getMaxBusinesses());
                        assertEquals(pubIn.getMaxServicePerBusiness(), pubOut.getMaxServicePerBusiness());
                        assertEquals(pubIn.getMaxTModels(), pubOut.getMaxTModels());

                        logger.debug("Querying for publisher: " + publisherXML);
                        //Querying for this publisher to make sure it's really gone
                        //We're expecting a invalid Key exception at this point.
                        PublisherDetail pdBeforeDelete = null;
                        try {
                                pdBeforeDelete = publisher.getPublisherDetail(gp);
                                Assert.assertNotNull(pdBeforeDelete);
                        } catch (InvalidKeyPassedException e) {
                                Assert.fail("We expected to find publisher " + publisherXML);
                        }

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
        }

        private void deletePublisher(String publisherId) {
                try {
                        String rootPublisherStr = AppConfig.getConfiguration().getString(Property.JUDDI_ROOT_PUBLISHER);
                        String authInfo = TckSecurity.getAuthToken(security, rootPublisherStr, "");
                        logger.debug("Delete publisher: " + publisherId);
                        //Now deleting this publisher
                        DeletePublisher dp = new DeletePublisher();
                        dp.setAuthInfo(authInfo);
                        dp.getPublisherId().add(publisherId);
                        publisher.deletePublisher(dp);

                        logger.info("Querying for publisher: " + publisherId + " after deletion.");
                        //Querying for this publisher to make sure it's really gone
                        //We're expecting a invalid Key exception at this point.
                        GetPublisherDetail gp = new GetPublisherDetail();
                        gp.getPublisherId().add(publisherId);
                        gp.setAuthInfo(authInfo);
                        PublisherDetail pdAfterDelete = null;
                        try {
                                pdAfterDelete = publisher.getPublisherDetail(gp);
                                Assert.fail("We did not expect to find this publisher anymore.");
                        } catch (InvalidKeyPassedException e) {
                                Assert.assertNull(pdAfterDelete);
                        }

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
        }

        private boolean isExistPublisher(String publisherId) {
                GetPublisherDetail gp = new GetPublisherDetail();
                gp.getPublisherId().add(publisherId);
                try {
                        publisher.getPublisherDetail(gp);
                        return true;
                } catch (Exception e) {
                        return false;
                }
        }

        protected String authInfoJoe() throws RemoteException, DispositionReportFaultMessage {
                return TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
        }

        protected String authInfoSam() throws RemoteException, DispositionReportFaultMessage {
                return TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
        }

        /**
         * Joe saves a subscription on Sam's behalf
         *
         * @throws Exception
         */
        @Test
        public void testAdminSaveSubscriptionAuthorized() throws Exception {
                saveJoePublisher();
                saveSamSyndicator();
                DatatypeFactory fac = DatatypeFactory.newInstance();
                List<Subscription> subs = new ArrayList<Subscription>();
                Subscription s = new Subscription();

                s.setMaxEntities(10);
                s.setBrief(false);
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());
                gcal.add(Calendar.HOUR, 1);
                s.setExpiresAfter(fac.newXMLGregorianCalendar(gcal));
                s.setSubscriptionFilter(new SubscriptionFilter());
                s.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                s.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                s.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                s.getSubscriptionFilter().getFindBusiness().getName().add(new Name(UDDIConstants.WILDCARD, null));
                subs.add(s);
                Holder<List<Subscription>> items = new Holder<List<Subscription>>();
                items.value = subs;
                publisher.adminSaveSubscription(authInfoJoe(), TckPublisher.getSamPublisherId(), items);
                for (int i = 0; i < items.value.size(); i++) {
                        tckSubscription.deleteSubscription(authInfoSam(), items.value.get(i).getSubscriptionKey());
                }

                deleteJoePublisher();
                deleteSamSyndicator();

        }

        /**
         * Sam saves a subscription on Sam's behalf (not authorized
         *
         * @throws Exception
         */
        @Test(expected = UserMismatchException.class)
        public void testAdminSaveSubscriptionNotAuthorized() throws Exception {
                saveJoePublisher();
                saveSamSyndicator();
                DatatypeFactory fac = DatatypeFactory.newInstance();
                List<Subscription> subs = new ArrayList<Subscription>();
                Subscription s = new Subscription();

                s.setMaxEntities(10);
                s.setBrief(false);
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());
                gcal.add(Calendar.HOUR, 1);
                s.setExpiresAfter(fac.newXMLGregorianCalendar(gcal));
                s.setSubscriptionFilter(new SubscriptionFilter());
                s.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                s.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                s.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                s.getSubscriptionFilter().getFindBusiness().getName().add(new Name(UDDIConstants.WILDCARD, null));
                subs.add(s);
                Holder<List<Subscription>> items = new Holder<List<Subscription>>();
                items.value = subs;
                publisher.adminSaveSubscription(authInfoSam(), TckPublisher.getJoePublisherId(), items);
                deleteJoePublisher();
                deleteSamSyndicator();

        }

        @Test
        public void testAdminSaveTModel() throws Exception {
                saveJoePublisher();
                saveSamSyndicator();
                List<org.apache.juddi.api_v3.AdminSaveTModelWrapper> values = new ArrayList<org.apache.juddi.api_v3.AdminSaveTModelWrapper>();
                org.apache.juddi.api_v3.AdminSaveTModelWrapper x = new org.apache.juddi.api_v3.AdminSaveTModelWrapper();
                x.setPublisherID(TckPublisher.getSamPublisherId());
                TModel tm = new TModel();
                tm.setName(new Name("testAdminSaveTModel joe on sam's behalf", null));
                
                x.getTModel().add(tm);
                publisher.adminSaveTModel(authInfoJoe(), values);
                deleteJoePublisher();
                deleteSamSyndicator();
        }
        
}
