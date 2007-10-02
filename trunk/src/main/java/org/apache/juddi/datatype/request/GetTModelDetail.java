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
import org.apache.juddi.datatype.TModelKey;

/**
 * "Used to get full details for a given set of registered tModel
 *  data. Returns a tModelDetail message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetTModelDetail implements RegistryObject,Inquiry
{
  String generic;
  Vector tModelKeyVector;

  /**
   * default constructor
   */
  public GetTModelDetail()
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
   *
   */
  public Vector getTModelKeyVector()
  {
    return this.tModelKeyVector;
  }

  /**
   * Add a TModelKey to the collection of TModelKeys
   *
   * @param key The new TModelKey to add
   */
  public void addTModelKey(TModelKey key)
  {
    if ((key != null) && (key.getValue() != null))
      addTModelKey(key.getValue());
  }

  /**
   * Add a ServiceKey to the collection of ServiceKeys
   *
   * @param key The new ServiceKey to add to the collection of ServiceKeys
   */
  public void addTModelKey(String key)
  {
    if (tModelKeyVector == null)
      tModelKeyVector = new Vector();
    tModelKeyVector.add(key);
  }

  /**
   * Sets the TModelKey Vector
   *
   * @param keys The new collection of TModelKeys
   */
  public void setTModelKeyVector(Vector keys)
  {
    this.tModelKeyVector = keys;
  }
}