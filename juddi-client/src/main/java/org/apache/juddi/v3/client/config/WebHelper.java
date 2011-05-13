/*
 * Copyright 2001-2011 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.v3.client.config;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.transport.Transport;

public class WebHelper {
	
	public static Log logger = LogFactory.getLog(WebHelper.class);
	public static final String UDDI_CLIENT_MANAGER_NAME  = "uddi.client.manager.name";
	public static final String UDDI_CLIENT_CONFIG_FILE    = "uddi.client.config.file";
	public static final String JUDDI_CLIENT_MANAGER_NAME  = "juddi.client.manager.name";
	public static final String JUDDI_CLIENT_TRANSPORT     = "juddi.client.transport";
	

	/**
	 * Checks the servlet context for the manager defined in the web context. Optionally, in your 
	 * web.xml you can specify either the manager name if you want to use an existing manager 
	 * called 'uddi-portlet-manager':
	 * <pre>
	 * &lt;context-param&gt;
	 *   &lt;param-name&gt;uddi.client.manager.name&lt;/param-name&gt;
	 *   &lt;param-value&gt;uddi-portlet-manager&lt;/param-value&gt;
	 * &lt;/context-param&gt;
	 * </pre>
	 * or, if you don't want to use the default META-INF/uddi.xml file path, but 'META-INF/my-uddi.xml' instead,
	 * then you can set:
	 * <pre>
	 * &lt;context-param&gt;
	 *   &lt;param-name&gt;uddi.client.config.path&lt;/param-name&gt;
	 *   &lt;param-value&gt;META-INF/my-uddi.xml&lt;/param-value&gt;
	 * &lt;/context-param&gt;
	 * </pre>
	 * @param servletContext
	 * @return
	 * @throws ConfigurationException
	 */
	public static UDDIClerkManager getUDDIClerkManager(ServletContext servletContext) throws ConfigurationException 
	{
		if (servletContext.getAttribute(JUDDI_CLIENT_MANAGER_NAME)!=null) {
			String managerName = String.valueOf(servletContext.getAttribute(JUDDI_CLIENT_MANAGER_NAME));
			return UDDIClientContainer.getUDDIClerkManager(managerName);
		} else {
			String managerName = servletContext.getInitParameter(UDDI_CLIENT_MANAGER_NAME);
			if (managerName!=null) {
				try {
					UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					logger.info("Manager " + managerName + " was already started.");
					servletContext.setAttribute(JUDDI_CLIENT_MANAGER_NAME, managerName);
					return manager;
				} catch (ConfigurationException ce) {
					logger.debug("Manager " + managerName + " is not yet started.");
				}
			}
			String clientConfigFile = servletContext.getInitParameter(UDDI_CLIENT_CONFIG_FILE);
			if (clientConfigFile==null) clientConfigFile = ClientConfig.DEFAULT_UDDI_CONFIG;
			
			logger.info("Reading the managerName from the clientConfig file " + clientConfigFile);
			UDDIClerkManager manager = new UDDIClerkManager(clientConfigFile);
			if (clientConfigFile==null && manager.getName()==null) {
				logger.warn("Deprecated, manager name set to 'default', however it should be provided in the uddi.xml");
				managerName = "default";
			}
			if (manager.getName()!=null) {
				logger.info("Starting Clerk Manager " + manager.getName() + "...");
			} else {
				throw new ConfigurationException("A manager name needs to be specified in the client config file.");
			}
			manager.start();
			UDDIClientContainer.addClerkManager(manager);
			servletContext.setAttribute(JUDDI_CLIENT_MANAGER_NAME, managerName);
			return manager;
		}
	}
	/**
	 * 
	 * @param servletContext
	 * @return
	 * @throws ConfigurationException
	 */
	public static UDDINode getUDDIHomeNode(ServletContext servletContext) throws ConfigurationException {
		UDDIClerkManager manager = getUDDIClerkManager(servletContext);
		return manager.getClientConfig().getHomeNode();	
	}
	
	public static Transport getTransport(ServletContext servletContext) 
		   throws ConfigurationException, ClassNotFoundException, IllegalArgumentException, 
		    SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		Transport transport = (Transport) servletContext.getAttribute(JUDDI_CLIENT_TRANSPORT);
		if (transport==null) {
			UDDIClerkManager manager = getUDDIClerkManager(servletContext);
			UDDINode node = manager.getClientConfig().getHomeNode();
			Class<?> transportClass = ClassUtil.forName(node.getProxyTransport(), Transport.class);
			transport = (Transport) transportClass.getConstructor(String.class,String.class).newInstance(manager.getName(),node.getName());
			servletContext.setAttribute(JUDDI_CLIENT_TRANSPORT, transport);
		}
		return transport;
	}
	
	public static Transport getTransport(ServletContext servletContext, UDDINode remoteNode) 
	   throws ConfigurationException, ClassNotFoundException, IllegalArgumentException, 
	    SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		Transport transport = (Transport) servletContext.getAttribute(JUDDI_CLIENT_TRANSPORT + "-" + remoteNode.getName());
		if (transport==null) {
			UDDIClerkManager manager = getUDDIClerkManager(servletContext);
			Class<?> transportClass = ClassUtil.forName(remoteNode.getProxyTransport(), Transport.class);
			transport = (Transport) transportClass.getConstructor(String.class,String.class).newInstance(manager.getName(),remoteNode.getName());
			servletContext.setAttribute(JUDDI_CLIENT_TRANSPORT + "-" + remoteNode.getName(), transport);
		}
		return transport;
	}
	
}
