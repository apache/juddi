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
package org.apache.juddi.datatype;

import java.util.Vector;

/**
 * Optional structure in InstanceDetails used for overview information about
 * a particular TModel use within a BindingTemplate.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class OverviewDoc implements RegistryObject
{
  Vector descVector;
  String overviewURL;

  /**
   * Construct a new emtpy overviewDoc instance.
   */
  public OverviewDoc()
  {
  }

  /**
   * Construct a new overviewDoc with a given overviewURL.
   *
   * @param url The overviewURL of this overviewDoc
   */
  public OverviewDoc(String url)
  {
    this.overviewURL = url;
  }

  /**
   * Construct a new overviewDoc with a given overviewURL.
   *
   * @param url The overviewURL of this overviewDoc
   */
  public OverviewDoc(OverviewURL url)
  {
    setOverviewURL(url);
  }

  /**
   * Adds the given description. If there was already a description with the
   * same language-code as the new description, an exception will be thrown.
   *
   * @param desc The description to add.
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
   * @param descs Descriptions of Description objects to set
   */
  public void setDescriptionVector(Vector descs)
  {
    this.descVector = descs;
  }

  /**
   * Returns the descriptions.
   *
   * @return the descriptions instance.
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   * Sets the overviewURL to the String value of the given URL.
   *
   * @param url The new overviewURL.
   */
  public void setOverviewURL(OverviewURL url)
  {
    if ((url != null) && (url.getValue() != null))
      this.setOverviewURL(url.getValue());
  }

  /**
   * Sets the overviewURL to the given URL.
   *
   * @param url The new overviewURL.
   */
  public void setOverviewURL(String url)
  {
    this.overviewURL = url;
  }

  /**
   * Returns the overviewURL of this overviewDoc.
   *
   * @return The overviewURL of this overviewDoc, or null if there is no
   *  overviewURL.
   */
  public OverviewURL getOverviewURL()
  {
    if (this.overviewURL != null)
      return new OverviewURL(this.overviewURL);
    else
      return null;
  }

  /**
   * Returns the overviewURL of this overviewDoc.
   *
   * @return The overviewURL of this overviewDoc as a String, or null if
   *  there is no overviewURL.
   */
  public String getOverviewURLString()
  {
    return this.overviewURL;
  }
}