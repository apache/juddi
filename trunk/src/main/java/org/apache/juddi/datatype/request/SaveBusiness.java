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
import org.apache.juddi.datatype.UploadRegister;
import org.apache.juddi.datatype.business.BusinessEntity;

/**
 * "Used to register new businessEntity information or update existing
 *  businessEntity information.  Use this to control the overall
 *  information about the entire business.  Of the save_x APIs this one
 *  has the broadest effect."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveBusiness implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector businessVector;
  Vector uploadRegisterVector;

  /**
   *
   */
  public SaveBusiness()
  {
  }

  /**
   *
   */
  public SaveBusiness(AuthInfo info, BusinessEntity business)
  {
    this.authInfo = info;
    addBusinessEntity(business);
  }

  /**
   *
   */
  public SaveBusiness(AuthInfo info,Vector businesses)
  {
    this.authInfo = info;
    this.businessVector = businesses;
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
  public void addBusinessEntity(BusinessEntity businessEntity)
  {
    if (this.businessVector == null)
      this.businessVector = new Vector();
    this.businessVector.add(businessEntity);
  }

  /**
   *
   */
  public void setBusinessEntityVector(Vector businesses)
  {
    this.businessVector = businesses;
  }

  /**
   *
   */
  public Vector getBusinessEntityVector()
  {
    return businessVector;
  }

  /**
   *
   */
  public void addUploadRegister(UploadRegister register)
  {
    if (this.uploadRegisterVector == null)
      this.uploadRegisterVector = new Vector();
    this.uploadRegisterVector.add(register);
  }

  /**
   *
   */
  public void setUploadRegisterVector(Vector registers)
  {
    this.uploadRegisterVector = registers;
  }

  /**
   *
   */
  public Vector getUploadRegisterVector()
  {
    return this.uploadRegisterVector;
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
    return authInfo;
  }
}
