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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelKey;

/**
 * "Used to get full details for a given set of registered tModel
 *  data. Returns a tModelDetail message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetTModelDetail implements RegistryObject,Inquiry
{
  String generic;
  Vector tModelKeyVector;

  /**
   * default constructor
   */
  public GetTModelDetail()
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
   *
   */
  public Vector getTModelKeyVector()
  {
    return this.tModelKeyVector;
  }

  /**
   * Add a TModelKey to the collection of TModelKeys
   *
   * @param key The new TModelKey to add
   */
  public void addTModelKey(TModelKey key)
  {
    if ((key != null) && (key.getValue() != null))
      addTModelKey(key.getValue());
  }

  /**
   * Add a ServiceKey to the collection of ServiceKeys
   *
   * @param key The new ServiceKey to add to the collection of ServiceKeys
   */
  public void addTModelKey(String key)
  {
    if (tModelKeyVector == null)
      tModelKeyVector = new Vector();
    tModelKeyVector.add(key);
  }

  /**
   * Sets the TModelKey Vector
   *
   * @param keys The new collection of TModelKeys
   */
  public void setTModelKeyVector(Vector keys)
  {
    this.tModelKeyVector = keys;
  }
}