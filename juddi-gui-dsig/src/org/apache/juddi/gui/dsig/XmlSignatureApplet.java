/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.gui.dsig;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import netscape.javascript.JSObject;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Alex O'Ree
 */
public class XmlSignatureApplet extends java.applet.Applet {

    /**
     * Initializes the applet XmlSignatureApplet
     */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setupCertificates();
    }

    
    
    private XMLSignatureFactory initXMLSigFactory() {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
        return fac;
    }

    private Reference initReference(XMLSignatureFactory fac) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        List transformers = new ArrayList();
        transformers.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));

        //  String dm = map.getProperty(SIGNATURE_OPTION_DIGEST_METHOD);
        //if (dm == null) {
        String dm = DigestMethod.SHA1;
        //}
        Reference ref = fac.newReference("", fac.newDigestMethod(dm, null), transformers, null, null);
        return ref;
    }

    private SignedInfo initSignedInfo(XMLSignatureFactory fac) throws Exception {
        Reference ref = initReference(fac);
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
                (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1,
                null),
                Collections.singletonList(ref));
        return si;
    }

    private static Document stringToDom(String xmlSource)
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

    private String sign(String xml) throws Exception {

        //DOMResult domResult = new DOMResult();

        //JAXB.marshal(jaxbObj, domResult);
        Document doc = stringToDom(xml);
        Element docElement = doc.getDocumentElement();
        //  KeyStore.PrivateKeyEntry keyEntry = null;
        // keyEntry= keyStore.getKey((String)jList1.getSelectedValue(), null);
        //   keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry((String) jList1.getSelectedValue(), null);
        PrivateKey key = (PrivateKey) keyStore.getKey((String) jList1.getSelectedValue(), null);

        // PrivateKey privateKey = keyStore.getKey((String)jList1.getSelectedValue(),null);
        Certificate origCert = keyStore.getCertificate((String) jList1.getSelectedValue());
        //PublicKey validatingKey = origCert.getPublicKey();
        this.signDOM(docElement, key, origCert);

        //  DOMSource domSource = new DOMSource(doc);

        return getStringFromDoc(doc);

    }

    public String getStringFromDoc(org.w3c.dom.Document doc) {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("xml-declaration", false);
        //lsSerializer.getDomConfig().setParameter("xml-declaration", false);
        
        return lsSerializer.writeToString(doc);
    }
    KeyStore keyStore = null;
    KeyStore firefox = null;

    private void setupCertificates() {

        this.jList1.clearSelection();
        this.jList1.removeAll();
        Vector<String> certs = new Vector<String>();

        //Provider p1 = new sun.security.pkcs11.SunPKCS11(strCfg);
        //Security.addProvider(p1);
        //KeyStore keyStore = KeyStore.getInstance("PKCS11");
        //printMessageToConsole("Key Store instance created");
        //keyStore.load(null, "password".toCharArray());



        //covers all modern browsers in windows
        try {
            keyStore = KeyStore.getInstance("Windows-MY");
            keyStore.load(null, null);
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, ex.getMessage());

        }
        //firefox keystore
        if (keyStore != null) {

            try {

                String strCfg = System.getProperty("user.home") + File.separator
                        + "jdk6-nss-mozilla.cfg";
                Provider p1 = new sun.security.pkcs11.SunPKCS11(strCfg);
                Security.addProvider(p1);
                keyStore = KeyStore.getInstance("PKCS11");
                keyStore.load(null, "password".toCharArray());
            } catch (Exception ex) {
                //JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
        //MacOS with Safari possibly others
        if (keyStore != null) {
            try {
                keyStore = KeyStore.getInstance("KeychainStore");
                keyStore.load(null, null);

            } catch (Exception ex) {
                //JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
        try {
            //printMessageToConsole("Key Store loaded");
            Enumeration<String> aliases = keyStore.aliases();

            while (aliases.hasMoreElements()) {
                String a = aliases.nextElement();
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(a);
                //PublicKey publicKey = certificate.getPublicKey();
                //  X509Certificate cert = (X509Certificate) publicKey;
                try {
                    Key key = keyStore.getKey(a, null);
                    certs.add(a);

                } catch (Exception x) {
                    System.out.println("error loading certificate " + a + " " + x.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        jList1.setListData(certs);
        if (!certs.isEmpty())
        {
            jList1.setSelectedIndex(0);
        }
    }
    public final static String XML_DIGSIG_NS = "http://www.w3.org/2000/09/xmldsig#";

    private void signDOM(Node node, PrivateKey privateKey, Certificate origCert) {
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
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        jButton1.setText("Sign");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(jList1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        JSObject window = JSObject.getWindow(this);
        Object object2 = window.call("getBrowserName", null);
        Object object1 = window.call("getOsName", null);
        String browserName = (String) object2;
        String osName = (String) object2;


        //get the xml
        String xml = (String) window.call("getXml", new Object[]{});
        //sign it

        String signedXml = "error!";;
        try {
            signedXml = this.sign(xml);
        } catch (Exception ex) {
            signedXml = ex.getMessage();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        //write it back to the web page
        window.call("writeXml", new Object[]{signedXml});
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
