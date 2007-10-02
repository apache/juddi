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