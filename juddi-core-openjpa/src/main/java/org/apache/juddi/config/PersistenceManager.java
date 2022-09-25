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

package org.apache.juddi.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PersistenceManager {
	private static Log log = LogFactory.getLog(PersistenceManager.class);
	
	public static final String PERSISTENCE_UNIT_NAME = "juddiDatabase";

	private static EntityManagerFactory emf;

	public static EntityManager getEntityManager() {
		try {
			// Now, the factory will be initialized in the config. This will force the config to initialize, thereby initializing emf.
			if (emf == null)
				AppConfig.getInstance();
		} 
		catch (ConfigurationException e) {
			log.error("Error initializing config in PersistenceManager", e);
			throw new ExceptionInInitializerError(e);
		}
		
		return emf.createEntityManager();
	}
	
	public static void closeEntityManager() {
		if (emf.isOpen())
			emf.close();
	}
	
	protected static void initializeEntityManagerFactory(String persistenceUnitName, Properties properties) {
		try {
			if (emf == null)
				if (properties==null || properties.size()==0) {
					emf = Persistence.createEntityManagerFactory(persistenceUnitName);
				} else {
					emf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
				}
		}
		catch (Throwable t) {
			log.error("entityManagerFactory creation failed", t);
			throw new ExceptionInInitializerError(t);
		}
		
	}
	
}
