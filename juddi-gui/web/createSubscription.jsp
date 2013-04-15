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
                Subscription sub = new Subscription();

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
                                <a href="#" class="btn">Changes to a specific item</a>
                                <a href="#" class="btn">Changes to search results, such as a new item</a>
                            </div>
                        </div>
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
                            <div class="btn-group" id="alertCritera" data-toggle="buttons-radio">
                                <a href="#" class="btn">Binding</a>
                                <a href="#" class="btn">Business</a>
                                <a href="#" class="btn">Publisher Assertion Status</a>
                                <a href="#" class="btn">Related Business</a>
                                <a href="#" class="btn">Service</a>
                                <a href="#" class="btn">tModel</a>
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
                                <a href="#" class="btn">Send me alerts directly</a>
                                <a href="#" class="btn">I'll pick them up</a>
                            </div><br>
                            <input type="text" id="bindingKey" autocomplete="false" placeholder="Binding Template">
                        </div>
                    </div>
                </div>
                
                
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapse4">
                            Step 3 - How do want to receive the alerts?
                        </a>
                    </div>
                    <div id="collapse4" class="accordion-body collapse">
                        <div class="accordion-inner">
                            <div class="btn-group" id="alertTransport" data-toggle="buttons-radio">
                                <a href="#" class="btn">Send me alerts directly</a>
                                <a href="#" class="btn">I'll pick them up</a>
                            </div><br>
                            <input type="text" id="bindingKey" autocomplete="false" placeholder="Binding Template">
                        </div>
                    </div>
                </div>
                
                
            </div>


        </div>
    </div>
    <%@include file="header-bottom.jsp" %>