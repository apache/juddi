/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.juddi.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnknownUserException;
import org.apache.juddi.util.Config;
import org.jboss.security.AuthenticationManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.security.Principal;

/**
 * This is a implementation of jUDDI's Authenticator interface, 
 * that uses the JBoss authentication manager.
 *
 * @author Antoni Reus (areus@ibit.org)
 */
public class JBossAuthenticator implements Authenticator 
{
	// private reference to the jUDDI logger
	private static Log log = LogFactory.getLog(JBossAuthenticator.class);
	
	// static default security-domain to use.
	private static final String SECURITY_DOMAIN_KEY = "juddi.securityDomain";
	private static final String DEFAULT_SECURITY_DOMAIN = "java:/jaas/other";
	
	// JBoss authentication manager
	AuthenticationManager authManager;

	/**
	 *
	 */
	public JBossAuthenticator() 
	{
		init();
	}
	
	/**
	 *
	 */
	public String authenticate(final String userID, final String credential)
		throws RegistryException 
	{
		if (userID == null) {
		  throw new UnknownUserException("Invalid user ID = "+userID);
		}
	
		// Create a principal for the userID
		Principal principal = new Principal() 
		{
	    public String getName() {
	      return userID;
	    }
		};
		
		if (!authManager.isValid(principal, credential)) {
		  throw new UnknownUserException("Invalid credentials");
		}
		
		return userID;
	}


	private void init() 
	{
    String securityDomain = Config.getStringProperty(SECURITY_DOMAIN_KEY,DEFAULT_SECURITY_DOMAIN);

    try {
			// lookup for the authentication manager.
			Context ctx = new InitialContext();
			authManager = (AuthenticationManager) ctx.lookup(securityDomain);
			ctx.close();
    } catch (NamingException e) {
			log.error("JNDI Exception looking for autentication manager: " + 
				securityDomain, e);
    }
	}
}