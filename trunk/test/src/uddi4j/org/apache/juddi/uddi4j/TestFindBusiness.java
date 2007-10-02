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
import org.uddi4j.response.BusinessInfo;
import org.uddi4j.response.BusinessList;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;

/**
 * Sample code that exercises the find_business API.
 * @author Andy Cutright (acutright@borland.com)
 * @author Chrissie Hynde, for variable name inspirations
 * <p> tests the following find qualifiers..
 * </p>
 * EM   CS
 * 0     0  ee matches [eE][eE]* UPPER(NAME LIKE) text.toUpperCase  < default case
 * 0     1  ee matches ee*       NAME LIKE
 * 1     0  ee matches [eE][eE]  UPPER(NAME =) text.toUpperCase
 * 1     1  ee matches ee        NAME =
 *
 */

public class TestFindBusiness extends UDDITestBase
{
  public void testSortByNameDesc() {
    BusinessDetail busDetail = null;
    try {
      BusinessEntity bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("ABC"));
      Vector busVector = new Vector();
      busVector.add(bEntity);

      bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("CDE"));
      busVector.add(bEntity);

      bEntity = new BusinessEntity();
      bEntity.setDefaultName(new Name("EFG"));
      busVector.add(bEntity);

      busDetail = proxy.save_business(token.getAuthInfoString(), busVector);
      assertTrue(querySortByNameDesc());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    catch (TransportException ex){
      fail(ex.toString());
    }
    finally {
      cleanupBusinessDetail(busDetail);
    }
  }

  protected boolean querySortByNameDesc() {
    try {
      Vector names = new Vector();
      names.add(new Name("ABC"));
      names.add(new Name("CDE"));
      names.add(new Name("EFG"));

      FindQualifiers findQualifiers = new FindQualifiers();
      Vector qualifier = new Vector();
      qualifier.add(new FindQualifier("sortByNameDesc"));
      findQualifiers.setFindQualifierVector(qualifier);

      BusinessList businessList = null;
      businessList = proxy.find_business(names, null, null, null, null,
                                         findQualifiers, 5);

      Vector victor = businessList.getBusinessInfos().getBusinessInfoVector();
      if (victor.size() !=3 ) {
        return false;
      }
      BusinessInfo info = (BusinessInfo)victor.elementAt(0);
      if (! ((BusinessInfo)victor.elementAt(0)).getDefaultNameString().equals("EFG")) {
        return false;
      }
      if (! ((BusinessInfo)victor.elementAt(1)).getDefaultNameString().equals("CDE")) {
        return false;
      }
      if (! ((BusinessInfo)victor.elementAt(2)).getDefaultNameString().equals("ABC")) {
        return false;
      }
    }
    catch (UDDIException ex) {
      return false;
    }
    catch (TransportException ex) {
      return false;
    }
    return true;
  }

  public void testExactMatch() {
    BusinessDetail busDetail = null;

    try {
      BusinessEntity bEntity = new BusinessEntity();
      Name name = new Name("BadBoysGetSpanked");
      bEntity.setDefaultName(name);
      Vector busVector = new Vector();
      busVector.add(bEntity);
      busDetail = proxy.save_business(token.getAuthInfoString(), busVector);

      assertTrue(queryExactMatch());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    catch(TransportException ex) {
      fail(ex.toString());
    }
    finally {
      cleanupBusinessDetail(busDetail);
    }
  }

  protected boolean queryExactMatch() {

    try {
      Vector names = new Vector();
      names.add(new Name("BadBoys"));

      FindQualifiers findQualifiers = new FindQualifiers();
      Vector qualifier = new Vector();
      qualifier.add(new FindQualifier("exactNameMatch"));
      findQualifiers.setFindQualifierVector(qualifier);

      BusinessList businessList = null;

      businessList = proxy.find_business(names, null, null, null, null,
                                         findQualifiers, 5);

      if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 0) {
        return false;
      }

      names.clear();
      names.add(new Name("BadBoysGetSpanked"));

      businessList = proxy.find_business(names, null, null, null, null,
                                         findQualifiers, 5);

      if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 1) {
        return false;
      }

      /**
       * exact match, case insensitive
       */
      names.clear();
      names.add(new Name("badboysgetspanked"));

      businessList = proxy.find_business(names, null, null, null, null,
                                         findQualifiers, 5);

      if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 1) {
        return false;
      }

      /**
       * exact match, case sensitive, negative
       */
      findQualifiers = new FindQualifiers();
      qualifier = new Vector();
      qualifier.add(new FindQualifier("exactNameMatch"));
      qualifier.add(new FindQualifier("caseSensitiveMatch"));
      findQualifiers.setFindQualifierVector(qualifier);

      names.clear();
      names.add(new Name("badboysgetspanked"));
      businessList = proxy.find_business(names, null, null, null, null,
                                         findQualifiers, 5);

      if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 0) {
        return false;
      }

      /**
       * exact match, case sensitive, positive
       */
      names.clear();
      names.add(new Name("BadBoysGetSpanked"));
      businessList = proxy.find_business(names, null, null, null, null,
                                         findQualifiers, 5);

      if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 1) {
        return false;
      }

    }
    catch (UDDIException ex) {
      return false;
    }
    catch (TransportException ex) {
      return false;
    }
    return true;
  }

  public void testCaseSensitiveMatch() {
    BusinessDetail busDetail = null;
    try {
      // create the business CaseSensitive
      BusinessEntity bEntity = new BusinessEntity();
      Name name = new Name("casesensitive");
      bEntity.setDefaultName(name);
      Vector busVector = new Vector();
      busVector.add(bEntity);

      bEntity = new BusinessEntity();
      name = new Name("CaseSensitive");
      bEntity.setDefaultName(name);
      busVector.add(bEntity);
      busDetail = proxy.save_business(token.getAuthInfoString(),busVector);

      assertTrue(queryCaseSensitiveMatch());
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
    finally {
      cleanupBusinessDetail(busDetail);
    }
  }

  /**
   * tests
   * 0. default case (not sensitive and not exact)
   * 1. case sensitive, not exact
   */
  protected boolean queryCaseSensitiveMatch() throws TransportException, UDDIException {
    Vector names = new Vector();
    names.add(new Name("c"));

    FindQualifiers findQualifiers = new FindQualifiers();
    Vector qualifier = new Vector();
    qualifier.add(new FindQualifier("caseSensitiveMatch"));
    findQualifiers.setFindQualifierVector(qualifier);

    BusinessList businessList = proxy.find_business(names, null, null, null,null,findQualifiers,5);

    if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 1) {
      return false;
    }

    names.clear();
    names.add(new Name("C"));
    businessList = proxy.find_business(names, null, null, null,null,findQualifiers,5);

    if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 1) {
      return false;
    }
    businessList = proxy.find_business(names, null, null, null,null,null,5);

    if (businessList.getBusinessInfos().getBusinessInfoVector().size() != 2) {
      return false;
    }
    return true;
  }
}
