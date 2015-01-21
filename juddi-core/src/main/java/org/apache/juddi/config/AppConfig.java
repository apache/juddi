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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.Registry;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.model.UddiEntityPublisher;

/**
 * Handles the application level configuration for jUDDI. By default it first
 * looks at system properties (juddi.propertiesFile)
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class AppConfig 
{
        /**
         * This system property's value should be a path to a configuration file
         */
        public static final String JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY="juddi.propertiesFile";
        /**
         * The default configuration file name for juddi
         */
	public static final String JUDDI_PROPERTIES = "juddiv3.xml";
	private Log log = LogFactory.getLog(AppConfig.class);
	private Configuration config;
	private static AppConfig instance=null;
        private static URL loadedFrom=null;
        private static XMLConfiguration propConfig=null;
        
        /**
         * Enables an administrator to identify the physical location of the configuration file from which it was loaded.<br>
         * Always call via the singleton function AppConfig.getInstance().getConfigFileURL()
         * @since 3.2
         * @return may return null if no config file was found
         */
        public static  URL getConfigFileURL()
        {
            return loadedFrom;
        }
	
	/**
	 * Constructor (note Singleton pattern).
	 * @throws ConfigurationException
	 */
	private AppConfig() throws ConfigurationException 
	{
		loadConfiguration();
	}
        public static void setJuddiProperty(String key, Object val) throws ConfigurationException{
                if (instance==null) {
			instance = new AppConfig();
		}
                propConfig.setProperty(key, val);
                propConfig.save();
        }
        
        public static void saveConfiguration() throws ConfigurationException{
                Configuration configuration = getConfiguration();
                propConfig.save();
        }
       
        
	/**
	 * Does the actual work of reading the configuration from System
	 * Properties and/or juddiv3.xml file. When the juddiv3.xml
	 * file is updated the file will be reloaded. By default the reloadDelay is
	 * set to 1 second to prevent excessive date stamp checking.
	 */
	private void loadConfiguration() throws ConfigurationException
	{
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from file
                //changed 7-19-2013 AO for JUDDI-627
		propConfig = null;
	        final String filename = System.getProperty(JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY);
		if (filename != null) {
                  propConfig = new XMLConfiguration (filename); 
                    try {
                        loadedFrom = new File(filename).toURI().toURL();
                    //	propConfig = new PropertiesConfiguration(filename);
                    } catch (MalformedURLException ex) {
                      try {
                          loadedFrom = new URL("file://" + filename);
                      } catch (MalformedURLException ex1) {
                          log.warn("unable to get an absolute path to " + filename + ". This may be ignorable if everything works properly.", ex1);
                      }
                    }
		} else {
			//propConfig = new PropertiesConfiguration(JUDDI_PROPERTIES);
                    propConfig = new XMLConfiguration(JUDDI_PROPERTIES);
                    loadedFrom = ClassUtil.getResource(JUDDI_PROPERTIES, this.getClass()); 
		}
                //Hey! this may break things
                propConfig.setAutoSave(true);

		log.info("Reading from jUDDI config file from:  " + loadedFrom);
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
			String nodeId = config.getString(Property.JUDDI_NODE_ID);
                        if (nodeId==null)
                                log.fatal("Error! " + Property.JUDDI_NODE_ID + " is not defined in the config!");
                        else
                                result.setProperty(Property.JUDDI_NODE_ID, nodeId);
			/*
                        CategoryBag categoryBag = new CategoryBag();
			KeyedReference keyedRef = new KeyedReference();
			keyedRef.setTModelKey(Constants.NODE_CATEGORY_TMODEL);
			keyedRef.setKeyValue(Constants.NODE_KEYVALUE);
			categoryBag.getKeyedReference().add(keyedRef);
			List<?> keyList = FindBusinessByCategoryQuery.select(em, new FindQualifiers(), categoryBag, null);
			if (keyList != null && keyList.size() > 1)
                        {
                                StringBuilder sb = new StringBuilder();
                                Iterator<?> iterator = keyList.iterator();
                                while(iterator.hasNext()){
                                        sb.append(iterator.next()).append(",");
                                }
				//
                                //throw new ConfigurationException("Only one business entity can be categorized as the node. Config loaded from " + loadedFrom + " Key's listed at the node: " + sb.toString());
                                //unless of course, we are in a replicated environment
                        }
			if (keyList != null && keyList.size() > 0) {
				nodeId = (String)keyList.get(0);
			}
			else
				throw new ConfigurationException("A node business entity was not found.  Please make sure that the application is properly installed.");
			*/
                        String rootbiz=config.getString(Property.JUDDI_NODE_ROOT_BUSINESS);
                        if (rootbiz==null)
                                log.fatal("Error! " + Property.JUDDI_NODE_ROOT_BUSINESS + " is not defined in the config");
                        else
                                result.setProperty(Property.JUDDI_NODE_ROOT_BUSINESS, rootbiz);
                        
                        
			
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
        
        public static void triggerReload() throws ConfigurationException{
                getInstance().loadConfiguration();
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
