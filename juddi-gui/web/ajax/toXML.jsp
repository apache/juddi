<%-- 
    Document   : toXML
    Created on : Mar 14, 2013, 9:17:21 PM
    Author     : Alex O'Ree
--%><%@page import="javax.xml.bind.JAXB"%><%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%><%@page import="org.apache.juddi.jaxb.JAXBMarshaller"%><%@page import="org.apache.juddi.jaxb.EntityCreator"%><%@page import="org.apache.juddi.webconsole.hub.UddiHub"%><%@page import="org.apache.juddi.jaxb.PrintUDDI"%><%@page contentType="text/html" pageEncoding="UTF-8"%><%
//<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    //org.apache.juddi.jaxb.PrintUDDI p = new PrintUDDI();
    UddiHub x = UddiHub.getInstance(application, session);

    String type = request.getParameter("type");
    String id = request.getParameter("id");
    if ((type != null && type.length() != 0) && (id != null && id.length() != 0)) {
        Object j = null;
        if (type.equalsIgnoreCase("business")) {
            j = x.GetBusinessDetailsAsObject(id);
        } else if (type.equalsIgnoreCase("service")) {
            j = x.GetServiceDetailsAsObject(id);
        } else if (type.equalsIgnoreCase("bindingTemplate")) {
            j = x.GetBindingDetailsAsObject(id);
        } else if (type.equalsIgnoreCase("tModel")) {
            j = x.GettModelDetailsAsObject(id);
        }
        if (j != null) {
            JAXB.marshal(j, out);
         //   out.write(JAXBMarshaller.marshallToString(j, JAXBMarshaller.PACKAGE_UDDIAPI));
            // out.write(EntityCreator.outputEntityToString(j, "org.apache.juddi.api_v3"));
        } else {
            out.write(ResourceLoader.GetResource(session, "items.unknown"));
        }
    }

    //get parameter type
    //fetch from UDDI
    //convert to string and output



%>