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