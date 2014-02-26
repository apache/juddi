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

import java.util.Arrays;
import java.util.UUID;

import javax.xml.ws.BindingProvider;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.JAXWSv2TranslationTransport;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v2.*;
import org.uddi.v2_service.*;

/**
 * This test case exercises a number of the FindXXX API's in the UDDI Inquiry
 * spec. It also checks for the service catching invalid or incorrect
 * combinations of find qualifiers.<br><br>
 *
 * Most of the tests in this class reference's valid and invalid combinations of
 * Find Qualifiers. See <a
 * href="http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Ref535479804">UDDI spec
 * on valid find qualifiers</a>
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_070_FindEntityIntegrationTest {

        private static final Log logger = LogFactory.getLog(UDDI_070_FindEntityIntegrationTest.class);
        private static TckTModel tckTModel = null;
        private static TckBusiness tckBusiness = null;
        private static TckBusinessService tckBusinessService = null;
        private static TckBindingTemplate tckBindingTemplate = null;
        private static TckFindEntity tckFindEntity = null;
        private static String authInfoJoe = null;
        private static UDDIClient manager;
        private static Inquire inquiry = null;

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
                        JAXWSv2TranslationTransport transport = (JAXWSv2TranslationTransport) manager.getTransport("uddiv2");
                        Publish security = transport.getUDDIv2PublishService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        //Assert.assertNotNull(authInfoJoe);

                        Publish publication = transport.getUDDIv2PublishService();
                        inquiry = transport.getUDDIv2InquiryService();

                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                        tckTModel = new TckTModel(publication, inquiry);
                        tckBusiness = new TckBusiness(publication, inquiry);
                        tckBusinessService = new TckBusinessService(publication, inquiry);
                        tckBindingTemplate = new TckBindingTemplate(publication, inquiry);
                        tckFindEntity = new TckFindEntity(inquiry);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @Test
        public void findEntities() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe, true);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
                        tckFindEntity.findBusiness();
                        tckFindEntity.findService(null);
                        tckFindEntity.findBinding(null);
                        tckFindEntity.findTModel(null);
                        tckFindEntity.getNonExitingBusiness();
                } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail(e.getMessage() + e.getClass().getCanonicalName());
                } catch (Throwable t) {
                        t.printStackTrace();
                        Assert.fail(t.getMessage() + t.getClass().getCanonicalName());
                } finally {
                        tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        

        private void before() {
                tckTModel.saveJoePublisherTmodel(authInfoJoe, true);
                tckBusiness.saveJoePublisherBusiness(authInfoJoe);

        }

        private void after() {
                tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                tckTModel.deleteJoePublisherTmodel(authInfoJoe);
        }

        private void findbuinsess(String fq) throws Exception {
                FindBusiness fb = new FindBusiness();
                //fb.setAuthInfo(authInfoJoe);
                fb.setGeneric("2.0");
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(fq);
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBusiness(fb);
        }

        private void findservice(String fq) throws Exception {
                FindService fb = new FindService();
                //fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.setGeneric("2.0");
                fb.getFindQualifiers().getFindQualifier().add(fq);
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findService(fb);
        }

        private void findtmodel(String fq) throws Exception {
                FindTModel fb = new FindTModel();
                //fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.setGeneric("2.0");
                fb.getFindQualifiers().getFindQualifier().add(fq);
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findTModel(fb);
        }

        private void findbinding(String fq) throws Exception {
                FindBinding fb = new FindBinding();
                //fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(fq);
                fb.setGeneric("2.0");
                //fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                //fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBinding(fb);
        }

        private void findrelated(String fq) throws Exception {
                before();
                FindRelatedBusinesses fb = new FindRelatedBusinesses();
                fb.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                //fb.setAuthInfo(authInfoJoe);
                fb.setGeneric("2.0");
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.getFindQualifiers().getFindQualifier().add(fq);
                inquiry.findRelatedBusinesses(fb);
                after();
        }

        private void findbuinsess(String[] fq) throws Exception {
                FindBusiness fb = new FindBusiness();
                //fb.setAuthInfo(authInfoJoe);
                fb.setGeneric("2.0");
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));

                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBusiness(fb);
        }

        private void findservice(String[] fq) throws Exception {
                FindService fb = new FindService();
                //fb.setAuthInfo(authInfoJoe);
                fb.setGeneric("2.0");
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findService(fb);
        }

        private void findtmodel(String[] fq) throws Exception {
                FindTModel fb = new FindTModel();
                //fb.setAuthInfo(authInfoJoe);
                fb.setGeneric("2.0");
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findTModel(fb);
        }

        private void findbinding(String[] fq) throws Exception {
                FindBinding fb = new FindBinding();
                //fb.setAuthInfo(authInfoJoe);
                fb.setGeneric("2.0");
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                //fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBinding(fb);
        }

        private void findbinding(String[] fq, KeyedReference[] cats) throws Exception {
                FindBinding fb = new FindBinding();
                //fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                fb.setGeneric("2.0");
                //fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBinding(fb);
        }

        private void findrelated(String[] fq) throws Exception {
                before();
                FindRelatedBusinesses fb = new FindRelatedBusinesses();
                fb.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                fb.setGeneric("2.0");
                //fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                inquiry.findRelatedBusinesses(fb);
                after();
        }


}
