<%-- 
    Document   : transfer
    Created on : Apr 27, 2013, 8:52:12 AM
    Author     : Alex O'Ree
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.ws.Holder"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%=ResourceLoader.GetResource(session, "actions.transfer")%></h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            <%=ResourceLoader.GetResource(session, "pages.transfer.content")%>
            <Br>
            <br>


            <ul class="nav nav-tabs" id="myTab">
                <li class="active"><a  href="#general"><%=ResourceLoader.GetResource(session, "pages.transfer.createtoken")%></a></li>

                <li><a href="#discard" ><%=ResourceLoader.GetResource(session, "pages.transfer.discardtoken")%></a></li>

                <li><a href="#accept" ><%=ResourceLoader.GetResource(session, "pages.transfer.accepttransfer")%></a></li>

            </ul>
            <script>
                $(function () {
                    $('#myTab').tab;//('show');
                })
                $('#myTab a[href=#general]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });
                $('#myTab a[href=#discard]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });
                $('#myTab a[href=#accept]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });
                    
            </script>
            <div class="tab-content">
                <div class="tab-pane active" id="general">

                    <%=ResourceLoader.GetResource(session, "pages.transfer.createtoken.content")%><br>
                    <a href="javascript:refreshBusinessList()"><i class="icon-refresh icon-2x"></i> 
                        <big><%=ResourceLoader.GetResource(session, "actions.refresh")%></big></a><br>

                    <div id="data">

                        <img src="img/bigrollergreen.gif">
                    </div>

                    <div>
                        <a href="javascript:getToken();" class="btn btn-primary btn-large" style="width:95%">
                            <i class="icon-asterisk  icon-large"></i><%=ResourceLoader.GetResource(session, "pages.transfer.gettoken")%></a>
                        
                    </div>
                    <script type="text/javascript">
                        function refreshBusinessList()
                        { 
                            $('#data').html("<img src=\"img/ajax-loader.gif\">");
                            $.get('ajax/businessAsSelect.jsp', function(data) {
                                $('#data').html(data);
                                //  $('#tmodellist').resizable();
                                //  $('#businesslist').resizable();
                                $('#tmodellist').css("width", "49%");
                                $('#businesslist').css("width", "49%");
                            });
                            
                        }
                        $("#transferto").resizable();
                        refreshBusinessList();
                        
                        
                        
                        function getToken()
                        {
                            var url='ajax/getTransferToken.jsp';
                            var postbackdata = new Array();
                            var keys = new Array();
                            $(".transferable").each(function()
                            {
                                var id=$(this).attr("id");
                                if ($(this).is(':checked')) {
                                    keys.push(
                                    id);
                                    window.console && console.log('adding ' + id);                                
                                }
                            }); 
                            postbackdata.push({
                                name: "keylist", 
                                value: keys.join()
                            });
                                  
                            postbackdata.push({
                                name:"transferto", 
                                value: $("#transferto").val()
                            });
                            
                            postbackdata.push({
                                name:"nonce", 
                                value: $("#nonce").val()
                            });
    
                            var request=   $.ajax({
                                url: url,
                                type:"POST",
                                //  data" + i18n_type + ": "html", 
                                cache: false, 
                                //  processData: false,f
                                data: postbackdata
                            });
                
                
                            request.done(function(msg) {
                                window.console && console.log('postback done '  + url);                
        
                                //$("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' +
                               //     safe_tags_replace(msg));
                                //$("#resultBar").show();
                                $("#transfercontent").html(UndoBreaks(safe_tags_replace(msg)));
                                $("#tranfermodal").modal("show");
        
                            });

                            request.fail(function(jqXHR, textStatus) {
                                window.console && console.log('postback failed ' + url);                                
                                $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' +jqXHR.responseText + " " + textStatus );
                                //$(".alert").alert();
                                $("#resultBar").show();
        
                            });
                        }
                        
                        function UndoBreaks(content)
                        {
                           // var repl ="/"+ safe_tags_replace("<br>") + "/g";
                            
                            return content.replace(/BREAK/g, "<br>");;
                        }
                        
                        function discardToken()
                        {
                            var url='ajax/abortTransferToken.jsp';
                            var postbackdata = new Array();

                           
                            postbackdata.push({
                                name:"tokenxml", 
                                value: $("#tokenxml").val()
                            });
                            
                            postbackdata.push({
                                name:"nonce", 
                                value: $("#nonce").val()
                            });
    
                            var request=   $.ajax({
                                url: url,
                                type:"POST",
                                //  data" + i18n_type + ": "html", 
                                cache: false, 
                                //  processData: false,f
                                data: postbackdata
                            });
                
                
                            request.done(function(msg) {
                                window.console && console.log('postback done '  + url);                
        
                                $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' + msg);
                                $("#resultBar").show();
        
                            });

                            request.fail(function(jqXHR, textStatus) {
                                window.console && console.log('postback failed ' + url);                                
                                $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' +jqXHR.responseText + textStatus );
                                //$(".alert").alert();
                                $("#resultBar").show();
        
                            });
                        }
                        
                        
                      
                        
                        
                        function acceptToken()
                        {
                            var url='ajax/acceptTransferToken.jsp';
                            var postbackdata = new Array();

                           
                            postbackdata.push({
                                name:"tokenxml", 
                                value: $("#accepttoken").val()
                            });
                            
                            
                            postbackdata.push({
                                name:"keybag", 
                                value: $("#keybag").val()
                            });
                            
                            postbackdata.push({
                                name:"nonce", 
                                value: $("#nonce").val()
                            });
    
                            var request=   $.ajax({
                                url: url,
                                type:"POST",
                                //  data" + i18n_type + ": "html", 
                                cache: false, 
                                //  processData: false,f
                                data: postbackdata
                            });
                
                
                            request.done(function(msg) {
                                window.console && console.log('postback done '  + url);                
                                
                                $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' + msg);
                                $("#resultBar").show();
        
                            });

                            request.fail(function(jqXHR, textStatus) {
                                window.console && console.log('postback failed ' + url);                                
                                $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' +jqXHR.responseText + " " + textStatus );
                                //$(".alert").alert();
                                $("#resultBar").show();
        
                            });
                        }
                    </script>
                </div>
                <div class="tab-pane" id="accept">
                    <%=ResourceLoader.GetResource(session, "pages.transfer.accepttransfer")%><Br>
                    <%=ResourceLoader.GetResource(session, "pages.transfer.accepttransfer.content")%>
                    <br>

                    <%=ResourceLoader.GetResource(session, "items.token")%>: <textarea  id="accepttoken" 
                                 placeholder="<%=ResourceLoader.GetResource(session, "items.token.xml")%>"></textarea> <br>
                                         
                    <%=ResourceLoader.GetResource(session, "items.key")%>: <textarea  id="keybag" 
                                 placeholder="<%=ResourceLoader.GetResource(session, "items.kegbag.xml")%>"></textarea> <br>
                    <a href="javascript:acceptToken();" class="btn btn-info btn-large" style="width:95%"><i class="icon-large icon-plus"></i> 
                      <%=ResourceLoader.GetResource(session, "actions.accept")%>  </a>
                </div>

                <div class="tab-pane" id="discard">
                    <%=ResourceLoader.GetResource(session, "pages.transfer.discardtoken")%><Br>
                    <%=ResourceLoader.GetResource(session, "pages.transfer.discardtoken.content")%><br>
                    <br>

                     <%=ResourceLoader.GetResource(session, "items.token")%>: <textarea id="tokenxml" placeholder="Token XML"></textarea> <br>
                    <a href="javascript:discardToken();" class="btn btn-danger btn-large" style="width:95%"><i class="icon-large icon-trash"></i> <%=ResourceLoader.GetResource(session, "pages.transfer.discardtoken")%></a>
                </div>
            </div>
        </div>
    </div>


    <div class="modal hide fade container" id="tranfermodal">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3><%=ResourceLoader.GetResource(session, "pages.transfer.createtoken")%></h3>
        </div>
        <div class="modal-body" id="transfercontent">
            
        </div>
        <div class="modal-footer">

            <a href="javascript:closeXmlPop('tranfermodal');" class="btn"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
        </div>
    </div>
         
    <%@include file="header-bottom.jsp" %>