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
package org.apache.juddi.datatype.publisher;

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Publisher implements RegistryObject
{
  private String publisherID;
  private String nameValue;
  private String firstName;
  private String lastName;
  private String middleInit;
  private String workPhone;
  private String mobilePhone;
  private String pager;
  private String emailAddress;
  private boolean admin;
  private boolean enabled;

  /**
   *
   */
  public Publisher()
  {
  }

  /**
   *
   */
  public Publisher(String pubID,String name)
  {
    this.publisherID = pubID;
    this.nameValue = name;
  }

  /**
   *
   */
  public Publisher(String pubID,String name,boolean adminValue)
  {
    this(pubID,name);
    this.admin = adminValue;
  }

  /**
   *
   */
  public void setPublisherID(String pubID)
  {
    this.publisherID = pubID;
  }

  /**
   *
   */
  public String getPublisherID()
  {
    return this.publisherID;
  }

  /**
   * Sets the name of this Publisher to the given name.
   *
   * @param name The new name of this Publisher.
   */
  public void setName(String name)
  {
    this.nameValue = name;
  }

  /**
   * Returns the name of this Publisher as a String.
   *
   * @return The name of this Publisher.
   */
  public String getName()
  {
    return this.nameValue;
  }

  /**
    * Sets the name of this Publisher to the given name.
    *
    * @param name The new name of this Publisher.
    */
  public void setName(Name name)
  {
    if (name != null)
      this.nameValue = name.getValue();
    else
      this.nameValue = null;
  }

  /**
   *
   */
  public void setFirstName(String name)
  {
    this.firstName = name;
  }

  /**
   *
   */
  public String getFirstName()
  {
    return this.firstName;
  }

  /**
   *
   */
  public void setLastName(String name)
  {
    this.lastName = name;
  }

  /**
   *
   */
  public String getLastName()
  {
    return this.lastName;
  }

  /**
   *
   */
  public void setMiddleInit(String init)
  {
    this.middleInit = init;
  }

  /**
   *
   */
  public String getMiddleInit()
  {
    return this.middleInit;
  }

  /**
   *
   */
  public void setWorkPhone(String phone)
  {
    this.workPhone = phone;
  }

  /**
   *
   */
  public String getWorkPhone()
  {
    return this.workPhone;
  }

  /**
   *
   */
  public void setMobilePhone(String phone)
  {
    this.mobilePhone = phone;
  }

  /**
   *
   */
  public String getMobilePhone()
  {
    return this.mobilePhone;
  }

  /**
   *
   */
  public void setPager(String pager)
  {
    this.pager = pager;
  }

  /**
   *
   */
  public String getPager()
  {
    return this.pager;
  }

  /**
   *
   */
  public void setEmailAddress(String email)
  {
    this.emailAddress = email;
  }

  /**
   *
   */
  public String getEmailAddress()
  {
    return this.emailAddress;
  }

  /**
   *
   */
  public void setAdmin(boolean adminValue)
  {
    this.admin = adminValue;
  }

  /**
   *
   */
  public void setAdminValue(String adminValue)
  {
    if (adminValue == null)
      this.admin = false;
    else
      this.admin = (adminValue.equalsIgnoreCase("true"));
  }

  /**
   *
   */
  public boolean isAdmin()
  {
    return this.admin;
  }


  /**
   *
   */
  public void setEnabled(boolean enabledValue)
  {
    this.enabled = enabledValue;
  }

  /**
   *
   */
  public void setEnabledValue(String enabledValue)
  {
    if (enabledValue == null)
      this.enabled = false;
    else
      this.enabled = (enabledValue.equalsIgnoreCase("true"));
  }

  /**
   *
   */
  public boolean isEnabled()
  {
    return this.enabled;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  // test-driver
  public static void main(String[] args)
  {
  }
}