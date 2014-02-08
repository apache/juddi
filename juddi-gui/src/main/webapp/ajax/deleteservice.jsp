<%-- 
    Document   : deleteservice
    Created on : Mar 16, 2013, 12:25:13 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) { 
        UddiHub x = UddiHub.getInstance(application, session);
        
        String msg=(x.deleteService(request.getParameter("id")));
        if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                response.setStatus(406);
        out.write(msg);
    }

%>