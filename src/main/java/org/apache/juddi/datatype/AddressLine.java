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
 * The address structure is a simple list of AddressLine elements within
 * the address container. AddressLine elements contain string data with a
 * suggested line length of max. 40 chars.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AddressLine implements RegistryObject
{
  String lineValue;
  String keyName;
  String keyValue;

  /**
   * Constructs a new initialized Addressline instance.
   */
  public AddressLine()
  {
  }

  /**
   * Constructs a new addressline with the given data.
   *
   * @param line The data of the addressline.
   */
  public AddressLine(String line)
  {
    this.lineValue = line;
  }

  /**
   * Constructs a new addressline with the given data.
   *
   * @param line The data of the addressline.
   * @param name ...
   * @param value ...
   */
  public AddressLine(String line,String name,String value)
  {
    this.lineValue = line;
    this.keyName = name;
    this.keyValue = value;
  }

  /**
   * Sets the data of this addressline to the given data.
   *
   * @param line The new data of this addressline.
   */
  public void setLineValue(String line)
  {
    this.lineValue = line;
  }

  /**
   * Returns the data of the addressline.
   *
   * @return The data of the addressline.
   */
  public String getLineValue()
  {
    return this.lineValue;
  }

  /**
   * Sets ...
   *
   * @param name
   */
  public void setKeyName(String name)
  {
    this.keyName = name;
  }

  /**
   * Returns ...
   *
   * @return the key name
   */
  public String getKeyName()
  {
    return this.keyName;
  }

  /**
   * Sets ...
   *
   * @param value
   */
  public void setKeyValue(String value)
  {
    this.keyValue = value;
  }

  /**
   * Returns ...
   *
   * @return the key value
   */
  public String getKeyValue()
  {
    return this.keyValue;
  }
}