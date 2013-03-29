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
        <h1>tModel Partitions</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            
            <div id="container">
                TModel Key Generators are a special kind of tModel that enables you to define new tModels with any arbitrary tModel prefix that you want.
                For example, if you wanted a tModel defined as "uddi:www.mycompany.com:ServiceAuthenticationMethod", you would first have to create a tModel
                key generator with the value of "uddi:www.mycompany.com:keyGenerator". This is part of the UDDI specification and acts as a governance mechanism.
                <br>
                <i class="icon-info-sign"></i>For jUDDI implementations of UDDI, the "root" account cannot be used to create a keyGenerator.
                <br><Br>
                <table class="table">
                    <tr><td>The UDDI tModel key</td><td><input type="text" value="uddi:www.mycompany.com:keyGenerator" id="keyGeneratorKey"  style="width:100%"></td></tr>
                    <tr><td>A name describing the key</td><td><input type="text" value="My business's key generator" id="keyGeneratorName" style="width:100%"></td></tr>
                    <tr><td>Language</td><td><input type="text" value="en" id="keyGeneratorLang" style="width:100%"></td></tr>
                </table>
                                
                <a class="btn btn-primary" title="Save" id="savekeygen" onclick="javascript:savekeygen();">Save</a>
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
