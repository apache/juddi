<%-- 
    Document   : tmodelPartitions
    Created on : Feb 28, 2013, 8:21:25 AM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%=ResourceLoader.GetResource(session, "pages.tmodelpart.title")%></h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            
            <div id="container">
                <%=ResourceLoader.GetResource(session, "pages.tmodelpart.content")%>
                <br><br>
                <div class="alert alert-info">
                <i class="icon-info-sign  icon-large"></i> <%=ResourceLoader.GetResource(session, "pages.tmodelpart.juddinote")%></div>
                <br><Br>
                <table class="table">
                    <tr><td><%=ResourceLoader.GetResource(session, "pages.tmodelpart.key")%></td><td><input type="text" value="uddi:www.mycompany.com:keyGenerator" id="keyGeneratorKey"  style="width:100%"></td></tr>
                    <tr><td><%=ResourceLoader.GetResource(session, "pages.thmodepart.name")%></td><td><input type="text" value="My business's key generator" id="keyGeneratorName" style="width:100%"></td></tr>
                    <tr><td><%=ResourceLoader.GetResource(session, "items.lang")%></td><td><input type="text" value="<%=ResourceLoader.GetResource(session, "language")%>" id="keyGeneratorLang" style="width:100%"></td></tr>
                </table>
                                
                <a class="btn btn-primary" title="Save" id="savekeygen" onclick="javascript:savekeygen();">
                   <i class="icon-save icon-large"></i> 
                    <%=ResourceLoader.GetResource(session, "actions.save")%></a>
            </div>
            <div id="results"></div>
            <script type="text/javascript">
                function savekeygen()
                {
                     $("#results").html("<img src=\"img");
                    $("#savekeygen").addClass("disabled");
                    var keygen=$("#keyGeneratorKey").val();
                    var keyname=$("#keyGeneratorName").val();
                    var keylang=$("#keyGeneratorLang").val();
                    $.get("ajax/tmodel.jsp?action=createKeyGen&key=" + keygen + "&name=" + keyname + "&lang=" + keylang,
                    function(data) {
                        $("#results").html(data);
                        $("#savekeygen").removeClass("disabled");
                    });
                }
            </script>

        </div>
    </div>
    <%@include file="header-bottom.jsp" %>
