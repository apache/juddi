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
import org.apache.juddi.datatype.tmodel.TModel;

/**
 * "Used to register or update complete information about a tModel."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveTModel implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector tModelVector;
  Vector uploadRegisterVector;

  /**
   *
   */
  public SaveTModel()
  {
  }

  /**
   *
   */
  public SaveTModel(AuthInfo authInfo,TModel tModel)
  {
    setAuthInfo(authInfo);
    addTModel(tModel);
  }

  /**
   *
   */
  public SaveTModel(AuthInfo authInfo,Vector tModels)
  {
    setAuthInfo(authInfo);
    setTModelVector(tModels);
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
   * addTModel
   */
  public void addTModel(TModel tModel)
  {
    if (this.tModelVector == null)
      this.tModelVector = new Vector();
    this.tModelVector.add(tModel);
  }

  /**
   *
   */
  public void setTModelVector(Vector tModels)
  {
    this.tModelVector = tModels;
  }

  /**
   * getTModels
   */
  public Vector getTModelVector()
  {
    return this.tModelVector;
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
    if (authInfo == null)
      throw new NullPointerException("The authentication info of a publish message cannot be null!");

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
