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

import de.svenjacobs.loremipsum.LoremIpsum;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
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
        String curretNode = null;
        public UddiCreatebulk(String node) {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport(node);
                        curretNode=node;
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
        
        public UddiCreatebulk(Transport transport, boolean notused, String node) {
                try {
                       curretNode=node;
                        security = transport.getUDDISecurityService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /**
         * bulk creates businesses, services and binding templates
         * @param token if null, root/root will be used to authenticate
         * @param businesses
         * @param servicesPerBusiness
         * @param user purely for display purposes
         */
        public void publishBusiness(String token, int businesses, int servicesPerBusiness, String user) {
                try {
                        // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
                        // and can save other publishers).
                        if (token == null) {
                                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                                getAuthTokenRoot.setUserID("root");
                                getAuthTokenRoot.setCred("root");

                                // Making API call that retrieves the authentication token for the 'root' user.
                                AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                                System.out.println("root AUTHTOKEN = " + "don't log auth tokens!");
                                token = rootAuthToken.getAuthInfo();
                        }

                        LoremIpsum textgen = new LoremIpsum();
                        DatatypeFactory df = DatatypeFactory.newInstance();
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);
                        for (int i = 0; i < businesses; i++) {
                                // Creating the parent business entity that will contain our service.
                                BusinessEntity myBusEntity = new BusinessEntity();
                                Name myBusName = new Name();
                                myBusName.setLang("en");
                                myBusName.setValue(user + "'s Business " +curretNode +" " + i + " " + xcal.toString() + " " + textgen.getWords(5, 2) );
                                myBusEntity.getDescription().add(new Description( textgen.getWords(10, 2), null));
                                myBusEntity.getName().add(myBusName);

                                // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                                SaveBusiness sb = new SaveBusiness();
                                sb.getBusinessEntity().add(myBusEntity);
                                sb.setAuthInfo(token);
                                BusinessDetail bd = publish.saveBusiness(sb);
                                String myBusKey = bd.getBusinessEntity().get(0).getBusinessKey();
                                System.out.println("saved: Business key:  " + myBusKey);
                                for (int k = 0; k < servicesPerBusiness; k++) {
                                        // Creating a service to save.  Only adding the minimum data: the parent business key retrieved from saving the business 
                                        // above and a single name.
                                        BusinessService myService = new BusinessService();
                                        myService.setBusinessKey(myBusKey);
                                        Name myServName = new Name();
                                        myServName.setLang("en");
                                        myServName.setValue(user + "'s Service " +curretNode+" "+ i + " " + k + " " + xcal.toString()+ " " + textgen.getWords(5, 2) );
                                        myService.getName().add(myServName);
                                        myService.getDescription().add(new Description( textgen.getWords(10, 2), null));
                                        
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
                                        tii.getInstanceDetails().setInstanceParms("heres some useful stuff describing this endpoint, up to 4KB of data"+ " " + textgen.getWords(20, 2) );
                                        tii.getInstanceDetails().getDescription().add(d);
                                        OverviewDoc od = new OverviewDoc();
                                        d = new Description();
                                        d.setValue("ovweview doc description"+ " " + textgen.getWords(5, 2) );
                                        od.getDescription().add(d);
                                        od.setOverviewURL(new OverviewURL());
                                        od.getOverviewURL().setUseType("www");
                                        od.getOverviewURL().setValue("www.apache.org");
                                        tii.getInstanceDetails().getOverviewDoc().add(od);
                                        myBindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo().add(tii);

                                        BindingTemplates myBindingTemplates = new BindingTemplates();
                                        myBindingTemplate = UDDIClient.addSOAPtModels(myBindingTemplate);
                                        myBindingTemplates.getBindingTemplate().add(myBindingTemplate);
                                        myService.setBindingTemplates(myBindingTemplates);
                                        try {
                                                // Adding the service to the "save" structure, using our publisher's authentication info and saving away.
                                                SaveService ss = new SaveService();
                                                ss.getBusinessService().add(myService);
                                                ss.setAuthInfo(token);
                                                ServiceDetail sd = publish.saveService(ss);
                                                String myServKey = sd.getBusinessService().get(0).getServiceKey();
                                                System.out.println("saved: service key:  " + myServKey);
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
                UddiCreatebulk sp = new UddiCreatebulk(null);
                sp.publishBusiness(null, 15, 20, "root");
        }
}
