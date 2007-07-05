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

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class RelatedBusinessesList implements RegistryObject
{
  String generic;
  String operator;
  boolean truncated;

  RelatedBusinessInfos relatedBusinessInfos;
  BusinessKey  businessKey;

  /**
   * default constructor
   */
  public RelatedBusinessesList()
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
  public void addRelatedBusinessInfo(RelatedBusinessInfo info)
  {
    if (this.relatedBusinessInfos == null)
      this.relatedBusinessInfos = new RelatedBusinessInfos();
    this.relatedBusinessInfos.addRelatedBusinessInfo(info);
  }

  /**
   *
   */
  public void setRelatedBusinessInfos(RelatedBusinessInfos infos)
  {
    this.relatedBusinessInfos = infos;
  }

  /**
   *
   */
  public RelatedBusinessInfos getRelatedBusinessInfos()
  {
    return this.relatedBusinessInfos;
  }

  /**
   * @return businessKey
   */
  public BusinessKey getBusinessKey()
  {
    return businessKey;
  }

  /**
   * @param key
   */
  public void setBusinessKey(BusinessKey key)
  {
    businessKey = key;
  }
}