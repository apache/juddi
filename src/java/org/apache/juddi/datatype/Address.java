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

import java.util.Vector;

/**
 * Represents a postal address. Essentially holds a simple set of AddressLines,
 * but can be adorned with an optional useType attribute and sortcode. The
 * useType attribute is used to describe the type of the address in
 * freeform text. Examples are "headquarters", "billing department", etc.
 * The sortCode values are not significant, but can be used by user-interfaces
 * that present contact information in some ordered fashion, thereby using the
 * sortCode values.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Address implements RegistryObject
{
  String useType;
  String sortCode;
  String tModelKey;
  Vector addressLineVector;

  /**
   * Constructs a new Address with no address-lines and no useType or sortCode
   * attribute.
   */
  public Address()
  {
  }

  /**
   * Constructs a new Address with no address-lines, but with the given useType
   * and sortCode attributes.
   *
   * @param type The usetype of the new address, or null if the address
   *  doesn't have a usetype.
   * @param sort The sortcode of the new address, or null if the address
   *  doesn't have a sortcode.
   */
  public Address(String type,String sort)
  {
    this.useType = type;
    this.sortCode = sort;
  }

  /**
   * Sets the usetype of this address to the given usetype. If the new usetype
   * is null, this address doesn't have a usetype anymore.
   *
   * @param type The new usetype of this address, or null if the address
   *  doesn't have an usetype anymore.
   */
  public void setUseType(String type)
  {
    this.useType = type;
  }

  /**
   * Returns the usetype of this address.
   *
   * @return The usetype of this address, or null if this address doesn't have
   *  an usetype.
   */
  public String getUseType()
  {
    return this.useType;
  }

  /**
   * Sets the sortcode of this address to the given sortcode. If the new
   * sortcode is null, this address doesn't have a sortcode anymore.
   *
   * @param sort The new sortcode of this address, or null if the address
   *  doesn't have a sortcode anymore.
   */
  public void setSortCode(String sort)
  {
    this.sortCode = sort;
  }

  /**
   * Returns the sortcode of this address.
   *
   * @return The sortcode of this address, or null if the address doesn't
   *  have a sortcode.
   */
  public String getSortCode()
  {
    return this.sortCode;
  }

  /**
   * Sets the key of this tModel to the given key.
   *
   * @param key The new key of this tModel.
   */
  public void setTModelKey(String key)
  {
    this.tModelKey = key;
  }

  /**
   * Returns the String of this Address.
   *
   * @return The key of this tModel.
   */
  public String getTModelKey()
  {
    return this.tModelKey;
  }

  /**
   * Add a new addressline to this address. The addressline is added at the
   * end of the already existing set of addresslines.
   *
   * @param line The addressline to be added to this address.
   */
  public void addAddressLine(AddressLine line)
  {
    if (this.addressLineVector == null)
      this.addressLineVector = new Vector();
    this.addressLineVector.add(line);
  }

  /**
   * Add a collection of new addresslines to this address. The new
   * addresslines are added at the end of the set of the already existing
   * addresslines.
   *
   * @param lines The collection of addresslines to add.
   */
  public void setAddressLineVector(Vector lines)
  {
    if (this.addressLineVector == null)
      this.addressLineVector = new Vector();
    this.addressLineVector = lines;
  }

  /**
   * Returns the addresslines of this address.
   *
   * @return The addresslines of this address. If this address doesn't have
   *  any addresslines, an empty enumeration of addresslines is returned.
   */
  public Vector getAddressLineVector()
  {
    return this.addressLineVector;
  }
}