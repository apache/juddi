<%-- 
    Document   : businesseditor
    Created on : Feb 24, 2013, 3:31:39 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.net.URLEncoder"%>
<%@page import="org.uddi.api_v3.IdentifierBag"%>
<%@page import="org.uddi.api_v3.CategoryBag"%>
<%@page import="org.uddi.api_v3.Contacts"%>
<%@page import="org.uddi.api_v3.BusinessEntity"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%=ResourceLoader.GetResource(session, "pages.businesseditor.title")%></h1>
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
                <b><%=ResourceLoader.GetResource(session, "pages.businesskey")%></b>-
                <%=ResourceLoader.GetResource(session, "pages.businesskey.description")%>
                <br>
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
                    <li class="active"><a  href="#general"><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.general")%></a></li>

                    <li><a href="#discovery" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.discovery")%></a></li>
                    <li><a href="#contacts" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.contacts")%></a></li>
                    <li><a href="#categories" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.categories")%></a></li>

                    <li><a href="#identifiers" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.identifiers")%></a></li>
                    <li><a href="#services" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.services")%></a></li>
                    <li><a href="#signatures" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.signatures")%></a></li>

                    <li><a href="#opinfo" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.opinfo")%></a></li>
                    <li><a href="#relations" ><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.relatedbusinesses")%></a></li>
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
                    $('#myTab a[href=#opinfo]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    $('#myTab a[href=#relations]').click(function (e) {
                        e.preventDefault();
                        $(this).tab('show');
                    });
                    
                </script>
                <div class="tab-content">
                    <div class="tab-pane active" id="general">
                        <a href="javascript:AddName();"><i class="icon-plus-sign"></i></a> <b><%=ResourceLoader.GetResource(session, "items.name")%></b> - 
                        <%=ResourceLoader.GetResource(session, "items.business.name.description")%>

                        <div id="nameContainer" style="border-width: 2px; border-style: solid;" >
                            <%
                                for (int i = 0; i < bd.getName().size(); i++) {
                                    out.write("<div id=\"" + PostBackConstants.NAME + i + "\" style=\"border-width:1px; border-style:solid\" >");
                                    out.write("<div style=\"float:left; height:100%\"><a href=\"javascript:Remove('Name" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.value") + ":&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.NAME + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getName().get(i).getValue()) + "</div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.lang") + "&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.NAME + i + PostBackConstants.LANG + "\">");
                                    if (bd.getName().get(i).getLang() != null) {
                                        out.write(StringEscapeUtils.escapeHtml(bd.getName().get(i).getLang()));
                                    }
                                    out.write("</div>");

                                    out.write("</div>");
                                }
                            %>
                        </div>
                        <Br>
                        <a href="javascript:AddDescription();"><i class="icon-plus-sign"></i></a> <b><%=ResourceLoader.GetResource(session, "items.description")%> </b>- 
                        <%=ResourceLoader.GetResource(session, "items.businesses.description")%>.
                        <div id="Description" style="border-width: 2px; border-style: solid;" >
                            <%
                                for (int i = 0; i < bd.getDescription().size(); i++) {
                                    out.write("<div id=\"" + PostBackConstants.DESCRIPTION + i + "\" style=\"border-width:1px; border-style:solid\">");
                                    out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('Description" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.value") + ":&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.DESCRIPTION + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getDescription().get(i).getValue()) + "</div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.lang") + ":&nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.DESCRIPTION + i + PostBackConstants.LANG + "\">" + StringEscapeUtils.escapeHtml(bd.getDescription().get(i).getLang()) + "</div>");

                                    out.write("</div>");
                                }
                            %>
                        </div>
                    </div>
                    <div class="tab-pane " id="discovery">

                        <a href="javascript:AddDisco();"><i class="icon-plus-sign"></i></a>
                        <b><%=ResourceLoader.GetResource(session, "items.discoveryurl")%></b>- <%=ResourceLoader.GetResource(session, "items.discoveryurl.description")%>
                        <div id="discoContainer" style="border-width: 2px; border-style: solid;" >
                            <%
                                if (bd.getDiscoveryURLs()
                                        != null) {
                                    for (int i = 0; i < bd.getDiscoveryURLs().getDiscoveryURL().size(); i++) {

                                        out.write("<div id=\"disco" + i + "\" style=\"border-width:1px; border-style:solid\">");
                                        out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('disco" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                        out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.value") + ":&nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.DISCOVERYURL + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getDiscoveryURLs().getDiscoveryURL().get(i).getValue()) + "</div>");
                                        out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.type") + ":&nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.DISCOVERYURL + i + PostBackConstants.TYPE + "\">" + StringEscapeUtils.escapeHtml(bd.getDiscoveryURLs().getDiscoveryURL().get(i).getUseType()) + "</div>");

                                        out.write("</div>");


                                    }
                                }

                            %>
                        </div>
                    </div>
                    <div class="tab-pane " id="contacts">
                        <a href="javascript:AddContact();"><i class="icon-plus-sign"></i></a>
                        <b><%=ResourceLoader.GetResource(session, "items.contacts")%></b>-
                        <%=ResourceLoader.GetResource(session, "items.contacts.description")%><br>

                        <div id="contactsContainer" style="border-width: 2px; border-style: solid;" >
                            <%
                                if (bd.getContacts() == null) {
                                    bd.setContacts(new Contacts());
                                }

                                if (bd.getContacts()
                                        != null) {
                                    for (int i = 0; i < bd.getContacts().getContact().size(); i++) {
                                        //this is the outer framework, the add buttons
                                        out.write("<div id=\"" + PostBackConstants.CONTACT_PREFIX + i + "\" style=\"border-width:2px; border-style:solid; border-color:red\" >"
                                                + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CONTACT_PREFIX + i
                                                + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.contact.type") + ": &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + i + PostBackConstants.TYPE + "\">"
                                                + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getUseType())
                                                + "</div>"
                                                + "<a href=\"javascript:AddContactName('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + ResourceLoader.GetResource(session, "items.name") + " &nbsp"
                                                + "<a href=\"javascript:AddContactEmail('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + ResourceLoader.GetResource(session, "items.email") + " &nbsp"
                                                + "<a href=\"javascript:AddContactDescription('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + ResourceLoader.GetResource(session, "items.description") + " &nbsp"
                                                + "<a href=\"javascript:AddContactPhone('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + ResourceLoader.GetResource(session, "items.phone") + " &nbsp"
                                                + "<a href=\"javascript:AddContactAddress('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + ResourceLoader.GetResource(session, "items.address") + " &nbsp");
                                        int contactid = i;
                                        //person name
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getPersonName().size(); k++) {

                                            int contactname = k;
                                            out.write("<div id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.NAME + contactname + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.NAME + contactname
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.name") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.NAME + contactname + PostBackConstants.VALUE + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPersonName().get(k).getValue()) + "</div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.lang") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.NAME + contactname + PostBackConstants.LANG + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPersonName().get(k).getLang()) + "</div>"
                                                    + "</div>"
                                                    + "</div>");
                                        }
                                        //email
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getEmail().size(); k++) {
                                            int contactemail = k;
                                            out.write("<div id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.EMAIL + contactemail + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.EMAIL + contactemail
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.type") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.EMAIL + contactemail + PostBackConstants.TYPE + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getEmail().get(k).getUseType())
                                                    + "</div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.value") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.EMAIL + contactemail + PostBackConstants.VALUE + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getEmail().get(k).getValue()) + "</div>"
                                                    + "</div>");
                                        }
                                        out.write("</div>");
                                        //contact description
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getDescription().size(); k++) {
                                            int contactdescription = k;
                                            out.write("<div id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.DESCRIPTION + contactdescription + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.DESCRIPTION + contactdescription
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.description") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.DESCRIPTION + contactdescription + PostBackConstants.VALUE + "\">"
                                                    + "</div>"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getDescription().get(k).getValue())
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.lang") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.DESCRIPTION + contactdescription + PostBackConstants.LANG + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getDescription().get(k).getLang())
                                                    + "</div>"
                                                    + "</div>");
                                        }

                                        //contact phone
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getPhone().size(); k++) {
                                            int contactphone = k;
                                            out.write("<div id=\"" + PostBackConstants.CONTACT_PREFIX
                                                    + contactid
                                                    + PostBackConstants.PHONE
                                                    + contactphone
                                                    + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('"
                                                    + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.PHONE + contactphone
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.phone") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.PHONE + contactphone + PostBackConstants.VALUE + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPhone().get(k).getValue())
                                                    + "</div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.type") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.PHONE + contactphone + PostBackConstants.TYPE + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getPhone().get(k).getUseType())
                                                    + "</div>"
                                                    + "</div>");

                                        }

                                        //contact addresses
                                        for (int k = 0; k < bd.getContacts().getContact().get(i).getAddress().size(); k++) {
                                            int contactaddress = k;
                                            out.write("<div id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + contactaddress + "\" style=\"border-width:1px; border-style:solid\" >"
                                                    + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + contactaddress
                                                    + "');\"><i class=\"icon-remove-sign\"></i></a>" + ResourceLoader.GetResource(session, "items.address") + "</div><br>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.lang") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + contactaddress + PostBackConstants.LANG + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getLang())
                                                    + "</div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.sortcode") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + contactaddress + "Sortcode\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getSortCode())
                                                    + "</div>"
                                                    + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.type") + ": &nbsp;</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + contactaddress + PostBackConstants.TYPE + "\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getSortCode())
                                                    + "</div>"
                                                    + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + contactaddress + "KeyName\">"
                                                    + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getTModelKey())
                                                    + "</div>"
                                                    + "<div><a href=\"javascript:AddContactAddressLine('" + contactid + PostBackConstants.ADDRESS + contactaddress + "');\">"
                                                    + "<i class=\"icon-plus-sign\"></i></a> " + ResourceLoader.GetResource(session, "items.addressline.add") + "</div>");


                                            for (int j = 0; j < bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().size(); j++) {
                                                int contactaddresslines = j;
                                                out.write("<div id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + k + PostBackConstants.ADDRESSLINE + contactaddresslines
                                                        + "\" style=\"border-width:1px; border-style:solid\" >"
                                                        + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESSLINE + contactaddresslines
                                                        + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                                        + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.addressvalue") + ": &nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + k + PostBackConstants.ADDRESSLINE + contactaddresslines + PostBackConstants.VALUE + "\">"
                                                        + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().get(k).getValue())
                                                        + "</div>"
                                                        + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.keyname.optional") + ": &nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + k + PostBackConstants.ADDRESSLINE + contactaddresslines + PostBackConstants.KEYNAME + "\">"
                                                        + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().get(k).getKeyName())
                                                        + "</div>"
                                                        + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.keyvalue.optional") + ": &nbsp;</div>"
                                                        + "<div class=\"edit\" id=\"" + PostBackConstants.CONTACT_PREFIX + contactid + PostBackConstants.ADDRESS + k + PostBackConstants.ADDRESSLINE + contactaddresslines + PostBackConstants.KEYVALUE + "\">"
                                                        + StringEscapeUtils.escapeHtml(bd.getContacts().getContact().get(i).getAddress().get(k).getAddressLine().get(k).getKeyValue())
                                                        + "</div>"
                                                        + "</div>");
                                            }
                                        }

                                    }
                                }

                            %>
                        </div>
                    </div>
                    <div class="tab-pane " id="categories">

                        <b><%=ResourceLoader.GetResource(session, "pages.editor.tabnav.categories")%> </b>- 
                        <%=ResourceLoader.GetResource(session, "items.categories.description")%>
                        <br><br>
                        <b><%=ResourceLoader.GetResource(session, "items.keyrefcats")%>:</b><br>
                        <a href="javascript:AddCategoryKeyReference();"><i class="icon-plus-sign"></i></a> <%=ResourceLoader.GetResource(session, "items.keyrefcat.add")%> <Br>
                        <div id="catContainer" style="border-width: 2px; border-style: solid;" >



                            <%
                                if (bd.getCategoryBag() == null) {
                                    bd.setCategoryBag(new CategoryBag());
                                }
                                //                        out.write("Keyed Reference Categories:");
                                for (int i = 0; i < bd.getCategoryBag().getKeyedReference().size(); i++) {

                                    out.write("<div id=\"" + PostBackConstants.CATBAG_KEY_REF + i + "\" style=\"border-width:2px; border-style:solid\">");
                                    out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CATBAG_KEY_REF + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.key") + ":  &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.CATBAG_KEY_REF + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReference().get(i).getTModelKey()) + "</div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.name") + ": &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.CATBAG_KEY_REF + i + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReference().get(i).getKeyName()) + "</div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.value") + ": &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.CATBAG_KEY_REF + i + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReference().get(i).getKeyValue()) + "</div>");
                                    out.write("</div>");
                                }
                            %>
                        </div>
                        <br>
                        <b><%=ResourceLoader.GetResource(session, "items.keyrefgroup")%></b><br>
                        <a href="javascript:AddCategoryKeyReferenceGroup();"><i class="icon-plus-sign"></i></a> <%=ResourceLoader.GetResource(session, "items.keyrefgroup.add")%><br>
                        <div id="catContainerGrp" style="border-width: 2px; border-style: solid;" >


                            <%
                                for (int i = 0; i < bd.getCategoryBag().getKeyedReferenceGroup().size(); i++) {

                                    out.write("<div id=\"" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "\" style=\"border-width:2px; border-style:solid\">"
                                            + "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"
                                            + "<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.key") + ":  &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"" + PostBackConstants.CATBAG_KEY_REF_GRP + i + PostBackConstants.VALUE + "\">"
                                            + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getTModelKey())
                                            + "</div>"
                                            + "<div id=\"" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "keyref\" style=\"border-width:1px; border-style:solid\">"
                                            + "<div style=\"float:left;height:100%\"><a href=\"javascript:AddCategoryKeyReferenceGroupKeyRef('" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "keyref');\"><i class=\"icon-plus-sign\"></i></a></div>"
                                            + ResourceLoader.GetResource(session, "items.keyrefcat.add")
                                            + "</div>");
                                    for (int k = 0; k < bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {

                                        out.write("<div id=\"" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "keyref" + k + "\" style=\"border-width:1px; border-style:solid\">");
                                        out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "keyref" + k + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                        out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.key") + ":  &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "keyref" + k + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getTModelKey()) + "</div>");
                                        out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.name") + ":  &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "keyref" + k + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getKeyName()) + "</div>");
                                        out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.value") + ": &nbsp;</div>"
                                                + "<div class=\"edit\" id=\"" + PostBackConstants.CATBAG_KEY_REF_GRP + i + "keyref" + k + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getKeyValue()) + "</div>");
                                        out.write("</div>");
                                    }

                                    out.write("</div>");
                                }


                            %>
                        </div>
                    </div>
                    <div class="tab-pane " id="identifiers">
                        <b><%=ResourceLoader.GetResource(session, "items.identifiers")%> </b>- 
                        <%=ResourceLoader.GetResource(session, "items.identifiers.description")%>

                        <Br>
                        <a href="javascript:AddIdentKeyReference();"><i class="icon-plus-sign"></i></a> <%=ResourceLoader.GetResource(session, "items.keyrefcat.add")%> <Br>
                        <div id="identContainer" style="border-width: 2px; border-style: solid;" >
                            <%
                                for (int i = 0; i < bd.getIdentifierBag().getKeyedReference().size(); i++) {
                                    out.write("<div id=\"identbagkeyref" + i + "\" style=\"border-width:2px; border-style:solid\">");
                                    out.write("<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('identbagkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.key") + ":  &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"identbagkeyref" + i + PostBackConstants.VALUE + "\">" + StringEscapeUtils.escapeHtml(bd.getIdentifierBag().getKeyedReference().get(i).getTModelKey()) + "</div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.name") + ":  &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"identbagkeyref" + i + "KeyName\">" + StringEscapeUtils.escapeHtml(bd.getIdentifierBag().getKeyedReference().get(i).getKeyName()) + "</div>");
                                    out.write("<div style=\"float:left\">" + ResourceLoader.GetResource(session, "items.value") + ": &nbsp;</div>"
                                            + "<div class=\"edit\" id=\"identbagkeyref" + i + "KeyValue\">" + StringEscapeUtils.escapeHtml(bd.getIdentifierBag().getKeyedReference().get(i).getKeyValue()) + "</div>");
                                    out.write("</div>");
                                }
                            %>
                        </div>
                    </div>
                    <div class="tab-pane " id="services">
                        <b><%=ResourceLoader.GetResource(session, "pages.businesseditor.businesslist")%> </b>- 
                        <%
                            if (bd.getBusinessServices() != null) {
                                out.write(Integer.toString(bd.getBusinessServices().getBusinessService().size()));
                            } else {
                                out.write("0");
                            }
                        %> <%=ResourceLoader.GetResource(session, "pages.businesseditor.businesslist2")%>
                        <%if (!newitem) {
                        %>

                        <a href="serviceEditor.jsp?bizid=<%=URLEncoder.encode(bd.getBusinessKey(), "UTF-8")%>" class="btn btn-primary">+</a>
                        <%
                            }
                        %>
                        <br>
                        <table class="table table-hover"><tr><th><%=ResourceLoader.GetResource(session, "items.key")%> </th><th><%=ResourceLoader.GetResource(session, "items.name")%></th><th><%=ResourceLoader.GetResource(session, "items.bindingtemplate")%></th></tr>
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
                    <div class="tab-pane" id="signatures"><b><%=ResourceLoader.GetResource(session, "items.dsigs")%></b>
                        <br>
                        <%
                            if (bd.getSignature().isEmpty()) {
                                out.write(ResourceLoader.GetResource(session, "items.signed.not"));
                            } else {
                                out.write(ResourceLoader.GetResource(session, "items.signed") + " " + bd.getSignature().size());
                        %>
                        <table class="table">


                            <%
                                    for (int k = 0; k < bd.getSignature().size(); k++) {
                                        out.write("<tr><td>");
                                        out.write(x.SignatureToReadable(bd.getSignature().get(k)));
                                        out.write("</td><td>");
                                        out.write("<a href=\"ajax/getCert.jsp?type=business&id=" + URLEncoder.encode(bd.getBusinessKey(), "UTF-8") + "&index=" + k + "\">" + ResourceLoader.GetResource(session, "items.signed.viewcert") + "</a>");
                                        out.write("</td></tr>");
                                    }
                                }

                            %>
                        </table>
                    </div>

                    <div class="tab-pane" id="opinfo">
                        <script type="text/javascript">
                            $.get("ajax/opInfo.jsp?id=<%=StringEscapeUtils.escapeJavaScript(bd.getBusinessKey())%>", function(data){
                                $("#opinfodiv").html(data);
                            } )
                        </script>
                        <div id="opinfodiv"></div>

                    </div>
                    <div class="tab-pane" id="relations">
                        <script type="text/javascript">
                            var data2 = new Array();
                            data2.push({
                                name: "selection",
                                value: "key"});
                            data2.push({
                                name: "nonce",
                                value:"<%=(String) session.getAttribute("nonce")%>"});
                            data2.push({    
                                name: "searchfor", 
                                value: "RelatedBusiness"
                            });
                            data2.push({
                                name:"searchcontent",
                                value:"<%=StringEscapeUtils.escapeJavaScript(bd.getBusinessKey())%>"
                            });
                            
                            $.ajax({url:"ajax/search.jsp", type:"post", data:data2, 
                                success: function (x){
                                    $("#relationresults").html(x);
                                }});
                            
                        </script>
                        <div id="relationresults"></div>

                    </div>
                </div>
            </div>
            <div><br>
                <%
                    if (bd.getSignature().isEmpty()) {
                %>
                <a class="btn btn-primary " href="javascript:saveBusiness();"><%=ResourceLoader.GetResource(session, "actions.save")%></a>
                <%  } else {
                %>
                <a href="#confirmDialog" role="button" class="btn btn-primary" data-toggle="modal"><%=ResourceLoader.GetResource(session, "actions.save")%></a>

                <%        }
                    if (!newitem) {

                %> | 
                <a class="btn btn-danger " href="javascript:deleteBusiness();"><%=ResourceLoader.GetResource(session, "actions.delete")%></a> |
                <a class="btn btn-success " href="signer.jsp?id=<%=URLEncoder.encode(bizid, "UTF-8")%>&type=business"><%=ResourceLoader.GetResource(session, "actions.sign")%></a> |
                <a class="btn btn-info " href="#" title="Alert me when this entity changes"><%=ResourceLoader.GetResource(session, "actions.subscribe")%></a> |
                <a class="btn btn-warning " href="#" title="Transfer this entity to another UDDI node"><%=ResourceLoader.GetResource(session, "actions.transfer")%></a> |
                <a class="btn "  href="javascript:ViewAsXML();"><%=ResourceLoader.GetResource(session, "actions.asxml")%></a>
                <%}%>
                <script type="text/javascript" src="js/businessEditor.js"></script>
                <script type="text/javascript">
                    Reedit();
                    function ViewAsXML()
                    {
                        $.get("ajax/toXML.jsp?id=<%=URLEncoder.encode(bizid, "UTF-8")%>&type=business", function(data){
                            window.console && console.log('asXml success');                
                            $("#viewAsXmlContent").html(safe_tags_replace(data));
                            $( "#viewAsXml" ).modal('show');
                        });
                       
                    }
                </script>

            </div>
        </div>
    </div>


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
            <a href="javascript:saveBusiness();$('#confirmDialog').modal('hide');" class="btn btn-primary">
                <%=ResourceLoader.GetResource(session, "modal.savechanges")%></a>
        </div>
    </div>
    <!-- container div is in header bottom-->
    <%@include file="header-bottom.jsp" %>