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

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.BindingTemplate;

/**
 * "Used to register new bindingTemplate information or update existing
 *  bindingTemplate information.  Use this to control information about
 *  technical capabilities exposed by a registered business."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveBinding implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector bindingTemplateVector;

  /**
   *
   */
  public SaveBinding()
  {
  }

  /**
   *
   */
  public SaveBinding(AuthInfo authInfo,Vector bindingTemplates)
  {
    this.authInfo = authInfo;
    this.bindingTemplateVector = bindingTemplates;
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
  public void setAuthInfo(AuthInfo authInfo)
  {
    this.authInfo = authInfo;
  }

  /**
   *
   */
  public AuthInfo getAuthInfo()
  {
    return authInfo;
  }

  /**
   *
   */
  public void addBindingTemplate(BindingTemplate bindingTemplate)
  {
    if (this.bindingTemplateVector == null)
      this.bindingTemplateVector = new Vector();
    this.bindingTemplateVector.add(bindingTemplate);
  }

  /**
   *
   */
  public void setBindingTemplateVector(Vector bindingTemplates)
  {
    this.bindingTemplateVector = bindingTemplates;
  }

  /**
   *
   */
  public Vector getBindingTemplateVector()
  {
    return bindingTemplateVector;
  }
}