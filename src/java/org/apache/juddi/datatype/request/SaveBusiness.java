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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.UploadRegister;
import org.apache.juddi.datatype.business.BusinessEntity;

/**
 * "Used to register new businessEntity information or update existing
 *  businessEntity information.  Use this to control the overall
 *  information about the entire business.  Of the save_x APIs this one
 *  has the broadest effect."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveBusiness implements RegistryObject,Publish
{
  String generic;
  AuthInfo authInfo;
  Vector businessVector;
  Vector uploadRegisterVector;

  /**
   *
   */
  public SaveBusiness()
  {
  }

  /**
   *
   */
  public SaveBusiness(AuthInfo info, BusinessEntity business)
  {
    this.authInfo = info;
    addBusinessEntity(business);
  }

  /**
   *
   */
  public SaveBusiness(AuthInfo info,Vector businesses)
  {
    this.authInfo = info;
    this.businessVector = businesses;
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
  public void addBusinessEntity(BusinessEntity businessEntity)
  {
    if (this.businessVector == null)
      this.businessVector = new Vector();
    this.businessVector.add(businessEntity);
  }

  /**
   *
   */
  public void setBusinessEntityVector(Vector businesses)
  {
    this.businessVector = businesses;
  }

  /**
   *
   */
  public Vector getBusinessEntityVector()
  {
    return businessVector;
  }

  /**
   *
   */
  public void addUploadRegister(UploadRegister register)
  {
    if (this.uploadRegisterVector == null)
      this.uploadRegisterVector = new Vector();
    this.uploadRegisterVector.add(register);
  }

  /**
   *
   */
  public void setUploadRegisterVector(Vector registers)
  {
    this.uploadRegisterVector = registers;
  }

  /**
   *
   */
  public Vector getUploadRegisterVector()
  {
    return this.uploadRegisterVector;
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
    return authInfo;
  }
}
