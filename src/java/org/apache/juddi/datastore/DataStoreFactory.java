/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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