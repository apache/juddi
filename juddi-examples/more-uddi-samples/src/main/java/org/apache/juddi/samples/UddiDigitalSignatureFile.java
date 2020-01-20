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
package org.apache.juddi.samples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.bind.JAXB;

import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;
import org.apache.juddi.v3.client.cryptor.XmlUtils;
import org.uddi.api_v3.*;

/**
 * This class shows you how to digital sign a business and save to file
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiDigitalSignatureFile {

        /**
         * This sets up the ws proxies using uddi.xml in META-INF
         */
        public UddiDigitalSignatureFile() {

        }

        public enum UddiType {

                Business, Service, Binding, TModel, PublisherAssertion
        }

        public void fire(String fileIn, String fileOut, UddiType type) {
                try {
                        System.out.println("WARN - All previous signatures will be removed!");
                        if (fileIn == null || fileOut == null || type == null) {
                                System.out.print("Input file: ");
                                fileIn = System.console().readLine();
                                System.out.print("Out file: ");
                                fileOut = System.console().readLine();
                                System.out.println();
                                for (int i = 0; i < UddiType.values().length; i++) {
                                        System.out.println("[" + i + "] " + UddiType.values()[i].toString());
                                }
                                System.out.print("UDDI Type: ");
                                String t = System.console().readLine();
                                type = UddiType.values()[Integer.parseInt(t)];
                        }

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

                        FileInputStream fis = new FileInputStream(fileIn);
                        Class expectedType = null;
                        switch (type) {
                                case Binding:
                                        expectedType = BindingTemplate.class;
                                        break;
                                case Business:
                                        expectedType = BusinessEntity.class;
                                        break;
                                case PublisherAssertion:
                                        expectedType = PublisherAssertion.class;
                                        break;
                                case Service:
                                        expectedType = BusinessService.class;
                                        break;
                                case TModel:
                                        expectedType = TModel.class;
                                        break;
                        }
                        Object be = XmlUtils.unmarshal(fis, expectedType);
                        fis.close();
                        fis = null;

                        switch (type) {
                                case Binding:
                                        ((BindingTemplate) be).getSignature().clear();
                                        break;
                                case Business:
                                        ((BusinessEntity) be).getSignature().clear();
                                        break;
                                case PublisherAssertion:
                                        ((PublisherAssertion) be).getSignature().clear();
                                        break;
                                case Service:
                                        ((BusinessService) be).getSignature().clear();
                                        break;
                                case TModel:
                                        ((TModel) be).getSignature().clear();
                                        break;
                        }

                        System.out.println("signing");
                        Object signUDDI_JAXBObject = ds.signUddiEntity(be);
                        System.out.println("signed");
                        DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);

                        System.out.println("verifing");
                        AtomicReference<String> msg = new AtomicReference<String>();
                        boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
                        if (verifySigned_UDDI_JAXB_Object) {
                                System.out.println("signature validation passed (expected)");
                                FileOutputStream fos = new FileOutputStream(fileOut);
                                JAXB.marshal(signUDDI_JAXBObject, fos);
                                fos.close();
                        } else {
                                System.out.println("signature validation failed (not expected)");
                        }
                        System.out.println(msg.get());

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

}
