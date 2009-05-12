package org.apache.juddi.portlets.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class UDDISubscriptionNotificationPortlet extends GenericPortlet 
{
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) 
		throws PortletException, PortletSecurityException, IOException
	{		
	  renderResponse.setContentType("text/html");
	  PrintWriter writer = renderResponse.getWriter();
	  writer.println("<script type='text/javascript' language='javascript' src='" 
		  + renderRequest.getContextPath() 
		  + "/org.apache.juddi.portlets.UDDISubscriptionNotification/org.apache.juddi.portlets.UDDISubscriptionNotification.nocache.js'></script>");
	  writer.println("<div id=\"notification\"></div>");
	  writer.close();
	}

	protected void doHelp(RenderRequest renderRequest, RenderResponse renderResponse) 
		throws PortletException, PortletSecurityException, IOException
	{
	  renderResponse.setContentType("text/html");
	  PrintWriter writer = renderResponse.getWriter();
	  writer.write("Help");
	  writer.close();
	}
}
