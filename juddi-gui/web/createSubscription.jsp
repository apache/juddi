<%-- 
/*
 * Copyright 2001-2013 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

    Document   : createSubscription
    Created on : Apr 7, 2013, 8:47:48 AM
    Author     : Alex O'Ree
--%>

<%@page import="java.util.List"%>
<%@page import="org.uddi.sub_v3.Subscription"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.uddi.api_v3.*"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>
<%@page import="org.apache.juddi.webconsole.hub.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1><%=ResourceLoader.GetResource(session, "navbar.subscriptions")%></h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            <p>


            </p>

            <%                
                
            %>


            <div class="accordion" id="accordion2">
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
                            Step 1 - What do you type of information to you want alerts on?
                        </a>
                    </div>
                    <div id="collapseOne" class="accordion-body collapse in">
                        <div class="accordion-inner">
                            <div class="btn-group" id="alertType" data-toggle="buttons-radio">
                                <button onclick="return toggleType1();" value="specificItem" class="btn active" >Changes to a specific item</button>
                                <button onclick="return toggleType2();" value="searchResults" class="btn">Changes to search results, such as a new item</button>
                            </div>
                        </div>
                        <script type="text/javascript">
                            function toggleType1()
                            {
                                $("#basedonresults").hide();
                                $("#specific").show();
                                return false;
                            }
                            function toggleType2()
                            {
                                  $("#basedonresults").show();
                                $("#specific").hide();
                                return false;
                            }
                        </script>
                    </div>
                </div>

                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">
                            Step 2 - Which items do you want alerts on?
                        </a>
                    </div>
                    <div id="collapseTwo" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <div id="specific">
                                A specific item:<Br>
                                <div class="btn-group" id="alertCriteraSingleItem" data-toggle="buttons-radio">
                                    <button onclick="return false;" class="btn" value="binding">Binding</button>
                                    <button onclick="return false;" class="btn" value="business">Business</button>
                                    <button onclick="return false;" class="btn" value="service">Service</button>
                                    <button onclick="return false;" class="btn" value="tmodel">tModel</button>
                                </div><br><br>
                                <div style="float:left">Key: &nbsp;</div>
                                <div class="edit" id="itemKey"></div>
                            </div>
                            <div id="basedonresults">
                                Search results:<br>
                                <div class="btn-group" id="alertCriteraMultipleItem" data-toggle="buttons-radio">
                                    <button onclick="return false;" class="btn" value="binding">Binding</button>
                                    <button onclick="return false;" class="btn" value="business">Business</button>
                                    <button onclick="return false;" class="btn" value="service">Service</button>
                                    <button onclick="return false;" class="btn" value="tmodel">tModel</button>
                                    <button onclick="return false;" class="btn" value="publisherAssertion">Publisher Assertion Status</button>
                                    <button onclick="return false;" class="btn" value="relatedBusiness">Related Business</button>
                                </div>
                                <br><br>
                                <input type="text" placeholder="Type something…" id="searchcontent">
                                <input type="text" placeholder="Language" id="lang"><br>
                            </div>


                        </div>

                    </div>
                </div>

                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseThree">
                            Step 3 - How do want to receive the alerts?
                        </a>
                    </div>
                    <div id="collapseThree" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <div class="btn-group" id="alertTransport" data-toggle="buttons-radio">
                                <button onclick="return toggleTransport1();" value="bindingTemplate" class="btn" title="Either via a UDDI Subscription Callback or via Email">Send me alerts directly</button>
                                <button onclick="return toggleTransport2();" value="manual" class="btn" title="Either via this website or from your own software">I'll pick them up</button>
                            </div><br>
                            <input type="text" id="bindingKey" autocomplete="false" placeholder="Binding Template Key or Email Address">
                            <script type="text/javascript">
                                function toggleTransport1()
                                {
                                    $("#bindingKey").show();
                                    return false;
                                }
                                function toggleTransport2()
                                {
                                    $("#bindingKey").hide();
                                    return false;
                                }
                            </script>
                        </div>
                    </div>
                </div>


                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapse4">
                            Step 4 - Options
                        </a>
                    </div>
                    <div id="collapse4" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <div style="float:left">Max records per callback: &nbsp;</div>
                            <div class="edit" id="maxRecords">50</div>
                            <div style="float:left">Brief subscription: &nbsp;</div>
                            <div class="edit" id="brief">true</div>

                            Expiration: 
                            <div id="datetimepicker2" class="input-append">
                                <input data-format="MM/dd/yyyy HH:mm:ss PP" type="text"></input>
                                <span class="add-on">
                                    <i data-time-icon="icon-time" data-date-icon="icon-calendar">
                                    </i>
                                </span>
                            </div>
                            <script type="text/javascript">
                                $(function() {
                                    $('#datetimepicker2').datetimepicker({
                                        language: '<%=(String) session.getAttribute("locale")%>',
                                        pick12HourFormat: true
                                    });
                                });
                            </script>


                            <div style="">Notification Interval: &nbsp;</div>

                            <div class="input-append bootstrap-timepicker">
                                <input id="timepicker2" type="text" class="input-small">
                                <span class="add-on">
                                    <i class="icon-time"></i>
                                </span>
                            </div>

                            <script type="text/javascript">
                                $('#timepicker2').timepicker({
                                    minuteStep: 1,
                                    template: 'modal',
                                    showSeconds: true,
                                    showMeridian: false
                                });
                            </script>
                            <br>
                            <div style="float:left">Subscription Key: &nbsp;</div>
                            <div class="edit" id="subkey"></div>



                        </div>
                    </div>
                </div>


                <script type="text/javascript">
                    Reedit()
                    function saveSubscription()
                    {
                        
                        var interval = $("#interval").val();
                        var maxRecords = $("#maxRecords").val();
                        var brief = $("#brief").val();
                        var datetimepicker2 = $("#datetimepicker2").val();
                        var subkey = $("#subkey").val();

                        var alertCriteraSingleItem = $("#alertCriteraSingleItem > button.btn.active").val();
                        var alertTransport = $("#alertTransport > button.btn.active").val();
                        var itemKey = $("#itemKey").val();
                        var bindingKey = $("#bindingKey").val();
                        var alertType = $("#alertType > button.btn.active").val();
                        var postbackdata = new Array();
                        var url='ajax/subscription.jsp';
                        
                        
                        
                        postbackdata.push({
                            name:"subkey", 
                            value: subkey
                        });
                        
                        postbackdata.push({
                            name:"datetimepicker2", 
                            value: datetimepicker2
                        });
                        
                        postbackdata.push({
                            name:"brief", 
                            value: brief
                        });
                        
                        postbackdata.push({
                            name:"maxRecords", 
                            value: maxRecords
                        });
                        
                        postbackdata.push({
                            name:"interval", 
                            value: interval
                        });
                        
                        
                        
                        
                        
                        postbackdata.push({
                            name:"alertType", 
                            value: alertType
                        });
                        postbackdata.push({
                            name:"itemKey", 
                            value: itemKey
                        });
                        postbackdata.push({
                            name:"alertCriteraSingleItem", 
                            value: alertCriteraSingleItem
                        });
                        postbackdata.push({
                            name:"bindingKey", 
                            value: bindingKey
                        });
                        postbackdata.push({
                            name:"alertTransport", 
                            value: alertTransport
                        });
            
                        var request=   $.ajax({
                            url: url,
                            type:"POST",
                            //  dataType: "html", 
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
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' + textStatus);
                            $("#resultBar").show();
        
                        });
                    }
                </script>
            </div>

            <a  class="btn btn-primary" href="javascript:saveSubscription();"><i class="icon-save icon-large"></i> <%=ResourceLoader.GetResource(session, "actions.save")%></a>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>