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
package org.apache.juddi.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Loader;

/**
 * Implementation of Factory pattern used to create an implementation
 * of the org.apache.juddi.transport.Transport interface.
 *
 * The name of the Transport implementation to create is passed to the
 * getTransport method.  If a null value is passed then the default
 * Transport implementation "org.apache.juddi.transport.axis.AxisTransport" 
 * is created.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TransportFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(TransportFactory.class);

  // the loaded Transport class
  private static Class transportClass = null;

  /**
   * Made private to limit instantiate to other classes
   * in this package.
   */
  private TransportFactory()
  {
  }

  /**
   * Returns the shared instance of TransportFactory.
   *
   * @return TransportFactory
   */
  public static Transport getTransport()
  {
    if (transportClass == null)
    {
      // obtain the name of the jUDDI Transport to create
      String className = Config.getTransport();

      try {
        // instruct class loader to load the TransportFactory
        transportClass = Loader.getClassForName(className);
      }
      catch(ClassNotFoundException e) {
        log.error("The specified Transport implementation class '" +
          className + "' was not found in classpath.");
        log.error(e);
      }
    }

    Transport transport = null;

    try {
      // try to instantiate the TransportFactory
      transport = (Transport)transportClass.newInstance();
    }
    catch(java.lang.Exception e) {
      log.error("Exception while attempting to instantiate implementation " +
        "of Transport: " + transportClass.getName() + "\n" + e.getMessage());
      log.error(e);
    }

    return transport;
  }
}