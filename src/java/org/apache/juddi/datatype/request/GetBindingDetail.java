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
package org.apache.juddi.datatype.request;

import java.util.Vector;

import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.RegistryObject;

/**
 * "Used to get full bindingTemplate information suitable for make one
 *  or more service requests. Returns a bindingDetail message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetBindingDetail implements RegistryObject,Inquiry
{
  String generic;
  Vector bindingKeyVector;

  /**
   * Construct a new empty get_bindingDetail request
   */
  public GetBindingDetail()
  {
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
   * Add a reference to a bindingTemplate to this search. All the details for the
   * referenced bindingTemplate will be searched.
   *
   * @param key The bindingkey of the referenced bindingTemplate.
   */
  public void addBindingKey(BindingKey key)
  {
    if ((key != null) && (key.getValue() != null))
      addBindingKey(key.getValue());
  }

  /**
   * Adds a BindingKey to the collection of binding keys used by this query.
   *
   * @param key A BindingKey
   */
  public void addBindingKey(String key)
  {
    if (bindingKeyVector == null)
      bindingKeyVector = new Vector();
    bindingKeyVector.add(key);
  }

  /**
   * Add a collection of bindingTemplate references to this search. All the details for the
   * given referenced bindingTemplates will be searched.
   *
   * @param keys The collection of bindingkeys.
   */
  public void setBindingKeyVector(Vector keys)
  {
    this.bindingKeyVector = keys;
  }

  /**
   *
   */
  public Vector getBindingKeyVector()
  {
    return this.bindingKeyVector;
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();

    if (this.bindingKeyVector != null)
      for (int i=0; i<this.bindingKeyVector.size(); i++)
        buffer.append(this.bindingKeyVector.elementAt(i));

    return buffer.toString();
  }
}