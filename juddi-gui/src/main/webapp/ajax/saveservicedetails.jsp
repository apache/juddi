<%-- 
    Document   : saveservicedetails
    Created on : Feb 24, 2013, 3:12:11 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>

<%@include  file="../csrf.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) { 
        UddiHub x = UddiHub.getInstance(application, request.getSession());
        String msg=(x.SaveServiceDetails(request));
        if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                response.setStatus(406);
        out.write(msg);
    }


%>