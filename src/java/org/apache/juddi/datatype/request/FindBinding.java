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

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;

/**
 * "Used to locate specific bindings within a registered
 *  businessService. Returns a bindingDetail message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindBinding implements RegistryObject,Inquiry
{
  String serviceKey;
  String generic;
  CategoryBag categoryBag;
  TModelBag tModelBag;
  FindQualifiers findQualifiers;
  int maxRows;

  /**
   *
   */
  public FindBinding()
  {
  }


  /**
   * Construct a new find_binding request. The bindings searched for must be part of the
   * businessService with the given serviceKey.
   *
   * @param serviceKey The key of the businessService to search in.
   * @throws NullPointerException If the given key is null.
   */
  public FindBinding(String serviceKey)
  {
    setServiceKey(serviceKey);
  }

  /**
   * Sets the servicekey of this find_binding request to the given key. Only bindingTemplates
   * of the referenced businessService are searched.
   *
   * @param key A reference to the businessService.
   */
  public void setServiceKey(String key)
  {
    serviceKey = key;
  }

  /**
   * Returns the servicekey of this find_binding request. Only bindingTemplates of the
   * reference businessService are searched.
   *
   * @return The servicekey of the referenced businessService.
   */
  public String getServiceKey()
  {
    return serviceKey;
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
   * lets you search for bindings that are compatible with a specific tModel pattern.
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
   * Sets the TModelBag value
   *
   * @param bag The new TModelBag value
   */
  public void setTModelBag(TModelBag bag)
  {
    this.tModelBag = bag;
  }

  /**
   * Returns the TModelBag value.
   *
   * @return The current TModelBag value
   */
  public TModelBag getTModelBag()
  {
    return tModelBag;
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