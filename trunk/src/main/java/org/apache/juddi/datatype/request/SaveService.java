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
import org.apache.juddi.datatype.service.BusinessService;

/**
 * "Used to register or update complete information about a businessService
 *  exposed by a specified businessEntity."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveService implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector serviceVector;

  /**
   *
   */
  public SaveService()
  {
  }

  /**
   *
   */
  public SaveService(AuthInfo info, BusinessService service)
  {
    this.authInfo = info;
    addBusinessService(service);
  }

  /**
   *
   */
  public SaveService(AuthInfo info,Vector services)
  {
    this.authInfo = info;
    this.serviceVector = services;
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
  public void setAuthInfo(AuthInfo info)
  {
    this.authInfo = info;
  }

  /**
   *
   */
  public AuthInfo getAuthInfo()
  {
    return this.authInfo;
  }

  /**
   *
   */
  public void addBusinessService(BusinessService businessService)
  {
    if (this.serviceVector == null)
      this.serviceVector = new Vector();
    this.serviceVector.add(businessService);
  }

  /**
   *
   */
  public void setServiceVector(Vector services)
  {
    this.serviceVector = services;
  }

  /**
   *
   */
  public Vector getBusinessServiceVector()
  {
    return this.serviceVector;
  }
}