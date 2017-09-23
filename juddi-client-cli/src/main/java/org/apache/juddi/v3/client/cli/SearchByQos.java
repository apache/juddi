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
package org.apache.juddi.v3.client.cli;

import java.util.Properties;
import javax.xml.bind.JAXB;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.ext.wsdm.WSDMQosConstants;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelBag;
import org.uddi.v3_service.UDDIInquiryPortType;

/**
 *
 * @author Alex O'Ree
 */
public class SearchByQos {

        static PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
        static Properties properties = new Properties();
        static String wsdlURL = null;
        private static UDDIInquiryPortType inquiry;

        public static void doFindService(String token, Transport transport) throws Exception {

                // Now you create a reference to the UDDI API
            
                inquiry = transport.getUDDIInquiryService();
                
                // Making API call that retrieves the authentication token for the 'root' user.
                //String rootAuthToken = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                String uddi = token;// security.getAuthToken(getAuthTokenRoot).getAuthInfo();

                ServiceList after = getServiceList(uddi);
                if (after.getServiceInfos()==null || after.getServiceInfos().getServiceInfo() == null) {
                        System.out.println("no services returned!");
                        return;
                }
                //PrintUDDI<ServiceList> p = new PrintUDDI<ServiceList>();
                System.out.println(after.getServiceInfos().getServiceInfo().size() + " services returned!");
                JAXB.marshal(after,System.out);
                

        }

        public static void doFindBinding(String token, Transport transport) throws Exception {

                // Now you create a reference to the UDDI API
                inquiry = transport.getUDDIInquiryService();

                // Making API call that retrieves the authentication token for the 'root' user.
                //String rootAuthToken = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                String uddi = token;//security.getAuthToken(getAuthTokenRoot).getAuthInfo();

                BindingDetail after = getBindingList(uddi);
                if (after.getBindingTemplate() == null) {
                        System.out.println("no bindings returned!");
                        return;
                } 
                PrintUDDI<BindingDetail> p = new PrintUDDI<BindingDetail>();

                System.out.println(p.print(after));

        }

        public static void doFindBusiness(String token, Transport transport) throws Exception {
                // create a manager and read the config in the archive; 
                // you can use your config file name

                // Now you create a reference to the UDDI API
                inquiry = transport.getUDDIInquiryService();
                String uddi = token;//security.getAuthToken(getAuthTokenRoot).getAuthInfo();

                BusinessList after2 = getBusinessList(uddi);

                PrintUDDI<BusinessList> p2 = new PrintUDDI<BusinessList>();

                System.out.println(p2.print(after2));

        }


        private static BusinessList getBusinessList(String token) throws Exception {
                FindBusiness fb = new FindBusiness();
                fb.setAuthInfo(token);
                org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
                fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fq.getFindQualifier().add(UDDIConstants.OR_ALL_KEYS);
                fb.setFindQualifiers(fq);
                fb.getName().add((new Name(UDDIConstants.WILDCARD, null)));

                fb.setTModelBag(new TModelBag());
                fb.getTModelBag().getTModelKey().addAll(WSDMQosConstants.getAllQOSKeys());

                return inquiry.findBusiness(fb);
        }

        private static ServiceList getServiceList(String token) throws Exception {
                FindService fb = new FindService();
                fb.setAuthInfo(token);
                org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
                fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fq.getFindQualifier().add(UDDIConstants.OR_ALL_KEYS);
                fb.setFindQualifiers(fq);
                fb.getName().add((new Name(UDDIConstants.WILDCARD, null)));

                fb.setTModelBag(new TModelBag());
                fb.getTModelBag().getTModelKey().addAll(WSDMQosConstants.getAllQOSKeys());

                return inquiry.findService(fb);
        }

        private static BindingDetail getBindingList(String token) throws Exception {
                FindBinding fb = new FindBinding();
                fb.setAuthInfo(token);
                fb.setTModelBag(new TModelBag());
                fb.getTModelBag().getTModelKey().addAll(WSDMQosConstants.getAllQOSKeys());
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.OR_ALL_KEYS_TMODEL);
                return inquiry.findBinding(fb);
        }

        
}
