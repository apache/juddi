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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelKey;

/**
 * A TModelInstanceInfo object represents the BindingTemplate instance
 * specific details for a single TModel by reference.<p>
 *
 * A set of these is held by the TModelInstanceDetails Map. When taken as a
 * group they form a technically descriptive fingerprint by virtue of the
 * unordered list of tModelKey references that they form. Each singular
 * TModelInstanceInfo refers to a single tModel, and its presence in a
 * bindingTemplate.tModelInstanceDetails implies that this containing web
 * service supports the tModel that is referenced.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelInstanceInfo implements RegistryObject
{
  // Required attribute. This is the unique key referenced that implies that
  // the service being described has implementation details that are specified
  // by the specifications associated with the tModel that is referenced.
  String tModelKey;

  // Optional repeating element. This is one or more language qualified text
  // descriptions that designate what role a TModel referenc plays in the
  // overall service description.
  Vector descVector;

  // Optional, can be used when tModel reference specific settings or other
  // descriptive information are required to either describe a tModel specific
  // component of a service description or support services that require
  // additional technical data support (eg, via settings or other handshake
  // support). If this element is supplied it should not be empty.
  InstanceDetails instanceDetails;

  /**
   * Constructs a new empty TModelInstanceDetails.
   */
  public TModelInstanceInfo()
  {
  }

  /**
   * Constructs a new empty TModelInstanceDetails.
   */
  public TModelInstanceInfo(String key)
  {
    this.tModelKey = key;
  }

  /**
   * Sets the tModel key of this tModelInstanceInfo to the given key.
   *
   * @param key The key of the tModel this tModelInstanceInfo is
   *  referencing to.
   */
  public void setTModelKey(TModelKey key)
  {
    if ((key != null) && (key.getValue() != null))
      this.tModelKey = key.getValue();
  }

  /**
   * Sets the tModel key of this tModelInstanceInfo to the given key.
   *
   * @param key The key of the tModel this tModelInstanceInfo is
   *  referencing to.
   */
  public void setTModelKey(String key)
  {
    this.tModelKey = key;
  }

  /**
   * Returns the tModel key of this tModelInstanceInfo.
   *
   * @return The tModel key of this tModelInstanceInfo.
   */
  public String getTModelKey()
  {
    return this.tModelKey;
  }

  /**
   * Add a Description to the collection of Descriptions.
   *
   * @param desc The Description to add.
   */
  public void addDescription(Description desc)
  {
    if (this.descVector == null)
      this.descVector = new Vector();
    this.descVector.add(desc);
  }

  /**
   * Sets the Description intance to the one one passed in.
   *
   * @param descs Descriptions of Description objects to set
   */
  public void setDescriptionVector(Vector descs)
  {
    this.descVector = descs;
  }

  /**
   * Returns the descriptions.
   *
   * @return the descriptions. If the aren't any descriptions, an empty
   *  enumeration is returned.
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   * Sets the instanceDetails of this tModelInstanceInfo to the given
   * instanceDetails.
   *
   * @param details The instanceDetails of this tModelInstanceInfo, or null if
   *  this tModelInstanceInfo doesn't have any instanceDetails.
   */
  public void setInstanceDetails(InstanceDetails details)
  {
    this.instanceDetails = details;
  }

  /**
   * Returns the instanceDetails of this tModelInstanceInfo.
   *
   * @return The instanceDetails of this tModelInstanceInfo, or null if this
   *  tModelInstanceInfo doesn't have an instanceDetails.
   */
  public InstanceDetails getInstanceDetails()
  {
    return this.instanceDetails;
  }
}