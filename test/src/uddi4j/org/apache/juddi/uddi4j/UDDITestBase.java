/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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

      PublisherManager.createPublisher("juddi2", "juddi2");
      PublisherManager.createPublisher("juddi3", "juddi3");
      PublisherManager.createPublisher("juddi4", "juddi4");
      PublisherManager.createPublisher("expired", "expired");

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