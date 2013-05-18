<%-- 
    Document   : deleteservice
    Created on : Mar 16, 2013, 12:25:13 PM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        UddiHub x = UddiHub.getInstance(application, session);
        out.write(x.deleteService(request.getParameter("id")));
    }

%>