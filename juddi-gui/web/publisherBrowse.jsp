<%-- 
    Document   : publisherBrowse
    Created on : Feb 28, 2013, 6:44:04 AM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1>Publishers</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            <div id="publisherlist">
                <%
                    UddiHub x = UddiHub.getInstance(application, request.getSession());

                    out.write(x.GetPublisherListAsHtml());
                %>
            </div>
            <script type="text/javascript">
                
                $('.edit').editable(function(value, settings) { 
                    console.log(this);
                    console.log(value);
                    console.log(settings);
                    return(value);
                }, { 
                    type    : 'text',
                    submit  : 'OK'
                });
            </script>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>
