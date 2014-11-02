<%-- 
    Document   : savebinding
    Created on : Feb 8, 2014, 7:55:57 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<!DOCTYPE html>
<%

        if (request.getMethod().equalsIgnoreCase("POST")) {
                UddiHub x = UddiHub.getInstance(application, request.getSession());
                String msg = (x.deleteBinding(request.getParameter("id")));
                String svcid = ((request.getParameter("svcid")));
                if (msg.contains(ResourceLoader.GetResource(session, "errors.generic"))) {
                        response.setStatus(406);
                }
                out.write(msg);
                if (svcid!=null)
                        out.write("<a href=\"serviceEditor.jsp?id=" + StringEscapeUtils.escapeHtml(svcid.trim()) + "\">" +ResourceLoader.GetResource(session, "actions.return") +"</a>");
        }

%>