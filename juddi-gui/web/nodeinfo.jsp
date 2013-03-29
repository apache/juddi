<%-- 
    Document   : UDDI node info
    Created on : March 28, 2013, 9:31:19 PM
    Author     : Alex O'Ree
--%>



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
        <h1>UDDI Node Information</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            <p>
                <%
                    UddiHub x = UddiHub.getInstance(application, session);
                    
                %>

                
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>