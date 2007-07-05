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
import org.apache.juddi.datatype.assertion.PublisherAssertion;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherAssertions implements RegistryObject
{
  String generic;
  String operator;
  Vector publisherAssertionVector;

  /**
   *
   */
  public PublisherAssertions()
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
  public void addPublisherAssertion(PublisherAssertion assertion)
  {
    if (this.publisherAssertionVector == null)
      this.publisherAssertionVector = new Vector();
    this.publisherAssertionVector.add(assertion);
  }

  /**
   *
   */
  public void setPublisherAssertionVector(Vector assertions)
  {
    this.publisherAssertionVector = assertions;
  }

  /**
   *
   */
  public Vector getPublisherAssertionVector()
  {
     return this.publisherAssertionVector;
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("org.apache.juddi.datatype.response.PublisherAssertions.operator=");
    buffer.append(this.operator);
    buffer.append("\n");

    if (this.publisherAssertionVector != null)
      for (int i=0; i<this.publisherAssertionVector.size(); i++)
        buffer.append(this.publisherAssertionVector.elementAt(i));

    return buffer.toString();
  }
}
