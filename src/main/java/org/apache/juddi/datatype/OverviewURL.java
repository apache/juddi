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
 * Used in BusinessEntity as the Name of the BusinessEntity, in BusinessService
 * as the name of the BusinessService and in TModel as the name of the TModel.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class OverviewURL implements RegistryObject
{
  String urlValue;

  /**
   * Construct a new initialized name instance.
   */
  public OverviewURL()
  {
  }

  /**
   * Construct a new name with a given name.
   *
   * @param url The name of the new name-object.
   */
  public OverviewURL(String url)
  {
    this.urlValue = url;
  }

  /**
   * Sets the name of this name-object to the new given name.
   *
   * @param url The new name for this name-object.
   */
  public void setValue(String url)
  {
    this.urlValue = url;
  }

  /**
   * Returns the overviewURL value as a String.
   *
   * @return The overviewURL as a String.
   */
  public String getValue()
  {
    return this.urlValue;
  }
}