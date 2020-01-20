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

import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class was created to demonstrate that UDDI entities's keys do not need
 * to be from the same key domain. I.e. Business key = "uddi:1234:business"
 * which owns Service key "uddi:4567:service1"
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class BusinessServiceKeymismatch {

        private static UDDISecurityPortType security = null;
        private static JUDDIApiPortType juddiApi = null;
        private static UDDIPublicationPortType publish = null;

        public static void main(String[] args) throws Exception {

                // create a manager and read the config in the archive; 
                // you can use your config file name
                UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                Transport transport = clerkManager.getTransport("default");
                // Now you create a reference to the UDDI API
                security = transport.getUDDISecurityService();
                publish = transport.getUDDIPublishService();

                //step one, get a token
                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                getAuthTokenRoot.setUserID("uddi");

                getAuthTokenRoot.setCred("uddi");
                AuthToken authToken = security.getAuthToken(getAuthTokenRoot);
                TModel createKeyGenator = UDDIClerk.createKeyGenator("uddi:testdomain:keygenerator", "a name", "en");

                TModel createKeyGenator2 = UDDIClerk.createKeyGenator("uddi:testdomain2:keygenerator", "a name", "en");

                SaveTModel st = new SaveTModel();
                st.setAuthInfo(authToken.getAuthInfo());
                st.getTModel().add(createKeyGenator);
                st.getTModel().add(createKeyGenator2);
                publish.saveTModel(st);

                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authToken.getAuthInfo());
                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey("uddi:testdomain:biz1");
                be.getName().add(new Name("test", "en"));
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.setServiceKey("uddi:testdomain2:svc");
                bs.setBusinessKey("uddi:testdomain:biz1");
                bs.getName().add(new Name("svc", "en"));
                bs.setBindingTemplates(new BindingTemplates());
                BindingTemplate bt = new BindingTemplate();
                bt.setAccessPoint(new AccessPoint("http://localhost", "wsdlDeployment"));
                bt = UDDIClient.addSOAPtModels(bt);
                bs.getBindingTemplates().getBindingTemplate().add(bt);
                be.getBusinessServices().getBusinessService().add(bs);
                sb.getBusinessEntity().add(be);
                publish.saveBusiness(sb);
        }
}
