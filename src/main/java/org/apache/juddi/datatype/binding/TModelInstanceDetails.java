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
package org.apache.juddi.datatype.binding;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;

/**
 * Represents the "technical fingerprint". A set of 0..n references to
 * tModelInstanceInfos that are all completely supported by this web service.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelInstanceDetails implements RegistryObject
{
  // tModelInstanceInfos hold the technical service description
  // information related to this businessService.
  Vector tModelInstanceInfoVector;

  /**
   * Constructs a new empty TModelInstanceDetails.
   */
  public TModelInstanceDetails()
  {
  }

  /**
   *
   */
  public void addTModelInstanceInfo(TModelInstanceInfo info)
  {
    if (this.tModelInstanceInfoVector == null)
      this.tModelInstanceInfoVector = new Vector();
    this.tModelInstanceInfoVector.add(info);
  }

  /**
   *
   */
  public Vector getTModelInstanceInfoVector()
  {
    return this.tModelInstanceInfoVector;
  }

  /**
   *
   */
  public void setTModelInstanceInfoVector(Vector infos)
  {
    this.tModelInstanceInfoVector = infos;
  }
}