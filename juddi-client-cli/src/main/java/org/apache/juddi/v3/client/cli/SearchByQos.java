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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXB;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.ext.wsdm.WSDMQosConstants;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelBag;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class SearchByQos {

        static PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
        static Properties properties = new Properties();
        static String wsdlURL = null;
        private static UDDISecurityPortType security = null;
        private static JUDDIApiPortType juddiApi = null;
        private static UDDIPublicationPortType publish = null;
        private static UDDIInquiryPortType inquiry;

        public static void doFindService(String token, Transport transport) throws Exception {

                // Now you create a reference to the UDDI API
                security = transport.getUDDISecurityService();
                publish = transport.getUDDIPublishService();
                inquiry = transport.getUDDIInquiryService();
        //step one, get a token
                //GetAuthToken getAuthTokenRoot = new GetAuthToken();
                //getAuthTokenRoot.setUserID("uddi");
                //getAuthTokenRoot.setCred("uddi");

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
                security = transport.getUDDISecurityService();
                publish = transport.getUDDIPublishService();
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
                security = transport.getUDDISecurityService();
                publish = transport.getUDDIPublishService();
                inquiry = transport.getUDDIInquiryService();
                String uddi = token;//security.getAuthToken(getAuthTokenRoot).getAuthInfo();

                BusinessList after2 = getBusinessList(uddi);

                PrintUDDI<BusinessList> p2 = new PrintUDDI<BusinessList>();

                System.out.println(p2.print(after2));

        }

        private static void DeleteIfExists(String key, String authInfo) {
                GetBusinessDetail gbd = new GetBusinessDetail();
                gbd.setAuthInfo(authInfo);
                gbd.getBusinessKey().add(key);
                boolean found = false;
                try {
                        BusinessDetail businessDetail = inquiry.getBusinessDetail(gbd);
                        if (businessDetail != null
                                && !businessDetail.getBusinessEntity().isEmpty()
                                && businessDetail.getBusinessEntity().get(0).getBusinessKey().equals(key)) {
                                found = true;
                        }
                } catch (Exception ex) {
                }
                if (found) {
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfo);
                        db.getBusinessKey().add(key);
                        try {
                                publish.deleteBusiness(db);
                        } catch (Exception ex) {
                                Logger.getLogger(FindBusinessBugHunt.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
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

        /**
         * -
         * adds a business, service, bt with tmodel instance details with qos
         * parameters
         *
         * @param rootAuthToken
         * @throws Exception
         */
        private static void SaveMary(String rootAuthToken) throws Exception {
                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey("uddi:uddi.marypublisher.com:marybusinessone");
                be.setDiscoveryURLs(new DiscoveryURLs());
                be.getDiscoveryURLs().getDiscoveryURL().add(new DiscoveryURL("home", "http://www.marybusinessone.com"));
                be.getDiscoveryURLs().getDiscoveryURL().add(new DiscoveryURL("serviceList", "http://www.marybusinessone.com/services"));
                be.getName().add(new Name("Mary Doe Enterprises", "en"));
                be.getName().add(new Name("Maria Negocio Uno", "es"));
                be.getDescription().add(new Description("This is the description for Mary Business One.", "en"));
                be.setContacts(new Contacts());
                Contact c = new Contact();
                c.setUseType("administrator");
                c.getPersonName().add(new PersonName("Mary Doe", "en"));
                c.getPersonName().add(new PersonName("Juan Doe", "es"));
                c.getDescription().add(new Description("This is the administrator of the service offerings.", "en"));
                be.getContacts().getContact().add(c);
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.setBusinessKey("uddi:uddi.marypublisher.com:marybusinessone");
                bs.setServiceKey("uddi:uddi.marypublisher.com:marybusinessoneservice");
                bs.getName().add(new Name("name!", "en"));
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint("http://localhost", AccessPointType.WSDL_DEPLOYMENT.toString()));
                bt.setTModelInstanceDetails(new TModelInstanceDetails());
                TModelInstanceInfo tii = new TModelInstanceInfo();
                tii.setTModelKey(WSDMQosConstants.METRIC_FAULT_COUNT_KEY);
                tii.setInstanceDetails(new InstanceDetails());
                tii.getInstanceDetails().setInstanceParms("400");
                bt = UDDIClient.addSOAPtModels(bt);
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tii);
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.getBusinessServices().getBusinessService().add(bs);
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(rootAuthToken);
                sb.getBusinessEntity().add(be);
                publish.saveBusiness(sb);
        }

        private static void SaveTM(TModel createKeyGenator, String uddi) throws Exception {
                SaveTModel stm = new SaveTModel();
                stm.setAuthInfo(uddi);
                stm.getTModel().add(createKeyGenator);
                publish.saveTModel(stm);
        }
}
