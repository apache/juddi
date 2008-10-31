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

package org.apache.juddi.uuidgen;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.util.Loader;
import org.apache.log4j.Logger;

/**
 * Used to create the org.apache.juddi.uuidgen.UUIDGen implementation
 * as specified by the 'juddi.uuidgen.impl' property. Defaults to
 * org.apache.juddi.uuidgen.SecureUUIDGen if an implementation is not
 * specified.
 *
 * @author Steve Viens (sviens@apache.org)
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public abstract class UUIDGenFactory {
	private static Logger log = Logger.getLogger(UUIDGenFactory.class);

	// UUIDGen default implementation
	private static final String DEFAULT_IMPL = "org.apache.juddi.uuidgen.DefaultUUIDGen";

	// the shared UUIDGen instance
	private static UUIDGen uuidgen = null;

	/*
	 * Returns a new instance of a UUIDGenFactory.
	 * 
	 * @return UUIDGen
	 */
	public static UUIDGen getUUIDGen() {
		if (uuidgen == null)
			uuidgen = createUUIDGen();
		return uuidgen;
	}

  /**
   * Returns a new instance of a UUIDGen.
   *
   * @return UUIDGen
   */

	
	private static synchronized UUIDGen createUUIDGen() {
		if (uuidgen != null)
			return uuidgen;

		// grab class name of the UUIDGen implementation to create
		String className = DEFAULT_IMPL;
		try {
			// grab class name of the Authenticator implementation to create
			className = AppConfig.getConfiguration().getString(Property.JUDDI_UUID_GENERATOR, DEFAULT_IMPL);
		}
		catch(ConfigurationException ce) {
			log.error("Configuration exception occurred retrieving: " + Property.JUDDI_UUID_GENERATOR);
		}

		
		// write the UUIDGen implementation name to the log
		log.debug("UUIDGen Implementation = " + className);

		Class<?> uuidgenClass = null;
		try {
			// Use Loader to locate & load the UUIDGen implementation
			uuidgenClass = Loader.getClassForName(className);
		}
		catch(ClassNotFoundException e) {
			log.error("The specified UUIDGen class '" + className + "' was not found in classpath.");
			log.error(e);
		}

		try {
			// try to instantiate the UUIDGen implementation
			uuidgen = (UUIDGen)uuidgenClass.newInstance();
		}
		catch(Exception e) {
			log.error("Exception while attempting to instantiate the implementation of UUIDGen: " + uuidgenClass.getName() + "\n" + e.getMessage());
			log.error(e);
		}

		return uuidgen;
	}
}