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
package org.apache.juddi.datatype.response;

import java.util.Vector;

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.SharedRelationships;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Manavalan
 */
public class RelatedBusinessInfo implements RegistryObject
{
  String businessKey;
  Vector nameVector;
  Vector descVector;
  SharedRelationships sharedRelationships;

  /**
   * default constructor
   */
  public RelatedBusinessInfo()
  {
  }

  /**
   * copy constructor
   */
  public RelatedBusinessInfo(RelatedBusinessInfo bizInfo)
  {
    businessKey = bizInfo.getBusinessKey();
    nameVector = bizInfo.getNameVector();
    descVector = bizInfo.getDescriptionVector();
    sharedRelationships = bizInfo.getSharedRelationships();
  }

  /**
   *
   */
  public RelatedBusinessInfo(String key)
  {
    this.businessKey = key;
  }

  /**
   *
   */
  public void setBusinessKey(String key)
  {
    this.businessKey = key;
  }

  /**
   *
   */
  public String getBusinessKey()
  {
    return this.businessKey;
  }

  /**
   *
   */
  public void addName(Name name)
  {
    if (this.nameVector == null)
      this.nameVector = new Vector();
    this.nameVector.add(name);
  }

  /**
   *
   */
  public void setNameVector(Vector names)
  {
    this.nameVector = names;
  }

  /**
   *
   */
  public Vector getNameVector()
  {
    return this.nameVector;
  }

  /**
   *
   */
  public void addDescription(Description desc)
  {
    if (this.descVector == null)
      this.descVector = new Vector();
    this.descVector.add(desc);
  }

  /**
   *
   */
  public void setDescriptionVector(Vector descriptions)
  {
    this.descVector = descriptions;
  }

  /**
   *
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   *
   */
  public void addKeyedReference(KeyedReference ref)
  {
    if (this.sharedRelationships == null)
      this.sharedRelationships = new SharedRelationships();
    this.sharedRelationships.addKeyedReference(ref);
  }

  /**
   * @return SharedRelationships
   */
  public SharedRelationships getSharedRelationships()
  {
    return this.sharedRelationships;
  }

  /**
   * @param relationships
   */
  public void setSharedRelationships(SharedRelationships relationships)
  {
    this.sharedRelationships = relationships;
  }

  /**
   *
   */
  public void setBusinessKey(BusinessKey businessKey)
  {
    if ((businessKey != null) && (businessKey.getValue() != null))
    this.setBusinessKey(businessKey.getValue());
  }

}