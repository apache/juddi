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

import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessServices implements RegistryObject
{
  Vector businessServiceVector;

  /**
   *
   */
  public BusinessServices()
  {
  }

  /**
   *
   */
  public BusinessServices(int size)
  {
    this.businessServiceVector = new Vector(size);
  }

  /**
   *
   */
  public void addBusinessService(BusinessService service)
  {
    if (this.businessServiceVector == null)
      this.businessServiceVector = new Vector();
    this.businessServiceVector.add(service);
  }

  /**
   *
   */

  /**
   *
   */
  public void setBusinessServiceVector(Vector services)
  {
    this.businessServiceVector = services;
  }

  /**
   *
   */
  public Vector getBusinessServiceVector()
  {
    return this.businessServiceVector;
  }
}