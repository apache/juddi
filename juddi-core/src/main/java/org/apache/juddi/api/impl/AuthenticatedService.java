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
import javax.annotation.Resource;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.auth.Authenticator;
import org.apache.juddi.v3.auth.AuthenticatorFactory;
import org.apache.juddi.v3.error.AuthTokenRequiredException;
import org.apache.juddi.v3.error.AuthTokenExpiredException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**Although this class is abstract, it provides token validation
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * 
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a> - modified to include token expiration validation
 */
public abstract class AuthenticatedService {
	public static final int AUTHTOKEN_ACTIVE = 1;
	public static final int AUTHTOKEN_RETIRED = 0;
	Log logger = LogFactory.getLog(this.getClass());
	
        @Resource
        protected WebServiceContext ctx;
	public UddiEntityPublisher getEntityPublisher(EntityManager em, String authInfo) throws DispositionReportFaultMessage {
		
		if (authInfo == null || authInfo.length() == 0)
			throw new AuthTokenRequiredException(new ErrorMessage("errors.auth.AuthRequired"));
		
		org.apache.juddi.model.AuthToken modelAuthToken = em.find(org.apache.juddi.model.AuthToken.class, authInfo);
		if (modelAuthToken == null)
			throw new AuthTokenRequiredException(new ErrorMessage("errors.auth.AuthInvalid"));
	
		int allowedMinutesOfInactivity = 0;
		try {
			allowedMinutesOfInactivity = AppConfig.getConfiguration().getInt(Property.JUDDI_AUTH_TOKEN_TIMEOUT, 0);
		} catch (ConfigurationException ce) {
			logger.error("Error reading property " + Property.JUDDI_AUTH_TOKEN_EXPIRATION + " from "
					+ "the application's configuration. No automatic timeout token invalidation will occur. "
					+ ce.getMessage(), ce);
		}
		int maxMinutesOfAge = 0;
		try {
			maxMinutesOfAge = AppConfig.getConfiguration().getInt(Property.JUDDI_AUTH_TOKEN_EXPIRATION, 0);
		} catch (ConfigurationException ce) {
			logger.error("Error reading property " + Property.JUDDI_AUTH_TOKEN_EXPIRATION + " from "
					+ "the application's configuration. No automatic timeout token invalidation will occur. "
					+ ce.getMessage(), ce);
		}
		Date now = new Date();
		// 0 or negative means token does not expire
		if (allowedMinutesOfInactivity > 0) {
			// expire tokens after # minutes of inactivity
			// compare the time in milli-seconds
			if (now.getTime() > modelAuthToken.getLastUsed().getTime() + allowedMinutesOfInactivity * 60000l) {
				logger.debug("Token " + modelAuthToken.getAuthToken() + " expired due to inactivity");
				modelAuthToken.setTokenState(AUTHTOKEN_RETIRED);
			}
		}
		if (maxMinutesOfAge > 0) {
			// expire tokens when max age is reached
			// compare the time in milli-seconds
			if (now.getTime() > modelAuthToken.getCreated().getTime()  + maxMinutesOfAge * 60000l) {
				logger.debug("Token " + modelAuthToken.getAuthToken() + " expired due to old age");
				modelAuthToken.setTokenState(AUTHTOKEN_RETIRED);
			}
		}

		if (modelAuthToken.getTokenState() == AUTHTOKEN_RETIRED)
			throw new AuthTokenExpiredException(new ErrorMessage("errors.auth.AuthTokenExpired"));
		if (ctx !=null){
                    try{
                        boolean check=true;
                        try{
                            check=AppConfig.getConfiguration().getBoolean(Property.JUDDI_AUTH_TOKEN_ENFORCE_SAME_IP, true);
                        }
                        catch (ConfigurationException ex){
                            logger.warn("Error loading config property " + Property.JUDDI_AUTH_TOKEN_ENFORCE_SAME_IP + 
                                    " Enforcing Same IP for Auth Tokens will be enabled by default", ex);
                        }
                        if (check){
                            MessageContext mc = ctx.getMessageContext();
                            HttpServletRequest req = null;
                            if (mc!=null){
                                req=(HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST); 
                            }
                            if (req!=null &&
                                    modelAuthToken.getIPAddress()!=null &&
                                    modelAuthToken.getIPAddress()!=null &&
                                    !modelAuthToken.getIPAddress().equalsIgnoreCase(req.getRemoteAddr()))
                            {
                                modelAuthToken.setTokenState(AUTHTOKEN_RETIRED);
                                logger.error("Security Alert - Attempt to use issued auth token from a different IP address, user " +
                                        modelAuthToken.getAuthorizedName() + ", issued IP " + modelAuthToken.getIPAddress() + 
                                        ", attempted use from " + req.getRemoteAddr() + ", forcing reauthentication.");
                                throw new AuthTokenRequiredException(new ErrorMessage("errors.auth.AuthInvalid"));
                                //invalidate the token, someone's intercepted it or it was reused on another ip
                            }
                        }
                    }
                    catch (Exception ex){
                        if (ex instanceof AuthTokenRequiredException)
                            throw (AuthTokenRequiredException)ex;
                        logger.error("unexpected error caught looking up requestor's ip address", ex);
                    }
                    
                }
		Authenticator authenticator = AuthenticatorFactory.getAuthenticator();
		UddiEntityPublisher entityPublisher = authenticator.identify(authInfo, modelAuthToken.getAuthorizedName());
		
		// Must make sure the returned publisher has all the necessary fields filled
		if (entityPublisher == null)
			throw new AuthTokenRequiredException(new ErrorMessage("errors.auth.AuthInvalid"));

		if (entityPublisher.getAuthorizedName() == null)
			throw new AuthTokenRequiredException(new ErrorMessage("errors.auth.AuthInvalid"));

		// Auth token is being used.  Adjust appropriate values so that it's internal 'expiration clock' is reset.
		modelAuthToken.setLastUsed(new Date());
		modelAuthToken.setNumberOfUses(modelAuthToken.getNumberOfUses() + 1);
		
		return entityPublisher;
				   
	}

        /**
         * Attempts to get the requestor's ip address from the servlet context, defaults to null it it can't be
         * retrieved
         * @return 
         */
        public String getRequestorsIPAddress()
        {
            try {
                MessageContext mc = ctx.getMessageContext();
                HttpServletRequest req = null;
                if (mc != null) {
                    req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
                }
                if (req != null) {
                    return req.getRemoteAddr();
                }
            } catch (Exception ex) {
                logger.debug("Error caught looking up the requestor's ip address", ex);
            }
            return null;
        }
}
