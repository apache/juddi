/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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