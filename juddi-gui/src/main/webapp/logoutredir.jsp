<%-- 
    Document   : logout page
    Created on : Feb 25, 2013, 10:25:48 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    UddiHub hub = UddiHub.getInstance(application, session);
    UddiHub.reset(session);
    hub.die();
    session.removeAttribute("username");
    session.removeAttribute("password");
   
    response.sendRedirect("index.jsp");
%>