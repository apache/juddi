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
 * Used in BusinessEntity as the Name of the BusinessEntity, in BusinessService
 * as the name of the BusinessService and in TModel as the name of the TModel.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Name implements RegistryObject
{
  String nameValue;
  String langCode; // ISO language code

  /**
   * Construct a new initialized name instance.
   */
  public Name()
  {
  }

  /**
   * Construct a new name from a String.
   *
   * @param name The name of the new name-object.
   */
  public Name(String name)
  {
    setValue(name);
  }

  /**
   * Construct a new name with a given name.
   *
   * @param name The name of the new name-object.
   * @param lang The language of the new name-object.
   */
  public Name(String name,String lang)
  {
    setValue(name);
    setLanguageCode(lang);
  }

  /**
   * Sets the name of this name-object to the new given name.
   *
   * @param newName The new name for this name-object.
   */
  public void setValue(String newName)
  {
    this.nameValue = newName;
  }

  /**
   * Returns the name of this name-object.
   *
   * @return The name of this name-object.
   */
  public String getValue()
  {
    return this.nameValue;
  }

  /**
   * Sets the name of this name-object to the new given name.
   *
   * @param newLang The new name for this name-object.
   */
  public void setLanguageCode(String newLang)
  {
    this.langCode = newLang;
  }

  /**
   * Returns the LanguageCode of this Name object.
   *
   * @return The LanguageCode of this name-object.
   */
  public String getLanguageCode()
  {
    return this.langCode;
  }
}