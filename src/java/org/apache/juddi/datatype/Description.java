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