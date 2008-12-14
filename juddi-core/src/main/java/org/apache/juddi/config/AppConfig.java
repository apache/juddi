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
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.model.KeyGeneratorKey;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.util.Constants;
import org.apache.juddi.util.Install;
import org.apache.log4j.Logger;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Handles the application level configuration for jUDDI. By default it first
 * looks at system properties
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class AppConfig 
{
	private final static String JUDDI_PROPERTIES = "juddi.properties";
	private Logger log = Logger.getLogger(AppConfig.class);
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
	 * Properties and/or juddi.properties file. When the juddi.properties
	 * file is updated the file will be reloaded. By default the reloadDelay is
	 * set to 1 second to prevent excessive date stamp checking.
	 */
	private void loadConfiguration() throws ConfigurationException
	{
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from file
		PropertiesConfiguration propConfig = new PropertiesConfiguration(JUDDI_PROPERTIES);
		// Properties from the persistence layer 
		MapConfiguration persistentConfig = new MapConfiguration(getPersistentConfiguration());
		
		long refreshDelay = propConfig.getLong(Property.JUDDI_CONFIGURATION_RELOAD_DELAY, 1000l);
		log.debug("Setting refreshDelay to " + refreshDelay);
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
		propConfig.setReloadingStrategy(fileChangedReloadingStrategy);
		compositeConfig.addConfiguration(propConfig);
		compositeConfig.addConfiguration(persistentConfig);
		//Making the new configuration globally accessible.
		config = compositeConfig;
	}

	/*
	 * This method will build any "persisted" properties. Persisted properties are those that are stored in the database.  These values
	 * should be stored when the application is installed.  If they don't exist, then an error should occur.
	 */
	private Properties getPersistentConfiguration() throws ConfigurationException {
		Properties result = new Properties();
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher rootPublisher = em.find(UddiEntityPublisher.class, Constants.ROOT_PUBLISHER);
		if (rootPublisher == null) {
			log.info("The 'root' publisher was not found, loading...");
			try {
				Install.install();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DispositionReportFaultMessage e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tx.commit();
		tx.begin();
		rootPublisher = em.find(UddiEntityPublisher.class, Constants.ROOT_PUBLISHER);
		Set<KeyGeneratorKey> rootKeyGenList = rootPublisher.getKeyGeneratorKeys();
		if (rootKeyGenList == null || rootKeyGenList.size() == 0)
			throw new ConfigurationException("The 'root' publisher key generator was not found.  Please make sure that the application is properly installed.");
		
		String rootKeyGen = rootKeyGenList.iterator().next().getKeygenTModelKey();
		rootKeyGen = rootKeyGen.substring((KeyGenerator.UDDI_SCHEME + KeyGenerator.PARTITION_SEPARATOR).length());
		rootKeyGen = rootKeyGen.substring(0, rootKeyGen.length() - (KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX).length());
		log.debug("root domain:  " + rootKeyGen);
		
		result.setProperty(Property.JUDDI_ROOT_DOMAIN, rootKeyGen);
		
		tx.commit();
		em.close();
		
		return result;
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
