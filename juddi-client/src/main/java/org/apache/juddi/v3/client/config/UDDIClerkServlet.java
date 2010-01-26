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
package org.apache.juddi.v3.client.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

/**
 * This servlet is to initialize the UDDIclient on deployment and
 * cleanup on shutdown.
 * 
 * @author Kurt Stam (kstam@apache.org)
 */
public class UDDIClerkServlet extends HttpServlet {
	
	private static final long serialVersionUID = -91998529871296125L;
	private Logger logger = Logger.getLogger(UDDIClerkServlet.class);
	UDDIClerkManager manager = null;
	/**
	 * Starting the UDDIClerkManager
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			String clientConfigFile = config.getInitParameter("uddi.client.config");
			if (clientConfigFile==null) clientConfigFile = ClientConfig.UDDI_CONFIG;
			UDDIClerkManager manager = new UDDIClerkManager(clientConfigFile);
			logger.info("Starting Clerk Manager " + manager.getName() + "...");
			manager.start();
		} catch (Exception e) {
			logger.error("UDDI-client could not be started for manager " + manager.getName() + ". "
					+ e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("UDDI-client could not be started."
					+ t.getMessage(), t);
		}
	}
	
	@Override
	public void destroy() {
		try {
			manager.stop();
		} catch (Exception e) {
			logger.error("UDDI-Clerk Manager could not be stopped for manager " + manager.getName() + ". "
					+ e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("UDDI-client could not be stopped."
					+ t.getMessage(), t);
		}
		super.destroy();
	}

}