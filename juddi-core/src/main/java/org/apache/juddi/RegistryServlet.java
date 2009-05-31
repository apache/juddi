/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * This servlet is used to initialize the jUDDI webapp on
 * startup and cleanup the jUDDI webapp on shutdown.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class RegistryServlet extends HttpServlet
{
	private Logger log = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	/**
	 * Initialize all core components.
	 */
	public void init(ServletConfig config) 
	throws ServletException
	{
		log.info("Initializing jUDDI components.");
		super.init(config);
		try {
			Registry.start();
		} catch (ConfigurationException ce) {
			throw new ServletException(ce.getMessage(),ce);
		}
	}

	/**
	 * Notify all sub-components and
	 * stop any background threads and release any external
	 * resources they may have acquired.
	 */
	public void destroy()
	{
		super.destroy();
		log.info("jUDDI Stopping: Cleaning up existing resources.");
		Registry.stop();
	}
}