<%-- 
    Document   : getTransferToken
    Created on : Apr 27, 2013, 9:13:27 AM
    Author     : Alex O'Ree
--%>

<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.ws.Holder"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String businesslist = request.getParameter("keylist");

        //String tmodellist = request.getParameter("tmodellist");
        String username = request.getParameter("transferto");


        UddiHub x = UddiHub.getInstance(application, session);
        Holder<byte[]> outToken = new Holder<byte[]>();
        Holder<XMLGregorianCalendar> outXcal = new Holder<XMLGregorianCalendar>();
        DatatypeFactory df = DatatypeFactory.newInstance();
        outXcal.value = df.newXMLGregorianCalendar();
        Holder<String> transferTo = new Holder<String>();
        transferTo.value = username;
        List<String> keys = new ArrayList<String>();
        if (businesslist != null) {
            String[] businesskeys = businesslist.split(",");

            for (int i = 0; i < businesskeys.length; i++) {
                keys.add(businesskeys[i]);
            }
        }
        /*if (tmodellist != null) {
            String[] tmodelkeys = tmodellist.split(",");
            for (int i = 0; i < tmodelkeys.length; i++) {
                keys.add(tmodelkeys[i]);
            }
        }*/
        x.GetCustodyTransferToken(keys, transferTo, outXcal, outToken);
        try{
        if (outXcal!=null && outXcal.value!=null)
        out.write(outXcal.value.toString() + "<br>");
               }
        catch (Exception ex){}
        out.write(new String(outToken.value) + "<br>");
    }


%>