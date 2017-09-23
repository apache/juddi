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

import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * This class shows you how to get the results of an existing subscription in
 * UDDI.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiSubscribeValidate {

        private static UDDISecurityPortType security = null;
        private static UDDISubscriptionPortType uddiSubscriptionService = null;

        public UddiSubscribeValidate() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        uddiSubscriptionService = transport.getUDDISubscriptionService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
        
          public UddiSubscribeValidate(Transport transport) {
                try {
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();
                        uddiSubscriptionService = transport.getUDDISubscriptionService();

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /**
         * gets subscription results synchronously
         *
         * @param authtoken
         * @param key
         */
        public void go(String authtoken, String key) {
                if (key == null) {
                        System.out.println("No key was specified!");
                        return;
                }
                try {

                        // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
                        // and can save other publishers).
                        if (authtoken == null) {
                                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                                getAuthTokenRoot.setUserID("root");
                                getAuthTokenRoot.setCred("root");

                                // Making API call that retrieves the authentication token for the 'root' user.
                                AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                                System.out.println("root AUTHTOKEN = " + "don't log 'em");
                                authtoken = rootAuthToken.getAuthInfo();
                        }
                        DatatypeFactory df = DatatypeFactory.newInstance();
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(System.currentTimeMillis());
                        XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);

                        //
                        GetSubscriptionResults req = new GetSubscriptionResults();
                        req.setAuthInfo(authtoken);
                        //insert your subscription key values here
                        req.setSubscriptionKey(key);
                        req.setCoveragePeriod(new CoveragePeriod());
                        req.getCoveragePeriod().setEndPoint(xcal);

                        gcal = new GregorianCalendar();
                        //Time range that we want change logs on
                        gcal.add(Calendar.MONTH, -1);
                        req.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                        SubscriptionResultsList subscriptionResults = uddiSubscriptionService.getSubscriptionResults(req);
                        System.out.println("items modified: " + subscriptionResults.getBusinessDetail().getBusinessEntity().size());

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String args[]) {
                UddiSubscribeValidate sp = new UddiSubscribeValidate();
                sp.go(null, null);
        }
}
