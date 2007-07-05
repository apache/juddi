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
package org.apache.juddi.datatype;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessKey implements RegistryObject
{
  String keyValue;

  /**
   * Construct a new initialized businessKey instance.
   */
  public BusinessKey()
  {
  }

  /**
   * Construct a new BusinessKey with a given key value.
   *
   * @param keyValue The BusinessKey of the new key value.
   */
  public BusinessKey(String keyValue)
  {
    setValue(keyValue);
  }

  /**
   * Sets the value of this BusinessKey to the new key value.
   *
   * @param newValue The new key value for this BusinessKey.
   */
  public void setValue(String newValue)
  {
    this.keyValue = newValue;
  }

  /**
   * Returns the key value of this BusinessKey.
   *
   * @return The key value of this BusinessKey.
   */
  public String getValue()
  {
    return this.keyValue;
  }
}