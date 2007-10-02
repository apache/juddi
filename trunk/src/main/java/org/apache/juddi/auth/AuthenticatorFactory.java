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
package org.apache.juddi.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Loader;

/**
 * Implementation of Factory pattern used to create an implementation of
 * the org.apache.juddi.auth.Authenticator interface.
 *
 * The name of the Authenticator implementation to create is passed to the
 * getAuthenticator method.  If a null value is passed then the default
 * Authenticator implementation "org.apache.juddi.auth.SimpleAuthenticator" is
 * created.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AuthenticatorFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AuthenticatorFactory.class);

  // Authenticator property key & default implementation
  private static final String IMPL_KEY = "juddi.auth";
  private static final String DEFAULT_IMPL = "org.apache.juddi.auth.DefaultAuthenticator";

  // the shared Authenticator instance
  private static Authenticator auth = null;

  /**
   * Returns a new instance of a AuthenticatorFactory.
   *
   * @return Authenticator
   */
  public static Authenticator getAuthenticator()
  {
    if (auth == null)
      auth = createAuthenticator();
    return auth;
  }

  /**
   * Returns a new instance of a Authenticator.
   *
   * @return Authenticator
   */
  private static synchronized Authenticator createAuthenticator()
  {
    if (auth != null)
      return auth;

    // grab class name of the Authenticator implementation to create
    String className = Config.getStringProperty(IMPL_KEY,DEFAULT_IMPL);

    // write the Authenticator implementation name to the log
    log.debug("Authenticator Implementation = " + className);

    Class authClass = null;
    try
    {
      // Use Loader to locate & load the Authenticator implementation
      authClass = Loader.getClassForName(className);
    }
    catch(ClassNotFoundException e)
    {
      log.error("The specified Authenticator class '" + className +
        "' was not found in classpath.");
      log.error(e);
    }

    try
    {
      // try to instantiate the Authenticator implementation
      auth = (Authenticator)authClass.newInstance();
    }
    catch(Exception e)
    {
      log.error("Exception while attempting to instantiate the " +
        "implementation of Authenticator: " + authClass.getName() +
        "\n" + e.getMessage());
      log.error(e);
    }

    return auth;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
      Authenticator auth = AuthenticatorFactory.getAuthenticator();
      if (auth != null)
      {
        System.out.println("Got Authenticator: "+auth.getClass().getName());

        String userID = auth.authenticate("sviens","password");

        System.out.println("The id "+userID+" was successfully authenticated.");
      }
      else
        System.out.println("Sorry: getAuthenticator returned 'null'.");
  }
}