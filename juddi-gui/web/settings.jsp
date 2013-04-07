<%-- 
    Document   : settings
    Created on : Feb 23, 2013, 2:05:35 PM
    Author     : Alex O'Ree
--%>

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

            <table class="table table-hover">
                <tr><th>Key</th><th>Value</th></tr>
                <%

                    UddiHub x = UddiHub.getInstance(application, session);
                    Properties p = x.GetRawConfiguration();
                    Set<Entry<Object, Object>> set = p.entrySet();
                    Iterator<Entry<Object, Object>> it = set.iterator();
                    while (it.hasNext()) {
                        out.write("<tr><td>");
                        Entry<Object, Object> item = (Entry<Object, Object>) it.next();
                        String key = (String) item.getKey();
                        String value = (String) item.getValue();
                        out.write(StringEscapeUtils.escapeHtml(key));
                        out.write("</td><td><div class=\"edit\" id=\"" + StringEscapeUtils.escapeHtml(key) + "\">");
                        out.write(StringEscapeUtils.escapeHtml(value));
                        out.write("</div></td></tr>");
                    }
                %>
            </table>
            <a class="btn btn-primary " href="javascript:saveSettings();"><%=ResourceLoader.GetResource(session, "actions.save")%></a>
            <script type="text/javascript">
                
                Reedit();
                function saveSettings()
                {
                    var url='ajax/settings.jsp';
                    var postbackdata = new Array();
                    $("div.edit").each(function()
                    {
                        var id=$(this).attr("id");
                        var value=$(this).text();
                        postbackdata.push({
                            name: id, 
                            value: value
                        });
                    }); 
                    postbackdata.push({
                        name:"nonce", 
                        value: $("#nonce").val()
                    });
                    $("div.noedit").each(function()
                    {
                        var id=$(this).attr("id");
                        var value=$(this).text();
                        postbackdata.push({
                            name: id, 
                            value: value
                        });
                    }); 
    
    
                    var request=   $.ajax({
                        url: url,
                        type:"POST",
                        //  data" + i18n_type + ": "html", 
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
                        $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' +jqXHR.responseText );
                        //$(".alert").alert();
                        $("#resultBar").show();
        
                    });
                }
            </script>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>