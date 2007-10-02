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

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AssertionStatusReport implements RegistryObject
{
  // Assertion Status Report Completion Codes
  public static final String STATUS_COMPLETE = "status:complete";
  public static final String STATUS_TO_KEY_INCOMPLETE = "status:toKey_incomplete";
  public static final String STATUS_FROM_KEY_INCOMPLETE = "status:fromKey_incomplete";

  String generic;
  String operator;
  Vector itemVector;

  /**
   * default constructor
   */
  public AssertionStatusReport()
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
   * @return String UDDI response generic value.
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
  public Vector getAssertionStatusItemVector()
  {
    return this.itemVector;
  }

  /**
   *
   */
  public void addAssertionStatusItem(AssertionStatusItem item)
  {
    if (this.itemVector == null)
      this.itemVector = new Vector();
    this.itemVector.add(item);
  }

  /**
   *
   */
  public void setAssertionStatusItemVector(Vector items)
  {
    this.itemVector = items;
  }
}