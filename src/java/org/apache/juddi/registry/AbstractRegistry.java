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
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.request.AddPublisherAssertions;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.DeleteBinding;
import org.apache.juddi.datatype.request.DeleteBusiness;
import org.apache.juddi.datatype.request.DeletePublisher;
import org.apache.juddi.datatype.request.DeletePublisherAssertions;
import org.apache.juddi.datatype.request.DeleteService;
import org.apache.juddi.datatype.request.DeleteTModel;
import org.apache.juddi.datatype.request.DiscardAuthToken;
import org.apache.juddi.datatype.request.FindBinding;
import org.apache.juddi.datatype.request.FindBusiness;
import org.apache.juddi.datatype.request.FindPublisher;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.request.FindRelatedBusinesses;
import org.apache.juddi.datatype.request.FindService;
import org.apache.juddi.datatype.request.FindTModel;
import org.apache.juddi.datatype.request.GetAssertionStatusReport;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.GetBindingDetail;
import org.apache.juddi.datatype.request.GetBusinessDetail;
import org.apache.juddi.datatype.request.GetBusinessDetailExt;
import org.apache.juddi.datatype.request.GetPublisherAssertions;
import org.apache.juddi.datatype.request.GetPublisherDetail;
import org.apache.juddi.datatype.request.GetRegisteredInfo;
import org.apache.juddi.datatype.request.GetServiceDetail;
import org.apache.juddi.datatype.request.GetTModelDetail;
import org.apache.juddi.datatype.request.SaveBinding;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.request.SavePublisher;
import org.apache.juddi.datatype.request.SaveService;
import org.apache.juddi.datatype.request.SaveTModel;
import org.apache.juddi.datatype.request.SetPublisherAssertions;
import org.apache.juddi.datatype.request.ValidateValues;
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
public abstract class AbstractRegistry implements IRegistry
{
  /**
   * @exception RegistryException;
   */
  public DispositionReport addPublisherAssertions(AuthInfo authInfo,Vector assertionVector)
    throws RegistryException
  {
    AddPublisherAssertions request = new AddPublisherAssertions();
    request.setAuthInfo(authInfo);
    request.setPublisherAssertionVector(assertionVector);

    return (DispositionReport)execute(request);
  }

  /**
   * "Used to remove an existing bindingTemplate from the bindingTemplates
   *  collection that is part of a specified businessService structure."
   *
   * @exception RegistryException;
   */
  public DispositionReport deleteBinding(AuthInfo authInfo,Vector bindingKeyVector)
    throws RegistryException
  {
    DeleteBinding request = new DeleteBinding();
    request.setAuthInfo(authInfo);
    request.setBindingKeyVector(bindingKeyVector);

    return (DispositionReport)execute(request);
  }

  /**
   * "Used to delete registered businessEntity information from the registry."
   *
   * @exception RegistryException;
   */
  public DispositionReport deleteBusiness(AuthInfo authInfo,Vector businessKeyVector)
    throws RegistryException
  {
    DeleteBusiness request = new DeleteBusiness();
    request.setAuthInfo(authInfo);
    request.setBusinessKeyVector(businessKeyVector);

    return (DispositionReport)execute(request);
  }

  /**
   * "Used to delete an existing publisher accounts."
   *
   * @exception RegistryException;
   */
  public DispositionReport deletePublisher(AuthInfo authInfo,Vector publisherIDVector)
    throws RegistryException
  {
    DeletePublisher request = new DeletePublisher();
    request.setAuthInfo(authInfo);
    request.setPublisherIDVector(publisherIDVector);

    return (DispositionReport)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public DispositionReport deletePublisherAssertions(AuthInfo authInfo,Vector assertionVector)
    throws RegistryException
  {
    DeletePublisherAssertions request = new DeletePublisherAssertions();
    request.setAuthInfo(authInfo);
    request.setPublisherAssertionVector(assertionVector);

    return (DispositionReport)execute(request);
  }

  /**
   * "Used to delete an existing businessService from the businessServices
   *  collection that is part of a specified businessEntity."
   *
   * @exception RegistryException;
   */
  public DispositionReport deleteService(AuthInfo authInfo,Vector serviceKeyVector)
    throws RegistryException
  {
    DeleteService request = new DeleteService();
    request.setAuthInfo(authInfo);
    request.setServiceKeyVector(serviceKeyVector);

    return (DispositionReport)execute(request);
  }

  /**
   * "Used to delete registered information about a tModel.  If there
   *  are any references to a tModel when this call is made, the tModel
   *  will be marked deleted instead of being physically removed."
   *
   * @exception RegistryException;
   */
  public DispositionReport deleteTModel(AuthInfo authInfo,Vector tModelKeyVector)
    throws RegistryException
  {
    DeleteTModel request = new DeleteTModel();
    request.setAuthInfo(authInfo);
    request.setTModelKeyVector(tModelKeyVector);

    return (DispositionReport)execute(request);
  }

  /**
   * "Used to inform an Operator Site that a previously provided
   *  authentication token is no longer valid.  See get_authToken."
   *
   * @exception RegistryException;
   */
  public DispositionReport discardAuthToken(AuthInfo authInfo)
    throws RegistryException
  {
    DiscardAuthToken request = new DiscardAuthToken();
    request.setAuthInfo(authInfo);

    return (DispositionReport)execute(request);
  }

  /**
   * "Used to locate specific bindings within a registered
   *  businessService. Returns a bindingDetail message."
   *
   * @exception RegistryException
   */
  public BindingDetail findBinding(String serviceKey,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException
  {
    FindBinding request = new FindBinding();
    request.setServiceKey(serviceKey);
    request.setCategoryBag(categoryBag);
    request.setTModelBag(tModelBag);
    request.setFindQualifiers(findQualifiers);
    request.setMaxRows(maxRows);

    return (BindingDetail)execute(request);
  }

  /**
   * Used to locate information about one or more businesses. Returns a
   * businessList message that matches the conditions specified.
   *
   * @exception RegistryException;
   */
  public BusinessList findBusiness(Vector nameVector,DiscoveryURLs discoveryURLs,IdentifierBag identifierBag,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException
  {
    FindBusiness request = new FindBusiness();
    request.setNameVector(nameVector);
    request.setDiscoveryURLs(discoveryURLs);
    request.setIdentifierBag(identifierBag);
    request.setCategoryBag(categoryBag);
    request.setTModelBag(tModelBag);
    request.setFindQualifiers(findQualifiers);
    request.setMaxRows(maxRows);

    return (BusinessList)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public PublisherList findPublisher(String publisherID,String name,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException
  {
    FindPublisher request = new FindPublisher();
    request.setName(name);
    request.setFindQualifiers(findQualifiers);
    request.setMaxRows(maxRows);

    return (PublisherList)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public RelatedBusinessesList findRelatedBusinesses(String businessKey,KeyedReference keyedReference,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException
  {
    FindRelatedBusinesses request = new FindRelatedBusinesses();
    request.setBusinessKey(businessKey);
    request.setKeyedReference(keyedReference);
    request.setFindQualifiers(findQualifiers);
    request.setMaxRows(maxRows);

    return (RelatedBusinessesList)execute(request);
  }

  /**
   * "Used to locate specific services within a registered
   *  businessEntity. Return a serviceList message." From the
   *  XML spec (API, p18) it appears that the name, categoryBag,
   *  and tModelBag arguments are mutually exclusive.
   *
   * @exception RegistryException;
   */
  public ServiceList findService(String businessKey,Vector nameVector,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException
  {
    FindService request = new FindService();
    request.setBusinessKey(businessKey);
    request.setNameVector(nameVector);
    request.setCategoryBag(categoryBag);
    request.setTModelBag(tModelBag);
    request.setFindQualifiers(findQualifiers);
    request.setMaxRows(maxRows);

    return (ServiceList)execute(request);
  }

  /**
   * "Used to locate one or more tModel information structures. Returns a
   *  tModelList structure."
   *
   * @exception RegistryException;
   */
  public TModelList findTModel(String name,CategoryBag categoryBag,IdentifierBag identifierBag,FindQualifiers findQualifiers,int maxRows)
    throws RegistryException
  {
    FindTModel request = new FindTModel();
    request.setName(name);
    request.setCategoryBag(categoryBag);
    request.setIdentifierBag(identifierBag);
    request.setFindQualifiers(findQualifiers);
    request.setMaxRows(maxRows);

    return (TModelList)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public AssertionStatusReport getAssertionStatusReport(AuthInfo authInfo,String completionStatus)
    throws RegistryException
  {
    GetAssertionStatusReport request = new GetAssertionStatusReport();
    request.setAuthInfo(authInfo);
    request.setCompletionStatus(completionStatus);

    return (AssertionStatusReport)execute(request);
  }

  /**
   * "Used to request an authentication token from an Operator Site.
   *  Authentication tokens are required to use all other APIs defined
   *  in the publishers API.  This server serves as the program's
   *  equivalent of a login request."
   *
   * @exception RegistryException;
   */
  public AuthToken getAuthToken(String userID,String cred)
    throws RegistryException
  {
    GetAuthToken request = new GetAuthToken();
    request.setUserID(userID);
    request.setCredential(cred);

    return (AuthToken)execute(request);
  }

  /**
   * Used to get full bindingTemplate information suitable for a
   * particular business service. Returns a bindingDetail message.
   *
   * @exception RegistryException;
   */
  public BindingDetail getBindingDetail(String bindingKey)
    throws RegistryException
  {
  	Vector keys = new Vector(1);
  	keys.addElement(bindingKey);
  	
  	return getBindingDetail(keys);
  }

  /**
   * "Used to get full bindingTemplate information suitable for make one
   *  or more service requests. Returns a bindingDetail message."
   *
   * @exception RegistryException;
   */
  public BindingDetail getBindingDetail(Vector bindingKeys)
    throws RegistryException
  {
    GetBindingDetail request = new GetBindingDetail();
    request.setBindingKeyVector(bindingKeys);

    return (BindingDetail)execute(request);
  }

  /**
   * Used to get the full businessEntity information for a 
   * particular business entity. Returns a businessDetail message.
   *
   * @exception RegistryException;
   */
  public BusinessDetail getBusinessDetail(String businessKey)
    throws RegistryException
  {
  	Vector keys = new Vector(1);
  	keys.addElement(businessKey);

    return getBusinessDetail(keys);
  }

  /**
   * "Used to get the full businessEntity information for one or more
   *  businesses. Returns a businessDetail message."
   *
   * @exception RegistryException;
   */
  public BusinessDetail getBusinessDetail(Vector businessKeyVector)
    throws RegistryException
  {
    GetBusinessDetail request = new GetBusinessDetail();
    request.setBusinessKeyVector(businessKeyVector);

    return (BusinessDetail)execute(request);
  }

  /**
   * "Used to get extended businessEntity information. Returns a
   *  businessDetailExt message."
   *
   * @exception RegistryException;
   */
  public BusinessDetailExt getBusinessDetailExt(String businessKey)
    throws RegistryException
  {
  	Vector keys = new Vector(1);
  	keys.addElement(businessKey);

    return getBusinessDetailExt(keys);
  }

  /**
   * "Used to get extended businessEntity information. Returns a
   *  businessDetailExt message."
   *
   * @exception RegistryException;
   */
  public BusinessDetailExt getBusinessDetailExt(Vector businessKeyVector)
    throws RegistryException
  {
    GetBusinessDetailExt request = new GetBusinessDetailExt();
    request.setBusinessKeyVector(businessKeyVector);

    return (BusinessDetailExt)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public PublisherAssertions getPublisherAssertions(AuthInfo authInfo)
    throws RegistryException
  {
    GetPublisherAssertions request = new GetPublisherAssertions();
    request.setAuthInfo(authInfo);

    return (PublisherAssertions)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public PublisherDetail getPublisherDetail(Vector publisherIDVector)
    throws RegistryException
  {
    GetPublisherDetail request = new GetPublisherDetail();
    request.setPublisherIDVector(publisherIDVector);

    return (PublisherDetail)execute(request);
  }

  /**
   * "Used to request an abbreviated synopsis of all information currently
   *  managed by a given individual."
   *
   * @exception RegistryException;
   */
  public RegisteredInfo getRegisteredInfo(AuthInfo authInfo)
    throws RegistryException
  {
    GetRegisteredInfo request = new GetRegisteredInfo();
    request.setAuthInfo(authInfo);

    return (RegisteredInfo)execute(request);
  }

  /**
   * "Used to get full details for a particular registered
   *  businessService. Returns a serviceDetail message."
   *
   * @exception RegistryException;
   */
  public ServiceDetail getServiceDetail(String serviceKey)
    throws RegistryException
  {
  	Vector keys = new Vector(1);
  	keys.addElement(serviceKey);

    return getServiceDetail(keys);
  }

  /**
   * "Used to get full details for a given set of registered
   *  businessService data. Returns a serviceDetail message."
   *
   * @exception RegistryException;
   */
  public ServiceDetail getServiceDetail(Vector serviceKeyVector)
    throws RegistryException
  {
    GetServiceDetail request = new GetServiceDetail();
    request.setServiceKeyVector(serviceKeyVector);

    return (ServiceDetail)execute(request);
  }

  /**
   * "Used to get full details for a particular registered 
   *  TModel. Returns a tModelDetail message."
   *
   * @exception RegistryException;
   */
  public TModelDetail getTModelDetail(String tModelKey)
    throws RegistryException
  {
  	Vector keys = new Vector(1);
  	keys.addElement(tModelKey);

    return getTModelDetail(keys);
  }

  /**
   * "Used to get full details for a given set of registered tModel
   *  data. Returns a tModelDetail message."
   *
   * @exception RegistryException;
   */
  public TModelDetail getTModelDetail(Vector tModelKeyVector)
    throws RegistryException
  {
    GetTModelDetail request = new GetTModelDetail();
    request.setTModelKeyVector(tModelKeyVector);

    return (TModelDetail)execute(request);
  }

  /**
   * "Used to register new bindingTemplate information or update existing
   *  bindingTemplate information.  Use this to control information about
   *  technical capabilities exposed by a registered business."
   *
   * @exception RegistryException;
   */
  public BindingDetail saveBinding(AuthInfo authInfo,Vector bindingVector)
    throws RegistryException
  {
    SaveBinding request = new SaveBinding();
    request.setAuthInfo(authInfo);
    request.setBindingTemplateVector(bindingVector);

    return (BindingDetail)execute(request);
  }

  /**
   * "Used to register new businessEntity information or update existing
   *  businessEntity information.  Use this to control the overall
   *  information about the entire business.  Of the save_x APIs this one
   *  has the broadest effect."
   *
   * @exception RegistryException;
   */
  public BusinessDetail saveBusiness(AuthInfo authInfo,Vector businessVector)
    throws RegistryException
  {
    SaveBusiness request = new SaveBusiness();
    request.setAuthInfo(authInfo);
    request.setBusinessEntityVector(businessVector);

    return (BusinessDetail)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public PublisherDetail savePublisher(AuthInfo authInfo,Vector publisherVector)
    throws RegistryException
  {
    SavePublisher request = new SavePublisher();
    request.setAuthInfo(authInfo);
    request.setPublisherVector(publisherVector);

    return (PublisherDetail)execute(request);
  }

  /**
   * "Used to register or update complete information about a businessService
   *  exposed by a specified businessEntity."
   *
   * @exception RegistryException;
   */
  public ServiceDetail saveService(AuthInfo authInfo,Vector serviceVector)
    throws RegistryException
  {
    SaveService request = new SaveService();
    request.setAuthInfo(authInfo);
    request.setServiceVector(serviceVector);

    return (ServiceDetail)execute(request);
  }

  /**
   * "Used to register or update complete information about a tModel."
   *
   * @exception RegistryException;
   */
  public TModelDetail saveTModel(AuthInfo authInfo,Vector tModelVector)
    throws RegistryException
  {
    SaveTModel request = new SaveTModel();
    request.setAuthInfo(authInfo);
    request.setTModelVector(tModelVector);

    return (TModelDetail)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public PublisherAssertions setPublisherAssertions(AuthInfo authInfo,Vector assertionVector)
    throws RegistryException
  {
    SetPublisherAssertions request = new SetPublisherAssertions();
    request.setAuthInfo(authInfo);
    request.setPublisherAssertionVector(assertionVector);

    return (PublisherAssertions)execute(request);
  }

  /**
   * @exception RegistryException;
   */
  public DispositionReport validateValues(Vector businessVector,Vector serviceVector,Vector tModelVector)
    throws RegistryException
	{
  	ValidateValues request = new ValidateValues();
  	request.setBusinessEntityVector(businessVector);
  	request.setBusinessServiceVector(serviceVector);
  	request.setTModelVector(tModelVector);

    return (DispositionReport)execute(request);
	}
}