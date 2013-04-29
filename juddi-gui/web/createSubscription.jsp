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

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.uddi.sub_v3.SubscriptionFilter"%>
<%@page import="org.apache.juddi.webconsole.hub.builders.SubscriptionHelper"%>
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
    <%
        //TODO i18n
        //TODO use this page as a subscription editor
        Subscription sub = null;
        boolean newitem = false;
        UddiHub x = UddiHub.getInstance(application, session);
        if (request.getParameter("id") != null) {
            sub = x.GetSubscriptionDetails(request.getParameter("id"));
        }
        if (sub == null) {
            sub = new Subscription();
            sub.setMaxEntities(50);
            sub.setBrief(false);
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.YEAR, 1);
            DatatypeFactory df = DatatypeFactory.newInstance();
            sub.setNotificationInterval(df.newDuration(1000 * 60 * 15));
            sub.setExpiresAfter(df.newXMLGregorianCalendar(gcal));
            sub.setSubscriptionFilter(new SubscriptionFilter());
            newitem = true;
        }
        
    %>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            UDDI has a feature that allows you to be alerted of updates to either specific UDDI entries or to search results.
            This page will help you setup a subscription to meet your needs. Note: only one type of subscription filter can be defined per subscription.
            Normally, users can make as many subscriptions as they want, however it may be effectively limited by the registry implementation.
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
                                <button onclick="return toggleType1();" id="btn-specificitem" value="specificItem" class="btn <%                                    
                                    if (newitem || SubscriptionHelper.isSpecificItem(sub)) {
                                        out.write(" active");
                                    }
                                        %>" >Changes to a specific item</button>
                                <button onclick="return toggleType1();" id="btn-searchresults" value="searchResults" class="btn <%                                    
                                    if (!newitem && !SubscriptionHelper.isSpecificItem(sub)) {
                                        out.write(" active");
                                    }
                                        %>">Changes to search results, such as a new item</button>
                            </div>
                        </div>
                        <script type="text/javascript">
                            function toggleType1()
                            {
                                if ($("#btn-specificitem").hasClass("active"))
                                {
                                    $("#basedonresults").hide();
                                    $("#specific").show();
                                }
                                else
                                {
                                    $("#basedonresults").show();
                                    $("#specific").hide();
                                }
                                return false;
                            }
                            $(document).ready(function(){ toggleType1();});
                           
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
                                    <button onclick="javascript:bindingModal('itemKey'); return false;" class="btn" value="binding">Binding</button>
                                    <button onclick="javascript:businessModal('itemKey'); return false;" class="btn" value="business">Business</button>
                                    <button onclick="javascript:serviceModal('itemKey'); return false;" class="btn" value="service">Service</button>
                                    <button onclick="javascript:tModelModal('itemKey'); return false;" class="btn" value="tmodel">tModel</button>
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
                            <div class="" id="bindingKeyDiv">
                                <input type="text" id="bindingKey" placeholder="Binding Template Key or Email Address" style="width:300px">
                                <a href="javascript:bindingModal('bindingKey');" class="btn " data-dismiss="modal"><%=ResourceLoader.GetResource(session, "actions.select")%></a>
                            </div>
                            <script type="text/javascript">
                                
                             
                                function toggleTransport1()
                                {
                                    $("#bindingKeyDiv").show();
                                    return false;
                                }
                                function toggleTransport2()
                                {
                                    $("#bindingKeyDiv").hide();
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
                            <div> <input type="checkbox" id="brief" 
                                         <%                                             
                                             out.write(sub.isBrief() ? "checked" : "");
                                         %> >
                            </div><br>

                            Expiration: 
                            <div id="datetimepicker2" class="input-append">
                                <input data-format="MM/dd/yyyy HH:mm:ss PP" type="text" value="<%                                    
                                    if (sub.getExpiresAfter() != null) {
                                        Date d = sub.getExpiresAfter().toGregorianCalendar().getTime();
                                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
                                        String dateStr = dateFormat.format(d);
                                   //TODO DOES NOT WORK     out.write(dateStr);
                                    }
                                       %>">
                                
                                <span class="add-on">
                                    <i data-time-icon="icon-time" data-date-icon="icon-calendar">
                                    </i>
                                </span>
                            </div>
                            <script type="text/javascript">
                                $(document).ready(function() {
                                    $('#datetimepicker2').datetimepicker({
                                        language: '<%=(String) session.getAttribute("locale")%>',
                                        pick12HourFormat: true
                                    });
                                });
                            </script>


                            <div style="">Notification Interval (hh:mm:ss) : &nbsp;</div>

                            <div class="input-append bootstrap-timepicker">
                                <input id="timepicker2" type="text" class="input-small" placeholder="Duration of time (hh:mm:ss)" value="<%                                    
                                    if (sub.getNotificationInterval() != null) {
                                        out.write(sub.getNotificationInterval().getHours() + ":"
                                                + sub.getNotificationInterval().getMinutes() + ":"
                                                + sub.getNotificationInterval().getSeconds());
                                    }
                                       %>">
                                <span class="add-on">
                                    <i class="icon-time"></i>
                                </span>
                            </div>

                            <script type="text/javascript">
                                $('#timepicker2').timepicker({
                                    minuteStep: 1,
                                    defaultTime: '00:05:00',
                                    showSeconds: true,
                                    template: 'modal',
                                    showSeconds: true,
                                    showMeridian: false
                                });
                            </script>
                            <br>

                            <div style="float:left">Subscription Key: &nbsp;</div>
                            <div class="edit" id="subkey"><%                                
                                if (sub.getSubscriptionKey() != null) {
                                    out.write(StringEscapeUtils.escapeHtml(sub.getSubscriptionKey()));
                                }
                                %> </div>
                            About Subscription Keys: You can optionally specify a subscription key. If you do, it must follow the rules for UDDI keys (uniqueness, prefixes, tModel Partitions, etc).
                            If you do not define one, the UDDI server should generate one for you. 



                        </div>
                    </div>
                </div>


                <script type="text/javascript">
                    Reedit()
                    $("#bindingKey").resizable();
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

    <%@include file="tmodelChooser.jsp" %>
    <%@include file="bindingChooser.jsp" %>
    <%@include file="businessChooser.jsp" %>
    <%@include file="serviceChooser.jsp" %>
    <%@include file="header-bottom.jsp" %>