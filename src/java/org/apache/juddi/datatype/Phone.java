/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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