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
package org.apache.juddi.datatype;

/**
 * Not just name / value. Also contains a tModel reference. This makes the
 * identifier scheme extensible by allowing tModels to be used as conceptual
 * namespace qualifiers.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class KeyedReference implements RegistryObject
{
  String tModelKey;
  String keyName;
  String keyValue;

  /**
   * Construct a new initialized keyedReference instance.
   */
  public KeyedReference()
  {
  }

  /**
   * Construct a new KeyedReference with a name and value.
   *
   * @param name The name of the name-value pair.
   * @param value The value of the name-value pair.
   */
  public KeyedReference(String name,String value)
  {
    this.keyName = name;
    this.keyValue = value;
  }

  /**
   * Construct a new KeyedReference with a given TModel String, key
   * name and key value.
   *
   * @param tModelKey The optional TModelKey String.
   * @param name The name of the name-value pair.
   * @param value The value of the name-value pair.
   */
  public KeyedReference(String tModelKey,String name,String value)
  {
    this.tModelKey = tModelKey;
    this.keyName = name;
    this.keyValue = value;
  }

  /**
   * Construct a new KeyedReference with a given TModelKey, key name
   * and key value.
   *
   * @param tModelKey The optional TModelKey.
   * @param name The name of the name-value pair.
   * @param value The value of the name-value pair.
   */
  public KeyedReference(TModelKey tModelKey,String name,String value)
  {
    this.setTModelKey(tModelKey);
    this.keyName = name;
    this.keyValue = value;
  }

  /**
   * Sets the name of this keyedReference.
   *
   * @param name The new name of this keyedReference
   */
  public void setKeyName(String name)
  {
    this.keyName = name;
  }

  /**
   * Returns the name of this keyedReference.
   *
   * @return The name of this keyedReference.
   */
  public String getKeyName()
  {
    return this.keyName;
  }

  /**
   * Sets the value of this keyedReference.
   *
   * @param value The new value of this keyedReference
   */
  public void setKeyValue(String value)
  {
    this.keyValue = value;
  }

  /**
   * Returns the value of this keyedReference.
   *
   * @return The value of this keyedReference.
   */
  public String getKeyValue()
  {
    return this.keyValue;
  }

  /**
   * Sets the reference to the tModel to the given reference. The reference is
   * represented by the key of the tModel. If this keyedReference doesn't point
   * to a tModel anymore, the new reference must be null.
   *
   * @param key The key of the tModel to reference to.
   */
  public void setTModelKey(TModelKey key)
  {
    if ((key != null) && (key.getValue() != null))
      setTModelKey(key.getValue());
  }

  /**
   * Sets the reference to the tModel to the given reference. The reference is
   * represented by the key of the tModel. If this keyedReference doesn't point
   * to a tModel anymore, the new reference must be null.
   *
   * @param key The key of the tModel to reference to.
   */
  public void setTModelKey(String key)
  {
    this.tModelKey = key;
  }

  /**
   * Returns the reference to the tModel. Null is returned if this
   * KeyedReference doesn't point to a tModel.
   *
   * @return The reference to the tModel.
   */
  public String getTModelKey()
  {
    return this.tModelKey;
  }
}