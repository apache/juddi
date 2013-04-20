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

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.Registry;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.FindBusinessByCategoryQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;

/**
 * Handles the application level configuration for jUDDI. By default it first
 * looks at system properties
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class AppConfig 
{
	private final static String JUDDI_PROPERTIES = "juddiv3.properties";
	private Log log = LogFactory.getLog(AppConfig.class);
	private Configuration config;
	private static AppConfig instance=null;
	
	/**
	 * Constructor (note Singleton pattern).
	 * @throws ConfigurationException
	 */
	private AppConfig() throws ConfigurationException 
	{
		loadConfiguration();
	}
	/**
	 * Does the actual work of reading the configuration from System
	 * Properties and/or juddiv3.properties file. When the juddiv3.properties
	 * file is updated the file will be reloaded. By default the reloadDelay is
	 * set to 1 second to prevent excessive date stamp checking.
	 */
	private void loadConfiguration() throws ConfigurationException
	{
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from file
		PropertiesConfiguration propConfig = null;
	    final String filename = System.getProperty("juddi.propertiesFile");
		if (filename != null) {
			propConfig = new PropertiesConfiguration(filename);
		} else {
			propConfig = new PropertiesConfiguration(JUDDI_PROPERTIES);
		}
		URL url = ClassUtil.getResource(JUDDI_PROPERTIES, this.getClass()); 
		log.info("Reading from properties file:  " + url);
		long refreshDelay = propConfig.getLong(Property.JUDDI_CONFIGURATION_RELOAD_DELAY, 1000l);
		log.debug("Setting refreshDelay to " + refreshDelay);
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
		propConfig.setReloadingStrategy(fileChangedReloadingStrategy);
		compositeConfig.addConfiguration(propConfig);
		
		
		Properties properties = new Properties();
		if ("Hibernate".equals(propConfig.getString(Property.PERSISTENCE_PROVIDER))) {
			if (propConfig.containsKey(Property.DATASOURCE)) 
				properties.put("hibernate.connection.datasource",propConfig.getString(Property.DATASOURCE));
			if (propConfig.containsKey(Property.HBM_DDL_AUTO))
				properties.put("hibernate.hbm2ddl.auto",propConfig.getString(Property.HBM_DDL_AUTO));
			if (propConfig.containsKey(Property.DEFAULT_SCHEMA))
				properties.put("hibernate.default_schema",propConfig.getString(Property.DEFAULT_SCHEMA));
			if (propConfig.containsKey(Property.HIBERNATE_DIALECT))
				properties.put("hibernate.dialect",propConfig.getString(Property.HIBERNATE_DIALECT));
		}
		// initialize the entityManagerFactory.
		PersistenceManager.initializeEntityManagerFactory(propConfig.getString(Property.JUDDI_PERSISTENCEUNIT_NAME), properties);
		// Properties from the persistence layer 
		MapConfiguration persistentConfig = new MapConfiguration(getPersistentConfiguration(compositeConfig));
		
		compositeConfig.addConfiguration(persistentConfig);
		//Making the new configuration globally accessible.
		config = compositeConfig;
	}

	/*
	 * This method will build any "persisted" properties. Persisted properties are those that are stored in the database.  These values
	 * should be stored when the application is installed.  If they don't exist, then an error should occur.
	 */
	private Properties getPersistentConfiguration(Configuration config) throws ConfigurationException {
		Properties result = new Properties();
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			boolean seedAlways = config.getBoolean("juddi.seed.always",false);
			if (seedAlways || !Install.alreadyInstalled(config)) {
			    if (seedAlways) {
			    	log.info("Installing UDDI seed data, loading...");
			    } else {
			    	log.info("The 'root' publisher was not found, loading...");
			    }
				try {
					Install.install(config);
				} catch (Exception e) {
					throw new ConfigurationException(e);
				} catch (Throwable t) {
					throw new ConfigurationException(t);
				}
			}

			tx.begin();
	
			String rootPublisherStr = config.getString(Property.JUDDI_ROOT_PUBLISHER);
			UddiEntityPublisher  rootPublisher = new UddiEntityPublisher(rootPublisherStr);
			rootPublisher.populateKeyGeneratorKeys(em);
			List<String> rootKeyGenList = rootPublisher.getKeyGeneratorKeys();
			if (rootKeyGenList == null || rootKeyGenList.size() == 0)
				throw new ConfigurationException("The 'root' publisher key generator was not found.  Please make sure that the application is properly installed.");
			
			String rootKeyGen = rootKeyGenList.iterator().next();
			//rootKeyGen = rootKeyGen.substring((KeyGenerator.UDDI_SCHEME + KeyGenerator.PARTITION_SEPARATOR).length());
			rootKeyGen = rootKeyGen.substring(0, rootKeyGen.length() - (KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX).length());
			log.debug("root partition:  " + rootKeyGen);
	
			result.setProperty(Property.JUDDI_ROOT_PARTITION, rootKeyGen);
			
			// The node Id is defined as the business key of the business entity categorized as a node.  This entity is saved as part of the install.
			// Only one business entity should be categorized as a node.
			String nodeId = "";
			CategoryBag categoryBag = new CategoryBag();
			KeyedReference keyedRef = new KeyedReference();
			keyedRef.setTModelKey(Constants.NODE_CATEGORY_TMODEL);
			keyedRef.setKeyValue(Constants.NODE_KEYVALUE);
			categoryBag.getKeyedReference().add(keyedRef);
			List<?> keyList = FindBusinessByCategoryQuery.select(em, new FindQualifiers(), categoryBag, null);
			if (keyList != null && keyList.size() > 1)
				throw new ConfigurationException("Only one business entity can be categorized as the node.");
			
			if (keyList != null && keyList.size() > 0) {
				nodeId = (String)keyList.get(0);
			}
			else
				throw new ConfigurationException("A node business entity was not found.  Please make sure that the application is properly installed.");
			result.setProperty(Property.JUDDI_NODE_ID, nodeId);
			
			tx.commit();
			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
	
	
	/**
	 * Obtains the reference to the Singleton instance.
	 * 
	 * @return the APplicationConfuration Singleton Instance.
	 * @throws ConfigurationException
	 */
	public static AppConfig getInstance() throws ConfigurationException 
	{
		if (instance==null) {
			instance = new AppConfig();
		}
		return instance;
	}
	/**
	 * Hook to receive configuration reload events from an external application.
	 * 
	 * @throws ConfigurationException
	 */
	public static void reloadConfig() throws ConfigurationException
	{
		Registry.stop();
		getInstance().loadConfiguration();
		Registry.start();
	}
	/**
	 * The object from which property values can be obtained.
	 * @return the commons Configuration interface
	 * @throws ConfigurationException 
	 */
	public static Configuration getConfiguration() throws ConfigurationException
	{
		return getInstance().config;
	}
}
