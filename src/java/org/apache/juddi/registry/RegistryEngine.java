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
package org.apache.juddi.registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.function.FunctionMaker;
import org.apache.juddi.function.IFunction;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Loader;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryEngine extends AbstractRegistry
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(RegistryEngine.class);

  // jUDDI Registry Property File Name
  private static final String PROPFILE_NAME = "juddi.properties";
  
  // jUDDI Function maker
  private FunctionMaker maker = null;

  // registry singleton instance
  private static RegistryEngine registry = null;

  // registry status
  private boolean isAvailable = false;

  /**
   * Create a new instance of RegistryEngine.  This constructor
   * looks in the classpath for a file named 'juddi.properties'
   * and uses property values in this file to initialize the
   * new instance. Default values are used if the file does not
   * exist or if a particular property value is not present.
   */
  private RegistryEngine()
  {
    super();

    // Add jUDDI properties from the juddi.properties
    // file found in the classpath. Duplicate property
    // values added in init() will be overwritten.
    try {
      InputStream stream = Loader.getResourceAsStream(PROPFILE_NAME);
      if (stream != null)
      {
        Properties props = new Properties();
        props.load(stream);
        Config.addProperties(props);
      }
    }
    catch (IOException ioex) {
      log.error("An error occured while loading properties from: "+PROPFILE_NAME,ioex);
    }
  }

  /**
   * Creates a new instance of RegistryEngine. This constructor
   * uses the property values passed in the Properties parameter
   * to initialize the new RegistryProxy instance. Default values 
   * are used if the file does not exist or if a particular 
   * property value is not present.
   */
  private RegistryEngine(Properties props)
  {
    super();

    if (props != null)
      Config.addProperties(props);
  }

  /**
   *
   */
  public static RegistryEngine getInstance()
  {
    if (registry == null)
      registry = createInstance();
    return registry;
  }

  /**
   *
   */
  private static synchronized RegistryEngine createInstance()
  {
    if (registry == null)
      registry = new RegistryEngine();
    return registry;
  }

  /**
   * Initialize required resources.
   */
  public void init()
  {
    // turn off registry access

    isAvailable = false;

    // create, initialize and register
    // the core jUDDI components.

    DataStoreFactory.initFactory();
    
    this.maker = FunctionMaker.getInstance();

    // turn on registry access

    isAvailable = true;
  }

  /**
   * Releases any acquired resources. Will stop these
   * if they are currently running.
   */
  public void dispose()
  {
    // turn off registry access

    isAvailable = false;

    // call each sub-component's dispose methods

    DataStoreFactory.destroyFactory();
  }

  /**
   * Returns 'true' if the registry is available
   * to handle requests, otherwise returns 'false'.
   */
  public boolean isAvailable()
  {
    return this.isAvailable;
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject request)
    throws RegistryException
  {
    String className = request.getClass().getName();

    IFunction function = (IFunction)maker.lookup(className);
    if (function == null)
      throw new UnsupportedException(className);

    RegistryObject response = function.execute(request);

    return response;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    // okay, let's build the registry
    RegistryEngine registry = RegistryEngine.getInstance();
    registry.init();

    // dump all registry properties to the console
    System.out.println(Config.getProperties());

    // test the logger
    log.debug("log.debug");
    log.info("log.info");
    log.warn("log.warn");
    log.error("log.error");
    log.fatal("log.fatal");

    // tear down the registry
    registry.dispose();
  }
}