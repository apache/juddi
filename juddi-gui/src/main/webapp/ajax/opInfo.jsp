<%-- 
    Document   : opInfo
    Created on : Mar 28, 2013, 10:25:03 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String id=null;
    id = request.getParameter("id");
    if (id!=null){ 
        UddiHub x = UddiHub.getInstance(application, session);
        String msg=(x.GetOperationalInfo(x.GetOperationalInfo(id)));
        if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                response.setStatus(406);
        out.write(msg);
    }


%>