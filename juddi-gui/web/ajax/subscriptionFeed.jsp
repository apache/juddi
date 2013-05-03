<%-- 
    Document   : subscriptionFeed
    Created on : Apr 14, 2013, 7:56:16 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.util.Calendar"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    UddiHub x = UddiHub.getInstance(application, session);
    DatatypeFactory df = DatatypeFactory.newInstance();
    GregorianCalendar gcal = new GregorianCalendar();
    gcal.setTimeInMillis(System.currentTimeMillis());
    //TODO get/set cookie data
    gcal.add(Calendar.DATE, -1);
    XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);
    out.write(x.GetNewsFeed(xcal));

%>