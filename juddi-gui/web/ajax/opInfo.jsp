<%-- 
    Document   : opInfo
    Created on : Mar 28, 2013, 10:25:03 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String id=null;
    id = request.getParameter("id");
    if (id!=null){
        UddiHub x = UddiHub.getInstance(application, session);
        out.write(x.GetOperationalInfo(x.GetOperationalInfo(id)));
    }


%>