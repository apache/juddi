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
package org.apache.juddi.v3.client.cryptor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.JAXB;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.security.provider.certpath.OCSP;
import sun.security.provider.certpath.OCSP.RevocationStatus;

/**
 * A utility class for signing and verifying JAXB Objects, such as UDDI
 * entities.
 *
 * Notes: This class only supports elements that are signed once. Multiple
 * signature are not currently supported.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree </a>
 */
public class DigSigUtil {

    /**
     * Expects a properties object containing the desired configuration
     *
     * @param config
     * @throws CertificateException
     */
    public DigSigUtil(Properties config) throws CertificateException {
        cf = CertificateFactory.getInstance("X.509");
        this.map = config;
    }

    public DigSigUtil() throws CertificateException {
        cf = CertificateFactory.getInstance("X.509");
    }
    private Log logger = LogFactory.getLog(this.getClass());

    public void put(String key, String value) {
        map.put(key, value);
    }

    /**
     * clears the configuration for reuse
     */
    public void clear() {
        map.clear();
    }
    private Properties map = new Properties();
    /**
     * This is the location of the keystore
     *
     * If referencing a Windows certificate store, use WINDOWS-MY as a value
     * with a null password
     */
    public final static String SIGNATURE_KEYSTORE_FILE = "keyStorePath";
    /**
     * The type of file, such as JKS for most Java applications, or WINDOWS-MY
     * to use the Windows certificate store of the current user or KeychainStore
     * for MacOS
     */
    public final static String SIGNATURE_KEYSTORE_FILETYPE = "keyStoreType";
    public final static String SIGNATURE_KEYSTORE_FILE_PASSWORD = "filePassword";
    public final static String SIGNATURE_KEYSTORE_KEY_PASSWORD = "keyPassword";
    public final static String SIGNATURE_KEYSTORE_KEY_ALIAS = "keyAlias";
    public final static String TRUSTSTORE_FILE = "trustStorePath";
    public final static String TRUSTSTORE_FILETYPE = "trustStoreType";
    public final static String TRUSTSTORE_FILE_PASSWORD = "trustStorePassword";
    /**
     * default is CanonicalizationMethod.EXCLUSIVE
     *
     * @see CanonicalizationMethod
     */
    public final static String CANONICALIZATIONMETHOD = "CanonicalizationMethod";
    /**
     * default is RSA_SHA1
     *
     * @see SignatureMethod
     */
    public final static String SIGNATURE_METHOD = "SignatureMethod";
    /**
     * Defines whether or not a certificate is included with the signature<Br>
     * Values - Include whole X509 Public Key in the signature (recommended)
     * (default) * Example
     * <pre>
     * Map map = new HashMap();
     * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, true);</pre>
     * any value can be used.
     */
    public final static String SIGNATURE_OPTION_CERT_INCLUSION_BASE64 = "BASE64";
    /*
     * Include the signer's thumbprint of the public key.
     * 
     * Clients will not be able to validate the signature unless they have a copy of the signer's public key 
     * in a trust store or the full certificate is included
     * out of band
     * 
     * Example
     * <pre>
     * Map map = new HashMap();
     * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_THUMBPRINT, true);</pre>
     * any value can be used.
     *@see SIGNATURE_OPTION_CERT_INCLUSION_BASE64
     */
    //public final static String SIGNATURE_OPTION_CERT_INCLUSION_THUMBPRINT = "THUMBPRINT";
    /**
     * Include the signer's serial of the public key and the issuer's subject
     * name
     *
     * Clients will not be able to validate the signature unless they have a
     * copy of the signer's public key in a trust store or the full certificate
     * is included out of band
     *
     * Example
     * <pre>
     * Map map = new HashMap();
     * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, true);</pre>
     * any value can be used.
     *
     * @see SIGNATURE_OPTION_CERT_INCLUSION_BASE64
     */
    public final static String SIGNATURE_OPTION_CERT_INCLUSION_SERIAL = "SERIAL";
    /**
     * Include the signer's Subject DN of the public key.
     *
     * Clients will not be able to validate the signature unless they have a
     * copy of the signer's public key in a trust store or the full certificate
     * is included out of band
     *
     * Example
     * <pre>
     * Map map = new HashMap();
     * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, true);</pre>
     * any value can be used.
     *
     * @see SIGNATURE_OPTION_CERT_INCLUSION_BASE64
     */
    public final static String SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN = "SUBJECTDN";
    /**
     * Include the signer's X500 Prinicple of the public key.
     *
     * Clients will not be able to validate the signature unless they have a
     * copy of the signer's public key in a trust store or the full certificate
     * is included out of band
     *
     * Example
     * <pre>
     * Map map = new HashMap();
     * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_X500_PRINICPAL, true);</pre>
     * any value can be used.
     *
     * @see SIGNATURE_OPTION_CERT_INCLUSION_BASE64
     */
    //public final static String SIGNATURE_OPTION_CERT_INCLUSION_X500_PRINICPAL = "X500";
    public final static String XML_DIGSIG_NS = "http://www.w3.org/2000/09/xmldsig#";
    /**
     * Default value DigestMethod.SHA1 =
     * "http://www.w3.org/2000/09/xmldsig#sha1"
     *
     * @see javax.xml.crypto.dsig.DigestMethod
     */
    public final static String SIGNATURE_OPTION_DIGEST_METHOD = "digestMethod";
    /**
     * When validating a signature, include this field will validate that the
     * signature is still valid with regards to timestamps NotBefore and
     * OnOrAfter
     *
     * Example
     * <pre>
     * Map map = new HashMap();
     * map.put(DigSigUtil.CHECK_TIMESTAMPS, true);</pre> any value can be used.
     */
    public final static String CHECK_TIMESTAMPS = "checkTimestamps";
    private CertificateFactory cf = null;
    public final static String CHECK_REVOCATION_STATUS_OCSP = "checkRevocationOCSP";
    public final static String CHECK_REVOCATION_STATUS_CRL = "checkRevocationCRL";
    public final static String CHECK_TRUST_CHAIN = "checkTrust";

    /**
     * Digital signs a UDDI entity, such as a business, service, tmodel or
     * binding template using the map to provide certificate key stores and
     * credentials<br><br> The UDDI entity MUST support XML Digital Signatures
     * (tModel, Business, Service, Binding Template)
     *
     * @param <T> Any UDDI entity that supports digital signatures
     * @param jaxbObj
     * @return an enveloped signed UDDI element, do not modify this object after
     * signing
     */
    public <T> T signUddiEntity(T jaxbObj) {
        DOMResult domResult = new DOMResult();
        JAXB.marshal(jaxbObj, domResult);
        Document doc = ((Document) domResult.getNode());
        Element docElement = doc.getDocumentElement();

        try {
            KeyStore ks = KeyStore.getInstance(map.getProperty(SIGNATURE_KEYSTORE_FILETYPE));
            URL url = Thread.currentThread().getContextClassLoader().getResource(map.getProperty(SIGNATURE_KEYSTORE_FILE));
            if (url == null) {
                try {
                    url = new File(map.getProperty(SIGNATURE_KEYSTORE_FILE)).toURI().toURL();
                } catch (Exception x) {
                }
            }
            if (url == null) {
                try {
                    url = this.getClass().getClassLoader().getResource(map.getProperty(SIGNATURE_KEYSTORE_FILE));
                } catch (Exception x) {
                }
            }
            KeyStore.PrivateKeyEntry keyEntry = null;
            if (!map.getProperty(SIGNATURE_KEYSTORE_FILETYPE).equalsIgnoreCase("WINDOWS-MY")) {
                ks.load(url.openStream(), (map.getProperty(SIGNATURE_KEYSTORE_FILE_PASSWORD)).toCharArray());
                if (map.getProperty(SIGNATURE_KEYSTORE_KEY_PASSWORD) == null) {
                    keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(map.getProperty(SIGNATURE_KEYSTORE_KEY_ALIAS),
                            new KeyStore.PasswordProtection(map.getProperty(SIGNATURE_KEYSTORE_FILE_PASSWORD).toCharArray()));
                } else {
                    keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(map.getProperty(SIGNATURE_KEYSTORE_KEY_ALIAS),
                            new KeyStore.PasswordProtection(map.getProperty(SIGNATURE_KEYSTORE_KEY_PASSWORD).toCharArray()));
                }
            } else {
                //Windows only
                ks.load(null, null);
                keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(map.getProperty(SIGNATURE_KEYSTORE_KEY_ALIAS),
                        null);
            }


            PrivateKey privateKey = keyEntry.getPrivateKey();
            Certificate origCert = keyEntry.getCertificate();
            //PublicKey validatingKey = origCert.getPublicKey();
            this.signDOM(docElement, privateKey, origCert);

            DOMSource domSource = new DOMSource(doc);
            T result = (T) JAXB.unmarshal(domSource, jaxbObj.getClass());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Signature failure due to: " + e.getMessage(), e);
        }
    }

    /**
     * Digitally signs a UDDI entity, such as a business, service, tmodel or
     * binding template, provided you've already done the legwork to provide the
     * signing keys <br><br> The UDDI entity MUST support XML Digital Signatures
     * (tModel, Business, Service, Binding Template)
     *
     * @param <T>
     * @param jaxbObj
     * @param publicKey
     * @param privateKey
     * @return
     */
    public <T> T signUddiEntity(T jaxbObj, Certificate publicKey, PrivateKey privateKey) {
        DOMResult domResult = new DOMResult();
        JAXB.marshal(jaxbObj, domResult);
        Document doc = ((Document) domResult.getNode());
        Element docElement = doc.getDocumentElement();
        try {

            //PublicKey validatingKey = origCert.getPublicKey();
            this.signDOM(docElement, privateKey, publicKey);
            DOMSource domSource = new DOMSource(doc);
            T result = (T) JAXB.unmarshal(domSource, jaxbObj.getClass());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Signature failure due to: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes a JAXB object and prints to stdout
     *
     * @param obj
     */
    public static void JAXB_ToStdOut(Object obj) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(obj, sw);
        System.out.println(sw.toString());
    }

    /**
     * Serializes a JAXB object and prints to stdout
     *
     * @param obj
     * @return
     */
    public static String JAXB_ToString(Object obj) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(obj, sw);
        return (sw.toString());
    }

    /**
     *
     * returns the public key of the signing certificate used for a signed JAXB
     * object.
     *
     * @param obj
     * @return null if the item is not signed or if it references a certificate
     * that is not present in the current keystore
     * * @throws IllegalArgumentException for null input
     */
    public X509Certificate getSigningCertificatePublicKey(Object obj) throws IllegalArgumentException, CertificateException {
        DOMResult domResult = new DOMResult();
        JAXB.marshal(obj, domResult);

        Document doc = ((Document) domResult.getNode());
        Element docElement = doc.getDocumentElement();  //this is our signed node
        return getSigningCertificatePublicKey(docElement);
    }

    /**
     *
     * returns the public key of the signing certificate used for a signed JAXB
     * object.
     *
     * @param obj
     * @return null if the item is not signed or if it references a certificate
     * that is not present in the current keystore
     * * @throws IllegalArgumentException for null input
     */
    private X509Certificate getSigningCertificatePublicKey(Element docElement) throws IllegalArgumentException, CertificateException {
        if (docElement == null) {
            throw new IllegalArgumentException();
        }

        NodeList childNodes = docElement.getChildNodes();   //children, one of these SHOULD be our signature element
        // X509Certificate signingcert = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            //System.out.println(childNodes.item(i).getNamespaceURI() + " " + childNodes.item(i).getNodeName());
            if (childNodes.item(i).getNamespaceURI().equalsIgnoreCase(XML_DIGSIG_NS) && childNodes.item(i).getLocalName().equalsIgnoreCase("Signature")) {
                Node sig = childNodes.item(i);
                for (int k = 0; k < sig.getChildNodes().getLength(); k++) {
                    //      System.out.println(sig.getChildNodes().item(k).getNamespaceURI() + " " + sig.getChildNodes().item(k).getNodeName());
                    if (sig.getChildNodes().item(k).getLocalName().equalsIgnoreCase("KeyInfo")) {
                        //TODO figure out how to reference Subject DN, serial, thumbprint, etc
                        for (int j = 0; j < sig.getChildNodes().item(k).getChildNodes().getLength(); j++) {
                            if (sig.getChildNodes().item(k).getChildNodes().item(j).getLocalName().equalsIgnoreCase("X509Data")) {
                                Node X509Data = sig.getChildNodes().item(k).getChildNodes().item(j);
                                for (int x = 0; x < X509Data.getChildNodes().getLength(); x++) {
                                    if (X509Data.getChildNodes().item(x).getLocalName().equalsIgnoreCase("X509Certificate")) {
                                        //yay found it!


                                        String c =
                                                "-----BEGIN CERTIFICATE-----\n"
                                                + X509Data.getChildNodes().item(x).getTextContent()
                                                + "\n-----END CERTIFICATE-----";
                                        //System.out.println("X509 Public key: " + c);
                                        InputStream is = new ByteArrayInputStream(c.getBytes());
                                        X509Certificate cert = (X509Certificate) cf.generateCertificate(is);

                                        logger.info("embedded certificate found, X509 public key " + cert.getSubjectDN().toString());
                                        return cert;

                                    }

                                    //if we have a 
                                    //TODO other parsing items, lots of other potentials here
                                }
                                X509Certificate cert = FindCert(X509Data.getChildNodes());
                                if (cert != null) {
                                    logger.info("certificate loaded from local trust store, X509 public key " + cert.getSubjectDN().toString());
                                    return cert;
                                }
                            }

                        }
                        break;
                    }

                }

                break;
            }
        }
        return null;
    }

    /**
     * Verifies the signature on an enveloped digital signature on a UDDI
     * entity, such as a business, service, tmodel or binding template. <br><Br>
     * It is expected that either the public key of the signing certificate is
     * included within the signature keyinfo section OR that sufficient
     * information is provided in the signature to reference a public key
     * located within the Trust Store provided<br><Br> Optionally, this function
     * also validate the signing certificate using the options provided to the
     * configuration map.
     *
     * @param obj an enveloped signed JAXB object
     * @param OutErrorMessage a human readable error message explaining the
     * reason for failure
     * @return true if the validation passes the signature validation test, and
     * optionally any certificate validation or trust chain validation
     * @throws IllegalArgumentException for null input
     */
    public boolean verifySignedUddiEntity(Object obj, AtomicReference<String> OutErrorMessage) throws IllegalArgumentException {
        if (OutErrorMessage == null) {
            OutErrorMessage = new AtomicReference<String>();
            OutErrorMessage.set("");
        }
        if (obj == null) {
            throw new IllegalArgumentException("obj");
        }
        try {
            DOMResult domResult = new DOMResult();
            JAXB.marshal(obj, domResult);

            Document doc = ((Document) domResult.getNode());
            Element docElement = doc.getDocumentElement();  //this is our signed node

            X509Certificate signingcert = getSigningCertificatePublicKey(docElement);

            if (signingcert != null) {
                logger.info("verifying signature based on X509 public key " + signingcert.getSubjectDN().toString());
                if (map.containsKey(CHECK_TIMESTAMPS) && Boolean.parseBoolean(map.getProperty(CHECK_TIMESTAMPS))) {
                    signingcert.checkValidity();
                }
                if (map.containsKey(CHECK_REVOCATION_STATUS_OCSP)
                        && Boolean.parseBoolean(map.getProperty(CHECK_REVOCATION_STATUS_OCSP))) {
                    logger.info("verifying revocation status via OSCP for X509 public key " + signingcert.getSubjectDN().toString());
                    X500Principal issuerX500Principal = signingcert.getIssuerX500Principal();
                    logger.info("certificate " + signingcert.getSubjectDN().toString() + " was issued by " + issuerX500Principal.getName() + ", attempting to retrieve certificate");
                    Security.setProperty("ocsp.enable", "false");
                    X509Certificate issuer = FindCertByDN(issuerX500Principal);
                    if (issuer == null) {
                        OutErrorMessage.set("Unable to verify certificate status from OCSP because the issuer of the certificate is not in the trust store. " + OutErrorMessage.get());
                        //throw new CertificateException("unable to locate the issuers certificate in the trust store");
                    } else {
                        RevocationStatus check = OCSP.check(signingcert, issuer);
                        logger.info("certificate " + signingcert.getSubjectDN().toString() + " revocation status is " + check.getCertStatus().toString() + " reason " + check.getRevocationReason().toString());
                        if (check.getCertStatus() != RevocationStatus.CertStatus.GOOD) {
                            OutErrorMessage.set("Certificate status is " + check.getCertStatus().toString() + " reason " + check.getRevocationReason().toString() + "." + OutErrorMessage.get());

                            //throw new CertificateException("Certificate status is " + check.getCertStatus().toString() + " reason " + check.getRevocationReason().toString());
                        }
                    }
                }
                if (map.containsKey(CHECK_REVOCATION_STATUS_CRL) && Boolean.parseBoolean(map.getProperty(CHECK_REVOCATION_STATUS_CRL))) {
                    logger.info("verifying revokation status via CRL for X509 public key " + signingcert.getSubjectDN().toString());

                    Security.setProperty("ocsp.enable", "false");
                    System.setProperty("com.sun.security.enableCRLDP", "true");

                    X509CertSelector targetConstraints = new X509CertSelector();
                    targetConstraints.setCertificate(signingcert);
                    PKIXParameters params = new PKIXParameters(GetTrustStore());
                    params.setRevocationEnabled(true);
                    CertPath certPath = cf.generateCertPath(Arrays.asList(signingcert));

                    CertPathValidator certPathValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
                    CertPathValidatorResult result = certPathValidator.validate(certPath, params);
                    try {
                        PKIXCertPathValidatorResult pkixResult = (PKIXCertPathValidatorResult) result;
                        logger.info("revokation status via CRL PASSED for X509 public key " + signingcert.getSubjectDN().toString());
                    } catch (Exception ex) {
                        OutErrorMessage.set("Certificate status is via CRL Failed: " + ex.getMessage() + "." + OutErrorMessage.get());
                    }
                }
                if (map.containsKey(CHECK_TRUST_CHAIN) && Boolean.parseBoolean(map.getProperty(CHECK_TRUST_CHAIN))) {
                    logger.info("verifying trust chain X509 public key " + signingcert.getSubjectDN().toString());
                    try {
                        PKIXParameters params = new PKIXParameters(GetTrustStore());
                        params.setRevocationEnabled(false);
                        CertPath certPath = cf.generateCertPath(Arrays.asList(signingcert));

                        CertPathValidator certPathValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
                        CertPathValidatorResult result = certPathValidator.validate(certPath, params);

                        PKIXCertPathValidatorResult pkixResult = (PKIXCertPathValidatorResult) result;

                        TrustAnchor ta = pkixResult.getTrustAnchor();
                        X509Certificate cert = ta.getTrustedCert();

                        logger.info("trust chain validated X509 public key " + signingcert.getSubjectDN().toString());
                    } catch (Exception ex) {
                        OutErrorMessage.set("Certificate status Trust validation failed: " + ex.getMessage() + "." + OutErrorMessage.get());
                    }
                }
                boolean b = verifySignature(docElement, signingcert.getPublicKey(), OutErrorMessage);
                if ((OutErrorMessage.get() == null || OutErrorMessage.get().length() == 0) && b) {
                    //no error message and its cryptographically valid
                    return true;
                }
                return false;
            }

            //last chance validation
            logger.info("signature did not have an embedded X509 public key. reverting to user specified certificate");
            //cert wasn't included in the signature, revert to some other means
            KeyStore ks = KeyStore.getInstance(map.getProperty(SIGNATURE_KEYSTORE_FILETYPE));
            URL url = Thread.currentThread().getContextClassLoader().getResource(map.getProperty(SIGNATURE_KEYSTORE_FILE));
            if (url == null) {
                try {
                    url = new File(map.getProperty(SIGNATURE_KEYSTORE_FILE)).toURI().toURL();
                } catch (Exception x) {
                }
            }
            if (url == null) {
                try {
                    url = this.getClass().getClassLoader().getResource(map.getProperty(SIGNATURE_KEYSTORE_FILE));
                } catch (Exception x) {
                }
            }
            if (url == null) {
                logger.error("");
                OutErrorMessage.set("The signed entity is signed but does not have a certificate attached and"
                        + "you didn't specify a keystore for me to look it up in. " + OutErrorMessage.get());
                return false;
            }
            KeyStore.PrivateKeyEntry keyEntry = null;

            ks.load(url.openStream(), map.getProperty(SIGNATURE_KEYSTORE_FILE_PASSWORD).toCharArray());

            if (map.getProperty(SIGNATURE_KEYSTORE_KEY_PASSWORD) == null) {
                keyEntry =
                        (KeyStore.PrivateKeyEntry) ks.getEntry(map.getProperty(SIGNATURE_KEYSTORE_KEY_ALIAS),
                        new KeyStore.PasswordProtection(map.getProperty(SIGNATURE_KEYSTORE_FILE_PASSWORD).toCharArray()));
            } else {
                keyEntry =
                        (KeyStore.PrivateKeyEntry) ks.getEntry(map.getProperty(SIGNATURE_KEYSTORE_KEY_ALIAS),
                        new KeyStore.PasswordProtection(map.getProperty(SIGNATURE_KEYSTORE_KEY_PASSWORD).toCharArray()));
            }


            Certificate origCert = keyEntry.getCertificate();
            if (map.containsKey(CHECK_TIMESTAMPS)) {
                if (origCert.getPublicKey() instanceof X509Certificate) {
                    X509Certificate x = (X509Certificate) origCert.getPublicKey();
                    x.checkValidity();
                }
            }
            PublicKey validatingKey = origCert.getPublicKey();
            return verifySignature(docElement, validatingKey, OutErrorMessage);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            logger.error("Error caught validating signature", e);
            OutErrorMessage.set(e.getMessage());
            return false;
        }
    }

    private KeyStore GetTrustStore() throws Exception {
        String type = map.getProperty(TRUSTSTORE_FILETYPE);
        if (type == null) {
            type = "JKS";
        }
        KeyStore ks = KeyStore.getInstance(type);
        boolean ksLoaded = false;

        //try windows trust store first
        try {
            if (map.getProperty(TRUSTSTORE_FILETYPE).equalsIgnoreCase("WINDOWS-ROOT")) {
                ks.load(null, null);
                ksLoaded = true;
                logger.info("trust store loaded from windows");
            }
        } catch (Exception ex) {
            logger.debug("unable to load truststore from windows", ex);
        }

        //load from thread classloader
        if (!ksLoaded) {
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource(map.getProperty(TRUSTSTORE_FILE));
                ks.load(url.openStream(), (map.getProperty(TRUSTSTORE_FILE_PASSWORD)).toCharArray());
                ksLoaded = true;
                logger.info("trust store loaded from classpath(1) " + map.getProperty(TRUSTSTORE_FILE));
            } catch (Exception x) {
                logger.debug("unable to load truststore from classpath", x);
            }
        }

        //load from this classloader
        if (!ksLoaded) {
            try {
                URL url = this.getClass().getClassLoader().getResource(map.getProperty(TRUSTSTORE_FILE));
                ks.load(url.openStream(), (map.getProperty(TRUSTSTORE_FILE_PASSWORD)).toCharArray());
                ksLoaded = true;
                logger.info("trust store loaded from classpath(2) " + map.getProperty(TRUSTSTORE_FILE));
            } catch (Exception x) {
                logger.debug("unable to load truststore from classpath", x);
            }
        }
        //load as a file
        if (!ksLoaded) {
            try {
                URL url = new File(map.getProperty(TRUSTSTORE_FILE)).toURI().toURL();
                ks.load(url.openStream(), (map.getProperty(TRUSTSTORE_FILE_PASSWORD)).toCharArray());
                ksLoaded = true;
                logger.info("trust store loaded from file " + map.getProperty(TRUSTSTORE_FILE));
            } catch (Exception x) {
                logger.debug("unable to load truststore from file", x);
            }
        }


        //    logger.error("Unable to load user specified trust store! attempting to load the default", ex);

        //load from system property
        if (!ksLoaded) {
            try {
                String truststore = System.getProperty("javax.net.ssl.keyStore");
                String pwd = System.getProperty("javax.net.ssl.keyStorePassword");
                if (truststore != null && pwd != null) {
                    ks.load(new File(truststore).toURI().toURL().openStream(), pwd.toCharArray());
                    ksLoaded = true;
                    logger.info("trust store loaded from sysprop " + truststore);
                }
            } catch (Exception ex) {
                logger.debug("unable to load truststore from sysprop", ex);
            }
        }

        if (!ksLoaded) {
            try {
                URL cacerts = new File(System.getenv("JAVA_HOME") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts").toURI().toURL();
                ks.load(cacerts.openStream(), "changeit".toCharArray());
                logger.info("trust store loaded from JRE " + cacerts.toExternalForm());
                ksLoaded = true;
            } catch (Exception c) {
                logger.debug("unable to load default JDK truststore", c);
            }
        }
        if (!ksLoaded) {
            try {
                URL cacerts = new File(System.getenv("JAVA_HOME") + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts").toURI().toURL();
                ks.load(cacerts.openStream(), "changeit".toCharArray());
                logger.info("trust store loaded from JRE " + cacerts.toExternalForm());
                ksLoaded = true;
            } catch (Exception c) {
                logger.debug("unable to load default jdk/jre truststore", c);
            }
        }
        if (!ksLoaded)
        {
            logger.warn("unable to load trust store!");
        }


        return ks;
    }

    private XMLSignatureFactory initXMLSigFactory() {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
        return fac;
    }

    private Reference initReference(XMLSignatureFactory fac) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        List transformers = new ArrayList();
        transformers.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));

        String dm = map.getProperty(SIGNATURE_OPTION_DIGEST_METHOD);
        if (dm == null) {
            dm = DigestMethod.SHA1;
        }
        Reference ref = fac.newReference("", fac.newDigestMethod(dm, null), transformers, null, null);
        return ref;
    }

    private SignedInfo initSignedInfo(XMLSignatureFactory fac) throws Exception {
        Reference ref = initReference(fac);
        String cm = null;
        cm = map.getProperty(CANONICALIZATIONMETHOD);
        String sigmethod = null;
        sigmethod = map.getProperty(SIGNATURE_METHOD);
        if (sigmethod == null) {
            sigmethod = SignatureMethod.RSA_SHA1;
        }
        if (cm == null) {
            cm = CanonicalizationMethod.EXCLUSIVE;
        }
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
                cm,
                (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(sigmethod,
                null), Collections.singletonList(ref));
        return si;
    }

    private boolean verifySignature(Element element, PublicKey validatingKey, AtomicReference<String> OutReadableErrorMessage) {
        if (OutReadableErrorMessage == null) {
            OutReadableErrorMessage = new AtomicReference<String>();
        }
        XMLSignatureFactory fac = initXMLSigFactory();
        NodeList nl = element.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new RuntimeException("Cannot find Signature element");
        }
        DOMValidateContext valContext = new DOMValidateContext(validatingKey, nl.item(0));
        try {
            valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);
            boolean coreValidity = signature.validate(valContext);
            // Check core validation status.
            if (coreValidity == false) {
                logger.warn("Signature failed core validation");
                boolean sv = signature.getSignatureValue().validate(valContext);
                logger.debug("signature validation status: " + sv);
                OutReadableErrorMessage.set("signature validation failed: " + sv + "." + OutReadableErrorMessage.get());
                // Check the validation status of each Reference.
                @SuppressWarnings("unchecked")
                Iterator<Reference> i = signature.getSignedInfo().getReferences().iterator();
                //System.out.println("---------------------------------------------");
                for (int j = 0; i.hasNext(); j++) {
                    Reference ref = (Reference) i.next();
                    boolean refValid = ref.validate(valContext);
                    logger.debug(j);
                    logger.debug("ref[" + j + "] validity status: " + refValid);
                    if (!refValid) {
                        OutReadableErrorMessage.set("signature reference " + j + " invalid. " + OutReadableErrorMessage.get());
                    }
                    logger.debug("Ref type: " + ref.getType() + ", URI: " + ref.getURI());
                    for (Object xform : ref.getTransforms()) {
                        logger.debug("Transform: " + xform);
                    }
                    String calcDigValStr = digestToString(ref.getCalculatedDigestValue());
                    String expectedDigValStr = digestToString(ref.getDigestValue());
                    logger.warn("    Calc Digest: " + calcDigValStr);
                    logger.warn("Expected Digest: " + expectedDigValStr);
                    if (!calcDigValStr.equalsIgnoreCase(expectedDigValStr)) {
                        OutReadableErrorMessage.set("digest mismatch for signature ref " + j + "." + OutReadableErrorMessage.get());
                    }
                }
            } else {
                logger.info("Signature passed core validation");
            }
            return coreValidity;
        } catch (Exception e) {
            OutReadableErrorMessage.set("signature validation failed: " + e.getMessage() + OutReadableErrorMessage.get());
            logger.fatal(e);
            return false;
        }
    }

    private String digestToString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private void signDOM(Node node, PrivateKey privateKey, Certificate origCert) {
        XMLSignatureFactory fac = initXMLSigFactory();
        X509Certificate cert = (X509Certificate) origCert;
        // Create the KeyInfo containing the X509Data.

        KeyInfoFactory kif = fac.getKeyInfoFactory();


        List<Object> x509Content = null;//new ArrayList<Object>();
        List<X509Data> data = new ArrayList<X509Data>();
        if (map.containsKey(SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN)) {
            x509Content = new ArrayList<Object>();

            x509Content.add(cert.getSubjectDN().getName());
            //  x509Content.add(cert);
            //x509Content.add(cert.getSubjectDN().getName());
            X509Data xd = kif.newX509Data(x509Content);
            data.add(xd);
        }

        //  if (map.containsKey(SIGNATURE_OPTION_CERT_INCLUSION_X500_PRINICPAL)) {
        // }
        if (map.containsKey(SIGNATURE_OPTION_CERT_INCLUSION_BASE64)) {
            x509Content = new ArrayList<Object>();
            x509Content.add(cert);
            //x509Content.add(cert.getSubjectX500Principal().getName());
            X509Data xd = kif.newX509Data(x509Content);
            data.add(xd);
        }
        if (map.containsKey(SIGNATURE_OPTION_CERT_INCLUSION_SERIAL)) {
            x509Content = new ArrayList<Object>();

            X509IssuerSerial issuer = kif.newX509IssuerSerial(cert.getIssuerX500Principal().getName(), cert.getSerialNumber());

            x509Content.add(issuer);
            X509Data xd = kif.newX509Data(x509Content);
            data.add(xd);
        }

        //  
        //x509Content.add(cert);


        KeyInfo ki = kif.newKeyInfo(data);

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext(privateKey, node);
        dsc.putNamespacePrefix(XML_DIGSIG_NS, "ns2");

        // Create the XMLSignature, but don't sign it yet.
        try {
            SignedInfo si = initSignedInfo(fac);
            XMLSignature signature = fac.newXMLSignature(si, ki);

            // Marshal, generate, and sign the enveloped signature.
            signature.sign(dsc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * searches local keystores for a referenced signing certificate
     *
     * @param childNodes
     * @return null or the public key of a signing certificate
     */
    private X509Certificate FindCert(NodeList childNodes) {
        try {
            for (int x = 0; x < childNodes.getLength(); x++) {
                if (childNodes.item(x).getLocalName().equalsIgnoreCase("X509SubjectName")) {

                    String dn = childNodes.item(x).getTextContent().trim();
                    return FindCertByDN(new X500Principal(dn));

                }
                if (childNodes.item(x).getLocalName().equalsIgnoreCase("X509IssuerSerial")) {
                    String X509IssuerName = null;
                    String X509SerialNumber = null;
                    for (int k = 0; k < childNodes.item(x).getChildNodes().getLength(); k++) {
                        if (childNodes.item(x).getChildNodes().item(x).getLocalName().equalsIgnoreCase("X509IssuerName")) {
                            X509IssuerName = childNodes.item(x).getTextContent().trim();
                        }
                        if (childNodes.item(x).getChildNodes().item(x).getLocalName().equalsIgnoreCase("X509SerialNumber")) {
                            X509SerialNumber = childNodes.item(x).getTextContent().trim();
                        }

                    }
                    if (X509IssuerName != null && X509SerialNumber != null) {
                        return FindCertByIssuer(X509IssuerName, X509SerialNumber);
                    }


                }
            }
        } catch (Exception ex) {
            logger.warn("error caught searching for a certificate", ex);
        }
        return null;
    }

    private X509Certificate FindCertByDN(X500Principal name) throws Exception {
        KeyStore ks = GetTrustStore();
        if (ks == null) {
            return null;
        }
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String nextElement = aliases.nextElement();
            Certificate certificate = ks.getCertificate(nextElement);
            X509Certificate x = (X509Certificate) certificate;
            if (x.getSubjectX500Principal().equals(name)) {
                return x;
            }
        }
        return null;
    }

    /**
     * Downloads a CRL from given HTTP/HTTPS/FTP URL, e.g.
     * http://crl.infonotary.com/crl/identity-ca.crl
     */
    private X509CRL downloadCRLFromWeb(String crlURL)
            throws MalformedURLException, IOException, CertificateException,
            CRLException {
        URL url = new URL(crlURL);
        InputStream crlStream = url.openStream();
        try {
            //	CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
            return crl;
        } finally {
            crlStream.close();
        }
    }

    private X509Certificate FindCertByIssuer(String X509IssuerName, String X509SerialNumber) throws Exception {
        KeyStore ks = GetTrustStore();
        if (ks == null) {
            return null;
        }
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String nextElement = aliases.nextElement();
            Certificate certificate = ks.getCertificate(nextElement);
            X509Certificate x = (X509Certificate) certificate;
            if (x.getIssuerDN().getName().equals(X509IssuerName)
                    && x.getSerialNumber().toString().equalsIgnoreCase(X509SerialNumber)) {
                return x;
            }
        }
        return null;
    }
}
