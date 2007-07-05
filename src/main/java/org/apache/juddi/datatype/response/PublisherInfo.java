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
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherInfo implements RegistryObject
{
  String publisherID;
  String nameValue;

  /**
   * default constructor
   */
  public PublisherInfo()
  {
  }

  /**
   *
   */
  public PublisherInfo(String pubID)
  {
    this.publisherID = pubID;
  }

  /**
   *
   */
  public PublisherInfo(String pubID,String name)
  {
    this.publisherID = pubID;
    this.nameValue = name;
  }

  /**
   *
   */
  public void setPublisherID(String pubID)
  {
    this.publisherID = pubID;
  }

  /**
   *
   */
  public String getPublisherID()
  {
    return publisherID;
  }

  /**
   *
   */
  public void setNameValue(String nameValue)
  {
    this.nameValue = nameValue;
  }

  /**
    * Sets the name of this tModel to the given name.
    *
    * @param name The new name of this tModel.
    */
  public void setName(Name name)
  {
    if (name != null)
      this.nameValue = name.getValue();
    else
      this.nameValue = null;
  }

  /**
   *
   */
  public String getNameValue()
  {
    return nameValue;
  }

  /**
   *
   */
  public Name getName()
  {
    if (this.nameValue != null)
      return new Name(nameValue);
    else
      return null;
  }
}