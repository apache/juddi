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