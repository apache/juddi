<%-- 
    Document   : tmodel browser
    Created on : Feb 24, 2013, 9:14:01 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h2><%=ResourceLoader.GetResource(session, "items.tmodel.browser")%></h2>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            <%=ResourceLoader.GetResource(session, "totals.records")%>: <span id="totalrecords"></span><br>
            <%=ResourceLoader.GetResource(session, "totals.recordsreturned")%>: <span id="displayrecords"></span><br>
            <%=ResourceLoader.GetResource(session, "totals.offset")%> : <span id="offset">0</span><br>
            <%=ResourceLoader.GetResource(session, "items.lang")%>: <span id="lang" class="edit"></span><br>

            <a href="javascript:pagedown();"><i class="icon-circle-arrow-left icon-2x" id="pageup"></i></a>
            <a href="javascript:reload();"><i class="icon-refresh icon-2x"></i></a>
            <a href="javascript:pageup();"><i class="icon-circle-arrow-right icon-2x" id="pagedown"></i></a>

            <div id="tmodellist">
                <img src="img/bigrollergreen.gif" title="Loading"/>
            </div>
            <script src="js/tmodelsearch.js"></script>
            <script type="text/javascript">
                
                function reload()
                {
                    RenderTmodelListBySearch('%', offset, maxrecords, false);
                }
                $('.edit').editable(function(value, settings) { 
                    console.log(this);
                    console.log(value);
                    console.log(settings);
                    reload();
                    //  RenderTmodelListBySearch('%', offset, maxrecords);
                    return(value);
                }, { 
                    type    : 'text',
                    submit  : i18n_ok
                });
                reload();
            </script>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>