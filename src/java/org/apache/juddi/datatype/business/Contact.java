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
package org.apache.juddi.datatype.business;

import java.util.Vector;

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.PersonName;
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.datatype.RegistryObject;

/**
 * A businessEntity has a "contacts" attribute which is a collection of these.
 * Holds the "human contact" information.
 *
 * @author Steve Viens (sviens@apache.org)
 */
//public class Contact extends Person implements RegistryObject
public class Contact implements RegistryObject
{
  String useType;
  Vector descVector;
  String personName;
  Vector phoneVector;
  Vector emailAddrVector;
  Vector addressVector;

  /**
   * Construct a new initialized Contact instance.
   */
  public Contact()
  {
  }

  /**
   * Construct a new contact with a given name. The given name is the name of
   * the person or the name of the persons job role, e.g. "administrator",
   * "webmaster", etc.
   *
   * @param name The name of the contact.
   */
  public Contact(String name)
  {
    this.setPersonNameValue(name);
  }

  /**
   * Construct a new contact with a given UseType and Name. The given name is
   * the name of the person or the name of the persons job role, e.g.
   * "administrator", "webmaster", etc. The UseType is used to describe
   * the contact in freeform text. Examples are: "technical questions", "sales
   * contact", etc.
   *
   * @param name The UseType of the contact.
   * @param name The Name of the contact.
   */
  public Contact(String name,String type)
  {
    this(name);
    this.useType = type;
  }

  /**
    * Add an description to this contact.
    *
    * @param description The description to add to this contact.
    */
   public void addDescription(Description description)
   {
     if (this.descVector == null)
       this.descVector = new Vector();
     this.descVector.add(description);
   }

   /**
    * Set the descriptions of this contact replacing the old
    *
    * @param descriptions
    */
   public void setDescriptionVector(Vector descriptions)
   {
     this.descVector = descriptions;
   }

   /**
    * Returns the descriptions of this contact.
    *
    * @return The descriptions of this contact. If this contact has no
    * descriptions, null is returned.
    */
   public Vector getDescriptionVector()
   {
     return this.descVector;
   }

  /**
   * Sets the usetype of this contact to the given usetype. The usetype is
   * used to describe the contact in freeform text. Examples are: "technical
   * questions", "sales contact", etc.
   *
   * @param type The usetype of this contact.
   */
  public void setUseType(String type)
  {
    this.useType = type;
  }

  /**
   * Returns the usetype of this contact. The usetype is used to describe
   * the contact in freeform text. Examples are: "technical questions",
   * "sales contact", etc.
   *
   * @return The usetype of this contact, or null if this contact doesn't
   *  have a usetype.
   */
  public String getUseType()
  {
    return useType;
  }

  /**
   * Returns the name of this person. The name can be the name of this
   * person or it can be the role of this person. Examples are
   * "administrator", "webmaster", etc.
   *
   * @return The name of this person.
   */
  public String getPersonNameValue()
  {
    return personName;
  }

  /**
   * Returns the name of this person. The name can be the name of this
   * person or it can be the role of this person. Examples are
   * "administrator", "webmaster", etc.
   *
   * @return The name of this person as a PersonName.
   */
  public PersonName getPersonName()
  {
    if (this.personName == null)
      return null;
    else
      return new PersonName(this.personName);
  }

  /**
   * Sets the name of this person to the given name. The name can be
   * the name of the person or it can be the role of this person, e.g.
   * "administrator", "webmaster", etc.
   *
   * @param name The new name of this person.
   */
  public void setPersonNameValue(String name)
  {
    this.personName = name;
  }

  /**
   * Sets the name of this person to the given name. The name can be
   * the name of the person or it can be the role of this person, e.g.
   * "administrator", "webmaster", etc.
   *
   * @param personName The new name of this person.
   */
  public void setPersonName(PersonName personName)
  {
    if (personName != null)
      this.personName = personName.getValue();
    else
      this.personName = null;
  }

  /**
   * Add an address to this person.
   *
   * @param address The address to add to this person.
   */
  public void addAddress(Address address)
  {
    // just return if the Address parameter is null (nothing to add)
    if (address == null)
      return;

    if (this.addressVector == null)
      this.addressVector = new Vector();
    this.addressVector.add(address);
  }

  /**
   * Set the addresses of this person replacing the old
   *
   * @param adds
   */
  public void setAddressVector(Vector adds)
  {
    this.addressVector = adds;
  }

  /**
   * Returns the addresses of this person.
   *
   * @return The addresses of this person. If this person
   *  has no addresses, an empty enumeration is returned.
   */
  public Vector getAddressVector()
  {
    return this.addressVector;
  }

  /**
   * Add a phone to this person.
   *
   * @param phone The phone to add to this person.
   */
  public void addPhone(Phone phone)
  {
    // just return if the Phone parameter is null (nothing to add)
    if (phone == null)
      return;

    if (this.phoneVector == null)
      this.phoneVector = new Vector();
    this.phoneVector.add(phone);
  }

  /**
   * Set a collection of phone numbers to this person
   * replacing the old.
   *
   * @param phones The vector of new Phone instances to
   *  this person.
   */
  public void setPhoneVector(Vector phones)
  {
    this.phoneVector = phones;
  }

  /**
   * Returns the phones of this person.
   *
   * @return The phones of this person. If this person
   *  doesn't have any phones, an empty enumeration is
   *  returned.
   */
  public Vector getPhoneVector()
  {
    return this.phoneVector;
  }

  /**
   * Add an emailaddress to this person.
   *
   * @param email The email address to add to this person.
   */
  public void addEmail(Email email)
  {
    // just return if the Email parameter is null (nothing to add)
    if (email == null)
      return;

    if (this.emailAddrVector == null)
      this.emailAddrVector = new Vector();
    this.emailAddrVector.add(email);
  }

  /**
   * Sets a collection of email-addresses to this person
   * replacing the old one
   *
   * @param emailAddresses
   */
  public void setEmailVector(Vector emailAddresses)
  {
    this.emailAddrVector = emailAddresses;
  }

  /**
   * Returns the email addresses of this person.
   *
   * @return The emailaddresses of this person. If this person
   *  doesn't have any emailaddresses, an empty enumeration is
   *  returned.
   */
  public Vector getEmailVector()
  {
    return this.emailAddrVector;
  }
}