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

import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.RegistryObject;

/**
 * "Used to get full bindingTemplate information suitable for make one
 *  or more service requests. Returns a bindingDetail message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetBindingDetail implements RegistryObject,Inquiry
{
  String generic;
  Vector bindingKeyVector;

  /**
   * Construct a new empty get_bindingDetail request
   */
  public GetBindingDetail()
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
   * Add a reference to a bindingTemplate to this search. All the details for the
   * referenced bindingTemplate will be searched.
   *
   * @param key The bindingkey of the referenced bindingTemplate.
   */
  public void addBindingKey(BindingKey key)
  {
    if ((key != null) && (key.getValue() != null))
      addBindingKey(key.getValue());
  }

  /**
   * Adds a BindingKey to the collection of binding keys used by this query.
   *
   * @param key A BindingKey
   */
  public void addBindingKey(String key)
  {
    if (bindingKeyVector == null)
      bindingKeyVector = new Vector();
    bindingKeyVector.add(key);
  }

  /**
   * Add a collection of bindingTemplate references to this search. All the details for the
   * given referenced bindingTemplates will be searched.
   *
   * @param keys The collection of bindingkeys.
   */
  public void setBindingKeyVector(Vector keys)
  {
    this.bindingKeyVector = keys;
  }

  /**
   *
   */
  public Vector getBindingKeyVector()
  {
    return this.bindingKeyVector;
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();

    if (this.bindingKeyVector != null)
      for (int i=0; i<this.bindingKeyVector.size(); i++)
        buffer.append(this.bindingKeyVector.elementAt(i));

    return buffer.toString();
  }
}