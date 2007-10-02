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

import org.apache.juddi.datatype.RegistryObject;

/**
 * "Used to request an authentication token from an Operator Site.
 *  Authentication tokens are required to use all other APIs defined
 *  in the publishers API.  This server serves as the program's
 *  equivalent of a login request."
 *
 *  <get_authToken generic="2.0" xmlns="urn:uddi-org:api_v2"
 *    userID="someLoginName"
 *    cred="someCredential" />
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetAuthToken implements RegistryObject,SecurityPolicy
{
  String generic;
  String userID;
  String credential;

  /**
   *
   */
  public GetAuthToken()
  {
  }

  /**
   *
   */
  public GetAuthToken(String idValue,String credValue)
  {
    this.userID = idValue;
    this.credential = credValue;
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
  public void setUserID(String idValue)
  {
    userID = idValue;
  }

  /**
   *
   */
  public String getUserID()
  {
    return userID;
  }

  /**
   *
   */
  public void setCredential(String credValue)
  {
    credential = credValue;
  }

  /**
   *
   */
  public String getCredential()
  {
    return credential;
  }
}