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