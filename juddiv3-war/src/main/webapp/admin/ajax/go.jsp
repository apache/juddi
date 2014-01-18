<%-- 
    Document   : go - handles requests for the admin actions page
    Created on : Aug 9, 2013, 6:52:50 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiAdminHub"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        UddiAdminHub x = UddiAdminHub.getInstance(application, session);
        try {
            out.write(x.go(request));
            /*Enumeration it=request.getParameterNames();
            while (it.hasMoreElements()){
                    String s= (String)it.nextElement();
                    out.write("<br>"+s+"="+ request.getParameter(s));
            }*/
        } catch (Exception ex) {
            out.write("<i class=\"icon-thumbs-down icon-2x\"> " + ResourceLoader.GetResource(session, "pages.config.savefailed") +"<br>" + StringEscapeUtils.escapeHtml(ex.getMessage()));
        }
    }
%>