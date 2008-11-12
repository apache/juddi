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

package org.apache.juddi.keygen;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.util.Loader;
import org.apache.log4j.Logger;

/**
 * Used to create the org.apache.juddi.keygen.KeyGenerator implementation
 * as specified by the 'juddi.keygenerator' property. Defaults to
 * org.apache.juddi.cryptor.DefaultKeyGenerator if an implementation is not
 * specified.
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public abstract class KeyGeneratorFactory {
	private static Logger log = Logger.getLogger(KeyGeneratorFactory.class);

	// Key Generator default implementation
	private static final String DEFAULT_IMPL = "org.apache.juddi.keygen.DefaultKeyGenerator";

	// the shared Key Generator instance
	private static KeyGenerator keyGenerator = null;

	/*
	 * Returns a new instance of a KeyGenerator.
	 * 
	 * @return KeyGenerator
	 */
	public static KeyGenerator getKeyGenerator() {
		if (keyGenerator == null)
			keyGenerator = createKeyGenerator();
		return keyGenerator;
	}

	/*
	 * Returns a new instance of a Cryptor.
	 * 
	 * @return Cryptor
	 */
	private static synchronized KeyGenerator createKeyGenerator() {
		if (keyGenerator != null)
			return keyGenerator;
	
		// grab class name of the Cryptor implementation to create
		String className = DEFAULT_IMPL;
		try {
			// grab class name of the Authenticator implementation to create
			className = AppConfig.getConfiguration().getString(Property.JUDDI_KEYGENERATOR, DEFAULT_IMPL);
		}
		catch(ConfigurationException ce) {
			log.error("Configuration exception occurred retrieving: " + Property.JUDDI_KEYGENERATOR);
		}
		
		// write the Cryptor implementation name to the log
		log.debug("Key Generator Implementation = " + className);
	
		Class<?> keygenClass = null;
		try {
			// Use Loader to locate & load the Key Generator implementation
			keygenClass = Loader.getClassForName(className);
		}
		catch(ClassNotFoundException e) {
			log.error("The specified Key Generator class '" + className + "' was not found in classpath.");
			log.error(e);
		}
	
		try {
			// try to instantiate the Key Generator implementation
			keyGenerator = (KeyGenerator)keygenClass.newInstance();
		}
		catch(Exception e) {
			log.error("Exception while attempting to instantiate the implementation of Key Generator: " + keygenClass.getName() + "\n" + e.getMessage());
			log.error(e);
		}
	
		return keyGenerator;
	}
}