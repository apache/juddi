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
import org.uddi4j.response.TModelDetail;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.KeyedReference;

public class TestTModel extends UDDITestBase {

  public void testCases() {
    _testNoNameQualifier();
    _testEmptyFindQualifier(); 
  }
  public void _testEmptyFindQualifier() {
    Vector tmods = new Vector();
    TModel tModel =  new TModel();
    tModel.setName("AnimalProtocol");
    TModelDetail tmodDetail = null;

    tmods.add(tModel);
    try {
      tmodDetail = proxy.save_tModel(token.getAuthInfoString(),tmods);
      assertTrue(_queryEmptyQualifiers()) ;
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    finally {
      cleanupTModels(tmodDetail);
    }
 }

  public boolean _queryEmptyQualifiers() {
    FindQualifiers findQualifiers = new FindQualifiers();
    FindQualifier findQualifier = new FindQualifier();
    findQualifiers.add(findQualifier);
    try {
      proxy.find_tModel("AnimalProtocol", null, null, findQualifiers, 5);
    }
    catch (UDDIException ex) {
      return false;
    }
    catch (TransportException ex) {
      return false;
    }
    return true;
  }
  
  public boolean _testNoNameQualifier() {
    
    Vector tmods = new Vector();
    TModel tModel =  new TModel();
    tModel.setName("PuffyProtocol");
    TModelDetail tmodDetail = null;

    tmods.add(tModel);
    try {
      tmodDetail = proxy.save_tModel(token.getAuthInfoString(), tmods);
      assertTrue(_queryNoNameQualifiers());
    }
    catch (TransportException ex) {
      fail(ex.toString()); 
    } 
    catch (UDDIException ex) {
      fail(ex.toString()); 
    }
    finally {
      cleanupTModels(tmodDetail); 
    }
    return true;
  }

  public boolean _queryNoNameQualifiers() {
    CategoryBag catBag = new CategoryBag(); 
   KeyedReference kref = new KeyedReference("", "categorization", TModel.TYPES_TMODEL_KEY);
    catBag.add(kref);
    try {
      proxy.find_tModel("", catBag, null, null, 5);
    }
    catch (UDDIException ex) {
      return false;
    }
    catch (TransportException ex) {
      return false;
    }
    return true;
  }
}
