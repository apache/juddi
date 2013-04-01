<%-- 
    Document   : service editor
    Created on : Feb 24, 2013, 3:31:39 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.net.URLEncoder"%>
<%@page import="org.uddi.api_v3.*"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%=ResourceLoader.GetResource(session, "pages.serviceeditor.title")%></h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >

            <div id="businesseditor">
                <%
                    boolean newitem = false;

                    UddiHub x = UddiHub.getInstance(application, request.getSession());
                    String serviceid = request.getParameter("id");
                    String businessid = request.getParameter("bizid");
                    if (serviceid == null || serviceid.isEmpty()) {
                        //response.sendRedirect("browse.jsp");
                        if (businessid != null && businessid.length() > 0) {
                            newitem = true;
                        } else {
                            response.sendRedirect("index.jsp");
                        }

                    }

                    BusinessService bd = null;
                    if (!newitem) {
                        bd = x.GetServiceDetail(serviceid);
                    } else {
                        bd = new BusinessService();
                        bd.setBusinessKey(businessid);
                        BusinessEntity be = x.GetBusinessDetails(businessid);
                        if (be == null) {
                            //incase an invalid business id was passed in
                            response.sendRedirect("index.jsp");
                        } else {
                            bd.setBusinessKey(be.getBusinessKey());
                        }
                    }

                    if (bd == null) {
                        response.sendError(501);
                    }
                    int totalBTDescriptions = 0;
                %>

                <i class="icon-lock"></i><b> Business Key </b>-
                The Business Key is the unique identifier for the business that this service belongs to. It cannot be modified.<br>
                <div style="border-width: 2px; border-style: solid;" class="noedit" id="<%=PostBackConstants.BUSINESSKEY%>">
                    <%
                        out.write(StringEscapeUtils.escapeHtml(bd.getBusinessKey()));

                        if (bd.getCategoryBag() == null) {
                            bd.setCategoryBag(new CategoryBag());
                        }

                    %></div><br>
                    <%
                        if (!newitem) {
                            out.write("<i class=\"icon-lock\"></i>");
                        }
                    %>
                <b>Service Key </b>-
                The Business Key is the unique identifier for the business that this service belongs to. If you specify a service key, it must be prefixed with
                an existing partition (key generator).
                <div style="border-width: 2px; border-style: solid;" <%
                    if (!newitem) {
                        out.write("class=\"noedit\"");
                    } else {
                        out.write("class=\"edit\"");
                    }
                     %>
                     id="<%=PostBackConstants.SERVICEKEY%>">
                    <%                        out.write(StringEscapeUtils.escapeHtml(bd.getServiceKey()));


                        if (bd.getCategoryBag() == null) {
                            bd.setCategoryBag(new CategoryBag());
                        }

                        if (bd.getBindingTemplates() == null) {
                            bd.setBindingTemplates(new BindingTemplates());
                        }
                        int currentcatkeyrefBT = 0;
                        int currentcatkeyrefgrpBT = 0;
                        int currentbindingtemplatesInstance = 0;
                        for (int i = 0; i < bd.getBindingTemplates().getBindingTemplate().size(); i++) {
                            if (bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag() != null) {
                                currentcatkeyrefBT += bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReference().size();
                                currentcatkeyrefgrpBT += bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReferenceGroup().size();
                            }
                            if (bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails() != null) {
                                currentbindingtemplatesInstance = bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().size();
                            }
                        }


                    %></div><br>

                <script type="text/javascript">
                    var currentNameEntries=<%= bd.getName().size() - 1%>;
                    var currentbindingtemplatesInstance =<%=currentbindingtemplatesInstance%>;
                    var currentDescriptionEntries=<%= bd.getDescription().size() - 1%>;
                    var currentcatkeyrefBT=<%=currentcatkeyrefBT%>;
                    var currentcatkeyref=<%=bd.getCategoryBag().getKeyedReference().size()%>;
                    var currentcatkeyrefgrpBT=<%=currentcatkeyrefgrpBT%>;
                    var currentcatkeyrefgrp=<%=bd.getCategoryBag().getKeyedReferenceGroup().size()%>;
                    var currentbindingtemplates = <%=bd.getBindingTemplates().getBindingTemplate().size()%>;
                </script> 

                <ul class="nav nav-tabs" id="myTab">
                    <li class="active"><a  href="#general">General</a></li>

                    <li><a href="#categories" >Categories</a></li>

                    <li><a href="#bindingtemplates" >Binding Templates</a></li>

                    <li><a href="#signatures" >Signatures</a></li>

                    <li><a href="#opinfo" >Operational Info</a></li>
                </ul>
                <script>
                    $(function () {
                        $('#myTab').tab;//('show');
                    })
                    $('#myTab a[href=#general]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    $('#myTab a[href=#categories]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    $('#myTab a[href=#bindingtemplates]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    $('#myTab a[href=#signatures]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    $('#myTab a[href=#opinfo]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    
                </script>
                <div class="tab-content">
                    <div class="tab-pane active" id="general">




                        <a href="javascript:AddName();"><i class="icon-plus-sign"></i></a> <b>Name </b>- 
                        Services are identified by one or more name. Multiple names are useful for different languages, legal names, or abbreviations.
                        <div id="nameContainer" style="border-width: 2px; border-style: solid;" >
                            <%
                                for (int i = 0; i < bd.getName().size(); i++) {
                                    out.write("<div id=\"" + PostBackConstants.NAME + i + "\" style=\"border-width:1px; border-style:solid\" >");
                                    out.write("<div style=\"float:left; height:100%\"><a href=\"javascript:Remove('Name" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.NAME + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getName().get(i).getValue()) + "</div>");
                                    out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.NAME + i + PostBackConstants.LANG + "\">" + StringEscapeUtils.escapeHtml(bd.getName().get(i).getLang()) + "</div>");

                                    out.write("</div>");
                                }
                            %></div>
                        <Br>
                        <a href="javascript:AddDescription('Description');"><i class="icon-plus-sign"></i></a> <b>Description </b>- businesses can have more than one description, such as in a different language.
                        <div id="Description" style="border-width: 2px; border-style: solid;" >
                            <%
                                for (int i = 0; i < bd.getDescription().size(); i++) {
                                    out.write("<div id=\"" + PostBackConstants.DESCRIPTION + i + "\" style=\"border-width:1px; border-style:solid\">");
                                    out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('Description" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.DESCRIPTION + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getDescription().get(i).getValue()) + "</div>");
                                    out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.DESCRIPTION + i + PostBackConstants.LANG + "\">" + StringEscapeUtils.escapeHtml(bd.getDescription().get(i).getLang()) + "</div>");

                                    out.write("</div>");
                                }
                            %>
                        </div>
                    </div>
                    <div class="tab-pane" id="categories">

                        <b>Categories </b>- UDDI uses a taxonomy system to categorize businesses and their services. These categories are defined as UDDI tModels and
                        are defined by the administrator(s) of this UDDI node. These categories are appended to business registrations either by adding one or more "Key References"
                        or by adding one or more "Key Reference Groups", which in turn can be a zero or more of Key References as part of it.<br><br>
                        Service Keyed Reference Categories:<Br>
                        <div id="catContainer" style="border-width: 2px; border-style: solid;" >

                            <a href="javascript:AddCategoryKeyReference();"><i class="icon-plus-sign"></i></a> Add Key Reference Category <Br>

                            <%
                                if (bd.getCategoryBag() == null) {
                                    bd.setCategoryBag(new CategoryBag());
                                }
                                //                        out.write("Keyed Reference Categories:");
                                for (int i = 0; i < bd.getCategoryBag().getKeyedReference().size(); i++) {

                                    out.write("<div id=\"catbagkeyref" + i + "\" style=\"border-width:2px; border-style:solid\">");
                                    out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('catbagkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">Key: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"catbagkeyref" + i + "Value\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReference().get(i).getTModelKey()) + "</div>");
                                    out.write("<div style=\"float:left\">Name: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"catbagkeyref" + i + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReference().get(i).getKeyName()) + "</div>");
                                    out.write("<div style=\"float:left\">Value: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"catbagkeyref" + i + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReference().get(i).getKeyValue()) + "</div>");
                                    out.write("</div>");
                                }
                            %>
                        </div>
                        <br>
                        <b>Service Keyed Reference Groups</b><br>
                        <div id="catContainerGrp" style="border-width: 2px; border-style: solid;" >

                            <a href="javascript:AddCategoryKeyReferenceGroup();"><i class="icon-plus-sign"></i></a> Add Key Reference Group Category<br>
                            <%
                                for (int i = 0; i < bd.getCategoryBag().getKeyedReferenceGroup().size(); i++) {

                                    out.write("<div id=\"catbaggrpkeyref" + i + "\" style=\"border-width:2px; border-style:solid\">"
                                            + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('catbaggrpkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                            + "<div style=\"float:left\">Key: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"catbaggrpkeyref" + i + "Value\"></div>"
                                            + "<div id=\"catbaggrpkeyref" + i + "keyref\" style=\"border-width:1px; border-style:solid\">"
                                            + "<div style=\"float:left;height:100%\"><a href=\"javascript:AddCategoryKeyReferenceGroupKeyRef('catbaggrpkeyref" + i + "keyref');\"><i class=\"icon-plus-sign\"></i></a></div>"
                                            + "Add Key Reference"
                                            + "</div>");
                                    //+ "</div>");
                                    /*
                                     out.write("<div id=\"catbaggrpkeyref" + i + "\" style=\"border-width:2px; border-style:solid\">");
                                     out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('catbaggrpkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                     out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:AddCategoryKeyReferenceGroupKeyRef('catbaggrpkeyref" + i + "keyref');\"><i class=\"icon-plus-sign\"></i></a></div>");
                                     out.write("Add Key Reference");
                                     out.write("<div style=\"float:left\">Key: &nbsp;</div>"
                                     + "<div class=\"edit\" id=\"catbagkeyrefgrp" + i + "Value\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getTModelKey()) + "</div>");
                                     * */
                                    for (int k = 0; k < bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {

                                        out.write("<div id=\"catbaggrpkeyref" + i + "keyref" + k + "\" style=\"border-width:1px; border-style:solid\">");
                                        out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('catbaggrpkeyref" + i + "keyref" + k + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                        out.write("<div style=\"float:left\">Key: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"catbaggrpkeyref" + i + "keyref" + k + "Value\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getTModelKey()) + "</div>");
                                        out.write("<div style=\"float:left\">Name: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"catbaggrpkeyref" + i + "keyref" + k + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getKeyName()) + "</div>");
                                        out.write("<div style=\"float:left\">Value: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"catbaggrpkeyref" + i + "keyref" + k + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getKeyValue()) + "</div>");
                                        out.write("</div>");
                                    }
                                    //out.write("<div style=\"float:left\">Name&nbsp;</div>"
                                    //+ "<div class=\"edit\" id=\"discoType" + i + "\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).()) + "</div>");
                                    //out.write("<div style=\"float:left\">Value&nbsp;</div>"
                                    //+ "<div class=\"edit\" id=\"discoType" + i + "\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyValue()) + "</div>");

                                    out.write("</div>");
                                }


                            %>
                        </div>
                    </div>

                    <div class="tab-pane" id="bindingtemplates">
                        <b>Binding Templates </b>- A service in UDDI really defines a specific type of service, not necessarily an implementation of a service. Binding templates
                        define specifically an implementation of a service and normally includes an access point describing how to use the service. Each service may have 0 or more
                        binding templates. Some registries impose limits on the number of binding templates per service.<br>
                        <a href="javascript:AddBindingTemplate();"><i class="icon-plus-sign"></i></a> Add a Binding Template<Br>
                        <div id="bindingTemplatesContainer" style="border-width: 2px; border-style: solid">
                            <%
                                for (int i = 0; i < bd.getBindingTemplates().getBindingTemplate().size(); i++) {
                                    out.write("<div id=\"bindingTemplate" + i + "\"  style=\"border-width: 2px; border-style: dashed;; border-color: lightseagreen\" >");
                                    out.write("<div style=\"float:left\">"
                                            + "<a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + "');\"><i class=\"icon-remove-sign\"></i></a>"
                                            + "Binding Template Key: &nbsp;</div>"
                                            + "<div class=\"");
                                    if (!newitem) {
                                        out.write("no");
                                    }
                                    out.write("edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.VALUE + "\">");
                                    if (!newitem) {
                                        out.write(StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getBindingKey()));
                                    }
                                    // out.write("</div>");
%>          
                            <br>
                            <a href="javascript:AddDescriptionSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.DESCRIPTION%>');"><i class="icon-plus-sign"></i></a>Add a Binding Template Description - binding templates can have more than one description, such as in a different language.<Br>

                            <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.DESCRIPTION%>" style="border-width: 1px; border-style: dotted;" >
                                <%
                                    for (int k = 0; k < bd.getBindingTemplates().getBindingTemplate().get(i).getDescription().size(); k++) {
                                        out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.DESCRIPTION + k + "\" style=\"border-width:1px; border-style:solid\">");
                                        out.write("<div style=\"float:left;height:100%\">"
                                                + "<a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.DESCRIPTION + k + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                        out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.DESCRIPTION + k + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getDescription().get(k).getValue()) + "</div>");
                                        out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.DESCRIPTION + k + PostBackConstants.LANG + "\">"
                                                + (bd.getBindingTemplates().getBindingTemplate().get(i).getDescription().get(k).getLang() == null ? " " : StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getDescription().get(k).getLang()))
                                                + "</div>");
                                        out.write("</div>");
                                        totalBTDescriptions++;

                                    }
                                %>
                            </div>
                            <b>Access Point</b> - UDDI allows for a choice of either a Hosting Redirector OR an Access Point. Access Point is recommend. The access point is usually a URL for the service endpoint.<br>
                            <%



                                //out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + "\" style=\"border-width:1px; border-style:solid\" >");
                                //need an html select in here
                                if (bd.getBindingTemplates().getBindingTemplate().get(i).getHostingRedirector() != null) {
                                    out.write("<div style=\"float:left\">Hosting Redirector: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.HOSTINGREDIRECTOR + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getHostingRedirector().getBindingKey()) + "</div>");
                                }
                                if (bd.getBindingTemplates().getBindingTemplate().get(i).getAccessPoint() != null) {

                                    out.write("<div style=\"float:left\">Access Point Type: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.ACCESSPOINT_TYPE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getAccessPoint().getUseType()) + "</div>");
                                    out.write("<div style=\"float:left\">Access Point Value: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.ACCESSPOINT_VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getAccessPoint().getValue()) + "</div>");
                                    out.write("</div>");
                                }
                            %>

                            <br>
                            <b>tModel Instance Information</b> - a binding template can have additional information attached to it using the tModel Instance.<br>
                            <a href="javascript:AddTmodelInstance('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE%>');"><i class="icon-plus-sign"></i></a> Add a tModel Instance<br>
                            <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE%>" style="border-width: 1px; border-style: solid; border-color: red" >        
                                <%
                                    if (bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails() != null) {
                                        for (int k = 0; k < bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().size(); k++) {
                                %>
                                <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k%>" style="border-width: 2px; border-style: dashed; border-color: red" >        
                                    <%
                                        out.write("<div style=\"float:left;height:100%\">"
                                                + "<a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + "');\"><i class=\"icon-remove-sign\"></i></a></div>");

                                        out.write("<div style=\"float:left\"><b>tModel Key: </b>&nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.KEYNAME + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getTModelKey()) + "</div>");
                                        //  out.write("<div style=\"float:left\"><span title=\"Instance Params\">Value</span>:&nbsp;</div>"
                                        //          + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.VALUE + "\">" + ((bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails() != null) ? StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getInstanceParms()) : "") + "</div>");
%>
                                    <br>
                                    <%

                                        out.write("<div style=\"float:left\"><b>tModel Instance Parameters:</b> &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getInstanceParms()) + "</div>");
                                    %>
                                    <br>

                                    <b>tModel Instance Description</b> - tModel instance infos can have more than one description, such as in a different language.<br>
                                    <a href="javascript:AddDescriptionSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION%>');"><i class="icon-plus-sign"></i></a> Add a tModel Instance Description<br>
                                    <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION%>" style="border-width: 1px; border-style: groove;" >
                                        <%
                                            for (int j = 0; j < bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().size(); j++) {
                                                out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION + j + "\" style=\"border-width:1px; border-style:solid\">");
                                                out.write("<div style=\"float:left;height:100%\">"
                                                        + "<a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION + j + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                                out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION + j + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().get(j).getValue()) + "</div>");
                                                out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.DESCRIPTION + j + PostBackConstants.LANG + "\">"
                                                        + (bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().get(j).getLang() == null ? " " : StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().get(j).getLang()))
                                                        + "</div>");
                                                out.write("</div>");
                                            }

                                        %>
                                    </div>

                                    <div><br>
                                        <b>Overview Documents</b> - These are typically URLs to web pages that describe this tModel's details and usage scenarios.<br>
                                        <a href="javascript:AddOverviewDocumentSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW%>');"><i class="icon-plus-sign"></i></a> Add an Overview Document<br>
                                        <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.OVERVIEW%>" style="border-width: 1px; border-style: groove;" >
                                            <%
                                                //  out.write("<div id=\"" + PostBackConstants.OVERVIEW + "\" style=\"border-width:2px; border-style:solid\">");
                                                for (int j = 0; j < bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getOverviewDoc().size(); j++) {
                                                    out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + "\" style=\"border-width:1px; border-style:solid\">");
                                                    out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                                    out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getOverviewDoc().get(j).getOverviewURL().getValue()) + "</div>");
                                                    out.write("<div style=\"float:left\">Use type:&nbsp;</div>"
                                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.TYPE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getOverviewDoc().get(j).getOverviewURL().getUseType()) + "</div>");
                                            %>
                                            <br><b>Overview Document Descriptions</b><br>
                                            <a href="javascript:AddDescriptionSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.DESCRIPTION%>');"><i class="icon-plus-sign"></i></a>Add an Overview Document Description
                                            <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.DESCRIPTION%>" style="border-width: 1px; border-style: groove;" >
                                                <%
                                                    //  out.write("<div id=\"" + PostBackConstants.OVERVIEW + "\" style=\"border-width:2px; border-style:solid\">");
                                                    for (int h = 0; h < bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getOverviewDoc().get(j).getDescription().size(); h++) {
                                                        out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.DESCRIPTION + h + "\" style=\"border-width:1px; border-style:solid\">");
                                                        out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.DESCRIPTION + h + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                                        out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.DESCRIPTION + h + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getOverviewDoc().get(j).getDescription().get(h).getValue()) + "</div>");
                                                        out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.INSTANCE + PostBackConstants.OVERVIEW + j + PostBackConstants.DESCRIPTION + h + PostBackConstants.LANG + "\">"
                                                                + (bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getOverviewDoc().get(j).getDescription().get(h).getLang() == null ? "" : StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getOverviewDoc().get(j).getDescription().get(h).getLang())) + "</div>");
                                                        out.write("</div>");
                                                    }
                                                %>
                                            </div>
                                            <%
                                                    out.write("</div>");
                                                }
                                            %>

                                        </div>

                                    </div>

                                </div>

                                <%    } //end of instance details
%>
                            </div>                                        
                            <%
                                }
                            %><Br>
                            <b>Binding Template Keyed Reference Categories:</b><Br>

                            <a href="javascript:AddCategoryKeyReferenceSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF%>');"><i class="icon-plus-sign"></i></a> Add Key Reference Category <Br>
                            <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF%>" style="border-width: 1px; border-style: dotted;" >
                                <%
                                    if (bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag() == null) {
                                        bd.getBindingTemplates().getBindingTemplate().get(i).setCategoryBag(new CategoryBag());
                                    }
                                    for (int k = 0; k < bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                        out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF + k + "\" style=\"border-width:2px; border-style:solid\">");
                                        out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF + k + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                        out.write("<div style=\"float:left\">Key: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF + k + "Value\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReference().get(i).getTModelKey()) + "</div>");
                                        out.write("<div style=\"float:left\">Name: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF + k + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReference().get(i).getKeyName()) + "</div>");
                                        out.write("<div style=\"float:left\">Value: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF + k + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReference().get(i).getKeyValue()) + "</div>");
                                        out.write("</div>");
                                    }

                                %>
                            </div>
                            <br>    
                            <b>Binding Template Keyed Reference Groups</b><br>
                            <a href="javascript:AddCategoryKeyReferenceGroupSpecificBT('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP%>');"><i class="icon-plus-sign"></i></a> Add Key Reference Group Category<br>
                            <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP%>" style="border-width: 1px; border-style: dotted;" >

                                <%
                                    for (int z = 0; z < bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReferenceGroup().size(); z++) {

                                        out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "\" style=\"border-width:2px; border-style:solid\">"
                                                + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                + "<div style=\"float:left\">Key: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "Value\"></div>"
                                                + "<div id=\"catbaggrpkeyref" + i + "keyref\" style=\"border-width:1px; border-style:solid\">"
                                                + "<div style=\"float:left;height:100%\"><a href=\"javascript:AddCategoryKeyReferenceSpecific('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "keyref');\"><i class=\"icon-plus-sign\"></i></a></div>"
                                                + "Add Key Reference"
                                                + "</div>");
                                        for (int k = 0; k < bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReferenceGroup().get(z).getKeyedReference().size(); k++) {

                                            out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "keyref" + k + "\" style=\"border-width:1px; border-style:solid\">");
                                            out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "keyref" + k + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                            out.write("<div style=\"float:left\">Key: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "keyref" + k + "Value\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReferenceGroup().get(z).getKeyedReference().get(k).getTModelKey()) + "</div>");
                                            out.write("<div style=\"float:left\">Name: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "keyref" + k + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReferenceGroup().get(z).getKeyedReference().get(k).getKeyName()) + "</div>");
                                            out.write("<div style=\"float:left\">Value: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF_GRP + z + "keyref" + k + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReferenceGroup().get(z).getKeyedReference().get(k).getKeyValue()) + "</div>");
                                            out.write("</div>");
                                        }
                                        out.write("</div>");
                                    }
                                %>
                            </div>
                        </div>
                        <%

                            } //end of binding templates loop
%>


                        <%//end of tmodel instance%>

                    </div>
                    <%//end of binding tempaltes container%>

                </div>



                <div class="tab-pane" id="signatures">
                    <br>
                    <%
                        if (bd.getSignature().isEmpty()) {
                            out.write("This item is not digitally signed.");
                        } else {
                            out.write("This item is digitally signed " + bd.getSignature().size());
                    %>
                    <table class="table">


                        <%
                                for (int k = 0; k < bd.getSignature().size(); k++) {
                                    out.write("<tr><td>");
                                    out.write(x.SignatureToReadable(bd.getSignature().get(k)));
                                    out.write("</td><td>");
                                    out.write("<a href=\"ajax/getCert.jsp?type=service&id=" + URLEncoder.encode(bd.getServiceKey(), "UTF-8") + "&index=" + k + "\">View Certificate</a>");
                                    out.write("</td></tr>");
                                }
                            }

                        %>
                    </table>
                </div>

                <div class="tab-pane" id="opinfo">
                    <script type="text/javascript">
                        $.get("ajax/opInfo.jsp?id=<%=StringEscapeUtils.escapeJavaScript(bd.getServiceKey())%>", function(data){
                            $("#opinfodiv").html(data);
                        } )
                    </script>
                    <div id="opinfodiv"></div>

                </div>
            </div>
        </div>
        <script type="text/javascript">
            var currentDescriptionSpecific=<%=totalBTDescriptions%>;
        </script>
        <Br><br>
        <%
            if (bd.getSignature().isEmpty()) {
        %>
        <a class="btn btn-primary " href="javascript:saveService();">Save</a> | 
        <%  } else {
        %>
        <a href="#confirmDialog" role="button" class="btn btn-primary" data-toggle="modal"><%=ResourceLoader.GetResource(session, "actions.save")%></a> |

        <%        }
            //        <a class="btn btn-primary " href="javascript:saveService();">Save</a> | 
        %>


        <a class="btn btn-danger " href="javascript:deleteService();"><%=ResourceLoader.GetResource(session, "actions.delete")%></a> |
        <a class="btn btn-success " href="signer.jsp?id=<%=URLEncoder.encode(bd.getServiceKey(), "UTF8")%>&type=service"><%=ResourceLoader.GetResource(session, "actions.sign")%></a> |
        <a class="btn btn-info " href="#" title="Alert me when this entity changes"><%=ResourceLoader.GetResource(session, "actions.subscribe")%></a> |
        <a class="btn btn-warning " href="#" title="Transfer this entity to another UDDI node"><%=ResourceLoader.GetResource(session, "actions.transfer")%></a>
    </div>
    <script type="text/javascript" src="js/businessEditor.js"></script>
    <script type="text/javascript" src="js/serviceEditor.js"></script>

    <div class="modal hide fade" id="confirmDialog">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3><%=ResourceLoader.GetResource(session, "modal.digitalsignaturewarning.title")%></h3>
        </div>
        <div class="modal-body">
            <p><%=ResourceLoader.GetResource(session, "modal.digitalsignaturewarning.body")%></p>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
            <a href="javascript:saveService();$('#confirmDialog').modal('hide');" class="btn btn-primary">
                <%=ResourceLoader.GetResource(session, "modal.savechanges")%></a>
        </div>
    </div>
    <!-- container div is in header bottom-->
    <%@include file="header-bottom.jsp" %>