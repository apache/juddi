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
package org.apache.juddi.gui.dsig;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXB;
import netscape.javascript.JSObject;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.TModel;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * This is the current Digital Signature Applet used by juddi-gui. It can easily
 * be adapted to sign any xml document
 *
 * @author <a href="mailto:alexoree@apache.org>Alex O'Ree</a>
 */
public class XmlSigApplet2 extends java.applet.Applet {

   private static final long serialVersionUID = 1L;

   /**
    * Initializes the applet XmlSigApplet2
    */
   @Override
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

   /**
    * this converts a xml document to a string for writing back to the browser
    *
    * @param doc
    * @return string
    */
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

      String keyStoreError = "";
      //covers all modern browsers in windows
      if (System.getProperty("os.name").startsWith("Windows")) {
         try {
            keyStore = KeyStore.getInstance("Windows-MY");
            keyStore.load(null, null);
         } catch (Exception ex) {
            keyStoreError += "Error loading Windows cert store " + ex.getMessage() + "\n";
	            //ex.printStackTrace();
            //JOptionPane.showMessageDialog(this, ex.getMessage());
         }
      }
      //firefox keystore
      if (keyStore == null) {

         try {

            String strCfg = System.getProperty("user.home") + File.separator
                    + "jdk6-nss-mozilla.cfg";
                //   Provider p1 = new sun.security.pkcs11.SunPKCS11(strCfg);
            //   Security.addProvider(p1);
            keyStore = KeyStore.getInstance("PKCS11");
            keyStore.load(null, "password".toCharArray());
         } catch (Exception ex) {
            //JOptionPane.showMessageDialog(this, ex.getMessage());
            keyStoreError += "Error loading Firefox cert store " + ex.getMessage() + "\n";
            //ex.printStackTrace();
         }
      }
      //MacOS with Safari possibly others
      if (keyStore == null) {
         try {
            keyStore = KeyStore.getInstance("KeychainStore");
            keyStore.load(null, null);

         } catch (Exception ex) {
                //JOptionPane.showMessageDialog(this, ex.getMessage());
            //ex.printStackTrace();
            keyStoreError += "Error loading MACOS Key chain cert store " + ex.getMessage() + "\n";
         }
      }

      if (keyStore == null) {
         System.err.println(keyStoreError);
          jTextArea1.setText(keyStoreError);
          jTabbedPane1.setSelectedIndex(2);
         
      } else {
         try {
            Enumeration<String> aliases = keyStore.aliases();

            while (aliases.hasMoreElements()) {
               String a = aliases.nextElement();
               X509Certificate certificate = (X509Certificate) keyStore.getCertificate(a);
               //this is needed to test for access

               try {
                  char[] cp = jPasswordField1.getPassword();
                  if (cp != null && cp.length <= 0) {
                     cp = null;
                  }
                  if (cp != null) {
                     String s = new String(cp);
                     s = s.trim();
                     if ("".equalsIgnoreCase(s)) {
                        cp = null;
                     }
                  }
                  Key key = keyStore.getKey(a, cp);
                  certs.add(a);

               } catch (Exception x) {
                  System.out.println("error loading certificate " + a + " " + x.getMessage());
               }
            }

         } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
         }
      }
      jList1.setListData(certs);
      if (!certs.isEmpty()) {
         jList1.setSelectedIndex(0);
      }
   }

   /**
    * This method is called from within the init() method to initialize the
    * form. WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      buttonGroup1 = new javax.swing.ButtonGroup();
      jPanel3 = new javax.swing.JPanel();
      jButton3 = new javax.swing.JButton();
      jTabbedPane1 = new javax.swing.JTabbedPane();
      jPanel1 = new javax.swing.JPanel();
      jButton1 = new javax.swing.JButton();
      jScrollPane1 = new javax.swing.JScrollPane();
      jList1 = new javax.swing.JList();
      jButton2 = new javax.swing.JButton();
      jLabel5 = new javax.swing.JLabel();
      jPanel2 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      isIncludeSubjectName = new javax.swing.JCheckBox();
      isIncludePublicKey = new javax.swing.JCheckBox();
      isIncludeIssuer = new javax.swing.JCheckBox();
      jLabel2 = new javax.swing.JLabel();
      jTextFieldSigMethod = new javax.swing.JTextField();
      jLabel3 = new javax.swing.JLabel();
      jTextFieldDigestMethod = new javax.swing.JTextField();
      jLabel4 = new javax.swing.JLabel();
      jTextFieldc14n = new javax.swing.JTextField();
      jPanel4 = new javax.swing.JPanel();
      jScrollPane2 = new javax.swing.JScrollPane();
      jTextArea1 = new javax.swing.JTextArea();
      jPanel5 = new javax.swing.JPanel();
      jToggleButton1 = new javax.swing.JToggleButton();
      jPasswordField1 = new javax.swing.JPasswordField();
      jLabel6 = new javax.swing.JLabel();
      jLabel7 = new javax.swing.JLabel();

      jButton3.setText("jButton3");

      setLayout(new java.awt.BorderLayout());

      jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
      jButton1.setText("Digitally Sign");
      jButton1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });

      jScrollPane1.setViewportView(jList1);

      jButton2.setText("Show Certificate Details");
      jButton2.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
         }
      });

      jLabel5.setText("Available Certificates");

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap(70, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(82, 82, 82))
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
               .addGroup(jPanel1Layout.createSequentialGroup()
                  .addComponent(jLabel5)
                  .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap())
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
            .addComponent(jButton2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton1)
            .addContainerGap())
      );

      jTabbedPane1.addTab("Sign", jPanel1);

      jLabel1.setText("Advanced Settings");

      isIncludeSubjectName.setSelected(true);
      isIncludeSubjectName.setText("Include your certificate's subject name");

      isIncludePublicKey.setSelected(true);
      isIncludePublicKey.setText("Include your public key in the signature (recommended)");

      isIncludeIssuer.setText("Include your certificate's issuer and your certificate's serial");

      jLabel2.setText("Signature Method");

      jTextFieldSigMethod.setText("http://www.w3.org/2000/09/xmldsig#rsa-sha1");

      jLabel3.setText("Digest Method");

      jTextFieldDigestMethod.setText("http://www.w3.org/2000/09/xmldsig#sha1");
      jTextFieldDigestMethod.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jTextFieldDigestMethodActionPerformed(evt);
         }
      });

      jLabel4.setText("Canonicalization Method");

      jTextFieldc14n.setText("http://www.w3.org/2001/10/xml-exc-c14n#");
      jTextFieldc14n.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jTextFieldc14nActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jTextFieldDigestMethod)
               .addComponent(jTextFieldSigMethod)
               .addGroup(jPanel2Layout.createSequentialGroup()
                  .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jLabel1)
                     .addComponent(jLabel2)
                     .addComponent(isIncludeIssuer)
                     .addComponent(isIncludePublicKey)
                     .addComponent(isIncludeSubjectName)
                     .addComponent(jLabel3)
                     .addComponent(jLabel4))
                  .addGap(0, 0, Short.MAX_VALUE))
               .addComponent(jTextFieldc14n, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
            .addContainerGap())
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(isIncludePublicKey)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(isIncludeSubjectName)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(isIncludeIssuer)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextFieldSigMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextFieldDigestMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextFieldc14n, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(127, Short.MAX_VALUE))
      );

      jTabbedPane1.addTab("Settings", jPanel2);

      jTextArea1.setColumns(20);
      jTextArea1.setRows(5);
      jScrollPane2.setViewportView(jTextArea1);

      javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
      jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
            .addContainerGap())
      );
      jPanel4Layout.setVerticalGroup(
         jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .addContainerGap())
      );

      jTabbedPane1.addTab("Info", jPanel4);

      jToggleButton1.setText("OK");
      jToggleButton1.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            jToggleButton1MouseClicked(evt);
         }
      });
      jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jToggleButton1ActionPerformed(evt);
         }
      });

      jLabel6.setText("Password");

      jLabel7.setText("<html>For Firefox and certain browser and OS combinations, you may need to specify a password in order to get access to certificates. This is typically used for non-Windows users.</html>");

      javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
      jPanel5.setLayout(jPanel5Layout);
      jPanel5Layout.setHorizontalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
         .addGroup(jPanel5Layout.createSequentialGroup()
            .addGap(34, 34, 34)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addGroup(jPanel5Layout.createSequentialGroup()
                  .addComponent(jLabel6)
                  .addGap(18, 18, 18)
                  .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)))
            .addGap(100, 100, 100))
      );
      jPanel5Layout.setVerticalGroup(
         jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jLabel6))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jToggleButton1)
            .addContainerGap(248, Short.MAX_VALUE))
      );

      jTabbedPane1.addTab("Key Store", null, jPanel5, "");

      add(jTabbedPane1, java.awt.BorderLayout.CENTER);
      jTabbedPane1.getAccessibleContext().setAccessibleName("Key Store");
   }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

       //JPopupMenu jp = new JPopupMenu("Certificate Info");
       String data = "No certificate selected";
       try {
          Certificate publickey = keyStore.getCertificate((String) jList1.getSelectedValue());
          data = "Issuer: " + ((X509Certificate) publickey).getIssuerDN().getName() + System.getProperty("line.separator");
          data += "Subject: " + ((X509Certificate) publickey).getSubjectDN().getName() + System.getProperty("line.separator");
          data += "Valid From: " + ((X509Certificate) publickey).getNotBefore().toString() + System.getProperty("line.separator");
          data += "Valid Until: " + ((X509Certificate) publickey).getNotAfter().toString() + System.getProperty("line.separator");
          data += "Serial Number: " + ((X509Certificate) publickey).getSerialNumber() + System.getProperty("line.separator");
       } catch (KeyStoreException ex) {
          Logger.getLogger(XmlSigApplet2.class.getName()).log(Level.SEVERE, null, ex);
       }
       jTextArea1.setText(data);
       jPanel4.setVisible(true);
       jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

       boolean error = false;
       String signedXml = "error!";
       JSObject window = JSObject.getWindow(this);
       try {
          if (keyStore == null || keyStore.size() == 0) {
             error = true;
             signedXml = "Unforunately, it looks as if you don't have any certificates to choose from.";
             jTextArea1.setText(signedXml);
             jTabbedPane1.setSelectedIndex(2);
             return;
          }
       } catch (Exception ex) {
          error = true;
          signedXml = "Unforunately, it looks as if you don't have any certificates to choose from.";
          jTextArea1.setText(signedXml);
          jTabbedPane1.setSelectedIndex(2);
       }
       if (jList1.getSelectedValue() == null) {
          error = true;
          signedXml = "You must pick a certificate first";
          jTextArea1.setText(signedXml);
          jTabbedPane1.setSelectedIndex(2);
       }

        //Object object2 = window.call("getBrowserName", null);
       //Object object1 = window.call("getOsName", null);
       Object object3 = window.call("getObjectType", null);
        //String browserName = (String) object2;
       //tring osName = (String) object2;
       String objecttype = (String) object3;

       //get the xml
       String xml = (String) window.call("getXml", new Object[]{});
       Object j = null;

       if (objecttype.equalsIgnoreCase("business")) {
          try {
             StringReader sr = new StringReader(xml.trim());
             j = (BusinessEntity) JAXB.unmarshal(sr, BusinessEntity.class);
          } catch (Exception ex) {
          }
       }
       if (objecttype.equalsIgnoreCase("service")) {
          try {
             StringReader sr = new StringReader(xml.trim());
             j = (BusinessService) JAXB.unmarshal(sr, BusinessService.class);
          } catch (Exception ex) {
          }
       }
       if (objecttype.equalsIgnoreCase("bindingTemplate")) {
          try {
             StringReader sr = new StringReader(xml.trim());
             j = (BindingTemplate) JAXB.unmarshal(sr, BindingTemplate.class);
          } catch (Exception ex) {
          }
       }
       if (objecttype.equalsIgnoreCase("tmodel")) {
          try {
             StringReader sr = new StringReader(xml.trim());
             j = (TModel) JAXB.unmarshal(sr, TModel.class);
          } catch (Exception ex) {
          }
       }

       if (j != null) {
          try {
             //sign it
             org.apache.juddi.v3.client.cryptor.DigSigUtil ds = new DigSigUtil();
             if (isIncludePublicKey.isSelected()) {
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "true");
             }
             if (isIncludeSubjectName.isSelected()) {
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "true");
             }
             if (isIncludeIssuer.isSelected()) {
                ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "true");
             }
             ds.put(DigSigUtil.SIGNATURE_METHOD, jTextFieldSigMethod.getText());
             ds.put(DigSigUtil.SIGNATURE_OPTION_DIGEST_METHOD, jTextFieldDigestMethod.getText());
             ds.put(DigSigUtil.CANONICALIZATIONMETHOD, jTextFieldc14n.getText());

             char[] cp = jPasswordField1.getPassword();
             if (cp != null && cp.length <= 0) {
                cp = null;
             }
             if (cp != null) {
                String s = new String(cp);
                s = s.trim();
                if ("".equalsIgnoreCase(s)) {
                   cp = null;
                }
             }
             PrivateKey key = (PrivateKey) keyStore.getKey((String) jList1.getSelectedValue(), cp);
             Certificate publickey = keyStore.getCertificate((String) jList1.getSelectedValue());

             j = ds.signUddiEntity(j, publickey, key);
             ds.clear();
             StringWriter sw = new StringWriter();
             JAXB.marshal(j, sw);
             signedXml = sw.toString();
          } catch (Exception ex) {
             error = true;
             Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
             signedXml = "Sorry I couldn't sign the data. " + ex.getMessage();
          }
       } else {
          error=true;
          signedXml = "Unable to determine which type of object that we're signing";
       }


       /*
        try {
        signedXml = this.sign(xml);
        } catch (Exception ex) {
        signedXml = ex.getMessage();
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }*/
       if (error) {
          jTextArea1.setText(signedXml);
          jTabbedPane1.setSelectedIndex(2);
       } else {
          //write it back to the web page
          window.call("writeXml", new Object[]{signedXml});

       }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextFieldDigestMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDigestMethodActionPerformed
       // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDigestMethodActionPerformed

    private void jTextFieldc14nActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldc14nActionPerformed
       // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldc14nActionPerformed

   private void jToggleButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseClicked
      // TODO add your handling code here:
   }//GEN-LAST:event_jToggleButton1MouseClicked

   private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
      setupCertificates();


   }//GEN-LAST:event_jToggleButton1ActionPerformed
   /**
    * XML digital signature namespace
    */
   public final static String XML_DIGSIG_NS = "http://www.w3.org/2000/09/xmldsig#";


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.ButtonGroup buttonGroup1;
   private javax.swing.JCheckBox isIncludeIssuer;
   private javax.swing.JCheckBox isIncludePublicKey;
   private javax.swing.JCheckBox isIncludeSubjectName;
   private javax.swing.JButton jButton1;
   private javax.swing.JButton jButton2;
   private javax.swing.JButton jButton3;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel5;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JList jList1;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JPanel jPanel5;
   private javax.swing.JPasswordField jPasswordField1;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JTabbedPane jTabbedPane1;
   private javax.swing.JTextArea jTextArea1;
   private javax.swing.JTextField jTextFieldDigestMethod;
   private javax.swing.JTextField jTextFieldSigMethod;
   private javax.swing.JTextField jTextFieldc14n;
   private javax.swing.JToggleButton jToggleButton1;
   // End of variables declaration//GEN-END:variables
}
