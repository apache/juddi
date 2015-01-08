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

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.Address;
import org.uddi.api_v3.AddressLine;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.Email;
import org.uddi.api_v3.HostingRedirector;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.Phone;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * The Negative Publication tests validates adherence to UDDI's string
 * validations, max lengths, null values (and more) by sending bogus requests to
 * a Publication endpoint. Tests that succeed or return stack traces are
 * actually failing test cases
 *
 * This class is for the most part complete.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_140_NegativePublicationIntegrationTest {

        private static Log logger = LogFactory.getLog(UDDI_140_NegativePublicationIntegrationTest.class);
        static UDDISecurityPortType security = null;
        static UDDIInquiryPortType inquiryJoe = null;
        static UDDIPublicationPortType publicationJoe = null;
        static TckTModel tckTModelJoe = null;
        protected static String authInfoJoe = null;
        private static UDDIClient manager;
        static final String str256 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        static final String str255 = "uddi:tmodelkey:categories:1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        static final String strkey256 = "uddi:tmodelkey:categories:11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
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
             if (!TckPublisher.isEnabled()) return;
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
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
                        security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());


                        publicationJoe = transport.getUDDIPublishService();
                        inquiryJoe = transport.getUDDIInquiryService();

                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }


                        tckTModelJoe = new TckTModel(publicationJoe, inquiryJoe);

                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());

                        transport = manager.getTransport("uddiv3");
                        UDDIPublicationPortType publicationJoe = transport.getUDDIPublishService();
                        UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();

                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publicationJoe, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        }


                        TckTModel uddi = new TckTModel(publicationJoe, inquiry);
                        uddi.saveUDDIPublisherTmodel(authInfoUDDI);
                        uddi.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
                JUDDI_300_MultiNodeIntegrationTest.testSetupReplicationConfig();
        }

        static void HandleException(Exception ex) {
                if (logger.isDebugEnabled()) {
                        System.err.println("Error caught of type " + ex.getClass().getCanonicalName());
                        ex.printStackTrace();
                }
                Assert.assertFalse(ex.getMessage().contains(TRANS));
                Assert.assertFalse(ex.getMessage().contains(MISSING_RESOURCE));
                if (ex instanceof SOAPFault) {
                        SOAPFault sf = (SOAPFault) ex;
                        if (!sf.getTextContent().contains("org.apache.juddi.v3.error.ValueNotAllowedException")) {
                                Assert.fail();
                        }
                }
        }

        //<editor-fold defaultstate="collapsed" desc="Business Name tests">
        

        @Test(expected = SOAPFaultException.class)
        public void BusinessKeyTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessKeyTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BusinessKeyTooLongTest -Hello Nurse");
                be.getName().add(n);
                be.setBusinessKey(strkey256_1);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void BusinessNameTooShortTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessNameTooShortTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void BusinessNameMinLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessNameMinLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("1");
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BusinessNameTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessNameTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                //256 chars
                n.setValue(str256);
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }

        }

        @Test
        public void BusinessNameMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessNameMaxLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                //255 chars
                n.setValue(str255);
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BusinessNameLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessNameLangTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessNameLangTooLongTest A Test business");
                //27
                n.setLang(str27);
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }

        }

        @Test
        public void BusinessNameLangMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessNameLangMaxLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessNameLangMaxLengthTest A Test business");
                n.setLang(str26);

                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BusinessDescriptionLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDescriptionLangTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDescriptionLangTooLongTest A Test business");
                Description d = new Description();
                d.setValue("a description");
                //27
                d.setLang(str27);
                be.getName().add(n);
                be.getDescription().add(d);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }

        }

        @Test
        public void BusinessDescriptionLangMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDescriptionLangMaxLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDescriptionLangMaxLengthTest A Test business");
                Description d = new Description();
                d.setValue("a description");
                //26
                d.setLang(str26);
                be.getDescription().add(d);
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test
        public void BusinessDescriptionMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDescriptionMaxLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDescriptionMaxLengthTest A Test business");
                Description d = new Description();
                d.setValue(str255);
                be.getDescription().add(d);
                be.getName().add(n);
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BusinessDescriptionTooLongLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDescriptionTooLongLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDescriptionTooLongLengthTest A Test business");
                Description d = new Description();
                d.setValue(str256);
                be.getDescription().add(d);
                be.getName().add(n);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }

        }

        @Test(expected = SOAPFaultException.class)
        public void BusinessDiscoveryURLTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDiscoveryURLTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDiscoveryURLTooLongTest A Test business");
                be.getName().add(n);
                be.setDiscoveryURLs(new DiscoveryURLs());
                DiscoveryURL d = new DiscoveryURL();
                d.setValue(str4097);
                be.getDiscoveryURLs().getDiscoveryURL().add(d);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }

        }

        @Test
        public void BusinessDiscoveryURLMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDiscoveryURLMaxLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDiscoveryURLMaxLengthTest A Test business");
                be.getName().add(n);
                be.setDiscoveryURLs(new DiscoveryURLs());
                DiscoveryURL d = new DiscoveryURL();
                d.setValue(str4096);
                be.getDiscoveryURLs().getDiscoveryURL().add(d);
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test
        public void BusinessDiscoveryURLMaxLengthMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDiscoveryURLMaxLengthMaxUseTypeTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDiscoveryURLMaxLengthMaxUseTypeTest A Test business");
                be.getName().add(n);
                be.setDiscoveryURLs(new DiscoveryURLs());
                DiscoveryURL d = new DiscoveryURL();
                d.setValue(str4096);
                d.setUseType(str255);
                be.getDiscoveryURLs().getDiscoveryURL().add(d);
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BusinessDiscoveryURLMaxLengthToolongUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BusinessDiscoveryURLMaxLengthToolongUseTypeTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();

                n.setValue("BusinessDiscoveryURLMaxLengthToolongUseTypeTest A Test business");
                be.getName().add(n);
                be.setDiscoveryURLs(new DiscoveryURLs());
                DiscoveryURL d = new DiscoveryURL();
                d.setValue(str4096);
                d.setUseType(str256);
                be.getDiscoveryURLs().getDiscoveryURL().add(d);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void ContactMaxEmailMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxEmailMaxUseTypeTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxEmailMaxUseTypeTest A Test business");
                be.getName().add(n);
                be.setContacts(ContactMaxEmailMaxUseType());
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        private Contacts ContactMaxEmailMaxUseType() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Email m = new Email();
                m.setValue(str255);
                m.setUseType(str255);
                c.getEmail().add(m);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactTooLongEmailMaxUseType() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Email m = new Email();
                m.setValue(str256);
                m.setUseType(str255);
                c.getEmail().add(m);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactEmailUseTypeToolong() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Email m = new Email();
                m.setValue(str255);
                m.setUseType(str256);
                c.getEmail().add(m);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactMaxDescription() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Email m = new Email();
                m.setValue(str255);
                m.setUseType(str255);
                c.getEmail().add(m);
                Description d = new Description();
                d.setLang(str26);
                d.setValue(str255);
                c.getDescription().add(d);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactDescriptionTooLong() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Email m = new Email();
                m.setValue(str255);
                m.setUseType(str255);
                c.getEmail().add(m);
                Description d = new Description();
                d.setLang(str256);
                d.setValue(str26);
                c.getDescription().add(d);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactPhoneTooLong() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Phone p = new Phone();
                p.setValue(str51);
                c.getPhone().add(p);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactPhoneUseTypeTooLong() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Phone p = new Phone();
                p.setValue(str50);
                p.setUseType(str256);
                c.getPhone().add(p);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactPhoneUseTypeMaxLen() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Phone p = new Phone();
                p.setValue(str50);
                p.setUseType(str255);
                c.getPhone().add(p);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactPhoneMaxLength() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Phone p = new Phone();
                p.setValue(str50);
                c.getPhone().add(p);
                cc.getContact().add(c);;
                return cc;
        }

        private Contacts ContactAddressAllMax(boolean oversizedLang, boolean oversizedTmodel, boolean oversizedSortCode, boolean oversizedUseType,
                boolean oversizedAddressLineValue, boolean oversizedAddressKN, boolean oversizedAddressKV) {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);

                cc.getContact().add(c);
                Address a = new Address();
                if (oversizedSortCode) {
                        a.setSortCode(str11);
                } else {
                        a.setSortCode(str10);
                }
                if (oversizedLang) {
                        a.setLang(str27);
                } else {
                        a.setLang(str26);
                }
                if (oversizedTmodel) {
                        a.setTModelKey(str256);
                } else {
                        a.setTModelKey(str255);
                }
                if (oversizedUseType) {
                        a.setUseType(str256);
                } else {
                        a.setUseType(str255);
                }
                if (oversizedSortCode) {
                        a.setSortCode(str11);
                } else {
                        a.setSortCode(str10);
                }
                AddressLine al = new AddressLine();
                if (oversizedAddressKN) {
                        al.setKeyName(str256); //optional
                } else {
                        al.setKeyName(str255); //optional
                }
                if (oversizedAddressKV) {
                        al.setKeyValue(str256); //optional
                } else {
                        al.setKeyValue(str255); //optional
                }
                if (oversizedAddressLineValue) {
                        al.setValue(str81); //this one is required
                } else {
                        al.setValue(str80);
                }
                a.getAddressLine().add(al);
                c.getAddress().add(a);
                return cc;
        }

        private Contacts ContactDescriptionLangTooLong() {
                Contacts cc = new Contacts();
                Contact c = new Contact();
                PersonName n = new PersonName();
                n.setValue("Bob");
                c.getPersonName().add(n);
                Email m = new Email();
                m.setValue(str255);
                m.setUseType(str255);
                c.getEmail().add(m);
                Description d = new Description();
                d.setLang(str255);
                d.setValue(str27);
                c.getDescription().add(d);
                cc.getContact().add(c);
                return cc;
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactTooLongEmailMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactTooLongEmailMaxUseTypeTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactTooLongEmailMaxUseTypeTest A Test business");
                be.getName().add(n);
                be.setContacts(ContactTooLongEmailMaxUseType());
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxEmailToolongUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxEmailToolongUseTypeTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxEmailToolongUseTypeTest A Test business");
                be.getName().add(n);

                be.setContacts(ContactEmailUseTypeToolong());


                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void ContactDescriptionMaxLangMaxtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactDescriptionMaxLangMaxtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactDescriptionMaxLangMaxtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactMaxDescription());
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);

        }

        @Test(expected = SOAPFaultException.class)
        public void ContactDescriptionTooLongtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactDescriptionTooLongtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactDescriptionTooLongtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactDescriptionTooLong());
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactDescriptionLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactDescriptionLangTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactDescriptionLangTooLongTest A Test business");
                be.getName().add(n);
                be.setContacts(ContactDescriptionLangTooLong());
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void ContactPhoneMaxLentest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactPhoneMaxLentest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactPhoneMaxLentest A Test business");
                be.getName().add(n);
                be.setContacts(ContactPhoneMaxLength());
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);

        }

        @Test(expected = SOAPFaultException.class)
        public void ContactPhoneTooLongtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactPhoneTooLongtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactPhoneTooLongtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactPhoneTooLong());
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void ContactPhoneMaxLongtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactPhoneMaxLongtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactPhoneMaxLongtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactPhoneMaxLength());
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test
        public void ContactPhoneMaxLongMaxUseTypetest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactPhoneMaxLongMaxUseTypetest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactPhoneMaxLongMaxUseTypetest A Test business");
                be.getName().add(n);
                be.setContacts(ContactPhoneUseTypeMaxLen());
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactPhoneUseTypeTooLongtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactPhoneUseTypeTooLongtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactPhoneUseTypeTooLongtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactPhoneUseTypeTooLong());
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void ContactMaxAddressFFFFFFFtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressFFFFFFFtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressFFFFFFFtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(false, false, false, false, false, false, false));
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);

        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxAddressTFFFFFFtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressTFFFFFFtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressTFFFFFFtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(true, false, false, false, false, false, false));
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxAddressFTFFFFFtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressFTFFFFFtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressFTFFFFFtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(false, true, false, false, false, false, false));
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxAddressFFTFFFFtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressFFTFFFFtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressFFTFFFFtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(false, false, true, false, false, false, false));
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxAddressFFFTFFFtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressFFFTFFFtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressFFFTFFFtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(false, false, false, true, false, false, false));
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxAddressFFFFTFFtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressFFFFTFFtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressFFFFTFFtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(false, false, false, false, true, false, false));
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxAddressFFFFFTFtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressFFFFFTFtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressFFFFFTFtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(false, false, false, false, false, true, false));
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ContactMaxAddressFFFFFFTtest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ContactMaxAddressFFFFFFTtest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ContactMaxAddressFFFFFFTtest A Test business");
                be.getName().add(n);
                be.setContacts(ContactAddressAllMax(false, false, false, false, false, false, true));
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void KeyReferenceMax() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("KeyReferenceMax");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("KeyReferenceMax A Test business");
                be.getName().add(n);
                be.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setKeyName(str255);
                kr.setKeyValue(str255);
                kr.setTModelKey(str255);
                be.getCategoryBag().getKeyedReference().add(kr);
                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void KeyReferenceKeyTooLong() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("KeyReferenceKeyTooLong");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("KeyReferenceKeyTooLong A Test business");
                be.getName().add(n);
                be.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setKeyName(str255);
                kr.setKeyValue(str255);
                kr.setTModelKey(str256);
                be.getCategoryBag().getKeyedReference().add(kr);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void KeyReferenceNameTooLong() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("KeyReferenceNameTooLong");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("KeyReferenceNameTooLong A Test business");
                be.getName().add(n);
                be.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setKeyName(str256);
                kr.setKeyValue(str255);
                kr.setTModelKey(str255);
                be.getCategoryBag().getKeyedReference().add(kr);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void KeyReferenceValueTooLong() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("KeyReferenceValueTooLong");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("KeyReferenceValueTooLong A Test business");
                be.getName().add(n);
                be.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setKeyName(str255);
                kr.setKeyValue(str256);
                kr.setTModelKey(str255);
                be.getCategoryBag().getKeyedReference().add(kr);
                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ServiceNameTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceNameTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceNameTooLongTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                n = new Name();
                n.setValue(str256);
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void ServiceNameMaxLenTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceNameMaxLenTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceNameMaxLenTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test
        public void ServiceNameMaxLangLenTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceNameMaxLangLenTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceNameMaxLangLenTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                n = new Name();
                n.setValue(str255);
                n.setLang(str26);
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void ServiceNameTooLongLangTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceNameTooLongLangTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceNameTooLongLangTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                n = new Name();
                n.setValue(str255);
                n.setLang(str27);
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ServiceDescTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceDescTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceDescTooLongTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                Description d = new Description();
                d.setValue(str256);
                bs.getDescription().add(d);
                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void ServiceDescLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceDescLangTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceDescLangTooLongTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                Description d = new Description();
                d.setValue(str255);
                d.setLang(str27);
                bs.getDescription().add(d);
                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void ServiceDescMaxLangTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceDescMaxLangTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceDescMaxLangTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                Description d = new Description();
                d.setValue(str255);
                d.setLang(str26);
                bs.getDescription().add(d);
                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);

        }

        @Test
        public void ServiceMaxCatBagTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceDescMaxLangTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceDescMaxLangTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                Description d = new Description();
                d.setValue(str255);
                d.setLang(str26);
                bs.getDescription().add(d);
                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                bs.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setKeyName(str255);
                kr.setKeyValue(str255);
                kr.setTModelKey(str255);
                bs.getCategoryBag().getKeyedReference().add(kr);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void ServiceMaxCatBagTooBigTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ServiceMaxCatBagTooBigTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("ServiceMaxCatBagTooBigTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                Description d = new Description();
                d.setValue(str255);
                d.setLang(str26);
                bs.getDescription().add(d);
                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                bs.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setKeyName(str256);
                kr.setKeyValue(str256);
                kr.setTModelKey(str256);
                bs.getCategoryBag().getKeyedReference().add(kr);
                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateNoAccessPointTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateNoAccessPointTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateNoAccessPointTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void BindingTemplateAccessPointMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointMaxUseTypeTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointMaxUseTypeTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                AccessPoint ap = new AccessPoint();
                ap.setUseType(str255);
                ap.setValue("http://localhost");
                bt.setAccessPoint(ap);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateAccessPointUseTypeTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointUseTypeTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointUseTypeTooLongTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                AccessPoint ap = new AccessPoint();
                ap.setUseType(str256);
                ap.setValue("http://localhost");
                bt.setAccessPoint(ap);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateAccessPointValueTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointValueTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointValueTooLongTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                AccessPoint ap = new AccessPoint();
                ap.setUseType(str255);
                ap.setValue(str4097);
                bt.setAccessPoint(ap);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void BindingTemplateAccessPointMaxValueTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointMaxValueTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointMaxValueTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                AccessPoint ap = new AccessPoint();
                ap.setUseType(str255);
                ap.setValue(str4096);
                bt.setAccessPoint(ap);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateNoAccessPointNoRedirectorTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateNoAccessPointNoRedirectorTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateNoAccessPointNoRedirectorTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();


                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());

                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateAccessPointAndRedirectorTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointAndRedirectorTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointAndRedirectorTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setUseType(str26);

                bt.setHostingRedirector(new HostingRedirector());

                bt.getHostingRedirector().setBindingKey(str26);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void BindingTemplateHostRedirectorReferencalIntegritytest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateHostRedirectorReferencalIntegritytest");
                //TODO create a binding template, get the key, use the key as the specific redirector
                String url = "http://juddi.apache.org";
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateHostRedirectorReferencalIntegritytest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue("A first business service");
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue(url);

                //bt.setHostingRedirector(new HostingRedirector());
                //bt.getHostingRedirector().setBindingKey(str255);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                System.out.println("Saving the business with the first service");
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);

                TckCommon.PrintBusinessDetails(saveBusiness.getBusinessEntity());

                //setup the next one
                bs = new BusinessService();
                n = new Name();
                n.setValue("A a redirected business service");
                bt = new BindingTemplate();
                bt.setHostingRedirector(new HostingRedirector());
                bt.getHostingRedirector().setBindingKey(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey());
                bs.getName().add(n);
                bs.setBindingTemplates(new BindingTemplates());
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().add(bs);

                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                sb.getBusinessEntity().add(saveBusiness.getBusinessEntity().get(0));

                //This SHOULD be allowed
                System.out.println("Saving the business with the first and second service as a host redirect");
                saveBusiness = publicationJoe.saveBusiness(sb);
                TckCommon.PrintBusinessDetails(saveBusiness.getBusinessEntity());

                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test
        public void BindingTemplateAccessPointAsBindingTemplateReferencalIntegritytest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointAsBindingTemplateReferencalIntegritytest");
                //create a binding template, get the key, use the key as the specific redirector
                String url = "http://juddi.apache.org";
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointAsBindingTemplateReferencalIntegritytest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue("A first business service");
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue(url);

                //bt.setHostingRedirector(new HostingRedirector());
                //bt.getHostingRedirector().setBindingKey(str255);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                System.out.println("Saving the business with the first service");
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);

                TckCommon.PrintBusinessDetails(saveBusiness.getBusinessEntity());

                //setup the next one
                bs = new BusinessService();
                n = new Name();
                n.setValue("A a redirected business service");
                bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setUseType(AccessPointType.BINDING_TEMPLATE.toString());
                bt.getAccessPoint().setValue(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey());
                bs.getName().add(n);
                bs.setBindingTemplates(new BindingTemplates());
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().add(bs);

                sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                sb.getBusinessEntity().add(saveBusiness.getBusinessEntity().get(0));

                //This SHOULD be allowed
                System.out.println("Saving the business with the first and second service as a host redirect");
                saveBusiness = publicationJoe.saveBusiness(sb);
                TckCommon.PrintBusinessDetails(saveBusiness.getBusinessEntity());

                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateAccessPointAsBindingTemplateINVALIDReferencalIntegritytest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointAsBindingTemplateINVALIDReferencalIntegritytest");
                //create a binding template, get the key, use the key as the specific redirector
                SaveBusiness sb;
                try {

                        sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);

                        BusinessEntity be = new BusinessEntity();
                        Name bsn = new Name();
                        bsn.setValue("BindingTemplateAccessPointAsBindingTemplateINVALIDReferencalIntegritytest A bogus business");
                        be.getName().add(bsn);

                        BusinessService bs = new BusinessService();
                        Name n = new Name();
                        n.setValue("A a redirected business service");
                        BindingTemplate bt = new BindingTemplate();
                        bt.setAccessPoint(new AccessPoint());
                        bt.getAccessPoint().setUseType(AccessPointType.BINDING_TEMPLATE.toString());
                        bt.getAccessPoint().setValue("uddi:" + UUID.randomUUID().toString());
                        bs.getName().add(n);
                        bs.setBindingTemplates(new BindingTemplates());
                        bs.getBindingTemplates().getBindingTemplate().add(bt);
                        be.setBusinessServices(new BusinessServices());
                        be.getBusinessServices().getBusinessService().add(bs);
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);

                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateHostRedirectorTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateHostRedirectorTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateHostRedirectorTooLongTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());

                bt.setHostingRedirector(new HostingRedirector());
                bt.getHostingRedirector().setBindingKey(str256);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test
        public void BindingTemplateAccessPointMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointMaxLengthTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointMaxLengthTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();
                bs.setBindingTemplates(new BindingTemplates());
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue(str4096);

                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);

                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                publicationJoe.deleteBusiness(db);
        }

        @Test(expected = SOAPFaultException.class)
        public void BindingTemplateAccessPointTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateAccessPointTooLongTest");
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                Name n = new Name();
                n.setValue("BindingTemplateAccessPointTooLongTest A Test business");
                be.getName().add(n);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();

                n = new Name();
                n.setValue(str255);
                bs.getName().add(n);
                BindingTemplate bt = new BindingTemplate();

                bs.setBindingTemplates(new BindingTemplates());
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue(str4097);

                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                sb.getBusinessEntity().add(be);
                try {
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfoJoe);
                        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        publicationJoe.deleteBusiness(db);
                        Assert.fail("request should have been rejected");

                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }
        //</editor-fold>
        static final String validTmodelKeyGenMax = "uddi:www.mycoolxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxcompany.com:keygenerator";
        static final String validTmodelKeyGenTooLong = "uddi:www.mycoolzxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxcompany.com:keygenerator";

        //create a basic key gen
        @Test
        public void CreateKeyGenMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("CreateKeyGenMaxLengthTest");

                SaveTModel st = new SaveTModel();
                st.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name());
                tm.getName().setValue("CreateKeyGenMaxLengthTest My Cool Company Keymodel generator");
                tm.getName().setLang("en");
                tm.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:categorization:types");
                kr.setKeyName("uddi-org:keyGenerator");
                kr.setKeyValue("keyGenerator");
                tm.getCategoryBag().getKeyedReference().add(kr);
                tm.setTModelKey(validTmodelKeyGenMax);
                st.getTModel().add(tm);

                @SuppressWarnings("unused")
                TModelDetail saveTModel = publicationJoe.saveTModel(st);
                DeleteTModel dm = new DeleteTModel();
                dm.setAuthInfo(authInfoJoe);
                dm.getTModelKey().add(validTmodelKeyGenMax);
                publicationJoe.deleteTModel(dm);

        }

        //create a oversized tmodel keygen fail
        @Test(expected = SOAPFaultException.class)
        public void CreateKeyGenTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("CreateKeyGenTooLongTest");

                SaveTModel st = new SaveTModel();
                st.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name());
                tm.getName().setValue("CreateKeyGenTooLongTest My Cool Company Keymodel generator");
                tm.getName().setLang("en");
                tm.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:categorization:types");
                kr.setKeyName("uddi-org:keyGenerator");
                kr.setKeyValue("keyGenerator");
                tm.getCategoryBag().getKeyedReference().add(kr);
                tm.setTModelKey(validTmodelKeyGenTooLong);
                st.getTModel().add(tm);
                try {
                        @SuppressWarnings("unused")
                        TModelDetail saveTModel = publicationJoe.saveTModel(st);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }

        }

        //create a tmodel with a key gen defined valid, with oversized Name
        @Test(expected = SOAPFaultException.class)
        public void CreateKeyGenKeyDescriptionTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("CreateKeyGenKeyDescriptionTooLongTest");

                SaveTModel st = new SaveTModel();
                st.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name());
                tm.getName().setValue("CreateKeyGenKeyDescriptionTooLongTest Key gen name");
                tm.getName().setLang("en");
                Description d = new Description();
                d.setValue(str256);
                tm.getDescription().add(d);
                tm.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:categorization:types");
                kr.setKeyName("uddi-org:keyGenerator");
                kr.setKeyValue("keyGenerator");
                tm.getCategoryBag().getKeyedReference().add(kr);
                tm.setTModelKey("uddi:uddi.joepublisher.com:mycustomkey");
                st.getTModel().add(tm);
                try {
                        @SuppressWarnings("unused")
                        TModelDetail saveTModel = publicationJoe.saveTModel(st);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void CreateKeyGenKeyDescriptionLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("CreateKeyGenKeyDescriptionTooLongTest");

                SaveTModel st = new SaveTModel();
                st.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name());
                tm.getName().setValue("CreateKeyGenKeyDescriptionTooLongTest Key gen name");
                tm.getName().setLang("en");
                Description d = new Description();
                d.setValue("A description");
                d.setLang(str256);
                tm.getDescription().add(d);
                tm.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:categorization:types");
                kr.setKeyName("uddi-org:keyGenerator");
                kr.setKeyValue("keyGenerator");
                tm.getCategoryBag().getKeyedReference().add(kr);
                String key = UUID.randomUUID().toString();
                tm.setTModelKey(key);
                st.getTModel().add(tm);
                try {
                        @SuppressWarnings("unused")
                        TModelDetail saveTModel = publicationJoe.saveTModel(st);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                } finally {
                        //TODO delete the key
                }
        }

        @Test(expected = SOAPFaultException.class)
        public void CreateKeyGenNameLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("CreateKeyGenNameLangTooLongTest");

                SaveTModel st = new SaveTModel();
                st.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name());
                tm.getName().setValue("CreateKeyGenNameLangTooLongTest hello world");
                tm.getName().setLang(str27);
                tm.setCategoryBag(new CategoryBag());
                KeyedReference kr = new KeyedReference();
                kr.setTModelKey("uddi:uddi.org:categorization:types");
                kr.setKeyName("uddi-org:keyGenerator");
                kr.setKeyValue("keyGenerator");
                tm.getCategoryBag().getKeyedReference().add(kr);
                tm.setTModelKey(validTmodelKeyGenTooLong);
                st.getTModel().add(tm);
                try {
                        @SuppressWarnings("unused")
                        TModelDetail saveTModel = publicationJoe.saveTModel(st);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }
        }

        /**
         * //create a tmodel with a key gen defined valid, regular tmodel,
         * //then a business, service, binding template, tmodel instance infos,
         * attach tmodel with some data, success //create a tmodel without a key
         * gen defined- fail
         *
         * @throws DispositionReportFaultMessage
         * @throws RemoteException
         */
        @Test(expected = SOAPFaultException.class)
        public void CreateTmodelnoKeyGen() throws DispositionReportFaultMessage, RemoteException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("CreateTmodelnoKeyGen");

                SaveTModel st = new SaveTModel();
                st.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name());
                tm.getName().setValue("CreateTmodelnoKeyGen My Cool Company's tmodel");
                tm.getName().setLang("en");

                tm.setTModelKey("uddi:uddi.joepublisher.com:nokeygenerator:customkey");
                st.getTModel().add(tm);
                try {
                        @SuppressWarnings("unused")
                        TModelDetail saveTModel = publicationJoe.saveTModel(st);
                        Assert.fail("request should have been rejected");
                } catch (SOAPFaultException ex) {
                        HandleException(ex);
                        throw ex;
                }

        }

        @Test
        public void BindingTemplateTmodelInstanceInfoTest() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("BindingTemplateTmodelInstanceInfoTest");
                //create a key gen
                //create a tmodels
                //create a business, service, binding with tmodel instance infos

        }
        
        //TODO binding template tmodel instance info
        //TODO tmodel tests
        //TODO create tests for enforcing ref integrity of tmodel keys, after enforcing this, the tests in this class will need to be heavily revised
}
