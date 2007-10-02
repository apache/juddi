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
package org.apache.juddi.datatype.response;

import org.apache.juddi.datatype.RegistryObject;


/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Property implements RegistryObject
{
  String name;
  String value;

  /**
   * Constructs a new initialized Property instance.
   */
  public Property()
  {
  }

  /**
   * Constructs a new Property with the given data.
   *
   * @param name The property name/key.
   * @param value The property data/value.
   */
  public Property(String name,String value)
  {
    this.name = name;
    this.value = value;
  }

  /**
   * Sets the name of this property.
   *
   * @param name The name of this property.
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Returns the name/key of the property.
   *
   * @return The name of the property.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Sets the value of this property.
   *
   * @param value The value of this property
   */
  public void setValue(String value)
  {
    this.value = value;
  }

  /**
   * Returns the value of this property.
   *
   * @return The value of this property.
   */
  public String getValue()
  {
    return this.value;
  }
}