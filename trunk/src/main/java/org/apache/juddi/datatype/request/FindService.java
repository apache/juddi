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
package org.apache.juddi.datatype.request;

import java.util.Vector;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;

/**
 * "Used to locate specific services within a registered
 *  businessEntity. Return a serviceList message." From the
 *  XML spec (API, p18) it appears that the name, categoryBag,
 *  and tModelBag arguments are mutually exclusive.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindService implements RegistryObject,Inquiry
{
  String generic;
  String businessKey;
  Vector nameVector;
  CategoryBag categoryBag;
  TModelBag tModelBag;
  FindQualifiers findQualifiers;
  int maxRows;

  /**
   * Constructs a new empty find_service request.
   */
  public FindService()
  {
  }

  /**
   * Construct a new find_service request. The services searched for must be part of the
   * businessEntity with the given businessKey.
   *
   * @param businessKey The key of the businessEntity to search in.
   * @throws NullPointerException If the given key is null.
   */
  public FindService(String businessKey)
  {
    setBusinessKey(businessKey);
  }

  /**
   * Sets the businesskey of this find_service request to the given key. Only services of the
   * referenced businessEntity are searched.
   *
   * @param key A reference to the businessEntity.
   * @throws NullPointerException If the given key is null.
   */
  public void setBusinessKey(String key)
  {
    businessKey = key;
  }

  /**
  * Returns the businesskey of this find_service request. Only services of the referenced
  * businessEntity are searched.
  *
  * @return The businesskey of the referenced businessEntity.
  */
  public String getBusinessKey()
  {
    return businessKey;
  }

  /**
   *
   * @param genericValue
   */
  public void setGeneric(String genericValue)
  {
    this.generic = genericValue;
  }

  /**
   *
   * @return String UDDI request's generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   * Sets the name argument of the search to the given name. This value is a partial
   * name. The serviceList return contains ServiceInfo objects for services whose
   * name matches the value passed (leftmost match).
   *
   * @param nameValue The name argument of the search.
   */
  public void addName(Name nameValue)
  {
    if (this.nameVector == null)
      this.nameVector = new Vector();
    this.nameVector.add(nameValue);
  }

  /**
   * Returns the name argument of the search. Null is returned if the name
   * argument for this search has not been specified.
   *
   * @return The name argument of the search, or null if the argument has not been specified.
   */
  public Vector getNameVector()
  {
    return this.nameVector;
  }

  /**
   * Sets the name argument of the search to the given name. This value is a partial
   * name. The serviceList return contains ServiceInfo objects for services whose
   * name matches the value passed (leftmost match).
   *
   * @param names The name argument of the search.
   */
  public void setNameVector(Vector names)
  {
    this.nameVector = names;
  }

  /**
   * Adds a category reference to the categoryBag argument of this search.
   *
   * @param ref The category reference to add.
   */
  public void addCategory(KeyedReference ref)
  {
    // just return if the KeyedReference parameter is null (nothing to add)
    if (ref == null)
      return;

    // make sure the CategoryBag has been initialized
    if (this.categoryBag == null)
      this.categoryBag = new CategoryBag();

    this.categoryBag.addKeyedReference(ref);
  }

  /**
   * Sets the CategoryBag value
   *
   * @param bag The new CategoryBag
   */
  public void setCategoryBag(CategoryBag bag)
  {
    categoryBag = bag;
  }

  /**
   * Returns the CategoryBag value
   *
   * @return The CategoryBag value
   */
  public CategoryBag getCategoryBag()
  {
    return this.categoryBag;
  }

  /**
   * Adds a tModel reference to the tModelBag argument of this search. This tModelBag argument
   * lets you search for businesses that have bindings that are compatible with a specific
   * tModel pattern.
   *
   * @param key The key of the tModel to add to the tModelBag argument.
   */
  public void addTModelKey(String key)
  {
    // just return if the TModel key parameter is null (nothing to add)
    if (key == null)
      return;

    // make sure the TModelBag has been initialized
    if (this.tModelBag == null)
      this.tModelBag = new TModelBag();

    this.tModelBag.addTModelKey(key);
  }

  /**
   * Sets the TModelBag value.
   *
   * @param bag the new TModelBag.
   */
  public void setTModelBag(TModelBag bag)
  {
    this.tModelBag = bag;
  }

  /**
   * Returns the list of tModel references as an enumeration. If the tModelBag has not
   * been specified, an empty list is returned.
   *
   * @return The list of the tModel references.
   */
  public TModelBag getTModelBag()
  {
    return this.tModelBag;
  }

  /**
   *
   */
  public int getMaxRows()
  {
    return maxRows;
  }

  /**
   *
   */
  public void setMaxRows(int maxRows)
  {
    this.maxRows = maxRows;
  }

  /**
   *
   */
  public void setMaxRows(String maxRows)
  {
    setMaxRows(Integer.parseInt(maxRows));
  }

  /**
   *
   */
  public void addFindQualifier(FindQualifier findQualifier)
  {
    if (this.findQualifiers == null)
      this.findQualifiers = new FindQualifiers();
    this.findQualifiers.addFindQualifier(findQualifier);
  }

  /**
   *
   */
  public void setFindQualifiers(FindQualifiers findQualifiers)
  {
    this.findQualifiers = findQualifiers;
  }

  /**
   *
   */
  public FindQualifiers getFindQualifiers()
  {
    return this.findQualifiers;
  }
}