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
 * Holds email address, can be adorned with an optinal useType attribute. If more than
 * one Email element is saved within the same contact then the useType must be supplied.
 *
 * The useType attribute is used to describe the type of the email address in freeform text.
 * Examples are "technical questions", "sales contact", etc.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Email implements RegistryObject
{
  String emailAddress;
  String useType;

  /**
   * Construct a new initialized Email instance.
   */
  public Email()
  {
  }

  /**
   * Construct a new Email with a given address.
   *
   * @param email The address of the email.
   */
  public Email(String email)
  {
    this.emailAddress = email;
  }

  /**
   * Construct a new Email with a given address and given usetype.
   *
   * @param email The address of the email.
   * @param type The usetype of the email.
   */
  public Email(String email, String type)
  {
    this.emailAddress = email;
    this.useType = type;
  }

  /**
   * Sets the emailAddress of the Email to the given address.
   *
   * @param email The new address of this email.
   */
  public void setValue(String email)
  {
    this.emailAddress = email;
  }

  /**
   * Returns the emailAddress of this Email.
   *
   * @return The emailAddress of this Email.
   */
  public String getValue()
  {
    return this.emailAddress;
  }

  /**
   * Sets the usetype of this Email to the given usetype. If the new usetype is
   * null, this Email doesn't have a usetype anymore.
   *
   * @param type The new usetype of this Email, or null if this Email doesn't
   *  have an usetype anymore.
   */
  public void setUseType(String type)
  {
    this.useType = type;
  }

  /**
   * Returns the usetype of this Email.
   *
   * @return The usetype of this Email, or null if this Email doesn't have
   *  an usetype.
   */
  public String getUseType()
  {
    return this.useType;
  }
}