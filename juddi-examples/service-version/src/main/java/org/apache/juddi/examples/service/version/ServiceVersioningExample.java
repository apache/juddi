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
 *
 */
package org.apache.juddi.examples.service.version;

import java.util.Iterator;
import java.util.Set;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Hello world!
 *
 * This gives you an example of one way to use service version with UDDI and is
 * a partial solution to https://issues.apache.org/jira/browse/JUDDI-509
 * http://www.ibm.com/developerworks/webservices/library/ws-version/
 *
 */
public class ServiceVersioningExample {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        new ServiceVersioningExample().go();
    }

    /**
     * Main entry point
     */
    private void go() throws Exception {

        Init();

        Setup();

        ServiceLookUpByName();

        ServiceLookUpByVersion();

        Destroy();
    }

    /**
     * This will setup new a business, service, and binding template that's
     * versioned per the article linked above
     */
    private void Setup() {

        BusinessEntity be = new BusinessEntity();
        keygen = clerk.register(UDDIClerk.createKeyGenator(domain_prefix + "keygenerator", "my domain", lang)).getTModel().get(0);
        be.setBusinessServices(new BusinessServices());
        be.setBusinessKey(domain_prefix + "zerocoolbiz");
        be.getName().add(new Name("ZeroCool Business", lang));
        BusinessService bs = new BusinessService();
        bs.getName().add(new Name("ZeroCool Service", lang));
        bs.setBindingTemplates(new BindingTemplates());
        bs.setBusinessKey(domain_prefix + "zerocoolbiz");
        bs.setServiceKey(domain_prefix + "zerocoolsvc");

        //version 1
        BindingTemplate bt1 = new BindingTemplate();
        String version = "1.0.0.0";
        bt1.setBindingKey(domain_prefix + "binding10");
        bt1.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));
        bt1.setTModelInstanceDetails(new TModelInstanceDetails());
        bt1.getTModelInstanceDetails().getTModelInstanceInfo().add(UDDIClerk.createServiceInterfaceVersion(version, lang));
        bt1 = UDDIClient.addSOAPtModels(bt1);
        bs.getBindingTemplates().getBindingTemplate().add(bt1);
        


        //version 2
        BindingTemplate bt2 = new BindingTemplate();
        bt2.setBindingKey(domain_prefix + "binding12");
        String version2 = "1.2.0.0";
        bt2.setAccessPoint(new AccessPoint("http://localhost", "wsdl"));
        bt2.setTModelInstanceDetails(new TModelInstanceDetails());
        bt2.getTModelInstanceDetails().getTModelInstanceInfo().add(UDDIClerk.createServiceInterfaceVersion(version2, lang));
        bt2 = UDDIClient.addSOAPtModels(bt2);
        bs.getBindingTemplates().getBindingTemplate().add(bt2);

        be.getBusinessServices().getBusinessService().add(bs);
        clerk.register(be);

    }
    private String domain_prefix = "uddi:mydomain.com:";
    ;
    private String lang = "en";

    /**
     * this will look up our service by name and we'll discover multiple
     * bindings with different versions
     */
    private void ServiceLookUpByName() throws Exception {

        //here we are assuming we don't know what key is used for the service, so we look it up
        FindService fs = new FindService();
        fs.setFindQualifiers(new FindQualifiers());
        fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.EXACT_MATCH);
        fs.getName().add(new Name("ZeroCool Service", lang));
        ServiceList findService = inquiry.findService(fs);

        //parse the results and get a list of services to get the details on
        GetServiceDetail gsd = new GetServiceDetail();
        for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
            gsd.getServiceKey().add(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey());
        }
        ServiceDetail serviceDetail = inquiry.getServiceDetail(gsd);

        //parse the service details, looking for our versioned service
        if (serviceDetail != null) {
            for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
                if (serviceDetail.getBusinessService().get(i).getBindingTemplates() != null) {
                    Set<BindingTemplate> bindingByVersion = UDDIClerk.getBindingByVersion("1.2.0.0", serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate());
                    Iterator<BindingTemplate> iterator = bindingByVersion.iterator();
                    while (iterator.hasNext()) {
                        BindingTemplate next = iterator.next();
                        System.out.println("SUCCESS! Found the right version on key " + next.getBindingKey());
                        //TODO so now that you've found the right version of the right service
                        //now you can go execute that the url
                    }

                }
            }
        }
    }

    /**
     * this will look up our service by name and version number
     */
    private void ServiceLookUpByVersion() throws Exception {
        //here we are assuming we don't know what key is used for the service, so we look it up
        //TODO
    }
    private TModel keygen = null;
    private UDDISecurityPortType security = null;
    private UDDIInquiryPortType inquiry = null;
    private UDDIPublicationPortType publish = null;
    private UDDIClient client = null;
    private UDDIClerk clerk = null;

    private void Init() {
        try {
            // create a client and read the config in the archive; 
            // you can use your config file name
            client = new UDDIClient("META-INF/uddi.xml");
            clerk = client.getClerk("default");
            // a UddiClient can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = client.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            inquiry = transport.getUDDIInquiryService();
            publish = transport.getUDDIPublishService();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void Destroy() {
        clerk.unRegisterBusiness(domain_prefix + "zerocoolbiz");
        clerk.unRegisterTModel(keygen.getTModelKey());
    }
}
