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

import org.apache.juddi.datatype.RegistryObject;

/**
 * "This structure contains abbreviated information about registered
 * businessEntity information.  This message contains zero or more
 * businessInfo structures.  It is returned in response to a
 * find_business message."
 *
 * "BusinessInfo structures are abbreviated versions of BusinessEntity
 * data suitable for populating lists of search results in anticipation of
 * further drill-down detail inquiries." - XML Structure Reference
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessList implements RegistryObject
{
  String generic;
  String operator;
  boolean truncated;
  BusinessInfos businessInfos;

  /**
   *
   */
  public BusinessList()
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
  public void addBusinessInfo(BusinessInfo info)
  {
    if (this.businessInfos == null)
      this.businessInfos = new BusinessInfos();
    this.businessInfos.addBusinessInfo(info);
  }

  /**
   *
   */
  public void setBusinessInfos(BusinessInfos infos)
  {
    this.businessInfos = infos;
  }

  /**
   *
   */
  public BusinessInfos getBusinessInfos()
  {
    return businessInfos;
  }
}
