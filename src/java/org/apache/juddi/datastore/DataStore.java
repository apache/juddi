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
package org.apache.juddi.datastore;

import java.util.Vector;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.PublisherInfo;
import org.apache.juddi.datatype.response.ServiceInfo;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public interface DataStore
{
  /**
   * release this DataStore
   */
  void release();

  /**
   * begin transaction
   */
  void beginTrans()
    throws RegistryException;

  /**
   * commit transaction
   */
  void commit()
    throws RegistryException;

  /**
   * rollback transaction
   */
  void rollback()
    throws RegistryException;

  /**
   * verify that the individual or system identified by the 'userID'
   * is using the correct password and has the 'authority' to publish
   * to the UDDI registry.
   *
   * @param publisherID
   * @return publisher
   * @throws RegistryException
   */
  Publisher getPublisher(String publisherID)
    throws RegistryException;

  /**
   *
   * @param publisherID
   * @return boolean
   * @throws RegistryException
   */
  boolean isAdministrator(String publisherID)
    throws RegistryException;

  /**
   *
   * @param publisher
   * @return a secure Token
   * @throws RegistryException
   */
  String generateToken(Publisher publisher)
    throws RegistryException;

  /**
   *
   */
  void storeAuthToken(String token,Publisher publisher)
    throws RegistryException;

  /**
   *
   */
  void retireAuthToken(String token)
    throws RegistryException;

  /**
   *
   */
  Publisher getAuthTokenPublisher(String token)
    throws RegistryException;

  /**
   *
   */
  boolean isAuthTokenExpired(String token)
    throws RegistryException;

  /**
   *
   */
  void touchAuthToken(String token)
    throws RegistryException;

  /**
   *
   */
  void saveBusiness(BusinessEntity business,String publisherID)
    throws RegistryException;

  /**
   *
   */
  BusinessEntity fetchBusiness(String businessKey)
    throws RegistryException;

  /**
   *
   */
  void deleteBusiness(String businessKey)
    throws RegistryException;

  /**
   *
   */
  boolean isBusinessPublisher(String businessKey,String publisherID)
    throws RegistryException;

  /**
   *
   */
  boolean isValidBusinessKey(String businessKey)
    throws RegistryException;

  /**
   *
   */
  void saveService(BusinessService service)
    throws RegistryException;

  /**
   *
   */
  BusinessService fetchService(String serviceKey)
    throws RegistryException;

  /**
   *
   */
  void deleteService(String serviceKey)
    throws RegistryException;

  /**
   *
   */
  boolean isValidServiceKey(String serviceKey)
    throws RegistryException;

  /**
   *
   */
  boolean isServicePublisher(String serviceKey,String publisherID)
    throws RegistryException;

  /**
   *
   */
  void saveBinding(BindingTemplate binding)
    throws RegistryException;

  /**
   *
   */
  BindingTemplate fetchBinding(String bindingKey)
    throws RegistryException;

  /**
   *
   */
  void deleteBinding(String bindingKey)
    throws RegistryException;

  /**
   *
   */
  boolean isValidBindingKey(String bindingKey)
    throws RegistryException;

  /**
   *
   */
  boolean isBindingPublisher(String bindingKey,String publisherID)
    throws RegistryException;

  /**
   *
   */
  void saveTModel(TModel tModel,String publisherID)
    throws RegistryException;

  /**
   *
   */
  TModel fetchTModel(String tModelKey)
    throws RegistryException;

  /**
   *
   */
  void deleteTModel(String tModelKey)
    throws RegistryException;

  /**
   *
   */
  void markTModelAsDeleted(String tModelKey)
   throws RegistryException;

  /**
   *
   */
  boolean isValidTModelKey(String tModelKey)
    throws RegistryException;

  /**
   *
   */
  boolean isTModelPublisher(String tModelKey,String publisherID)
    throws RegistryException;

  /**
   *
   */
  BusinessInfo fetchBusinessInfo(String businessKey)
    throws RegistryException;

  /**
   *
   */
  ServiceInfo fetchServiceInfo(String serviceKey)
    throws RegistryException;

  /**
   *
   */
  TModelInfo fetchTModelInfo(String tModelKey)
    throws RegistryException;

  /**
   *
   */
  Vector findBusiness(Vector nameVector,
                      DiscoveryURLs discoveryURLs,
                      IdentifierBag identifierBag,
                      CategoryBag categoryBag,
                      TModelBag tModelBag,
                      FindQualifiers findQualifiers)
    throws RegistryException;

  /**
   *
   */
  Vector findService(String businessKey,
                     Vector names,
                     CategoryBag categoryBag,
                     TModelBag tModelBag,
                     FindQualifiers findQualifiers)
    throws RegistryException;

  /**
   *
   */
  Vector findBinding(String serviceKey,
                     CategoryBag categoryBag,
                     TModelBag tModelbag,
                     FindQualifiers findQualifiers)
    throws RegistryException;

  /**
   *
   */
  Vector findTModel(String name,
                    CategoryBag categoryBag,
                    IdentifierBag identifierBag,
                    FindQualifiers findQualifiers)
    throws RegistryException;

  /**
   *
   */
  Vector findRelatedBusinesses(String businessKey,
                               KeyedReference keyedReference,
                               FindQualifiers findQualifiers)
    throws RegistryException;

  /**
   *
   */
  Vector findRegisteredBusinesses(String publisherID)
    throws RegistryException;

  /**
   *
   */
  Vector findRegisteredTModels(String publisherID)
    throws RegistryException;

  /**
   *
   */
  void saveAssertions(String publisherID,Vector assertionVector)
    throws RegistryException;

  /**
   *
   */
  void deleteAssertions(String publisherID,Vector assertionVector)
    throws RegistryException;

  /**
   *
   */
  Vector getAssertions(String publisherID)
    throws RegistryException;

  /**
   *
   */
  Vector setAssertions(String publisherID,Vector assertionVector)
    throws RegistryException;

  /**
   *
   */
  Vector getAssertionStatusItems(String publisherID,
                                 String completionStatus)
    throws RegistryException;

  /**
   *
   */
  void savePublisher(Publisher publisher)
    throws RegistryException;

  /**
   *
   */
  void deletePublisher(String publisherID)
    throws RegistryException;

  /**
   *
   */
  PublisherInfo fetchPublisherInfo(String publisherID)
    throws RegistryException;

  /**
   *
   */
  Vector findPublisher(String name,FindQualifiers findQualifiers)
    throws RegistryException;
}