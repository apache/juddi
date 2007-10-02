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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;

/**
 * "Each businessInfo structure contains company name and optional description
 * data, along with a collection element named serviceInfos that in turn can
 * contain one or more serviceInfo structures" - UDDI Version 2.0 Data Structure
 * Reference
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessInfo implements RegistryObject
{
  String businessKey;
  Vector nameVector;
  Vector descVector;
  ServiceInfos infos;

  /**
   * default constructor
   */
  public BusinessInfo()
  {
  }

  /**
   * copy constructor
   */
  public BusinessInfo(BusinessInfo bizInfo)
  {
    businessKey = bizInfo.getBusinessKey();
    nameVector = bizInfo.getNameVector();
    descVector = bizInfo.getDescriptionVector();
    infos = bizInfo.getServiceInfos();
  }

  /**
   *
   */
  public BusinessInfo(String key)
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
  public void addServiceInfo(ServiceInfo info)
  {
    if (this.infos == null)
      this.infos = new ServiceInfos();
    this.infos.addServiceInfo(info);
  }

  /**
   *
   */
  public void setServiceInfos(ServiceInfos infos)
  {
    this.infos = infos;
  }

  /**
   *
   */
  public ServiceInfos getServiceInfos()
  {
    return this.infos;
  }
}