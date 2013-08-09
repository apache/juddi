<%-- 
    Document   : logout page
    Created on : Feb 25, 2013, 10:25:48 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiAdminHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    UddiAdminHub hub = UddiAdminHub.getInstance(application, session);
    UddiAdminHub.reset(session);
    hub.die();
    session.removeAttribute("username");
    session.removeAttribute("password");
    // session.invalidate();
    // response.sendRedirect("index.jsp");
//do not invalidate the session, this will cause the form nonce to clear until the browser url changes
%>