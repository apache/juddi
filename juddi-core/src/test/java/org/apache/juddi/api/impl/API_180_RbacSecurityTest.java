/*
 * Copyright 2019 The Apache Software Foundation.
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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.api.impl.mock.WebServiceContextMock;
import org.apache.juddi.api_v3.AccessLevel;
import org.apache.juddi.api_v3.Action;
import org.apache.juddi.api_v3.GetPermissionsMessageRequest;
import org.apache.juddi.api_v3.GetPermissionsMessageResponse;
import org.apache.juddi.api_v3.*;
import org.apache.juddi.api_v3.SetPermissionsMessageRequest;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.security.AccessControlFactory;
import org.apache.juddi.security.rbac.RbacRulesModel;
import org.apache.juddi.security.rbac.RoleBasedAccessControlImpl;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckTModel;

import org.junit.Assert;

import org.apache.juddi.v3.tck.TckSecurity;

import org.apache.juddi.v3.tck.TckSubscription;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class API_180_RbacSecurityTest {

    private static final Log logger = LogFactory.getLog(API_010_PublisherTest.class);

    private static void grant(AccessLevel accessLevel, List<String> keys, String user) throws Exception {
        JUDDIApiImpl publisher = new JUDDIApiImpl();
        SetPermissionsMessageRequest req = new SetPermissionsMessageRequest();
        req.setAuthInfo(authInfoUDDI);
        for (String key : keys) {

            Permission level = new Permission();
            level.setAction(Action.ADD);
            level.setEntityId(key);
            level.setLevel(accessLevel);
            level.setTarget(user);
            level.setType(null);
            req.getLevel().add(level);
        }
        SetPermissionsMessageResponse response = publisher.setPermissions(req);
    }

    private JUDDIApiImpl publisher = new JUDDIApiImpl();
    private UDDISecurityPortType security = new UDDISecurityImpl();
    private static TckSubscription tckSubscription = new TckSubscription(new UDDISubscriptionImpl(), new UDDISecurityImpl(), new UDDIInquiryImpl());

    private static API_010_PublisherTest api010 = new API_010_PublisherTest();
    private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
    private static TckFindEntity tckFindEntity = new TckFindEntity(new UDDIInquiryImpl());
    private static String authInfoJoe = null;
    private static String authInfoSam = null;
    private static final String TEST_ROLE = "TESTROLE1";
    private static String authInfoUDDI = null;

    @BeforeClass
    public static void startRegistry() throws ConfigurationException {
        System.setProperty(AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, "src/test/resources/juddiv3rbac.xml");
        Registry.start();
        AccessControlFactory.reset();
        Assert.assertTrue(AccessControlFactory.getAccessControlInstance() instanceof RoleBasedAccessControlImpl);
        logger.info("API_180_RbacSecurityTest");
        logger.debug("Getting auth token..");
        try {
            api010.saveJoePublisher();
            api010.saveSamSyndicator();
            UDDISecurityPortType security = new UDDISecurityImpl();
            authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
            authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
            authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
            TModel m = tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
            List<String> keys = new ArrayList<>();
            keys.add(m.getTModelKey());
            // keys.clear();
            grant(AccessLevel.READ, keys, RoleBasedAccessControlImpl.EVERYONE);
            keys = tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
            keys.clear();
            grant(AccessLevel.READ, keys, RoleBasedAccessControlImpl.EVERYONE);
            tckTModel.saveJoePublisherTmodel(authInfoJoe);
            tckBusiness.saveJoePublisherBusiness(authInfoJoe);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Assert.fail("Could not obtain authInfo token.");
        }
    }

    @AfterClass
    public static void stopRegistry() throws ConfigurationException {
        tckTModel.deleteCreatedTModels(authInfoJoe);
        Registry.stop();
        System.clearProperty(AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY);
       
        AppConfig.triggerReload();
          AccessControlFactory.reset();
    }

    @Test
    public void getSetDeletePerm() throws Exception {
        publisher.ctx = new WebServiceContextMock(TckPublisher.getUDDIPublisherId(), TEST_ROLE);

        GetPermissionsMessageRequest request = new GetPermissionsMessageRequest();
        request.setAuthInfo(authInfoUDDI);

        GetPermissionsMessageResponse permissions = publisher.getPermissions(request);
        //Assert.assertTrue(permissions.getLevel().isEmpty());

        SetPermissionsMessageRequest req = new SetPermissionsMessageRequest();
        req.setAuthInfo(authInfoUDDI);
        Permission level = new Permission();
        level.setAction(Action.ADD);
        level.setEntityId(TckBusiness.JOE_BUSINESS_KEY);
        level.setLevel(AccessLevel.READ);
        level.setTarget(TckPublisher.getSamPublisherId());
        level.setType(null);
        req.getLevel().add(level);

        SetPermissionsMessageResponse response = publisher.setPermissions(req);
        Assert.assertNotNull(response);
        permissions = publisher.getPermissions(request);
        //Assert.assertEquals(permissions.getLevel().size(), 1);
        boolean ok = false;
        Permission p1 = null;
        for (Permission p : permissions.getLevel()) {
            if (p.getTarget().equals(TckPublisher.getSamPublisherId())
                    && p.getEntityId().equals(TckBusiness.JOE_BUSINESS_KEY)
                    && p.getLevel() == AccessLevel.READ) {
                ok = true;
                p1 = p;
            }

        }
        Assert.assertTrue(ok);

        p1.setAction(Action.REMOVE);
        req.getLevel().clear();
        req.getLevel().add(p1);
        response = publisher.setPermissions(req);
        Assert.assertNotNull(response);
        permissions = publisher.getPermissions(request);
        //Assert.assertEquals(permissions.getLevel().size(), 0);

    }
}
