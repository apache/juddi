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
package org.apache.juddi.datatype.subscription;

import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SubscriptionKey implements RegistryObject
{
  String keyValue;

  /**
   * Construct a new initialized instance.
   */
  public SubscriptionKey()
  {
  }

  /**
   * Construct a new instance with a given key value.
   *
   * @param keyValue The value of the new key value.
   */
  public SubscriptionKey(String keyValue)
  {
    setValue(keyValue);
  }

  /**
   * Sets the value of this instance to the new key value.
   *
   * @param newValue The new key value.
   */
  public void setValue(String newValue)
  {
    this.keyValue = newValue;
  }

  /**
   * Returns the key value of this instance.
   *
   * @return The key value of this instance.
   */
  public String getValue()
  {
    return this.keyValue;
  }
}