<%-- 
    Document   : addKeyGenerator
    Created on : Feb 28, 2013, 8:10:10 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%    
    UddiHub hub = UddiHub.getInstance(application, session);
    if (request.getParameter("action") != null) {
        String action = request.getParameter("action");
        if (action.equalsIgnoreCase("createKeyGen")) {
            out.write(hub.AddTmodelKenGenerator(request.getParameter("key"),request.getParameter("name"), request.getParameter("lang")));
        }
    }
%>
