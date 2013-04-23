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
package org.apache.juddi.api.impl;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
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
 * This is incomplete. items left to test: all service related entries,
 * including binding templates business/contact/useType business/contact/address
 * amd addressLine business/catgag, ident bag
 *
 * @author Alex O'Ree
 */
public class API_140_NegativePublicationTest {

    private static Log logger = LogFactory.getLog(API_140_NegativePublicationTest.class);
    static UDDISecurityPortType security = new UDDISecurityImpl();
    static UDDIInquiryPortType inquiry = new UDDIInquiryImpl();
    static UDDIPublicationPortType publication = new UDDIPublicationImpl();
    static TckTModel tckTModel               = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());

    protected static String authInfoJoe = null;
    protected static String authInfoSam = null;
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
    public static  void stopManager() throws ConfigurationException {
    	Registry.stop();
    }

    @BeforeClass
    public static void startManager() throws ConfigurationException {
    	Registry.start();

        logger.debug("Getting auth tokens..");
        try {
            authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
            authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
            Assert.assertNotNull(authInfoJoe);
            Assert.assertNotNull(authInfoSam);
            String authInfoUDDI  = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(),  TckPublisher.getUDDIPassword());
			tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
			tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Assert.fail("Could not obtain authInfo token.");
        }
    }

    static void HandleException(Exception ex) {
    	if (logger.isDebugEnabled()) {
	        System.err.println("Error caught of type " + ex.getClass().getCanonicalName());
	        ex.printStackTrace();
    	}
        Assert.assertFalse(ex.getMessage().contains(TRANS));
        Assert.assertFalse(ex.getMessage().contains(MISSING_RESOURCE));
        if (! (ex instanceof ValueNotAllowedException)) {
            Assert.fail();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Business Name tests">
    @Test
    public void BusinessNameSanityTest() {
        System.out.println("BusinessNameSanityTest");
    }

    @Test(expected = ValueNotAllowedException.class)
    public void BusinessKeyTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessKeyTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("Hello Nurse");
        be.getName().add(n);
        be.setBusinessKey(strkey256_1);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected = ValueNotAllowedException.class)
    public void BusinessNameTooShortTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessNameTooShortTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("");
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void BusinessNameMinLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessNameMinLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("1");
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected = ValueNotAllowedException.class)
    public void BusinessNameTooLongTest() throws DispositionReportFaultMessage, RemoteException {
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }

    }

    @Test
    public void BusinessNameMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessNameMaxLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        //255 chars
        n.setValue(str255);
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected = ValueNotAllowedException.class)
    public void BusinessNameLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessNameLangTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        //27
        n.setLang(str27);
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }

    }

    @Test
    public void BusinessNameLangMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessNameLangMaxLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        n.setLang(str26);

        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }
    
    @Test(expected = ValueNotAllowedException.class)
    public void BusinessDescriptionLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDescriptionLangTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        Description d = new Description();
        d.setValue("a description");
        //27
        d.setLang(str27);
        be.getName().add(n);
        be.getDescription().add(d);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }

    }

    @Test
    public void BusinessDescriptionLangMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDescriptionLangMaxLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        Description d = new Description();
        d.setValue("a description");
        //26
        d.setLang(str26);
        be.getDescription().add(d);
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test
    public void BusinessDescriptionMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDescriptionMaxLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        Description d = new Description();
        d.setValue(str255);
        be.getDescription().add(d);
        be.getName().add(n);
        sb.getBusinessEntity().add(be);

        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected = ValueNotAllowedException.class)
    public void BusinessDescriptionTooLongLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDescriptionTooLongLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        Description d = new Description();
        d.setValue(str256);
        be.getDescription().add(d);
        be.getName().add(n);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }

    }
    
    @Test(expected = ValueNotAllowedException.class)
    public void BusinessDiscoveryURLTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDiscoveryURLTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        be.getName().add(n);
        be.setDiscoveryURLs(new DiscoveryURLs());
        DiscoveryURL d = new DiscoveryURL();
        d.setValue(str4097);
        be.getDiscoveryURLs().getDiscoveryURL().add(d);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }

    }

    @Test
    public void BusinessDiscoveryURLMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDiscoveryURLMaxLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        be.getName().add(n);
        be.setDiscoveryURLs(new DiscoveryURLs());
        DiscoveryURL d = new DiscoveryURL();
        d.setValue(str4096);
        be.getDiscoveryURLs().getDiscoveryURL().add(d);
        sb.getBusinessEntity().add(be);
     
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test
    public void BusinessDiscoveryURLMaxLengthMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDiscoveryURLMaxLengthMaxUseTypeTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        be.getName().add(n);
        be.setDiscoveryURLs(new DiscoveryURLs());
        DiscoveryURL d = new DiscoveryURL();
        d.setValue(str4096);
        d.setUseType(str255);
        be.getDiscoveryURLs().getDiscoveryURL().add(d);
        sb.getBusinessEntity().add(be);
   
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected = ValueNotAllowedException.class)
    public void BusinessDiscoveryURLMaxLengthToolongUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BusinessDiscoveryURLMaxLengthToolongUseTypeTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();

        n.setValue("A Test business");
        be.getName().add(n);
        be.setDiscoveryURLs(new DiscoveryURLs());
        DiscoveryURL d = new DiscoveryURL();
        d.setValue(str4096);
        d.setUseType(str256);
        be.getDiscoveryURLs().getDiscoveryURL().add(d);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }
    
    @Test
    public void ContactMaxEmailMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxEmailMaxUseTypeTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactMaxEmailMaxUseType());
        sb.getBusinessEntity().add(be);
        
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
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

    @Test(expected = ValueNotAllowedException.class)
    public void ContactTooLongEmailMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactTooLongEmailMaxUseTypeTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactTooLongEmailMaxUseType());
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected = ValueNotAllowedException.class)
    public void ContactMaxEmailToolongUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxEmailToolongUseTypeTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);

        be.setContacts(ContactEmailUseTypeToolong());


        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void ContactDescriptionMaxLangMaxtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactDescriptionMaxLangMaxtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactMaxDescription());
        sb.getBusinessEntity().add(be);
     
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
        
    }

    @Test (expected = ValueNotAllowedException.class)
    public void ContactDescriptionTooLongtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactDescriptionTooLongtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactDescriptionTooLong());
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test (expected = ValueNotAllowedException.class)
    public void ContactDescriptionLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactDescriptionLangTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactDescriptionLangTooLong());
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void ContactPhoneMaxLentest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactPhoneMaxLentest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactPhoneMaxLength());
        sb.getBusinessEntity().add(be);
       
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);

    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactPhoneTooLongtest() throws DispositionReportFaultMessage, RemoteException  {
        System.out.println("ContactPhoneTooLongtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactPhoneTooLong());
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void ContactPhoneMaxLongtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactPhoneMaxLongtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactPhoneMaxLength());
        sb.getBusinessEntity().add(be);
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test
    public void ContactPhoneMaxLongMaxUseTypetest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactPhoneMaxLongMaxUseTypetest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactPhoneUseTypeMaxLen());
        sb.getBusinessEntity().add(be);
        
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected = ValueNotAllowedException.class)
    public void ContactPhoneUseTypeTooLongtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactPhoneUseTypeTooLongtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactPhoneUseTypeTooLong());
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void ContactMaxAddressFFFFFFFtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressFFFFFFFtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(false, false, false, false, false, false, false));
        sb.getBusinessEntity().add(be);
       
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);

    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactMaxAddressTFFFFFFtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressTFFFFFFtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(true, false, false, false, false, false, false));
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactMaxAddressFTFFFFFtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressFTFFFFFtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(false, true, false, false, false, false, false));
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactMaxAddressFFTFFFFtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressFFTFFFFtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(false, false, true, false, false, false, false));
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactMaxAddressFFFTFFFtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressFFFTFFFtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(false, false, false, true, false, false, false));
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactMaxAddressFFFFTFFtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressFFFFTFFtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(false, false, false, false, true, false, false));
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactMaxAddressFFFFFTFtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressFFFFFTFtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(false, false, false, false, false, true, false));
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ContactMaxAddressFFFFFFTtest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ContactMaxAddressFFFFFFTtest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setContacts(ContactAddressAllMax(false, false, false, false, false, false, true));
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void KeyReferenceMax() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("KeyReferenceMax");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setCategoryBag(new CategoryBag());
        KeyedReference kr = new KeyedReference();
        kr.setKeyName(str255);
        kr.setKeyValue(str255);
        kr.setTModelKey(str255);
        be.getCategoryBag().getKeyedReference().add(kr);
        sb.getBusinessEntity().add(be);
        
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected=ValueNotAllowedException.class)
    public void KeyReferenceKeyTooLong() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("KeyReferenceKeyTooLong");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setCategoryBag(new CategoryBag());
        KeyedReference kr = new KeyedReference();
        kr.setKeyName(str255);
        kr.setKeyValue(str255);
        kr.setTModelKey(str256);
        be.getCategoryBag().getKeyedReference().add(kr);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void KeyReferenceNameTooLong() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("KeyReferenceNameTooLong");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setCategoryBag(new CategoryBag());
        KeyedReference kr = new KeyedReference();
        kr.setKeyName(str256);
        kr.setKeyValue(str255);
        kr.setTModelKey(str255);
        be.getCategoryBag().getKeyedReference().add(kr);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void KeyReferenceValueTooLong() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("KeyReferenceValueTooLong");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setCategoryBag(new CategoryBag());
        KeyedReference kr = new KeyedReference();
        kr.setKeyName(str255);
        kr.setKeyValue(str256);
        kr.setTModelKey(str255);
        be.getCategoryBag().getKeyedReference().add(kr);
        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }
    
    @Test(expected=ValueNotAllowedException.class)
    public void ServiceNameTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceNameTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setBusinessServices(new BusinessServices());
        BusinessService bs = new BusinessService();
        n = new Name();
        n.setValue(str256);
        bs.getName().add(n);
        be.getBusinessServices().getBusinessService().add(bs);

        sb.getBusinessEntity().add(be);
        try {
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void ServiceNameMaxLenTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceNameMaxLenTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setBusinessServices(new BusinessServices());
        BusinessService bs = new BusinessService();
        n = new Name();
        n.setValue(str255);
        bs.getName().add(n);
        be.getBusinessServices().getBusinessService().add(bs);

        sb.getBusinessEntity().add(be);
       
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test
    public void ServiceNameMaxLangLenTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceNameMaxLangLenTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
        be.getName().add(n);
        be.setBusinessServices(new BusinessServices());
        BusinessService bs = new BusinessService();
        n = new Name();
        n.setValue(str255);
        n.setLang(str26);
        bs.getName().add(n);
        be.getBusinessServices().getBusinessService().add(bs);

        sb.getBusinessEntity().add(be);
        
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ServiceNameTooLongLangTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceNameTooLongLangTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ServiceDescTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceDescTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ServiceDescLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceDescLangTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void ServiceDescMaxLangTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceDescMaxLangTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
       
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
        
    }

    @Test
    public void ServiceMaxCatBagTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceDescMaxLangTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
       
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected=ValueNotAllowedException.class)
    public void ServiceMaxCatBagTooBigTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("ServiceMaxCatBagTooBigTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }
   
    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateNoAccessPointTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateNoAccessPointTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void BindingTemplateAccessPointMaxUseTypeTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointMaxUseTypeTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
       
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateAccessPointUseTypeTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointUseTypeTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateAccessPointValueTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointValueTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void BindingTemplateAccessPointMaxValueTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointMaxValueTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
     
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateNoAccessPointNoRedirectorTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateNoAccessPointNoRedirectorTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateAccessPointAndRedirectorTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointAndRedirectorTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void BindingTemplateHostRedirectorReferencalIntegritytest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateHostRedirectorReferencalIntegritytest");
        //TODO create a binding template, get the key, use the key as the specific redirector
        String url = "http://juddi.apache.org";
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
        BusinessDetail saveBusiness = publication.saveBusiness(sb);

        PrintBusinessDetails(saveBusiness.getBusinessEntity());

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
        saveBusiness = publication.saveBusiness(sb);
        PrintBusinessDetails(saveBusiness.getBusinessEntity());

        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test
    public void BindingTemplateAccessPointAsBindingTemplateReferencalIntegritytest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointAsBindingTemplateReferencalIntegritytest");
        //create a binding template, get the key, use the key as the specific redirector
        String url = "http://juddi.apache.org";
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
        BusinessDetail saveBusiness = publication.saveBusiness(sb);

        PrintBusinessDetails(saveBusiness.getBusinessEntity());

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
        saveBusiness = publication.saveBusiness(sb);
        PrintBusinessDetails(saveBusiness.getBusinessEntity());

        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateAccessPointAsBindingTemplateINVALIDReferencalIntegritytest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointAsBindingTemplateINVALIDReferencalIntegritytest");
        //create a binding template, get the key, use the key as the specific redirector
        SaveBusiness sb;
        try {

            sb = new SaveBusiness();
            sb.setAuthInfo(authInfoJoe);

            BusinessEntity be = new BusinessEntity();
            Name bsn = new Name();
            bsn.setValue("A bogus business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);

            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateHostRedirectorTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateHostRedirectorTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test
    public void BindingTemplateAccessPointMaxLengthTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointMaxLengthTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
        
        BusinessDetail saveBusiness = publication.saveBusiness(sb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(authInfoJoe);
        db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        publication.deleteBusiness(db);
    }

    @Test(expected=ValueNotAllowedException.class)
    public void BindingTemplateAccessPointTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("BindingTemplateAccessPointTooLongTest");
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(authInfoJoe);
        BusinessEntity be = new BusinessEntity();
        Name n = new Name();
        n.setValue("A Test business");
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
            BusinessDetail saveBusiness = publication.saveBusiness(sb);
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfoJoe);
            db.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
            publication.deleteBusiness(db);
            Assert.fail("request should have been rejected");

        } catch (ValueNotAllowedException ex) {
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
        System.out.println("CreateKeyGenMaxLengthTest");

        SaveTModel st = new SaveTModel();
        st.setAuthInfo(authInfoJoe);
        TModel tm = new TModel();
        tm.setName(new Name());
        tm.getName().setValue("My Cool Company Keymodel generator");
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
		TModelDetail saveTModel = publication.saveTModel(st);
        DeleteTModel dm = new DeleteTModel();
        dm.setAuthInfo(authInfoJoe);
        dm.getTModelKey().add(validTmodelKeyGenMax);
        publication.deleteTModel(dm);

    }

    //create a oversized tmodel keygen fail
    @Test(expected=ValueNotAllowedException.class)
    public void CreateKeyGenTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("CreateKeyGenTooLongTest");

        SaveTModel st = new SaveTModel();
        st.setAuthInfo(authInfoJoe);
        TModel tm = new TModel();
        tm.setName(new Name());
        tm.getName().setValue("My Cool Company Keymodel generator");
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
			TModelDetail saveTModel = publication.saveTModel(st);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }

    }

    //create a tmodel with a key gen defined valid, with oversized Name
    @Test(expected=ValueNotAllowedException.class)
    public void CreateKeyGenKeyDescriptionTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("CreateKeyGenKeyDescriptionTooLongTest");

        SaveTModel st = new SaveTModel();
        st.setAuthInfo(authInfoJoe);
        TModel tm = new TModel();
        tm.setName(new Name());
        tm.getName().setValue("Key gen name");
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
            TModelDetail saveTModel = publication.saveTModel(st);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void CreateKeyGenKeyDescriptionLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("CreateKeyGenKeyDescriptionTooLongTest");

        SaveTModel st = new SaveTModel();
        st.setAuthInfo(authInfoJoe);
        TModel tm = new TModel();
        tm.setName(new Name());
        tm.getName().setValue("Key gen name");
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
            TModelDetail saveTModel = publication.saveTModel(st);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        } finally {
            //TODO delete the key
        }
    }

    @Test(expected=ValueNotAllowedException.class)
    public void CreateKeyGenNameLangTooLongTest() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("CreateKeyGenNameLangTooLongTest");

        SaveTModel st = new SaveTModel();
        st.setAuthInfo(authInfoJoe);
        TModel tm = new TModel();
        tm.setName(new Name());
        tm.getName().setValue("hello world");
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
            TModelDetail saveTModel = publication.saveTModel(st);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }
    }

    //create a tmodel with a key gen defined valid, regular tmodel,
    //then a business, service, binding template, tmodel instance infos, attach tmodel with some data, success
    //create a tmodel without a key gen defined- fail
    @Test(expected=InvalidKeyPassedException.class)
    public void CreateTmodelnoKeyGen() throws DispositionReportFaultMessage, RemoteException {
        System.out.println("CreateTmodelnoKeyGen");

        SaveTModel st = new SaveTModel();
        st.setAuthInfo(authInfoJoe);
        TModel tm = new TModel();
        tm.setName(new Name());
        tm.getName().setValue("My Cool Company's tmodel");
        tm.getName().setLang("en");

        tm.setTModelKey("uddi:uddi.joepublisher.com:nokeygenerator:customkey");
        st.getTModel().add(tm);
        try {
        	@SuppressWarnings("unused")
            TModelDetail saveTModel = publication.saveTModel(st);
            Assert.fail("request should have been rejected");
        } catch (ValueNotAllowedException ex) {
            HandleException(ex);
            throw ex;
        }

    }

    @Test
    public void BindingTemplateTmodelInstanceInfoTest() {
        System.out.println("BindingTemplateTmodelInstanceInfoTest");
        //create a key gen
        //create a tmodels
        //create a business, service, binding with tmodel instance infos

    }

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

    public static void PrintBusinessDetails(List<BusinessEntity> businessDetail) {


        for (int i = 0; i < businessDetail.size(); i++) {
            System.out.println("Business Detail - key: " + businessDetail.get(i).getBusinessKey());
            System.out.println("Name: " + ListToString(businessDetail.get(i).getName()));
            System.out.println("CategoryBag: " + CatBagToString(businessDetail.get(i).getCategoryBag()));
            PrintContacts(businessDetail.get(i).getContacts());
        }
    }
   
}
