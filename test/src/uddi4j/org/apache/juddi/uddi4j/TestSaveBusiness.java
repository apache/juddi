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
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.transport.TransportException;

public class TestSaveBusiness extends UDDITestBase {

  public void testSaveMultipleBusiness() {

    BusinessDetail busDetail = null;
    try {
      BusinessEntity bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("ABC"));
      Vector busVector = new Vector();
      busVector.add(bEntity);

      bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("CDE"));
      busVector.add(bEntity);

      busDetail = proxy.save_business(token.getAuthInfoString(), busVector);
      Vector victor = busDetail.getBusinessEntityVector();
      assertEquals(victor.size(),2);
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    finally {
      cleanupBusinessDetail(busDetail);
    }
  }

  /**
   * possible bug reported 5/22/03, by
   * Ralph M�ller <ram77@gmx.de>, but can't reproduce
   * @return
   */
  public void testSaveBusinessMultipleNames() {
    BusinessDetail busDetail = null;

    try {
      BusinessEntity bEntity = new BusinessEntity();
      Vector names = new Vector();
      names.addElement(new Name("AuthInfo1"));
      names.addElement(new Name("AuthInfo2"));
      bEntity.setNameVector(names);

      Vector busVector = new Vector();
      busVector.add(bEntity);

      busDetail = proxy.save_business(token.getAuthInfoString(), busVector);
      Vector victor = busDetail.getBusinessEntityVector();
      assertEquals(victor.size(),1);
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    finally {
      cleanupBusinessDetail(busDetail);
    }
  }
}