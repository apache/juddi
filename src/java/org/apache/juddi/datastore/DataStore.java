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
   * @throws UDDIException
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