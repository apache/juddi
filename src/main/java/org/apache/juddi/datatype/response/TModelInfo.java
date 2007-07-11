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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;

/**
 * Part of RegisteredInfo.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelInfo implements RegistryObject
{
  String tModelKey;
  Name name;

  /**
   * default constructor
   */
  public TModelInfo()
  {
  }

  /**
   *
   */
  public TModelInfo(String key,String name)
  {
    this.tModelKey = key;
    setNameValue(name);
  }

  /**
   *
   */
  public void setTModelKey(String key)
  {
    this.tModelKey = key;
  }

  /**
   *
   */
  public void setNameValue(String nameValue)
  {
      if (nameValue == null) {
          this.name = null;
      } else {
          this.name = new Name(nameValue);
      }
  }

  /**
    * Sets the name of this tModel to the given name.
    *
    * @param name The new name of this tModel.
    */
  public void setName(Name name)
  {
      this.name = name;
  }

  /**
   *
   */
  public String getTModelKey()
  {
    return tModelKey;
  }

  /**
   *
   */
  public String getNameValue()
  {
      if (this.name == null) {
          return null;
      } else {
          return this.name.getValue();
      }
  }

  /**
   *
   */
  public Name getName()
  {
      return this.name;
  }
}
