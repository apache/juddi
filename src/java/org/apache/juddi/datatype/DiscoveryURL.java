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