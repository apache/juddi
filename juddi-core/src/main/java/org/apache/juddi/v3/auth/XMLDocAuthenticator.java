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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceContext;

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
 * This is a simple implementation of jUDDI's Authenticator interface. The
 * credential store is simply an unencrypted xml document called 'juddi.users'
 * that can be found in jUDDI's config directory. Below is an example of what
 * you might find in this document.
 *
 * Example juddi.users document: =============================
 * <?xml version="1.0" encoding="UTF-8"?>
 * <juddi-users>
 * <user userid="sviens" password="password" />
 * <user userid="griddell" password="password" />
 * <user userid="bhablutzel" password="password" />
 * </juddi-users>
 *
 * @author Steve Viens (sviens@apache.org)
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class XMLDocAuthenticator implements Authenticator {

        protected final static Log log = LogFactory.getLog(AuthenticatorFactory.class);
        /**
         * Container for the user credentials
         */
        Map<String, User> userTable;

        /**
         *
         */
        public XMLDocAuthenticator() throws JAXBException, IOException, ConfigurationException {
                readUserFile();
        }

        /**
         * an empty constructor
         */
        public XMLDocAuthenticator(boolean b) {

        }

        protected String getFilename() throws ConfigurationException {
                return AppConfig.getConfiguration().getString(Property.JUDDI_USERSFILE, Property.DEFAULT_XML_USERSFILE);
        }

        /**
         * Read user data from the juddi-users file.
         *
         * @throws IOException when the file cannot be opened JAXBException when
         * the content is malformed.
         * @throws ConfigurationException
         */
        public synchronized void readUserFile() throws JAXBException, IOException, ConfigurationException {

                userTable = new HashMap<String, User>();
                String usersFileName = getFilename();
                if (usersFileName == null || usersFileName.length() == 0) {
                        throw new ConfigurationException("usersFileName value is null!");
                }
                File file = new File(usersFileName);
                InputStream stream = null;
                try {
                        if (file.exists()) {
                                log.info("Reading jUDDI Users File: " + usersFileName + "...");
                                stream = new FileInputStream(file);
                        } else {
                                URL resource = ClassUtil.getResource(usersFileName, this.getClass());
                                if (resource != null) {
                                        log.info("Reading jUDDI Users File: " + usersFileName + "...from " + resource.toExternalForm());
                                } else {
                                        log.info("Reading jUDDI Users File: " + usersFileName + "...");
                                }
                                stream = ClassUtil.getResource(usersFileName, this.getClass()).openStream();
                        }
                        JAXBContext jaxbContext = JAXBContext.newInstance(JuddiUsers.class);
                        Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
                        JAXBElement<JuddiUsers> element = unMarshaller.unmarshal(new StreamSource(stream), JuddiUsers.class);
                        JuddiUsers users = element.getValue();
                        for (User user : users.getUser()) {
                                userTable.put(user.getUserid(), user);
                                log.debug("Loading user credentials for user: " + user.getUserid());
                        }
                } catch (IOException ex) {
                        log.warn("io exception", ex);
                } finally {
                        if (stream != null) {
                                stream.close();
                        }
                }
        }

        /**
         *
         * @param userID
         * @param credential
         */
        public String authenticate(String userID, String credential)
                throws AuthenticationException, FatalErrorException {
                // a userID must be specified.
                if (userID == null) {
                        throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidUserId"));
                }

                // credential (password) must be specified.
                if (credential == null) {
                        throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials"));
                }

                if (userTable.containsKey(userID)) {
                        User user = (User) userTable.get(userID);
                        if ((user.getPassword() == null) || (!credential.equals(user.getPassword()))) {
                                throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidCredentials"));
                        }
                } else {
                        throw new UnknownUserException(new ErrorMessage("errors.auth.InvalidUserId", userID));
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

        @Override
        public UddiEntityPublisher identify(String authInfo, String authorizedName, WebServiceContext ctx) throws AuthenticationException {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();
                        Publisher publisher = em.find(Publisher.class, authorizedName);
                        if (publisher == null) {
                                throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", authorizedName));
                        }

                        return publisher;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }

        }

}
