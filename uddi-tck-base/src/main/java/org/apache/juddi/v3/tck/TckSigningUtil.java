package org.apache.juddi.v3.tck;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TckSigningUtil {

    private static XMLSignatureFactory initXMLSigFactory() {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
        return fac;
    }

    private static Reference initReference(XMLSignatureFactory fac) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        List transformers = new ArrayList();
        transformers.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null), transformers, null, null);
        return ref;
    }

    private static SignedInfo initSignedInfo(XMLSignatureFactory fac) throws Exception {
        Reference ref = initReference(fac);
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null), fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null), Collections.singletonList(ref));
        return si;
    }

    public static boolean verifySignature(Element element, PublicKey validatingKey) {
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
                System.err.println("Signature failed core validation");
                boolean sv = signature.getSignatureValue().validate(valContext);
                System.out.println("signature validation status: " + sv);
                // Check the validation status of each Reference.
                @SuppressWarnings("unchecked")
                Iterator<Reference> i = signature.getSignedInfo().getReferences().iterator();
                System.out.println("---------------------------------------------");
                for (int j = 0; i.hasNext(); j++) {
                    Reference ref = (Reference) i.next();
                    boolean refValid = ref.validate(valContext);
                    System.out.println("ref[" + j + "] validity status: " + refValid);
                    System.out.println("Ref type: " + ref.getType() + ", URI: " + ref.getURI());
                    for (Object xform : ref.getTransforms()) {
                        System.out.println("Transform: " + xform);
                    }
                    String calcDigValStr = digestToString(ref.getCalculatedDigestValue());
                    String expectedDigValStr = digestToString(ref.getDigestValue());
                    System.out.println("    Calc Digest: " + calcDigValStr);
                    System.out.println("Expected Digest: " + expectedDigValStr);
                    InputStream is = ref.getDigestInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                    is.close();
                    System.out.println("---------------------------------------------");
                }
            } else {
                System.out.println("Signature passed core validation");
            }
            return coreValidity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String digestToString(byte[] digest) {
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

    public static void signDOM(Node node, PrivateKey privateKey, Certificate origCert) {
        XMLSignatureFactory fac = initXMLSigFactory();
        X509Certificate cert = (X509Certificate) origCert;
        // Create the KeyInfo containing the X509Data.
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = new ArrayList<Object>();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext(privateKey, node);
        dsc.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ns2");

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
    
    public static void serializeNode(Node node, String filename) {
        try {
            Transformer xform = TransformerFactory.newInstance().newTransformer();
            DOMSource domSrc = new DOMSource(node);
            FileOutputStream fos = new FileOutputStream(filename);
            StreamResult streamResult = new StreamResult(fos);
            xform.transform(domSrc, streamResult);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
