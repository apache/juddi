<%-- 
    Document   : configure jUDDI
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>


<%@page import="java.util.Iterator"%>
<%@page import="org.apache.juddi.config.AppConfig"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp"%>

<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well">
        <h1>Configure jUDDI</h1>
        Just click to edit each field, then click save when you are done. (Not all fields can be modified)
    </div>

    <!-- Example row of columns -->
    <div class="row">


        <div class="span12">
            <%
                URL file = AppConfig.getInstance().getConfigFileURL();
                if (file != null) {
                    out.write("Loaded from: " + StringEscapeUtils.escapeHtml(file.toString()) + "<br>");
                } else {
                    out.write("Loaded from: (location unknown)<br>");
                }
            %>
            <h2>Server Config</h2>
            <table class="table table-hover">
                <tr><th>Field</th><th>Value</th></tr>
                        <%

                            Iterator it = AppConfig.getConfiguration().getKeys();
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                if (!key.equalsIgnoreCase("nonce")
                                        && key.startsWith("juddi.")) {
                                    out.write("<tr ><td>" + StringEscapeUtils.escapeHtml(key) + "</td><td><div ");
                                    out.write(" id=\"" + StringEscapeUtils.escapeHtml(key) + "\" class=\"edit\"");
                                    out.write(">" + StringEscapeUtils.escapeHtml(AppConfig.getConfiguration().getProperty(key).toString()) + "</div></td></tr>");
                                }
                            }
                        %>
            </table>
            
            <h2>Admin Console Config (this web site)</h2>
            <table class="table table-hover">
                <tr><th>Field</th><th>Value</th></tr>
                        <%
                            UddiAdminHub ahub = UddiAdminHub.getInstance(application, session);
                            it = ahub.GetJuddiClientConfig().getConfiguration().getKeys();
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                if (!key.equalsIgnoreCase("nonce")
                                        && (key.startsWith("config.props.")
                                        || key.startsWith("client."))) {
                                    out.write("<tr ><td>" + StringEscapeUtils.escapeHtml(key) + "</td><td><div ");
                                    out.write(" id=\"" + StringEscapeUtils.escapeHtml(key) + "\" class=\"edit\"");
                                    out.write(">" + StringEscapeUtils.escapeHtml(AppConfig.getConfiguration().getProperty(key).toString()) + "</div></td></tr>");
                                }
                            }
                        %>
            </table>
            
            
            <script type="text/javascript">
                function save()
                {
                    alert("hi");
                    var url = 'ajax/saveconfig.jsp';
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

                        $("#saveConfigresultBar").html(msg);


                    });

                    request.fail(function(jqXHR, textStatus) {
                        window.console && console.log('postback failed ' + url);
                        $("#saveConfigresultBar").html(jqXHR.responseText + textStatus);

                    });
                }
                Reedit();
            </script>
            <a class="btn btn-primary" href="javascript:save();">Save</a><br><br>


            <div id="saveConfigresultBar" class="well-small"></div>

            <h2>Debug Information</h2>
            <table class="table table-hover">
                <tr><th>Field</th><th>Value</th></tr>
                        <%

                            it = AppConfig.getConfiguration().getKeys();
                            while (it.hasNext()) {
                                String key = (String) it.next();

                                if (!key.equalsIgnoreCase("nonce")
                                        && !key.startsWith("juddi.")
                                        && !key.startsWith("client.")
                                        && key.startsWith("config.props.")) {
                                    out.write("<tr ><td>" + StringEscapeUtils.escapeHtml(key) + "</td><td><div ");

                                    out.write(">" + StringEscapeUtils.escapeHtml(AppConfig.getConfiguration().getProperty(key).toString()) + "</div></td></tr>");
                                }
                            }
                        %>
            </table>
        </div>

    </div>
    <%@include file="header-bottom.jsp"%>