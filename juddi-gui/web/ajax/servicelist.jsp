<%-- 
    Document   : businesslist
    Created on : Feb 24, 2013, 10:27:22 AM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<!DOCTYPE html>
<%
    UddiHub x = UddiHub.getInstance(application, request.getSession());
    out.write(x.GetServiceList(request.getParameter("id")));
%>