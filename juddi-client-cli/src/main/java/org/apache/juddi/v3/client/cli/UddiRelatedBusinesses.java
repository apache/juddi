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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This will create two businesses under different users, then setup a
 * relationship between the two
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiRelatedBusinesses {

        private UDDISecurityPortType security = null;
        private UDDIPublicationPortType publish = null;

        public UddiRelatedBusinesses() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        publish = transport.getUDDIPublishService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void fire(String businessKey, String authInfo, String businessKey1, String authInfo1, String relationship) throws Exception {
                try {

                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        //ROOT creates half of the relationship
                        //create a business relationship (publisher assertion)
                        Holder<List<PublisherAssertion>> x = new Holder<List<PublisherAssertion>>();
                        PublisherAssertion pa = new PublisherAssertion();
                        pa.setFromKey(businessKey);
                        pa.setToKey(businessKey1);
                        pa.setKeyedReference(new KeyedReference());
                        pa.getKeyedReference().setKeyName("Subsidiary");
                        pa.getKeyedReference().setKeyValue(relationship);

                        pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                        x.value = new ArrayList<PublisherAssertion>();
                        x.value.add(pa);
                        publish.setPublisherAssertions(authInfo, x);

                        //now "UDDI" the user, creates the other half. It has to be mirrored exactly
                        x = new Holder<List<PublisherAssertion>>();
                        pa = new PublisherAssertion();
                        pa.setFromKey(businessKey);
                        pa.setToKey(businessKey1);
                        pa.setKeyedReference(new KeyedReference());
                        pa.getKeyedReference().setKeyName("Subsidiary");
                        pa.getKeyedReference().setKeyValue(relationship);

                        pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                        x.value = new ArrayList<PublisherAssertion>();
                        x.value.add(pa);
                        publish.setPublisherAssertions(authInfo1, x);

                        System.out.println("Success!");
                        /*
                         * Here's some notes:
                         * You can use
                         * List<AssertionStatusItem> assertionStatusReport = publish.getAssertionStatusReport(UDDIAuthToken.getAuthInfo(), CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);
                         * to determine if there's any assertions/relationships requests that are pending
                         * this should have one item it in, the relationship that's incomplete
                         * 
                         * There's also publish.deletePublisherAssertions();
                         */
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) throws Exception {
                UddiRelatedBusinesses sp = new UddiRelatedBusinesses();

                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                getAuthTokenRoot.setUserID("root");
                getAuthTokenRoot.setCred("root");

                // Making API call that retrieves the authentication token for the 'root' user.
                AuthToken rootAuthToken = sp.security.getAuthToken(getAuthTokenRoot);
                System.out.println("root AUTHTOKEN = " + "don't log auth tokens!");
                BusinessEntity rootbiz = sp.createBusiness("root");

                getAuthTokenRoot = new GetAuthToken();
                getAuthTokenRoot.setUserID("uddi");
                getAuthTokenRoot.setCred("uddi");

                // Making API call that retrieves the authentication token for the 'root' user.
                AuthToken uddiAuthToken = sp.security.getAuthToken(getAuthTokenRoot);
                System.out.println("uddi AUTHTOKEN = " + "don't log auth tokens!");
                BusinessEntity uddibiz = sp.createBusiness("uddi");

                //save user uddi's business
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(uddiAuthToken.getAuthInfo());
                sb.getBusinessEntity().add(uddibiz);
                BusinessDetail uddibize = sp.publish.saveBusiness(sb);

                sb = new SaveBusiness();
                sb.setAuthInfo(rootAuthToken.getAuthInfo());
                sb.getBusinessEntity().add(rootbiz);
                BusinessDetail rootbize = sp.publish.saveBusiness(sb);

                sp.fire(rootbize.getBusinessEntity().get(0).getBusinessKey(), rootAuthToken.getAuthInfo(),
                        uddibize.getBusinessEntity().get(0).getBusinessKey(), uddiAuthToken.getAuthInfo(),
                        "parent-child");
        }

        private BusinessEntity createBusiness(String user) {
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name(user + "'s business", null));
                return be;
        }

}
