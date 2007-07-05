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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ServiceInfo implements RegistryObject
{
  String businessKey;
  String serviceKey;
  Vector nameVector;

  /**
   * default constructor
   */
  public ServiceInfo()
  {
  }

  /**
   *
   */
  public ServiceInfo(String bizKey,String servKey)
  {
    this.businessKey = bizKey;
    this.serviceKey = servKey;
  }

  /**
   *
   */
  public void setServiceKey(String key)
  {
    this.serviceKey = key;
  }

  /**
   *
   */
  public String getServiceKey()
  {
    return serviceKey;
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
    return businessKey;
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
}
