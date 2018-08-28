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

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
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
import org.junit.Test;
import org.uddi.api_v3.*;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * This test class provides test cases of items discovered or reported through
 * the Juddi JIRA issue tracker The idea is that these tests apply to any UDDI
 * registry and that the tests are either implied or derived requirements
 * defined in the UDDI spec that were missed in other test cases for Juddi.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_141_JIRAIntegrationTest {

        public UDDI_141_JIRAIntegrationTest() throws RemoteException {
        }
        private static Log logger = LogFactory.getLog(UDDI_141_JIRAIntegrationTest.class);
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
        static TckTModel tckTmodelUddi = null;
        static TckBusiness tckBusinessSam = null;
        protected static String authInfoJoe = null;
        protected static String authInfoSam = null;
        protected static String authInfoUddi = null;
        private static UDDIClient manager;
        static final String str256 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        static final String str255 = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        static final String strkey256 = "uddi:11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        static final String strkey256_1 = "uddi:org.apache:omething.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.something.somethi.com";
        static final String str26 = "11111111111111111111111111";
        static final String str27 = "111111111111111111111111110";
        static final String str10 = "0123456789";
        static final String str11 = "01234567890";
        static final String str80 = "01234567890123456789012345678901234567890123456789012345678901234567890123456789";
        static final String str81 = "012345678901234567890123456789012345678901234567890123456789012345678901234567891";
        static final String TRANS = "The transaction has been rolled back";
        static final String str4096 = "12345670000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001234567000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000123456700000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000012345670000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001234567000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000123456700000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000012345670000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001234567000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000XXXXXXXX";
        static final String str4097 = "12345670000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001234567000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000123456700000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000012345670000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001234567000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000123456700000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000012345670000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001234567000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000XXXXXXXXZ";
        static final String str51 = "111111111111111111111111111111111111111111111111111";
        static final String str50 = "11111111111111111111111111111111111111111111111111";
        static final String MISSING_RESOURCE = "Can't find resource for bundle";

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
                logger.info("UDDI_141_JIRAIntegrationTest");
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
                        authInfoUddi = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
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

                        transport = manager.getTransport("uddiv3");

                        UDDIPublicationPortType uddiPublishService = transport.getUDDIPublishService();
                        UDDIInquiryPortType uddiInquiryService = transport.getUDDIInquiryService();

                        tckTmodelUddi = new TckTModel(uddiPublishService, uddiInquiryService);
                        tckTmodelUddi.saveUDDIPublisherTmodel(authInfoUddi);

                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) uddiPublishService, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                                TckSecurity.setCredentials((BindingProvider) uddiInquiryService, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());

                        }

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
                JUDDI_300_MultiNodeIntegrationTest.testSetupReplicationConfig();
        }

        static void HandleException(Exception ex) {
                logger.error("Error caught of type " + ex.getClass().getCanonicalName(),ex);
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
        public void JUDDI_JIRA_571_Part1_Test() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                //add a business
                //add a business with lang defined
                //find business with lang defined, expecting one result
                //find business without lang defined, expecting 2 results
                List<String> businesskeysToDelete = new ArrayList<String>();
                String failuremsg = "";
                logger.info("JUDDI_JIRA_571_Part1_Test");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_JIRA_571_Part1_Test no lang");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);

                be = new BusinessEntity();
                n = new Name();
                n.setValue("JUDDI_JIRA_571_Part1_Test with lang");
                n.setLang("en");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        businesskeysToDelete.add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        businesskeysToDelete.add(saveBusiness.getBusinessEntity().get(1).getBusinessKey());

                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }

                int found1 = 0;
                FindBusiness fb = new FindBusiness();
                fb.setAuthInfo(authInfoJoe);
                n = new Name();
                n.setValue("%");
                fb.getName().add(n);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                try {
                        BusinessList findBusiness = inquiryJoe.findBusiness(fb);
                        if (findBusiness.getBusinessInfos() != null) {
                                for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                                        if (businesskeysToDelete.contains(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())) {
                                                found1++;
                                        }
                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                if (found1 != 2) {
                        failuremsg += "No lang defined, " + found1 + " records found instead of 2";
                }

                found1 = 0;
                fb = new FindBusiness();
                fb.setAuthInfo(authInfoJoe);
                n = new Name();
                n.setLang("en");
                n.setValue("%");
                fb.getName().add(n);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                try {
                        BusinessList findBusiness = inquiryJoe.findBusiness(fb);
                        if (findBusiness.getBusinessInfos() != null) {
                                for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                                        if (businesskeysToDelete.contains(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())) {
                                                found1++;
                                        }
                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                if (found1 != 1) {
                        failuremsg += "Lang defined, " + found1 + " records found instead of 1";
                }

                DeleteBusinesses(businesskeysToDelete, authInfoJoe, publicationJoe);
                if (failuremsg.length() > 0) {
                        Assert.fail(failuremsg);
                }
                logger.info("Pass");

        }

        @Test
        public void JUDDI_JIRA_571_Part2_Test() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                //add a service
                //add a service with lang defined
                //find service with lang defined, expecting one result
                //find service without lang defined, expecting 2 results

                List<String> businesskeysToDelete = new ArrayList<String>();
                List<String> targetServiceKeys = new ArrayList<String>();
                String failuremsg = "";
                logger.info("JUDDI_JIRA_571_Part2_Test");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_JIRA_571_Part2_Test no lang");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);

                BusinessService bs = new BusinessService();
                n = new Name();
                n.setValue("Service1 No Lang");
                bs.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(bs);

                bs = new BusinessService();
                n = new Name();
                n.setValue("Service2 Lang");
                n.setLang("en");
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        businesskeysToDelete.add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        targetServiceKeys.add(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getServiceKey());
                        targetServiceKeys.add(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(1).getServiceKey());

                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }

                int found1 = 0;
                FindService fb = new FindService();
                fb.setAuthInfo(authInfoJoe);
                n = new Name();
                n.setValue("%");
                fb.getName().add(n);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                try {
                        ServiceList findBusiness = inquiryJoe.findService(fb);
                        if (findBusiness.getServiceInfos() != null) {
                                for (int i = 0; i < findBusiness.getServiceInfos().getServiceInfo().size(); i++) {
                                        if (targetServiceKeys.contains(findBusiness.getServiceInfos().getServiceInfo().get(i).getServiceKey())) {
                                                found1++;
                                        }
                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                if (found1 != 2) {
                        failuremsg += "No lang defined, " + found1 + " records found instead of 2";
                }

                found1 = 0;
                fb = new FindService();
                fb.setAuthInfo(authInfoJoe);
                n = new Name();
                n.setLang("en");
                n.setValue("%");
                fb.getName().add(n);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                try {
                        ServiceList findBusiness = inquiryJoe.findService(fb);
                        if (findBusiness.getServiceInfos() != null) {
                                for (int i = 0; i < findBusiness.getServiceInfos().getServiceInfo().size(); i++) {
                                        if (businesskeysToDelete.contains(findBusiness.getServiceInfos().getServiceInfo().get(i).getBusinessKey())) {
                                                found1++;
                                        }
                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                if (found1 != 1) {
                        failuremsg += "Lang defined, " + found1 + " records found instead of 1";
                }

                DeleteBusinesses(businesskeysToDelete, authInfoJoe, publicationJoe);
                if (failuremsg.length() > 0) {
                        Assert.fail(failuremsg);
                }
                logger.info("Pass");

        }

        @Test
        public void JUDDI_571_Part3_Test() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                //add a tmodel
                //add a tmodel with lang defined
                //find tmodel with lang defined, expecting one result
                //find tmodel without lang defined, expecting 2 results

                List<String> businesskeysToDelete = new ArrayList<String>();

                String failuremsg = "";
                logger.info("JUDDI_571_Part3_Test");
                SaveTModel sb = new SaveTModel();
                sb.setAuthInfo(authInfoJoe);
                TModel be = new TModel();
                Name n = new Name();
                n.setValue("JUDDI_571_Part3_Test no lang");
                be.setName(n);
                sb.getTModel().add(be);

                be = new TModel();
                n = new Name();
                n.setValue("JUDDI_571_Part3_Test lang");
                n.setLang("en");
                be.setName(n);
                sb.getTModel().add(be);

                try {
                        TModelDetail saveTModel = publicationJoe.saveTModel(sb);
                        businesskeysToDelete.add(saveTModel.getTModel().get(0).getTModelKey());
                        businesskeysToDelete.add(saveTModel.getTModel().get(1).getTModelKey());
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }

                int found1 = 0;
                FindTModel fb = new FindTModel();
                fb.setAuthInfo(authInfoJoe);
                n = new Name();
                n.setValue("%JUDDI_571_Part3_Test%");
                fb.setName(n);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                try {
                        TModelList findTModel = inquiryJoe.findTModel(fb);
                        if (findTModel.getTModelInfos() != null) {
                                for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                                        if (businesskeysToDelete.contains(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey())) {
                                                found1++;
                                        }
                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                if (found1 != 2) {
                        failuremsg += "No lang defined, " + found1 + " records found instead of 2";
                }

                found1 = 0;
                fb = new FindTModel();
                fb.setAuthInfo(authInfoJoe);
                n = new Name();
                n.setLang("en");
                n.setValue("%JUDDI_571_Part3_Test%");
                fb.setName(n);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                try {
                        TModelList findTModel = inquiryJoe.findTModel(fb);
                        if (findTModel.getTModelInfos() != null) {
                                for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                                        if (businesskeysToDelete.contains(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey())) {
                                                found1++;
                                        }
                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                if (found1 != 1) {
                        failuremsg += "Lang defined, " + found1 + " records found instead of 1";
                }

                DeleteTModels(businesskeysToDelete);
                if (failuremsg.length() > 0) {
                        Assert.fail(failuremsg);
                }
                logger.info("Pass");

        }

        @Test
        public void JUDDI_574() {
                Assume.assumeTrue(TckPublisher.isEnabled());
                //make a test model with a lang

                //search for it by name
                //confirm that the lang is present
                List<String> businesskeysToDelete = new ArrayList<String>();

                String failuremsg = "";
                logger.info("JUDDI_574");
                SaveTModel sb = new SaveTModel();
                sb.setAuthInfo(authInfoJoe);
                TModel be = new TModel();
                Name n = new Name();
                n.setValue("JUDDI_574");
                n.setLang("en");
                be.setName(n);
                sb.getTModel().add(be);

                try {
                        TModelDetail saveTModel = publicationJoe.saveTModel(sb);
                        businesskeysToDelete.add(saveTModel.getTModel().get(0).getTModelKey());
                        logger.info("tmodel created with key " + saveTModel.getTModel().get(0).getTModelKey());
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                int found1 = 0;
                FindTModel fb = new FindTModel();
                fb.setAuthInfo(authInfoJoe);
                n = new Name();
                n.setValue("JUDDI_574");
                fb.setName(n);
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                try {
                        TModelList findTModel = inquiryJoe.findTModel(fb);
                        if (findTModel.getTModelInfos() != null) {
                                for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                                        if (businesskeysToDelete.contains(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey())) {
                                                found1++;
                                                if (findTModel.getTModelInfos().getTModelInfo().get(i).getName() == null
                                                        || findTModel.getTModelInfos().getTModelInfo().get(i).getName().getLang() == null
                                                        || findTModel.getTModelInfos().getTModelInfo().get(i).getName().getLang().length() == 0) {
                                                        failuremsg += "Tmodel key " + findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey()
                                                                + " has a null or empty lang";
                                                }
                                        }

                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                        Assert.fail("unexpected failure");
                }
                if (found1 != 1) {
                        failuremsg += "found " + found1 + " records found instead of 1";
                }

                DeleteTModels(businesskeysToDelete);
                if (failuremsg.length() > 0) {
                        Assert.fail(failuremsg);
                }
                logger.info("Pass");

        }

        /**
         * sets up a compelte publisher assertion
         *
         * @throws Exception
         */
        @Test
        public void JUDDI_590() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                //create two businesses
                logger.info("JUDDI_590");

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_590 Joe");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                String joeBiz = null;
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        joeBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
                        //DeleteBusiness db = new DeleteBusiness();
                        //db.setAuthInfo(authInfoJoe);
                        //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        //publicationJoe.deleteBusiness(db);
                        //Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                }

                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                be = new BusinessEntity();
                n = new Name();
                n.setValue("JUDDI_590 Sam");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                String samBiz = null;
                try {
                        BusinessDetail saveBusiness = publicationSam.saveBusiness(sb);
                        samBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
                        //DeleteBusiness db = new DeleteBusiness();
                        //db.setAuthInfo(authInfoJoe);
                        //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        //publicationJoe.deleteBusiness(db);
                        //Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                }

                //create an assertion on one end
                AddPublisherAssertions apa = new AddPublisherAssertions();
                apa.setAuthInfo(authInfoJoe);
                apa.getPublisherAssertion().add(new PublisherAssertion());
                apa.getPublisherAssertion().get(0).setFromKey(joeBiz);
                apa.getPublisherAssertion().get(0).setToKey(samBiz);
                KeyedReference kr = new KeyedReference();
                kr.setKeyName("Subsidiary");
                kr.setKeyValue("parent-child");
                kr.setTModelKey("uddi:uddi.org:relationships");
                apa.getPublisherAssertion().get(0).setKeyedReference(kr);
                publicationJoe.addPublisherAssertions(apa);
                //check get status is not null from 1 and from 2
                boolean ok = true;
                String msg = "";
                try {
                        List<AssertionStatusItem> assertionStatusReport = publicationJoe.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                        if (assertionStatusReport.isEmpty()) {
                                msg = "Stage1: no result returned, expected at least 1";
                                ok = false;
                        }
                        for (int i = 0; i < assertionStatusReport.size(); i++) {
                                JAXB.marshal(assertionStatusReport.get(i), System.out);
                                if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                                        if (!assertionStatusReport.get(i).getCompletionStatus().equals(CompletionStatus.STATUS_TO_KEY_INCOMPLETE)) {
                                                ok = false;
                                                msg = "Stage1: status type mismatch";
                                        }
                                }
                        }
                } catch (Exception ex) {
                        ok = false;
                        ex.printStackTrace();
                }

                //aprove the assertion from sam
                apa = new AddPublisherAssertions();
                apa.setAuthInfo(authInfoSam);
                apa.getPublisherAssertion().add(new PublisherAssertion());
                apa.getPublisherAssertion().get(0).setFromKey(joeBiz);
                apa.getPublisherAssertion().get(0).setToKey(samBiz);
                kr = new KeyedReference();
                kr.setKeyName("Subsidiary");
                kr.setKeyValue("parent-child");
                kr.setTModelKey("uddi:uddi.org:relationships");
                apa.getPublisherAssertion().get(0).setKeyedReference(kr);
                publicationSam.addPublisherAssertions(apa);
                try {
                        List<AssertionStatusItem> assertionStatusReport = publicationJoe.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_COMPLETE);
                        if (assertionStatusReport.isEmpty()) {
                                msg = "Stage2: no result returned, expected at least 1";
                                ok = false;
                        }
                        for (int i = 0; i < assertionStatusReport.size(); i++) {
                                JAXB.marshal(assertionStatusReport.get(i), System.out);
                                if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                                        if (!assertionStatusReport.get(i).getCompletionStatus().equals(CompletionStatus.STATUS_COMPLETE)) {
                                                ok = false;
                                                msg = "Stage2: status type mismatch";
                                        }
                                }
                        }
                        //test to see what the status actually is
                        if (!ok) {
                                assertionStatusReport = publicationJoe.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);
                                for (int i = 0; i < assertionStatusReport.size(); i++) {
                                        JAXB.marshal(assertionStatusReport.get(i), System.out);
                                        if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                                                msg = "Stage3: status is " + assertionStatusReport.get(i).getCompletionStatus().toString() + " instead of complete";
                                        }
                                }

                                assertionStatusReport = publicationJoe.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                                for (int i = 0; i < assertionStatusReport.size(); i++) {
                                        JAXB.marshal(assertionStatusReport.get(i), System.out);
                                        if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                                                msg = "Stage3: status is " + assertionStatusReport.get(i).getCompletionStatus().toString() + " instead of complete";
                                        }
                                }
                                assertionStatusReport = publicationJoe.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_BOTH_INCOMPLETE);
                                for (int i = 0; i < assertionStatusReport.size(); i++) {
                                        JAXB.marshal(assertionStatusReport.get(i), System.out);
                                        if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                                                msg = "Stage3: status is " + assertionStatusReport.get(i).getCompletionStatus().toString() + " instead of complete";
                                        }
                                }

                        }
                } catch (Exception ex) {
                        ok = false;
                        ex.printStackTrace();
                }
                List<String> biz = new ArrayList<String>();
                biz.add(samBiz);
                DeleteBusinesses(biz, authInfoSam, publicationSam);

                biz = new ArrayList<String>();
                biz.add(joeBiz);
                DeleteBusinesses(biz, authInfoJoe, publicationJoe);
                Assert.assertTrue(msg, ok);

        }

        /**
         * setups up a partial relationship and confirms its existence
         *
         * @throws Exception
         */
        @Test
        public void JUDDI_590_1() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                //create two businesses
                logger.info("JUDDI_590_1");

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("JUDDI_590 Joe");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                String joeBiz = null;
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        joeBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
                        //DeleteBusiness db = new DeleteBusiness();
                        //db.setAuthInfo(authInfoJoe);
                        //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        //publicationJoe.deleteBusiness(db);
                        //Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                }

                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                be = new BusinessEntity();
                n = new Name();
                n.setValue("JUDDI_590 Sam");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                String samBiz = null;
                try {
                        BusinessDetail saveBusiness = publicationSam.saveBusiness(sb);
                        samBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
                        //DeleteBusiness db = new DeleteBusiness();
                        //db.setAuthInfo(authInfoJoe);
                        //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        //publicationJoe.deleteBusiness(db);
                        //Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                }

                //create an assertion on one end
                AddPublisherAssertions apa = new AddPublisherAssertions();
                apa.setAuthInfo(authInfoJoe);
                apa.getPublisherAssertion().add(new PublisherAssertion());
                apa.getPublisherAssertion().get(0).setFromKey(joeBiz);
                apa.getPublisherAssertion().get(0).setToKey(samBiz);
                KeyedReference kr = new KeyedReference();
                kr.setKeyName("Subsidiary");
                kr.setKeyValue("parent-child");
                kr.setTModelKey("uddi:uddi.org:relationships");
                apa.getPublisherAssertion().get(0).setKeyedReference(kr);
                publicationJoe.addPublisherAssertions(apa);
                //ok so joe has asserted that he knows sam

                //check get status is not null from 1 and from 2
                boolean ok = true;
                String msg = "";
                try {
                        List<AssertionStatusItem> assertionStatusReport = publicationJoe.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                        if (assertionStatusReport.isEmpty()) {
                                msg = "Stage1: no result returned, expected at least 1";
                                ok = false;
                        }
                        for (int i = 0; i < assertionStatusReport.size(); i++) {
                                if (TckCommon.isDebug()) {
                                        JAXB.marshal(assertionStatusReport.get(i), System.out);
                                }
                                if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                                        if (!assertionStatusReport.get(i).getCompletionStatus().equals(CompletionStatus.STATUS_TO_KEY_INCOMPLETE)) {
                                                ok = false;
                                                msg = "Stage1: status type mismatch";
                                        }
                                }
                        }
                } catch (Exception ex) {
                        ok = false;
                        ex.printStackTrace();
                }
                //check that sam got the message

                try {
                        List<AssertionStatusItem> assertionStatusReport = publicationSam.getAssertionStatusReport(authInfoSam, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                        if (assertionStatusReport.isEmpty()) {
                                msg = "Stage2: no result returned, expected at least 1";
                                ok = false;
                        }
                        for (int i = 0; i < assertionStatusReport.size(); i++) {
                                if (TckCommon.isDebug()) {
                                        JAXB.marshal(assertionStatusReport.get(i), System.out);
                                }
                                if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                                        if (!assertionStatusReport.get(i).getCompletionStatus().equals(CompletionStatus.STATUS_TO_KEY_INCOMPLETE)) {
                                                ok = false;
                                                msg = "Stage2: status type mismatch";
                                        }
                                }
                        }
                } catch (Exception ex) {
                        ok = false;
                        ex.printStackTrace();
                }

                List<String> biz = new ArrayList<String>();
                biz.add(samBiz);
                DeleteBusinesses(biz, authInfoSam, publicationSam);

                biz = new ArrayList<String>();
                biz.add(joeBiz);
                DeleteBusinesses(biz, authInfoJoe, publicationJoe);
                Assert.assertTrue(msg, ok);

        }
        UDDISubscriptionListenerImpl impl = new UDDISubscriptionListenerImpl();

        /**
         * testing upper case subscription callbacks
         *
         * @throws Exception
         */
        @Test
        public void JIRA_597() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_597");

                int port = 7000;
                String hostname = TckPublisher.getProperties().getProperty("bindaddress");
                if (hostname == null) {
                        hostname = InetAddress.getLocalHost().getHostName();
                }

                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                UDDISubscriptionListenerImpl.notifcationMap.clear();
                UDDISubscriptionListenerImpl.notificationCount = 0;
                Endpoint ep = null;
                boolean ok = false;
                do {
                        try {
                                logger.info("Attempting to bring up endpoint at " + "http://" + hostname + ":" + port + "/UDDI_CALLBACK");
                                ep = Endpoint.publish("http://" + hostname + ":" + port + "/UDDI_CALLBACK", impl);
                                ok = true;
                        } catch (Exception ex) {
                                logger.warn("Trouble starting endpoint: " + ex.getMessage());
                                port++;
                        }
                } while (!ok);
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's callback business");
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name());
                bs.getName().get(0).setValue("Joe's callback service");
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue("http://" + hostname + ":" + port + "/UDDI_CALLBACK");
                bt.getAccessPoint().setUseType("endPoint");
                //Added per Kurt
                TModelInstanceInfo instanceInfo = new TModelInstanceInfo();
                instanceInfo.setTModelKey("uddi:uddi.org:transport:http");
                bt.setTModelInstanceDetails(new TModelInstanceDetails());
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(instanceInfo);

                bs.getBindingTemplates().getBindingTemplate().add(bt);

                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);

                List<String> deleteme = new ArrayList<String>();
                deleteme.add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                //ok Joe's callback is setup

                //Setup a business to subscribe to
                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Sam's business");
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness1 = publicationSam.saveBusiness(sb);

                //ok Joe now needs to subscribe for Sam's business
                Holder<List<Subscription>> list = new Holder<List<Subscription>>();
                list.value = new ArrayList<Subscription>();
                Subscription s = new Subscription();
                s.setBindingKey(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey());
                s.setSubscriptionFilter(new SubscriptionFilter());
                s.getSubscriptionFilter().setGetBusinessDetail(new GetBusinessDetail());
                s.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().add(saveBusiness1.getBusinessEntity().get(0).getBusinessKey());
                DatatypeFactory df = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());
                gcal.add(Calendar.HOUR, 1);
                s.setExpiresAfter(df.newXMLGregorianCalendar(gcal));

                s.setNotificationInterval(df.newDuration(5000));
                list.value.add(s);
                subscriptionJoe.saveSubscription(authInfoJoe, list);

                //ok have sam change his business around.
                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                be = saveBusiness1.getBusinessEntity().get(0);
                be.getName().get(0).setLang("en");
                sb.getBusinessEntity().add(be);
                publicationSam.saveBusiness(sb);
                int maxwait = 30000;
                logger.info("waiting for callbacks");
                while (maxwait > 0) {
                        if (UDDISubscriptionListenerImpl.notifcationMap.size() > 0) {
                                break;
                        }
                        Thread.sleep(1000);
                        maxwait = maxwait - 1000;
                }
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                this.DeleteBusinesses(deleteme, authInfoJoe, publicationJoe);
                deleteme.clear();
                deleteme.add(saveBusiness1.getBusinessEntity().get(0).getBusinessKey());
                this.DeleteBusinesses(deleteme, authInfoSam, publicationSam);
                ep.stop();
                if (UDDISubscriptionListenerImpl.notifcationMap.isEmpty()) {
                        Assert.fail("no callbacks were recieved.");
                }

        }

        /**
         * testing callbacks with undefined transport type with a uppercase path
         * this also tests the case of one user subscribing to a specific entity
         * via GetBusinessDetail subscription filter
         *
         * @throws Exception
         */
        @Test
        public void JIRA_596() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_596");
                int port = 9000;

                String hostname = TckPublisher.getProperties().getProperty("bindaddress");
                if (hostname == null) {
                        hostname = InetAddress.getLocalHost().getHostName();
                }

                // String localhostname = "localhost";//java.net.InetAddress.getLocalHost().getHostName();
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                //UDDISubscriptionListenerImpl impl = new UDDISubscriptionListenerImpl();
                UDDISubscriptionListenerImpl.notifcationMap.clear();
                UDDISubscriptionListenerImpl.notificationCount = 0;

                Endpoint ep = null;
                boolean ok = false;
                do {
                        try {
                                ep = Endpoint.publish("http://" + hostname + ":" + port + "/UDDI_CALLBACK", impl);
                                ok = true;
                        } catch (Exception ex) {
                                port++;
                        }
                } while (!ok);
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's callback business");
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name());
                bs.getName().get(0).setValue("Joe's callback service");
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue("http://" + hostname + ":" + port + "/UDDI_CALLBACK");
                bt.getAccessPoint().setUseType("endPoint");
                //obmitted as part of the jira test case
                /*TModelInstanceInfo instanceInfo = new TModelInstanceInfo();
                 instanceInfo.setTModelKey("uddi:uddi.org:transport:http");
                 bt.setTModelInstanceDetails(new TModelInstanceDetails());
                 bt.getTModelInstanceDetails().getTModelInstanceInfo().add(instanceInfo);
                 */
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);
                logger.info("setting up joe's callback business");
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);

                List<String> deleteme = new ArrayList<String>();
                deleteme.add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                //ok Joe's callback is setup

                //Setup a business to subscribe to
                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Sam's business");
                sb.getBusinessEntity().add(be);
                logger.info("saving sam's business");
                BusinessDetail saveBusiness1 = publicationSam.saveBusiness(sb);

                //ok Joe now needs to subscribe for Sam's business
                Holder<List<Subscription>> list = new Holder<List<Subscription>>();
                list.value = new ArrayList<Subscription>();
                Subscription s = new Subscription();
                s.setBindingKey(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey());
                s.setSubscriptionFilter(new SubscriptionFilter());
                s.getSubscriptionFilter().setGetBusinessDetail(new GetBusinessDetail());
                s.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().add(saveBusiness1.getBusinessEntity().get(0).getBusinessKey());
                DatatypeFactory df = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());
                gcal.add(Calendar.HOUR, 1);
                s.setExpiresAfter(df.newXMLGregorianCalendar(gcal));

                s.setNotificationInterval(df.newDuration(5000));
                list.value.add(s);
                logger.info("subscribing joe's to updates for sam's business");
                subscriptionJoe.saveSubscription(authInfoJoe, list);

                //ok have sam change his business around.
                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                be = saveBusiness1.getBusinessEntity().get(0);
                be.getName().get(0).setLang("en");
                sb.getBusinessEntity().add(be);
                logger.info("altering sam's business");
                publicationSam.saveBusiness(sb);
                logger.info("Waiting...");
                int maxwait = 30000;
                while (maxwait > 0) {
                        if (UDDISubscriptionListenerImpl.notifcationMap.size() > 0) {
                                break;
                        }
                        Thread.sleep(1000);
                        maxwait = maxwait - 1000;
                }
                TckCommon.removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                DeleteBusinesses(deleteme, authInfoJoe, publicationJoe);
                deleteme.clear();
                deleteme.add(saveBusiness1.getBusinessEntity().get(0).getBusinessKey());
                DeleteBusinesses(deleteme, authInfoSam, publicationSam);
                ep.stop();
                if (UDDISubscriptionListenerImpl.notifcationMap.isEmpty()) {
                        logger.error("no callbacks were recieved");
                        Assert.fail("no callbacks were recieved.");
                }
                logger.info("callback response was " + UDDISubscriptionListenerImpl.notifcationMap.get(0));
                logger.info("PASS");

        }

        //once more without any relationship
        //binding template tmodel instance info
        @Test
        public void JIRA_575_BT() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_BT");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's JIRA_575_BT business");
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name());
                bs.getName().get(0).setValue("Joe's JIRA_575_BT service");
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue("http://JIRA_575_BT/UDDI_CALLBACK");
                bt.getAccessPoint().setUseType("endPoint");

                TModelInstanceInfo instanceInfo = new TModelInstanceInfo();
                instanceInfo.setTModelKey(madeupTmodel);
                bt.setTModelInstanceDetails(new TModelInstanceDetails());
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(instanceInfo);

                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);
                logger.info("setting up joe's callback business");
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }

        }

        @Test
        public void JIRA_575_KR_Biz() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_KR_Biz");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's JIRA_575_KR_Biz business");
                //be.setBusinessServices(new BusinessServices());
                be.setCategoryBag(new CategoryBag());
                be.getCategoryBag().getKeyedReference().add(new KeyedReference(madeupTmodel, "name", "val"));
                sb.getBusinessEntity().add(be);

                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }

        }

        @Test
        public void JIRA_575_IDENT_Biz() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_IDENT_Biz");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's JIRA_575_IDENT_Biz business");
                //be.setBusinessServices(new BusinessServices());
                be.setIdentifierBag(new IdentifierBag());
                be.getIdentifierBag().getKeyedReference().add(new KeyedReference(madeupTmodel, "name", "val"));
                sb.getBusinessEntity().add(be);

                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }

        }

        @Test
        public void JIRA_575_KR_TMODEL() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_KR_TMODEL");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name("JIRA_575_KR_TMODEL", null));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(madeupTmodel, "name", "val"));
                stm.getTModel().add(tm);
                try {
                        publicationJoe.saveTModel(stm);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }
        }

        @Test
        public void JIRA_575_KRGRP_TMODEL() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_KRGRP_TMODEL");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name("JIRA_575_KRGRP_TMODEL", null));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReferenceGroup().add(new KeyedReferenceGroup());
                tm.getCategoryBag().getKeyedReferenceGroup().get(0).setTModelKey(madeupTmodel);
                stm.getTModel().add(tm);
                try {
                        publicationJoe.saveTModel(stm);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }
        }

        @Test
        public void JIRA_575_KRGRP_Biz() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_KRGRP_Biz");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's JIRA_575_KRGRP_Biz business");
                // be.setBusinessServices(new BusinessServices());
                be.setCategoryBag(new CategoryBag());
                //be.getCategoryBag().getKeyedReference().add(new KeyedReference(madeupTmodel, "name", "val"));
                be.getCategoryBag().getKeyedReferenceGroup().add(new KeyedReferenceGroup());
                be.getCategoryBag().getKeyedReferenceGroup().get(0).setTModelKey(madeupTmodel);

                sb.getBusinessEntity().add(be);

                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }

        }

        @Test
        public void JIRA_575_KRGRP_PA() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_KRGRP_PA");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                tckTmodelUddi.saveTmodels(authInfoUddi);
                tckTmodelUddi.saveUDDIPublisherTmodel(authInfoUddi);
                
                tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);

                tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);

                AddPublisherAssertions apa = new AddPublisherAssertions();
                apa.setAuthInfo(authInfoJoe);
                PublisherAssertion pa = new PublisherAssertion();
                pa.setKeyedReference(new KeyedReference(madeupTmodel, "name", "val"));
                pa.setFromKey(TckBusiness.JOE_BUSINESS_KEY);
                pa.setToKey(TckBusiness.SAM_BUSINESS_KEY);
                apa.getPublisherAssertion().add(pa);
                try {
                        publicationJoe.addPublisherAssertions(apa);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                } finally {
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }

        @Test
        public void JIRA_575_SVC_KR() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_SVC_KR");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's JIRA_575_SVC_KR business");
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name());
                bs.getName().get(0).setValue("Joe's JIRA_575_SVC_KR service");
                //bs.setBindingTemplates(new BindingTemplates());
                bs.setCategoryBag(new CategoryBag());
                bs.getCategoryBag().getKeyedReference().add(new KeyedReference(madeupTmodel, "name", "val"));

                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);

                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }
        }

        @Test
        public void JIRA_575_SVC_KRGRP() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("JIRA_575_SVC_KRGRP");
                String madeupTmodel = "uddi" + UUID.randomUUID().toString();
                GetTModelDetail gtm = new GetTModelDetail();
                gtm.setAuthInfo(authInfoJoe);
                gtm.getTModelKey().add(madeupTmodel);
                TModelDetail tModelDetail = null;
                try {
                        tModelDetail = inquiryJoe.getTModelDetail(gtm);
                } catch (Exception ex) {
                }
                Assume.assumeTrue(tModelDetail == null);

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name());
                be.getName().get(0).setValue("Joe's JIRA_575_SVC_KRGRP business");
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name());
                bs.getName().get(0).setValue("Joe's JIRA_575_SVC_KRGRP service");
                // bs.setBindingTemplates(new BindingTemplates());
                bs.setCategoryBag(new CategoryBag());
                bs.getCategoryBag().getKeyedReferenceGroup().add(new KeyedReferenceGroup());
                bs.getCategoryBag().getKeyedReferenceGroup().get(0).setTModelKey(madeupTmodel);

                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);

                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.error(ex.getMessage());
                }
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

        private void DeleteTModels(List<String> businesskeysToDelete) {

                //cleanup
                try {
                        DeleteTModel db = new DeleteTModel();
                        db.setAuthInfo(authInfoJoe);
                        db.getTModelKey().addAll(businesskeysToDelete);
                        publicationJoe.deleteTModel(db);
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        /**
         * Each addressLine element MAY be adorned with two optional descriptive
         * attributes, keyName and keyValue. Both attributes MUST be present in
         * each address line if a tModelKey is specified in the address
         * structure. When no tModelKey is provided for the address structure,
         * the keyName and keyValue attributes have no defined meaning.
         * http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc515847027
         */
        @Test
        public void JUDDI_849_AddressLineAttributeTest() throws Exception {
                BusinessEntity be = new BusinessEntity();
                be.setContacts(new Contacts());
                Contact c = new Contact();
                c.getPersonName().add(new PersonName("bob", null));
                Address addr = new Address();
                addr.setTModelKey("uddi:tmodelkey:address");
                addr.getAddressLine().add(new AddressLine(null, null, "1313 mockingbird lane"));
                c.getAddress().add(addr);
                be.getContacts().getContact().add(c);
                be.getName().add(new Name("test JUDDI849", null));

                SaveBusiness sb = new SaveBusiness();
                sb.getBusinessEntity().add(be);
                sb.setAuthInfo(authInfoJoe);
                try {
                        publicationJoe.saveBusiness(sb);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure: " + ex.getMessage());
                        logger.debug("Expected failure: " + ex.getMessage(), ex);
                }

        }

}
