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