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
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.assertion.PublisherAssertion;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.ErrInfo;
import org.uddi4j.response.Result;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.KeyedReference;

public class TestAddPublisherAssertion extends UDDITestBase {
  protected BusinessDetail[] _details;
  private static final String TEST_KEY = "irrelevant-key";
  protected String _businessKeyZero = null;
  protected String _businessKeyOne = null;
  protected String _businessKeyTwo = null;

  public void setUp() {
    _details = new BusinessDetail[3];

    try {
      BusinessEntity bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("ABC"));
      Vector busVector = new Vector();
      busVector.add(bEntity);

      _details[0] = proxy.save_business(token.getAuthInfoString(), busVector);

      bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("CDE"));
      busVector = new Vector();
      busVector.add(bEntity);

      _details[1] = proxy.save_business(getSecondAuthToken().getAuthInfoString(), busVector);

      bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("FGE"));
      busVector = new Vector();
      busVector.add(bEntity);

      _details[2] = proxy.save_business(getThirdAuthToken().getAuthInfoString(), busVector);

      Vector victor = _details[0].getBusinessEntityVector();
      assertNotNull(victor);
      _businessKeyZero = ((BusinessEntity)(victor.elementAt(0))).getBusinessKey();
      assertNotNull(_businessKeyZero);

      victor = _details[1].getBusinessEntityVector();
      assertNotNull(victor);
      _businessKeyOne = ((BusinessEntity)(victor.elementAt(0))).getBusinessKey();
      assertNotNull(_businessKeyOne);

      victor = _details[2].getBusinessEntityVector();
      assertNotNull(victor);
      _businessKeyTwo = ((BusinessEntity)(victor.elementAt(0))).getBusinessKey();
      assertNotNull(_businessKeyTwo);
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
  }
  public void testCases() {
    _createPeerToPeer();
    _createInvalidPeerToPeer();
    _userMismatch();
    _invalidToken();
  }

  private void _invalidToken() {
    PublisherAssertion pubAssert = new PublisherAssertion();

    pubAssert.setToKeyString(_businessKeyOne);
    pubAssert.setFromKeyString(_businessKeyZero);
    KeyedReference keyedRef = new KeyedReference(TModel.RELATIONSHIPS_TMODEL_KEY, TEST_KEY, "peer-peer");
    pubAssert.setKeyedReference(keyedRef);

    try {
      DispositionReport disp =
          proxy.add_publisherAssertions("NONSENSE", pubAssert);
      assertFalse(disp.success());

      Vector results = disp.getResultVector();
      Result result = (Result)results.elementAt(0);
      ErrInfo errInfo = result.getErrInfo();
      assertEquals(errInfo.getErrCode(),DispositionReport.E_authTokenRequired);
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
  private void _userMismatch() {
    PublisherAssertion pubAssert = new PublisherAssertion();

    pubAssert.setToKeyString(_businessKeyOne);
    pubAssert.setFromKeyString(_businessKeyZero);
    KeyedReference keyedRef = new KeyedReference(TModel.RELATIONSHIPS_TMODEL_KEY, TEST_KEY, "peer-peer");
    pubAssert.setKeyedReference(keyedRef);

    try {
      DispositionReport disp =
          proxy.add_publisherAssertions(getThirdAuthToken().getAuthInfoString(), pubAssert);
      assertFalse(disp.success());

      Vector results = disp.getResultVector();
      Result result = (Result)results.elementAt(0);
      ErrInfo errInfo = result.getErrInfo();
      assertEquals(errInfo.getErrCode(),DispositionReport.E_userMismatch);
      //assertEquals(disp.getErrCode(),disp.E_userMismatch);
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
      assertEquals(errInfo.getErrCode(),DispositionReport.E_userMismatch);
      //assertEquals(disp.getErrCode(),disp.E_userMismatch);
    }
  }

  private void _createInvalidPeerToPeer() {
    PublisherAssertion pubAssert = new PublisherAssertion();

    pubAssert.setToKeyString("NONSENSE");
    pubAssert.setFromKeyString(_businessKeyZero);
    KeyedReference keyedRef = new KeyedReference(TModel.RELATIONSHIPS_TMODEL_KEY, TEST_KEY, "peer-peer");
    pubAssert.setKeyedReference(keyedRef);

    try {
      DispositionReport disp = proxy.add_publisherAssertions(token.
          getAuthInfoString(), pubAssert);
      assertFalse(disp.success());

      Vector results = disp.getResultVector();
      Result result = (Result)results.elementAt(0);
      ErrInfo errInfo = result.getErrInfo();
      assertEquals(errInfo.getErrCode(),DispositionReport.E_invalidKeyPassed);
      //assertEquals(disp.getErrCode(),disp.E_invalidKeyPassed);
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
      assertEquals(errInfo.getErrCode(),DispositionReport.E_invalidKeyPassed);
      //assertEquals(disp.getErrCode(),disp.E_invalidKeyPassed);
    }
  }

  private void _createPeerToPeer() {
    PublisherAssertion pubAssert = new PublisherAssertion();

    pubAssert.setToKeyString(_businessKeyOne);
    pubAssert.setFromKeyString(_businessKeyZero);
    KeyedReference keyedRef = new KeyedReference(TModel.RELATIONSHIPS_TMODEL_KEY, TEST_KEY, "peer-peer");
    pubAssert.setKeyedReference(keyedRef);

    try {
      DispositionReport disp = proxy.add_publisherAssertions(token.
          getAuthInfoString(), pubAssert);
      assertTrue(disp.success());
      disp = proxy.delete_publisherAssertions(token.getAuthInfoString(),pubAssert);
      assertTrue(disp.success());
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    finally {
      try {
        // not sure this actually is legal..
        proxy.delete_publisherAssertions(token.getAuthInfoString(), pubAssert);
      }
      catch(Exception ex) {
        // don't care..
      }
    }
  }

  public void tearDown() {
    cleanupBusinessDetail(_details[0]);
    cleanupBusinessDetail(_details[1],getSecondAuthToken().getAuthInfoString());
    cleanupBusinessDetail(_details[2],getThirdAuthToken().getAuthInfoString());
  }
  private void _expiredToken() {
    PublisherAssertion pubAssert = new PublisherAssertion();

    pubAssert.setToKeyString(_businessKeyOne);
    pubAssert.setFromKeyString(_businessKeyZero);
    KeyedReference keyedRef = new KeyedReference(TModel.RELATIONSHIPS_TMODEL_KEY, TEST_KEY, "peer-peer");
    pubAssert.setKeyedReference(keyedRef);

    try {
       DispositionReport disp = proxy.add_publisherAssertions(fourthAuthToken.getAuthInfoString(), pubAssert);
       assertTrue(disp.success());

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
      //assertEquals(disp.getErrCode(),disp.E_authTokenExpired);
    }
    finally {
      try {

      }
      catch(Exception ex) {

      }
    }
  }

  private void _emptyKeyNameAndKeyValue() {
      PublisherAssertion pubAssert = new PublisherAssertion();

      pubAssert.setToKeyString(_businessKeyOne);
      pubAssert.setFromKeyString(_businessKeyZero);
      KeyedReference keyedRef = new KeyedReference(TModel.RELATIONSHIPS_TMODEL_KEY, "", "");
      pubAssert.setKeyedReference(keyedRef);


      try {
         DispositionReport disp = proxy.add_publisherAssertions(token.getAuthInfoString(), pubAssert);
         assertTrue(disp.success());
      }
      catch (TransportException ex) {
        fail(ex.toString());
      }
      catch (UDDIException ex) {

      }
      finally {
        try {
          // not sure this actually is legal..
          proxy.delete_publisherAssertions(token.getAuthInfoString(), pubAssert);
        }
        catch(Exception ex) {
          // don't care..
        }

      }


    }



}