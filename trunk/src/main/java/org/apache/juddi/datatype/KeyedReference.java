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