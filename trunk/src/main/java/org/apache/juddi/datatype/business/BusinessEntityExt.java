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

import org.apache.juddi.datatype.RegistryObject;

/**
 * Implementation specific extension to BusinessEntity. Spec is not
 * clear on this, not sure whether to subclass or contain - from the
 * XML Reference Spec p27 it seems more natural to contain..
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessEntityExt implements RegistryObject
{
  BusinessEntity businessEntity;

  /**
   * Creates a new empty BusinessEntityExt instance with no
   * reference to a/the BusinessEnity that's being extended.
   */
  public BusinessEntityExt()
  {
  }

  /**
   * Creates a new BusinessEntityExt instance and sets the instance of the
   * BusinessEntity that's being extended.
   *
   * @param business The instance of the BusinessEntity that's being extended
   */
  public BusinessEntityExt(BusinessEntity business)
  {
    this.businessEntity = business;
  }

  /**
   * Returns the instance of the BusinessEntity that's being extended.
   *
   * @return the instance of the BusinessEntity that's being extended
   */
  public BusinessEntity getBusinessEntity()
  {
    return this.businessEntity;
  }

  /**
   * Sets the instance of the BusinessEntity that's being extended.
   *
   * @param newValue The instance of the BusinessEntity that's being extended
   */
  public void setBusinessEntity(BusinessEntity newValue)
  {
    this.businessEntity = newValue;
  }
}