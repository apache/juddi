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
 * "Knows about the creation and populating of TModelBag objects.
 * Returns TModelBag."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelBag implements RegistryObject
{
  Vector tModelKeyVector;

  /**
   *
   */
  public TModelBag()
  {
  }

  /**
   *
   */
  public TModelBag(int size)
  {
    this.tModelKeyVector = new Vector(size);
  }

  /**
   *
   */
  public void addTModelKey(TModelKey key)
  {
    if ((key != null) && (key.getValue() != null))
      this.addTModelKey(key.getValue());
  }

  /**
   *
   */
  public void addTModelKey(String key)
  {
    if (this.tModelKeyVector == null)
      this.tModelKeyVector = new Vector();
    this.tModelKeyVector.add(key);
  }

  /**
   *
   */
  public void setTModelKeyVector(Vector keyVector)
  {
    this.tModelKeyVector = keyVector;
  }

  /**
   *
   */
  public Vector getTModelKeyVector()
  {
    return this.tModelKeyVector;
  }

  /**
   *
   */
  public int size()
  {
    if (this.tModelKeyVector != null)
      return this.tModelKeyVector.size();
    else
      return 0;
  }
}