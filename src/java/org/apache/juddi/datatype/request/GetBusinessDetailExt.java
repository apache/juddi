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

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.RegistryObject;

/**
 * "Used to get extended businessEntity information. Returns a
 *  businessDetailExt message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetBusinessDetailExt implements RegistryObject,Inquiry
{
  String generic;
  Vector businessKeyVector;

  /**
   * Construct a new empty get_businessDetailExt request.
   */
  public GetBusinessDetailExt()
  {
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
   * Add a BusinessKey to the collection of BusinessKeys
   *
   * @param key The new BusinessKey to add
   */
  public void addBusinessKey(BusinessKey key)
  {
    if ((key != null) && (key.getValue() != null))
      addBusinessKey(key.getValue());
  }

  /**
   * Add a reference to an extended business entity.
   *
   * @param key The key of the referenced extended business entity.
   * @throws ClassCastException If the given key is not a businesskey.
   */
  public void addBusinessKey(String key)
  {
    if (businessKeyVector == null)
      businessKeyVector = new Vector();
    businessKeyVector.add(key);
  }

  /**
   * Sets the BusinessKey Vector
   *
   * @param keys The new collection of BusinessKeys
   */
  public void setBusinessKeyVector(Vector keys)
  {
    this.businessKeyVector = keys;
  }

  /**
   *
   */
  public Vector getBusinessKeyVector()
  {
    return this.businessKeyVector;
  }
}