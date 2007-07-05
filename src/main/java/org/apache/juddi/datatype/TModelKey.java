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
 * Used in BusinessEntity as the Name of the BusinessEntity, in BusinessService
 * as the name of the BusinessService and in TModel as the name of the TModel.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelKey implements RegistryObject
{
  String keyValue;

  /**
   * Construct a new initialized name instance.
   */
  public TModelKey()
  {
  }

  /**
   * Construct a new name with a given name.
   *
   * @param keyValue The name of the new name-object.
   */
  public TModelKey(String keyValue)
  {
    setValue(keyValue);
  }

  /**
   * Sets the name of this name-object to the new given name.
   *
   * @param newValue The new name for this name-object.
   */
  public void setValue(String newValue)
  {
    this.keyValue = newValue;
  }

  /**
   * Returns the name of this name-object.
   *
   * @return The name of this name-object.
   */
  public String getValue()
  {
    return this.keyValue;
  }
}