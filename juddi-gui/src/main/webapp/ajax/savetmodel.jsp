<%-- 
   Document   : savebusiness
   Created on : Feb 26, 2013, 6:57:52 AM
   Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        /*  Enumeration it = request.getParameterNames();
         while (it.hasMoreElements()) {
         String name = (String) it.nextElement();
         out.write(name + " " + request.getParameter(name) + "<br>");
         }*/
        //out.write("success");
        UddiHub hub = UddiHub.getInstance(application, session);

        out.write(hub.SaveTModel(request));
      
    }

%>