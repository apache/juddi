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
import org.apache.juddi.datatype.binding.BindingTemplate;

/**
 * Holds specific bindingTemplate information in response to a
 * get_bindingDetail or find_binding inquiry message. BindingTemplates
 * supplies getFirst() and getNext() to allow iteration through the
 * resultset, each returning a BindingTemplate object.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BindingDetail implements RegistryObject
{
  String generic;
  String operator;
  boolean truncated;
  Vector bindingVector;

  /**
   * Constructor, creates empty BindingDetail.
   */
  public BindingDetail()
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
    return truncated;
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
  public void addBindingTemplate(BindingTemplate template)
  {
    if (this.bindingVector == null)
      this.bindingVector = new Vector();
    this.bindingVector.add(template);
  }

  /**
   *
   */
  public void setBindingTemplateVector(Vector templates)
  {
    this.bindingVector = templates;
  }

  /**
   *
   */
  public Vector getBindingTemplateVector()
  {
    return this.bindingVector;
  }
}