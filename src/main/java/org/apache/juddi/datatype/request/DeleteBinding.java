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
 * "Used to remove an existing bindingTemplate from the bindingTemplates
 *  collection that is part of a specified businessService structure."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DeleteBinding implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector bindingKeyVector;

  /**
   *
   */
  public DeleteBinding()
  {
  }

  /**
   *
   */
  public DeleteBinding(AuthInfo authInfo,String bindingKey)
  {
    this.authInfo = authInfo;
    addBindingKey(bindingKey);
  }

  /**
   *
   */
  public DeleteBinding(AuthInfo authInfo,Vector bindingKeys)
  {
    this.authInfo = authInfo;
    setBindingKeyVector(bindingKeys);
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
  public void addBindingKey(BindingKey bindingKey)
  {
    if ((bindingKey != null) && (bindingKey.getValue() != null))
      this.addBindingKey(bindingKey.getValue());
  }

  /**
   *
   */
  public void addBindingKey(String bindingKey)
  {
    if (this.bindingKeyVector == null)
      this.bindingKeyVector = new Vector();
    this.bindingKeyVector.add(bindingKey);
  }

  /**
   *
   */
  public void setBindingKeyVector(Vector bindingKeys)
  {
    this.bindingKeyVector = bindingKeys;
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
  public void setAuthInfo(AuthInfo authInfo)
  {
    this.authInfo = authInfo;
  }

  /**
   *
   */
  public AuthInfo getAuthInfo()
  {
    return this.authInfo;
  }
}