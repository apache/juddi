<%-- 
    Document   : settings
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
        <div class="container">

            <!-- Main hero unit for a primary marketing message or call to action -->
            <div class="well">
                <h1><%=ResourceLoader.GetResource(session, "navbar.settings")%></h1>
            </div>

            <!-- Example row of columns -->
            <div class="row">
                <div class="span12">
                    <h2><%=ResourceLoader.GetResource(session, "navbar.settings")%></h2>
                    <p><%=ResourceLoader.GetResource(session, "items.settings.description")%></p>
                    <p><a class="btn" href="businessBrowse.jsp"><%=ResourceLoader.GetResource(session, "viewdetails")%> &raquo;</a></p>
                </div>
            </div>
<%@include file="header-bottom.jsp" %>