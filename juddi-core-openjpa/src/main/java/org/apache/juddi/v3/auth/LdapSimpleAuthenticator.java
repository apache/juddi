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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.UnknownUserException;

import org.apache.commons.configuration.ConfigurationException;

/**
 * This is a implementation of jUDDI's Authenticator interface, that uses the
 * LDAP. z
 * 
 * Usage:
 * 
 * To use this class you must add the following properties to the
 * juddiv3.properties file:
 * 
 * # The LDAP Authenticator
 * juddi.authenticator=org.apache.juddi.v3.auth.LdapSimpleAuthenticator
 * 
 * # LDAP authentication URL
 * juddi.authenticator.url=ldap://localhost:389
 *
 * This authenticator assumes that the publisher username is the same as the LDAP 
 * principal name, which may not be the case as the LDAP principal might be a bind name.
 * This class could easily be extended so that the uid of the LDAP authenticated user is
 * used, or to authenticate by group.<br><br>
 *
 * This class was tested with OpenLDAP.<br>
 * <br> See properties:
 *  {@link org.apache.juddi.config.Property#JUDDI_AUTHENTICATOR_INITIAL_CONTEXT JUDDI_AUTHENTICATOR_INITIAL_CONTEXT}
 *  {@link org.apache.juddi.config.Property#JUDDI_AUTHENTICATOR_STYLE JUDDI_AUTHENTICATOR_STYLE}
 *
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:gunnlaugursig@gmail.com">Gunnlaugur Sigurðsson</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * 
 * @since 3.2, all values are now configurable
 * @see Property
 * 
 */
public class LdapSimpleAuthenticator implements Authenticator {
    private Log logger = LogFactory.getLog(this.getClass());

    private LdapContext ctx = null;
    //this needs to be a Hashtable, HashMap won't work here
    private Hashtable<String, String> env = null;
    private String url = null;
    
    private static final String DEFAULT_URL = "ldap://localhost:389";
    
    public LdapSimpleAuthenticator() throws NamingException, ConfigurationException {
    	String authURL = null;
    	try {
    		authURL = AppConfig.getConfiguration().getString(Property.JUDDI_AUTHENTICATOR_URL, DEFAULT_URL);
    	} catch (ConfigurationException ce) {
    		logger.error("Configuration exception occurred retrieving: " + Property.JUDDI_AUTHENTICATOR_URL);
    		throw new NamingException(Property.JUDDI_AUTHENTICATOR_URL + " missing from config or config is not available.");
    	}
    	init(authURL);
    }
    
    public LdapSimpleAuthenticator(String url) throws NamingException, ConfigurationException {
    	init(url);
    }

    public void init(String url) throws NamingException, ConfigurationException {
        env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, AppConfig.getConfiguration().getString(Property.JUDDI_AUTHENTICATOR_INITIAL_CONTEXT, "com.sun.jndi.ldap.LdapCtxFactory"));
        env.put(Context.SECURITY_AUTHENTICATION, AppConfig.getConfiguration().getString(Property.JUDDI_AUTHENTICATOR_STYLE, "simple"));
        env.put(Context.PROVIDER_URL, url); // organization ldap url, example ldap://localhost:389

        this.url = url;
        
        try {
            ctx = new InitialLdapContext(env, null);
        } catch (NamingException e) {
            logger.error("Naming exception " + e);
            throw e;
        }
    }
    
    public String authenticate(String authorizedName, String cred)
            throws AuthenticationException, FatalErrorException {
        if (authorizedName == null || "".equals(authorizedName)) {
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
                logger.error("config exception! " + authorizedName, ex);
        }
        boolean isLdapUser = false;
        try {
            env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, AppConfig.getConfiguration().getString(Property.JUDDI_AUTHENTICATOR_INITIAL_CONTEXT, "com.sun.jndi.ldap.LdapCtxFactory"));
            env.put(Context.SECURITY_AUTHENTICATION, AppConfig.getConfiguration().getString(Property.JUDDI_AUTHENTICATOR_STYLE, "simple"));
            env.put(Context.PROVIDER_URL, url); // organization ldap url, example ldap://localhost:389
            env.put(Context.SECURITY_PRINCIPAL, authorizedName);
            env.put(Context.SECURITY_CREDENTIALS, cred);
            ctx = new InitialLdapContext(env, null);
            isLdapUser = true;
            logger.info(authorizedName + " is authenticated");
           
        } catch (ConfigurationException e) {
            logger.error(authorizedName + " is not authenticated", e);
            throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", authorizedName));
        }
        catch (NamingException e) {
            logger.error(authorizedName + " is not authenticated");
            throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", authorizedName));
        }finally {
            try {
                ctx.close();
            } catch (NamingException e) {
                logger.error("Context close failure " + e);
            }
        }

        if (isLdapUser) {
            EntityManager em = PersistenceManager.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Publisher publisher = em.find(Publisher.class, authorizedName);
                if (publisher == null) {
                    logger.warn("Publisher was not found in the database, adding the publisher in on the fly.");
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
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
                em.close();
            }
        } else {
            throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", authorizedName));
        }
        return authorizedName;
    }

    public UddiEntityPublisher identify(String authInfo, String authorizedName, WebServiceContext ctx) throws AuthenticationException, FatalErrorException {
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
