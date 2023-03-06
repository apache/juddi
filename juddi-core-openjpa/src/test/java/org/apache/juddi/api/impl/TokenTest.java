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
package org.apache.juddi.api.impl;

import java.rmi.RemoteException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.auth.MockWebServiceContext;
import org.apache.juddi.v3.error.AuthTokenRequiredException;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckPublisherAssertion;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This was created to test the features added for JIRA JUDDI-686 which implies
 * that auth tokens can only be used from the IP address that it was issued to
 *
 * @author Alex O'Ree
 */
public class TokenTest {

    private static Log logger = LogFactory.getLog(TokenTest.class);
    private static API_010_PublisherTest api010 = new API_010_PublisherTest();
    private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckPublisherAssertion tckAssertion = new TckPublisherAssertion(new UDDIPublicationImpl());
    private static TckFindEntity tckFindEntity = new TckFindEntity(new UDDIInquiryImpl());
    private static String authInfoJoe = null;
    private static String authInfoSam = null;
    private static String authInfoMary = null;

    @BeforeClass
    public static void startRegistry() throws ConfigurationException {
        Registry.start();
        logger.debug("Getting auth token..");
        try {
            api010.saveJoePublisher();
            api010.saveSamSyndicator();
            UDDISecurityPortType security = new UDDISecurityImpl();
            authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
            authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
            authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
        } catch (RemoteException e) {
            logger.error(e.getMessage(), e);
            Assert.fail("Could not obtain authInfo token.");
        }
    }

    @AfterClass
    public static void stopRegistry() throws ConfigurationException {
        Registry.stop();
    }

    @Test
    public void TestMatchingIPAddress() throws Exception {
        MockWebServiceContext mwsc = new MockWebServiceContext("192.168.12.42");
        UDDIPublicationImpl pub = new UDDIPublicationImplExt(mwsc);
        UDDISecurityImpl security = new UDDISecurityImpl(mwsc);
        String authToken1 = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
        pub.getPublisherAssertions(authToken1);
    }

    @Test(expected = AuthTokenRequiredException.class)
    public void TestMisMatchingIPAddress() throws Exception {
        MockWebServiceContext mwsc = new MockWebServiceContext("192.168.12.42");
        MockWebServiceContext mwsc2 = new MockWebServiceContext("10.1.1.1");
        UDDIPublicationImpl pub = new UDDIPublicationImplExt(mwsc2);
        UDDISecurityImpl security = new UDDISecurityImpl(mwsc);
        String authToken1 = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
        pub.getPublisherAssertions(authToken1);
    }

    @Test
    public void TestNullIPAddress() throws Exception {
        MockWebServiceContext mwsc = new MockWebServiceContext("192.168.12.42");
        UDDIPublicationImpl pub = new UDDIPublicationImplExt(null);
        UDDISecurityImpl security = new UDDISecurityImpl(mwsc);
        String authToken1 = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
        pub.getPublisherAssertions(authToken1);
    }

    @Test
    public void TestNullIPAddress2() throws Exception {
        MockWebServiceContext mwsc = new MockWebServiceContext("192.168.12.42");
        UDDIPublicationImpl pub = new UDDIPublicationImplExt(mwsc);
        UDDISecurityImpl security = new UDDISecurityImpl(null);
        String authToken1 = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
        pub.getPublisherAssertions(authToken1);
    }

    @Test
    public void TestNullIPAddress3() throws Exception {
        UDDIPublicationImpl pub = new UDDIPublicationImplExt(null);
        UDDISecurityImpl security = new UDDISecurityImpl(null);
        String authToken1 = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
        pub.getPublisherAssertions(authToken1);
    }
}
