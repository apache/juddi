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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.CompletionStatus;

/**
 * "The get_assertionStatusReport API call provides administrative support
 *  for determining the status of current and outstanding publisher
 *  assertions that involve any of the business registrations managed by
 *  the individual publisher account. Using this message, a publisher can
 *  see the status of assertions that they have made, as well as see
 *  assertions that others have made that involve businessEntity structures
 *  controlled by the calling publisher account. See Appendix J on
 *  relationships and publisher assertions for more information."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetAssertionStatusReport implements RegistryObject,Publish
{
  /**
   * status:complete: passing this value will cause only the
   * publisher assertions that are complete to be returned. Each
   * businessEntity listed in assertions that are complete has a
   * visible relationship that directly reflects the data in a
   * complete assertion (as per the find_relatedBusinesses message).
   */
  public static final String STATUS_COMPLETE = "status:complete";

  /**
   * status:toKey_incomplete: passing this value will cause only
   * those publisher assertions where the party who controls the
   * businessEntity referenced by the toKey value in an assertion
   * has not made a matching assertion to be listed.
   */
  public static final String STATUS_TOKEY_INCOMPLETE = "status:toKey_incomplete";

  /**
   * status:fromKey_incomplete: passing this value will cause only
   * those publisher assertions where the party who controls the
   * businessEntity referenced by the fromKey value in an assertion
   * has not made a matching assertion to be listed.
   */
  public static final String STATUS_FROMKEY_INCOMPLETE = "status:fromKey_incomplete";

  String generic;
  AuthInfo authInfo;
  String completionStatus;

  /**
   *
   */
  public GetAssertionStatusReport()
  {
  }

  /**
   *
   */
  public GetAssertionStatusReport(AuthInfo authInfo)
  {
    this.authInfo = authInfo;
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
   * @return String UDDI request's generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public AuthInfo getAuthInfo()
  {
    return this.authInfo;
  }

  /**
   *
   */
  public void setAuthInfo(AuthInfo authInfo)
  {
    this.authInfo = authInfo;
  }

  /**
   *
   */
  public String getCompletionStatus()
  {
    return this.completionStatus;
  }

  /**
   *
   */
  public void setCompletionStatus(String status)
  {
    this.completionStatus = status;
  }

  /**
   *
   */
  public void setCompletionStatus(CompletionStatus status)
  {
    if (status != null)
      this.completionStatus = status.getValue();
    else
      this.completionStatus = null;
  }
}