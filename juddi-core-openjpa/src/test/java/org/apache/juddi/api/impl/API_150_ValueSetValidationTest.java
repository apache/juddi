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
package org.apache.juddi.api.impl;

import java.util.UUID;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class API_150_ValueSetValidationTest {

        private static Log logger = LogFactory.getLog(API_141_JIRATest.class);
        static UDDISecurityPortType security = new UDDISecurityImpl();
        static UDDIInquiryPortType inquiry = new UDDIInquiryImpl();
        static UDDIPublicationPortType publication = new UDDIPublicationImpl();
        static JUDDIApiImpl juddi = new JUDDIApiImpl();
        static UDDIValueSetValidationImpl vsv = new UDDIValueSetValidationImpl();
        static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        protected static String authInfoJoe = null;
        protected static String authInfoMary = null;
        protected static String authInfoSam = null;
        protected static String authInfoRoot = null;
        protected static String authInfoUDDI = null;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                Registry.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                Registry.start();

                logger.debug("Getting auth tokens..");
                try {
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        authInfoRoot = TckSecurity.getAuthToken(security, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                        authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        Assert.assertNotNull(authInfoJoe);
                        Assert.assertNotNull(authInfoSam);
                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveMaryPublisherTmodel(authInfoMary);
                        
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }
        static String VSV_KEY = "uddi:juddi.apache.org:node1";
        //VIA InVM transport
        final static String VSV_BT_KEY = "uddi:juddi.apache.org:servicebindings-valueset-cp";
        //VIA JAXWS Transport "uddi:juddi.apache.org:servicebindings-valueset-ws";
        final static String TMODEL = "uddi:uddi.org:categorization:nodes";

        /**
         * scenario 1
         * <ul>
         * <ol>create checked tmodel x</ol>
         * <ol>register business/service/bt for validatevalues service</ol>
         * <ol>add/alter BT to include instance info for ref to tmodel
         * checked</ol>
         * <ol>alter checked tmodel x, add reference to the BT for the
         * validator</ol>
         * <ol>someone tries to use the checked tmodel as a KR from some other
         * entity</ol>
         * <ol>on publish, the server attempts to validate the KR's value
         * against the VV service via the binding Template</ol>
         * </ul>
         *
         * @throws Exception
         */
        @Ignore
        @Test
        public void Scenario1() throws Exception {
                //create checked tmodel x
                //no longer needed, included with default install data
                //SaveCheckedTModel(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);
                //register business/service/bt for validatevalues service
                //this is done by the install process using the built in vsv

                //add/alter BT to include instance info for ref to tmodel checked
                //turns out we don't need this either, its in the default install data
                //AlterRootBusiness();
                //configure jUDDI API service to "set" the valid values
                //SaveVSVValues(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);
                //add a new service for joe, reference the checked tmodel with an invalid token 
                SaveBusiness(authInfoJoe, true, TMODEL);

                //add a new service for joe, reference the checked tmodel with invalid token
        }

        @Ignore
        @Test(expected = ValueNotAllowedException.class)
        public void Scenario1Invalid() throws Exception {
                //create checked tmodel x
                //logger.info("Saving checked tModel");
                // SaveCheckedTModel(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);
                //register business/service/bt for validatevalues service
                //this is done by the install process using the built in vsv

                //add/alter BT to include instance info for ref to tmodel checked
                //turns out we don't need this either, its in the default install data
                //AlterRootBusiness();
                //configure jUDDI API service to "set" the valid values
                //logger.info("Saving VS valid values");
                //SaveVSVValues(TckTModel.JOE_PUBLISHER_KEY_PREFIX + TMODEL);
                //add a new service for joe, reference the checked tmodel with an invalid token 
                logger.info("Saving a business using those values");
                SaveBusiness(authInfoJoe, false, TMODEL);

                //add a new service for joe, reference the checked tmodel with invalid token
        }

        @Ignore
        @Test
        public void Scenarion2() {
                /*
                 * use x has a keyed reference for service xyz
                 register business/service/bt for validatevalues service
                 add/alter BT to include instance info for ref to tmodel checked
                 alter checked tmodel, add reference to the BT for the validator
                 >>> open question? do we check existing instances of tmodel x at this point? do we reject the request if validation fails?
                 someone tries to use the checked tmodel as a KR
                 on publish, the server attempts to validate the KR's value against the VV service via the binding Template
                 */
        }

        private void SaveCheckedTModel(String key) throws Exception {
                TModel tm = new TModel();
                tm.setTModelKey(key);
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(
                     new KeyedReference(UDDIConstants.IS_VALIDATED_BY, UDDIConstants.IS_VALIDATED_BY_KEY_NAME, VSV_BT_KEY));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        /**
         * The bindingTemplate for the get_allValidValues or the validate_values
         * Web service SHOULD reference in its tModelInstanceDetails the
         * appropriate value set API tModel (Section 11.2.7 Value Set Caching
         * API tModel or Section 11.2.8 Value Set Validation API tModel) as well
         * tModels for all of the value sets the service applies to.
         *
         * @throws Exception
         */
        private void AlterRootBusiness() throws Exception {
                GetBindingDetail gsd = new GetBindingDetail();
                gsd.getBindingKey().add(VSV_BT_KEY);
                BindingDetail bindingDetail = inquiry.getBindingDetail(gsd);
                if (bindingDetail.getBindingTemplate().get(0).getTModelInstanceDetails() == null) {
                        bindingDetail.getBindingTemplate().get(0).setTModelInstanceDetails(new TModelInstanceDetails());
                }
                for (int i = 0; i < bindingDetail.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().size(); i++) {
                }
        }

        private void SaveBusiness(String authInfoJoe, boolean isValid, String key) throws Exception {
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("VSV", null));
                be.setCategoryBag(new CategoryBag());
                if (isValid) {
                        be.getCategoryBag().getKeyedReference().add(new KeyedReference(key, "node", "node"));
                } else {
                        be.getCategoryBag().getKeyedReference().add(new KeyedReference(key, "name", "qwerty"));
                }
                sb.getBusinessEntity().add(be);
                publication.saveBusiness(sb);
        }

     

        public void DerviedFromValid() throws Exception {

                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_DERIVED_FROM, "", "uddi:uddi.org:categorization:nodes"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test(expected = ValueNotAllowedException.class)
        public void DerviedFromInValid() throws Exception {

                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_DERIVED_FROM, "", "uddi:juddi.apache.org:" + UUID.randomUUID().toString()));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test
        public void EntitKeyValuesValid() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.ENTITY_KEY_VALUES, "", "tModelKey"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

        }

        @Test(expected = ValueNotAllowedException.class)
        public void EntitKeyValuesInValid() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.ENTITY_KEY_VALUES, "", "asdasdasd"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test
        @Ignore
        public void UDDINodeValid() throws Exception {
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("test", "en"));
                be.setCategoryBag(new CategoryBag());
                be.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:nodes", "", "node"));
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoRoot);
                sb.getBusinessEntity().add(be);
                publication.saveBusiness(sb);
        }

        @Test(expected = ValueNotAllowedException.class)
        public void UDDINodeInValid1() throws Exception {
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("test", "en"));
                be.setCategoryBag(new CategoryBag());
                be.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:nodes", "", "asdasd"));
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoRoot);
                sb.getBusinessEntity().add(be);
                publication.saveBusiness(sb);
        }

        @Test(expected = ValueNotAllowedException.class)
        public void UDDINodeInValid2() throws Exception {
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("test", "en"));
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.setCategoryBag(new CategoryBag());
                bs.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:nodes", "", "asdasd"));
                be.getBusinessServices().getBusinessService().add(bs);
                be.setCategoryBag(new CategoryBag());

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoRoot);
                sb.getBusinessEntity().add(be);
                publication.saveBusiness(sb);
        }

        @Test
        public void OwningBusinessValid() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.OWNING_BUSINESS, "",VSV_KEY));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test(expected = ValueNotAllowedException.class)
        public void OwningBusinessInValid() throws Exception {
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name("test", "en"));
                be.setCategoryBag(new CategoryBag());
                be.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.OWNING_BUSINESS, "", "uddi:juddi.apache.org:" + UUID.randomUUID().toString()));
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoJoe);
                sb.getBusinessEntity().add(be);
                publication.saveBusiness(sb);
        }

        @Test(expected = ValueNotAllowedException.class)
        public void OwningBusinessInValid2() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.OWNING_BUSINESS, "", "uddi:juddi.apache.org:" + UUID.randomUUID().toString()));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test
        public void TypeTmodelValid() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "namespace"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

        }

        @Test(expected = ValueNotAllowedException.class)
        public void TypeTModelInValid() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "wsdlDeployment"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

        }

        @Test(expected = ValueNotAllowedException.class)
        public void TypeBindingInValid() throws Exception {
                SaveBinding sb = new SaveBinding();
                sb.setAuthInfo(authInfoRoot);
                BindingTemplate bt = new BindingTemplate();
                bt.setServiceKey("uddi:juddi.apache.org:services-inquiry");
                bt.setAccessPoint(new AccessPoint("http://test", "endPoint"));
                bt.setCategoryBag(new CategoryBag());
                bt.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "namespace"));
                publication.saveBinding(sb);
        }

        @Test
        public void TypeBindingValid() throws Exception {
                SaveBinding sb = new SaveBinding();
                sb.setAuthInfo(authInfoRoot);
                BindingTemplate bt = new BindingTemplate();
                bt.setServiceKey("uddi:juddi.apache.org:services-inquiry");
                bt.setAccessPoint(new AccessPoint("http://test", "endPoint"));
                bt.setCategoryBag(new CategoryBag());
                bt.getCategoryBag().getKeyedReference().add(new KeyedReference("uddi:uddi.org:categorization:types", "", "wsdlDeployment"));
                sb.getBindingTemplate().add(bt);
                publication.saveBinding(sb);
        }

        @Test(expected = ValueNotAllowedException.class)
        public void ValidatedByInValid() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_VALIDATED_BY, UDDIConstants.IS_VALIDATED_BY_KEY_NAME, "uddi:juddi.apache.org:s:nonexistentvalidator"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

        }

        @Test
        @Ignore
        public void ValidatedByValid() throws Exception {
                //this is covered by the default install
        }

        @Test
        public void ReplacedByValid() throws Exception {
                TModel tm = new TModel();
                tm.setName(new Name("My old tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publication.saveTModel(stm);

                tm = new TModel();
                tm.setName(new Name("My new tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setIdentifierBag(new IdentifierBag());
                tm.getIdentifierBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveTModel.getTModel().get(0).getTModelKey()));
                stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test
        public void ReplacedByValid2() throws Exception {
                TModel tm = new TModel();
                tm.setName(new Name("My old tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                TModelDetail saveTModel = publication.saveTModel(stm);

                tm = new TModel();
                tm.setName(new Name("My new tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveTModel.getTModel().get(0).getTModelKey()));
                stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test
        public void ReplacedByValid3() throws Exception {
                BusinessEntity tm = new BusinessEntity();
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness = publication.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setIdentifierBag(new IdentifierBag());
                tm.getIdentifierBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveBusiness.getBusinessEntity().get(0).getBusinessKey()));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                publication.saveBusiness(stm);
        }

        @Test
        public void ReplacedByValid4() throws Exception {
                BusinessEntity tm = new BusinessEntity();
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                BusinessDetail saveBusiness = publication.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", saveBusiness.getBusinessEntity().get(0).getBusinessKey()));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                publication.saveBusiness(stm);
        }

        @Test
        public void ReplacedByValid5Projected() throws Exception {
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
                publication.saveBusiness(stm);
        }
        
        
         @Test
        public void ReplacedByValid6DifferentOwners() throws Exception {
                BusinessEntity tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.MARY_KEY_PREFIX + "testbiz");
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoMary);
                stm.getBusinessEntity().add(tm);
                publication.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "oldbiz");
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", TckTModel.MARY_KEY_PREFIX + "testbiz"));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                publication.saveBusiness(stm);
        }
        
           @Test
        public void ReplacedByValid7DifferentOwners() throws Exception {
                BusinessEntity tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.MARY_KEY_PREFIX + "testbiz");
                tm.getName().add(new Name("My old business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                SaveBusiness stm = new SaveBusiness();
                stm.setAuthInfo(authInfoMary);
                stm.getBusinessEntity().add(tm);
                publication.saveBusiness(stm);

                tm = new BusinessEntity();
                tm.setBusinessKey(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "oldbiz");
                tm.getName().add(new Name("My new business", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setIdentifierBag(new IdentifierBag());
                tm.getIdentifierBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", TckTModel.MARY_KEY_PREFIX + "testbiz"));
                stm = new SaveBusiness();
                stm.setAuthInfo(authInfoJoe);
                stm.getBusinessEntity().add(tm);
                publication.saveBusiness(stm);
        }

        /**
         * reference undefined tmodel
         *
         * @throws Exception
         */
        @Test(expected = ValueNotAllowedException.class)
        public void ReplacedByInValid() throws Exception {

                TModel tm = new TModel();
                tm.setName(new Name("My new tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "", TckTModel.JOE_PUBLISHER_KEY_PREFIX + UUID.randomUUID().toString()));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        /**
         * reference business key
         *
         * @throws Exception
         */
        @Test(expected = ValueNotAllowedException.class)
        public void ReplacedByInValid2() throws Exception {

                TModel tm = new TModel();
                tm.setName(new Name("My new tmodel", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.setCategoryBag(new CategoryBag());
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_REPLACED_BY, "",VSV_KEY));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);
        }

        @Test
        public void RelationshipsValid() throws Exception {
                TModel tm = new TModel();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.RELATIONSHIPS, "", "peer-peer"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

        }

        @Test(expected = ValueNotAllowedException.class)
        public void RelationshipsInValid() throws Exception {
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.RELATIONSHIPS, "", "asdasdasd"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

        }

        @Test(expected = ValueNotAllowedException.class)
        public void ClassLoadingTestsInValid() throws Exception {

                //save a tmodel that's is validated but no validator exists
                TModel tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                tm.setTModelKey(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "nonexistentvalidator");
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_VALIDATED_BY, UDDIConstants.IS_VALIDATED_BY_KEY_NAME, "uddi:juddi.apache.org:servicebindings-valueset-cp"));
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

                tm = new TModel();
                //tm.setTModelKey();
                tm.setCategoryBag(new CategoryBag());
                // tm.setTModelKey("uddi:uddi.org:categorization:nonexistentvalidator");
                tm.setName(new Name("My Custom validated key", "en"));
                tm.getDescription().add(new Description("valid values include 'one', 'two', 'three'", "en"));
                tm.getCategoryBag().getKeyedReference().add(new KeyedReference(TckTModel.JOE_PUBLISHER_KEY_PREFIX + "nonexistentvalidator", "", "asdasd"));
                stm = new SaveTModel();
                stm.setAuthInfo(authInfoJoe);
                stm.getTModel().add(tm);
                publication.saveTModel(stm);

                //use it has a keyed reference, expect failure
        }
}
