/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.example.partition;

import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.config.UDDIClerk;

import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * An example for creating a key partition, aka key generator, aka 'special'
 * tModel
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class SimpleCreateTmodelPartition {

        private static UDDISecurityPortType security = null;
        private static UDDIPublicationPortType publish = null;
        private static UDDIClient uddiClient = null;

        /**
         * This sets up the ws proxies using uddi.xml in META-INF
         */
        public SimpleCreateTmodelPartition() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        uddiClient = new UDDIClient("META-INF/partition-uddi.xml");
                        uddiClient.start();

                        // a UddiClient can be a client to multiple UDDI nodes, so 
                        // supply the nodeName (defined in your uddi.xml.
                        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                        Transport transport = uddiClient.getTransport("default");
                        // Now you create a reference to the UDDI API

                        security = transport.getUDDISecurityService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private static void DisplayHelp() {
                //TODO
        }

        /**
         * Main entry point
         *
         * @param args
         */
        public static void main(String args[]) throws ConfigurationException {
                if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                        DisplayHelp();
                        return;
                }
                SimpleCreateTmodelPartition sp = new SimpleCreateTmodelPartition();
                sp.TmodelsTheLongAndHardWay(args);
                sp.TmodelsTheEasyWay(args);
                
                uddiClient.stop();
        }

        public void TmodelsTheEasyWay(String[] args) {
                try {
                        
                        //This reads from the config file
                        UDDIClerk clerk = uddiClient.getClerk("defaultClerk");
                        //Since the password isn't set in the above config file, we have to provide it manually
                        //or thrown some fancy dialog box
                        clerk.setPublisher("uddi");     //username
                        clerk.setPassword("uddi");     //pass
                        

                        TModel keygen = UDDIClerk.createKeyGenator("www.mycoolcompany.com", "My Company's Keymodel generator", "en");
                        clerk.register(keygen);
                        System.out.println("Creation of Partition Success!");


                        //Now lets make a few tModels using the new domain
                        TModel tm = new TModel();
                        tm.setName(new Name());
                        tm.getName().setValue("My Company's Department");
                        tm.getName().setLang("en");
                        tm.setTModelKey("uddi:www.mycoolcompany.com:department");
                        clerk.register(tm);
                        System.out.println("Creation of tModel Department Success!");

                        tm = new TModel();
                        tm.setName(new Name());
                        tm.getName().setValue("My Company's Authentication Method");
                        tm.getName().setLang("en");
                        tm.setTModelKey("uddi:www.mycoolcompany.com:authmode");
                        clerk.register(tm);
                        System.out.println("Creation of tModel Auth Mode Success!");

                        clerk.discardAuthToken();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void TmodelsTheLongAndHardWay(String[] args) {
                try {
                        //TODO this is where you, the developer need to find out if your UDDI server
                        //supports UDDI's auth tokens or not. jUDDI does, so moving onwards.
                        AuthStyle style = AuthStyle.UDDI_AUTH;
                        //Login
                        String key = GetAuthKey("uddi", "password", style);

                        //Note: when creating a tModel Key Generator, aka Partition, you MUST follow the below pattern
                        //for jUDDI, the following is required
                        //	Name
                        //	CategoryBag/KR for the below fixed values
                        //	a tModelKey that starts with uddi:<something>:keygenerator - all lower case
                        //First, Here's the long way to do it to make a Key Generator tModel.
                        SaveTModel st = new SaveTModel();
                        st.setAuthInfo(key);
                        TModel tm = new TModel();
                        tm.setName(new Name());
                        tm.getName().setValue("My Company's Keymodel generator");
                        tm.getName().setLang("en");
                        tm.setCategoryBag(new CategoryBag());
                        KeyedReference kr = new KeyedReference();
                        kr.setTModelKey("uddi:uddi.org:categorization:types");
                        kr.setKeyName("uddi-org:keyGenerator");
                        kr.setKeyValue("keyGenerator");
                        tm.getCategoryBag().getKeyedReference().add(kr);
                        tm.setTModelKey("uddi:www.mycoolcompany.com:keygenerator");
                        st.getTModel().add(tm);
                        TModelDetail saveTModel = publish.saveTModel(st);
                        System.out.println("Creation of Partition Success!");

                        //Here's the easy and fun way!
                        TModel keygen = UDDIClerk.createKeyGenator("www.mycoolcompany.com", "My Company's Keymodel generator", "en");
                        //st = new SaveTModel();
                        //st.setAuthInfo(key);
                        //st.getTModel().add(keygen);
                        //saveTModel = publish.saveTModel(st);
                        

                        //Now lets make a few tModels using the new domain
                        tm = new TModel();
                        tm.setName(new Name());
                        tm.getName().setValue("My Company's Department");
                        tm.getName().setLang("en");
                        tm.setTModelKey("uddi:www.mycoolcompany.com:department");
                        st.getTModel().add(tm);
                        saveTModel = publish.saveTModel(st);
                        System.out.println("Creation of tModel Department Success!");

                        tm = new TModel();
                        tm.setName(new Name());
                        tm.getName().setValue("My Company's Authentication Method");
                        tm.getName().setLang("en");
                        tm.setTModelKey("uddi:www.mycoolcompany.com:authmode");
                        st.getTModel().add(tm);
                        saveTModel = publish.saveTModel(st);
                        System.out.println("Creation of tModel Auth Mode Success!");

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        
        private enum AuthStyle {

                HTTP,
                UDDI_AUTH

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
        private String GetAuthKey(String username, String password, AuthStyle style) {
                switch (style) {
                        case HTTP:
                                ((BindingProvider) publish).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
                                ((BindingProvider) publish).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
                                return null;
                        case UDDI_AUTH:
                                try {

                                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                                        getAuthTokenRoot.setUserID(username);
                                        getAuthTokenRoot.setCred(password);

                                        // Making API call that retrieves the authentication token for the 'root' user.
                                        AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                                        System.out.println(username + " AUTHTOKEN = (don't log auth tokens!)");
                                        return rootAuthToken.getAuthInfo();
                                } catch (Exception ex) {
                                        System.out.println("Could not authenticate with the provided credentials " + ex.getMessage());
                                }
                }

                return null;
        }
}
