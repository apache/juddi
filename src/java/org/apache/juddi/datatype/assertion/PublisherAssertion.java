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
package org.apache.juddi.datatype.assertion;

import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;

/**
 * Many businesses, like large enterprises or marketplaces, are not effectively
 * represented by a single businessEntity, since their description and
 * discovery are likely to be diverse. As a consequence, serveral BusinessEntity
 * structures can be published, representing individual subsidiaries of a large
 * enterprise or individual participants of a marketplace. Nevertheless, they
 * still represent a more or less coupled community and would like to make
 * some of their relationships visible in their UDDI registrations. Therefore
 * two related businesses use the xx_pulisherAssertion messages, publishing
 * assertiions of business relationships.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherAssertion implements RegistryObject
{
  String fromKeyValue;
  String toKeyValue;
  KeyedReference keyedReference;

  /**
   * Construct a new initialized keyedReference instance.
   */
  public PublisherAssertion()
  {
  }

  /**
   * Construct a new initialized keyedReference instance.
   */
  public PublisherAssertion(String fromKey,String toKey,KeyedReference keyedRef)
  {
    this.fromKeyValue = fromKey;
    this.toKeyValue = toKey;
    this.keyedReference = keyedRef;
  }

  /**
   * Sets the fromKey.
   *
   * @param fromKey The fromKey.
   */
  public void setFromKey(String fromKey)
  {
    this.fromKeyValue = fromKey;
  }

  /**
   * Returns the fromKey.
   *
   * @return The fromKey.
   */
  public String getFromKey()
  {
    return fromKeyValue;
  }

  /**
   * Sets the toKey.
   *
   * @param toKey The toKey.
   */
  public void setToKey(String toKey)
  {
    this.toKeyValue = toKey;
  }

  /**
   * Returns the toKey.
   *
   * @return The toKey.
   */
  public String getToKey()
  {
    return this.toKeyValue;
  }

  /**
   * Sets the KeyedReference.
   *
   * @param keyedRef The new KeyedReference instance or null.
   */
  public void setKeyedReference(KeyedReference keyedRef)
  {
    this.keyedReference = keyedRef;
  }

  /**
   * Returns the KeyedReference instance.
   *
   * @return The KeyedReference instance.
   */
  public KeyedReference getKeyedReference()
  {
    return this.keyedReference;
  }

  /**
   * Sets the reference to this assertion's tModel to the given reference.
   *
   * @param tModelKey The key of the tModel to reference.
   */
  public void setTModelKey(String tModelKey)
  {
    if (this.keyedReference == null)
      this.keyedReference = new KeyedReference();
    this.keyedReference.setTModelKey(tModelKey);
  }

  /**
   * Sets the name of this assertion's keyedReference.
   *
   * @param keyName The new name of this keyedReference
   */
  public void setKeyName(String keyName)
  {
    if (this.keyedReference == null)
      this.keyedReference = new KeyedReference();
    this.keyedReference.setKeyName(keyName);
  }

  /**
   * Sets the value of this assertion's keyedReference.
   *
   * @param keyValue The new value of this keyedReference
   */
  public void setKeyValue(String keyValue)
  {
    if (this.keyedReference == null)
      this.keyedReference = new KeyedReference();
    this.keyedReference.setKeyValue(keyValue);
  }
}