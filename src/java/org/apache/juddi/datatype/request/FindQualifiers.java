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