<%-- 
    Document   : deletebusiness
    Created on : Feb 27, 2013, 8:39:54 PM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) { 
        UddiHub x =UddiHub.getInstance(application, session);
       out.write(x.deleteBusiness(request.getParameter("id")));
    }

%>