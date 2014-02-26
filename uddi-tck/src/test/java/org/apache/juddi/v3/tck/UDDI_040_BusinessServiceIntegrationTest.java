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

import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_040_BusinessServiceIntegrationTest {

        protected static Log logger = LogFactory.getLog(UDDI_040_BusinessServiceIntegrationTest.class);
        protected static TckTModel tckTModelJoe = null;
        protected static TckBusiness tckBusinessJoe = null;
        protected static TckBusinessService tckBusinessServiceJoe = null;
        protected static TckTModel tckTModelSam = null;
        protected static TckBusiness tckBusinessSam = null;
        protected static TckBusinessService tckBusinessServiceSam = null;
        protected static UDDIInquiryPortType inquiryJoe=null;
        protected static UDDIInquiryPortType inquirySam=null;
        protected static String authInfoJoe = null;
        protected static String authInfoSam = null;
        private static UDDIClient manager;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
             if (!TckPublisher.isEnabled()) return;
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                tckTModelSam.deleteCreatedTModels(authInfoSam);
                manager.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
             if (!TckPublisher.isEnabled()) return;
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth tokens..");
                try {
                        Transport transport = manager.getTransport("uddiv3");
                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        //Assert.assertNotNull(authInfoJoe);
                        //Assert.assertNotNull(authInfoSam);


                        UDDIPublicationPortType publication = transport.getUDDIPublishService();
                        UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }
                        tckTModelJoe = new TckTModel(publication, inquiry);
                        tckBusinessJoe = new TckBusiness(publication, inquiry);
                        tckBusinessServiceJoe = new TckBusinessService(publication, inquiry);
                        inquiryJoe = inquiry;

                        transport = manager.getTransport("uddiv3");
                        publication = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        }
                        tckTModelSam = new TckTModel(publication, inquiry);
                        tckBusinessSam = new TckBusiness(publication, inquiry);
                        tckBusinessServiceSam = new TckBusinessService(publication, inquiry);
                        inquirySam = inquiry;
                        
                        transport = manager.getTransport("uddiv3");
                        publication = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();
                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        }
                        
                        TckTModel tckTModel = new TckTModel(publication, inquiry);
                        tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @Test
        public void joepublisher() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                } finally {
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void samsyndicator() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {

                        tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusinessServiceSam.saveSamSyndicatorService(authInfoSam);
                        tckBusinessServiceSam.deleteSamSyndicatorService(authInfoSam);
                } finally {
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }

        /**
         * 5.2.16.3 paragraph 4 Data contained within businessEntity structures
         * can be rearranged with this API call. This can be done by redefining
         * parent container relationships for other registered information. For
         * instance, if a new businessEntity is saved with information about a
         * businessService that is registered already as part of a different
         * businessEntity, this results in the businessService being moved from
         * its current container to the new businessEntity. This condition
         * occurs when the businessKey of the businessService being saved
         * matches the businessKey of the businessEntity being saved. An attempt
         * to delete or move a businessService in this manner by a party who is
         * not the publisher of the businessService MUST be rejected with an
         * error E_userMismatch.
         */
        @Test
        public void joepublisherMoveBusinessService() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        tckBusinessJoe.checkServicesBusinessOne(1);
                        tckBusinessJoe.saveJoePublisherBusiness3(authInfoJoe);
                        //check that this business has no services
                        tckBusinessJoe.checkServicesBusinessThree(0);
                        //Now move the service from one to three
                        tckBusinessJoe.saveJoePublisherBusiness1to3(authInfoJoe);
                        tckBusinessJoe.checkServicesBusinessOne(0);
                        tckBusinessJoe.checkServicesBusinessThree(1);
                } catch (Exception e) {
                        logger.error(e);
                        Assert.fail(e.getMessage());
                } finally {
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness3(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }
}
