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
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_020_TmodelIntegrationTest {

        private static TckTModel tckTModelJoe = null;
        private static TckTModel tckTModelSam = null;
        private static Log logger = LogFactory.getLog(UDDI_020_TmodelIntegrationTest.class);
        private static String authInfoJoe = null;
        private static String authInfoSam = null;
        private static UDDIClient manager;

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
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

                        transport = manager.getTransport("uddiv3");
                        publication = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        }
                        tckTModelSam = new TckTModel(publication, inquiry);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
                JUDDI_300_MultiNodeIntegrationTest.testSetupReplicationConfig();
        }

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                tckTModelSam.deleteCreatedTModels(authInfoSam);
                manager.stop();
        }

        @Test
        public void testJoePublisherTmodel() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                tckTModelJoe.saveJoePublisherTmodel(authInfoJoe, true);

                //Now if we use a finder it should be found.
                TModelList tModelList = tckTModelJoe.findJoeTModelDetail();
                Assert.assertNotNull(tModelList.getTModelInfos());

                tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);

                //Even if it deleted you should still be able to access it through a getTModelDetail
                TModelDetail detail = tckTModelJoe.getJoePublisherTmodel(authInfoJoe);
                Assert.assertNotNull(detail.getTModel());

                //However if we use a finder it should not be found.
                TModelList tModelList2 = tckTModelJoe.findJoeTModelDetail();
                Assert.assertNull(tModelList2.getTModelInfos());

                //Make sure none of the found key generators is Joe's key generator
                TModelList tModelList3 = tckTModelJoe.findJoeTModelDetailByCategoryBag();
                for (TModelInfo tModelInfo : tModelList3.getTModelInfos().getTModelInfo()) {
                        Assert.assertFalse("uddi:uddi.joepublisher.com:keygenerator".equals(tModelInfo.getTModelKey()));
                }
        }

        @Test
        public void testSamSyndicatorTmodelTest() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
        }
}
