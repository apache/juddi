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

    Document   : editSubscription
    Created on : Apr 7, 2013, 8:47:48 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.v3.client.UDDIConstants"%>
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
            if (request.getParameter("svcid") != null) {
                //TODO handled a linked in subscription
            }
            if (request.getParameter("bizid") != null) {
                //TODO handled a linked in subscription
            }
            if (request.getParameter("tid") != null) {
                //TODO handled a linked in subscription
            }
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
            <%=ResourceLoader.GetResource(session, "pages.subscription.content")%>

            <div class="accordion" id="accordion2">
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
                            <%=ResourceLoader.GetResource(session, "pages.subscription.step1")%>

                        </a>
                    </div>
                    <div id="collapseOne" class="accordion-body collapse in">
                        <div class="accordion-inner">
                            <div class="btn-group" id="alertType" data-toggle="buttons-radio">
                                <button onclick="return toggleType1(false);" id="btn-specificitem" value="specificItem" class="btn <%
                                    if (newitem || SubscriptionHelper.isSpecificItem(sub)) {
                                        out.write(" active");
                                    }
                                        %>" >
                                    <%=ResourceLoader.GetResource(session, "pages.subscription.step1.specific")%>
                                </button>
                                <button onclick="return toggleType1(false);" id="btn-searchresults" value="searchResults" class="btn <%
                                    if (!newitem && !SubscriptionHelper.isSpecificItem(sub)) {
                                        out.write(" active");
                                    }
                                        %>">
                                    <%=ResourceLoader.GetResource(session, "pages.subscription.step1.search")%>
                                </button>
                            </div>
                        </div>
                        <script type="text/javascript">
                            function toggleType1(firstLoad)
                            {
                                //window.console && console.log('hi  ' + $("#btn-specificitem").hasClass("active"));   
                                setTimeout(function(){
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
                                    if (firstLoad!=true){
                                        $('#collapseOne').collapse('hide');
                                        $('#collapseTwo').collapse('show');}
                                }, 100);
                                
                                return false;
                            }
                            $(document).ready(function(){ toggleType1(true);});
                           
                        </script>
                    </div>
                </div>

                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">

                            <%=ResourceLoader.GetResource(session, "pages.subscription.step2")%>
                        </a>
                    </div>
                    <div id="collapseTwo" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <div id="specific">
                                A specific item:<Br>
                                <div class="btn-group" id="alertCriteraSingleItem" data-toggle="buttons-radio">
                                    <button onclick="javascript:clearbox(); return false;" class="btn <%=SubscriptionHelper.isBindingSpecific(sub)%>" value="binding"><%=ResourceLoader.GetResource(session, "items.bindingtemplate")%></button>
                                    <button onclick="javascript:clearbox(); return false;" class="btn <%=SubscriptionHelper.isBusinessSpecific(sub)%>" value="business"><%=ResourceLoader.GetResource(session, "items.business")%></button>
                                    <button onclick="javascript:clearbox(); return false;" class="btn <%=SubscriptionHelper.isServiceSpecific(sub)%>" value="service"><%=ResourceLoader.GetResource(session, "items.service")%></button>
                                    <button onclick="javascript:clearbox(); return false;" class="btn <%=SubscriptionHelper.isTModelSpecific(sub)%>" value="tmodel"><%=ResourceLoader.GetResource(session, "items.tmodel")%></button>
                                    <button onclick="javascript:publisherAssertionPicker(); return false;" class="btn <%=SubscriptionHelper.isPublisherAssertionSpecific(sub)%>" value="publisherAssertion"><%=ResourceLoader.GetResource(session, "items.publisherassertion.status")%></button>
                                </div><br><br>
                                <div id="keylistcontainer">
                                    <a href="javascript:additem();" class="btn" ><%=ResourceLoader.GetResource(session, "actions.add")%></a>
                                    <a href="javascript:removeitem();" class="btn" ><%=ResourceLoader.GetResource(session, "actions.remove")%></a> 
                                    <select  id="keylist" size="5" style="width:100%">
                                        <%=SubscriptionHelper.getItemKeySpecific(sub)%>
                                    </select>

                                </div>
                                <script type="text/javascript">
                                    $("#pubassertcontainer").hide();
                                    $("#keylist").resizable();
                                    <%
                                        if (!SubscriptionHelper.isPublisherAssertionSpecific(sub).equals("")) {
                                            out.write("publisherAssertionPicker();");
                                        }
                                    %>
                                        function publisherAssertionPicker()
                                        {
                                            $("#keylistcontainer").hide();
                                            $("#pubassertcontainer").show();
                                            selectPublisherAssertionStatus();
                                        }
                                        function clearbox()
                                        {
                                            $("#keylist option").remove();
                                            $("#keylistcontainer").show();
                                            $("#pubassertcontainer").hide();
                                            return false;
                                        }
                                        function additem()
                                        {
                                            var alertCriteraSingleItem = $("#alertCriteraSingleItem > button.btn.active").val();
                                            if (alertCriteraSingleItem=="binding")
                                            {
                                                reloadBindingModal();
                                                $.dialogBinding.confirm({
                                                    callback: function(success, result) {
                                                        if (success)
                                                        {
                                                            for (var i=0;i<result.length;i++)
                                                                if ($("#keylist option[value='"+result[i]+"']").length == 0)
                                                                    $("#keylist").append("<option value=\"" + result[i] + "\">" + result[i] + "</option>");
                                                        }
                                                    }
                                                });
                                            }
                                            if (alertCriteraSingleItem=="business"){
                                                reloadBusinessModal();
                                                $.dialogBusiness.confirm({
                                                    callback: function(success, result) {
                                                        if (success)
                                                        {
                                                            for (var i=0;i<result.length;i++)
                                                                if ($("#keylist option[value='"+result[i]+"']").length == 0)
                                                                    $("#keylist").append("<option value=\"" + result[i] + "\">" + result[i] + "</option>");
                                                        }
                                                    }
                                                });
                                            }
                                            if (alertCriteraSingleItem=="service"){
                                                reloadServiceModal();
    
                                                $.dialogService.confirm({
                                                    callback: function(success, result) {
                                                        if (success)
                                                        {
                                                            for (var i=0;i<result.length;i++)
                                                                if ($("#keylist option[value='"+result[i]+"']").length == 0)
                                                                    $("#keylist").append("<option value=\"" + result[i] + "\">" + result[i] + "</option>");
                                                        }
                                                    }
                                                });

                                            }
                                            if (alertCriteraSingleItem=="tmodel"){
                                                reloadTmodelModal();
                                                $.dialogTmodel.confirm({
                                                    callback: function(success, result) {
                                                        if (success)
                                                        {
                                                            for (var i=0;i<result.length;i++)
                                                                if ($("#keylist option[value='"+result[i]+"']").length == 0)
                                                                    $("#keylist").append("<option value=\"" + result[i] + "\">" + result[i] + "</option>");
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        function removeitem()
                                        {
                                            $("#keylist option:selected").remove();
                                        }
                                </script>
                                <div id="pubassertcontainer" class="">
                                    <div style="float:left"><%=ResourceLoader.GetResource(session, "items.key")%>: &nbsp;</div>
                                    <div class="" id="itemKey"><%
                                        if (!SubscriptionHelper.isPublisherAssertionSpecific(sub).equals("")) {
                                            out.write(StringEscapeUtils.escapeHtml(SubscriptionHelper.getItemKeySpecific(sub)));
                                        }
                                        %></div>
                                </div>
                            </div>
                            <div id="basedonresults">
                                <%=ResourceLoader.GetResource(session, "search.results")%>:<br>
                                <div class="btn-group" id="alertCriteraMultipleItem" data-toggle="buttons-radio">
                                    <button onclick="return false;" class="btn" value="binding"><%=ResourceLoader.GetResource(session, "items.bindingtemplate")%></button>
                                    <button onclick="return false;" class="btn" value="business"><%=ResourceLoader.GetResource(session, "items.business")%></button>
                                    <button onclick="return false;" class="btn" value="service"><%=ResourceLoader.GetResource(session, "items.service")%></button>
                                    <button onclick="return false;" class="btn" value="tmodel"><%=ResourceLoader.GetResource(session, "items.tmodel")%></button>
                                    <button onclick="return false;" class="btn" value="relatedBusiness"><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.relatedbusinesses")%></button>
                                </div>
                                <br><br>
                                <%=ResourceLoader.GetResource(session, "items.findqualifiers")%><br>
                                <table class="table">
                                    <tr>

                                        <td><input class="fq" type="checkbox" name="<%=UDDIConstants.AND_ALL_KEYS%>" value="<%=UDDIConstants.AND_ALL_KEYS%>"> <%=UDDIConstants.AND_ALL_KEYS%></td>
                                        <td><input class="fq" type="checkbox" name="<%=UDDIConstants.APPROXIMATE_MATCH%>" value="<%=UDDIConstants.APPROXIMATE_MATCH%>"> <%=UDDIConstants.APPROXIMATE_MATCH%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.BINARY_SORT%>" value="<%=UDDIConstants.BINARY_SORT%>"> <%=UDDIConstants.BINARY_SORT%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.BINDING_SUBSET%>" value="<%=UDDIConstants.BINDING_SUBSET%>"> <%=UDDIConstants.BINDING_SUBSET%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.CASE_INSENSITIVE_MATCH%>" value="<%=UDDIConstants.CASE_INSENSITIVE_MATCH%>"> <%=UDDIConstants.CASE_INSENSITIVE_MATCH%></td>
                                    </tr>
                                    <tr>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.CASE_INSENSITIVE_SORT%>" value="<%=UDDIConstants.CASE_INSENSITIVE_SORT%>"> <%=UDDIConstants.CASE_INSENSITIVE_SORT%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.CASE_SENSITIVE_MATCH%>" value="<%=UDDIConstants.CASE_SENSITIVE_MATCH%>"> <%=UDDIConstants.CASE_SENSITIVE_MATCH%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.CASE_SENSITIVE_SORT%>" value="<%=UDDIConstants.CASE_SENSITIVE_SORT%>"> <%=UDDIConstants.CASE_SENSITIVE_SORT%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.COMBINE_CATEGORY_BAGS%>" value="<%=UDDIConstants.COMBINE_CATEGORY_BAGS%>"> <%=UDDIConstants.COMBINE_CATEGORY_BAGS%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.DIACRITIC_INSENSITIVE_MATCH%>" value="<%=UDDIConstants.DIACRITIC_INSENSITIVE_MATCH%>"> <%=UDDIConstants.DIACRITIC_INSENSITIVE_MATCH%></td>
                                    </tr>
                                    <tr>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.DIACRITIC_SENSITIVE_MATCH%>" value="<%=UDDIConstants.DIACRITIC_SENSITIVE_MATCH%>"> <%=UDDIConstants.DIACRITIC_SENSITIVE_MATCH%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.EXACT_MATCH%>" value="<%=UDDIConstants.EXACT_MATCH%>"> <%=UDDIConstants.EXACT_MATCH%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.OR_ALL_KEYS%>" value="<%=UDDIConstants.OR_ALL_KEYS%>"> <%=UDDIConstants.OR_ALL_KEYS%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.OR_LIKE_KEYS%>" value="<%=UDDIConstants.OR_LIKE_KEYS%>"> <%=UDDIConstants.OR_LIKE_KEYS%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.SERVICE_SUBSET%>" value="<%=UDDIConstants.SERVICE_SUBSET%>"> <%=UDDIConstants.SERVICE_SUBSET%></td>
                                    </tr>
                                    <tr>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.SIGNATURE_PRESENT%>" value="<%=UDDIConstants.SIGNATURE_PRESENT%>"> <%=UDDIConstants.SIGNATURE_PRESENT%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.SORT_BY_DATE_ASC%>" value="<%=UDDIConstants.SORT_BY_DATE_ASC%>"> <%=UDDIConstants.SORT_BY_DATE_ASC%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.SORT_BY_DATE_DESC%>" value="<%=UDDIConstants.SORT_BY_DATE_DESC%>"> <%=UDDIConstants.SORT_BY_DATE_DESC%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.SORT_BY_NAME_ASC%>" value="<%=UDDIConstants.SORT_BY_NAME_ASC%>"> <%=UDDIConstants.SORT_BY_NAME_ASC%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.SORT_BY_NAME_DESC%>" value="<%=UDDIConstants.SORT_BY_NAME_DESC%>"> <%=UDDIConstants.SORT_BY_NAME_DESC%></td>
                                    </tr>
                                    <tr>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.SUPPRESS_PROJECTED_SERVICES%>" value="<%=UDDIConstants.SUPPRESS_PROJECTED_SERVICES%>"> <%=UDDIConstants.SUPPRESS_PROJECTED_SERVICES%></td>
                                        <td><input class="fq"  type="checkbox" name="<%=UDDIConstants.UTS_10%>" value="<%=UDDIConstants.UTS_10%>"> <%=UDDIConstants.UTS_10%></td>
                                    </tr>
                                </table>



                                <div>

                                    <input type="text" placeholder="<%=ResourceLoader.GetResource(session, "items.name")%>..." id="searchcontent">
                                    <input type="text" placeholder="<%=ResourceLoader.GetResource(session, "items.lang")%>..." id="searchlang"><br></div>
                            </div>


                        </div>

                    </div>
                </div>

                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseThree">
                            <%=ResourceLoader.GetResource(session, "pages.subscription.step3")%>
                        </a>
                    </div>
                    <div id="collapseThree" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <div class="btn-group" id="alertTransport" data-toggle="buttons-radio">
                                <button id="btn-bindingTemplate" onclick="return toggleTransport1();" value="bindingTemplate" class="btn <%=(sub.getBindingKey() == null ? "" : " active")%>" 
                                        title="<%=ResourceLoader.GetResource(session, "pages.subscription.step3.direct.tooltip")%>"><%=ResourceLoader.GetResource(session, "pages.subscription.step3.direct")%></button>
                                <button id="btn-manual" onclick="return toggleTransport1();" value="manual" class="btn <%=(sub.getBindingKey() == null ? " active " : "")%>" 
                                        title="<%=ResourceLoader.GetResource(session, "pages.subscription.step3.pickup.tooltip")%>"><%=ResourceLoader.GetResource(session, "pages.subscription.step3.pickup")%></button>
                            </div><br>
                            <div class="" id="bindingKeyDiv">
                                <%=ResourceLoader.GetResource(session, "pages.subscription.step3.content")%>
                                <b><%=UDDIConstants.TRANSPORT_HTTP%></b>.
                                <input type="text" id="bindingKey" placeholder="<%=ResourceLoader.GetResource(session, "items.bindingtemplate.key")%>" style="width:360px">
                                <button onClick="javascript:bindingModal('bindingKey', 'val'); return false;" class="btn "><%=ResourceLoader.GetResource(session, "actions.select")%></button>
                            </div>
                            <script type="text/javascript">
                                function selectPublisherAssertionStatus()
                                {
                                    $("#assertionStatusChooser").modal('show');
                                }
                             
                                function toggleTransport1()
                                {
                                    //window.console && console.log('hi  ' + $("#btn-specificitem").hasClass("active"));   
                                    setTimeout(function(){
                                        if ($("#btn-manual").hasClass("active"))
                                        {
                                            $("#bindingKeyDiv").hide();
                                            //$("#specific").show();
                                        }
                                        else
                                        {
                                            $("#bindingKeyDiv").show();
                                            // $("#specific").hide();
                                        }
                                        
                                    }, 100);
                                
                                   
                                    // $("#bindingKeyDiv").show();
                                    return false;
                                }
                                toggleTransport1();
                            </script>
                        </div>
                    </div>
                </div>


                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapse4">
                            <%=ResourceLoader.GetResource(session, "pages.subscription.step4")%>

                        </a>
                    </div>
                    <div id="collapse4" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <div style="float:left"><%=ResourceLoader.GetResource(session, "items.maxrecords.callback")%>: &nbsp;</div>
                            <div class="edit" id="maxRecords"><%
                                if (sub.getMaxEntities() != null) {
                                    sub.getMaxEntities().toString();
                                }
                                %></div>
                            <div style="float:left"><%=ResourceLoader.GetResource(session, "items.subscriptionbrief")%>: &nbsp;</div>
                            <div> <input type="checkbox" id="brief" 
                                         <%
                                             out.write(sub.isBrief() ? "checked" : "");
                                         %> >
                            </div><br>
                            <%=ResourceLoader.GetResource(session, "items.expiration")%>: 
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


                            <div style=""><%=ResourceLoader.GetResource(session, "items.notificationinterval")%>: &nbsp;</div>

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
                    Reedit();
                    $("#bindingKey").resizable();
                    function saveSubscription()
                    {
                        
                        var interval = $("#interval").val();
                        var maxRecords = $("#maxRecords").val();
                        var brief = $("#brief").val();
                        var datetimepicker2 = $("#datetimepicker2").val();
                        
                        var subkey = $("#subkey").html();

                        var alertCriteraSingleItem = $("#alertCriteraSingleItem > button.btn.active").val();
                        var alertTransport = $("#alertTransport > button.btn.active").val();
                        var itemKey = $("#keylist option");
                        var keys="";
                        var first=true;
                        $.each(itemKey, function(idx, value){
                            if (first)
                                keys = value.value;
                            else
                                keys = keys + "," + value.value;
                            first=false;
                        });
                        var bindingKey = $("#bindingKey").val();
                        var alertType = $("#alertType > button.btn.active").val();
                        
                        var alertCriteraMultipleItem = $("#alertCriteraMultipleItem > button.btn.active").val();
                        var searchcontent = $("#searchcontent").val();
                        var searchlang = $("#searchlang").val();
                        
                        var postbackdata = new Array();
                        var url='ajax/subscription.jsp';
                        itemKey = $("#itemKey").html();
                        
                        //  var tqs = new Array();
                        $.each($('.fq input:checkbox'), function(index,item){
                            var itemname = item.id;
                            if (item.checked)
                            {
                                postbackdata.push({
                                    name:"findqualifier", 
                                    value: itemname
                                });
                            }
                        });
                        
                        alertCriteraMultipleItem
                        postbackdata.push({
                            name:"alertCriteraMultipleItem", 
                            value: alertCriteraMultipleItem
                        });
                        postbackdata.push({
                            name:"searchcontent", 
                            value: searchcontent
                        });
                        
                        postbackdata.push({
                            name:"searchlang", 
                            value: searchlang
                        });
                        
                        
                        postbackdata.push({
                            name:"subkey", 
                            value: subkey
                        });
                        
                        postbackdata.push({
                            name:"expires", 
                            value: datetimepicker2
                        });
                        
                        postbackdata.push({
                            name:"interval", 
                            value: interval
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
                            name:"alertType", 
                            value: alertType
                        });
                        postbackdata.push({
                            name:"itemKey", 
                            value: keys
                        });
                        
                        postbackdata.push({
                            name:"assertionStatus", 
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
                        postbackdata.push({
                            name:"nonce", 
                            value: $("#nonce").val()
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
                            $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' +jqXHR.responseText + textStatus);
                            $("#resultBar").show();
        
                        });
                    }
                </script>
            </div>

            <a  class="btn btn-primary" href="javascript:saveSubscription();"><i class="icon-save icon-large"></i> <%=ResourceLoader.GetResource(session, "actions.save")%></a>
        </div>
    </div>


    <div class="modal hide fade " id="assertionStatusChooser">
        <div class="modal-header">
            <a href="javascript:$('#assertionStatusChooser').modal('hide');" class="close" data-dismiss="modal" aria-hidden="true">&times;</a>
            <h3><%=ResourceLoader.GetResource(session, "items.assertion.chooser")%> </h3>
        </div>
        <div class="modal-body">
            <select id="assertionName" style="width:100%">
                <%
                    for (int i = 0; i < CompletionStatus.values().length; i++) {
                        out.write("<option value=\""
                                + StringEscapeUtils.escapeHtml(CompletionStatus.values()[i].toString()) + "\">"
                                + StringEscapeUtils.escapeHtml(CompletionStatus.values()[i].toString()) + "</option>");
                    }
                %>

            </select><br>
            Explanation:<br>
            <ul>
                <%
                    for (int i = 0; i < CompletionStatus.values().length; i++) {
                        out.write("<li><b>" + StringEscapeUtils.escapeHtml(CompletionStatus.values()[i].toString()) + "</b> = "
                                + ResourceLoader.GetResource(session, "items.subscription.CompletionStatus." + CompletionStatus.values()[i].toString()) + "</li>");
                    }
                %></ul>

        </div>
        <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal"><%=ResourceLoader.GetResource(session, "actions.cancel")%></a>
            <a href="javascript:assertionSuccess();" class="btn btn-primary" ><%=ResourceLoader.GetResource(session, "actions.select")%></a>
            <script type="text/javascript">
                function assertionSuccess()
                {
                    $("#itemKey").html($('#assertionName option:selected').val());
                    $('#assertionStatusChooser').modal('hide');
                }
            </script>
        </div>
    </div>


    <%@include file="tmodelChooser.jsp" %>
    <%@include file="bindingChooser.jsp" %>
    <%@include file="businessChooser.jsp" %>
    <%@include file="serviceChooser.jsp" %>
    <%@include file="header-bottom.jsp" %>