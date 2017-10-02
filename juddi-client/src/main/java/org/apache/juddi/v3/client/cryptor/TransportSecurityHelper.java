/*
 * Copyright 2015 The Apache Software Foundation.
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

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.BindingProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author alexoree@apache.org
 */
public class TransportSecurityHelper {

        private static final Log log = LogFactory.getLog(TransportSecurityHelper.class);

        public static boolean applyTransportSecurity(BindingProvider webServicePort) {
                try {
                        File currentdir = new File(".");
                        String s = System.getProperty("javax.net.ssl.keyStore");
                        String st = System.getProperty("javax.net.ssl.trustStore");
                        log.info("Attempting to initialize keystore and truststore from " + s + " " + st);
                        if (s == null) {
                                log.warn("keystore isn't defined!");
                                return false;
                        } else if (st == null) {
                                log.warn("truststore isn't defined! " + s);
                                return false;
                        } else {
                                File keystore = new File(s);
                                if (keystore == null || !keystore.exists()) {
                                        log.warn("keystore doesn't exist! input was " + s + " working dir is " + currentdir
                                                .getAbsolutePath());
                                        return false;
                                }
                                //File truststore =new File(System.getProperty("javax.net.ssl.trustStore"));
                                String pwd = System.getProperty("javax.net.ssl.keyStorePassword");
                                if (pwd == null) {
                                        log.warn("keystore password isn't defined!");
                                        return false;
                                }

                                File truststore = new File(st);
                                if (truststore == null || !truststore.exists()) {
                                        log.warn("truststore doesn't exist! input was " + s + " working dir is " + currentdir
                                                .getAbsolutePath());
                                        return false;
                                }
                                //File truststore =new File(System.getProperty("javax.net.ssl.trustStore"));
                                String pwdt = System.getProperty("javax.net.ssl.trustStorePassword");
                                if (pwdt == null) {
                                        log.warn("truststore password isn't defined!");
                                        return false;
                                }

                                if (keystore.exists()) {
                                        FileInputStream fis = null;
                                        try {
                                                log.info("Using keystore from " + keystore.getAbsolutePath() + " current dir is " + currentdir.getAbsolutePath());

                                                log.info("Using truststore from " + truststore.getAbsolutePath() + " current dir is " + currentdir.getAbsolutePath());
                                                //log.info("Using truststure from " + truststore.getAbsolutePath() + " current dir is " + currentdir.getAbsolutePath());
                                                SSLContext sc = SSLContext.getInstance("SSLv3");

                                                KeyManagerFactory kmf
                                                        = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                                                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                                                try {
                                                        fis = new FileInputStream(keystore);
                                                        ks.load(fis, pwd.toCharArray());
                                                } catch (Exception ex) {
                                                        log.warn("unable to load key store " + keystore.getAbsolutePath(), ex);
                                                } finally {
                                                        if (fis != null) {
                                                                fis.close();
                                                        }
                                                }

                                                kmf.init(ks, pwd.toCharArray());

                                                String alg = TrustManagerFactory.getDefaultAlgorithm();
                                                TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);

                                                fis = new FileInputStream(st);
                                                KeyStore kst = KeyStore.getInstance("jks");
                                                try {
                                                        kst.load(fis, pwdt.toCharArray());
                                                } catch (Exception ex) {
                                                        log.warn("unable to load key store " + st, ex);
                                                } finally {
                                                        if (fis != null) {
                                                                fis.close();
                                                        }
                                                }

                                                tmFact.init(kst);

                                                //TrustManager[] tms = tmFact.getTrustManagers();
                                                sc.init(kmf.getKeyManagers(), null, null);
                                                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                                                ((BindingProvider) webServicePort).getRequestContext().put("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory", sc.getSocketFactory());
                                                ((BindingProvider) webServicePort).getRequestContext().put("com.sun.xml.ws.transport.https.client.SSLSocketFactory", sc.getSocketFactory());
                                                return true;
                                        } catch (Exception ex) {
                                                log.warn("unable to establish ssl settings", ex);
                                        }
                                }
                        }
                        return false;
                } catch (Exception x) {
                        log.error("unexpected error", x);
                }
                return false;
        }

}
