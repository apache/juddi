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
import javax.xml.ws.WebServiceContext;

import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.SecurityQuery;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.v3.auth.Authenticator;
import org.apache.juddi.v3.auth.AuthenticatorFactory;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.UnknownUserException;

/**
 * This class implements the UDDI Security Service and basically handles all authentication requests
 * for jUDDI. These authentication requests are routed to the appropriately configured
 * authenticator for validation, then persisted in the database until they either
 * expire or are discarded.
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a> (and many others)
 */
@WebService(serviceName="UDDISecurityService", 
			endpointInterface="org.uddi.v3_service.UDDISecurityPortType",
			targetNamespace = "urn:uddi-org:api_v3_portType")
public class UDDISecurityImpl extends AuthenticatedService implements UDDISecurityPortType {

	public static final String AUTH_TOKEN_PREFIX = "authtoken:";
        private UDDIServiceCounter serviceCounter;

        public UDDISecurityImpl() {
            super();
            serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(UDDISecurityImpl.class);
        }
        
        /**
         * used for unit tests only
         * @param ctx 
         */
        protected UDDISecurityImpl(WebServiceContext ctx) {
            super();
            this.ctx = ctx;
            serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(UDDISecurityImpl.class);
        }
	
        @Override
	public void discardAuthToken(DiscardAuthToken body)
			throws DispositionReportFaultMessage {
	        long startTime = System.currentTimeMillis();
	    
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
                                logger.info("AUDIT: AuthToken discarded for " + modelAuthToken.getAuthorizedName() + " from " + getRequestorsIPAddress());
			}
	
			tx.commit();
                        
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(SecurityQuery.DISCARD_AUTHTOKEN, 
                                QueryStatus.SUCCESS, procTime);
                } catch (DispositionReportFaultMessage drfm) {
                    logger.info("AUDIT: AuthToken discard request aborted, issued from " + getRequestorsIPAddress());
                    long procTime = System.currentTimeMillis() - startTime;
                    serviceCounter.update(SecurityQuery.DISCARD_AUTHTOKEN, 
                            QueryStatus.FAILED, procTime);                      
                    throw drfm;                                                                                                 
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}


        @Override
	public AuthToken getAuthToken(GetAuthToken body)
			throws DispositionReportFaultMessage {
            
                logger.info("AUDIT: AuthToken request for " + body.getUserID() + " from " + getRequestorsIPAddress());
		Authenticator authenticator = AuthenticatorFactory.getAuthenticator();
		
		String publisherId = authenticator.authenticate(body.getUserID(), body.getCred());
		
		return getAuthToken(publisherId);
	}
	
	public AuthToken getAuthToken(String publisherId)
            throws DispositionReportFaultMessage {
	        long startTime = System.currentTimeMillis();

		if (publisherId == null || publisherId.length() == 0)
			throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials", publisherId));

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			//Check if this publisher exists 
			Publisher publisher = em.find(Publisher.class, publisherId);
			if (publisher == null)
				throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials", publisherId));

			// Generate auth token and store it!
			String authInfo = AUTH_TOKEN_PREFIX + UUID.randomUUID();
			org.apache.juddi.model.AuthToken modelAuthToken = new org.apache.juddi.model.AuthToken();
                        modelAuthToken.setAuthToken(authInfo);
                        modelAuthToken.setCreated(new Date());
                        modelAuthToken.setLastUsed(new Date());
                        modelAuthToken.setAuthorizedName(publisherId);
                        modelAuthToken.setNumberOfUses(0);
                        modelAuthToken.setTokenState(AUTHTOKEN_ACTIVE);
                        modelAuthToken.setIPAddress(this.getRequestorsIPAddress());
                        em.persist(modelAuthToken);

			org.uddi.api_v3.AuthToken apiAuthToken = new org.uddi.api_v3.AuthToken();

			MappingModelToApi.mapAuthToken(modelAuthToken, apiAuthToken);

			tx.commit();
                        logger.info("AUDIT: AuthToken issued for " + modelAuthToken.getAuthorizedName() + " from " + getRequestorsIPAddress());
	                long procTime = System.currentTimeMillis() - startTime;
	                serviceCounter.update(SecurityQuery.GET_AUTHTOKEN, 
	                        QueryStatus.SUCCESS, procTime);

			return apiAuthToken;
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.currentTimeMillis() - startTime;
                    serviceCounter.update(SecurityQuery.GET_AUTHTOKEN, 
                            QueryStatus.FAILED, procTime);                      
                    logger.info("AUDIT: AuthToken issue FAILED " + publisherId + " from " + getRequestorsIPAddress());
                    throw drfm;                                                                                                 
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
}
