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
package org.apache.juddi.registry;

import java.util.Vector;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.response.AssertionStatusReport;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BindingDetail;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.BusinessDetailExt;
import org.apache.juddi.datatype.response.BusinessList;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.PublisherAssertions;
import org.apache.juddi.datatype.response.PublisherDetail;
import org.apache.juddi.datatype.response.PublisherList;
import org.apache.juddi.datatype.response.RegisteredInfo;
import org.apache.juddi.datatype.response.RelatedBusinessesList;
import org.apache.juddi.datatype.response.ServiceDetail;
import org.apache.juddi.datatype.response.ServiceList;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.response.TModelList;
import org.apache.juddi.error.RegistryException;

/**
 * Represents a vesion 2.0 UDDI registry and implements all
 * services as specified in the UDDI version 2.0 specification.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public interface IRegistry
{
  String UDDI_V1_GENERIC = "1.0";
  String UDDI_V2_GENERIC = "2.0";
  String UDDI_V3_GENERIC = "3.0";
  String JUDDI_V1_GENERIC = "1.0";

  String UDDI_V1_NAMESPACE = "urn:uddi-org:api";
  String UDDI_V2_NAMESPACE = "urn:uddi-org:api_v2";
  String UDDI_V3_NAMESPACE = "urn:uddi-org:api_v3";
  String JUDDI_V1_NAMESPACE = "urn:juddi-org:api_v1";

  /**
   * "Used to locate specific bindings within a registered
   *  businessService. Returns a bindingDetail message."
   *
   * @exception RegistryException
   */
  BindingDetail findBinding(String serviceKey,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException;

  /**
   * Used to locate information about one or more businesses. Returns a
   * businessList message that matches the conditions specified.
   *
   * @exception RegistryException;
   */
  BusinessList findBusiness(Vector nameVector,DiscoveryURLs discoveryURLs,IdentifierBag identifierBag,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  RelatedBusinessesList findRelatedBusinesses(String businessKey,KeyedReference keyedReference,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException;

  /**
   * "Used to locate specific services within a registered
   *  businessEntity. Return a serviceList message." From the
   *  XML spec (API, p18) it appears that the name, categoryBag,
   *  and tModelBag arguments are mutually exclusive.
   *
   * @exception RegistryException;
   */
  ServiceList findService(String businessKey,Vector nameVector,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException;

  /**
   * "Used to locate one or more tModel information structures. Returns a
   *  tModelList structure."
   *
   * @exception RegistryException;
   */
  TModelList findTModel(String name,CategoryBag categoryBag,IdentifierBag identifierBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException;

  /**
   * "Used to get full bindingTemplate information suitable for make one
   *  or more service requests. Returns a bindingDetail message."
   *
   * @exception RegistryException;
   */
  BindingDetail getBindingDetail(Vector bindingKeys)
    throws RegistryException;

  /**
   * "Used to get the full businessEntity information for one or more
   *  businesses. Returns a businessDetail message."
   *
   * @exception RegistryException;
   */
  BusinessDetail getBusinessDetail(Vector businessKeyVector)
    throws RegistryException;

  /**
   * "Used to get extended businessEntity information. Returns a
   *  businessDetailExt message."
   *
   * @exception RegistryException;
   */
  BusinessDetailExt getBusinessDetailExt(Vector businessKeyVector)
    throws RegistryException;
  
  /**
   * "Used to get full details for a given set of registered
   *  businessService data. Returns a serviceDetail message."
   *
   * @exception RegistryException;
   */
  ServiceDetail getServiceDetail(Vector serviceKeyVector)
    throws RegistryException;
  
  /**
   * "Used to get full details for a given set of registered tModel
   *  data. Returns a tModelDetail message."
   *
   * @exception RegistryException;
   */
  TModelDetail getTModelDetail(Vector tModelKeyVector)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  DispositionReport addPublisherAssertions(AuthInfo authInfo,Vector assertionVector)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  AssertionStatusReport getAssertionStatusReport(AuthInfo authInfo,String completionStatus)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  PublisherAssertions getPublisherAssertions(AuthInfo authInfo)
    throws RegistryException;

  /**
   * "Used to remove an existing bindingTemplate from the bindingTemplates
   *  collection that is part of a specified businessService structure."
   *
   * @exception RegistryException;
   */
  DispositionReport deleteBinding(AuthInfo authInfo,Vector bindingKeyVector)
    throws RegistryException;

  /**
   * "Used to delete registered businessEntity information from the registry."
   *
   * @exception RegistryException;
   */
  DispositionReport deleteBusiness(AuthInfo authInfo,Vector businessKeyVector)
    throws RegistryException;

  /**
   * "Used to delete an existing businessService from the businessServices
   *  collection that is part of a specified businessEntity."
   *
   * @exception RegistryException;
   */
  DispositionReport deleteService(AuthInfo authInfo,Vector serviceKeyVector)
    throws RegistryException;

  /**
   * "Used to delete registered information about a tModel.  If there
   *  are any references to a tModel when this call is made, the tModel
   *  will be marked deleted instead of being physically removed."
   *
   * @exception RegistryException;
   */
  DispositionReport deleteTModel(AuthInfo authInfo,Vector tModelKeyVector)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  DispositionReport deletePublisherAssertions(AuthInfo authInfo,Vector assertionVector)
    throws RegistryException;

  /**
   * "Used to inform an Operator Site that a previously provided
   *  authentication token is no longer valid.  See get_authToken."
   *
   * @exception RegistryException;
   */
  DispositionReport discardAuthToken(AuthInfo authInfo)
    throws RegistryException;

  /**
   * "Used to request an authentication token from an Operator Site.
   *  Authentication tokens are required to use all other APIs defined
   *  in the publishers API.  This server serves as the program's
   *  equivalent of a login request."
   *
   * @exception RegistryException;
   */
  AuthToken getAuthToken(String userID,String cred)
    throws RegistryException;

  /**
   * "Used to request an abbreviated synopsis of all information currently
   *  managed by a given individual."
   *
   * @exception RegistryException;
   */
  RegisteredInfo getRegisteredInfo(AuthInfo authInfo)
    throws RegistryException;

  /**
   * "Used to register new bindingTemplate information or update existing
   *  bindingTemplate information.  Use this to control information about
   *  technical capabilities exposed by a registered business."
   *
   * @exception RegistryException;
   */
  BindingDetail saveBinding(AuthInfo authInfo,Vector bindingVector)
    throws RegistryException;

  /**
   * "Used to register new businessEntity information or update existing
   *  businessEntity information.  Use this to control the overall
   *  information about the entire business.  Of the save_x APIs this one
   *  has the broadest effect."
   *
   * @exception RegistryException;
   */
  BusinessDetail saveBusiness(AuthInfo authInfo,Vector businessVector)
    throws RegistryException;

  /**
   * "Used to register or update complete information about a businessService
   *  exposed by a specified businessEntity."
   *
   * @exception RegistryException;
   */
  ServiceDetail saveService(AuthInfo authInfo,Vector serviceVector)
    throws RegistryException;

  /**
   * "Used to register or update complete information about a tModel."
   *
   * @exception RegistryException;
   */
  TModelDetail saveTModel(AuthInfo authInfo,Vector tModelVector)
    throws RegistryException;;

  /**
   * @exception RegistryException;
   */
  PublisherAssertions setPublisherAssertions(AuthInfo authInfo,Vector assertionVector)
    throws RegistryException;

  /**
   * "Used to delete an existing publisher accounts."
   *
   * @exception RegistryException;
   */
  DispositionReport deletePublisher(AuthInfo authInfo,Vector publisherIDVector)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  PublisherDetail getPublisherDetail(Vector publisherIDVector)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  PublisherList findPublisher(String publisherID,String name,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  PublisherDetail savePublisher(AuthInfo authInfo,Vector publisherVector)
    throws RegistryException;

  /**
   * @exception RegistryException;
   */
  DispositionReport validateValues(Vector businessVector,Vector serviceVector,Vector tModelVector)
    throws RegistryException;

  /**
   * @exception RegistryException
   */
  RegistryObject execute(RegistryObject request)
    throws RegistryException;
}