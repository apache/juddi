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

import org.apache.juddi.datatype.RegistryObject;

/**
 * Is an attribute-qualified pointer to a service entry point. Many types of
 * entry point are accommodated. A single urlType attribute is used to indicate
 * the type of entry point so searches for specific types can be performed.
 * Types might be "http", "smtp", "ftp", etc. In addition there is the actual
 * string value for the entry point. Example accessPoints therefore may be:
 * mailto:purch@fabrikam.com
 * http://www.sviens.com/purchasing
 * ftp://ftp.sviens.com/public, etc.
 *
 * If the AccessPoint has a custom addressformat, the "other" urlType has to
 * be used. When this value is used, one or more of the tModel signatures
 * found in the tModelInstanceInfo collection must imply that a particual
 * format or transport type is required.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AccessPoint implements RegistryObject
{
  /**
   * Constant which designates that the AccessPoint url is formatted as an
   * electronic mail address reference.
   */
  public static final String MAILTO = "mailto";

  /**
   * Constant which designates that the AccessPoint url is formatted as an
   * HTTP compatible Uniform Resource Locator (URL).
   */
  public static final String HTTP = "http";

  /**
   * Constant which designates that the AccessPoint url is formatted as a
   * secure HTTP compatible URL.
   */
  public static final String HTTPS = "https";

  /**
   * Constant which designates that the AccessPoint url is formatted as a
   * writable FTP directory address.
   */
  public static final String FTP = "ftp";

  /**
   * Constant which designates that the AccessPoint url is formatted as a
   * telephone number that will contact a facsimile machine.
   */
  public static final String FAX = "fax";

  /**
   * Constant which designates that the AccessPoint url is formatted as a
   * telephone number that will connect to a human or suitable voice or
   * tone response based system.
   */
  public static final String PHONE = "phone";

  /**
   * Constant which designates that the AccessPoint is formatted as some other
   * address format. When this value is used, one or more of the tModel
   * signatures found in the tModelInstanceInfo collection must imply that a
   * particual format or transport type is required.
   */
  public static final String OTHER = "other";

  String urlType;
  String urlValue;

  /**
   * Constructs a new initialized AccessPoint
   */
  public AccessPoint()
  {
  }

  /**
   * Constructs a new AccessPoint for a given urlType and a given urlValue.
   *
   * @param urlType The urlType of the AccessPoint.
   * @param urlValue The value of the AccessPoint url.
   */
  public AccessPoint(String urlType,String urlValue)
  {
    this.urlType = urlType;
    this.urlValue = urlValue;
  }

  /**
   * Set the urlType of this AccessPoint to the given urlType. The urlType
   * cannot be null.
   *
   * @param urlType The new type of the AccessPoint.
   */
  public void setURLType(String urlType)
  {
    this.urlType = urlType;
  }

  /**
   * Set the url value of this AccessPoint to the given value. The value cannot
   * be null.
   *
   * @param url The new value of this AccessPoint.
   */
  public void setURL(String url)
  {
    this.urlValue = url;
  }

  /**
   * Returns the value of this AccessPoint.
   *
   * @return The value of this AccessPoint.
   */
  public String getURL()
  {
    return this.urlValue;
  }

  /**
   * Returns the type of this AccessPoint.
   *
   * @return The type of this AccessPoint.
   */
  public String getURLType()
  {
    return this.urlType;
  }
}