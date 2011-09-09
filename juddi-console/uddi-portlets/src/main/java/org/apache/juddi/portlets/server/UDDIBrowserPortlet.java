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
 *
 */
package org.apache.juddi.portlets.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
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
	  writer.println("<link rel=\"stylesheet\" href=\""
			  + renderRequest.getContextPath() 
			  + "/uddiportlets.css\">");
	  writer.println("<div id=\"token\"></div>");
	  writer.println("<div id=\"browser\"></div>");
//	  PortletURL url = renderResponse.createActionURL(); 
//	  url.setWindowState(WindowState.MAXIMIZED);
//	  writer.print("<form method=\"post\" action=\"" + url.toString() + ">");
//	  writer.print("<input type='submit'");
//	  writer.print("</form>");
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
