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