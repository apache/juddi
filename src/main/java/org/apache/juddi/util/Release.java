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
package org.apache.juddi.util;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Release 
{
  private static final String REGISTRY_VERSION = "2.0.2";
  private static final String UDDI_VERSION = "2.0";
  
  // Made private to avoid instantiation
  private Release()
  {
  }
  
  public static String getRegistryVersion()
  {
    return REGISTRY_VERSION;
  }
  
  public static String getUDDIVersion()
  {
    return UDDI_VERSION;
  }
}
