/*
 * Copyright 2014 The Apache Software Foundation.
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

package org.apache.juddi.v3.client.mapping;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXB;
import org.apache.juddi.api_v3.AccessPointType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.Address;
import org.uddi.api_v3.AddressLine;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.Email;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;

/**
 *
 * @author Alex O'Ree
 */
public class MapUDDI23Test {

        static final String domainprefix="uddi:businessdomain:";
        static BusinessEntity getBus(){
                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey(domainprefix+"businesskey");
                be.setBusinessServices(getServices());
                be.setCategoryBag(getCatbag());
                be.setContacts(getContacts());
                be.setDiscoveryURLs(getDisco());
                be.setIdentifierBag(getIdentbag());
                be.getDescription().addAll(getDescriptions());
                be.getName().addAll(getNames());
                return be;
        }

        private static BusinessServices getServices() {
                BusinessServices r = new BusinessServices();
                BusinessService x = new BusinessService();
                x.setBusinessKey(domainprefix+"businesskey");
                x.setServiceKey(domainprefix+"servicekey");
                x.setCategoryBag(getCatbag());
                x.getDescription().addAll(getDescriptions());
                x.getName().addAll(getNames());
                x.setBindingTemplates(getBindingTemplates());
                r.getBusinessService().add(x);
                return r;
        }

        private static CategoryBag getCatbag() {
                
                CategoryBag c = new CategoryBag();
                c.getKeyedReference().add(new KeyedReference(domainprefix+"key", "name", "value"));
                return c;
        }

        private static Contacts getContacts() {
                Contacts c = new Contacts();
                Contact admin = new Contact();
                admin.setUseType("it support");
                admin.getEmail().add(new Email("admin@localhost", "primary"));
                admin.getDescription().add(new Description("the guy in that keeps the lights green", "en"));
                admin.getPersonName().add(new PersonName("admin1", "en"));
                Address r=new Address();
                r.setLang("en");
                r.setSortCode("none");
                r.setTModelKey(domainprefix + "address");
                r.setUseType("mailing address");
                r.getAddressLine().add(new AddressLine("keyname","keyval","1313 mockingbird lane"));
                admin.getAddress().add(r);
                c.getContact().add(admin);
                return c;
                
        }

        private static DiscoveryURLs getDisco() {
                DiscoveryURLs r = new DiscoveryURLs();
                r.getDiscoveryURL().add(new DiscoveryURL("public website", "http://localhost"));
                return r;
                
        }

        private static IdentifierBag getIdentbag() {
                IdentifierBag ret = new IdentifierBag();
                ret.getKeyedReference().add(new KeyedReference(domainprefix + "key", "keyname", "keyval"));
                return ret;
        }

        private static Collection<? extends Description> getDescriptions() {
                
                List<Description> r = new ArrayList<Description>();
                r.add(new Description("description", "en"));
                return r;
        }

        private static Collection<? extends Name> getNames() {
                List<Name> r = new ArrayList<Name>();
                r.add(new Name("name1", "en"));
                return r;
        }

        private static BindingTemplates getBindingTemplates() {
                BindingTemplates bt = new BindingTemplates();
                BindingTemplate t = new BindingTemplate();
                t.setAccessPoint(new AccessPoint("http://localhost", AccessPointType.END_POINT.toString()));
                t.setBindingKey(domainprefix+"binding");
//                t.setCategoryBag(getCatbag());
                t.getDescription().addAll(getDescriptions());
                t.setHostingRedirector(null);
                t.setServiceKey(domainprefix+"servicekey");
                
                t.setTModelInstanceDetails(getTID());
                
                bt.getBindingTemplate().add(t);
                return bt;
        }

        private static TModelInstanceDetails getTID() {
                TModelInstanceDetails r = new TModelInstanceDetails();
                TModelInstanceInfo x = new TModelInstanceInfo();
                x.getDescription().addAll(getDescriptions());
                x.setTModelKey(domainprefix+"tid");
                x.setInstanceDetails(new InstanceDetails());
                x.getInstanceDetails().getDescription().addAll(getDescriptions());
                x.getInstanceDetails().setInstanceParms("asdasdasdasdasd");
                OverviewDoc o = new OverviewDoc();
  //              o.getDescription().addAll(getDescriptions());
                o.setOverviewURL(new OverviewURL("http://localhost", "overview"));
                x.getInstanceDetails().getOverviewDoc().add(o);
                r.getTModelInstanceInfo().add(x);
                return r;
        }
        
        @Test
        @Ignore //ignoring this for now, 100% mapping is not possible, this works as of 1-23-2014
        public void AssertCloseEnoughBusinessUDDI3to2to3(){
                BusinessEntity before = getBus();
                BusinessEntity after =MapUDDIv2Tov3.MapBusiness(MapUDDIv3Tov2.MapBusiness(before, "node"));
                StringWriter sw1= new StringWriter();
                JAXB.marshal(before, sw1);
                
                StringWriter sw2= new StringWriter();
                JAXB.marshal(after, sw2);
                System.out.println(sw1.toString());
                System.out.println(sw2.toString());
                Assert.assertEquals("these should be the same", sw1.toString(), sw2.toString());
                
        }
}
