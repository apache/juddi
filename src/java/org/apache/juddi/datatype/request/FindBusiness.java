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
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;

/**
 * Used to locate information about one or more businesses. Returns a
 * businessList message that matches the conditions specified.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindBusiness implements RegistryObject,Inquiry
{
  String generic;
  Vector nameVector;
  IdentifierBag identifierBag;
  CategoryBag categoryBag;
  TModelBag tModelBag;
  DiscoveryURLs discoveryURLs;
  FindQualifiers findQualifiers;
  int maxRows;

  /**
   * Constructs a new empty find_business request.
   */
  public FindBusiness()
  {
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
   * name. The businessList return contains BusinessInfo objects for businesses whose
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
   * Sets the name argument of the search to the given name. This value is a partial
   * name. The businessList return contains BusinessInfo objects for businesses whose
   * name matches the value passed (leftmost match).
   *
   * @param names The name argument of the search.
   */
  public void setNameVector(Vector names)
  {
    this.nameVector = names;
  }

  /**
   * Returns the name argument of the search. Null is returned if the name
   * argument for this search has not been specified.
   *
   * @return The vector of Name argument of the search, or null if the argument has not
   *  been specified.
   */
  public Vector getNameVector()
  {
    return nameVector;
  }

  /**
   * Adds a business identifier reference to the identifierBag argument of this search.
   *
   * @param ref The business identifer reference to add.
   */
  public void addIdentifier(KeyedReference ref)
  {
    identifierBag.addKeyedReference(ref);
  }

  /**
   * Adds a collection of business identifier references to the bag argument
   * of this search.
   *
   * @param bag The references to add.
   */
  public void setIdentifierBag(IdentifierBag bag)
  {
    this.identifierBag = bag;
  }

  /**
   * Returns the list of business identifier references as an enumeration. If the
   * identifierBag has not been specified, an empty list is returned.
   *
   * @return The list of business identifier references.
   */
  public IdentifierBag getIdentifierBag()
  {
    return identifierBag;
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
   * Adds a collection of category references to the bag argument of this search.
   *
   * @param bag of KeyedReferences.
   */
  public void setCategoryBag(CategoryBag bag)
  {
    this.categoryBag = bag;
  }

  /**
   * Returns the list of category references as an enumeration. If the categoryBag has
   * not been specified, an empty list is returned.
   *
   * @return The list of category references.
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
   * @param key The tModel to add to the tModelBag.
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
   * Adds a collection of category references to the bag argument of this search.
   *
   * @param bag of TModelKeys.
   */
  public void setTModelBag(TModelBag bag)
  {
    this.tModelBag = bag;
  }

  /**
   * Adds a discoveryURL to the discoveryURLs argument of this search.
   *
   * @param url The discoveryURL to add to the discoveryURLs argument.
   */
  public void addDiscoveryURL(DiscoveryURL url)
  {
    // just return if the DiscoveryURL parameter is null (nothing to add)
    if (url == null)
      return;

    // make sure the DiscoveryURLs has been initialized
    if (this.discoveryURLs == null)
      this.discoveryURLs = new DiscoveryURLs();

    this.discoveryURLs.addDiscoveryURL(url);
  }

  /**
   * Sets the collection of discoveryURLs to the new discoveryURLs argument
   *
   * @param urls The new collection of discoveryURLs.
   */
  public void setDiscoveryURLs(DiscoveryURLs urls)
  {
    this.discoveryURLs = urls;
  }

  /**
   * Returns the list of discoveryURLs as an enumeration. If the discoveryURLs argument has
   * not been specified, an empty list is returned.
   *
   * @return The list of discoveryURLs.
   */
  public DiscoveryURLs getDiscoveryURLs()
  {
    return discoveryURLs;
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
    return findQualifiers;
  }
}