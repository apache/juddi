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
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.tmodel.TModel;

/**
 * "Whenever save_business, save_service or save_tModel are called,
 *  the contents of any included categoryBag or identifierBag element
 *  may be checked to see that it contains valid values. Checking is
 *  performed for taxonomies and identifier schemes that are classified
 *  as "checked". UDDI version 2 provides the ability for third parties
 *  to register new taxonomies and identifier schemes, and then control
 *  the validation process used by UDDI to perform such checks. Third
 *  parties that want to provision such a capability must implement a
 *  web service in the same manner that UDDI does (e.g. using SOAP 1.1
 *  over HTTP for message passing) that exposes a single method named
 *  validate_values. The interface for validate_values is described
 *  here."
 *
 * "A UDDI operator sends the validate_values message to the appropriate
 *  external service, of which there is exactly one, whenever a publisher
 *  saves data that uses a categorization value or identifier whose use
 *  is regulated by the external party who controls that service. For
 *  purposes of discussion, these identifiers and classifications are
 *  called checked value sets. The normal use is to verify that specific
 *  categories or identifiers (checking the keyValue attribute values
 *  supplied) exist within the given taxonomy or identifier system. For
 *  certain categorizations and identifiers, the party providing the
 *  validation service may further restrict the use of a value to certain
 *  parties based on the identifiers passed in the message or any other
 *  type of contextual check that is possible using the passed data."
 *
 *  - from UDDI Version 2 Programmers API Specification, pg. 70
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ValidateValues implements RegistryObject,Inquiry
{
  String generic;
  Vector businessVector;
  Vector serviceVector;
  Vector tModelVector;

  /**
   *
   */
  public ValidateValues()
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
   * @return String UDDI request's generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public void addBusinessEntity(BusinessEntity business)
  {
    if (business == null)
      return;

    if (this.businessVector == null)
      this.businessVector = new Vector();
    this.businessVector.add(business);
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
    return this.businessVector;
  }

  /**
   *
   */
  public void addBusinessService(BusinessService service)
  {
    if (service == null)
      return;

    if (this.serviceVector == null)
      this.serviceVector = new Vector();
    this.serviceVector.add(service);
  }

  /**
   *
   */
  public void setBusinessServiceVector(Vector services)
  {
    this.serviceVector = services;
  }

  /**
   *
   */
  public Vector getBusinessServiceVector()
  {
    return this.serviceVector;
  }

  /**
   *
   */
  public void addTModel(TModel tModel)
  {
    if (tModel == null)
      return;

    if (this.tModelVector == null)
      this.tModelVector = new Vector();
    this.tModelVector.add(tModel);
  }

  /**
   *
   */
  public void setTModelVector(Vector tModels)
  {
    this.tModelVector = tModels;
  }

  /**
   *
   */
  public Vector getTModelVector()
  {
    return this.tModelVector;
  }
}