<%-- 
    Document   : servicedetails
    Created on : Feb 24, 2013, 1:55:14 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<!DOCTYPE html>
<%
    UddiHub x = UddiHub.getInstance(application, request.getSession());

    out.write(x.GetServiceDetailAsHtml(request.getParameter("id")));
%>