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

package org.apache.juddi.v3.auth;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.UnknownUserException;

/**
 * This is the default implementation of jUDDI's Authenticator interface, which
 * if the user id has an associated publisher, and adds the publisher is this is not the
 * case. Please do NOT use this class in production.
 *
 * @author Steve Viens (sviens@apache.org)
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class JUDDIAuthenticator implements Authenticator {
	
	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * 
	 * @return the userId that came in on the request providing the user has a publishing account in jUDDI.
	 */
	public String authenticate(String authorizedName, String credential) throws AuthenticationException {
		if (authorizedName==null || "".equals(authorizedName)) {
			throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", authorizedName));
		}
                int MaxBindingsPerService = -1;
                int MaxServicesPerBusiness = -1;
                int MaxTmodels = -1;
                int MaxBusinesses = -1;
                try {
                        MaxBindingsPerService = AppConfig.getConfiguration().getInt(Property.JUDDI_MAX_BINDINGS_PER_SERVICE, -1);
                        MaxServicesPerBusiness = AppConfig.getConfiguration().getInt(Property.JUDDI_MAX_SERVICES_PER_BUSINESS, -1);
                        MaxTmodels = AppConfig.getConfiguration().getInt(Property.JUDDI_MAX_TMODELS_PER_PUBLISHER, -1);
                        MaxBusinesses = AppConfig.getConfiguration().getInt(Property.JUDDI_MAX_BUSINESSES_PER_PUBLISHER, -1);
                } catch (Exception ex) {
                        MaxBindingsPerService = -1;
                        MaxServicesPerBusiness = -1;
                        MaxTmodels = -1;
                        MaxBusinesses = -1;
                        log.error("config exception! " + authorizedName, ex);
                }
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Publisher publisher = em.find(Publisher.class, authorizedName);
			if (publisher == null) {
				log.warn("Publisher was not found, adding the publisher in on the fly.");
				publisher = new Publisher();
				publisher.setAuthorizedName(authorizedName);
				publisher.setIsAdmin("false");
				publisher.setIsEnabled("true");
				publisher.setMaxBindingsPerService(MaxBindingsPerService);
                                publisher.setMaxBusinesses(MaxBusinesses);
                                publisher.setMaxServicesPerBusiness(MaxServicesPerBusiness);
                                publisher.setMaxTmodels(MaxTmodels);
				publisher.setPublisherName("Unknown");
				em.persist(publisher);
				tx.commit();
			}
			return authorizedName;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
	
	public UddiEntityPublisher identify(String authInfo, String authorizedName) throws AuthenticationException {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Publisher publisher = em.find(Publisher.class, authorizedName);
			if (publisher == null)
				throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", authorizedName));
			
			return publisher;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
}