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
public class RelatedBusinessInfos implements RegistryObject
{
  private Vector relatedBusinessInfoVector;

  /**
   * default constructor
   */
  public RelatedBusinessInfos()
  {
  }

  /**
   *
   */
  public RelatedBusinessInfos(int size)
  {
    this.relatedBusinessInfoVector = new Vector(size);
  }

  /**
   *
   */
  public void addRelatedBusinessInfo(RelatedBusinessInfo info)
  {
    if (this.relatedBusinessInfoVector == null)
      this.relatedBusinessInfoVector = new Vector();
    this.relatedBusinessInfoVector.add(info);
  }

  /**
   *
   */
  public void setRelatedBusinessInfoVector(Vector infos)
  {
    this.relatedBusinessInfoVector = infos;
  }

  /**
   *
   */
  public Vector getRelatedBusinessInfoVector()
  {
    return this.relatedBusinessInfoVector;
  }
}
