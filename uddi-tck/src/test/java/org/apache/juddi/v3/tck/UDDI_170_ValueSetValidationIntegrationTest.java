/*
 * Copyright 2013 The Apache Software Foundation.
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

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.UUID;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
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
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.ErrInfo;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.KeyType;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDIValueSetValidationPortType;
import org.uddi.vs_v3.ValidateValues;

/**
 *
 * @author Alex O'Ree
 */
@WebService(name = "UDDI_ValueSetValidation_PortType", targetNamespace = "urn:uddi-org:v3_service")
public class UDDI_170_ValueSetValidationIntegrationTest implements UDDIValueSetValidationPortType {

        private static UDDIInquiryPortType inquiry = null;
        private static UDDIPublicationPortType publicationJoe = null;
        private static UDDIPublicationPortType publicationMary = null;
        static UDDISecurityPortType security = null;
        //static UDDIV vsvc = null;
        private static Log logger = LogFactory.getLog(UDDI_170_ValueSetValidationIntegrationTest.class);
        private static String authInfoJoe = null;
        private static String authInfoMary = null;
        private static UDDIClient manager;
        private static boolean VALID = true;

        @BeforeClass
        public static void startRegistry() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                logger.info("__________________________________________________________");
                logger.info("UDDI_170_ValueSetValidationIntegrationTest");
                logger.info("__________________________________________________________");
                logger.info("__________________________________________________________");
                logger.info("__________________________________________________________");
                manager = new UDDIClient();
                manager.start();

                try {
                        Transport transport = manager.getTransport("uddiv3");
                        inquiry = transport.getUDDIInquiryService();
                        publicationJoe = transport.getUDDIPublishService();
                        security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) publicationJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                        transport = manager.getTransport("uddiv3");
                        inquiry = transport.getUDDIInquiryService();
                        publicationMary = transport.getUDDIPublishService();
                        security = transport.getUDDISecurityService();
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) publicationMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        }

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                manager.stop();
        }
        final static String VSV_KEY = "uddi:juddi.apache.org:node1";
        //final static String VSV_BT_KEY = "uddi:juddi.apache.org:servicebindings-valueset-cp";
        final static String TMODEL = "validated.checked.tmodel";

        /**
         * this will confirm that the uddi registry will call out to me during a
         * vsv validation
         *
         * @throws Exception
         */
        @Test
        public void testVSV() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("testVSV");
                BusinessEntity SaveBusiness = null;
                BusinessEntity SaveVSVCallbackService = null;
                try {
                        Reset();
                        VALID = true;
                        SaveVSVCallbackService = SaveVSVCallbackService(authInfoJoe, publicationJoe);
                        SaveCheckedTModel(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL, SaveVSVCallbackService.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey());
                        logger.info("Saving a business using those values");
                        SaveBusiness = SaveBusiness(authInfoJoe, true, TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);

                        DeleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL, publicationJoe);

                } catch (Exception ex) {
                        logger.error(ex);
                        Assert.fail("unexpected failure " + ex.getMessage());
                } finally {
                        if (SaveBusiness != null) {
                                DeleteBusiness(authInfoJoe, SaveBusiness.getBusinessKey(), publicationJoe);
                        }
                        if (SaveVSVCallbackService != null) {
                                DeleteBusiness(authInfoJoe, SaveVSVCallbackService.getBusinessKey(), publicationJoe);
                        }

                }
                Assert.assertTrue(messagesReceived == 1);
        }

        @Test
        public void testVSVInvalid() throws Exception {
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                Assume.assumeTrue(TckPublisher.isEnabled());
                System.out.println("testVSVInvalid");
                BusinessEntity SaveBusiness = null;
                BusinessEntity SaveVSVCallbackService = null;
                try {
                        Reset();
                        VALID = false;
                        SaveVSVCallbackService = SaveVSVCallbackService(authInfoJoe, publicationJoe);
                        SaveCheckedTModel(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL, SaveVSVCallbackService.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey());
                        logger.info("Saving a business using those values");
                        SaveBusiness = SaveBusiness(authInfoJoe, false, TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);
                        Assert.assertTrue(messagesReceived == 1);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                } finally {
                        if (SaveBusiness != null) {
                                DeleteBusiness(authInfoJoe, SaveBusiness.getBusinessKey(), publicationJoe);
                        }
                        if (SaveVSVCallbackService != null) {
                                DeleteBusiness(authInfoJoe, SaveVSVCallbackService.getBusinessKey(), publicationJoe);
                        }
                        DeleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL, publicationJoe);

                }
        }

        private BusinessEntity SaveBusiness(String authInfoJoe, boolean isValid, String key) throws Exception {
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("VSV", null));
                be.setCategoryBag(new CategoryBag());
                if (isValid) {
                        be.getCategoryBag().getKeyedReference().add(new KeyedReference(key, "name", "abcdefg"));
                } else {
                        be.getCategoryBag().getKeyedReference().add(new KeyedReference(key, "name", "qwerty"));
                }
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                return saveBusiness.getBusinessEntity().get(0);
        }

        private void SaveCheckedTModel(String key, String binding) throws Exception {
                TModel tm = new TModel();
                tm.setTModelKey(key);
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(
                     new KeyedReference(UDDIConstants.IS_VALIDATED_BY, UDDIConstants.IS_VALIDATED_BY_KEY_NAME, binding));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publicationJoe.saveTModel(stm);
        }

        @Override
        @WebMethod(operationName = "validate_values", action = "validate_values")
        @WebResult(name = "dispositionReport", targetNamespace = "urn:uddi-org:api_v3", partName = "body")
        @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
        public DispositionReport validateValues(
             @WebParam(name = "validate_values", targetNamespace = "urn:uddi-org:vs_v3", partName = "body") ValidateValues body)
             throws DispositionReportFaultMessage, RemoteException {
                messagesReceived++;
                if (VALID) {
                        DispositionReport dispositionReport = new DispositionReport();
                        dispositionReport.getResult().add(new Result());
                        return dispositionReport;
                }
                DispositionReport dispositionReport = new DispositionReport();
                Result r = new Result();
                r.setKeyType(KeyType.T_MODEL_KEY);
                r.setErrno(20200);
                r.setErrInfo(new ErrInfo());
                r.getErrInfo().setErrCode("E_invalidValue");
                r.getErrInfo().setValue("E_invalidValue");

                dispositionReport.getResult().add(r);
                throw new DispositionReportFaultMessage("error", dispositionReport);
        }

        private BusinessEntity SaveVSVCallbackService(String auth, UDDIPublicationPortType port) throws Exception {
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(auth);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("VSV business", null));
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name("VSV callout", null));
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint(url, "endPoint"));
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);

                return port.saveBusiness(sb).getBusinessEntity().get(0);
        }

        private void DeleteTModel(String auth, String key, UDDIPublicationPortType port) throws Exception {
                DeleteTModel db = new DeleteTModel();
                db.setAuthInfo(auth);
                db.getTModelKey().add(key);
                port.deleteTModel(db);
        }

        private void DeleteBusiness(String auth, String key, UDDIPublicationPortType port) throws Exception {
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(auth);
                db.getBusinessKey().add(key);
                port.deleteBusiness(db);
        }
        Endpoint ep = null;
        String url = null;
        int messagesReceived = 0;

        private void Reset() throws Exception {
                messagesReceived = 0;
                if (ep == null || !ep.isPublished()) {
                        int httpPort = 9600 + new Random().nextInt(99);
                        String hostname = TckPublisher.getProperties().getProperty("bindaddress");
                        if (hostname == null) {
                                hostname = InetAddress.getLocalHost().getHostName();
                        }
                        url = "http://" + hostname + ":" + httpPort + "/" + UUID.randomUUID().toString();
                        logger.info("Firing up embedded endpoint at " + url);
                        do {
                                try {

                                        ep = Endpoint.publish(url, this);
                                        httpPort = 9600 + new Random().nextInt(99);
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                }
                        } while (ep != null && !ep.isPublished());

                }

        }

        /**
         * value set caching service TODO
         */
        //@Test
        public void testVSCUnknownItem() {
                /*
                 *   
                 * E_invalidKeyPassed: Signifies that the tModelKey passed did not match with 
                 * the uddiKey of any known tModels.  The details on the invalid key SHOULD be 
                 * included in the dispositionReport element.
                 * 
                 * E_noValuesAvailable: Signifies that no values could be returned.
                 */
                //Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
        }
        //TODO ·         E_invalidValue: Signifies that the chunkToken value supplied is either invalid or has expired.
        //TODO maybe? ·         E_unsupported: Signifies that the Web service does not support this API.
        //this may be untestable unless the endpoint exists but isn't implemented

        public void DerviedFromValid() throws Exception {

                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("DerviedFromValid");
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_DERIVED_FROM, "", "uddi:uddi.org:categorization:nodes"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
        }

        @Test
        public void DerviedFromInValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                  System.out.println("DerviedFromInValid");
                try {
                        TModel tm = new TModel();
                        //tm.setTModelKey();
                        tm.setCategoryBag(new CategoryBag());
                        tm.setName(new Name("My Custom validated key", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_DERIVED_FROM, "", "uddi:juddi.apache.org:" + UUID.randomUUID().toString()));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void EntitKeyValuesValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("EntitKeyValuesValid");
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.ENTITY_KEY_VALUES, "", "tModelKey"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);

        }

        @Test
        public void EntitKeyValuesInValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("EntitKeyValuesInValid");
                try {
                        TModel tm = new TModel();
                        //tm.setTModelKey();
                        tm.setCategoryBag(new CategoryBag());
                        tm.setName(new Name("My Custom validated key", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.ENTITY_KEY_VALUES, "", "asdasdasd"));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        @Ignore
        public void UDDINodeValid() throws Exception {
                //This test should be ignored, only one business per node is allowed to be the node
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                 System.out.println("UDDINodeValid");
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("test", "en"));
                be.setCategoryBag(new CategoryBag());
                be.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:nodes", "", "node"));
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
        }

        @Test
        public void UDDINodeInValid1() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("UDDINodeInValid1");
                try {
                        BusinessEntity be = new BusinessEntity();
                        be.getName().add(new Name("test", "en"));
                        be.setCategoryBag(new CategoryBag());
                        be.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:nodes", "", "asdasd"));
                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void UDDINodeInValid2() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("UDDINodeInValid2");
                try {
                        BusinessEntity be = new BusinessEntity();
                        be.getName().add(new Name("test", "en"));
                        be.setBusinessServices(new BusinessServices());
                        BusinessService bs = new BusinessService();
                        bs.setCategoryBag(new CategoryBag());
                        bs.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:nodes", "", "asdasd"));
                        be.getBusinessServices().getBusinessService().add(bs);
                        be.setCategoryBag(new CategoryBag());

                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void OwningBusinessValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("OwningBusinessValid");
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.OWNING_BUSINESS, "", "uddi:juddi.apache.org:node1"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
        }

        @Test
        public void OwningBusinessInValid() throws Exception {
                System.out.println("OwningBusinessInValid");
                try {
                        Assume.assumeTrue(TckPublisher.isEnabled());
                        Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                        BusinessEntity be = new BusinessEntity();
                        be.getName().add(new Name("test", "en"));
                        be.setCategoryBag(new CategoryBag());
                        be.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.OWNING_BUSINESS, "", "uddi:juddi.apache.org:" + UUID.randomUUID().toString()));
                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);
                        DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void OwningBusinessInValid2() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("OwningBusinessInValid2");
                try {
                        TModel tm = new TModel();
                        //tm.setTModelKey();
                        tm.setCategoryBag(new CategoryBag());
                        tm.setName(new Name("My Custom validated key", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.OWNING_BUSINESS, "", "uddi:juddi.apache.org:" + UUID.randomUUID().toString()));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void TypeTmodelValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("TypeTmodelValid");
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "namespace"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);

        }

        @Test
        public void TypeTModelInValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("TypeTModelInValid");
                try {
                        TModel tm = new TModel();
                        //tm.setTModelKey();
                        tm.setCategoryBag(new CategoryBag());
                        tm.setName(new Name("My Custom validated key", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "wsdlDeployment"));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void TypeBindingInValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("TypeBindingInValid");
                BusinessDetail saveBusiness = null;
                try {
                        BusinessEntity be = new BusinessEntity();
                        be.getName().add(new Name("test", "en"));
                        be.setBusinessServices(new BusinessServices());
                        be.getBusinessServices().getBusinessService().add(new BusinessService());
                        be.getBusinessServices().getBusinessService().get(0).getName().add(new Name("test", "en"));
                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(authInfoJoe);
                        saveBusiness = publicationJoe.saveBusiness(sb);
                        SaveBinding sbb = new SaveBinding();

                        sbb.setAuthInfo(authInfoJoe);
                        BindingTemplate bt = new BindingTemplate();
                        bt.setServiceKey(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getServiceKey());
                        bt.setAccessPoint(new AccessPoint("http://test", "endPoint"));
                        bt.setCategoryBag(new CategoryBag());
                        bt.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "namespace"));
                        publicationJoe.saveBinding(sbb);
                        DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void TypeBindingValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("TypeBindingValid");
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("test", "en"));
                be.setBusinessServices(new BusinessServices());
                be.getBusinessServices().getBusinessService().add(new BusinessService());
                be.getBusinessServices().getBusinessService().get(0).getName().add(new Name("test", "en"));
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                sb.getBusinessEntity().add(be);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(sb);

                SaveBinding sbb = new SaveBinding();
                sbb.setAuthInfo(authInfoJoe);
                BindingTemplate bt = new BindingTemplate();
                bt.setServiceKey(saveBusiness.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getServiceKey());
                bt.setAccessPoint(new AccessPoint("http://test", "endPoint"));
                bt.setCategoryBag(new CategoryBag());
                bt.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "wsdlDeployment"));
                sbb.getBindingTemplate().add(bt);
                publicationJoe.saveBinding(sbb);
                DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
        }

        @Test
        public void ValidatedByInValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ValidatedByInValid");
                BusinessEntity SaveVSVCallbackService = null;
                try {
                        SaveVSVCallbackService = SaveVSVCallbackService(authInfoJoe, publicationJoe);

                        TModel tm = new TModel();
                        //tm.setTModelKey();
                        tm.setCategoryBag(new CategoryBag());
                        tm.setName(new Name("My Custom validated key", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_VALIDATED_BY, UDDIConstants.IS_VALIDATED_BY_KEY_NAME, TckTModel.JOE_PUBLISHER_KEY_PREFIX + UUID.randomUUID().toString()));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {
                        DeleteBusiness(authInfoJoe, SaveVSVCallbackService.getBusinessKey(), publicationJoe);
                }
        }

        @Test
        public void ValidatedByValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());

                System.out.println("ValidatedByValid");
                BusinessEntity SaveVSVCallbackService = SaveVSVCallbackService(authInfoJoe, publicationJoe);

                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_VALIDATED_BY, UDDIConstants.IS_VALIDATED_BY_KEY_NAME, SaveVSVCallbackService.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey()));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                DeleteBusiness(authInfoJoe, SaveVSVCallbackService.getBusinessKey(), publicationJoe);

        }

        @Test
        public void ReplacedByValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByValid");
                TModel tm = new TModel();
                tm.setName(new Name("My old tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);

                tm = new TModel();
                tm.setName(new Name("My new tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setIdentifierBag(new IdentifierBag());
                tm.getIdentifierBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveTModel.getTModel().get(0).getTModelKey()));
                stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel1 = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                DeleteTModel(authInfoJoe, saveTModel1.getTModel().get(0).getTModelKey(), publicationJoe);
        }

        @Test
        public void ReplacedByValid2() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByValid2");
                TModel tm = new TModel();
                tm.setName(new Name("My old tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);

                tm = new TModel();
                tm.setName(new Name("My new tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveTModel.getTModel().get(0).getTModelKey()));
                stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel1 = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                DeleteTModel(authInfoJoe, saveTModel1.getTModel().get(0).getTModelKey(), publicationJoe);
        }

        @Test
        public void ReplacedByValid3() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByValid3");
                BusinessEntity tm = new BusinessEntity();
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setIdentifierBag(new IdentifierBag());
                tm.getIdentifierBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveBusiness.getBusinessEntity().get(0).getBusinessKey()));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness1 = publicationJoe.saveBusiness(stm);
                DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                DeleteBusiness(authInfoJoe, saveBusiness1.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
        }

        @Test
        public void ReplacedByValid4() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByValid4");
                BusinessEntity tm = new BusinessEntity();
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveBusiness.getBusinessEntity().get(0).getBusinessKey()));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness1 = publicationJoe.saveBusiness(stm);
                DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                DeleteBusiness(authInfoJoe, saveBusiness1.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
        }

        @Test
        public void ReplacedByValid5Projected() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByValid5Projected");
                BusinessEntity tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "testbiz");
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);

                tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "oldbiz");
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", TckTModel.JOE_PUBLISHER_KEY_PREFIX + "testbiz"));
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(stm);
                DeleteBusiness(authInfoJoe, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
        }

        @Test
        public void ReplacedByValid6DifferentOwners() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByValid6DifferentOwners");
                BusinessEntity tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.MARY_KEY_PREFIX + "testbiz");
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoMary);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness = publicationMary.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "oldbiz");
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", TckTModel.MARY_KEY_PREFIX + "testbiz"));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness1 = publicationJoe.saveBusiness(stm);

                DeleteBusiness(authInfoJoe, saveBusiness1.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                DeleteBusiness(authInfoMary, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationMary);
        }

        @Test
        public void ReplacedByValid7DifferentOwners() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByValid7DifferentOwners");
                BusinessEntity tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.MARY_KEY_PREFIX + "testbiz");
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoMary);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness = publicationJoe.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "oldbiz");
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setIdentifierBag(new IdentifierBag());
                tm.getIdentifierBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", TckTModel.MARY_KEY_PREFIX + "testbiz"));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness1 = publicationJoe.saveBusiness(stm);

                DeleteBusiness(authInfoJoe, saveBusiness1.getBusinessEntity().get(0).getBusinessKey(), publicationJoe);
                DeleteBusiness(authInfoMary, saveBusiness.getBusinessEntity().get(0).getBusinessKey(), publicationMary);
        }

        /**
         * reference undefined tmodel
         *
         * @throws Exception
         */
        @Test
        public void ReplacedByInValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByInValid");
                try {
                        TModel tm = new TModel();
                        tm.setName(new Name("My new tmodel", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.setCategoryBag(new CategoryBag());
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", TckTModel.JOE_PUBLISHER_KEY_PREFIX + UUID.randomUUID().toString()));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        /**
         * reference business key
         *
         * @throws Exception
         */
        @Test
        public void ReplacedByInValid2() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("ReplacedByInValid2");
                try {
                        TModel tm = new TModel();
                        tm.setName(new Name("My new tmodel", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.setCategoryBag(new CategoryBag());
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", "uddi:juddi.apache.org:node1"));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }
        }

        @Test
        public void RelationshipsValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("RelationshipsValid");
                TModel tm = new TModel();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.RELATIONSHIPS, "", "peer-peer"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);

        }

        @Test
        public void RelationshipsInValid() throws Exception {
                Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());
                System.out.println("RelationshipsInValid");
                try {
                        TModel tm = new TModel();
                        //tm.setTModelKey();
                        tm.setCategoryBag(new CategoryBag());
                        tm.setName(new Name("My Custom validated key", "en"));
                        tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                        tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.RELATIONSHIPS, "", "asdasdasd"));
                        SaveTModel stm = new SaveTModel();
                        stm.setAuthInfo(authInfoJoe);
                        stm.getTModel().add(tm);
                        TModelDetail saveTModel = publicationJoe.saveTModel(stm);
                        DeleteTModel(authInfoJoe, saveTModel.getTModel().get(0).getTModelKey(), publicationJoe);
                        Assert.fail("unexpected success");
                } catch (Exception ex) {
                        logger.info("Expected failure " + ex.getMessage());
                        logger.debug("Expected failure " + ex.getMessage(), ex);
                } finally {

                }

        }

}
