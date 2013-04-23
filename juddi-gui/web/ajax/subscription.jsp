<%-- 
    Document   : subscription
    Created on : Apr 21, 2013, 12:00:30 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.util.concurrent.atomic.AtomicReference"%>
<%@page import="org.apache.juddi.webconsole.hub.builders.Builders"%>
<%@page import="org.uddi.sub_v3.Subscription"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        UddiHub x = UddiHub.getInstance(application, session);
        AtomicReference<String> outmsg = new AtomicReference<String>();
        Subscription sub = Builders.BuildClientSubscription(request.getParameterMap(), outmsg);
        if (sub == null) {
            out.write(outmsg.get());
        } else {
            out.write(x.AddSubscription(sub));
        }
    } else {
        response.setStatus(500);
    }


%>