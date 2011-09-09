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

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class JUDDIPublisherPortlet extends GenericPortlet 
{
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) 
		throws PortletException, PortletSecurityException, IOException
	{
	  renderResponse.setContentType("text/html");
	  PrintWriter writer = renderResponse.getWriter();
	  writer.println("<script type='text/javascript' language='javascript' src='" 
			  + renderRequest.getContextPath() 
			  + "/org.apache.juddi.portlets.JUDDIPublisher/org.apache.juddi.portlets.JUDDIPublisher.nocache.js'></script>");
	  writer.println("<link rel=\"stylesheet\" href=\""
			  + renderRequest.getContextPath() 
			  + "/uddiportlets.css\">");
	  writer.println("<div id=\"publisher\"></div>");
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
