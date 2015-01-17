/*
 * Copyright 2014 The Apache Software Foundation.
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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
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

/**
 * This authenticator trust's what's provided by the container, such as HTTP
 * BASIC, DIGEST, or CLIENT CERT
 *
 * @author Alex O'Ree
 */
public class HTTPContainerAuthenticator implements Authenticator {

        private Log log = LogFactory.getLog(this.getClass());
        @Override
        public String authenticate(String authorizedName, String cred) throws AuthenticationException, FatalErrorException {
                throw new UnknownUserException(new ErrorMessage("errros.UnsupportedAuthenticator"));
        }

        @Override
        public UddiEntityPublisher identify(String authInfoNotused, String authorizedNameNotused, WebServiceContext ctx) throws AuthenticationException, FatalErrorException {
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
                        log.error("config exception! ", ex);
                }
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        String user = null;
                        if (ctx==null)
                                throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher", "no web service context!"));
                        if (ctx.getUserPrincipal() != null) {
                                user = ctx.getUserPrincipal().getName();
                        }
                        if (user == null) {
                                MessageContext mc = ctx.getMessageContext();
                                HttpServletRequest req = null;
                                if (mc != null) {
                                        req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
                                }
                                if (req != null && req.getUserPrincipal()!=null) {
                                        user = req.getUserPrincipal().getName();
                                }
                        }
                        if (user==null || user.length()==0){
                                throw new UnknownUserException(new ErrorMessage("errors.auth.NoPublisher"));
                        }
                        tx.begin();
                        Publisher publisher = em.find(Publisher.class, user);
                        if (publisher == null) {
                                log.warn("Publisher \"" + user + "\" was not found, adding the publisher in on the fly.");
                                publisher = new Publisher();
                                publisher.setAuthorizedName(user);
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
                        
                        return publisher;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

}
