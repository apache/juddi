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

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class BindingTemplates implements RegistryObject
{
  Vector bindingTemplateVector;

  /**
   * Constructs a new empty BindingTemplates instance.
   */
  public BindingTemplates()
  {
  }

  /**
   *
   */
  public BindingTemplates(int size)
  {
    this.bindingTemplateVector = new Vector(size);
  }

  /**
   *
   */
  public void addBindingTemplate(BindingTemplate binding)
  {
    if (this.bindingTemplateVector == null)
      this.bindingTemplateVector = new Vector();
    this.bindingTemplateVector.add(binding);
  }

  /**
   *
   */
  public Vector getBindingTemplateVector()
  {
    return this.bindingTemplateVector;
  }

  /**
   *
   */
  public void setBindingTemplateVector(Vector bindings)
  {
    this.bindingTemplateVector = bindings;
  }
}