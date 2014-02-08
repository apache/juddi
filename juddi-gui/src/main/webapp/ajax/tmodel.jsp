<%-- 
    Document   : addKeyGenerator
    Created on : Feb 28, 2013, 8:10:10 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%        UddiHub hub = UddiHub.getInstance(application, session);
        if (request.getMethod().equalsIgnoreCase("POST")) {
                if (request.getParameter("action") != null) {
                        String action = request.getParameter("action");
                        if (action.equalsIgnoreCase("createKeyGen")) {
                               String msg=(hub.AddTmodelKenGenerator(request.getParameter("keygen"), 
                                        request.getParameter("keyname"), 
                                        request.getParameter("keylang")));
                               if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                                        response.setStatus(406);
                                out.write(msg);
                        } else {
                                response.sendRedirect("/index.jsp");
                        }
                } else {
                        response.sendRedirect("/index.jsp");
                }
        } else {
                response.sendRedirect("/index.jsp");
        }
%>
