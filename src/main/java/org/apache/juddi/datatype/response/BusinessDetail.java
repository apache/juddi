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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.business.BusinessEntity;

/**
 * "This structure contains full details for zero or more businessEntity
 *  structures.  It is returned in response to a get_businessDetail message,
 *  and optionally in response to the save_business message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessDetail implements RegistryObject
{
  String generic;
  String operator;
  boolean truncated;
  Vector businessVector;

  /**
   *
   */
  public BusinessDetail()
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
   * @return String UDDI generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  /**
   *
   */
  public String getOperator()
  {
     return this.operator;
  }

  /**
   *
   */
  public boolean isTruncated()
  {
    return this.truncated;
  }

  /**
   *
   */
  public void setTruncated(boolean val)
  {
    this.truncated = val;
  }

  /**
   *
   */
  public void addBusinessEntity(BusinessEntity entity)
  {
    if (this.businessVector == null)
      this.businessVector = new Vector();
    this.businessVector.add(entity);
  }

  /**
   *
   */
  public void setBusinessEntityVector(Vector businesses)
  {
    this.businessVector = businesses;
  }

  /**
   *
   */
  public Vector getBusinessEntityVector()
  {
    return this.businessVector;
  }
}