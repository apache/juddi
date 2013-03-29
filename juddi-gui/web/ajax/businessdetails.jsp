<%-- 
    Document   : businessdetails
    Created on : Feb 24, 2013, 10:49:18 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<!DOCTYPE html>
<%
    UddiHub x = UddiHub.getInstance(application, request.getSession());
    out.write(x.GetBusinessDetailsAsHtml(request.getParameter("id")));
%>