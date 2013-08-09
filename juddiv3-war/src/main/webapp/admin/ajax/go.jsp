<%-- 
    Document   : go - handles requests for the admin actions page
    Created on : Aug 9, 2013, 6:52:50 PM
    Author     : Alex O'Ree
--%>

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
        } catch (Exception ex) {
            out.write("<i class=\"icon-thumbs-down icon-2x\"> Save Failed!<br>" + StringEscapeUtils.escapeHtml(ex.getMessage()));
        }
    }
%>