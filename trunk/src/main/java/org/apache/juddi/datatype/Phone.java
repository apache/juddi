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
 * Holds telephone nbrs, can be adorned with an optinal useType attribute. If
 * more than one Phone element is saved (in a Contacts.Phone[] ) then the
 * useType must be supplied.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Phone implements RegistryObject
{
  String phoneNumber;
  String useType;

  /**
   * Construct a new initialized Phone instance.
   */
  public Phone()
  {
  }

  /**
   * Construct a new phone with a given phone-number.
   *
   * @param number The number of the new phone.
   */
  public Phone(String number)
  {
    this.phoneNumber = number;
  }

  /**
   * Construct a new phone with a given phone-number and usetype.
   *
   * @param number The number of the new phone.
   * @param useType The usetype of the new phone.
   */
  public Phone(String number, String useType)
  {
    this.phoneNumber = number;
    this.useType = useType;
  }

  /**
   * Sets the number of this Phone to the given number.
   *
   * @param number The new number of this phone.
   */
  public void setValue(String number)
  {
    this.phoneNumber = number;
  }

  /**
   * Returns the number of this Phone.
   *
   * @return The number of this Phone.
   */
  public String getValue()
  {
    return this.phoneNumber;
  }

  /**
   * Sets the UseType of this Phone to the given UseType. If this Phone
   * doesn't have a UseType anymore, then the new usetype must be null.
   *
   * @param type The new UseType of this Phone.
   */
  public void setUseType(String type)
  {
    this.useType = type;
  }

  /**
   * Returns the UseType of this Phone.
   *
   * @return The UseType of this Phone, or null if this Phone doesn't have
   * an UseType.
   */
  public String getUseType()
  {
    return this.useType;
  }
}