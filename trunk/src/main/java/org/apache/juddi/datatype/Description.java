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
 * A Description object contains a textual description and an optional
 * language code.<p>
 *
 * A default ISO language code will be determined for a publisher at the time
 * that a party establishes permissions to publish at a given operator site
 * or implementation. This default language code will be applied to any
 * description values that are provided with no language code.<p>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Description implements RegistryObject
{
  String descValue;
  String langCode; // ISO language code

  /**
   * Construct a new initialized Description instance.
   */
  public Description()
  {
  }

  /**
   * Construct a new initialized Description instance.
   */
  public Description(String descValue)
  {
    this.setValue(descValue);
  }

  /**
   * Construct a new initialized Description instance.
   */
  public Description(String descValue,String langCode)
  {
    this.setValue(descValue);
    this.setLanguageCode(langCode);
  }

  /**
   * Sets the LanguageCode of this Description.
   *
   * @param langCode The new LanguageCode.
   */
  public void setLanguageCode(String langCode)
  {
    this.langCode = langCode;
  }

  /**
   * Returns the LanguageCode of this Description.
   *
   * @return The LanguageCode of this Description, or null if this
   *  description doesn't have a LanguageCode.
   */
  public String getLanguageCode()
  {
    return this.langCode;
  }

  /**
   * Sets the text of this Description to the given text. If the
   * Description has more than 255 characters, it will be trunctated.
   *
   * @param newDesc The new text of this Description.
   */
  public void setValue(String newDesc)
  {
    this.descValue = newDesc;
  }

  /**
   * Returns the text of this Description.
   *
   * @return The text of this Description.
   */
  public String getValue()
  {
    return this.descValue;
  }
}