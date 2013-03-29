<%-- 
    Document   : tmodelEditor
    Created on : Feb 27, 2013, 9:31:19 PM
    Author     : Alex O'Ree
--%>



<%@page import="java.net.URLEncoder"%>
<%@page import="org.uddi.api_v3.*"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>
<%@page import="org.apache.juddi.webconsole.hub.*"%>
<%@page import="org.apache.juddi.query.FindBusinessByNameQuery"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1>tModel Editor</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            <p>
                <%
                    UddiHub x = UddiHub.getInstance(application, session);
                    TModel bd = hub.getTmodelDetails(request.getParameter("id"));
                    boolean newitem = false;
                    if (bd == null) {
                        bd = new TModel();
                        newitem = true;
                    }
                %>

            <ul class="nav nav-tabs" id="myTab">
                <li class="active"><a  href="#general">General</a></li>

                <li><a href="#discovery" >Overview Documents</a></li>

                <li><a href="#categories" >Categories</a></li>

                <li><a href="#identifiers" >Identifiers</a></li>
                <li><a href="#signatures" >Signatures</a></li>
            </ul>
            <script type="text/javascript">
                $(function () {
                    $('#myTab').tab;//('show');
                })
                $('#myTab a[href=#general]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });
                $('#myTab a[href=#discovery]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });
                
                $('#myTab a[href=#categories]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });
                $('#myTab a[href=#identifiers]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });

                $('#myTab a[href=#signatures]').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                });
                    
                var currentDescriptionEntries=<%=bd.getDescription().size()%>;
                var currentOverviewDocs=<%=bd.getOverviewDoc().size()%>;
                <%
                    int currentDescriptionSpecific = 0;
                    for (int i = 0; i < bd.getOverviewDoc().size(); i++) {
                        currentDescriptionSpecific += bd.getOverviewDoc().get(i).getDescription().size();
                    }

                    int currentcatkeyref = 0;
                    if (bd.getCategoryBag() != null) {
                        currentcatkeyref = bd.getCategoryBag().getKeyedReference().size();
                    }
                    int currentident = 0;
                    if (bd.getIdentifierBag() != null) {
                        currentident = bd.getIdentifierBag().getKeyedReference().size();
                    }
                %>
                    var currentcatkeyref =<%=currentcatkeyref%>;
                    var currentDescriptionSpecific=<%=currentDescriptionSpecific%>;
                    var currentident=<%=currentident%>;
            </script>
            <div class="tab-content">
                <div class="tab-pane active" id="general">
                    <%
                        if (!newitem) {
                            out.write("<i class=\"icon-lock\"></i>");
                        }
                    %>
                    The tModel Key is the unique identifier. If you specify a tModel, it must be prefixed with
                    an existing partition (key generator), other one is generated for you.

                    <div style="border-width: 2px; border-style: solid;" <%
                        if (!newitem) {
                            out.write("class=\"noedit\"");
                        } else {
                            out.write("class=\"edit\"");
                        }
                         %>
                         id="<%=PostBackConstants.SERVICEKEY%>"><%
                             out.write(StringEscapeUtils.escapeHtml(bd.getTModelKey()));
                        %></div><br>


                    Name - The name describes this tModel. It is a required field.

                    <%

                        if (bd.getName() == null) {
                            bd.setName(new Name());
                        }
                        out.write("<div id=\"" + PostBackConstants.NAME + "\" style=\"border-width:2px; border-style:solid\" >");
                        out.write("<div style=\"float:left; height:100%\"><a href=\"javascript:Remove('Name');\"><i class=\"icon-remove-sign\"></i></a></div>");
                        out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                + "<div class=\"edit\" id=\"" + PostBackConstants.NAME + PostBackConstants.VALUE + "\">" + (bd.getName().getValue() == null ? " " : StringEscapeUtils.escapeHtml(bd.getName().getValue())) + "</div>");
                        out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                + "<div class=\"edit\" id=\"" + PostBackConstants.NAME + PostBackConstants.LANG + "\">"
                                + ((bd.getName().getLang() == null ? " " : StringEscapeUtils.escapeHtml(bd.getName().getLang())))
                                + "</div>");

                        out.write("</div>");

                    %>

                    <Br>
                    <a href="javascript:AddDescription();"><i class="icon-plus-sign"></i></a> Description - businesses can have more than one description, such as in a different language.
                    <div id="Description" style="border-width: 2px; border-style: solid;" >
                        <%
                            if (bd.getDescription() != null) //bd.(new Description());
                            {
                                for (int i = 0; i < bd.getDescription().size(); i++) {
                                    out.write("<div id=\"" + PostBackConstants.DESCRIPTION + i + "\" style=\"border-width:1px; border-style:solid\">");
                                    out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('Description" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.DESCRIPTION + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getDescription().get(i).getValue()) + "</div>");
                                    out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.DESCRIPTION + i + PostBackConstants.LANG + "\">"
                                            + (bd.getDescription().get(i).getLang() != null
                                            ? StringEscapeUtils.escapeHtml(bd.getDescription().get(i).getLang()) : "")
                                            + "</div>");

                                    out.write("</div>");
                                }
                            }
                        %>
                    </div>
                </div>

                <div class="tab-pane " id="discovery">
                    <a href="javascript:AddOverviewDocument();"><i class="icon-plus-sign"></i></a>Overview Documents - These are typically URLs to web pages that describe this tModel's details and usage scenarios.
                    <%
                        out.write("<div id=\"" + PostBackConstants.OVERVIEW + "\" style=\"border-width:2px; border-style:solid\">");
                        for (int i = 0; i < bd.getOverviewDoc().size(); i++) {
                            out.write("<div id=\"" + PostBackConstants.OVERVIEW + i + "\" style=\"border-width:1px; border-style:solid\">");
                            out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.OVERVIEW + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                            out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                    + "<div class=\"edit\" id=\"" + PostBackConstants.OVERVIEW + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getOverviewDoc().get(i).getOverviewURL().getValue()) + "</div>");
                            out.write("<div style=\"float:left\">Use type:&nbsp;</div>"
                                    + "<div class=\"edit\" id=\"" + PostBackConstants.OVERVIEW + i + PostBackConstants.TYPE + "\">" + StringEscapeUtils.escapeHtml(bd.getOverviewDoc().get(i).getOverviewURL().getUseType()) + "</div>");

                    %>

                    <a href="javascript:AddDescriptionSpecific('<%=PostBackConstants.OVERVIEW + i + PostBackConstants.DESCRIPTION%> ');"><i class="icon-plus-sign"></i></a> Add a description
                    <%
                            out.write("<div id=\"" + PostBackConstants.OVERVIEW + i + PostBackConstants.DESCRIPTION + "\" style=\"border-width:1px; border-style:dotted\">");
                            for (int k = 0; k < bd.getOverviewDoc().get(i).getDescription().size(); k++) {
                                out.write("<div id=\"" + PostBackConstants.OVERVIEW + i + PostBackConstants.DESCRIPTION + k + "\" style=\"border-width:1px; border-style:solid\">");
                                out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.OVERVIEW + i + PostBackConstants.DESCRIPTION + k + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                        + "<div class=\"edit\" id=\"" + PostBackConstants.OVERVIEW + i + PostBackConstants.DESCRIPTION + k + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getOverviewDoc().get(i).getDescription().get(k).getValue()) + "</div>");
                                out.write("<div style=\"float:left\">Language:&nbsp;</div>"
                                        + "<div class=\"edit\" id=\"" + PostBackConstants.OVERVIEW + i + PostBackConstants.DESCRIPTION + k + PostBackConstants.LANG + "\">" + StringEscapeUtils.escapeHtml(bd.getOverviewDoc().get(i).getDescription().get(k).getLang()) + "</div>");
                                out.write("</div>"); //end of this instance of overview doc description
                            }
                            out.write("</div>");//end description
                            out.write("</div>");//end this block
                        }
                        out.write("</div>");//end of overview
%>

                </div>

                <div class="tab-pane " id="categories">


                    <a href="javascript:AddCategoryKeyReference();"><i class="icon-plus-sign"></i></a> Add Key Reference Category <Br>
                    <div id="catContainer" style="border-width: 2px; border-style: solid;" >
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

                </div>
                <div class="tab-pane " id="identifiers">
                    Identifiers - optionally, you can attach identifiers that uniquely identify this business from other systems, such as a tax ID or a <a href="http://www.whitehouse.gov/sites/default/files/omb/grants/duns_num_guide.pdf">DUNS Number</a>.<Br>
                    <a href="javascript:AddIdentKeyReference();"><i class="icon-plus-sign"></i></a> Add Key Reference Category <Br>
                    <div id="identContainer" style="border-width: 2px; border-style: solid;" >
                        <%
                            if (bd.getIdentifierBag() == null) {
                                bd.setIdentifierBag(new IdentifierBag());
                            }
                            for (int i = 0; i < bd.getIdentifierBag().getKeyedReference().size(); i++) {
                                out.write("<div id=\"identbagkeyref" + i + "\" style=\"border-width:2px; border-style:solid\">");
                                out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('identbagkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                out.write("<div style=\"float:left\">Key: &nbsp;</div>"
                                        + "<div class=\"edit\" id=\"identbagkeyref" + i + "Value\">" + StringEscapeUtils.escapeHtml(bd.getIdentifierBag().getKeyedReference().get(i).getTModelKey()) + "</div>");
                                out.write("<div style=\"float:left\">Name: &nbsp;</div>"
                                        + "<div class=\"edit\" id=\"identbagkeyref" + i + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getIdentifierBag().getKeyedReference().get(i).getKeyName()) + "</div>");
                                out.write("<div style=\"float:left\">Value: &nbsp;</div>"
                                        + "<div class=\"edit\" id=\"identbagkeyref" + i + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getIdentifierBag().getKeyedReference().get(i).getKeyValue()) + "</div>");
                                out.write("</div>");
                            }
                        %>
                    </div>
                </div>
                <div class="tab-pane " id="signatures">

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
                                        out.write("<a href=\"ajax/getCert.jsp?type=tmodel&id=" + URLEncoder.encode(bd.getTModelKey(), "UTF-8") + "&index=" + k + "\">View Certificate</a>");
                                        out.write("</td></tr>");
                                    }
                                }

                            %>
                        </table>
                </div>
                <div><br>
                    <a class="btn btn-primary " href="javascript:savetModel();">Save</a> | 
                    <a class="btn btn-danger " href="javascript:deletetModel();">Delete</a> |
                    <a class="btn btn-success " href="#"">Digitally Sign</a> |
                    <a class="btn btn-info " href="#" title="Alert me when this entity changes">Subscribe</a> |
                    <a class="btn btn-warning " href="#" title="Transfer this entity to another UDDI node">Transfer</a>
                </div>
            </div>
            <script src="js/tmodeledit.js"></script>
            <script src="js/businessEditor.js"></script>
            <script type="text/javascript">
                Reedit();
            </script>

            </p>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>