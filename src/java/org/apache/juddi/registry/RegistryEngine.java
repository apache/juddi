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
package org.apache.juddi.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.function.FunctionMaker;
import org.apache.juddi.function.IFunction;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryEngine extends Registry
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(RegistryEngine.class);

  // jUDDI shared UDDI Function maker
  private static FunctionMaker maker = FunctionMaker.getInstance();

  // registry singleton instance
  private static RegistryEngine registry = null;

  // registry status
  private boolean isAvailable = false;

  /**
   *
   */
  private RegistryEngine()
  {
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