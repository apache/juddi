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
            <%=ResourceLoader.GetResource(session, "items.dsigs.description")%><br>
            <%
                //figure out what we are signing
                //fetch the xml from ajex/toXml and fill the text area
                String id = request.getParameter("id");
                String itemtype = request.getParameter("type");

            %>

            You're about to digitally sign the <b><%=StringEscapeUtils.escapeHtml(itemtype)%></b> identified by the key <b><%=StringEscapeUtils.escapeHtml(id)%></b>.<br>
            By electronically signing this UDDI entry, other users will then be able to verify that this entry hasn't been modified.<br>

            <applet code="org.apache.juddi.gui.dsig.XmlSignatureApplet" archive="applets/juddi-gui-dsig.jar"></applet> 
            <script type="text/javascript">
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
                        //TODO timer to auto redirect to the
                        window.setTimeout(function(){
                <%
                    if (itemtype == "business") {
                        out.write("window.location=\"businessEditor2.jsp?id=" + StringEscapeUtils.escapeJavaScript(id) + "\";");
                    }
                    if (itemtype == "service") {
                        out.write("window.location=\"serviceEditor.jsp?id=" + StringEscapeUtils.escapeJavaScript(id) + "\";");
                    }
                    if (itemtype == "tmodel") {
                        out.write("window.location=\"tmodelEditor.jsp?id=" + StringEscapeUtils.escapeJavaScript(id) + "\";");
                    }
                 
                %>
                            }, 5000);
                        });

                        request.fail(function(jqXHR, textStatus) {
                            window.console && console.log('postback failed ');                                
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' +jqXHR.responseText + textStatus);
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
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' +jqXHR.responseText + textStatus);
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
                    function getObjectType()
                    {
                        return "<%=StringEscapeUtils.escapeJavaScript(itemtype)%>";
                    }
                    //
                    //display:none  <a class="btn" href="javascript:go();">Go</a>
            </script>

            <textarea name="data" rows="15" cols="80" id="data" style="">Loading....</textarea>
        </div>

    </div>
    <%@include file="header-bottom.jsp" %>