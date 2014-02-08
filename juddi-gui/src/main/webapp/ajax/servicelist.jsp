<%-- 
    Document   : businesslist
    Created on : Feb 24, 2013, 10:27:22 AM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<!DOCTYPE html>
<%
    UddiHub x = UddiHub.getInstance(application, request.getSession());
    String msg=(x.GetServiceList(request.getParameter("id"))); 
    if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                response.setStatus(406);
        out.write(msg);
%>