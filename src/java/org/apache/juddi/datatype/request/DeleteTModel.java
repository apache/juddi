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
 * "Used to delete registered information about a tModel.  If there
 *  are any references to a tModel when this call is made, the tModel
 *  will be marked deleted instead of being physically removed."
 *
 *  <delete_tModel generic="2.0" xmlns="urn:uddi-org:api_v2" >
 *    <authInfo/>
 *    <tModelKey/> [<tModelKey/> �]
 *  </delete_tModel>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DeleteTModel implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector tModelKeyVector;

  /**
   *
   */
  public DeleteTModel()
  {
  }

  /**
   *
   */
  public DeleteTModel(AuthInfo authInfo,String tModelKey)
  {
    setAuthInfo(authInfo);
    addTModelKey(tModelKey);
  }

  /**
   *
   */
  public DeleteTModel(AuthInfo authInfo,Vector tModelKeys)
  {
    setAuthInfo(authInfo);
    setTModelKeyVector(tModelKeys);
  }

  /**
   *
   */
  public void addTModelKey(TModelKey tModelKey)
  {
    if ((tModelKey != null) && (tModelKey.getValue() != null))
      addTModelKey(tModelKey.getValue());
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
  public void addTModelKey(String tModelKey)
  {
    if (this.tModelKeyVector == null)
      this.tModelKeyVector = new Vector();
    this.tModelKeyVector.add(tModelKey);
  }

  /**
   *
   */
  public void setTModelKeyVector(Vector tModelKeys)
  {
    this.tModelKeyVector = tModelKeys;
  }

  /**
   *
   */
  public Vector getTModelKeyVector()
  {
    return this.tModelKeyVector;
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