<%-- 
    Document   : service editor
    Created on : Feb 24, 2013, 3:31:39 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.uddi.api_v3.BindingTemplates"%>
<%@page import="org.uddi.api_v3.BusinessService"%>
<%@page import="org.uddi.api_v3.IdentifierBag"%>
<%@page import="org.uddi.api_v3.CategoryBag"%>
<%@page import="org.uddi.api_v3.Contacts"%>
<%@page import="org.uddi.api_v3.BusinessEntity"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>

<%@page import="org.apache.juddi.webconsole.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1>Service Editor</h1>
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

                <i class="icon-lock"></i>Business Key -
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
                Service Key -
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

                    %></div><br>

                <script type="text/javascript">
                    var currentNameEntries=<%= bd.getName().size() - 1%>;
                    
                    var currentDescriptionEntries=<%= bd.getDescription().size() - 1%>;
                    
                    var currentcatkeyref=<%=bd.getCategoryBag().getKeyedReference().size()%>;
                    var currentcatkeyrefgrp=<%=bd.getCategoryBag().getKeyedReferenceGroup().size()%>;
                    var currentbindingtemplates = <%=bd.getBindingTemplates().getBindingTemplate().size()%>;
                    
                </script> 

                <ul class="nav nav-tabs" id="myTab">
                    <li class="active"><a  href="#general">General</a></li>

                    <li><a href="#categories" >Categories</a></li>

                    <li><a href="#bindingtemplates" >Binding Templates</a></li>

                    <li><a href="#signatures" >Signatures</a></li>
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
                    
                </script>
                <div class="tab-content">
                    <div class="tab-pane active" id="general">




                        <a href="javascript:AddName();"><i class="icon-plus-sign"></i></a> Name - 
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
                        <a href="javascript:AddDescription('Description');"><i class="icon-plus-sign"></i></a> Description - businesses can have more than one description, such as in a different language.
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

                        Categories - UDDI uses a taxonomy system to categorize businesses and their services. These categories are defined as UDDI tModels and
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
                        Service Keyed Reference Groups<br>
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
                        Binding Templates - A service in UDDI really defines a specific type of service, not necessarily an implementation of a service. Binding templates
                        define specifically an implementation of a service and normally includes an access point describing how to use the service. Each service may have 0 or more
                        binding templates. Some registries impose limits on the number of binding templates per service.<br>
                        <a href="javascript:AddBindingTemplate();"><i class="icon-plus-sign"></i></a> Add a Binding Template<Br>
                        <div id="bindingTemplatesContainer" style="border-width: 2px; border-style: solid;">
                            <%
                                for (int i = 0; i < bd.getBindingTemplates().getBindingTemplate().size(); i++) {
                                    out.write("<div id=\"bindingTemplate" + i + "\"  style=\"border-width: 1px; border-style: dashed;\" >");
                                    out.write("<div style=\"float:left\">"
                                            + "<a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + "');\"><i class=\"icon-remove-sign\"></i></a>"
                                            + "Binding Template Key: &nbsp;</div>"
                                            + "<div class=\"");
                                    if (!newitem) {
                                        out.write("no");
                                    }
                                    out.write("edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getBindingKey()) + "</div>");
                            %>          
                            <a href="javascript:AddDescriptionSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.DESCRIPTION%>');"><i class="icon-plus-sign"></i></a> Description - binding templates can have more than one description, such as in a different language.
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
                            <%


                                out.write("<Br>UDDI allows for a choice of either a Hosting Redirector OR an Access Point. Access Point is recommend.<br>");
                                out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + "\" style=\"border-width:1px; border-style:solid\" >");
                                //need an html select in here
                                if (bd.getBindingTemplates().getBindingTemplate().get(i).getHostingRedirector() != null) {
                                    out.write("<div style=\"float:left\">Hosting Redirector&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + "hostingRedirector" + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getHostingRedirector().getBindingKey()) + "</div>");
                                }
                                if (bd.getBindingTemplates().getBindingTemplate().get(i).getAccessPoint() != null) {

                                    out.write("<div style=\"float:left\">Access Point Type: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + "accessPointType" + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getAccessPoint().getUseType()) + "</div>");
                                    out.write("<div style=\"float:left\">Access Point Type: &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + "accessPointValue" + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getAccessPoint().getValue()) + "</div>");
                                    out.write("</div>");
                                }
                            %>

                            <a href="javascript:AddTmodelInstance('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE%>');"><i class="icon-plus-sign"></i></a> tModel Instance Information - a binding template can have one or more instances of tModels. This can be used to attach any data you wish to a binding template, provided the tModel has been defined.
                            <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE%>" style="border-width: 1px; border-style: solid; border-color: red" >        
                                <%
                                    if (bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails() != null) {
                                        for (int k = 0; k < bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().size(); k++) {
                                            out.write("<div style=\"float:left\">tModel Key: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.KEYNAME + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getTModelKey()) + "</div>");
                                            out.write("<div style=\"float:left\"><span title=\"Instance Params\">Value</span>:&nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.VALUE + "\">" + ((bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails() != null) ? StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getInstanceDetails().getInstanceParms()) : "") + "</div>");
                                %>
                                <a href="javascript:AddDescriptionSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.DESCRIPTION%>');"><i class="icon-plus-sign"></i></a> Description - tModel instance infos can have more than one description, such as in a different language.
                                <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.DESCRIPTION%>" style="border-width: 1px; border-style: groove;" >
                                    <%
                                        for (int j = 0; j < bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().size(); k++) {
                                            out.write("<div id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.DESCRIPTION + j + "\" style=\"border-width:1px; border-style:solid\">");
                                            out.write("<div style=\"float:left;height:100%\">"
                                                    + "<a href=\"javascript:Remove('" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.DESCRIPTION + j + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                            out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.DESCRIPTION + j + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().get(j).getValue()) + "</div>");
                                            out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.TMODELINSTANCE + k + PostBackConstants.DESCRIPTION + j + PostBackConstants.LANG + "\">"
                                                    + (bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().get(j).getLang() == null ? " " : StringEscapeUtils.escapeHtml(bd.getBindingTemplates().getBindingTemplate().get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getDescription().get(j).getLang()))
                                                    + "</div>");
                                            out.write("</div>");
                                        }
                                    %>
                                </div>
                                <%    }
                                    }
                                    //TODO bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag()
%>
                                Binding Template Keyed Reference Categories:<Br>

                                <a href="javascript:AddCategoryKeyReferenceSpecific('<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF%>');"><i class="icon-plus-sign"></i></a> Add Key Reference Category <Br>
                                <div id="<%=PostBackConstants.BINDINGTEMPLATE + i + PostBackConstants.CATBAG_KEY_REF%>" style="border-width: 1px; border-style: dotted;" >
                                    <%
                                        if (bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag() != null) {
                                            for (int k = 0; k < bd.getBindingTemplates().getBindingTemplate().get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                            }
                                        }

                                    %>
                                </div>
                            </div>
                            <%
                                    /*
                                     out.write("<div>Add a description</div>");
                                     for (int k = 0; k < bd.getBindingTemplates().getBindingTemplate().get(i).getDescription().size(); k++) {
                                     out.write("lang : " + bd.getBindingTemplates().getBindingTemplate().get(i).getDescription().get(k).getLang());
                                     out.write("value: " + bd.getBindingTemplates().getBindingTemplate().get(i).getDescription().get(k).getValue());
                                     }
                                     //                            bd.getBindingTemplates().getBindingTemplate().get(i).getServiceKey()*/
                                    //    out.write("</div>");
                                }
                            %>
                        </div>

                    </div>


                </div>
                <div class="tab-pane" id="signatures">
                    <%
                        out.write("This service is " + (bd.getSignature().isEmpty() ? "not" : "") + " signed.");


                    %>
                </div>

            </div>
            <script type="text/javascript">
                var currentDescriptionSpecific=<%=totalBTDescriptions%>;
            </script>
            <Br><br>
            <a class="btn btn-primary " href="javascript:saveService();">Save</a> | 
            <a class="btn btn-danger " href="javascript:deleteService();">Delete</a> |
            <a class="btn btn-success " href="#"">Digitally Sign</a>
            <script type="text/javascript" src="js/businessEditor.js"></script>
            <script type="text/javascript" src="js/serviceEditor.js"></script>
        </div>

    </div>

    <!-- container div is in header bottom-->
    <%@include file="header-bottom.jsp" %>