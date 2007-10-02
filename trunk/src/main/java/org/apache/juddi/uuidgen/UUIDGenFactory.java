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
package org.apache.juddi.uuidgen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Loader;

/**
 * Used to create the org.apache.juddi.uuidgen.UUIDGen implementation
 * as specified by the 'juddi.uuidgen.impl' property. Defaults to
 * org.apache.juddi.uuidgen.SecureUUIDGen if an implementation is not
 * specified.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public abstract class UUIDGenFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(UUIDGenFactory.class);

  // UUIDGen property key & default implementation
  private static final String IMPL_KEY = "juddi.uuidgen";
  private static final String DEFAULT_IMPL = "org.apache.juddi.uuidgen.DefaultUUIDGen";

  // the shared UUIDGen instance
  private static UUIDGen uuidgen = null;

  /**
   * Returns a new instance of a UUIDGenFactory.
   *
   * @return UUIDGen
   */
  public static UUIDGen getUUIDGen()
  {
    if (uuidgen == null)
      uuidgen = createUUIDGen();
    return uuidgen;
  }

  /**
   * Returns a new instance of a UUIDGen.
   *
   * @return UUIDGen
   */
  private static synchronized UUIDGen createUUIDGen()
  {
    if (uuidgen != null)
      return uuidgen;

    // grab class name of the UUIDGen implementation to create
    String className = Config.getStringProperty(IMPL_KEY,DEFAULT_IMPL);

    // write the UUIDGen implementation name to the log
    log.debug("UUIDGen Implementation = " + className);

    Class uuidgenClass = null;
    try
    {
      // Use Loader to locate & load the UUIDGen implementation
      uuidgenClass = Loader.getClassForName(className);
    }
    catch(ClassNotFoundException e)
    {
      log.error("The specified UUIDGen class '" + className +
        "' was not found in classpath.");
      log.error(e);
    }

    try
    {
      // try to instantiate the UUIDGen implementation
      uuidgen = (UUIDGen)uuidgenClass.newInstance();
    }
    catch(Exception e)
    {
      log.error("Exception while attempting to instantiate the " +
        "implementation of UUIDGen: " + uuidgenClass.getName() +
        "\n" + e.getMessage());
      log.error(e);
    }

    return uuidgen;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // number of UUID's to generate
    final int max = 100;

    try
    {
      UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

      for (int i=0; i<max; ++i)
        System.out.println( i + ":  " + uuidgen.uuidgen());
    }
    catch (Exception ex) { ex.printStackTrace(); }
  }
}