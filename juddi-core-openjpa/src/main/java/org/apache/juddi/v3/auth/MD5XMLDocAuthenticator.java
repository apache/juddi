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

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.RegistryException;
import org.apache.juddi.v3.error.UnknownUserException;

/**
 * Uses MD5 hashes for passwords
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class MD5XMLDocAuthenticator extends XMLDocAuthenticator {
	
	private Log logger = LogFactory.getLog(this.getClass());
	/**
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ConfigurationException 
	 * 
	 */
	public MD5XMLDocAuthenticator() throws JAXBException, IOException, ConfigurationException {
		super();
	}
        /**
         * A private constructor used for calculating hashes only
         * @param x 
         */
        private MD5XMLDocAuthenticator(boolean x)  {
            super(x);
	}
	@Override
	protected String getFilename() throws ConfigurationException {
		return AppConfig.getConfiguration().getString(Property.JUDDI_USERSFILE, Property.DEFAULT_HASHED_XML_USERSFILE);
	}
	/**
	 *
	 */
	@Override
	public String authenticate(String userID, String credential)
	throws AuthenticationException, FatalErrorException {
		preProcess(userID, credential);
		String encryptedCredential = hash(credential);
		return postProcess(userID, encryptedCredential);
	}
	/**
	 *
	 */
	private String hash(String str) throws FatalErrorException {
		try {
                        return DigestUtils.md5Hex(str)       ;
		} catch (Exception e) {
			logger.error("Exception caught hashing password", e);
			throw new FatalErrorException(new ErrorMessage(
					"errors.auth.cryptor.InvalidKey", e.getMessage()));
		} 
	}
	/**
	 * @param userID
	 * @param credential
	 * @throws RegistryException
	 */
	private void preProcess(String userID, String credential)
	throws AuthenticationException {
		// a userID must be specified.
		if (userID == null) {
			throw new UnknownUserException(new ErrorMessage(
					"errors.auth.InvalidUserId"));
		}
		// credential (password) must be specified.
		if (credential == null) {
			throw new UnknownUserException(new ErrorMessage(
			"errors.auth.InvalidCredentials"));
		}
	}
	/**
	 * @param userID
	 * @param encryptedCredential
	 * @return user id
	 * @throws AuthenticationException
	 */
	private String postProcess(String userID, String encryptedCredential)
	throws AuthenticationException {
		if (userTable.containsKey(userID)) {
			User user = (User) userTable.get(userID);
			if ((user.getPassword() == null)
					|| (!encryptedCredential.equals(user.getPassword()))) {
				throw new UnknownUserException(new ErrorMessage(
						"errors.auth.InvalidCredentials", userID));
			}
		} else {
			throw new UnknownUserException(new ErrorMessage(
					"errors.auth.InvalidUserId", userID));
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
                        log.error("config exception! " + userID, ex);
                }
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();
                        Publisher publisher = em.find(Publisher.class, userID);
                        if (publisher == null) {
                                log.warn("Publisher \"" + userID + "\" was not found in the database, adding the publisher in on the fly.");
                                publisher = new Publisher();
                                publisher.setAuthorizedName(userID);
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
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
		return userID;
	}
        
         public static void main(String[] args) throws Exception
         {
             System.out.print("Password: ");
             char[] readPassword = System.console().readPassword();
             System.out.println("Cipher: " + new MD5XMLDocAuthenticator(true).hash(new String(readPassword)));
         }
}
