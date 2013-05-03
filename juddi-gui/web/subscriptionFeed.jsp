<%-- 
    Document   : subscriptionFeed
    Created on : Apr 14, 2013, 7:45:24 PM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp"%>

<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1><%=ResourceLoader.GetResource(session, "navbar.subscriptions.feed")%></h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            <p><%=ResourceLoader.GetResource(session, "navbar.subscriptions.feed.content")%>.</p>
            <div id="feedcontent"></div>
            <script type="text/javascript">
                $("#feedcontent").html("<img src=\"img/bigrollergreen.gif\" title=\"Loading\"/>");
                var request=   $.ajax({
                    url: 'ajax/subscriptionFeed.jsp',
                    type:"GET",
                    cache: false
                });
                  
                request.done(function(msg) {
                    window.console && console.log('postback done ');                
                    $("#feedcontent").html(msg);
                    //refresh();
                });

                request.fail(function(jqXHR, textStatus) {
                    window.console && console.log('postback failed ');                                
                    $("#feedcontent").html("An error occured! " + textStatus + jqXHR);
                    //refresh();
                });
            </script>
        </div>

    </div>
    <%@include file="header-bottom.jsp"%>