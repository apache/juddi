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
package org.apache.juddi.datatype.service;

import java.util.Vector;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.BindingTemplates;

/**
 * "A descriptive container that is used to group a series of related
 * Web Services related to either a business process or category of
 * services." - technical whitepaper
 *
 * "descriptive information about the party who publishes information about
 * a service" - XML Structure Reference
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessService implements RegistryObject
{
  String businessKey;
  String serviceKey;
  Vector nameVector;
  Vector descrVector;
  BindingTemplates bindingTemplates;
  CategoryBag categoryBag;

  /**
   * Constructs a new initialized BusinessService instance.
   */
  public BusinessService()
  {
  }

  /**
   * Sets the String of this BusinessService. If the BusinessEntity for
   * this BusinessService has been set, this new String must be null or
   * must be the same as the BusinessEntity. If the BusinessEntity for this
   * BindingTemplate has not been set, this new String may not be null.
   *
   * @param key The new business key.
   */
  public void setBusinessKey(String key)
  {
    this.businessKey = key;
  }

  /**
   * Returns the business key of this business service. This method can return
   * null if the business entity for this business service has been set and if
   * the business key for this business service has not been set.
   *
   * @return The business key of this business service.
   */
  public String getBusinessKey()
  {
    return this.businessKey;
  }

  /**
   * Sets the service key of this business service to the given key.
   *
   * @param key The service key of this business service.
   */
  public void setServiceKey(String key)
  {
    serviceKey = key;
  }

  /**
   * Returns the service key of this business service.
   *
   * @return The service key of this business service.
   */
  public String getServiceKey()
  {
    return serviceKey;
  }

  /**
   * Add a name to this BusinessService.
   *
   * @param name The Name to add.
   */
  public void addName(Name name)
  {
    if (nameVector == null)
      nameVector = new Vector();
    nameVector.add(name);
  }

  /**
   * Adds the given description. If there was already a description with the
   * same language-code as the new description, an exception will be thrown.
   *
   * @param desc The description to add.
   */
  public void addDescription(Description desc)
  {
    if (descrVector == null)
      descrVector = new Vector();
    descrVector.add(desc);
  }

  /**
   * Returns the names of this BusinessService.
   *
   * @return The names of thes BusinessService. If this BusinessService
   *  doesn't have any names, an empty set is returned.
   */
  public Vector getNameVector()
  {
    return nameVector;
  }

  /**
   * Sets the name list to the current one. Ignores any object in the
   * collection that is not an "instanceof" the Name class.
   *
   * @param names the Names object to set
   */
  public void setNameVector(Vector names)
  {
    this.nameVector = names;
  }

  /**
   * Sets the description list to the current one. Ignores any object in the
   * collection that is not an "instanceof" the Description class.
   *
   * @param descs Descriptions object to set
   */
  public void setDescriptionVector(Vector descs)
  {
    this.descrVector = descs;
  }

  /**
   * Returns the descriptions.
   *
   * @return the descriptions object
   */
  public Vector getDescriptionVector()
  {
    return this.descrVector;
  }

  /**
   * Add a binding template to this business service.
   *
   * @param binding The binding template to add.
   */
  public void addBindingTemplate(BindingTemplate binding)
  {
    if (this.bindingTemplates == null)
      this.bindingTemplates = new BindingTemplates();
    this.bindingTemplates.addBindingTemplate(binding);
  }

  /**
   * Returns the binding templates of this business service.
   *
   * @return The binding templates of this business service.
   */
  public BindingTemplates getBindingTemplates()
  {
    return this.bindingTemplates;
  }

  /**
   * Sets the binding templates of this business service.
   */
  public void setBindingTemplates(BindingTemplates bindings)
  {
    this.bindingTemplates = bindings;
  }

  /**
   * Add a category to the categorybag of this business service.
   * @param keyref The category to add.
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
   * Returns the categorybag of this business service. If this business
   * service doesn't contain any categories, an empty enumeration is
   * returned.
   *
   * @return The categorybag of this business service.
   */
  public CategoryBag getCategoryBag()
  {
    return this.categoryBag;
  }

  /**
   * Set the categorybag of this business service to the given one.
   *
   * @param bag The new categorybag.
   */
  public void setCategoryBag(CategoryBag bag)
  {
    this.categoryBag = bag;
  }
}