/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */

using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Xml;
using System.Text;
using System.Xml;

namespace org.apache.juddi.v3.client.cryptor
{
    /// <summary>
    /// A utility class for signing and verifying JAXB Objects, such as UDDI  entities.   
    /// Notes: This class only supports elements that are signed once. 
    /// Multiple signature are not currently supported.
    /// 
    /// Digital signatures can be generated using a standalone PFX file or via the Windows Certificate store
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class DigSigUtil
    {
        /// <summary>
        /// creates an uninitialized DigSigUtil, use put to configure
        /// </summary>
        public DigSigUtil()
        {
            map = new Properties();
        }


        /// <summary>
        /// Constructor that will accept a properties set from the juddi config file, or whatever you want
        /// 
        /// </summary>
        /// <param name="c"></param>
        public DigSigUtil(Properties c)
        {
            map = c;
        }

        private Log logger = LogFactory.getLog(typeof(DigSigUtil));

        /// <summary>
        /// added a new key/value to the running config
        /// </summary>
        /// <param name="key"></param>
        /// <param name="value"></param>
        public void put(String key, String value)
        {
            map.put(key, value);
        }

        /**
         * clears the configuration for reuse
         */
        public void clear()
        {

        }
        private Properties map = new Properties();

        /**
     * This is the location of the keystore
     *
     * If referencing a Windows certificate store, use WINDOWS-MY as a value
     * with a null password
     */
        public readonly static String SIGNATURE_KEYSTORE_FILE = "keyStorePath";
        /**
         * The type of file, such as JKS for most Java applications, or WINDOWS-MY
         * to use the Windows certificate store of the current user or KeychainStore
         * for MacOS
         */
        public readonly static String SIGNATURE_KEYSTORE_FILETYPE = "keyStoreType";
        public readonly static string SIGNATURE_KEYSTORE_FILETYPE_VALUE_PFX = "PFX";
        public readonly static String SIGNATURE_KEYSTORE_FILE_PASSWORD = "filePassword";
        public readonly static String SIGNATURE_KEYSTORE_KEY_PASSWORD = "keyPassword";
        public readonly static String SIGNATURE_KEYSTORE_KEY_ALIAS = "keyAlias";
        public readonly static String TRUSTSTORE_FILE = "trustStorePath";
        public readonly static String TRUSTSTORE_FILETYPE = "trustStoreType";
        public readonly static String TRUSTSTORE_FILE_PASSWORD = "trustStorePassword";
        /**
         * default is CanonicalizationMethod.EXCLUSIVE
         *
         * @see CanonicalizationMethod
         */
        public readonly static String CANONICALIZATIONMETHOD = "CanonicalizationMethod";
        /**
         * default is http://www.w3.org/2000/09/xmldsig#rsa-sha1
         *
         * @see SignatureMethod
         */
        public readonly static String SIGNATURE_METHOD = "SignatureMethod";
        /**
         * Defines whether or not a certificate is included with the signature.
         * Values - Include whole X509 Public Key in the signature (recommended)
         * (default) * Example
         * <example>
         * <code>
         * Map map = new HashMap();
         * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "true");
         * </code>
         * </example>
         */
        public readonly static String SIGNATURE_OPTION_CERT_INCLUSION_BASE64 = "BASE64";


        /// <summary>
        /// Include the signer's serial of the public key and the issuer's subject name
        /// 
        ///  Clients will not be able to validate the signature unless they have a copy of the signer's public key 
        ///  in a trust store or the full certificate is included
        ///  out of band
        ///      
        ///  Example
        ///  <pre>
        ///  Map map = new HashMap();
        ///  map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "true");</pre>
        ///  any value can be used.
        /// @see SIGNATURE_OPTION_CERT_INCLUSION_BASE64
        /// </summary>
        public readonly static String SIGNATURE_OPTION_CERT_INCLUSION_SERIAL = "SERIAL";
        /*
         * Include the signer's Subject DN of the public key.
         * 
         * Clients will not be able to validate the signature unless they have a copy of the signer's public key 
         * in a trust store or the full certificate is included
         * out of band
         *     
         * Example
         * <pre>
         * Map map = new HashMap();
         * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "true");</pre>
         * any value can be used.
         *@see SIGNATURE_OPTION_CERT_INCLUSION_BASE64
         */
        public readonly static String SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN = "SUBJECTDN";
        /*
         * Include the signer's X500 Prinicple of the public key.
         * 
         * Clients will not be able to validate the signature unless they have a copy of the signer's public key 
         * in a trust store or the full certificate is included
         * out of band
         * <example>    
         * Example
         * <code>
         * Map map = new HashMap();
         * map.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_X500_PRINICPAL, "true");
         * </code></example>
         *@see SIGNATURE_OPTION_CERT_INCLUSION_BASE64
         */
        //public readonly static String SIGNATURE_OPTION_CERT_INCLUSION_X500_PRINICPAL = "X500";
        public readonly static String XML_DIGSIG_NS = "http://www.w3.org/2000/09/xmldsig#";
        /**
         * Default value DigestMethod.SHA1 =
         * "http://www.w3.org/2000/09/xmldsig#sha1"
         *
         * @see javax.xml.crypto.dsig.DigestMethod
         */
        public readonly static String SIGNATURE_OPTION_DIGEST_METHOD = "digestMethod";
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
        public readonly static String CHECK_TIMESTAMPS = "checkTimestamps";
        public readonly static String CHECK_REVOCATION_STATUS_OCSP = "checkRevocationOCSP";
        public readonly static String CHECK_REVOCATION_STATUS_CRL = "checkRevocationCRL";
        public readonly static String CHECK_TRUST_CHAIN = "checkTrust";



        /// <summary>
        ///
        /// Verifies the signature on an enveloped digital signature on a UDDI
        /// entity, such as a business, service, tmodel or binding template. 
        /// It is expected that either the public key of the signing certificate is
        /// included within the signature keyinfo section OR that sufficient
        /// information is provided in the signature to reference a public key
        /// located within the Trust Store provided. Optionally, this function
        /// also validate the signing certificate using the options provided to the
        /// configuration map.
        /// </summary>
        /// <param name="obj"></param>
        /// <param name="OutErrorMessage"></param>
        /// <returns></returns>
        public bool verifySignedUddiEntity(Object obj, out String OutErrorMessage)
        {
            if (obj == null)
            {
                throw new ArgumentNullException("obj");
            }
            string msg = "";
            //serialize to string
            XmlDocument doc = null;
            if (obj.GetType().Equals(typeof(bindingTemplate)))
            {
                PrintUDDI<bindingTemplate> p = new PrintUDDI<bindingTemplate>();
                String s = p.print(obj);
                doc = StringToXmlDocument(s);
            }
            if (obj.GetType().Equals(typeof(businessService)))
            {
                PrintUDDI<businessService> p = new PrintUDDI<businessService>();
                String s = p.print(obj);
                doc = StringToXmlDocument(s);
            }
            if (obj.GetType().Equals(typeof(businessEntity)))
            {
                PrintUDDI<businessEntity> p = new PrintUDDI<businessEntity>();
                String s = p.print(obj);
                doc = StringToXmlDocument(s);
            }
            if (obj.GetType().Equals(typeof(tModel)))
            {
                PrintUDDI<tModel> p = new PrintUDDI<tModel>();
                String s = p.print(obj);
                doc = StringToXmlDocument(s);
            }

            //get signing certificate 
            X509Certificate2 signingCert = getSigningCertificatePublicKey(doc);

            //check timestamps
            if (map.containsKey(DigSigUtil.CHECK_TIMESTAMPS) && map.getProperty(DigSigUtil.CHECK_TIMESTAMPS).Equals("true", StringComparison.CurrentCultureIgnoreCase))
            {
                if (DateTime.Now < signingCert.NotBefore)
                {
                    msg += "Signing certificate is not yet valid";
                }
                if (DateTime.Now > signingCert.NotAfter)
                {
                    msg += "Signing certificate is not yet valid";
                }
            }
            if (map.containsKey(DigSigUtil.CHECK_TRUST_CHAIN) && map.getProperty(DigSigUtil.CHECK_TRUST_CHAIN).Equals("true", StringComparison.CurrentCultureIgnoreCase))
            {
                //check trust
                X509Chain chain = new X509Chain();
                chain.ChainPolicy.RevocationMode = X509RevocationMode.NoCheck;
                chain.ChainPolicy.RevocationFlag = X509RevocationFlag.EntireChain;
                bool r = chain.Build(signingCert);
                if (!r)
                {
                    foreach (X509ChainElement element in chain.ChainElements)
                    {
                        msg += ("Element issuer name: " + element.Certificate.Issuer + " is " + element.Certificate.Verify());
                    }
                }
            }
            //check ocsp
            //check crl
            if ((map.containsKey(DigSigUtil.CHECK_REVOCATION_STATUS_OCSP) && map.getProperty(DigSigUtil.CHECK_REVOCATION_STATUS_OCSP).Equals("true", StringComparison.CurrentCultureIgnoreCase)) ||
                (map.containsKey(DigSigUtil.CHECK_REVOCATION_STATUS_CRL) && map.getProperty(DigSigUtil.CHECK_REVOCATION_STATUS_CRL).Equals("true", StringComparison.CurrentCultureIgnoreCase)))
            {
                //check trust
                X509Chain chain = new X509Chain();
                chain.ChainPolicy.RevocationMode = X509RevocationMode.Online;
                chain.ChainPolicy.RevocationFlag = X509RevocationFlag.EntireChain;
                chain.ChainPolicy.VerificationFlags = X509VerificationFlags.NoFlag;
                bool r = chain.Build(signingCert);
                if (!r)
                {
                    foreach (X509ChainElement element in chain.ChainElements)
                    {
                        msg += ("Element issuer name: " + element.Certificate.Issuer + " is " + element.Certificate.Verify());
                    }
                }
            }


            //verify crypto (math)
            String verifytext = "";
            bool valid = verifySignature(doc, signingCert, out verifytext);
            OutErrorMessage = verifytext + msg;
            return valid;
        }

        private bool verifySignature(XmlDocument Doc, X509Certificate2 cert, out string OutErrorMessage)
        {
            string msg = "";


            // Create a new SignedXml object and pass it 
            // the XML document class.
            SignedXml signedXml = new SignedXml(Doc);

            // Find the "Signature" node and create a new 
            // XmlNodeList object.
            XmlNodeList nodeList = Doc.GetElementsByTagName("Signature");

            // Throw an exception if no signature was found. 
            if (nodeList.Count <= 0)
            {
                msg += ("Verification failed: No Signature was found in the document.");
            }

            // This example only supports one signature for 
            // the entire XML document.  Throw an exception  
            // if more than one signature was found. 
            if (nodeList.Count >= 2)
            {
                msg += ("Verification failed: More that one signature was found for the document.");
            }

            // Load the first <signature> node.  
            signedXml.LoadXml((XmlElement)nodeList[0]);

            // Check the signature and return the result. 
            OutErrorMessage = msg;
            return signedXml.CheckSignature(cert, true);

        }

        private X509Certificate2 getSigningCertificatePublicKey(XmlDocument doc)
        {
            if (doc == null)
                throw new ArgumentNullException("doc");
            XmlNode node = doc.ChildNodes[1];   //this should be the uddi entry
            X509Certificate2 cert = null;
            IEnumerator it = node.ChildNodes.GetEnumerator();
            while (it.MoveNext())
            {
                XmlNode x = (XmlNode)it.Current;
                if (x.NamespaceURI.Equals(DigSigUtil.XML_DIGSIG_NS, StringComparison.CurrentCultureIgnoreCase) &&
                    x.LocalName.Equals("Signature", StringComparison.CurrentCultureIgnoreCase))
                {
                    IEnumerator it2 = x.ChildNodes.GetEnumerator();
                    while (it2.MoveNext())
                    {
                        XmlNode x2 = (XmlNode)it2.Current;
                        if (x2.LocalName.Equals("KeyInfo", StringComparison.CurrentCultureIgnoreCase))
                        {
                            IEnumerator it3 = x2.ChildNodes.GetEnumerator();
                            while (it3.MoveNext())
                            {
                                XmlNode x3 = (XmlNode)it3.Current;
                                if (x3.LocalName.Equals("X509Data", StringComparison.CurrentCultureIgnoreCase))
                                {
                                    //X509Certificate
                                    IEnumerator it4 = x3.ChildNodes.GetEnumerator();
                                    while (it4.MoveNext())
                                    {
                                        XmlNode x4 = (XmlNode)it4.Current;
                                        if (x4.LocalName.Equals("X509Certificate", StringComparison.CurrentCultureIgnoreCase))
                                        {
                                            //X509Certificate
                                            String c =
                                                //"-----BEGIN CERTIFICATE-----\n"
                                                x4.InnerText;
                                            //+ "\n-----END CERTIFICATE-----";

                                            cert = new X509Certificate2(Convert.FromBase64String(c));
                                            logger.info("embedded certificate found, X509 public key " + cert.Subject);
                                            return cert;
                                        }

                                    }
                                }

                            }
                        }

                    }
                }
            }
            return null;
        }

        /// <summary>
        ///  Digitally signs a UDDI entity, such as a business, service, tmodel or
        /// binding template using the map to provide certificate key stores and
        /// credentials. The UDDI entity MUST support XML Digital Signatures
        /// (tModel, Business, Service, Binding Template)
        /// </summary>
        /// <param name="bt"></param>
        /// <returns></returns>
        public object signUddiEntity(object bt)
        {

            XmlDocument doc = null;
            if (bt.GetType().Equals(typeof(bindingTemplate)))
            {
                PrintUDDI<bindingTemplate> p = new PrintUDDI<bindingTemplate>();
                String s = p.print(bt);
                doc = StringToXmlDocument(s);
            }
            if (bt.GetType().Equals(typeof(businessService)))
            {
                PrintUDDI<businessService> p = new PrintUDDI<businessService>();
                String s = p.print(bt);
                doc = StringToXmlDocument(s);
            }
            if (bt.GetType().Equals(typeof(businessEntity)))
            {
                PrintUDDI<businessEntity> p = new PrintUDDI<businessEntity>();
                String s = p.print(bt);
                doc = StringToXmlDocument(s);
            }
            if (bt.GetType().Equals(typeof(tModel)))
            {
                PrintUDDI<tModel> p = new PrintUDDI<tModel>();
                String s = p.print(bt);
                doc = StringToXmlDocument(s);
            }


            X509Certificate2 key = GetKey();
            XmlElement sig = SignXml(doc, key);
            //append the signature to the document
            doc.ChildNodes[1].AppendChild(sig);

            String signedXml = doc.OuterXml;
            if (bt.GetType().Equals(typeof(bindingTemplate)))
            {
                PrintUDDI<bindingTemplate> p = new PrintUDDI<bindingTemplate>();
                return p.createObject(signedXml);
            }
            if (bt.GetType().Equals(typeof(businessService)))
            {
                PrintUDDI<businessService> p = new PrintUDDI<businessService>();
                return p.createObject(signedXml);
            }
            if (bt.GetType().Equals(typeof(businessEntity)))
            {
                PrintUDDI<businessEntity> p = new PrintUDDI<businessEntity>();
                return p.createObject(signedXml);
            }
            if (bt.GetType().Equals(typeof(tModel)))
            {
                PrintUDDI<tModel> p = new PrintUDDI<tModel>();
                return p.createObject(signedXml);
            }
            return bt;
        }


        private X509Certificate2 GetKey()
        {
            
            String storelocation = map.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE);
            if (storelocation.Equals( DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE_VALUE_PFX, StringComparison.CurrentCultureIgnoreCase))
            {
                logger.info("Attempting to load certificate from " + map.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE));
                X509Certificate2 cert = new X509Certificate2(map.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE), 
                    //this should be decrypted already
                    map.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD));
                return cert;

            }
            String storename = map.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE);
            String keyserial = map.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS);
            
            X509Store store = new X509Store(
                (StoreName)Enum.Parse(typeof(StoreName), storename),
                (StoreLocation)Enum.Parse(typeof(StoreLocation), storelocation));
            store.Open(OpenFlags.ReadOnly);
            X509Certificate2Enumerator it = store.Certificates.GetEnumerator();
            while (it.MoveNext())
            {
                X509Certificate2 cert = it.Current;
                if (cert.HasPrivateKey)
                {
                    //do some comparisions
                    if (cert.SerialNumber.Equals(keyserial, StringComparison.CurrentCultureIgnoreCase) ||
                        cert.FriendlyName.Equals(keyserial, StringComparison.CurrentCultureIgnoreCase) ||
                        cert.Subject.Equals(keyserial, StringComparison.CurrentCultureIgnoreCase) ||
                        cert.Thumbprint.Equals(keyserial, StringComparison.CurrentCultureIgnoreCase))
                    {
                        store.Close();
                        return cert;
                    }
                }
            }
            store.Close();
            return null;

        }


        private XmlDocument StringToXmlDocument(String s)
        {
            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.PreserveWhitespace = false;
            xmlDoc.LoadXml(s);
            return xmlDoc;
        }


        //source http://objectmix.com/dotnet/794749-digitally-sign-xml-doc-x509certificate-solution.html
        //which came from the msdn tutorial

        //Certificate get Signature method
        private XmlElement SignXml(XmlDocument xmlDoc, X509Certificate2 cert)
        {
            //preserve ws - difference here I noticed - mine was set to true
            xmlDoc.PreserveWhitespace = false;

            // Create a SignedXml object.
            SignedXml signedXml = new SignedXml(xmlDoc);

            // Load the certificate into a KeyInfoX509Data object
            // and add it to the KeyInfo object.
            //// Add an RSAKeyValue KeyInfo (optional; helps recipient find key to validate).
            KeyInfo keyInfo = new KeyInfo();
            if (map.getProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64).Equals("true", StringComparison.CurrentCultureIgnoreCase))
                keyInfo.AddClause(new KeyInfoX509Data(cert));
            if (map.getProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN).Equals("true", StringComparison.CurrentCultureIgnoreCase))
            {
                KeyInfoX509Data data = new KeyInfoX509Data();
                data.AddSubjectName(cert.SubjectName.Name);
                keyInfo.AddClause(data);
            }
            if (map.getProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL).Equals("true", StringComparison.CurrentCultureIgnoreCase))
            {
                KeyInfoX509Data data = new KeyInfoX509Data();
                data.AddIssuerSerial(cert.IssuerName.Name, cert.SerialNumber);
                keyInfo.AddClause(data);
            }

            signedXml.KeyInfo = keyInfo;

            //CANON method
            signedXml.SignedInfo.CanonicalizationMethod = map.getProperty(DigSigUtil.CANONICALIZATIONMETHOD);
            if (String.IsNullOrEmpty(signedXml.SignedInfo.CanonicalizationMethod))
                signedXml.SignedInfo.CanonicalizationMethod = SignedXml.XmlDsigExcC14NWithCommentsTransformUrl;

            signedXml.SignedInfo.SignatureMethod = map.getProperty(DigSigUtil.SIGNATURE_METHOD);
            if (String.IsNullOrEmpty(signedXml.SignedInfo.SignatureMethod))
                signedXml.SignedInfo.SignatureMethod = SignedXml.XmlDsigRSASHA1Url;


            // Set the rsaKey to the certificate's private key
            RSACryptoServiceProvider rsaKey = (RSACryptoServiceProvider)cert.PrivateKey;

            // Add the key to the SignedXml document.
            signedXml.SigningKey = rsaKey;

            // Create a reference to be signed.
            Reference reference = new Reference();
            reference.Uri = "";

            // Add an enveloped transformation to the reference.
            XmlDsigEnvelopedSignatureTransform env = new XmlDsigEnvelopedSignatureTransform();
            reference.AddTransform(env);


            // Add the reference to the SignedXml object.
            signedXml.AddReference(reference);

            // Now we can compute the signature.
            signedXml.ComputeSignature();
            return signedXml.GetXml();
            //   return signedXml;

        }

        //gets payload data and returns xmn XMLDocument
        private XmlDocument GetPayLoadData(string xmlstring)
        {
            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(xmlstring);
            return xmlDoc;

        }
    }
}
