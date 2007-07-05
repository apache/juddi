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
public class PersonName implements RegistryObject
{
  String nameValue;

  /**
   * Construct a new initialized PersonName instance.
   */
  public PersonName()
  {
  }

  /**
   * Construct a new PersonName with a given name value.
   *
   * @param nameValue The value of the new PersonName.
   */
  public PersonName(String nameValue)
  {
    setValue(nameValue);
  }

  /**
   * Sets the value of this PersonName to the new name value.
   *
   * @param newValue The new name value for this PersonName.
   */
  public void setValue(String newValue)
  {
    this.nameValue = newValue;
  }

  /**
   * Returns the key value of this PersonName.
   *
   * @return The key value of this PersonName.
   */
  public String getValue()
  {
    return this.nameValue;
  }
}