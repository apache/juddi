<%-- 
    Document   : businessAsSelect This actually returns a list of all businesses and/or tmodel keys owned by the current user
    Created on : Apr 27, 2013, 10:05:21 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="../csrf.jsp" />
<%
    UddiHub x = UddiHub.getInstance(application, session);
    String msg2 = x.GetMyTransferableKeys(true, true);
    if (msg2.contains(ResourceLoader.GetResource(session, "errors.generic")))
            response.setStatus(406);
    out.write(msg2);

%>