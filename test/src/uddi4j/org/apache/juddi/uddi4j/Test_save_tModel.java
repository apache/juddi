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

import java.util.Vector;

import org.uddi4j.UDDIException;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.ErrInfo;
import org.uddi4j.response.Result;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.KeyedReference;

public class Test_save_tModel extends UDDITestBase {
  TModelDetail _tModelDetail0 = null;

  public void testCases() {
    _testCreatetModel();
    _testCreatetModel_noName();
    _testAuthTokenExpired();
    _testAuthTokenRequired();
  }

  public void setUp() {
    PublisherManager.createPublisher("tModel_Publisher", "tModel_Publisher");
  }

  public void tearDown() {
    Vector victor = _tModelDetail0.getTModelVector();
    TModel tModel = (TModel)victor.elementAt(0);
    String UUID = tModel.getTModelKey();
    try {
      proxy.delete_tModel(token.getAuthInfoString(), UUID);
    }
    catch (TransportException ex) {
      // don't care ?? could invalidate further tests..
    }
    catch (UDDIException ex) {
      // don't care ?? could invalidate further tests..
    }
  }

  private Vector _buildValidTModelVector() {
      TModel tester = new TModel();
      KeyedReference kr = new KeyedReference();
      kr.setKeyName("testKeyName0");
      kr.setKeyValue("testKeyValue0");
      CategoryBag catBag = new CategoryBag();
      catBag.add(kr);
      tester.setCategoryBag(catBag);
      tester.setName("testKeyName0");
      Vector tModelVector = new Vector();
      tModelVector.add(tester);
      return tModelVector;
  }

  private void _testCreatetModel() {
    Vector tModelVector = _buildValidTModelVector();
    assertNotNull(tModelVector);

    try {
      _tModelDetail0 = proxy.save_tModel(token.getAuthInfoString(),
                                         tModelVector);
      assertNotNull(_tModelDetail0);
      tModelVector = _tModelDetail0.getTModelVector();
      assertNotNull(tModelVector);
      assertEquals(tModelVector.size(), 1);
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
  }

  private void _testCreatetModel_noName() {
    TModel tester = new TModel();
    KeyedReference kr = new KeyedReference();
    kr.setKeyName("testKeyName0");
    kr.setKeyValue("testKeyValue0");
    CategoryBag catBag = new CategoryBag();
    catBag.add(kr);
    tester.setCategoryBag(catBag);
    Vector tModelVector = new Vector();
    tModelVector.add(tester);
    try {
      _tModelDetail0 = proxy.save_tModel(token.getAuthInfoString(),
                                         tModelVector);
      Object _empty = null;
      assertNotNull(_empty);
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      assertTrue(true);
    }
  }

  private void _testAuthTokenExpired() {
    getExpiredAuthToken();
    Vector tModelVector = _buildValidTModelVector();
    assertNotNull(tModelVector);
    try {
      String authInfo = PublisherManager.getExpiredAuthToken("tModel_Publisher", "password");
      proxy.save_tModel(authInfo,
                        tModelVector);
      assertNotNull(null);
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      DispositionReport disp = ex.getDispositionReport();
      assertFalse(disp.success());

      Vector results = disp.getResultVector();
      Result result = (Result)results.elementAt(0);
      ErrInfo errInfo = result.getErrInfo();
      assertEquals(errInfo.getErrCode(),DispositionReport.E_authTokenExpired);
    }
  }

  private void _testAuthTokenRequired() {
    TModel tester = new TModel();
    KeyedReference kr = new KeyedReference();
    kr.setKeyName("testKeyName0");
    kr.setKeyValue("testKeyValue0");
    CategoryBag catBag = new CategoryBag();
    catBag.add(kr);
    tester.setCategoryBag(catBag);
    Vector tModelVector = new Vector();
    tModelVector.add(tester);
    try {
      _tModelDetail0 = proxy.save_tModel("JUNK",
                                         tModelVector);
      assertNotNull(null);
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      DispositionReport disp = ex.getDispositionReport();
      assertFalse(disp.success());

      Vector results = disp.getResultVector();
      Result result = (Result)results.elementAt(0);
      ErrInfo errInfo = result.getErrInfo();
      assertEquals(errInfo.getErrCode(),DispositionReport.E_authTokenRequired);
    }
  }
}