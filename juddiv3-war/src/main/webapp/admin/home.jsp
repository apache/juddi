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
        <h1>jUDDI Administration</h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">


        <div class="span4">
            <h2>Configure jUDDI</h2>
            <p><% //=ResourceLoader.GetResource(session, "index.config")%>Configure jUDDI</p>
            <p><a class="btn" href="configure.jsp"><%=ResourceLoader.GetResource(session, "viewdetails")%> &raquo;</a></p>
        </div>
        <div class="span4">
            <h2>Status and Statistics</h2>
            <p><% //=ResourceLoader.GetResource(session, "index.search")%>Find out how busy jUDDI is.</p>
            <p><a class="btn" href="stats.jsp"><%=ResourceLoader.GetResource(session, "viewdetails")%> &raquo;</a></p>
        </div>
        <div class="span4">
            <h2>Administration</h2>
            <p><%//=ResourceLoader.GetResource(session, "index.learn")%>Administration options, such as deleting entities.</p>
            <p><a class="btn" href="admin.jsp"><%=ResourceLoader.GetResource(session, "viewdetails")%> &raquo;</a></p>
        </div>

    </div>
<%@include file="header-bottom.jsp"%>