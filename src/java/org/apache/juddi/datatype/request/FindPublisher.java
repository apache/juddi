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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;

/**
 * Used to locate information about one or more publishers. Returns a
 * publisherList message that matches the conditions specified.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindPublisher implements RegistryObject,Admin
{
  String generic;
  Name name;
  FindQualifiers findQualifiers;
  int maxRows;

  /**
   * Constructs a new empty find_publisher request.
   */
  public FindPublisher()
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
   * Sets the name argument of the search to the given name. This value is a partial
   * name. The PublisherList returned contains PublisherInfo objects for Publisher whose
   * name matches the value passed (leftmost match).
   *
   * @param newName The name argument of the search.
   */
  public void setName(String newName)
  {
    setName(new Name(newName));
  }

  /**
   * Sets the name argument of the search to the given name. This value is a partial
   * name. The PublisherList returned contains PublisherInfo objects for Publisher whose
   * name matches the value passed (leftmost match).
   *
   * @param newname The name argument of the search.
   */
  public void setName(Name newname)
  {
    name = newname;
  }

  /**
   * Returns the name argument of the search. Null is returned if the name
   * argument for this search has not been specified.
   *
   * @return The name argument of the search, or null if the argument has not been specified.
   */
  public Name getName()
  {
    return name;
  }

  /**
   * Returns the name argument of the search. Null is returned if the name
   * argument for this search has not been specified.
   *
   * @return The name argument of the search as a String, or null if the argument has not been specified.
   */
  public String getNameString()
  {
    if (this.name != null)
      return this.name.getValue();
    else
      return null;
  }

  /**
   *
   */
  public int getMaxRows()
  {
    return maxRows;
  }

  /**
   *
   */
  public void setMaxRows(int maxRows)
  {
    this.maxRows = maxRows;
  }

  /**
   *
   */
  public void setMaxRows(String maxRows)
  {
    setMaxRows(Integer.parseInt(maxRows));
  }

  /**
   *
   */
  public void addFindQualifier(FindQualifier findQualifier)
  {
    if (this.findQualifiers == null)
      this.findQualifiers = new FindQualifiers();
    this.findQualifiers.addFindQualifier(findQualifier);
  }

  /**
   *
   */
  public void setFindQualifiers(FindQualifiers findQualifiers)
  {
    this.findQualifiers = findQualifiers;
  }

  /**
   *
   */
  public FindQualifiers getFindQualifiers()
  {
    return findQualifiers;
  }
}