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

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class was used to identify performance issues when a given node has a
 * large number of UDDI entities. It may not work on some commercial UDDI nodes
 * due to licensing restrictions (some limit the number of business, services,
 * etc)
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiCreatebulk {

        private static UDDISecurityPortType security = null;
        private static UDDIPublicationPortType publish = null;

        public UddiCreatebulk() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        // register the clerkManager with the client side container
                        UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
                        // supply the nodeName (defined in your uddi.xml.
                        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                        Transport transport = clerkManager.getTransport("default");
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void publish() {
                try {
                        // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
                        // and can save other publishers).
                        GetAuthToken getAuthTokenRoot = new GetAuthToken();
                        getAuthTokenRoot.setUserID("root");
                        getAuthTokenRoot.setCred("root");

                        // Making API call that retrieves the authentication token for the 'root' user.
                        AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                        System.out.println("root AUTHTOKEN = " + "***** don't log auth tokens");
                        int servicesPerBusiness = 5;
                        int businesses = 15;

                        DatatypeFactory df = DatatypeFactory.newInstance();
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);
                        for (int i = 0; i < businesses; i++) {
                                // Creating the parent business entity that will contain our service.
                                BusinessEntity myBusEntity = new BusinessEntity();
                                Name myBusName = new Name();
                                myBusName.setLang("en");
                                myBusName.setValue("My Business " + i + " " + xcal.toString());
                                myBusEntity.getName().add(myBusName);

                                // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                                SaveBusiness sb = new SaveBusiness();
                                sb.getBusinessEntity().add(myBusEntity);
                                sb.setAuthInfo(rootAuthToken.getAuthInfo());
                                BusinessDetail bd = publish.saveBusiness(sb);
                                String myBusKey = bd.getBusinessEntity().get(0).getBusinessKey();
                                System.out.println("myBusiness key:  " + myBusKey);
                                for (int k = 0; k < servicesPerBusiness; k++) {
                                        // Creating a service to save.  Only adding the minimum data: the parent business key retrieved from saving the business 
                                        // above and a single name.
                                        BusinessService myService = new BusinessService();
                                        myService.setBusinessKey(myBusKey);
                                        Name myServName = new Name();
                                        myServName.setLang("en");
                                        myServName.setValue("My Service " + i + " " + k + " " + xcal.toString());
                                        myService.getName().add(myServName);
                                        Description d2 = new Description();
                                        d2.setValue("my service description");
                                        d2.setLang("en");
                                        myService.getDescription().add(d2);
                                        // Add binding templates, etc...
                                        BindingTemplate myBindingTemplate = new BindingTemplate();
                                        myBindingTemplate.setCategoryBag(new CategoryBag());
                                        KeyedReference kr = new KeyedReference();
                                        kr.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                        kr.setKeyName("keyname1");
                                        kr.setKeyValue("myvalue1");

                                        myBindingTemplate.getCategoryBag().getKeyedReference().add(kr);

                                        KeyedReferenceGroup krg = new KeyedReferenceGroup();
                                        krg.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                        kr = new KeyedReference();
                                        kr.setTModelKey(UDDIConstants.PROTOCOL_SSLv3);
                                        kr.setKeyName("keyname1grp");
                                        kr.setKeyValue("myvalue1grp");

                                        krg.getKeyedReference().add(kr);
                                        myBindingTemplate.getCategoryBag().getKeyedReferenceGroup().add(krg);


                                        myService.setCategoryBag(new CategoryBag());

                                        kr = new KeyedReference();
                                        kr.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                        kr.setKeyName("Servicekeyname2grp");
                                        kr.setKeyValue("Servicemyvalue2grp");
                                        myService.getCategoryBag().getKeyedReference().add(kr);

                                        krg = new KeyedReferenceGroup();
                                        krg.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                        kr = new KeyedReference();
                                        kr.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                        kr.setKeyName("keyname1grp");
                                        kr.setKeyValue("myvalue1grp");

                                        krg.getKeyedReference().add(kr);
                                        myService.getCategoryBag().getKeyedReferenceGroup().add(krg);

                                        AccessPoint accessPoint = new AccessPoint();
                                        accessPoint.setUseType(AccessPointType.WSDL_DEPLOYMENT.toString());
                                        accessPoint.setValue("http://example.org/services/myservice" + i + k + "?wsdl");
                                        myBindingTemplate.setAccessPoint(accessPoint);
                                        myBindingTemplate.setTModelInstanceDetails(new TModelInstanceDetails());
                                        TModelInstanceInfo tii = new TModelInstanceInfo();
                                        Description d = new Description();
                                        d.setValue("Tmodel instance description");
                                        tii.getDescription().add(d);
                                        tii.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                        tii.setInstanceDetails(new InstanceDetails());
                                        tii.getInstanceDetails().setInstanceParms("heres some useful stuff describing this endpoint, up to 4KB of data");
                                        tii.getInstanceDetails().getDescription().add(d);
                                        OverviewDoc od = new OverviewDoc();
                                        d = new Description();
                                        d.setValue("ovweview doc description");
                                        od.getDescription().add(d);
                                        od.setOverviewURL(new OverviewURL());
                                        od.getOverviewURL().setUseType("www");
                                        od.getOverviewURL().setValue("www.apache.org");
                                        tii.getInstanceDetails().getOverviewDoc().add(od);
                                        myBindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo().add(tii);


                                        BindingTemplates myBindingTemplates = new BindingTemplates();
                                        myBindingTemplates.getBindingTemplate().add(myBindingTemplate);
                                        myService.setBindingTemplates(myBindingTemplates);
                                        try {
                                                // Adding the service to the "save" structure, using our publisher's authentication info and saving away.
                                                SaveService ss = new SaveService();
                                                ss.getBusinessService().add(myService);
                                                ss.setAuthInfo(rootAuthToken.getAuthInfo());
                                                ServiceDetail sd = publish.saveService(ss);
                                                String myServKey = sd.getBusinessService().get(0).getServiceKey();
                                                System.out.println("myService key:  " + myServKey);
                                        } catch (Exception x) {
                                                x.printStackTrace();
                                        }
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                UddiCreatebulk sp = new UddiCreatebulk();
                sp.publish();
        }
}
