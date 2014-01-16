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
package org.apache.juddi.example.publish;

import org.uddi.api_v3.*;
import org.apache.juddi.api_v3.*;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDIPublicationPortType;

/**
 * This shows you to interact with a UDDI server by publishing a Business,
 * Service and Binding Template. It uses some fairly generic code that should be
 * mostly portable to any other UDDI client library and is therefore consider
 * "portable". URLs are set in uddi.xml
 *
 */
public class SimplePublishPortable {

        private static UDDISecurityPortType security = null;
        private static UDDIPublicationPortType publish = null;

        public SimplePublishPortable() {
                try {
                        // create a client and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient uddiClient = new UDDIClient("META-INF/uddi.xml");
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

        /**
         * This function shows you how to publish to UDDI using a fairly generic
         * mechanism that should be portable (meaning use any UDDI v3 library
         * with this code)
         */
        public void publish() {
                try {

                        // Login aka retrieve its authentication token
                        GetAuthToken getAuthTokenMyPub = new GetAuthToken();
                        getAuthTokenMyPub.setUserID("bob");                    //your username
                        getAuthTokenMyPub.setCred("bob");                          //your password
                        AuthToken myPubAuthToken = security.getAuthToken(getAuthTokenMyPub);
                        System.out.println(getAuthTokenMyPub.getUserID() + "'s AUTHTOKEN = " + "******* never log auth tokens!");

                        // Creating the parent business entity that will contain our service.
                        BusinessEntity myBusEntity = new BusinessEntity();
                        Name myBusName = new Name();
                        myBusName.setValue("My Business");
                        myBusEntity.getName().add(myBusName);

                        // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                        SaveBusiness sb = new SaveBusiness();
                        sb.getBusinessEntity().add(myBusEntity);
                        sb.setAuthInfo(myPubAuthToken.getAuthInfo());
                        BusinessDetail bd = publish.saveBusiness(sb);
                        String myBusKey = bd.getBusinessEntity().get(0).getBusinessKey();
                        System.out.println("myBusiness key:  " + myBusKey);

                        // Creating a service to save.  Only adding the minimum data: the parent business key retrieved from saving the business 
                        // above and a single name.
                        BusinessService myService = new BusinessService();
                        myService.setBusinessKey(myBusKey);
                        Name myServName = new Name();
                        myServName.setValue("My Service");
                        myService.getName().add(myServName);

                        // Add binding templates, etc...
                        BindingTemplate myBindingTemplate = new BindingTemplate();
                        AccessPoint accessPoint = new AccessPoint();
                        accessPoint.setUseType(AccessPointType.WSDL_DEPLOYMENT.toString());
                        accessPoint.setValue("http://example.org/services/myservice?wsdl");
                        myBindingTemplate.setAccessPoint(accessPoint);
                        BindingTemplates myBindingTemplates = new BindingTemplates();
                        //optional but recommended step, this annotations our binding with all the standard SOAP tModel instance infos
                        myBindingTemplate = UDDIClient.addSOAPtModels(myBindingTemplate);
                        myBindingTemplates.getBindingTemplate().add(myBindingTemplate);

                        myService.setBindingTemplates(myBindingTemplates);

                        // Adding the service to the "save" structure, using our publisher's authentication info and saving away.
                        SaveService ss = new SaveService();
                        ss.getBusinessService().add(myService);
                        ss.setAuthInfo(myPubAuthToken.getAuthInfo());
                        ServiceDetail sd = publish.saveService(ss);
                        String myServKey = sd.getBusinessService().get(0).getServiceKey();
                        System.out.println("myService key:  " + myServKey);

                        security.discardAuthToken(new DiscardAuthToken(myPubAuthToken.getAuthInfo()));
                        // Now you have published a business and service via 
                        // the jUDDI API!
                        System.out.println("Success!");

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                SimplePublishPortable sp = new SimplePublishPortable();
                sp.publish();
        }
}
