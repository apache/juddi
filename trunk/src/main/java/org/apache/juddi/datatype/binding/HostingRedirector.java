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

import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.RegistryObject;

/**
 * The HostingRedirector in the bindingTemplate is used to indicate that the
 * bindingTemplate entry is a pointer to a different bindingTemplate entry.
 * The value in this is seen when a business or entity wants to expose a
 * service description (ie advertise a service that fulfills a specific
 * purpose) that is actually a service that is described in a separate
 * bindingTemplate record. This might occur when a service is remotely hosted
 * or when many service descriptions could benefit from a single service
 * description.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class HostingRedirector implements RegistryObject
{
  // BindingKey to a different BindingTemplate
  String bindingKey;

  /**
   * Constructs a new initialized HostingRedirector.
   */
  public HostingRedirector()
  {
  }

  /**
   * Constructs a new HostingRedirector with a given String.
   *
   * @param key The binding key.
   */
  public HostingRedirector(String key)
  {
    setBindingKey(key);
  }

  /**
   * Sets the key of this HostingRedirector to the given key.
   *
   * @param key The new key of this HostingRedirector.
   */
  public void setBindingKey(BindingKey key)
  {
    if ((key != null) && (key.getValue() != null))
      setBindingKey(key.getValue());
  }

  /**
   * Sets the key of this HostingRedirector to the given key.
   *
   * @param key The new key of this HostingRedirector.
   */
  public void setBindingKey(String key)
  {
    this.bindingKey = key;
  }

  /**
   * Returns the key of this HostingRedirector.
   *
   * @return The key of this HostingRedirector.
   */
  public String getBindingKey()
  {
    return this.bindingKey;
  }
}