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
package org.apache.juddi.datatype;

import java.util.Vector;

/**
 * @author Anou Manavalan
 * @author Steve Viens (sviens@apache.org)
 */
public class SharedRelationships implements RegistryObject
{
  Vector keyedReferenceVector;
  String direction;

  /**
   *
   */
  public SharedRelationships()
  {
  }

  /**
   *
   */
  public SharedRelationships(Vector refs)
  {
    this.keyedReferenceVector = refs;
  }

  /**
   * @return Returns the direction.
   */
  public String getDirection() 
  {
    return direction;
  }
  
  /**
   * @param direction The direction to set.
   */
  public void setDirection(String direction) 
  {
    this.direction = direction;
  }
  
  /**
   *
   */
  public void addKeyedReference(KeyedReference ref)
  {
    if (this.keyedReferenceVector == null)
      this.keyedReferenceVector = new Vector();
    this.keyedReferenceVector.add(ref);
  }

  /**
   *
   */
  public Vector getKeyedReferenceVector()
  {
    return this.keyedReferenceVector;
  }

  /**
   *
   */
  public void setKeyedReferenceVector(Vector refs)
  {
    this.keyedReferenceVector = refs;
  }

  /**
   *
   */
  public int size()
  {
    if (this.keyedReferenceVector != null)
      return this.keyedReferenceVector.size();
    else
      return 0;
  }
}