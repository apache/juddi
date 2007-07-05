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

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindRelatedBusinesses implements RegistryObject,Inquiry
{
  String businessKey;
  String generic;
  KeyedReference keyedReference;
  FindQualifiers findQualifiers;
  int maxRows;

  /**
   *
   */
  public FindRelatedBusinesses()
  {
  }

  /**
   * Sets the businesskey of this find_related_business request to the given key. Only
   * businesses related to the of the referenced businessEntity are returned.
   *
   * @param key A reference to the businessEntity.
   */
  public void setBusinessKey(String key)
  {
    this.businessKey = key;
  }

  /**
   * Sets the businesskey of this find_related_business request to the given key. Only
   * businesses related to the of the referenced businessEntity are returned.
   *
   * @param key An instance of BusinessKey
   */
  public void setBusinessKey(BusinessKey key)
  {
    if (key != null)
     this.businessKey = key.getValue();
  }

  /**
  * Returns the businesskey of this find_related_business request. Only businesses related
  * to the of the referenced businessEntity are returned.
  *
  * @return The businesskey of the referenced businessEntity.
  */
  public String getBusinessKey()
  {
    return this.businessKey;
  }

  /**
   *
   * @param genericValue
   */
  public void setGeneric(String genericValue)
  {
    this.generic = genericValue;
  }

  /**
   *
   * @return String UDDI request's generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public KeyedReference getKeyedReference()
  {
    return this.keyedReference;
  }

  /**
   *
   */
  public void setKeyedReference(KeyedReference keyedRef)
  {
    this.keyedReference = keyedRef;
  }

  /**
   *
   */
  public int getMaxRows()
  {
    return maxRows;
  }

  /**
   *
   */
  public void setMaxRows(int maxRows)
  {
    this.maxRows = maxRows;
  }

  /**
   *
   */
  public void setMaxRows(String maxRows)
  {
    setMaxRows(Integer.parseInt(maxRows));
  }

  /**
   *
   */
  public void addFindQualifier(FindQualifier findQualifier)
  {
    if (this.findQualifiers == null)
      this.findQualifiers = new FindQualifiers();
    this.findQualifiers.addFindQualifier(findQualifier);
  }

  /**
   *
   */
  public void setFindQualifiers(FindQualifiers findQualifiers)
  {
    this.findQualifiers = findQualifiers;
  }

  /**
   *
   */
  public FindQualifiers getFindQualifiers()
  {
    return findQualifiers;
  }
}