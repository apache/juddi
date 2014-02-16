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

import java.util.GregorianCalendar;
import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

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
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.custody_v3.TransferToken;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This test class provides test cases of items discovered or reported through
 * the Juddi JIRA issue tracker The idea is that these tests apply to any UDDI
 * registry and that the tests are either implied or derived requirements
 * defined in the UDDI spec that were missed in other test cases for Juddi.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_150_CustodyTransferIntegrationTest {

        private static Log logger = LogFactory.getLog(UDDI_150_CustodyTransferIntegrationTest.class);
        static UDDISecurityPortType security = null;
        static UDDIInquiryPortType inquiryJoe = null;
        static UDDIPublicationPortType publishJoe = null;
        static UDDICustodyTransferPortType custodyTransferPortTypeJoe = null;
        static TckTModel tckTModelJoe = null;
        static UDDIInquiryPortType inquirySam = null;
        static UDDIPublicationPortType publishSam = null;
        static UDDICustodyTransferPortType custodyTransferPortTypeSam = null;
        protected static String authInfoJoe = null;
        protected static String authInfoSam = null;
        private static UDDIClient manager;
        static final String TRANS = "The transaction has been rolled back";
        static final String MISSING_RESOURCE = "Can't find resource for bundle";

        @AfterClass
        public static void stopManager() throws ConfigurationException {
             Assume.assumeTrue(TckPublisher.isEnabled());
                tckTModelJoe.deleteCreatedTModels(authInfoJoe);
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
                        security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        //Assert.assertNotNull(authInfoJoe);
                        //Assert.assertNotNull(authInfoSam);


                        publishJoe = transport.getUDDIPublishService();
                        inquiryJoe = transport.getUDDIInquiryService();
                        custodyTransferPortTypeJoe = transport.getUDDICustodyTransferService();

                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publishJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) custodyTransferPortTypeJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                        tckTModelJoe = new TckTModel(publishJoe, inquiryJoe);

                        transport = manager.getTransport("uddiv3");
                        publishSam = transport.getUDDIPublishService();
                        inquirySam = transport.getUDDIInquiryService();
                        custodyTransferPortTypeSam = transport.getUDDICustodyTransferService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publishJoe, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) custodyTransferPortTypeJoe, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        }

                        transport = manager.getTransport("uddiv3");
                        UDDIPublicationPortType uddiPublishService = transport.getUDDIPublishService();
                        UDDIInquiryPortType uddiInquiryService = transport.getUDDIInquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publishJoe, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) custodyTransferPortTypeJoe, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        }

                        TckTModel tckTModelUDDI = new TckTModel(uddiPublishService, uddiInquiryService);
                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        tckTModelUDDI.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModelUDDI.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
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

        /**
         * tests a user to user transfer on the same node
         *
         * @throws Exception
         */
        @Test
        public void ValidTransfer() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("ValidTransfer");
                DatatypeFactory df = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());
                XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);


                BusinessEntity myBusEntity = new BusinessEntity();
                Name myBusName = new Name();
                myBusName.setLang("en");
                myBusName.setValue("ValidTransfer UDDI's Business" + " " + xcal.toString());
                myBusEntity.getName().add(myBusName);
                myBusEntity.setBusinessServices(new BusinessServices());
                myBusEntity.getBusinessServices().getBusinessService().add(CreateBusiness("UDDI"));
                SaveBusiness sb = new SaveBusiness();
                sb.getBusinessEntity().add(myBusEntity);
                sb.setAuthInfo(authInfoJoe);
                BusinessDetail bd = publishJoe.saveBusiness(sb);

                String keyJoeBiz = bd.getBusinessEntity().get(0).getBusinessKey();
                //String keyJoeBizSvc = bd.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getServiceKey();



                myBusEntity = new BusinessEntity();
                myBusName = new Name();
                myBusName.setLang("en");
                myBusName.setValue("ValidTransfer Root's Business" + " " + xcal.toString());
                myBusEntity.getName().add(myBusName);
                myBusEntity.setBusinessServices(new BusinessServices());
                myBusEntity.getBusinessServices().getBusinessService().add(CreateBusiness("root"));
                sb = new SaveBusiness();
                sb.getBusinessEntity().add(myBusEntity);
                sb.setAuthInfo(authInfoSam);
                bd = publishSam.saveBusiness(sb);

                String keySamBiz = bd.getBusinessEntity().get(0).getBusinessKey();
                //String keySamBizSvc = bd.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getServiceKey();

                //transfers from Joe to Sam
                KeyBag kb = new KeyBag();
                kb.getKey().add(keyJoeBiz);

                Holder<String> nodeidOUT = new Holder<String>();
                Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
                Holder<byte[]> tokenOUT = new Holder<byte[]>();
                custodyTransferPortTypeJoe.getTransferToken(authInfoJoe, kb, nodeidOUT, expiresOUT, tokenOUT);

                //sam accepts
                TransferEntities te = new TransferEntities();
                te.setAuthInfo(authInfoSam);
                te.setKeyBag(kb);
                TransferToken tt = new TransferToken();
                tt.setExpirationTime(expiresOUT.value);
                tt.setNodeID(nodeidOUT.value);
                tt.setOpaqueToken(tokenOUT.value);
                te.setTransferToken(tt);

                custodyTransferPortTypeSam.transferEntities(te);

                //confirm the transfer
                GetOperationalInfo go = new GetOperationalInfo();
                go.setAuthInfo(authInfoSam);
                go.getEntityKey().add(keySamBiz);
                go.getEntityKey().add(keyJoeBiz);
                OperationalInfos operationalInfo = inquirySam.getOperationalInfo(go);

                for (int i = 0; i < operationalInfo.getOperationalInfo().size(); i++) {
                        if (operationalInfo.getOperationalInfo().get(i).getEntityKey().equalsIgnoreCase(keyJoeBiz)) {
                                Assert.assertEquals(operationalInfo.getOperationalInfo().get(i).getAuthorizedName(), (TckPublisher.getSamPublisherId()));

                        }
                }
                System.out.println("Business Entity transfered successfull");

                //note, we transfered ownership here so sam has to delete both of them
                TckCommon.DeleteBusiness(keyJoeBiz, authInfoSam, publishSam);
                TckCommon.DeleteBusiness(keySamBiz, authInfoSam, publishSam);


        }

        private BusinessService CreateBusiness(String root) {
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name());
                bs.getName().get(0).setValue(root + "'s callback endpoint");
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint());
                bt.getAccessPoint().setValue("http://localhost:9999/" + root);
                bt.getAccessPoint().setUseType("endPoint");
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                return bs;
        }

        @Test
        public void InvalidTransferTokenNullKeybag() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        //transfers from Joe to Sam
                        KeyBag kb = null;

                        Holder<String> nodeidOUT = new Holder<String>();
                        Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
                        Holder<byte[]> tokenOUT = new Holder<byte[]>();
                        custodyTransferPortTypeJoe.getTransferToken(authInfoJoe, kb, nodeidOUT, expiresOUT, tokenOUT);
                        Assert.fail();
                } catch (Exception ex) {
                        logger.info("Expected exception: " + ex.getMessage());
                }

        }

        @Test
        public void InvalidTransferTokenEmptyKeybag() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        //transfers from Joe to Sam
                        KeyBag kb = new KeyBag();
                        //kb.getKey().add(keyJoeBiz);

                        Holder<String> nodeidOUT = new Holder<String>();
                        Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
                        Holder<byte[]> tokenOUT = new Holder<byte[]>();
                        custodyTransferPortTypeJoe.getTransferToken(authInfoJoe, kb, nodeidOUT, expiresOUT, tokenOUT);

                        Assert.fail();
                } catch (Exception ex) {
                        logger.info("Expected exception: " + ex.getMessage());
                }

        }

        @Test
        public void InvalidTransferTokenEmptyNullAuthToken() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                String keyJoeBiz = null;
                try {
                        DatatypeFactory df = DatatypeFactory.newInstance();
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);


                        BusinessEntity myBusEntity = new BusinessEntity();
                        Name myBusName = new Name();
                        myBusName.setLang("en");
                        myBusName.setValue("InvalidTransferTokenEmptyNullAuthToken UDDI's Business" + " " + xcal.toString());
                        myBusEntity.getName().add(myBusName);
                        myBusEntity.setBusinessServices(new BusinessServices());
                        myBusEntity.getBusinessServices().getBusinessService().add(CreateBusiness("UDDI"));
                        SaveBusiness sb = new SaveBusiness();
                        sb.getBusinessEntity().add(myBusEntity);
                        sb.setAuthInfo(authInfoJoe);
                        BusinessDetail bd = publishJoe.saveBusiness(sb);

                        keyJoeBiz = bd.getBusinessEntity().get(0).getBusinessKey();

                        //transfers from Joe to Sam
                        KeyBag kb = new KeyBag();
                        kb.getKey().add(keyJoeBiz);

                        Holder<String> nodeidOUT = new Holder<String>();
                        Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
                        Holder<byte[]> tokenOUT = new Holder<byte[]>();
                        custodyTransferPortTypeJoe.getTransferToken(null, kb, nodeidOUT, expiresOUT, tokenOUT);

                        Assert.fail();
                } catch (Exception ex) {
                        logger.info("Expected exception: " + ex.getMessage());

                } finally {
                        TckCommon.DeleteBusiness(keyJoeBiz, authInfoJoe, publishJoe);

                }

        }

        /**
         * a valid transfer token issued, then modified out of band, this should
         * fail
         */
        @Test
        public void InvalidTransferTokenModified() {
             Assume.assumeTrue(TckPublisher.isEnabled());
                String keySamBiz = null;
                String keyJoeBiz = null;
                try {
                        DatatypeFactory df = DatatypeFactory.newInstance();
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);


                        BusinessEntity myBusEntity = new BusinessEntity();
                        Name myBusName = new Name();
                        myBusName.setLang("en");
                        myBusName.setValue("InvalidTransferTokenModified UDDI's Business" + " " + xcal.toString());
                        myBusEntity.getName().add(myBusName);
                        myBusEntity.setBusinessServices(new BusinessServices());
                        myBusEntity.getBusinessServices().getBusinessService().add(CreateBusiness("UDDI"));
                        SaveBusiness sb = new SaveBusiness();
                        sb.getBusinessEntity().add(myBusEntity);
                        sb.setAuthInfo(authInfoJoe);
                        BusinessDetail bd = publishJoe.saveBusiness(sb);

                        keyJoeBiz = bd.getBusinessEntity().get(0).getBusinessKey();



                        myBusEntity = new BusinessEntity();
                        myBusName = new Name();
                        myBusName.setLang("en");
                        myBusName.setValue("Root's Business" + " " + xcal.toString());
                        myBusEntity.getName().add(myBusName);
                        myBusEntity.setBusinessServices(new BusinessServices());
                        myBusEntity.getBusinessServices().getBusinessService().add(CreateBusiness("root"));
                        sb = new SaveBusiness();
                        sb.getBusinessEntity().add(myBusEntity);
                        sb.setAuthInfo(authInfoSam);
                        bd = publishSam.saveBusiness(sb);

                        keySamBiz = bd.getBusinessEntity().get(0).getBusinessKey();
                        //String keySamBizSvc = bd.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getServiceKey();

                        //transfers from Joe to Sam
                        KeyBag kb = new KeyBag();
                        kb.getKey().add(keyJoeBiz);

                        Holder<String> nodeidOUT = new Holder<String>();
                        Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
                        Holder<byte[]> tokenOUT = new Holder<byte[]>();
                        custodyTransferPortTypeJoe.getTransferToken(authInfoJoe, kb, nodeidOUT, expiresOUT, tokenOUT);


                        //sam accepts
                        TransferEntities te = new TransferEntities();
                        te.setAuthInfo(authInfoSam);
                        te.setKeyBag(kb);
                        te.getKeyBag().getKey().add("uddi:uddi.joepublisher.com:" + UUID.randomUUID().toString());
                        TransferToken tt = new TransferToken();
                        tt.setExpirationTime(expiresOUT.value);
                        tt.setNodeID(nodeidOUT.value);
                        tt.setOpaqueToken(tokenOUT.value);
                        te.setTransferToken(tt);

                        custodyTransferPortTypeSam.transferEntities(te);
                        Assert.fail();
                } catch (Exception ex) {
                        //  HandleException(ex);
                        logger.info("Expected exception: " + ex.getMessage());
                } finally {
                        TckCommon.DeleteBusiness(keyJoeBiz, authInfoJoe, publishJoe);
                        TckCommon.DeleteBusiness(keySamBiz, authInfoSam, publishSam);
                }

        }

        @Test
        public void InvalidTransferTokenServiceDoesntExist() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                try {
                        //transfers from Joe to Sam
                        KeyBag kb = new KeyBag();
                        kb.getKey().add("uddi:uddi.joepublisher.com:" + UUID.randomUUID().toString());

                        Holder<String> nodeidOUT = new Holder<String>();
                        Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
                        Holder<byte[]> tokenOUT = new Holder<byte[]>();
                        custodyTransferPortTypeJoe.getTransferToken(authInfoJoe, kb, nodeidOUT, expiresOUT, tokenOUT);
                        Assert.fail();
                } catch (Exception ex) {
                        //HandleException(ex);
                        logger.info("Expected exception: " + ex.getMessage());
                }
        }

        @Test
        public void ExpiredTransferToken() {
                //TODO
        }
}
