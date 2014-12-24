package org.apache.juddi.v2.tck;

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

import java.util.List;
import javax.xml.ws.BindingProvider;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.JAXWSv2TranslationTransport;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v2.*;

import org.uddi.v2_service.*;

/**
 * Test to verify JUDDI-398
 *
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_110_FindBusinessIntegrationTest {

        final static String TOM_PUBLISHER_TMODEL_XML = "uddi_data_v2/tompublisher/tModelKeyGen.xml";
        final static String TOM_PUBLISHER_TMODEL01_XML = "uddi_data_v2/tompublisher/tModel01.xml";
        final static String TOM_PUBLISHER_TMODEL02_XML = "uddi_data_v2/tompublisher/tModel02.xml";
        final static String TOM_PUBLISHER_TMODEL_KEY = "uuid:b6a1c995-3dc2-4411-a6c6-f5f6739e46fd";
        
        final static String TOM_PUBLISHER_TMODEL01_KEY = "uuid:165a69c5-996a-49cc-8d7c-6600a30c2ad9";
        final static String TOM_PUBLISHER_TMODEL01_NAME = "tmodeltest01";
        final static String TOM_PUBLISHER_TMODEL02_KEY = "uuid:9b79f267-1536-4a9e-85a1-d333f54b5038";
        final static String TOM_BUSINESS_XML = "uddi_data_v2/tompublisher/businessEntity.xml";
        
        final static String TOM_BUSINESS_KEY = "a0dbe2d4-3eea-4321-969f-827a4cc1d175";
        final static String TOM_PUBLISHER_SERVICEINFO_NAME = "servicetest01";
        private static Log logger = LogFactory.getLog(UDDI_040_BusinessServiceIntegrationTest.class);
        protected static TckTModel tckTModel = null;
        protected static TckTModel tckTModel01 = null;
        protected static TckTModel tckTModel02 = null;
        protected static TckBusiness tckBusiness = null;
        protected static String authInfoJoe = null;
        private static Inquire inquiry = null;
        private static UDDIClient manager;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) return;
                tckTModel.deleteCreatedTModels(authInfoJoe);
                tckTModel01.deleteCreatedTModels(authInfoJoe);
                tckTModel02.deleteCreatedTModels(authInfoJoe);
                manager.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) return;
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth tokens..");
                try {
                        JAXWSv2TranslationTransport transport = (JAXWSv2TranslationTransport) manager.getTransport("uddiv2");
                        Publish security = transport.getUDDIv2PublishService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        //Assert.assertNotNull(authInfoJoe);
                        
                        Publish publication = transport.getUDDIv2PublishService();
                        inquiry = transport.getUDDIv2InquiryService();
                        if (!TckPublisher.isUDDIAuthMode()){
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                        tckTModel = new TckTModel(publication, inquiry);
                        tckTModel01 = new TckTModel(publication, inquiry);
                        tckTModel02 = new TckTModel(publication, inquiry);
                        tckBusiness = new TckBusiness(publication, inquiry);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        /**
         * JUDDI-398
         * JUDDI-881
         * "If a tModelBag or find_tModel was used in the search, the resulting serviceInfos structure reflects data only for the businessServices that actually contained a matching bindingTemplate.
         */
        @Test
        public void findBusinessByTModelBag() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
                        tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
                        tckTModel.saveTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

                        tckBusiness.saveBusinesses(authInfoJoe, TOM_BUSINESS_XML, TOM_BUSINESS_KEY, 1);

                        try {
                                int size = 0;
                                BusinessList bl = null;

                                FindBusiness fbb = new FindBusiness();
                                TModelBag tmb = new TModelBag();
                                tmb.getTModelKey().add(TOM_PUBLISHER_TMODEL01_KEY);
                                fbb.setTModelBag(tmb);
                                bl = inquiry.findBusiness(fbb);
                                size = bl.getBusinessInfos().getBusinessInfo().size();
                                if (size != 1) {
                                        Assert.fail("Should have found one entry on FindBusiness with TModelBag, "
                                                + "found " + size);
                                } else {
                                        List<BusinessInfo> biList = bl.getBusinessInfos().getBusinessInfo();
                                        if (biList.get(0).getServiceInfos().getServiceInfo().size() != 1) {
                                                Assert.fail("Should have found one ServiceInfos");
                                        } else {
                                                List<ServiceInfo> siList = biList.get(0).getServiceInfos().getServiceInfo();
                                                ServiceInfo si = siList.get(0);
                                                if (!TOM_PUBLISHER_SERVICEINFO_NAME.equals(si.getName().get(0).getValue())) {
                                                        Assert.fail("Should have found " + TOM_PUBLISHER_TMODEL01_NAME + " as the "
                                                                + "ServiceInfo name, found " + si.getName().get(0).getValue());
                                                }
                                        }
                                }
                        } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                        }
                } finally {
                        tckBusiness.deleteBusinesses(authInfoJoe, TOM_BUSINESS_XML, TOM_BUSINESS_KEY, 1);

                        tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL_XML, TOM_PUBLISHER_TMODEL_KEY);
                        tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL01_XML, TOM_PUBLISHER_TMODEL01_KEY);
                        tckTModel.deleteTModel(authInfoJoe, TOM_PUBLISHER_TMODEL02_XML, TOM_PUBLISHER_TMODEL02_KEY);

                }
        }
}
