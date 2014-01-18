<%-- 
    Document   : index
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp"%>

<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="hero-unit">
        <h1><%=ResourceLoader.GetResource(session, "pages.home.title")%></h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">


        <div class="span4">
            <h2><%=ResourceLoader.GetResource(session, "pages.home.config")%></h2>
            <p><%=ResourceLoader.GetResource(session, "pages.home.config")%></p>
            <p><a class="btn btn-primary" href="configure.jsp"><%=ResourceLoader.GetResource(session, "viewdetails")%> &raquo;</a></p>
        </div>
        <div class="span4">
            <h2><%=ResourceLoader.GetResource(session, "pages.home.stats")%></h2>
            <p><%=ResourceLoader.GetResource(session, "pages.home.stats.content")%></p>
            <p><a class="btn btn-primary" href="stats.jsp"><%=ResourceLoader.GetResource(session, "viewdetails")%> &raquo;</a></p>
        </div>
        <div class="span4">
            <h2><%=ResourceLoader.GetResource(session, "pages.home.admin")%></h2>
            <p><%=ResourceLoader.GetResource(session, "pages.home.admin.content")%>Administration options, such as deleting entities.</p>
            <p><a class="btn btn-primary" href="admin.jsp"><%=ResourceLoader.GetResource(session, "viewdetails")%> &raquo;</a></p>
        </div>

    </div>
<%@include file="header-bottom.jsp"%>