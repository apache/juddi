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
 * "Used to delete registered businessEntity information from the registry."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DeleteBusiness implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector businessKeyVector;

  /**
   *
   */
  public DeleteBusiness()
  {
  }

  /**
   *
   */
  public DeleteBusiness(AuthInfo authInfo, String businessKey)
  {
    this.authInfo = authInfo;
    addBusinessKey(businessKey);
  }

  /**
   *
   */
  public DeleteBusiness(AuthInfo authInfo,Vector businessKeys)
  {
    this.authInfo = authInfo;
    this.businessKeyVector = businessKeys;
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
  public void addBusinessKey(BusinessKey businessKey)
  {
    if ((businessKey != null) && (businessKey.getValue() != null))
      this.addBusinessKey(businessKey.getValue());
  }

  /**
   *
   */
  public void addBusinessKey(String businessKey)
  {
    if (this.businessKeyVector == null)
      this.businessKeyVector = new Vector();
    this.businessKeyVector.add(businessKey);
  }

  /**
   *
   */
  public void setBusinessKeyVector(Vector businessKeys)
  {
    this.businessKeyVector = businessKeys;
  }

  /**
   *
   */
  public Vector getBusinessKeyVector()
  {
    return this.businessKeyVector;
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