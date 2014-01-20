<%-- 
    Document   : abortTransferToken
    Created on : Apr 27, 2013, 1:20:37 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.jaxb.PrintUDDI"%>
<%@page import="org.uddi.custody_v3.TransferToken"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.ws.Holder"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="../csrf.jsp" />
<!DOCTYPE html>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String tokenxml = request.getParameter("tokenxml");
 

        UddiHub x = UddiHub.getInstance(application, session);

        String msg = x.DiscardToken(tokenxml);
        if (msg != null) {
            out.write(msg);
        } else {
            out.write(ResourceLoader.GetResource(session, "actions.canceled"));

        }
    }


%>