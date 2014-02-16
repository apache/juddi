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
package org.apache.juddi.v2.tck;

/**
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.JAXWSv2TranslationTransport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class UDDI_060_PublisherAssertionIntegrationTest {

        private static Log logger = LogFactory.getLog(UDDI_060_PublisherAssertionIntegrationTest.class);
        private static TckTModel tckTModelJoe = null;
        private static TckBusiness tckBusinessJoe = null;
        private static TckPublisherAssertion tckAssertionJoe = null;
        private static TckFindEntity tckFindEntityJoe = null;
        private static TckTModel tckTModelSam = null;
        private static TckBusiness tckBusinessSam = null;
        private static TckPublisherAssertion tckAssertionSam = null;
        private static TckFindEntity tckFindEntitySam = null;
        private static TckTModel tckTModelMary = null;
        private static TckBusiness tckBusinessMary = null;
        private static TckPublisherAssertion tckAssertionMary = null;
        private static TckFindEntity tckFindEntityMary = null;
        private static String authInfoJoe = null;
        private static String authInfoSam = null;
        private static String authInfoMary = null;
        private static UDDIClient manager;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                tckTModelSam.deleteCreatedTModels(authInfoSam);
                tckTModelMary.deleteCreatedTModels(authInfoMary);
                manager.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth tokens..");
                try {
                        JAXWSv2TranslationTransport transport = (JAXWSv2TranslationTransport) manager.getTransport("uddiv2");
                        org.uddi.v2_service.Publish security = transport.getUDDIv2PublishService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        
                        org.uddi.v2_service.Publish publication = transport.getUDDIv2PublishService();
                        org.uddi.v2_service.Inquire inquiry = transport.getUDDIv2InquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }
                        tckTModelJoe = new TckTModel(publication, inquiry);
                        tckBusinessJoe = new TckBusiness(publication, inquiry);
                        tckAssertionJoe = new TckPublisherAssertion(publication);
                        tckFindEntityJoe = new TckFindEntity(inquiry);

                        transport = (JAXWSv2TranslationTransport) manager.getTransport("uddiv2");
                        publication = transport.getUDDIv2PublishService();
                        inquiry = transport.getUDDIv2InquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        }
                        tckTModelSam = new TckTModel(publication, inquiry);
                        tckBusinessSam = new TckBusiness(publication, inquiry);
                        tckAssertionSam = new TckPublisherAssertion(publication);
                        tckFindEntitySam = new TckFindEntity(inquiry);
                        
                        
                        transport = (JAXWSv2TranslationTransport) manager.getTransport("uddiv2");
                        publication = transport.getUDDIv2PublishService();
                        inquiry = transport.getUDDIv2InquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        }
                        tckTModelMary = new TckTModel(publication, inquiry);
                        tckBusinessMary = new TckBusiness(publication, inquiry);
                        tckAssertionMary = new TckPublisherAssertion(publication);
                        tckFindEntityMary = new TckFindEntity(inquiry);
                        tckTModelJoe.saveTmodels(authInfoJoe);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @Test
        public void testJoepublisherToSamSyndicator() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
                        tckAssertionJoe.saveJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertionJoe.deleteJoePublisherPublisherAssertion(authInfoJoe);
                } finally {
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }

        /**
         * This test should find no publisher assertions because we only save
         * them from the joe publisher side.
         */
        @Test
        public void testFindNoAssertions() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);
                        tckAssertionJoe.saveJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertionJoe.saveJoePublisherPublisherAssertion2(authInfoJoe);

                        tckFindEntityJoe.findRelatedBusiness_sortByName(true);
                        //NOT APPLICABLE TO UDDIv2 tckFindEntityJoe.findRelatedBusinessToKey(true);
                        //NOT APPLICABLE TO UDDIv2 tckFindEntityJoe.findRelatedBusinessFromKey(true);

                        tckAssertionJoe.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertionJoe.deleteJoePublisherPublisherAssertion2(authInfoJoe);
                } finally {
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);
                }
        }

        /**
         * This test should find 2 publisher assertions.
         */
        @Test
        public void testFindAssertions() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                        tckTModelMary.saveMaryPublisherTmodel(authInfoMary);
                        
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);
                        tckAssertionJoe.saveJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertionSam.saveJoePublisherPublisherAssertion2(authInfoJoe);
                        tckAssertionSam.saveSamPublisherPublisherAssertion(authInfoSam);
                        tckAssertionMary.saveMaryPublisherPublisherAssertion(authInfoMary);

                        tckFindEntityJoe.findRelatedBusiness_sortByName(false);
                        //these tests are for uddiv3 only, not supportable using v2
                        //tckFindEntityJoe.findRelatedBusinessToKey(false);
                        //tckFindEntityJoe.findRelatedBusinessFromKey(false);

                        tckAssertionJoe.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertionJoe.deleteJoePublisherPublisherAssertion2(authInfoJoe);

                } finally {
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                        tckTModelMary.deleteMaryPublisherTmodel(authInfoMary);
                }
        }
}
