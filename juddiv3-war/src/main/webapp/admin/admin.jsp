<%-- 
    Document   : admin
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp"%>

<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1>Administration</h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">


        <div class="span12">
            Here will be a number of administrative actions available from the jUDDI API, such as:
            <select>
                <option>adminDelete_tmodel</option>
                <option>delete_ClientSubscriptionInfo</option>
                <option>delete_publisher</option>
                <option>get_allPublisherDetail</option>
                <option>get_publisherDetail</option>
                <option>invoke_SyncSubscription</option>
                <option>save_Clerk</option>
                <option>save_ClientSubscriptionInfo</option>
                <option>save_Node</option>
                <option>save_publisher</option>
                <option>View/Delete all pending Transfer Tokens</option>
            </select>
        </div>

    </div>
    <%@include file="header-bottom.jsp"%>