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
import java.io.InputStream;
import java.util.Hashtable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.UnknownUserException;

/**
 * This is a simple implementation of jUDDI's Authenticator interface. The credential
 * store is simply an unencrypted xml document called 'juddi.users' that can be
 * found in jUDDI's config directory. Below is an example of what you might find
 * in this document.
 *
 *     Example juddi.users document:
 *     =============================
 *     <?xml version="1.0" encoding="UTF-8"?>
 *     <juddi-users>
 *       <user userid="sviens" password="password" />
 *       <user userid="griddell" password="password" />
 *       <user userid="bhablutzel" password="password" />
 *     </juddi-users>
 *
 * @author Steve Viens (sviens@apache.org)
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class XMLDocAuthenticator implements Authenticator
{
	private static Log log = LogFactory.getLog(AuthenticatorFactory.class);
	/** Container for the user credentials */
	Hashtable<String,User> userTable;
	
	/**
	 *
	 */
	public XMLDocAuthenticator() throws JAXBException, IOException, ConfigurationException {
		readUserFile();
	}
	
	protected String getFilename() throws ConfigurationException {
		return AppConfig.getConfiguration().getString(Property.JUDDI_USERSFILE, Property.DEFAULT_XML_USERSFILE);
	}
	/**
	 * Read user data from the juddi-users file.
	 * 
	 * @throws IOException when the file cannot be opened
	 *         JAXBException when the content is misformed.
	 * @throws ConfigurationException 
	 */
	public synchronized void readUserFile() throws JAXBException, IOException, ConfigurationException
	{
		userTable = new Hashtable<String,User>();
		String usersFileName = getFilename();
		log.info("Reading jUDDI Users File: " + usersFileName + "...");
		InputStream stream = ClassUtil.getResource(usersFileName, this.getClass()).openStream();
		JAXBContext jaxbContext=JAXBContext.newInstance(JuddiUsers.class);
		Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<JuddiUsers> element = unMarshaller.unmarshal(new StreamSource(stream),JuddiUsers.class);
		JuddiUsers users = element.getValue();
		for (User user : users.getUser()) {
			userTable.put(user.getUserid(), user);
			log.debug("Loading user credentials for user: " + user.getUserid());
		}
	}

	/**
	 *
	 */
	public String authenticate(String userID,String credential)
	throws AuthenticationException, FatalErrorException
	{
		// a userID must be specified.
		if (userID == null)
			throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidUserId"));

		// credential (password) must be specified.
		if (credential == null)
			throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials"));

		if (userTable.containsKey(userID))
		{
			User user = (User)userTable.get(userID);
			if ((user.getPassword() == null) || (!credential.equals(user.getPassword())))
				throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials"));
		}
		else
			throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidUserId", userID));

		return userID;
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