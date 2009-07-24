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

import java.util.Date;
import java.util.UUID;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

import org.apache.juddi.auth.AuthenticatorFactory;
import org.apache.juddi.auth.Authenticator;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.error.UnknownUserException;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.mapping.MappingModelToApi;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@WebService(serviceName="UDDISecurityService", 
			endpointInterface="org.uddi.v3_service.UDDISecurityPortType",
			targetNamespace = "urn:uddi-org:api_v3_portType")
public class UDDISecurityImpl extends AuthenticatedService implements UDDISecurityPortType {

	public static final String AUTH_TOKEN_PREFIX = "authtoken:";

	public void discardAuthToken(DiscardAuthToken body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			
			this.getEntityPublisher(em, body.getAuthInfo());
			
			org.apache.juddi.model.AuthToken modelAuthToken = em.find(org.apache.juddi.model.AuthToken.class, body.getAuthInfo());
			if (modelAuthToken != null) {
				modelAuthToken.setLastUsed(new Date());
				modelAuthToken.setNumberOfUses(modelAuthToken.getNumberOfUses() + 1);
				modelAuthToken.setTokenState(AUTHTOKEN_RETIRED);
			}
	
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}


	public AuthToken getAuthToken(GetAuthToken body)
			throws DispositionReportFaultMessage {

		Authenticator authenticator = AuthenticatorFactory.getAuthenticator();
		
		String publisherId = authenticator.authenticate(body.getUserID(), body.getCred());
		
		if (publisherId == null || publisherId.length() == 0)
			throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials", publisherId));
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
					
			// Generate auth token and store it!
			String authInfo = AUTH_TOKEN_PREFIX + UUID.randomUUID();
			org.apache.juddi.model.AuthToken modelAuthToken = new org.apache.juddi.model.AuthToken();
			if (authInfo != null) {
				modelAuthToken.setAuthToken(authInfo);
				modelAuthToken.setCreated(new Date());
				modelAuthToken.setLastUsed(new Date());
				modelAuthToken.setAuthorizedName(publisherId);
				modelAuthToken.setNumberOfUses(0);
				modelAuthToken.setTokenState(AUTHTOKEN_ACTIVE);
				
				em.persist(modelAuthToken);
			}
			
			org.uddi.api_v3.AuthToken apiAuthToken = new org.uddi.api_v3.AuthToken();
			
			MappingModelToApi.mapAuthToken(modelAuthToken, apiAuthToken);
			
			tx.commit();
			return apiAuthToken;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
}
