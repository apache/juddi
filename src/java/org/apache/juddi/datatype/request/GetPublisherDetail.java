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
import org.apache.juddi.datatype.publisher.PublisherID;

/**
 * Used to get full details for a given set of registered
 * publisher data. Returns a publisherList message.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetPublisherDetail implements RegistryObject,Admin
{
  String generic;
  Vector publisherIDVector;

  /**
   * Construct a new empty get_publisherDetail request.
   */
  public GetPublisherDetail()
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
   * @return String request's generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public Vector getPublisherIDVector()
  {
    return this.publisherIDVector;
  }

  /**
   * Add a PublisherID to the collection of PublisherIDs
   *
   * @param key The new PublisherID to add
   */
  public void addPublisherID(PublisherID publisherID)
  {
    if ((publisherID != null) && (publisherID.getValue() != null))
      addPublisherID(publisherID.getValue());
  }

  /**
   * Add a PublisherID to the collection of PublisherIDs
   *
   * @param key The new PublisherID to add
   */
  public void addPublisherID(String publisherID)
  {
    // nothing to add so just return
    if (publisherID == null)
      return;

    if (publisherIDVector == null)
      publisherIDVector = new Vector();
    publisherIDVector.add(publisherID);
  }

  /**
   * Sets the PublisherID Vector
   *
   * @param keys The new collection of PublisherIDs
   */
  public void setPublisherIDVector(Vector publisherIDs)
  {
    this.publisherIDVector = publisherIDs;
  }
}