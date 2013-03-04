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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;


/**
 * @author Steve Viens (sviens@apache.org)
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class AuthenticatorFactory {
	private static Log log = LogFactory.getLog(AuthenticatorFactory.class);

	// Default authenticator implementation
	private static final String DEFAULT_IMPL = "org.apache.juddi.v3.auth.JUDDIAuthenticator";

	// the shared Authenticator instance
	private static Authenticator auth = null;


	/*
	 * Returns a new instance of a AuthenticatorFactory.
	 * 
	 * @return Authenticator
	 */
	public static Authenticator getAuthenticator() {
		if (auth == null)
			auth = createAuthenticator();
		return auth;
	}

	/*
	 * Returns a new instance of a Authenticator.
	 * 
	 * @return Authenticator
	 */
	private static synchronized Authenticator createAuthenticator() {
		if (auth != null)
			return auth;
	
		String className = DEFAULT_IMPL;
		try {
			// grab class name of the Authenticator implementation to create
			className = AppConfig.getConfiguration().getString(Property.JUDDI_AUTHENTICATOR, DEFAULT_IMPL);
		}
		catch(ConfigurationException ce) {
			log.error("Configuration exception occurred retrieving: " + Property.JUDDI_AUTHENTICATOR);
		}
	
		// write the Authenticator implementation name to the log
		log.debug("Authenticator Implementation = " + className);
	
		Class<?> authClass = null;
		try {
			// Use Loader to locate & load the Authenticator implementation
			authClass = ClassUtil.forName(className, AuthenticatorFactory.class);
		}
		catch(ClassNotFoundException e) {
			log.error("The specified Authenticator class '" + className + "' was not found in classpath.");
			log.error(e);
		}
	
		try {
			if (authClass!=null) {
				// try to instantiate the Authenticator implementation
				auth = (Authenticator)authClass.newInstance();
			} else {
				log.error("Could not load " + className + " authClass is null");
			}
		}
		catch(Exception e) {
			log.error("Exception while attempting to instantiate the implementation of Authenticator: " + authClass.getName() + "\n" + e.getMessage());
			log.error(e);
		}
	
		return auth;
	}
}
