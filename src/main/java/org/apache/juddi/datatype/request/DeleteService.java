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
import org.apache.juddi.datatype.ServiceKey;

/**
 * "Used to delete an existing businessService from the businessServices
 *  collection that is part of a specified businessEntity."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DeleteService implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector serviceKeyVector;

  /**
   *
   */
  public DeleteService()
  {
  }

  /**
   *
   */
  public DeleteService(AuthInfo authInfo,String serviceKey)
  {
    setAuthInfo(authInfo);
    addServiceKey(serviceKey);
  }

  /**
   *
   */
  public DeleteService(AuthInfo authInfo,Vector serviceKeys)
  {
    setAuthInfo(authInfo);
    setServiceKeyVector(serviceKeys);
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
  public void addServiceKey(ServiceKey serviceKey)
  {
    if ((serviceKey != null) && (serviceKey.getValue() != null))
      addServiceKey(serviceKey.getValue());
  }

  /**
   *
   */
  public void addServiceKey(String serviceKey)
  {
    if (this.serviceKeyVector == null)
      this.serviceKeyVector = new Vector();
    this.serviceKeyVector.add(serviceKey);
  }

  /**
   *
   */
  public void setServiceKeyVector(Vector serviceKeys)
  {
    this.serviceKeyVector = serviceKeys;
  }

  /**
   *
   */
  public Vector getServiceKeyVector()
  {
    return this.serviceKeyVector;
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