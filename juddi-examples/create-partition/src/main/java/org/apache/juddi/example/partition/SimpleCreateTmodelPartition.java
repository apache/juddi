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
package org.apache.juddi.example.browse;

import java.util.Properties;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * An example for creating a key partition, aka key generator, aka 'special'
 * tModel
 *
 * @author Alex O'Ree
 */
public class SimpleCreateTmodelPartition {

    private static UDDISecurityPortType security = null;
    private static UDDIInquiryPortType inquiry = null;
    private static UDDIPublicationPortType publish = null;

    /**
     * This sets up the ws proxies using uddi.xml in META-INF
     */
    public SimpleCreateTmodelPartition() {
        try {
            String clazz = UDDIClientContainer.getUDDIClerkManager(null).
                    getClientConfig().getUDDINode("default").getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
            if (transportClass != null) {
                Transport transport = (Transport) transportClass.
                        getConstructor(String.class).newInstance("default");

                security = transport.getUDDISecurityService();
                inquiry = transport.getUDDIInquiryService();
                publish = transport.getUDDIPublishService();
            }
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
    public static void main(String args[]) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            DisplayHelp();
            return;
        }
        SimpleCreateTmodelPartition sp = new SimpleCreateTmodelPartition();
        sp.Go(args);
    }

    public void Go(String[] args) {
        try {
            Properties prop = ParseArgs(args);
            if (prop.containsKey("AuthStyle")) {
                //TODO, determine a way to pass parameters from the command line, hardcoded for now
                //UDDI Token
                //HTTP Username/Password (basic or digest)
                //HTTP Client Cert
            }

			
			//Note: when creating a tModel Key Generator, aka Partition, you MUST follow the below pattern
			//for jUDDI, the following is required
			//	Name
			//	CategoryBag/KR for the below fixed values
			//	a tModelKey that starts with uddi:<something>:keygenerator - recommended all lower case

            String key = GetAuthKey("uddi", "uddi", AuthStyle.UDDI_AUTH);
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

    /**
     * This function is useful for translating UDDI's somewhat complex data
     * format to something that is more useful.
     *
     * @param bindingTemplates
     */
    private void PrintBindingTemplates(BindingTemplates bindingTemplates) {
        if (bindingTemplates == null) {
            return;
        }
        for (int i = 0; i < bindingTemplates.getBindingTemplate().size(); i++) {
            System.out.println("Binding Key: " + bindingTemplates.getBindingTemplate().get(i).getBindingKey());
            //TODO The UDDI spec is kind of strange at this point.
            //An access point could be a URL, a reference to another UDDI binding key, a hosting redirector (which is 
            //esscentially a pointer to another UDDI registry) or a WSDL Deployment
            //From an end client's perspective, all you really want is the endpoint.

            //So if you have a wsdlDeployment useType, fetch the wsdl and parse for the invocation URL
            //If its hosting director, you'll have to fetch that data from uddi recursively until the leaf node is found
            //Consult the UDDI specification for more information

            if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint() != null) {
                System.out.println("Access Point: " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getValue() + " type " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType());
            }

        }
    }

    private enum AuthStyle {

        HTTP_BASIC,
        HTTP_DIGEST,
        HTTP_NTLM,
        UDDI_AUTH,
        HTTP_CLIENT_CERT
    }

    /**
     * Gets a UDDI style auth token, otherwise, appends credentials to the ws
     * proxies (not yet implemented)
     *
     * @param username
     * @param password
     * @param style
     * @return
     */
    private String GetAuthKey(String username, String password, AuthStyle style) {
        try {

            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID(username);
            getAuthTokenRoot.setCred(password);

            // Making API call that retrieves the authentication token for the 'root' user.
            AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
            System.out.println("root AUTHTOKEN = " + rootAuthToken.getAuthInfo());
            return rootAuthToken.getAuthInfo();
        } catch (Exception ex) {
            System.out.println("Could not authenticate with the provided credentials " + ex.getMessage());
        }
        return null;
    }

    /**
     * Converts command line args into a simple property structure
     *
     * @param args
     * @return
     */
    private Properties ParseArgs(String[] args) {

        Properties p = new Properties();
        if (args == null) {
            return p;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && args[i].length() >= 3) {
                p.put(args[i].split("=")[0], args[i].split("=")[1]);
            }
        }
        return p;
    }
}
