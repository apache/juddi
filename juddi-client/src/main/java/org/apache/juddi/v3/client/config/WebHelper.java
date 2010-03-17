package org.apache.juddi.v3.client.config;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.transport.Transport;

public class WebHelper {
	
	public static String JUDDI_CLIENT_MANAGER_INSTANCE  = "juddi.client.manager.instance";
	public static String JUDDI_CLIENT_HOMENODE_INSTANCE = "juddi.client.homenode.instance";
	public static String JUDDI_CLIENT_TRANSPORT_INSTANCE = "juddi.client.transport.instance";
	

	public static UDDIClerkManager getUDDIClerkManager(ServletContext servletContext) throws ConfigurationException {
		UDDIClerkManager manager = (UDDIClerkManager) servletContext.getAttribute(JUDDI_CLIENT_MANAGER_INSTANCE);
		if (manager==null) {
			manager = UDDIClientContainer.getUDDIClerkManager(getUDDIClerkManagerName(servletContext));
			servletContext.setAttribute(JUDDI_CLIENT_MANAGER_INSTANCE, manager);
		}
		return manager;
	}
	
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
	
	private static String getUDDIClerkManagerName(ServletContext servletContext) throws ConfigurationException {
		
		if (servletContext.getAttribute(UDDIClerkServlet.UDDI_CLIENT_MANAGER_NAME) != null) {
			return String.valueOf(servletContext.getAttribute(UDDIClerkServlet.UDDI_CLIENT_MANAGER_NAME));
		} else {
			String fileName = servletContext.getInitParameter(UDDIClerkServlet.UDDI_CLIENT_CONFIG_FILE);
		    if (fileName!=null) {
		    	UDDIClerkManager manager = new UDDIClerkManager(fileName);
				manager.getName();
		    	servletContext.setAttribute(UDDIClerkServlet.UDDI_CLIENT_MANAGER_NAME, manager.getName());
		    	return manager.getName();
		    } else {
		    	String managerName = String.valueOf(servletContext.getInitParameter(UDDIClerkServlet.UDDI_CLIENT_MANAGER_NAME));
		    	if (managerName!=null) {
		    		return managerName;
		    	} else {
		    		throw new ConfigurationException("ManagerName could not be obtained. Please check your web.xml and" +
		    				" make sure to either specify the " + UDDIClerkServlet.UDDI_CLIENT_CONFIG_FILE + " or " + UDDIClerkServlet.UDDI_CLIENT_MANAGER_NAME);
		    	}
		    }
		}
	}
}
