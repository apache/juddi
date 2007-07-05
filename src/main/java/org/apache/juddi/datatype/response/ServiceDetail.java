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
import org.apache.juddi.datatype.service.BusinessService;

/**
 * "This structure contains full details for zero or more businessService
 * structures.  It is returned in response to a get_serviceDetail message,
 * and optionally in response to the save_binding and save_service messages."
 * "Note that the businessKey value is supplied in this message because the
 * container does not provide a link to the parent businessEntity structure"
 * XML Structure Reference. (The businessKey in the businessService must
 * be populated).
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ServiceDetail implements RegistryObject
{
  String generic;
  String operator;
  boolean truncated;
  Vector serviceVector;

  /**
   * default constructor
   */
  public ServiceDetail()
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
  public void addBusinessService(BusinessService service)
  {
    if (this.serviceVector == null)
      this.serviceVector = new Vector();
    this.serviceVector.add(service);
  }

  /**
   *
   */
  public void setBusinessServiceVector(Vector services)
  {
    this.serviceVector = services;
  }

  /**
   *
   */
  public Vector getBusinessServiceVector()
  {
    return this.serviceVector;
  }
}