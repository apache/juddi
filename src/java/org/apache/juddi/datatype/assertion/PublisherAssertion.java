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