<%-- 
    Document   : loginpost
    Created on : Feb 24, 2013, 3:36:37 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.util.Properties"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.net.URL"%>
<%@page import="org.apache.juddi.webconsole.AES"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include  file="../csrf.jsp" %>
<%

    URL prop = application.getResource("/META-INF/config.properties");
    if (prop == null) {
        throw new Exception("Cannot locate the configuration file.");
    }

    InputStream in = prop.openStream();
    Properties p = new Properties();
    p.load(in);
    in.close();
    session.setAttribute("username", request.getParameter("username"));
    session.setAttribute("password", AES.Encrypt(request.getParameter("password"), (String)p.get("key")));



    UddiHub.reset(request.getSession());
    UddiHub x = UddiHub.getInstance(application, request.getSession());
    


%>