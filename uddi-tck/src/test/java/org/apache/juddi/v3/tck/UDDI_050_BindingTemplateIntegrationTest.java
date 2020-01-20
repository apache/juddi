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
public class UDDI_050_BindingTemplateIntegrationTest {

        private static Log logger = LogFactory.getLog(UDDI_050_BindingTemplateIntegrationTest.class);
        private static TckTModel tckTModel = null;
        private static TckBusiness tckBusiness = null;
        private static TckBusinessService tckBusinessService = null;
        private static TckBindingTemplate tckBindingTemplate = null;
        private static TckFindEntity tckFindEntity = null;
        private static String authInfoJoe = null;
        private static UDDIClient manager;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
             if (!TckPublisher.isEnabled()) return;
                tckTModel.deleteCreatedTModels(authInfoJoe);
                
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
                        UDDIPublicationPortType publication = transport.getUDDIPublishService();
                        UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        //Assert.assertNotNull(authInfoJoe);
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }


                        tckTModel = new TckTModel(publication, inquiry);
                        tckBusiness = new TckBusiness(publication, inquiry);
                        tckBusinessService = new TckBusinessService(publication, inquiry);
                        tckBindingTemplate = new TckBindingTemplate(publication, inquiry);
                        tckFindEntity = new TckFindEntity(inquiry);


                        transport = manager.getTransport("uddiv3");
                        security = transport.getUDDISecurityService();
                        publication = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();
                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        //Assert.assertNotNull(authInfoJoe);
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        }


                        tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
                JUDDI_300_MultiNodeIntegrationTest.testSetupReplicationConfig();
        }

        @Test
        public void joepublisher() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
                        tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void findService() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
                        tckBindingTemplate.deleteBinding(authInfoJoe, "uddi:uddi.joepublisher.com:bindingone");
                        String serviceKey = tckFindEntity.findService(null);
                        tckFindEntity.findServiceDetail(serviceKey);

                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);

                        serviceKey = tckFindEntity.findService(null);
                        tckFindEntity.findServiceDetail(serviceKey);

                        tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);

                        tckFindEntity.findService(null);
                        tckFindEntity.findServiceDetail(serviceKey);
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }
}
