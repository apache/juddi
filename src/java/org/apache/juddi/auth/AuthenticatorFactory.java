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