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

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.RegistryObject;

/**
 * "Used to delete registered businessEntity information from the registry."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DeleteBusiness implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector businessKeyVector;

  /**
   *
   */
  public DeleteBusiness()
  {
  }

  /**
   *
   */
  public DeleteBusiness(AuthInfo authInfo, String businessKey)
  {
    this.authInfo = authInfo;
    addBusinessKey(businessKey);
  }

  /**
   *
   */
  public DeleteBusiness(AuthInfo authInfo,Vector businessKeys)
  {
    this.authInfo = authInfo;
    this.businessKeyVector = businessKeys;
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
  public void addBusinessKey(BusinessKey businessKey)
  {
    if ((businessKey != null) && (businessKey.getValue() != null))
      this.addBusinessKey(businessKey.getValue());
  }

  /**
   *
   */
  public void addBusinessKey(String businessKey)
  {
    if (this.businessKeyVector == null)
      this.businessKeyVector = new Vector();
    this.businessKeyVector.add(businessKey);
  }

  /**
   *
   */
  public void setBusinessKeyVector(Vector businessKeys)
  {
    this.businessKeyVector = businessKeys;
  }

  /**
   *
   */
  public Vector getBusinessKeyVector()
  {
    return this.businessKeyVector;
  }

  /**
   *
   */
  public void setAuthInfo(AuthInfo authInfo)
  {
    this.authInfo = authInfo;
  }

  /**
   *
   */
  public AuthInfo getAuthInfo()
  {
    return this.authInfo;
  }
}