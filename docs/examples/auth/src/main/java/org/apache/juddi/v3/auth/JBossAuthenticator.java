/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.v3.auth;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.juddi.model.AuthToken;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.AuthTokenRequiredException;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.UnknownUserException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.log4j.Logger;
import org.jboss.security.AuthenticationManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import java.security.Principal;

/**
 * This is a implementation of jUDDI's Authenticator interface, that uses the
 * JBoss authentication manager.
 * 
 * Usage:
 * 
 * To use this class you must add the following properties to the
 * juddiv3.properties file:
 * 
 * # The JBoss Authenticator
 * juddi.authenticator=org.apache.juddi.auth.JBossAuthenticator
 * 
 * # The security-domain, defined in $JBOSS/default/conf/login-config.xml
 * juddi.securityDomain=java:/jaas/other
 * 
 * @author Antoni Reus (areus@ibit.org)
 * @author Tom Cunningham (tcunning@apache.org)
 */
public class JBossAuthenticator implements Authenticator {
	// private reference to the logger
	private Logger logger = Logger.getLogger(this.getClass());

	// JBoss authentication manager
	AuthenticationManager authManager;

	public JBossAuthenticator() throws NamingException, ConfigurationException {
		init();
	}

	/**
     *
     */
	public String authenticate(final String userID, final String credential)
			throws AuthenticationException {
		if (userID == null) {
			throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidUserId", userID));
		}

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			// Create a principal for the userID
			Principal principal = new Principal() {
				public String getName() {
					return userID;
				}
			};
	
			if (!authManager.isValid(principal, credential)) {
				throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials"));
			} else {
				tx.begin();
				Publisher publisher = em.find(Publisher.class, userID);
				if (publisher == null) {
					publisher = new Publisher();
					publisher.setAuthorizedName(userID);
					publisher.setIsAdmin("false");
					publisher.setIsEnabled("true");
					publisher.setMaxBindingsPerService(199);
					publisher.setMaxBusinesses(100);
					publisher.setMaxServicesPerBusiness(100);
					publisher.setMaxTmodels(100);
					publisher.setPublisherName("Unknown");
					em.persist(publisher);
					tx.commit();
				}
			}
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
		return userID;
	}

	private void init() throws NamingException, ConfigurationException {
		String securityDomain = AppConfig.getConfiguration().getString(
				Property.JUDDI_SECURITY_DOMAIN,
				Property.DEFAULT_SECURITY_DOMAIN);

		// lookup the authentication manager.
		Context ctx = new InitialContext();
		authManager = (AuthenticationManager) ctx.lookup(securityDomain);
		ctx.close();
	}
	
	public UddiEntityPublisher identify(String authInfo, String authorizedName) throws AuthenticationException {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		Publisher publisher = null;
		try {
			tx.begin();
			publisher = em.find(Publisher.class, authorizedName);
			if (publisher == null)
				throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", authorizedName));
			
			AuthToken at = em.find(AuthToken.class, authInfo);
			if (at == null) 
				throw new AuthTokenRequiredException(new ErrorMessage("E_authTokenRequired", authInfo));				
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
		return publisher;
	}
}