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
package org.apache.juddi.datatype.request;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;

/**
 * "Knows about the creation and populating of FindQualifiers objects.
 * Returns FindQualifiers."
 *
 *  Example Usage:
 *
 *    // build the find qualifiers instance
 *    FindQualifiers qualifiers = new FindQualifiers();
 *    qualifiers.add(new FindQualifier(FindQualifier.exactNameMatch));
 *    qualifiers.add(new FindQualifier(FindQualifier.andAllKeys));
 *
 *    // set find qualifiers into request
 *    FindBusiness findBusiness = new FindBusiness();
 *    findBusiness.setFindQualifiers(qualifiers);
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindQualifiers implements RegistryObject
{
  Vector findQualifierVector;

  public boolean exactNameMatch;
  public boolean caseSensitiveMatch;
  public boolean orAllKeys;
  public boolean orLikeKeys;
  public boolean andAllKeys;
  public boolean sortByNameAsc;
  public boolean sortByNameDesc;
  public boolean sortByDateAsc;
  public boolean sortByDateDesc;
  public boolean serviceSubset;
  public boolean combineCategoryBags;

  /**
   *
   */
  public FindQualifiers()
  {
  }

  /**
   *
   */
  public FindQualifiers(int size)
  {
    this.findQualifierVector = new Vector(size);
  }

  /**
   *
   */
  public void addFindQualifier(FindQualifier findQualifier)
  {
    // ignore null FindQualifier instances.
    if (findQualifier == null)
      return;

    // ignore null/empty FindQualifier values.
    String qValue = findQualifier.getValue();
    if ((qValue == null) || (qValue.trim().length() == 0))
      return;

    if (this.findQualifierVector == null)
      this.findQualifierVector = new Vector();
    this.findQualifierVector.add(findQualifier);

    if (qValue.equals(FindQualifier.EXACT_NAME_MATCH))
      exactNameMatch = true;
    else if (qValue.equals(FindQualifier.CASE_SENSITIVE_MATCH))
      caseSensitiveMatch = true;
    else if (qValue.equals(FindQualifier.OR_ALL_KEYS))
      orAllKeys = true;
    else if (qValue.equals(FindQualifier.OR_LIKE_KEYS))
      orLikeKeys = true;
    else if (qValue.equals(FindQualifier.AND_ALL_KEYS))
      andAllKeys = true;
    else if (qValue.equals(FindQualifier.SORT_BY_NAME_ASC))
      sortByNameAsc = true;
    else if (qValue.equals(FindQualifier.SORT_BY_NAME_DESC))
      sortByNameDesc = true;
    else if (qValue.equals(FindQualifier.SORT_BY_DATE_ASC))
      sortByDateAsc = true;
    else if (qValue.equals(FindQualifier.SORT_BY_DATE_DESC))
      sortByDateDesc = true;
    else if (qValue.equals(FindQualifier.SERVICE_SUBSET))
      serviceSubset = true;
    else if (qValue.equals(FindQualifier.COMBINE_CATEGORY_BAGS))
      combineCategoryBags = true;
  }

  /**
   *
   */
  public void setFindQualifierVector(Vector qualifierVector)
  {
    this.exactNameMatch = false;
    this.caseSensitiveMatch = false;
    this.orAllKeys = false;
    this.orLikeKeys = false;
    this.andAllKeys = false;
    this.sortByNameAsc = false;
    this.sortByNameDesc = false;
    this.sortByDateAsc = false;
    this.sortByDateDesc = false;
    this.serviceSubset = false;
    this.combineCategoryBags = false;

    if ((qualifierVector != null) && (qualifierVector.size() > 0))
    {
      for (int i=0; i<qualifierVector.size(); i++)
        addFindQualifier((FindQualifier)qualifierVector.elementAt(i));
    }
  }

  /**
   *
   */
  public Vector getFindQualifierVector()
  {
    return this.findQualifierVector;
  }

  /**
   *
   */
  public int size()
  {
    if (this.findQualifierVector != null)
      return this.findQualifierVector.size();
    else
      return 0;
  }

  // test driver
  public static void main(String[] args)
  {
    FindQualifiers fq = new FindQualifiers();
    fq.addFindQualifier(new FindQualifier(FindQualifier.CASE_SENSITIVE_MATCH));
    fq.addFindQualifier(new FindQualifier(""));
    fq.addFindQualifier(null);
  }
}