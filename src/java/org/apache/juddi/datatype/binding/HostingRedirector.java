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