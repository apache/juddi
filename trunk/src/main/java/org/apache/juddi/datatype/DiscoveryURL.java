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

/**
 * A businessEntity has a "discoveryURLs" attribute which is a collection of these.
 * Consists of an attribute whose value designates the URL use type convention
 * and a string found within the body of the element. The use type will be
 * "businessEntity" or "businessEntityExt" according to the containing object being
 * saved. Each time a businessEntity object is saved this collection is augmented
 * with the new URL identifying the newly saved object. The useType can be an
 * empty string ("") if the discoveryURL is used in a find_business message.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DiscoveryURL implements RegistryObject
{
  String useType;
  String urlValue;

  /**
   * Construct a new discoveryURL with null usetype and url values.
   */
  public DiscoveryURL()
  {
  }

  /**
   * Construct a new discoveryURL with the given usetype and url.
   * @param type The usetype of this discoveryURL.
   * @param url The url of this discoveryURL
   */
  public DiscoveryURL(String type,String url)
  {
    this.useType = type;
    this.urlValue = url;
  }

  /**
   * Sets the usetype of this discoveryURL to the given usetype.
   * @param useType The new usetype of this discoveryURL.
   */
  public void setUseType(String useType)
  {
    this.useType = useType;
  }

  /**
   * Returns the usetype of this discoveryURL.
   * @return The usetype of this discoveryURL.
   */
  public String getUseType()
  {
    return this.useType;
  }

  /**
   * Sets the url of this discoveryURL to the given url.
   *
   * @param url The new url of this discoveryURL.
   */
  public void setValue(String url)
  {
    this.urlValue = url;
  }

  /**
   * Returns the url of this discoveryURL.
   * @return The url of this discoveryURL.
   */
  public String getValue()
  {
    return this.urlValue;
  }
}