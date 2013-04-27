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
            UDDI supports the transfer the ownership of Businesses (and all their child objects including services, binding templates) and tModel entities to
            another publisher. Publishers represent one or more logins or usernames.<br><Br>
            Transfers can occur within a UDDI registry node (intra-node) and between multiple registries nodes (inter-node). This utility will help you
            setup intra-node transfers only. No known UDDI implementations support inter-node transfers (replication API's are not implemented).
            <br><br>
            The process is straightforward, select what entities you want to transfer, then click OK. A token will be presented to you which then
            need to give to the other publisher. The other publisher then needs to accept the token and transfer the ownership to him. Finally, the token
            is then destroyed to prevent any inadvertent transfers.<br>
            Transfers can be aborted (token invalidated) before the other publisher transfers the ownership over. Once it's been transfered and you want to undo the transfer, contact
            the registry administrator and the other publisher.<Br>
            <br>


            <ul class="nav nav-tabs" id="myTab">
                <li class="active"><a  href="#general">Create a new Token</a></li>

                <li><a href="#discard" >Discard a Token</a></li>

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
                    
            </script>
            <div class="tab-content">
                <div class="tab-pane active" id="general">

                    <a href="javascript:refreshBusinessList()"><i class="icon-refresh icon-2x"></i> <big>Refresh</big></a><br>

                    <div id="data">

                        <img src="img/ajax-loader.gif">
                    </div>


                    <div>
                        Transfer to: <input type="text" id="transferto" placeholder="Username"> 
                    </div>
                    <div>
                        <a href="javascript:getToken();" class="btn btn-primary btn-large" style="width:90%">Get Token</a>
                    </div>
                    <script type="text/javascript">
                        function refreshBusinessList()
                        { 
                            $('#data').html("<img src=\"img/ajax-loader.gif\">");
                            $.get('ajax/businessAsSelect.jsp', function(data) {
                                $('#data').html(data);
                                //  $('#tmodellist').resizable();
                                //  $('#businesslist').resizable();
                                $('#tmodellist').css("width", "40%");
                                $('#businesslist').css("width", "40%");
                            });
                            //TODO query parameter to auto highlight an item
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
                            /*
                            var id=$("#businesslist.transferable").find('option:selected').attr('id');
                            //$("#businesslist").val();
                            if (id!=null)
                                postbackdata.push({
                                    name: "businesslist", 
                                    value: id.join()
                                });
                            id=id=$("#tmodellist").find('option:selected').attr('id');//$("#tmodellist").val();
                            if (id!=null)
                                postbackdata.push({
                                    name: "tmodellist", 
                                    value: id.join()
                                });
                             */              
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
                    </script>
                </div>
                <div class="tab-pane" id="discard">
                    Discard a transfer token (abort the transfer)<Br><br>

                    Token: <input type="text" id="discardtoken" placeholder="Token data"> <br>
                    <a href="javascript:submit();" class="btn btn-danger btn-large" style="width:90%"><i class="icon-large icon-remove-sign"></i> Discard Token</a>
                </div>
            </div>
        </div>
    </div>


    <div class="modal hide fade" id="tranfermodal">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3>Transfer</h3>
        </div>
        <div class="modal-body">
            You can setup a subscription for this business to automatically alert you when there is a change
        </div>
        <div class="modal-footer">

            <a href="javascript:closeXmlPop('viewAsXml');" class="btn"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
        </div>
    </div>

    <%@include file="header-bottom.jsp" %>