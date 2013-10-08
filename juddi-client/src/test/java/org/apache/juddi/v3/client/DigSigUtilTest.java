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
package org.apache.juddi.v3.client;

import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.crypto.dsig.CanonicalizationMethod;

import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.TModel;
import org.w3._2000._09.xmldsig_.SignatureType;

/**
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class DigSigUtilTest {

    org.apache.juddi.v3.client.cryptor.DigSigUtil ds = null;

    public DigSigUtilTest() throws Exception {
        Default();
    }

    void Default() throws CertificateException {
        ds = new DigSigUtil();
        SetCertStoreSettigns();
        ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "t");
    }
    
    void SetCertStoreSettigns(){
        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, "./src/test/resources/keystore.jks");
        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "JKS");
        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "Test");
        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "Test");
        
        ds.put(DigSigUtil.TRUSTSTORE_FILE, "./src/test/resources/truststore.jks");
        ds.put(DigSigUtil.TRUSTSTORE_FILETYPE, "JKS");
        ds.put(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, "Test");
        
        
    }

    void SubjectDNOnly() throws CertificateException {
        ds = new DigSigUtil();
        SetCertStoreSettigns();
        ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "t");
    }

    void SerialAndIssuerOnly() throws CertificateException {
        ds = new DigSigUtil();
        SetCertStoreSettigns();
        ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "t");
    }

    @Test
    public void testSignBusinessSubjectDNOnly() throws CertificateException {
        
        SubjectDNOnly();
        System.out.println("testSignBusinessSubjectDNOnly signing");
        BusinessEntity be = new BusinessEntity();
        be.setBusinessKey("uddi:juddi.apache.org:testkey");
        be.setDiscoveryURLs(new DiscoveryURLs());
        be.getDiscoveryURLs().getDiscoveryURL().add(new DiscoveryURL("website", "http://localhost"));
        be.getDescription().add(new Description("a description", "en"));
        be.getName().add(new Name("My biz", "en"));

        BusinessEntity signUDDI_JAXBObject = ds.signUddiEntity(be);
        DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);
        Assert.assertNotSame("items are the same", be, signUDDI_JAXBObject);
        //System.out.println("verifing");
        AtomicReference<String> msg = new AtomicReference<String>();
        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
        if (verifySigned_UDDI_JAXB_Object) {
            //System.out.println("signature validation passed (expected)");
        } else {
            System.out.println("signature validation failed (not expected)");
            Assert.fail(msg.get());
        }
        validAllSignatureElementsArePresent(signUDDI_JAXBObject.getSignature());
    }

    @Test
    public void testSignBusinessSerialAndIssuerOnly() throws CertificateException {
        System.out.println("testSignBusinessSerialAndIssuerOnly signing");
        SerialAndIssuerOnly();
        
        BusinessEntity be = new BusinessEntity();
        be.setBusinessKey("uddi:juddi.apache.org:testkey");
        be.setDiscoveryURLs(new DiscoveryURLs());
        be.getDiscoveryURLs().getDiscoveryURL().add(new DiscoveryURL("website", "http://localhost"));
        be.getDescription().add(new Description("a description", "en"));
        be.getName().add(new Name("My biz", "en"));

        BusinessEntity signUDDI_JAXBObject = ds.signUddiEntity(be);
        DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);
        Assert.assertNotSame("items are the same", be, signUDDI_JAXBObject);
        //System.out.println("verifing");
        AtomicReference<String> msg = new AtomicReference<String>();
        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
        if (verifySigned_UDDI_JAXB_Object) {
            //System.out.println("signature validation passed (expected)");
        } else {
            System.out.println("signature validation failed (not expected)");
            Assert.fail(msg.get());
        }
        validAllSignatureElementsArePresent(signUDDI_JAXBObject.getSignature());
    }

    @Test
    public void testSignBusiness() throws CertificateException {
        Default();


        System.out.println("testSignBusiness signing");
        BusinessEntity be = new BusinessEntity();
        be.setBusinessKey("uddi:juddi.apache.org:testkey");
        be.setDiscoveryURLs(new DiscoveryURLs());
        be.getDiscoveryURLs().getDiscoveryURL().add(new DiscoveryURL("website", "http://localhost"));
        be.getDescription().add(new Description("a description", "en"));
        be.getName().add(new Name("My biz", "en"));

        BusinessEntity signUDDI_JAXBObject = ds.signUddiEntity(be);
        Assert.assertNotSame("items are the same", be, signUDDI_JAXBObject);
        //System.out.println("verifing");
        AtomicReference<String> msg = new AtomicReference<String>();
        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
        if (verifySigned_UDDI_JAXB_Object) {
            //System.out.println("signature validation passed (expected)");
        } else {
            System.out.println("signature validation failed (not expected)");
            Assert.fail(msg.get());
        }
        validAllSignatureElementsArePresent(signUDDI_JAXBObject.getSignature());
    }

    @Test
    public void testSignService() throws CertificateException {
        Default();
        System.out.println("testSignService signing");
        BusinessService be = new BusinessService();
        be.setBusinessKey("uddi:juddi.apache.org:testkey");

        be.getDescription().add(new Description("a description", "en"));
        be.getName().add(new Name("My biz", "en"));

        BusinessService signUDDI_JAXBObject = ds.signUddiEntity(be);
        Assert.assertNotSame("items are the same", be, signUDDI_JAXBObject);
        //System.out.println("verifing");
        AtomicReference<String> msg = new AtomicReference<String>();
        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
        if (verifySigned_UDDI_JAXB_Object) {
            //System.out.println("signature validation passed (expected)");
        } else {
            System.out.println("signature validation failed (not expected)");
            Assert.fail(msg.get());
        }
        validAllSignatureElementsArePresent(signUDDI_JAXBObject.getSignature());
    }

    @Test
    public void testSignTmodel() throws CertificateException {
        Default();
        System.out.println("testSignTmodel signing");
        TModel be = new TModel();
        be.setTModelKey("uddi:juddi.apache.org:testkey");

        be.getDescription().add(new Description("a description", "en"));
        be.setName(new Name("My biz", "en"));

        TModel signUDDI_JAXBObject = ds.signUddiEntity(be);
        Assert.assertNotSame("items are the same", be, signUDDI_JAXBObject);
        //System.out.println("verifing");
        AtomicReference<String> msg = new AtomicReference<String>();
        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
        if (verifySigned_UDDI_JAXB_Object) {
            //System.out.println("signature validation passed (expected)");
        } else {
            System.out.println("signature validation failed (not expected)");
            Assert.fail(msg.get());
        }
        validAllSignatureElementsArePresent(signUDDI_JAXBObject.getSignature());
    }

    @Test
    public void testSignBinding() throws CertificateException {
        Default();
        System.out.println("testSignBinding signing");
        BindingTemplate be = new BindingTemplate();
        be.setBindingKey("uddi:juddi.apache.org:testkey");

        be.getDescription().add(new Description("a description", "en"));


        BindingTemplate signUDDI_JAXBObject = ds.signUddiEntity(be);
        Assert.assertNotSame("items are the same", be, signUDDI_JAXBObject);
        //System.out.println("verifing");
        AtomicReference<String> msg = new AtomicReference<String>();
        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
        if (verifySigned_UDDI_JAXB_Object) {
            //System.out.println("signature validation passed (expected)");
        } else {
            System.out.println("signature validation failed (not expected)");
            Assert.fail(msg.get());
        }
        validAllSignatureElementsArePresent(signUDDI_JAXBObject.getSignature());
    }

    @Test
    public void testSignPublisherAssertion() throws CertificateException {
        Default();
        System.out.println("testSignPublisherAssertion signing");
        PublisherAssertion be = new PublisherAssertion();
        be.setFromKey("uddi:juddi.apache.org:testkey");
        be.setToKey("uddi:juddi.apache.org:testkey");

        PublisherAssertion signUDDI_JAXBObject = ds.signUddiEntity(be);
        Assert.assertNotSame("items are the same", be, signUDDI_JAXBObject);
        //System.out.println("verifing");
        AtomicReference<String> msg = new AtomicReference<String>();
        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
        if (verifySigned_UDDI_JAXB_Object) {
            //System.out.println("signature validation passed (expected)");
        } else {
            System.out.println("signature validation failed (not expected)");
            Assert.fail(msg.get());
        }
        validAllSignatureElementsArePresent(signUDDI_JAXBObject.getSignature());


    }

    static void validAllSignatureElementsArePresent(List<SignatureType> sigs) {
        Assert.assertNotNull(sigs);
        Assert.assertFalse(sigs.isEmpty());
        for (int i = 0; i < sigs.size(); i++) {
            Assert.assertFalse(sigs.get(i).getKeyInfo().getContent().isEmpty());
            for (int k = 0; k < sigs.get(i).getSignedInfo().getCanonicalizationMethod().getContent().size(); k++) {
                Assert.assertTrue(sigs.get(i).getSignedInfo().getCanonicalizationMethod().getContent().get(k).equals(CanonicalizationMethod.EXCLUSIVE));
            }
        }
    }
}
