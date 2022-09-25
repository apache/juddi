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
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.ext.wsdm.WSDMQosConstants;
import org.apache.juddi.v3.client.mapping.ServiceLocator;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.apache.juddi.v3.tck.TckBindingTemplate;
import static org.apache.juddi.v3.tck.TckBindingTemplate.JOE_BINDING_KEY;
import static org.apache.juddi.v3.tck.TckBindingTemplate.JOE_BINDING_XML;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckCommon;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.TModelBag;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_050_BindingTemplateTest {

        private static Log logger = LogFactory.getLog(API_050_BindingTemplateTest.class);

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
                        api010.saveJoePublisher();
                        authInfoJoe = TckSecurity.getAuthToken(new UDDISecurityImpl(), TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        UDDISecurityPortType security = new UDDISecurityImpl();
                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
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
        public void joepublisher() throws Exception {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
                        tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        @Test
        public void testSearchBinding() throws Exception {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);

                        tckFindEntity.findServiceDetail("uddi:uddi.joepublisher.com:serviceone");
                        tckBindingTemplate.deleteBinding(authInfoJoe, "uddi:uddi.joepublisher.com:bindingone");
                        String serviceKey = tckFindEntity.findService(null);
                        tckFindEntity.findServiceDetail(serviceKey);

                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);

                        serviceKey = tckFindEntity.findService(null);
                        tckFindEntity.findServiceDetail(serviceKey);

                        tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);

                        tckFindEntity.findService(null);
                        tckFindEntity.findServiceDetail(serviceKey);
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        /**
         * https://issues.apache.org/jira/browse/JUDDI-728 Inquiry NPE on
         * find_binding
         *
         * @throws Exception
         */
        @Test
        public void testJUDDI_728() throws Exception {
                UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
                FindBinding fb = new FindBinding();
                fb.setAuthInfo(authInfoJoe);
                org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
                fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.setFindQualifiers(fq);
                fb.setFindTModel(new FindTModel());
                fb.getFindTModel().setIdentifierBag(new IdentifierBag());
                fb.getFindTModel().getIdentifierBag().getKeyedReference().add(new KeyedReference(WSDMQosConstants.METRIC_FAULT_COUNT_KEY, "%", "%"));
                inquiry.findBinding(fb);
        }

        /**
         * https://issues.apache.org/jira/browse/JUDDI-899 findBinding by
         * category bag doesn't work
         *
         * @throws Exception
         */
        @Test 
        public void testJUDDI_899() throws Exception {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
                        
                        UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

                        FindBinding fb = new FindBinding();
                        fb.setAuthInfo(authInfoJoe);

                        fb.setTModelBag(new TModelBag());
                        //this is stored in joe's binding template 
                        fb.getTModelBag().getTModelKey().add("uddi:uddi.org:protocol:serverauthenticatedssl3");
                        logger.info("testJUDDI_899 searching...");
                        BindingDetail findBinding = inquiry.findBinding(fb);
                        logger.info("testJUDDI_899 searching complete.");
                        Assert.assertNotNull(findBinding);
                        Assert.assertNotNull(findBinding.getBindingTemplate());
                        Assert.assertFalse(findBinding.getBindingTemplate().isEmpty());

                        for (int i = 0; i < findBinding.getBindingTemplate().size(); i++) {
                                if (findBinding.getBindingTemplate().get(i).getBindingKey().equals(TckBindingTemplate.JOE_BINDING_KEY)) {
                                        return;
                                }
                        }

                        Assert.fail("Binding wasn't returned");
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
                /*  if (findBinding.isTruncated()==null) {
                 findBinding.setTruncated(false);
                 }
                 //support for paging
                 while (!findBinding.isTruncated() && !findBinding.getBindingTemplate().isEmpty()) {
                       
                 findBinding = inquiry.findBinding(fb);
                 Assert.assertNotNull(findBinding);
                        
                 if (findBinding.isTruncated()) {
                 findBinding.setTruncated(false);
                 }
                 }

                 Assert.fail("The expected service wasn't returned");*/
        }
        
        /**
         * https://issues.apache.org/jira/browse/JUDDI-999
         * max length test for tmodel instance info
         */
         @Test 
        public void testJUDDI_999() throws Exception {
            try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                       tckBindingTemplate. saveBinding(authInfoJoe, "uddi_data/joepublisher/bindingTemplateMaxLength.xml", JOE_BINDING_KEY);
                                
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }
        
        
        /**
         * https://issues.apache.org/jira/browse/JUDDI-999
         * too long length test for tmodel instance info
         */
        
        @Test(expected=org.apache.juddi.v3.error.ValueNotAllowedException.class)
        public void testJUDDI_999_2() throws Exception {
            try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveBinding(authInfoJoe, "uddi_data/joepublisher/bindingTemplateInvalid.xml", JOE_BINDING_KEY,false);
                                
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
        }
        
        @Test
        public void testJuddi937ServiceLocatorWithCache() throws Exception {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveBinding(authInfoJoe, "uddi_data/joepublisher/bindingTemplate.xml", JOE_BINDING_KEY, false);

                        //create a client using invm transport
                        UDDIClientContainer.removeAll();
                        UDDIClient.clearServiceLocatorCaches();
                        UDDIClient c = new UDDIClient("META-INF/uddi.xml");
                        ServiceLocator serviceLocator = c.getServiceLocator("joe");
                        serviceLocator.withSimpleCache();
                        serviceLocator.clearCaches();
                        String lookupEndpoint = serviceLocator.lookupEndpoint(TckBusinessService.JOE_SERVICE_KEY);
                        String lookupEndpoint2 = serviceLocator.lookupEndpoint(TckBusinessService.JOE_SERVICE_KEY);
                        System.out.println(TckCommon.DumpAllServices(authInfoJoe, c.getTransport("default").getUDDIInquiryService()));
                        Assert.assertNotEquals(lookupEndpoint, lookupEndpoint2);
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
         }
        
        
        
        @Test
        public void testJuddi937ServiceLocatorWithoutCache() throws Exception {
                try {
                    
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessService.saveJoePublisherService(authInfoJoe);
                        tckBindingTemplate.saveBinding(authInfoJoe, "uddi_data/joepublisher/bindingTemplate.xml", JOE_BINDING_KEY, false);

                        //create a client using invm transport
                        UDDIClientContainer.removeAll();
                        UDDIClient.clearServiceLocatorCaches();
                        UDDIClient c = new UDDIClient("META-INF/uddi.xml");
                        ServiceLocator serviceLocator = c.getServiceLocator("joe");
                        serviceLocator.clearCaches();
                        //serviceLocator.withSimpleCache();
                        String lookupEndpoint = serviceLocator.lookupEndpoint(TckBusinessService.JOE_SERVICE_KEY);
                        String lookupEndpoint2 = serviceLocator.lookupEndpoint(TckBusinessService.JOE_SERVICE_KEY);
                        
                        System.out.println(TckCommon.DumpAllServices(authInfoJoe, c.getTransport("default").getUDDIInquiryService()));
                        Assert.assertEquals(lookupEndpoint, lookupEndpoint2);
                } finally {
                        tckBusinessService.deleteJoePublisherService(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }
         }
}
