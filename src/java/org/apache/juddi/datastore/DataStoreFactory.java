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
package org.apache.juddi.datastore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.util.Config;

/**
 * Implementation of Factory pattern responsible for instantiating
 * the DataStore interface implementation.
 *
 * The name of the class to instantiate should exist as a property
 * in the juddi.properties configuration file with a property name
 * of juddi.datasource.datastoreClassName. If the property is not
 * found an Exception is thrown.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public abstract class DataStoreFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(DataStoreFactory.class);

  // the shared DataStoreFactory instance
  private static DataStoreFactory factory = null;

  /**
   *
   */
  public abstract DataStore acquireDataStore()
    throws RegistryException;

  /**
   *
   */
  public abstract void releaseDataStore(DataStore datastore)
    throws RegistryException;

  /**
   *
   */
  public abstract void init();
  
  /**
   *
   */
  public abstract void destroy();

  /**
   *
   */
  public static void initFactory()
  {
    if (factory != null)
    {
      destroyFactory();
    }

    factory = createInstance();
  }

  /**
   * Returns a new instance of the DataStore interface as specified by the
   * juddi.datastore property in the juddi.properties configuration file.
   * @return DataStoreFactory
   */
  public static DataStoreFactory getFactory()
  {
    if (factory == null)
    {
      factory = createInstance();
    }

    return factory;
  }

  /**
   *
   */
  public static void destroyFactory()
  {
    if (factory != null)
    {
      factory.destroy();
    }

    factory = null;
  }

  /**
   * Loads and returns a shared instance of the DataStore interface
   * as specified by the juddi.dataStoreFactory property.
   */
  private static synchronized DataStoreFactory createInstance()
  {
    if (factory != null)
      return factory;

    // try to obtain the name of the DataStore implementaion to create
    String className = Config.getDataStoreFactory();

    // write DataStoreFactory Property to the log for good measure
    log.debug(className);

    Class factoryClass = null;
    try
    {
      // instruct the class loader to load the DataStore implementation
      factoryClass = java.lang.Class.forName(className);
    }
    catch(ClassNotFoundException e)
    {
      log.error("The specified sub class of the DataStoreFactory class was not " +
        "found in classpath: " + className + " not found.");
      log.error(e);
    }

    try
    {
      // try to instantiate the DataStoreFactory subclass
      factory = (DataStoreFactory)factoryClass.newInstance();
    }
    catch(java.lang.Exception e)
    {
      log.error("Exception while attempting to instantiate subclass of " +
        "DataStoreFactory class: " + factoryClass.getName() + "\n" + e.getMessage());
      log.error(e);
    }

    // initialize the newly created
    // DataStoreFactory instance.

    factory.init();

    return factory;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    DataStoreFactory factory = DataStoreFactory.getFactory();
    if (factory != null)
    {
      System.out.println("Got a DataStoreFactory: "+factory.getClass().getName());
    }
    else
    {
      System.out.println("Sorry - no DataStoreFactory for you.");
    }
  }
}