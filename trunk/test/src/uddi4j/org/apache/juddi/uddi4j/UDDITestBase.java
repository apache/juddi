/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.juddi.uddi4j;

import java.net.MalformedURLException;
import java.util.Properties;
import java.util.Vector;

import junit.framework.TestCase;

import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.transport.TransportException;

public class UDDITestBase extends TestCase {
  Properties config = null;
  UDDIProxy proxy = null;
  AuthToken token = null;
  AuthToken secondAuthToken = null;
  AuthToken thirdAuthToken = null;
  AuthToken fourthAuthToken = null;
  AuthToken expiredAuthToken = null;
  boolean _inited = false;

  {
    init();
  }
  protected void _cleanupBusinessDetail(BusinessDetail busDetail, String auth) {
    if (busDetail != null) {
      Vector victor = busDetail.getBusinessEntityVector();
      for (int index = 0; index < victor.size(); index++) {
        BusinessEntity bet = (BusinessEntity) victor.elementAt(index);
        try {
          proxy.delete_business(auth, bet.getBusinessKey());
        }
        catch (Exception ex) {
        }
      }
    }
  }
  protected void cleanupBusinessDetail(BusinessDetail busDetail, String auth) {
    _cleanupBusinessDetail(busDetail, auth);
  }
  protected void cleanupBusinessDetail(BusinessDetail busDetail) {
    _cleanupBusinessDetail(busDetail, token.getAuthInfoString());
  }

  protected void cleanupTModels(TModelDetail details) {
    Vector victor = details.getTModelVector();
    Vector roger = new Vector();
    for (int index = 0; index < victor.size(); index++) {
      roger.add( ((TModel)victor.elementAt(index)).getTModelKey() );
    }
      try {
        proxy.delete_tModel(token.getAuthInfoString(), roger);
      }
      catch (TransportException ex) {
        // don't care
      }
      catch (UDDIException ex) {
        // dont' care
      }
  }
  protected void init() {
    if (_inited) {
      return;
    }
    try {
      config = Configurator.load();

      // Construct a UDDIProxy object.
      proxy = new UDDIProxy();

      // Pass in userid and password registered at the UDDI site
      proxy.setInquiryURL(config.getProperty("inquiryURL"));
      proxy.setPublishURL(config.getProperty("publishURL"));

      PublisherManager.createPublisher("juddi2", "juddi2", config);
      PublisherManager.createPublisher("juddi3", "juddi3", config);
      PublisherManager.createPublisher("juddi4", "juddi4", config);
      PublisherManager.createPublisher("expired", "expired", config);

      // Pass in userid and password registered at the UDDI site
      token = proxy.get_authToken(config.getProperty("userid"),
                                  config.getProperty("password"));
      secondAuthToken = proxy.get_authToken(config.getProperty("userid2"),
                                  config.getProperty("password"));
      thirdAuthToken = proxy.get_authToken(config.getProperty("userid3"),
                                  config.getProperty("password"));
      fourthAuthToken = proxy.get_authToken(config.getProperty("userid4"),
                                  config.getProperty("password"));

    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    catch (TransportException ex) {
      fail(ex.toString());

    }
    catch (MalformedURLException ex) {
      fail(ex.toString());
    }
    _inited = true;
  }

  protected AuthToken getSecondAuthToken() {
    return secondAuthToken;
  }
  protected AuthToken getThirdAuthToken() {
    return thirdAuthToken;
  }
  protected AuthToken getExpiredAuthToken() {
    if (expiredAuthToken == null) {
      try {
        expiredAuthToken = proxy.get_authToken(config.getProperty("expired"),
                                               config.getProperty("password"));
        DispositionReport disp =
            proxy.discard_authToken(expiredAuthToken.getAuthInfoString());
        assertTrue(disp.success());
      }
      catch (TransportException ex) {
      }
      catch (UDDIException ex) {
      }
    }
    return expiredAuthToken;
  }
}