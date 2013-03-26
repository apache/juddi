<%-- 
    Document   : header-top
    Created on : Feb 24, 2013, 9:08:12 AM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Apache jUDDI - Open Source UDDI Discovery Services</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <!-- Le styles -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <style type="text/css">
            body {
                padding-top: 60px;
                padding-bottom: 40px;
            }
        </style>
        <link href="css/bootstrap-responsive.css" rel="stylesheet">

        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="js/html5shiv.js"></script>
        <![endif]-->

        <!-- Fav and touch icons -->

        <link rel="shortcut icon" href="ico/favicon.png">
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/jquery.jeditable.js"></script>
        <script src="js/main.js"></script>
    </head>

    <body>
        <form id="uddiform">
            <%@include  file="csrf.jsp" %>
            <input type="hidden" name="nonce" id="nonce" value="<%=StringEscapeUtils.escapeHtml((String) session.getAttribute("nonce"))%>" />
            <div class="alert" style="display:none; position: fixed; top:45px; width:80%; left: 10%; z-index: 1000; opacity: 1.0; background-color: #FFD530; color:black " id="resultBar">
            </div>
            <%
                UddiHub hub = UddiHub.getInstance(application, session);
            %>

            <div class="navbar navbar-inverse navbar-fixed-top">
                <div class="navbar-inner">
                    <div class="container">
                        <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <a class="brand" href="index.jsp" style="padding-left:19px; padding-top:0px; padding-bottom:0px"><img src="img/logo2.png"></a>
                        <div class="nav-collapse collapse">
                            <ul class="nav">
                                <li class="active"><a href="index.jsp">Home</a></li>
                                <li class="dropdown" ><a href="#" data-toggle="dropdown" class="dropdowb-town">Discover<b class="caret"></b></a>
                                    <ul class="dropdown-menu">
                                        <li><a href="businessBrowse.jsp" title="Browse for businesses">Businesses</a></li>
                                        <li><a href="serviceBrowse.jsp" title="Browse for Services">Services</a></li>
                                        <li><a href="tmodelBrowse.jsp" title="Browse for tModels">tModels</a></li>
                                        <li><a href="publisherBrowse.jsp" title="Browse for Publishers">Publishers</a></li>
                                        <li class="divider"></li>
                                        <li><a href="search.jsp" title="Browse for Services">Search</a></li>
                                    </ul>
                                </li>
                                <li class="dropdown"><a href="#" data-toggle="dropdown" class="dropdowb-town">Create<b class="caret"></b></a>
                                    <ul class="dropdown-menu">
                                        <li><a href="businessEditor2.jsp" title="Create a businesses">Business</a></li>
                                        <li><a href="businessBrowse.jsp" title="Create a Service">Service</a></li>
                                        <li><a href="tmodelEditor.jsp" title="Create a tModel">tModel</a></li>
                                        <li><a href="tmodelPartitions.jsp" title="Create a tModel">tModel Partition (Key Generator)</a></li>
                                        <%
                                            if (hub.IsJuddiRegistry()) {
                                        %>
                                        <li><a href="tmodelBrowse.jsp" title="Create a publisher">Publisher</a></li>
                                        <%                                            }
                                        %>
                                    </ul>
                                </li>
                                <li class="dropdown"><a href="#" data-toggle="dropdown" class="dropdowb-town">Subscriptions<b class="caret"></b></a>
                                    <ul class="dropdown-menu">
                                        <li><a href="businessBrowse.jsp" title="Create a businesses">View</a></li>
                                        <li><a href="serviceBrowse.jsp" title="Create a Services">Create</a></li>
                                        <li><a href="tmodelBrowse.jsp" title="Create a tModel">Something</a></li>
                                        <li><a href="tmodelBrowse.jsp" title="Create a publisher">Something else</a></li>
                                    </ul>
                                </li>
                                <li><a href="settings.jsp">Settings</a></li>
                                <li class="dropdown"><a href="#" data-toggle="dropdown" class="dropdowb-town">Help<b class="caret"></b></a>
                                    <ul class="dropdown-menu">
                                        <li><a href="http://juddi.apache.org/docs/3.x/userguide/html/index.html" title="User Guide">User Guide</a></li>
                                        <li><a href="http://juddi.apache.org/docs/3.x/devguide/html/index.html" title="Developer Guide">Dev Guide</a></li>
                                        <li><a href="http://juddi.apache.org/docs.html" title="API Level Developer Documentation">API Documentation</a></li>
                                        <li><a href="http://wiki.apache.org/juddi" title="jUDDI's Official Wiki">jUDDI Wiki</a></li>
                                        <li><a href="http://juddi.apache.org/issue-tracking.html" title="jUDDI's Issue Tracker">File a bug</a></li>
                                        <li><a href="http://juddi.apache.org/" title="The Offical jUDDI Website">jUDDI Website</a></li>
                                        <li><a href="http://www.nabble.com/jUDDI-f218.html" title="User/Dev/SVN Mailing Lists">Mailing List Archive</a></li>
                                        
                                    </ul>
                                </li>
                            </ul>
                            <div id="loginfield">
                                <%@include file="login.jsp" %>
                            </div>
                        </div><!--/.nav-collapse -->
                    </div>
                </div>
            </div>
