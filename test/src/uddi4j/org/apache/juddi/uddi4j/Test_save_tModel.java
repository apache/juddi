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
    PublisherManager.createPublisher("tModel_Publisher", "tModel_Publisher",config);
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
