<%-- 
    Document   : savebinding
    Created on : Feb 8, 2014, 7:55:57 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%

        if (request.getMethod().equalsIgnoreCase("POST")) {
                UddiHub x = UddiHub.getInstance(application, request.getSession());
                String msg = (x.SaveBindingTemplate(request));
                if (msg.contains(ResourceLoader.GetResource(session, "errors.generic"))) {
                        response.setStatus(406);
                }
                out.write(msg);
        }

%>