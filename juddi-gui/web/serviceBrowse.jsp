<%-- 
    Document   : Service Browser
    Created on : Feb 24, 2013, 9:14:01 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1>Service Browser</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            <div id="businesslist">
                Total records: <span id="totalrecords"></span><br>
                Records returned: <span id="displayrecords"></span><br>
                Offset: <span id="offset">0</span><br>
                Lang: <span id="lang" class="edit"></span><br>
                
                <a href="javascript:pagedown();"><i class="icon-circle-arrow-left " id="pageup"></i></a>
                <a href="javascript:refreshServiceList();"><i class="icon-refresh " id="refresh"></i></a>
                <a href="javascript:pageup();"><i class="icon-circle-arrow-right " id="pagedown"></i></a>

                <div id="serviceBrowserListings">
                    <img src="img/bigrollergreen.gif" title="Loading"/>
                </div>
                <script src="js/serviceBrowse.js"></script>
                <script type="text/javascript">
                
                    $('.edit').editable(function(value, settings) { 
                        console.log(this);
                        console.log(value);
                        console.log(settings);
                        RenderServiceListBySearch('%', offset, maxrecords);
                        return(value);
                    }, { 
                        type    : 'text',
                        submit  : 'OK'
                    });
                </script>
            </div>

        </div>
    </div>
    <%@include file="header-bottom.jsp" %>