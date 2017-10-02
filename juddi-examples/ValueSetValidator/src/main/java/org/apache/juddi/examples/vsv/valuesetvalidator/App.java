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
 */
package org.apache.juddi.examples.vsv.valuesetvalidator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api.impl.UDDIValueSetValidationImpl;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.TModel;

/**
 * Hello world!
 *
 */
public class App {

        public static void main(String[] args) throws ConfigurationException {
                System.out.println("Notice! before running this, build this project and copy the jar file into juddi-tomcat/target/tomcat/apache-tomcat.../juddiv3.war/WEB-INF/lib and restart!");

                System.out.println(UDDIValueSetValidationImpl.convertKeyToClass("uddi:www.bob.com:verified-tmodel"));
               
                
                
                
                
                //register joe publisher key generator
                //register tmodel that's validated
                UDDIClient client = new UDDIClient("META-INF/uddi.xml");
                UDDIClerk clerk = client.getClerk("default");
                TModel keygen = UDDIClerk.createKeyGenator("www.bob.com", "Bob's Key Generator", "This key generator is used for the jUDDI example Value Set Validator example");

                keygen = clerk.register(keygen).getTModel().get(0);

                TModel verifiedTmodel = new TModel();
                verifiedTmodel.setTModelKey(keygen.getTModelKey().replace("keygenerator", "verified-tmodel"));
                verifiedTmodel.setCategoryBag(new CategoryBag());

                verifiedTmodel.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.IS_VALIDATED_BY, UDDIConstants.IS_VALIDATED_BY_KEY_NAME, "uddi:juddi.apache.org:servicebindings-valueset-cp"));
                verifiedTmodel.setName(new Name("Bob's tModel with validation", "en"));
                verifiedTmodel = clerk.register(verifiedTmodel).getTModel().get(0);
                
                //now try to use it with a valid value
                
                //try to use it when an invalid value

        }
}
