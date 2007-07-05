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
package org.apache.juddi.datatype;

/**
 * The UploadRegister type has been depricated in the UDDI version 2.0
 * specification but it remains because it is still found in the UDDI
 * version 2.0 XML schema.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class UploadRegister implements RegistryObject
{
  String value = null;

  /**
   *
   */
  public UploadRegister()
  {
  }

  /**
   *
   */
  public UploadRegister(String newValue)
  {
    this.value = newValue;
  }

  /**
   *
   */
  public void setValue(String newValue)
  {
    this.value = newValue;
  }

  /**
   *
   */
  public String getValue()
  {
    return this.value;
  }
}