/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */

package org.apache.juddi.api.impl;

import java.rmi.RemoteException;
import java.util.List;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.juddi.validation.ValidatePublish;
import org.apache.juddi.validation.ValidatePublisher;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
@WebService(serviceName="JUDDIApiService", 
			endpointInterface="org.apache.juddi.v3_service.JUDDIApiPortType",
			targetNamespace = "urn:apache-org:juddi_api_v3_portType")
public class JUDDIApiImpl extends AuthenticatedService implements JUDDIApiPortType {

	/**
	 * Saves publisher(s) to the persistence layer.  This method is specific to jUDDI.
	 */
	public PublisherDetail savePublisher(SavePublisher body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
			
			new ValidatePublish(publisher).validateSavePublisher(em, body);
			
			PublisherDetail result = new PublisherDetail();
	
			List<org.apache.juddi.api_v3.Publisher> apiPublisherList = body.getPublisher();
			for (org.apache.juddi.api_v3.Publisher apiPublisher : apiPublisherList) {
				
				org.apache.juddi.model.Publisher modelPublisher = new org.apache.juddi.model.Publisher();
				
				MappingApiToModel.mapPublisher(apiPublisher, modelPublisher);
				
				Object existingUddiEntity = em.find(modelPublisher.getClass(), modelPublisher.getAuthorizedName());
				if (existingUddiEntity != null)
					em.remove(existingUddiEntity);
				
				em.persist(modelPublisher);
				
				result.getPublisher().add(apiPublisher);
			}
	
			tx.commit();
			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	/**
	 * Deletes publisher(s) from the persistence layer.  This method is specific to jUDDI.
	 */
	public void deletePublisher(DeletePublisher body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
			
			new ValidatePublish(publisher).validateDeletePublisher(em, body);
	
			List<String> entityKeyList = body.getPublisherId();
			for (String entityKey : entityKeyList) {
				Object obj = em.find(org.apache.juddi.model.Publisher.class, entityKey);
				em.remove(obj);
			}
	
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
	
	/**
	 * Retrieves publisher(s) from the persistence layer.  This method is specific to jUDDI.
	 */
	public PublisherDetail getPublisherDetail(GetPublisherDetail body)
			throws DispositionReportFaultMessage {

		new ValidatePublisher(null).validateGetPublisherDetail(body);

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			this.getEntityPublisher(em, body.getAuthInfo());
			
			PublisherDetail result = new PublisherDetail();
			
			List<String> publisherIdList = body.getPublisherId();
			for (String publisherId : publisherIdList) {
				
				org.apache.juddi.model.Publisher modelPublisher = em.find(org.apache.juddi.model.Publisher.class, publisherId);
				if (modelPublisher == null) {
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.PublisherNotFound", publisherId));
				}
				
				org.apache.juddi.api_v3.Publisher apiPublisher = new org.apache.juddi.api_v3.Publisher();
				
				MappingModelToApi.mapPublisher(modelPublisher, apiPublisher);
				
				result.getPublisher().add(apiPublisher);
			}
	
			tx.commit();
			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}

	}

	@SuppressWarnings("unchecked")
	public PublisherDetail getAllPublisherDetail(GetAllPublisherDetail body)
			throws DispositionReportFaultMessage, RemoteException {
		
		new ValidatePublisher(null).validateGetAllPublisherDetail(body);

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			this.getEntityPublisher(em, body.getAuthInfo());
			
			PublisherDetail result = new PublisherDetail();
			
			Query query = em.createQuery("SELECT p from Publisher as p");
			List<Publisher> modelPublisherList = query.getResultList();
			
			for (Publisher modelPublisher : modelPublisherList) {
				
				org.apache.juddi.api_v3.Publisher apiPublisher = new org.apache.juddi.api_v3.Publisher();
				
				MappingModelToApi.mapPublisher(modelPublisher, apiPublisher);
				
				result.getPublisher().add(apiPublisher);
			}
	
			tx.commit();
			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
}
