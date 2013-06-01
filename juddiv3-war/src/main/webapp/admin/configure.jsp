<%-- 
    Document   : configure jUDDI
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>


<%@page import="java.util.Iterator"%>
<%@page import="org.apache.juddi.config.AppConfig"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp"%>

<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1>Configure jUDDI</h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">


        <div class="span12">
            
            <table class="table table-hover">
                <tr><th>Field</th><th>Value</th></tr>
            <%
                Iterator it = AppConfig.getConfiguration().getKeys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    out.write("<tr id=\"" + StringEscapeUtils.escapeHtml(key) + "\"><td>"+ StringEscapeUtils.escapeHtml(key) + "</td><td>" +
                             StringEscapeUtils.escapeHtml(AppConfig.getConfiguration().getProperty(key).toString()) + "</td></tr>");
                }
            %>
            </table>
        </div>

    </div>
    <%@include file="header-bottom.jsp"%>