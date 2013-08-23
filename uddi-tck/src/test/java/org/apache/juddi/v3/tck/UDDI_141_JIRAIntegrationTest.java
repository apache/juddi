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

import java.net.Inet4Address;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.soap.SOAPFault;
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

    private static Log logger = LogFactory.getLog(UDDI_141_JIRAIntegrationTest.class);
    static UDDISecurityPortType security = null;
    static UDDISubscriptionPortType subscription = null;
    static UDDIInquiryPortType inquiry = null;
    static UDDIPublicationPortType publication = null;
    protected static String authInfoJoe = null;
    protected static String authInfoSam = null;
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
        manager.stop();
    }

    @BeforeClass
    public static void startManager() throws ConfigurationException {
        manager = new UDDIClient();
        manager.start();

        logger.debug("Getting auth tokens..");
        try {
            Transport transport = manager.getTransport();
            security = transport.getUDDISecurityService();
            subscription = transport.getUDDISubscriptionService();
            authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
            authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
            Assert.assertNotNull(authInfoJoe);
            Assert.assertNotNull(authInfoSam);

            publication = transport.getUDDIPublishService();
            inquiry = transport.getUDDIInquiryService();


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Assert.fail("Could not obtain authInfo token.");
        }
    }

    static void HandleException(Exception ex) {
        System.err.println("Error caught of type " + ex.getClass().getCanonicalName());
        ex.printStackTrace();
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
        //add a business
        //add a business with lang defined
        //find business with lang defined, expecting one result
        //find business without lang defined, expecting 2 results
        List<String> businesskeysToDelete = new ArrayList<String>();
        String failuremsg = "";
        System.out.println("JUDDI_JIRA_571_Part1_Test");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
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
            BusinessList findBusiness = inquiry.findBusiness(fb);
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
            BusinessList findBusiness = inquiry.findBusiness(fb);
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


        DeleteBusinesses(businesskeysToDelete);
        if (failuremsg.length() > 0) {
            Assert.fail(failuremsg);
        }
        System.out.println("Pass");

    }

    @Test
    public void JUDDI_JIRA_571_Part2_Test() {
        //add a service
        //add a service with lang defined
        //find service with lang defined, expecting one result
        //find service without lang defined, expecting 2 results



        List<String> businesskeysToDelete = new ArrayList<String>();
        List<String> targetServiceKeys = new ArrayList<String>();
        String failuremsg = "";
        System.out.println("JUDDI_JIRA_571_Part2_Test");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
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
            ServiceList findBusiness = inquiry.findService(fb);
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
            ServiceList findBusiness = inquiry.findService(fb);
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


        DeleteBusinesses(businesskeysToDelete);
        if (failuremsg.length() > 0) {
            Assert.fail(failuremsg);
        }
        System.out.println("Pass");

    }

    @Test
    public void JUDDI_571_Part3_Test() {
        //add a tmodel
        //add a tmodel with lang defined
        //find tmodel with lang defined, expecting one result
        //find tmodel without lang defined, expecting 2 results


        List<String> businesskeysToDelete = new ArrayList<String>();

        String failuremsg = "";
        System.out.println("JUDDI_571_Part3_Test");
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
            TModelDetail saveTModel = publication.saveTModel(sb);
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
            TModelList findTModel = inquiry.findTModel(fb);
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
            TModelList findTModel = inquiry.findTModel(fb);
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
        System.out.println("Pass");

    }

    @Test
    public void JUDDI_574() {
        //make a test model with a lang

        //search for it by name

        //confirm that the lang is present

        List<String> businesskeysToDelete = new ArrayList<String>();

        String failuremsg = "";
        System.out.println("JUDDI_574");
        SaveTModel sb = new SaveTModel();
        sb.setAuthInfo(authInfoJoe);
        TModel be = new TModel();
        Name n = new Name();
        n.setValue("JUDDI_574");
        n.setLang("en");
        be.setName(n);
        sb.getTModel().add(be);

        try {
            TModelDetail saveTModel = publication.saveTModel(sb);
            businesskeysToDelete.add(saveTModel.getTModel().get(0).getTModelKey());
            System.out.println("tmodel created with key " + saveTModel.getTModel().get(0).getTModelKey());
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
            TModelList findTModel = inquiry.findTModel(fb);
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
        System.out.println("Pass");

    }

    /**
     * sets up a compelte publisher assertion
     *
     * @throws Exception
     */
    @Test
    public void JUDDI_590() throws Exception {
        //create two businesses
        System.out.println("JUDDI_590");

        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("JUDDI_590 Joe");
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        String joeBiz = null;
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            joeBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
            //DeleteBusiness db = new DeleteBusiness();
            //db.setAuthInfo(authInfoJoe);
            //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            //publication.deleteBusiness(db);
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            samBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
            //DeleteBusiness db = new DeleteBusiness();
            //db.setAuthInfo(authInfoJoe);
            //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            //publication.deleteBusiness(db);
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
        publication.addPublisherAssertions(apa);
        //check get status is not null from 1 and from 2
        boolean ok = true;
        String msg = "";
        try {
            List<AssertionStatusItem> assertionStatusReport = publication.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
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
        publication.addPublisherAssertions(apa);
        try {
            List<AssertionStatusItem> assertionStatusReport = publication.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_COMPLETE);
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
                assertionStatusReport = publication.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);
                for (int i = 0; i < assertionStatusReport.size(); i++) {
                    JAXB.marshal(assertionStatusReport.get(i), System.out);
                    if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                        msg = "Stage3: status is " + assertionStatusReport.get(i).getCompletionStatus().toString() + " instead of complete";
                    }
                }

                assertionStatusReport = publication.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                for (int i = 0; i < assertionStatusReport.size(); i++) {
                    JAXB.marshal(assertionStatusReport.get(i), System.out);
                    if (assertionStatusReport.get(i).getToKey().equals(samBiz)) {
                        msg = "Stage3: status is " + assertionStatusReport.get(i).getCompletionStatus().toString() + " instead of complete";
                    }
                }
                assertionStatusReport = publication.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_BOTH_INCOMPLETE);
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
        DeleteBusinesses(biz, authInfoSam);

        biz = new ArrayList<String>();
        biz.add(joeBiz);
        DeleteBusinesses(biz, authInfoJoe);
        Assert.assertTrue(msg, ok);


    }

    /**
     * setups up a partial relationship and confirms its existence
     *
     * @throws Exception
     */
    @Test
    public void JUDDI_590_1() throws Exception {
        //create two businesses
        System.out.println("JUDDI_590_1");

        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("JUDDI_590 Joe");
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        String joeBiz = null;
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            joeBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
            //DeleteBusiness db = new DeleteBusiness();
            //db.setAuthInfo(authInfoJoe);
            //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            //publication.deleteBusiness(db);
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            samBiz = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
            //DeleteBusiness db = new DeleteBusiness();
            //db.setAuthInfo(authInfoJoe);
            //db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            //publication.deleteBusiness(db);
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
        publication.addPublisherAssertions(apa);
        //ok so joe has asserted that he knows sam

        //check get status is not null from 1 and from 2
        boolean ok = true;
        String msg = "";
        try {
            List<AssertionStatusItem> assertionStatusReport = publication.getAssertionStatusReport(authInfoJoe, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
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
        //check that sam got the message

        try {
            List<AssertionStatusItem> assertionStatusReport = publication.getAssertionStatusReport(authInfoSam, CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
            if (assertionStatusReport.isEmpty()) {
                msg = "Stage2: no result returned, expected at least 1";
                ok = false;
            }
            for (int i = 0; i < assertionStatusReport.size(); i++) {
                JAXB.marshal(assertionStatusReport.get(i), System.out);
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
        DeleteBusinesses(biz, authInfoSam);

        biz = new ArrayList<String>();
        biz.add(joeBiz);
        DeleteBusinesses(biz, authInfoJoe);
        Assert.assertTrue(msg, ok);


    }

    /**
     *  //testing upper case subscription callbacks
     *
     * @throws Exception
     */
    @Test
    public void JIRA_597() throws Exception {
        System.out.println("JIRA_597");
        int port = 4444;
        String localhostname = java.net.InetAddress.getLocalHost().getHostName();
        UDDISubscriptionListenerImpl impl = new UDDISubscriptionListenerImpl();
        UDDISubscriptionListenerImpl.notifcationMap.clear();
        UDDISubscriptionListenerImpl.notificationCount = 0;
        Endpoint ep = null;
        boolean ok = false;
        do {
            try {
                ep = Endpoint.publish("http://" + localhostname + ":" + port + "/UDDI_CALLBACK", impl);
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
        bt.getAccessPoint().setValue("http://" + localhostname + ":" + port + "/UDDI_CALLBACK");
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
        BusinessDetail saveBusiness = publication.saveBusiness(sb);

        //ok Joe's callback is setup

        //Setup a business to subscribe to
        sb = new SaveBusiness();
        sb.setAuthInfo(authInfoSam);
        be = new BusinessEntity();
        be.getName().add(new Name());
        be.getName().get(0).setValue("Sam's business");
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness1 = publication.saveBusiness(sb);

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
        subscription.saveSubscription(authInfoJoe, list);

        //ok have sam change his business around.
        sb = new SaveBusiness();
        sb.setAuthInfo(authInfoSam);
        be = saveBusiness1.getBusinessEntity().get(0);
        be.getName().get(0).setLang("en");
        sb.getBusinessEntity().add(be);
        publication.saveBusiness(sb);
        int maxwait = 30000;
        while (maxwait > 0) {
            if (UDDISubscriptionListenerImpl.notifcationMap.size() > 0) {
                break;
            }
            Thread.sleep(1000);
            maxwait = maxwait - 1000;
        }
        if (UDDISubscriptionListenerImpl.notifcationMap.isEmpty()) {
            Assert.fail("no callbacks were recieved.");
        }


    }

    /**
     * testing callbacks with underfined transport type
     *
     * @throws Exception
     */
    @Test
    public void JIRA_596() throws Exception {
        System.out.println("JIRA_596");
        int port = 4444;
        String localhostname = java.net.InetAddress.getLocalHost().getHostName();
        UDDISubscriptionListenerImpl impl = new UDDISubscriptionListenerImpl();
        UDDISubscriptionListenerImpl.notifcationMap.clear();
        UDDISubscriptionListenerImpl.notificationCount = 0;
        Endpoint ep = null;
        boolean ok = false;
        do {
            try {
                ep = Endpoint.publish("http://" + localhostname + ":" + port + "/UDDI_CALLBACK", impl);
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
        bt.getAccessPoint().setValue("http://" + localhostname + ":" + port + "/UDDI_CALLBACK");
        bt.getAccessPoint().setUseType("endPoint");
        //Added per Kurt
        /*TModelInstanceInfo instanceInfo = new TModelInstanceInfo();
         instanceInfo.setTModelKey("uddi:uddi.org:transport:http");
         bt.setTModelInstanceDetails(new TModelInstanceDetails());
         bt.getTModelInstanceDetails().getTModelInstanceInfo().add(instanceInfo);
         */
        bs.getBindingTemplates().getBindingTemplate().add(bt);

        bs.getBindingTemplates().getBindingTemplate().add(bt);
        be.getBusinessServices().getBusinessService().add(bs);
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness = publication.saveBusiness(sb);

        //ok Joe's callback is setup

        //Setup a business to subscribe to
        sb = new SaveBusiness();
        sb.setAuthInfo(authInfoSam);
        be = new BusinessEntity();
        be.getName().add(new Name());
        be.getName().get(0).setValue("Sam's business");
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness1 = publication.saveBusiness(sb);

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
        subscription.saveSubscription(authInfoJoe, list);

        //ok have sam change his business around.
        sb = new SaveBusiness();
        sb.setAuthInfo(authInfoSam);
        be = saveBusiness1.getBusinessEntity().get(0);
        be.getName().get(0).setLang("en");
        sb.getBusinessEntity().add(be);
        publication.saveBusiness(sb);
        int maxwait = 30000;
        while (maxwait > 0) {
            if (UDDISubscriptionListenerImpl.notifcationMap.size() > 0) {
                break;
            }
            Thread.sleep(1000);
            maxwait = maxwait - 1000;
        }
        if (UDDISubscriptionListenerImpl.notifcationMap.isEmpty()) {
            Assert.fail("no callbacks were recieved.");
        }


    }

    //once more without any relationship
    //TODO binding template tmodel instance info
    //TODO tmodel tests
    //TODO create tests for enforcing ref integrity of tmodel keys, after enforcing this, the tests in this class will need to be heavily revised
    //<editor-fold defaultstate="collapsed" desc="Some basic util functions to print out the data structure">
    /**
     * Converts category bags of tmodels to a readable string
     *
     * @param categoryBag
     * @return
     */
    public static String CatBagToString(CategoryBag categoryBag) {
        StringBuilder sb = new StringBuilder();
        if (categoryBag == null) {
            return "no data";
        }
        for (int i = 0; i < categoryBag.getKeyedReference().size(); i++) {
            sb.append(KeyedReferenceToString(categoryBag.getKeyedReference().get(i)));
        }
        for (int i = 0; i < categoryBag.getKeyedReferenceGroup().size(); i++) {
            sb.append("Key Ref Grp: TModelKey=");
            for (int k = 0; k < categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {
                sb.append(KeyedReferenceToString(categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().get(k)));
            }
        }
        return sb.toString();
    }

    public static String KeyedReferenceToString(KeyedReference item) {
        StringBuilder sb = new StringBuilder();
        sb.append("Key Ref: Name=").
                append(item.getKeyName()).
                append(" Value=").
                append(item.getKeyValue()).
                append(" tModel=").
                append(item.getTModelKey()).
                append(System.getProperty("line.separator"));
        return sb.toString();
    }

    public static void PrintContacts(Contacts contacts) {
        if (contacts == null) {
            return;
        }
        for (int i = 0; i < contacts.getContact().size(); i++) {
            System.out.println("Contact " + i + " type:" + contacts.getContact().get(i).getUseType());
            for (int k = 0; k < contacts.getContact().get(i).getPersonName().size(); k++) {
                System.out.println("Name: " + contacts.getContact().get(i).getPersonName().get(k).getValue());
            }
            for (int k = 0; k < contacts.getContact().get(i).getEmail().size(); k++) {
                System.out.println("Email: " + contacts.getContact().get(i).getEmail().get(k).getValue());
            }
            for (int k = 0; k < contacts.getContact().get(i).getAddress().size(); k++) {
                System.out.println("Address sort code " + contacts.getContact().get(i).getAddress().get(k).getSortCode());
                System.out.println("Address use type " + contacts.getContact().get(i).getAddress().get(k).getUseType());
                System.out.println("Address tmodel key " + contacts.getContact().get(i).getAddress().get(k).getTModelKey());
                for (int x = 0; x < contacts.getContact().get(i).getAddress().get(k).getAddressLine().size(); x++) {
                    System.out.println("Address line value " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getValue());
                    System.out.println("Address line key name " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyName());
                    System.out.println("Address line key value " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyValue());
                }
            }
            for (int k = 0; k < contacts.getContact().get(i).getDescription().size(); k++) {
                System.out.println("Desc: " + contacts.getContact().get(i).getDescription().get(k).getValue());
            }
            for (int k = 0; k < contacts.getContact().get(i).getPhone().size(); k++) {
                System.out.println("Phone: " + contacts.getContact().get(i).getPhone().get(k).getValue());
            }
        }

    }

    /**
     * This function is useful for translating UDDI's somewhat complex data
     * format to something that is more useful.
     *
     * @param bindingTemplates
     */
    public static void PrintBindingTemplates(BindingTemplates bindingTemplates) {
        if (bindingTemplates == null) {
            return;
        }
        for (int i = 0; i < bindingTemplates.getBindingTemplate().size(); i++) {
            System.out.println("Binding Key: " + bindingTemplates.getBindingTemplate().get(i).getBindingKey());

            if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint() != null) {
                System.out.println("Access Point: " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getValue() + " type " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType());
            }

            if (bindingTemplates.getBindingTemplate().get(i).getHostingRedirector() != null) {
                System.out.println("Hosting Redirection: " + bindingTemplates.getBindingTemplate().get(i).getHostingRedirector().getBindingKey());
            }
        }
    }

    public static void PrintBusinessInfo(BusinessInfos businessInfos) {
        if (businessInfos == null) {
            System.out.println("No data returned");
        } else {
            for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
                System.out.println("===============================================");
                System.out.println("Business Key: " + businessInfos.getBusinessInfo().get(i).getBusinessKey());
                System.out.println("Name: " + ListToString(businessInfos.getBusinessInfo().get(i).getName()));

                System.out.println("Name: " + ListToDescString(businessInfos.getBusinessInfo().get(i).getDescription()));
                System.out.println("Services:");
                PrintServiceInfo(businessInfos.getBusinessInfo().get(i).getServiceInfos());
            }
        }
    }

    public static String ListToString(List<Name> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return sb.toString();
    }

    public static String ListToDescString(List<Description> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return sb.toString();
    }

    public static void PrintServiceInfo(ServiceInfos serviceInfos) {
        for (int i = 0; i < serviceInfos.getServiceInfo().size(); i++) {
            System.out.println("-------------------------------------------");
            System.out.println("Service Key: " + serviceInfos.getServiceInfo().get(i).getServiceKey());
            System.out.println("Owning Business Key: " + serviceInfos.getServiceInfo().get(i).getBusinessKey());
            System.out.println("Name: " + ListToString(serviceInfos.getServiceInfo().get(i).getName()));
        }
    }

    public static void PrintBusinessDetails(List<BusinessEntity> businessDetail) throws Exception {


        for (int i = 0; i < businessDetail.size(); i++) {
            System.out.println("Business Detail - key: " + businessDetail.get(i).getBusinessKey());
            System.out.println("Name: " + ListToString(businessDetail.get(i).getName()));
            System.out.println("CategoryBag: " + CatBagToString(businessDetail.get(i).getCategoryBag()));
            PrintContacts(businessDetail.get(i).getContacts());
        }
    }
    //</editor-fold>

    private void DeleteBusinesses(List<String> businesskeysToDelete) {

        DeleteBusinesses(businesskeysToDelete, authInfoJoe);

    }

    private void DeleteBusinesses(List<String> businesskeysToDelete, String authinfo) {


        //cleanup
        try {
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authinfo);
            db.getBusinessKey().addAll(businesskeysToDelete);
            publication.deleteBusiness(db);
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
            publication.deleteTModel(db);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
