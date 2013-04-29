<%-- 
    Document   : businessAsSelect
    Created on : Apr 27, 2013, 10:05:21 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="../csrf.jsp" />
<%
    UddiHub x = UddiHub.getInstance(application, session);

    String lang = request.getParameter("lang");
    if (lang == null || lang.length() == 0) {
        lang = null;
    }
    if (lang != null && lang.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
        lang = null;
    }
  
   

    out.write(x.GetMyTransferableKeys(true,true));

%>