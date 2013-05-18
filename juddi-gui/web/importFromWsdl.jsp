<%-- 
    Document   : importFromWsdl
    Created on : May 10, 2013, 6:52:05 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.ws.Holder"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%=ResourceLoader.GetResource(session, "navbar.create.serviceimport")%></h1>

    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            <%=ResourceLoader.GetResource(session, "pages.serviceimport.content")%>
            <Br>
            <br>

            <div class="accordion" id="accordion2">
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapse1">
                            Step 1)    WSDL Location and Credentials
                        </a>
                    </div>
                    <div id="collapse1" class="accordion-body collapse in">
                        <div class="accordion-inner">
                            The first step is to tell us where we can find the Web Service Description Language (WSDL) document that
                            describes your services' operations, inputs and outputs and execution URLs. If you don't know where to find it
                            you can usually add a '?wsdl' to the end of the URL to the service. WSDLs are XML documents.<br>
                            <input type="text" id="wsdlurl" placeholder="http://localhost:8080/services/myService?wsdl" style="width:100%" ><br>
                            Sometimes, access to the WSDL file is restricted by a username and password, if this is the case, enter
                            your credentials here. It won't be saved anywhere.<br>
                            <%
                                if (!request.isSecure()) {
                            %>
                            <div class="alert alert-error">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <h4><i class="icon-warning-sign"></i> Warning!</h4>
                                Please consider switching to a secure connection such as SSL or TLS (the address bar starts with https://), 
                                otherwise your password may be exposed.
                            </div>
                            <%                                }
                            %>
                            <input type="text" id="wsdlusername" placeholder="username (optional)" ><br>
                            <input type="text" id="wsdlpassword" placeholder="password (optional)" ><br>
                            <input type="checkbox" id="wsdlignoressl" > Ignore SSL Errors<br>
                        </div>
                    </div>
                </div>
                <script type="text/javascript">
                 
                    //by James Padolsey
                    //http://james.padolsey.com/javascript/parsing-urls-with-the-dom/
                    function parseURL(url) {
                        var a =  document.createElement('a');
                        a.href = url;
                        return {
                            source: url,
                            protocol: a.protocol.replace(':',''),
                            host: a.hostname,
                            port: a.port,
                            query: a.search,
                            params: (function(){
                                var ret = {},
                                seg = a.search.replace(/^\?/,'').split('&'),
                                len = seg.length, i = 0, s;
                                for (;i<len;i++) {
                                    if (!seg[i]) { continue; }
                                    s = seg[i].split('=');
                                    ret[s[0]] = s[1];
                                }
                                return ret;
                            })(),
                            file: (a.pathname.match(/\/([^\/?#]+)$/i) || [,''])[1],
                            hash: a.hash.replace('#',''),
                            path: a.pathname.replace(/^([^\/])/,'/$1'),
                            relative: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [,''])[1],
                            segments: a.pathname.replace(/^\//,'').split('/')
                        };
                    }
                    
                    //after is entered, fetch the wsdl, parse the key domain
                    
                    //considerations, if its an ip address? what about localhost
                    function trigger1()
                    {
                        var l = parseURL($("#wsdlurl").val());
                        $("#keydomain").val(l.host);
                        $("#collapse1").collapse('hide');
                        $("#collapse2").collapse('show');
                    }
                    
                    function preview()
                    {
                        var postbackdata = new Array();
                        var url='ajax/importFromWsdl.jsp';
                        postbackdata.push({
                            name:"nonce", 
                            value: $("#nonce").val()
                        });
                        postbackdata.push({
                            name:"formaction", 
                            value: "preview"
                        });
                        postbackdata.push({
                            name:"wsdlusername", 
                            value: $("#wsdlusername").val()
                        });
                        postbackdata.push({
                            name:"wsdlpassword", 
                            value: $("#wsdlpassword").val()
                        });
                        postbackdata.push({
                            name:"wsdlurl", 
                            value: $("#wsdlurl").val()
                        });
                        if($('#wsdlignoressl').is(':checked')) {
                            postbackdata.push({
                                name:"ignoressl", 
                                value: true
                            });
                        }
                        else
                        {
                            postbackdata.push({
                                name:"ignoressl", 
                                value: false
                            }); 
                        }
                        postbackdata.push({
                            name:"businessname", 
                            value: $("#businessname").val()
                        });
                        
                        postbackdata.push({
                            name:"keydomain", 
                            value: $("#keydomain").val()
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
                            $("#preview").html(msg);
                        });

                        request.fail(function(jqXHR, textStatus) {
                            window.console && console.log('postback failed ' + url);                                
                            $("#preview").html(jqXHR + textStatus);
       
                        });
                    }

                </script>
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapse2" onclick="trigger1();">
                            Step 2)  Key Domain
                        </a>
                    </div>
                    <div id="collapse2" class="accordion-body collapse">
                        <div class="accordion-inner">
                            Enter a key domain or select an existing tModel Key Generator. We'll make one if it doesn't exist<br>
                            <input type="text" id="keydomain" placeholder="autofilled from the URL">
                        </div>
                    </div>
                </div>

                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapse3">
                            Step 3)   Business Selection
                        </a>
                    </div>
                    <div id="collapse3" class="accordion-body collapse">
                        <div class="accordion-inner">
                            Create a new one or use an existing one
                            <br>
                            Business Chooser or enter a new business name<br>

                            <input type="text" id="businessname" placeholder="Business Key">
                        </div>
                    </div>
                </div>


                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapse4">
                            Step 4) Review and Approve</a>
                    </div>
                    <div id="collapse4" class="accordion-body collapse">
                        <div class="accordion-inner">
                            Preview the new items and continue. Don't worry, you can always change these values later
                            <Br><br>
                            <div id="preview"></div>
                            <a class="btn btn-info" onclick="preview();"><i class="icon-eye-open icon-large"></i> Preview</a>
                            <a class="btn btn-primary" href="#"><i class="icon-save icon-large"></i> Approve</a>
                        </div>
                    </div>
                </div>

            </div>



        </div>
    </div>


    <%@include file="header-bottom.jsp" %>