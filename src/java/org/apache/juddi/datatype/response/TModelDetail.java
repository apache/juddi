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
package org.apache.juddi.datatype.response;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.tmodel.TModel;

/**
 * "This structure contains full details for zero or more tModel structures.
 *  It is returned in response to a get_tModelDetail message, and optionally
 *  in response to the save_tModel message."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelDetail implements RegistryObject
{
  // "Because tModel structures are top-level data. the authorized name value is
  // expressed. This is the name of the person whose account was used to register
  // the data. The two expressions of the operator attribute each express the
  // distinguished name of the Operator Site that is providing the data and
  // the operator where the data is managed. (publishing operator in the tModel
  // + operator in the ResponseMessage base class).

  String generic;
  String operator;
  boolean truncated;
  Vector tModelVector;

  /**
   * default constructor
   */
  public TModelDetail()
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
   * @return String UDDI generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  /**
   *
   */
  public String getOperator()
  {
     return this.operator;
  }

  /**
   *
   */
  public boolean isTruncated()
  {
    return this.truncated;
  }

  /**
   *
   */
  public void setTruncated(boolean val)
  {
    this.truncated = val;
  }

  /**
   *
   */
  public void addTModel(TModel tModel)
  {
    if (this.tModelVector == null)
      this.tModelVector = new Vector();
    this.tModelVector.add(tModel);
  }

  /**
   *
   */
  public Vector getTModelVector()
  {
    return this.tModelVector;
  }

  /**
   *
   */
  public void setTModelVector(Vector tModels)
  {
    this.tModelVector = tModels;
  }
}