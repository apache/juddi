/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.tck;

import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 *
 * @author Alex O'Ree
 */
public class UDDI_142_DigitalSignatureIntegrationTest {

        private static Log logger = LogFactory.getLog(UDDI_142_DigitalSignatureIntegrationTest.class);
        static UDDISecurityPortType security = null;
        static UDDISubscriptionPortType subscriptionJoe = null;
        static UDDIInquiryPortType inquiryJoe = null;
        static UDDIPublicationPortType publicationJoe = null;
        static TckTModel tckTModelJoe = null;
        static TckBusiness tckBusinessJoe = null;
        static UDDISubscriptionPortType subscriptionSam = null;
        static UDDIInquiryPortType inquiryJoeSam = null;
        static UDDIPublicationPortType publicationSam = null;
        static TckTModel tckTModelSam = null;
        static TckBusiness tckBusinessSam = null;
        protected static String authInfoJoe = null;
        protected static String authInfoSam = null;
        private static UDDIClient manager;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
                tckTModelSam.deleteCreatedTModels(authInfoSam);
                manager.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                logger.info("UDDI_142_DigitalSignatureIntegrationTests");
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth tokens..");
                try {
                        Transport transport = manager.getTransport("uddiv3");
                        security = transport.getUDDISecurityService();

                        publicationJoe = transport.getUDDIPublishService();
                        inquiryJoe = transport.getUDDIInquiryService();

                        subscriptionJoe = transport.getUDDISubscriptionService();

                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        //Assert.assertNotNull(authInfoJoe);
                        //Assert.assertNotNull(authInfoSam);
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());

                        }
                        tckTModelJoe = new TckTModel(publicationJoe, inquiryJoe);
                        tckBusinessJoe = new TckBusiness(publicationJoe, inquiryJoe);

                        transport = manager.getTransport("uddiv3");

                        publicationSam = transport.getUDDIPublishService();
                        inquiryJoeSam = transport.getUDDIInquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationSam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoeSam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionSam, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }
                        subscriptionSam = transport.getUDDISubscriptionService();
                        tckTModelSam = new TckTModel(publicationSam, inquiryJoeSam);
                        tckBusinessSam = new TckBusiness(publicationSam, inquiryJoeSam);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
                JUDDI_300_MultiNodeIntegrationTest.testSetupReplicationConfig();
        }

        private void DeleteBusinesses(List<String> businesskeysToDelete, String authinfo, UDDIPublicationPortType pub) {
                //cleanup
                try {
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authinfo);
                        db.getBusinessKey().addAll(businesskeysToDelete);
                        pub.deleteBusiness(db);
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        private void DeleteBusinesses(String businesskeysToDelete, String authinfo, UDDIPublicationPortType pub) {
                //cleanup
                try {
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authinfo);
                        db.getBusinessKey().add(businesskeysToDelete);
                        pub.deleteBusiness(db);
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        @Test()
        public void JUDDI_712_SaveBusinessProjectionWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_JUDDI_712_SaveBusinessProjectionWithSignature");
                be.getName().add(n);
                DigSigUtil ds = GetDigSig();
                be = ds.signUddiEntity(be);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveBusinessProjectionNoServiceKeyWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveBusinessProjectionNoServiceKeyWithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                //service has neither business or service key
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name("Joe's bs", null));
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                DigSigUtil ds = GetDigSig();
                be = ds.signUddiEntity(be);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveBusinessProjectionNoServiceKey2WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveBusinessProjectionNoServiceKey2WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                //service has business but not service key
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.getName().add(new Name("Joe's bs", null));
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                DigSigUtil ds = GetDigSig();
                be = ds.signUddiEntity(be);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveBusinessProjectionNoServiceKey3WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveBusinessProjectionNoServiceKey3WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                //service has business but not service key
                BusinessService bs = new BusinessService();
                //bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.getName().add(new Name("Joe's bs", null));
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                DigSigUtil ds = GetDigSig();
                be = ds.signUddiEntity(be);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveServiceProjectionNoServiceKey3WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveServiceProjectionNoServiceKey3WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                BusinessService bs = new BusinessService();
                bs.setBusinessKey(null);
                bs.setServiceKey(null);
                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bs = ds.signUddiEntity(bs);

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveServiceProjectionNoServiceKey1WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveServiceProjectionNoServiceKey1WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                BusinessService bs = new BusinessService();
                bs.setBusinessKey(null);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bs = ds.signUddiEntity(bs);

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveServiceProjectionNoServiceKey2WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveServiceProjectionNoServiceKey2WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(null);
                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bs = ds.signUddiEntity(bs);

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        public void JUDDI_712_SaveBusinessNoneDefined() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveServiceWithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        throw ex;
                }
        }

        @Test()
        public void JUDDI_712_SaveServiceWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveServiceWithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(null);
                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bs = ds.signUddiEntity(bs);

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        publicationJoe.saveService(ss);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveService1WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveService1WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(null);
                bt.setServiceKey(null);
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bs = ds.signUddiEntity(bs);

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        publicationJoe.saveService(ss);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        @Test()
        public void JUDDI_712_SaveService2WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveService2WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {

                        saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(null);
                bt.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bs = ds.signUddiEntity(bs);

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        publicationJoe.saveService(ss);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        @Test()
        public void JUDDI_712_SaveService3WithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveService3WithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(TckBusinessService.JOE_BINDING_KEY_1);
                bt.setServiceKey(null);
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bs = ds.signUddiEntity(bs);

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        publicationJoe.saveService(ss);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        @Test()
        public void JUDDI_712_SaveTModelWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveTModel sb = new SaveTModel();
                sb.setAuthInfo(authInfoJoe);
                DigSigUtil ds = GetDigSig();
                TModel bs = new TModel();
                bs.setName(new Name("Joe's Tmodel", null));
                bs = ds.signUddiEntity(bs);

                sb.getTModel().add(bs);
                try {
                        publicationJoe.saveTModel(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                }
        }

        @Test()
        public void JUDDI_712_SaveService4BTWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveService4BTWithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(TckBusinessService.JOE_BINDING_KEY_1);
                bt.setServiceKey(null);
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));

                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bt = ds.signUddiEntity(bt);
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        publicationJoe.saveService(ss);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        @Test()
        public void JUDDI_712_SaveService5BTWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveService5BTWithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(null);
                bt.setServiceKey(null);
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));

                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bt = ds.signUddiEntity(bt);
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        publicationJoe.saveService(ss);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        @Test()
        public void JUDDI_712_SaveService6BTWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveService6BTWithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(null);
                bt.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));

                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bt = ds.signUddiEntity(bt);
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        publicationJoe.saveService(ss);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        @Test()
        public void JUDDI_712_SaveBusinessWithSignature() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_712_SaveBindingWithSignature");
                be.getName().add(n);
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                } catch (Exception ex) {
                        logger.info("UnExpected failure: ", ex);
                        Assert.fail();
                }

                SaveService ss = new SaveService();
                ss.setAuthInfo(authInfoJoe);
                BusinessService bs = new BusinessService();
                bs.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);
                bs.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bs.getName().add(new Name("joe's service", null));

                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                ServiceDetail saveService = null;
                ss.getBusinessService().add(bs);
                try {
                        saveService = publicationJoe.saveService(ss);
                } catch (Exception ex) {
                        //logger.error("unExpected failure: ",ex);
                        Assert.fail("unexpected failure " + ex.getMessage() + ex.toString());
                }

                bs = saveService.getBusinessService().get(0);
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(null);
                bt.setServiceKey(TckBusinessService.JOE_SERVICE_KEY);
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));

                bs.getName().add(new Name("Joe's bs", null));
                DigSigUtil ds = GetDigSig();
                bt = ds.signUddiEntity(bt);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                try {
                        SaveBinding sb1 = new SaveBinding();
                        sb1.setAuthInfo(authInfoJoe);
                        sb1.getBindingTemplate().add(bt);
                        publicationJoe.saveBinding(sb1);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        //JUDDI-716
        @Test()
        public void JUDDI_716_SaveBusinessWithSignatureX509IssuerSerial() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                Name n = new Name();
                n.setValue("JUDDI_716_SaveBusinessWithSignatureX509IssuerSerial");
                be.getName().add(n);
                DigSigUtil ds = GetDigSig();
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "true");
                be = ds.signUddiEntity(be);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                        GetBusinessDetail gsb = new GetBusinessDetail();
                        gsb.setAuthInfo(authInfoJoe);
                        gsb.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        BusinessDetail businessDetail = inquiryJoe.getBusinessDetail(gsb);
                        PrintUDDI<BusinessEntity> printer = new PrintUDDI<BusinessEntity>();
                        if (TckCommon.isDebug()) {
                                System.out.println(printer.print(businessDetail.getBusinessEntity().get(0)));
                        }
                        AtomicReference<String> msg = new AtomicReference<String>();
                        boolean b = ds.verifySignedUddiEntity(businessDetail.getBusinessEntity().get(0), msg);
                        Assert.assertTrue(msg.get(), b);
                        Assert.assertTrue(msg.get() == null || msg.get().length() == 0);

                } catch (Exception ex) {
                        logger.error("unExpected failure: ", ex);
                        Assert.fail("unexpected failure");
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        //JUDDI-716 
        @Test()
        public void JUDDI_716_SaveBusinessAllOptions() throws CertificateException {
                Assume.assumeTrue(TckPublisher.isEnabled());
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey(TckBusiness.JOE_BUSINESS_KEY);

                Name n = new Name();
                n.setValue("JUDDI_716_SaveBusinessAllOptions");
                be.getName().add(n);
                DigSigUtil ds = GetDigSig();
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "true");
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "true");
                be = ds.signUddiEntity(be);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = null;
                try {
                        saveBusiness = publicationJoe.saveBusiness(sb);
                        GetBusinessDetail gsb = new GetBusinessDetail();
                        gsb.setAuthInfo(authInfoJoe);
                        gsb.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        BusinessDetail businessDetail = inquiryJoe.getBusinessDetail(gsb);
                        PrintUDDI<BusinessEntity> printer = new PrintUDDI<BusinessEntity>();
                        if (TckCommon.isDebug()) {
                                System.out.println(printer.print(businessDetail.getBusinessEntity().get(0)));
                        }
                        AtomicReference<String> msg = new AtomicReference<String>();
                        boolean b = ds.verifySignedUddiEntity(businessDetail.getBusinessEntity().get(0), msg);
                        Assert.assertTrue(msg.get(), b);
                        Assert.assertTrue(msg.get() == null || msg.get().length() == 0);

                } catch (Exception ex) {
                        logger.error("unExpected failure: ", ex);
                        Assert.fail("unexpected failure");
                } finally {
                        DeleteBusinesses(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), authInfoJoe, publicationJoe);
                }
        }

        org.apache.juddi.v3.client.cryptor.DigSigUtil GetDigSig() throws CertificateException {
                org.apache.juddi.v3.client.cryptor.DigSigUtil ds = new DigSigUtil(TckPublisher.getProperties());
                /*
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, "./src/test/resources/keystore.jks");
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "JKS");
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "Test");
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "Test");
                ds.put(DigSigUtil.TRUSTSTORE_FILE, "./src/test/resources/truststore.jks");
                ds.put(DigSigUtil.TRUSTSTORE_FILETYPE, "JKS");
                ds.put(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, "Test");*/
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "true");
                return ds;
        }
}
