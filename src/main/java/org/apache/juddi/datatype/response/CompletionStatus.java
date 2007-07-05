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
package org.apache.juddi.datatype.response;

import org.apache.juddi.datatype.RegistryObject;

/**
 * Contains a single authInfo element that contains an access token that is to
 * be passed back in all of the publisher API messages that change data. This
 * message is always returned using SSL encryption as a synchronous response
 * to the get_authToken message.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class CompletionStatus implements RegistryObject
{
  public static final String COMPLETE = "status:complete";
  public static final String TOKEY_INCOMPLETE= "status:toKey_incomplete";
  public static final String FROMKEY_INCOMPLETE = "status:fromKey_incomplete";

  String statusValue;

  /**
   *
   */
  public CompletionStatus()
  {
  }

  /**
   *
   */
  public CompletionStatus(String statusValue)
  {
    this.statusValue = statusValue;
  }

  /**
   *
   */
  public void setValue(String value)
  {
    this.statusValue = value;
  }

  /**
   *
   */
  public String getValue()
  {
    return this.statusValue;
  }
}
