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
import org.apache.juddi.util.Loader;

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
public class DataStoreFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(DataStoreFactory.class);

  // Authenticator property key & default implementation
  private static final String IMPL_KEY = "juddi.datastore";
  private static final String DEFAULT_IMPL = "org.apache.juddi.datastore.jdbc.JDBCDataStore";

  private static Class implClass = null;
  
  /**
   * Returns a new instance of a DataStore.
   *
   * @return DataStore
   */
  public static DataStore getDataStore()
  {
    DataStore dataStore = null;
    
    try
    {
      // make sure we know what class to create
      if (implClass == null)
        implClass = loadImplClass();

      // true if a configuration problem exists
      if (implClass == null)
        throw new RegistryException("The registry is not configured " +
            "correctly.");
        
      // try to instantiate a new DataStore
      dataStore = (DataStore)implClass.newInstance();
    }
    catch(Exception e)
    {
      log.error("Exception while attempting to instantiate the " +
        "implementation of DataStore: " + implClass.getName() +
        "\n" + e.getMessage());
      log.error(e);
    }

    return dataStore;
  }

  /**
   * Returns a new instance of a Authenticator.
   *
   * @return Authenticator
   */
  private static synchronized Class loadImplClass()
  {
    if (implClass != null)
      return implClass;
    
    // grab class name of the DataStore implementation to create
    String className = Config.getStringProperty(IMPL_KEY,DEFAULT_IMPL);

    // write the DataStore implementation name to the log
    log.debug("DataStore Implementation = " + className);

    try
    {
      // Use Loader to locate & load the DataStore implementation
      implClass = Loader.getClassForName(className);
    }
    catch(ClassNotFoundException e)
    {
      log.error("The registry is not configured correctly. The specified " +
                "DataStore class '" + className + "' was not found in " +
                "classpath.");
      log.error(e);
    }
    
    return implClass;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // delecare work variables
    int connCount = 10;
    DataStore[] stores = new DataStore[connCount];

    // aquire some connections
    for (int i=0; i<connCount; i++)
    {
      stores[i] = (DataStore)DataStoreFactory.getDataStore();
      if (stores[i] != null)
        System.out.println("Got a DataStore: "+stores[i].getClass().getName());
      else
        System.out.println("Sorry - A DataStore object could not be created.");
    }

    // release those connections
    for (int i=0; i<connCount; i++)
    {
      if (stores[i] != null)
      {
        stores[i].release();
        System.out.println("DataStore "+i+" released.");
      }
      else
        System.out.println("DataStore "+i+" was never successfully created.");
    }
  }
}