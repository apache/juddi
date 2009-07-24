package org.apache.juddi.portlets.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

public class UDDIBrowserPortlet extends GenericPortlet 
{
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) 
		throws PortletException, PortletSecurityException, IOException
	{
	  renderResponse.setContentType("text/html");
	  PrintWriter writer = renderResponse.getWriter();
	  writer.println("<script type='text/javascript' language='javascript' src='" 
			  + renderRequest.getContextPath() 
			  + "/org.apache.juddi.portlets.UDDIBrowser/org.apache.juddi.portlets.UDDIBrowser.nocache.js'></script>");
	  writer.println("<div id=\"token\"></div>");
	  writer.println("<div id=\"browser\"></div>");
	  PortletURL url = renderResponse.createActionURL(); 
	  url.setWindowState(WindowState.MAXIMIZED);
	  writer.print("<form method=\"post\" action=\"" + url.toString() + ">");
	  writer.print("<input type='submit'");
	  writer.print("</form>");

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
	
	@Override
	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
		response.setWindowState(WindowState.MAXIMIZED);
		System.out.println("ProcessAction");
		super.processAction(request, response);
	}
	
	
}
