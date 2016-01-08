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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.tck.TckBindingTemplate;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelList;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_070_FindEntityTest {

        private static Log logger = LogFactory.getLog(API_070_FindEntityTest.class);

        private static API_010_PublisherTest api010 = new API_010_PublisherTest();
        private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBindingTemplate tckBindingTemplate = new TckBindingTemplate(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckFindEntity tckFindEntity = new TckFindEntity(new UDDIInquiryImpl());

        private static String authInfoJoe = null;

        @BeforeClass
        public static void setup() throws ConfigurationException {
                Registry.start();
                logger.debug("Getting auth token..");
                try {
                         tckTModel.saveUDDIPublisherTmodel(authInfoJoe);
                        api010.saveJoePublisher();
                        authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                } catch (RemoteException e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                Registry.stop();
        }

        @Test
        public void findEntities() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe, true);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        //uddi:tmodelkey:categories:bindings
                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
                        tckFindEntity.findBusiness();
                        tckFindEntity.findService(null);
                        tckFindEntity.findBinding(null);
                        tckFindEntity.findTModel(null);
                        tckFindEntity.findAllBusiness();
                        tckFindEntity.getNonExitingBusiness();
                } finally {
                        tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }

        }

        @Test
        public void findSignedEntities() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe, true);
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

        @Test
        public void JUDDI_843_tModel() throws Exception {

                UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

                UDDIPublicationImpl pub = new UDDIPublicationImpl();
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                TModel tm = new TModel();
                tm.setName(new Name("Buenos Dias", "es-US"));
                stm.getTModel().add(tm);
                pub.saveTModel(stm);

                FindTModel ftm = new FindTModel();
                ftm.setAuthInfo(authInfoJoe);
                ftm.setName(new Name("%", "es-US"));
                ftm.setFindQualifiers(new FindQualifiers());
                ftm.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                ftm.getFindQualifiers().getFindQualifier().add(UDDIConstants.SORT_BY_NAME_ASC);
                ftm.getFindQualifiers().getFindQualifier().add(UDDIConstants.CASE_INSENSITIVE_MATCH);
                TModelList findTModel = inquiry.findTModel(ftm);
                Assert.assertNotNull(findTModel);
                Assert.assertNotNull(findTModel.getTModelInfos());
                Assert.assertNotNull(findTModel.getTModelInfos().getTModelInfo());
                boolean found = false;
                for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                        if (findTModel.getTModelInfos().getTModelInfo().get(i).getName().getValue().equalsIgnoreCase("Buenos Dias")
                                && findTModel.getTModelInfos().getTModelInfo().get(i).getName().getLang().equalsIgnoreCase("es-US")) {
                                found = true;
                                break;
                        }
                }
                Assert.assertTrue("tMdoel search by name with language defined failed, item not found", found);

        }

        /**
         * matches for tModel by catbag, default settings, exact match
         * @throws Exception 
         */
        @Test
        public void JUDDI_899() throws Exception {
                UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

            
                FindTModel ftm = new FindTModel();
                ftm.setAuthInfo(authInfoJoe);
                ftm.setCategoryBag(new CategoryBag());
                ftm.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "uddi-org:types:keyGenerator", "keyGenerator"));
                TModelList findTModel = inquiry.findTModel(ftm);
                Assert.assertNotNull(findTModel);
                Assert.assertNotNull(findTModel.getTModelInfos());
                Assert.assertNotNull(findTModel.getTModelInfos().getTModelInfo());

        }
        
        /**
         * matches for tModel by catbag, default settings, approximate match
         * @throws Exception 
         */
        @Test
        public void JUDDI_899_1() throws Exception {
                UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

                FindTModel ftm = new FindTModel();
                ftm.setFindQualifiers(new FindQualifiers());
                ftm.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                ftm.setAuthInfo(authInfoJoe);
                ftm.setCategoryBag(new CategoryBag());
                ftm.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "uddi-org:types:keyGenerator", "key%"));
                TModelList findTModel = inquiry.findTModel(ftm);
                Assert.assertNotNull(findTModel);
                Assert.assertNotNull(findTModel.getTModelInfos());
                Assert.assertNotNull(findTModel.getTModelInfos().getTModelInfo());

        }
        
         /**
         * matches for tModel by catbag, default settings, case insensitive 
         * @throws Exception 
         */
        @Test
        public void JUDDI_899_2() throws Exception {
                UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

                UDDIPublicationImpl pub = new UDDIPublicationImpl();

                FindTModel ftm = new FindTModel();
                ftm.setAuthInfo(authInfoJoe);
                ftm.setFindQualifiers(new FindQualifiers());
                ftm.getFindQualifiers().getFindQualifier().add(UDDIConstants.CASE_INSENSITIVE_MATCH);
                
                ftm.setCategoryBag(new CategoryBag());
                //all of the defeault installed data is for "keyGenerator"
                ftm.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "uddi-org:types:keyGenerator", "keygenerator"));
                TModelList findTModel = inquiry.findTModel(ftm);
                Assert.assertNotNull(findTModel);
                Assert.assertNotNull(findTModel.getTModelInfos());
                Assert.assertNotNull(findTModel.getTModelInfos().getTModelInfo());

        }
}
