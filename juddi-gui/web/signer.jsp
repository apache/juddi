<%-- 
    Document   : signer
    Created on : Mar 24, 2013, 8:23:30 AM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1>
            <%=ResourceLoader.GetResource(session, "items.dsigs")%>
            </h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            <%=ResourceLoader.GetResource(session, "items.dsigs.description")%>
            
            <applet code="org.apache.juddi.gui.dsig.XmlSignatureApplet" archive="applets/juddi-gui-dsig.jar"></applet> 
            <script type="text/javascript">
                <%
                    //figure out what we are signing
                    //fetch the xml from ajex/toXml and fill the text area
                    String id = request.getParameter("id");
                    String itemtype = request.getParameter("type");

                %>
                    $.get("ajax/toXML.jsp?id=<%=id%>&type=<%=itemtype%>", function(data){
                        $("#data").val(data);
                    });
                    /**
                     * Called by the applet to obtaining the xml to be signed
                     */
                    function getXml()
                    {
                        return $("#data").val();
                    }
                    
                    function go()
                    {
                        
                        var form = $("#uddiform");
                        var d = form.serializeArray();
                        var request=   $.ajax({
                            url: 'ajax/saveFromXML.jsp?id=<%=id%>&type=<%=itemtype%>',
                            type:"POST",
                            cache: false, 

                            data: d
                        });
                  
                        request.done(function(msg) {
                            window.console && console.log('postback done ');                
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' + msg);
                            $("#resultBar").show();
                        });

                        request.fail(function(jqXHR, textStatus) {
                            window.console && console.log('postback failed ');                                
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' +jqXHR + textStatus);
                            $("#resultBar").show();
                        });
                    }
				 
                    /**
                     * called by the applet to refresh the page with the signed data
                     */	 
                    function writeXml(data)
                    {
                        $("#data").val(data);
                        //post back to the publishing thread
                        
                        var form = $("#uddiform");
                        var d = form.serializeArray();
                        var request=   $.ajax({
                            url: 'ajax/saveFromXML.jsp?id=<%=id%>&type=<%=itemtype%>',
                            type:"POST",
                            cache: false, 

                            data: d
                        });
                  
                        request.done(function(msg) {
                            window.console && console.log('postback done ');                
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' + msg);
                            $("#resultBar").show();
                        });

                        request.fail(function(jqXHR, textStatus) {
                            window.console && console.log('postback failed ');                                
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' +jqXHR + textStatus);
                            $("#resultBar").show();
                        });
                    }
				
                    function getBrowserName()
                    {
                        return navigator.appName;
                    }
                    function getOsName()
                    {
                        var OSName="unknown OS";
                        if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows";
                        if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS";
                        if (navigator.appVersion.indexOf("X11")!=-1) OSName="UNIX";
                        if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux";
                        return OSName;
                    }
            </script>
            <a class="btn" href="javascript:go();">Go</a>
            <textarea name="data" rows="15" cols="80" id="data">Loading....</textarea>
        </div>

    </div>
    <%@include file="header-bottom.jsp" %>