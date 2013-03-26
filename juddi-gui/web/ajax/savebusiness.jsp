<%-- 
   Document   : savebusiness
   Created on : Feb 26, 2013, 6:57:52 AM
   Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.UddiHub"%>
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
        
        out.write(hub.SaveBusinessDetails(request));
        /*
 * first loop through to validate the data
 * second loop identify counts of each item, we may have to extend the class structure to make it countable
 */
    }

%>