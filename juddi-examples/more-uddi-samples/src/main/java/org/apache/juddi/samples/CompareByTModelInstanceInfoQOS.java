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
package org.apache.juddi.samples;

import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.ext.wsdm.WSDMQosConstants;
import org.apache.juddi.v3.client.compare.TModelInstanceDetailsComparator;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;

/**
 * doesn't make changes to a remote server, just creates a few businesses and
 * compares the two by qos parameters
 *
 * @author Alex O'Ree
 */
public class CompareByTModelInstanceInfoQOS {

        public static void main(String[] args) throws Exception {
                BusinessEntity mary = CreateMary();
                BindingTemplate bt1 = mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0);
                BindingTemplate bt2 = mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(1);

                TModelInstanceDetailsComparator tidc = new TModelInstanceDetailsComparator(WSDMQosConstants.METRIC_FAULT_COUNT_KEY, true, false, false);
                int compare = tidc.compare(bt1.getTModelInstanceDetails(), bt2.getTModelInstanceDetails());
                if (compare > 0) {
                        System.out.println(mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getAccessPoint().getValue()
                                + " is greater than " + mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(1).getAccessPoint().getValue());
                }
                if (compare < 0) {
                        System.out.println(mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getAccessPoint().getValue()
                                + " is less than " + mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(1).getAccessPoint().getValue());
                }
                if (compare == 0) {
                        System.out.println(mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getAccessPoint().getValue()
                                + " is equal to " + mary.getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(1).getAccessPoint().getValue());
                }

        }

        /**
         * creates a business, service, bt with tmodel instance details with qos
         * parameters
         *
         * @param rootAuthToken
         * @throws Exception
         */
        private static BusinessEntity CreateMary() throws Exception {
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
                bt.setAccessPoint(new AccessPoint("http://localhost/endpoint1BAD", AccessPointType.WSDL_DEPLOYMENT.toString()));
                bt.setTModelInstanceDetails(new TModelInstanceDetails());
                TModelInstanceInfo tii = new TModelInstanceInfo();
                tii.setTModelKey(WSDMQosConstants.METRIC_FAULT_COUNT_KEY);

                tii.setInstanceDetails(new InstanceDetails());
                tii.getInstanceDetails().setInstanceParms("400");
                bt = UDDIClient.addSOAPtModels(bt);
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tii);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint("http://localhost/endpoint2BETTER", AccessPointType.WSDL_DEPLOYMENT.toString()));
                bt.setTModelInstanceDetails(new TModelInstanceDetails());
                tii = new TModelInstanceInfo();
                tii.setTModelKey(WSDMQosConstants.METRIC_FAULT_COUNT_KEY);

                tii.setInstanceDetails(new InstanceDetails());
                tii.getInstanceDetails().setInstanceParms("4");
                bt = UDDIClient.addSOAPtModels(bt);
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tii);
                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);

                return be;
        }
}
