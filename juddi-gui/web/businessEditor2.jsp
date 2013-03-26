<%-- 
    Document   : businesseditor
    Created on : Feb 24, 2013, 3:31:39 PM
    Author     : Alex O'Ree
--%>

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
        <h1>Business Editor</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >

            <div id="businesseditor">
                <%
                    boolean newitem = false;

                    UddiHub x = UddiHub.getInstance(application, request.getSession());
                    String bizid = request.getParameter("id");
                    if (bizid == null || bizid.isEmpty()) {
                        //response.sendRedirect("browse.jsp");
                        newitem = true;
                    }

                    BusinessEntity bd = null;
                    if (!newitem) {
                        bd = x.GetBusinessDetails(bizid);
                    } else {
                        bd = new BusinessEntity();
                    }

                %>
                <%

                    if (!newitem) {
                        out.write("<i class=\"icon-lock\"></i>");
                    }
                %>
                Business Key -
                The Business Key is the unique identifier for this business and exists within this registry. It cannot be modified.<br>
                <div style="border-width: 2px; border-style: solid;" class="<%

                    if (newitem) {
                        out.write("edit");
                    } else {
                        out.write("noedit");
                    }

                     %>" id="<%=PostBackConstants.BUSINESSKEY%>">
                    <%
                        out.write(StringEscapeUtils.escapeHtml(bd.getBusinessKey()));

                        if (bd.getContacts()
                                == null) {
                            bd.setContacts(new Contacts());
                        }
                        if (bd.getCategoryBag() == null) {
                            bd.setCategoryBag(new CategoryBag());
                        }
                        if (bd.getIdentifierBag() == null) {
                            bd.setIdentifierBag(new IdentifierBag());
                        }
                    %></div><br>

                <script type="text/javascript">
                    var currentNameEntries=<%= bd.getName().size() - 1%>;
                    var currentDisco=<%= bd.getContacts().getContact().size() - 1%>;
                    var currentDescriptionEntries=<%= bd.getDescription().size() - 1%>;
                    var currentContacts=<%= bd.getContacts().getContact().size() - 1%>;
                    var currentcatkeyref=<%=bd.getCategoryBag().getKeyedReference().size()%>;
                    var currentcatkeyrefgrp=<%=bd.getCategoryBag().getKeyedReferenceGroup().size()%>;
                    var currentident=<%=bd.getIdentifierBag().getKeyedReference().size()%>;
                </script>

                <ul class="nav nav-tabs" id="myTab">
                    <li class="active"><a  href="#general">General</a></li>

                    <li><a href="#discovery" >Discovery</a></li>
                    <li><a href="#contacts" >Contacts</a></li>
                    <li><a href="#categories" >Categories</a></li>

                    <li><a href="#identifiers" >Identifiers</a></li>
                    <li><a href="#services" >Services</a></li>
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
                    $('#myTab a[href=#discovery]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    $('#myTab a[href=#contacts]').click(function (e) {
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
                    $('#myTab a[href=#services]').click(function (e) {
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
                        Businesses are identified by one or more name. Multiple names are useful for different languages, legal names, or abbreviations.
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
                            %>
                        </div>
                        <Br>
                        <a href="javascript:AddDescription();"><i class="icon-plus-sign"></i></a> Description - businesses can have more than one description, such as in a different language.
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
                    <div class="tab-pane " id="discovery">
                        <Br>
                        <a href="javascript:AddDisco();"><i class="icon-plus-sign"></i></a>
                        Discovery URLs are typically a link to a simple web page with additional information on it, such as a listing a services.
                        Two reserved values are specified in the specification, 'homepage' and 'businessEntity'.
                        <div id="discoContainer" style="border-width: 2px; border-style: solid;" >
                            <%
                                if (bd.getDiscoveryURLs()
                                        != null) {
                                    for (int i = 0; i < bd.getDiscoveryURLs().getDiscoveryURL().size(); i++) {

                                        out.write("<div id=\"disco" + i + "\" style=\"border-width:1px; border-style:solid\">");
                                        out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('disco" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                        out.write("<div style=\"float:left\">Value:&nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.DISCOVERYURL + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getDiscoveryURLs().getDiscoveryURL().get(i).getValue()) + "</div>");
                                        out.write("<div style=\"float:left\">Type:&nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.DISCOVERYURL + i + PostBackConstants.TYPE + "\">" + StringEscapeUtils.escapeHtml(bd.getDiscoveryURLs().getDiscoveryURL().get(i).getUseType()) + "</div>");

                                        out.write("</div>");

                                        /*
                                         out.write("<div id=\"disco" + i + "\">");
                                         out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('disco" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                         out.write("Value <div class=\"edit\" id=\"discoValue" + i + "\">" + bd.getDiscoveryURLs().getDiscoveryURL().get(i).getValue() + "</div>");
                                         out.write("Type <div class=\"edit\" id=\"discoType" + i + "\">" + bd.getDiscoveryURLs().getDiscoveryURL().get(i).getUseType() + "</div>");

                                         out.write("</div>");*/
                                    }
                                }

                            %>
                        </div>
                    </div>
                    <div class="tab-pane " id="contacts">
                        <a href="javascript:AddContact();"><i class="icon-plus-sign"></i></a>
                        Contacts - Each business typically has several points of contact 
                        for a person or a job role within the
                        business so that someone who finds the information can make human contact for any
                        purpose. Examples for Type: "technical questions", "technical contact", "establish account", "sales
                        contact"<br>

                        <div id="contactsContainer" style="border-width: 2px; border-style: solid;" >
                            <%                        if (bd.getContacts()
                                        == null) {
                                    bd.setContacts(new Contacts());
                                }

                                if (bd.getContacts()
                                        != null) {
                                    for (int i = 0; i < bd.getContacts().getContact().size(); i++) {
                                        //this is the outer framework, the add buttons
                                        out.write("<div id=\"contact" + i + "\" style=\"border-width:2px; border-style:solid; border-color:red\" >"
                                                + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + i
                                                + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                + "<div style=\"float:left\">Contact Type: &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"contact" + i + "Type\">"
                                                + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getUseType())
                                                + "</div>"
                                                + "<a href=\"javascript:AddContactName('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>Name &nbsp"
                                                + "<a href=\"javascript:AddContactEmail('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>Email &nbsp"
                                                + "<a href=\"javascript:AddContactDescription('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>Description &nbsp"
                                                + "<a href=\"javascript:AddContactPhone('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>Phone &nbsp"
                                                + "<a href=\"javascript:AddContactAddress('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>Address &nbsp");
                                        int contactid = i;
                                        //person name
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getPersonName().size(); k++) {

                                            int contactname = k;
                                            out.write("<div id=\"contact" + contactid + "Name" + contactname + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Name" + contactname
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">Name: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Name" + contactname + "Value\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPersonName().get(k).getValue()) + "</div>"
                                                    + "<div style=\"float:left\">Language: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Name" + contactname + "Lang\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPersonName().get(k).getLang()) + "</div>"
                                                    + "</div>"
                                                    + "</div>");
                                        }
                                        //email
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getEmail().size(); k++) {
                                            int contactemail = k;
                                            out.write("<div id=\"contact" + contactid + "Email" + contactemail + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Email" + contactemail
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">Type: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Email" + contactemail + "Type\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getEmail().get(k).getUseType())
                                                    + "</div>"
                                                    //+ "</div>"
                                                    + "<div style=\"float:left\">Value: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Email" + contactemail + "Value\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getEmail().get(k).getValue()) + "</div>"
                                                    //+ "</div>"
                                                    + "</div>");//.insertAfter("#contact" + contactid);
                                        }
                                        out.write("</div>");
                                        //contact description
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getDescription().size(); k++) {
                                            int contactdescription = k;
                                            out.write("<div id=\"contact" + contactid + "Description" + contactdescription + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Description" + contactdescription
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">Description: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Description" + contactdescription + "Value\">"
                                                    + "</div>"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getDescription().get(k).getValue())
                                                    + "<div style=\"float:left\">Language: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Description" + contactdescription + "Lang\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getDescription().get(k).getLang())
                                                    + "</div>"
                                                    + "</div>");
                                        }

                                        //contact phone
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getPhone().size(); k++) {
                                            int contactphone = k;
                                            out.write("<div id=\"contact"
                                                    + contactid
                                                    + "Phone"
                                                    + contactphone
                                                    + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Phone" + contactphone
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">Phone: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Phone" + contactphone + "Value\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPhone().get(k).getValue())
                                                    + "</div>"
                                                    + "<div style=\"float:left\">Type: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Phone" + contactphone + "Type\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPhone().get(k).getUseType())
                                                    + "</div>"
                                                    + "</div>");

                                        }

                                        //contact addresses
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getAddress().size(); k++) {
                                            int contactaddress = k;
                                            out.write("<div id=\"contact" + contactid + "Address" + contactaddress + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Address" + contactaddress
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a>Address</div><br>"
                                                    + "<div style=\"float:left\">Language: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "Lang\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getLang())
                                                    + "</div>"
                                                    + "<div style=\"float:left\">Sort Code: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "Sortcode\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getSortCode())
                                                    + "</div>"
                                                    + "<div style=\"float:left\">Type: &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "Type\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getSortCode())
                                                    + "</div>"
                                                    + "<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "KeyName\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getTModelKey())
                                                    + "</div>"
                                                    + "<div><a href=\"javascript:AddContactAddressLine('" + contactid + "Address" + contactaddress + "');\"><i class=\"icon-plus-sign\"></i></a> Add an addline (at least one is required)</div>");


                                            for (int j = 0; j < bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().size(); j++) {
                                                int contactaddresslines = j;
                                                out.write("<div id=\"contact" + contactid + "Address" + k + "addressLine" + contactaddresslines
                                                        + "\" style=\"border-width:1px; border-style:solid\" >"
                                                        + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "addressLine" + contactaddresslines
                                                        + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                        + "<div style=\"float:left\">Address Value: &nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"contact" + contactid + "Address" + k + "addressLine" + contactaddresslines + "Value\">"
                                                        + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().get(k).getValue())
                                                        + "</div>"
                                                        + "<div style=\"float:left\">Key Name (optional): &nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"contact" + contactid + "Address" + k + "addressLine" + contactaddresslines + "KeyName\">"
                                                        + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().get(k).getKeyName())
                                                        + "</div>"
                                                        + "<div style=\"float:left\">Key Value (optional): &nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"contact" + contactid + "Address" + k + "addressLine" + contactaddresslines + "KeyValue\">"
                                                        + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().get(k).getKeyValue())
                                                        + "</div>"
                                                        + "</div>");
                                            }
                                        }

                                    }
                                }

                                bd.getIdentifierBag();
                                if (bd.getSignature() != null && !bd.getSignature().isEmpty()) {
                                    out.write("WARNING: This business entity is digitally signed. After editing it, it will no longer be signed");
                                }
                                //if using savebusiness, can you attach services that are not owned by the business

                            %>
                        </div>
                    </div>
                    <div class="tab-pane " id="categories">

                        Categories - UDDI uses a taxonomy system to categorize businesses and their services. These categories are defined as UDDI tModels and
                        are defined by the administrator(s) of this UDDI node. These categories are appended to business registrations either by adding one or more "Key References"
                        or by adding one or more "Key Reference Groups", which in turn can be a zero or more of Key References as part of it.<br><br>
                        Keyed Reference Categories:<Br>
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
                        <br>
                        Keyed Reference Groups<br>
                        <a href="javascript:AddCategoryKeyReferenceGroup();"><i class="icon-plus-sign"></i></a> Add Key Reference Group Category<br>
                        <div id="catContainerGrp" style="border-width: 2px; border-style: solid;" >


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
                    <div class="tab-pane " id="identifiers">
                        Identifiers - optionally, you can attach identifiers that uniquely identify this business from other systems, such as a tax ID or a <a href="http://www.whitehouse.gov/sites/default/files/omb/grants/duns_num_guide.pdf">DUNS Number</a>.<Br>
                        <a href="javascript:AddIdentKeyReference();"><i class="icon-plus-sign"></i></a> Add Key Reference Category <Br>
                        <div id="identContainer" style="border-width: 2px; border-style: solid;" >
                            <%
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
                    <div class="tab-pane " id="services">
                        Business Services - 
                        <%
                            if (bd.getBusinessServices() != null) {
                                out.write(Integer.toString(bd.getBusinessServices().getBusinessService().size()));
                            } else {
                                out.write("0");
                            }
                        %> are defined for this business.<br>
                        <table class="table table-hover"><tr><th>Key</th><th>Name</th><th>BTs</th></tr>
                            <%
                                if (bd.getBusinessServices() != null) {
                                    for (int i = 0; i < bd.getBusinessServices().getBusinessService().size(); i++) {
                            %><tr>
                                <td><%
                                    if (!bd.getBusinessServices().getBusinessService().get(i).getName().isEmpty()) {
                                        out.write(bd.getBusinessServices().getBusinessService().get(i).getName().get(0).getValue());
                                    }
                                    %>
                                </td><td><a href="serviceEditor.jsp?id=<%=StringEscapeUtils.escapeHtml(bd.getBusinessServices().getBusinessService().get(i).getServiceKey())%>">
                                        <%
                                            out.write(bd.getBusinessServices().getBusinessService().get(i).getServiceKey());
                                        %>
                                        <i class="icon-edit"></i></a>
                                </td><td>
                                    <%
                                        if (bd.getBusinessServices().getBusinessService().get(i).getBindingTemplates() == null) {
                                            out.write("0");
                                        } else {
                                            out.write(Integer.toString(bd.getBusinessServices().getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size()));
                                        }
                                    %>
                                </td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                        </table>

                    </div>
                    <div class="tab-pane" id="signatures">Digital Signatures
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
                                        out.write("</td></tr>");
                                    }
                                }

                            %>
                        </table>
                    </div>
                </div>
            </div>
            <div><br>
                <a class="btn btn-primary " href="javascript:saveBusiness();">Save</a> | 
                <a class="btn btn-danger " href="javascript:deleteBusiness();">Delete</a> |
                <a class="btn btn-success " href="signer.jsp?id=<%=bizid%>&type=business">Digitally Sign</a></div>
            <script type="text/javascript" src="js/businessEditor.js"></script>
            <script type="text/javascript">
                Reedit();
            </script>
        </div>

    </div>



    <!-- container div is in header bottom-->
    <%@include file="header-bottom.jsp" %>