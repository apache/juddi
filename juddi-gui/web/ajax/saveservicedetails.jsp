<%-- 
    Document   : saveservicedetails
    Created on : Feb 24, 2013, 3:12:11 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>

<%@include  file="../csrf.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        UddiHub x = UddiHub.getInstance(application, request.getSession());
        out.write(x.SaveServiceDetails(request));
    }


%>