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
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
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
public class UDDI_170_ValueSetValidation implements UDDIValueSetValidationPortType {

        private static UDDIInquiryPortType inquiry = null;
        private static UDDIPublicationPortType publication = null;
        static UDDISecurityPortType security = null;
        //static UDDIV vsvc = null;
        private static Log logger = LogFactory.getLog(UDDI_170_ValueSetValidation.class);
        private static String authInfoJoe = null;
        private static UDDIClient manager;

        @BeforeClass
        public static void startRegistry() throws ConfigurationException {

                manager = new UDDIClient();
                manager.start();

                try {
                        Transport transport = manager.getTransport();
                        inquiry = transport.getUDDIInquiryService();
                        publication = transport.getUDDIPublishService();
                        security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                manager.stop();
        }
        final static String VSV_KEY = "uddi:juddi.apache.org:businesses-asf";
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
                Assume.assumeTrue(TckPublisher.isValueSetAPIEnabled());

                BusinessEntity SaveBusiness = null;
                BusinessEntity SaveVSVCallbackService = null;
                try {
                        Reset();
                        SaveVSVCallbackService = SaveVSVCallbackService();
                        SaveCheckedTModel(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL, SaveVSVCallbackService.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey());
                        logger.info("Saving a business using those values");
                        SaveBusiness = SaveBusiness(authInfoJoe, true, TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);
                        Assert.assertTrue(messagesReceived == 1);

                } finally {
                        DeleteBusiness(authInfoJoe, SaveBusiness.getBusinessKey());
                        DeleteBusiness(authInfoJoe, SaveVSVCallbackService.getBusinessKey());
                        DeleteCheckedTModel(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);
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
                BusinessDetail saveBusiness = publication.saveBusiness(sb);
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
                publication.saveTModel(stm);
        }

        @Override
        public DispositionReport validateValues(ValidateValues body) throws DispositionReportFaultMessage, RemoteException {
                messagesReceived++;
                return new DispositionReport();
        }

        private BusinessEntity SaveVSVCallbackService() throws Exception {
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("VSV business", null));
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name("VSV callout", null));
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint(url, "endPoint"));
                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);

                return publication.saveBusiness(sb).getBusinessEntity().get(0);
        }

        private void DeleteCheckedTModel(String string) throws Exception {
                DeleteTModel db = new DeleteTModel();
                db.setAuthInfo(authInfoJoe);
                db.getTModelKey().add(string);
                publication.deleteTModel(db);
        }

        private void DeleteBusiness(String authInfoJoe, String string) throws Exception {
                DeleteBusiness db = new DeleteBusiness();
                db.setAuthInfo(authInfoJoe);
                db.getBusinessKey().add(string);
                publication.deleteBusiness(db);
        }
        Endpoint ep = null;
        String url = null;
        int messagesReceived = 0;

        private void Reset() throws Exception {
                messagesReceived = 0;
                if (ep == null) {
                        int httpPort = 9600 + new Random().nextInt(99);
                        String hostname = InetAddress.getLocalHost().getHostName();
                        url = "http://" + hostname + ":" + httpPort + "/" + UUID.randomUUID().toString();
                        do {
                                try {

                                        ep = Endpoint.publish(url, this);
                                        httpPort = 9600 + new Random().nextInt(99);
                                } catch (Exception ex) {
                                }
                        } while (ep != null && ep.isPublished());

                }

        }

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
}