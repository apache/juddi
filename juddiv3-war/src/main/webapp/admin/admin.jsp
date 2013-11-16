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
        <h1>Administration</h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">


        <div class="span12">
            <%
                UddiAdminHub x = UddiAdminHub.getInstance(application, session);


            %>
            This page lets you access the jUDDI Web Service.
            Its functions are outside the scope of the UDDI specification and provide basic administrative functions
            for managing your UDDI node. <br><br><b>You'll need to be logged (top right) in order to do anything.</b><br>
            Please select an item from the drop down menu.<br>
            <select onchange="toggledivs();" id="divselector">
                <option>adminDelete_tmodel</option>
                <option>delete_ClientSubscriptionInfo</option>
                <option>delete_publisher</option>
                <option>getAllPublisherDetail</option>
                <option>get_publisherDetail</option>
                <option>invoke_SyncSubscription</option>
<!--                <option>save_Clerk</option>-->
                <option>save_ClientSubscriptionInfo</option>
                <!--<option>save_Node</option>-->
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
                    Leave the auth token blank!*<br>
                    <textarea rows="4" cols="80" id="invokeSyncSubscriptionXML" class="forminput" placeholder="Enter subscription XML"></textarea>
                </div>
                <div id="delete_publisher" style="display:none">
                    <input type="text"  class="forminput" id="delete_publisherKEY" placeholder="Enter publisher id">
                </div>
                <div id="getAllPublisherDetail" style="display:none">
                    no input required.
                </div>
                <div id="get_publisherDetail" style="display:none">
                    <input type="text" id="get_publisherDetailKEY"  class="forminput" placeholder="Enter publisher id">
                </div>
                <div id="save_ClientSubscriptionInfo" style="display:none">
                    Leave the auth token blank!*<br>
                    <textarea rows="4" cols="80" id="ClientSubscriptionInfoDetailXML" class="forminput" placeholder="Enter subscription XML"></textarea>
                </div>
                <div id="save_publisher" style="display:none">
                    Name<input type="text" id="savePublisherNAME"  class="forminput" placeholder="Enter name"><br>
                    Email<input type="text" id="savePublisherEMAIL"  class="forminput" placeholder="Enter email"><br>
                    Authorized Name (username) <input type="text" id="savePublisherAuthorizedName"  class="forminput" placeholder="Enter Authorized Name"><br>
                    Is Admin <input type="text" id="savePublisherIsAdmin"  class="forminput" ><br>
                    Is Enabled <input type="text" id="savePublisherIsEnabled"  class="forminput"><br>
                    Max bindings per service <input type="text" id="savePublisherMaxBindings" placeholder="100"  class="forminput"><br>
                    Max Services Per Business <input type="text" id="savePublisherMaxServices" placeholder="1000" class="forminput"><br>
                    Max Business <input type="text" id="savePublisherMaxBusiness" placeholder="1000"  class="forminput"><br>
                    Max TModels <input type="text" id="savePublisherMaxTModels" placeholder="1000" class="forminput"><br>
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
                    });

                    request.fail(function(jqXHR, textStatus) {
                        window.console && console.log('postback failed ' + url);
                        $("#adminresults").html(jqXHR.responseText + textStatus);


                    });
                }
            </script>
            <br>
            * For items that require XML input, leave the UDDI authentication token blank. It will be populated automatically.
            <a href="javascript:submitform();" class="btn btn-primary">Go!</a>
            <div id="adminresults"></div>
        </div>

    </div>
    <%@include file="header-bottom.jsp"%>