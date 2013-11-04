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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
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

    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;

    public UddiRelatedBusinesses() {
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
            juddiApi = transport.getJUDDIApiService();
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


            GetAuthToken getAuthTokenUDDI = new GetAuthToken();
            getAuthTokenRoot.setUserID("uddi");
            getAuthTokenRoot.setCred("uddi");

            // Making API call that retrieves the authentication token for the 'root' user.
            AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
            System.out.println("root AUTHTOKEN = " + rootAuthToken.getAuthInfo());

            AuthToken UDDIAuthToken = security.getAuthToken(getAuthTokenUDDI);
            System.out.println("UDDI AUTHTOKEN = " + rootAuthToken.getAuthInfo());



            DatatypeFactory df = DatatypeFactory.newInstance();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);
            for (int i = 0; i < 1; i++) {   //this loop is used for testing at scale
                // Creating the parent business entity that will contain our service.
                BusinessEntity myBusEntity = new BusinessEntity();
                Name myBusName = new Name();
                myBusName.setLang("en");
                myBusName.setValue("My Business Dept 1" + " " + xcal.toString());
                myBusEntity.getName().add(myBusName);

                // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                SaveBusiness sb = new SaveBusiness();
                sb.getBusinessEntity().add(myBusEntity);
                sb.setAuthInfo(rootAuthToken.getAuthInfo());
                BusinessDetail bd = publish.saveBusiness(sb);
                String myBusKey1 = bd.getBusinessEntity().get(0).getBusinessKey();
                System.out.println("myBusiness key:  " + myBusKey1);


                myBusEntity = new BusinessEntity();
                myBusName = new Name();
                myBusName.setLang("en");
                myBusName.setValue("My Business Dept 2" + " " + xcal.toString());
                myBusEntity.getName().add(myBusName);

                // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                sb = new SaveBusiness();
                sb.getBusinessEntity().add(myBusEntity);
                sb.setAuthInfo(UDDIAuthToken.getAuthInfo());
                bd = publish.saveBusiness(sb);
                String myBusKey2 = bd.getBusinessEntity().get(0).getBusinessKey();
                System.out.println("myBusiness key:  " + myBusKey2);

                //ROOT creates half of the relationship

                //create a business relationship (publisher assertion)
                Holder<List<PublisherAssertion>> x = new Holder<List<PublisherAssertion>>();
                PublisherAssertion pa = new PublisherAssertion();
                pa.setFromKey(myBusKey2);
                pa.setToKey(myBusKey1);
                pa.setKeyedReference(new KeyedReference());
                pa.getKeyedReference().setKeyName("Subsidiary");
                pa.getKeyedReference().setKeyValue("parent-child");

                pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                x.value = new ArrayList<PublisherAssertion>();
                x.value.add(pa);
                publish.setPublisherAssertions(rootAuthToken.getAuthInfo(), x);

                //now "UDDI" the user, creates the other half. It has to be mirrored exactly


                x = new Holder<List<PublisherAssertion>>();
                pa = new PublisherAssertion();
                pa.setFromKey(myBusKey1);
                pa.setToKey(myBusKey2);
                pa.setKeyedReference(new KeyedReference());
                pa.getKeyedReference().setKeyName("Subsidiary");
                pa.getKeyedReference().setKeyValue("parent-child");

                pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                x.value = new ArrayList<PublisherAssertion>();
                x.value.add(pa);
                publish.setPublisherAssertions(UDDIAuthToken.getAuthInfo(), x);

                /*
                 * Here's some notes:
                 * You can use
                 * List<AssertionStatusItem> assertionStatusReport = publish.getAssertionStatusReport(UDDIAuthToken.getAuthInfo(), CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);
                 * to determine if there's any assertions/relationships requests that are pending
                 * this should have one item it in, the relationship that's incomplete
                 * 
                 * There's also publish.deletePublisherAssertions();
                 */

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        UddiRelatedBusinesses sp = new UddiRelatedBusinesses();
        sp.publish();
    }
}
