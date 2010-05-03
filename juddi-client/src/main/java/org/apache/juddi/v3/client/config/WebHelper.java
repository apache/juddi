package org.apache.juddi.v3.client.config;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.log4j.Logger;

public class WebHelper {
	
	public static Logger logger = Logger.getLogger(WebHelper.class);
	public static String JUDDI_CLIENT_MANAGER_INSTANCE   = "juddi.client.manager.instance";
	public static String JUDDI_CLIENT_HOMENODE_INSTANCE  = "juddi.client.homenode.instance";
	public static String JUDDI_CLIENT_TRANSPORT_INSTANCE = "juddi.client.transport.instance";
	

	/**
	 * Checks the servlet context for the manager defined in the web context, this means 
	 * @param servletContext
	 * @return
	 * @throws ConfigurationException
	 */
	public static UDDIClerkManager getUDDIClerkManager(ServletContext servletContext) throws ConfigurationException 
	{
		UDDIClerkManager manager = (UDDIClerkManager) servletContext.getAttribute(JUDDI_CLIENT_MANAGER_INSTANCE);
		if (manager==null) {
			String managerName = String.valueOf(servletContext.getAttribute(UDDIClerkServlet.UDDI_CLIENT_MANAGER_NAME));
			if (managerName!=null) {
				try {
					manager = UDDIClientContainer.getUDDIClerkManager(managerName);
					logger.info("Manager " + managerName + " was already started.");
					return manager;
				} catch (ConfigurationException ce) {
					logger.debug("Manager " + managerName + " is not yet started.");
				}
			}
			String clientConfigFile = servletContext.getInitParameter(UDDIClerkServlet.UDDI_CLIENT_CONFIG_FILE);
			if (clientConfigFile==null) clientConfigFile = ClientConfig.DEFAULT_UDDI_CONFIG;
			
			logger.info("Reading the managerName from the clientConfig file " + clientConfigFile);
			manager = new UDDIClerkManager(clientConfigFile);
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
			servletContext.setAttribute(UDDIClerkServlet.UDDI_CLIENT_MANAGER_NAME, manager.getName());
			servletContext.setAttribute(JUDDI_CLIENT_MANAGER_INSTANCE, manager);
		}
		return manager;
	}
	/**
	 * 
	 * @param servletContext
	 * @return
	 * @throws ConfigurationException
	 */
	public static UDDINode getUDDIHomeNode(ServletContext servletContext) throws ConfigurationException {
		UDDINode homeNode = (UDDINode) servletContext.getAttribute(JUDDI_CLIENT_HOMENODE_INSTANCE);
		if (homeNode==null) {
			UDDIClerkManager manager = getUDDIClerkManager(servletContext);
			homeNode = manager.getClientConfig().getHomeNode();
			servletContext.setAttribute(JUDDI_CLIENT_HOMENODE_INSTANCE, homeNode);
		}
		return homeNode;
	}
	
	public static Transport getTransport(ServletContext servletContext) 
		   throws ConfigurationException, ClassNotFoundException, IllegalArgumentException, 
		    SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		Transport transport = (Transport) servletContext.getAttribute(JUDDI_CLIENT_TRANSPORT_INSTANCE);
		if (transport==null) {
			UDDIClerkManager manager = getUDDIClerkManager(servletContext);
			UDDINode node = getUDDIHomeNode(servletContext); 
			Class<?> transportClass = ClassUtil.forName(node.getProxyTransport(), Transport.class);
			transport = (Transport) transportClass.getConstructor(String.class,String.class).newInstance(manager.getName(),node.getName());
			servletContext.setAttribute(JUDDI_CLIENT_TRANSPORT_INSTANCE, transport);
		}
		return transport;
	}
	
	public static Transport getTransport(ServletContext servletContext, UDDINode remoteNode) 
	   throws ConfigurationException, ClassNotFoundException, IllegalArgumentException, 
	    SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		Transport transport = (Transport) servletContext.getAttribute(JUDDI_CLIENT_TRANSPORT_INSTANCE + "-" + remoteNode.getName());
		if (transport==null) {
			UDDIClerkManager manager = getUDDIClerkManager(servletContext);
			Class<?> transportClass = ClassUtil.forName(remoteNode.getProxyTransport(), Transport.class);
			transport = (Transport) transportClass.getConstructor(String.class,String.class).newInstance(manager.getName(),remoteNode.getName());
			servletContext.setAttribute(JUDDI_CLIENT_TRANSPORT_INSTANCE + "-" + remoteNode.getName(), transport);
		}
		return transport;
	}
	
}
