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
package org.apache.juddi.transport.axis;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.transport.http.AxisServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.registry.RegistryEngine;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AdminServlet extends AxisServlet
{
  // private reference to the webapp's logger.
  private static Log log = LogFactory.getLog(AdminServlet.class);

  /**
   * Grab the shared instance of jUDDI's Registry class
   * (this will typically create the registry for the first
   * time) and call it's "init()" method to get all core
   * components initialized.
   */
  public void init()
  {
    super.init();

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

    RegistryEngine registry = RegistryEngine.getInstance();
    if (registry != null)
      registry.dispose();
  }

  /**
   *
   */
   public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
  {
    req.getRequestDispatcher("/index.html").forward(req,res);
  }
}