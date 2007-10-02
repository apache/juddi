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