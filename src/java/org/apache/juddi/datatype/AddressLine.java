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
 * The address structure is a simple list of AddressLine elements within
 * the address container. AddressLine elements contain string data with a
 * suggested line length of max. 40 chars.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AddressLine implements RegistryObject
{
  String lineValue;
  String keyName;
  String keyValue;

  /**
   * Constructs a new initialized Addressline instance.
   */
  public AddressLine()
  {
  }

  /**
   * Constructs a new addressline with the given data.
   *
   * @param line The data of the addressline.
   */
  public AddressLine(String line)
  {
    this.lineValue = line;
  }

  /**
   * Constructs a new addressline with the given data.
   *
   * @param line The data of the addressline.
   * @param name ...
   * @param value ...
   */
  public AddressLine(String line,String name,String value)
  {
    this.lineValue = line;
    this.keyName = name;
    this.keyValue = value;
  }

  /**
   * Sets the data of this addressline to the given data.
   *
   * @param line The new data of this addressline.
   */
  public void setLineValue(String line)
  {
    this.lineValue = line;
  }

  /**
   * Returns the data of the addressline.
   *
   * @return The data of the addressline.
   */
  public String getLineValue()
  {
    return this.lineValue;
  }

  /**
   * Sets ...
   *
   * @param name
   */
  public void setKeyName(String name)
  {
    this.keyName = name;
  }

  /**
   * Returns ...
   *
   * @return the key name
   */
  public String getKeyName()
  {
    return this.keyName;
  }

  /**
   * Sets ...
   *
   * @param value
   */
  public void setKeyValue(String value)
  {
    this.keyValue = value;
  }

  /**
   * Returns ...
   *
   * @return the key value
   */
  public String getKeyValue()
  {
    return this.keyValue;
  }
}