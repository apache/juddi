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
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;

/**
 * Holds service-instance specific information that is required to either
 * understand the service implementation details relative to a specific
 * tModelKey reference, or to provide further parameter and settings support.
 * If present this element should not be empty.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class InstanceDetails implements RegistryObject
{
  // Optional repeating element. This language-quailified string is
  // intended for holding a description of the purpose and/or use of the
  // particular InstanceDetails entry.
  Vector descVector;

  // Optional, used to house references to remote descriptive or instructions
  // related to proper use of a bindingTemplate technical sub-element.
  OverviewDoc overviewDoc;

  // Optional, used to contain settings parameters or a URL reference to a
  // file that contains settings or parameters required to use a specific
  // facet of a bindingTemplate description. If used to house the parameters
  // themselves, the suggested content is a namespace qualifed XML string -
  // using a namespace outside of the XML schema. If used to house a URL
  // pointer to a file, the suggested format is a URL that is suitable for
  // retrieving the settings or parameters via HTTP GET.
  String instanceParms;

  /**
   * Construct a new empty instanceDetails object.
   */
  public InstanceDetails()
  {
  }

  /**
   * Adds the given description. If there was already a description with the
   * same language-code as the new description, an exception will be thrown.
   *
   * @param desc The description to add.
   *  languagecode.
   */
  public void addDescription(Description desc)
  {
    if (this.descVector == null)
      this.descVector = new Vector();
    this.descVector.add(desc);
  }

  /**
   * Sets the description list to the current one. Ignores any object in the
   * collection that is not an "instanceof" the Description class.
   *
   * @param descs Collection of Description objects to set
   */
  public void setDescriptionVector(Vector descs)
  {
    this.descVector = descs;
  }

  /**
   * Returns the descriptions.
   *
   * @return the descriptions.
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   * Sets the overview document of this instanceDetails to the given overview
   * document.
   *
   * @param doc The new overview document, or null if the instanceDetails
   *  doesn't have an overview document anymore.
   */
  public void setOverviewDoc(OverviewDoc doc)
  {
    this.overviewDoc = doc;
  }

  /**
   * Returns the overview document of this instanceDetails.
   *
   * @return The overview document of this instanceDetails, or null if this
   *  instanceDetails doesn't have an overvieuw document.
   */
  public OverviewDoc getOverviewDoc()
  {
    return this.overviewDoc;
  }

  /**
   * Sets the instance parameters of this instanceDetails to the given instance
   * parameters.
   *
   * @param parms The new instance parameters, or null if this instanceDetails
   *  doesn't have instance parameters anymore.
   */
  public void setInstanceParms(String parms)
  {
    this.instanceParms = parms;
  }

  /**
   * Sets the instance parameters of this instanceDetails to the given instance
   * parameters.
   *
   * @param parms The new instance parameters, or null if this instanceDetails
   *  doesn't have instance parameters anymore.
   */
  public void setInstanceParms(InstanceParms parms)
  {
    if ((parms != null) && (parms.getValue() != null))
      setInstanceParms(parms.getValue());
  }

  /**
   * Returns the instance parameters of this instanceDetails as a String.
   *
   * @return An InstanceParms instance containing the value of the instance
   *  parameters of this instanceDetails, or null if this instanceDetails
   *  doesn't have instance parameters.
   */
  public InstanceParms getInstanceParms()
  {
    if (this.instanceParms != null)
      return new InstanceParms(this.instanceParms);
    else
      return null;
  }

  /**
   * Returns the instance parameters of this instanceDetails as a String.
   *
   * @return A String containing the text value of the instance parameters
   *  of this instanceDetails, or null if this instanceDetails doesn't have
   *  instance parameters.
   */
  public String getInstanceParmsString()
  {
    return this.instanceParms;
  }
}