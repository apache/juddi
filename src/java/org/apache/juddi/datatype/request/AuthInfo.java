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
 * Authentication info is used in calls to the Publish API, the content of an
 * AuthToken. It contains an access token that is to be passed back in all of
 * the publisher API messages that change data.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AuthInfo implements RegistryObject
{
  String value;

  /**
   * Constructs a new initialized authentication info.
   */
  public AuthInfo()
  {
  }

  /**
   * Constructs a new authentication info with the given access token.
   * @param auth The access token of the new authentication info.
   */
  public AuthInfo(String auth)
  {
    this.value = auth;
  }

  /**
   * Sets the access token of this authentication info.
   * @param newValue The new value of the access token.
   */
  public void setValue(String newValue)
  {
    this.value = newValue;
  }

  /**
   * Returns the value of the access token of this authentication info.
   * @return The value of the access token of this authentication info.
   */
  public String getValue()
  {
    return this.value;
  }
}