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
import org.apache.juddi.datatype.publisher.Publisher;

/**
 * "This structure contains full details for zero or more publisher
 * structures.  It is returned in response to a get_publisherDetail message,
 * and optionally in response to the save_publisher message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherDetail implements RegistryObject
{
  String generic;
  String operator;
  boolean truncated;
  Vector publisherVector;

  /**
   * default constructor
   */
  public PublisherDetail()
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
   * @return String generic value.
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
  public void addPublisher(Publisher publisher)
  {
    // if publisher is null then simply return (nothing to add)
    if (publisher == null)
      return;

    if (this.publisherVector == null)
      this.publisherVector = new Vector();
    this.publisherVector.add(publisher);
  }

  /**
   *
   */
  public void setPublisherVector(Vector publishers)
  {
    this.publisherVector = publishers;
  }

  /**
   *
   */
  public Vector getPublisherVector()
  {
    return this.publisherVector;
  }
}