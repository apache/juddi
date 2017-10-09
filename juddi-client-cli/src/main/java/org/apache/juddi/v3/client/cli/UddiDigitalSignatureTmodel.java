/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.cli;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class shows you how to digitally sign a tmodel and verify the signature
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiDigitalSignatureTmodel {

        private UDDISecurityPortType security = null;
        private UDDIInquiryPortType inquiry = null;
        private UDDIPublicationPortType publish = null;
        private UDDIClient clerkManager = null;

        /**
         * This sets up the ws proxies using uddi.xml in META-INF
         */
        public UddiDigitalSignatureTmodel() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        inquiry = transport.getUDDIInquiryService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /**
         * Main entry point
         *
         * @param args
         */
        public static void main(String args[]) {
                UddiDigitalSignatureTmodel sp = new UddiDigitalSignatureTmodel();
                sp.fire(null, null);
        }

        public void fire(String token, String key) {
                try {
                        DigSigUtil ds = null;

                        //option 1), set everything manually
                        ds = new DigSigUtil();
                        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, "keystore.jks");
                        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "JKS");
                        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "Test");
                        ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "Test");
                        ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "true");

                        ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "true");
                        ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "true");
                        ds.put(DigSigUtil.TRUSTSTORE_FILE, "truststore.jks");
                        ds.put(DigSigUtil.TRUSTSTORE_FILETYPE, "JKS");
                        ds.put(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, "Test");

                        //option 2), load it from the juddi config file
                        //ds = new DigSigUtil(clerkManager.getClientConfig().getDigitalSignatureConfiguration());
                        //login
                        if (token == null) //option, load from juddi config
                        {
                                token = getAuthKey(clerkManager.getClerk("default").getPublisher(),
                                        clerkManager.getClerk("default").getPassword());
                        }
                        if (key==null){
                                SaveTModel stm = new SaveTModel();
                                stm.setAuthInfo(token);
                                TModel tm = new TModel();
                                tm.setName(new Name("my cool signed tmodel", null));
                                stm.getTModel().add(tm);
                                TModelDetail saveTModel = publish.saveTModel(stm);
                                key = saveTModel.getTModel().get(0).getTModelKey();
                        }

                        TModel be = getTmodelDetails(key);
                        if (!be.getSignature().isEmpty())
                        {
                                System.out.println("WARN, the entity with the key " + key + " is already signed! aborting");
                                return;
                        }
                        
                        //DigSigUtil.JAXB_ToStdOut(be);
                        System.out.println("signing");
                        TModel signUDDI_JAXBObject = ds.signUddiEntity(be);
                        DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);
                        System.out.println("signed, saving");

                        SaveTModel sb = new SaveTModel();
                        sb.setAuthInfo(token);
                        sb.getTModel().add(signUDDI_JAXBObject);
                        publish.saveTModel(sb);
                        System.out.println("saved, fetching");

                        be = getTmodelDetails(key);
                        DigSigUtil.JAXB_ToStdOut(be);
                        System.out.println("verifing");
                        AtomicReference<String> msg = new AtomicReference<String>();
                        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(be, msg);
                        if (verifySigned_UDDI_JAXB_Object) {
                                System.out.println("signature validation passed (expected)");
                        } else {
                                System.out.println("signature validation failed (not expected)");
                        }
                        System.out.println(msg.get());

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private TModel getTmodelDetails(String key) throws Exception {
                //   BusinessInfo get
                GetTModelDetail r = new GetTModelDetail();
                r.getTModelKey().add(key);
                return inquiry.getTModelDetail(r).getTModel().get(0);
        }

        /**
         * Gets a UDDI style auth token, otherwise, appends credentials to the
         * ws proxies (not yet implemented)
         *
         * @param username
         * @param password
         * @param style
         * @return
         */
        private String getAuthKey(String username, String password) {
                try {

                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID(username);
                        getAuthTokenRoot.setCred(password);

                        // Making API call that retrieves the authentication token for the 'root' user.
                        AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                        System.out.println("root AUTHTOKEN = " + "don't log auth tokens!");
                        return rootAuthToken.getAuthInfo();
                } catch (Exception ex) {
                        System.out.println("Could not authenticate with the provided credentials " + ex.getMessage());
                }
                return null;
        }
}
