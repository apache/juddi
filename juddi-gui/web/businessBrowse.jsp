<%-- 
    Document   : browse
    Created on : Feb 24, 2013, 9:14:01 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%=ResourceLoader.GetResource(session, "navbar.businesses")%></h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            <%=ResourceLoader.GetResource(session, "totals.records")%>: <span id="totalrecordsBusiness"></span><br>
            <%=ResourceLoader.GetResource(session, "totals.recordsreturned")%>: <span id="displayrecordsBusiness"></span><br>
            <%=ResourceLoader.GetResource(session, "totals.offset")%> : <span id="offsetBusiness">0</span><br>
            <%=ResourceLoader.GetResource(session, "items.lang")%>: <span id="lang" class="edit"></span><br>
            <a href="javascript:pagedownBusiness();"><i class="icon-circle-arrow-left disabled icon-2x" id="pageupBusiness"></i></a>
            <a href="javascript:refreshBusinessList();"><i class="icon-refresh icon-2x" id="refresh"></i></a>
            <a href="javascript:pageupBusiness();"><i class="icon-circle-arrow-right disabled icon-2x" id="pagedownBusiness"></i></a>

            <div id="businesslist">
                <img src="img/bigrollergreen.gif" title="<%=ResourceLoader.GetResource(session, "items.loading")%>"/>
            </div>

            <script src="js/businesssearch.js"></script>
            <script type="text/javascript">
                
                $('.edit').editable(function(value, settings) { 
                    console.log(this);
                    console.log(value);
                    console.log(settings);
                    RenderBusinessListBySearch('%', offset, maxrecords);
                    return(value);
                }, { 
                    type    : 'text',
                    submit  : 'OK'
                });
            </script>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>