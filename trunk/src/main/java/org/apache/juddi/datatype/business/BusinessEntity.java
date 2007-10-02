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
package org.apache.juddi.datatype.business;

import java.util.Vector;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;

/**
 * "Top-level information manager for all of the information about a
 * particular set of information related to a business unit"
 * - technical whitepaper
 *
 * "Information about the party who publishes information about a service"
 * - XML Structure Reference
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessEntity implements RegistryObject
{
  String businessKey;
  String authorizedName;
  String operator;
  DiscoveryURLs discoveryURLs;
  Vector nameVector;
  Vector descVector;
  Contacts contacts;
  BusinessServices businessServices;
  IdentifierBag identifierBag;
  CategoryBag categoryBag;

  /**
   * Constructs a new BusinessEntity instance.
   */
  public BusinessEntity()
  {
  }

  /**
   * Sets the business key of this business entity to the given key.
   * @param key The new key of this business entity.
   */
  public void setBusinessKey(String key)
  {
    this.businessKey = key;
  }

  /**
   * Returns the business key of this business entity.
   * @return The business key of this business entity.
   */
  public String getBusinessKey()
  {
    return this.businessKey;
  }

  /**
   * Sets the authorized name of this business entity to the given name.
   * @param name The new authorized name of this business entity.
   */
  public void setAuthorizedName(String name)
  {
    this.authorizedName = name;
  }

  /**
   * Returns the authorized name of this business entity.
   * @return The authorized name of this business entity, or null if
   *  this business entity doesn't have an authorized name.
   */
  public String getAuthorizedName()
  {
    return this.authorizedName;
  }

  /**
   * Sets the operator of this business entity to the given operator.
   * @param operator The new operator of this business entity.
   */
  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  /**
   * Returns the operator of this business entity.
   * @return The operator of this business entity, or null if this business entity
   * doesn't have an operator.
   */
  public String getOperator()
  {
    return this.operator;
  }

  /**
   * Adds a new discoveryURL to this business enitity's set of discoverURLs.
   * The discoveryURL is added to the end of the set. When a businessEntity is
   * saved a new discoveryURL is generated automatically, eg,
   * http://www.some-operator.com?businessKey=BE3D2F08-CEB3-11D3-849F-0050DA1803C0
   * Each time the businessEntity is saved this set is augmented with a newly
   * generated discoveryURL.
   *
   * @param url The discoveryURL to add.
   */
  public void addDiscoveryURL(DiscoveryURL url)
  {
    // just return if the DiscoveryURL parameter is null (nothing to add)
    if (url == null)
      return;

    // make sure DiscoveryURLs has been initialized
    if (this.discoveryURLs == null)
      this.discoveryURLs = new DiscoveryURLs();

    discoveryURLs.addDiscoveryURL(url);
  }

  /**
   * Sets the collection of discoveryURLs to this business entity.
   * @param urls The set of discoveryURLs to add.
   */
  public void setDiscoveryURLs(DiscoveryURLs urls)
  {
    this.discoveryURLs = urls;
  }

  /**
   * Returns the set of discoveryURLs used for finding this business entity.
   *
   * @return The set of discoveryURLs. If this business entity doesn't
   *  have a any discoveryURL, an empty enumeration is returned.
   */
  public DiscoveryURLs getDiscoveryURLs()
  {
    return this.discoveryURLs;
  }

  /**
   * Adds the given description.
   *
   * @param descr The description to add.
   */
  public void addDescription(Description descr)
  {
    // just return if the Description parameter is null (nothing to add)
    if (descr == null)
      return;

    if (this.descVector == null)
      this.descVector = new Vector();
    this.descVector.add(descr);
  }

  /**
   * Sets the description list to the current one. Ignores any object in the
   * collection that is not an "instanceof" the Description class.
   *
   * @param descr Collection of Description objects to set
   */
  public void setDescriptionVector(Vector descr)
  {
    this.descVector = descr;
  }

  /**
   * Returns the descriptions.
   *
   * @return the descriptions. If the aren't any descriptions, an empty
   *  enumeration is returned.
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   * Add a contact to this business entity.
   *
   * @param contact The contact to add.
   */
  public void addContact(Contact contact)
  {
    // just return if contact is null (nothing to add)
    if (contact == null)
      return;

    // make sure Contacts has been initialized
    if (this.contacts == null)
      this.contacts = new Contacts();
    this.contacts.addContact(contact);
  }

  /**
   * Set the set of contacts of this business entity to the given set.
   *
   * @param contacts The new set of contacts.
   */
  public void setContacts(Contacts contacts)
  {
    this.contacts = contacts;
  }

  /**
   * Returns the contacts of this business entity.
   *
   * @return The contacts of thes business entity. If this business
   *  entity doesn't have any contacts, an empty set is returned.
   */
  public Contacts getContacts()
  {
    return this.contacts;
  }


  /**
   * Add a name to this business entity.
   *
   * @param name The Name to add.
   */
  public void addName(Name name)
  {
    // just return if name is null (nothing to add)
    if (name == null)
      return;

    if (this.nameVector == null)
      this.nameVector = new Vector();
    this.nameVector.add(name);
  }

  /**
   * Set the set of names of this business entity to the given set.
   *
   * @param names The new set of contacts.
   */
  public void setNameVector(Vector names)
  {
    this.nameVector = names;
  }

  /**
   * Returns the names of this business entity.
   *
   * @return The names of thes business entity. If this business entity
   *  doesn't have any names, an empty set is returned.
   */
  public Vector getNameVector()
  {
    return this.nameVector;
  }

  /**
   * Add a business service to this business entity.
   *
   * @param service The service to add.
   */
  public void addBusinessService(BusinessService service)
  {
    // just return if service is null (nothing to add)
    if (service == null)
      return;

    // make sure the BusinessServices has been initialized
    if (this.businessServices == null)
      this.businessServices = new BusinessServices();
    this.businessServices.addBusinessService(service);
  }

  /**
   * Sets business services to this business services list.
   * @param services The set of business services to add.
   */
  public void setBusinessServices(BusinessServices services)
  {
    this.businessServices = services;
  }

  /**
   * Returns the business services of this business entity.
   *
   * @return The business services of this business entity. If this
   *  business entity doesn't have any services, an empty enumeration is
   *  returned.
   */
  public BusinessServices getBusinessServices()
  {
    return this.businessServices;
  }

  /**
   * Add an identifier to the identifierbag of this business entity.
   * @param keyref The identifier to add.
   */
  public void addIdentifier(KeyedReference keyref)
  {
    // just return if the KeyedReference parameter is null (nothing to add)
    if (keyref == null)
      return;

    // make sure IdentifierBag has been initialized
    if (this.identifierBag == null)
      this.identifierBag = new IdentifierBag();

    this.identifierBag.addKeyedReference(keyref);
  }

  /**
   * Set the identifierbag of this business entity to the given one.
   * @param bag The new identifierbag.
   */
  public void setIdentifierBag(IdentifierBag bag)
  {
    this.identifierBag = bag;
  }

  /**
   * Returns the identifierbag of this business entity.
   * @return The identifierbag of this business entity. If this business entity doesn't
   * contain any identifiers, an empty bag is returned.
   */
  public IdentifierBag getIdentifierBag()
  {
    return this.identifierBag;
  }

  /**
   * Add a category to the categorybag of this business entity.
   *
   * @param keyref The category to add.
   */
  public void addCategory(KeyedReference keyref)
  {
    // just return if the KeyedReference parameter is null (nothing to add)
    if (keyref == null)
      return;

    // make sure CategoryBag has been initialized
    if (this.categoryBag == null)
      this.categoryBag = new CategoryBag();

    this.categoryBag.addKeyedReference(keyref);
  }

  /**
   * Set the categorybag of this business entity to the given one.
   *
   * @param bag The new categorybag.
   */
  public void setCategoryBag(CategoryBag bag)
  {
    this.categoryBag = bag;
  }

  /**
   * Returns the categorybag of this business entity.
   *
   * @return The categorybag of this business entity. If this business entity doesn't
   * contain any categories, an empty bag is returned.
   */
  public CategoryBag getCategoryBag()
  {
    return this.categoryBag;
  }
}