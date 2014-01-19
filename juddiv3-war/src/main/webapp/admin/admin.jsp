<%-- 
    Document   : admin
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.api_v3.Publisher"%>
<%@page import="org.apache.juddi.api_v3.SavePublisher"%>
<%@page import="org.apache.juddi.api_v3.Clerk"%>
<%@page import="org.apache.juddi.api_v3.SaveClerk"%>
<%@page import="org.apache.juddi.api_v3.SyncSubscription"%>
<%@page import="org.apache.juddi.api_v3.GetPublisherDetail"%>
<%@page import="org.apache.juddi.api_v3.GetAllPublisherDetail"%>
<%@page import="org.apache.juddi.api_v3.DeletePublisher"%>
<%@page import="org.uddi.api_v3.DeleteTModel"%>
<%@page import="org.apache.juddi.v3_service.JUDDIApiPortType"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp"%>

<div class="container">

        <!-- Main hero unit for a primary marketing message or call to action -->
        <div class="well">
                <h1><%=ResourceLoader.GetResource(session, "pages.home.admin")%></h1>

        </div>

        <!-- Example row of columns -->
        <div class="row">


                <div class="span12">
                        <% UddiAdminHub x = UddiAdminHub.getInstance(application, session);
                        %> 
                        <%=ResourceLoader.GetResource(session, "pages.admin.content")%>
                        <br>
                        <select onchange="toggledivs();" id="divselector">
                                <option>adminDelete_tmodel</option>
                                <option>delete_ClientSubscriptionInfo</option>
                                <option>delete_publisher</option>
                                <option>getAllPublisherDetail</option>
                                <option>get_publisherDetail</option>
                                <option>invoke_SyncSubscription</option>
                                <option>save_Clerk</option>
                                <option>save_ClientSubscriptionInfo</option>
                                <option>save_Node</option>
                                <option>save_publisher</option>
                        </select>

                        <div>
                                <div id="adminDelete_tmodel" style="display:none">
                                        <input type="text" id="adminDelete_tmodelKEY" class="forminput" placeholder="Enter tModel Key">
                                </div>
                                <div id="delete_ClientSubscriptionInfo"  style="display:none">
                                        <input type="text" id="delete_ClientSubscriptionInfoKEY" class="forminput" placeholder="Enter subscription Key">
                                </div>
                                <div id="invoke_SyncSubscription" style="display:none">
                                        <%=ResourceLoader.GetResource(session, "items.noauthtoken")%>  <br>
                                        <textarea rows="4" cols="80" id="invokeSyncSubscriptionXML" class="forminput" placeholder="Enter subscription XML"></textarea>
                                </div>
                                <div id="delete_publisher" style="display:none">
                                        <input type="text"  class="forminput" id="delete_publisherKEY" placeholder="Enter publisher id">
                                </div>
                                <div id="getAllPublisherDetail" style="display:none">
                                        <%=ResourceLoader.GetResource(session, "items.noinput")%>
                                </div>
                                <div id="get_publisherDetail" style="display:none">
                                        <input type="text" id="get_publisherDetailKEY"  class="forminput" placeholder="Enter publisher id">
                                </div>
                                <div id="save_ClientSubscriptionInfo" style="display:none">
                                        <%=ResourceLoader.GetResource(session, "items.noauthtoken")%><br>
                                        <textarea rows="4" cols="80" id="ClientSubscriptionInfoDetailXML" class="forminput" placeholder="Enter subscription XML"></textarea>
                                </div>
                                <div id="save_publisher" style="display:none">
                                        <%=ResourceLoader.GetResource(session, "items.name")%> <input type="text" id="savePublisherNAME"  class="forminput" placeholder="Enter name"><br>
                                        <%=ResourceLoader.GetResource(session, "items.email")%>  <input type="text" id="savePublisherEMAIL"  class="forminput" placeholder="Enter email"><br>
                                        <%=ResourceLoader.GetResource(session, "items.authorizedname")%>  <input type="text" id="savePublisherAuthorizedName"  class="forminput" placeholder="Enter Authorized Name (username)"><br>
                                        <%=ResourceLoader.GetResource(session, "items.publisher.admin")%>  <input type="checkbox" id="savePublisherIsAdmin"  class="forminput" ><br>
                                        <%=ResourceLoader.GetResource(session, "items.enable")%>  <input type="checkbox" id="savePublisherIsEnabled"  class="forminput"><br>
                                        <%=ResourceLoader.GetResource(session, "max.bindings.per.service")%> <input type="text" id="savePublisherMaxBindings" placeholder="100"  class="forminput"><br>
                                        <%=ResourceLoader.GetResource(session, "max.services.per.business")%> <input type="text" id="savePublisherMaxServices" placeholder="1000" class="forminput"><br>
                                        <%=ResourceLoader.GetResource(session, "max.business")%> <input type="text" id="savePublisherMaxBusiness" placeholder="1000"  class="forminput"><br>
                                        <%=ResourceLoader.GetResource(session, "max.tmodel")%> <input type="text" id="savePublisherMaxTModels" placeholder="1000" class="forminput"><br>
                                        <br>
                                        <%=ResourceLoader.GetResource(session, "pages.admin.max")%>
                                </div>
                                <div id="save_Node" style="display:none">

                                        <%=ResourceLoader.GetResource(session, "items.clientname")%> <input type="text" id="NODEsetClientName"  class="forminput" placeholder="Client Name"><br>
                                        <%=ResourceLoader.GetResource(session, "items.name")%> <input type="text" id="NODEsetName"  class="forminput" placeholder="Enter name"><br>

                                        <%=ResourceLoader.GetResource(session, "items.description")%> <input type="text" id="NODEsetDescription"  class="forminput" placeholder="Enter description"><br>
                                        Factory Initial <input type="text" id="NODEsetFactoryInitial"  class="forminput" placeholder="only needed for RMI transport"><br>
                                        Factory URL Packages <input type="text" id="NODEsetFactoryURLPkgs"  class="forminput" placeholder="only needed for RMI transport"><br>
                                        Factory Naming Provider <input type="text" id="NODEsetFactoryNamingProvider"  class="forminput" placeholder="only needed for RMI transport"><br>
                                        <%=ResourceLoader.GetResource(session, "items.transport")%> <input type="text" id="NODEsetProxyTransport"  class="forminput" placeholder="org.apache.juddi.v3.client.transport.JAXWSTransport" value="org.apache.juddi.v3.client.transport.JAXWSTransport"><br>
                                        <%=ResourceLoader.GetResource(session, "items.inquiry")%> <input type="text" id="NODEsetInquiryUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/inquiry" value="http://localhost:8080/juddiv3/services/inquiry"><br>
                                        <%=ResourceLoader.GetResource(session, "items.publish")%> <input type="text" id="NODEsetPublishUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/publish" value="http://localhost:8080/juddiv3/services/publish"><br>
                                        <%=ResourceLoader.GetResource(session, "items.security")%> <input type="text" id="NODEsetSecurityUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/security" value="http://localhost:8080/juddiv3/services/security"><br>
                                        <%=ResourceLoader.GetResource(session, "items.subscription.list")%> <input type="text" id="NODEsetSubscriptionListenerUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/subscription-listener" value="http://localhost:8080/juddiv3/services/subscription-listener"><br>
                                        <%=ResourceLoader.GetResource(session, "items.subscription")%> <input type="text" id="NODEsetSubscriptionUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/subscription" value="http://localhost:8080/juddiv3/services/subscription"><br>
                                        <%=ResourceLoader.GetResource(session, "items.custodytransfer")%> <input type="text" id="NODEsetCustodyTransferUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/custody-transfer" value="http://localhost:8080/juddiv3/services/custody-transfer"><br>
                                        <%=ResourceLoader.GetResource(session, "items.replication")%> <input type="text" id="NODEsetReplicationUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/replication" value="http://localhost:8080/juddiv3/services/replication"><br>
                                        jUDDI API <input type="text" id="NODEsetJuddiApiUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/juddi-api" value="http://localhost:8080/juddiv3/services/juddi-api"><br>
                                </div>
                                <div id="save_Clerk" style="display:none">
                                        <%=ResourceLoader.GetResource(session, "items.name")%> <input type="text" id="CLERKsetName"  class="forminput" placeholder="Enter name"><br>
                                        <%=ResourceLoader.GetResource(session, "items.authorizedname")%> <input type="text" id="CLERKsetPublisher"  class="forminput" placeholder="Enter Authorized Name (username)"><br>
                                        <%=ResourceLoader.GetResource(session, "navbar.login.password")%> <input type="password" id="CLERKsetPassword"  class="forminput" placeholder="Enter password"><br>
                                        <hr>
                                        <%=ResourceLoader.GetResource(session, "items.clientname")%> <input type="text" id="CLERKNODEsetClientName"  class="forminput" placeholder="Client Name"><br>
                                        <%=ResourceLoader.GetResource(session, "items.name")%> <input type="text" id="CLERKNODEsetName"  class="forminput" placeholder="Enter name"><br>

                                        <%=ResourceLoader.GetResource(session, "items.description")%> <input type="text" id="CLERKNODEsetDescription"  class="forminput" placeholder="Enter description"><br>
                                        Factory Initial <input type="text" id="CLERKNODEsetFactoryInitial"  class="forminput" placeholder="only needed for RMI transport"><br>
                                        Factory URL Packages <input type="text" id="CLERKNODEsetFactoryURLPkgs"  class="forminput" placeholder="only needed for RMI transport"><br>
                                        Factory Naming Provider <input type="text" id="CLERKNODEsetFactoryNamingProvider"  class="forminput" placeholder="only needed for RMI transport"><br>
                                        <%=ResourceLoader.GetResource(session, "items.transport")%> <input type="text" id="CLERKNODEsetProxyTransport"  class="forminput" placeholder="org.apache.juddi.v3.client.transport.JAXWSTransport" value="org.apache.juddi.v3.client.transport.JAXWSTransport"><br>
                                        <%=ResourceLoader.GetResource(session, "items.inquiry")%> <input type="text" id="CLERKNODEsetInquiryUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/inquiry" value="http://localhost:8080/juddiv3/services/inquiry"><br>
                                        <%=ResourceLoader.GetResource(session, "items.publish")%> <input type="text" id="CLERKNODEsetPublishUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/publish" value="http://localhost:8080/juddiv3/services/publish"><br>
                                        <%=ResourceLoader.GetResource(session, "items.security")%> <input type="text" id="CLERKNODEsetSecurityUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/security" value="http://localhost:8080/juddiv3/services/security"><br>
                                        <%=ResourceLoader.GetResource(session, "items.subscription.list")%> <input type="text" id="CLERKNODEsetSubscriptionListenerUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/subscription-listener" value="http://localhost:8080/juddiv3/services/subscription-listener"><br>
                                        <%=ResourceLoader.GetResource(session, "items.subscription")%> <input type="text" id="CLERKNODEsetSubscriptionUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/subscription" value="http://localhost:8080/juddiv3/services/subscription"><br>
                                        <%=ResourceLoader.GetResource(session, "items.custodytransfer")%> <input type="text" id="CLERKNODEsetCustodyTransferUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/custody-transfer" value="http://localhost:8080/juddiv3/services/custody-transfer"><br>
                                        <%=ResourceLoader.GetResource(session, "items.replication")%> <input type="text" id="CLERKNODEsetReplicationUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/replication" value="http://localhost:8080/juddiv3/services/replication"><br>
                                        jUDDI API <input type="text" id="CLERKNODEsetJuddiApiUrl"  class="forminput" placeholder="http://localhost:8080/juddiv3/services/juddi-api" value="http://localhost:8080/juddiv3/services/juddi-api"><br>

                                </div>

                        </div>
                        <script type="text/javascript">
                                function toggledivs()
                                {
                                        var x = $("#divselector").val();
                                        //alert(x);
                                        $("#adminDelete_tmodel").hide();
                                        $("#delete_ClientSubscriptionInfo").hide();
                                        $("#delete_publisher").hide();
                                        $("#getAllPublisherDetail").hide();
                                        $("#get_publisherDetail").hide();
                                        $("#invoke_SyncSubscription").hide();
                                        $("#save_ClientSubscriptionInfo").hide();
                                        $("#save_publisher").hide();
                                        $("#save_Clerk").hide();
                                        $("#save_Node").hide();
                                        $("#" + x).show();
                                }
                                toggledivs();//run when the page loads
                                function submitform() {
                                        var url = 'ajax/go.jsp';

                                        var postbackdata = new Array();

                                        var x = $("#divselector").val();
                                        postbackdata.push({
                                                name: "soapaction",
                                                value: x
                                        });

                                        postbackdata.push({
                                                name: "nonce",
                                                value: $("#nonce").val()
                                        });

                                        $(".forminput").each(function()
                                        {
                                                var id = $(this).attr("id");
                                                var value = $(this).text();
                                                if (value === null || value == "" || value == undefined)
                                                        value = $(this).val();
                                                postbackdata.push({
                                                        name: id,
                                                        value: value
                                                });
                                        });


                                        var request = $.ajax({
                                                url: url,
                                                type: "POST",
                                                //  data" + i18n_type + ": "html", 
                                                cache: false,
                                                //  processData: false,f
                                                data: postbackdata
                                        });


                                        request.done(function(msg) {
                                                window.console && console.log('postback done ' + url);
                                                $("#adminresults").html(msg);
                                                $('#adminresultsmodal').modal();
                                        });

                                        request.fail(function(jqXHR, textStatus) {
                                                window.console && console.log('postback failed ' + url);
                                                $("#adminresults").html(jqXHR.responseText + textStatus);
                                                $('#adminresultsmodal').modal();

                                        });
                                }
                        </script>
                        <br>
                        <%=ResourceLoader.GetResource(session, "pages.admin.notes")%>

                        <br>
                        <a href="javascript:submitform();" class="btn btn-primary"><%=ResourceLoader.GetResource(session, "actions.go")%></a>
                </div>
        </div>


        <div class="modal hide fade container " id="adminresultsmodal">
                <div class="modal-header">
                        <a href="javascript:$('#adminresultsmodal').modal('hide');" class="close" data-dismiss="modal" aria-hidden="true">&times;</a>
                        <h3><%=ResourceLoader.GetResource(session, "items.results")%></h3>
                </div>
                <div class="modal-body" align="center">
                        <div id="adminresults"></div>
                </div>
                <div class="modal-footer">
                        <a href="javascript:$('#adminresultsmodal').modal('hide');" class="btn btn-primary" data-dismiss="modal"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
                </div>
        </div>

        <%@include file="header-bottom.jsp"%>