<%-- 
    Document   : getCert - returns a base64 PKI certificate
    Created on : Mar 28, 2013, 6:39:09 PM
    Author     : Alex O'Ree
--%><%@page import="org.apache.juddi.webconsole.hub.UddiHub"%><%
        //note CSRF left off due to additiona endlines being injected (screws with parsing of the x509 cert)
    String type = request.getParameter("type");
    String id = request.getParameter("id");
    int index = 0;
    try {
        index = Integer.parseInt(request.getParameter("index"));
    } catch (Exception ex) {
    } 

    UddiHub x = UddiHub.getInstance(application, session);
    String data = null;
    if (type.equalsIgnoreCase("business")) {
        data = (x.GetCertificate(UddiHub.FindType.Business, id, index));
    } else if (type.equalsIgnoreCase("service")) {
        data = (x.GetCertificate(UddiHub.FindType.Service, id, index));
    } else if (type.equalsIgnoreCase("bindingTemplate")) {
        data = (x.GetCertificate(UddiHub.FindType.BindingTemplate, id, index));
    } else if (type.equalsIgnoreCase("tModel")) {
        data = (x.GetCertificate(UddiHub.FindType.tModel, id, index));
    }
    if (data != null && !data.startsWith("Error")) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=cert.crt");
        out.write("-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator"));
        out.write(data + System.getProperty("line.separator"));
        out.write("-----END CERTIFICATE-----" + System.getProperty("line.separator"));
    } else {
        response.setStatus(500);
        out.write(data);
    }

%>