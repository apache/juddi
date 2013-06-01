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
<pre>adminDelete_tmodel
delete_ClientSubscriptionInfo
delete_publisher
get_allPublisherDetail
get_publisherDetail
invoke_SyncSubscription
save_Clerk
save_ClientSubscriptionInfo
save_Node
save_publisher
View/Delete all pending Transfer Tokens</pre>
        </div>

    </div>
    <%@include file="header-bottom.jsp"%>