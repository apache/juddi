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
package org.apache.juddi.registry.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Loader;

/**
 * This servlet is ONLY used to initialize the jUDDI webapp on
 * startup and cleanup the jUDDI webapp on shutdown.
 * 
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public abstract class Registry
{ 
  private static final String CONFIG_FILE_PROPERTY_NAME = "juddi.propertiesFile";
  // default config file name. 
  private static final String DEFAULT_PROPERTY_FILE = "juddi.properties";
   
  // private reference to the webapp's logger.
  private static Log log = LogFactory.getLog(Registry.class);

  // registry singleton instance
  private static RegistryEngine registry;

  static
  {
      init() ;
  }
  
/**
   * Create the shared instance of jUDDI's Registry class
   * and call it's "init()" method to initialize all core 
   * components.
   */
  private static void init() 
  {   

    Properties props = new Properties();

    try
    {      
      log.debug("Loading jUDDI configuration.");
        
//    determine the name of the juddi property file to use from web.xml
      String propFile = System.getProperty(CONFIG_FILE_PROPERTY_NAME);
      if ((propFile == null) || (propFile.trim().length() == 0)) {
        propFile = "/" + DEFAULT_PROPERTY_FILE;
      }
   
      InputStream is = Loader.getResourceAsStream(propFile);
      if (is==null) {
    	  log.debug("Loading " + propFile + " using the context classloader failed, trying regular classloader..");
    	  is = Class.class.getResourceAsStream(propFile);
      }
      if (is==null) {
    	  log.debug("Loading " + propFile + " using the Class classloader failed.");
    	  if (("/" + DEFAULT_PROPERTY_FILE).equals(propFile)) {
    		  propFile=DEFAULT_PROPERTY_FILE;
    	  }
    	  File configFile = new File(propFile);
    	  if (!configFile.exists()) {
    		  log.debug("Could not find " + configFile.getAbsolutePath());
    	  } else {
    		  log.debug("Reading juddi properties from " + configFile.getAbsolutePath());
              is = new FileInputStream(configFile);
    	  } 
      }
      //Adding this to make maven happy, what maven wants, maven gets.
      if (is==null) {
          log.debug("Trying the classloader of the class itself. (workaround for maven2)");
          Loader loader = new Loader();
          is = loader.getResourceAsStreamFromClass(propFile);
      }
      
      if (is != null)
      {
        log.debug("Resources loaded from: "+propFile);
        props.load(is);
        is.close();
      }
      else
      {
        log.warn("Could not locate jUDDI properties '" + propFile + 
                "'. Using defaults.");

        // A juddi.properties file doesn't exist
        // yet so create create a new Properties 
        // instance using default property values.
        
        props.put(RegistryEngine.PROPNAME_OPERATOR_NAME,
                  RegistryEngine.DEFAULT_OPERATOR_NAME);
        
        props.put(RegistryEngine.PROPNAME_I18N_LANGUAGE_CODE,
            			RegistryEngine.DEFAULT_I18N_LANGUAGE_CODE);
  
        props.put(RegistryEngine.PROPNAME_I18N_COUNTRY_CODE,
            			RegistryEngine.DEFAULT_I18N_COUNTRY_CODE);
  
        props.put(RegistryEngine.PROPNAME_DISCOVERY_URL,
                  RegistryEngine.DEFAULT_DISCOVERY_URL);
        
        props.put(RegistryEngine.PROPNAME_ADMIN_EMAIL_ADDRESS,
                  RegistryEngine.DEFAULT_ADMIN_EMAIL_ADDRESS);
        
        props.put(RegistryEngine.PROPNAME_DATASOURCE_NAME,
                  RegistryEngine.DEFAULT_DATASOURCE_NAME);
        
        props.put(RegistryEngine.PROPNAME_IS_USE_DATASOURCE,
                  RegistryEngine.DEFAULT_IS_USE_DATASOURCE);
        
        props.put(RegistryEngine.PROPNAME_JDBC_DRIVER,
                RegistryEngine.DEFAULT_JDBC_DRIVER);
      
        props.put(RegistryEngine.PROPNAME_JDBC_URL,
                RegistryEngine.DEFAULT_JDBC_URL);
      
        props.put(RegistryEngine.PROPNAME_JDBC_USERNAME,
                RegistryEngine.DEFAULT_JDBC_USERNAME);
      
        props.put(RegistryEngine.PROPNAME_JDBC_PASSWORD,
                RegistryEngine.DEFAULT_JDBC_PASSWORD);
              
        props.put(RegistryEngine.PROPNAME_AUTH_CLASS_NAME,
                  RegistryEngine.DEFAULT_AUTH_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_CRYPTOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_CRYPTOR_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_DATASTORE_CLASS_NAME,
                  RegistryEngine.DEFAULT_DATASTORE_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_UUIDGEN_CLASS_NAME,
                  RegistryEngine.DEFAULT_UUIDGEN_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_VALIDATOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_VALIDATOR_CLASS_NAME);  

        props.put(RegistryEngine.PROPNAME_MAX_NAME_ELEMENTS,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_NAME_ELEMENTS));
        
        props.put(RegistryEngine.PROPNAME_MAX_NAME_LENGTH,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_NAME_LENGTH));

        props.put(RegistryEngine.PROPNAME_MAX_MESSAGE_SIZE,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_MESSAGE_SIZE));        

        props.put(RegistryEngine.PROPNAME_MAX_BUSINESSES_PER_PUBLISHER,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BUSINESSES_PER_PUBLISHER));
        
        props.put(RegistryEngine.PROPNAME_MAX_SERVICES_PER_BUSINESS,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_SERVICES_PER_BUSINESS));
        
        props.put(RegistryEngine.PROPNAME_MAX_BINDINGS_PER_SERVICE,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BINDINGS_PER_SERVICE));
        
        props.put(RegistryEngine.PROPNAME_MAX_TMODELS_PER_PUBLISHER,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_TMODELS_PER_PUBLISHER));
        
        props.put(RegistryEngine.PROPNAME_MAX_ROWS_LIMIT,           
                  Integer.toString(RegistryEngine.DEFAULT_MAX_ROWS_LIMIT));
        
        props.put(RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_INITIAL,           
                RegistryEngine.DEFAULT_JAVA_NAMING_FACTORY_INITIAL);
      
        props.put(RegistryEngine.PROPNAME_JAVA_NAMING_PROVIDER_URL,           
                RegistryEngine.DEFAULT_JAVA_NAMING_PROVIDER_URL);
      
        props.put(RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_URL_PKGS,           
                RegistryEngine.DEFAULT_JAVA_NAMING_FACTORY_URL_PKGS);
        
      }
    }
    catch(IOException ioex) {
      log.error(ioex.getMessage(),ioex);
    }

    log.debug("Initializing jUDDI components.");
    
    registry = new RegistryEngine(props);
    registry.init();
  }
  
  /**
   * Grab the shared instance of jUDDI's Registry class and
   * call it's "dispose()" method to notify all sub-components
   * to stop any background threads and release any external
   * resources they may have aquired.
   */
  public void destroy()
  {
    log.info("jUDDI Stopping: Cleaning up existing resources.");

    RegistryEngine registry = Registry.getRegistry();
    if (registry != null)
      registry.dispose();
  }

  /**
   *
   */
  public static RegistryEngine getRegistry()
  {
      return registry;
  }

  private Registry() {
	super();
  }
  
  
}