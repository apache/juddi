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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.registry.RegistryEngine;

/**
 * This servlet is ONLY used to initialize the jUDDI webapp on
 * startup and cleanup the jUDDI webapp on shutdown.
 * 
 * @author Steve Viens (sviens@apache.org)
 */
public class InitServlet extends HttpServlet
{
  // private reference to the webapp's logger.
  private static Log log = LogFactory.getLog(InitServlet.class);

  /**
   * Grab the shared instance of jUDDI's Registry class
   * (this will typically create the registry for the first
   * time) and call it's "init()" method to get all core
   * components initialized.
   */
  public void init()
  	throws ServletException
  {
    super.init();
    
    log.info("jUDDI Starting: Initializing resources and subsystems.");

    RegistryEngine registry = RegistryEngine.getInstance();
    if (registry != null)
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
    super.destroy();
    
    log.info("jUDDI Stopping: Cleaning up existing resources.");

    RegistryEngine registry = RegistryEngine.getInstance();
    if (registry != null)
      registry.dispose();
  }
}