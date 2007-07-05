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
package org.apache.juddi.error;

import org.apache.juddi.datatype.response.Result;

/**
 * Thrown to indicate that a UDDI Exception was encountered.
 * (RETIRED) Replaced by E_valueNotAllowed in UDDI v2.0. See the
 * UDDI Programmers API, Appendix A for more information.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class CategorizationNotAllowedException extends RegistryException
{
  public CategorizationNotAllowedException(String msg)
  {
    super("Client",Result.E_CATEGORIZATION_NOT_ALLOWED,msg);
  }
}