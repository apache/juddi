<%-- 
    Document   : fromXML
    Created on : Mar 24, 2013, 9:31:37 AM
    Author     : Alex O'Ree
--%><%@page import="org.apache.juddi.jaxb.JAXBMarshaller"%>
<%@page import="org.uddi.api_v3.TModel"%>
<%@page import="org.uddi.api_v3.BindingTemplate"%>
<%@page import="org.uddi.api_v3.BusinessService"%>
<%@page import="org.uddi.api_v3.BusinessEntity"%>
<%@page import="org.apache.juddi.jaxb.EntityCreator"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%><%@page import="org.apache.juddi.jaxb.PrintUDDI"%><%@page contentType="text/html" pageEncoding="UTF-8"%><%

    UddiHub x = UddiHub.getInstance(application, session);
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String type = request.getParameter("type");
        String id = request.getParameter("id");
        if ((type != null && type.length() != 0) && (id != null && id.length() != 0)) {


            String signedxml = request.getParameter("data");
            if (signedxml == null) {
                out.write("no input");
            } else {
                signedxml = signedxml.trim();
                if (signedxml.startsWith("<?xml ")) {
                    int idx=signedxml.indexOf(">");
                    signedxml = signedxml.substring(idx+1, signedxml.length()).trim();
                    
                }
                Object j = JAXBMarshaller.unmarshallFromString(signedxml.trim(), JAXBMarshaller.PACKAGE_UDDIAPI);

                if (type.equalsIgnoreCase("business")) {
                    BusinessEntity be = (BusinessEntity) j;
                    out.write(x.SaveBusinessDetails(be));
                } else if (type.equalsIgnoreCase("service")) {
                    BusinessService be = (BusinessService) j;
                    out.write(x.SaveService(be));
                } else if (type.equalsIgnoreCase("bindingTemplate")) {
                    BindingTemplate be = (BindingTemplate) j;
                    out.write(x.SaveBindingTemplate(be));
                } else if (type.equalsIgnoreCase("tModel")) {
                    TModel be = (TModel) j;
                    out.write(x.SaveTModel(be));
                } else {
                    out.write("Unrecongized entity type");
                }
            }
        }
    }



    //get parameter type
    //fetch from UDDI
    //convert to string and output



%>