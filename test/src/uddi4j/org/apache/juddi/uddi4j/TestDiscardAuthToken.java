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
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.ErrInfo;
import org.uddi4j.response.Result;
import org.uddi4j.transport.TransportException;

public class TestDiscardAuthToken extends UDDITestBase {
  private void _discardAuthToken(){
    try{
      DispositionReport disp =
      proxy.discard_authToken(fourthAuthToken.getAuthInfoString());
      assertTrue(disp.success());
   }
   catch(TransportException ex) {
      fail(ex.toString());
   }
   catch (UDDIException uddiEx) {
     fail(uddiEx.toString());
   }
  }
  private void _authTokenRequired(){
    try{
     DispositionReport disp =
     proxy.discard_authToken("");
     assertTrue(disp.success());
    }
    catch(TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException uddiEx) {
     DispositionReport disp = uddiEx.getDispositionReport();
     assertFalse(disp.success());

     Vector results = disp.getResultVector();
     Result result = (Result)results.elementAt(0);
     ErrInfo errInfo = result.getErrInfo();
     assertEquals(errInfo.getErrCode(),DispositionReport.E_authTokenRequired);
   }
  }

  public void testCases() {
    _discardAuthToken();
    _authTokenRequired();
  }

}