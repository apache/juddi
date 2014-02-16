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

import java.util.Arrays;
import java.util.UUID;

import javax.xml.ws.BindingProvider;

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
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

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
        private static UDDIInquiryPortType inquiry = null;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                tckTModel.deleteCreatedTModels(authInfoJoe);
                manager.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth tokens..");
                try {
                        Transport transport = manager.getTransport("uddiv3");
                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        //Assert.assertNotNull(authInfoJoe);

                        UDDIPublicationPortType publication = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();

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
                        Assert.fail();
                } catch (Throwable t) {
                        t.printStackTrace();
                        Assert.fail();
                } finally {
                        tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void findSignedEntities() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusinessX509Signature(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);

                        tckFindEntity.findAllSignedBusiness();
                        tckFindEntity.findService(UDDIConstants.SIGNATURE_PRESENT);
                        tckFindEntity.findBinding(UDDIConstants.SIGNATURE_PRESENT);
                        //tckFindEntity.findTModel(UDDIConstants.SIGNATURE_PRESENT);

                        tckFindEntity.findAllBusiness();
                        tckFindEntity.getNonExitingBusiness();
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
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(fq);
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBusiness(fb);
        }

        private void findservice(String fq) throws Exception {
                FindService fb = new FindService();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(fq);
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findService(fb);
        }

        private void findtmodel(String fq) throws Exception {
                FindTModel fb = new FindTModel();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(fq);
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findTModel(fb);
        }

        private void findbinding(String fq) throws Exception {
                FindBinding fb = new FindBinding();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(fq);
                //fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                //fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBinding(fb);
        }

        private void findrelated(String fq) throws Exception {
                before();
                FindRelatedBusinesses fb = new FindRelatedBusinesses();
                fb.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.getFindQualifiers().getFindQualifier().add(fq);
                inquiry.findRelatedBusinesses(fb);
                after();
        }

        private void findbuinsess(String[] fq) throws Exception {
                FindBusiness fb = new FindBusiness();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));

                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBusiness(fb);
        }

        private void findservice(String[] fq) throws Exception {
                FindService fb = new FindService();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findService(fb);
        }

        private void findtmodel(String[] fq) throws Exception {
                FindTModel fb = new FindTModel();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findTModel(fb);
        }

        private void findbinding(String[] fq) throws Exception {
                FindBinding fb = new FindBinding();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                //fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBinding(fb);
        }

        private void findbinding(String[] fq, KeyedReference[] cats) throws Exception {
                FindBinding fb = new FindBinding();
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                if (cats != null) {
                        fb.setCategoryBag(new CategoryBag());
                        fb.getCategoryBag().getKeyedReference().addAll(Arrays.asList(cats));
                }
                //fb.setName(new Name(UDDIConstants.WILDCARD, null));
                inquiry.findBinding(fb);
        }

        private void findrelated(String[] fq) throws Exception {
                before();
                FindRelatedBusinesses fb = new FindRelatedBusinesses();
                fb.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                fb.setAuthInfo(authInfoJoe);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
                inquiry.findRelatedBusinesses(fb);
                after();
        }

        //JUDDI-764
        //binarySort
        //<editor-fold defaultstate="collapsed" desc="binarySort">
        @Test
        public void UDDI_764binarySortBusiness() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.BINARY_SORT);
        }

        @Test
        public void UDDI_764binarySortService() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.BINARY_SORT);
        }

        @Test
        public void UDDI_764binarySortTModel() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.BINARY_SORT);
        }

        @Test
        public void UDDI_764binarySortBinding() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.BINARY_SORT);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764binarySortRelatedBiz() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.BINARY_SORT);
        }

        @Test
        public void UDDI_764binarySortBusinessKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.BINARY_SORT_TMODEL);
        }

        @Test
        public void UDDI_764binarySortServicev() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.BINARY_SORT_TMODEL);
        }

        @Test
        public void UDDI_764binarySortTModelKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.BINARY_SORT_TMODEL);
        }

        @Test
        public void UDDI_764binarySortBindingKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.BINARY_SORT_TMODEL);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764binarySortRelatedBizKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.BINARY_SORT_TMODEL);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="CASE_INSENSITIVE_SORT">
        //see JUDDI-785, jUDDI doesn't support it, and thus should throw.
        /**
         * Note that jUDDI ignores caseInsensiveSort.
         * {@link org.apache.juddi.v3.client.UDDIConstants#CASE_INSENSITIVE_SORT CASE_INSENSITIVE_SORT}
         * @throws Exception
         */
        @Test
        public void UDDI_764caseInsensitiveSortBusiness() throws Exception {
                Assume.assumeFalse(TckPublisher.isJUDDI());
                findbuinsess(UDDIConstants.CASE_INSENSITIVE_SORT);
        }

        /**
         * Note that jUDDI ignores caseInsensiveSort.
         *
         * {@link org.apache.juddi.v3.client.UDDIConstants#CASE_INSENSITIVE_SORT CASE_INSENSITIVE_SORT}
         * @throws Exception
         */
        @Test
        public void UDDI_764caseInsensitiveSortService() throws Exception {
                Assume.assumeFalse(TckPublisher.isJUDDI());
                findservice(UDDIConstants.CASE_INSENSITIVE_SORT);
        }

        /**
         * Note that jUDDI ignores caseInsensiveSort.
         *
         * {@link org.apache.juddi.v3.client.UDDIConstants#CASE_INSENSITIVE_SORT CASE_INSENSITIVE_SORT}
         * @throws Exception
         */
        @Test
        public void UDDI_764caseInsensitiveSortTModel() throws Exception {
                Assume.assumeFalse(TckPublisher.isJUDDI());
                findtmodel(UDDIConstants.CASE_INSENSITIVE_SORT);
        }

        @Test
        public void UDDI_764caseInsensitiveSortBinding() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.CASE_INSENSITIVE_SORT);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        /**
         * Note that jUDDI ignores caseInsensiveSort.
         *
         * {@link org.apache.juddi.v3.client.UDDIConstants#CASE_INSENSITIVE_SORT CASE_INSENSITIVE_SORT}
         * @throws Exception
         */
        @Test
        public void UDDI_764caseInsensitiveSortRelatedBiz() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeFalse(TckPublisher.isJUDDI());
                findrelated(UDDIConstants.CASE_INSENSITIVE_SORT);
        }

        @Test
        public void UDDI_764caseInsensitiveSortBusinessKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeFalse(TckPublisher.isJUDDI());
                findbuinsess(UDDIConstants.CASE_INSENSITIVE_SORT_TMODEL);
        }

        @Test
        public void UDDI_764caseInsensitiveSortServiceKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeFalse(TckPublisher.isJUDDI());
                findservice(UDDIConstants.CASE_INSENSITIVE_SORT_TMODEL);
        }

        @Test
        public void UDDI_764caseInsensitiveSortTModelKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeFalse(TckPublisher.isJUDDI());
                findtmodel(UDDIConstants.CASE_INSENSITIVE_SORT_TMODEL);
        }

        @Test
        public void UDDI_764caseInsensitiveSortBindingKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeFalse(TckPublisher.isJUDDI());
                try {
                        findbinding(UDDIConstants.CASE_INSENSITIVE_SORT_TMODEL);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        @Ignore
        public void UDDI_764caseInsensitiveSortRelatedBizKey() throws Exception {
                Assume.assumeFalse(TckPublisher.isJUDDI());
                Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.CASE_INSENSITIVE_SORT_TMODEL);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="caseSensitiveSort">
        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortBusiness() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.CASE_SENSITIVE_SORT);
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortService() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.CASE_SENSITIVE_SORT);
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortTModel() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.CASE_SENSITIVE_SORT);
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortBinding() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.CASE_SENSITIVE_SORT);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortRelatedBiz() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.CASE_SENSITIVE_SORT);
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortBusinessKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.CASE_SENSITIVE_SORT_TMODEL);
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortServicev() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.CASE_SENSITIVE_SORT_TMODEL);
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortTModelKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.CASE_SENSITIVE_SORT_TMODEL);
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortBindingKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.CASE_SENSITIVE_SORT_TMODEL);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_SORTSortRelatedBizKey() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.CASE_SENSITIVE_SORT_TMODEL);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="SORT_BY_DATE_ASC">
        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortBusiness() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_DATE_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortService() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_DATE_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_DATE_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbinding(new String[]{UDDIConstants.SORT_BY_DATE_ASC},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});

        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_DATE_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_DATE_ASC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortServicev() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_DATE_ASC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_DATE_ASC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                //  findbinding(UDDIConstants.SORT_BY_DATE_ASC_TMODEL);
                findbinding(new String[]{UDDIConstants.SORT_BY_DATE_ASC_TMODEL},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});

        }

        @Test
        public void UDDI_764SORT_BY_DATE_ASCSortRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_DATE_ASC_TMODEL);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="SORT_BY_DATE_DESC">
        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_DATE_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_DATE_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_DATE_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbinding(new String[]{UDDIConstants.SORT_BY_DATE_DESC},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});
                //(UDDIConstants.SORT_BY_DATE_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_DATE_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_DATE_DESC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortServicev() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_DATE_DESC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_DATE_DESC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbinding(new String[]{UDDIConstants.SORT_BY_DATE_DESC_TMODEL},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});
        }

        @Test
        public void UDDI_764SORT_BY_DATE_DESCSortRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_DATE_DESC_TMODEL);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="SORT_BY_NAME_ASC">
        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_NAME_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_NAME_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_NAME_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.SORT_BY_NAME_ASC);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_NAME_ASC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_NAME_ASC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortServicev() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_NAME_ASC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_NAME_ASC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.SORT_BY_NAME_ASC_TMODEL);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764SORT_BY_NAME_ASCSortRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_NAME_ASC_TMODEL);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="SORT_BY_NAME_DESC">
        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_NAME_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_NAME_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_NAME_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.SORT_BY_NAME_DESC);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_NAME_DESC);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.SORT_BY_NAME_DESC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortServicev() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.SORT_BY_NAME_DESC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.SORT_BY_NAME_DESC_TMODEL);
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UDDIConstants.SORT_BY_NAME_DESC_TMODEL);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764SORT_BY_NAME_DESCSortRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(UDDIConstants.SORT_BY_NAME_DESC_TMODEL);
        }
//</editor-fold>

        //JIS-X4061 OPTIONAL
        //<editor-fold defaultstate="collapsed" desc="andAllKeys">
        @Test
        public void UDDI_764AND_ALL_KEYSSortBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.AND_ALL_KEYS);
        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.AND_ALL_KEYS);
        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.AND_ALL_KEYS);
        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                //findbinding(UDDIConstants.AND_ALL_KEYS);
                findbinding(new String[]{UDDIConstants.AND_ALL_KEYS},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});

        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(UDDIConstants.AND_ALL_KEYS);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(UDDIConstants.AND_ALL_KEYS_TMODEL);
        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortServicev() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(UDDIConstants.AND_ALL_KEYS_TMODEL);
        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(UDDIConstants.AND_ALL_KEYS_TMODEL);
        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());

                //findbinding(UDDIConstants.AND_ALL_KEYS_TMODEL);
                findbinding(new String[]{UDDIConstants.AND_ALL_KEYS_TMODEL},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});

        }

        @Test
        public void UDDI_764AND_ALL_KEYSSortRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(UDDIConstants.AND_ALL_KEYS_TMODEL);
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }
//</editor-fold>

        //
        //<editor-fold defaultstate="collapsed" desc="bindingSubset">
        @Test
        public void UDDI_764BINDING_SUBSETBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.BINDING_SUBSET});
        }

        @Test
        public void UDDI_764BINDING_SUBSETService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.BINDING_SUBSET});
        }

        @Test
        public void UDDI_764BINDING_SUBSETTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.BINDING_SUBSET});
        }

        @Test
        public void UDDI_764BINDING_SUBSETBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbinding(new String[]{UDDIConstants.BINDING_SUBSET},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});

                //        findbinding(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.BINDING_SUBSET});
        }

        @Test
        public void UDDI_764BINDING_SUBSETRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.BINDING_SUBSET});
        }

        @Test
        public void UDDI_764BINDING_SUBSETBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.BINDING_SUBSET_TMODEL});
        }

        @Test
        public void UDDI_764BINDING_SUBSETServiceKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.BINDING_SUBSET_TMODEL});
        }

        @Test
        public void UDDI_764BINDING_SUBSETTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.BINDING_SUBSET_TMODEL});
        }

        @Test
        public void UDDI_764BINDING_SUBSETBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbinding(new String[]{UDDIConstants.BINDING_SUBSET_TMODEL},
                     new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});
        }

        @Test
        public void UDDI_764BINDING_SUBSETRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.BINDING_SUBSET_TMODEL});
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="caseInsensitiveMatch">
        @Test
        public void UDDI_764caseInsensitiveMatchBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764caseInsensitiveMatchService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764caseInsensitiveMatchTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764caseInsensitiveMatchBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{UDDIConstants.CASE_INSENSITIVE_MATCH},
                             new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});
                        Assert.fail("unexpected success");

                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764caseInsensitiveMatchRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764caseInsensitiveMatchBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_INSENSITIVE_MATCH_TMODEL});
        }

        @Test
        public void UDDI_764caseInsensitiveMatchServiceKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_INSENSITIVE_MATCH_TMODEL});
        }

        @Test
        public void UDDI_764caseInsensitiveMatchTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_INSENSITIVE_MATCH_TMODEL});
        }

        @Test
        public void UDDI_764caseInsensitiveMatchBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                //findbinding(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_INSENSITIVE_MATCH_TMODEL});
                try {
                        findbinding(new String[]{UDDIConstants.CASE_INSENSITIVE_MATCH_TMODEL},
                             new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});
                        Assert.fail("unexpected success");

                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764caseInsensitiveMatchRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_INSENSITIVE_MATCH_TMODEL});
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="caseSensitiveMatch">
        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_SENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_SENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_SENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{UDDIConstants.CASE_SENSITIVE_MATCH},
                             new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.CASE_SENSITIVE_MATCH});
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL});
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHServiceKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL});
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL});
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                //findbinding(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL});
                try {
                        findbinding(new String[]{UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL},
                             new KeyedReference[]{new KeyedReference(UDDIConstants.TRANSPORT_HTTP, "test", "test")});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764CASE_SENSITIVE_MATCHRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL});
        }
//</editor-fold>

        //
        //
        //<editor-fold defaultstate="collapsed" desc="combineCategoryBags">
        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSBusiness() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.COMBINE_CATEGORY_BAGS});
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSService() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.COMBINE_CATEGORY_BAGS});
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSTModel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.COMBINE_CATEGORY_BAGS});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSBinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.COMBINE_CATEGORY_BAGS});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSRelatedBiz() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH, UDDIConstants.COMBINE_CATEGORY_BAGS});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSBusinessKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());

                findbuinsess(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL});
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSServiceKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                findservice(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL});
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSTModelKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSBindingKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764COMBINE_CATEGORY_BAGSRelatedBizKey() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{UDDIConstants.APPROXIMATE_MATCH_TMODEL, UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL});
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }
//</editor-fold>

        //diacriticInsensitiveMatch	Optional	yes	
        //diacriticSensitiveMatch
        //exactMatch
        //signaturePresent
        //orAllKeys
        //orLikeKeys
        //serviceSubset
        //suppressProjectedServices
        //<editor-fold defaultstate="collapsed" desc="invalid combos andAllKeys, orAllKeys, and orLikeKeys are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo1Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1Business1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.AND_ALL_KEYS //, UDDIConstants.OR_ALL_KEYS
                                , UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1Business2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1Business3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                //  UDDIConstants.AND_ALL_KEYS
                                UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1Service1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.AND_ALL_KEYS //, UDDIConstants.OR_ALL_KEYS
                                , UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1Service2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1Service3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                //  UDDIConstants.AND_ALL_KEYS
                                UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findtmodel1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.AND_ALL_KEYS //, UDDIConstants.OR_ALL_KEYS
                                , UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findtmodel2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findtmodel3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                //  UDDIConstants.AND_ALL_KEYS
                                UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findbinding1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.AND_ALL_KEYS //, UDDIConstants.OR_ALL_KEYS
                                , UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findbinding2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findbinding3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                //  UDDIConstants.AND_ALL_KEYS
                                UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findrelated1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.AND_ALL_KEYS //, UDDIConstants.OR_ALL_KEYS
                                , UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findrelated2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.AND_ALL_KEYS, UDDIConstants.OR_ALL_KEYS
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo1findrelated3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                //  UDDIConstants.AND_ALL_KEYS
                                UDDIConstants.OR_ALL_KEYS, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //
        //<editor-fold defaultstate="collapsed" desc="invalid combos sortByNameAsc and sortByNameDesc are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo2Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.SORT_BY_NAME_ASC, UDDIConstants.SORT_BY_NAME_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo2Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.SORT_BY_NAME_ASC, UDDIConstants.SORT_BY_NAME_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo2findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.SORT_BY_NAME_ASC, UDDIConstants.SORT_BY_NAME_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo2findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.SORT_BY_NAME_ASC, UDDIConstants.SORT_BY_NAME_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo2findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.SORT_BY_NAME_ASC, UDDIConstants.SORT_BY_NAME_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //
        //<editor-fold defaultstate="collapsed" desc="invalid combos sortByDateAsc and sortByDateDesc are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo3Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.SORT_BY_DATE_ASC, UDDIConstants.SORT_BY_DATE_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo3Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.SORT_BY_DATE_ASC, UDDIConstants.SORT_BY_DATE_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo3findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.SORT_BY_DATE_ASC, UDDIConstants.SORT_BY_DATE_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo3findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.SORT_BY_DATE_ASC, UDDIConstants.SORT_BY_DATE_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo3findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.SORT_BY_DATE_ASC, UDDIConstants.SORT_BY_DATE_DESC
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="combineCategoryBags, serviceSubset and bindingSubset are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo4Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4Business1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4Business2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET

                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4Business3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4Service1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4Service2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4Service3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findtmodel1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findtmodel2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findtmodel3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findbinding1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findbinding2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findbinding3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findrelated1() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findrelated2() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.COMBINE_CATEGORY_BAGS, UDDIConstants.SERVICE_SUBSET
                        //, UDDIConstants.OR_LIKE_KEYS
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo4findrelated3() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.SERVICE_SUBSET, UDDIConstants.BINDING_SUBSET
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //
        //
        //
        //<editor-fold defaultstate="collapsed" desc="exactMatch and approximateMatch are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo5Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.APPROXIMATE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo5Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.APPROXIMATE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo5findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.APPROXIMATE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo5findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.APPROXIMATE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo5findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.APPROXIMATE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //
        //<editor-fold defaultstate="collapsed" desc="exactMatch and caseInsensitiveMatch are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo6Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo6Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo6findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo6findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo6findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="binarySort and UTS-10 are mutually exclusive, as are all collation algorithm tModels with each other">
        @Test
        public void UDDI_764InvalidCombo7Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.BINARY_SORT, UDDIConstants.UTS_10
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo7Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.BINARY_SORT, UDDIConstants.UTS_10
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo7findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.BINARY_SORT, UDDIConstants.UTS_10
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo7findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.BINARY_SORT, UDDIConstants.UTS_10
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo7findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.BINARY_SORT, UDDIConstants.UTS_10
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="diacriticSensitiveMatch and diacriticInsensitiveMatch are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo8Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.DIACRITIC_SENSITIVE_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo8Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.DIACRITIC_SENSITIVE_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo8findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.DIACRITIC_SENSITIVE_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo8findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.DIACRITIC_SENSITIVE_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo8findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.DIACRITIC_SENSITIVE_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="exactMatch and diacriticInsensitiveMatch are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo9Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo9Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo9findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo9findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo9findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.EXACT_MATCH, UDDIConstants.DIACRITIC_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="caseSensitiveSort and caseInsensitiveSort are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo10Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.CASE_INSENSITIVE_SORT, UDDIConstants.CASE_SENSITIVE_SORT
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo10Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.CASE_INSENSITIVE_SORT, UDDIConstants.CASE_SENSITIVE_SORT
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo10findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.CASE_INSENSITIVE_SORT, UDDIConstants.CASE_SENSITIVE_SORT
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo10findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.CASE_INSENSITIVE_SORT, UDDIConstants.CASE_SENSITIVE_SORT
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo10findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.CASE_INSENSITIVE_SORT, UDDIConstants.CASE_SENSITIVE_SORT
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="caseSensitiveMatch and caseInsensitiveMatch are mutually exclusive">
        @Test
        public void UDDI_764InvalidCombo11Business() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(new String[]{
                                UDDIConstants.CASE_SENSITIVE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo11Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(new String[]{
                                UDDIConstants.CASE_SENSITIVE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo11findtmodel() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(new String[]{
                                UDDIConstants.CASE_SENSITIVE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo11findbinding() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(new String[]{
                                UDDIConstants.CASE_SENSITIVE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_764InvalidCombo11findrelated() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(new String[]{
                                UDDIConstants.CASE_SENSITIVE_MATCH, UDDIConstants.CASE_INSENSITIVE_MATCH
                        });
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        //</editor-fold>
        //
       
        //<editor-fold defaultstate="collapsed" desc="Find qualifiers not recognized by a node will return the error E_unsupported. ">
        @Test
        public void UDDI_785UNSUPPORTED_FIND_QUALIFIER_Business() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbuinsess(UUID.randomUUID().toString());
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_785UNSUPPORTED_FIND_QUALIFIER_Service() throws Exception { Assume.assumeTrue(TckPublisher.isEnabled());
        Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findservice(UUID.randomUUID().toString());
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_785UNSUPPORTED_FIND_QUALIFIER_TModel() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findtmodel(UUID.randomUUID().toString());
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_785UNSUPPORTED_FIND_QUALIFIER_Binding() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findbinding(UUID.randomUUID().toString());
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

        @Test
        public void UDDI_785UNSUPPORTED_FIND_QUALIFIER_RelatedBiz() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        findrelated(UUID.randomUUID().toString());
                        Assert.fail("Unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }
        }

//</editor-fold>
        
         //TODO test cases for mixed case find qualifiers
        //"Registries MUST support both forms, and MUST accept the find qualifiers case-insensitively. "
}
