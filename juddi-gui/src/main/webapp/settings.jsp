-<%-- 
    Document   : settings
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.v3.client.config.ClientConfig"%>
<%@page import="org.apache.commons.configuration.Configuration"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Properties"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1><%=ResourceLoader.GetResource(session, "navbar.settings")%></h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12">
            <h2><%=ResourceLoader.GetResource(session, "navbar.settings")%></h2>
            <p><%=ResourceLoader.GetResource(session, "items.settings.description")%></p>
            <%
                UddiHub x = UddiHub.getInstance(application, session);


            %>


            <%=ResourceLoader.GetResource(session, "pages.settings.loading")%> <%=StringEscapeUtils.escapeHtml(x.GetJuddiClientConfig().getConfigurationFile())%><br>
            <table class="table table-hover">
                <tr><th><%=ResourceLoader.GetResource(session, "items.key")%></th>
                    <th><%=ResourceLoader.GetResource(session, "items.value")%></th></tr>
                        <%
                            try {
                                ClientConfig cfg = x.GetJuddiClientConfig();
                                Configuration cfg2 = cfg.getConfiguration();
                                Iterator<String> it2 = cfg.getConfiguration().getKeys();

                                String[] nodes = cfg2.getStringArray("client.nodes.node.name");

                                while (it2.hasNext()) {

                                    String key = it2.next();

                                    String value = cfg.getConfiguration().getString(key);
                                    if (key.startsWith("client") && !key.startsWith("client.nodes.node")) {
                                        out.write("<tr><td>");
                                        out.write(StringEscapeUtils.escapeHtml(key));
                                        out.write("</td><td><div ");
                                        if (key.startsWith("client") && !key.startsWith("client.nodes")) {
                                            out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\"");
                                        }
                                        out.write(">");
                                        out.write(StringEscapeUtils.escapeHtml(value));
                                        out.write("</div></td></tr>");
                                    }
                                }

                                for (int i = 0; i < nodes.length; i++) {

                                    String key = "client.nodes.node(" + i + ").name";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                    key = "client.nodes.node(" + i + ").description";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                    key = "client.nodes.node(" + i + ").proxyTransport";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                    key = "client.nodes.node(" + i + ").custodyTransferUrl";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                    key = "client.nodes.node(" + i + ").inquiryUrl";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                    key = "client.nodes.node(" + i + ").publishUrl";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                    key = "client.nodes.node(" + i + ").securityUrl";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                    key = "client.nodes.node(" + i + ").subscriptionUrl";
                                    out.write("<tr><td>");
                                    out.write(StringEscapeUtils.escapeHtml(key));
                                    out.write("</td><td><div ");
                                    out.write("class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                                    out.write(StringEscapeUtils.escapeHtml(cfg2.getString(key)));
                                    out.write("</div></td></tr>");

                                }


                            } catch (Exception ex) {
                                x.log.error(ex);
                            }
                        %>
            </table>


            <a class="btn btn-primary " href="javascript:saveSettings();"><i class="icon-large icon-save"></i> <%=ResourceLoader.GetResource(session, "actions.save")%></a>
            <script type="text/javascript">

                Reedit();
                function saveSettings()
                {
                    var url = 'ajax/settings.jsp';
                    var postbackdata = new Array();
                    $("div.edit").each(function()
                    {
                        var id = $(this).attr("id");
                        var value = $(this).text();
                        postbackdata.push({
                            name: id,
                            value: value
                        });
                    });
                    postbackdata.push({
                        name: "nonce",
                        value: $("#nonce").val()
                    });
                    $("div.noedit").each(function()
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

                        $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' + msg);
                        $("#resultBar").show();

                    });

                    request.fail(function(jqXHR, textStatus) {
                        window.console && console.log('postback failed ' + url);
                        $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' + jqXHR.responseText);
                        //$(".alert").alert();
                        $("#resultBar").show();

                    });
                }
            </script>

            <br><br>
            <script type="text/javascript">
                function showDebug() {
                    $("#debugtable").show();
                }
            </script>
            <a class="btn " href="javascript:showDebug();"><i class="icon-large icon-save"></i> <%=ResourceLoader.GetResource(session, "pages.settings.debuginfo")%></a>
            <div id="debugtable" class="hide">
                <table class="table table-hover">
                    <tr><th><%=ResourceLoader.GetResource(session, "items.key")%></th>
                        <th><%=ResourceLoader.GetResource(session, "items.value")%></th></tr>
                            <%

                                try {
                                    ClientConfig cfg = x.GetJuddiClientConfig();
                                    Iterator<String> it2 = cfg.getConfiguration().getKeys();

                                    while (it2.hasNext()) {

                                        String key = it2.next();

                                        if (!key.startsWith("config.props.") && !key.startsWith("client")) {
                                            String value = cfg.getConfiguration().getString(key);
                                            out.write("<tr><td>");
                                            out.write(StringEscapeUtils.escapeHtml(key));
                                            out.write("</td><td><div ");
                                            out.write(">");
                                            out.write(StringEscapeUtils.escapeHtml(value));
                                            out.write("</div></td></tr>");
                                        }
                                    }
                                } catch (Exception ex) {
                                    x.log.error(ex);
                                }

                            %>
                </table>
            </div>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>