<%-- 
    Document   : view subscriptions
    Created on : March 28, 2013, 9:31:19 PM
    Author     : Alex O'Ree
--%>



<%@page import="java.util.List"%>
<%@page import="org.uddi.sub_v3.Subscription"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.uddi.api_v3.*"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>
<%@page import="org.apache.juddi.webconsole.hub.*"%>
<%@page import="org.apache.juddi.query.FindBusinessByNameQuery"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1>Subscriptions</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            <%
                UddiHub x = UddiHub.getInstance(application, session);

                List<Subscription> list = x.GetSubscriptions();
                if (list == null) {
                    out.write("Either an error occured or you're not signed in.");
                }

            %>
            <table class="table">
                <%
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            out.write("<tr><td>");
                            out.write(StringEscapeUtils.escapeHtml(list.get(i).getSubscriptionKey()));
                            out.write("</td><td>");
                            out.write(StringEscapeUtils.escapeHtml(list.get(i).getExpiresAfter().toString()));
                            out.write("</td></tr>");
                        }
                        if (list.isEmpty())
                            out.write("No subscriptions are currently defined.");
                    }
                %>
            </table>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>