<%-- 
    Document   : advanced
    Created on : Aug 9, 2013, 4:09:06 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.adminconsole.hub.JUDDIRequestsAsXML"%>
<%@page import="org.apache.juddi.adminconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.adminconsole.PostBackConstants"%>
<%@page import="org.apache.juddi.adminconsole.hub.UddiAdminHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String action = request.getParameter("advancedaction");
        if (action != null) {
            String method = request.getParameter("method");
            
            UddiAdminHub x = UddiAdminHub.getInstance(application,session);
            if (action.equalsIgnoreCase("getdefaultrequest")) {
 
               
                    out.write(JUDDIRequestsAsXML.getSampleXML(method));
               
            } else if (action.equalsIgnoreCase("senddata")) {
                Object j=JUDDIRequestsAsXML.getObjectJuddi(method, request.getParameter("content"));
                String msg=(x.SendAdvanced(j, method));
                if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                        response.setStatus(406);
                out.write(msg);
            }
        }



    }


%>