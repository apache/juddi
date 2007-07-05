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
import org.apache.juddi.datatype.tmodel.TModel;

/**
 * "This structure contains full details for zero or more tModel structures.
 *  It is returned in response to a get_tModelDetail message, and optionally
 *  in response to the save_tModel message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelDetail implements RegistryObject
{
  // "Because tModel structures are top-level data. the authorized name value is
  // expressed. This is the name of the person whose account was used to register
  // the data. The two expressions of the operator attribute each express the
  // distinguished name of the Operator Site that is providing the data and
  // the operator where the data is managed. (publishing operator in the tModel
  // + operator in the ResponseMessage base class).

  String generic;
  String operator;
  boolean truncated;
  Vector tModelVector;

  /**
   * default constructor
   */
  public TModelDetail()
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
  public void addTModel(TModel tModel)
  {
    if (this.tModelVector == null)
      this.tModelVector = new Vector();
    this.tModelVector.add(tModel);
  }

  /**
   *
   */
  public Vector getTModelVector()
  {
    return this.tModelVector;
  }

  /**
   *
   */
  public void setTModelVector(Vector tModels)
  {
    this.tModelVector = tModels;
  }
}