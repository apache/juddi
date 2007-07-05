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
package org.apache.juddi.datatype.request;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.publisher.Publisher;

/**
 * Used to register or update complete information about a publisher.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SavePublisher implements RegistryObject,Admin
{
  String generic;
  AuthInfo authInfo;
  Vector publisherVector;

  /**
   *
   */
  public SavePublisher()
  {
  }

  /**
   *
   */
  public SavePublisher(AuthInfo info,Publisher publisher)
  {
    this.authInfo = info;
    addPublisher(publisher);
  }

  /**
   *
   */
  public SavePublisher(AuthInfo info,Vector publishers)
  {
    this.authInfo = info;
    this.publisherVector = publishers;
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
   * @return String request's generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public void setAuthInfo(AuthInfo info)
  {
    this.authInfo = info;
  }

  /**
   *
   */
  public AuthInfo getAuthInfo()
  {
    return this.authInfo;
  }

  /**
   *
   */
  public void addPublisher(Publisher publisher)
  {
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