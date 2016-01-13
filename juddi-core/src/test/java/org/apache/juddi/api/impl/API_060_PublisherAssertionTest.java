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

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
import java.rmi.RemoteException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.bind.JAXB;
import javax.xml.ws.Holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.jaxb.EntityCreator;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckCommon;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckPublisherAssertion;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

public class API_060_PublisherAssertionTest {

        private static Log logger = LogFactory.getLog(API_060_PublisherAssertionTest.class);

        private static API_010_PublisherTest api010 = new API_010_PublisherTest();
        private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckPublisherAssertion tckAssertion = new TckPublisherAssertion(new UDDIPublicationImpl());
        private static TckFindEntity tckFindEntity = new TckFindEntity(new UDDIInquiryImpl());
        private static String authInfoJoe = null;
        private static String authInfoSam = null;
        private static String authInfoMary = null;
        private static UDDIPublicationImpl pub = new UDDIPublicationImpl();

        @BeforeClass
        public static void setup() throws Exception {
                Registry.start();
                logger.debug("Getting auth token..");
                try {
                        api010.saveJoePublisher();
                        api010.saveSamSyndicator();
                        UDDISecurityPortType security = new UDDISecurityImpl();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        TckCommon.DumpAllTModelsOpInfo(authInfoJoe, new UDDIInquiryImpl());
                        String root = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        tckTModel.saveTmodels(root);
                } catch (RemoteException e) {
                        System.out.println("the test failed, dumping ownership information for all tmodels....");
                        
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token." + e.getMessage());
                }
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                Registry.stop();
        }

        @Test
        public void testJoepublisherToSamSyndicator() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                } finally {
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }

        /**
         * This test should find no publisher assertions because we only save
         * them from the joe publisher side.
         */
        @Test
        public void testFindNoAssertions() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckTModel.saveMaryPublisherTmodel(authInfoMary);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusiness.saveMaryPublisherBusiness(authInfoMary);
                        tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertion.saveJoePublisherPublisherAssertion2(authInfoJoe);

                        tckFindEntity.findRelatedBusiness_sortByName(true);
                        tckFindEntity.findRelatedBusinessToKey(true);
                        tckFindEntity.findRelatedBusinessFromKey(true);

                        tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertion.deleteJoePublisherPublisherAssertion2(authInfoJoe);
                } finally {
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                        tckTModel.deleteMaryPublisherTmodel(authInfoMary);
                }
        }

        /**
         * This test should find 2 publisher assertions.
         */
        @Test
        public void testFindAssertions() {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckTModel.saveMaryPublisherTmodel(authInfoMary);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusiness.saveMaryPublisherBusiness(authInfoMary);
                        tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertion.saveJoePublisherPublisherAssertion2(authInfoJoe);
                        tckAssertion.saveSamPublisherPublisherAssertion(authInfoSam);
                        tckAssertion.saveMaryPublisherPublisherAssertion(authInfoMary);

                        tckFindEntity.findRelatedBusiness_sortByName(false);
                        tckFindEntity.findRelatedBusinessToKey(false);
                        tckFindEntity.findRelatedBusinessFromKey(false);

                        tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckAssertion.deleteJoePublisherPublisherAssertion2(authInfoJoe);

                } finally {
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                        tckTModel.deleteMaryPublisherTmodel(authInfoMary);
                }
        }

        /**
         * covers
         * <a href="https://issues.apache.org/jira/browse/JUDDI-908">JUDDI-908</a>
         *
         * @throws Exception
         */
        @Test(expected = DispositionReportFaultMessage.class)
        public void deleteAssertionNonowner() throws Exception {
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);

                        DeletePublisherAssertions dp = new DeletePublisherAssertions();
                        dp.setAuthInfo(authInfoMary);

                        PublisherAssertion paIn = (PublisherAssertion) EntityCreator.buildFromDoc(TckPublisherAssertion.JOE_ASSERT_XML, "org.uddi.api_v3");
                        dp.getPublisherAssertion().add(paIn);

                        new UDDIPublicationImpl().deletePublisherAssertions(dp);
                        //
                } finally {
                        tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }

        @Test
        public void testSetPublisherAssertions() throws Exception {
                //create 1/2 PA

                //use Set with no inputs
                //confirm all are deleted
                try {

                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        Holder<List<PublisherAssertion>> x = new Holder<List<PublisherAssertion>>();
                        x.value = new ArrayList<PublisherAssertion>();
                        logger.info("Clearing all Joe's publisher assertions....");
                        pub.setPublisherAssertions(authInfoJoe, x);

                        logger.info("Clearing all Sam's publisher assertions....");
                        pub.setPublisherAssertions(authInfoSam, x);

                        logger.info("Confirming we're clear");
                        List<PublisherAssertion> before = pub.getPublisherAssertions(authInfoJoe);
                        Assert.assertNotNull(before);
                        Assert.assertTrue(before.isEmpty());
                        System.out.println(before.size());
                        for (int i = 0; i < before.size(); i++) {
                                JAXB.marshal(before.get(i), System.out);
                        }

                        before = pub.getPublisherAssertions(authInfoSam);
                        Assert.assertNotNull(before);
                        Assert.assertTrue(before.isEmpty());
                        System.out.println(before.size());
                        for (int i = 0; i < before.size(); i++) {
                                JAXB.marshal(before.get(i), System.out);
                        }

                        List<AssertionStatusItem> assertionStatusReport = pub.getAssertionStatusReport(authInfoJoe, null);
                        Assert.assertTrue(assertionStatusReport.isEmpty());

                        assertionStatusReport = pub.getAssertionStatusReport(authInfoSam, null);
                        Assert.assertTrue(assertionStatusReport.isEmpty());

                        logger.info("Saving 1/2 publisher assertion....");
                        List<PublisherAssertion> onehalfPA = tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);

                        before = pub.getPublisherAssertions(authInfoJoe);
                        Assert.assertNotNull(before);
                        Assert.assertFalse(before.isEmpty());
                        System.out.println(before.size());
                        for (int i = 0; i < before.size(); i++) {
                                JAXB.marshal(before.get(i), System.out);
                        }
                        //PublisherAssertion paIn = (PublisherAssertion)EntityCreator.buildFromDoc(TckPublisherAssertion.JOE_ASSERT_XML, "org.uddi.api_v3");
                        //dp.getPublisherAssertion().add(paIn);
                        x = new Holder<List<PublisherAssertion>>();
                        x.value = new ArrayList<PublisherAssertion>();
                        logger.info("Clearing all publisher assertions....");
                        pub.setPublisherAssertions(authInfoJoe, x);
                        System.out.println(x.value.size());
                        for (int i = 0; i < x.value.size(); i++) {
                                JAXB.marshal(x.value.get(i), System.out);
                        }

                        logger.info("Fetch all publisher assertions....there should be none");
                        List<PublisherAssertion> publisherAssertions = pub.getPublisherAssertions(authInfoJoe);
                        System.out.println(publisherAssertions.size());
                        for (int i = 0; i < publisherAssertions.size(); i++) {
                                JAXB.marshal(publisherAssertions.get(i), System.out);
                        }
                        Assert.assertNotNull(publisherAssertions);
                        Assert.assertTrue(publisherAssertions.isEmpty());
                        //
                } finally {
                        //tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }

        @Test
        public void testSetPublisherAssertions2() throws Exception {
                //create 1/2 PA

                //use Set with the same 1/2 PA
                //confirm still present
                try {
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        List<PublisherAssertion> onehalfPA = tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);

                        List<PublisherAssertion> before = pub.getPublisherAssertions(authInfoJoe);
                        Assert.assertNotNull(before);
                        Assert.assertFalse(before.isEmpty());

                        pub.setPublisherAssertions(authInfoJoe, new Holder<List<PublisherAssertion>>(onehalfPA));
                        List<PublisherAssertion> publisherAssertions = pub.getPublisherAssertions(authInfoJoe);
                        Assert.assertNotNull(publisherAssertions);
                        Assert.assertFalse(publisherAssertions.isEmpty());
                        //
                } finally {
                        //tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }

        @Test
        public void testSetPublisherAssertions3() throws Exception {
                //create 1/2 PA
                //use Set with a new PA
                //confirm first PA is gone and the new PA exists
                try {

                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckTModel.saveMaryPublisherTmodel(authInfoMary);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        tckBusiness.saveMaryPublisherBusiness(authInfoMary);
                        Holder<List<PublisherAssertion>> x = new Holder<List<PublisherAssertion>>();
                        x.value = new ArrayList<PublisherAssertion>();
                        logger.info("Clearing all Joe's publisher assertions....");
                        pub.setPublisherAssertions(authInfoJoe, x);

                        logger.info("Clearing all Sam's publisher assertions....");
                        pub.setPublisherAssertions(authInfoSam, x);

                        logger.info("Clearing all Mary's publisher assertions....");
                        pub.setPublisherAssertions(authInfoMary, x);

                        logger.info("Confirming we're clear");
                        List<PublisherAssertion> before = pub.getPublisherAssertions(authInfoJoe);
                        Assert.assertNotNull(before);

                        System.out.println(before.size());
                        for (int i = 0; i < before.size(); i++) {
                                JAXB.marshal(before.get(i), System.out);
                        }
                        Assert.assertTrue(before.isEmpty());

                        before = pub.getPublisherAssertions(authInfoSam);
                        Assert.assertNotNull(before);

                        System.out.println(before.size());
                        for (int i = 0; i < before.size(); i++) {
                                JAXB.marshal(before.get(i), System.out);
                        }
                        Assert.assertTrue(before.isEmpty());

                        before = pub.getPublisherAssertions(authInfoMary);
                        Assert.assertNotNull(before);

                        System.out.println(before.size());
                        for (int i = 0; i < before.size(); i++) {
                                JAXB.marshal(before.get(i), System.out);
                        }
                        Assert.assertTrue(before.isEmpty());

                        List<AssertionStatusItem> assertionStatusReport = pub.getAssertionStatusReport(authInfoJoe, null);
                        Assert.assertTrue(assertionStatusReport.isEmpty());

                        assertionStatusReport = pub.getAssertionStatusReport(authInfoSam, null);
                        Assert.assertTrue(assertionStatusReport.isEmpty());

                        assertionStatusReport = pub.getAssertionStatusReport(authInfoMary, null);
                        Assert.assertTrue(assertionStatusReport.isEmpty());

                        logger.info("Saving 1/2 publisher assertion....");
                        List<PublisherAssertion> onehalfPA = tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);

                        before = pub.getPublisherAssertions(authInfoJoe);
                        Assert.assertNotNull(before);
                        Assert.assertFalse(before.isEmpty());
                        System.out.println(before.size());
                        for (int i = 0; i < before.size(); i++) {
                                JAXB.marshal(before.get(i), System.out);
                        }
                        //PublisherAssertion paIn = (PublisherAssertion)EntityCreator.buildFromDoc(TckPublisherAssertion.JOE_ASSERT_XML, "org.uddi.api_v3");
                        //dp.getPublisherAssertion().add(paIn);
                        x = new Holder<List<PublisherAssertion>>();
                        x.value = new ArrayList<PublisherAssertion>();
                        PublisherAssertion pa = new PublisherAssertion();

                        pa.setFromKey(TckBusiness.JOE_BUSINESS_KEY);
                        pa.setToKey(TckBusiness.MARY_BUSINESS_KEY);
                        pa.setKeyedReference(new KeyedReference(UDDIConstants.RELATIONSHIPS, "parent-child", "child"));

                        x.value.add(pa);
                        logger.info("Using set to clear existing and add a new publisher assertion....");
                        pub.setPublisherAssertions(authInfoJoe, x);
                        System.out.println(x.value.size());
                        for (int i = 0; i < x.value.size(); i++) {
                                JAXB.marshal(x.value.get(i), System.out);
                        }

                        logger.info("Fetch all publisher assertions....there should be 1");
                        List<PublisherAssertion> publisherAssertions = pub.getPublisherAssertions(authInfoJoe);
                        System.out.println(publisherAssertions.size());
                        for (int i = 0; i < publisherAssertions.size(); i++) {
                                JAXB.marshal(publisherAssertions.get(i), System.out);
                        }
                        Assert.assertEquals(publisherAssertions.get(0).getFromKey(), pa.getFromKey());
                        Assert.assertEquals(publisherAssertions.get(0).getToKey(), pa.getToKey());
                        Assert.assertEquals(publisherAssertions.get(0).getKeyedReference().getKeyName(), pa.getKeyedReference().getKeyName());
                        Assert.assertEquals(publisherAssertions.get(0).getKeyedReference().getKeyValue(), pa.getKeyedReference().getKeyValue());
                        Assert.assertEquals(publisherAssertions.get(0).getKeyedReference().getTModelKey(), pa.getKeyedReference().getTModelKey());

                        //
                } finally {
                        //tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckBusiness.deleteMaryPublisherBusiness(authInfoMary);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                        tckTModel.deleteMaryPublisherTmodel(authInfoMary);
                }

        }
        DigSigUtil ds;

        void SetCertStoreSettigns() {
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, "./src/test/resources/keystore.jks");
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "JKS");
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "Test");
                ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "Test");
                ds.put(DigSigUtil.TRUSTSTORE_FILE, "./src/test/resources/truststore.jks");
                ds.put(DigSigUtil.TRUSTSTORE_FILETYPE, "JKS");
                ds.put(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, "Test");
        }

        void Default() throws CertificateException {
                ds = new DigSigUtil();
                SetCertStoreSettigns();
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "true");
        }

        @Test
        public void testPublisherAssertionSignatures() throws Exception {
                try {
                        Default();
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                        tckBusiness.saveJoePublisherBusiness(authInfoJoe);
                        tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                        AddPublisherAssertions ap = new AddPublisherAssertions();
                        ap.setAuthInfo(authInfoJoe);

                        PublisherAssertion paIn = (PublisherAssertion) EntityCreator.buildFromDoc(TckPublisherAssertion.JOE_ASSERT_XML, "org.uddi.api_v3");
                        paIn = ds.signUddiEntity(paIn);

                        ap.getPublisherAssertion().add(paIn);
                        Assert.assertFalse(paIn.getSignature().isEmpty());
                        pub.addPublisherAssertions(ap);

                        List<PublisherAssertion> onehalfPA = tckAssertion.saveJoePublisherPublisherAssertion(authInfoJoe);

                       
                        Assert.assertNotNull(onehalfPA);
                        Assert.assertFalse(onehalfPA.get(0).getSignature().isEmpty());
                        Assert.assertFalse(onehalfPA.isEmpty());
                        
                        Assert.assertNotNull(onehalfPA);
                        Assert.assertFalse(onehalfPA.get(0).getSignature().isEmpty());
                        Assert.assertFalse(onehalfPA.isEmpty());
                        Assert.assertEquals(paIn.getSignature().size(),onehalfPA.get(0).getSignature().size());
                        Assert.assertEquals(paIn.getSignature().get(0).getId(),onehalfPA.get(0).getSignature().get(0).getId());
                        Assert.assertEquals(paIn.getSignature().get(0).getKeyInfo().getId(),onehalfPA.get(0).getSignature().get(0).getKeyInfo().getId());
                        Assert.assertEquals(paIn.getSignature().get(0).getKeyInfo().getContent().size(),onehalfPA.get(0).getSignature().get(0).getKeyInfo().getContent().size());
                        
                        Assert.assertEquals(paIn.getSignature().get(0).getSignedInfo().getCanonicalizationMethod().getAlgorithm(),onehalfPA.get(0).getSignature().get(0).getSignedInfo().getCanonicalizationMethod().getAlgorithm());
                        Assert.assertEquals(paIn.getSignature().get(0).getSignedInfo().getId(),onehalfPA.get(0).getSignature().get(0).getSignedInfo().getId());
                        Assert.assertEquals(paIn.getSignature().get(0).getSignedInfo().getReference().size(),onehalfPA.get(0).getSignature().get(0).getSignedInfo().getReference().size());
                        AtomicReference<String> outmsg=new AtomicReference<String>();
                        boolean success=ds.verifySignedUddiEntity(onehalfPA.get(0), outmsg);
                        Assert.assertTrue(outmsg.get(), success);
                     
                        //
                } finally {
                        //tckAssertion.deleteJoePublisherPublisherAssertion(authInfoJoe);
                        tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
                        tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
                }
        }
}
