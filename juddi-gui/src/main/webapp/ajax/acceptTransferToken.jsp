<%-- 
    Document   : acceptTransferToken
    Created on : Apr 27, 2013, 2:17:35 PM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="javax.xml.bind.JAXB"%>
<%@page import="java.io.StringWriter"%>
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
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String keyBagXML = request.getParameter("keybag");
        String tokenXML = request.getParameter("tokenxml");

        if (keyBagXML==null || tokenXML==null)
                       {
            response.setStatus(400);
        }
        else
                       {
        UddiHub x = UddiHub.getInstance(application, session);
     
        String msg = x.AcceptCustodyTranferToken(tokenXML, keyBagXML);
        if (msg != null) {
            out.write(msg);
        } else {
           out.write(ResourceLoader.GetResource(session, "actions.success"));
          
        }
    }

    }
%>