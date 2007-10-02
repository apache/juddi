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
 * tModel information.  This message contains zero or more tModelInfo
 * structures.  It is returned in response to a find_tModel message."
 * "This data is suitable for finding candidate tModels, populating lists of
 * results, and then providing drill-down features that rely on the get_xxDetail
 * messages.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelList implements RegistryObject
{
  String generic;
  String operator;
  boolean truncated;
  TModelInfos tModelInfos;

  /**
   * Constructor, creates empty BindingTemplates list.
   */
  public TModelList()
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
  public void addTModelInfo(TModelInfo info)
  {
    // just return if the TModelInfo parameter is null (nothing to add)
    if (info == null)
      return;

    if (this.tModelInfos == null)
      this.tModelInfos = new TModelInfos();
    this.tModelInfos.addTModelInfo(info);
  }

  /**
   *
   */
  public void setTModelInfos(TModelInfos infos)
  {
    this.tModelInfos = infos;
  }

  /**
   *
   */
  public TModelInfos getTModelInfos()
  {
    return this.tModelInfos;
  }
}